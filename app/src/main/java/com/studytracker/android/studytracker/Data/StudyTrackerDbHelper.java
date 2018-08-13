package com.studytracker.android.studytracker.Data;

import com.studytracker.android.studytracker.Data.StudyTrackerContract.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudyTrackerDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "studytracker.db";

    private static final int DATABASE_VERSION = 2;

    public StudyTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // COMPLETED (6) Inside, create an String query called SQL_CREATE_WAITLIST_TABLE that will create the table
        // Create a table to hold waitlist data
        final String SQL_CREATE_SUBJECTS_TABLE = "CREATE TABLE " + SubjectsEntry.TABLE_NAME + " (" +
                SubjectsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubjectsEntry.COLUMN_NAME + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_SUBJECTS_DETAILS_TABLE = "CREATE TABLE " + SubjectsDetailsEntry.TABLE_NAME + " (" +
                SubjectsDetailsEntry.COLUMN_SUBJECT_ID + " INTEGER NOT NULL, " +
                SubjectsDetailsEntry.COLUMN_START_TIME + " TIMESTAMP , " +
                SubjectsDetailsEntry.COLUMN_END_TIME + " TIMESTAMP, " +
                SubjectsDetailsEntry.COLUMN_NOTES + " TEXT, " +
                " FOREIGN KEY ("+SubjectsDetailsEntry.COLUMN_SUBJECT_ID+") REFERENCES "+SubjectsEntry.TABLE_NAME +"("+SubjectsEntry._ID+")" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_SUBJECTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBJECTS_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        // COMPLETED (9) Inside, execute a drop table query, and then call onCreate to re-create it
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubjectsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubjectsDetailsEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

}
