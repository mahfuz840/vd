package com.the_spartan.virtualdiary.util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;

public class ViewUtil {

    public static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
}
