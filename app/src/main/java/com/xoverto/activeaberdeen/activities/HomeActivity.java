package com.xoverto.activeaberdeen.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.xoverto.activeaberdeen.BuildConfig;
import com.xoverto.activeaberdeen.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class HomeActivity extends ActionBarActivity {

    Button todayButton;
    Button venuesButton;
    Button searchButton;
    Button preferencesButton;
    TextView versionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        todayButton = (Button)findViewById(R.id.button_today);
        venuesButton = (Button)findViewById(R.id.button_venues);
        searchButton = (Button)findViewById(R.id.button_search);
        preferencesButton = (Button)findViewById(R.id.button_preferences);
        versionTextView = (TextView)findViewById(R.id.text_version);

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TodayActivity.class);
                startActivity(intent);
            }
        });

        venuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, VenuesActivity.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivitiesPreferencesActivity.class);
                startActivity(intent);
            }
        });

        // Get the version details
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionTextView.setText("Development build\n" + versionName);

        /*
        // Display prompt if preferences have not yet been set

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("In order to help us recommend activities that are more relevant to you we would like to ask you some questions.")
                .setTitle("Your activity preferences")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(HomeActivity.this, ActivitiesPreferencesActivity.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
