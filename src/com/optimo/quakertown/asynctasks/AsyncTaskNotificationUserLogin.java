

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

import com.optimo.quakertown.SchoolAppLoginActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskNotificationUserLogin extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskNotificationUserLogin";


	//Should be changing to the splashScreen?
	SchoolAppLoginActivity schoolAppLoginActivity;
	String id;
	String login;
	String password;

	ProgressDialog loader;


	public AsyncTaskNotificationUserLogin(SchoolAppLoginActivity schoolAppLoginActivity, String id, String login, String password){
		this.schoolAppLoginActivity = schoolAppLoginActivity;
		this.id = id;
		this.login = login;
		this.password = password;
	}

	public AsyncTaskNotificationUserLogin(SchoolAppLoginActivity schoolAppLoginActivity, String login, String password){
		this.schoolAppLoginActivity = schoolAppLoginActivity;
		this.id = Constants.ACTIVE_ID;
		this.login = login;
		this.password = password;
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
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/login/";
		url += id;
		url += "/"+login+"/"+password;
		
		Log.d("URL: ",url);
		Log.d("login: ",login+"a");
		Log.d("password: ",password+"a");

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
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);

		schoolAppLoginActivity.returnResult(result.trim());

	}

}

