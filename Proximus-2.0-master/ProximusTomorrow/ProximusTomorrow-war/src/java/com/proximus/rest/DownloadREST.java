/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.manager.CampaignManagerLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

/**
 * Call /download/wifi/2 Call /download/bluetooth/1
 *
 * @author eric
 */
@Path("/download/{campaign_id}/{type}/{filename}")
@RequestScoped
public class DownloadREST
{
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UploadREST.class.getName());
    @Context
    private UriInfo context;
    @EJB
    private CampaignManagerLocal campaignManager;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("campaign_id") Long campaign_id, @PathParam("type") String type, @PathParam("filename") String filename)
    {
        File file = this.campaignManager.getFile(campaign_id, type);
        if (file != null && file.exists()) {
            InputStream stream;
            try {
                stream = new FileInputStream(file);
            } catch (Exception ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
            return Response.ok().entity(stream).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No Content").build();





    }
}
