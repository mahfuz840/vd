package com.the_spartan.virtualdiary.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.the_spartan.virtualdiary.adapter.AddOptionsAdapter;
import com.the_spartan.virtualdiary.adapter.RecyclerAdapter;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.CreateNoteActivity;
import com.the_spartan.virtualdiary.activity.GoogleSigninActivity;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.data.NoteContract;
import com.the_spartan.virtualdiary.data.NoteDbHelper;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class MainFragment extends Fragment {

    ListView noteListView;
    RecyclerView noteView;
    Spinner monthSpinner;
    Spinner yearSpinner;
    ArrayList<Note> notes;
    int month, year;
    ArrayAdapter<CharSequence> yearAdapter;
    ArrayAdapter<CharSequence> monthAdapter;
    private DrawerLayout mDrawerLayout;
    //    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    NavigationView navigationView;
    private RelativeLayout homepageEmptyLayout;
    FloatingActionButton floatingActionButton;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        rfaLayout = view.findViewById(R.id.activity_main_rfal);
//        rfaButton = view.findViewById(R.id.activity_main_rfab);
        floatingActionButton = view.findViewById(R.id.fab);
        homepageEmptyLayout = view.findViewById(R.id.home_page_empty_relative_layout);
        toolbar = view.findViewById(R.id.my_toolbar);
        ((MainActivity)getActivity()).setToolbar(toolbar);
        ((MainActivity)getActivity()).setTitle("");

        noteView = view.findViewById(R.id.notes_grid_view);
//        navigationView = view.findViewById(R.id.navigation_view);
        monthSpinner = view.findViewById(R.id.month_spinner);
        yearSpinner = view.findViewById(R.id.year_spinner);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        monthAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.months_array, R.layout.spinner_item);
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        yearAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.year_array, R.layout.spinner_item);
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        setSpinnerDefaults();

        month = monthSpinner.getSelectedItemPosition();
        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        ArrayList<String> addOptions = new ArrayList<>();
        addOptions.add("Text");
        addOptions.add("To-Do");

        final AddOptionsAdapter addOptionsAdapter = new AddOptionsAdapter(getContext(), addOptions);

        displayDatabaseInfo(month, year);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return false;
    }


    @Override
    public void onResume() {
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


        month = monthSpinner.getSelectedItemPosition();
        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
        displayDatabaseInfo(month, year);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNoteActivity.class));
            }
        });

    }

    private void displayDatabaseInfo(int month, int year) {

        Log.d("MainActivity", month + " " + year);

        NoteDbHelper mDbHelper = new NoteDbHelper(getContext());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                NoteContract.NoteEntry.COLUMN_ID,
                NoteContract.NoteEntry.COLUMN_DATE,
                NoteContract.NoteEntry.COLUMN_TITLE,
                NoteContract.NoteEntry.COLUMN_DESCRIPTION,
                NoteContract.NoteEntry.COLUMN_MONTH,
                NoteContract.NoteEntry.COLUMN_YEAR
        };

        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(
                    NoteProvider.CONTENT_URI,
                    projection,
                    NoteContract.NoteEntry.COLUMN_MONTH + "=? AND " + NoteContract.NoteEntry.COLUMN_YEAR + " =?",
                    new String[]{String.valueOf(month + 1), String.valueOf(year)},
                    NoteContract.NoteEntry.COLUMN_DATE + " ASC");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor.getCount() == 0)
                Log.d("cursor", "null");
            notes = new ArrayList<>();
            while (cursor.moveToNext()) {
                Note note = new Note(cursor.getInt(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_ID)),
                        cursor.getLong(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_DESCRIPTION)));

                notes.add(note);
            }

            cursor.close();
            db.close();
        }


        if (notes.isEmpty()) {
            homepageEmptyLayout.setVisibility(View.VISIBLE);
            homepageEmptyLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));
            noteView.setVisibility(View.INVISIBLE);
            return;
        } else {
            homepageEmptyLayout.setVisibility(View.INVISIBLE);
            noteView.setVisibility(View.VISIBLE);
//            noteView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));
        }

        noteView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        noteView.hasFixedSize();
        final RecyclerAdapter adapter = new RecyclerAdapter(getContext(), notes);

        adapter.setOnItemClickListener(new RecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Note note = notes.get(position);
                Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                intent.putExtra(NoteContract.NoteEntry.COLUMN_ID, note.getID());
                intent.putExtra(NoteContract.NoteEntry.COLUMN_DATE, note.getmDateTime());
                intent.putExtra("formatted_time", note.getDateTimeFormatted(getContext()));
                intent.putExtra(NoteContract.NoteEntry.COLUMN_TITLE, note.getMtitle());
                intent.putExtra(NoteContract.NoteEntry.COLUMN_DESCRIPTION, note.getmContent());
                startActivity(intent);
            }
        });

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
    public void onPause() {
        notes.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        notes.clear();
        super.onDestroy();
    }

    private void checkSignin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null)
            startActivity(new Intent(getContext(), GoogleSigninActivity.class));
    }
}
