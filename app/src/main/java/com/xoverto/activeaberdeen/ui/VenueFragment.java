package com.xoverto.activeaberdeen.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.xoverto.activeaberdeen.activities.OpportunitiesActivity;
import com.xoverto.activeaberdeen.activities.OpportunityActivity;

/**
 * Created by andrew on 13/04/15.
 */
public class VenueFragment extends Fragment  {

    public static final String VENUE_ID_KEY = "venueId";
    public static final String VENUE_NAME = "name";
    public static final String VENUE_ADDRESS = "address";
    public static final String VENUE_TELEPHONE = "telephone";
    public static final String VENUE_LAT = "lat";
    public static final String VENUE_LONG = "long";
    public static final String VENUE_OWNER_SLUG = "slug";

    public static FragmentManager fgmanger;

    private TextView nameText;
    private ImageView logoImage;
    private TextView timeText;
    private TextView venueText;
    private TextView descriptionText;
    private TextView addressText;
    private TextView telephoneText;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public VenueFragment() {
    }

    public static Fragment newInstance(String venueId, String name, String address, String telephone, LatLng venueLatLng, String venueOwnerSlug)
    {
        Fragment myFragment = new VenueFragment();

        Bundle args = new Bundle();

        args.putString(VENUE_ID_KEY, venueId);
        args.putString(VENUE_NAME, name);
        args.putString(VENUE_ADDRESS, address);
        args.putString(VENUE_TELEPHONE, telephone);
        args.putDouble(VENUE_LAT, venueLatLng.latitude);
        args.putDouble(VENUE_LONG, venueLatLng.longitude);
        args.putString(VENUE_OWNER_SLUG, venueOwnerSlug);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Add the Opportunities fragment
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTran = childFragMan.beginTransaction();
        OpportunityFeedFragment fragOpportunities = new OpportunityFeedFragment();
        fragOpportunities.setArguments(getArguments());
        childFragTran.add(R.id.fragement_opportunities, fragOpportunities);
        childFragTran.commit();

        View rootView = inflater.inflate(R.layout.fragment_venue2, container, false);

        nameText = (TextView)rootView.findViewById(R.id.name);
        addressText = (TextView)rootView.findViewById(R.id.address);
        telephoneText = (TextView)rootView.findViewById(R.id.telephone);
        logoImage = (ImageView)rootView.findViewById(R.id.imageView);

        setUpMapIfNeeded();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String name = getArguments().getString(VENUE_NAME);
        String address = getArguments().getString(VENUE_ADDRESS);
        String telephone = getArguments().getString(VENUE_TELEPHONE);
        String venueOwnerSlug = getArguments().getString(VENUE_OWNER_SLUG);

        nameText.setText(name);
        addressText.setText(address);
        telephoneText.setText(telephone);

        if(venueOwnerSlug.isEmpty() == false) {
            int resID = getResources().getIdentifier(venueOwnerSlug , "drawable", getActivity().getPackageName());

            Picasso.with(getActivity())
                    .load(resID)
                            //.placeholder(R.drawable.time_mini)
                            //.error(R.drawable.time_mini)
                    .fit()
                    .centerInside()
                    .into(logoImage);
        } else {
            logoImage.setVisibility(View.GONE);
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

        double venueLat = getArguments().getDouble(VENUE_LAT);
        double venueLong = getArguments().getDouble(VENUE_LONG);
        String venue = getArguments().getString(VENUE_NAME);

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
