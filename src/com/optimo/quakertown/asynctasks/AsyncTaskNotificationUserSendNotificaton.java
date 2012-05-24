package com.optimo.quakertown.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.optimo.quakertown.R;
import com.optimo.quakertown.SchoolAppBroadCastTextActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskNotificationUserSendNotificaton extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskNotificationUserSendNotificaton";


	//Should be changing to the splashScreen?
	SchoolAppBroadCastTextActivity schoolAppBroadCastTextActivity;
	String id;
	String channelId;
	String token;
	int sms;
	int email;
	int call;
	String replyEmail;
	String messageText;
	
	ProgressDialog loader;


	public AsyncTaskNotificationUserSendNotificaton(SchoolAppBroadCastTextActivity schoolAppBroadCastTextActivity, String id, String channelId, String token, int sms, int email, int call, String replyEmail, String messageText){
		this.schoolAppBroadCastTextActivity = schoolAppBroadCastTextActivity;
		this.id = id;
		this.channelId = channelId;
		this.token = token;
		this.sms = sms;
		this.email = email;
		this.call = call;
		this.replyEmail = replyEmail;
		this.messageText = messageText;
	}

	public AsyncTaskNotificationUserSendNotificaton(SchoolAppBroadCastTextActivity schoolAppBroadCastTextActivity, String channelId, String token, int sms, int email, int call, String replyEmail, String messageText){
		this.schoolAppBroadCastTextActivity = schoolAppBroadCastTextActivity;
		this.id = Constants.ACTIVE_ID;
		this.channelId = channelId;
		this.token = token;
		this.sms = sms;
		this.email = email;
		this.call = call;
		this.replyEmail = replyEmail;
		this.messageText = messageText;
	}

	protected void onPreExecute() {
		loader = new ProgressDialog(schoolAppBroadCastTextActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		
		if(replyEmail.equals(schoolAppBroadCastTextActivity.getResources().getString(R.string.noreturnemail))){
			replyEmail = "";
		}
		
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/sendMessage/";
		url += id;
	
		url += "/"+channelId+"/"+token+"/"+sms+"/"+email+"/"+call+"/"+replyEmail+"/"+messageText.trim();

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

		schoolAppBroadCastTextActivity.returnResult(result);

	}

}