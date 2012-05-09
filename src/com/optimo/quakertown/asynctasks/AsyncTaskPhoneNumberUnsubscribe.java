

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
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskPhoneNumberUnsubscribe extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPhoneNumberUnsubscribe";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	String id;
	String phoneNumber;
	String email;

	ProgressDialog loader;
	
	public AsyncTaskPhoneNumberUnsubscribe(SchoolAppListActivity schoolAppListActivity, String id, String phoneNumber, String email){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public AsyncTaskPhoneNumberUnsubscribe(SchoolAppListActivity schoolAppListActivity, String phoneNumber, String email){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	protected void onPreExecute() {
		loader = new ProgressDialog(schoolAppListActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/unsubscribe/";
		url += id;

		if(phoneNumber==null){
			phoneNumber = "";
		}
		if(email==null){
			email = "";
		}

		if((phoneNumber.equals("")&&email.equals(""))){
			return "ERROR";
		}else{

			url += "/"+phoneNumber+Constants.URLPIPE+email;
			
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
		}
		return page;


	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();
		
		Log.d(TAG,result);

		schoolAppListActivity.returnResult(result);

	}

}
