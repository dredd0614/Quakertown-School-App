package com.optimo.quakertown.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.optimo.quakertown.SchoolAppChannelSubscribeActivity;
import com.optimo.quakertown.SchoolAppEnterPhoneEmailActivity;
import com.optimo.quakertown.SchoolAppNotificationsListActivity;
import com.optimo.quakertown.SchoolAppSubscribeActivity;
import com.optimo.quakertown.SchoolAppWebActivity;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.objects.NotificationListObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class AsyncTaskPushNotificationSubscribe extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPushNotificationSubscribe";


	//Should be changing to the splashScreen?
	SchoolAppSubscribeActivity sASA;
	SchoolAppNotificationsListActivity sANLA;
	SchoolAppChannelSubscribeActivity sACSA;
	SchoolAppEnterPhoneEmailActivity sAEPEA;
	SchoolAppWebActivity schoolAppWebActivity;
	String id;
	String channelId;
	String phoneNumber;
	String email;
	NotificationListObject notificationListObject;
	PhoneEmailListObject phoneEmailListObject;
	ArrayList<PhoneEmailListObject> phoneEmailListArrayList = null; 

	ProgressDialog loader;

	public AsyncTaskPushNotificationSubscribe(SchoolAppNotificationsListActivity sANLA, String id, NotificationListObject notificationListObject, String pushRegistration){
		this.sANLA = sANLA;
		this.id = id;
		this.notificationListObject = notificationListObject;
		this.phoneNumber = pushRegistration;
	}
	public AsyncTaskPushNotificationSubscribe(SchoolAppWebActivity sAWA, String id, NotificationListObject notificationListObject, String pushRegistration){
		this.schoolAppWebActivity = sAWA;
		this.id = id;
		this.notificationListObject = notificationListObject;
		this.phoneNumber = pushRegistration;
	}

	protected void onPreExecute() {

		if(sANLA!=null){
			loader = new ProgressDialog(sANLA);
		}else if(schoolAppWebActivity!=null){
			loader = new ProgressDialog(schoolAppWebActivity);
		}
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/subscribe/";


		//Doing a single phone number/email
		url += id+"/"+notificationListObject.getChannelId();
		url += "/"+phoneNumber+Constants.URLPIPE+"";

		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
		HttpGet request = new HttpGet();
		request.setHeader("Content-Type", "text/plain; charset=utf-8");
		Log.d("URL: ",url);
		try{
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";

			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) 
			{
				sb.append(line + NL);
			}

			in.close();
			page = sb.toString();
			Log.d("page",page);

		}
		catch(Exception e){
			e.printStackTrace();
		}





		return page;

	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);
		if(sANLA!=null){
			sANLA.returnPushSubscribeResult(result, notificationListObject);
		}else if(schoolAppWebActivity!=null){
			schoolAppWebActivity.returnPushSubscribeResult(result, notificationListObject);
		}

	}

}




