/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.example;

import com.proximus.data.response.example.ExampleErrorResponseObject;
import com.proximus.data.response.example.ExampleResponseObject;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ronald
 */
@Path("/example")
@RequestScoped
public class ExampleREST {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getExample() {
        try {
            List<String> cars = new ArrayList<String>();
            cars.add("Volvo");
            cars.add("Camry");
            ExampleResponseObject obj = new ExampleResponseObject(12L, "CarList");
            obj.setElements(cars);
            obj.getElements().remove(40);

            return Response.status(Response.Status.OK).entity(obj).build();
        } catch (Exception ex) {
            ExampleErrorResponseObject errorObj = new ExampleErrorResponseObject(1L, ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorObj).build();
        }
    }

    @OPTIONS
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response optionsExample() {
        return Response.status(Response.Status.OK).entity("Connected").build();
    }
}
