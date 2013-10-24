import logging
from proximus.data.Action import Action
from proximus.interfaces.Observer import Observer
from proximus.handlers.UploadHandler import UploadHandler
from proximus.handlers.SoftwareUpdateHandler import SoftwareUpdateHandler
from proximus.handlers.ShellCommandHandler import ShellCommandHandler

logger = logging.root

# Is this an Observer, Observable or both?
class ActionHandler(Observer):
    def __init__(self, mainParent):
        self.mainParent = mainParent
        self.uploadHandler = UploadHandler()
        self.softwareUpdateHandler = SoftwareUpdateHandler()
        self.shellCommandHandler = ShellCommandHandler()
    
    def notify(self, obj):
        if isinstance(obj, Action):
            logger.info( "Action processed -- not really")
            
    def parseNode(self, actionsNode):
        self.parsedXml = actionsNode
        
        for actionNode in list(self.parsedXml):
            actionType = actionNode.tag # "upload" or "softwareupdate"
            
            action = Action(actionNode, actionType)
            
            if actionType == "upload":
                try:
                    self.uploadHandler.handleUpload(action)
                except Exception as err:
                    logger.error(err)
            elif actionType == "softwareupdate":
                try:
                    self.softwareUpdateHandler.handle(action)
                except Exception as err:
                    logger.error(err)
            elif actionType == "shellcommand":
                try:
                    self.shellCommandHandler.handle(action)
                except Exception as err:
                    logger.error(err)
            else:
                logger.error("Unknown action type: %s" % actionType)