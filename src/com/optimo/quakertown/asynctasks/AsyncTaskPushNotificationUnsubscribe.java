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
import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppNotificationsListActivity;
import com.optimo.quakertown.SchoolAppSettingsActivity;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.objects.ChannelSubscribeObject;
import com.optimo.quakertown.objects.NotificationListObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class AsyncTaskPushNotificationUnsubscribe extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPushNotificationUnsubscribe";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppNotificationsListActivity schoolAppNotificationsListActivity;
	SchoolAppChannelSubscribeActivity sACSA;
	SchoolAppSettingsActivity schoolAppSettingsActivity;
	String id;
	String channelId;
	String phoneNumber;
	String email;
	NotificationListObject notificationListObject;
	PhoneEmailListObject phoneEmailListObject;
	ArrayList<PhoneEmailListObject> phoneEmailListArrayList = null; 
	ArrayList<ChannelSubscribeObject> channelListFromPhoneEmailId = null;

	ProgressDialog loader;

	//Batch upload One Notification Item, Multiple Phone #s
	public AsyncTaskPushNotificationUnsubscribe(SchoolAppNotificationsListActivity sANLA, String id, NotificationListObject notificationListObject, String pushRegistration){
		this.schoolAppNotificationsListActivity = sANLA;
		this.id = id;
		this.notificationListObject = notificationListObject;
		this.phoneNumber = pushRegistration;
	}

	protected void onPreExecute() {

		loader = new ProgressDialog(schoolAppNotificationsListActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/unsubscribe/";


		//Doing a single phone number/email
		url += id+"/"+notificationListObject.getChannelId();;
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
		if(page.contains("ERROR")||page.equals(""))
			return page;


	//Doing a single phone number/email


	return page;

}

protected void onProgressUpdate(String... progress) {
	loader.setMessage("Loading...");
}

protected void onPostExecute(String result) {
	loader.dismiss();

	Log.d(TAG,result);

	schoolAppNotificationsListActivity.returnPushUnsubscribeResult(result, notificationListObject);


}//		this.channelListFromPhoneEmailId = channelListFromPhoneEmailId;
//this.phoneEmailListObject = phoneEmailListObject;

}
