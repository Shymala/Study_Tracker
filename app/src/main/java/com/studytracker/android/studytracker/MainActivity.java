package com.studytracker.android.studytracker;

//import android.app.AlertDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.studytracker.android.studytracker.Data.StudyTrackerContract;
import com.studytracker.android.studytracker.Data.StudyTrackerDbHelper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity {

    private SubjectListAdapter mAdapter;

    // Create a local field SQLiteDatabase called mDb
    private SQLiteDatabase mDb;

    //Navigation Drawer
    private DrawerLayout mDrawerLayout;

   // private EditText mNewSubjectNameEditText;

    private String mSubjectName = "";

    private boolean studyInProgress = false;

    private  long studyingSubjectID ;
    private String studyStartTime = "";
    private String studyingSubName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // if(savedInstanceState == null) {
            /*SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
*/
       // }
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24px);


            // mNewSubjectNameEditText = (EditText) this.findViewById(R.id.subject_name_edit_text);

            RecyclerView subjectListRecyclerView;

            // Set local attributes to corresponding views
            subjectListRecyclerView = (RecyclerView) this.findViewById(R.id.all_subjects_list_view);

            // Set layout for the RecyclerView, because it's a list we are using the linear layout
            subjectListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // COMPLETED (2) Create a StudyTrackerDbHelper instance, pass "this" to the constructor
            // Create a DB helper (this will create the DB if run for the first time)
            StudyTrackerDbHelper dbHelper = new StudyTrackerDbHelper(this);

            // COMPLETED (3) Get a writable database reference using getWritableDatabase and store it in mDb
            // Keep a reference to the mDb until paused or killed. Get a writable database
            // because you will be adding restaurant customers
            mDb = dbHelper.getWritableDatabase();
            // COMPLETED (7) Run the getAllGuests function and store the result in a Cursor variable
            Cursor cursor = getAllSubjects();

            // COMPLETED (12) Pass the resulting cursor count to the adapter
            // Create an adapter for that cursor to display the data
            mAdapter = new SubjectListAdapter(this, cursor);

            // Link the adapter to the RecyclerView
            subjectListRecyclerView.setAdapter(mAdapter);

            //set the item touch listener

            subjectListRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                    subjectListRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    //Values are passing to activity & to fragment as well

                }

                @Override
                public void onLongClick(View view, int position) {

                    if (!studyInProgress) {
                        studyStateDialog(view);
                    } else {
                        TextView t1 = (TextView) view.findViewById(R.id.subject_name_text_view);
                        String currentSubName = t1.getText().toString();
                        if (!studyingSubName.equals(currentSubName)) {
                            Toast.makeText(MainActivity.this, "Hope you finished studying " + studyingSubName + "\n Please stop it before starting " + currentSubName,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            stopStudying(view);
                        }


                    }
                    //long subject_id = (long) view.getTag();


                }
            }));

            mDrawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            menuItem.setChecked(true);

                            switch (menuItem.getItemId()) {
                                case R.id.daily_report:
                                    startActivity(new Intent(MainActivity.this, DailyStudyReport.class));
                            }
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here

                            return true;
                        }
                    });

    }

    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     * @param view The calling view (button)
     */
   /* public void addToSubjectlist(View view) {
        // COMPLETED (9) First thing, check if any of the EditTexts are empty, return if so
        if (mNewSubjectNameEditText.getText().length() == 0 ) {
            return;
        }

        // COMPLETED (14) call addNewGuest with the guest name and party size
        // Add guest info to mDb
        addNewSubject(mNewSubjectNameEditText.getText().toString());

        // COMPLETED (19) call mAdapter.swapCursor to update the cursor by passing in getAllGuests()
        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllSubjects());

        // COMPLETED (20) To make the UI look nice, call .getText().clear() on both EditTexts, also call clearFocus() on mNewPartySizeEditText
        //clear UI text fields
        mNewSubjectNameEditText.clearFocus();
        mNewSubjectNameEditText.getText().clear();


    }*/


    // COMPLETED (5) Create a private method called getAllGuests that returns a cursor
    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllSubjects() {
        // COMPLETED (6) Inside, call query on mDb passing in the table name and projection String [] order by COLUMN_TIMESTAMP

        String[] columnNames = {StudyTrackerContract.SubjectsEntry._ID , StudyTrackerContract.SubjectsEntry.COLUMN_NAME};
        return mDb.query(
                StudyTrackerContract.SubjectsEntry.TABLE_NAME,
                columnNames,
                null,
                null,
                null,
                null,
                StudyTrackerContract.SubjectsEntry.COLUMN_NAME
        );
    }

    /**
     * Adds a new guest to the mDb including the party count and the current timestamp
     *
     * @param name  Guest's name
     * @return id of new record added
     */
    private long addNewSubject(String name) {
        // COMPLETED (5) Inside, create a ContentValues instance to pass the values onto the insert query
        ContentValues cv = new ContentValues();
        // COMPLETED (6) call put to insert the name value with the key COLUMN_GUEST_NAME
        cv.put(StudyTrackerContract.SubjectsEntry.COLUMN_NAME, name);

        // COMPLETED (8) call insert to run an insert query on TABLE_NAME with the ContentValues created
        return mDb.insert(StudyTrackerContract.SubjectsEntry.TABLE_NAME, null, cv);
    }


    //Add button functionalities


    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     *
     */
    public void addToSubjectlistFromDialog() {

        Log.d("deb 2 : mSubject ",mSubjectName );
        // COMPLETED (9) First thing, check if any of the EditTexts are empty, return if so
        if (mSubjectName == "" ) {
            return;
        }

        // COMPLETED (14) call addNewGuest with the guest name and party size
        // Add guest info to mDb
        Log.d("deb 2 : mSubject ",mSubjectName );
        addNewSubject(mSubjectName);

        // COMPLETED (19) call mAdapter.swapCursor to update the cursor by passing in getAllGuests()
        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllSubjects());

        // COMPLETED (20) To make the UI look nice, call .getText().clear() on both EditTexts, also call clearFocus() on mNewPartySizeEditText
        //clear UI text fields
        //mNewSubjectNameEditText.clearFocus();
        //mNewSubjectNameEditText.getText().clear();


    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addsubjectmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        mSubjectName = "";

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add_button:
                showDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    // Dialog box for adding the subject activities

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View inf =  inflater.inflate(R.layout.subject_name_dialog, null);
        builder.setView(inf);

        final EditText et1 = (EditText) inf.findViewById(R.id.subject_name_dialog_edit_text);

        builder.setPositiveButton(R.string.add_button_text, new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int id) {
                mSubjectName = et1.getText().toString();
                Log.d("deb 1 : mSubject ",mSubjectName );
                addToSubjectlistFromDialog();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    // Code for adding touch listener for subject name

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    ///RecyclerView.ViewHolder viewHolder = recycleView.findViewHolderForItemId(R.id.subject_name_text_view);
                    //Log.d("Click Listner"," view holder: "+viewHolder);
                    Log.d("Click Listner"," view tag: "+child.getTag());
                    //Log.d("Click Listner"," view id: "+child.getId());
                    if( child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

// code for displaying dialog when the subject name is clicked.

    private long insertStartStudyTime(long id){

        ContentValues cv1 = new ContentValues();
        studyStartTime = getDateTime();
        // COMPLETED (6) call put to insert the name value with the key COLUMN_GUEST_NAME
        cv1.put(StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID, id);
        cv1.put(StudyTrackerContract.SubjectsDetailsEntry.COLUMN_START_TIME, studyStartTime);
        //cv1.put(StudyTrackerContract.SubjectsDetailsEntry.COLUMN_END_TIME, NULL);



        Log.d("deb insert study time","insertStudyTime: id "+id + "date "+ getDateTime());

        // COMPLETED (8) call insert to run an insert query on TABLE_NAME with the ContentValues created
        return mDb.insert(StudyTrackerContract.SubjectsDetailsEntry.TABLE_NAME, null, cv1);


    }

    private long insertEndStudyTime(long subject_id){
        ContentValues cv = new ContentValues();
        String studyEndTime = getDateTime();
        // COMPLETED (6) call put to insert the name value with the key COLUMN_GUEST_NAME
        //cv.put(StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID, id);
        cv.put(StudyTrackerContract.SubjectsDetailsEntry.COLUMN_END_TIME, studyEndTime);

        Log.d("insertEndStudyTime","Inside insertEndStudyTime:");

        Log.d("insertEndStudyTime","getDateTime: "+studyEndTime+" subject_id "+subject_id+" studyStartTime "+studyStartTime);
       try {
           long dateDiff = getDateDifference(studyEndTime, studyStartTime);
           Toast.makeText(MainActivity.this, "Congratulations!! you have studied "+dateDiff+" minutes",
                   Toast.LENGTH_LONG).show();

       }
       catch(Exception e)
       {
           Log.e("Error","Error during time difference calculation");
       }

        //Log.d("insertEndStudyTime","Inside insertEndStudyTime time diff:"+ ts1-ts2);
       return mDb.update(StudyTrackerContract.SubjectsDetailsEntry.TABLE_NAME,cv,
                StudyTrackerContract.SubjectsDetailsEntry.COLUMN_SUBJECT_ID +" = "+subject_id+" AND DATETIME("+
                        StudyTrackerContract.SubjectsDetailsEntry.COLUMN_START_TIME+") = DATETIME('"+studyStartTime+"')",
                null);

      //return 1;


    }

    private void studyStateDialog(final View  view) {


        Log.d("ID","Inside studyStateDialog");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        //LayoutInflater inflater = this.getLayoutInflater();
        TextView t1 = (TextView) view.findViewById(R.id.subject_name_text_view);
        studyingSubName = t1.getText().toString();

        String msg = getString(R.string.start_study);

        builder.setMessage(msg+" "+studyingSubName);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //View inf =  inflater.inflate(R.layout.subject_name_dialog, null);
        //builder.setView(inf);

        //final EditText et1 = (EditText) inf.findViewById(R.id.subject_name_dialog_edit_text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int id) {

                studyInProgress = true;
                //mSubjectName = et1.getText().toString();
                //Log.d("deb 1 : mSubject ",mSubjectName );
                //addToSubjectlistFromDialog();


                //view.itemView.setBackgroundColor(Color.parseColor("#8BC34A"));
                studyingSubjectID = (long) view.getTag();

                TextView t1 = (TextView) view.findViewById(R.id.subject_name_text_view);
                studyingSubName = t1.getText().toString();

                view.setBackgroundColor(Color.parseColor("#8BC34A"));
                insertStartStudyTime(studyingSubjectID);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private  void stopStudying(final View view){


        Log.d("ID","Inside stopStudying");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        //LayoutInflater inflater = this.getLayoutInflater();

        builder.setMessage(R.string.stop_study );//+" "+studyingSubName);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int id) {

                studyInProgress = false;
                Log.d("stopStudying","studyingSubjectID: "+studyingSubjectID);
                view.setBackgroundColor(Color.TRANSPARENT);
               long res = insertEndStudyTime(studyingSubjectID);
                Log.d("stopStudying","res: "+res);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private long getDateDifference(String dateString1 , String dateString2) throws ParseException {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date1 = (Date)format.parse(dateString1);
        Date date2 = (Date)format.parse(dateString2);

        long diffInMillies = date1.getTime() - date2.getTime();
        long diffMinutes = diffInMillies / (60 * 1000) % 60;
        return diffMinutes;


    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("studyingSubName", studyingSubName); // value to store
        editor.putString("studyStartTime", studyStartTime); // value to store
        editor.putLong("studyingSubjectID", studyingSubjectID); // value to store
        editor.putBoolean("studyInProgress", studyInProgress);

        // Commit to storage
        editor.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        studyingSubName = preferences.getString("studyingSubName", "");
        studyStartTime = preferences.getString("studyStartTime", "");
        studyingSubjectID = preferences.getLong("studyingSubjectID",-1);
        studyInProgress = preferences.getBoolean("studyInProgress",false);



    }

    }
