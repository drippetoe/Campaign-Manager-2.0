from proximus.tools.TimeConstants import TimeConstants

class Logging:
    
    def __init__(self):
        # in milliseconds
        self.rotation = 3600000
        
    def getSleepTimeSeconds(self):
        return ( int(self.rotation)/TimeConstants.ONE_SECOND_MILLIS )
    
    def getCronExpression(self):
        # If rotation is less than 1 minute use 30 seconds
        if ( self.rotation < TimeConstants.ONE_MINUTE ):
            return "0,30 * * * * ?"
        
        # If rotation is greater than 48 hours
        # Just rotate every 2 days
        if ( self.rotation > TimeConstants.FORTYEIGHT_HOURS ):
            return  "* * * 1-31/2 * ?"
        
        ms = self.rotation
        sec = ms / 1000
        min = sec / 60
        hour = min / 60
        min = min % 60
        sec = sec % 60
        
        if (hour > 0):
            if (min > 30):
                hour += 1
                min = 0
            cron = "0 0 */" + hour + " * * ?";
        else:
            if (sec > 30):
                min += 1
                sec = 0
            cron = "0 0/" + min + " * * * ?";
        
        return cron