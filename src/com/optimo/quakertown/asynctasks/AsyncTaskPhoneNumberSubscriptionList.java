
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

import com.optimo.quakertown.SchoolAppActivity;
import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppSettingsOptionsActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskPhoneNumberSubscriptionList extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPhoneNumberSubscriptionList";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppActivity schoolAppActivity;
	SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity;

	String id;
	String phoneNumberOrEmail;
	String type;

	ProgressDialog loader;

	public AsyncTaskPhoneNumberSubscriptionList(SchoolAppListActivity schoolAppListActivity, String id, String phoneNumberOrEmail, String type){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;

	}

	public AsyncTaskPhoneNumberSubscriptionList(SchoolAppListActivity schoolAppListActivity, String phoneNumberOrEmail, String type){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;

	}

	public AsyncTaskPhoneNumberSubscriptionList(SchoolAppActivity schoolAppActivity, String phoneNumberOrEmail, String type){
		this.schoolAppActivity = schoolAppActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;
	}

	public AsyncTaskPhoneNumberSubscriptionList(SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity, String phoneNumberOrEmail, String type){
		this.schoolAppSettingsOptionsActivity = schoolAppSettingsOptionsActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;
	}

	protected void onPreExecute() {

		if(schoolAppListActivity!=null){
//			loader = new ProgressDialog(schoolAppListActivity);
		}if(schoolAppActivity!=null){
//			loader = new ProgressDialog(schoolAppActivity);
		}
		if(schoolAppSettingsOptionsActivity!=null){
//			loader = new ProgressDialog(schoolAppSettingsOptionsActivity);
		}
//		loader.setMessage("Loading...");
//		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/subscriptions/";
		url += id;

		if(type.equals("")){
			return "ERROR";
		}else{

			if(type.equals(Constants.PHONENUMBER)){
				url += "/"+phoneNumberOrEmail+Constants.URLPIPE+"";
			}else if(type.equals(Constants.EMAIL)){
				url += "/"+""+Constants.URLPIPE+phoneNumberOrEmail;
			}else{
				return "ERROR";
			}

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
//		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
//		loader.dismiss();

		Log.d(TAG,result);

		if(schoolAppListActivity!=null){
			schoolAppListActivity.returnResult(result);
		}if(schoolAppActivity!=null){

			schoolAppActivity.returnSubscriptionList(result, phoneNumberOrEmail);

		}
		if(schoolAppSettingsOptionsActivity!=null){

			schoolAppSettingsOptionsActivity.returnSubscriptionList(result, phoneNumberOrEmail);

		}
	}

}


