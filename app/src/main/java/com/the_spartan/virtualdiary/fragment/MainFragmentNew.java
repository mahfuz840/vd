package com.the_spartan.virtualdiary.fragment;

import static com.the_spartan.virtualdiary.model.Note.NOTE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.CreateNoteActivity;
import com.the_spartan.virtualdiary.adapter.NoteAdapterNew;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.listener.NoteChildEventListener;
import com.the_spartan.virtualdiary.listener.NoteItemLongClickListener;
import com.the_spartan.virtualdiary.model.Month;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.util.DateUtil;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainFragmentNew extends Fragment {

    private static MainFragmentNew instance;

    private NoteAdapterNew noteAdapter;
    private ListView lvNote;
    private FloatingActionButton floatingActionButton;
    private SearchView noteSearchView;
    private LinearLayout lvMonthYearPicker;
    private TextView tvMonthYearPicker;

    private MainFragmentNew() {
    }

    public static MainFragmentNew getInstance() {
        if (instance == null) {
            instance = new MainFragmentNew();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_new, container, false);

        lvNote = view.findViewById(R.id.list_view_notes);
        floatingActionButton = view.findViewById(R.id.floating_btn_add_note);
        noteSearchView = view.findViewById(R.id.search_view_note);
        lvMonthYearPicker = view.findViewById(R.id.picker_note_month_year);
        tvMonthYearPicker = view.findViewById(R.id.tv_month_year_picker);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateMonthYearPicker();
        populateNotesAndSetAdapter();
        registerListeners();
    }

    private void populateMonthYearPicker() {
        Calendar now = Calendar.getInstance();
        StringBuilder monthYearStrBuiler = new StringBuilder()
                .append(Objects.requireNonNull(Month.fromIntValue(now.get(Calendar.MONTH) + 1))
                        .getFullName())
                .append(", ")
                .append(now.get(Calendar.YEAR));

        tvMonthYearPicker.setText(monthYearStrBuiler.toString());
    }

    private void populateMonthYearPicker(int month, int year) {
        StringBuilder monthYearStringBuilder = new StringBuilder()
                .append(Month.fromIntValue(month + 1).getFullName())
                .append(", ")
                .append(year);

        tvMonthYearPicker.setText(monthYearStringBuilder.toString());
    }

    private void populateNotesAndSetAdapter() {
        ArrayList<Note> notes = new ArrayList<>();
        noteAdapter = new NoteAdapterNew(getContext(), notes);

        populateNotes(notes);
        lvNote.setAdapter(noteAdapter);
    }

//    private void testWrite() {
//        Note note = new Note(0, 0, "Title", "Content");
//        FirebaseHelper.getReference().push().setValue(note);
//    }

    private void populateNotes(ArrayList<Note> notes) {
        FirebaseHelper.getQueryForNotes()
                .addChildEventListener(
                        new NoteChildEventListener(notes, noteAdapter)
                );
    }

    private void registerListeners() {
        lvNote.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent noteIntent = new Intent(MainFragmentNew.this.getContext(), CreateNoteActivity.class);
            noteIntent.putExtra(NOTE, noteAdapter.getItem(position));
            MainFragmentNew.this.startActivity(noteIntent);
        });
        lvNote.setOnItemLongClickListener(new NoteItemLongClickListener());

        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), CreateNoteActivity.class));
        });

        noteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                noteAdapter.getFilter().filter(queryText);

                return true;
            }
        });

        lvMonthYearPicker.setOnClickListener(view -> {
            Calendar today = Calendar.getInstance();

            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                    (selectedMonth, selectedYear) -> {
                        noteAdapter.getFilter()
                                .filter(DateUtil.getEncodedMonthYearWithPrefix(selectedMonth + 1, selectedYear));

                        populateMonthYearPicker(selectedMonth, selectedYear);
                    }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

            builder.setActivatedMonth(today.get(Calendar.MONTH))
                    .setActivatedYear(today.get(Calendar.YEAR))
                    .setMinYear(2015)
                    .setMaxYear(2030)
                    .setTitle("Select Month For Notes")
                    .build()
                    .show();
        });
    }
}
