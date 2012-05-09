package com.optimo.quakertown.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchoolAppPhoneEmailDBAdapter {
	
    public static final String ROW_ID = "_id";  //Long
    public static final String VALUE = "value";  //String
    public static final String TYPE = "type";  //String
    public static final String ISTEXTABLE = "isTextable"; //Int 0/1  (no/yes)
    public static final String ISCALLABLE = "isCallable";//Int 0/1  (no/yes)
    
    private static final String DATABASE_TABLE = "phoneEmail";

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
    public SchoolAppPhoneEmailDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the PhoneEmails database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public SchoolAppPhoneEmailDBAdapter open() throws SQLException {
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
     * Create a new PhoneEmail. If the PhoneEmail is successfully created return the new
     * rowId for that PhoneEmail, otherwise return a -1 to indicate failure.
     * @return rowId or -1 if failed
     */
    public long createPhoneEmail(String value, String type, int isTextable, int isCallable){
        ContentValues initialValues = new ContentValues();
        initialValues.put(VALUE, value);
        initialValues.put(TYPE, type);
        initialValues.put(ISTEXTABLE, isTextable);
        initialValues.put(ISCALLABLE, isCallable);

        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the PhoneEmail with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deletePhoneEmail(long rowId) {

        return this.mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0; 
    }

    /**
     * Return a Cursor over the list of all PhoneEmails in the database
     * 
     * @return Cursor over all PhoneEmails
     */
    public Cursor getAllPhoneEmails() {

        return this.mDb.query(DATABASE_TABLE, new String[] { ROW_ID,
        		VALUE, TYPE, ISTEXTABLE, ISCALLABLE}, null, null, null ,null, null );
    }

    /**
     * Return a Cursor positioned at the PhoneEmail that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching PhoneEmail, if found
     * @throws SQLException if PhoneEmail could not be found/retrieved
     */
    public Cursor getPhoneEmail(long rowId) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
        		VALUE, TYPE, ISTEXTABLE, ISCALLABLE}, ROW_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the PhoneEmail.
     * 
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updatePhoneEmail(long rowId, String value, String type, int isTextable, int isCallable){
        ContentValues args = new ContentValues();
        args.put(VALUE, value);
        args.put(TYPE, type);
        args.put(ISTEXTABLE, isTextable);
        args.put(ISCALLABLE, isCallable);

        return this.mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}