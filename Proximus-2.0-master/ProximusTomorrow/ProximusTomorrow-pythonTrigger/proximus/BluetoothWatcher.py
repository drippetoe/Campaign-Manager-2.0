'''
Created on Jul 6, 2012

@author: dshaw
'''
from proximus.Watcher import Watcher
import datetime

class BluetoothWatcher(Watcher):
    '''
3c:74:37:67:c6:a5    1341584007
c0:18:85:fc:2e:22    1341584006
70:f1:a1:09:b8:ca    1341598235
40:2c:f4:51:dc:0a    1341598083
00:25:4b:f8:64:d3    1341598085
    '''
    filePath = "/home/proximus/config/blocklist.dump"

    def __init__(self):
        super(BluetoothWatcher, self).__init__()
    
    def parseUpdatedFile(self):
        
        newEntryDict = {}
        
        inf = open(self.filePath, 'ru')
        for line in inf.readlines():
            mac, expiration = line.strip().split()
            expiration = datetime.datetime.fromtimestamp(long(expiration))
            mac = mac.replace(":", "").upper()
            newEntryDict[mac] = { "expiration":expiration.strftime(self.DATE_FMT), "mac":mac }
        inf.close()
        
        self.compareAndPublish("BLUETOOTH", newEntryDict)