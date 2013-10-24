/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.data.SoftwareRelease;
import com.proximus.manager.SoftwareReleaseManagerLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;

/**
 * Call /download/wifi/2 Call /download/bluetooth/1
 *
 * @author eric
 */

@RequestScoped
@Path("/software-update/{platform}/{major}/{minor}/{build}/{filename}")
public class SoftwareREST {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UploadREST.class.getName());

    @EJB
    private SoftwareReleaseManagerLocal
            softwareManager;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("platform") String platform, @PathParam("major") Long major, @PathParam("minor") Long minor, @PathParam("build") Long build, @PathParam("filename") String filename) {
        
        
        
        SoftwareRelease sr = this.softwareManager.getRelease(platform, major, minor, build, null);
        logger.debug("Got software release: "+sr.getPlatform());
        File file = new File(sr.getPath());
        if (file != null && file.exists()) {            
            InputStream stream;
            try {
                stream = FileUtils.openInputStream(file);
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
            return Response.ok().entity(stream).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No Content").build();
    }
    
  
}
