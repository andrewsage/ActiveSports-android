package com.xoverto.activeaberdeen;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        refreshVenues();
        refreshActivities();
        refreshSubActivities();
        refreshOpportunities();
    }

    private void refreshVenues() {
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
                    JSONObject venueOwnerJSON = venue.getJSONObject("venue_owner");

                    String id = venue.getString("id");
                    String name = venue.getString("name");
                    String address = venue.getString("address");
                    String postcode = venue.getString("postcode");
                    String latitude = venue.getString("latitude");
                    String longitude = venue.getString("longitude");
                    String web = venue.getString("web");
                    String email = venue.getString("email");
                    String telephone = venue.getString("telephone");
                    String venueOwnerSlug = venueOwnerJSON.getString("slug").replace('-', '_');

                    addNewVenue(id, name, address, postcode, latitude, longitude, web, email, telephone, venueOwnerSlug);
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

    private void refreshActivities() {
        // Get the JSON
        URL url;
        try {
            String venuesFeed = getString(R.string.activities_feed);
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
                    String title = venue.getString("title");
                    String category = venue.getString("category");

                    addNewActivity(id, title, category);
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

    private void refreshSubActivities() {
        // Get the JSON
        URL url;
        try {
            String venuesFeed = getString(R.string.sub_activities_feed);
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
                    String title = venue.getString("title");
                    String activity_id = venue.getString("activity_id");

                    addNewSubActivity(id, title, activity_id);
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


    private void refreshOpportunities() {
        // Get the JSON
        URL url;
        try {
            String venuesFeed = getString(R.string.opportunities_feed);
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
                    String description = venue.getString("description");
                    String activity_id = venue.getString("activity_id");
                    String sub_activity_id = venue.getString("sub_activity_id");
                    String venue_id = venue.getString("venue_id");
                    String room = venue.getString("room");
                    String start_time = venue.getString("start_time");
                    String end_time = venue.getString("end_time");
                    String day_of_week = venue.getString("day_of_week");
                    String image_html = venue.getString("image_url");
                    JSONArray tagsJSONArray = venue.getJSONArray("tag_list");

                    ArrayList<String> tagsArray = new ArrayList<String>();
                    for(int t = 0, count = tagsJSONArray.length(); t < count; t++)
                    {
                        try {
                            String tag = tagsJSONArray.getString(t);
                            tagsArray.add(tag);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    String tags = tagsArray.toString();
                    tags = tags.substring(1, tags.length()-1);

                    String image_url = "";
                    Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\">]+)\"");
                    Matcher matcher = pattern.matcher(image_html);
                    while (matcher.find()) {
                        image_url = matcher.group(1);
                    }

                    addNewOpportunity(id, name, description, activity_id, sub_activity_id,
                            venue_id, room, start_time, end_time, day_of_week, image_url,
                            tags);
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

    private void addNewVenue(String id, String name, String address, String postcode, String latitude, String longitude, String web, String email, String telephone,
                             String venueOwnerSlug) {
        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this venue in the provider
        String w = DataProvider.KEY_NAME + " = '" + name + "'";
        w = DataProvider.KEY_NAME + "=?";

        String[] selectionArgs = { name };

        // If the venue is new, insert it into the provider
        Cursor query = cr.query(DataProvider.CONTENT_URI_VENUES, null, w, selectionArgs, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_VENUE_ID, id);
            values.put(DataProvider.KEY_NAME, name);
            values.put(DataProvider.KEY_ADDRESS, address);
            values.put(DataProvider.KEY_TELEPHONE, telephone);
            values.put(DataProvider.KEY_VENUE_OWNER_SLUG, venueOwnerSlug);

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

            values.put(DataProvider.KEY_LOCATION_LAT, latPosition);
            values.put(DataProvider.KEY_LOCATION_LNG, longPosition);
            values.put(DataProvider.KEY_UPDATED, java.lang.System.currentTimeMillis());


            cr.insert(DataProvider.CONTENT_URI_VENUES, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_VENUE_ID, id);
            values.put(DataProvider.KEY_NAME, name);
            values.put(DataProvider.KEY_ADDRESS, address);
            values.put(DataProvider.KEY_TELEPHONE, telephone);
            values.put(DataProvider.KEY_VENUE_OWNER_SLUG, venueOwnerSlug);

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

            values.put(DataProvider.KEY_LOCATION_LAT, latPosition);
            values.put(DataProvider.KEY_LOCATION_LNG, longPosition);
            values.put(DataProvider.KEY_UPDATED, java.lang.System.currentTimeMillis());

            int rowsUpdated = cr.update(DataProvider.CONTENT_URI_VENUES, values, w, selectionArgs);
            Log.d(TAG, "Rows updated: " + rowsUpdated);
        }
        query.close();
    }

    private void addNewActivity(String id, String title, String category) {
        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this venue in the provider
        String w = DataProvider.KEY_ACTIVITY_ID + " = '" + id + "'";

        // If the venue is new, insert it into the provider
        Cursor query = cr.query(DataProvider.CONTENT_URI_ACTIVITIES, null, w, null, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_ACTIVITY_ID, id);
            values.put(DataProvider.KEY_ACTIVITY_TITLE, title);
            values.put(DataProvider.KEY_ACTIVITY_CATEGORY, category);

            cr.insert(DataProvider.CONTENT_URI_ACTIVITIES, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_ACTIVITY_TITLE, title);
            values.put(DataProvider.KEY_ACTIVITY_CATEGORY, category);

            cr.update(DataProvider.CONTENT_URI_ACTIVITIES, values, w, null);
        }
        query.close();
    }

    private void addNewSubActivity(String id, String title, String activity_id) {
        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this venue in the provider
        String w = DataProvider.KEY_SUB_ACTIVITY_ID + " = '" + id + "'";

        // If the venue is new, insert it into the provider
        Cursor query = cr.query(DataProvider.CONTENT_URI_SUB_ACTIVITIES, null, w, null, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_SUB_ACTIVITY_ID, id);
            values.put(DataProvider.KEY_SUB_ACTIVITY_TITLE, title);
            values.put(DataProvider.KEY_SUB_ACTIVITY_ACTIVITY_ID, activity_id);

            cr.insert(DataProvider.CONTENT_URI_SUB_ACTIVITIES, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_SUB_ACTIVITY_TITLE, title);
            values.put(DataProvider.KEY_SUB_ACTIVITY_ACTIVITY_ID, activity_id);

            cr.update(DataProvider.CONTENT_URI_SUB_ACTIVITIES, values, w, null);
        }
        query.close();
    }

    private void addNewOpportunity(String id, String name, String description, String activity_id,
                                   String sub_activity_id, String venue_id, String room,
                                   String start_time, String end_time, String day_of_week,
                                   String image_url,
                                   String tags) {
        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this venue in the provider
        String w = DataProvider.KEY_OPPORTUNITY_ID + " = '" + id + "'";

        // If the venue is new, insert it into the provider
        Cursor query = cr.query(DataProvider.CONTENT_URI_OPPORTUNITIES, null, w, null, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_OPPORTUNITY_ID, id);
            values.put(DataProvider.KEY_OPPORTUNITY_NAME, name);
            values.put(DataProvider.KEY_OPPORTUNITY_DESCRIPTION, description);
            values.put(DataProvider.KEY_OPPORTUNITY_ACTIVITY_ID, activity_id);
            values.put(DataProvider.KEY_OPPORTUNITY_SUB_ACTIVITY_ID, sub_activity_id);
            values.put(DataProvider.KEY_OPPORTUNITY_VENUE_ID, venue_id);
            values.put(DataProvider.KEY_OPPORTUNITY_ROOM, room);
            values.put(DataProvider.KEY_OPPORTUNITY_START_TIME, start_time);
            values.put(DataProvider.KEY_OPPORTUNITY_END_TIME, end_time);
            values.put(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK, day_of_week);
            values.put(DataProvider.KEY_OPPORTUNITY_IMAGE_URL, image_url);
            values.put(DataProvider.KEY_OPPORTUNITY_TAGS, tags);

            cr.insert(DataProvider.CONTENT_URI_OPPORTUNITIES, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(DataProvider.KEY_OPPORTUNITY_NAME, name);
            values.put(DataProvider.KEY_OPPORTUNITY_DESCRIPTION, description);
            values.put(DataProvider.KEY_OPPORTUNITY_ACTIVITY_ID, activity_id);
            values.put(DataProvider.KEY_OPPORTUNITY_SUB_ACTIVITY_ID, sub_activity_id);
            values.put(DataProvider.KEY_OPPORTUNITY_VENUE_ID, venue_id);
            values.put(DataProvider.KEY_OPPORTUNITY_ROOM, room);
            values.put(DataProvider.KEY_OPPORTUNITY_START_TIME, start_time);
            values.put(DataProvider.KEY_OPPORTUNITY_END_TIME, end_time);
            values.put(DataProvider.KEY_OPPORTUNITY_DAY_OF_WEEK, day_of_week);
            values.put(DataProvider.KEY_OPPORTUNITY_IMAGE_URL, image_url);
            values.put(DataProvider.KEY_OPPORTUNITY_TAGS, tags);

            cr.update(DataProvider.CONTENT_URI_OPPORTUNITIES, values, w, null);
        }
        query.close();
    }
}
