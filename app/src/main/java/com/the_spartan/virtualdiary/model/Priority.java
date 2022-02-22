package com.the_spartan.virtualdiary.model;

import android.graphics.Color;

;

public enum Priority {

    LOW("Low", 0, Color.rgb(204, 214, 0)),
    MEDIUM("Medium", 1, Color.rgb(255, 170, 0)),
    HIGH("High", 2, Color.rgb(255, 0, 0));

    private final int intValue;
    private final int color;
    private String displayName;

    Priority(String displayName, int intValue, int color) {
        this.displayName = displayName;
        this.intValue = intValue;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getColor() {
        return color;
    }
}