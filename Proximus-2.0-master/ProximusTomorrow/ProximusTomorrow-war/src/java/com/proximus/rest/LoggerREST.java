/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.data.Company;
import com.proximus.data.User;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.UserManagerLocal;
import com.proximus.util.RESTAuthenticationUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
@Path("/logger")
@RequestScoped
public class LoggerREST {

    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    UserManagerLocal userMgr;
    static final String BASE_URL = "http://api.proximusmobility.com:8080/ProximusTomorrow-war/api/logger/";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{level}/{loggerPath}/{user_name}/{token}/")
    public Response setLevel(@PathParam("level") String level, @PathParam("loggerPath") String loggerPath, @PathParam("user_name") String userName, @PathParam("token") String requestToken) {

        if (isUserAuthorized(userName, requestToken)) {
            Logger logger;
            Level logLevel = null;
            if (level != null) {
                logLevel = Level.toLevel(level, Level.INFO);
                if (loggerPath != null) {
                    logger = Logger.getLogger(loggerPath);
                } else {
                    logger = Logger.getRootLogger();
                }
                if (logLevel.equals(Level.OFF)) {
                    return Response.status(Response.Status.FORBIDDEN).entity("CANNOT TURN OFF LOGGING").build();
                }
                String msg = "PREVIOUS LOG LEVEL WAS: " + logger.getLevel();
                logger.setLevel(logLevel);
                msg += "\nCURRENT LOG LEVEL IS: " + logger.getLevel();
                logger.log(Priority.toPriority(logLevel.toInt()), "Changing Log Level");
                return Response.status(Response.Status.OK).entity(msg).build();

            }
            return Response.status(Response.Status.BAD_REQUEST).entity("INVALID LOG LEVEL").build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity("USERNAME DOES NOT HAVE ACCESS TO LOGGER (CHECK WITH PROX SUPPORT)").build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("level/{path}/{user_name}/{token}")
    public Response checkLevel(@PathParam("path") String path, @PathParam("user_name") String userName, @PathParam("token") String requestToken) {
        if (isUserAuthorized(userName, requestToken)) {
            try {
                Logger logger = Logger.getLogger(path);
                return Response.status(Response.Status.OK).entity("LOV LEVEL IS: " + logger.getLevel()).build();
            } catch (Exception e) {

                return Response.status(Response.Status.BAD_REQUEST).entity("PATH COULD NOT BE FOUND (CAN'T CHECK LOGGING LEVEL)").build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).entity("USERNAME DOES NOT HAVE ACCESS TO LOGGER (CHECK WITH PROX SUPPORT)").build();
    }

    private boolean isUserAuthorized(String userName, String requestToken) {

        if (userName != null && requestToken != null) {
            try {
                userName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return false;
            }
        } else {
            return false;
        }

        User user = userMgr.getUserByUsername(userName);
        if (user == null) {
            return false;
        }

        if (user.getRole().getPriority() != 1) {
            return false;
        }
        String password = user.getApiPassword();
        String requestURL = BASE_URL + userName + "/" + password;
        Company company = companyMgr.getCompanybyId(100L); //Getting Proximus Development Company
        String companySalt = company.getSalt();
        return RESTAuthenticationUtil.apiAuthorized(userName, password, requestToken, requestURL, companySalt);
    }
}
