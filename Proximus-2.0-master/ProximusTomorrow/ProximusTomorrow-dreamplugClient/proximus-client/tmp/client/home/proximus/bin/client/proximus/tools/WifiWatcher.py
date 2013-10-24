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

class WifiWatcher(Watcher):
    '''
    Watch the dnsmasq lease file for changes and act when it does

Expires    MAC_address       IP_address     Hostname    Client-id
1341602849 c8:df:7c:4e:6a:4d 192.168.45.70 * 01:c8:df:7c:4e:6a:4d
1341602464 90:21:55:d8:ce:74 192.168.45.190 Android_354753040303576 01:90:21:55:d8:ce:74
1341601843 5c:da:d4:27:e8:bd 192.168.45.92 * 01:5c:da:d4:27:e8:bd
    '''
    filePath = ClientURISettings.DNSMASQ_LEASES_FILE
    #"/home/proximus/config/dnsmasq.leases"

    def __init__(self, parent):
        Watcher.__init__(self, parent)

    def parseUpdatedFile(self):
        
        newEntryDict = {}
        messageType = "Proximity"
        status = None
        messageFailed = None
        
        inf = open(self.filePath, 'ru')
        for line in inf.readlines():
            try:
                expiration, mac, ip, host, client = line.strip().split()
                expiration = datetime.datetime.fromtimestamp(long(expiration))
                mac = mac.replace(":", "").upper()
                logger.debug("MAC Address %s" % mac)
                status = "Success"
                newEntryDict[mac] = { "Type":messageType, "Status":status, "Source":"Wi-Fi", "Expiration":expiration.strftime(self.DATE_FMT), "MAC":mac, "IP":ip, "Device": ProcessExecutor.getMACeth0() }
            except Exception:
                logger.error("Unable to parse line: %s" % line)  
                status = "Failed"
                messageFailed = "Unable to parse line"
                newEntryDict[mac] = { "Type":messageType,"Status":status, "Error": messageFailed }
        inf.close()
        
        self.compareAndPublish("WIFI", newEntryDict)
