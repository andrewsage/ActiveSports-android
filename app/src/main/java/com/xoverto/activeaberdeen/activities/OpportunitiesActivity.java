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
import android.widget.TextView;

import com.xoverto.activeaberdeen.R;
import com.xoverto.activeaberdeen.ui.OpportunityFeedFragment;

import java.util.ArrayList;


public class OpportunitiesActivity extends ActionBarActivity implements OpportunityFeedFragment.OnFragmentInteractionListener,
OpportunityFeedFragment.OnDataPass {

    public final static String EXTRA_OPPORTUNITY_ID = "com.xoverto.activeaberdeen.OPPORTUNITY_ID";
    public final static String EXTRA_SEARCH_DAY = "SEARCH_DAY";
    public final static String EXTRA_SEARCH_NAME ="NAME";
    public final static String EXTRA_SEARCH_VENUE = "VENUE_ID";
    public final static String EXTRA_SEARCH_TAGS = "TAGS";
    public final static String EXTRA_LIST_TITLE = "LIST_TITLE";

    private TextView mActionBarTitleView;
    private TextView mActionBarSubTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunities);


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.opportunties_actionbar, null);
        mActionBarTitleView = (TextView)actionBarLayout.findViewById(R.id.actionbar_titleview);
        mActionBarTitleView.setText("Activities");
        mActionBarSubTitleView = (TextView)actionBarLayout.findViewById(R.id.actionbar_subtitleview);
        mActionBarSubTitleView.setText("x Activities");
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
            public void onClick(View v) {
                Intent intent = new Intent(OpportunitiesActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);

        String title = null;
        String day = null;
        String name = null;
        String venueId = null;
        ArrayList<String> tags = null;

        if(getIntent() != null)
        {
            title = getIntent().getStringExtra(OpportunitiesActivity.EXTRA_LIST_TITLE);
            name = getIntent().getStringExtra(OpportunitiesActivity.EXTRA_SEARCH_NAME);
            day = getIntent().getStringExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY);
            venueId = getIntent().getStringExtra(OpportunitiesActivity.EXTRA_SEARCH_VENUE);
            tags = getIntent().getStringArrayListExtra(OpportunitiesActivity.EXTRA_SEARCH_TAGS);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OpportunityFeedFragment.newInstance(title, name, day, venueId, tags))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.opportunities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String value) {

        Intent intent = new Intent(this, OpportunityActivity.class);
        intent.putExtra(EXTRA_OPPORTUNITY_ID, value);
        startActivity(intent);
    }

    @Override
    public void onDataPass(int activities, String title) {
        mActionBarTitleView.setText(title);
        mActionBarSubTitleView.setText(activities + " Activities");
    }
}
