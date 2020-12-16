package com.the_spartan.virtualdiary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getTwelveHourFormattedTime(String time) {
        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            formattedTime = new SimpleDateFormat("K:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return formattedTime;
    }

    public static String getTwentyFourHourFormattedTime(String time) {
        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("K:mm a");
            final Date dateObj = sdf.parse(time);
            formattedTime = new SimpleDateFormat("HH:mm").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return formattedTime;
    }

    public static String formatTime(int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;

        return getTwelveHourFormattedTime(time);
    }
}
