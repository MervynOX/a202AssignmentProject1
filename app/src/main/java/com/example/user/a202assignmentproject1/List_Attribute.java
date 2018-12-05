package com.example.user.a202assignmentproject1;

import android.provider.BaseColumns;

public class List_Attribute {

    private List_Attribute() {
    }

    public static final class ListEntry implements BaseColumns {//attributes of the database
        public static final String TABLE_NAME = "List";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}