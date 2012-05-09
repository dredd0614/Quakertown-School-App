package com.optimo.quakertown;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskMainMenuList;
import com.optimo.quakertown.asynctasks.AsyncTaskNotificationChannelList;
import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberSubscriptionList;
import com.optimo.quakertown.asynctasks.AsyncTaskSchoolSettings;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;
import com.optimo.quakertown.database.SchoolAppPhoneEmailDBAdapter;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;
import com.optimo.quakertown.objects.SubscribeListObject;

public class SchoolAppSettingsOptionsActivity extends Activity{

	Resources res = null;
	
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static final String MENUFILE = "AppMenuFile";
	private static final String NOTIFICATIONFILE = "AppNotificationFile";
	private static final String APP_SUBSCRIPTION_LIST_FILE = "AppSubscriptionsListFile";
	
	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	
	boolean asyncMenu = false;
	boolean asyncNotification = false;
	boolean asyncSetting = false;
	boolean asyncSubscriptionList = false;
	
	int subscriptionListNum = -1;

	
	ArrayList<PhoneEmailListObject> phoneNumberList = new ArrayList<PhoneEmailListObject>();

	
	SharedPreferences settings;
	private String titleColor;
	
	protected void onDestroy() {
		super.onDestroy();

		if (dbHelperPhoneEmail != null) {
			dbHelperPhoneEmail.close();
		}
		if (dbHelperChannel != null) {
			dbHelperChannel.close();
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settingsoptions);
		
		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppSettingsOptionsActivity.this);
		dbHelperPhoneEmail.open();
		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppSettingsOptionsActivity.this);
		dbHelperChannel.open();

		settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");
		AppSettingsObject appSettingsObject = null;
		try {
			appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(appSettingsObject.getTitleBar());

		RelativeLayout titlelayoutholder = (RelativeLayout) findViewById(R.id.titlelayoutholder);

		ColorFixer cf = new ColorFixer();
		titleColor = cf.RBGStringToHexString(appSettingsObject.getTitleBarRed(), appSettingsObject.getTitleBarGreen(), appSettingsObject.getTitleBarBlue());

		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));

		res = getResources();
		
		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
		actionTitle.setText(res.getString(R.string.settings));

		Button updateAappButton = (Button) findViewById(R.id.updateappbutton);
		updateAappButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//TODO
				//Need to update database information too
				
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
					new AsyncTaskPhoneNumberSubscriptionList(SchoolAppSettingsOptionsActivity.this, phoneNumberList.get(i).getValue(), phoneNumberList.get(i).getType() ).execute();
					i++;
				}
				
				new AsyncTaskSchoolSettings(SchoolAppSettingsOptionsActivity.this).execute();

				Toast.makeText(SchoolAppSettingsOptionsActivity.this, "Updating app...", Toast.LENGTH_SHORT).show();
			
			}
		});
		updateAappButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		Button aboutEdulertButton = (Button) findViewById(R.id.aboutedulertbutton);
		aboutEdulertButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppSettingsOptionsActivity.this, SchoolAppWebActivity.class);
				myIntent.putExtra("url", Constants.ABOUT_EDULERT);
				startActivity(myIntent);
			}
		});
		aboutEdulertButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

	}
	
	public synchronized void returnSubscriptionList(String result, String phoneOrEmail) {

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
		isFinishedUpdating();

	}
	
	public synchronized void returnSettings(String result) {

		if(!result.equals("ERROR")){

			SharedPreferences settings = null;
			SharedPreferences.Editor editor;
			JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();
			settings = getSharedPreferences(APP_SETTINGS_FILE, 0);
			String storedSettings="";

			storedSettings = settings.getString(this.getString(R.string.JSONString), "");

			if(storedSettings.equals("")){

				editor = settings.edit();
				editor.putString(SchoolAppSettingsOptionsActivity.this.getString(R.string.JSONString), result);
				editor.commit();

				asyncMenu = false;
				asyncNotification = false;
				new AsyncTaskMainMenuList(SchoolAppSettingsOptionsActivity.this).execute();
				new AsyncTaskNotificationChannelList(SchoolAppSettingsOptionsActivity.this).execute();
				
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
					editor.putString(SchoolAppSettingsOptionsActivity.this.getString(R.string.JSONString), result);
					editor.commit();

					asyncMenu = false;
					asyncNotification = false;
					new AsyncTaskMainMenuList(SchoolAppSettingsOptionsActivity.this).execute();
					new AsyncTaskNotificationChannelList(SchoolAppSettingsOptionsActivity.this).execute();
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

		isFinishedUpdating();
	}
	
	public synchronized void returnMainMenu(String result) {
		if(!result.equals("ERROR")){
			SharedPreferences menus;
			SharedPreferences.Editor editor;

			menus = getSharedPreferences(MENUFILE, 0);
			editor = menus.edit();
			editor.putString(SchoolAppSettingsOptionsActivity.this.getString(R.string.JSONString), result);
			editor.commit();
		}

		asyncMenu = true;

		isFinishedUpdating();

	}

	public synchronized void returnNotificationMenu(String result) {

		if(!result.equals("ERROR")){
			SharedPreferences notifications;
			SharedPreferences.Editor editor;

			notifications = getSharedPreferences(NOTIFICATIONFILE, 0);
			editor = notifications.edit();
			editor.putString(SchoolAppSettingsOptionsActivity.this.getString(R.string.JSONString), result);
			editor.commit();
		}

		asyncNotification = true;

		isFinishedUpdating();

	}
	
	private void isFinishedUpdating() {
		if(asyncMenu&&asyncNotification&&asyncSetting&&asyncSubscriptionList){

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
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
				finish();
			}else if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
				Intent myIntent = new Intent(this, SchoolAppTabHostActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
				finish();
			}else{
				Intent myIntent = new Intent(this, SchoolAppTabHostActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
				finish();
			}
			

			Toast.makeText(SchoolAppSettingsOptionsActivity.this, "Successfully Updated the app!", Toast.LENGTH_SHORT).show();
			
		}
	}

}