package com.optimo.quakertown.asynctasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.optimo.quakertown.SchoolAppListActivity;
import com.optimo.quakertown.SchoolAppListActivityListView;
import com.optimo.quakertown.constants.Constants;

public class AsyncTaskMainMenuGetImage extends AsyncTask<Void, String, String> {
	String TAG = "AsyncTaskMainMenuList";

	//Should be changing to the splashScreen?
	SchoolAppListActivity schoolAppListActivity;
	SchoolAppListActivityListView schoolAppListActivityListView;
	String id;
	String imageLink;

	ProgressDialog loader;

	

	public AsyncTaskMainMenuGetImage(SchoolAppListActivity schoolAppListActivity, String id, String imageLink){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = id;
		this.imageLink = imageLink;
	}

	public AsyncTaskMainMenuGetImage(SchoolAppListActivity schoolAppListActivity, String imageLink){
		this.schoolAppListActivity = schoolAppListActivity;
		this.id = Constants.ACTIVE_ID;
		this.imageLink = imageLink;
	}
	
	public AsyncTaskMainMenuGetImage(SchoolAppListActivityListView schoolAppListActivityListView, String imageLink){
		this.schoolAppListActivityListView = schoolAppListActivityListView;
		this.id = Constants.ACTIVE_ID;
		this.imageLink = imageLink;
	}

	protected void onPreExecute() {
		
		if(schoolAppListActivityListView!=null)
			loader = new ProgressDialog(schoolAppListActivityListView);
		else if(schoolAppListActivity!=null)
			loader = new ProgressDialog(schoolAppListActivity);
		loader.setMessage("Loading...");
		loader.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d(TAG,"doInBackground");
		try {

			String urlBase = Constants.ROOT_SCHOOLAPP_URL + "img/"+id+"/"+imageLink;


			URL url = new URL(urlBase);

			long startTime = System.currentTimeMillis();
			Log.d("ImageManager", "download begining");
			Log.d("ImageManager", "download url:" + url);
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}


			File file = new File("");
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/com.optimo.quakertown/images/");
				Log.d("Make dir?:",""+dir.mkdirs());
				file = new File(dir, imageLink);
				FileOutputStream f = new FileOutputStream(file);
				f.write(baf.toByteArray());
				f.close();
			}else{
					return "Need SD-Card for images";
			}
			
			Log.d("ImageManager", "download ready in"
					+ ((System.currentTimeMillis() - startTime) / 1000)
					+ " sec");

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
			return "ERROR";
		}
		return "OK";


	}

	protected void onProgressUpdate(String... progress) {
		loader.setMessage("Loading...");
	}

	protected void onPostExecute(String result) {
		loader.dismiss();

		Log.d(TAG,result);

		if(schoolAppListActivityListView!=null)
			schoolAppListActivityListView.returnImageResult(result);
		else if(schoolAppListActivity!=null)
			schoolAppListActivity.returnImageResult(result);

	}

}
