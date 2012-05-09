package com.optimo.quakertown.asynctasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.optimo.quakertown.SchoolAppBroadCastRecordVoiceActivity;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskSendRecordingUploadBroadcast extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskSendRecordingUploadBroadcast";


	//Should be changing to the splashScreen?
	SchoolAppBroadCastRecordVoiceActivity schoolAppBroadCastRecordVoiceActivity;
	String id;
	String channelId;
	String token;
	byte[] recordedMessage;

	ProgressDialog loader;

	public AsyncTaskSendRecordingUploadBroadcast(SchoolAppBroadCastRecordVoiceActivity schoolAppBroadCastRecordVoiceActivity, String id, String channelId, String token, byte[] recordedMessage){
		this.schoolAppBroadCastRecordVoiceActivity = schoolAppBroadCastRecordVoiceActivity;
		this.id = id;
		this.channelId = channelId;
		this.token = token;
		this.recordedMessage = recordedMessage;
	}

	public AsyncTaskSendRecordingUploadBroadcast(SchoolAppBroadCastRecordVoiceActivity schoolAppBroadCastRecordVoiceActivity, String channelId, String token, byte[] recordedMessage){
		this.schoolAppBroadCastRecordVoiceActivity = schoolAppBroadCastRecordVoiceActivity;
		this.id = Constants.ACTIVE_ID;
		this.channelId = channelId;
		this.token = token;
		this.recordedMessage = recordedMessage;
	}

	protected void onPreExecute() {
		loader = new ProgressDialog(schoolAppBroadCastRecordVoiceActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/send3gpRecording/";
		url += id;
		url += "/"+channelId+"/"+token;

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		//URLEncoder.encode(text);	
		
		try{
            ByteArrayBody cb = new ByteArrayBody(recordedMessage,"recordedMessage");

            //MultipartEntity multipartContent = new MultipartEntity();
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			//reqEntity.addPart("recordingData",isb);
			reqEntity.addPart("recordingData",cb);
			httppost.setEntity(reqEntity);
			Log.d("beforeexecute: ","beforeexecute");

			HttpResponse response = httpclient.execute(httppost);
			Log.d("post execute: ","post execute");

			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			Log.d("from: ","here3");

			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null){
				sb.append(line + NL);
			}
			Log.d("from: ","here4");

			in.close();
			page = sb.toString();
			Log.d("here?: ", page+"gh");
		}catch(Exception e){
			//TODO
			//handle this exception
		}
		
		Log.d(TAG,page+"meh");

		return page;


	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);

		schoolAppBroadCastRecordVoiceActivity.returnResult(result);

	}

}