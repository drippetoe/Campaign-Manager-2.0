package com.proximus.data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * Static methods relating to date manipulation
 *
 * @author dshaw
 */
public class DateUtil {

    private static final String DATE_FMT = "MMM dd, yyyy";
    private static final String SHORT_DATE_FMT = "MM/dd/yy";
    private static final String MONTH_YEAR_FMT = "MMM-yyyy";
    private static final String VERY_SHORT_DATE_FMT = "MM/dd";
    private static final String TIMESTAMP_FMT = "dd-MMMM-yyyy HH:mm:ss";
    private static final String STRINGTODATE_FMT = "yyyyMMddHHmmss";
    private static final String IMPORT_FORMAT = "MM/dd/yyyy HH:mm";

    public static Date getEndOfDay(Date day) {
        if (day == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    /**
     *
     * @param day the current day
     * @return the current day with no hours, minutes, or seconds
     */
    public static Date getStartOfDay(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     *
     * @param day the current day
     * @return the next day with no hours, minutes, or seconds
     */
    public static Date getNextDay(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
        return cal.getTime();
    }

    public static Date getPreviousDay(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
        return cal.getTime();
    }

    public static Date getFirstDayOfMonth(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getLastDayOfMonth(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    public static Date getOneWeekAgo(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 7);
        return cal.getTime();
    }

    public static String formatTimestampForWeb(Date date) {
        if (date == null) {
            return new SimpleDateFormat(TIMESTAMP_FMT).format(new Date());
        }
        return new SimpleDateFormat(TIMESTAMP_FMT).format(date);
    }

    public static String formatDateForWeb(Date date) {
        return new SimpleDateFormat(DATE_FMT).format(date);
    }

    public static String formatShortDateForWeb(Date date) {
        return new SimpleDateFormat(SHORT_DATE_FMT).format(date);
    }

    public static String formatDateForChart(Date date) {
        return new SimpleDateFormat(VERY_SHORT_DATE_FMT).format(date);
    }

    public static String formatDateForReport(Date date) {
        return new SimpleDateFormat(MONTH_YEAR_FMT).format(date);
    }

    public static Date formatStringToDate(String dateString) {
        try {
            return new SimpleDateFormat(STRINGTODATE_FMT).parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }
     public static Date formatStringToDateForImport(String dateString) {
        try {
            return new SimpleDateFormat(IMPORT_FORMAT).parse(dateString);
        } catch (ParseException ex) {
            System.out.println("ex: " + ex);
            return null;
        }
    }

    /**
     * It is tempting to just call getTime() on the two dates and do a diff but
     * this doesn't allow for Daylight savings. This way is better
     *
     * @param startDate Cal start date
     * @param endDate Cal end date
     * @return number of days between 2 dates
     */
    public static long daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static void main(String[] args) {
        System.out.println(formatTimestampForWeb(null));
        System.out.println(formatTimestampForWeb(new Date(System.currentTimeMillis() - 100000)));
        System.out.println(formatDateForReport(new Date()));
    }
}