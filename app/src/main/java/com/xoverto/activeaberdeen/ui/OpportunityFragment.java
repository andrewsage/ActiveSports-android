package com.xoverto.activeaberdeen.ui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.xoverto.activeaberdeen.R;

public class OpportunityFragment extends Fragment {

    public static final String OPPORTUNITY_ID_KEY = "opportunityId";
    public static final String OPPORTUNITY_IMAGE_URI = "imageUri";
    public static final String OPPORTUNITY_NAME = "name";
    public static final String OPPORTUNITY_TIME = "time";
    public static final String OPPORTUNITY_VENUE = "venue";
    public static final String OPPORTUNITY_DESCRIPTION = "description";
    public static final String OPPORTUNITY_ADDRESS = "address";
    public static final String OPPORTUNITY_TELEPHONE = "telephone";
    public static final String OPPORTUNITY_LAT = "lat";
    public static final String OPPORTUNITY_LONG = "long";
    public static final String OPPORTUNITY_TAGS = "tags";

    public static FragmentManager fgmanger;

    private TextView nameText;
    private ImageView photoImage;
    private TextView timeText;
    private TextView venueText;
    private TextView descriptionText;
    private TextView addressText;
    private TextView telephoneText;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TextView mTagsText;

    public OpportunityFragment() {
    }

    public static Fragment newInstance(String name, String venue, String time, String photoUri, String description,
                                       String address, String telephone, LatLng venueLatLng, String tags)
    {
        Fragment myFragment = new OpportunityFragment();

        Bundle args = new Bundle();

        args.putString(OPPORTUNITY_NAME, name);
        args.putString(OPPORTUNITY_TIME, time);
        args.putString(OPPORTUNITY_VENUE, venue);
        args.putString(OPPORTUNITY_IMAGE_URI, photoUri);
        args.putString(OPPORTUNITY_DESCRIPTION, description);
        args.putString(OPPORTUNITY_ADDRESS, address);
        args.putString(OPPORTUNITY_TELEPHONE, telephone);
        args.putDouble(OPPORTUNITY_LAT, venueLatLng.latitude);
        args.putDouble(OPPORTUNITY_LONG, venueLatLng.longitude);
        args.putString(OPPORTUNITY_TAGS, tags);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_opportunity2, container, false);

        nameText = (TextView)rootView.findViewById(R.id.name);
        timeText = (TextView)rootView.findViewById(R.id.start_time);
        venueText = (TextView)rootView.findViewById(R.id.venue);
        photoImage = (ImageView)rootView.findViewById(R.id.imageView);
        descriptionText = (TextView)rootView.findViewById(R.id.description);
        addressText = (TextView)rootView.findViewById(R.id.address);
        telephoneText = (TextView)rootView.findViewById(R.id.telephone);
        mTagsText = (TextView)rootView.findViewById(R.id.tags);

        setUpMapIfNeeded();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String name = getArguments().getString(OPPORTUNITY_NAME);
        String time = getArguments().getString(OPPORTUNITY_TIME);
        String venue = getArguments().getString(OPPORTUNITY_VENUE);
        String photoUri = getArguments().getString(OPPORTUNITY_IMAGE_URI);
        String description = getArguments().getString(OPPORTUNITY_DESCRIPTION);
        String address = getArguments().getString(OPPORTUNITY_ADDRESS);
        String telephone = getArguments().getString(OPPORTUNITY_TELEPHONE);
        String tags = getArguments().getString(OPPORTUNITY_TAGS);



        nameText.setText(name);
        timeText.setText(time);
        venueText.setText(venue);
        descriptionText.setText(Html.fromHtml(description));
        addressText.setText(address);
        telephoneText.setText(telephone);
        mTagsText.setText(tags);

        if(photoUri.isEmpty() == false) {
            Picasso.with(getActivity())
                    .load(photoUri)
                            //.placeholder(R.drawable.time_mini)
                            //.error(R.drawable.time_mini)
                    .centerCrop()
                    .fit()
                    .into(photoImage);
        } else {
            photoImage.setVisibility(View.GONE);
        }
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

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
    }

    private void drawMarker(LatLng latLng, String name, String updated){
        // add a marker to the map indicating our current position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(updated)
                .title(name));
    }
}