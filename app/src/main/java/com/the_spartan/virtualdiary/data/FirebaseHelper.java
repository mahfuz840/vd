package com.the_spartan.virtualdiary.data;

import static com.the_spartan.virtualdiary.model.Note.NOTE;
import static com.the_spartan.virtualdiary.model.ToDo.TODO;

import android.app.DownloadManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FirebaseHelper {

    public static ArrayList<String> noteKeys;

    private static DatabaseReference reference;

    private FirebaseHelper() {
    }

    public static ArrayList<String> getNoteKeys() {
        if (noteKeys == null) {
            noteKeys = new ArrayList<>();
        }

        return noteKeys;
    }

    public static DatabaseReference getReference() {
        if (reference == null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);

            reference = db.getReference();
            reference.keepSynced(true);
        }

        return reference;
    }

    public static Query getQueryForNotes() {
        return getReference().child(NOTE);
    }

    public static Query getQueryForTodos() {
        return getReference().child(TODO);
    }

    public static void removeNote(int position) {
        String key = getNoteKeys().get(position);
        getReference().child("Note").child(key).removeValue();
    }
}
