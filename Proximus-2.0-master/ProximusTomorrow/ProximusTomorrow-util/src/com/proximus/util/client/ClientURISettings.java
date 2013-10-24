package com.proximus.util.client;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author ejohansson
 */
public class ClientURISettings
{

    private static final Logger logger = Logger.getLogger(ClientURISettings.class.getName());
    private static final boolean DEV = false;
    private static String warNameApi = "ProximusTomorrow-war/api";
    private static String devBaseUri = "http://localhost:8080" + "/" + warNameApi;
    private static String prodBaseUri = "http://dev.proximusmobility.net:8080" + "/" + warNameApi;
    private static String baseUri = DEV ? devBaseUri : prodBaseUri;
    //server specific
    public static String registrationUri = baseUri + "/register";
    public static String statusUri = baseUri + "/status";
    public static String uploadUri = baseUri + "/upload";
    public static String downloadUri = baseUri + "/download";
    public static String softwareUpdateUri = baseUri + "/software-update";       
    //Directory
    public static final String DIR = System.getProperty("file.separator");   
    //client specific
    public static final String PROXIMUS_ROOT = DIR + "home" + DIR + "proximus";
    //client's config files
    public static final String CONFIG_ROOT = PROXIMUS_ROOT + DIR + "config";
    public static final String CONFIG_FILE = CONFIG_ROOT + DIR + "client.cfg.xml";
    public static final String CAMPAIGNS_FILE = CONFIG_ROOT + DIR + "campaigns.cfg.xml";
    public static final String BLUETOOTH_CONFIG_FILE = "bluetooth.cfg.xml"; //This is campaign dependent (don't know the absoule path)
    public static final String BT_CAMPAIGN_PROPERTIES_FILE = CONFIG_ROOT + DIR + "bluetooth.properties";
    public static final String LIGHTTPD_FILE = CONFIG_ROOT + DIR + "lighttpd-inc.conf";
    public static final String PROXIMUS_PROPERTIES_FILE = CONFIG_ROOT + DIR + "proximus.properties";
    public static final String LOG_PROPERTIES_FILE = CONFIG_ROOT + DIR + "log.properties";
    //client's server content    
    public static final String CAMPAIGNS_ROOT_DIR = PROXIMUS_ROOT + DIR + "campaigns";
    public static final String ERROR_ROOT_DIR = CONFIG_ROOT + DIR + "www-errors";
    public static String accessPoint = "192.168.3.1";
    public static String serverPort = "80";
    //client log directory structure
    public static final String LOG_ROOT = PROXIMUS_ROOT + DIR + "logs";
    public static final String LOG_QUEUE = LOG_ROOT + DIR + "queue";
    public static final String LOG_COMPLETED = LOG_ROOT + DIR + "completed";
    public static final String LOG_WORKING = LOG_ROOT + DIR + "working";
    public static final String DATEFORMAT = "MMM/dd/yyyy HH:mm:ss z";
    //client bin directory
    public static final String BIN_DIR = PROXIMUS_ROOT + DIR + "bin";

    /*
     * BLUETOOTH VARIABLES
     */
    public static final int FILE_TIMEOUT_MINUTES = 240;
    public static final int MAX_CONNECTION_ATTEMPTS = 5;
    public static final int CONNECT_RETRY_SLEEP = 1500;
    public static final int MAX_SEARCH_ATTEMPTS = 5;
    public static final int SEARCH_RETRY_SLEEP = 1000;
    public static final long MAIN_THREAD_SLEEP = 20 * 1000;
    public static final long THREAD_POOL_KEEP_ALIVE = 15000;
    public static final String[] SUPPORTED_FILETYPES =
    {
        "3GP", "RM", "MP4", "MP3", "WMV", "OGG", "ACC", "JPG", "JPEG", "GIF", "PNG", "JAR", "TXT", "3gp", "rm", "mp4", "mp3", "wmv", "ogg", "acc", "jpg", "jpeg", "gif", "png", "jar", "txt"
    };
    /*
     * END BLUETOOTH VARIABLES
     */

    public static boolean isDEV()
    {
        return DEV;
    }

    /**
     * Change it only if a valid string and is not the same as the previous one
     *
     * @param server
     */
    public static void changeServer(String server)
    {

        if (server != null && !server.isEmpty() && !server.equals(baseUri.substring(0, server.length())))
        {
            baseUri = server + "/" + warNameApi;
            registrationUri = baseUri + "/register";
            statusUri = baseUri + "/status";
            uploadUri = baseUri + "/upload";
            logger.log(Priority.DEBUG, "Changing server to: " + baseUri);
        }
    }

    public static void changePort(String port)
    {
        if (port != null && !port.isEmpty() && !port.equals(serverPort))
        {
            serverPort = port;
            logger.log(Priority.DEBUG, "Changing port to: " + serverPort);
        }
    }

    public static void changeAccessPoint(String accessPoint)
    {
        if (accessPoint != null && !accessPoint.isEmpty() && !accessPoint.equals(ClientURISettings.accessPoint))
        {
            ClientURISettings.accessPoint = accessPoint;
            logger.log(Priority.DEBUG, "Changing Access Point to: " + ClientURISettings.accessPoint);
        }
    }
}
