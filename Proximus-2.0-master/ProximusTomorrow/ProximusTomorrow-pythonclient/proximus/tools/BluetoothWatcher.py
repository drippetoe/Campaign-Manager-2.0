'''
Created on Jul 6, 2012

@author: dshaw
'''
from proximus.config.ClientURISettings import ClientURISettings
from Watcher import Watcher
import datetime
import logging
from proximus.tools.ProcessExecutor import ProcessExecutor

logger = logging.root

class BluetoothWatcher(Watcher):
    '''
3c:74:37:67:c6:a5    1341584007
c0:18:85:fc:2e:22    1341584006
70:f1:a1:09:b8:ca    1341598235
40:2c:f4:51:dc:0a    1341598083
00:25:4b:f8:64:d3    1341598085
    '''
    filePath = ClientURISettings.BLUETOOTH_OBEXSENDER_BLOCK_LIST
    #"/home/proximus/config/blocklist.dump"

    def __init__(self, parent):
        Watcher.__init__(self, parent)
        
    
    def parseUpdatedFile(self):
        messageType = "Proximity"
        status = None
        messageFailed = None
        newEntryDict = {}
        
        inf = open(self.filePath, 'ru')
        for line in inf.readlines():
            try:
                mac, expiration = line.strip().split()
                expiration = datetime.datetime.fromtimestamp(long(expiration))
                mac = mac.replace(":", "").upper()
                logger.debug("MAC Address %s" % mac)
                status = "Success"
                newEntryDict[mac] = { "Type":messageType, "Status":status, "Source":"Bluetooth", "Expiration":expiration.strftime(self.DATE_FMT), "MAC":mac, "Device": ProcessExecutor.getMACeth0() }
            except Exception:
                logger.error("Unable to parse line: %s" % line)  
                status = "Failed"
                messageFailed = "Unable to parse line"
                newEntryDict[mac] = { "Type":messageType, "Status":status, "Error": messageFailed }
        inf.close()
        
        self.compareAndPublish("BLUETOOTH", newEntryDict)
