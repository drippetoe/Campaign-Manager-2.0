/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.PreferenceReponse;
import com.proximus.data.response.WebInteractionResponse;
import com.proximus.data.response.WebRegistrationResponse;
import com.proximus.data.sms.Carrier;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Keyword;
import com.proximus.data.sms.Locale;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.CarrierManagerLocal;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.KeywordManagerLocal;
import com.proximus.manager.sms.LocaleManagerLocal;
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
@Path("/webinteraction")
@RequestScoped
public class WebInteractionREST {

    static final Logger logger = Logger.getLogger(WebInteractionREST.class.getName());
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/webinteraction/";
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/webinteraction/";
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
    @EJB
    private LocaleManagerLocal localeMgr;
    @EJB
    private KeywordManagerLocal keywordMgr;

    public WebInteractionREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        String msisdn = formParams.getFirst("msisdn");
        String emailAddress = formParams.getFirst("email");
        String zipcode = formParams.getFirst("zipcode");
        String carrier = formParams.getFirst("carrier");
        String gender = formParams.getFirst("gender");
        String categories = formParams.getFirst("category");
        String keyword = formParams.getFirst("keyword");
        String locales = formParams.getFirst("locale");
        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");

        Company company;
        String password;
        String requestURL;
        String companySalt;
        String decodedUserName;

        logger.info("email: " + emailAddress);
        logger.info("zipcode: " + zipcode);
        logger.info("carrier: " + carrier);
        logger.info("gender: " + gender);
        logger.info("categories: " + categories);
        logger.info("keyword: " + keyword);
        logger.info("locale: " + locales);
        logger.info("user: " + userName);

        WebInteractionResponse webActionResponse = new WebInteractionResponse();
        if (userName != null && requestToken != null) {
            try {
                decodedUserName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                webActionResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                webActionResponse.setStatus_message(PreferenceReponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).entity(webActionResponse).build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            webActionResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            webActionResponse.setStatus_message(PreferenceReponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(webActionResponse).build();
        }
        User user = userMgr.getUserByUsername(decodedUserName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            webActionResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            webActionResponse.setStatus_message(PreferenceReponse.INVALID_CREDENTIALS);
            return Response.status(Response.Status.BAD_REQUEST).entity(webActionResponse).build();
        }
        /*
         * TODO: Get the company in a better way than this.
         */
        company = user.getCompanies().get(0);
        password = user.getApiPassword();
        requestURL = BASE_URL + userName + "/" + password;
        companySalt = company.getSalt();

        logger.warn("url to encode: " + requestURL);
        logger.warn("campany SALT: " + companySalt);
        logger.debug("request token: " + requestToken);


        if (msisdn != null) {
            msisdn = msisdn.replaceAll("\\D", "");
            if ((msisdn != null) && (msisdn.length() == 10)) {
                // assume US country code if not specified
                msisdn = "1" + msisdn;
            }
            logger.info("msisdn: " + msisdn);

            return webRegistrationResponse(userName, requestToken, password, requestURL, companySalt, company, msisdn,
                    emailAddress, zipcode, carrier, gender, categories, locales, keyword);
        } else {
            return preferenceResponse(userName, requestToken, password, requestURL, companySalt, company);
        }

    }

    public Response webRegistrationResponse(String userName, String requestToken, String password, String requestURL, String companySalt,
            Company company, String msisdn, String emailAddress, String zipcode, String carrier, String gender, String categories, String locales, String keyword) {
        WebRegistrationResponse registrationResponse = new WebRegistrationResponse();
        try {
            logger.info("Web Registration Response CALLED");

            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {

                /* Get category by id, they dont send them to us this way though*/
//                List<String> categoryList = Arrays.asList(categories.split(","));
//                List<Category> realCategories = new ArrayList<Category>();
//                for (String id : categoryList) {
//                    Category category = categoryMgr.find(Long.parseLong(id));
//                    if (category != null) {
//                        realCategories.add(category);
//                    }
//                }
                String decodedCategories = null;
                try {
                    decodedCategories = URLDecoder.decode(categories, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.info("Error decoding categories" + categories);
                }
                if (decodedCategories != null) {
                    decodedCategories = decodedCategories.toLowerCase();
                    }
                List<Category> realCategories = new ArrayList<Category>();
                try {
                    List<String> categoryList = Arrays.asList(decodedCategories.split("\\s*,\\s*"));
                    for (String name : categoryList) {
                        Category category = categoryMgr.getCategoryByBrandAndWebSafeName(company.getBrand(), name);
                        if (category != null) {
                            realCategories.add(category);
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Could not parse categories: " + categories);
                    logger.error(ex.getMessage());
                    realCategories = null;
                }

                List<Locale> realLocales = new ArrayList<Locale>();
                try {
                    List<String> localeList = Arrays.asList(locales.split(","));
                    for (String languageCode : localeList) {
                        Locale locale = localeMgr.getLocaleByLanguageCode(languageCode);
                        if (locale != null) {
                            realLocales.add(locale);
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Could not parse locales: " + locales);
                    logger.error(ex.getMessage());
                    realLocales = null;
                }

                Carrier myCarrier = carrierMgr.findByName(carrier);

                Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
                if (subscriber == null) {
                    if (keyword == null || keyword.isEmpty()) {
                        keyword = "WEBSITE";
                    }
                    Keyword realKeyword = keywordMgr.getKeywordByKeywordString(keyword.toUpperCase());
                    if (realKeyword != null && !realLocales.contains(realKeyword.getLocale())) {
                        realLocales.add(realKeyword.getLocale());
                    }

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
                    subscriber = new Subscriber();
                    subscriber.setRegistrationDate(new Date());
                    subscriber.setMsisdn(msisdn);
                    if (emailAddress != null) {
                        subscriber.setEmail(emailAddress.toLowerCase());
                    }
                    subscriber.setCarrier(myCarrier);

                    subscriber.setZipcode(zipcode);
                    subscriber.setCompany(company);
                    subscriber.setBrand(company.getBrand());
                    if (gender != null) {
                        subscriber.setGender(gender.toLowerCase());
                    }
                    subscriber.setStatus(Subscriber.STATUS_SMS_OPT_IN_PENDING);
                    subscriber.setCategories(realCategories);
                    subscriber.setLocales(realLocales);
                    subscriber.setKeyword(realKeyword);
                    subscriberMgr.update(subscriber);
                    registrationResponse.setStatus(Response.Status.OK.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                    registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                    logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                    return Response.status(Response.Status.OK).entity(registrationResponse).build();
                } else {
                    //Just set their new preferences
                    subscriber.setLocales(realLocales);
                    subscriber.setCategories(realCategories);
                    subscriberMgr.update(subscriber);
                    registrationResponse.setStatus(Response.Status.OK.toString());
                    registrationResponse.setStatus_message(WebRegistrationResponse.SUCCESSFUL);
                    registrationResponse.setSubscriber(subscriberMgr.findByMsisdn(msisdn));
                    logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                    return Response.status(Response.Status.OK).entity(registrationResponse).build();
                }


            } else {
                registrationResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
                registrationResponse.setStatus_message(WebRegistrationResponse.INVALID_CREDENTIALS);
                logger.info(registrationResponse.getStatus() + " : " + registrationResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(registrationResponse).build();
            }

        } catch (Exception ex) {
            logger.fatal("Web Registration Response fatal exception: " + ex);
            WebRegistrationResponse errorResponse = new WebRegistrationResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(WebRegistrationResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    public Response preferenceResponse(String userName, String requestToken, String password, String requestURL, String companySalt, Company company) {
        try {
            logger.info("Preference Response CALLED");

            PreferenceReponse preferenceResponse = new PreferenceReponse();
            //authenticate
            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                List<Carrier> carriers = carrierMgr.findAllSupported();
                List<Category> categories = categoryMgr.getAllByBrand(company.getBrand());
                List<Locale> locales = localeMgr.getAllSorted();

                preferenceResponse.setLocales(locales);
                preferenceResponse.setCategories(categories);
                preferenceResponse.setCarriers(carriers);
                preferenceResponse.setStatus(Response.Status.OK.toString());
                preferenceResponse.setStatus_message(PreferenceReponse.SUCCESSFUL);
                logger.info(preferenceResponse.getStatus() + " : " + preferenceResponse.getStatus_message());
                return Response.status(Response.Status.OK).entity(preferenceResponse).build();
            } else {
                preferenceResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
                preferenceResponse.setStatus_message(PreferenceReponse.INVALID_CREDENTIALS);
                logger.info(preferenceResponse.getStatus() + " : " + preferenceResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(preferenceResponse).build();
            }
        } catch (Exception ex) {
            logger.fatal("Preference Response fatal exception: " + ex);
            PreferenceReponse errorResponse = new PreferenceReponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(PreferenceReponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }
}
