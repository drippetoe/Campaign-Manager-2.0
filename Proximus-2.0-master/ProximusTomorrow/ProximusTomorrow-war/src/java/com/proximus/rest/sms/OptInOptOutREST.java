/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.sms.*;
import com.proximus.location.client.FakeLocationFinder;
import com.proximus.location.client.LocationFinder;
import com.proximus.location.client.LocationFinderLocal;
import com.proximus.manager.sms.*;
import com.proximus.registration.client.FakeRegistrar;
import com.proximus.registration.client.Registrar;
import com.proximus.registration.client.RegistrarLocal;
import com.proximus.registration.client.RegistrarResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Path("/optinoptout/")
@RequestScoped
public class OptInOptOutREST {
    
    static final Logger logger = Logger.getLogger(OptInOptOutREST.class.getName());
    //static final String BASE_URL = "http://proximusmobility.com/api/optInStatus";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private SubscriberManagerLocal subscriberMgr;
    @EJB
    private LocationDataManagerLocal locMgr;
    @EJB
    private KeywordManagerLocal keywordMgr;
    @EJB
    private MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    private CarrierManagerLocal carrierMgr;
    @EJB
    private MobileSystemMessageManagerLocal messageMgr;
    @EJB
    private GeoFenceManagerLocal geoFenceMgr;
    
    
    private static final String LOCAID_OPTIN_COMPLETE = "OPTIN_COMPLETE";
    
    public OptInOptOutREST() {
    }
    
    private String getCleanKeyword(String messageBody) {
        messageBody = messageBody.replaceAll("[^A-Za-z0-9 ]", "");
        String[] splitBody = messageBody.split("[\\s]");
        return splitBody[splitBody.length - 1];
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_PLAIN})
    public Response post(MultivaluedMap<String, String> formParams) {
        String msisdn = null;
        String messageBody = null;
        String carrierId = null;
        Subscriber subscriber = null;
        Carrier carrier = null;
        try {
            
            logger.info("OptInOpOutREST CALLED");
            for (String key : formParams.keySet()) {
                logger.info("Key: " + key + " Param: " + formParams.getFirst(key));
            }
            msisdn = formParams.getFirst("msisdn");
            if (msisdn != null) {
                msisdn = msisdn.replaceAll("\\D", "");
                if (!msisdn.startsWith("1") && msisdn.length() == 10) {
                    msisdn = "1" + msisdn;
                }
                
            } else {
                logger.warn("MSISDN can't be empty");
                return Response.status(Response.Status.BAD_REQUEST).entity("msisdn is not set up ").build();
            }
            carrierId = formParams.getFirst("carrier-id");
            if (carrierId != null) {
                try {
                    carrier = carrierMgr.find(Long.parseLong(carrierId));
                } catch (NumberFormatException ex) {
                    logger.error("Number Format Exception getting carrier with id: " + carrierId);
                    carrier = null;
                }
            }
            messageBody = getCleanKeyword(formParams.getFirst("message-body"));
            
            subscriber = subscriberMgr.findByMsisdn(msisdn);
            if (messageBody == null) {
                logger.warn("Message body can't be empty " + messageBody);
                return Response.status(Response.Status.BAD_REQUEST).entity("message-body is not set up ").build();
            } else {
                messageBody = messageBody.toUpperCase();
            }

            //subscriber opts-out
            if (messageBody.equalsIgnoreCase("STOP") || messageBody.equalsIgnoreCase("ALTO") || messageBody.equalsIgnoreCase("PARAR")) {
                if (subscriber == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Received stop request but subscriber does not exist!").build();
                }
                
                MobileSystemMessage message = new MobileSystemMessage();
                message.setMessage(messageBody);
                message.setMsisdn(msisdn);
                message.setBrand(subscriber.getBrand());
                messageMgr.create(message);
                
                Date optoutDate = new Date();
                subscriber.setSmsOptOutDate(optoutDate);
                subscriber.setTrackingOptOutDate(optoutDate);
                subscriber.setStatus(Subscriber.STATUS_OPT_OUT);
                subscriberMgr.update(subscriber);
                //2.opt-out to Locaid             
                Registrar registrar = new Registrar();

                //check locaid status
                String locaidStatus = registrar.getSingleMsisdnStatus(msisdn);
                logger.info("Subscriber status in LOCAID: " + locaidStatus);
                if (!locaidStatus.equalsIgnoreCase(Registrar.STATUS_OPTIN_COMPLETE) && !locaidStatus.equalsIgnoreCase(Registrar.STATUS_OPTIN_PENDING)) {

                    //subscriber not in locaid -- just update db
                    subscriber.setStatus(Subscriber.STATUS_OPT_OUT);
                } else {

                    //subscriber in locaid need to cancel loc-aid subscription
                    if (registrar.cancelSubscription(msisdn)) {
                        subscriber.setStatus(Subscriber.STATUS_OPT_OUT);
                    } else {
                        subscriber.setStatus(Subscriber.STATUS_CANCELLATION_FAILED);
                    }
                }
                
                subscriber.setLocaidStatus(locaidStatus);
                subscriberMgr.update(subscriber);
                SMSPlatformRESTClient client = new SMSPlatformRESTClient();
                try {
                    
                    client.optOutMsisdn(msisdn);
                    
                } catch (Exception ex) {
                    logger.error("exception thrown from kaze");
                    return Response.status((Response.Status.SERVICE_UNAVAILABLE)).entity("Opt-out service unavailable from Kaze").build();
                }
                return Response.status(Response.Status.OK).entity("Subscriber optin-optout complete").build();
                
            } else {
                if (messageBody.equalsIgnoreCase("Y") || messageBody.equalsIgnoreCase("YES")
                        || messageBody.equalsIgnoreCase("SI")) {
                    if (subscriber == null) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Subscriber not found").build();
                    }
                    
                    MobileSystemMessage message = new MobileSystemMessage();
                    message.setMessage(messageBody);
                    message.setMsisdn(msisdn);
                    message.setCompany(subscriber.getCompany());
                    message.setBrand(subscriber.getBrand());
                    messageMgr.create(message);
                    MobileOfferSettings settings = settingsMgr.findByBrand(subscriber.getCompany().getBrand());

                    boolean liveLocation = settings.isLiveGeoLocation();

                    if (subscriber.getStatus().equals(Subscriber.STATUS_SMS_OPT_IN_PENDING)) {
                        logger.info("MSISDN: " + msisdn + " REPLIED " + messageBody);
                        Date d = new Date();
                        subscriber.setSmsOptInDate(d);
                        subscriber.setTrackingOptInDate(d);
                        subscriber.setRegistrationDate(d);
                        if (carrier != null) {
                            subscriber.setCarrier(carrier);
                        }
                        subscriber.setStatus(Subscriber.STATUS_OPT_IN_COMPLETE);

                        //check locaid status
                        RegistrarLocal registrar;
                        if (liveLocation) {
                            registrar = new Registrar();
                        } else {
                            registrar = new FakeRegistrar();
                        }
                        String locaidStatus = registrar.getSingleMsisdnStatus(msisdn);
                        logger.info("Subscriber entered Y, status in LOCAID: " + locaidStatus);
                        if (locaidStatus != null && locaidStatus.equalsIgnoreCase(LOCAID_OPTIN_COMPLETE)) {
                            logger.info("Updating subscriber because locaid status is OPTIN_COMPLETE");

                            //user has already registered and has just entered a new keyword
                            subscriber.setLocaidStatus(locaidStatus);
                            subscriberMgr.update(subscriber);
                            return Response.status(Response.Status.OK).entity("Subscriber optin-optout complete").build();
                        }

                        //Register to locaid
                        List<Subscriber> subscribers = new ArrayList<Subscriber>();
                        subscribers.add(subscriber);
                        
                        logger.info("Registering with LOC-AID: " + msisdn);
                        RegistrarResponse registrarResponse = registrar.singleMsisdnRegistration(msisdn);
                        if (!registrarResponse.getRegistrationCode().equals(RegistrarResponse.REGISTRATION_SUCCESS_CODE)) {
                            if (registrarResponse.getRegistrationCode().equals(RegistrarResponse.CARRIER_UNSUPPORTED_CODE)) {
                                logger.info("Registering with LOC-AID FAILED for: " + msisdn + " reason: " + RegistrarResponse.CARRIER_UNSUPPORTED_MSG);
                                subscriber.setStatus(Subscriber.STATUS_CARRIER_UNSUPPORTED);
                                subscriberMgr.update(subscriber);
                                return Response.status(Response.Status.OK).entity("Subscriber's carrier is unsupported").build();
                            } else {
                                logger.info("Registering with LOC-AID FAILED for: " + msisdn + " reason: " + registrarResponse.getRegistrationMessage());
                                subscriber.setStatus(Subscriber.STATUS_TRACKING_FAILED);
                                subscriberMgr.update(subscriber);
                                return Response.status(Response.Status.OK).entity("Registration for location services failed.").build();
                            }
                        }
                        logger.info("Opting in with LOC-AID: " + msisdn);
                        RegistrarResponse optinResponse = registrar.singleOptIn(msisdn);
                        if (!optinResponse.getRegistrationCode().equals(RegistrarResponse.OPT_IN_SUCCESS_CODE)) {
                            logger.info("OPTING IN with LOC-AID FAILED: " + msisdn);
                            subscriber.setStatus(Subscriber.STATUS_TRACKING_FAILED);
                        }
                        
                        subscriberMgr.update(subscriber);

                        // when we opt in a subscriber, we need to immediately look them up and send
                        // an appropriate offer, if possible
                        try {
                            logger.info("Locating new subscriber " + subscriber.getMsisdn());
                            
                            
                            List<String> numbers = new ArrayList<String>();
                            numbers.add(subscriber.getMsisdn());
                            List<LocationData> results;
                            LocationFinderLocal finder;
                            if (liveLocation) {
                                finder = new LocationFinder();
                            } else {
                                finder = new FakeLocationFinder();
                            }
                            List<GeoFence> geoFences = geoFenceMgr.getGeoFencesByCompany(subscriber.getCompany());
                            finder.synchFind(numbers, 0, 0, locMgr, subscriberMgr, subscriber.getBrand(),settings, geoFences);
                            
                        } catch (Exception err) {
                            logger.error(err);
                        }
                        
                        return Response.status(Response.Status.OK).entity("Subscriber optin-optout complete").build();
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Received '" + messageBody + "' but subscriber not " + Subscriber.STATUS_SMS_OPT_IN_PENDING).build();
                    }
                } //We got a HELP request
                else {
                    if (messageBody.equalsIgnoreCase("HELP") || messageBody.equalsIgnoreCase("AYUDA")) {
                        if (subscriber == null) {
                            return Response.status(Response.Status.BAD_REQUEST).entity("Subscriber not found").build();
                        }
                        
                        MobileSystemMessage message = new MobileSystemMessage();
                        message.setMessage(messageBody);
                        message.setMsisdn(msisdn);
                        message.setCompany(subscriber.getCompany());
                        messageMgr.create(message);
                        return Response.status(Response.Status.OK).entity("Received a help request, adding to mobile_system_messages").build();
                    } //we have a subscriber that doesn't exist that sends a keyword
                    else {
                        if (subscriber == null) {
                            Keyword keyword = keywordMgr.getKeywordByKeywordString(messageBody);
                            Property optInProperty = null;
                            Company company = null;
                            Locale locale = null;
                            List<Locale> localesList = new ArrayList<Locale>();
                            if (keyword != null) {
                                company = keyword.getCompany();
                                locale = keyword.getLocale();
                                optInProperty = keyword.getProperty();
                                if (locale != null) {
                                    localesList.add(locale);
                                }
                            }
                            
                            if (company == null) {
                                //this keyword is no part of any company
                                logger.warn("Couln't find keyword: " + messageBody + " on any company");
                                return Response.status(Response.Status.BAD_REQUEST).entity("Message Body:" + messageBody + " doesn't exist in our system (This reserved keyword is not tied to any of our companies)").build();
                            } else {
                                MobileSystemMessage message = new MobileSystemMessage();
                                message.setMessage(messageBody);
                                message.setMsisdn(msisdn);
                                message.setCompany(company);
                                messageMgr.create(message);
                                
                                subscriber = new Subscriber();
                                subscriber.setKeyword(keyword);
                                subscriber.setRegistrationDate(new Date());
                                subscriber.setMsisdn(msisdn);
                                subscriber.setCompany(company);
                                subscriber.setBrand(company.getBrand());
                                subscriber.setOptInProperty(optInProperty);
                                
                                if (localesList != null && !localesList.isEmpty()) {
                                    subscriber.setLocales(localesList);
                                }
                                if (carrier != null) {
                                    subscriber.setCarrier(carrier);
                                }
                                subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                                subscriberMgr.create(subscriber);
                                
                                return Response.status(Response.Status.OK).entity("subscriber_opt_in_status_completed").build();
                            }
                            //we have a subscriber that exist and sends another or keyword, update subscriber    
                        } else {
                            Keyword keyword = keywordMgr.getKeywordByKeywordString(messageBody);
                            Property optInProperty = null;
                            Company company = null;
                            Locale locale = null;
                            List<Locale> localesList = new ArrayList<Locale>();
                            if (keyword != null) {
                                company = keyword.getCompany();
                                locale = keyword.getLocale();
                                optInProperty = keyword.getProperty(); 
                            }
                            localesList = subscriber.getLocales();
                            if (locale != null) {
                                localesList.add(locale);
                            }
                            if (keyword != null && company != null) {
                                subscriber.setKeyword(keyword);
                                subscriber.setRegistrationDate(new Date());
                                subscriber.setOptInProperty(optInProperty);
                                if (!subscriber.getStatus().equals(Subscriber.STATUS_OPT_IN_COMPLETE)) {
                                    subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                                }
                                if (localesList != null && !localesList.isEmpty()) {
                                    subscriber.setLocales(localesList);
                                }
                                if (carrier != null) {
                                    subscriber.setCarrier(carrier);
                                }
                                subscriber.setCompany(company);
                                subscriber.setBrand(company.getBrand());
                                subscriberMgr.update(subscriber);
                                return Response.status(Response.Status.OK).entity("subscriber_opt_in_status_completed").build();
                            }
                        }
                    }
                }
            }
            return Response.status(Response.Status.OK).entity("Nothing could be completed").build();
        } catch (Exception err) {
            logger.error("**************************");
            logger.error(err);
            logger.error("OptInOpOutREST Error: subsc: " + subscriber + " - " + "msisdn: " + msisdn + " - messageBody: " + messageBody);
            for (String key : formParams.keySet()) {
                logger.error("Key: " + key + " Param: " + formParams.getFirst(key));
            }
            logger.error("**************************");
            return Response.status(Response.Status.BAD_REQUEST).entity("Subscriber opt in/out was not processed correctly").build();
        }
        
    }
}
