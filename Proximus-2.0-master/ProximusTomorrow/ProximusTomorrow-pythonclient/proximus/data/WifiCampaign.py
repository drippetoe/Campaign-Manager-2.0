from proximus.tools.XMLUtils import XMLUtils
from proximus.config.ClientURISettings import ClientURISettings

class WifiCampaign:
    """
    <campaign active="true" days_of_week="M,T,W,R,F,S,U" end_date="2012-03-30T00:00:00Z" end_time="23:59" id="3" last_modified="2012-02-03T18:56:09Z" name="Star Wars" start_date="2012-01-01T00:00:00Z" start_time="00:00">
        <wifi_campaign checksum="7777" hostspot_mode="2" network_name="Macy Open wifi">
            <hotspot_domains>
                <domain name="www.xkcd.com" />
                <domain name="www.espn.com" />
            </hotspot_domains>
        </wifi_campaign>
        <bluetooth_campaign bluetooth_mode="1" checksum="778899" friendly_name="DarthVader" />
    </campaign>

    """
    def __init__(self, parsedNode):
        self.hotspotDomains = []
        self.parsedNode = parsedNode
        self.checksum = XMLUtils.getAttributeSafe(parsedNode, "checksum")
        self.hotspotMode = XMLUtils.getAttributeSafe(parsedNode, "hotspot_mode")
        self.networkName = XMLUtils.getAttributeSafe(parsedNode, "network_name")

        if ( int(self.hotspotMode) == int(ClientURISettings.CAMPAIGN_LIMITED) ):
            allHotspotDomains = self.parsedNode.findall("*/domain")
            for hotspotNode in allHotspotDomains:
                domainName = XMLUtils.getAttributeSafe(hotspotNode, "name")
                self.hotspotDomains.append(domainName)