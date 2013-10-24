import threading, os, logging, time, re
from zipfile import ZipFile

from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.RESTClient import RESTClient
from proximus.tools.FileSystemChecker import FileSystemChecker
import shutil

logger = logging.root

class GenericCampaignDownloader(threading.Thread):

    def __init__(self, campaignType, campaignList):
        threading.Thread.__init__(self)
        self.campaignList = campaignList
        self.campaignType = campaignType

    def validateInstalledCampaign(self, campaignFolder):
        possible_indexes = [ "index.html", "index.php", "index.htm" ]
        
        for index in possible_indexes:
            if os.path.exists(campaignFolder + os.sep + index):
                return True # all set
        
        # if we get here, there was no root index, we look for one in a subfolder
        for root, dirs, files in os.walk(campaignFolder):
            for index in possible_indexes:
                if index in files:
                    logger.info("Found an index in the folder %s , This campaign may not work as sent" % os.path.abspath(root))
                    return True
        return False

    """ Attempt to download and extract all Campaigns of type self.campaignType"""
    def run(self):
        for campaign in self.campaignList:
            success = False
            
            campaignFolder = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + campaign.id + os.sep + self.campaignType
            downloadFolder = ClientURISettings.CAMPAIGNS_ROOT_DIR + os.sep + campaign.id + os.sep + "download"
            FileSystemChecker.initDir(campaignFolder)
            FileSystemChecker.initDir(downloadFolder)

            # PATH contains the checksum so if the campaign changes, it will be re-downloaded
            if campaign.content.has_key(self.campaignType):
                
                if ( self.campaignType == ClientURISettings.MESSAGE_WIFI ):
                    checksum = campaign.wifiCampaign.checksum
                elif ( self.campaignType == ClientURISettings.MESSAGE_BLUETOOTH ):
                    checksum = campaign.bluetoothCampaign.checksum
                else:
                    checksum = "0"
                
                zipfileName = re.sub("[^0-9a-zA-Z_-]", "", "%s_%s_%s" % (checksum, self.campaignType, campaign.name)) + ".zip"
                zipFilePath = campaignFolder + os.sep + zipfileName
                zipFileDownloadPath = downloadFolder + os.sep + zipfileName
        
                if ( os.path.exists(zipFilePath)):
                    logger.info("Campaign %s (%s) already downloaded" % (str(campaign.id), self.campaignType))
                    continue
                
                else:
                    restClient = RESTClient()
                    while not success:
                        try:
                            url =  ClientURISettings.getDownloadUri() 
                            url += "/" + campaign.id 
                            url += "/" + self.campaignType
                            url += "/" + zipfileName
                            success = restClient.GETFile(url, zipFileDownloadPath)
                            
                            if ( not success ):
                                logger.error("Failure downloading file %s, sleeping 1 min" % url)
                                time.sleep(60)
                                continue
                                
                            elif ( success and not os.path.exists(zipFileDownloadPath)):
                                success = False
                                logger.error("Failure in saving downloaded zip file to %s" % zipFileDownloadPath)
                                if ( os.path.exists(zipFilePath)):
                                    logger.info("BUT zip file was downloaded at some point to %s" % zipFilePath)

                            if success:
                                try:
                                    campaign_zip = ZipFile(zipFileDownloadPath, 'r')
                                    fullCampaignPath = os.path.abspath(campaignFolder)
                                    # remove anything that was there
                                    shutil.rmtree(fullCampaignPath, ignore_errors=True)
                                    FileSystemChecker.initDir(fullCampaignPath)
                                    campaign_zip.extractall(fullCampaignPath)
                                    shutil.move(zipFileDownloadPath, zipFilePath)
                                    
                                    logger.info("Campaign ZIP %s extracted to %s" % ( zipfileName, campaignFolder))
                                except Exception as err:
                                    logger.error("Could not extract zip file %s" % (zipFilePath))
                                    logger.error(err)
                                    os.remove(zipFilePath)
                                    success = False
                                    time.sleep(60)
                                    
                        except Exception as err:
                            logger.error("Could not download campaign content: " + str(err))
                            time.sleep(60)
                    # Here indicates success
                    self.validateInstalledCampaign(campaignFolder)