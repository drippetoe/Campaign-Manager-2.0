from proximus.config.ClientURISettings import ClientURISettings
from proximus.data.PubNubKey import PubNubKey
from proximus.interfaces.Observer import Observer

from proximus.tools.XMLUtils import XMLUtils
from proximus.tools.WifiWatcher import WifiWatcher
from proximus.tools.BluetoothWatcher import BluetoothWatcher
import logging
import os
from proximus.tools.XMLParser import XMLParser


logger = logging.root

class PubNubHandler(Observer):
    """
    This class exists to check if the campaign contains pubnub keys
    It should look at the data and make changes to the filesystem as needed
    """
    def __init__(self, mainParent):
        self.mainParent = mainParent
        self.parsedXml = None      
        self.pubNubKey = None       
        self.wasEmpty = False 
        self.loadKeys()
        
        self.wifiWatcher = WifiWatcher(self)
        self.wifiWatcher.start()
        self.btWatcher = BluetoothWatcher(self)
        self.btWatcher.start()
    
    def loadKeys(self):
        
        logger.info("Loading configuration from:" + ClientURISettings.CAMPAIGNS_FILE)
        if not (os.path.exists(ClientURISettings.CAMPAIGNS_FILE) and os.path.isfile(ClientURISettings.CAMPAIGNS_FILE)):
            return
        try:
            xmlContent = XMLUtils.readNodeFromFile(ClientURISettings.CAMPAIGNS_FILE)
            self.parsedXml = XMLParser.getPubNubKeysFromCampaignsConfig(xmlContent)
            self.parseNode(self.parsedXml)
        except Exception as err:
            logger.error(err)
    def notify(self, obj):
        if isinstance(obj, PubNubKey):
            logger.debug("PubNubKey processed -- not really")
    
    def getPubNubKey(self):
        return self.pubNubKey
        
    def parseNode(self, pubNubKeysNode):
                
        self.parsedXml = pubNubKeysNode
        if(pubNubKeysNode != None):
            for parsedNode in pubNubKeysNode.findall('pubNubKey'):
                self.pubNubKey = PubNubKey(parsedNode)
        else:
            logger.debug("No PubNubKey - Empty/No XML (check status API)")                             
