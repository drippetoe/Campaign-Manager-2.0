/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.rest;

import com.proximus.bean.BugTracker;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * From:
 * http://www.nabisoft.com/tutorials/glassfish/securing-java-ee-6-web-applications-on-glassfish-using-jaas
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Response RESPONSE;

    static {
        RESPONSE = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error").build();
    }

    @Override
    @Produces(MediaType.TEXT_PLAIN)
    public Response toResponse(Throwable ex) {
        System.err.println("ThrowableExceptionMapper caught: " + ex.toString());
        BugTracker.ReportError("REST API: " + ex.toString(), ex.toString(), ex);
        return RESPONSE;
    }
}
