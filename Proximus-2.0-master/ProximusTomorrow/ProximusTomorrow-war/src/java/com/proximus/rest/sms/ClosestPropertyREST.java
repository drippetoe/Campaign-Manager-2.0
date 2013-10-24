/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.data.response.ClosestPropertyResponse;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Property;
import com.proximus.manager.UserManagerLocal;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.util.server.GeoFenceResponse;
import com.proximus.util.server.GeoUtil;
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
import org.primefaces.model.map.LatLng;

/**
 *
 * @author ronald
 */
@Path("/closestproperty")
@RequestScoped
public class ClosestPropertyREST {

    static final Logger logger = Logger.getLogger(ClosestPropertyREST.class.getName());
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

    public ClosestPropertyREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response post(MultivaluedMap<String, String> formParams) {
        String latitude = formParams.getFirst("latitude");
        String longitude = formParams.getFirst("longitude");
        String userName = formParams.getFirst("username");

        ClosestPropertyResponse propertyResponse = new ClosestPropertyResponse();


        User user = userMgr.getUserByUsername(userName);
        if (user == null) {
            logger.warn("User: " + userName + " not found.");
            propertyResponse.setStatus(Response.Status.BAD_REQUEST.toString());
            propertyResponse.setStatus_message(ClosestPropertyResponse.MISSING_PARAMETERS);
            return Response.status(Response.Status.BAD_REQUEST).entity(propertyResponse).build();
        }
        /*
         * TODO: Get the company in a better way than this.
         */
        Company company = user.getCompanies().get(0);

        try {
            MobileOfferSettings mobileOfferSettings = settingsMgr.findByBrand(company.getBrand());
            List<GeoFence> allGeoFences = geoFenceMgr.getGeoFencesByBrand(company.getBrand());
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            GeoFenceResponse geoResponse = GeoUtil.CalculateClosestGeoFence(mobileOfferSettings, location, allGeoFences);
            Property closestProperty = geoResponse.getGeoFence().getProperty();
            propertyResponse.setClosestPropertyId(closestProperty.getId());
            propertyResponse.setClosestPropertyWebHash(closestProperty.getWebHash());
            propertyResponse.setStatus(Response.Status.OK.toString());
            propertyResponse.setStatus_message(ClosestPropertyResponse.SUCCESSFUL);
            logger.info(propertyResponse.getStatus() + " : " + propertyResponse.getStatus_message());
            return Response.status(Response.Status.OK).entity(propertyResponse).build();

        } catch (Exception ex) {
            logger.fatal("ClosestPropertyREST fatal exception: " + ex);
            ClosestPropertyResponse errorResponse = new ClosestPropertyResponse();
            errorResponse.setStatus(Response.Status.SERVICE_UNAVAILABLE.toString());
            errorResponse.setStatus_message(ClosestPropertyResponse.SERVICE_UNAVAILABLE);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }
}
