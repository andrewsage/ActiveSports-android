package com.xoverto.activeaberdeen;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.xoverto.activeaberdeen.activities.OpportunitiesActivity;
import com.xoverto.activeaberdeen.ui.OpportunityFeedFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrew on 08/05/15.
 */
public class OpportunityCursorAdapter extends CursorTreeAdapter {
    private final String TAG = getClass().getSimpleName().toString();
    private OpportunitiesActivity mActivity;
    public HashMap<String, View> childView = new HashMap<String, View>();
    private LayoutInflater mInflater;
    private String mSelection;
    private ArrayList<String> mSelectionArgs;

    /**
     * The columns we are interested in from the database
     */
    protected static final String[] BOOK_PROJECTION = new String[] {
            DataProvider.KEY_ID,
            DataProvider.KEY_OPPORTUNITY_START_TIME,
            DataProvider.KEY_OPPORTUNITY_NAME,
            DataProvider.KEY_OPPORTUNITY_VENUE_ID,
            DataProvider.KEY_OPPORTUNITY_END_TIME,
            DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK
    };

    // Please Note: Here cursor is not provided to avoid querying on main
    // thread.
    public OpportunityCursorAdapter(Cursor cursor, Context context, String selection, ArrayList<String> selectionArgs) {

        super(cursor, context);
        mActivity = (OpportunitiesActivity) context;
        mInflater = LayoutInflater.from(context);
        mSelection = selection;
        mSelectionArgs = selectionArgs;
    }

    @Override
    public View newGroupView(Context context, Cursor cursor,
                             boolean isExpanded, ViewGroup parent) {

        final View view = mInflater
                .inflate(R.layout.opportunity_list_section_item, parent, false);
        return view;
    }

    @Override
    public void bindGroupView(View view, Context context, Cursor cursor,
                              boolean isExpanded) {

        TextView dayName = (TextView) view.findViewById(R.id.separator);

        dayName.setText(cursor.getString(cursor
                .getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK)));
    }

    @Override
    public View newChildView(Context context, Cursor cursor,
                             boolean isLastChild, ViewGroup parent) {

        final View view = mInflater.inflate(R.layout.opportunity_list_item, parent, false);

        return view;
    }

    @Override
    public void bindChildView(View view, Context context, Cursor cursor,
                              boolean isLastChild) {

        TextView opportunityName = (TextView) view.findViewById(R.id.name);

        opportunityName.setText(cursor.getString(cursor
                .getColumnIndex(DataProvider.KEY_OPPORTUNITY_NAME)));

        TextView text = (TextView)view.findViewById(R.id.start_time);
        String day = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK));
        String startTime = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_START_TIME));
        String endTime = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_END_TIME));
        String label = "" + day
                + " " + startTime
                + "-" + endTime;
        text.setText(label);


        int venue_id = cursor.getInt(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_VENUE_ID));

        ContentResolver cr = view.getContext().getContentResolver();

        String w = DataProvider.KEY_VENUE_ID + " = '" + venue_id + "'";
        label = "unknown venue";

        Cursor query = cr.query(DataProvider.CONTENT_URI_VENUES, null, w, null, null);

        if(query.getCount() > 0) {
            query.moveToFirst();
            label = query.getString(query.getColumnIndex(DataProvider.KEY_NAME));
        }
        query.close();


        text = (TextView)view.findViewById(R.id.venue);
        text.setText(label);


    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Cursor itemCursor = getGroup(groupCursor.getPosition());

        ArrayList<String> theseSelectionArgs = new ArrayList<String>(mSelectionArgs);
        theseSelectionArgs.add(itemCursor.getString(itemCursor
                .getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK)));

        String[] selectionArgsArray = new String[theseSelectionArgs.size()];
        selectionArgsArray = theseSelectionArgs.toArray(selectionArgsArray);

        CursorLoader cursorLoader = new CursorLoader(mActivity,
                DataProvider.CONTENT_URI_OPPORTUNITIES,
                BOOK_PROJECTION,
                mSelection + " AND " + DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "=?",
                selectionArgsArray,
                DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "," + DataProvider.KEY_OPPORTUNITY_START_TIME);

        Cursor childCursor = null;

        try {
            childCursor = cursorLoader.loadInBackground();
            childCursor.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return childCursor;
    }

    static class ViewHolder {
        public TextView venueText;
        public TextView timeText;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // TODO: Improve peformance of opportunities display
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.opportunity_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.venueText = (TextView) rowView.findViewById(R.id.name);
            viewHolder.timeText = (TextView) rowView.findViewById(R.id.start_time);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = "Test name";
        holder.venueText.setText(s);


        return rowView;
    }
}
