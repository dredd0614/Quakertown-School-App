

package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskLoginGetNotificationUserChannelList;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppLoginActivity extends Activity{

	SchoolAppLoginActivity sALA;
	private Handler mHandler = new Handler();

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static final String APP_USER_NOTIFICATIONS_FILE = "AppUserNotificationsFile";

	SharedPreferences.Editor editor;
	SharedPreferences settings;

	private String titleColor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);

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

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(appSettingsObject.getTitleBar());

		RelativeLayout titlelayoutholder = (RelativeLayout) findViewById(R.id.titlelayoutholder);

		ColorFixer cf = new ColorFixer();
		titleColor = cf.RBGStringToHexString(appSettingsObject.getTitleBarRed(), appSettingsObject.getTitleBarGreen(), appSettingsObject.getTitleBarBlue());

		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));


		sALA = this;

		//Bundle extras = getIntent().getExtras();

		//TextView topBarCookieTrail = (TextView) findViewById(R.id.topBarCookieTrail);


		final EditText usernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);	
		final EditText passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);	

		Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
		subscribeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				if(usernameEditText.getText().toString().equals("")||passwordEditText.getText().toString().equals("")){
					Toast.makeText(SchoolAppLoginActivity.this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
				}else{
					String username="";
					String password = "";
					username = usernameEditText.getText().toString();
					password = passwordEditText.getText().toString();
					new AsyncTaskLoginGetNotificationUserChannelList(sALA, username, password).execute();
				}
			}

		});
		subscribeButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));
		subscribeButton.setTextColor(getResources().getColor(R.color.white));

	}

	public void onResume(){
		super.onResume();

	//	EditText usernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);	
	//	EditText passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);	

	//	usernameEditText.setText("");
	//	passwordEditText.setText("");

	}

	public void returnLoginResult(String result, String token){
		Log.d("Subscribe Response: ",result);
		if(result.contains("ERROR")||result.equals("{}")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(sALA);
					helpBuilder.setTitle("Login");
					helpBuilder.setMessage("Invalid Username/Password");

					helpBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog helpDialog = helpBuilder.create();
					helpDialog.show();
				}
			});

		}else {
			//	new AsyncTaskNotificationUserChannelList(sALA, result ).execute();

			SharedPreferences menus;
			SharedPreferences.Editor editor;


			menus = getSharedPreferences(APP_USER_NOTIFICATIONS_FILE, 0);
			editor = menus.edit();
			editor.putString(SchoolAppLoginActivity.this.getString(R.string.JSONString), result);
			editor.commit();

			Intent myIntent = new Intent(SchoolAppLoginActivity.this, SchoolAppUserNotificationChannelList.class);
			myIntent.putExtra("token", token);
			startActivity(myIntent);
		}
	}

	/*
	public void returnUserNotificationListResult(String result){
		Log.d("Subscribe Response: ",result);
		if(result.contains("ERROR")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(sALA);
					helpBuilder.setTitle("Error");
					helpBuilder.setMessage("Something went wrong, please try again later");

					helpBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog helpDialog = helpBuilder.create();
					helpDialog.show();
				}
			});

		}else {




			Intent myIntent = new Intent(SchoolAppLoginActivity.this, SchoolAppUserNotificationChannelList.class);
			myIntent.putExtra("token", result);
			startActivity(myIntent);
		}
	}
	 */

}