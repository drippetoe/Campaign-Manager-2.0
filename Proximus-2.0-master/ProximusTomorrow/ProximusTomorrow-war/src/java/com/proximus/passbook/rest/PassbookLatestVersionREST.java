/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Path("/{version}/passes/pass.com.proximus.ValuText/{serial_number}")
@RequestScoped
public class PassbookLatestVersionREST {

    public PassbookLatestVersionREST()
    {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContact(@PathParam("version") String version, @PathParam("serial_number") String serialNumber)
    {
        return Response.status(Response.Status.CREATED).entity("SUCCESS").build();
    }

}
