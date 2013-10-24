'''
Created on Jul 6, 2012

@author: dshaw
'''
from threading import Thread
import os, time, logging
import datetime

logger = logging.root

class Watcher(Thread):
    
    ADDED = "ADDED"
    REMOVED = "REMOVED"
    UPDATED = "UPDATED"
    DATE_FMT = "%Y-%m-%d.%H:%M:%S"
    '''
    classdocs
    '''
    filePath = None
    lastModifiedTime = 0
    ssl_on = False
    entryDict = {}
    pubnub = None
        
    def pubnubPublishDict(self, messageDict, eventType=None):

        eventDate = datetime.datetime.now().strftime(self.DATE_FMT)
        messageDict["EventDate"] = eventDate
        if eventType != None:
            messageDict["EventType"] = eventType
        
        info = self.pubNubHandler.pubNubKey.pubnub.publish({
                               'channel' : self.pubNubHandler.pubNubKey.channel,
                               'message' : messageDict })
        logger.info(info)

    def __init__(self, handler):
        Thread.__init__(self)
        self.pubNubHandler = handler
        

    
    def compareAndPublish(self, messageKeyPrefix, newEntryDict):
        removedMacs = []
        addedMacs = []
        changedMacs = []
        # figure out which MAC addresses changed
        for key, value in self.entryDict.items():
            if key not in newEntryDict.keys():
                removedMacs.append(value)
        for key, value in newEntryDict.items():
            if key not in self.entryDict.keys():
                addedMacs.append(value)
            elif (value != self.entryDict[key]):
                    changedMacs.append(value)
        
        self.entryDict = newEntryDict

        for removed in removedMacs:
            self.pubnubPublishDict(removed, eventType="removed")
        
        for added in addedMacs:
            self.pubnubPublishDict(added, eventType="added")
    
    def run(self):
        while (True):
            if (self.pubNubHandler.pubNubKey):
                time.sleep(10)        
                try:
                    if (os.path.exists(self.filePath)):
                        currentModificationTime = os.stat(self.filePath).st_mtime
                
                        if (currentModificationTime > self.lastModifiedTime):
                            self.lastModifiedTime = currentModificationTime
                            self.parseUpdatedFile()
                    else:
                        logger.error("File %s does not exist" % self.filePath)
                except Exception as err:
                    logger.error(self)
                    logger.info("Concurrent file modification error, sleeping three seconds: %s" % self.filePath)
                    time.sleep(3)        
            else:    
                logger.debug("No PubNub keys are available for this campaign.")
                time.sleep(60)
    
    def parseUpdatedFile(self):
        '''override this method'''
        raise TypeError('Abstract method `parseUpdatedFile` called')
