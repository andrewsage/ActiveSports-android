package com.xoverto.activeaberdeen;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class OpportunityFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "OPPORTUNITIES";

    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter mCursorAdapter;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static OpportunityFragment newInstance(String param1, String param2) {
        OpportunityFragment fragment = new OpportunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpportunityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue, container, false);

        // Set the adapter
        mCursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.opportunity_list_item,
                null,
                new String[] { DataProvider.KEY_OPPORTUNITY_NAME,
                        DataProvider.KEY_OPPORTUNITY_VENUE_ID,
                        DataProvider.KEY_OPPORTUNITY_START_TIME
                },
                new int[] { R.id.name, R.id.venue, R.id.start_time }, 0);


        mCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int column) {

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

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mCursorAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        refreshVenues();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        Cursor cursor = (Cursor)mCursorAdapter.getItem(position);
        String value = cursor.getString(cursor
                .getColumnIndex(DataProvider.KEY_OPPORTUNITY_NAME));


        Toast.makeText(parent.getContext(), value, Toast.LENGTH_SHORT).show();
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(value);
        }
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
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_OPPORTUNITIES,
                projection, null, null, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    public void refreshVenues() {

        getLoaderManager().restartLoader(0, null, OpportunityFragment.this);
        getActivity().startService(new Intent(getActivity(), DataUpdateService.class));

    }
}
