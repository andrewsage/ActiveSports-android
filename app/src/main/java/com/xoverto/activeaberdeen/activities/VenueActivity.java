package com.xoverto.activeaberdeen.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.ui.OpportunityFeedFragment;
import com.xoverto.activeaberdeen.ui.OpportunityFragment;
import com.xoverto.activeaberdeen.ui.VenueFragment;


public class VenueActivity extends FragmentActivity implements OpportunityFeedFragment.OnFragmentInteractionListener {

    private TextView actionBarTitleView;
    public static FragmentManager fgmanger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.venues_actionbar, null);
        actionBarTitleView = (TextView)actionBarLayout.findViewById(R.id.actionbar_titleview);

        android.app.ActionBar.LayoutParams params = new android.app.ActionBar.LayoutParams(
                android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        ImageView actionBarImageViewBack = (ImageView)actionBarLayout.findViewById(R.id.action_bar_imageview_back);

        actionBarImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        ImageView actionBarImageViewHome = (ImageView)actionBarLayout.findViewById(R.id.action_bar_imageview_home);

        actionBarImageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenueActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);

        String venueId = null; // This is the local database ID

        if(getIntent() != null)
        {
            venueId = getIntent().getStringExtra(VenuesActivity.EXTRA_VENUE_ID);
        }

        ContentResolver cr = getContentResolver();

        String venueName = "";
        String venueOwnerSlug = "";
        String address = "";
        String telephone = "";
        LatLng venueLatLng = null;
        String venueRemoteID = null;   // this is the ID that anything that references it will use,

        String wVenue = DataProvider.KEY_ID + " = '" + venueId + "'";
        Cursor queryVenue = cr.query(DataProvider.CONTENT_URI_VENUES, null, wVenue, null, null);

        if (queryVenue.getCount() > 0) {
            queryVenue.moveToFirst();
            venueRemoteID = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_VENUE_ID));
            venueName = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_NAME));
            telephone = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_TELEPHONE));
            address = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_ADDRESS));
            venueOwnerSlug = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_VENUE_OWNER_SLUG));
            double lat = queryVenue.getDouble(queryVenue.getColumnIndex(DataProvider.KEY_LOCATION_LAT));
            double lng = queryVenue.getDouble(queryVenue.getColumnIndex(DataProvider.KEY_LOCATION_LNG));

            venueLatLng = new LatLng(lat, lng);
        }
        queryVenue.close();

        actionBarTitleView.setText(venueName);

        //initialising the fragment manager
        fgmanger  = getSupportFragmentManager() ;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, VenueFragment.newInstance(venueRemoteID, venueName, address, telephone, venueLatLng, venueOwnerSlug))
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_venue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(String value) {

        Intent intent = new Intent(this, OpportunityActivity.class);
        intent.putExtra(OpportunitiesActivity.EXTRA_OPPORTUNITY_ID, value);
        startActivity(intent);
    }
}
