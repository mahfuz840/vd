package com.the_spartan.virtualdiary.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.fragment.MainFragmentNew;
import com.the_spartan.virtualdiary.fragment.ToDoParentFragment;

public class MainActivity extends AppCompatActivity {

    private int backPressed = 0;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_new);

        initBottomNavigationView();
//        toolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        mDrawerLayout = findViewById(R.id.drawer_layout);
//        drawerToggle = setupDrawerToggle();
//        navigationView = findViewById(R.id.navigation_view);
//
//        drawerToggle.setDrawerIndicatorEnabled(true);
//        drawerToggle.syncState();
//
//        mDrawerLayout.addDrawerListener(drawerToggle);
//
//        if (mDrawerLayout != null) {
//            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                    selectDrawerItem(menuItem);
//                    mDrawerLayout.closeDrawers();
//
//                    return true;
//                }
//            });
//        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_fragment_container, MainFragmentNew.getInstance())
                .commit();
    }

    private void initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_nav_main);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.nav_todo:
                    fragment = ToDoParentFragment.getInstance();
                    break;

                case R.id.nav_notes:
                    fragment = MainFragmentNew.getInstance();
                    break;

                default:
                    fragment = MainFragmentNew.getInstance();
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_fragment_container, fragment)
                    .commit();

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_notes);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
//                mDrawerLayout.openDrawer(GravityCompat.START);
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

    private void openPlaystore() {
        try {
            String market_uri = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(market_uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                    .FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Sorry! unable to access playstore", Toast.LENGTH_SHORT).show();
        }
    }
}
