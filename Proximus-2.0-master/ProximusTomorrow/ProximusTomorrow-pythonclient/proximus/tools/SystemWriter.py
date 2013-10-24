from proximus.config.ClientURISettings import ClientURISettings
from proximus.config.files import hostapd, blutooth, obexsender, lighttpd, \
    captiveportal, captiveportal_hotspot, captiveportal_limited, default_dnsmasq, dnsmasq
from proximus.tools.FileSystemChecker import FileSystemChecker
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.TimeConstants import TimeConstants
import os
import datetime
import logging
from proximus.tools.Platform import Platform

logger = logging.root

class SystemWriter:

    @staticmethod
    def makeDefaultIndexPage():
        try:
            html = "<html><head><title>Proximus Mobility - Proximus Mobility, LLC.</title></head>"
            html += "<body><h1>Proximus Mobility</h1><div id=\"footer-address\"><p>75 Fifth Street NW, Atlanta, GA 30308</p>"
            html += "<p>Toll Free : 1.888.665.2527 <br> Direct : 404.477.3310 <br> Fax : 775.269.0387<br>Email : site@proximusmobility.com</p>"
            html += "</div></body></html>"
            FileSystemChecker.initDir(ClientURISettings.CAMPAIGNS_ROOT_DIR)
            filePath = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + "index.html"
            SystemWriter.writeFile(filePath, html)
            
        except Exception as err:
                logger.error(err)
    
    @staticmethod
    def makeErrorsPage():
        try:
            FileSystemChecker.initDir(ClientURISettings.ERROR_ROOT_DIR)
            filename = ClientURISettings.ERROR_ROOT_DIR + os.sep + "404.php"
            writer = open(filename, "w")
            writer.write("<?php")
            writer.write("\n")
            writer.write("header(\"Location: http://%s/\")" % ClientURISettings.ACCESS_POINT)
            writer.write("\n")
            writer.write("?>")
            writer.write("\n")
            writer.close()
        except Exception as err:
            logger.error(err)

    @staticmethod                                                                                                                                      
    def getCorrectExtBackhaulInterface(write=True):
        try:
            if ( write ):
                ProcessExecutor.setCorrectBackhaulInterface()                                                                              
            content = SystemWriter.readFileToString(ClientURISettings.CAPTIVEPORTAL_BACKHAUL_EXT_INTERFACE_FILE)                                           
            if (content != None):                                                                                                                        
                return content.strip()   
            else:
                if Platform.getPlatform() == Platform.PLATFORM_LINUX_BLUEGIGA:
                    return "nap"
                else:
                    return "eth0"       
        except Exception as err:
                logger.error(err)    
                logger.debug("Returning default interface")                                                                                                       
                if Platform.getPlatform() == Platform.PLATFORM_LINUX_BLUEGIGA:
                    return "nap"
                else:
                    return "eth0"
    
    @staticmethod
    def getCurrentExtBackhaulInterface():
        currentBackhaul = None
        try:
            if os.path.exists(ClientURISettings.CAPTIVEPORTAL_CONFIG_FILE):
                inf = open(ClientURISettings.CAPTIVEPORTAL_CONFIG_FILE, "r")
                for line in inf.readlines():
                    if line.find("EXT_INTERFACE=") == 0:
                        try:
                            currentBackhaul = line.split("=")[1].strip()
                        except Exception as err:
                            logger.info(err)
                            currentBackhaul = None
                inf.close()
        except Exception as err:
                    logger.error(err)
        return currentBackhaul
    
    @staticmethod
    def getCurrentAccessPointInterface():
        content = SystemWriter.readFileToString(ClientURISettings.ACCESS_POINT_INTERFACE_FILE)
        if (content != None):
            return content.strip()
        else:
            if(Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
                return "uap0"
            else:
                return "wlan0"
    
    @staticmethod
    def clearObexSenderBlockList():
        SystemWriter.writeFile(ClientURISettings.BLUETOOTH_OBEXSENDER_BLOCK_LIST, "")
    
    """
    Bluegiga sets the wi-fi channel and network name in a file hostapd.conf
    Read by the hostapd daemon
    """
    @staticmethod
    def writeHostAPDConfFile(campaign, restartServer=True):
        global DeviceClientInstance
        clientConfig = DeviceClientInstance.clientConfig
        
        if campaign != None:
            wifiCampaign = campaign.wifiCampaign
        else:
            wifiCampaign = None
        
        if wifiCampaign == None:
            networkName = ClientURISettings.MESSAGE_DEFAULT_WIFI
        else:
            networkName = wifiCampaign.networkName
            
        accessPointInterface = SystemWriter.getCurrentAccessPointInterface()
        
        data = hostapd.CONTENT.replace("PROXIMUSCHANNELHERE", clientConfig.channel
                                       ).replace("PROXIMUSNETWORKNAMEHERE", networkName
                                       ).replace("PROXIMUSACCESSPOINTHERE", accessPointInterface)
        
        if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DDWRT):
            logger.info("writing hostapd.conf file %s" % ClientURISettings.HOSTAPD_CONF_FILE_DDWRT)
            SystemWriter.writeFile(ClientURISettings.HOSTAPD_CONF_FILE_DDWRT, data)
        else:
            logger.info("writing hostapd.conf file %s" % ClientURISettings.HOSTAPD_CONF_FILE)
            SystemWriter.writeFile(ClientURISettings.HOSTAPD_CONF_FILE, data)
        if (restartServer):
            ProcessExecutor.restartLighttpd() # on bluegiga this script restarts the hostapd daemon too
    
    @staticmethod
    def makeDefaultLighttpdConfFile(restartServer=True):
            SystemWriter.makeLighttpdConfFile(None, restartServer)
    
    @staticmethod
    def makeLighttpdConfFile(campaign=None, restartServer=True):
        
        props = SystemWriter.readLogProperties()
        
        try:
            logger.info("writing lighttpd.conf file %s" % ClientURISettings.LIGHTTPD_FILE)
            
            proxdata = ""
            phppath = "/usr/bin/php"
            if (campaign != None):
                campaign_id = campaign.id
                doc_root = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + campaign_id + "/WIFI/"
            else:
                doc_root = ClientURISettings.CAMPAIGNS_ROOT_DIR
                
            log_file = ClientURISettings.LOG_WORKING + os.sep + props[ClientURISettings.WIFI_TRANSFER_PROP]
            
            http_doc_root = """server.document-root = "%s"\n""" % doc_root
            
            proxdata += http_doc_root
            
            if Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG:
                # On non-bluegiga devices we have to insert the MAC address in a pipe logger
                proxdata += """accesslog.filename = "|/home/proximus/bin/accesslog_addmac.py"\n"""
                proxdata += """accesslog.format = "[%h] %t [%r] [%s] [%{User-Agent}i] [%V]"\n"""
                phppath = "/usr/bin/php-cgi"
            else:
                proxdata += """accesslog.filename = "%s"\n""" % log_file
                proxdata += """accesslog.format = "[%h] %t [%r] [%s] [%{User-Agent}i] [%V] [%M]"\n"""
                phppath = "/usr/bin/php"
            
            content = lighttpd.CONTENT.replace("PROXIMUS_HTTP_CONFIG_HERE", proxdata)
            # virtual dir setup coolspot.proximusmobility\.com
            virtualDir ="""
$HTTP["host"] =~ "(^|\.)coolspot.proximusmobility\.com$" { 
  %s
}""" % http_doc_root
            content = content.replace("PROXIMUS_VIRTUAL_DIR", virtualDir)
            content = content.replace("PROXIMUS_PHP_PATH", phppath)
            SystemWriter.writeFile(ClientURISettings.LIGHTTPD_FILE, content)
            
            # Commented out, why is this here?  Do it separately
            #SystemWriter.writeCaptivePortalConf(campaign)
            
            if (restartServer):
                ProcessExecutor.restartLighttpd()
        
        except Exception as err:
            logger.error(err)
            
    @staticmethod
    def makeDnsmasqConfFile(restartDNSMasq=True):
        try:
            logger.info("writing dnsmasq.conf file %s" % ClientURISettings.DNSMASQ_CONFIG_FILE)      
                        
            range = "dhcp-range=192.168.45.50,192.168.45.150,1h"          
            leases = "dhcp-leasefile=%s" % (ClientURISettings.DNSMASQ_LEASES_FILE)
            config_dir = "conf-dir=/home/proximus/config/dnsmasq.d" 
            if Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG:
                pass 
            content = dnsmasq.CONTENT.replace("PROXIMUS_DHCP_RANGE", range).replace("PROXIMUS_DNS_LEASES_FILE", leases).replace("PROXIMUS_DNS_CONFIG_DIR", config_dir)
            SystemWriter.writeFile(ClientURISettings.DNSMASQ_CONFIG_FILE, content)           
            if (restartDNSMasq):
                ProcessExecutor.restartDNS()
        
        except Exception as err:
            logger.error(err)
            
    @staticmethod
    def makeDefaultDnsmasqConfFile(restartDNSMasq=True):
        #Only write file if its a dreamplug
        #bluegiga captiveportal takes care of this automatically
        if Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG:
            try:
                ProcessExecutor.setCorrectBackhaulInterface()
                logger.info("writing dnsmasq file %s" % ClientURISettings.DNSMASQ_DEFAULT_FILE)         
                content = default_dnsmasq.CONTENT.replace("PROXIMUS_DNSMASQ_CONFIG_FILE", ClientURISettings.DNSMASQ_CONFIG_FILE).replace("PROXIMUS_DNS_INTERFACE", SystemWriter.getCurrentAccessPointInterface())            
                SystemWriter.writeFile(ClientURISettings.DNSMASQ_DEFAULT_FILE, content)
                if (restartDNSMasq):
                    ProcessExecutor.restartDNS()
                    
            except Exception as err:
                logger.error(err)
        
    @staticmethod
    def writeFile(filepath, strContent):
        logger.debug("Writing to %s" % filepath)
        outf = open(filepath, "w")
        outf.write(strContent)
        outf.close()
        
    @staticmethod
    def readLogProperties():
        props = {}
        propfile = ClientURISettings.LOG_PROPERTIES_FILE
        if os.path.exists(propfile):
            inf = open(propfile, "r")
            for line in inf.readlines():
                key, val = line.split("=")
                key = key.strip()
                val = val.strip()
                props[key] = val
            inf.close()
        else:
            # sensible defaults
            return SystemWriter.writeLogProperties(read=False)
        return props

    @staticmethod
    def setLogProperty(key, value):
        props = SystemWriter.readLogProperties()
        props[key] = value
        SystemWriter.writeLogProperties(props=props)

    @staticmethod
    def saveLogProperties(props):
        propfile = ClientURISettings.LOG_PROPERTIES_FILE
        outf = open(propfile, "w")
        for key, val in props.items():
            outf.write("%s=%s\n" % (key, val))
        outf.close()
    
    @staticmethod
    def getFileName(campaign_type, campaignId=None):
        curr = datetime.datetime.utcnow()
        if (campaignId == None):
            campaignId = ClientURISettings.MESSAGE_NOACTIVE
        return campaign_type + "_" + ProcessExecutor.getMACeth0() + "_" + campaignId + "_" + TimeConstants.cronDateFormat(curr) + ".log"
    
    @staticmethod
    def writeLogProperties(props=None, read=True):
        global DeviceClientInstance
        
        if props != None:
            pass
        elif (read):
            props = SystemWriter.readLogProperties()
        else:
            props = {}
       
        currentBTCampaignId = None
        currentWifiCampaignId = None
        
        currentBTCampaign = DeviceClientInstance.campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_BLUETOOTH)
        if (currentBTCampaign != None):
            currentBTCampaignId = currentBTCampaign.id
        
        currentWifiCampaign = DeviceClientInstance.campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_WIFI)
        if (currentWifiCampaign != None):
            currentWifiCampaignId = currentWifiCampaign.id
            
        platform = Platform.getPlatform().lower()
        currBluetoothTransferFile = SystemWriter.getFileName(("bluetoothTransfer_%s" % platform), currentBTCampaignId)
        props[ClientURISettings.BLUETOOTH_TRANSFER_PROP] = currBluetoothTransferFile

        currentWifiLogFile = SystemWriter.getFileName(("wifi_%s" % platform), currentWifiCampaignId)
        props[ClientURISettings.WIFI_TRANSFER_PROP] = currentWifiLogFile
        SystemWriter.saveLogProperties(props)
        return props
    
    @staticmethod
    def writeBluetoothConfig(bluetoothCampaign):
        confFile = ClientURISettings.BLUETOOTH_IWRAP_CONFIG_FILE
        replaceToken = 'PROXIMUSBTNAMEHERE'
        if (bluetoothCampaign != None):
            confData = blutooth.CONTENT.replace(replaceToken, bluetoothCampaign.bluetoothCampaign.friendlyName)
        else:
            confData = blutooth.CONTENT.replace(replaceToken, ClientURISettings.MESSAGE_DEFAULT_BT)
        logger.info("Writing bluetooth.conf file %s" % confFile)
        SystemWriter.writeFile(confFile, confData)
    
    @staticmethod
    def getValidBTContent(folder):
        valid_files = []       
        btConf = "bt.conf"
        valid_extensions = ["jpg", "jpeg", "gif", "png", "3gp", "avi", "mov", "wmv", "mp4", "mp3", "jad", "jar", "apk", "ics", "txt", "vcf", "htm", "html"]   
        if os.path.exists(folder) and os.path.isdir(folder):
            if os.path.exists(folder + os.sep + btConf):
                btConfFile = open(folder + os.sep + btConf)
                for line in btConfFile.readlines():                    
                    line = line.strip()
                    splitname = line.split(".")
                    if len(splitname) > 1:
                        extension = splitname[-1].lower()
                        if extension in valid_extensions:
                            valid_files.append(line)
            else:
                logger.debug("bt.conf does not exist, we couldn't order the files")
                valid_files = []
                if os.path.exists(folder):
                    for item in os.listdir(folder):
                        if os.path.isfile(folder + os.sep + item):
                            splitname = item.split(".")
                            if len(splitname) > 1:
                                extension = splitname[-1].lower()
                                if extension in valid_extensions:
                                    valid_files.append(item)
                        else:
                            logger.debug("Item: %s does not exist, but that is probably normal" % item)
        else:
            logger.debug("Folder: %s does not exist, but that is probably normal" % folder)
        return valid_files

    @staticmethod
    def writeCaptivePortalConf(campaign=None):
        logger.info("Writing captive portal conf")                                                                                             
        extInterface = SystemWriter.getCorrectExtBackhaulInterface(write=False)
        
        if (campaign != None and int(campaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_HOTSPOT)):
            logger.info("Enabling internet hotspot")                               
            content = captiveportal_hotspot.CONTENT.replace("PROXIMUSEXTHERE", extInterface)
        elif (campaign != None and int(campaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_LIMITED)):
            logger.info("Enabling limited access internet")                               
            content = captiveportal_limited.CONTENT.replace("PROXIMUSEXTHERE", extInterface)
        elif (campaign != None and int(campaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_FACEBOOK)):
            logger.info("Enabling facebook hotspot")                               
            content = captiveportal_hotspot.CONTENT.replace("PROXIMUSEXTHERE", extInterface)
        else:                                   
            logger.info("Disabling internet hotspot, mode is captive only")
            content = captiveportal.CONTENT.replace("PROXIMUSEXTHERE", extInterface)         
                                                                                          
        if (campaign != None):                
            logger.info("campaign hotspot mode was " + campaign.wifiCampaign.hotspotMode)
        
        SystemWriter.writeFile(ClientURISettings.CAPTIVEPORTAL_CONFIG_FILE, content)      

    
    @staticmethod
    def writeObexSenderConfig(bluetoothCampaign):
        if bluetoothCampaign != None:
            campaignId = bluetoothCampaign.id
            campaignFolder = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + campaignId + os.sep + ClientURISettings.MESSAGE_BLUETOOTH
        else:
            campaignId = ClientURISettings.MESSAGE_NOACTIVE
            campaignFolder = ClientURISettings.CAMPAIGNS_ROOT_DIR
        
        block = "\nbasedir %s\n\n" % (campaignFolder + os.sep)
        bluetoothFiles = SystemWriter.getValidBTContent(campaignFolder)
        if len(bluetoothFiles) > 0:
            block += "send {\n"
            for file in bluetoothFiles:
                block += "    file %s\n" % file                                           
            block += "} "
            
        # now we add the log file name
        props = SystemWriter.readLogProperties()
        if (props.has_key(ClientURISettings.BLUETOOTH_TRANSFER_PROP)):
            current_bt_logfile_name = props[ClientURISettings.BLUETOOTH_TRANSFER_PROP]
        else:
            # need to generate it
            SystemWriter.writeLogProperties()
            props = SystemWriter.readLogProperties()
            current_bt_logfile_name = props[ClientURISettings.BLUETOOTH_TRANSFER_PROP]
            
        block += "\n\nlog %s\n\n" % (ClientURISettings.LOG_WORKING + os.sep + current_bt_logfile_name)
        
        content = obexsender.CONTENT.replace("PROXIMUSOBEXBODYHERE", block)
        logger.info("writing obexsender.conf file %s" % ClientURISettings.BLUETOOTH_OBEXSENDER_CONFIG_FILE)
        SystemWriter.writeFile(ClientURISettings.BLUETOOTH_OBEXSENDER_CONFIG_FILE, content)
        
    @staticmethod
    def readFileToString(filepath):
        if os.path.exists(filepath) and os.path.isfile(filepath):
            inf = open(filepath, "r")
            data = inf.read()
            inf.close()
            return data
        else:
            return None
    
    
