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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * This rest api is used for message delivery status from the Kaze platform
 *
 * @author Angela Mercer
 */
@Path("/messagestatus/")
@RequestScoped
public class MessageStatusREST
{
    //api url: http://dev.proximusmobility.net:8080/ProximusTomorrow-war/api/messagestatus
    static final Logger logger = Logger.getLogger(MessageStatusREST.class.getName());
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

    public MessageStatusREST()
    {
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_PLAIN})
    public Response post(MultivaluedMap<String, String> formParams)
    {
        logger.info("@POST MessageStatus API CALLED");
        for (String key : formParams.keySet()) {
            logger.info("Key: " + key + " Param: " + formParams.getFirst(key));
        }
        try {
            String msisdn = formParams.getFirst("msisdn");
            String campaignID = formParams.getFirst("campaign-id");
            String carrierId = formParams.getFirst("carrier-id");
            String messageStatus = formParams.getFirst("message-status");
            Carrier carrier = null;

            //check for valid msisdn
            if (msisdn != null) {
                msisdn = msisdn.replaceAll("\\+", "");
                msisdn = msisdn.replaceAll("\\s", "");
            } else {
                logger.warn("MSISDN can't be empty");
                return Response.status(Response.Status.BAD_REQUEST).entity("No msisdn was provided").build();
            }

            //get carrier info
            if (carrierId != null && !carrierId.isEmpty()) {
                try {
                    carrier = carrierMgr.find(Long.parseLong(carrierId));
                } catch (NumberFormatException ex) {
                    logger.error("Number Format Exception getting carrier with id: " + carrierId, ex);
                    carrier = null;
                }
            }

            //check for message status
            if (messageStatus != null || !messageStatus.isEmpty()) {
                if (messageStatus.equalsIgnoreCase("MESSAGE_REJECTED")) {
                    Subscriber subscriber = subscriberMgr.findByMsisdn(msisdn);
                    if (subscriber == null) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Subscriber not found").build();
                    } else {
                        subscriber.setStatus(Subscriber.STATUS_SMS_UNSUPPORTED);
                        subscriber.setCarrier(carrier);
                        subscriberMgr.update(subscriber);
                        return Response.status(Response.Status.OK).entity("Subscriber status updated -- message rejected").build();
                    }
                }
                if (messageStatus.equalsIgnoreCase("MESSAGE_DELIVERED")) {
                    if (campaignID == null) {
                        logger.warn("campaign-id: " + campaignID + " is not recognized");
                        return Response.status(Response.Status.BAD_REQUEST).entity("campaign-id: " + campaignID
                                + " is not recognized -- message delivered").build();
                    }
                    logger.debug("updating send status -- " + messageStatus);
                    updateSendStatus(Long.parseLong(campaignID), msisdn, messageStatus);

                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("No message-status was provided").build();
            }
            logger.info("message status completed");
            return Response.status(Response.Status.OK).entity("message status completed").build();
        } catch (Exception ex) {
            logger.error("Exception in MessageStatusREST: ", ex);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("SERVICE UNAVAILABLE").build();
        }
    }

    /**
     * Sets the mobile offer to status
     *
     * @param campaignId Kaze Campaign Id
     * @param msisdn Phone number of the subscriber
     * @param status MobileOfferSendLog.STATUS_DELIVERED,
     * MobileOfferSendLog.STATUS_FAILED or MobileOfferSendLog.STATUS_PENDING
     */
    private void updateSendStatus(Long campaignId, String msisdn, String status)
    {

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
                logger.error("Exception in MessageStatusREST:updateSendStatus ", ex);

            }

        } else {
            logger.info("KazePacket: Unable to update send status from " + fileName);
        }
    }
}
