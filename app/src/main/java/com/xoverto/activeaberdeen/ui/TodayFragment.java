package com.xoverto.activeaberdeen.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.activities.OpportunitiesActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andrew on 14/04/15.
 */
public class TodayFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button mResultsButton;
    ToggleButton mStrengthButton;
    ToggleButton mCardioButton;
    ToggleButton mWeightlossButton;
    ToggleButton mFlexibilityButton;
    ArrayList<String> mTagsArray;

    public static FragmentManager fgmanger;

    public TodayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        fgmanger  = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fgmanger.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mResultsButton = (Button) rootView.findViewById(R.id.results_button);
        mStrengthButton = (ToggleButton) rootView.findViewById(R.id.toggle_strength);
        mCardioButton = (ToggleButton) rootView.findViewById(R.id.toggle_cardio);
        mWeightlossButton = (ToggleButton) rootView.findViewById(R.id.toggle_weightloss);
        mFlexibilityButton = (ToggleButton) rootView.findViewById(R.id.toggle_flexibility);

        mTagsArray = new ArrayList<String>();

        mStrengthButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("strength");
                } else {
                    mTagsArray.remove("strength");
                }
            }
        });

        mCardioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("cardio");
                } else {
                    mTagsArray.remove("cardio");
                }
            }
        });

        mWeightlossButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("weightloss");
                } else {
                    mTagsArray.remove("weightloss");
                }
            }
        });

        mFlexibilityButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("flexibility");
                } else {
                    mTagsArray.remove("flexibility");
                }
            }
        });

        mStrengthButton.setChecked(true);
        mCardioButton.setChecked(true);
        mWeightlossButton.setChecked(true);
        mFlexibilityButton.setChecked(true);

        mResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String todayName = sdf.format(c.getTime());

                Intent intent = new Intent(getActivity(), OpportunitiesActivity.class);
                intent.putStringArrayListExtra(OpportunitiesActivity.EXTRA_SEARCH_TAGS, mTagsArray);
                intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY, todayName);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        getLoaderManager().initLoader(0, null, this);
    }

    private void drawMarker(LatLng latLng, String name, String updated){
        // add a marker to the map indicating our current position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(updated)
                .title(name));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DataProvider.KEY_ID,
                DataProvider.KEY_NAME,
                DataProvider.KEY_VENUE_ID,
                DataProvider.KEY_LOCATION_LAT,
                DataProvider.KEY_LOCATION_LNG,
                DataProvider.KEY_UPDATED
        };
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_VENUES,
                projection, null, null, null);

        return loader;
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (mMap != null) {
            mMap.clear();

            int locationCount = 0;
            int validLocations = 0;
            double lat = 0;
            double lng = 0;
            long updated = 0;
            String name = "";

            locationCount = cursor.getCount();
            cursor.moveToFirst();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < locationCount; i++) {
                lat = cursor.getDouble(cursor.getColumnIndex(DataProvider.KEY_LOCATION_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(DataProvider.KEY_LOCATION_LNG));

                // Ignore any of  venues with no location set
                if (lat != 0.0 && lng != 0.0) {
                    name = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_NAME));
                    updated = cursor.getLong(cursor.getColumnIndex(DataProvider.KEY_UPDATED));

                    LatLng location = new LatLng(lat, lng);
                    builder.include(location);

                    DateFormat dateF = DateFormat.getDateTimeInstance();
                    String text = "Last Updated: " + dateF.format(new Date(updated));

                    drawMarker(location, name, text);
                    validLocations++;
                }

                cursor.moveToNext();
            }

            if (validLocations > 0) {
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        }
    }
}
