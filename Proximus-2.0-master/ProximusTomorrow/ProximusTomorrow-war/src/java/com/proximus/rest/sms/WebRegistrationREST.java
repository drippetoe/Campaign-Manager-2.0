/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.WebRegistrationResponse;
import com.proximus.data.sms.Carrier;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.CarrierManagerLocal;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
@Path("/webregistration")
@RequestScoped
public class WebRegistrationREST {

    static final Logger logger = Logger.getLogger(WebRegistrationREST.class.getName());
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/webregistration/";
//   static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/webregistration/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private SubscriberManagerLocal subscriberMgr;
    @EJB
    CategoryManagerLocal categoryMgr;
    @EJB
    private UserManagerLocal userMgr;
    @EJB
    private CarrierManagerLocal carrierMgr;

    public WebRegistrationREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        WebRegistrationResponse registrationResponse = new WebRegistrationResponse();
        try {
            //get params
            String responseType = "";
            String msisdn = formParams.getFirst("msisdn");
            String emailAddress = formParams.getFirst("email");
            String zipcode = formParams.getFirst("zipcode");
            String carrier = formParams.getFirst("carrier");
            String gender = formParams.getFirst("gender");
            String categories = formParams.getFirst("category");
            String userName = formParams.getFirst("username");
            String keyword = formParams.getFirst("keyword");
            String requestToken = formParams.getFirst("token");
            if (formParams.getFirst("responseType") != null) {
                responseType = formParams.getFirst("responseType");
                if (responseType.equalsIgnoreCase("JSON")) {
                    return post_JSON(msisdn, emailAddress, zipcode, carrier,
                            gender, categories, userName, requestToken);
                }
            }

            if ( keyword == null || keyword.isEmpty() )
            {
                keyword = "WEBSITE";
            }

            Carrier myCarrier = null;

            logger.info("WebRegistrationREST CALLED");

            if (( msisdn != null ) && ( msisdn.length() == 10 ))
            {
                    // assume US country code if not specified
                    msisdn = "1" + msisdn;
            }

            if (userName != null && requestToken != null) {
                try {
                    userName = URLDecoder.decode(userName, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.fatal("Decoding Exception: " + ex);
                    registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                    return Response.status(Response.Status.BAD_REQUEST).entity(registrationResponse).build();
                }
            } else {
                logger.warn("User name and request token can't be empty.");
                registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).entity(registrationResponse).build();
            }
            User user = userMgr.getUserByUsername(userName);
            if (user == null) {
                logger.warn("User: " + userName + " not found.");
                registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.INVALID_CREDENTIALS);
                return Response.status(Response.Status.BAD_REQUEST).entity(registrationResponse).build();
            }
            Company company = user.getCompanies().get(0);
            String password = user.getApiPassword();
            String requestCategory = "";
            if (categories != null) {
                requestCategory = categories;
            }


            String requestURL = BASE_URL + userName + "/" + password
                    + "/" + msisdn
                    + "/" + emailAddress
                    + "/" + zipcode
                    + "/" + carrier
                    + "/" + gender
                    + "/" + requestCategory;

            String companySalt = company.getSalt();
            logger.warn("url to encode: " + requestURL);
            logger.warn("campany SALT: " + companySalt);

            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                logger.warn("Opting in to Kaze Platform with msisdn: " + msisdn);
                SMSPlatformRESTClient rest = new SMSPlatformRESTClient();
                try {
                    rest.optInMsisdn(msisdn, keyword, carrier);
                } catch (Exception ex) {
                    logger.error("Exception Thrown: " + ex);
                    registrationResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.SERVICE_UNAVAILABLE);
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(registrationResponse).build();
                }

                List<String> categoryList = Arrays.asList(categories.split("\\s*,\\s*"));
                List<Category> realCategories = new ArrayList<Category>();
                for (String name : categoryList) {
                    Category category = categoryMgr.getCategoryByBrandAndName(company.getBrand(), name);
                    if (category != null) {
                        realCategories.add(category);
                    }
                }
                try {
                    myCarrier = carrierMgr.findByName(carrier);
                } catch (Exception ex) {
                    myCarrier = null;
                    logger.error("exception in web registration - carrier mgr:" + ex);
                }
                if (msisdn != null) {
                    Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
                    if (subscriber == null) {
                        subscriber = new Subscriber();
                        subscriber.setRegistrationDate(new Date());
                        subscriber.setMsisdn(msisdn);
                        if (emailAddress != null) {
                            subscriber.setEmail(emailAddress.toLowerCase());
                        }
                        if (myCarrier != null) {
                            subscriber.setCarrier(myCarrier);
                        }
                        subscriber.setZipcode(zipcode);
                        subscriber.setCompany(company);
                        subscriber.setBrand(company.getBrand());
                        if (gender != null) {
                            subscriber.setGender(gender.toLowerCase());
                        }
                        subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                        subscriber.setCategories(realCategories);
                        subscriberMgr.update(subscriber);
                        registrationResponse.setStatus(Response.Status.OK.toString());
                        registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                        registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                        logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                        return Response.status(Response.Status.OK).entity(registrationResponse).build();
                    } else {
                        subscriber.setSmsOptInDate(null);
                        subscriber.setTrackingOptInDate(null);
                        subscriber.setSmsOptOutDate(null);
                        subscriber.setTrackingOptOutDate(null);
                        subscriber.setRegistrationDate(new Date());
                        subscriber.setMsisdn(msisdn);
                        subscriber.setCompany(company);
                        subscriber.setBrand(company.getBrand());
                        subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                        subscriberMgr.update(subscriber);
                        registrationResponse.setStatus(Response.Status.OK.toString());
                        registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                        registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                        logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                        return Response.status(Response.Status.OK).entity(registrationResponse).build();
                    }
                } else {
                    registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                    logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                    return Response.status(Response.Status.BAD_REQUEST).entity(registrationResponse).build();
                }

            } else {
                registrationResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.INVALID_CREDENTIALS);
                logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(registrationResponse).build();
            }

        } catch (Exception ex) {
            logger.fatal("WebRegistrationREST fatal exception: " + ex);
            WebRegistrationResponse errorResponse = new WebRegistrationResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(WebRegistrationResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    public Response post_JSON(String msisdn, String emailAddress, String zipcode, String carrier,
            String gender, String categories, String userName, String requestToken) {
        try {
            WebRegistrationResponse registrationResponse = new WebRegistrationResponse();
            Carrier myCarrier = null;
            if (userName != null && requestToken != null) {
                try {
                    userName = URLDecoder.decode(userName, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.fatal("Decoding Exception: " + ex);
                    registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                    return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
                }
            } else {
                logger.warn("User name and request token can't be empty.");
                registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
            }
            User user = userMgr.getUserByUsername(userName);
            if (user == null) {
                logger.warn("User: " + userName + "not found.");
                registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.INVALID_CREDENTIALS);
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
            }
            Company company = user.getCompanies().get(0);
            String password = user.getApiPassword();
            String requestCategory = "";
            if (categories != null) {
                requestCategory = categories;
            }


            String requestURL = BASE_URL + userName + "/" + password
                    + "/" + msisdn
                    + "/" + emailAddress
                    + "/" + zipcode
                    + "/" + carrier
                    + "/" + gender
                    + "/" + requestCategory;

            String companySalt = company.getSalt();
            logger.warn("url to encode: " + requestURL);
            logger.warn("campany SALT: " + companySalt);

            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                logger.warn("Opting in to Kaze Platform with msisdn: " + msisdn);
                SMSPlatformRESTClient rest = new SMSPlatformRESTClient();
                try {
                    rest.optInMsisdn(msisdn, "WEBSITE", carrier);
                } catch (Exception ex) {
                    logger.error("Exception Thrown: " + ex);
                    registrationResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.SERVICE_UNAVAILABLE);
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
                }

                List<String> categoryList = Arrays.asList(categories.split("\\s*,\\s*"));
                List<Category> realCategories = new ArrayList<Category>();
                for (String name : categoryList) {
                    Category category = categoryMgr.getCategoryByBrandAndName(company.getBrand(), name);
                    if (category != null) {
                        realCategories.add(category);
                    }
                }
                try {
                    myCarrier = carrierMgr.findByName(carrier);
                } catch (Exception ex) {
                    myCarrier = null;
                    logger.error("exception in web registration - carrier mgr:" + ex);
                }
                if (msisdn != null) {
                    Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
                    if (subscriber == null) {
                        subscriber = new Subscriber();
                        subscriber.setRegistrationDate(new Date());
                        subscriber.setMsisdn(msisdn);
                        if (emailAddress != null) {
                            subscriber.setEmail(emailAddress.toLowerCase());
                        }
                        if (myCarrier != null) {
                            subscriber.setCarrier(myCarrier);
                        }
                        subscriber.setZipcode(zipcode);
                        subscriber.setCompany(company);
                        subscriber.setBrand(company.getBrand());
                        if (gender != null) {
                            subscriber.setGender(gender.toLowerCase());
                        }
                        subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                        subscriber.setCategories(realCategories);
                        subscriberMgr.update(subscriber);
                        registrationResponse.setStatus(Response.Status.OK.toString());
                        registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                        registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                        logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
                    } else {
                        subscriber.setSmsOptInDate(null);
                        subscriber.setTrackingOptInDate(null);
                        subscriber.setSmsOptOutDate(null);
                        subscriber.setTrackingOptOutDate(null);
                        subscriber.setRegistrationDate(new Date());
                        subscriber.setMsisdn(msisdn);
                        subscriber.setCompany(company);
                        subscriber.setBrand(company.getBrand());
                        subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                        subscriberMgr.update(subscriber);
                        registrationResponse.setStatus(Response.Status.OK.toString());
                        registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                        registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                        logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
                    }
                } else {
                    registrationResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.MISSING_PARAMETERS);
                    logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                    return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
                }

            } else {

                registrationResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.INVALID_CREDENTIALS);
                logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(registrationResponse).build();
            }

        } catch (Exception ex) {
            logger.fatal("WebRegistrationREST fatal exception: " + ex);
            WebRegistrationResponse errorResponse = new WebRegistrationResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(WebRegistrationResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
        }
    }
}
