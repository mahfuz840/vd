package com.the_spartan.virtualdiary.model;

public enum Month {

    JANUARY("January", "Jan", 0),
    FEBRUARY("February", "Feb", 1),
    MARCH("March", "Mar", 2),
    APRIL("April", "Apr", 3),
    MAY("May", "May", 4),
    JUNE("June", "Jun", 5),
    JULY("July", "Jul", 6),
    AUGUST("August", "Aug", 7),
    SEPTEMBER("September", "Sep", 8),
    OCTOBER("October", "Oct", 9),
    NOVEMBER("November", "Nov", 10),
    DECEMBER("December", "Dec", 11);

    private String fullName;
    private String shortName;
    private int intValue;

    Month(String fullName, String shortName, int intValue) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.intValue = intValue;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public int getIntValue() {
        return intValue;
    }

    public static Month fromIntValue(int intValue) {
//        switch (intValue) {
//            case 1:
//                return JANUARY;
//            case 2:
//                return FEBRUARY;
//            case 3:
//                return MARCH;
//            case 4:
//                return APRIL;
//            case 5:
//                return MAY;
//            case 6:
//                return JUNE;
//            case 7:
//                return JULY;
//            case 8:
//                return AUGUST;
//            case 9:
//                return SEPTEMBER;
//            case 10:
//                return OCTOBER;
//            case 11:
//                return NOVEMBER;
//            case 12:
//                return DECEMBER;
//        }
//
        for (Month month : values()) {
            if (month.getIntValue() == intValue) {
                return month;
            }
        }

        return null;
    }
}
