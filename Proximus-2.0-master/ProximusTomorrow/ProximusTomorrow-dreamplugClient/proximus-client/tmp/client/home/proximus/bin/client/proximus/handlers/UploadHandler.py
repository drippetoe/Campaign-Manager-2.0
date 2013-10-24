from proximus.data.Action import Action
import logging, os
from proximus.config.ClientURISettings import ClientURISettings
from proximus.tools.RESTClient import RESTClient
from proximus.data.response.HttpStatus import HttpStatus
from proximus.tools.XMLUtils import XMLUtils

logger = logging.root

class UploadHandler:
    
    def __init__(self):
        self.rest = RESTClient()
        self.actionNode = None
    
    def uploadFile(self, fullPath):

        if ( os.path.exists(fullPath) and os.path.isfile(fullPath)):
            fileName = os.path.basename(fullPath)
            form = {"metadata": fileName}
            files = {'body': (fileName, open(fullPath, 'rb'))}

            r = self.rest.POSTFiles(ClientURISettings.getUploadUri(), data=form, files=files)
            if ( r.status_code == HttpStatus.SC_OK ):
                logger.info("%s uploaded" % fileName)
            else:
                logger.error("%s upload failed, %s: " % (fileName, r.content))
            return r
    
    def handleUpload(self, someObject):
        if isinstance(someObject, Action) and someObject.actionType == "upload":
            logCount = XMLUtils.getAttributeSafe(someObject.actionNode, "count")
            try:
                logCount = int(logCount)
            except Exception:
                logCount = 10
            
            sendAll = XMLUtils.getAttributeSafe(someObject.actionNode, "all")
            if ( sendAll == "True"):
                sendAll = True
            else:
                sendAll = False
            
            filesInUploadFolder = os.listdir(ClientURISettings.LOG_QUEUE)
            if ( len(filesInUploadFolder) == 0):
                return
            
            filesUploaded = 0
            
            for logFile in filesInUploadFolder:
                fullPath = ClientURISettings.LOG_QUEUE + os.sep + logFile
                response = self.uploadFile(fullPath)
                if ( response.status_code == HttpStatus.SC_OK):
                    #destinationPath = ClientURISettings.LOG_COMPLETED + os.sep + logFile
                    #shutil.move(fullPath, destinationPath)
                    os.remove(fullPath)
                    logger.info("Deleting uploaded log file %s" % fullPath)
                
                if not sendAll and ( filesUploaded >= logCount ):
                    return
        else:
            logger.error( "Got an incorrect notification" )
    