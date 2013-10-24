/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.sms.KazePacket;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.parsers.SubscriberCSVParser;
import com.proximus.parsers.SubscriberImportParser;
import com.proximus.registration.client.Registrar;
import com.proximus.util.JAXBUtil;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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

/**
 *
 * @author Angela Mercer
 */
@Path("/subscriberImport/")
@RequestScoped
public class SubscriberImportREST
{
    static final Logger logger = Logger.getLogger(SubscriberImportREST.class.getName());
    @Context
    private UriInfo context;
    @Context
    Request request;
    @Context
    HttpServletResponse response;
    @EJB
    private SubscriberManagerLocal subscriberMgr;
    @EJB
    private MobileOfferSendLogManagerLocal sendLogMgr;

    public SubscriberImportREST()
    {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_PLAIN})
    public Response post(MultivaluedMap<String, String> formParams)
    {
        logger.info("@POST SubscriberImport API CALLED");
        String fileName = formParams.getFirst("file-name");
        String file_separator = System.getProperty("file.separator");
        if (fileName != null) {
            try {
                File f = new File(ServerURISettings.SMS_USER_IMPORT_DIR + file_separator + fileName);
                if (f.exists()) {
                    SubscriberImportParser parser = new SubscriberImportParser();
                    //parser.parse(f, subscriberMgr);
                    return Response.status(Response.Status.OK).entity("File: " + fileName + " was imported successfully").build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("cannot find file").build();
                }
            } catch (Exception ex) {
                logger.error(ex);
                return Response.status(Response.Status.BAD_REQUEST).entity("error in subscriber import").build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("file-name is null").build();
        }
    }
}
