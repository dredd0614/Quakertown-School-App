
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

import com.optimo.quakertown.SchoolAppActivity;
import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class AsyncTaskPhoneNumberSubscriptionList_withArraylist extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPhoneNumberSubscriptionList";


	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppActivity schoolAppActivity;
	ArrayList<PhoneEmailListObject> phoneNumberList = null;

	String id;
	String phoneNumberOrEmail;
	String type;

	String[] results;
	String[] phoneNumberListStrings;

//	ProgressDialog loader;

	public AsyncTaskPhoneNumberSubscriptionList_withArraylist(SchoolAppListActivity schoolAppListActivity, String id, String phoneNumberOrEmail, String type){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;

	}

	public AsyncTaskPhoneNumberSubscriptionList_withArraylist(SchoolAppListActivity schoolAppListActivity, String phoneNumberOrEmail, String type){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;

	}

	public AsyncTaskPhoneNumberSubscriptionList_withArraylist(SchoolAppActivity schoolAppActivity, String phoneNumberOrEmail, String type){
		this.schoolAppActivity = schoolAppActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberOrEmail = phoneNumberOrEmail;
		this.type = type;
	}

	public AsyncTaskPhoneNumberSubscriptionList_withArraylist(SchoolAppActivity schoolAppActivity, ArrayList<PhoneEmailListObject> phoneNumberList){
		this.schoolAppActivity = schoolAppActivity;
		this.id = Constants.ACTIVE_ID;
		this.phoneNumberList = phoneNumberList;
		this.results = new String[phoneNumberList.size()];
		this.phoneNumberListStrings = new String[phoneNumberList.size()];
	}

	protected void onPreExecute() {

		if(schoolAppListActivity!=null){
//			loader = new ProgressDialog(schoolAppListActivity);
		}if(schoolAppActivity!=null){
//			loader = new ProgressDialog(schoolAppActivity);
		}
//		loader.setMessage("Loading...");
//		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");
		String phoneOrEmailURLExtension = "";

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/subscriptions/";
		url += id;

		int i = 0;
		while(i<phoneNumberList.size()){

			if(phoneNumberList.get(i).getType().equals(Constants.PHONENUMBER)){
				phoneOrEmailURLExtension = "/"+phoneNumberList.get(i).getValue()+Constants.URLPIPE+"";
			}else if(phoneNumberList.get(i).getType().equals(Constants.EMAIL)){
				phoneOrEmailURLExtension = "/"+""+Constants.URLPIPE+phoneNumberList.get(i).getValue();
			}else{
				return "ERROR";
			}

			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
			HttpGet request = new HttpGet();
			request.setHeader("Content-Type", "text/plain; charset=utf-8");
			Log.d("URL: ",url+phoneOrEmailURLExtension);
			try{
				request.setURI(new URI(url+phoneOrEmailURLExtension));
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
			results[i] = page;
			phoneNumberListStrings[i] = phoneNumberList.get(i).getValue();
			i++;
		}

		return page;

	}

	protected void onProgressUpdate(String... progress) {
//		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
//		loader.dismiss();

		//Log.d(TAG,result);

		//	if(schoolAppListActivity!=null){
		//		schoolAppListActivity.returnResult(result);
		//	}
		if(schoolAppActivity!=null){
		//	schoolAppActivity.returnSubscriptionList(results, phoneNumberListStrings);
		}

	}

}


