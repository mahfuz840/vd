package com.the_spartan.virtualdiary.util;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class ViewUtil {

    public static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    public static void hideViewsOfaDay(CardView emptyView, TextView textView, ListView listView) {
        emptyView.setVisibility(View.VISIBLE);
        setVisibility(View.GONE, emptyView, textView, listView);
    }

    public static void showViewsOfaDay(CardView emptyView, TextView textView, ListView listView) {
        emptyView.setVisibility(View.GONE);
        setVisibility(View.VISIBLE, textView, listView);
    }
}
