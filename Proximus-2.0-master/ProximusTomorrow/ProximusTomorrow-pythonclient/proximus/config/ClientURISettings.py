import os, logging
from proximus.tools.Platform import Platform

logger = logging.root

class ClientURISettings:
    
    DEV = True
    if DEV:
        SSL = False
    else:
        SSL = True
    
    WAR_NAME_API = "ProximusTomorrow-war/api"
    # if this is explicitly set it overrides the "DEV" flag
    server = None
    server_port = None
    
    #DEV_SERVER = "localhost"
    DEV_SERVER = "dev.proximusmobility.net"
    PROD_SERVER = "devices.proximusmobility.com"
    
    if Platform.getPlatform() == Platform.PLATFORM_LINUX_DDWRT:
        PROXIMUS_ROOT = "/jffs/home/proximus"
    else:
        PROXIMUS_ROOT = "/home/proximus"
    
    #client's config files
    CONFIG_ROOT = PROXIMUS_ROOT + os.sep + "config"
    
    CONFIG_FILE = CONFIG_ROOT + os.sep + "client.cfg.xml"
    CAMPAIGNS_FILE = CONFIG_ROOT + os.sep + "campaigns.cfg.xml"
    BLUETOOTH_CONFIG_FILE = "bluetooth.cfg.xml" #This is campaign dependent (don't know the absolute path)
    ACCESS_POINT_INTERFACE_FILE = CONFIG_ROOT + os.sep + "access_point.conf"
    CAPTIVEPORTAL_BACKHAUL_EXT_INTERFACE_FILE = CONFIG_ROOT + os.sep + "backhaul.conf"
    BLUETOOTH_OBEXSENDER_CONFIG_FILE = CONFIG_ROOT + os.sep + "obexsender.conf"
    
    if Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG:
        BLUETOOTH_OBEXSENDER_BLOCK_LIST = CONFIG_ROOT + os.sep + "blocklist.dump"
    else:
        BLUETOOTH_OBEXSENDER_BLOCK_LIST = "/home/proximus/config/blocklist.dump" 

    BLUETOOTH_IWRAP_CONFIG_FILE = "/etc/bluetooth.conf"
    CAPTIVEPORTAL_CONFIG_DIR = "/etc/sysconfig"
    CAPTIVEPORTAL_CONFIG_FILE = CAPTIVEPORTAL_CONFIG_DIR + os.sep + "captiveportal"
    HOSTAPD_CONF_FILE = "/etc/hostapd.conf"
    HOSTAPD_CONF_FILE_DDWRT = CONFIG_ROOT + os.sep + "hostapd.conf"
    DNSMASQ_DEFAULT_FILE = "/etc/default/dnsmasq"
    DNSMASQ_CONFIG_FILE = CONFIG_ROOT + os.sep + "dnsmasq.conf"
    DNSMASQ_CONFIG_FOLDER = CONFIG_ROOT + os.sep + "dnsmasq.d"
    DNSMASQ_LEASES_FILE = CONFIG_ROOT + os.sep + "dnsmasq.leases"
    LIGHTTPD_FILE = CONFIG_ROOT + os.sep + "lighttpd-inc.conf"
    PROXIMUS_PROPERTIES_FILE = CONFIG_ROOT + os.sep + "proximus.properties"
    LOG_PROPERTIES_FILE = CONFIG_ROOT + os.sep + "log.properties"
    
    BLUETOOTH_TRANSFER_PROP = "bluetooth_transfer"
    BLUETOOTH_DWELL_PROP = "bluetooth_dwell"
    WIFI_TRANSFER_PROP = "wifi_transfer"
    
    #client's server content    
    CAMPAIGNS_ROOT_DIR = PROXIMUS_ROOT + os.sep + "campaigns"
    ERROR_ROOT_DIR = CONFIG_ROOT + os.sep + "www-errors"
    SWUPDATE_ROOT_DIR = PROXIMUS_ROOT + os.sep + "swupdate"
    
    # campaign connectivity types
    CAMPAIGN_NO_INTERNET = 1
    CAMPAIGN_LIMITED = 2
    CAMPAIGN_HOTSPOT = 3
    CAMPAIGN_MOBILOZOPHY = 4
    CAMPAIGN_FACEBOOK = 5
    
    # Bluegiga defaults to this value
    ACCESS_POINT = "192.168.45.3"
    ACCESS_POINT_PORT = "8080"
    
    #client log directory structure
    LOG_ROOT = PROXIMUS_ROOT + os.sep + "logs"
    LOG_QUEUE = LOG_ROOT + os.sep + "queue"
    LOG_COMPLETED = LOG_ROOT + os.sep + "completed"
    LOG_WORKING = LOG_ROOT + os.sep + "working"
    
    #client bin directory
    BIN_DIR = PROXIMUS_ROOT + os.sep + "bin"
    BASH_DIR = BIN_DIR + os.sep + "bash_scripts"
    
    # messages
    MESSAGE_NOACTIVE = "-1"
    MESSAGE_BLUETOOTH = "BLUETOOTH"
    MESSAGE_WIFI = "WIFI"
    MESSAGE_DEFAULT_WIFI = "Proximus Mobility Offer Network"
    MESSAGE_DEFAULT_BT = "Proximus Mobility"


    @staticmethod
    def isDEV():
        return ClientURISettings.DEV

    @staticmethod
    def getServer():
        if (ClientURISettings.server == None):
            this_server = ClientURISettings.DEV_SERVER if ClientURISettings.DEV else ClientURISettings.PROD_SERVER
        else:
            this_server = ClientURISettings.server
        
        if (ClientURISettings.SSL):
            transport = "https"
        else:
            transport = "http"
        
        return transport + "://" + this_server + ":" + ClientURISettings.getPort() + "/" + ClientURISettings.WAR_NAME_API
    
    @staticmethod
    def getPort():
        if (ClientURISettings.server_port != None):
            return ClientURISettings.server_port
        else:
            if (ClientURISettings.SSL):
                server_port = "8181" if ClientURISettings.isDEV() else "8443"
            else:
                server_port = "8080" if ClientURISettings.isDEV() else "80"
            return server_port
        
    @staticmethod
    def getRegistrationUri():
        logger.debug("Returning %s" % ClientURISettings.getServer() + "/register")
        return ClientURISettings.getServer() + "/register"
    
    @staticmethod
    def getStatusUri():
        return ClientURISettings.getServer() + "/status"
        
    @staticmethod
    def getUploadUri():  
        return ClientURISettings.getServer() + "/upload"
    
    @staticmethod
    def getDownloadUri():  
        return ClientURISettings.getServer() + "/download"
    
    @staticmethod
    def getSoftwareUpdateURI():  
        return ClientURISettings.getServer() + "/software-update"
        
    """
    * Change it only if a valid and is not the same as the previous one
    * @param server 
    """
    @staticmethod
    def changeServer(server):
        if (server != None and (len(server) > 0) and (server != ClientURISettings.server)):
            ClientURISettings.server = server
            ClientURISettings.logger.debug("Changing server to: " + ClientURISettings.server)

    @staticmethod
    def changePort(port):
        if ((port != None) and (len(port) > 0) and (port != ClientURISettings.server_port)):
            ClientURISettings.server_port = port
            ClientURISettings.logger.debug("Changing port to: " + ClientURISettings.server_port)

    @staticmethod
    def changeAccessPoint(accessPoint):
        if((accessPoint != None) and (len(accessPoint) > 0) and (accessPoint != ClientURISettings.ACCESS_POINT)):
            ClientURISettings.ACCESS_POINT = accessPoint
            ClientURISettings.logger.debug("Changing Access Point to: " + ClientURISettings.ACCESS_POINT)
