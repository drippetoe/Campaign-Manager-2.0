from proximus.tools.XMLUtils import XMLUtils

import datetime, hashlib
from proximus.tools.TimeConstants import TimeConstants
from proximus.data.WifiCampaign import WifiCampaign
from proximus.data.BluetoothCampaign import BluetoothCampaign
from proximus.data.PubNubKey import PubNubKey
from proximus.config.ClientURISettings import ClientURISettings

class Campaign:
    
    """
    <campaigns>
    <campaign active="false" days_of_week="M,T,W,R,S,U" end_date="2012-04-10T00:00:00Z" end_time="23:59" id="1" 
        last_modified="2012-02-03T18:56:09Z" name="MGM" start_date="2012-01-01T00:00:00Z" start_time="00:00">
        <wifi_campaign checksum="abcdefaf2" hotspot_mode="1" network_name="MGM-wifi">
            <hotspot_domains />
        </wifi_campaign>
        <bluetooth_campaign bluetooth_mode="1" checksum="123456" friendly_name="MGM-bt-images" />
        <pubNubKeys>
            <pubNubKey name="abe" pubkey="bass" secret="laboriel" subkey="player" />
        </pubNubKeys>
    </campaign>
    </campaigns>
    """
    def __init__(self, parsedNode):
        
        self.id = XMLUtils.getAttributeSafe(parsedNode, "id")
        self.name = XMLUtils.getAttributeSafe(parsedNode, "name")
        
        h = hashlib.new('ripemd160')
        h.update(XMLUtils.elementToString(parsedNode))
        self.checksum = h.hexdigest()
        
        self.active = XMLUtils.getAttributeSafe(parsedNode, "active")
        
        # THIS SHOULD NOT EXIST AT THE CAMPAIGN LEVEL AND AT THE WIFI/BT LEVEL TOO
        self.days_of_week = TimeConstants.getDaysOfWeek( XMLUtils.getAttributeSafe(parsedNode, "days_of_week") )
        
        self.start_date = TimeConstants.dateParseUTC( XMLUtils.getAttributeSafe(parsedNode, "start_date") )
        self.end_date = TimeConstants.dateParseUTC( XMLUtils.getAttributeSafe(parsedNode, "end_date") )

        self.start_time = XMLUtils.getAttributeSafe(parsedNode, "start_time")
        self.end_time = XMLUtils.getAttributeSafe(parsedNode, "end_time")
        
        self.wifiNode = parsedNode.find("wifi_campaign")
        self.btNode = parsedNode.find("bluetooth_campaign")
        self.pubNubNode = parsedNode.find("pubNubKey")
        
        self.type = []
        self.wifiCampaign = None
        self.bluetoothCampaign = None
        self.pubNubKey = None
        if ( self.wifiNode != None ):
            self.type.append(ClientURISettings.MESSAGE_WIFI)
            self.wifiCampaign = WifiCampaign(self.wifiNode)
            
        if ( self.btNode != None ):
            self.type.append(ClientURISettings.MESSAGE_BLUETOOTH)
            self.bluetoothCampaign = BluetoothCampaign(self.btNode)
                      
        if ( self.pubNubNode != None ):
            self.pubNubKey = PubNubKey(self.pubNubNode)
        
        # this exists to allow you to dynamically get the content by type
        self.content = {}
        self.content[ClientURISettings.MESSAGE_BLUETOOTH] = self.bluetoothCampaign
        self.content[ClientURISettings.MESSAGE_WIFI] = self.wifiCampaign
        
        self.last_modified = TimeConstants.dateParseUTC( XMLUtils.getAttributeSafe(parsedNode, "last_modified") )
        
    def __eq__(self, other):
        if ( other == None ):
            return False
        return self.checksum == other.checksum
    
    def __repr__(self):
        return repr((self.id, self.name, self.type, self.last_modified, self.checksum))

    def isActive(self):
        if not self.active:
            return False
        
        now = datetime.datetime.utcnow()
        
        if now.weekday() not in self.days_of_week:
            return False
        
        if ( self.start_date > now ):
            return False
        if ( self.end_date < now ):
            return False
        
        startTime = float(self.start_time.replace(":", "."))
        endTime = float(self.end_time.replace(":", "."))
        nowTime = now.hour + ( now.minute/100.0 )
        
        if ( startTime < endTime ):
            # not on the boundary
            return ( startTime <= nowTime <= endTime )
        elif ( startTime == endTime ):
            return ( startTime >= nowTime >= endTime )
        else:
            # time not a deciding factor
            return True
        
        
        