package com.the_spartan.virtualdiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.the_spartan.virtualdiary.data.NoteContract.NoteEntry;

/**
 * Created by the_spartan on 3/17/18.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "note.db";
    public static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE =
                "CREATE TABLE " + NoteEntry.TABLE_NAME + "("
                + NoteEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.COLUMN_DATE + " INTEGER NOT NULL, "
                + NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + NoteEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + NoteEntry.COLUMN_MONTH + " INTEGER NOT NULL, "
                + NoteEntry.COLUMN_YEAR + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
