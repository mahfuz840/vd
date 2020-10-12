package com.the_spartan.virtualdiary.activity.settings;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.the_spartan.virtualdiary.R;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FontColorActivity extends AppCompatActivity {

    int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_color);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.font_color_settings_title);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mDefaultColor = preferences.getInt("color", 0);
        if (mDefaultColor == 0)
            mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(FontColorActivity.this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                finish();
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("color", mDefaultColor);
                editor.apply();

                Drawable colorIcon = ContextCompat.getDrawable(FontColorActivity.this, R.drawable.font_color_preview);
                assert colorIcon != null;
                colorIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                finish();
            }
        });
        colorPicker.show();
    }

}
