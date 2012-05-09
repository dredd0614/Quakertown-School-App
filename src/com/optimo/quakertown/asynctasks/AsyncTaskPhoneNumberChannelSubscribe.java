

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
import com.optimo.quakertown.SchoolAppEnterPhoneEmailActivity;
import com.optimo.quakertown.SchoolAppNotificationsListActivity;
import com.optimo.quakertown.SchoolAppSubscribeActivity;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.objects.NotificationListObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class AsyncTaskPhoneNumberChannelSubscribe extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskPhoneNumberChannelSubscribe";


	//Should be changing to the splashScreen?
	SchoolAppSubscribeActivity sASA;
	SchoolAppNotificationsListActivity sANLA;
	SchoolAppChannelSubscribeActivity sACSA;
	SchoolAppEnterPhoneEmailActivity sAEPEA;
	String id;
	String channelId;
	String phoneNumber;
	String email;
	NotificationListObject notificationListObject;
	PhoneEmailListObject phoneEmailListObject;
	ArrayList<PhoneEmailListObject> phoneEmailListArrayList = null; 

	ProgressDialog loader;


	public AsyncTaskPhoneNumberChannelSubscribe(SchoolAppSubscribeActivity sASA, String id, String channelId, String phoneNumber, String email){
		this.sASA = sASA;
		this.id = id;
		this.channelId = channelId;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public AsyncTaskPhoneNumberChannelSubscribe(SchoolAppSubscribeActivity sASA, String channelId, String phoneNumber, String email){
		this.sASA = sASA;
		this.id = Constants.ACTIVE_ID;
		this.channelId = channelId;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	
	public AsyncTaskPhoneNumberChannelSubscribe(SchoolAppEnterPhoneEmailActivity sAEPEA, String id, PhoneEmailListObject phoneEmailListObject, String channelId){
		this.sAEPEA = sAEPEA;
		this.id = id;
		this.phoneEmailListObject = phoneEmailListObject;
		this.channelId = channelId;
	}

	//Single upload
	public AsyncTaskPhoneNumberChannelSubscribe(SchoolAppChannelSubscribeActivity sACSA, String id, PhoneEmailListObject phoneEmailListObject, String channelId){
		this.sACSA = sACSA;
		this.id = id;
		this.phoneEmailListObject = phoneEmailListObject;
		this.channelId = channelId;
	}

	//Batch Upload
	public AsyncTaskPhoneNumberChannelSubscribe(SchoolAppNotificationsListActivity sANLA, String id, NotificationListObject notificationListObject, ArrayList<PhoneEmailListObject> phoneNumberArrayList){
		this.sANLA = sANLA;
		this.id = id;
		this.notificationListObject = notificationListObject;
		this.phoneEmailListArrayList = phoneNumberArrayList;
	}

	protected void onPreExecute() {

		if(sASA!=null){
			loader = new ProgressDialog(sASA);
		}else if(sANLA!=null){
			loader = new ProgressDialog(sANLA);
		}else if(sACSA!=null){
			loader = new ProgressDialog(sACSA);
		}else if(sAEPEA!=null){
			loader = new ProgressDialog(sAEPEA);
		}
		
		
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/subscribe/";


		//Doing a single phone number/email
		if(phoneEmailListArrayList==null){
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


			//Doing a batch update
			//Make updates that will handle if there was a problem halfway through
			//Making the assumption that if this an all or nothing response.
			//If one "ERROR" they all failed
			//If "OK" they all passed
		}else{
			String phoneEmailString;
			url += id+"/"+notificationListObject.getChannelId();
			int i = 0;
			Log.d("arraySize: ",phoneEmailListArrayList.size()+"");

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
				Log.d("URL: ",url+phoneEmailString);
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

		}

		return page;

	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);

		if(sANLA!=null){
			if(phoneEmailListArrayList==null)
				sANLA.returnResult(result);
			else
				sANLA.returnSubscribeResult(result, notificationListObject, phoneEmailListArrayList);
		}else if(sASA!=null){
			sASA.returnResult(result);	
		}else if(sACSA!=null){
			sACSA.returnSubscribeResult(result, phoneEmailListObject.getRowId());	
		}else if(sAEPEA!=null){
			sAEPEA.returnSubscribeResult(result, phoneEmailListObject.getRowId());	
		}


	}

}




