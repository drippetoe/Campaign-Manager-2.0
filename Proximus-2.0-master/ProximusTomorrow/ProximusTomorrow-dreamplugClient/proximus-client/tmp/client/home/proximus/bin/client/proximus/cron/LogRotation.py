import logging,threading, os, shutil
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.SystemWriter import SystemWriter
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.TimeConstants import TimeConstants

logger = logging.root

"""
Class to rotate the logs on the device at a regular interval, determined by the configuration in the server

Logs are queued for upload when rotated
"""
class LogRotation(threading.Thread):
    
    def __init__(self):
        threading.Thread.__init__(self)
    
    @staticmethod
    def isRotationNeeded():
        global DeviceClientInstance
        
        logging = DeviceClientInstance.clientConfig.logging
        if ( logging == None):
            return False
        rotationInterval = logging.rotation  # interval in MS
        
        props = SystemWriter.readLogProperties()
        if props.has_key(ClientURISettings.WIFI_TRANSFER_PROP):
            wifiLogName = props[ClientURISettings.WIFI_TRANSFER_PROP]
        else:
            props = SystemWriter.writeLogProperties(props, False)
            wifiLogName = props[ClientURISettings.WIFI_TRANSFER_PROP]
            
        # wifi_bluegiga_B88D1200CFE2_-1_2012-03-27.12-04-41.log
        lastRotationDateStr = wifiLogName.split("_")[-1].replace(".log", "")
        lastRotationDate = TimeConstants.dateParseCronDate(lastRotationDateStr)
        if TimeConstants.dateIntervalExceeded(lastRotationDate, rotationInterval):
            return True
        else:
            return False
        
        
    
    @staticmethod
    def run():
        global DeviceClientInstance
        
        if not LogRotation.isRotationNeeded():
            logger.debug("Log rotation not needed at this time")
            return
        
        logger.info("Running log rotation")
        
        ProcessExecutor.stopBluetooth()
        ProcessExecutor.stopLighttpd()
        
        try:
            newProps = SystemWriter.writeLogProperties(None, False)
            
            try:
                logger.info("Rotating bluetooth log, new file is %s" % newProps[ClientURISettings.BLUETOOTH_TRANSFER_PROP])
                logger.info("Rotating wi-fi log, new file is %s" % newProps[ClientURISettings.WIFI_TRANSFER_PROP])
            except:
                pass # don't care
            
            filesToKeep = newProps.values()
            
            workingDir = ClientURISettings.LOG_WORKING
            for file in os.listdir(workingDir):
                if file not in filesToKeep:
                    sourceFile = ClientURISettings.LOG_WORKING + os.sep + file
                    destFile = ClientURISettings.LOG_QUEUE + os.sep + file
                    logger.info("Moving %s to queue" % sourceFile)
                    shutil.move(sourceFile, destFile)
                else:
                    logger.info("%s is active" % file)
     
            campaignHandler = DeviceClientInstance.campaignHandler
            if ( campaignHandler != None):
                activeBTCampaign = campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_BLUETOOTH)
                activeWifiCampaign = campaignHandler.getActiveCampaign(ClientURISettings.MESSAGE_WIFI)
                
                # we write some additional things here because it also runs at startup
                SystemWriter.writeHostAPDConfFile(activeWifiCampaign, False)
                SystemWriter.makeLighttpdConfFile(activeWifiCampaign, False)
                SystemWriter.writeObexSenderConfig(activeBTCampaign) 
                SystemWriter.writeBluetoothConfig(activeBTCampaign)    
            
        except Exception as err:
            logger.error(err)
        
        try:
            ProcessExecutor.startBluetooth()
            ProcessExecutor.startLighttpd()
        except Exception as err:
            logger.error(err)
            