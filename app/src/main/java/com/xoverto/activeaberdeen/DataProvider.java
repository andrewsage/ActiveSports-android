package com.xoverto.activeaberdeen;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataProvider extends ContentProvider {

    public static final Uri CONTENT_URI_VENUES = Uri.parse("content://com.xoverto.activeaberdeen/venues");
    public static final Uri CONTENT_URI_ACTIVITIES = Uri.parse("content://com.xoverto.activeaberdeen/activities");
    public static final Uri CONTENT_URI_SUB_ACTIVITIES = Uri.parse("content://com.xoverto.activeaberdeen/sub_activities");
    public static final Uri CONTENT_URI_OPPORTUNITIES = Uri.parse("content://com.xoverto.activeaberdeen/opportunities");
    public static final Uri CONTENT_URI_OPPORTUNITIES_DISTINCT = Uri.parse("content://com.xoverto.activeaberdeen/distinctOpportunties");

    // Column names
    public static final String KEY_ID = "_id"; // All tables use this field

    public static final String KEY_VENUE_ID = "venue_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_UPDATED = "updated";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_TELEPHONE = "telephone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_WEB = "web";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_POSTCODE = "postcode";
    public static final String KEY_VENUE_OWNER_SLUG = "venue_owner_slug";

    public static final String KEY_ACTIVITY_ID = "activity_id";
    public static final String KEY_ACTIVITY_TITLE = "title";
    public static final String KEY_ACTIVITY_CATEGORY = "category";

    public static final String KEY_SUB_ACTIVITY_ID = "sub_activity_id";
    public static final String KEY_SUB_ACTIVITY_ACTIVITY_ID = "activity_id";
    public static final String KEY_SUB_ACTIVITY_TITLE = "title";

    public static final String KEY_OPPORTUNITY_ID = "opportunity_id";
    public static final String KEY_OPPORTUNITY_DESCRIPTION = "description";
    public static final String KEY_OPPORTUNITY_NAME = "name";
    public static final String KEY_OPPORTUNITY_VENUE_ID = "venue_id";
    public static final String KEY_OPPORTUNITY_ACTIVITY_ID = "activity_id";
    public static final String KEY_OPPORTUNITY_SUB_ACTIVITY_ID = "sub_activity_id";
    public static final String KEY_OPPORTUNITY_ROOM = "room";
    public static final String KEY_OPPORTUNITY_START_TIME = "start_time";
    public static final String KEY_OPPORTUNITY_END_TIME = "end_time";
    public static final String KEY_OPPORTUNITY_DAY_OF_WEEK = "day_of_week";
    public static final String KEY_OPPORTUNITY_IMAGE_URL = "image_url";
    public static final String KEY_OPPORTUNITY_TAGS = "tags";

    // Create the constants used to differentiate between the different URI requests
    private static final int VENUES = 1;
    private static final int VENUE_ID = 2;
    private static final int ACTIVITIES = 3;
    private static final int ACTIVITY_ID = 4;
    private static final int SUB_ACTIVITIES = 5;
    private static final int SUB_ACTIVITY_ID = 6;
    private static final int OPPORTUNITIES = 7;
    private static final int OPPORTUNITY_ID = 8;
    private static final int OPPORTUNITIES_DISTINCT = 9;

    private static final UriMatcher uriMatcher;

    // Allocate the UriMatcher object, where a URI ending in 'venues' will correspond to a request for all venues,
    // and 'venues' with a trailing '/[rowID]' will represent a single venue row.

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "venues", VENUES);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "venues/#", VENUE_ID);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "activities", ACTIVITIES);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "activities/#", ACTIVITY_ID);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "sub_activities", SUB_ACTIVITIES);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "sub_activities/#", SUB_ACTIVITY_ID);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "opportunities", OPPORTUNITIES);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "opportunities/#", OPPORTUNITY_ID);
        uriMatcher.addURI("com.xoverto.activeaberdeen", "distinctOpportunties", OPPORTUNITIES_DISTINCT);
    }

    DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();

        dbHelper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String defaultSortBy = "";

        // If this is a row query, limit the result set to the passed in row
        switch (uriMatcher.match(uri)) {
            case VENUES:
                qb.setTables(DatabaseHelper.VENUE_TABLE);
                defaultSortBy = KEY_NAME;
                break;
            case VENUE_ID:
                qb.setTables(DatabaseHelper.VENUE_TABLE);
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                defaultSortBy = KEY_NAME;
                break;
            case ACTIVITIES:
                qb.setTables(DatabaseHelper.ACTIVITY_TABLE);
                defaultSortBy = KEY_ACTIVITY_TITLE;
                break;
            case ACTIVITY_ID:
                qb.setTables(DatabaseHelper.ACTIVITY_TABLE);
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                defaultSortBy = KEY_ACTIVITY_TITLE;
                break;
            case SUB_ACTIVITIES:
                qb.setTables(DatabaseHelper.SUB_ACTIVITY_TABLE);
                defaultSortBy = KEY_SUB_ACTIVITY_TITLE;
                break;
            case SUB_ACTIVITY_ID:
                qb.setTables(DatabaseHelper.SUB_ACTIVITY_TABLE);
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                defaultSortBy = KEY_SUB_ACTIVITY_TITLE;
                break;
            case OPPORTUNITIES:
                qb.setTables(DatabaseHelper.OPPORTUNITY_TABLE);
                defaultSortBy = KEY_OPPORTUNITY_NAME;
                break;
            case OPPORTUNITY_ID:
                qb.setTables(DatabaseHelper.OPPORTUNITY_TABLE);
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                defaultSortBy = KEY_OPPORTUNITY_NAME;
                break;
            default: break;
        }

        // If no sort order is specified, sort by name
        String orderBy;
        if(TextUtils.isEmpty(sortOrder)) {
            orderBy = defaultSortBy;
        } else {
            orderBy = sortOrder;
        }

        // Apply the query to the underlying database
        Cursor c;

        if(uriMatcher.match(uri) == OPPORTUNITIES_DISTINCT) {
            c = database.query(true, DatabaseHelper.OPPORTUNITY_TABLE, new String[]{ KEY_ID, KEY_OPPORTUNITY_DAY_OF_WEEK}, null, null, KEY_OPPORTUNITY_DAY_OF_WEEK, null, null, null);
        } else {
            c = qb.query(database,
                    projection,
                    selection, selectionArgs,
                    null, null,
                    orderBy);
        }

        // Register the contexts ContentResolver to be notified if the cursor result set changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return a cursor to the query result
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case VENUES:
            case VENUE_ID: {
                // Insert the new row. The call to the database.insert will return the row number if it is successful.
                long rowID = database.insert(DatabaseHelper.VENUE_TABLE, "venue", values);

                // Return a URI to the newly inserted row on success.
                if(rowID > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI_VENUES, rowID);
                    getContext().getContentResolver().notifyChange(CONTENT_URI_VENUES, null);
                    return newUri;
                }
            }
            break;

            case ACTIVITIES:
            case ACTIVITY_ID: {
                // Insert the new row. The call to the database.insert will return the row number if it is successful.
                long rowID = database.insert(DatabaseHelper.ACTIVITY_TABLE, "activity", values);

                // Return a URI to the newly inserted row on success.
                if(rowID > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI_ACTIVITIES, rowID);
                    getContext().getContentResolver().notifyChange(CONTENT_URI_ACTIVITIES, null);
                    return newUri;
                }
            }
            break;

            case SUB_ACTIVITIES:
            case SUB_ACTIVITY_ID: {
                // Insert the new row. The call to the database.insert will return the row number if it is successful.
                long rowID = database.insert(DatabaseHelper.SUB_ACTIVITY_TABLE, "sub_activity", values);

                // Return a URI to the newly inserted row on success.
                if(rowID > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI_SUB_ACTIVITIES, rowID);
                    getContext().getContentResolver().notifyChange(CONTENT_URI_SUB_ACTIVITIES, null);
                    return newUri;
                }
            }
            break;

            case OPPORTUNITIES:
            case OPPORTUNITY_ID: {
                // Insert the new row. The call to the database.insert will return the row number if it is successful.
                long rowID = database.insert(DatabaseHelper.OPPORTUNITY_TABLE, "opportunities", values);

                // Return a URI to the newly inserted row on success.
                if(rowID > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI_OPPORTUNITIES, rowID);
                    getContext().getContentResolver().notifyChange(CONTENT_URI_OPPORTUNITIES, null);
                    return newUri;
                }
            }
            break;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case VENUES:
                count = database.update(DatabaseHelper.VENUE_TABLE, values, selection, selectionArgs);
                break;

            case VENUE_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.update(DatabaseHelper.VENUE_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;


            case ACTIVITIES:
                count = database.update(DatabaseHelper.ACTIVITY_TABLE, values, selection, selectionArgs);
                break;

            case ACTIVITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.update(DatabaseHelper.ACTIVITY_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;

            case SUB_ACTIVITIES:
                count = database.update(DatabaseHelper.SUB_ACTIVITY_TABLE, values, selection, selectionArgs);
                break;

            case SUB_ACTIVITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.update(DatabaseHelper.SUB_ACTIVITY_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;

            case OPPORTUNITIES:
                count = database.update(DatabaseHelper.OPPORTUNITY_TABLE, values, selection, selectionArgs);
                break;

            case OPPORTUNITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.update(DatabaseHelper.OPPORTUNITY_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case VENUES:
                count = database.delete(DatabaseHelper.VENUE_TABLE, selection, selectionArgs);
                break;

            case VENUE_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.delete(DatabaseHelper.VENUE_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
                break;

            case ACTIVITIES:
                count = database.delete(DatabaseHelper.ACTIVITY_TABLE, selection, selectionArgs);
                break;

            case ACTIVITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.delete(DatabaseHelper.ACTIVITY_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
                break;

            case SUB_ACTIVITIES:
                count = database.delete(DatabaseHelper.SUB_ACTIVITY_TABLE, selection, selectionArgs);
                break;

            case SUB_ACTIVITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.delete(DatabaseHelper.SUB_ACTIVITY_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;

            case OPPORTUNITIES:
                count = database.delete(DatabaseHelper.OPPORTUNITY_TABLE, selection, selectionArgs);
                break;

            case OPPORTUNITY_ID: {
                String segment = uri.getPathSegments().get(1);
                count = database.delete(DatabaseHelper.OPPORTUNITY_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            }
            break;

            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case VENUES: return "vnd.android.cursor.dir/vnd.com.xoverto.activeaberdeen.venues";
            case VENUE_ID: return "vnd.android.cursor.item/vnd.com.xoverto.activeaberdeen.venues";
            case ACTIVITIES: return "vnd.android.cursor.dir/vnd.com.xoverto.activeaberdeen.activities";
            case ACTIVITY_ID: return "vnd.android.cursor.item/vnd.com.xoverto.activeaberdeen.activities";
            case SUB_ACTIVITIES: return "vnd.android.cursor.dir/vnd.com.xoverto.activeaberdeen.sub_activities";
            case SUB_ACTIVITY_ID: return "vnd.android.cursor.item/vnd.com.xoverto.activeaberdeen.sub_activities";
            case OPPORTUNITIES: return "vnd.android.cursor.dir/vnd.com.xoverto.activeaberdeen.opportunities";
            case OPPORTUNITY_ID: return "vnd.android.cursor.item/vnd.com.xoverto.activeaberdeen.opportunities";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // Helper class for opening, creating and managing database version control
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "VenueProvider";
        private static final String DATABASE_NAME = "venues.db";
        private static final int DATABASE_VERSION = 6;
        private static final String VENUE_TABLE = "venues";
        private static final String ACTIVITY_TABLE = "activities";
        private static final String SUB_ACTIVITY_TABLE = "sub_activities";
        private static final String OPPORTUNITY_TABLE = "opportunities";
        private static final String DATABASE_CREATE_VENUE = "create table " + VENUE_TABLE + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_VENUE_ID + " TEXT,"
                + KEY_NAME + " TEXT, "
                + KEY_UPDATED + " INTEGER, "
                + KEY_LOCATION_LAT + " FLOAT, "
                + KEY_LOCATION_LNG + " FLOAT, "
                + KEY_TELEPHONE + " TEXT, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_POSTCODE + " TEXT, "
                + KEY_WEB + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_VENUE_OWNER_SLUG + " TEXT);";

        private static final String DATABASE_CREATE_ACTIVITY =  "create table " + ACTIVITY_TABLE + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_ACTIVITY_ID + " INTEGER, "
                + KEY_ACTIVITY_TITLE + " TEXT, "
                + KEY_ACTIVITY_CATEGORY + " TEXT);";

        private static final String DATABASE_CREATE_SUB_ACTIVITY =  "create table " + SUB_ACTIVITY_TABLE + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_SUB_ACTIVITY_ID + " INTEGER, "
                + KEY_SUB_ACTIVITY_TITLE + " TEXT, "
                + KEY_SUB_ACTIVITY_ACTIVITY_ID + " INTEGER);";

        private static final String DATABASE_CREATE_OPPORTUNITY =  "create table " + OPPORTUNITY_TABLE + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_OPPORTUNITY_ID + " INTEGER, "
                + KEY_OPPORTUNITY_NAME + " TEXT, "
                + KEY_OPPORTUNITY_DESCRIPTION + " TEXT, "
                + KEY_OPPORTUNITY_ACTIVITY_ID + " INTEGER, "
                + KEY_OPPORTUNITY_SUB_ACTIVITY_ID + " INTEGER, "
                + KEY_OPPORTUNITY_VENUE_ID + " INTEGER, "
                + KEY_OPPORTUNITY_ROOM + " TEXT, "
                + KEY_OPPORTUNITY_START_TIME + " TEXT, "
                + KEY_OPPORTUNITY_END_TIME + " TEXT, "
                + KEY_OPPORTUNITY_DAY_OF_WEEK + " TEXT, "
                + KEY_OPPORTUNITY_IMAGE_URL + " TEXT, "
                + KEY_OPPORTUNITY_TAGS + " TEXT);";


        // The underlying database
        private SQLiteDatabase carParkDB;

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_VENUE);
            db.execSQL(DATABASE_CREATE_ACTIVITY);
            db.execSQL(DATABASE_CREATE_SUB_ACTIVITY);
            db.execSQL(DATABASE_CREATE_OPPORTUNITY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + " which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + VENUE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SUB_ACTIVITY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + OPPORTUNITY_TABLE);
            onCreate(db);
        }
    }
}