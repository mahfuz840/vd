package com.the_spartan.virtualdiary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.the_spartan.virtualdiary.R;

public class FontUtil {

    public static Typeface initializeFonts(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fontPath = preferences.getString(context.getString(R.string.pref_key_font), null);
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

        String fontSize = preferences.getString(context.getString(R.string.pref_key_font_size), null);

        return fontSize;
    }

}
