package com.the_spartan.virtualdiary.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.the_spartan.virtualdiary.adapter.NoteAdapterNew;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class NoteChildEventListener implements com.google.firebase.database.ChildEventListener {

    private ArrayList<Note> notes;
    private NoteAdapterNew noteAdapter;

    public NoteChildEventListener(ArrayList<Note> notes, NoteAdapterNew noteAdapter) {
        this.notes = notes;
        this.noteAdapter = noteAdapter;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        notes.add(dataSnapshot.getValue(Note.class));
        noteAdapter.notifyDataSetChanged();

        FirebaseHelper.getNoteKeys().add(dataSnapshot.getKey());
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        notes.remove(dataSnapshot.getValue(Note.class));
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
