from proximus.tools.XMLUtils import XMLUtils
from lib.pubnub.Pubnub import Pubnub
class PubNubKey:
    """
    <campaign active="true" days_of_week="M,T,W,R,F,S,U" end_date="2012-03-30T00:00:00Z" end_time="23:59" id="3" last_modified="2012-02-03T18:56:09Z" name="Star Wars" start_date="2012-01-01T00:00:00Z" start_time="00:00">
        <pubNubKeys>
            <pubNubKey channel="my_channel" pubkey="bass" secret="laboriel" subkey="player" />
            <pubNubKey channel="welcome" pubkey="drum" secret="laboriel" subkey="player" />
        </pubNubKeys>
    </campaign>

    """
    def __init__(self, parsedNode):
        self.channel = XMLUtils.getAttributeSafe(parsedNode, "channel")
        origin = XMLUtils.getAttributeSafe(parsedNode, "origin")
        publish_key = XMLUtils.getAttributeSafe(parsedNode, "pubkey")
        secret_key = XMLUtils.getAttributeSafe(parsedNode, "secret")
        subscribe_key = XMLUtils.getAttributeSafe(parsedNode, "subkey")
        ssl_on = XMLUtils.getAttributeSafe(parsedNode, "sslon")
        if(origin == None):
            origin = 'pubsub.pubnub.com'
        if(ssl_on == None):
            ssl_on = False 
        self.pubnub = Pubnub(publish_key, subscribe_key, secret_key, ssl_on, origin)

