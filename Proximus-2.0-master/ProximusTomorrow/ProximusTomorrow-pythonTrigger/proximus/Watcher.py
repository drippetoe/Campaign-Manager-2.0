'''
Created on Jul 6, 2012

@author: dshaw
'''
from threading import Thread
import os, time, logging
from pubnub.Pubnub import Pubnub
import datetime

logger = logging.root

class Watcher(Thread):
    
    ADDED="ADDED"
    REMOVED="REMOVED"
    UPDATED="UPDATED"
    
    DATE_FMT = "%Y-%m-%d.%H:%M:%S"
    
    '''
    classdocs
    '''
    filePath = None
    lastModifiedTime = 0
    
    entryDict = {}
    pubnub = None
    publish_key = 'pub-5fa3ce33-3bae-488d-ba50-7063adc4e7b7'
    subscribe_key = 'sub-2bb28138-c07c-11e1-807a-ab87a94d203e'
    secret_key = None
    ssl_on = False
    origin = 'pubsub.pubnub.com'
    channel = "proximus_test"

    def pubnubPublish(self, key, message):
        if ( self.pubnub == None):
            self.pubnub = Pubnub( self.publish_key, self.subscribe_key, self.secret_key, self.ssl_on, self.origin )

        eventDate = datetime.datetime.now().strftime(self.DATE_FMT)
        
        ## Publish Example
        info = self.pubnub.publish({
                               'channel' : self.channel,
                               'message' : { "eventdate": eventDate, key : message } })
        logger.info(info)
        
    def pubnubPublishDict(self, messageDict):
        if ( self.pubnub == None):
            self.pubnub = Pubnub( self.publish_key, self.subscribe_key, self.secret_key, self.ssl_on, self.origin )

        eventDate = datetime.datetime.now().strftime(self.DATE_FMT)
        messageDict["eventdate"] = eventDate
        
        info = self.pubnub.publish({
                               'channel' : self.channel,
                               'message' : messageDict })
        logger.info(info)

    def __init__(self):
        Thread.__init__(self)
    
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
            elif ( value != self.entryDict[key]):
                    changedMacs.append(value)
        
        self.entryDict = newEntryDict

        for removed in removedMacs:
            self.pubnubPublish(messageKeyPrefix + "_removed", removed)
        
        for added in addedMacs:
            self.pubnubPublish(messageKeyPrefix + "_added", added)
    
    def run(self):
        while ( True ):
            if ( os.path.exists(self.filePath)):
                currentModificationTime = os.stat(self.filePath).st_mtime
            
                if ( currentModificationTime > self.lastModifiedTime):
                    self.lastModifiedTime = currentModificationTime
                    self.parseUpdatedFile()
            else:
                logger.error("File %s does not exist" % self.filePath)
            
            time.sleep(10)
    
    def parseUpdatedFile(self):
        '''override this method'''
        raise TypeError('Abstract method `parseUpdatedFile` called')
