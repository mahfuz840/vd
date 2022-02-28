package com.the_spartan.virtualdiary.service;

import static com.the_spartan.virtualdiary.model.Note.NOTE;

import com.the_spartan.virtualdiary.adapter.NoteAdapter;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.listener.NoteChildEventListener;
import com.the_spartan.virtualdiary.model.Note;

import java.util.ArrayList;

public class NoteService {

    public void findAll(ArrayList<Note> notes, NoteAdapter noteAdapter) {
        FirebaseHelper.getQueryForNotes()
                .addChildEventListener(
                        new NoteChildEventListener(notes, noteAdapter)
                );
    }

    public void saveOrUpdate(Note note) {
        if (note.isNew()) {
            FirebaseHelper.getReference().child(NOTE).push().setValue(note);

        } else {
            FirebaseHelper.getReference().child(NOTE).child(note.getKey()).setValue(note);
        }
    }

    public void delete(Note note) {
        FirebaseHelper.getReference().child(NOTE).child(note.getKey()).removeValue();
    }
}
