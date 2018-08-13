package com.studytracker.android.studytracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.studytracker.android.studytracker.Data.StudyTrackerContract;
import com.studytracker.android.studytracker.Data.StudyTrackerDbHelper;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Properties;

public class DailyStudyReport extends AppCompatActivity {

    // Create a local field SQLiteDatabase called mDb
    private SQLiteDatabase mDb;

    private String[] subjectNames;
    private String[] studyTimes;
    private String[] subID;
    private Properties studyTimesProps = new Properties();
    private Properties subNameProps = new Properties();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_study_report);

        Toolbar toolbar = findViewById(R.id.daily_report_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        StudyTrackerDbHelper dbHelper = new StudyTrackerDbHelper(this);

        // COMPLETED (3) Get a writable database reference using getWritableDatabase and store it in mDb
        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();
        getSubjectNames();
        getStudyTimes();


    }

    private void getSubjectNames() {



        String[] columnNames = {StudyTrackerContract.SubjectsEntry._ID ,StudyTrackerContract.SubjectsEntry.COLUMN_NAME};
        Cursor subNameCursor = mDb.query(
                StudyTrackerContract.SubjectsEntry.TABLE_NAME,
                columnNames,
                null,
                null,
                null,
                null,
                StudyTrackerContract.SubjectsEntry.COLUMN_NAME
        );

        int i=0;
        subID = new String[subNameCursor.getCount()];
        while (subNameCursor.moveToNext()){
            //subjectNames[i] = subNameCursor.getString(i);

            String sName = subNameCursor.getString(subNameCursor.getColumnIndex("name"));

            Long id = subNameCursor.getLong(subNameCursor.getColumnIndex("_id"));
            subID[i] = id.toString();

            subNameProps.setProperty(id.toString(), sName);
            i = i+1;
        }


    }

    private void getStudyTimes(){

        String[] columnNames = {StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID ,
                "sum( round(((julianday("+StudyTrackerContract.SubjectsDetailsEntry.COLUMN_END_TIME+") - julianday("+StudyTrackerContract.SubjectsDetailsEntry.COLUMN_START_TIME+")) * 24),2) ) as duration"};
        Cursor studyTimesCursor = mDb.query(
                StudyTrackerContract.SubjectsDetailsEntry.TABLE_NAME,
                columnNames,
                null,
                null,
                StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID,
                null,
                StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID
        );

       while(studyTimesCursor.moveToNext()) {
            //subjectNames[i] = subNameCursor.getString(i);
            Long id = studyTimesCursor.getLong(studyTimesCursor.getColumnIndex("Subject_ID") );
            Long time = studyTimesCursor.getLong(studyTimesCursor.getColumnIndex("duration"));
            studyTimesProps.setProperty(id.toString(), time.toString());
        }

    }

    public void openChart(View view){
        buildChart();
    }

    private void buildChart(){



        XYSeries timeSeries = new XYSeries("Time(in hours)");

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Subjects vs Hour of Study Chart");
        multiRenderer.setXTitle("Subject Name");
        multiRenderer.setYTitle("Time in hours");
        multiRenderer.setZoomButtonsVisible(true);

        for(int i=0; i< subID.length;i++){

            String mSub = subNameProps.getProperty(subID[i]);
            Double mtime = Double.parseDouble(studyTimesProps.getProperty( subID[i]));

            timeSeries.add(i,mtime);
            multiRenderer.addXTextLabel(i, mSub);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(timeSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer timeRenderer = new XYSeriesRenderer();
        timeRenderer.setColor(Color.rgb(130, 130, 230));
        timeRenderer.setFillPoints(true);
        timeRenderer.setLineWidth(2);
        timeRenderer.setDisplayChartValues(true);

        multiRenderer.addSeriesRenderer(timeRenderer);

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Start Activity
        startActivity(intent);
    }
}
