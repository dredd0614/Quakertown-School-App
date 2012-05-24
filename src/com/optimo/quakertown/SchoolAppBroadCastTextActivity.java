package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.optimo.quakertown.asynctasks.AsyncTaskNotificationUserSendNotificaton;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppBroadCastTextActivity extends Activity{

	SchoolAppBroadCastTextActivity sAUMTA;
	private Handler mHandler = new Handler();
	String token;
	String schoolId;
	String channelId;
	
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	SharedPreferences.Editor editor;
	SharedPreferences settings;
	
	private String titleColor;
	
//	int sendText;
//	int sendEmail;
//	int sendRoboCall;
	
	TextView returnEmail;
	AppSettingsObject appSettingsObject = null;

	
	private static final String PREFS_NAME = "emailSettingsFile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcasttextlayout);
		
		settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");
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

		
		settings = getSharedPreferences(PREFS_NAME, 0);

		sAUMTA = this;

		Bundle extras = getIntent().getExtras();
		token = extras.getString("token");
		schoolId = extras.getString("schoolId");
		channelId = extras.getString("channelId");
		
		Button settingsButton = (Button) findViewById(R.id.settingsbutton);
		settingsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SchoolAppBroadCastTextActivity.this, SchoolAppReturnEmailActivity.class);
				startActivity(myIntent);
			}
			
		});
		settingsButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		final EditText broadcastText = (EditText) findViewById(R.id.broadcasttextedittext);		
		final Button broadcastButton = (Button) findViewById(R.id.broadcasttextbutton);

		returnEmail = (TextView) findViewById(R.id.returnEmail);
		returnEmail.setText(settings.getString(this.getString(R.string.returnemailsetting), this.getString(R.string.noreturnemail)));

		final ToggleButton sendtexttogglebutton = (ToggleButton) findViewById(R.id.sendtexttogglebutton);
		sendtexttogglebutton.setChecked(true);
		
		final ToggleButton sendemailtogglebutton = (ToggleButton) findViewById(R.id.sendemailtogglebutton);
		sendemailtogglebutton.setChecked(true);

		final ToggleButton sendrobocalltogglebutton = (ToggleButton) findViewById(R.id.sendrobocalltogglebutton);
		sendrobocalltogglebutton.setChecked(false);

		if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
			sendtexttogglebutton.setVisibility(View.GONE);
			sendemailtogglebutton.setVisibility(View.GONE);
			sendrobocalltogglebutton.setVisibility(View.GONE);
			
			TextView broadcasttexttitle = (TextView) findViewById(R.id.broadcasttexttitle);
			broadcasttexttitle.setText("Push Notification");
			broadcasttexttitle.setTextSize((float) 24.0);
			
			broadcastText.setHint("Enter push notification here.");
			broadcastButton.setText("Push the notification!");
			
			settingsButton.setVisibility(View.GONE);
			
			TextView sendtexttitle = (TextView) findViewById(R.id.sendtexttitle);
			sendtexttitle.setVisibility(View.GONE);

			TextView sendemailtitle = (TextView) findViewById(R.id.sendemailtitle);
			sendemailtitle.setVisibility(View.GONE);

			TextView sendrobocalltitle = (TextView) findViewById(R.id.sendrobocalltitle);
			sendrobocalltitle.setVisibility(View.GONE);

			TextView returnEmailLabel = (TextView) findViewById(R.id.returnEmailLabel);
			returnEmailLabel.setVisibility(View.GONE);

			returnEmail.setVisibility(View.GONE);
		}
		
		
		broadcastButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				int sendText = 0;
				int sendEmail = 0;
				int sendRoboCall = 0;
				
				if(sendtexttogglebutton.isChecked())
					sendText = 1;
				if(sendemailtogglebutton.isChecked())
					sendEmail = 1;
				if(sendrobocalltogglebutton.isChecked())
					sendRoboCall = 1;
				
				if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
					new AsyncTaskNotificationUserSendNotificaton(
							SchoolAppBroadCastTextActivity.this, schoolId, channelId, token, 1, 0, 0, "", broadcastText.getText().toString()).execute();
				}else{
				new AsyncTaskNotificationUserSendNotificaton(
						SchoolAppBroadCastTextActivity.this, schoolId, channelId, token, sendText, sendEmail, sendRoboCall, returnEmail.getText().toString(), broadcastText.getText().toString()).execute();
				}
			}
			
		});
		broadcastButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(broadcastButton.getWindowToken(), 0);
		
	/*	sendtexttogglebutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(sendtexttogglebutton.isChecked()){
					sendText = 1;
				}else{
					sendText = 0;
				}

			}

		});
		sendText = 1;
	*/
		
		
		/*
		sendemailtogglebutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(sendemailtogglebutton.isChecked()){
					sendEmail = 1;
				}else{
					sendEmail = 0;
				}

			}

		});
		sendEmail = 1;
		*/
		
		/*
		sendrobocalltogglebutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(sendrobocalltogglebutton.isChecked()){
					sendRoboCall = 1;
				}else{
					sendRoboCall = 0;
				}

			}

		});
		sendRoboCall = 0;
		*/

	}
	
	public void onResume(){
		super.onResume();
		
		returnEmail.setText(settings.getString(this.getString(R.string.returnemailsetting), this.getString(R.string.noreturnemail)));
		
	}

	public void returnResult(String result){
		
		Log.d("Subscribe Response: ",result);
		if(result.contains("OK")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(SchoolAppBroadCastTextActivity.this);
					helpBuilder.setTitle("Broadcast Message");
					helpBuilder.setMessage("Message Successful!!");

					helpBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog helpDialog = helpBuilder.create();
					helpDialog.show();
				}
			});
		}else if(result.contains("ERROR")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(SchoolAppBroadCastTextActivity.this);
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
		}
	}


}