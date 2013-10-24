/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.parsers;

import com.proximus.data.sms.MobileOffer;
import com.proximus.data.sms.MobileOfferClickthrough;
import com.proximus.data.sms.Property;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.sms.MobileOfferClickthroughManagerLocal;
import com.proximus.manager.sms.MobileOfferManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * @author ronald
 */
public class ClickThroughParser {

    private static final Logger logger = Logger.getLogger(ClickThroughParser.class.getName());
    int HEXADECIMAL = 16;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
    /*
     * TODO: Add pattern for getting user agent stuff
     */
    String VTEXT_PATTERN = "GET\\s+\\/(\\w{6})\\/(\\w{6})\\s+HTTP.*\\s+\\[(.*)\\s+\\+";
    Pattern pattern = Pattern.compile(VTEXT_PATTERN);

    public boolean parse(File f, MobileOfferClickthroughManagerLocal clickthroughMgr, MobileOfferManagerLocal offerMgr, PropertyManagerLocal propertyMgr) {
        BufferedReader in = null;
        List<MobileOfferClickthrough> clickthroughs = new ArrayList<MobileOfferClickthrough>();

        String filename = f.getName();
        if (filename.isEmpty() || !filename.startsWith("vtext")) {
            logger.warn("File " + filename + " is not appropriate for this parser");
            return false;
        }
        String path = f.getPath();
        try {
            in = new BufferedReader(new FileReader(path));

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() < 4) {
                    continue;
                }
                Matcher matcher = pattern.matcher(line);

                String propertyId, mobileId, date, requestUrl, httpStatus, userAgent, serverName;
                if (matcher.find()) {
                    try {
                        propertyId = matcher.group(1);
                        mobileId = matcher.group(2);
                        date = matcher.group(3);
                        requestUrl = matcher.group(4);
                        httpStatus = matcher.group(5);
                        userAgent = matcher.group(6);
                        serverName = matcher.group(7);
                    } catch (Exception e) {
                        continue;
                    }
                    Long realPropertyId, mobileOfferId;
                    try {
                        realPropertyId = Long.parseLong(propertyId, HEXADECIMAL);
                        mobileOfferId = Long.parseLong(mobileId, HEXADECIMAL);
                    } catch (Exception ex) {
                        continue;
                    }
                    Date d = sdf.parse(date);
                    Property property = propertyMgr.find(realPropertyId);
                    MobileOffer mobileOffer = offerMgr.find(mobileOfferId);
                    MobileOfferClickthrough clickthrough = new MobileOfferClickthrough();
                    clickthrough.setRequestUrl(requestUrl);
                    clickthrough.setHttpStatus(httpStatus);
                    clickthrough.setUserAgent(userAgent);

                    UserAgent agent = UserAgent.parseUserAgentString(clickthrough.getUserAgent());
                    Browser browser = agent.getBrowser();
                    OperatingSystem os = agent.getOperatingSystem();
                    Version version = agent.getBrowserVersion();

                    clickthrough.setBrowser(browser.getName());
                    clickthrough.setBrowserGroup(browser.getGroup().getName());
                    clickthrough.setOperatingSystem(os.getName());
                    clickthrough.setOperatingSystemGroup(os.getGroup().getName());
                    if (version != null) {
                        clickthrough.setBrowserVersion(version.getVersion());
                    }

                    clickthrough.setServerName(serverName);


                    clickthrough.setEventDate(d);
                    clickthrough.setMobileOffer(mobileOffer);
                    clickthrough.setProperty(property);
                    clickthroughs.add(clickthrough);
                }
            }
            in.close();
            for (MobileOfferClickthrough mobileOfferClickthrough : clickthroughs) {
                clickthroughMgr.create(mobileOfferClickthrough);
            }
            return true;
        } catch (Exception ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex2) {
                    logger.fatal(ex2);

                }
            }
            logger.fatal(ex);
            return false;
        }
    }

    public static void main(String args[]) throws ParseException, FileNotFoundException, IOException {
        DateFormat simple = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
        String VTEXT = "GET\\s+\\/(\\w{6})\\/(\\w{6})\\s+HTTP.*\\s+\\[(.*)\\s+\\+";
        Pattern vPattern = Pattern.compile(VTEXT);
        DateUtil.formatStringToDate(VTEXT);

        File file = new File("/home/ronald/Desktop/vtext.log");
        BufferedReader in;


        try {
            in = new BufferedReader(new FileReader(file));
            String lineIWant;

            while ((lineIWant = in.readLine()) != null) {

                Matcher matcher = vPattern.matcher(lineIWant);
                if (matcher.find()) {
                    System.out.println(matcher.group());

                    System.out.println(matcher.group(0));
                    String propertyId = matcher.group(1);
                    String mobileId = matcher.group(2);
                    String date = matcher.group(3);
                    System.out.println("Property Id " + propertyId);
                    System.out.println("Mobile Id " + mobileId);
                    System.out.println("Date is: " + date);
                    Date d = simple.parse(date);
                    System.out.println("In actual date object is: " + d.getTime());
                } else {
                    System.out.println("No Match");
                }
            }
        } catch (Exception ex) {
            System.err.print(ex);
        }
    }
}
