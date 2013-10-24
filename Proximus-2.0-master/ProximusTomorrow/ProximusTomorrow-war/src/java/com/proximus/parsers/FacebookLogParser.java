/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.FacebookUserLog;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.FacebookUserLogManagerLocal;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author ronald
 * @param f The facebook file to parse that looks like
 * fb_dreamplug_F0AD4E0114AC_240_2012-10-10.20-54-34.log
 */
public class FacebookLogParser {

    private static Logger logger = Logger.getLogger(FacebookLogParser.class.getName());

    public boolean parse(File f, FacebookUserLogManagerLocal facebookLogMgr, CampaignManagerLocal campMgr, DeviceManagerLocal deviceMgr) {
        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("fb")) {
            return false;
        }

        String[] split = filename.split("_");
        if (split.length != 5) {
            logger.warn("File " + filename + " doesn't have a valid filename");
            return false;
        }
        String deviceMAC = split[2];
        String campId = split[3];
        String logDate = split[4];

        if (campId.equalsIgnoreCase("-1") || campId.equalsIgnoreCase("NoActive")) {
            logger.debug("File " + filename + " is not associated with a campaign");
            return false;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
        Date eventDate;
        try {
            eventDate = sdf.parse(logDate);
        } catch (ParseException ex) {
            eventDate = new Date(Long.parseLong(logDate + "000"));
        }
        Campaign campaign = campMgr.find(Long.parseLong(campId));
        Device device = deviceMgr.getDeviceByMacAddress(deviceMAC);
        Company company = campaign.getCompany();
        ObjectMapper mapper = new ObjectMapper();
        try {

            JsonNode node = mapper.readValue(f, JsonNode.class);
            FacebookUserLog userLog = mapper.readValue(f, FacebookUserLog.class);
            if (node.has("hometown")) {
                try {
                    /*
                     * This does not return null so if something goes wrong 
                     * just set the value to null instead of not creating the entry.
                     */
                    userLog.setHometown(node.path("hometown").path("name").getTextValue());
                } catch (Exception e) {
                    userLog.setHometown(null);
                }
            }
            if (userLog.getUserMAC().isEmpty()) {
                userLog.setUserMAC(null);
            }
            userLog.setEventDate(eventDate);
            userLog.setCampaign(campaign);
            userLog.setCompany(company);
            userLog.setDevice(device);
            FacebookUserLog persistedLog = facebookLogMgr.getFacebookUserLogByFacebookId(userLog.getId());
            if (persistedLog == null) {
                facebookLogMgr.create(userLog);
            } else {
                facebookLogMgr.update(userLog);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void main(String[] args) {
        File f = new File("/home/ronald/facebookLogs/fb_dreamplug_F0AD4E0114AC_240_2012-10-10.20-54-34.log");
        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("fb")) {
            System.out.print("Bad File");
        }

        String[] split = filename.split("_");
        if (split.length != 5) {
            System.out.print("Filename invalid");
        }
        String deviceMAC = split[2];
        String campId = split[3];
        String logDate = split[4];
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readValue(f, JsonNode.class);
            FacebookUserLog user = mapper.readValue(f, FacebookUserLog.class);
            if (node.has("hometown")) {
                user.setHometown(node.path("hometown").path("name").getTextValue());
            }
            System.out.println(user);
        } catch (Exception e) {
        }

    }
}
