from proximus.tools.XMLUtils import XMLUtils
class BluetoothCampaign:
    def __init__(self, parsedNode):
        self.parsedNode = parsedNode
        
        self.hotspotDomains = []
        self.parsedNode = parsedNode
        self.checksum = XMLUtils.getAttributeSafe(parsedNode, "checksum")
        self.bluetooth_mode = XMLUtils.getAttributeSafe(parsedNode, "bluetooth_mode")
        self.friendlyName = XMLUtils.getAttributeSafe(parsedNode, "friendly_name")