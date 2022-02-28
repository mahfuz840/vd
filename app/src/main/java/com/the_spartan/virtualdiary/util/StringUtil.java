package com.the_spartan.virtualdiary.util;

import static com.the_spartan.virtualdiary.model.Action.SEARCH_BY_QUERY;

import com.the_spartan.virtualdiary.model.Action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String getMonthNameFromInt(int value) {
        switch (value) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "Unknown";
        }
    }

    public static String getEncodedQueryStrWithAction(Action action, String queryStr) {
        return "[action=" +
                action +
                "]" +
                queryStr;
    }

    public static Action getActionFromQueryStr(String queryStr) {
        String regex = "\\[action=([A-Z]+)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(queryStr);

        if (matcher.find()) {
            return Action.valueOf(matcher.group(1));
        }

        return SEARCH_BY_QUERY;
    }
}
