
/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.util.ServerURISettings;
import com.sun.jersey.multipart.FormDataParam;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Path("/upload")
@RequestScoped
public class UploadREST {

    private static final Logger logger = Logger.getLogger(UploadREST.class.getName());
    @Context
    private UriInfo context;
    @EJB
    private CompanyManagerLocal companyMgr;
    @EJB
    private DeviceManagerLocal deviceMgr;

    public UploadREST() {
    }

    /**
     * Logs are of the file format type_deviceMAC_campaignId_starDateTime.txt
     * Examples wifiLog_4FT6FD8M_4_2012-09-01.11-12-03.txt
     * btLog_4FT6FD8M_2_2012-01-25.03-56-00.txt
     *
     * We will be saving them in the directory structure logPath +
     * /companyId/deviceMAC/campaignId/type/startDateTime.txt Examples:
     * /home/proximus/server/logs/380/4FT6FD8M/4/wifi/2012-09-01.11-12-03.txt
     *
     * @param file
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(File file) {
        File dest = new File(ServerURISettings.LOG_WORKING, file.getName());

        try {
            FileUtils.copyFile(file, dest);
            return Response.status(Response.Status.OK).entity("YES").build();
        } catch (IOException ex) {
            logger.error(ex);
        }
        return Response.status(Response.Status.CONFLICT).entity("NO").build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postForm(@FormDataParam("body") File file, @FormDataParam("metadata") String fileInfo) {
        //Logic to create directories for company device...          
        File dest = new File(ServerURISettings.LOG_WORKING, fileInfo);
        try {
            FileUtils.copyFile(file, dest);
            return Response.status(Response.Status.OK).entity("YES").build();
        } catch (IOException ex) {
            logger.error(ex);
        }
        return Response.status(Response.Status.CONFLICT).entity("NO").build();
    }
}
