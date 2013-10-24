
import logging, os

from proximus.interfaces.Observable import Observable
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.XMLUtils import XMLUtils
from proximus.data.clientconfig.Connection import Connection
from proximus.data.clientconfig.Authentication import Authentication
from proximus.data.clientconfig.Logging import Logging
from proximus.data.clientconfig.Software import Software
from proximus.tools.SystemWriter import SystemWriter
from proximus.config.files import clientconfig

logger = logging.root

class ClientConfig(Observable):
    
    def __init__(self, mainParent):
        Observable.__init__(self)
        
        self.mainParent = mainParent
        self.logging = None
        
        self.channel = 9
        
        self.parsedXml = None
        self.loadConfiguration()
    
    def loadConfiguration(self):
        logger.info("Loading configuration from:" + ClientURISettings.CONFIG_FILE)
        if not (os.path.exists(ClientURISettings.CONFIG_FILE) and os.path.isfile(ClientURISettings.CONFIG_FILE)):
            outf = open(ClientURISettings.CONFIG_FILE, "w")
            outf.write(self.getDefaultConfiguration())
            outf.close()
        
        try:
            xmlContent = SystemWriter.readFileToString(ClientURISettings.CONFIG_FILE)
            
            if ( xmlContent.strip() == "" ):
                logger.error("Client config was empty, putting device into limbo with default config")
                os.remove(ClientURISettings.CONFIG_FILE)
                outf = open(ClientURISettings.CONFIG_FILE, "w")
                outf.write(self.getDefaultConfiguration())
                outf.close()
                xmlContent = SystemWriter.readFileToString(ClientURISettings.CONFIG_FILE)
            
            configNode = XMLUtils.stringToElement(xmlContent)
            self.parseNode(configNode)
        except Exception as err:
            logger.error(err)
 
    def saveConfiguration(self):
        if ( self.parsedXml != None ):
            self.setMacAddr()
            
            stringCurrentXML = XMLUtils.elementToString(self.parsedXml)
            stringSavedXML = SystemWriter.readFileToString(ClientURISettings.CONFIG_FILE)
            if (( stringCurrentXML != stringSavedXML) and ( len(stringCurrentXML) != len(self.getDefaultConfiguration()))):
                XMLUtils.writeNodeToFile(ClientURISettings.CONFIG_FILE, self.parsedXml)
                logger.info("ClientConfig updated on file system")
            else:
                logger.debug("ClientConfig change not needed")
        else:
            logger.error("Cannot save ClientConfig: no data available")

    def setToken(self, value):
        if ( self.parsedXml == None):
            self.loadConfiguration()
        self.parsedXml.find("authentication").attrib['token'] = value
        logger.info("Updated token")
        
    def setLoggingRotation(self, value):
        if ( self.parsedXml == None):
            self.loadConfiguration()
        self.parsedXml.find("logging").attrib['rotation'] = value
        
    def setChannel(self, value):
        if ( self.parsedXml == None):
            self.loadConfiguration()
        try:
            self.parsedXml.attrib['channel'] = value
        except Exception as err:
            logger.info(err)
    
    def setMacAddr(self):
        if ( self.parsedXml == None):
            self.loadConfiguration()
        macAddr = ProcessExecutor.getMACeth0()
        self.parsedXml.attrib["macAddr"] = macAddr

    def getConfigurationXml(self):
        return XMLUtils.elementToString(self.parsedXml)
    
    """
    Takes a clientconfig reference and raw XML as arguments
    Updates the config by reference 
    """
    def parseNode(self, configNode):
        
        self.parsedXml = configNode
        
        self.channel = XMLUtils.getAttributeSafe(configNode, "channel", "9")
        self.setChannel(self.channel)
        
        children = list(configNode)
        for child in children:
            if child.tag == "connection":
                connection = Connection()
                connection.keep_alive = XMLUtils.getAttributeSafe(child, "keep-alive")
                connection.reconnect_interval = XMLUtils.getAttributeSafe(child, "reconnect-interval")
                
                self.connection = connection
                
            elif child.tag == "authentication":
                authentication = Authentication()
                authentication.setToken(XMLUtils.getAttributeSafe(child, "token"))
                
                self.authentication = authentication
                
            elif child.tag == "logging":
                logging = Logging()
                logging.rotation = XMLUtils.getAttributeSafe(child, "rotation", "3600000")
                self.setLoggingRotation(logging.rotation)
                
                self.logging = logging
                
            elif child.tag == "software":
                software = Software()
                software.license = XMLUtils.getAttributeSafe(child, "license")
                software.major = XMLUtils.getAttributeSafe(child, "major")
                software.minor = XMLUtils.getAttributeSafe(child, "minor")
                software.build = XMLUtils.getAttributeSafe(child, "build")
                software.kernel = XMLUtils.getAttributeSafe(child, "kernel")
                
                self.software = software
                
            elif child.tag == "configProperties":
                properties = list(child)
                for prop in properties:
                    key = prop.attrib["prop_key"]
                    value = prop.attrib["prop_value"]
                    self.configProperties[key] = value
        
        self.saveConfiguration()
        self.setChanged()
        self.notifyObservers()        

    
    def getDefaultConfiguration(self):
        return clientconfig.CONTENT
