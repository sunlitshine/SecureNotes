package com.shansong.securenotes.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.shansong.securenotes.database.DatabaseHelper;
import com.shansong.securenotes.utils.APPEnv;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

import java.util.HashMap;

public class TitleListProvider extends ContentProvider {

    private static final String TAG = TitleListProvider.class.getName();

    private DatabaseHelper mDbHelper;
    private static HashMap<String, String> NOTES_PROJECTION_MAP;


    private static final String AUTHORITY = "com.shansong.securenotes.provider.TitleListProvider";
    private static final String BASE_PATH = "/titlelist";
    static final String URL = "content://" + AUTHORITY + BASE_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final int NOTE_TITLES = 1;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTE_TITLES);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = DatabaseHelper.getInstance(getContext());
        return false;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(mDbHelper.S_TABLE_SECURE_NOTES_NAME);

        projection = new String[]{mDbHelper.KEY_TITLE};//only the titles can be retrieved by other applications

        final String userName = selectionArgs[0];
        final String password = selectionArgs[1];

        if(uriMatcher.match(uri)==NOTE_TITLES) {
            queryBuilder.appendWhere( mDbHelper.KEY_USERNAME + "=" + "'"+userName+"'");
        }

        final SQLiteDatabase db = mDbHelper.getReadableDatabase(mDbHelper.getSecurePassword());


        Cursor cursor = null;
        if(mDbHelper.verifyPassword(userName, password, false)){//only when verification is successful, we will

            if(APPEnv.DEBUG){
                Log.i(TAG, "proceed to retrieve the title list ");
            }
            // proceed to retrieve the title list

            cursor = queryBuilder.query(db, projection, null, null, null, null, sortOrder);

            if(APPEnv.DEBUG){
                Log.i(TAG, "cursor.getcount: "+cursor.getCount());
            }
            /**
             * register to watch a content URI for changes
             */
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        }

        return cursor;
    }
    
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NOTE_TITLES:
                return "vnd.android.cursor.dir/titlelist";
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "Not supported");
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "Not supported");
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "Not supported");
        }
        return 0;
    }
}
