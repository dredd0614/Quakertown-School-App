package com.optimo.quakertown;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MyC2dmReceiver extends BroadcastReceiver {
	
	private static String KEY = "c2dmPref";
	private static String REGISTRATION_KEY = "registrationKey";

	//private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
    	Log.d("c2dm", "receieved push");

		  //this.context = context;
			if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
		        handleRegistration(context, intent);
		    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
		        handleMessage(context, intent);
		    }
	}
	
	private void handleRegistration(Context context, Intent intent) {
	    String registration = intent.getStringExtra("registration_id");
	    if (intent.getStringExtra("error") != null) {
	        // Registration failed, should try again later.
		    Log.d("c2dm", "registration failed");
		    String error = intent.getStringExtra("error");
		    if(error == "SERVICE_NOT_AVAILABLE"){
		    	Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
		    }else if(error == "ACCOUNT_MISSING"){
		    	Log.d("c2dm", "ACCOUNT_MISSING");
		    }else if(error == "AUTHENTICATION_FAILED"){
		    	Log.d("c2dm", "AUTHENTICATION_FAILED");
		    }else if(error == "TOO_MANY_REGISTRATIONS"){
		    	Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
		    }else if(error == "INVALID_SENDER"){
		    	Log.d("c2dm", "INVALID_SENDER");
		    }else if(error == "PHONE_REGISTRATION_ERROR"){
		    	Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
		    }
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // unregistration done, new messages from the authorized sender will be rejected
	    	Log.d("c2dm", "unregistered");

	    } else if (registration != null) {
	    	Log.d("c2dm", registration);
	    	Editor editor =
                context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
            editor.putString(REGISTRATION_KEY, registration);
    		editor.commit();
    		
    		
	       // Send the registration ID to the 3rd party site that is sending the messages.
	       // This should be done in a separate thread.
	       // When done, remember that all registration is done.
	    }
	}

	private void handleMessage(Context context, Intent intent)
	{
	    String message = intent.getExtras().getString("message");
	    String title = intent.getExtras().getString("title");
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		
		int icon = R.drawable.logo38;
		CharSequence tickerText = title;
		long when = System.currentTimeMillis();

		
		
		Notification notification = new Notification(icon, tickerText, when);
		
		//data.message
		//data.title
		
		CharSequence contentTitle = title;
		CharSequence contentText = message;
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
		//Do whatever you want with the message
	}

}
