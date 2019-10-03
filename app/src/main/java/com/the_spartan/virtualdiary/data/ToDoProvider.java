package com.the_spartan.virtualdiary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class ToDoProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "to.do.authority";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODO = "todos";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODO);

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int TODO = 200;
    public static final int TODO_ID = 201;
    public static final int DELETE_A_TODO = 300;
    public static final int DELETE_MULTIPLE_TODO = 301;

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODO, TODO);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODO, TODO_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODO + "/#", DELETE_A_TODO);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODO + "/#", DELETE_MULTIPLE_TODO);
    }

    ToDoDbHelper helper;

    @Override
    public boolean onCreate() {

        helper = new ToDoDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TODO:
                cursor = db.rawQuery("select * from todo", null);
                break;
            case TODO_ID:
                selection = ToDoContract.ToDoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ToDoContract.ToDoEntry.TABLE_NAME
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query on the database");
        }

        return cursor;

    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return insertToDo(uri, values);
    }

    private Uri insertToDo(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();

        long newRowId = db.insert(ToDoContract.ToDoEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Log.d("INSERT TODO", "ERROR");
            return null;
        }

        return ContentUris.withAppendedId(CONTENT_URI, newRowId);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.d("DELETE", "A SINGLE ITEM ");
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(ToDoContract.ToDoEntry.TABLE_NAME, ToDoContract.ToDoEntry.COLUMN_ID + "=?", selectionArgs);
        db.close();


        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(ToDoContract.ToDoEntry.TABLE_NAME, values, selection, selectionArgs);

        Log.d("update", "updated");

        return 0;
    }
}
