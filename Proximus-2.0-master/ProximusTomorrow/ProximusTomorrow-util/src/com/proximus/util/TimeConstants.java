/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Gaxiola
 */
public class TimeConstants
{
    public static final int ONE_SECOND_MILLIS = 1000;
    public static final int THIRTY_SECONDS = 30 * ONE_SECOND_MILLIS;
    public static final int ONE_MINUTE = 60 * ONE_SECOND_MILLIS;
    public static final int FIVE_MINUTES = 5 * ONE_MINUTE;
    public static final int TEN_MINUTES = 10 * ONE_MINUTE;
    public static final int FIFTEEN_MINUTES = 15 * ONE_MINUTE;
    public static final int TWENTY_MINUTES = 20 * ONE_MINUTE;
    public static final int THIRTY_MINUTES = 30 * ONE_MINUTE;
    public static final int FORTY_FIVE_MINUTES = 45 * ONE_MINUTE;
    public static final int HOUR = 2 * THIRTY_MINUTES;
    public static final int HALF_HOUR = THIRTY_MINUTES;
    private static final Pattern VALID_TIME = Pattern.compile("([01][0-9]|2[0-3]):[0-5][0-9]");

    public static int MINUTE(int minute)
    {
        if (minute < 0) {
            return 0;
        }
        return minute * ONE_MINUTE;
    }

    public static int HOUR(int hour)
    {
        if (hour < 0) {
            return 0;
        }
        return hour * HOUR;
    }

    public static int SECOND(int second)
    {
        if (second < 0) {
            return 0;
        }
        return second * ONE_SECOND_MILLIS;
    }
    
    public static String formatForLogFileName(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(date);
    }

    public static String format(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }
    
    public static String formatDate(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(date);
    }

    public static List<Integer> getDaysOfWeek(String daysOfWeek)
    {
        List<Integer> result = new ArrayList<Integer>();
        if (daysOfWeek.contains("U")) {
            result.add(1);
        }
        if (daysOfWeek.contains("M")) {
            result.add(2);
        }
        if (daysOfWeek.contains("T")) {
            result.add(3);
        }
        if (daysOfWeek.contains("W")) {
            result.add(4);
        }
        if (daysOfWeek.contains("R")) {
            result.add(5);
        }
        if (daysOfWeek.contains("F")) {
            result.add(6);
        }
        if (daysOfWeek.contains("S")) {
            result.add(7);
        }
        return result;
    }

    
    /**
     * return true is a time (HH:mm) is in range from range (HH:mm-HH:mm)
     * @param time
     * @param range
     * @return 
     */
    public static boolean inTimeRange(String time, String range)
    {
        if (VALID_TIME.matcher(time).matches()
                && VALID_TIME.matcher(range.substring(0, 5)).matches() && VALID_TIME.matcher(range.substring(6)).matches()) {
            if (range.substring(0, 1).compareTo(range.substring(6, 7)) > 0) {
                //crossing boundary
                if (time.compareTo(range.substring(0, 5)) > 0) {
                    //if time is greater than start time
                    //then must be also greater than end time
                    return time.compareTo(range.substring(6)) > 0;
                }
                //if time is less than start time it must also be less than end time
                return time.compareTo(range.substring(0, 5)) <= 0 && time.compareTo(range.substring(6)) <= 0;

            }
            //normal range in between
            return time.compareTo(range.substring(0, 5)) >= 0 && time.compareTo(range.substring(6)) <= 0;
        }
        return false;
    }
    
    /**
     * 
     * @param d date to consider, with time
     * @param startTime time as a 24H string, e.g. "04:23" -- 4:23 am
     * @param endTime time as a 24H string, e.h. "23:59" -- 11:59 pm
     * @param daysOfWeekStr String in internal format for days of week @SEE DayParts.java
     * @return true if the time component of the date is within the times specified
     */
    public static boolean isWithinTimeAndRange(Date d, String startTime, String endTime, String daysOfWeekStr)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int currDay = cal.get(Calendar.DAY_OF_WEEK);
        int currHour = cal.get(Calendar.HOUR_OF_DAY);
        int currMin = cal.get(Calendar.MINUTE);
        // check day of week first
        List<Integer> daysOfWeek = getDaysOfWeek(daysOfWeekStr);
        if ( ! daysOfWeek.contains(currDay))
        {
            return false;
        }
        
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int startMin = Integer.parseInt(startTime.split(":")[1]);
        
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int endMin = Integer.parseInt(endTime.split(":")[1]);
        
        if ( startHour <= endHour ) // daytime boundary
        {
            // if hour not within the allowed hours
            if ( ( startHour > currHour ) || ( currHour > endHour) )
            {
                return false;
            }
            // if min not within allowed min
            else if ( ! ( ( startMin <= currMin ) && ( currMin <= endMin) ) )
            {
                return false;
            }
            
            // daypart is active
            return true;
        }
        if ( startHour > endHour ) // overnight boundary
        {
            
            if (currHour < startHour && currHour > endHour) {
                return false;
            }

            // if min not within allowed min
            if ( ! ( ( startMin <= currMin ) && ( currMin <= endMin) ) )
            {
                return false;
            }
            
            // daypart is active
            return true;
        }
        return false;
    }
    
        /**
     * check if a date is within range
     * @param testDate
     * @param startDate
     * @param endDate
     * @return 
     */
    public static boolean dateWithinRange(Date testDate, Date startDate, Date endDate)
    {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }
    
}
