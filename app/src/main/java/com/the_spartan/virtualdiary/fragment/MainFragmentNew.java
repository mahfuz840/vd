package com.the_spartan.virtualdiary.fragment;

import static android.provider.ContactsContract.CommonDataKinds.Note.NOTE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class MainFragmentNew extends Fragment {

    private static MainFragmentNew instance;

    private NoteAdapterNew noteAdapter;
    private ListView lvNote;
    private FloatingActionButton floatingActionButton;
    private SearchView noteSearchView;

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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateNotesAndSetAdapter();
        registerListeners();
    }

    private void populateNotesAndSetAdapter() {
        ArrayList<Note> notes = new ArrayList<>();
        noteAdapter = new NoteAdapterNew(getContext(), notes);

        populateNotes(notes);
        lvNote.setAdapter(noteAdapter);
    }

    private void testWrite() {
        Note note = new Note(0, 0, "Title", "Content");
        FirebaseHelper.getReference().push().setValue(note);
    }

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
    }
}
