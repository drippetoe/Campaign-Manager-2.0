import logging, re, datetime

class TimeConstants:
    ONE_SECOND_MILLIS = 1000
    THIRTY_SECONDS = 30 * ONE_SECOND_MILLIS
    ONE_MINUTE = 60 * ONE_SECOND_MILLIS
    FIVE_MINUTES = 5 * ONE_MINUTE
    TEN_MINUTES = 10 * ONE_MINUTE
    FIFTEEN_MINUTES = 15 * ONE_MINUTE
    TWENTY_MINUTES = 20 * ONE_MINUTE
    THIRTY_MINUTES = 30 * ONE_MINUTE
    FORTY_FIVE_MINUTES = 45 * ONE_MINUTE
    ONE_HOUR = 60 * ONE_MINUTE
    HALF_HOUR = THIRTY_MINUTES
    FORTYEIGHT_HOURS = ONE_HOUR * 48
    
    ONE_MINUTE_SECONDS = 60
    
    VALID_TIME = re.compile("([01][0-9]|2[0-3]):[0-5][0-9]")

    @staticmethod
    def minutesAsMilliseconds(minutes):
        if (minutes < 0):
            return 0
        return minutes * TimeConstants.ONE_MINUTE
        
    @staticmethod
    def hoursAsMilliseconds(hours):
        if (hours < 0):
            return 0
        return hours * TimeConstants.ONE_HOUR

    @staticmethod
    def secondsAsMilliseconds(seconds):
        if (seconds < 0):
            return 0
        return seconds * TimeConstants.ONE_SECOND_MILLIS
    
    @staticmethod
    def cronDateFormat(date=datetime.datetime.utcnow()):
        return date.strftime("%Y-%m-%d.%H-%M-%S")
        
    @staticmethod
    def dateFormat(date=datetime.datetime.utcnow()):
        return date.strftime("%b/%d/%Y %H:%M:%S %z")
    
    @staticmethod
    def dateParseUTC(date_string):
        if ( date_string != None ):
            # if the XML has a timezone, drop it, we live in GMT
            if len(date_string) > 19:
                date_string = date_string[:date_string.find("-", date_string.find("T"))]
            return datetime.datetime.strptime(date_string, "%Y-%m-%dT%H:%M:%S")
    
    @staticmethod
    def dateParseCronDate(date_string):
        return datetime.datetime.strptime(date_string, "%Y-%m-%d.%H-%M-%S")

    @staticmethod
    #datetime.weekday() return the day of the week as n integer where Monday is 0 and Sunday is 6
    def getDaysOfWeek(daysOfWeek):
        result = []
        if (daysOfWeek.find("U") > -1):
            result.append(6)

        if (daysOfWeek.find("M") > -1):
            result.append(0)

        if (daysOfWeek.find("T") > -1):
            result.append(1)

        if (daysOfWeek.find("W") > -1):
            result.append(2)

        if (daysOfWeek.find("R") > -1):
            result.append(3)

        if (daysOfWeek.find("F") > -1):
            result.append(4)

        if (daysOfWeek.find("S") > -1):
            result.append(5)

        return result
    
    """ takes a day like 'U' for Sunday and returns the numeric value from datetime """
    @staticmethod
    def getDayAsOrdinal(day):
        days = [ "M", "T", "W", "R", "F", "S", "U" ]
        return days.index( day )
    
    """
     * return true is a time (HH:mm) is in range from range (HH:mm-HH:mm)
     * @param time
     * @param range
     * @return 
     """
    @staticmethod
    def inTimeRange(time, range):
        if (TimeConstants.VALID_TIME.match(time) != None):
            rangeStart = range[:5]
            rangeStartHours, rangeStartMins = rangeStart.split(":")
            rangeStartHours = int(rangeStartHours)
            rangeStartMins = int(rangeStartMins)
            
            rangeEnd = range[6:]
            rangeEndHours, rangeEndMins = rangeEnd.split(":")
            rangeEndMins = int(rangeEndMins)
            
            timef = float(time.replace(":", "."))
            timestart = float(rangeStart.replace(":", "."))
            timeend = float(rangeEnd.replace(":", "."))
            
            if ( rangeStartHours > rangeEndHours ):
                # crosses boundary to the next day
                return timestart >= timef >= timeend
            else:
                return timestart <= timef <= timeend

    @staticmethod
    def dateIntervalExceeded(testDate, intervalMS):
        now = datetime.datetime.utcnow()
        dateDiffDelta = now - testDate
        minTimeDelta = datetime.timedelta(milliseconds=long(intervalMS))
        
        # time that has passed is >= the time interval
        if ( dateDiffDelta >= minTimeDelta ):
            return True
        else:
            return False
        

    """
     * check if a date is within range (dates are datetime.datetime)
     * @param testDate
     * @param startDate
     * @param endDate
     * @return 
    """
    @staticmethod
    def dateWithinRange(testDate, startDate, endDate):
        return startDate <= testDate <= endDate