package com.optimo.quakertown;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskMainMenuList;
import com.optimo.quakertown.asynctasks.AsyncTaskNotificationChannelList;
import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberSubscriptionList;
import com.optimo.quakertown.asynctasks.AsyncTaskSchoolSettings;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;
import com.optimo.quakertown.database.SchoolAppDBAdapter;
import com.optimo.quakertown.database.SchoolAppPhoneEmailDBAdapter;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.ChannelSubscribeObject;
import com.optimo.quakertown.objects.MenuObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;
import com.optimo.quakertown.objects.SubscribeListObject;

public class SchoolAppActivity extends Activity {


	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static final String MENUFILE = "AppMenuFile";
	private static final String NOTIFICATIONFILE = "AppNotificationFile";
	private static final String APP_SUBSCRIPTION_LIST_FILE = "AppSubscriptionsListFile";

	SchoolAppDBAdapter SchoolAppdbHelper;
	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	ArrayList<ChannelSubscribeObject> channelConnectionList = new ArrayList<ChannelSubscribeObject>();
	ArrayList<PhoneEmailListObject> phoneNumberList = new ArrayList<PhoneEmailListObject>();


	SharedPreferences files;

	boolean asyncMenu = false;
	boolean asyncNotification = false;
	boolean asyncSetting = false;
	boolean pastSleepingTime = false;
	boolean asyncSubscriptionList = false;

	int subscriptionListNum = -1;

	protected void onDestroy() {
		super.onDestroy();

		if (SchoolAppdbHelper != null) {
			SchoolAppdbHelper.close();
		}
		if (dbHelperPhoneEmail != null) {
			dbHelperPhoneEmail.close();
		}
		if (dbHelperChannel != null) {
			dbHelperChannel.close();
		}

	}



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SchoolAppdbHelper = new SchoolAppDBAdapter(SchoolAppActivity.this);
		SchoolAppdbHelper.open();

		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppActivity.this);
		dbHelperPhoneEmail.open();

		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppActivity.this);
		dbHelperChannel.open();

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean MobileConnection = false;
		try{
			MobileConnection = connMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().compareTo(NetworkInfo.State.CONNECTED)==0;
		}catch (Exception e){
			MobileConnection = false;
		}
		boolean wifiConnection = false; 
		try{
			wifiConnection = wifi.getWifiState()==WifiManager.WIFI_STATE_ENABLED;
		}catch(Exception e){
			wifiConnection = false;
		}

		if(MobileConnection||wifiConnection){
			
			String KEY = "c2dmPref";
			String REGISTRATION_KEY = "registrationKey";
			SharedPreferences registration;
			registration = getSharedPreferences(KEY, 0);
			String storedRegistration = registration.getString(REGISTRATION_KEY, "");
			if(storedRegistration.equals("")){
				Log.d("no push registration stored","Key: nothing");
				Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
				registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
				registrationIntent.putExtra("sender", "optimo.android@gmail.com");
				startService(registrationIntent);
			}else{
				Log.d("push registration key","Key:"+storedRegistration);
			}

			//files = getSharedPreferences(APP_SETTINGS_FILE, 0);
			/*
			Cursor channelConnectionsCursor = dbHelperChannel.getAllChannelConnections();
			if(!(channelConnectionsCursor.moveToFirst())){
				channelConnectionsCursor.close();
			}else{
				while(!channelConnectionsCursor.isAfterLast()) {
					ChannelSubscribeObject row = new ChannelSubscribeObject();
					row.setRowId(channelConnectionsCursor.getInt(channelConnectionsCursor.getColumnIndex(SchoolAppChannelDBAdapter.ROW_ID)));
					row.setChannelId(channelConnectionsCursor.getString(channelConnectionsCursor.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELID)));
					row.setName(channelConnectionsCursor.getString(channelConnectionsCursor.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELNAME)));
					row.setPhoneEmailId(channelConnectionsCursor.getLong(channelConnectionsCursor.getColumnIndex(SchoolAppChannelDBAdapter.PHONEEMAILID)));

					channelConnectionList.add(row);
					channelConnectionsCursor.moveToNext();
				}
			}
			channelConnectionsCursor.close();
			 */
			Log.d("phonelist", "here");

			Cursor phoneOrEmailCursor = dbHelperPhoneEmail.getAllPhoneEmails();
			if(!(phoneOrEmailCursor.moveToFirst())){
				phoneOrEmailCursor.close();
				asyncSubscriptionList = true;
			}else{
				while(!phoneOrEmailCursor.isAfterLast()) {
					PhoneEmailListObject row = new PhoneEmailListObject();
					row.setRowId(phoneOrEmailCursor.getInt(phoneOrEmailCursor.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ROW_ID)));
					row.setValue(phoneOrEmailCursor.getString(phoneOrEmailCursor.getColumnIndex(SchoolAppPhoneEmailDBAdapter.VALUE)));
					row.setType(phoneOrEmailCursor.getString(phoneOrEmailCursor.getColumnIndex(SchoolAppPhoneEmailDBAdapter.TYPE)));		
					row.setIsTextable(phoneOrEmailCursor.getInt(phoneOrEmailCursor.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISTEXTABLE)));		
					row.setIsCallable(phoneOrEmailCursor.getInt(phoneOrEmailCursor.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISCALLABLE)));		

					phoneNumberList.add(row);
					phoneOrEmailCursor.moveToNext();
				}
			}
			phoneOrEmailCursor.close();
			subscriptionListNum = phoneNumberList.size();
			Log.d("subscriptionListNum START", ""+subscriptionListNum);
			int i = 0;
			while(i<phoneNumberList.size()){
				new AsyncTaskPhoneNumberSubscriptionList(SchoolAppActivity.this, phoneNumberList.get(i).getValue(), phoneNumberList.get(i).getType() ).execute();
				i++;
			}

			new AsyncTaskSchoolSettings(SchoolAppActivity.this).execute();

			Handler handler = new Handler(); 
			handler.postDelayed(new Runnable() { 
				public void run() { 
					pastSleepingTime = true;

					moveToMainActivity();
				} 
			}, 2000); 


		}else{
			Toast.makeText(SchoolAppActivity.this, "You need a mobile connection", Toast.LENGTH_LONG).show();
			finish();
		}

		moveToMainActivity();
	}

	private void moveToMainActivity() {

		//		Log.d("asyncMenu",asyncMenu+"a");
//		Log.d("asyncNotification",asyncNotification+"a");
//		Log.d("asyncSetting",asyncSetting+"a");
//		Log.d("pastSleepingTime",pastSleepingTime+"a");
//		Log.d("asyncSubscriptionList",asyncSubscriptionList+"a");

		if(asyncMenu&&asyncNotification&&asyncSetting&&pastSleepingTime&&asyncSubscriptionList){

			SharedPreferences settings;

			settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

			JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

			String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");
			AppSettingsObject appSettingsObject = null;
			try {
				appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			if(appSettingsObject.getLevel().equals(Constants.WEBSITE_NAVIGATION_ONLY)){
				Intent myIntent = new Intent(this, SchoolAppListActivity.class);
				startActivity(myIntent);
				finish();
			}else if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
				Intent myIntent = new Intent(this, SchoolAppTabHostActivity.class);
				startActivity(myIntent);
				finish();
			}else{
				Intent myIntent = new Intent(this, SchoolAppTabHostActivity.class);
				startActivity(myIntent);
				finish();
			}

		}
	}

	public void printArrayList(ArrayList<MenuObject> mList){
		Iterator<MenuObject> i = mList.iterator();
		while(i.hasNext()){
			MenuObject m = (MenuObject) i.next();
			if(m.getType().equals("menu"))
				printArrayList(m.getMenuObjectArrayList());
			Log.d("MenuObject:", m.toString());
		}
	}

	public ArrayList<MenuObject> orderArrayList(ArrayList<MenuObject> mList){

		/*
		Iterator<MenuObject> i = mList.iterator();
		while(i.hasNext()){
			MenuObject m = i.next();


		}
		 */

		return mList;
	}


	public synchronized void returnMainMenu(String result) {
		if(!result.equals("ERROR")){
			SharedPreferences menus;
			SharedPreferences.Editor editor;

			Log.d("MENU OBJECT", result+"??????");

			
			menus = getSharedPreferences(MENUFILE, 0);
			editor = menus.edit();
			editor.putString(SchoolAppActivity.this.getString(R.string.JSONString), result);
			editor.commit();
		}

		asyncMenu = true;

		moveToMainActivity();

	}

	public synchronized void returnNotificationMenu(String result) {

		if(!result.equals("ERROR")){
			SharedPreferences notifications;
			SharedPreferences.Editor editor;

			Log.d("NOTIFICATION OBJECT", result+"??????");

			
			notifications = getSharedPreferences(NOTIFICATIONFILE, 0);
			editor = notifications.edit();
			editor.putString(SchoolAppActivity.this.getString(R.string.JSONString), result);
			editor.commit();
		}

		asyncNotification = true;

		moveToMainActivity();

	}

	public synchronized void returnSubscriptionList(String result, String phoneOrEmail) {
				Log.d("asyncsubscribe",result+"a");

		if(!result.equals("ERROR")&&!result.equals("[]")){

			ArrayList<SubscribeListObject> globalArrayMenuList = new ArrayList<SubscribeListObject>(); 

			JSONObjectExtracter jsonOE = new JSONObjectExtracter();
			try {
				globalArrayMenuList = jsonOE.parseSubscriptionListJSONString(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int i = 0;
			while(i<phoneNumberList.size()){
				if(phoneNumberList.get(i).getValue().equals(phoneOrEmail)){
					break;
				}
				i++;
			}

			int j = 0;
			while(j<globalArrayMenuList.size()){
				Cursor channelConnectionsCursor = dbHelperChannel.getChannelConnection(globalArrayMenuList.get(j).getChannelId(),globalArrayMenuList.get(j).getTitle(), phoneNumberList.get(i).getRowId());	
				if(!channelConnectionsCursor.moveToFirst()){
					//TODO add to database
					channelConnectionsCursor.close();
					dbHelperChannel.createChannelConnection(globalArrayMenuList.get(j).getChannelId(),globalArrayMenuList.get(j).getTitle(), phoneNumberList.get(i).getRowId());
				}else{
					channelConnectionsCursor.close();
				}
				j++;
			}
			
			SharedPreferences menus;
			SharedPreferences.Editor editor;

			menus = getSharedPreferences(APP_SUBSCRIPTION_LIST_FILE, 0);
			editor = menus.edit();
			editor.putString(phoneOrEmail, result);
			editor.commit();
		}
		subscriptionListNum--;
		Log.d("phoene/emails ", "a"+phoneOrEmail);

		Log.d("subscriptionListNum minusone", "a"+subscriptionListNum);

		if(subscriptionListNum==0){
			asyncSubscriptionList = true;
			Log.d("subscriptionListNum zero", "a"+subscriptionListNum);
		}
		moveToMainActivity();

	}

	public synchronized void returnSettings(String result) {
				Log.d("asyncsettings",result+"a");

		if(!result.equals("ERROR")){

			SharedPreferences settings = null;
			SharedPreferences.Editor editor;
			JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();
			settings = getSharedPreferences(APP_SETTINGS_FILE, 0);
			String storedSettings="";

			storedSettings = settings.getString(this.getString(R.string.JSONString), "");

			if(storedSettings.equals("")){

				editor = settings.edit();
				editor.putString(SchoolAppActivity.this.getString(R.string.JSONString), result);
				editor.commit();

				asyncMenu = false;
				asyncNotification = false;
				new AsyncTaskMainMenuList(SchoolAppActivity.this).execute();
				new AsyncTaskNotificationChannelList(SchoolAppActivity.this).execute();


			}else{
				AppSettingsObject storedSettingsObject = null;
				AppSettingsObject newSettingsObject = null;
				try {
					storedSettingsObject = jsonOESettings.parseSettingsJSONString(storedSettings);
					newSettingsObject = jsonOESettings.parseSettingsJSONString(result);

				} catch (JSONException e1) {
					asyncMenu = true;
					asyncNotification = true;
					storedSettingsObject = new AppSettingsObject();
					storedSettingsObject.setVersion("0");
					newSettingsObject = new AppSettingsObject();
					newSettingsObject.setVersion("0");
				}

				if(storedSettingsObject.getVersion()!=newSettingsObject.getVersion()){
					editor = settings.edit();
					editor.putString(SchoolAppActivity.this.getString(R.string.JSONString), result);
					editor.commit();

					asyncMenu = false;
					asyncNotification = false;
					new AsyncTaskMainMenuList(SchoolAppActivity.this).execute();
					new AsyncTaskNotificationChannelList(SchoolAppActivity.this).execute();
				}else{
					asyncMenu = true;
					asyncNotification = true;
				}

			}

		}else{
			Toast.makeText(this, "You need a mobile connection for this app.", Toast.LENGTH_LONG).show();
			finish();

		}
		asyncSetting = true;

		moveToMainActivity();
	}
}







