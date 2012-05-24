

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

import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppLoginActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskNotificationUserChannelList extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskNotificationUserChannelList";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppLoginActivity schoolAppLoginActivity;

	String id;
	String token;
	
	ProgressDialog loader;


	public AsyncTaskNotificationUserChannelList(SchoolAppListActivity schoolAppListActivity, String id, String token){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.token = token;
	}

	public AsyncTaskNotificationUserChannelList(SchoolAppListActivity schoolAppListActivity, String token){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.token = token;
	}
	
	public AsyncTaskNotificationUserChannelList(SchoolAppLoginActivity schoolAppLoginActivity, String token){
		this.schoolAppLoginActivity = schoolAppLoginActivity;
		this.id = Constants.ACTIVE_ID;
		this.token = token;
	}

	protected void onPreExecute() {
		loader = new ProgressDialog(schoolAppLoginActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/getChannels/";
		url += id;
		url += "/"+token;

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

		}
		return page;


	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Getting User Notification List...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);

		//schoolAppLoginActivity.returnUserNotificationListResult(result);

	}

}