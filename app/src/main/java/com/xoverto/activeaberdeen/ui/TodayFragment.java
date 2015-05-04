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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
public class TodayFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button mResultsButton;

    public static FragmentManager fgmanger;

    public TodayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        mResultsButton = (Button) rootView.findViewById(R.id.results_button);

        mResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String todayName = sdf.format(c.getTime());
                ArrayList<String> tagsArray = new ArrayList<String>();
                tagsArray.add("strength");

                Intent intent = new Intent(getActivity(), OpportunitiesActivity.class);
                intent.putStringArrayListExtra(OpportunitiesActivity.EXTRA_SEARCH_TAGS, tagsArray);
                intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY, todayName);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            fgmanger  = getFragmentManager();
            mMap = ((SupportMapFragment) fgmanger.findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

            /*
            double venueLat = getArguments().getDouble(OPPORTUNITY_LAT);
            double venueLong = getArguments().getDouble(OPPORTUNITY_LONG);
            String venue = getArguments().getString(OPPORTUNITY_VENUE);

            LatLng latLng = new LatLng(venueLat, venueLong);

            mMap.setMyLocationEnabled(true);

            if(latLng == null) {
                Criteria criteria = new Criteria();
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                double lat =  location.getLatitude();
                double lng = location.getLongitude();
                latLng = new LatLng(lat, lng);
            } else {
                drawMarker(latLng, venue, "");
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            */
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

        int locationCount = 0;
        int validLocations = 0;
        double lat = 0;
        double lng = 0;
        long updated = 0;
        String name = "";

        locationCount = cursor.getCount();
        cursor.moveToFirst();

        setUpMapIfNeeded();
        mMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for(int i = 0; i < locationCount; i++) {
            lat = cursor.getDouble(cursor.getColumnIndex(DataProvider.KEY_LOCATION_LAT));
            lng = cursor.getDouble(cursor.getColumnIndex(DataProvider.KEY_LOCATION_LNG));

            // Ignore any of  venues with no location set
            if(lat != 0.0 && lng != 0.0) {
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

        if(validLocations > 0) {
            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }
}
