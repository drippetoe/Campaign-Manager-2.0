/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.config;

import com.proximus.client.Main;
import com.proximus.client.cron.LogCron;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.util.client.ClientURISettings;
import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
public class SystemWriter {

    private static final Logger logger = Logger.getLogger(SystemWriter.class.getName());
    public static final String BLUETOOTH_FILE_PROP = "bluetooth_file";
    public static final String BLUETOOTH_TRANSFER_PROP = "bluetooth_transfer";
    public static final String BLUETOOTH_DWELL_PROP = "bluetooth_dwell";
    public static final int BLUETOOTH_LOG = 1;
    public static final int BLUETOOTH_TRANSFER = 2;
    public static final int BLUETOOTH_DWELL = 3;
    private static String currWifiCamp = "noActive";
    private static String currBluetoothCamp = "noActive";
    private static String currLogFile;
    private static String currBluetoothLogFile;
    private static String currBluetoothTransferFile;
    private static String currBluetoothDwellFile;
    private static boolean allowBluetoothLogging;

    public static void makeDefaultIndexPage() {
        try {
            String html = "<html><head><title>Proximus Mobility - Proximus Mobility, LLC.</title></head><body><h1>Proximus Mobility</h1><div id=\"footer-address\"><p>75 Fifth Street NorthWest, Atlanta, GA 30308</p><p>Toll Free : 1.888.665.2527 <br> Direct : 404.477.3310 <br> Fax : 775.269.0387<br>Email : site@proximusmobility.com</p></div></body></html>";
            new File(ClientURISettings.CAMPAIGNS_ROOT_DIR).mkdirs();
            FileUtils.write(new File(ClientURISettings.CAMPAIGNS_ROOT_DIR + "/" + "index.html"), html);
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    public static void changeActiveCampaign(String id) {
        try {
            //checking valid input is a number
            int x = Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            return;
        }
        SystemWriter.makeLighttpdConfFile(id);

    }

    public static void makeErrorsPage() {
        BufferedWriter writer = null;
        try {
            new File(ClientURISettings.ERROR_ROOT_DIR).mkdirs();
            String filename = ClientURISettings.ERROR_ROOT_DIR + "/404.html";
            writer = new BufferedWriter(new FileWriter(new File(filename)));
            writer.write("<meta http-equiv=\"refresh\" content=\"0; url=/\">");
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                logger.log(Priority.ERROR, ex);
            }
        }

    }

    public static void makeDefaultLighttpdConfFile() {
        SystemWriter.currWifiCamp = "noActive";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ClientURISettings.LIGHTTPD_FILE)));
            writer.write("server.name = \"" + ClientURISettings.accessPoint + "\"");
            writer.newLine();
            writer.write("server.port = \"" + ClientURISettings.serverPort + "\"");
            writer.newLine();
            writer.write("server.document-root = \"" + ClientURISettings.CAMPAIGNS_ROOT_DIR + "\"");
            writer.newLine();
            writer.close();

        } catch (IOException ex) {
            logger.log(Priority.ERROR, "Couldn't write lighttpd.conf: " + ex);
        }
    }

    public static void makeLighttpdConfFile(String id) {

        SystemWriter.currWifiCamp = id;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ClientURISettings.LIGHTTPD_FILE)));
            writer.write("server.name = \"" + ClientURISettings.accessPoint + "\"");
            writer.newLine();
            writer.write("server.port = \"" + ClientURISettings.serverPort + "\"");
            writer.newLine();
            writer.write("server.document-root = \"" + ClientURISettings.CAMPAIGNS_ROOT_DIR + "/" + id + "/wifi/\"");
            writer.newLine();
            writer.close();
        } catch (IOException ex) {
            logger.log(Priority.ERROR, "Couldn't write lighttpd.conf: " + ex);
        }
    }

    public static void touchWifiLogProperties() {
        try {
            Properties prop = new Properties();
            if (new File(ClientURISettings.LOG_PROPERTIES_FILE).exists()) {
                prop.load(new FileReader(new File(ClientURISettings.LOG_PROPERTIES_FILE)));
            }
            Date curr = new Date();
            SystemWriter.currLogFile = "wifi_dreamplug_" + Main.deviceIdentifier + "_" + SystemWriter.currWifiCamp + "_" + LogCron.sdf.format(curr) + ".log";
            prop.setProperty("wifi_file", currLogFile);
            prop.store(new FileWriter(new File(ClientURISettings.LOG_PROPERTIES_FILE)), "Logging Current File");
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    public static void touchBluetoothLogProperties() {
        try {
            Properties prop = new Properties();
            if (new File(ClientURISettings.LOG_PROPERTIES_FILE).exists()) {
                prop.load(new FileReader(new File(ClientURISettings.LOG_PROPERTIES_FILE)));
            }
            Date curr = new Date();
            SystemWriter.currBluetoothLogFile = "bluetooth_dreamplug_" + Main.deviceIdentifier + "_" + SystemWriter.currBluetoothCamp + "_" + LogCron.sdf.format(curr) + ".log";
            prop.setProperty(BLUETOOTH_FILE_PROP, SystemWriter.currBluetoothLogFile);
            SystemWriter.currBluetoothTransferFile = "bluetoothTransfer_dreamplug_" + Main.deviceIdentifier + "_" + SystemWriter.currBluetoothCamp + "_" + LogCron.sdf.format(curr) + ".log";
            prop.setProperty(BLUETOOTH_TRANSFER_PROP, SystemWriter.currBluetoothTransferFile);
            SystemWriter.currBluetoothDwellFile = "bluetoothDwell_dreamplug_" + Main.deviceIdentifier + "_" + SystemWriter.currBluetoothCamp + "_" + LogCron.sdf.format(curr) + ".log";
            prop.setProperty(BLUETOOTH_DWELL_PROP, SystemWriter.currBluetoothDwellFile);
            prop.store(new FileWriter(new File(ClientURISettings.LOG_PROPERTIES_FILE)), "Logging Current File");
            
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
    }

    public static File getBluetoothLogFile(int type) {
        String filename = null;
        try {
            Properties prop = new Properties();
            if (new File(ClientURISettings.LOG_PROPERTIES_FILE).exists()) {
                prop.load(new FileReader(new File(ClientURISettings.LOG_PROPERTIES_FILE)));
            }
            switch (type) {
                case BLUETOOTH_LOG:
                    filename = prop.getProperty(BLUETOOTH_FILE_PROP);
                    break;
                case BLUETOOTH_DWELL:
                    filename = prop.getProperty(BLUETOOTH_DWELL_PROP);
                    break;
                case BLUETOOTH_TRANSFER:
                    filename = prop.getProperty(BLUETOOTH_TRANSFER_PROP);
                    break;
                default:
                    return null;
            }
            if (filename != null && !filename.isEmpty()) {
                return new File(ClientURISettings.LOG_WORKING + "/" + filename);
            }
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
        return null;
    }

    public static void writeToBluetooth(int type, String msg) {
        if(SystemWriter.allowBluetoothLogging){
        try {
            File logfile = getBluetoothLogFile(type);
            if (logfile == null) {
                return;
            }
            if(!logfile.exists()){
                FileUtils.touch(logfile);
            }
            FileUtils.writeStringToFile(logfile, msg, true);
        } catch (IOException ex) {
            logger.error(ex);
        }
        }else{
            logger.error("Logging is turned OFF: "+msg);
        }

    }

    public static void setCurrBluetoothCampaign(String currBluetoothCampaign) {
        SystemWriter.currBluetoothCamp = currBluetoothCampaign;
        SystemWriter.storeBluetoothCampaign();
    }

    public static void storeBluetoothCampaign() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)));
            prop.setProperty("active_bluetooth_campaign", SystemWriter.currBluetoothCamp);
            prop.store(new FileWriter(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)), "Logging Current Campaign");
        } catch (IOException ex) {
            logger.error(ex);
        }

    }
    
    public static String loadBluetoothCampaignId(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)));
            return prop.getProperty("active_bluetooth_campaign");
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static String loadBluetoothClearCache(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)));
            return prop.getProperty("clear_device_cache");
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static void storeBluetoothClearCache(boolean clearCache) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)));
            prop.setProperty("clear_device_cache", String.valueOf(clearCache));
            prop.store(new FileWriter(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)), "Storing cache");
        } catch (IOException ex) {
            logger.error(ex);
        }

    }
    
    public static void createBluetoothConfig(){
        File btProps = new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE);
        try
        {
            FileUtils.touch(btProps);
        } catch (IOException ex)
        {
            logger.error(ex);
        }
        
    }
    
    
    public static void allowBluetoothLogging(boolean allow){
        SystemWriter.allowBluetoothLogging = allow;
    }
    
}
