package com.shansong.securenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.shansong.securenotes.data.SecureNote;
import com.shansong.securenotes.data.UserInfo;
import com.shansong.securenotes.utils.APPEnv;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sunshine on 7/5/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = DatabaseHelper.class.getName();

    private static DatabaseHelper instance;

    //Database version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String CONSTANT_DATABASE_NAME = "SecureNotesDb";

    //TODO
    /**
     *
     * The first time you create the database you have to create a random mPassword.
     You store this mPassword in the Keystore.
     Whenever you open the app you read the mPassword from the keystore and use it for connecting to the database.
     */
    private static String mPassword ="1234";

    //Table names
    private static final String S_TABLE_USER_INFO_NAME = "__1";
    private static final String S_TABLE_SECURE_NOTES_NAME = "__2";

    // Common column names
    private static final String KEY_USERNAME = "username";

    //UserInfo Table - column names
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGEDIN = "loggedin";

    //SecureNote Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CREATED_AT = "created_at";

    // Table Create Statements
    // UserInfo table create statement
    private static final String CREATE_TABLE_USER_INFO = "CREATE TABLE " + S_TABLE_USER_INFO_NAME + "("
            + KEY_USERNAME + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_IS_LOGGEDIN + " INTEGER" + ")";

    // SecureNote table create statement
    private static final String CREATE_TABLE_SECURE_NOTE = "CREATE TABLE " + S_TABLE_SECURE_NOTES_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_CONTENT + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";



    public static DatabaseHelper getInstance(final Context context){
        if(instance ==null){
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(final Context context){
        super(context, CONSTANT_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [onCreate]");
            Log.d(TAG, "Create UserInfo table: "+ CREATE_TABLE_USER_INFO);
            Log.d(TAG, "Create Secure Note table: "+ CREATE_TABLE_SECURE_NOTE);
        }
        // creating required tables
        db.execSQL(CREATE_TABLE_USER_INFO);
        db.execSQL(CREATE_TABLE_SECURE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [onUpgrade]");
        }
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + S_TABLE_USER_INFO_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + S_TABLE_SECURE_NOTES_NAME);

        // create new tables
        onCreate(db);
    }

    /**
     * Add an item in the specific table
     * @param values
     * @param tableName
     */
    public long createItem(final String tableName, final ContentValues values ){

        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [createItem]");
        }
        SQLiteDatabase db = this.getWritableDatabase(mPassword);

        //insert row
        return db.insert(tableName, null, values);
    }


    /**
     * Delete an item from the SQLiteDatabse with the given unique key
     * @param tableName
     * @param uniqueKey
     * @return
     */

    public int deleteItem(final String tableName,final long uniqueKey){

        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [deleteItem]");
        }
        SQLiteDatabase db = this.getWritableDatabase(mPassword);

        return db.delete(tableName, KEY_ID + " = ?",
                new String[] { String.valueOf(uniqueKey) });
    }

    /**
     * Delete all the items in the given table
     *
     * @param tableName
     * @return
     */
    public int deleteAll(final String tableName){
        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [deleteAll]");
        }
        SQLiteDatabase db = this.getWritableDatabase(mPassword);
        return db.delete(tableName, null, null);
    }


    /**
     * Close database
     */

    public void closeDB() {
        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [closeDB]");
        }
        SQLiteDatabase db = this.getReadableDatabase(mPassword);
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Get datetime
     * */
    public String getDateTime() {
        if(APPEnv.DEBUG){
            Log.d(TAG, "DatabaseHelper - [getDateTime]");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void setDatabasePassword(final String dbPW){
        this.mPassword = dbPW;

    }


    //-----------------------------User Info related functions --------------------------------/
    /**
     * Creating a UserInfo
     *
     * return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long createUser(final UserInfo userInfo) {

        final boolean ifExists = checkIfUserExists(userInfo.getUserName());

        if(ifExists){
            return -1; //indicates that cannot create the user
        }else{
            //if the user does not exist and after the user is created, it will auto update the user login state and
            // log in the user, so the state is changed to logged in automatically
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, userInfo.getUserName());
            values.put(KEY_PASSWORD, userInfo.getPassword());
            values.put(KEY_IS_LOGGEDIN, 1);

            // insert row
            return createItem(S_TABLE_USER_INFO_NAME, values);
        }
    }

    /**
     * Get the username of the current logged in user
     * @return
     */
    public String getCurrentUserName(){
        String userName = "";
        Cursor cursor = null;

        try{
//            final String selectQuery = "SELECT * FROM " + S_TABLE_USER_INFO_NAME +;

            final String selectQuery = "SELECT " + KEY_USERNAME + " FROM " + S_TABLE_USER_INFO_NAME
                    +" WHERE "+ KEY_IS_LOGGEDIN +"=" + 1;

            if(APPEnv.DEBUG){
                Log.i(TAG, "getCurrentUserName SQL Command: "+ selectQuery);
            }
            //TODO Secure the mPassword
            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if(cursor!=null && cursor.getCount()>0){
                if(APPEnv.DEBUG){
                    Log.d(TAG, "User(s) available in the database");
                    Log.d(TAG, "cursor.getCount(): "+ cursor.getCount());
                }
                cursor.moveToFirst();
                userName = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));

            }else{
                if(APPEnv.DEBUG){
                    Log.d(TAG, "No user(s) in the database");
                }
            }

        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        if(APPEnv.DEBUG){
            Log.d(TAG, "userName: "+userName);
        }
        return userName;
    }

    /**
     * Verify the mPassword for login.
     */
    public boolean verifyPassword(final String userName, final String password ) {
        //TODO find a secure way for the mPassword
        boolean isSuccess = false;
        Cursor cursor = null;
        try {
            if(checkIfUserExists(userName)){

                SQLiteDatabase db = this.getWritableDatabase(DatabaseHelper.mPassword);

                //get the pw for the specific user
                final String selectQuery = "SELECT " + KEY_PASSWORD + " FROM " + S_TABLE_USER_INFO_NAME
                        +" WHERE "+ KEY_USERNAME +"=" + "'"+userName+"'";
                if(APPEnv.DEBUG){
                    Log.e(TAG, "getLoggedInState SQL Command; "+ selectQuery);
                }

                String sPW = "";

                cursor = db.rawQuery(selectQuery, null);
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    sPW = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
                }

                //verify the pw
                isSuccess = sPW.equals(password);

                ContentValues values = new ContentValues();
                values.put(KEY_IS_LOGGEDIN, isSuccess);
                db.update(S_TABLE_USER_INFO_NAME, values, KEY_USERNAME + " = ?", new String[] { userName });
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }

        return isSuccess;
    }

    /**
     * Logout user
     */
    public void logOutUser(final String userName ) {
        //TODO find a secure way for the mPassword

        SQLiteDatabase db = this.getWritableDatabase(mPassword);

        if(checkIfUserExists(userName)){
            ContentValues values = new ContentValues();
            values.put(KEY_IS_LOGGEDIN, 0);
            db.update(S_TABLE_USER_INFO_NAME, values, KEY_USERNAME + " = ?",
                    new String[] { userName });

        }
    }

    /**
     * Get log in state for a specific user
     */
    public boolean getLoggedInState(final String userName) {

        boolean isLoggedin = false;
        Cursor cursor = null;

        try{
            String selectQuery = "SELECT  * FROM " + S_TABLE_USER_INFO_NAME + " WHERE "
                    + KEY_USERNAME + "=" + "'"+userName+"'";

            if(APPEnv.DEBUG){
                Log.e(TAG, "getLoggedInState SQL Command; "+ selectQuery);
            }


            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
            }

            cursor.close();

            isLoggedin = (cursor.getInt(cursor.getColumnIndex(KEY_IS_LOGGEDIN)) == 1)? true : false;

        }finally {
            if(cursor != null){
                cursor.close();
            }
        }

        if(APPEnv.DEBUG){
            Log.d(TAG, "User "+ userName+" loggedin : "+ isLoggedin);
        }
        return isLoggedin;
    }


    /**
     * Check if user exists
     */
    public boolean checkIfUserExists(final String userName) {
        if(APPEnv.DEBUG){
            Log.d(TAG, "[checkIfUserExists] for user:"+ userName);
        }

        Cursor cursor = null;
        boolean ifExists = false;
        try{
            String selectQuery = "SELECT  * FROM " + S_TABLE_USER_INFO_NAME + " WHERE "
                    + KEY_USERNAME + "=" + "'"+userName+"'";

            if(APPEnv.DEBUG){
                Log.d(TAG, "SQL Command; "+ selectQuery);
            }

            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.getCount() > 0) {
                ifExists = true;
            }

        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        if(APPEnv.DEBUG){
            Log.d(TAG, "[IfUserExists] : "+ ifExists);
        }
        return ifExists;

    }


    //--------------------------- Secure Notes related functions ----------------------------/

    /**
     * Add secure note
     */
    public void createSecureNote(final SecureNote note) {
        if(APPEnv.DEBUG){
            Log.i(TAG, "CreateSecureNote ");
        }

        ContentValues values = new ContentValues();
        //auto assigned value
        values.put(KEY_CREATED_AT, getDateTime());

        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_USERNAME, getCurrentUserName());


        // insert row
        createItem(S_TABLE_SECURE_NOTES_NAME,  values);

    }

    /**
     * Get a secure note
     */
    public SecureNote getSecureNote(final long keyId ) {

        Cursor cursor = null;
        SecureNote note = new SecureNote();

        try{

            String selectQuery = "SELECT  * FROM " + S_TABLE_SECURE_NOTES_NAME+ " WHERE "
                    + KEY_ID + "=" + keyId;

            if(APPEnv.DEBUG){
                Log.e(TAG, "GetSecureNote SQL Command: "+ selectQuery);
            }

            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();

                note.setId(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                note.setDateTime(cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            }else{
                Log.d(TAG, "No secure note found for this ID");
            }


        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return note;

    }

    /**
     * Update a secure note tile/content/datetime. The other fields are not updatable
     */
    public int updateSecureNote(final SecureNote note) {
        SQLiteDatabase db = this.getWritableDatabase(mPassword);

        ContentValues values = new ContentValues();
        //the username should remain the same, the updatable fields are as follows
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(S_TABLE_SECURE_NOTES_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }

    /**
     * Delete a secure note
     *
     */

    public void deleteSecureNote(long secureNoteId) {
        deleteItem(S_TABLE_SECURE_NOTES_NAME, secureNoteId);
    }

    /**
     * Delete all secure notes
     */
    public void deleteAllSecureNotes() {
        deleteAll(S_TABLE_SECURE_NOTES_NAME);
    }

    /**
     * Get all secure notes
     * */
    public List<SecureNote> getSecureNotesList(final String currentUser) {
        List<SecureNote> secureNotes = new ArrayList<SecureNote>();
        Cursor cursor = null;

        try{

            final String selectQuery = "SELECT  * FROM " + S_TABLE_SECURE_NOTES_NAME+ " WHERE "
                    + KEY_USERNAME + "=" +  "'"+currentUser+"'";
            if(APPEnv.DEBUG){
                Log.i(TAG, "getSecureNotesList SQL Command; "+ selectQuery);
            }

            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if(cursor!=null){
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        SecureNote note = new SecureNote();
                        note.setId(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                        note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                        note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                        note.setDateTime(cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));
                        note.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));

                        // adding to tags list
                        secureNotes.add(note);
                    } while (cursor.moveToNext());
                }
            }
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return secureNotes;
    }

    /**
     * Get the secure note title list
     * */
    public List<String> getSecureNotesTitleList(final String currentUser) {

        List<String> titleList = new ArrayList<String>();
        Cursor cursor = null;

        try{
            final String selectQuery = "SELECT  * FROM " + S_TABLE_SECURE_NOTES_NAME+ " WHERE "
                    + KEY_USERNAME + "=" +  "'"+currentUser+"'";

            if(APPEnv.DEBUG){
                Log.i(TAG, "getSecureNotesTitleList SQL Command; "+ selectQuery);
            }

            SQLiteDatabase db = this.getReadableDatabase(mPassword);
            cursor = db.rawQuery(selectQuery, null);

            if(cursor!=null){
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                        // adding to title list
                        titleList.add(title);
                    } while (cursor.moveToNext());
                }
            }
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return titleList;
    }


}
