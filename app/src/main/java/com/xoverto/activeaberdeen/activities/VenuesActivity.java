package com.xoverto.activeaberdeen.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.ui.VenueFeedFragment;


public class VenuesActivity extends ActionBarActivity implements VenueFeedFragment.OnFragmentInteractionListener {

    public final static String EXTRA_VENUE_ID = "com.xoverto.activeaberdeen.VENUE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.venues_actionbar, null);
        //TextView actionBarTitleview = (TextView)actionBarLayout.findViewById(R.id.actionbar_titleview);
        //actionBarTitleview.setText("My Custom ActionBar Title");
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        ImageView actionBarImageViewBack = (ImageView)actionBarLayout.findViewById(R.id.action_bar_imageview_back);

        actionBarImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        ImageView actionBarImageViewHome = (ImageView)actionBarLayout.findViewById(R.id.action_bar_imageview_home);

        actionBarImageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_venues);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new VenueFeedFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.venues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_home) {
            finish();
        }

        if (id == R.id.action_activities) {

            Intent intent = new Intent(this, ActivitiesActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_sub_activities) {

            Intent intent = new Intent(this, SubActivitiesActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_opportunities) {

            Intent intent = new Intent(this, OpportunitiesActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String value) {

        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra(EXTRA_VENUE_ID, value);
        startActivity(intent);
    }
}
