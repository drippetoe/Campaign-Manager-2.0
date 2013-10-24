from proximus.tools.RESTClient import RESTClient
import logging
from proximus.tools.XMLUtils import XMLUtils
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.FileSystemChecker import FileSystemChecker
import os
import shutil

logger = logging.root

class SoftwareUpdateHandler:

    def __init__(self):
        self.rest = RESTClient()
        
    def handle(self, action):
        logging.info("Got software update action")
        path = XMLUtils.getAttributeSafe(action.actionNode, "path", None)
        if ( path != None ):
            # download the file
            restClient = RESTClient()
            
            url =  ClientURISettings.getSoftwareUpdateURI()
            if path.find("/") != 0:
                url += "/"
            url += path
            
            filename = os.path.basename(path)
            
            FileSystemChecker.initDir(ClientURISettings.SWUPDATE_ROOT_DIR )
            downloadPath = ClientURISettings.SWUPDATE_ROOT_DIR + os.sep + filename

            if ( os.path.exists(downloadPath )):
                success = True
            else:
                success = restClient.GETFile(url, downloadPath)
            
            if ( not success ):
                logger.error("Failed to download software update at URL '%s' to file '%s'" % (url, downloadPath))
            elif ( success and not os.path.exists(downloadPath)):
                success = False
                logger.error("Failure in saving downloaded wpk file to %s" % downloadPath)  
            else:
                # success, move to /tmp/obex to make it auto-install the WPK file
                logger.info("Installation of %s beginning" % downloadPath)
                fileName = os.path.basename(downloadPath)
                FileSystemChecker.initDir("/tmp/obex")
                destination = "/tmp/obex/%s" % fileName
                shutil.copy(downloadPath, destination)
                
                
                