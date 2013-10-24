'''
Created on Jul 6, 2012

@author: dshaw
'''
from proximus.Watcher import Watcher
import datetime

class WifiWatcher(Watcher):
    '''
    Watch the dnsmasq lease file for changes and act when it does

Expires    MAC_address       IP_address     Hostname    Client-id
1341602849 c8:df:7c:4e:6a:4d 192.168.45.70 * 01:c8:df:7c:4e:6a:4d
1341602464 90:21:55:d8:ce:74 192.168.45.190 Android_354753040303576 01:90:21:55:d8:ce:74
1341601843 5c:da:d4:27:e8:bd 192.168.45.92 * 01:5c:da:d4:27:e8:bd
    '''
    filePath = "/home/proximus/config/dnsmasq.leases"

    def __init__(self):
        super(WifiWatcher, self).__init__()

    def parseUpdatedFile(self):
        
        newEntryDict = {}
        
        inf = open(self.filePath, 'ru')
        for line in inf.readlines():
            expiration, mac, ip, host, client  = line.strip().split()
            expiration = datetime.datetime.fromtimestamp(long(expiration))
            mac = mac.replace(":", "").upper()
            newEntryDict[mac] = { "expiration":expiration.strftime(self.DATE_FMT), "mac":mac, "ip":ip, "host":host, "client":client }
        inf.close()
        
        self.compareAndPublish("WIFI", newEntryDict)