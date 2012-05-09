package com.optimo.quakertown.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


//Not being used

public class PhoneNumberDBAdapter {



	// Database fields
	public static final String KEY_ROWID = "_id";
	public final String NAME = "name";
	public final String PHONENUMBER = "phoneNumber";


	private static final String DATABASE_TABLE = "phoneNumbers";
	private Context context;
	private SQLiteDatabase database;
	private PhoneNumberDBHelper dbHelper;

	public PhoneNumberDBAdapter(Context context) {
		this.context = context;
	}

	public PhoneNumberDBAdapter open() throws SQLException {
		dbHelper = new PhoneNumberDBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}


	/**
	 * Create a new PhoneNumber If the phoneNumber is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */

	public long createPhoneNumber(String name, String phoneNumber) {
		ContentValues initialValues = createContentValues(name, phoneNumber);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}


	/**
	 * Update the phoneNumber
	 */

	public boolean updatePhoneNumber(long rowId, String name, String phoneNumber) {
		ContentValues updateValues = createContentValues(name, phoneNumber);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}


	/**
	 * Deletes phoneNumber
	 */

	public boolean deletePhoneNumber(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}


	/**
	 * Return a Cursor over the list of all phoneNumber in the database
	 * 
	 * @return Cursor over all notes
	 */
	/*
	FROM_LANGUAGE, TO_LANGUAGE, ORIGINAL_TEXT, RESULT_TEXT, IS_FAVORITE, IS_FROM_PHOTO, IS_FROM_CAMERA, AUDIO_PATH
	fromLanguage, toLanguage, originalText, resultText, isFavorite, isFromPhoto, isFromCamera, audioPath, myphoneNumbers
	 */

	public Cursor fetchAllPhoneNumbers() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				NAME, PHONENUMBER}, null, null, null,
				null, null);
	}

	/*
	public Cursor fetchAllFavoritephoneNumbers() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				NAME, PHONENUMBER}, IS_FAVORITE + "=" + 1, null, null,
				null, null);
	}
	 */

	public Cursor getNumberOfRows(){
		Cursor mCursor =  database.rawQuery("SELECT COUNT(*) AS NumOfRows FROM "+DATABASE_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Return a Cursor positioned at the defined phoneNumber
	 */

	public Cursor fetchPhoneNumber(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, NAME, PHONENUMBER},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String fromLanguage, String toLanguage) {
		ContentValues values = new ContentValues();
		values.put(NAME, fromLanguage);
		values.put(PHONENUMBER, toLanguage);

		return values;
	}

	public int deleteAllPhoneNumbers() {
		return database.delete(DATABASE_TABLE, null, null);	
	}

	public void deleteTable(){
		database.execSQL("DROP TABLE IF EXISTS phoneNumbers");
	}


}
