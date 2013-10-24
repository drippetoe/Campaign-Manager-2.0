/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.OfferPropertyResponse;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Property;
import com.proximus.data.sms.report.ViewActiveOffers;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.manager.sms.report.ViewActiveOffersManagerLocal;
import com.proximus.manager.web.WebOfferManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald
 */
@Path("/propertyoffers")
@RequestScoped
public class OfferPropertyREST {

    static final Logger logger = Logger.getLogger(OfferPropertyREST.class.getName());
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/propertyoffers/";
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/propertyoffers/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    WebOfferManagerLocal webOfferMgr;
    @EJB
    UserManagerLocal userMgr;
    @EJB
    CategoryManagerLocal categoryMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    ViewActiveOffersManagerLocal offersMgr;

    public OfferPropertyREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        String locale = formParams.getFirst("locale");
        String propertyWebHash = formParams.getFirst("propertyHash");
        String categories = formParams.getFirst("category");
        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");

        String decodedUserName;

        logger.info("locale: " + locale);
        logger.info("propertyWebHash: " + propertyWebHash);
        logger.info("categories: " + categories);
        logger.info("user: " + userName);

        OfferPropertyResponse offerPropertyResponse = new OfferPropertyResponse();

        if (userName != null && requestToken != null) {
            try {
                decodedUserName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                offerPropertyResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                offerPropertyResponse.setStatus_message(OfferPropertyResponse.ERROR);
                return Response.status(Response.Status.BAD_REQUEST).entity(offerPropertyResponse).build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            offerPropertyResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            offerPropertyResponse.setStatus_message(OfferPropertyResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(offerPropertyResponse).build();
        }
        User user = userMgr.getUserByUsername(decodedUserName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            offerPropertyResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            offerPropertyResponse.setStatus_message(OfferPropertyResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(offerPropertyResponse).build();
        }
        Company company = user.getCompanies().get(0);
        String password = user.getApiPassword();


        String requestURL = BASE_URL + userName + "/" + password;

        String companySalt = company.getSalt();
        logger.warn("url to encode: " + requestURL);
        logger.warn("campany SALT: " + companySalt);

        if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {

            try {
                List<Category> categoryList = getCategoryList(categories, company);
                List<ViewActiveOffers> webOffers = getOfferList(locale, propertyWebHash, categoryList);
                if (webOffers == null || webOffers.isEmpty()) {
                    logger.warn("Offers were not found for your combination of web hash and locale.");
                    offerPropertyResponse.setStatus(Response.Status.NOT_FOUND.toString());
                    offerPropertyResponse.setStatus_message(OfferPropertyResponse.ERROR);
                    return Response.status(Response.Status.NOT_FOUND).entity(offerPropertyResponse).build();
                }

                offerPropertyResponse.setPropertyOffers(webOffers);
                offerPropertyResponse.setStatus(Response.Status.OK.toString());
                offerPropertyResponse.setStatus_message(OfferPropertyResponse.SUCCESSFUL);
                logger.info(offerPropertyResponse.getStatus() + " : " + offerPropertyResponse.getStatus_message());
                return Response.status(Response.Status.OK).entity(offerPropertyResponse).build();

            } catch (Exception ex) {
                logger.fatal("OfferPropertyREST fatal exception: " + ex);
                OfferPropertyResponse errorResponse = new OfferPropertyResponse();
                errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
                errorResponse.setStatus_message(OfferPropertyResponse.SERVICE_UNAVAILABLE);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
            }
        } else {
            offerPropertyResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
            offerPropertyResponse.setStatus_message(OfferPropertyResponse.INVALID_CREDENTIALS);
            logger.info(offerPropertyResponse.getStatus() + " : " + offerPropertyResponse.getStatus_message());
            return Response.status(Response.Status.UNAUTHORIZED).entity(offerPropertyResponse).build();
        }
    }

    private List<Category> getCategoryList(String categories, Company company) {
        List<Category> realCategories = new ArrayList<Category>();
        if (categories != null) {
            String decodedCategories = null;
            try {
                decodedCategories = URLDecoder.decode(categories, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.info("Error decoding categories" + categories);
            }
            if (decodedCategories != null) {
                decodedCategories = decodedCategories.toLowerCase();
            }
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
        }
        return realCategories;
    }

    private List<ViewActiveOffers> getOfferList(String locale, String propertyWebHash, List<Category> realCategories) {
        List<ViewActiveOffers> fullOfferSet = null;
        Property property = propertyMgr.getPropertyByWebHash(propertyWebHash);
        if (property != null) {
            if (realCategories != null || !realCategories.isEmpty()) {
                fullOfferSet = new ArrayList<ViewActiveOffers>();
                for (Category category : realCategories) {
                    List<ViewActiveOffers> offersByCategory = offersMgr.getByPropertyAndCategoryAndLanguageCode(property, category,locale);
                    if (offersByCategory != null) {
                        for (ViewActiveOffers categoryOffer : offersByCategory) {
                            if (!fullOfferSet.contains(categoryOffer)) {
                                fullOfferSet.add(categoryOffer);
                            }
                        }
                    }
                }
            }
            if (fullOfferSet == null || fullOfferSet.isEmpty()) {
                fullOfferSet = offersMgr.getByPropertyAndLanguageCode(property,locale);
            }
        }
        return fullOfferSet;
    }
}
