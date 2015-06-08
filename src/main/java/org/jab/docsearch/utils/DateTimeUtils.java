/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jab.docsearch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;

/**
 * This class contains useful method for handling dates
 *
 * @version $Id: DateTimeUtils.java 159 2010-12-29 22:03:29Z henschel $
 */
public class DateTimeUtils {
    /**
     * Log4J
     */
    private final static Logger logger = Logger.getLogger(DateTimeUtils.class.getName());

    /**
     * Gets java.util.Date from date string in format M/d/yyyy
     *
     * @param dateString  Date string in format M/d/yyyy
     * @return            Date or null
     */
    public static Date getDateFromString(String dateString) {
        // DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        // (12.13.52 or 3:30pm ????)
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); (better,
        // but more changes in code)
        SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");

        Date rd = null;
        try {
            rd = df.parse(dateString);
        }
        catch (Exception eR) {
            logger.error("getDateFromString() failed during parsing date=" + dateString);
        }

        // Date rd = new Date(dateStr);
        // System.out.println("date:"+rd.toString());
        return rd;
    }


    /**
     * Gets days old
     *
     * @param date
     * @return
     */
    public static int getDaysOld(String date) {
        int returnInt = 0;

        if (! date.equals("")) {
            long indexTime;
            long nowTime;
            Date today = getDateFromString(getToday());
            Date then = getDateFromString(date);
            nowTime = today.getTime();
            indexTime = then.getTime();

            // perform math to compute the actual number of days
            if (nowTime > indexTime) {
                indexTime = indexTime / (1000 * 60 * 60 * 24);
                nowTime = nowTime / (1000 * 60 * 60 * 24);
                returnInt = (int) (nowTime - indexTime);
            }
        }

        return returnInt;
    }


    /**
     * Gets the actual date
     *
     * @return   Date string M/d/yyyy
     */
    public static String getToday() {
        Calendar cal = Calendar.getInstance();

        int mon = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return mon + "/" + day + "/" + year;
    }


    /**
     * Gets the actual time
     *
     * @return   Time string h:m:s
     */
    public static String getTime() {
        Calendar nowD = Calendar.getInstance();

        int hour = nowD.get(Calendar.HOUR);
        int min = nowD.get(Calendar.MINUTE);
        int sec = nowD.get(Calendar.SECOND);

        return hour + ":" + min + ":" + sec;
    }


    /**
     * Gets last year
     *
     * @return
     */
    public static String getLastYear() {
        Calendar nowD = Calendar.getInstance();

        int mon = nowD.get(Calendar.MONTH) + 1;
        int year = nowD.get(Calendar.YEAR);
        int day = nowD.get(Calendar.DAY_OF_MONTH);

        int lastYear = year - 1;

        return mon + "/" + day + "/" + lastYear;
    }


    /**
     * Gets this month
     *
     * @return  String y_m
     */
    public static String getThisMonth() {
        Calendar nowD = Calendar.getInstance();

        int mon = nowD.get(Calendar.MONTH) + 1;
        int year = nowD.get(Calendar.YEAR);

        return year + "_" + mon;
    }


    /**
     * Gets date string in format M/d/yyyy
     *
     * @param millis  millisecond
     * @return        Date string in format M/d/yyyy
     */
    public static String getDateString(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        int mon = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return mon + "/" + day + "/" + year;
    }


    /**
     * Returns time string for index
     *
     * @param date  Date
     * @return      Time string for indexing (yyyyMMddHHmmssSSS)
     */
    public static String getDateStringForIndex(final Date date) {
        return DateTools.dateToString(date, Resolution.MILLISECOND);
    }


    /**
     * Returns time string for index
     *
     * @param millis  Milli second
     * @return        Time string for indexing (yyyyMMddHHmmssSSS)
     */
    public static String getTimeStringForIndex(final long millis) {
        return DateTools.timeToString(millis, Resolution.MILLISECOND);
    }


    /**
     * Gets date parsed in format m/d/y from index format
     *
     * @param indexDateString  Index date string
     * @return                 Date string in format m/d/y
     */
    public static String getDateParsedFromIndex(final String indexDateString) {
        // convert index string to millis
        long millis = 1;
        try {
        	millis = DateTools.stringToTime(indexDateString);
        }
        catch (ParseException pe) {
        	logger.warn("getDateParsedFromIndex() failed. Possible by old indexDateString=" + indexDateString);
        }

        return getDateString(millis);
    }
}
