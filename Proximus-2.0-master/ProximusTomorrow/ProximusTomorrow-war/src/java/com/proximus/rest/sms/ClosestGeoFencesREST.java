/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.ClosestGeoFencesResponse;
import com.proximus.data.response.ClosestPropertyResponse;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import com.proximus.util.server.GeoUtil;
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
@Path("/closestgeofences")
@RequestScoped
public class ClosestGeoFencesREST {

    static final double DEFAULT_DISTANCE = 10.0;
    static final long DEFAULT_RESULTS = 3L;
    static final Logger logger = Logger.getLogger(ClosestGeoFencesREST.class.getName());
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/closestgeofences/";
//    static final String BASE_URL = "http://localhost:8080/ProximusTomorrow-war/api/closestgeofences/";
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    UserManagerLocal userMgr;
    @EJB
    MobileOfferSettingsManagerLocal settingsMgr;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;

    public ClosestGeoFencesREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        String latitude = formParams.getFirst("latitude");
        String longitude = formParams.getFirst("longitude");
        String distance = formParams.getFirst("maxDistance");
        String maxResults = formParams.getFirst("maxResults");
        String userName = formParams.getFirst("username");
        String requestToken = formParams.getFirst("token");

        String decodedUserName;

        logger.info("latitude: " + latitude);
        logger.info("longitude: " + longitude);
        logger.info("maxDistance: " + distance);
        logger.info("maxResults: " + maxResults);
        logger.info("user: " + userName);

        ClosestGeoFencesResponse geoFencesResponse = new ClosestGeoFencesResponse();

        if (userName != null && requestToken != null) {
            try {
                decodedUserName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.fatal("Decoding Exception: " + ex);
                geoFencesResponse.setStatus(Response.Status.BAD_REQUEST.toString());
                geoFencesResponse.setStatus_message(ClosestGeoFencesResponse.ERROR);
                return Response.status(Response.Status.BAD_REQUEST).entity(geoFencesResponse).build();
            }
        } else {
            logger.warn("User name and request token can't be empty.");
            geoFencesResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            geoFencesResponse.setStatus_message(ClosestGeoFencesResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(geoFencesResponse).build();
        }
        User user = userMgr.getUserByUsername(decodedUserName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            geoFencesResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            geoFencesResponse.setStatus_message(ClosestGeoFencesResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(geoFencesResponse).build();
        }

        Company company = user.getCompanies().get(0);
        String password = user.getApiPassword();


        String requestURL = BASE_URL + userName + "/" + password;

        String companySalt = company.getSalt();
        logger.warn("url to encode: " + requestURL);
        logger.warn("campany SALT: " + companySalt);

        if (RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt)) {

            try {
                MobileOfferSettings mobileOfferSettings = settingsMgr.findByBrand(company.getBrand());
                List<GeoFence> allGeoFences = geoFenceMgr.getGeoFencesByBrand(company.getBrand());
                List<GeoFence> closestGeoFenceList;
                double geoFenceDistance;
                long geoFenceRange;
                if (distance == null || distance.isEmpty()) {
                    geoFenceDistance = DEFAULT_DISTANCE;
                } else {
                    try {
                        geoFenceDistance = Double.parseDouble(distance);
                    } catch (Exception ex) {
                        geoFenceDistance = DEFAULT_DISTANCE;
                    }
                }
                if (maxResults == null || maxResults.isEmpty()) {
                    geoFenceRange = DEFAULT_RESULTS;
                } else {
                    try {
                        geoFenceRange = Long.parseLong(maxResults);
                    } catch (Exception ex) {
                        geoFenceRange = DEFAULT_RESULTS;
                    }
                }
                closestGeoFenceList = GeoUtil.getGeoFencesInRange(geoFenceRange, geoFenceDistance,
                        latitude, longitude, mobileOfferSettings, allGeoFences);

                geoFencesResponse.setClosestGeoFences(closestGeoFenceList);
                geoFencesResponse.setStatus(Response.Status.OK.toString());
                geoFencesResponse.setStatus_message(ClosestPropertyResponse.SUCCESSFUL);
                logger.info(geoFencesResponse.getStatus() + " : " + geoFencesResponse.getStatus_message());
                return Response.status(Response.Status.OK).entity(geoFencesResponse).build();

            } catch (Exception ex) {
                logger.fatal("ClosestGeoFencesREST fatal exception: " + ex);
                ClosestGeoFencesResponse errorResponse = new ClosestGeoFencesResponse();
                errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
                errorResponse.setStatus_message(ClosestPropertyResponse.SERVICE_UNAVAILABLE);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
            }
        } else {
            geoFencesResponse.setStatus(Response.Status.UNAUTHORIZED.toString());
            geoFencesResponse.setStatus_message(ClosestGeoFencesResponse.INVALID_CREDENTIALS);
            logger.info(geoFencesResponse.getStatus() + " : " + geoFencesResponse.getStatus_message());
            return Response.status(Response.Status.UNAUTHORIZED).entity(geoFencesResponse).build();
        }
    }
}
