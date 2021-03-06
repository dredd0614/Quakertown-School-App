package com.optimo.quakertown.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.os.AsyncTask;
import android.util.Log;

import com.optimo.quakertown.SchoolAppActivity;
import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppSettingsOptionsActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskNotificationChannelList extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskNotificationChannelList";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppActivity schoolAppActivity;
	SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity;
	String id;

	//	ProgressDialog loader;

	public AsyncTaskNotificationChannelList(SchoolAppListActivity schoolAppListActivity, String id){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
	}

	public AsyncTaskNotificationChannelList(SchoolAppListActivity schoolAppListActivity){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
	}

	public AsyncTaskNotificationChannelList(SchoolAppActivity schoolAppActivity){
		this.schoolAppActivity = schoolAppActivity;
		this.id = Constants.ACTIVE_ID;
	}

	public AsyncTaskNotificationChannelList(SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity){
		this.schoolAppSettingsOptionsActivity = schoolAppSettingsOptionsActivity;
		this.id = Constants.ACTIVE_ID;
	}

	protected void onPreExecute() {
		//	loader = new ProgressDialog(schoolAppListActivity);
		//	loader.setMessage("Loading...");
		//	loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/notifications/";
		url += id;

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
		//loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		//loader.dismiss();

		Log.d(TAG,result);
		if(schoolAppActivity!=null){
			schoolAppActivity.returnNotificationMenu(result);
		}else if(schoolAppSettingsOptionsActivity!=null){
			schoolAppSettingsOptionsActivity.returnNotificationMenu(result);
		}
		//	schoolAppListActivity.returnResult(result);

	}

}
