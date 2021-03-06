package com.xoverto.activeaberdeen.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.DataUpdateService;
import com.xoverto.activeaberdeen.OpportunityCursorAdapter;
import com.xoverto.activeaberdeen.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class OpportunityFeedFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = OpportunityFeedFragment.class.getName();
    public static final String SEARCH_NAME ="search_name";
    public static final String SEARCH_DAY = "search_day";
    public static final String SEARCH_VENUE = "search_venue";
    public static final String SEARCH_TAGS = "search_tags";
    public static final String LIST_TITLE = "list_title";

    private OnFragmentInteractionListener mListener;
    private OpportunityCursorAdapter mCursorAdapter;

    /**
     * The fragment's ListView/GridView.
     */
    private ExpandableListView mListView;

    public static OpportunityFeedFragment newInstance(String title, String name, String day, String venueID, ArrayList<String> tags) {
        OpportunityFeedFragment fragment = new OpportunityFeedFragment();
        Bundle args = new Bundle();
        args.putString(LIST_TITLE, title);
        args.putString(SEARCH_NAME, name);
        args.putString(SEARCH_DAY, day);
        args.putString(SEARCH_VENUE, venueID);
        args.putStringArrayList(SEARCH_TAGS, tags);

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpportunityFeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // fragment_opportunity actually points to fragment_opportunity_list
        View view = inflater.inflate(R.layout.fragment_opportunity, container, false);

        // Set the adapter
        // The layout resource is for the actual table cell if this was iOS
        //mCursorAdapter = new OpportunityCursorAdapter(null, getActivity());
        /*
        mCursorAdapter = new OpportunityCursorAdapter(getActivity(),
                R.layout.opportunity_list_item, // Group layout
                R.layout.opportunity_list_item, // Child layout
                new String[] {  // Group from
                        DataProvider.KEY_OPPORTUNITY_START_TIME,
                        DataProvider.KEY_OPPORTUNITY_NAME,
                        DataProvider.KEY_OPPORTUNITY_VENUE_ID,
                        DataProvider.KEY_OPPORTUNITY_START_TIME,
                },
                new int[] { R.id.separator, R.id.name, R.id.venue, R.id.start_time }, // Group to
                new String[] {  // Child from
                        DataProvider.KEY_OPPORTUNITY_START_TIME,
                        DataProvider.KEY_OPPORTUNITY_NAME,
                        DataProvider.KEY_OPPORTUNITY_VENUE_ID,
                        DataProvider.KEY_OPPORTUNITY_START_TIME,
                },
                new int[] { R.id.separator, R.id.name, R.id.venue, R.id.start_time } // Child to
        );
        */

        /*
        mCursorAdapter.setViewBinder(new OpportunityCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int column) {

                boolean needSeparator = false;

                if (view.getId() == R.id.separator) {
                    TextView separator = (TextView)view.findViewById(R.id.separator);

                    String label = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK));


                    if(needSeparator) {
                        separator.setText(label);
                        separator.setVisibility(View.VISIBLE);
                    } else {
                        separator.setVisibility(View.GONE);
                    }
                }

                if (view.getId() == R.id.start_time) {
                    TextView text = (TextView)view.findViewById(R.id.start_time);
                    String label = "" + cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK))
                            + " " + cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_START_TIME))
                            + "-" + cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_END_TIME));
                    text.setText(label);

                    return true;
                }

                if (view.getId() == R.id.venue) {
                    int venue_id = cursor.getInt(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_VENUE_ID));

                    ContentResolver cr = view.getContext().getContentResolver();

                    // Construct a where clause to make sure we don't already have this carpark in the provider
                    String w = DataProvider.KEY_VENUE_ID + " = '" + venue_id + "'";
                    String label = "unknown venue";

                    // If the carpark is new, insert it into the provider
                    Cursor query = cr.query(DataProvider.CONTENT_URI_VENUES, null, w, null, null);

                    if(query.getCount() > 0) {
                        query.moveToFirst();
                        label = query.getString(query.getColumnIndex(DataProvider.KEY_NAME));
                    }
                    query.close();


                    TextView text = (TextView)view.findViewById(R.id.venue);
                    text.setText(label);

                    return true;
                }


                return false;
            }
        });
        */

        mListView = (ExpandableListView) view.findViewById(android.R.id.list);
        mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String name = getArguments().getString(SEARCH_NAME);
        String day = getArguments().getString(SEARCH_DAY);
        String venueId = getArguments().getString(SEARCH_VENUE);
        ArrayList<String> tags = getArguments().getStringArrayList(SEARCH_TAGS);

        Log.w("TAG", "onCreateLoader");


        // Construct a where clause to filter the opportunities
        String w = "";
        ArrayList<String> selectionArgs = new ArrayList<String>();

        if(name != null) {
            if(name.isEmpty() == false) {
                w = w + "(" + DataProvider.KEY_OPPORTUNITY_NAME + " like ? OR " + DataProvider.KEY_OPPORTUNITY_DESCRIPTION + " like ? )";
                selectionArgs.add("%" + name + "%");
                selectionArgs.add("%" + name + "%");
            }
        }

        if(day != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "=?";
            selectionArgs.add(day);
        }

        if(venueId != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_VENUE_ID + "=?";
            selectionArgs.add(venueId);
        }

        if(tags != null) {
            if(tags.size() > 0) {
                if (selectionArgs.size() > 0) {
                    w = w + " AND ";
                }
                w = w + "(";
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.get(i);
                    if (i > 0) {
                        w = w + " OR ";
                    }
                    w = w + DataProvider.KEY_OPPORTUNITY_TAGS + " like ? ";
                    selectionArgs.add("%" + tag + "%");
                }
                w = w + ")";
            }
        }

        mCursorAdapter = new OpportunityCursorAdapter(null, getActivity(), w, selectionArgs);
        mListView.setAdapter(mCursorAdapter);

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        getLoaderManager().initLoader(0, null, this);

        refreshVenues();
    }


    OnDataPass dataPasser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPasser = (OnDataPass) activity;

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /*
        Cursor cursor = (Cursor)mCursorAdapter.getItem(position);
        String value = Integer.toString(cursor.getInt(cursor.getColumnIndex(DataProvider.KEY_ID)));

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(value);
        }
        */
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String message);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String name = getArguments().getString(SEARCH_NAME);
        String day = getArguments().getString(SEARCH_DAY);
        String venueId = getArguments().getString(SEARCH_VENUE);
        ArrayList<String> tags = getArguments().getStringArrayList(SEARCH_TAGS);

        Log.w("TAG", "onCreateLoader");


        // Construct a where clause to filter the opportunities
        String w = "";
        ArrayList<String> selectionArgs = new ArrayList<String>();

        if(name != null) {
            if(name.isEmpty() == false) {
                w = w + "(" + DataProvider.KEY_OPPORTUNITY_NAME + " like ? OR " + DataProvider.KEY_OPPORTUNITY_DESCRIPTION + " like ? )";
                selectionArgs.add("%" + name + "%");
                selectionArgs.add("%" + name + "%");
            }
        }

        if(day != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "=?";
            selectionArgs.add(day);
        }

        if(venueId != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_VENUE_ID + "=?";
            selectionArgs.add(venueId);
        }

        if(tags != null) {
            if(tags.size() > 0) {
                if (selectionArgs.size() > 0) {
                    w = w + " AND ";
                }
                w = w + "(";
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.get(i);
                    if (i > 0) {
                        w = w + " OR ";
                    }
                    w = w + DataProvider.KEY_OPPORTUNITY_TAGS + " like ? ";
                    selectionArgs.add("%" + tag + "%");
                }
                w = w + ")";
            }
        }

        String[] selectionArgsArray = new String[selectionArgs.size()];
        selectionArgsArray = selectionArgs.toArray(selectionArgsArray);

        String[] projection = {
                DataProvider.KEY_ID,
                DataProvider.KEY_OPPORTUNITY_NAME,
                DataProvider.KEY_OPPORTUNITY_VENUE_ID,
                DataProvider.KEY_OPPORTUNITY_ACTIVITY_ID,
                DataProvider.KEY_OPPORTUNITY_SUB_ACTIVITY_ID,
                DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK,
                DataProvider.KEY_OPPORTUNITY_START_TIME,
                DataProvider.KEY_OPPORTUNITY_END_TIME,
                DataProvider.KEY_OPPORTUNITY_DESCRIPTION
        };
        String [] projectionDays = {
                DataProvider.KEY_ID,
                DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK
        };

        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_OPPORTUNITIES_DISTINCT,
                projectionDays, w, selectionArgsArray, DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "," + DataProvider.KEY_OPPORTUNITY_START_TIME);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Bundle args = getArguments();
        String title = args.getString(LIST_TITLE);
        int opportunitiesCount = 0;

        ContentResolver cr = getActivity().getContentResolver();

        String name = getArguments().getString(SEARCH_NAME);
        String day = getArguments().getString(SEARCH_DAY);
        String venueId = getArguments().getString(SEARCH_VENUE);
        ArrayList<String> tags = getArguments().getStringArrayList(SEARCH_TAGS);

        // Construct a where clause to filter the opportunities
        String w = "";
        ArrayList<String> selectionArgs = new ArrayList<String>();

        if(name != null) {
            if(name.isEmpty() == false) {
                w = w + "(" + DataProvider.KEY_OPPORTUNITY_NAME + " like ? OR " + DataProvider.KEY_OPPORTUNITY_DESCRIPTION + " like ? )";
                selectionArgs.add("%" + name + "%");
                selectionArgs.add("%" + name + "%");
            }
        }

        if(day != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "=?";
            selectionArgs.add(day);
        }

        if(venueId != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_VENUE_ID + "=?";
            selectionArgs.add(venueId);
        }

        if(tags != null) {
            if(tags.size() > 0) {
                if (selectionArgs.size() > 0) {
                    w = w + " AND ";
                }
                w = w + "(";
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.get(i);
                    if (i > 0) {
                        w = w + " OR ";
                    }
                    w = w + DataProvider.KEY_OPPORTUNITY_TAGS + " like ? ";
                    selectionArgs.add("%" + tag + "%");
                }
                w = w + ")";
            }
        }

        String[] selectionArgsArray = new String[selectionArgs.size()];
        selectionArgsArray = selectionArgs.toArray(selectionArgsArray);

        String[] projection = {
                DataProvider.KEY_ID,
                DataProvider.KEY_OPPORTUNITY_NAME,
                DataProvider.KEY_OPPORTUNITY_VENUE_ID,
                DataProvider.KEY_OPPORTUNITY_ACTIVITY_ID,
                DataProvider.KEY_OPPORTUNITY_SUB_ACTIVITY_ID,
                DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK,
                DataProvider.KEY_OPPORTUNITY_START_TIME,
                DataProvider.KEY_OPPORTUNITY_END_TIME,
                DataProvider.KEY_OPPORTUNITY_DESCRIPTION
        };


        Cursor query = cr.query(DataProvider.CONTENT_URI_OPPORTUNITIES, null, w, selectionArgsArray, null);

        opportunitiesCount = query.getCount();
        query.close();

        passData(opportunitiesCount, title);

        // Swap the new cursor in.
        int id = loader.getId();

        if (id == 0) {

            mCursorAdapter.setGroupCursor(cursor);

            //expandAllChild();
        }
    }

    private void expandAllChild() {

        for (int i = 0; i < mCursorAdapter.getGroupCount(); i++) {
            mListView.expandGroup(i);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // is about to be closed.
        int id = loader.getId();
        if (id != 0) {
            // child cursor
            try {
                mCursorAdapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
                Log.w("TAG", "Adapter expired, try again on the next query: "
                        + e.getMessage());
            }
        } else {
            mCursorAdapter.setGroupCursor(null);
        }
    }
    public void refreshVenues() {

        getLoaderManager().restartLoader(0, null, OpportunityFeedFragment.this);
        getActivity().startService(new Intent(getActivity(), DataUpdateService.class));
    }

    public void passData(int activities, String title) {
        dataPasser.onDataPass(activities, title);
    }

    public interface OnDataPass {
        public void onDataPass(int activities, String title);
    }
}
