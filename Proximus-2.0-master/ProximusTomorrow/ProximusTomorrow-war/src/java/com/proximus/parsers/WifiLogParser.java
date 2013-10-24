/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.Campaign;
import com.proximus.data.Device;
import com.proximus.data.WifiLog;
import com.proximus.manager.CampaignManagerLocal;
import com.proximus.manager.DeviceManagerLocal;
import com.proximus.manager.WifiLogManagerLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
public class WifiLogParser {

    private static final Logger logger = Logger.getLogger(WifiLogParser.class.getName());
    final int NUM_FIELDS = 7;
    // format example: 
    final String logEntryPattern = "^\\[([0-9.]+)\\] \\[(\\S+\\s+\\S+)\\] \\[\\S+ (\\S+) \\S+\\] \\[(\\S+)\\] \\[([^\"]+)\\] \\[(\\S+)\\] \\[(\\S+)\\]";
    final Pattern p = Pattern.compile(logEntryPattern);
    // log files are in UTC, but the server time is UTC, so this will make it local time
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");

    public boolean parse(File f, DeviceManagerLocal deviceMgr, CampaignManagerLocal campMgr, WifiLogManagerLocal wifiMgr) {
        //From the file name we determine CampaignId and Company
        BufferedReader in = null;
        List<WifiLog> reports = new ArrayList<WifiLog>();

        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("wifi")) {
            logger.warn("File " + filename + " is not appropriate for this parser");
            return false;
        }

        String[] split = filename.split("_");
        if (split.length != 5) {
            logger.warn("File " + filename + " does not have a valid filename");
            return false;
        }
        String platform = split[1];
        String macAddr = split[2];
        String campId = split[3];

        Device d = deviceMgr.getDeviceByMacAddress(macAddr);
        Campaign camp = campMgr.find(Long.parseLong(campId));

        if (campId.equalsIgnoreCase("-1")) {
            logger.debug("Could not load file " + f.getAbsolutePath() + ", no active campaign");
            return true;
        }

        if (d == null || camp == null) {
            logger.warn("Could not load file " + f.getAbsolutePath() + ", campaign or device does not exist");
            return true;
        } else if (!d.getCompany().getId().equals(camp.getCompany().getId())) {
            logger.warn("Could not load file " + f.getAbsolutePath() + ", campaign " + camp + " and device company " + d.getCompany().logName() + " do not match");
            return false;
        }

        logger.info("Loading file: " + f.getAbsolutePath() + ", campaign " + camp + " and device company " + d.getCompany());


        try {
            in = new BufferedReader(new FileReader(f));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() < 8) {
                    continue;
                }
                Matcher matcher = p.matcher(line);
                if (!matcher.matches() || NUM_FIELDS != matcher.groupCount()) {
                    logger.debug("Error parsing line: " + line);
                    continue;
                }
                /*
                 * 1 2 3 4 5 6 7
                 * [192.168.3.109] [28/Feb/2012:16:47:01 +0000] [GET / HTTP/1.1]
                 * [200] [Devicescape-Agent/4.0.130 tmobile_wispr1]
                 * [dapi.devicescape.net] [d0:17:6a:5e:95:f5] Note we don't
                 * store IP, it's not interesting, MAC identifies the user
                 */
                WifiLog wr = new WifiLog();
                wr.setCampaign(camp);
                wr.setCompany(d.getCompany());
                wr.setDevice(d);
                wr.setEventDate(dateFormat.parse(matcher.group(2)));
                wr.setRequestUrl(matcher.group(3));
                wr.setHttpStatus(matcher.group(4));
                wr.setUserAgent(matcher.group(5));

                UserAgent agent = UserAgent.parseUserAgentString(wr.getUserAgent());
                Browser browser = agent.getBrowser();
                OperatingSystem os = agent.getOperatingSystem();
                Version version = agent.getBrowserVersion();

                wr.setBrowser(browser.getName());
                wr.setBrowserGroup(browser.getGroup().getName());
                wr.setOperatingSystem(os.getName());
                wr.setOperatingSystemGroup(os.getGroup().getName());
                if (version != null) {
                    wr.setBrowserVersion(version.getVersion());
                }

                wr.setServerName(matcher.group(6));
                wr.setMacAddress(matcher.group(7));
                wr.setUserAgentParsed(null);
                wr.setVersion("0");

                reports.add(wr);
            }

            in.close();
            wifiMgr.createListWifiLogs(reports);
            return true;
        } catch (Exception ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex2) {
                }
            }
            logger.fatal(ex);
            return false;
        } finally {
        }
    }
}
