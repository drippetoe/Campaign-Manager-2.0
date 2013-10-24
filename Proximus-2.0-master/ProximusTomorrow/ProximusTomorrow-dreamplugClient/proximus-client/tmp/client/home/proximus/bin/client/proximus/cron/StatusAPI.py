import logging, threading, datetime
from proximus.tools.TimeConstants import TimeConstants

logger = logging.root

class StatusAPI(threading.Thread):
    
    def __init__(self):
        threading.Thread.__init__(self)
    
    @staticmethod
    def isConnectNeeded():
        global DeviceClientInstance
        
        try:
            intervalBetweenConnects = DeviceClientInstance.clientConfig.connection.reconnect_interval
        except:
            logger.info("No connect interval available, defaulting to 60s")
            intervalBetweenConnects = 60000 # 60 seconds, if it can't read a value
        
        if TimeConstants.dateIntervalExceeded(DeviceClientInstance.getLastStatusApiTime(), intervalBetweenConnects):
            return True
        else:
            return False
    
    @staticmethod
    def run():
        try:
            global DeviceClientInstance
            
            if StatusAPI.isConnectNeeded():
                
                if ( not DeviceClientInstance.registrationHandler.isRegistered() ):
                    logger.debug("Cannot run status API, not registered")
                    return
                
                try:
                    DeviceClientInstance.statusHandler.checkStatus()
                except Exception as err:
                    logger.error(err)
                DeviceClientInstance.setLastStatusApiTime(datetime.datetime.utcnow())
            else:
                logger.debug("Status API connect not needed at this time")
        except Exception as err:
            logger.error(err)