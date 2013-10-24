from proximus.tools.RESTClient import RESTClient
import logging
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.HttpStatus import HttpStatus
from proximus.tools.XMLParser import XMLParser
from proximus.tools.XMLUtils import XMLUtils


logger = logging.root

class StatusHandler:
    
    def __init__(self, mainParent):
        logger.info("Status Handler initialized")
        self.mainParent = mainParent
        self.rest = RESTClient()

    def checkStatus(self):
        try:
            clientConfig = self.mainParent.clientConfig

            logger.debug("Connecting to status API")
            
            response = self.rest.POSTRequest(uri=ClientURISettings.getStatusUri(), content=clientConfig.getConfigurationXml())
            self.handleStatusResponse(response, self.mainParent)
            
        except Exception as err: 
            logger.error("Error: " + str(err))
    
    def handleStatusResponse(self, response, deviceClient):
        
        if ( response == None ):
            # no connectivity
            logger.info("Internet access unavailable")
            return
        
        code = response.status_code
        content = response.content
        
        if ( code == HttpStatus.SC_OK ):
            logger.info("Status API completed successfully")
            
            xmlNode = XMLUtils.stringToElement(content)
            
            configNode = XMLParser.getClientConfigFromStatusResponse(xmlNode)
            actionsNode = XMLParser.getActionsFromStatusResponse(xmlNode)
            campaignsNode = XMLParser.getCampaignsFromStatusResponseNode(xmlNode)
            pubNubNode = XMLParser.getPubNubKeysFromStatusResponse(xmlNode)
            
            deviceClient.clientConfig.parseNode(configNode)
            deviceClient.actionHandler.parseNode(actionsNode)
            deviceClient.campaignHandler.parseNode(campaignsNode)
            deviceClient.pubNubHandler.parseNode(pubNubNode)

        elif ( code ==  HttpStatus.SC_NO_CONTENT ):
            # git Nothing hast changed
            logger.warn("No Content")
        elif ( code ==  HttpStatus.SC_NOT_MODIFIED ):
            #Nothing changed and NO STATUS on Config moving on
            logger.warn("Not Modified")
        elif ( code ==  HttpStatus.SC_NOT_FOUND ):
            logger.warn("404 Response from Server (Unable to find device in Database)")
        elif ( code ==  HttpStatus.SC_FORBIDDEN ):
            logger.warn("403 Response from Server (Forbidden - Access to this device License has expired) " + content)
        elif ( code ==  HttpStatus.SC_INTERNAL_SERVER_ERROR ):
            logger.error("Status: %d, content: %s" % (response.status_code, content))
        else:
            logger.warn("Non 200 Response from server unable to handle response: %d" % code)