/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.CarrierResponse;
import com.proximus.data.sms.Carrier;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.CarrierManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Angela Mercer
 */
@Path("/carriers/")
@RequestScoped
public class CarriersREST
{
    static final Logger logger = Logger.getLogger(CarriersREST.class.getName());
    /*
     * TODO: Make this for DDR
     */
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/carriers/";
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/carriers/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private CarrierManagerLocal carrierMgr;
    @EJB
    private UserManagerLocal userMgr;

    public CarriersREST()
    {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams)
    {
        logger.info("CategoryListREST CALLED");
        
        try {
//       get params
            String userName = formParams.getFirst("username");
            String requestToken = formParams.getFirst("token");
            String list = "";
            if(formParams.getFirst("list") != null){
                list = formParams.getFirst("list");
            } 
            String responseType = "";
            if (formParams.getFirst("responseType") != null) {
                responseType = formParams.getFirst("responseType");
                if (responseType.equalsIgnoreCase("JSON")) {
                    return post_JSON(userName, requestToken, list);
                }
            }
            CarrierResponse carrierResponse = new CarrierResponse();
            String decodedUserName = null;
            if (userName != null && requestToken != null) {
                try {
                    decodedUserName = URLDecoder.decode(userName, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.fatal("Decoding Exception: " + ex);
                    carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                    carrierResponse.setStatus_message(CarrierResponse.MISSING_PARAMETERS);
                    return Response.status(Response.Status.BAD_REQUEST).entity(carrierResponse).build();
                }
            } else {
                logger.warn("User name and request token can't be empty.");
                carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                carrierResponse.setStatus_message(CarrierResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).entity(carrierResponse).build();
            }
            User user = userMgr.getUserByUsername(decodedUserName);
            if (user == null) {
                logger.warn("User: " + userName + " not found.");
                carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                carrierResponse.setStatus_message(CarrierResponse.INVALID_CREDENTIALS);
                return Response.status(Response.Status.BAD_REQUEST).entity(carrierResponse).build();
            }

            //company info user api password and company SALT
            Company company = user.getCompanies().get(0);
            String password = user.getApiPassword();
            String requestURL = BASE_URL + userName + "/" + password;
            String companySalt = company.getSalt();
            
            logger.warn("url to encode: " + requestURL);
            logger.warn("campany SALT: " + companySalt);
            System.out.println("url to encode: " + requestURL);
            System.out.println("campany SALT: " + companySalt);

            //authenticate
            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
                List<Carrier> carriers = new ArrayList<Carrier>();
                if(list.equalsIgnoreCase("all") || list == null || list.isEmpty()) {
                    carriers = carrierMgr.findAll();
                }    
                else if(list.equalsIgnoreCase("supported")) {
                    carriers = carrierMgr.findAllSupported();
                }
                carrierResponse.setCarriers(carriers);
                carrierResponse.setStatus(Response.Status.OK.toString());
                carrierResponse.setStatus_message(CarrierResponse.SUCCESSFUL);
                logger.info(carrierResponse.getStatus() + " : " + carrierResponse.getStatus_message());
                return Response.status(Response.Status.OK).entity(carrierResponse).build();
            } else {
                carrierResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
                carrierResponse.setStatus_message(CarrierResponse.INVALID_CREDENTIALS);
                logger.info(carrierResponse.getStatus() + " : " + carrierResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).entity(carrierResponse).build();
            }

        } catch (Exception ex) {
            logger.fatal("CarriersREST fatal exception: " + ex);
            CarrierResponse errorResponse = new CarrierResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(CarrierResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();


        }
    }

    public Response post_JSON(String userName, String requestToken, String list)
    {
        try {
            CarrierResponse carrierResponse = new CarrierResponse();
            String decodedUserName = null;
            if (userName != null && requestToken != null) {
                try {
                    decodedUserName = URLDecoder.decode(userName, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    logger.fatal("Decoding Exception: " + ex);
                   carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                   carrierResponse.setStatus_message(CarrierResponse.MISSING_PARAMETERS);
                    return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(carrierResponse).build();
                }
            } else {
                logger.warn("User name and request token can't be empty.");
               carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
               carrierResponse.setStatus_message(CarrierResponse.MISSING_PARAMETERS);
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(carrierResponse).build();
            }
            User user = userMgr.getUserByUsername(decodedUserName);
            if (user == null) {
                logger.warn("User: " + userName + " not found.");
               carrierResponse.setStatus(Response.Status.BAD_REQUEST.toString());
               carrierResponse.setStatus_message(CarrierResponse.INVALID_CREDENTIALS);
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(carrierResponse).build();
            }
            //company info user api password and company SALT
            Company company = user.getCompanies().get(0);
            String password = user.getApiPassword();
            String requestURL = BASE_URL + userName + "/" + password;
            String companySalt = company.getSalt();
            
            logger.warn("url to encode: " + requestURL);
            logger.warn("campany SALT: " + companySalt);

            //authenticate
            if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {
               List<Carrier> carriers = new ArrayList<Carrier>();
                if(list.equalsIgnoreCase("all") || list == null || list.isEmpty()) {
                    carriers = carrierMgr.findAll();
                }    
                else if(list.equalsIgnoreCase("supported")) {
                    carriers = carrierMgr.findAllSupported();
                }

               carrierResponse.setCarriers(carriers);
               carrierResponse.setStatus(Response.Status.OK.toString());
               carrierResponse.setStatus_message(CarrierResponse.SUCCESSFUL);
                logger.info(carrierResponse.getStatus() + " : " +carrierResponse.getStatus_message());
                return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(carrierResponse).build();
            } else {
               carrierResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
               carrierResponse.setStatus_message(CarrierResponse.INVALID_CREDENTIALS);
                logger.info(carrierResponse.getStatus() + " : " +carrierResponse.getStatus_message());
                return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(carrierResponse).build();
            }
        } catch (Exception ex) {
            logger.fatal("CarriersREST fatal exception: " + ex);
            CarrierResponse errorResponse = new CarrierResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(CarrierResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();


        }
    }
}