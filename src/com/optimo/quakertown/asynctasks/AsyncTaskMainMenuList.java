package com.optimo.quakertown.asynctasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.optimo.quakertown.SchoolAppActivity;
import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppSettingsOptionsActivity;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.MenuObject;

public class AsyncTaskMainMenuList extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskMainMenuList";

	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppActivity schoolAppActivity;
	SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity;
	String id;

	//	ProgressDialog loader;


	public AsyncTaskMainMenuList(SchoolAppListActivity schoolAppListActivity, String id){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
	}

	public AsyncTaskMainMenuList(SchoolAppListActivity schoolAppListActivity){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
	}

	public AsyncTaskMainMenuList(SchoolAppActivity schoolAppActivity){
		this.schoolAppActivity = schoolAppActivity;
		this.id = Constants.ACTIVE_ID;
	}

	public AsyncTaskMainMenuList(SchoolAppSettingsOptionsActivity schoolAppSettingsOptionsActivity){
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
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/menu/";
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

		try {
			JSONObject jsonObject = new JSONObject(page);
			Iterator<?> i = jsonObject.keys();
			while(i.hasNext()){
				String key = (String) i.next();
				JSONObject js = jsonObject.getJSONObject(key);
				Log.d("Getting XML for:", js.getString("title"));
				if(!js.getString("image").equals("")){
					Log.d("Getting Image for:", js.getString("title"));
					try{
						File sdCard = Environment.getExternalStorageDirectory();
						File doesFileExist = new File(sdCard.getAbsolutePath()+Constants.FOLDER_NAME+js.getString("image"));
						if (!doesFileExist.exists()) {
							JSONObjectExtracter.getImage(js.getString("image"));
						}

					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			schoolAppActivity.returnMainMenu(result);
		}else if(schoolAppSettingsOptionsActivity!=null){
			schoolAppSettingsOptionsActivity.returnMainMenu(result);
		}
		//schoolAppListActivity.returnResult(result);

	}

}
