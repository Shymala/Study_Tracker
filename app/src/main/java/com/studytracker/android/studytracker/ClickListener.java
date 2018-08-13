package com.studytracker.android.studytracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;

interface ClickListener {
    public void onClick(View view, int position);
    public void onLongClick(View view, int position);
}

