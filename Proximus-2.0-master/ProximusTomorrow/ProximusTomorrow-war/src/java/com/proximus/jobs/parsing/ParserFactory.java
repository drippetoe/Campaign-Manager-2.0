/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jobs.parsing;

import com.proximus.data.Device;
import com.proximus.manager.*;
import com.proximus.manager.sms.MobileOfferClickthroughManagerLocal;
import com.proximus.manager.sms.MobileOfferManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.parsers.*;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ParserFactory implements Runnable {

    public static final String TYPE_WIFI = "wifi";
    public static final String TYPE_BLUETOOTH = "bluetooth";
    public static final String TYPE_REGISTRATION = "registration";
    public static final String TYPE_DWELL = "dwell";
    public static final String TYPE_SHELL_COMMAND = "shellcommand";
    public static final String TYPE_BARCODE = "barcode";
    public static final String TYPE_DEVICE_IMPORT = "deviceimpport";  //This probably shouldn't be here since the parsing takes place in the UI
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_MOBILE_OFFER_CLICKTHROUGH = "clickthrough";
    private static final String TYPE_FACEBOOK = "fb";
    private static final Logger logger = Logger.getLogger(ParserFactory.class.getName());
    private WifiLogParser wifiParser = new WifiLogParser();
    private WifiRegistrationParser registrationParser = new WifiRegistrationParser();
    private BluetoothLogParser bluetoothParser = new BluetoothLogParser();
    private BarcodeLogParser barcodeParser = new BarcodeLogParser();
    private DeviceCSVParser deviceImportParser = new DeviceCSVParser();
    private ClickThroughParser clickthroughParser = new ClickThroughParser();
    private FacebookLogParser facebookParser = new FacebookLogParser();
    private final long THIRTY_SECONDS = 30000;
    private final long ONE_MINUTE = 60000;
    private final long TWO_MINUTES = 120000;
    private DeviceManagerLocal deviceMgr;
    private CampaignManagerLocal campMgr;
    private WifiLogManagerLocal wifiMgr;
    private WifiRegistrationManagerLocal wifiRegMgr;
    private BarcodeManagerLocal barcodeMgr;
    private BluetoothReportManagerLocal btReportMgr;
    private MobileOfferClickthroughManagerLocal clickthroughMgr;
    private MobileOfferManagerLocal offerMgr;
    private PropertyManagerLocal propertyMgr;
    private FacebookUserLogManagerLocal facebookLogMgr;

    public ParserFactory(DeviceManagerLocal deviceMgr, CampaignManagerLocal campMgr,
            WifiLogManagerLocal wifiMgr, WifiRegistrationManagerLocal wifiRegMgr, BarcodeManagerLocal barcodeMgr, BluetoothReportManagerLocal btReportMgr,
            MobileOfferClickthroughManagerLocal clickthroughMgr, MobileOfferManagerLocal offerMgr, PropertyManagerLocal propertyMgr,
            FacebookUserLogManagerLocal facebookLogMgr) {
        this.deviceMgr = deviceMgr;
        this.campMgr = campMgr;
        this.wifiMgr = wifiMgr;
        this.wifiRegMgr = wifiRegMgr;
        this.barcodeMgr = barcodeMgr;
        this.btReportMgr = btReportMgr;
        this.clickthroughMgr = clickthroughMgr;
        this.offerMgr = offerMgr;
        this.propertyMgr = propertyMgr;
        this.facebookLogMgr = facebookLogMgr;
    }

    public static String getLogType(String filename) {
        if (filename.startsWith("wifi")) {
            return TYPE_WIFI;
        } else if (filename.startsWith("registration")) {
            return TYPE_REGISTRATION;
        } else if (filename.startsWith("bluetooth")) {
            return TYPE_BLUETOOTH;
        } else if (filename.startsWith("dwell")) {
            return TYPE_DWELL;
        } else if (filename.startsWith("shellcommand")) {
            return TYPE_SHELL_COMMAND;
        } else if (filename.startsWith("barcode")) {
            return TYPE_BARCODE;
        } else if (filename.startsWith("vtext")) {
            return TYPE_MOBILE_OFFER_CLICKTHROUGH;
        } else if (filename.startsWith("fb")) {
            return TYPE_FACEBOOK;
        }
        return TYPE_UNKNOWN;
    }

    public void parse(File f) {
        String basePath = f.getParent();

        String filename = f.getName();
        logger.info("Parsing File: " + filename);
        //Only File that doesn't need to be parsed just moved
        if (getLogType(filename).equals(TYPE_SHELL_COMMAND)) {
            String[] split = filename.split("_");
            if (split.length == 4) {
                Device d = deviceMgr.getDeviceByMacAddress(split[1]);
                String filePath = ServerURISettings.LOG_ROOT + "/commands/" + d.getCompany().getId() + "/" + split[1];
                File parentFile = new File(filePath);
                parentFile.mkdirs();
                File destFile = new File(parentFile, filename);
                try {
                    safeMoveFile(f, destFile);
                    return;
                } catch (Exception ex) {
                    logger.error(ex);
                }
            } else {
                try {
                    File parent = new File(ServerURISettings.LOG_ERROR);
                    parent.mkdirs();
                    File destFile = new File(parent, filename);
                    safeMoveFile(f, destFile);
                } catch (Exception ex) {
                    logger.error(ex);
                }
                return;
            }
        }

        File workingFile = new File(ServerURISettings.LOG_WORKING, filename);
        File completedFile = getFileLocationFor(filename);
        completedFile.getParentFile().mkdirs();
        File errorFile = new File(ServerURISettings.LOG_ERROR, filename);
        if (getLogType(filename).equals(TYPE_WIFI)) {
            try {
                safeMoveFile(f, workingFile);
                if (wifiParser.parse(workingFile, deviceMgr, campMgr, wifiMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Could not Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }
            }
        } else if (getLogType(filename).equals(TYPE_REGISTRATION)) {
            try {
                safeMoveFile(f, workingFile);
                if (registrationParser.parse(workingFile, deviceMgr, campMgr, wifiRegMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Could not Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }
            }
        } else if (getLogType(filename).equals(TYPE_BLUETOOTH)) {
            try {
                safeMoveFile(f, workingFile);
                if (bluetoothParser.parse(workingFile, deviceMgr, campMgr, btReportMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Could not Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }

            }

        } else if (getLogType(filename).equals(TYPE_FACEBOOK)) {
            try {
                safeMoveFile(f, workingFile);
                if (facebookParser.parse(workingFile, facebookLogMgr, campMgr,deviceMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Could not Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }

            }

        } else if (getLogType(filename).equals(TYPE_DEVICE_IMPORT)) {
            try {
                safeMoveFile(f, workingFile);
                if (deviceImportParser.parse(workingFile, deviceMgr, null, null) > 0) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Couldn't Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }

            }
        } else if (getLogType(filename).equals(TYPE_MOBILE_OFFER_CLICKTHROUGH)) {
            try {
                safeMoveFile(f, workingFile);
                if (clickthroughParser.parse(f, clickthroughMgr, offerMgr, propertyMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Could not Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }

            }
        } else if (getLogType(filename).equals(TYPE_BARCODE)) {
            try {
                safeMoveFile(f, workingFile);
                if (barcodeParser.parse(workingFile, barcodeMgr, deviceMgr)) {
                    safeMoveFile(workingFile, completedFile);
                } else {
                    logger.warn("Couldn't Parse file correctly: " + f.getName());
                    safeMoveFile(workingFile, errorFile);
                }
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if (workingFile.exists() && workingFile.isFile()) {
                        safeMoveFile(workingFile, errorFile);
                    }
                } catch (Exception ex2) {
                }

            }
        } else {
            logger.warn("Parser Doesn't understand format of : " + filename);
            safeMoveFile(f, errorFile);
        }
    }

    private void safeMoveFile(File original, File destination) {
        if (!(original.exists() && original.isFile())) {
            logger.warn("Cannot move " + original.getAbsolutePath() + ", file does not exist");
            return;
        }

        int addon = 0;
        String originalDestPath = destination.getAbsolutePath();
        while (destination.exists()) {
            addon++;
            destination = new File(originalDestPath + "." + addon);
        }

        try {
            FileUtils.copyFile(original, destination);
            FileUtils.deleteQuietly(original);
        } catch (IOException ex) {
            logger.warn(null, ex);
        }
        logger.info("Moved " + original.getAbsolutePath() + " to " + destination.getAbsolutePath());
    }

    /**
     * helper method to get the appropriate log path
     *
     * @param filename
     * @return A File representing the 'completed' folder for this file
     */
    public File getFileLocationFor(String filename) {

        // Filename like: btLog_obex_4FT6FD8M_1_2012-01-25.03-56-00.txt
        //We need to get:
        //Log Path as : logs/company/device/campaign/type/filename
        String split[] = filename.split("_");
        String type = getLogType(filename);
        String platform = "prox";
        String mac = "";
        String campaignId = "";
        String dateFile = "";
        if (split.length == 5) {
            platform = split[1];
            mac = split[2];
            campaignId = split[3];
            dateFile = split[4];
        } else {
            if (split.length == 4) {
                mac = split[1];
                campaignId = split[2];
                dateFile = split[3];
            }
        }

        Device device = deviceMgr.getDeviceByMacAddress(mac);
        String companyId;
        if (device == null || device.getCompany() == null) {
            companyId = "noCompany";
        } else {
            companyId = device.getCompany().getId() + "";
        }

        File logFolder = new File(ServerURISettings.LOG_COMPLETED);
        logFolder = new File(logFolder, companyId);
        logFolder = new File(logFolder, mac);
        logFolder = new File(logFolder, campaignId);
        logFolder = new File(logFolder, type);
        logFolder = new File(logFolder, filename);

        return logFolder;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(THIRTY_SECONDS);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
        while (true) {
            try {
                File dir = new File(ServerURISettings.LOG_QUEUE);
                if (dir.exists() && dir.listFiles().length > 0) {
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        parse(file);
                    }
                }
                Thread.sleep(ONE_MINUTE);
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }
    }
}
