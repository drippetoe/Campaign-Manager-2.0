from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.Platform import Platform
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.SystemWriter import SystemWriter
import logging
import os
import threading

logger = logging.root

class WifiActiveCampaignCron(threading.Thread):
    
    def __init__(self):
        threading.Thread.__init__(self)
    
    @staticmethod
    def ensureDNSMasqCorrect(campaign):
        needsDNSRestart = False
        
        try:
            if (campaign == None):
                correctDomains = []
            else:
                correctDomains = campaign.wifiCampaign.hotspotDomains
            
            DEFAULT_HOTSPOT = "hotspot_rule"
            DEFAULT_CAPTIVE = "captive_rule"
            
            if (campaign != None and int(campaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_HOTSPOT)):
                correctDomains.append(DEFAULT_HOTSPOT)
            elif (campaign != None and int(campaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_FACEBOOK)):
                logger.debug("Facebook campaign, no DNS rules")
            else:
                correctDomains.append(DEFAULT_CAPTIVE)
            
            
            for domain_file in os.listdir(ClientURISettings.DNSMASQ_CONFIG_FOLDER):
                if domain_file not in correctDomains:
                    filepath = ClientURISettings.DNSMASQ_CONFIG_FOLDER + os.sep + domain_file
                    if (os.path.exists(filepath)):
                        logger.info("Deleting old hotspot domain %s" % domain_file)
                        os.remove(ClientURISettings.DNSMASQ_CONFIG_FOLDER + os.sep + domain_file)
                        needsDNSRestart = True
            for domain in correctDomains:
                filepath = ClientURISettings.DNSMASQ_CONFIG_FOLDER + os.sep + domain
                if not os.path.isfile(filepath):    
                    if (domain == DEFAULT_HOTSPOT):
                        strContent = "# disabled in hotspot mode\n#address=/#/192.168.45.3"
                        SystemWriter.writeFile(filepath, strContent)
                    elif (domain == DEFAULT_CAPTIVE):
                        strContent = "address=/#/192.168.45.3"
                        SystemWriter.writeFile(filepath, strContent)
                    else:
                        strContent = "server=/%s/#\n" % domain
                        SystemWriter.writeFile(filepath, strContent)
                    needsDNSRestart = True
            
        except Exception as err:
            logger.error(err)
            
        if (needsDNSRestart == True):
            ProcessExecutor.restartDNS()
        
    
    @staticmethod
    def run():
        global DeviceClientInstance
        
        # See which campaign is active now, if it's not right, 
        # the right way to see which Wi-Fi is active is to read the lighttpd.conf
        # and look at the 'server.document-root'
        logger.debug("WifiActiveCampaignCron running...")
        
        currentCampaignId = None
        
        try:
            if (os.path.exists(ClientURISettings.LIGHTTPD_FILE)):
                inf = open(ClientURISettings.LIGHTTPD_FILE, "r")
                
                for line in inf.readlines():
                    #  """server.document-root = "%s"\n""" % doc_root
                    if line.find("server.document-root") == 0:
                        try:
                            dirs = line.split('"')[1].split("/")
                            if len(dirs) == 4:
                                # no active campaign
                                pass
                            else:
                                currentCampaignId = dirs[4]
                            break
                        except Exception as err:
                            logger.info(err)
                inf.close()
            
                if (currentCampaignId == ClientURISettings.MESSAGE_NOACTIVE):
                    currentCampaignId = None
            else:
                # lighttpd doesn't exist, so we should ensure below that one gets written
                SystemWriter.makeLighttpdConfFile(None, True)
                SystemWriter.writeCaptivePortalConf(None)
                
            correctActiveCampaign = DeviceClientInstance.campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_WIFI)
            
            # Check to see if the Channel is correct, if not, that needs to happen too
            currentChannel = None
            currentAccessPointInterface = None
            currentSSID = None
            
            if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DDWRT):
                hostAPDFile = ClientURISettings.HOSTAPD_CONF_FILE_DDWRT
            else:
                hostAPDFile = ClientURISettings.HOSTAPD_CONF_FILE
           
            
            if (os.path.exists(hostAPDFile)):
                inf = open(hostAPDFile, "r")
                for line in inf.readlines():
                    #  """server.document-root = "%s"\n""" % doc_root
                    if line.find("channel") == 0:
                        try:
                            currentChannel = line.split("=")[1].strip()
                        except Exception as err:
                            logger.info(err)
                    elif line.find("interface") == 0:
                        try:
                            currentAccessPointInterface = line.split("=")[1].strip()
                        except Exception as err:
                            logger.info(err)
                    elif line.find("ssid") == 0:
                        try:
                            currentSSID = line.split("=")[1].strip()
                        except Exception as err:
                            logger.info(err)
                inf.close()
            
            correctChannel = DeviceClientInstance.clientConfig.channel
            
            correctAccessPointInterface = SystemWriter.getCurrentAccessPointInterface()
            
            try:
                correctSSID = correctActiveCampaign.wifiCampaign.networkName
            except:
                correctSSID = ClientURISettings.MESSAGE_DEFAULT_WIFI
            
            needHostAPDUpdate = False
            if (correctChannel != currentChannel):
                logger.info("Current channel: %s, correct channel is %s" % (currentChannel, correctChannel))
                needHostAPDUpdate = True
            
            if (correctSSID != currentSSID):
                logger.info("Current SSID: %s, correct SSID is %s" % (currentSSID, correctSSID))
                needHostAPDUpdate = True
                
            if (correctAccessPointInterface != currentAccessPointInterface):
                logger.info("Current interface: %s, correct interface is %s" % (currentAccessPointInterface, correctAccessPointInterface))
                needHostAPDUpdate = True
            

            needCaptivePortalConfUpdate = False 
            #correct
            correctBackhaul = SystemWriter.getCorrectExtBackhaulInterface()
            #current
            currentBackhaul = SystemWriter.getCurrentExtBackhaulInterface()
           
            
            if (currentBackhaul != correctBackhaul):
                logger.info("current (%s) != correct (%s)" % (str(currentBackhaul), str(correctBackhaul)))
                needCaptivePortalConfUpdate = True


            # Make sure the hotspot domains are correct
            WifiActiveCampaignCron.ensureDNSMasqCorrect(correctActiveCampaign)
            
            
            """
            Possibilites:
                1) No campaign is active and none should be  -- RETURN
                2) A campaign is active, and it's the right one -- RETURN
                3) A campaign is active, but none should be -- REDO AS DEFAULT CONFIG & RESTART
                4) No campaign is active and one should be -- REDO CONFIG & RESTART
                5) A campaign is active, but it's the wrong one  -- REDO CONFIG & RESTART
                
                For the Wi-Fi Channel, if it is incorrect, writing hostapd.conf and restarting lighttpd will fix it.  
                
            """
            logger.debug("Need HOSTAPD Update: %s " % str(needHostAPDUpdate))
            logger.debug("Need CaptivePortalConf Update: %s" % str(needCaptivePortalConfUpdate))
            
            if ((currentCampaignId == None) and (correctActiveCampaign == None)):
                if (needHostAPDUpdate):
                    SystemWriter.writeHostAPDConfFile(None, True)
                if (needCaptivePortalConfUpdate):
                    SystemWriter.writeCaptivePortalConf(currentCampaignId)
                    
                logger.debug("No campaign changes necessary, no campaign active")
                
            elif ((currentCampaignId != None) and (correctActiveCampaign != None) and ( int(currentCampaignId) == int(correctActiveCampaign.id) ) ):
                if (needHostAPDUpdate):
                    SystemWriter.writeHostAPDConfFile(correctActiveCampaign, True)
                if (needCaptivePortalConfUpdate):
                    SystemWriter.writeCaptivePortalConf(correctActiveCampaign)
                    
                logger.debug("No campaign changes necessary, campaign %s active" % str(correctActiveCampaign.id))
                
            elif (correctActiveCampaign == None):
                logger.info("No campaign should be active, deactivating")
                SystemWriter.setLogProperty(ClientURISettings.WIFI_TRANSFER_PROP, SystemWriter.getFileName(ClientURISettings.MESSAGE_WIFI, None))
                SystemWriter.writeCaptivePortalConf(None)
                SystemWriter.makeLighttpdConfFile(None, False)
                SystemWriter.writeHostAPDConfFile(None, True)
                
                if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
                    ProcessExecutor.openCaptiveOrLimitedPortal()
                
            else:
                logger.info("Campaign changes needed, updating and restarting with from campaign [%s] to active [%s]" % (str(currentCampaignId), (str(correctActiveCampaign.id))))
                SystemWriter.setLogProperty(ClientURISettings.WIFI_TRANSFER_PROP, SystemWriter.getFileName(ClientURISettings.MESSAGE_WIFI, correctActiveCampaign.id))
                SystemWriter.writeCaptivePortalConf(correctActiveCampaign)
                SystemWriter.makeLighttpdConfFile(correctActiveCampaign, False)
                SystemWriter.writeHostAPDConfFile(correctActiveCampaign, True)
                #CAMPAIGN_NO_INTERNET = 1
                #CAMPAIGN_LIMITED = 2
                #CAMPAIGN_HOTSPOT = 3
                #CAMPAIGN_MOBILOZOPHY = 4
                #CAMPAIGN_FACEBOOK = 5
                if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
                    if (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_HOTSPOT)):
                        ProcessExecutor.openHotspotPortal()
                    elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_NO_INTERNET)):
                        ProcessExecutor.openCaptiveOrLimitedPortal()
                    elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_LIMITED)):
                        ProcessExecutor.openCaptiveOrLimitedPortal()
                    elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_FACEBOOK)):
                        ProcessExecutor.openFacebookPortal()                    
                    else:
                        logger.error("Not sure what to do with wifi campaign of type %s" % correctActiveCampaign.wifiCampaign.hotspotMode)
                else:
                    logger.debug("Alternate campaign modes not set up for this platform")
            
        except Exception as err:
            logger.error(err)
            
               
