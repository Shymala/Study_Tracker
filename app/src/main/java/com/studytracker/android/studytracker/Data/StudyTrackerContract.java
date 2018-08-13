package com.studytracker.android.studytracker.Data;

import android.provider.BaseColumns;

public class StudyTrackerContract {

    public static final class SubjectsEntry implements BaseColumns{

        public static final String TABLE_NAME = "Subjects";
        public static final String COLUMN_NAME = "name";

    }

    public static final class SubjectsDetailsEntry implements BaseColumns{

        public static final String TABLE_NAME = "Subjects_Details";
        public static final String COLUMN_SUBJECT_ID = "Subject_ID";
        public static final String COLUMN_START_TIME = "Start_TimeStamp";
        public static final String COLUMN_END_TIME = "End_TimeStamp";
        public static final String COLUMN_NOTES = "Notes";

    }
}
