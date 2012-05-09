

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

public class AsyncTaskPhoneNumberChannelUnsubscribe extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPhoneNumberChannelUnsubscribe";


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

	public AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppListActivity schoolAppListActivity, String id, String channelId, String phoneNumber, String email){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.channelId = channelId;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppListActivity schoolAppListActivity, String channelId, String phoneNumber, String email){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.channelId = channelId;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	//Single upload
	public AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppChannelSubscribeActivity sACSA, String id, PhoneEmailListObject phoneEmailListObject, String channelId){
		this.sACSA = sACSA;
		this.id = id;
		this.phoneEmailListObject = phoneEmailListObject;
		this.channelId = channelId;
	}

	//Batch upload One Notification Item, Multiple Phone #s
	public AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppNotificationsListActivity sANLA, String id, NotificationListObject notificationListObject, ArrayList<PhoneEmailListObject> phoneNumberArrayList){
		this.schoolAppNotificationsListActivity = sANLA;
		this.id = id;
		this.notificationListObject = notificationListObject;
		this.phoneEmailListArrayList = phoneNumberArrayList;
	}

	//Batch upload Multiple Notification Items, One Phone #
	public AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppSettingsActivity sASA, String id, ArrayList<ChannelSubscribeObject> channelListFromPhoneEmailId, PhoneEmailListObject phoneEmailListObject){
		this.schoolAppSettingsActivity = sASA;
		this.id = id;
		this.channelListFromPhoneEmailId = channelListFromPhoneEmailId;
		this.phoneEmailListObject = phoneEmailListObject;
	}

	protected void onPreExecute() {

		if(schoolAppListActivity!=null){
			loader = new ProgressDialog(schoolAppListActivity);
		}else if(schoolAppNotificationsListActivity!=null){
			loader = new ProgressDialog(schoolAppNotificationsListActivity);
		}else if(sACSA!=null){
			loader = new ProgressDialog(sACSA);
		}else if(schoolAppSettingsActivity!=null){
			loader = new ProgressDialog(schoolAppSettingsActivity);
		}


		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/unsubscribe/";


		//Doing a batch update
		//Make updates that will handle if there was a problem halfway through
		//Making the assumption that if this an all or nothing response.
		//If one "ERROR" they all failed
		//If "OK" they all passed
		if(phoneEmailListArrayList!=null){

			String phoneEmailString = "";
			url += id+"/"+notificationListObject.getChannelId();
			int i = 0;
			while(i<phoneEmailListArrayList.size()){

				if(phoneEmailListArrayList.get(i).getType().equals(Constants.PHONENUMBER)||
						phoneEmailListArrayList.get(i).getType().equals(Constants.MASTER)){
					phoneEmailString = "/"+phoneEmailListArrayList.get(i).getValue()
							+Constants.URLPIPE+"";
				}else if(phoneEmailListArrayList.get(i).getType().equals(Constants.EMAIL)){
					phoneEmailString = "/"+""+Constants.URLPIPE+phoneEmailListArrayList.get(i).getValue();

				}else{
					return "ERROR";
				}


				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
				HttpGet request = new HttpGet();
				request.setHeader("Content-Type", "text/plain; charset=utf-8");
				Log.d("URL: ",url);
				try{
					request.setURI(new URI(url+phoneEmailString));
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
				i++;
			}

		}else if(channelListFromPhoneEmailId!=null){

			/*
			 * 		this.schoolAppSettingsActivity = sASA;
					this.id = id;
					this.channelListFromPhoneEmailId = channelListFromPhoneEmailId;
					this.phoneEmailListObject = phoneEmailListObject;
			 */
			String phoneEmailString = "";
			String channelIdString = "";

			if(phoneEmailListObject.getType().equals(Constants.PHONENUMBER)||
					phoneEmailListObject.getType().equals(Constants.MASTER)){
				phoneEmailString = "/"+phoneEmailListObject.getValue()
						+Constants.URLPIPE+"";
			}else if(phoneEmailListObject.getType().equals(Constants.EMAIL)){
				phoneEmailString = "/"+""+Constants.URLPIPE+phoneEmailListObject.getValue();

			}else{
				return "ERROR";
			}

			int i = 0;
			while(i<channelListFromPhoneEmailId.size()){

				channelIdString = id+"/"+channelListFromPhoneEmailId.get(i).getChannelId();

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
				HttpGet request = new HttpGet();
				request.setHeader("Content-Type", "text/plain; charset=utf-8");
				Log.d("URL: ",url);
				try{
					request.setURI(new URI(url+channelIdString+phoneEmailString));
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
				i++;
			}


			//Doing a single phone number/email
		}else{

			url += id+"/"+channelId;

			if(phoneEmailListObject.getType().equals(Constants.PHONENUMBER)||
					phoneEmailListObject.getType().equals(Constants.MASTER)){
				url += "/"+phoneEmailListObject.getValue()
						+Constants.URLPIPE+"";
			}else if(phoneEmailListObject.getType().equals(Constants.EMAIL)){
				url += "/"+""+Constants.URLPIPE+phoneEmailListObject.getValue();

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
				e.printStackTrace();
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

		if(schoolAppNotificationsListActivity!=null){
			if(phoneEmailListArrayList==null)
				schoolAppNotificationsListActivity.returnResult(result);
			else
				schoolAppNotificationsListActivity.returnUnsubscribeResult(result, notificationListObject, phoneEmailListArrayList);
		}else if(schoolAppListActivity!=null){
			schoolAppListActivity.returnResult(result);	
		}else if(sACSA!=null){
			sACSA.returnUnsubscribeResult(result, phoneEmailListObject.getRowId());	
		}else if(schoolAppSettingsActivity!=null){
			schoolAppSettingsActivity.returnResult(result, channelListFromPhoneEmailId, phoneEmailListObject);
		}

	}//		this.channelListFromPhoneEmailId = channelListFromPhoneEmailId;
	//this.phoneEmailListObject = phoneEmailListObject;

}
