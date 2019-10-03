package com.the_spartan.virtualdiary.data;

import android.provider.BaseColumns;

/**
 * Created by the_spartan on 3/17/18.
 */

public final class NoteContract{
    private NoteContract(){

    }

    public static final class NoteEntry implements BaseColumns{
        public static final String TABLE_NAME = "note";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_YEAR = "year";
    }
}
