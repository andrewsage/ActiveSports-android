package com.xoverto.activeaberdeen.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.activities.OpportunitiesActivity;
import com.xoverto.activeaberdeen.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by andrew on 14/04/15.
 */
public class TodayFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button mResultsButton;
    ToggleButton mStrengthButton;
    ToggleButton mCardioButton;
    ToggleButton mWeightlossButton;
    ToggleButton mFlexibilityButton;
    ArrayList<String> mTagsArray;
    private Hashtable<String, String> mMarkers;
    private Hashtable<String, String> mMarkersVenueIds;

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


        mMarkers = new Hashtable<String, String>();
        mMarkersVenueIds = new Hashtable<String, String>();

        mTagsArray = new ArrayList<String>();
        mTagsArray.add("strength");
        mTagsArray.add("cardio");
        mTagsArray.add("weightloss");
        mTagsArray.add("flexibility");

        mStrengthButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("strength");
                } else {
                    mTagsArray.remove("strength");
                }

                filterResults();
            }
        });

        mCardioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("cardio");
                } else {
                    mTagsArray.remove("cardio");
                }
                filterResults();

            }
        });

        mWeightlossButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("weightloss");
                } else {
                    mTagsArray.remove("weightloss");
                }
                filterResults();

            }
        });

        mFlexibilityButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTagsArray.add("flexibility");
                } else {
                    mTagsArray.remove("flexibility");
                }
                filterResults();

            }
        });

        mResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String todayName = sdf.format(c.getTime());

                Intent intent = new Intent(getActivity(), OpportunitiesActivity.class);
                intent.putExtra(OpportunitiesActivity.EXTRA_LIST_TITLE, "What's on today");
                intent.putStringArrayListExtra(OpportunitiesActivity.EXTRA_SEARCH_TAGS, mTagsArray);
                intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY, todayName);
                startActivity(intent);
            }
        });

        return rootView;
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnDataPass) a;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity().getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);
        filterResults();
    }

    private void filterResults() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String todayName = sdf.format(c.getTime());

        Bundle args = new Bundle();
        args.putString(OpportunityFeedFragment.SEARCH_DAY, todayName);
        args.putStringArrayList(OpportunityFeedFragment.SEARCH_TAGS, mTagsArray);
        getLoaderManager().restartLoader(0, args, TodayFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String day = args.getString(OpportunityFeedFragment.SEARCH_DAY);
        ArrayList<String> tags = args.getStringArrayList(OpportunityFeedFragment.SEARCH_TAGS);


        // Construct a where clause to filter the opportunities
        String w = "";
        ArrayList<String> selectionArgs = new ArrayList<String>();

        if(day != null) {
            if(selectionArgs.size() > 0) {
                w = w + " AND ";
            }
            w = w + DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK + "=?";
            selectionArgs.add(day);
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
                DataProvider.KEY_OPPORTUNITY_VENUE_ID
        };
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_OPPORTUNITIES,
                projection, w, selectionArgsArray, null);

        return loader;
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (mMap != null) {
            mMap.clear();

            int validLocations = 0;
            double lat = 0;
            double lng = 0;
            long updated = 0;
            String name = "";
            String logoName = "";

            int opportunitiesCount = 0;
            opportunitiesCount = cursor.getCount();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE d");
            String todayName = sdf.format(c.getTime());

            passData(opportunitiesCount, todayName + TimeUtil.getDayOfMonthSuffix(c.get(Calendar.DAY_OF_MONTH)));

            cursor.moveToFirst();

            ContentResolver cr = loader.getContext().getContentResolver();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            // Get a list of venues and the number of opportunities they have
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

            for(int i = 0; i < opportunitiesCount; i++) {
                String venue_id = cursor.getString(cursor.getColumnIndex(DataProvider.KEY_OPPORTUNITY_VENUE_ID));
                Integer count = 0;
                if(hashMap.containsKey(venue_id)) {
                    count = (Integer) hashMap.get(venue_id);
                }
                count++;
                hashMap.put(venue_id, count);
                cursor.moveToNext();
            }

            Iterator iterator = hashMap.keySet().iterator();

            mMarkers.clear();
            mMarkersVenueIds.clear();

            while(iterator.hasNext()) {

                String key = (String)iterator.next();
                Integer activitiesCount = (Integer)hashMap.get(key);

                String w = DataProvider.KEY_VENUE_ID + "=?";
                ArrayList<String> selectionArgs = new ArrayList<String>();
                selectionArgs.add(key);
                String[] selectionArgsArray = new String[selectionArgs.size()];
                selectionArgsArray = selectionArgs.toArray(selectionArgsArray);

                Cursor query = cr.query(DataProvider.CONTENT_URI_VENUES, null, w, selectionArgsArray, null);

                if(query.getCount() > 0) {
                    query.moveToFirst();
                    lat = query.getDouble(query.getColumnIndex(DataProvider.KEY_LOCATION_LAT));
                    lng = query.getDouble(query.getColumnIndex(DataProvider.KEY_LOCATION_LNG));
                    logoName = query.getString(query.getColumnIndex(DataProvider.KEY_VENUE_OWNER_SLUG));

                    // Ignore any of  venues with no location set
                    if (lat != 0.0 && lng != 0.0) {
                        validLocations++;

                        name = query.getString(query.getColumnIndex(DataProvider.KEY_NAME));
                        LatLng location = new LatLng(lat, lng);
                        builder.include(location);

                        String text = activitiesCount.toString();
                        drawMarker(location, name, text, logoName, key);
                    }
                }
                query.close();
            }

            if (validLocations > 0) {
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
            }
        }
    }

    private void drawMarker(LatLng latLng, String name, String text, String logoName, String venueId){

        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.map_pin);
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas1 = new Canvas(bitmap);

        // paint defines the text color,
        // stroke width, size
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() / 2);
        canvas1.drawText(text, x, y, paint);

        // add a marker to the map indicating our current position
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .anchor(0.5f, 1)
                //.snippet(text)
                .title(name));

        mMarkers.put(marker.getId(), logoName);
        mMarkersVenueIds.put(marker.getId(), venueId);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String todayName = sdf.format(c.getTime());
        String venueId = mMarkersVenueIds.get(marker.getId());

        Intent intent = new Intent(getActivity(), OpportunitiesActivity.class);
        intent.putExtra(OpportunitiesActivity.EXTRA_LIST_TITLE, "What's on today");
        intent.putStringArrayListExtra(OpportunitiesActivity.EXTRA_SEARCH_TAGS, mTagsArray);
        intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY, todayName);
        intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_VENUE, venueId);
        startActivity(intent);

    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        LayoutInflater inflater = null;

        MyInfoWindowAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View contentsView = inflater.inflate(R.layout.custom_info_contents, null);

            String logoName = mMarkers.get(marker.getId());

            ImageView imageView = ((ImageView)contentsView.findViewById(R.id.image));
            TextView tvTitle = ((TextView)contentsView.findViewById(R.id.title));


            if(logoName.isEmpty() == false) {
                int resID = getResources().getIdentifier(logoName, "drawable", getActivity().getPackageName());
                imageView.setImageResource(resID);
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setText(marker.getTitle());
            }


            return contentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public void passData(int activities, String date) {
        dataPasser.onDataPass(activities, date);
    }

    public interface OnDataPass {
        public void onDataPass(int activities, String date);
    }



}
