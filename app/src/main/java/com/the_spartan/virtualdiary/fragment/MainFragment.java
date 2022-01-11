package com.the_spartan.virtualdiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.CreateNoteActivity;
import com.the_spartan.virtualdiary.activity.GoogleSigninActivity;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.adapter.AddOptionsAdapter;
import com.the_spartan.virtualdiary.adapter.MyRecyclerAdapter;
import com.the_spartan.virtualdiary.data.NoteContract;
import com.the_spartan.virtualdiary.data.NoteDbHelper;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;
import java.util.Calendar;

public class MainFragment extends Fragment {

    private RecyclerView noteView;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private ArrayList<Note> notes;
    private int month, year;
    private ArrayAdapter<CharSequence> yearAdapter;
    private ArrayAdapter<CharSequence> monthAdapter;
    private Toolbar toolbar;

    private RelativeLayout homepageEmptyLayout;
    private FloatingActionButton floatingActionButton;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        floatingActionButton = view.findViewById(R.id.fab);
        homepageEmptyLayout = view.findViewById(R.id.home_page_empty_relative_layout);
        toolbar = view.findViewById(R.id.my_toolbar);
//        ((MainActivity)getActivity()).setToolbar(toolbar);
        ((MainActivity)getActivity()).setTitle("");

        noteView = view.findViewById(R.id.notes_grid_view);
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

        checkEmpty();

        int numOfColumns = calculateNoOfColumns(getContext(), 150);
        noteView.setLayoutManager(new GridLayoutManager(getContext(), numOfColumns));
        noteView.hasFixedSize();
        final MyRecyclerAdapter adapter = new MyRecyclerAdapter(getContext(), notes);

        adapter.setOnItemClickListener(new MyRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Note note = notes.get(position);
                Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                intent.putExtra(NoteContract.NoteEntry.COLUMN_ID, note.getID());
                intent.putExtra(NoteContract.NoteEntry.COLUMN_DATE, note.getDateTime());
                intent.putExtra("formatted_time", note.getDateTimeFormatted(getContext()));
                intent.putExtra(NoteContract.NoteEntry.COLUMN_TITLE, note.getTitle());
                intent.putExtra(NoteContract.NoteEntry.COLUMN_DESCRIPTION, note.getDescription());
                startActivity(intent);
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
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

    private void checkEmpty() {
        if (notes.isEmpty()) {
            homepageEmptyLayout.setVisibility(View.VISIBLE);
            homepageEmptyLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));
            noteView.setVisibility(View.INVISIBLE);
            return;
        } else {
            homepageEmptyLayout.setVisibility(View.INVISIBLE);
            noteView.setVisibility(View.VISIBLE);
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

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            columnWidthDp /= 1.8;
        }
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }
}
