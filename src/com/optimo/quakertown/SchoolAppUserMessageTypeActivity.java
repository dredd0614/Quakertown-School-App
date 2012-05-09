package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppUserMessageTypeActivity extends Activity{

	SchoolAppUserMessageTypeActivity sAUMTA;
	//private Handler mHandler = new Handler();
	String token;
	String schoolId;
	String channelId;
	
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";

	SharedPreferences.Editor editor;
	SharedPreferences settings;
	
	private String titleColor;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagetypelayout);

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

		
		sAUMTA = this;

		Bundle extras = getIntent().getExtras();
		token = extras.getString("token");
		schoolId = extras.getString("schoolId");
		channelId = extras.getString("channelId");

//		TextView topBarCookieTrail = (TextView) findViewById(R.id.topBarCookieTrail);

		final Button broadcastTextButton = (Button) findViewById(R.id.broadcasttextbutton);
		
		broadcastTextButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppUserMessageTypeActivity.this, SchoolAppBroadCastTextActivity.class);
				myIntent.putExtra("token", token);
				myIntent.putExtra("schoolId", schoolId);
				myIntent.putExtra("channelId", channelId);
				startActivity(myIntent);
				}

		});
		broadcastTextButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		final Button recordVoiceButton = (Button) findViewById(R.id.recordvoice);
		recordVoiceButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppUserMessageTypeActivity.this, SchoolAppBroadCastRecordVoiceActivity.class);
				myIntent.putExtra("token", token);
				myIntent.putExtra("schoolId", schoolId);
				myIntent.putExtra("channelId", channelId);
				startActivity(myIntent);
			}

		});
		recordVoiceButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


	}


}