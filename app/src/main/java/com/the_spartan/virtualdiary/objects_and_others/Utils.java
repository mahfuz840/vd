package com.the_spartan.virtualdiary.objects_and_others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activities.GoogleSigninActivity;
import com.the_spartan.virtualdiary.activities.MainActivity;

public class Utils {

    public static Typeface initializeFonts(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fontPath = preferences.getString(context.getString(R.string.font), null);
        Typeface defaultFont = context.getResources().getFont(R.font.roboto);

        Typeface myFont;


        if (fontPath != null && !fontPath.equals("null"))
            myFont = Typeface.createFromAsset(context.getAssets(), fontPath);
        else
            myFont = defaultFont;

        return myFont;
    }

    public static Typeface loadFontForFontChooserDialog(Context context, String path){
        Typeface font = Typeface.createFromAsset(context.getAssets(), path);
        return font;
    }

    public static int initializeColor(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int color = preferences.getInt("color", 0);
        return color;
    }

    public static String initializeFontSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String fontSize = preferences.getString(context.getString(R.string.font_size), null);

        return fontSize;
    }

}
