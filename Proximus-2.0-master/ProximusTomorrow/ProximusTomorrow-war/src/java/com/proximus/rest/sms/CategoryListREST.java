/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.CategoryResponse;
import com.proximus.data.sms.Category;
import com.proximus.data.sms.Locale;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.CategoryManagerLocal;
import com.proximus.manager.sms.LocaleManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
@Path("/categorylist/")
@RequestScoped
public class CategoryListREST {

    static final Logger logger = Logger.getLogger(CategoryListREST.class.getName());
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/categorylist/";
    static final String BASE_URL = "https://secure.proximusmobility.com/ProximusTomorrow-war/api/categorylist/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private CategoryManagerLocal categoryMgr;
    @EJB
    private UserManagerLocal userMgr;
    @EJB
    private LocaleManagerLocal localeMgr;

    public CategoryListREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {

        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");
        String locale = formParams.getFirst("locale");


        Company company;
        String password;
        String requestURL;
        String companySalt;
        String decodedUserName;

        logger.info("user: " + userName);

        CategoryResponse defaultResponse = new CategoryResponse();
        if (userName != null && requestToken != null) {
            try {
                decodedUserName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
                defaultResponse.setStatus_message(CategoryResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
            defaultResponse.setStatus_message(CategoryResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
        }
        User user = userMgr.getUserByUsername(decodedUserName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            defaultResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode() + "");
            defaultResponse.setStatus_message(CategoryResponse.INVALID_CREDENTIALS);
            return Response.status(Response.Status.BAD_REQUEST).entity(defaultResponse).build();
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

        return categoryListResponse(userName, requestToken, password,
                requestURL, companySalt, company, locale);
    }

    public Response categoryListResponse(String userName, String requestToken,
            String password, String requestURL, String companySalt,
            Company company, String locale) {
        try {
            logger.info("Category List Response CALLED");

            CategoryResponse categoryResponse = new CategoryResponse();
            //authenticate
            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                Locale realLocale = localeMgr.getLocaleByLanguageCode(locale);

                List<Category> categoryList = categoryMgr.getAllByBrandAndLocale(company.getBrand(), realLocale);
                categoryResponse.setCategories(categoryList);
                categoryResponse.setStatus(Response.Status.OK.getStatusCode() + "");
                categoryResponse.setStatus_message(CategoryResponse.SUCCESSFUL);
                logger.info(categoryResponse.getStatus() + " : " + categoryResponse.getStatus_message());
                return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(categoryResponse).build();
            } else {
                categoryResponse.setStatus(Response.Status.UNAUTHORIZED.getStatusCode() + "");
                categoryResponse.setStatus_message(CategoryResponse.INVALID_CREDENTIALS);
                logger.info(categoryResponse.getStatus() + " : " + categoryResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(categoryResponse).build();
            }
        } catch (Exception ex) {
            logger.fatal("CCategory List Response fatal exception: " + ex);
            CategoryResponse errorResponse = new CategoryResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.getStatusCode() + "");
            errorResponse.setStatus_message(CategoryResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }
}
