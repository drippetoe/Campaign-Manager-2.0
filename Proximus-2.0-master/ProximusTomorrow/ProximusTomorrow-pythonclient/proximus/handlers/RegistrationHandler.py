from proximus.tools.RESTClient import RESTClient
from proximus.tools.ProcessExecutor import ProcessExecutor

import logging, time
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.HttpStatus import HttpStatus
from proximus.tools.TimeConstants import TimeConstants
from proximus.tools.XMLParser import XMLParser
from proximus.tools.XMLUtils import XMLUtils

logger = logging.root

class RegistrationHandler:
    def __init__(self, mainParent):
        self.mac = ProcessExecutor.getMACeth0()
        self.serialNumber = ProcessExecutor.getSerialNumber()
        self.mainParent = mainParent
    
    def isRegistered(self):
        token = self.mainParent.clientConfig.authentication.getToken()
        if ( token != None and len(token) > 0):
            return True
        return False
    
    def register(self):
        
        rest = RESTClient()
        
        if ( not self.mac ):
            self.mac = ProcessExecutor.getMACeth0()
        if ( not self.serialNumber ):
            self.serialNumber = ProcessExecutor.getSerialNumber()
       
        while not self.isRegistered():
            try:
                logger.debug("Starting REGISTER Request, MAC %s, serial %s" % (self.mac, self.serialNumber))
                formparams = {}
                formparams["mac"] = self.mac
                formparams["serialNumber"] = self.serialNumber
                formparams["major"] = self.mainParent.clientConfig.software.major
                formparams["minor"] = self.mainParent.clientConfig.software.minor
                formparams["build"] = self.mainParent.clientConfig.software.build
                formparams["kernel"] = ProcessExecutor.getKernelVersion()
                formparams["platform"] = "BGAX4"
                
                r = rest.PUTRequest(uri=ClientURISettings.getRegistrationUri(), content=formparams)
                
                if ( r == None ):
                    logger.error("No response from server, check connectivity")
                elif ( r.status_code == HttpStatus.SC_FORBIDDEN ):
                    logger.error("Device %s already registered in database, please remedy" % self.mac)
                elif ( r.status_code == HttpStatus.SC_OK ):
                    xmlNode = XMLUtils.stringToElement(r.content)
                    token = XMLParser.getTokenFromRegisterResponse(xmlNode)
                    self.mainParent.clientConfig.setToken(token)
                    self.mainParent.clientConfig.authentication.setToken(token)
                    self.mainParent.clientConfig.saveConfiguration()
                    logger.info("Successful registration, token %s received" % token)
                    return
                else:
                    logger.error("Status: %d, content: %s" % (r.status_code, r.content))
            except Exception as err:
                logger.error(err)
            
            # If registration failed sleep 60 seconds before retrying
            logger.debug("Registration failed, sleeping %d s" % TimeConstants.ONE_MINUTE_SECONDS)
            time.sleep(TimeConstants.ONE_MINUTE_SECONDS)