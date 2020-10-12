package com.the_spartan.virtualdiary.model;

import android.graphics.Color;

import com.the_spartan.virtualdiary.activity.ToDoFragment;

;

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
        return ToDoFragment.getToDoContext().getString(ToDoFragment.getToDoContext().getResources().getIdentifier(name, "string",  ToDoFragment.getToDoContext().getPackageName()));
    }

    public int getColor() {
        return color;
    }

    public String toString(){
        return ToDoFragment.getToDoContext().getString(ToDoFragment.getToDoContext().getResources().getIdentifier(name, "string",  ToDoFragment.getToDoContext().getPackageName()));
    }
}