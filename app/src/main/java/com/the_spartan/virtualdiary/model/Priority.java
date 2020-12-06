package com.the_spartan.virtualdiary.model;

import android.graphics.Color;

import com.the_spartan.virtualdiary.fragment.ToDoActiveFragment;

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

//    public String getName() {
//        return ToDoActiveFragment.getToDoContext().getString(ToDoActiveFragment.getToDoContext().getResources().getIdentifier(name, "string",  ToDoActiveFragment.getToDoContext().getPackageName()));
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public String toString(){
//        return ToDoActiveFragment.getToDoContext().getString(ToDoActiveFragment.getToDoContext().getResources().getIdentifier(name, "string",  ToDoActiveFragment.getToDoContext().getPackageName()));
//    }
}