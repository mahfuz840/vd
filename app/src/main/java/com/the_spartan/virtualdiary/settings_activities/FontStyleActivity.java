package com.the_spartan.virtualdiary.settings_activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.fragments.CustomizationFragment;

public class FontStyleActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customization);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.settings_title);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new FontStylePreference()).commit();
        startActivity(new Intent(this, CustomizationFragment.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return true;
    }
}
