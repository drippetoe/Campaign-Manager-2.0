from proximus.data.Action import Action
import logging, os
from proximus.tools.RESTClient import RESTClient
from proximus.tools.XMLUtils import XMLUtils
from proximus.tools.ProcessExecutor import ProcessExecutor
from proximus.tools.SystemWriter import SystemWriter
from proximus.tools.TimeConstants import TimeConstants
from proximus.config.ClientURISettings import ClientURISettings

logger = logging.root

class ShellCommandHandler:
    
    def __init__(self):
        self.rest = RESTClient()
        self.actionNode = None
        
    def handle(self, someObject):
        if isinstance(someObject, Action) and someObject.actionType == "shellcommand":
            command = XMLUtils.getAttributeSafe(someObject.actionNode, "command")
            parameters = XMLUtils.getAttributeSafe(someObject.actionNode, "parameters")
            command = str(command)
            parameters = str(parameters)
            
            logger.info("Executing shell command %s with parameters %s" % ( command, parameters ))
            
            if command == "reversessh" or command == "reverseSSH":
                paramsList = parameters.split()
                try:                  
                    hostPort = (paramsList[0])
                    try:
                        devicePort = paramsList[1]
                    except Exception:
                        devicePort = "16222"
                    result = ProcessExecutor.reverseSSH(hostPort, devicePort)
                    logger.info(result)
                except ValueError as error:
                    logger.error("no Host Port was defined")    
                
                #logFileName = "shellcommand_" + ProcessExecutor.getMACeth0() + "_reversessh_" + TimeConstants.cronDateFormat() + ".txt"
                #filePath = ClientURISettings.LOG_QUEUE + os.sep + logFileName
                #SystemWriter.writeFile(filePath, result)
            elif command == "bash":
                splitCmd = parameters.split("|")
                if parameters.find("|") > -1 and len(splitCmd) > 1:
                    commandOne = splitCmd[0]
                    commandTwo = splitCmd[1]
                    result = ProcessExecutor.pipedCommand(commandOne, commandTwo)
                else:  
                    result = ProcessExecutor.executeCommand(parameters)
                logger.info(result)
                logFileName = "bash_" + ProcessExecutor.getMACeth0() + "_command_" + TimeConstants.cronDateFormat() + ".txt"
                filePath = ClientURISettings.LOG_QUEUE + os.sep + logFileName
                SystemWriter.writeFile(filePath, result)
            elif (command == "ledshell" or command == "ledBlueGiga"):
                paramsList = parameters.split()
                try:                  
                    action = (paramsList[0])
                except:
                    logger.error("no LED action was defined, defaulting to 'locate'")
                    action = "locate"
                result = ProcessExecutor.ledShell(action)
                logger.info(result)
                logFileName = "shellcommand_" + ProcessExecutor.getMACeth0() + "_ledshell_" + TimeConstants.cronDateFormat() + ".txt"
                filePath = ClientURISettings.LOG_QUEUE + os.sep + logFileName
                SystemWriter.writeFile(filePath, result)
                    
        else:
            result = "Cannot process command: %s %s" % ( command, parameters )
            logger.error( result )
            logFileName = "shellcommand_" + ProcessExecutor.getMACeth0() + "_unknowncommand_" + TimeConstants.cronDateFormat() + ".txt"
            filePath = ClientURISettings.LOG_QUEUE + os.sep + logFileName
            SystemWriter.writeFile(filePath, result)