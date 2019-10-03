package com.the_spartan.virtualdiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todo.db";
    public static final int DATABASE_VERSION = 1;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE =
                "CREATE TABLE " + ToDoContract.ToDoEntry.TABLE_NAME + "("
                        + ToDoContract.ToDoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ToDoContract.ToDoEntry.COLUMN_SUBJECT + " TEXT NOT NULL, "
                        + ToDoContract.ToDoEntry.COLUMN_DUE + " TEXT, "
                        + ToDoContract.ToDoEntry.COLUMN_TIME + " TEXT, "
                        + ToDoContract.ToDoEntry.COLUMN_PRIORITY + " INTEGER, "
                        + ToDoContract.ToDoEntry.COLUMN_ISDONE + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
