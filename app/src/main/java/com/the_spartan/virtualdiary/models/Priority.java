package com.the_spartan.virtualdiary.models;

import android.graphics.Color;
;import com.the_spartan.virtualdiary.activities.ToDoActivity;

public enum Priority {
    LOW("low", 0, Color.rgb(204, 214, 0)),
    MEDIUM("medium", 1, Color.rgb(255, 170, 0)),
    HIGH("high", 2, Color.rgb(255, 0, 0));

    private final int value;
    private final String name;
    private final int color;

    Priority(final String newName, final int newValue, final int newColor) {
        name = newName;
        value = newValue;
        color = newColor;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return ToDoActivity.getContext().getString(ToDoActivity.getContext().getResources().getIdentifier(name, "string",  ToDoActivity.getContext().getPackageName()));
    }

    public int getColor() {
        return color;
    }

    @Override public String toString(){
        return ToDoActivity.getContext().getString(ToDoActivity.getContext().getResources().getIdentifier(name, "string",  ToDoActivity.getContext().getPackageName()));
    }
}