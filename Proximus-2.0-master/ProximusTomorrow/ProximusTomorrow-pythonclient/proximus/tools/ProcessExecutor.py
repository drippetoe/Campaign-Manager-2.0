from proximus.config.ClientURISettings import ClientURISettings

import string, types, logging, re, shlex, os
import subprocess
from proximus.tools.Platform import Platform
import inspect

logger = logging.root

class ProcessExecutor:
    #BASH = ["/bin/bash", "-c"]
    SET_UID_CMD = ClientURISettings.BIN_DIR + "/setUIDWrapper"
    
    macMatch = "^[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}$"
    macRE = re.compile(macMatch)
    
    ipMatch = "^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"
    ipRE = re.compile(ipMatch)
    
    MAC = None
    
    
    @staticmethod
    def reverseSSH(hostPort, devicePort):
        cmdList = [ ProcessExecutor.SET_UID_CMD, "reversessh", hostPort, devicePort]
        return string.join(ProcessExecutor.executeCommand(cmdList))
    
    @staticmethod
    def ledShell(action):
        cmdList = [ ProcessExecutor.SET_UID_CMD, "ledshell", action]
        return string.join(ProcessExecutor.executeCommand(cmdList))
        
    @staticmethod
    def pipedCommand(commandOne, commandTwo):
        lexCmd1 = shlex.split(commandOne)
        pipedCmd1 = subprocess.Popen(lexCmd1, stdout=subprocess.PIPE)
        
        lexCmd2 = shlex.split(commandTwo)
        pipedCmd2 = subprocess.Popen(lexCmd2, stdin=pipedCmd1.stdout, stdout=subprocess.PIPE)
        
        stdout_value, stderr_value = pipedCmd1.communicate()
        return stdout_value + stderr_value
    
    @staticmethod
    def executeCommand(cmd):
        if (type(cmd) == types.StringType):
            cmd = shlex.split(cmd)
        logger.debug(cmd)
        try:
            proc = None
            shell = False
            if(Platform.getPlatform() == Platform.PLATFORM_LINUX_DDWRT):
                shell = True
            else:
                shell = False
            
            proc = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=shell)
            (stdout_value, stderr_value) = proc.communicate()
            logger.debug("O:" + str(stdout_value))
            logger.debug("E:" + str(stderr_value))
            return stdout_value.strip().split("\n")
        except Exception as err:
            logger.error("%s - %s" % (cmd, str(err)))
            return None
    
    @staticmethod
    def formatMAC(macAddr):
        return macAddr.replace(":", "").upper()
    
    @staticmethod
    def getSerialNumber():
        if (Platform.getPlatform() == Platform.PLATFORM_MAC):
            result = ProcessExecutor.executeCommand("/usr/sbin/system_profiler -detailLevel basic")
            for line in result:
                if (line.find("Serial Number (system)") > -1):
                    serial = line.strip().split()[-1] # get the last item
                    return serial
        elif Platform.getPlatform() == Platform.PLATFORM_LINUX_BLUEGIGA:
            result = ProcessExecutor.executeCommand("/sbin/wrapid")
            for line in result:
                if (line.find("Hardware serial number") > -1):
                    serial = line.strip().split()[-1] # get the last item
                    return serial
        return "Unknown"
    
    @staticmethod
    def setCorrectBackhaulInterface():
        if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
            result = ProcessExecutor.executeCommand(ClientURISettings.BASH_DIR + os.sep + Platform.PLATFORM_LINUX_DREAMPLUG + os.sep + "autoInterface.sh backhaul")
            logger.debug(str(result))
        else:
            result = ProcessExecutor.executeCommand(ClientURISettings.BASH_DIR + os.sep + "autoInterface.sh backhaul")
            logger.debug(str(result))
    
    @staticmethod
    def executePortalPythonScript(argumentString):
        if (Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG):
            logger.info("Opening Portal: %s" % argumentString)
            return ProcessExecutor.executeCommand(ClientURISettings.BIN_DIR + os.sep + "portal.py " + argumentString)
        else:
            logger.error("Sorry, portal.py not working for this platform yet")

    @staticmethod
    def openFacebookPortal():
        argumentString = "open facebook " + ClientURISettings.ACCESS_POINT
        return ProcessExecutor.executePortalPythonScript(argumentString)

    @staticmethod
    def openCaptiveOrLimitedPortal():
        argumentString = "closed start " + ClientURISettings.ACCESS_POINT
        return ProcessExecutor.executePortalPythonScript(argumentString)

    @staticmethod
    def openHotspotPortal():
        argumentString = "open start " + ClientURISettings.ACCESS_POINT
        return ProcessExecutor.executePortalPythonScript(argumentString)

    @staticmethod
    def getKernelVersion():
        if (Platform.getPlatform() == Platform.PLATFORM_MAC):
            result = ProcessExecutor.executeCommand("/usr/bin/uname -r")
        else:
            result = ProcessExecutor.executeCommand("/bin/uname -r")
        for line in result:
            return line.strip()
    
    @staticmethod
    def getMACeth0():
        if ProcessExecutor.MAC != None:
            return ProcessExecutor.MAC
        
        if (Platform.getPlatform() == Platform.PLATFORM_LINUX_BLUEGIGA or \
             Platform.getPlatform() == Platform.PLATFORM_LINUX_DREAMPLUG or \
             Platform.getPlatform() == Platform.PLATFORM_LINUX_DDWRT):
            result = ProcessExecutor.executeCommand("/sbin/ifconfig eth0")
            
            for line in result:
                if (line.find("HWaddr") > -1):
                    for token in line.split():
                        if (ProcessExecutor.macRE.match(token)):
                            ProcessExecutor.MAC = ProcessExecutor.formatMAC(token)
                            return ProcessExecutor.MAC
        
        elif (Platform.getPlatform() == Platform.PLATFORM_MAC):
            result = ProcessExecutor.executeCommand("/sbin/ifconfig en0")

            for line in result:
                if (line.find("ether") > -1):
                    for token in line.split():
                        if ProcessExecutor.macRE.match(token):
                            ProcessExecutor.MAC = ProcessExecutor.formatMAC(token)
                            return ProcessExecutor.MAC
        else:
            logger.info("getMACeth0 not implemented for this platform yet, please do so")
        
        logger.fatal("Could not determine the MAC address for this device!!!")
        return None
    
    @staticmethod
    def getIp():
        if (ProcessExecutor.getPlatform() == "Linux"):
            possible_interfaces = [ "eth0", "nap", "ppp0" ]
            for interface in possible_interfaces:
                line = ProcessExecutor.pipedCommand("/sbin/ifconfig %s" % interface, "grep 'inet addr:'")
                if type(line) is list and len(line) > 0:
                    line = line[0]
                    # found line if any looks like: inet addr:192.168.1.101  Bcast:192.168.1.255  Mask:255.255.255.0
                    if not (line.find("inet addr:") > -1):
                        continue
                    for element in line.split():
                        if (element.find("addr:") > -1):
                            ip = element.split(":")[1]
                            if ProcessExecutor.ipRE.match(ip):
                                return ip
        elif (ProcessExecutor.getPlatform() == "MacOSX"):
            # if we get here, none were valid.  Try one other method (BSD systems)
            line = ProcessExecutor.pipedCommand("/sbin/ifconfig en0", "grep 'inet '")
            if type(line) is list and len(line) > 0:
                line = line[0]
                for element in line.split():
                    if ProcessExecutor.ipRE.match(element):
                        return element
        return None            
    
    @staticmethod
    def stopLighttpd():
        # Script that will restart Lighttpd 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "lighttpd", "stop" ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def startLighttpd():
        # Script that will restart Lighttpd 
        # if 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "lighttpd", "start" ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def restartLighttpd():
        # Script that will restart Lighttpd 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "lighttpd", "restart" ]
        return ProcessExecutor.executeCommand(cmdList)

    """
     * The portal has two modes open and closed. The open portal mode sets
     * up the device as a captive portal. The system can add or remove
     * devices based upon an IP address. The closed mode sets up the devices
     * as a closed system where all DNS is managed by the device.
    
    open start <IP>         -> configs and starts open portal with router <IP>
    closed start <IP>       -> configs and starts closed portal witho router <IP>
    """
    @staticmethod
    def startCaptivePortal(openOrClosed):
        # Script that will restart the captive portal
        cmdList = [ ProcessExecutor.SET_UID_CMD, "portal", openOrClosed, "start", ClientURISettings.ACCESS_POINT ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def addClosedPortalDomain(domain):
        cmdList = [ ProcessExecutor.SET_UID_CMD, "portal", "closed", "add", domain ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def removeClosedPortalDomain(domain):
        cmdList = [ ProcessExecutor.SET_UID_CMD, "portal", "closed", "remove", domain ]
        return ProcessExecutor.executeCommand(cmdList)

    """
     * Set read-write-execute permissions to user: www-data
     * For this folder
     * @return 
    """
    @staticmethod
    def giveLogPermission():
        cmdList = [ ProcessExecutor.SET_UID_CMD, "permit", "logs" ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def restartDNS():
        cmdList = [ ProcessExecutor.SET_UID_CMD, "dns", "restart" ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def startBluetooth():
        # Script that will startup bt radio 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "bluetooth", "start" ]
        return ProcessExecutor.executeCommand(cmdList)

    @staticmethod
    def stopBluetooth():
        # Script that will shutudown bt radio 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "bluetooth", "stop" ]
        return ProcessExecutor.executeCommand(cmdList)
    
    @staticmethod
    def restartBluetooth():
        # Script that will restart bt radio 
        cmdList = [ ProcessExecutor.SET_UID_CMD, "bluetooth", "restart" ]
        return ProcessExecutor.executeCommand(cmdList)
