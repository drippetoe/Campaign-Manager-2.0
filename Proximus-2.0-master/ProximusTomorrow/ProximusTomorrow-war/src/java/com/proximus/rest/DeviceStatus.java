/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.rest;

import com.proximus.data.*;
import com.proximus.data.config.Config;
import com.proximus.data.response.ServerResponse;
import com.proximus.manager.*;
import com.proximus.util.JAXBUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("/status")
@RequestScoped
public class DeviceStatus {

    private static final Logger logger = Logger.getLogger(DeviceStatus.class.getName());
    @Context
    private UriInfo context;
    @Context
    HttpServletRequest request;
    @EJB
    private DeviceManagerLocal deviceMgr;
    @EJB
    private CampaignManagerLocal campMgr;
    @EJB
    private SoftwareReleaseManagerLocal updateMgr;
    @EJB
    private AuthenticatorManagerLocal authMgr;
    @EJB
    private ShellCommandActionManagerLocal shellMgr;
    private boolean isForbidden = false;
    private boolean isNotFound = false;
    private boolean isUnauthorized = false;

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response status(String statusMessage) throws Exception {
        if (statusMessage == null || statusMessage.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
        }

        clearChecks();
        Config config = JAXBUtil.fromXml(Config.class, statusMessage);
        ServerResponse response;
        response = createResponse(config);

        if (response != null) {
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            if (isForbidden) {
                return Response.status(Response.Status.FORBIDDEN).entity(null).build();
            } else if (isUnauthorized) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(null).build();
            } else {
                if (isNotFound) {
                    return Response.status(Response.Status.NOT_FOUND).entity(null).build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
                }
            }
        }
    }

    public void clearChecks() {
        isForbidden = false;
        isNotFound = false;
    }

    /**
     * Create the response from the DB
     *
     * @param config
     * @return
     */
    private ServerResponse createResponse(Config config) {
        //TODO real authentication and Licensing
        if (!isLicenseValid(config.getSoftwareLicense())) {
            isForbidden = true;
            logger.warn("This Device doesn't have a valid license: " + config.getSoftwareLicense());
            logger.warn("With Mac: " + config.getMacAddress() +  " and token: " + config.getAuthenticationToken());

            return null;
        }

        if (!authMgr.authenticate(config)) {
            isForbidden = true;
            handleDeviceInLimbo(config);
            return null;
        }

        ServerResponse response = new ServerResponse();
        String token = config.getAuthenticationToken();
        String mac = config.getMacAddress();
        Device d = deviceMgr.getDeviceByTokenAndMac(token, mac);


        if (d != null) {
            d.setLastIpAddress(request.getRemoteAddr());

            if (d.getAllowWifiClientOverride()
                    && config.getChannel() != null
                    && config.getChannel() > 0
                    && config.getChannel() < 12
                    && config.getChannel() != d.getWifiChannel()) {
                d.setWifiChannel(config.getChannel());
                d.setAllowWifiClientOverride(false);
            }

            try {
                Long totalVersionDevice = (config.getSoftwareMajor() * 100) + (config.getSoftwareMinor() * 10) + config.getSoftwareBuild();
                Long totalVersionDatabase = (d.getMajor() * 100) + (d.getMinor() * 10) + d.getBuild();

                if (totalVersionDevice > totalVersionDatabase) {
                    d.setMajor(config.getSoftwareMajor());
                    d.setMinor(config.getSoftwareMinor());
                    d.setBuild(config.getSoftwareBuild());
                    d.setKernel(config.getSoftwareKernel());
                }
            } catch (Exception err) {
                logger.fatal("Error setting updated device version",err);
            }

            d.setLastSeen(new Date());
            List<Campaign> newList = new ArrayList<Campaign>();
            //loop over campaigns to check if anyone has expired
            List<Campaign> currentList = d.getCampaigns();
            if (currentList != null) {
                for (Campaign c : currentList) {
                    if (!c.isActive() || c.isDeleted() || c.isExpired()) {
                        c.setActive(false);
                        campMgr.update(c);

                    } else {
                        newList.add(c);
                    }
                }
            }
            d.setCampaigns(newList);
            deviceMgr.update(d);

            response.setConfig(d.createConfig());
            Campaigns camps = new Campaigns(d.getCampaigns());
            response.setCampaigns(camps);

            Actions acts = new Actions();

            SoftwareRelease release = updateMgr.getReleaseForDevice(d);
            if (release != null) {
                SoftwareUpdateAction swupact = new SoftwareUpdateAction();
                swupact.setSoftwareReleasePath(release);
                acts.addAction(swupact);
            }

            LogUploadAction up = new LogUploadAction();
            up.setAll(false);
            up.setCount(10);
            acts.addAction(up);



            List<ShellCommandAction> shellCommands = shellMgr.getShellCommandsForDevice(d);

            if (shellCommands != null) {
                for (ShellCommandAction shellCommandAction : shellCommands) {
                    acts.addAction(shellCommandAction);
                    shellCommandAction.setCompleted(true);
                    shellMgr.updateShellCommandAction(shellCommandAction);

                }
            }


            response.setActions(acts);

        } else {
            handleDeviceInLimbo(config);
            logger.error("Couldn't fild Device with token: " + token + " and Mac: " + mac);
            isNotFound = true;
            return null;
        }
        return response;

    }

    /**
     * TODO checking if license is Valid
     *
     * @param license
     * @return
     */
    private boolean isLicenseValid(String license) {
        return true;
    }

    /**
     * The Device exists but it already has a token provided. During
     * registration tokens are created and given to devices, Therefore this
     * device already registers (or thinks it did)
     *
     * @param dd
     */
    private void handleDeviceInLimbo(Config config) {
        Device dd = deviceMgr.getDeviceByMacAddress(config.getMacAddress());
        DeviceInLimbo dl = deviceMgr.getDeviceInLimboByMacAdress(config.getMacAddress().toUpperCase());
        if (dl == null) {
            dl = new DeviceInLimbo();
            dl.setMacAddress(config.getMacAddress().toUpperCase());
            dl.setToken(dl.getToken());
            dl.setLastSeen(new Date());
            dl.setLastIpAddress(request.getRemoteAddr());
            if (dd != null) {
                dl.setSerialNumber(dd.getSerialNumber());
            }
            deviceMgr.createDeviceInLimbo(dl);
        } else {
            if (dd != null) {
                dl.setSerialNumber(dd.getSerialNumber());
            }
            dl.setToken(config.getAuthenticationToken());
            dl.setLastSeen(new Date());
            dl.setLastIpAddress(request.getRemoteAddr());
            deviceMgr.updateDeviceInLimbo(dl);
        }
    }
}