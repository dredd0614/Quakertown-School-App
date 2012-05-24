package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelSubscribe;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;
import com.optimo.quakertown.database.SchoolAppPhoneEmailDBAdapter;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.formvalidation.EmailValidator;
import com.optimo.quakertown.formvalidation.NumberValidator;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class SchoolAppEnterPhoneEmailActivity extends Activity{

	SchoolAppEnterPhoneEmailActivity sALA;
	private long id;
	private String channelId;
	private String name;
	private String type;

	private int numEmails = 0;
	private int numPhoneNumbers = 0;

	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
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

		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppEnterPhoneEmailActivity.this);
		dbHelperPhoneEmail.open();

		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppEnterPhoneEmailActivity.this);
		dbHelperChannel.open();

		//ArrayList<PhoneEmailListObject> mArrayList = new ArrayList<PhoneEmailListObject>();

		Cursor c = dbHelperPhoneEmail.getAllPhoneEmails();
		//		startManagingCursor(c);

		if(!(c.moveToFirst())){
			numEmails = 0;
			numPhoneNumbers = 0;
			c.close();
		}else{

			while(!c.isAfterLast()) {
				PhoneEmailListObject row = new PhoneEmailListObject();

				row.setType(c.getString(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.TYPE)));		
				if(row.getType().equals(Constants.PHONENUMBER))
					numPhoneNumbers++;
				if(row.getType().equals(Constants.EMAIL))
					numEmails++;
				c.moveToNext();
			}
		}
		c.close();

		setContentView(R.layout.enterphonenumberandemailaddress);

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

		Bundle extras = getIntent().getExtras();

		id = extras.getLong("id",0l);
		type = extras.getString("type");


		channelId = extras.getString("channelId");
		name = extras.getString("name");

		final RelativeLayout phoneNumberRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutPhoneNumberEditText);
		final RelativeLayout emailRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutEmailEditText);
		final RelativeLayout textThisNumberRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayouttextThisNumber);
		final RelativeLayout callThisNumberRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayoutcallThisNumber);

		final EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);	
		final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);	

		final ImageButton clearPhoneNumber = (ImageButton) findViewById(R.id.clearPhoneNumber);
		clearPhoneNumber.setVisibility(View.GONE);

		final ImageButton clearEmail = (ImageButton) findViewById(R.id.clearEmail);
		clearEmail.setVisibility(View.GONE);

		final ToggleButton textThisNumbertogglebutton = (ToggleButton) findViewById(R.id.textThisNumbertogglebutton);
		final ToggleButton callThisNumbertogglebutton = (ToggleButton) findViewById(R.id.callThisNumbertogglebutton);
		textThisNumbertogglebutton.setChecked(false);
		callThisNumbertogglebutton.setChecked(false);


		PhoneEmailListObject phoneEmail = new PhoneEmailListObject();
		if(id!=0l){
			phoneEmail = new PhoneEmailListObject(dbHelperPhoneEmail.getPhoneEmail(id));
		}

		if(type.equals(Constants.PHONENUMBER)){
			phoneNumberEditText.setText(phoneEmail.getValue());
			emailRelativeLayout.setVisibility(View.INVISIBLE);
			if(phoneEmail.getIsTextable()==1)
				textThisNumbertogglebutton.setChecked(true);
			if(phoneEmail.getIsCallable()==1)
				callThisNumbertogglebutton.setChecked(true);

		}else if(type.equals(Constants.EMAIL)){
			emailEditText.setText(phoneEmail.getValue());
			phoneNumberRelativeLayout.setVisibility(View.INVISIBLE);
			textThisNumberRelativeLayout.setVisibility(View.GONE);
			callThisNumberRelativeLayout.setVisibility(View.GONE);
		}

		Button submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				if(type.equals(Constants.PHONENUMBER)&&numPhoneNumbers>=3&&id==0l){
					Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Please modify a previously existing phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				if(type.equals(Constants.EMAIL)&&numEmails>=3&&id==0l){
					Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Please modify a previously existing email", Toast.LENGTH_SHORT).show();
					return;
				}
				//Check to make sure we dont go over our limit

				int text = 0;
				int call = 0;

				if(type.equals(Constants.PHONENUMBER)&&	phoneNumberEditText.getText().toString().length()==10){
					NumberValidator nm = new NumberValidator();
					if(nm.validate(phoneNumberEditText.getText().toString())){
						if(textThisNumbertogglebutton.isChecked())
							text = 1;
						if(callThisNumbertogglebutton.isChecked())
							call = 1;

						if(id==0l){
							id = dbHelperPhoneEmail.createPhoneEmail(
									phoneNumberEditText.getText().toString(),
									type,text,call);
						}else{
							dbHelperPhoneEmail.updatePhoneEmail(id,
									phoneNumberEditText.getText().toString(),
									type,text,call);
						}
						if(channelId!=null){
							//Subscribe
							PhoneEmailListObject o = new PhoneEmailListObject();
							o.setValue(phoneNumberEditText.getText().toString());
							o.setType(type);
							o.setIsTextable(text);
							o.setIsCallable(call);
							o.setRowId(id);

							new AsyncTaskPhoneNumberChannelSubscribe(SchoolAppEnterPhoneEmailActivity.this, 
									Constants.ACTIVE_ID, o, channelId).execute();
						}else{
							Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Added phone number", Toast.LENGTH_SHORT).show();
							finish();
						}
					}else{
						Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Please enter a full phone number with no symbols", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Please enter a full 10 digit phone number with no symbols", Toast.LENGTH_SHORT).show();
				}
				if(type.equals(Constants.EMAIL)&&	!emailEditText.getText().toString().equals("")){

					EmailValidator em = new EmailValidator();
					if(em.validate(emailEditText.getText().toString())){
						if(id==0l){
							id = dbHelperPhoneEmail.createPhoneEmail(
									emailEditText.getText().toString(),
									type,0,0);
						}else{
							dbHelperPhoneEmail.updatePhoneEmail(id, 
									emailEditText.getText().toString(),
									type,0,0);
						}
						if(channelId!=null){
							//Subscribe
							PhoneEmailListObject o = new PhoneEmailListObject();
							o.setValue(emailEditText.getText().toString());
							o.setType(type);
							o.setIsTextable(0);
							o.setIsCallable(0);
							o.setRowId(id);

							new AsyncTaskPhoneNumberChannelSubscribe(SchoolAppEnterPhoneEmailActivity.this, 
									Constants.ACTIVE_ID, o, channelId).execute();
						}else{
							Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Added email", Toast.LENGTH_SHORT).show();
							finish();
						}
					}else{
						Toast.makeText(SchoolAppEnterPhoneEmailActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		submitButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));
		submitButton.setTextColor(getResources().getColor(R.color.white));
	}

	public void returnSubscribeResult(String result, long phoneEmailId){

		if(result.contains("ERROR")){
			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppEnterPhoneEmailActivity.this);
			builder.setMessage("ERROR")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.setTitle("There was an error");
			AlertDialog alert = builder.create();
			alert.show();

		}else{

			if(channelId!=null)
				dbHelperChannel.createChannelConnection(channelId, name, phoneEmailId);

			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppEnterPhoneEmailActivity.this);
			builder.setMessage("Subscribed")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					dialog.cancel();
					finish();
				}
			});
			builder.setTitle("You have been subscribed to "+name);
			AlertDialog alert = builder.create();
			alert.show();



		}

	}

}