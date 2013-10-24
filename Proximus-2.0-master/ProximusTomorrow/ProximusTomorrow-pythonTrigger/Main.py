'''
Created on Jul 6, 2012

@author: dshaw
'''
import logging, logging.config
import os
from proximus.WifiWatcher import WifiWatcher
import time
from proximus.BluetoothWatcher import BluetoothWatcher

logger = logging.root

class Main:
    def __init__(self):
        Main.initLogging()
        
        self.wifiWatcher = WifiWatcher()
        self.wifiWatcher.start()
        
        self.btWatcher = BluetoothWatcher()
        self.btWatcher.start()
        
    
    @staticmethod
    def initDir(dir):
        if not os.path.exists(dir):
            try:
                os.makedirs(dir)
                logger.debug("Created directory: %s" % dir)
            except Exception as err:
                logger.error(err)
        else:
            logger.debug("Directory exists: %s" % dir)
            
    @staticmethod
    def initDirs(dirs=[]):
        for dir in dirs:
            Main.initDir(dir)
    
    @staticmethod
    def initLogging():
        Main.initDir("logs")
        logging.config.fileConfig("conf" + os.sep + "logging.conf")
        logger.info("Logging initialized")

if __name__ == '__main__':
    main= Main()
    while True:
        time.sleep(60)
    