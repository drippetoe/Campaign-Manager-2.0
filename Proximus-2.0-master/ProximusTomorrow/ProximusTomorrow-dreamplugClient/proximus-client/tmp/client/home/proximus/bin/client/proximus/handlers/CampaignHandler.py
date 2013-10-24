from proximus.config.ClientURISettings import ClientURISettings
from proximus.data.Campaign import Campaign
from proximus.handlers.GenericCampaignDownloader import \
    GenericCampaignDownloader
from proximus.interfaces.Observer import Observer
from proximus.tools.SystemWriter import SystemWriter
from proximus.tools.XMLUtils import XMLUtils
import logging
import os
import shutil
from proximus.tools.Platform import Platform
from proximus.tools.ProcessExecutor import ProcessExecutor


logger = logging.root

class CampaignHandler(Observer):
    """
    This class exists to check and install (as needed) the appropriate
    campaigns for Wi-F and Bluetooth
    
    It should look at the data and make changes to the filesystem as needed
    """
    def __init__(self, mainParent):
        self.mainParent = mainParent
        self.parsedXml = None
        
        self.campaigns = {}
        
        self.wasEmpty = False
        
        self.bluetoothDeployer = None
        self.wifiDeployer = None
        
        self.loadCampaigns()
        
        self.setHotSpotMode()
        
    def setHotSpotMode(self):
        correctActiveCampaign = self.getActiveCampaign(ClientURISettings.MESSAGE_WIFI)
        if (correctActiveCampaign == None):
            logger.info("No campaign is active (hotspot)")
            ProcessExecutor.openCaptiveOrLimitedPortal()
            return
        
        logger.info("Loading initial campaign hotspot mode as " + correctActiveCampaign.wifiCampaign.hotspotMode)
        #CAMPAIGN_NO_INTERNET = 1
        #CAMPAIGN_LIMITED = 2
        #CAMPAIGN_HOTSPOT = 3
        #CAMPAIGN_MOBILOZOPHY = 4
        #CAMPAIGN_FACEBOOK = 5
        if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
            if (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_HOTSPOT)):
                ProcessExecutor.openHotspotPortal()
            elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_NO_INTERNET)):
                    ProcessExecutor.openCaptiveOrLimitedPortal()
            elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_LIMITED)):
                ProcessExecutor.openCaptiveOrLimitedPortal()
            elif (int(correctActiveCampaign.wifiCampaign.hotspotMode) == int(ClientURISettings.CAMPAIGN_FACEBOOK)):
                ProcessExecutor.openFacebookPortal()
            else:
                logger.error("Not sure what to do with wifi campaign of type " + correctActiveCampaign.wifiCampaign.hotspotMode)
        else:
            logger.debug("Alternate campaign modes not set up for this platform")
    
    """ called at startup only to load from filesystem"""
    def loadCampaigns(self):
        
        logger.info("Loading configuration from:" + ClientURISettings.CAMPAIGNS_FILE)
        if not (os.path.exists(ClientURISettings.CAMPAIGNS_FILE) and os.path.isfile(ClientURISettings.CAMPAIGNS_FILE)):
            return
        try:
            self.parsedXml = XMLUtils.readNodeFromFile(ClientURISettings.CAMPAIGNS_FILE)
            self.parseNode(self.parsedXml)
        except Exception as err:
            logger.error(err)
    
    def saveCampaigns(self):
        if (self.parsedXml != None):
            stringCurrentXML = XMLUtils.elementToString(self.parsedXml)
            stringSavedXML = SystemWriter.readFileToString(ClientURISettings.CAMPAIGNS_FILE)
            if ((stringCurrentXML != stringSavedXML) and (len(stringCurrentXML) > 0)):
                XMLUtils.writeNodeToFile(ClientURISettings.CAMPAIGNS_FILE, self.parsedXml)
                logger.info("Campaigns updated on file system")
            else:
                logger.info("Campaigns change not needed")
        else:
            logger.error("Cannot save Campaigns: no data available")
    
    def notify(self, obj):
        if isinstance(obj, Campaign):
            logger.debug("Campaign processed -- not really")
    
    """Return the most recent campaign that is active right this minute"""
    def getActiveCampaign(self, type):
        activeCampaigns = []
        for campaign in self.campaigns.values():
            if type in campaign.type:
                if campaign.isActive():
                    activeCampaigns.append(campaign)
        
        if (len(activeCampaigns) > 0):
            activeCampaignsSorted = sorted(activeCampaigns, key=lambda campaign: campaign.last_modified)
            return activeCampaignsSorted[len(activeCampaigns) - 1]

    def getAllCampaignsByType(self, campaignType):
        typedCampaigns = []
        
        for campaign in self.campaigns.values():
            if campaignType == ClientURISettings.MESSAGE_WIFI and campaign.wifiCampaign != None:
                typedCampaigns.append(campaign)
            elif campaignType == ClientURISettings.MESSAGE_BLUETOOTH and campaign.bluetoothCampaign != None:
                typedCampaigns.append(campaign)
        
        return typedCampaigns

    def parseNode(self, campaignsNode):
        
        campaigns_changed = False
        
        self.parsedXml = campaignsNode
        
        new_campaigns = {}
        for parsedNode in campaignsNode.findall("campaign"):
            campaign = Campaign(parsedNode)
            new_campaigns[campaign.id] = campaign
        
        # first, if any campaigns are in the old list and not in the new
        # remove them
        for campaign_id in self.campaigns.keys():
            if not new_campaigns.has_key(campaign_id):
                logger.info("Removing campaign %s" % campaign_id)
                del self.campaigns[campaign_id]
                
                campaignFolder = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + campaign_id
                if os.path.isdir(campaignFolder):
                    shutil.rmtree(campaignFolder)
                
                campaigns_changed = True
                logger.info("Campaign removed: %s" % campaign_id)
        
        # check each to see if it is deployed already or not
        # if not, deploy it
        for (campaign_id, campaign) in new_campaigns.items():
            if not self.campaigns.has_key(campaign_id) or not (self.campaigns[campaign_id] == new_campaigns[campaign_id]):
                self.campaigns[campaign_id] = campaign
                campaigns_changed = True
                logger.info("Adding campaign %s" % campaign_id)
        
        if (campaigns_changed):
            self.loadAllCampaigns()
            self.saveCampaigns()
            
    def loadAllCampaigns(self):
        wiFiCampaigns = self.getAllCampaignsByType(ClientURISettings.MESSAGE_WIFI)
        bTCampaigns = self.getAllCampaignsByType(ClientURISettings.MESSAGE_BLUETOOTH)
        
        if (len(wiFiCampaigns) > 0):
            self.wifiDeployer = GenericCampaignDownloader(ClientURISettings.MESSAGE_WIFI, wiFiCampaigns)
            self.wifiDeployer.start()
        else:
            logger.info("No active WI-FI campaign")
        
        if (len(bTCampaigns) > 0):
            self.bluetoothDeployer = GenericCampaignDownloader(ClientURISettings.MESSAGE_BLUETOOTH, bTCampaigns)
            self.bluetoothDeployer.start()
        else:
            logger.info("No active Bluetooth campaign")
