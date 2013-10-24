#!/usr/bin/env python

import os, sys, threading, datetime

# This lets us import libraries in lib/ without a prefix
BASEDIR = os.path.realpath(os.curdir)
LIB_DIR = BASEDIR + os.sep + "lib"
sys.path.insert(0, LIB_DIR)

from proximus.cron.LogRotation import LogRotation
from proximus.data.config.ClientConfig import ClientConfig
from proximus.cron.ClientScheduler import ClientScheduler
from proximus.handlers.ActionHandler import ActionHandler
from proximus.handlers.CampaignHandler import CampaignHandler
from proximus.handlers.PubNubHandler import PubNubHandler
from proximus.handlers.RegistrationHandler import RegistrationHandler
from proximus.handlers.StatusHandler import StatusHandler
from proximus.tools.FileSystemChecker import FileSystemChecker
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.SystemWriter import SystemWriter
from proximus.tools.ProcessExecutor import ProcessExecutor

import logging, logging.config, time

logger = logging.root

global DeviceClientInstance

class DeviceClient(threading.Thread):

    def __init__(self):
        threading.Thread.__init__(self)
        self.initLogging()
        
        self.clientConfig = None
        self.clientScheduler = None
        self.campaignHandler = None
        self.actionHandler = None
        self.pubNubHandler = None
        
        self.lastStatusAPITime = datetime.datetime.fromordinal(1)
    
        self.initFileSystem()
        self.initDNSAndDHCP()


    def initFileSystem(self):
        # Filesystem setup
        dirs = []
        # Config Dir
        dirs.append(ClientURISettings.CONFIG_ROOT)
        dirs.append(ClientURISettings.DNSMASQ_CONFIG_FOLDER)
        dirs.append(ClientURISettings.BIN_DIR)
        dirs.append(ClientURISettings.CAPTIVEPORTAL_CONFIG_DIR)
      
        # Campaigns Dir
        dirs.append(ClientURISettings.CAMPAIGNS_ROOT_DIR)
        # Logs Dirs
        dirs.append(ClientURISettings.LOG_ROOT)
        dirs.append(ClientURISettings.LOG_COMPLETED)
        dirs.append(ClientURISettings.LOG_QUEUE)
        dirs.append(ClientURISettings.LOG_WORKING)
        
        FileSystemChecker.initDirs(dirs)
        # Setting up the default WEB CONTENT and config properties
        SystemWriter.makeDefaultIndexPage()
        SystemWriter.makeErrorsPage()
        ProcessExecutor.giveLogPermission()

    @staticmethod
    def initLogging():
        FileSystemChecker.initDir("logs")
        logging.config.fileConfig("conf" + os.sep + "logging.conf")
        logger.info("Logging initialized")

    def getLastStatusApiTime(self):
        return self.lastStatusAPITime
    
    def setLastStatusApiTime(self, lastStatusAPITime):
        self.lastStatusAPITime = lastStatusAPITime
        
    def initDNSAndDHCP(self):
        #if not bluegiga
        SystemWriter.makeDefaultDnsmasqConfFile(False)
        SystemWriter.makeDnsmasqConfFile()
        

    """ start event loop"""
    def run(self):
        # Setup
        self.clientConfig = ClientConfig(self)
        self.actionHandler = ActionHandler(self)
        self.campaignHandler = CampaignHandler(self)  # observer of clientconfig
        self.pubNubHandler = PubNubHandler(self)

        
        # Add observers
        self.clientConfig.addObserver(self.campaignHandler)
        self.clientConfig.addObserver(self.actionHandler)
        self.clientConfig.addObserver(self.pubNubHandler)
        
        # Create Registration Handler
        self.registrationHandler = RegistrationHandler(self)
             
        # Schedule jobs to run periodically and ensure things are working
        self.clientScheduler = ClientScheduler()
        
        # Ensure registered
        self.registrationHandler.register()

        """
        before anything happens at client startup, rotate the logs as needed
        """
        logRotate = LogRotation()
        logRotate.run() # calling run not start, we want this non-threaded

        self.statusHandler = StatusHandler(self)
        
        logger.info("Setup Complete")
        while ( True ):
            time.sleep(300) # sleep 5 mins, this doensn't do anything post registration


if __name__ == "__main__":

    ClientURISettings.DEV = True
    #ClientURISettings.DEV_SERVER = "127.0.0.1"
        
    # define a global value for getting to the DeviceClient instance
    __builtins__.DeviceClientInstance = DeviceClient()
    
    DeviceClientInstance.start()

