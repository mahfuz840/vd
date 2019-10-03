package com.the_spartan.virtualdiary.data;

import android.provider.BaseColumns;

public class ToDoContract {
    private ToDoContract(){

    }

    public static final class ToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_SUBJECT= "Subject";
        public static final String COLUMN_DUE = "DueDate";
        public static final String COLUMN_TIME = "DueTime";
        public static final String COLUMN_PRIORITY = "Priority";
        public static final String COLUMN_ISDONE = "Done";
    }
}
