/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ServerURISettings {
    //server specific

    private static final boolean DEV = false;
    private static String warNameApi = "ProximusTomorrow-war/api";
    private static String devBaseUri = "http://localhost:8080" + "/" + warNameApi;
    private static String prodBaseUri = "http://dev.proximusmobility.net:8080" + "/" + warNameApi;
    private static String baseUri = DEV ? devBaseUri : prodBaseUri;
    
    public static final String OS_SEP = System.getProperty("file.separator");
    public static final String PROXIMUS_ROOT = OS_SEP + "home" + OS_SEP + "proximus" + OS_SEP + "server";
    public static final String SERVER_TMP = PROXIMUS_ROOT + OS_SEP + "tmp";
    //where campaigns are going to be stored in the server
    public static final String CAMPAIGNS_ROOT_DIR = PROXIMUS_ROOT + OS_SEP + "campaigns";
    public static final String LOGOS_ROOT_DIR = PROXIMUS_ROOT + OS_SEP + "logos";
    public static final String TEMPLATE_ROOT_DIR = PROXIMUS_ROOT + OS_SEP + "templates";
    public static final String CONTENT_GENERATOR_TMP_DIR = SERVER_TMP + OS_SEP + "cgtemp";
//    public static final String CAMPAIGN_UPLOADS_DIR = CAMPAIGNS_ROOT_DIR + OS_SEP + "uploads";
//    public static final String WIFI_CAMPAIGN_UPLOADS = CAMPAIGN_UPLOADS_DIR + OS_SEP + "wifi";
//    public static final String BLUETOOTH_CAMPAIGN_UPLOADS = CAMPAIGN_UPLOADS_DIR + OS_SEP + "bluetooth";
    //Software updates
    public static final String SOFTWARE_RELEASE_DIR = PROXIMUS_ROOT + OS_SEP + "software_updates";
    public static final String DEVICE_IMPORT_DIR = PROXIMUS_ROOT + OS_SEP + "device-import";
    public static final String SMS_USER_IMPORT_DIR = PROXIMUS_ROOT + OS_SEP + "sms-user";
    //client log directory structure
    public static final String LOG_ROOT = PROXIMUS_ROOT + OS_SEP + "logs";
    public static final String LOG_QUEUE = LOG_ROOT + OS_SEP + "queue";
    public static final String LOG_COMPLETED = LOG_ROOT + OS_SEP + "completed";
    public static final String LOG_WORKING = LOG_ROOT + OS_SEP + "working";
    public static final String LOG_ERROR = LOG_ROOT + OS_SEP + "error";
    public static final String DATEFORMAT = "MMM/dd/yyyy HH:mm:ss z";
    public static final String FILE_DATEFORMAT = "MM.dd.yyyy.HH.mm.ss.z";
    //SMS
    public static final String KAZE_PACKET_FOLDER = PROXIMUS_ROOT + OS_SEP + "kaze";
    public static final String STATIC_MOBILE_OFFER_ROOT = PROXIMUS_ROOT + OS_SEP + "static" + OS_SEP + "www";
}
