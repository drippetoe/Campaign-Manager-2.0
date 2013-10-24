from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.XMLUtils import XMLUtils
class Action:
    """ these are things that get done when received"""
    def __init__(self, actionNode, actionType):
        self.actionNode = actionNode
        self.actionType = actionType
        self.count = XMLUtils.getAttributeSafe(self.actionNode, "count")
        self.shellCommand = XMLUtils.getAttributeSafe(self.actionNode, "shellCommand")
        
        if ( self.shellCommand != None ):
            ProcessExecutor.executeCommand(self.shellCommand)