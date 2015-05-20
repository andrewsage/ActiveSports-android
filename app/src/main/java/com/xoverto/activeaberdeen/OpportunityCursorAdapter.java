package com.xoverto.activeaberdeen;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by andrew on 08/05/15.
 */
public class OpportunityCursorAdapter extends SimpleCursorAdapter {
    public OpportunityCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
}
