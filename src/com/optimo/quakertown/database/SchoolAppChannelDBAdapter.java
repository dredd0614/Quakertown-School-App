package com.optimo.quakertown.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchoolAppChannelDBAdapter {
	
    public static final String ROW_ID = "_id";  //Int
    public static final String CHANNELID = "channelId";  //String
    public static final String CHANNELNAME = "channelName";  //String
    public static final String PHONEEMAILID = "phoneEmailId";  //Long

    private static final String DATABASE_TABLE = "channelList";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, SchoolAppDBAdapter.DATABASE_NAME, null, SchoolAppDBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx
     *            the Context within which to work
     */
    public SchoolAppChannelDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the ChannelConnections database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public SchoolAppChannelDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }

    /**
     * Create a new ChannelConnection. If the ChannelConnection is successfully created return the new
     * rowId for that ChannelConnection, otherwise return a -1 to indicate failure.
     * @return rowId or -1 if failed
     */
    public long createChannelConnection(String channelId, String channelName, long phoneEmailId){
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHANNELID, channelId);
        initialValues.put(CHANNELNAME, channelName);
        initialValues.put(PHONEEMAILID, phoneEmailId);

        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the ChannelConnection with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteChannelConnection(long rowId) {

        return this.mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0; 
    }

    /**
     * Return a Cursor over the list of all ChannelConnections in the database
     * 
     * @return Cursor over all ChannelConnections
     */
    public Cursor getAllChannelConnections() {

        return this.mDb.query(DATABASE_TABLE, new String[] { ROW_ID,
        		CHANNELID, CHANNELNAME, PHONEEMAILID}, null, null, null ,null, null );
    }

    /**
     * Return a Cursor positioned at the ChannelConnection that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching ChannelConnection, if found
     * @throws SQLException if ChannelConnection could not be found/retrieved
     */
    public Cursor getChannelConnection(long rowId) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
        		CHANNELID, CHANNELNAME, PHONEEMAILID}, ROW_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor positioned at the ChannelConnection that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching ChannelConnection, if found
     * @throws SQLException if ChannelConnection could not be found/retrieved
     */
    public Cursor getChannelConnection(String channelId, String channelName, long phoneEmailId) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
        		CHANNELID, CHANNELNAME, PHONEEMAILID}, 
        			CHANNELID + "='" + channelId +"' AND " +CHANNELNAME + "='" + channelName +"' AND " +PHONEEMAILID + "='" + phoneEmailId+"'",
        			null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor getChannelSubscribersList(String channelId) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
        		CHANNELID, CHANNELNAME, PHONEEMAILID}, 
        			CHANNELID + "='" + channelId +"'",
        			null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor getChannelSubscribersListFromPhoneEmailId(long phoneEmailId) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
        		CHANNELID, CHANNELNAME, PHONEEMAILID}, 
        		PHONEEMAILID + "='" + phoneEmailId +"'",
        			null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the ChannelConnection.
     * 
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateChannelConnection(long rowId, String channelId, String channelName, long phoneEmailId){
        ContentValues args = new ContentValues();
        args.put(CHANNELID, channelId);
        args.put(CHANNELNAME, channelName);
        args.put(PHONEEMAILID, phoneEmailId);

        return this.mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}