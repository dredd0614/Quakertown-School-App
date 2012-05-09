package com.optimo.quakertown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelSubscribe;

public class SchoolAppSubscribeActivity extends Activity{

	SchoolAppSubscribeActivity sASA;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribelayout);

		sASA = this;

		Bundle extras = getIntent().getExtras();

//		String cookieTrail = extras.getString("cookieTrail");
		final String id = extras.getString("id");

//		TextView topBarCookieTrail = (TextView) findViewById(R.id.topBarCookieTrail);
//		topBarCookieTrail.setText(cookieTrail);

		final EditText phoneNumberEditText = (EditText) findViewById(R.id.subscribePhoneNumberEditText);	
		final EditText emailAddressEditText = (EditText) findViewById(R.id.subscribeEmailEditText);	

		Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
		subscribeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				String phoneNumber="";
				String emailAddress = "";
				phoneNumber = phoneNumberEditText.getText().toString();
				emailAddress = emailAddressEditText.getText().toString();
				new AsyncTaskPhoneNumberChannelSubscribe(sASA, id, phoneNumber, emailAddress).execute();


			}

		});

	}

	public void returnResult(String result){
		Log.d("Subscribe Response: ",result);
		if(result.contains("OK")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(sASA);
					helpBuilder.setTitle("Subscribed!");
					helpBuilder.setMessage("Thank You for Subscribing!!");

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
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(sASA);
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
