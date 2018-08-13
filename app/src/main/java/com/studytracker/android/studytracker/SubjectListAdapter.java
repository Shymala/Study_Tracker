package com.studytracker.android.studytracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studytracker.android.studytracker.Data.StudyTrackerContract;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.SubjectsViewHolder> {

    private Context mContext;
    // COMPLETED (8) Add a new local variable mCount to store the count of items to be displayed in the recycler view
    private Cursor mCursor;

    // COMPLETED (9) Update the Adapter constructor to accept an integer for the count along with the context
    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public SubjectListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        // COMPLETED (10) Set the local mCount to be equal to count
        this.mCursor = cursor;
    }

    @Override
    public SubjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.subject_list, parent, false);
        return new SubjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectsViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        //call getString on the cursor to get the guest's name
        String name = mCursor.getString(mCursor.getColumnIndex(StudyTrackerContract.SubjectsEntry.COLUMN_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(StudyTrackerContract.SubjectsEntry._ID));

        Log.d(" Adapter ID","ID :"+id);

        holder.subjectNameTextView.setText(name);
        //holder.subjectNameTextView.setTag((Object)id);
        holder.itemView.setTag(id);



    }


    // COMPLETED (11) Modify the getItemCount to return the mCount value rather than 0
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    // COMPLETED (15) Create a new function called swapCursor that takes the new cursor and returns void
    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param newCursor the new cursor that will replace the existing one
     */
    public void swapCursor(Cursor newCursor) {
        // COMPLETED (16) Inside, check if the current cursor is not null, and close it if so
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        // COMPLETED (17) Update the local mCursor to be equal to  newCursor
        mCursor = newCursor;
        // COMPLETED (18) Check if the newCursor is not null, and call this.notifyDataSetChanged() if so
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class SubjectsViewHolder extends RecyclerView.ViewHolder {

        // Will display the subject name
        TextView subjectNameTextView;


        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *                 {@link SubjectListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public SubjectsViewHolder(View itemView) {
            super(itemView);
            subjectNameTextView = (TextView) itemView.findViewById(R.id.subject_name_text_view);
        }

    }
}