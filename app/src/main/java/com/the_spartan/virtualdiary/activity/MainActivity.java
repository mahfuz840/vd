package com.the_spartan.virtualdiary.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.navigation.NavigationView;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.fragment.AboutFragment;
import com.the_spartan.virtualdiary.fragment.BackupRestoreFragment;
import com.the_spartan.virtualdiary.fragment.CustomizationFragment;
import com.the_spartan.virtualdiary.fragment.MainFragment;
import com.the_spartan.virtualdiary.fragment.NotificationFragment;
import com.the_spartan.virtualdiary.fragment.PasswordSettingsFragment;
import com.the_spartan.virtualdiary.fragment.ToDoFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    private int backPressed = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        navigationView = findViewById(R.id.navigation_view);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        mDrawerLayout.addDrawerListener(drawerToggle);

        if(mDrawerLayout != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    selectDrawerItem(menuItem);
                    mDrawerLayout.closeDrawers();

                    return true;
                }
            });
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, MainFragment.newInstance())
                .commit();
        getSupportActionBar().hide();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,  R.string.close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_customiation:
                fragmentClass = CustomizationFragment.class;
                break;
            case R.id.nav_notification:
                fragmentClass = NotificationFragment.class;
                break;
            case R.id.nav_password_lock:
                fragmentClass = PasswordSettingsFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.backup_restore:
                fragmentClass = BackupRestoreFragment.class;
                break;
            case R.id.nav_notes:
                fragmentClass = MainFragment.class;
                break;
            case R.id.nav_todo:
                fragmentClass = ToDoFragment.class;
                break;
            default:
                fragmentClass = AboutFragment.class;
        }

        Objects.requireNonNull(getSupportActionBar()).hide();

        try {
            fragment = (Fragment)fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                .commit();

        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();

        AudienceNetworkAds.initialize(this);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d("TAG", "onPostCreate");
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                String msg = "Enter search term: ";
                String posText = "Search";
                String negText = "Cancel";

                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search, viewGroup, false);
                dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));


                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView msgView = dialogView.findViewById(R.id.text_dialog);
                msgView.setText(msg);

                TextView posBtn = dialogView.findViewById(R.id.pos_btn);
                posBtn.setText(posText);

                TextView negBtn = dialogView.findViewById(R.id.neg_btn);
                negBtn.setText(negText);

                final EditText searchTerm = dialogView.findViewById(R.id.search_edit_text);

                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String searchTermString = searchTerm.getText().toString();
                        if (searchTermString.trim().equals("")) {
                            searchTerm.setError("Please type something to search for!");
                        } else {
                            searchTermString = searchTermString.replace("\\s", "");
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            intent.putExtra("term", searchTermString);
                            alertDialog.dismiss();
                            startActivity(intent);
                        }
                    }
                });
                negBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed >= 2)
            super.onBackPressed();
        else
            Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
    }
}
