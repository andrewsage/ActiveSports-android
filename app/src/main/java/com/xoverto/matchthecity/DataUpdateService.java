package com.xoverto.matchthecity;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataUpdateService extends IntentService {

    public static String TAG = "DATA_UPDATE_SERVICE";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


    public DataUpdateService() {
        super("DataUpdateService");
    }

    public DataUpdateService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION = DataAlarmReceiver.ACTION_REFRESH_DATA_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            Context context = getApplicationContext();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int updateFrequency = 1; //Integer.parseInt(prefs.getString("refresh_frequency", 5));


            if(updateFrequency > 0) {
                int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                long timeToRefresh = SystemClock.elapsedRealtime() + updateFrequency*60*1000;
                alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFrequency*60*1000, alarmIntent);

            } else {
                alarmManager.cancel(alarmIntent);
            }
            refreshData();
        }
    }

    public void refreshData() {
        // Get the JSON
        URL url;
        try {
            String venuesFeed = getString(R.string.venues_feed);
            url = new URL(venuesFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONArray venues = new JSONArray(responseStrBuilder.toString());
                for(int i = 0; i < venues.length(); i++) {
                    JSONObject venue = venues.getJSONObject(i);

                    String id = venue.getString("id");
                    String name = venue.getString("name");
                    String address = venue.getString("address");
                    String postcode = venue.getString("postcode");
                    String latitude = venue.getString("latitude");
                    String longitude = venue.getString("longitude");
                    String web = venue.getString("web");
                    String email = venue.getString("email");
                    String telephone = venue.getString("telephone");

                    addNewVenue(id, name, address, postcode, latitude, longitude, web, email, telephone);
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e) {
            Log.d(TAG, "IOException");
        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
        } finally {
        }
    }

    private void addNewVenue(String id, String name, String address, String postcode, String latitude, String longitude, String web, String email, String telephone) {



        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this venue in the provider
        String w = VenueProvider.KEY_NAME + " = '" + name + "'";

        // If the venue is new, insert it into the provider
        Cursor query = cr.query(VenueProvider.CONTENT_URI, null, w, null, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(VenueProvider.KEY_VENUE_ID, id);
            values.put(VenueProvider.KEY_NAME, name);

            Double latPosition = 0.0;
            Double longPosition = 0.0;

            try {
                latPosition = Double.parseDouble(latitude);
                longPosition = Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                Log.d(TAG, "Location parsing exception for " + name, e);
            } catch (NullPointerException e) {
                Log.d(TAG, "Location parsing exception for " + name, e);
            }

            values.put(VenueProvider.KEY_LOCATION_LAT, latPosition);
            values.put(VenueProvider.KEY_LOCATION_LNG, longPosition);
            values.put(VenueProvider.KEY_UPDATED, java.lang.System.currentTimeMillis());


            cr.insert(VenueProvider.CONTENT_URI, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(VenueProvider.KEY_VENUE_ID, id);
            values.put(VenueProvider.KEY_NAME, name);

            Double latPosition = 0.0;
            Double longPosition = 0.0;

            try {
                latPosition = Double.parseDouble(latitude);
                longPosition = Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                Log.d(TAG, "Location parsing exception for " + name, e);
            } catch (NullPointerException e) {
                Log.d(TAG, "Location parsing exception for " + name, e);
            }

            values.put(VenueProvider.KEY_LOCATION_LAT, latPosition);
            values.put(VenueProvider.KEY_LOCATION_LNG, longPosition);
            values.put(VenueProvider.KEY_UPDATED, java.lang.System.currentTimeMillis());

            cr.update(VenueProvider.CONTENT_URI, values, w, null);
        }
        query.close();
    }
}
