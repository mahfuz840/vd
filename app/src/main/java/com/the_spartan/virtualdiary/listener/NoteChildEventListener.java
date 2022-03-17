package com.the_spartan.virtualdiary.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.the_spartan.virtualdiary.adapter.NoteAdapter;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class NoteChildEventListener implements ChildEventListener {

    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;

    public NoteChildEventListener(ArrayList<Note> notes, NoteAdapter noteAdapter) {
        this.notes = notes;
        this.noteAdapter = noteAdapter;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Note note = dataSnapshot.getValue(Note.class);
        note.setKey(dataSnapshot.getKey());
        notes.add(note);

        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Note note = dataSnapshot.getValue(Note.class);

        for (Note currentNote : notes) {
            if (currentNote.getKey().equals(dataSnapshot.getKey())) {
                currentNote.setTitle(note.getTitle());
                currentNote.setDescription(note.getDescription());
                currentNote.setTimestamp(note.getTimestamp());
            }
        }

        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Note noteToDelete = dataSnapshot.getValue(Note.class);
        noteToDelete.setKey(dataSnapshot.getKey());
        notes.remove(noteToDelete);

        noteAdapter.notifyDataSetChanged();

        FirebaseHelper.getNoteKeys().remove(dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
