package com.the_spartan.virtualdiary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.the_spartan.virtualdiary.data.NoteContract.NoteEntry;

/**
 * Created by the_spartan on 3/17/18.
 */

public class NoteProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "com.the_spartan.virtualdiary";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTES = "notes";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int NOTES = 100;
    public static final int NOTES_ID = 101;

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NOTES, NOTES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NOTES + "/#", NOTES_ID);
    }

    NoteDbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new NoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case NOTES:
//                selection = NoteEntry.COLUMN_MONTH + "=?";
                cursor = db.query(NoteEntry.TABLE_NAME
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
                break;
            case NOTES_ID:
                selection = NoteEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(NoteEntry.TABLE_NAME
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

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Long date = values.getAsLong(NoteEntry.COLUMN_DATE);
        if (date == null){
            throw new IllegalArgumentException("Fucked up!");
        }

        String title = values.getAsString(NoteEntry.COLUMN_TITLE);
        if (date == null){
            throw new IllegalArgumentException("Fucked up!");
        }

        String description = values.getAsString(NoteEntry.COLUMN_DESCRIPTION);
        if (description == null){
            throw new IllegalArgumentException("Fucked up!");
        }

        int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return insertNotes(values);
                default:
                    throw new IllegalArgumentException("Fucked up!");
        }
    }

    private Uri insertNotes(ContentValues values){
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d("directory", db.getPath());

        long newRowId = db.insert(NoteEntry.TABLE_NAME, null, values);
        if (newRowId == -1){
            Log.v("INSERTION", "ERROR");
            return null;
        }
        return ContentUris.withAppendedId(NoteProvider.CONTENT_URI, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
            case NOTES_ID:
                selection = NoteEntry._ID + "=?";
                Log.v("ID"," " + ContentUris.parseId(uri));
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);

                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES_ID:
                Log.v("UPDATE", "DONE");
                selection = NoteEntry.COLUMN_ID + " = ? ";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateNote(values, selection, selectionArgs);
        }

        return 0;
    }

    private int updateNote(ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(NoteEntry.COLUMN_DATE)){
            String date = values.getAsString(NoteEntry.COLUMN_DATE);
            if (date == null){
                throw new IllegalArgumentException("Cannot update due to invalid date");
            }
        }

        if (values.containsKey(NoteEntry.COLUMN_TITLE)){
            String title = values.getAsString(NoteEntry.COLUMN_TITLE);
            if (title == null){
                throw new IllegalArgumentException("CAnnot update due to invalid title");
            }
        }

        if (values.containsKey(NoteEntry.COLUMN_DESCRIPTION)){
            String description = values.getAsString(NoteEntry.COLUMN_DESCRIPTION);
            if (description == null){
                throw new IllegalArgumentException("CAnnot update due to invalid desc");
            }
        }

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);
    }

}
