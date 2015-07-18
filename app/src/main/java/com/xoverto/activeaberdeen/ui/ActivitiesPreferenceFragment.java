package com.xoverto.activeaberdeen.ui;


import android.app.Activity;
import android.app.LoaderManager;
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

import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.DataUpdateService;
import com.xoverto.activeaberdeen.R;

public class ActivitiesPreferenceFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "PREFERENCES";

    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter mCursorAdapter;
    private AbsListView mListView;
    private ListAdapter mAdapter;

    public ActivitiesPreferenceFragment() {
         }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preference_list, container, false);

        // Set the adapter
        mCursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.preference_list_item,
                null,
                new String[] {
                        DataProvider.KEY_ACTIVITY_TITLE
                },
                new int[] { R.id.name}, 0);

        mListView = (AbsListView) rootView.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mCursorAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        refreshPreferences();
        return rootView;
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
        String value = Integer.toString(cursor.getInt(cursor.getColumnIndex(DataProvider.KEY_ID)));

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(value);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String value);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DataProvider.KEY_ID,
                DataProvider.KEY_ACTIVITY_TITLE
        };
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_ACTIVITIES,
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
    public void refreshPreferences() {

        getLoaderManager().restartLoader(0, null, ActivitiesPreferenceFragment.this);
        getActivity().startService(new Intent(getActivity(), DataUpdateService.class));

    }
}