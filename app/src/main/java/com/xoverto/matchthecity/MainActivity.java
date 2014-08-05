package com.xoverto.matchthecity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends ActionBarActivity implements VenueFragment.OnFragmentInteractionListener {

    public final static String EXTRA_VENUE_LOCATION = "com.xoverto.matchthecity.VENUE_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new VenueFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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
    public void onFragmentInteraction(LatLng latLng) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_VENUE_LOCATION, latLng);
        startActivity(intent);
    }
}
