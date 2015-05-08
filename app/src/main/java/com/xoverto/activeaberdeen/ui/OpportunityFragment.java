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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.xoverto.activeaberdeen.R;

public class OpportunityFragment extends Fragment implements OnMapReadyCallback {

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
    private LinearLayout mTagsSectionLinearLayout;
    private LinearLayout mTagsLinearLayout;



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
        fgmanger  = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fgmanger.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nameText = (TextView)rootView.findViewById(R.id.name);
        timeText = (TextView)rootView.findViewById(R.id.start_time);
        venueText = (TextView)rootView.findViewById(R.id.venue);
        photoImage = (ImageView)rootView.findViewById(R.id.imageView);
        descriptionText = (TextView)rootView.findViewById(R.id.description);
        addressText = (TextView)rootView.findViewById(R.id.address);
        telephoneText = (TextView)rootView.findViewById(R.id.telephone);

        mTagsSectionLinearLayout = (LinearLayout)rootView.findViewById(R.id.tags_section);
        mTagsLinearLayout = (LinearLayout)rootView.findViewById(R.id.tagsLayout);

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

        if(tags.isEmpty() == false) {
            tags = tags.replace(", ", ",");

            String[] tagsArray = tags.split(",");
            for (int t = 0; t < tagsArray.length; t++) {
                String tag = tagsArray[t];

                StringBuilder tagSb = new StringBuilder(tag.toLowerCase());
                tagSb.setCharAt(0, Character.toUpperCase(tagSb.charAt(0)));
                tag = tagSb.toString();

                TextView tagText = (TextView) getLayoutInflater(savedInstanceState).inflate(R.layout.template_tag_textview, null);
                tagText.setText(tag);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 0, 10, 0);
                tagText.setLayoutParams(params);

                mTagsLinearLayout.addView(tagText);
            }
        } else {
            mTagsSectionLinearLayout.setVisibility(View.GONE);
        }

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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        setUpMap();
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