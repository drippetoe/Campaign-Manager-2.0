package com.proximus.passbook.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author dshaw
 */
@Path("/{version}/devices/{device_identifier}/registrations/{pass_type_identifier}/{serial_number}")
@RequestScoped
public class PassbookRegisterDeviceForPushREST {

    public PassbookRegisterDeviceForPushREST()
    {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContact(@PathParam("version") String version, @PathParam("device_identifier") String deviceIdentifier, @PathParam("pass_type_identifier") String passTypeIdentifier, @PathParam("serial_number") String serialNumber)
    {
        return Response.status(Response.Status.CREATED).entity("SUCCESS").build();
    }

}
