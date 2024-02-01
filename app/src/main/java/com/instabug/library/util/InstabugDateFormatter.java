package com.instabug.library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class InstabugDateFormatter {
    public static long getCurrentUTCTimeStampInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getCurrentUTCTimeStampInMiliSeconds() {
        return System.currentTimeMillis();
    }

    public static String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String formatUTCDate(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1000 * j);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String convertUnixTimeToFormattedDate(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(convertUnixTimeToDate(j));
    }

    public static Date convertUnixTimeToDate(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return calendar.getTime();
    }

    public static String formatMessageDate(long j) {
        return new SimpleDateFormat("dd-MMM HH:mm", Locale.US).format(Long.valueOf(1000 * j));
    }

    public static String formatConversationLastMessageDate(long j) {
        return new SimpleDateFormat("dd MMM", Locale.US).format(Long.valueOf(1000 * j));
    }

    public static Date getStandardizedDate(Date date) {
        return new Date(date.getTime());
    }

    public static Date getDate(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
