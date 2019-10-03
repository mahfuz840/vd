package com.the_spartan.virtualdiary.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.drive.Drive;
import com.the_spartan.virtualdiary.Adapters.AddOptionsAdapter;
import com.the_spartan.virtualdiary.Adapters.MyRecyclerAdapter;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.NoteContract;
import com.the_spartan.virtualdiary.data.NoteContract.NoteEntry;
import com.the_spartan.virtualdiary.data.NoteDbHelper;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.objects_and_others.ABShape;
import com.the_spartan.virtualdiary.objects_and_others.ABTextUtil;
import com.the_spartan.virtualdiary.objects_and_others.Note;
import com.the_spartan.virtualdiary.settings_activities.NotificationActivity;
import com.the_spartan.virtualdiary.settings_activities.PasswordSettingsActivity;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    ListView noteListView;
    RecyclerView noteView;
    Spinner monthSpinner;
    Spinner yearSpinner;
    ArrayList<Note> notes;
    int month, year;
    ArrayAdapter<CharSequence> yearAdapter;
    ArrayAdapter<CharSequence> monthAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private int backPressed = 0;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

//        checkSignin();

        // testing fab here
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(MainActivity.this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
            @Override
            public void onRFACItemLabelClick(int position, RFACLabelItem item) {
                if(position == 0)
                    startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
                else
                    startActivity(new Intent(MainActivity.this, ToDoActivity.class));
                rfabHelper.toggleContent();
            }

            @Override
            public void onRFACItemIconClick(int position, RFACLabelItem item) {
//                Toast.makeText(MainActivity.this, "clicked icon: " + position, Toast.LENGTH_SHORT).show();
                if(position == 0)
                    startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
                else
                    startActivity(new Intent(MainActivity.this, ToDoActivity.class));
                rfabHelper.toggleContent();
            }
        });
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("New Note")
                .setResId(R.drawable.ic_create_white_24dp)
                .setIconNormalColor(0xffbf7a4d)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("To-Do")
                .setResId(R.drawable.ic_todo)
                .setIconNormalColor(0xff946b50)
                .setIconPressedColor(0xff3e2723)
//                .setLabelColor(Color.WHITE)
                .setLabelSizeSp(14)
//                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(MainActivity.this, 4)))
                .setWrapper(1)
        );

        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(MainActivity.this, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(MainActivity.this, 5))
        ;

        rfaLayout = findViewById(R.id.activity_main_rfal);
        rfaButton = findViewById(R.id.activity_main_rfab);
        rfabHelper = new RapidFloatingActionHelper(
                MainActivity.this,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();
        // testing fab finishes here...


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        noteListView = findViewById(R.id.notes_list_view);
        noteView = findViewById(R.id.notes_grid_view);
        navigationView = findViewById(R.id.navigation_view);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);

        monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        setSpinnerDefaults();

        month = monthSpinner.getSelectedItemPosition();
        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        ArrayList<String> addOptions = new ArrayList<>();
        addOptions.add("Text");
        addOptions.add("To-Do");

        final AddOptionsAdapter addOptionsAdapter = new AddOptionsAdapter(MainActivity.this, addOptions);

        // old FAB guy is here...
//        FloatingActionButton newFAB = findViewById(R.id.new_fab);
//        newFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
////                startActivity(intent);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                        .setAdapter(addOptionsAdapter, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) {
//                                    Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
//                                    startActivity(intent);
//                                }
//
//                            }
//                        });
//
//                builder.show();
//            }
//        });

        displayDatabaseInfo(month, year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_search:
                final EditText searchTerm = new EditText(this);
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Enter search term: ")
                        .setMessage("Type anything that you want to search")
                        .setView(searchTerm)
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                try {
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean wantToCloseDialog = false;
                        //Do stuff, possibly set wantToCloseDialog to true then...
                        String searchTermString = searchTerm.getText().toString();
                        if (searchTermString.trim().equals("")) {
                            searchTerm.setError("Please type something to search for!");
                        } else {
                            searchTermString = searchTermString.replace("\\s", "");
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            intent.putExtra("term", searchTermString);
                            dialog.dismiss();
                            startActivity(intent);
                        }
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                    }
                });
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int month = position;
                int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                displayDatabaseInfo(month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int year = Integer.parseInt(parent.getItemAtPosition(position).toString());
                int month = monthSpinner.getSelectedItemPosition();
                displayDatabaseInfo(month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // set item as selected to persist highlight
                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.nav_about:
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_customiation:
                        Intent intent1 = new Intent(MainActivity.this, CustomizationActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_password_lock:
                        startActivity(new Intent(MainActivity.this, PasswordSettingsActivity.class));
                        break;

                    case R.id.nav_notification:
                        Intent notificationActivityIntent = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(notificationActivityIntent);
                        break;

                    case R.id.backup_restore:
                        startActivity(new Intent(MainActivity.this, BackupRestoreActivity.class));
                        break;
                }
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                return true;
            }
        });

        month = monthSpinner.getSelectedItemPosition();
        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
        displayDatabaseInfo(month, year);

    }

    private void displayDatabaseInfo(int month, int year) {

        Log.d("MainActivity", month + " " + year);

        NoteDbHelper mDbHelper = new NoteDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                NoteEntry.COLUMN_ID,
                NoteEntry.COLUMN_DATE,
                NoteEntry.COLUMN_TITLE,
                NoteEntry.COLUMN_DESCRIPTION,
                NoteEntry.COLUMN_MONTH,
                NoteEntry.COLUMN_YEAR
        };

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    NoteProvider.CONTENT_URI,
                    projection,
                    NoteEntry.COLUMN_MONTH + "=? AND " + NoteEntry.COLUMN_YEAR + " =?",
                    new String[]{String.valueOf(month + 1), String.valueOf(year)},
                    NoteEntry.COLUMN_DATE + " ASC");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor.getCount() == 0)
                Log.d("cursor", "null");
            notes = new ArrayList<>();
            while (cursor.moveToNext()) {
                Note note = new Note(cursor.getInt(cursor.getColumnIndex(NoteEntry.COLUMN_ID)),
                        cursor.getLong(cursor.getColumnIndex(NoteEntry.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_DESCRIPTION)));

                notes.add(note);
            }

            cursor.close();
            db.close();
        }


        if (notes.isEmpty()) {
            findViewById(R.id.home_page_empty_relative_layout).setVisibility(View.VISIBLE);
            noteView.setVisibility(View.INVISIBLE);
            return;
        } else {
            findViewById(R.id.home_page_empty_relative_layout).setVisibility(View.INVISIBLE);
            noteView.setVisibility(View.VISIBLE);
        }

        noteView.setLayoutManager(new GridLayoutManager(this, 3));
        noteView.hasFixedSize();
        final MyRecyclerAdapter adapter = new MyRecyclerAdapter(this, notes);

        adapter.setOnItemClickListener(new MyRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Note note = notes.get(position);
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtra(NoteEntry.COLUMN_ID, note.getID());
                intent.putExtra(NoteEntry.COLUMN_DATE, note.getmDateTime());
                intent.putExtra("formatted_time", note.getDateTimeFormatted(MainActivity.this));
                intent.putExtra(NoteEntry.COLUMN_TITLE, note.getMtitle());
                intent.putExtra(NoteEntry.COLUMN_DESCRIPTION, note.getmContent());
                startActivity(intent);
            }
        });

//        adapter.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder();
//                builder.setTitle("Warning!");
//                builder.setMessage("Do your really want to delete this note?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        int pos = adapter.getAdapterPosition();
//                        getApplicationContext().getContentResolver().delete(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(notes.get(which).getID())),
//                                null,
//                                null);
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                android.app.AlertDialog dialog = builder.create();
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//                dialog.show();
//
//                adapter.notifyDataSetChanged();
//
//
//                return true;
//            }
//        });
//        adapter.setOnLongClickListener(new MyRecyclerAdapter.ClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//
//            }
//        });

        noteView.setAdapter(adapter);
    }

    private void setSpinnerDefaults() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        monthSpinner.setSelection(month);

        int year = calendar.get(Calendar.YEAR);

        for (int i = 0; i < yearAdapter.getCount(); i++) {
            if (yearAdapter.getItem(i).equals(String.valueOf(year))) {
                yearSpinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed >= 2)
            super.onBackPressed();
        else
            Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        notes.clear();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        notes.clear();
        super.onDestroy();
    }

    private void checkSignin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
            startActivity(new Intent(this, GoogleSigninActivity.class));
    }
}
