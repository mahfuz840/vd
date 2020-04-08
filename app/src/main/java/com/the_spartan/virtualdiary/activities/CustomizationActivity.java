package com.the_spartan.virtualdiary.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.the_spartan.virtualdiary.Adapters.FontSizeAdapter;
import com.the_spartan.virtualdiary.Adapters.FontsAdapter;
import com.the_spartan.virtualdiary.Adapters.SettingsAdapter;
import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by Spartan on 3/31/2018.
 */

//public class FontStylePreference extends PreferenceFragmentCompat {
//
//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.font_style_preference);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//}

public class CustomizationActivity extends AppCompatActivity{

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        final ArrayList<String> titles = new ArrayList<>();
        titles.add("Font Style");
        titles.add("Font Size");
        titles.add("Font Color");

        SettingsAdapter adapter = new SettingsAdapter(this, titles);
        ListView listView = findViewById(R.id.settings_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        showFontChooserDialog();
                        break;
                    case 1:
                        showSizeChooserDialog();
                        break;
                    case 2:
                        showColorChooserDialog();
                        break;
                }
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(CustomizationActivity.this);
        editor = preferences.edit();

    }

    private void showFontChooserDialog(){
        ArrayList<String> fontTitles = new ArrayList<>();

        final String[] fontPaths = CustomizationActivity.this.getResources().getStringArray(R.array.font_values);
        String[] fontNames = CustomizationActivity.this.getResources().getStringArray(R.array.font_names);
        for(int i = 0; i<fontNames.length; i++){
            fontTitles.add(fontNames[i]);
        }

        FontsAdapter adapter = new FontsAdapter(this, fontTitles);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose your custom font")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.putString("font", fontPaths[i]);
                        editor.apply();
                    }
                });
        builder.show();
    }

    private void showSizeChooserDialog(){
        String[] sizeString = CustomizationActivity.this.getResources().getStringArray(R.array.font_size_array);
        final String[] sizeValueString = CustomizationActivity.this.getResources().getStringArray(R.array.font_size_values);
        ArrayList<String> size = new ArrayList<>();
        for(int i = 0; i < sizeString.length; i++){
            size.add(sizeString[i]);
        }
        FontSizeAdapter adapter = new FontSizeAdapter(CustomizationActivity.this, size);

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomizationActivity.this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.putString("font_size", sizeValueString[i]);
                editor.apply();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

    private void showColorChooserDialog(){
        int mDefaultColor = preferences.getInt("color", 0);
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(CustomizationActivity.this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("color", color);
                editor.apply();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        colorPicker.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}