package com.xoverto.activeaberdeen.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xoverto.activeaberdeen.R;

import java.util.Calendar;

public class SearchActivity extends ActionBarActivity {

    private TextView actionBarTitleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.search_actionbar, null);
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
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_search, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button mTodayButton;
        private Button mTomorrowButton;
        private Button mSearchButton;
        private EditText mNameEditText;
        private String[] days;
        private Spinner mDaysSpinner;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);


            days = getResources().getStringArray(R.array.days_list);
            mTodayButton = (Button) rootView.findViewById(R.id.today_button);
            mTomorrowButton = (Button) rootView.findViewById(R.id.tomorrow_button);
            mSearchButton = (Button) rootView.findViewById(R.id.button_search);
            mNameEditText = (EditText) rootView.findViewById(R.id.name_editText);
            mDaysSpinner = (Spinner) rootView.findViewById(R.id.day_spinner);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, days);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mDaysSpinner.setAdapter(dataAdapter);

            mTodayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    mDaysSpinner.setSelection(day - 1, true);

                }
            });

            mTomorrowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int day = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
                    mDaysSpinner.setSelection(day - 1, true);
                }
            });

            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), OpportunitiesActivity.class);
                    intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_NAME, mNameEditText.getText().toString());
                    if(mDaysSpinner.getSelectedItemPosition() < 7) {
                        intent.putExtra(OpportunitiesActivity.EXTRA_SEARCH_DAY, days[mDaysSpinner.getSelectedItemPosition()]);
                    }
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }
}
