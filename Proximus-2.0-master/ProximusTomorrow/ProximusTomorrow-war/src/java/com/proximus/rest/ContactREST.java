/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.data.Contact;
import com.proximus.data.Device;
import com.proximus.manager.ContactManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Path("/contact/{device_id}")
@RequestScoped
public class ContactREST
{
    private Logger logger = Logger.getLogger(ContactREST.class.getName());
    
    @Context
    private UriInfo context;
    @EJB
    private ContactManagerLocal contactMgr;
    @EJB
    private DeviceManagerLocal deviceMgr;

    public ContactREST()
    {
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response createContact(@PathParam("device_id") Long device_id)
    {
        Contact contact = new Contact();
        contact.setAddress1("75th 5th Street");
        contact.setCountry("USA");
        contact.setStateProvince("GA");
        contact.setEmail("ggaxiola@proximusmobility.com");
        contact.setZipcode("30308");
        
        Device d = deviceMgr.find(device_id);
        
        if(d != null) {    
            System.err.println("The Device id = " + d.getId());
            contact.addDevice(d);
            contact.setCompany(d.getCompany());
            d.setContact(contact);
            deviceMgr.update(d);
            return Response.status(Response.Status.CREATED).entity(contact.toString()).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).entity("Couldn't find Device with id: " + device_id).build();
        }
        
        
        
        
    }
}
