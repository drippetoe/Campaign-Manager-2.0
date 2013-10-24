from elementtree import SimpleXMLTreeBuilder

import xml.etree.ElementTree as xml
xml.XMLTreeBuilder = SimpleXMLTreeBuilder.TreeBuilder
import logging

logger = logging.root


class XMLParser:
    """
    The purpose of this class is to concentrate XML parsing into one place
    No classes aside from XMLParser and XMLUtils should need to import XML parsing classes
    """
    @staticmethod
    def getTokenFromRegisterResponse(xmlNode):
        if(xmlNode!=None):
            tokens = xmlNode.findall("token")
            if len(tokens) > 0:
                return tokens[0].text  # return the first token
        return None
    
    @staticmethod
    def getCampaignsFromStatusResponseNode(xmlNode):
        return xmlNode.find("campaigns")
    
    @staticmethod
    def getClientConfigFromStatusResponse(xmlNode):
        return xmlNode.find("config")
    
    @staticmethod
    def getActionsFromStatusResponse(xmlNode):
        return xmlNode.find("actions")
    
    @staticmethod
    def getPubNubKeysFromStatusResponse(xmlNode):
        return xmlNode.find("campaigns/campaign/pubNubKeys")
    
    @staticmethod
    def getPubNubKeysFromCampaignsConfig(xmlNode):
        return xmlNode.find("campaign/pubNubKeys")

