'''
Created on Jun 26, 2012

@author: eric
'''
import bluez

import dbus
from bluez import AgentMethod


AGENT_PATH = "/com/proximus/agent"

class ProximusAgent(bluez.Agent):
    
    def __init__(self, obj_path):
        self.__obj_path = obj_path
        bluez.Agent.__init__(self, obj_path)
        
    def GetObjectPath(self):
        return self.__obj_path
   
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent",in_signature="", out_signature="")
    def Release(self):
        pass
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent",in_signature="os", out_signature="")
    def Authorize(self, device, uuid):
        pass
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent",in_signature="o", out_signature="s")
    def RequestPinCode(self, path):
        print "RequestPinCode"
        return "0000"
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent",in_signature="o", out_signature="u")
    def RequestPasskey(self, device):
        print("RequestPasskey (%s): %s" % (device, "0000"))
        return dbus.UInt32("0000")
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent", in_signature="ou", out_signature="")
    def DisplayPasskey(self, device, passkey):
        print("DisplayPasskey (%s, %d)" % (device, passkey))
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent", in_signature="ou", out_signature="")
    def RequestConfirmation(self, device, passkey):
        print("RequestConfirmation (%s, %d)" % (device, passkey))
        print("passkey matches")
        return
    
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent", in_signature="s", out_signature="")
    def ConfirmModeChange(self, mode):
        print("ConfirmModeChange (%s)" % (mode))
 
 
    @AgentMethod
    #@dbus.service.method("org.bluez.Agent", in_signature="", out_signature="")
    def Cancel(self):
        print("Cancel")
    
