/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import com.proximus.data.WifiRegistration;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.WifiRegistrationManagerLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author ronald williams
 */
public class WifiRegistrationParser {

    private static Logger logger = Logger.getLogger(WifiRegistrationParser.class.getName());

    public boolean parse(File f, DeviceManagerLocal deviceMgr, CampaignManagerLocal campMgr, WifiRegistrationManagerLocal regMgr) {
        List<WifiRegistration> registration = new ArrayList<WifiRegistration>();
        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("registration")) {
            return false;
        }

        String[] split = filename.split("_");
        if (split.length != 5) {
            logger.warn("File " + filename + " doesn't have a valid filename");
            return false;
        }
        String platform = split[1];
        String macAddr = split[2];
        String campId = split[3];

        if (campId.equalsIgnoreCase("-1") || campId.equalsIgnoreCase("NoActive")) {
            logger.debug("File " + filename + " is not associated with a campaign");
            return false;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
        Device d = deviceMgr.getDeviceByMacAddress(macAddr);
        Company c = d.getCompany();
        Campaign camp = campMgr.find(Long.parseLong(campId));
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(f));
            // php tracks in seconds, not milliseconds
            String eventDateStr = prop.getProperty("eventDate");
            Date eventDate;
            try {
                eventDate = sdf.parse(eventDateStr);
            } catch (ParseException ex) {
                eventDate = new Date(Long.parseLong(eventDateStr + "000"));
            }

            String mac = prop.getProperty("macAddress");
            String field1 = prop.getProperty("field1");
            String field2 = prop.getProperty("field2");
            String field3 = prop.getProperty("field3");
            String field4 = prop.getProperty("field4");
            String field5 = prop.getProperty("field5");
            String field6 = prop.getProperty("field6");
            String field7 = prop.getProperty("field7");
            String field8 = prop.getProperty("field8");
            String field9 = prop.getProperty("field9");
            String field10 = prop.getProperty("field10");
            String field11 = prop.getProperty("field11");
            String field12 = prop.getProperty("field12");
            String field13 = prop.getProperty("field13");
            String field14 = prop.getProperty("field14");
            String field15 = prop.getProperty("field15");

            WifiRegistration ur = new WifiRegistration();
            ur.setEventDate(eventDate);
            ur.setCampaign(camp);
            ur.setCompany(c);
            ur.setDevice(d);
            ur.setMacAddress(mac);
            ur.setField1(field1);
            ur.setField2(field2);
            ur.setField3(field3);
            ur.setField4(field4);
            ur.setField5(field5);
            ur.setField6(field6);
            ur.setField7(field7);
            ur.setField8(field8);
            ur.setField9(field9);
            ur.setField10(field10);
            ur.setField11(field11);
            ur.setField12(field12);
            ur.setField13(field13);
            ur.setField14(field14);
            ur.setField15(field15);
            registration.add(ur);
            regMgr.createRegistrationList(registration);
            return true;
        } catch (IOException ex) {
            logger.fatal(ex);
            return false;
        }

    }
}
