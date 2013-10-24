import logging, os, threading
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.SystemWriter import SystemWriter
from proximus.tools.Platform import Platform

logger = logging.root
class BluetoothActiveCampaignCron(threading.Thread):
    
    def __init__(self):
        threading.Thread.__init__(self)
    
    @staticmethod
    def run():
        global DeviceClientInstance
        
        # See which campaign is active now, if it's not right, 
        # the right way to see which Wi-Fi is active is to read the lighttpd.conf
        # and look at the 'server.document-root'
        # we really should test further and see if the content served matches the content 
        # in the campaign
        
        currentCampaignId = None
        
        if (os.path.exists(ClientURISettings.BLUETOOTH_OBEXSENDER_CONFIG_FILE)):
            inf = open(ClientURISettings.BLUETOOTH_OBEXSENDER_CONFIG_FILE, "r")
            
            for line in inf.readlines():
                #  """server.document-root = "%s"\n""" % doc_root
                if line.find("basedir ") == 0:
                    try:
                        bt_dir = line.split("/")
                        
                        if (len(bt_dir) == 4) or (len(bt_dir) == 5):
                            pass # no active
                        else:
                            currentCampaignId = line.split("/")[4]
                        break
                    except Exception as err:
                        logger.info(err)
            inf.close()
        
            if (currentCampaignId == ClientURISettings.MESSAGE_NOACTIVE):
                currentCampaignId = None
        else:
            logger.debug("Config file does not exist %s" % ClientURISettings.BLUETOOTH_OBEXSENDER_CONFIG_FILE)
        
        correctActiveCampaign = DeviceClientInstance.campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_BLUETOOTH)
        
        """
        Possibilites:
            1) No campaign is active and none should be  -- RETURN
            2) A campaign is active, and it's the right one -- RETURN
            3) A campaign is active, but none should be -- REDO AS DEFAULT CONFIG & RESTART
            4) No campaign is active and one should be -- REDO CONFIG & RESTART
            5) A campaign is active, but it's the wrong one  -- REDO CONFIG & RESTART
        """
        
        if (currentCampaignId == None and correctActiveCampaign == None):
            logger.debug("No campaign changes necessary, no campaign active")
            if(Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):  
                logger.debug("Stopping bluetooth so we don't clog the wifi")
                ProcessExecutor.stopBluetooth()
            return
        elif ((currentCampaignId != None) and (correctActiveCampaign != None)  and (int(currentCampaignId) == int(correctActiveCampaign.id))):
            logger.debug("No campaign changes necessary, campaign %d active" % str(correctActiveCampaign.id))
            return
        elif correctActiveCampaign == None:
            logger.info("No campaign should be active, deactivating")
            SystemWriter.setLogProperty(ClientURISettings.BLUETOOTH_TRANSFER_PROP, SystemWriter.getFileName(ClientURISettings.MESSAGE_BLUETOOTH, None))
            SystemWriter.writeObexSenderConfig(None)
            SystemWriter.writeBluetoothConfig(None)
            if(Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):  
                logger.debug("Stopping bluetooth")
                ProcessExecutor.stopBluetooth()
            if(Platform.getPlatform() == Platform.PLATFORM_LINUX_BLUEGIGA):
                logger.debug("Restarting bluetooth")              
                ProcessExecutor.restartBluetooth()
                
        else:
            logger.info("Campaign changes needed, updating and restarting")
            SystemWriter.setLogProperty(ClientURISettings.BLUETOOTH_TRANSFER_PROP, SystemWriter.getFileName(ClientURISettings.MESSAGE_BLUETOOTH, correctActiveCampaign.id))
            SystemWriter.writeObexSenderConfig(correctActiveCampaign)
            SystemWriter.writeBluetoothConfig(correctActiveCampaign)
            SystemWriter.clearObexSenderBlockList()   
            logger.debug("Restarting bluetooth")
            ProcessExecutor.restartBluetooth()
            
