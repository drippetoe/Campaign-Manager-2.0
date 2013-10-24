/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.util;

import com.proximus.data.DayParts;
import com.proximus.data.ZipcodeToTimezone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Gilberto Gaxiola
 */
public class TimeZoneUtil {

    /**
     *
     * @param currentDate
     * @param timeZoneId Example of TimeZones: America/New_York UTC EST
     * @return
     */
    public static Date getTimeZone(Date currentDate, String timeZoneId) {

        Calendar mbCal = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
        mbCal.setTimeInMillis(currentDate.getTime());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

        return cal.getTime();

    }

    public static void printTimeZones() {
        String[] availableIDs = TimeZone.getAvailableIDs();
        List<String> orderList = new ArrayList<String>();
        orderList.addAll(Arrays.asList(availableIDs));
        Collections.sort(orderList);

        for (String object : orderList) {
            System.out.println(object);

        }
    }

    /**
     * Takes a String representation of a dayPart within a localOffet and if it honorsDST or not
     * @param daypart
     * @param localeOffset
     * @param honorsDST
     * @return the UTC dayPart equivalent
     */
    public static String dayPartFromLocaleToUTC(String daypart, int localeOffset, boolean honorsDST) {
        if(honorsDST && isTodayInDayLightSavingsTime()) {
            localeOffset = localeOffset + 1;
        }
        int hour = Integer.valueOf(daypart.split(":")[0]);
        String minute = daypart.split(":")[1];

      
        int a = hour - localeOffset;
        int hourResult = (a % 24 + 24) % 24;

        return (hourResult < 10 ? "0" + hourResult : hourResult) + ":" + minute;
    }

    /**
     * January is Month 0 - December is Month 11
     * Sunday is Day 1 - Saturday is Day 7
     * Daylight Savings Time
     *
     * Runs from Second Sunday of March
     *
     * A ends on First Sunday of November
     *
     * @return
     */
    public static boolean isTodayInDayLightSavingsTime() {
        
        Calendar c = new GregorianCalendar();
        //Taking out the months not on DST
        int currMonth = c.get(GregorianCalendar.MONTH);
        int currWeek = c.get(GregorianCalendar.WEEK_OF_MONTH);

        //Months that are not part of DST
        if (currMonth < 2 || currMonth > 10) {
            return false;
        }
        //Months that are part of DST
        if (currMonth > 2 && currMonth < 10) {
            return true;
        }

        //OUTLIER CASES ON MARCH AND NOVEMBER
        //Now check if greater than Second Sunday of March
        if (currMonth == 2) {
            Calendar change = new GregorianCalendar();
            //Going to the first day of the month and check if sunday
            change.set(GregorianCalendar.DATE, 1);
            if (change.get(GregorianCalendar.DAY_OF_WEEK) == 1) {
                if (currWeek >= 2) {
                    return true;
                }
            }
            if (currWeek >= 3) {
                return true;
            }
        }

        //Check First Sunday of November
        if (currMonth == 10) {
            Calendar change = new GregorianCalendar();
            //Going to the first day of the month and check if sunday
            change.set(GregorianCalendar.DATE, 1);
            if (change.get(GregorianCalendar.DAY_OF_WEEK) == 1) {
                if (currWeek >= 1) {
                    return false;
                }
            }
            if (currWeek >= 2) {
                return false;
            }
            return true;
        }

        return false;

    }
    


//    public static void main(String[] args) throws ParseException {
//        ZipcodeToTimezone beverlyHills = new ZipcodeToTimezone("90210", "CA", -8, true);
//        ZipcodeToTimezone elPaso = new ZipcodeToTimezone("79912", "TX", -7, true);
//        ZipcodeToTimezone atlanta = new ZipcodeToTimezone("30080", "GA", -5, true);
//        ZipcodeToTimezone hawaii = new ZipcodeToTimezone("41423", "HI", -10, false);
//        ZipcodeToTimezone london = new ZipcodeToTimezone("11111", "LONDON", 0, false);
//        ZipcodeToTimezone copenhagen = new ZipcodeToTimezone("11111", "SE", 2, false);
//
//        DayParts d = new DayParts();
//
//        List<String> selectedDays = new ArrayList<String>();
//        selectedDays.add("M");
//        selectedDays.add("W");
//        selectedDays.add("F");
//        selectedDays.add("R");
//        d.setSelectedDaysOfWeek(selectedDays);
//
//        d.setAmStart(true);
//        d.setAmEnd(false);
//        d.setHourStart(8);
//        d.setHourEnd(2);
//
//        //DayParts d is in UTC form we must convert it to the timezone used:
//
//        //I.e 8:00 UTC changes to whatever BeverlyHills offset is
//
//        System.out.println("Atlanta Timezone offset is: " + atlanta.getTimezone() + " does it use DST? " + atlanta.isUsesDaylightSavings());
//        System.out.println("Are we currently on DST? " + TimeZoneUtil.isTodayInDayLightSavingsTime());
//        DateFormat format = new SimpleDateFormat("HH:mm");
//        Date dd = format.parse(d.getStartTime());
//
//        System.out.println("The DayPart Start Time is: " + d.getStartTime());
//        System.out.println("The DayPart End Time is: " + d.getEndTime());
//        String resultStart = TimeZoneUtil.dayPartFromUTCToLocale(d.getStartTime(), atlanta.getTimezone(), atlanta.isUsesDaylightSavings());
//        String resultEnd = TimeZoneUtil.dayPartFromUTCToLocale(d.getEndTime(), atlanta.getTimezone(), atlanta.isUsesDaylightSavings());
//        System.out.println("resultStart: " + resultStart);
//        System.out.println("resultEnd: " + resultEnd);
//
//    }
}
