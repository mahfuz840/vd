package com.the_spartan.virtualdiary.model;

public enum Month {

    JANUARY("January", "Jan", 1),
    FEBRUARY("February", "Feb", 2),
    MARCH("March", "Mar", 3),
    APRIL("April", "Apr", 4),
    MAY("May", "May", 5),
    JUNE("June", "Jun", 6),
    JULY("July", "Jul", 7),
    AUGUST("August", "Aug", 8),
    SEPTEMBER("September", "Sep", 9),
    OCTOBER("October", "Oct", 10),
    NOVEMBER("November", "Nov", 11),
    DECEMBER("December", "Dec", 12);

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
