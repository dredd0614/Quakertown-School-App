package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppReturnEmailActivity extends Activity{
	
	private static final String PREFS_NAME = "emailSettingsFile";
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";

	SharedPreferences.Editor editor;
	SharedPreferences settings;

	SchoolAppReturnEmailActivity sAREA;
	
	private String titleColor;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settingsreturnemail);
		
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

		
		settings = getSharedPreferences(PREFS_NAME, 0);
		
		sAREA = this;

		//Bundle extras = getIntent().getExtras();

//		id = extras.getLong("id",0l);

		final EditText returnEmailEditText = (EditText) findViewById(R.id.returnemailedittext);	
		
		//Get from storage
		String returnEmailString = settings.getString(this.getString(R.string.returnemailsetting), this.getString(R.string.noreturnemail));
		if(returnEmailString.equals(this.getString(R.string.noreturnemail))){
			returnEmailEditText.setText("");
		}else{
			returnEmailEditText.setText(returnEmailString);
		}
		
		final ImageButton clearReturnEmail = (ImageButton) findViewById(R.id.clearreturnemail);
		clearReturnEmail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				returnEmailEditText.setText("");
			}
			
		});
		
		
		Button changeButton = (Button) findViewById(R.id.changereturnemailbutton);
		changeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//Write to storage
				editor = settings.edit();
				editor.putString(SchoolAppReturnEmailActivity.this.getString(R.string.returnemailsetting), returnEmailEditText.getText().toString() );
				editor.commit();
				finish();
			}
		});
		changeButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		Button setNullButton = (Button) findViewById(R.id.setnullreturnemailbutton);
		setNullButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				editor = settings.edit();
				editor.putString(SchoolAppReturnEmailActivity.this.getString(R.string.returnemailsetting), SchoolAppReturnEmailActivity.this.getString(R.string.noreturnemail) );
				editor.commit();
				finish();
			}
		});
		setNullButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

	}

}