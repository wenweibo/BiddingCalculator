package com.ertw.biddingcalculator.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: wenweibo
 * Time: 2025/2/18 11:41
 * Description:This is DateUtils
 */
public class DateUtils {
    private static  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static  SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static String getCurrentDateStr(){
        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentDateTimeStr(){
        return simpleDateTimeFormat.format(new Date());
    }

    public static String getCurrentDateByPattern(String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    /**
     * 日期比较
     * @param dateStr1 "yyyy/MM/dd"
     * @param dateStr2 "yyyy/MM/dd"
     * @return
     */
    public static boolean dateComparison(String dateStr1, String dateStr2){
        boolean result = false;
        try {
            Date date1 = simpleDateFormat.parse(dateStr1);
            Date date2 = simpleDateFormat.parse(dateStr2);
            result = date2.getTime() >= date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Calculate the delay time from the current time to 2:00 AM of the next day.
     * @return
     */
    public static long getInitialDelayFor2AM() {
        Calendar calendar = Calendar.getInstance();
        // Set it to 2:00 AM.
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the current time has already passed 2:00 AM, set it to 2:00 AM of the next day.
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    /**
     * Check if it is the first day of the current month.
     * @return
     */
    public static boolean isFirstDayOfMonth() {
        return LocalDate.now().getDayOfMonth() == 1;
    }

    /**
     * 获取当月1日的最早日期时间
     * @return
     */
    public static String getFirstDayStartTimeFormatted() {
        LocalDateTime firstDayStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String date = firstDayStart.format(formatter);
        Log.d("getFirstDayStartTimeFormatted","first day start time:"+date);
        return date;
    }
}
