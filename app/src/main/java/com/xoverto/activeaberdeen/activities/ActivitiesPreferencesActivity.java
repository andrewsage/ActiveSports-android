package com.xoverto.activeaberdeen.activities;

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
import com.xoverto.activeaberdeen.ui.ActivitiesPreferenceFragment;

public class ActivitiesPreferencesActivity extends ActionBarActivity implements ActivitiesPreferenceFragment.OnFragmentInteractionListener {

    private TextView actionBarTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.preferences_actionbar, null);
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
                finish();
            }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ActivitiesPreferenceFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
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

    }

}
