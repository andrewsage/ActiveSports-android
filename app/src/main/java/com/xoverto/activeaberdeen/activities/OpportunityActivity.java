package com.xoverto.activeaberdeen.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.xoverto.activeaberdeen.DataProvider;
import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.ui.OpportunityFragment;


public class OpportunityActivity extends ActionBarActivity {

    public static FragmentManager fgmanger;
    private TextView actionBarTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity);


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
                Intent intent = new Intent(OpportunityActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);



        String opportunityId = null;

        if(getIntent() != null)
        {
            opportunityId = getIntent().getStringExtra(OpportunitiesActivity.EXTRA_OPPORTUNITY_ID);
        }

        ContentResolver cr = getContentResolver();

        String w = DataProvider.KEY_ID + " = '" + opportunityId + "'";
        String label = "unknown activity";
        String name = "";
        String photoUri = "";
        String time = "";
        String venue = "";
        String description = "";
        String address = "";
        String telephone = "";
        LatLng venueLatLng = null;

        Cursor query = cr.query(DataProvider.CONTENT_URI_OPPORTUNITIES, null, w, null, null);

        if(query.getCount() > 0) {
            query.moveToFirst();
            name = query.getString(query.getColumnIndex(DataProvider.KEY_NAME));

            actionBarTitleView.setText(name);

            description = query.getString(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_DESCRIPTION));

            photoUri = query.getString(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_IMAGE_URL));

            time = "" + query.getString(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK))
                    + " " + query.getString(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_START_TIME))
                    + "-" + query.getString(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_END_TIME));

            int venue_id = query.getInt(query.getColumnIndex(DataProvider.KEY_OPPORTUNITY_VENUE_ID));

            String wVenue = DataProvider.KEY_VENUE_ID + " = '" + venue_id + "'";
            Cursor queryVenue = cr.query(DataProvider.CONTENT_URI_VENUES, null, wVenue, null, null);

            if(queryVenue.getCount() > 0) {
                queryVenue.moveToFirst();
                venue = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_NAME));
                telephone = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_TELEPHONE));
                address = queryVenue.getString(queryVenue.getColumnIndex(DataProvider.KEY_ADDRESS));
                double lat = queryVenue.getDouble(queryVenue.getColumnIndex(DataProvider.KEY_LOCATION_LAT));
                double lng = queryVenue.getDouble(queryVenue.getColumnIndex(DataProvider.KEY_LOCATION_LNG));

                venueLatLng = new LatLng(lat, lng);

            }
            queryVenue.close();
        }
        query.close();

        getActionBar().setTitle(name);

        //initialising the fragment manager
        fgmanger  = getSupportFragmentManager() ;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OpportunityFragment.newInstance(name, venue, time, photoUri, description, address, telephone, venueLatLng))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_opportunity, menu);
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
}
