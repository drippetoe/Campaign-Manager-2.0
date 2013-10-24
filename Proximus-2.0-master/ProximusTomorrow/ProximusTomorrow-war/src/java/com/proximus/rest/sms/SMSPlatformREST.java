/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest.sms;

import com.proximus.data.sms.Carrier;
import com.proximus.data.sms.KazePacket;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.manager.sms.CarrierManagerLocal;
import com.proximus.util.JAXBUtil;
import com.proximus.util.ServerURISettings;
import java.io.File;
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
 * This rest api is used for delivery status and to update carrier info if we get it
 * @author ronald
 */
@Path("/smsplatform/")
@RequestScoped
public class SMSPlatformREST {

    static final Logger logger = Logger.getLogger(SMSPlatformREST.class.getName());
    //static final String BASE_URL = "http://proximusmobility.com/api/optInStatus";
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
    @EJB
    private CarrierManagerLocal carrierMgr;

    public SMSPlatformREST() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_PLAIN})
    public Response post(MultivaluedMap<String, String> formParams) {


        logger.info("@POST smsplatform API CALLED");
        for (String key : formParams.keySet()) {
            logger.info("Key: " + key + " Param: " + formParams.getFirst(key));
        }
        String msisdn = formParams.getFirst("msisdn");
        String campaignID = formParams.getFirst("campaign-id");
        String messageBody = formParams.getFirst("message-body");
        String carrierId = formParams.getFirst("carrier-id");
        String status = formParams.getFirst("status");
        Carrier carrier = null;
        if(carrierId != null && !carrierId.isEmpty()){
           try {
                    carrier = carrierMgr.find(Long.parseLong(carrierId));
                } catch (NumberFormatException ex) {
                    logger.error("Number Format Exception getting carrier with id: " + carrierId);
                    carrier = null;
                }
        }    
        if (msisdn != null) {
            msisdn = msisdn.replaceAll("\\+", "");
            msisdn = msisdn.replaceAll("\\s", "");
        } else {
            logger.warn("MSISDN can't be empty");
            return Response.status(Response.Status.BAD_REQUEST).entity("Msisdn is not set up ").build();
        }

        Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
        if (subscriber == null) {
            logger.warn("MSISDN: " + msisdn + " is not part of our system");
            return Response.status(Response.Status.BAD_REQUEST).entity("msisdn: " + msisdn + " is not part of our system").build();
        }else{
            logger.info("setting carrier information for: " + msisdn);
            subscriber.setCarrier(carrier);
            subscriberMgr.update(subscriber);
        }

        if (status != null) {
            if (campaignID == null) {
                logger.warn("campaign-id: " + campaignID + " is not recognized");
                return Response.status(Response.Status.BAD_REQUEST).entity("campaign-id: " + campaignID + " is not recognized").build();
            }
            updateSendStatus(Long.parseLong(campaignID), msisdn, status);
        } else {
            logger.warn("neither message-body or status was defined. Please define message-body or status");
            return Response.status(Response.Status.BAD_REQUEST).entity("neither message-body or status was defined. Please define message-body or status").build();
        }

        logger.info("subscriber_campaign_status_completed");
        return Response.status(Response.Status.OK).entity("subscriber_campaign_status_completed").build();

    }

    /**
     * Sets the mobile offer to status
     *
     * @param campaignId Kaze Campaign Id
     * @param msisdn Phone number of the subscriber
     * @param status MobileOfferSendLog.STATUS_DELIVERED,
     * MobileOfferSendLog.STATUS_FAILED or MobileOfferSendLog.STATUS_PENDING
     */
    private void updateSendStatus(Long campaignId, String msisdn, String status) {

        String fileName = ServerURISettings.KAZE_PACKET_FOLDER + ServerURISettings.OS_SEP + "kaze_outbound_" + campaignId + ".log";
        if (new File(fileName).exists()) {
            try {
                logger.info("KazePacket: Status Update Opening file " + fileName);
                KazePacket kp = JAXBUtil.loadFromFile(KazePacket.class, fileName);
                List<MobileOfferSendLog> mobileOfferSendLogs = kp.getMobileOfferSendLogs();
                for (MobileOfferSendLog mobileOfferSendLog : mobileOfferSendLogs) {
                    MobileOfferSendLog realMosl = sendLogMgr.find(mobileOfferSendLog.getId());
                    if (realMosl != null) {
                        if (realMosl.getMsisdn().equals(msisdn)) {
                            realMosl.setStatus(status);
                            sendLogMgr.update(realMosl);
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }

        } else {
            logger.info("KazePacket: Unable to update send status from " + fileName);
        }
    }
}
