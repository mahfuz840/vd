package com.the_spartan.virtualdiary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static final String MONTH_YEAR_SEPARATOR = "-";
    public static final String MONTH_YEAR_PREFIX = "#DATE#";

    public static Date getDateFromString(String dateString) throws ParseException {
        if (dateString.trim().length() == 0) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("d/MM/yyyy", Locale.getDefault());

        return sdf.parse(dateString);
    }

    public static Date getTomorrowFormattedDate() throws ParseException {
        Date date = getCurrentFormattedDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    public static Date getCurrentFormattedDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("d/MM/yyyy", Locale.getDefault());
        String currentDateString = sdf.format(new Date());

        return sdf.parse(currentDateString);
    }

    public static Date getThirtyDaysEarlierDate(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);

        return calendar.getTime();
    }

    public static String getEncodedMonthYearWithPrefix(int month, int year) {
        return MONTH_YEAR_PREFIX + month + MONTH_YEAR_SEPARATOR + year;
    }

    public static int getDecodedMonthFromMonthYearStr(String encodedStr) {
        String[] splittedQuery = encodedStr.replace(MONTH_YEAR_PREFIX, "").split(DateUtil.MONTH_YEAR_SEPARATOR);
        return Integer.parseInt(splittedQuery[0]);
    }

    public static int getDecodedYearFromMonthYearStr(String encodedStr) {
        String[] splittedQuery = encodedStr.replace(MONTH_YEAR_PREFIX, "").split(DateUtil.MONTH_YEAR_SEPARATOR);
        return Integer.parseInt(splittedQuery[1]);
    }
}
