package com.optimo.quakertown;

import org.json.JSONException;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppTabHostActivity extends TabActivity{


	SharedPreferences settings;
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	AppSettingsObject appSettingsObject = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolapptabhost);

		settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");

		try {
			appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//TODO
		/*
		 * Update app with the server
		 * 	Get Phone Number Subscription List JSON string
		 * 		For each phone|email record in the database
		 * 	Merge all the JSON results
		 * 	Insert/Update/Delete records in the dbHelperChannel database 
		 * 		Doing it this way to not break the notification/phone set-up already used in the app
		 * 
		 */

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, SchoolAppListActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("menuList").setIndicator("Pages",
				res.getDrawable(R.drawable.pageblank3))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, SchoolAppNotificationsListActivity.class);
		spec = tabHost.newTabSpec("notifications").setIndicator("Notifications",
				res.getDrawable(R.drawable.notifblank3))
				.setContent(intent);
		tabHost.addTab(spec);

		if(appSettingsObject.getLevel().equals(Constants.ALL_OPEN)){
			intent = new Intent().setClass(this, SchoolAppSettingsActivity.class);
			spec = tabHost.newTabSpec("settings").setIndicator("Settings",
					res.getDrawable(R.drawable.settingsblank3))
					.setContent(intent);
			tabHost.addTab(spec);
		}else if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
			intent = new Intent().setClass(this, SchoolAppSettingsOptionsActivity.class);
			spec = tabHost.newTabSpec("settings").setIndicator("Settings",
					res.getDrawable(R.drawable.settingsblank3))
					.setContent(intent);
			tabHost.addTab(spec);
		}
		tabHost.setCurrentTab(0);
	}


}
