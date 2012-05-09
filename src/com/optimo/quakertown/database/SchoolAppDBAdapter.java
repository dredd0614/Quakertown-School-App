package com.optimo.quakertown.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchoolAppDBAdapter {

	public static final String DATABASE_NAME = "schoolApp";

	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_PHONE_EMAIL = "create table phoneEmail (_id integer primary key autoincrement, " 
		+ SchoolAppPhoneEmailDBAdapter.VALUE+ " TEXT,"
		+ SchoolAppPhoneEmailDBAdapter.TYPE+ " TEXT,"
		+ SchoolAppPhoneEmailDBAdapter.ISTEXTABLE+ " INTEGER,"
		+ SchoolAppPhoneEmailDBAdapter.ISCALLABLE+ " INTEGER" + ");";
	
	private static final String CREATE_TABLE_CHANNEL = "create table channelList (_id integer primary key autoincrement, " 
		+ SchoolAppChannelDBAdapter.CHANNELID+ " TEXT," 
		+ SchoolAppChannelDBAdapter.CHANNELNAME+ " TEXT," 
		+ SchoolAppChannelDBAdapter.PHONEEMAILID+ " INTEGER" + ");";


	private final Context context; 
	private DatabaseHelper DBHelper;
	/**
	 * Constructor
	 * @param ctx
	 */
	public SchoolAppDBAdapter(Context ctx)
	{
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper 
	{
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(CREATE_TABLE_PHONE_EMAIL);
			db.execSQL(CREATE_TABLE_CHANNEL);      
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
				int newVersion) 
		{               
			// Adding any table mods to this guy here
		}
	} 

	/**
	 * open the db
	 * @return this
	 * @throws SQLException
	 * return type: DBAdapter
	 */
	public SchoolAppDBAdapter open() throws SQLException 
	{
		this.DBHelper.getWritableDatabase();
		System.out.println("creating!");

		return this;
	}

	/**
	 * close the db 
	 * return type: void
	 */
	public void close() 
	{
		this.DBHelper.close();
	}
}
