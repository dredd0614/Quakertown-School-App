package com.optimo.quakertown;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskSendRecordingUploadBroadcast;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;

public class SchoolAppBroadCastRecordVoiceActivity extends Activity implements OnCompletionListener, OnChronometerTickListener{

	SchoolAppBroadCastRecordVoiceActivity sAUMTA;
	private static final String TAG = "SchoolAppBroadCastRecordVoiceActivity";
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	SharedPreferences.Editor editor;
	SharedPreferences settings;

	private String titleColor;

	private Handler mHandler = new Handler();

	MediaRecorder recorder;
	File audiofile = null;

	String token;
	String schoolId;
	String channelId;
	String audioPath = "";

	ImageView mic;
	ImageView mic_bright;


	ImageButton stopbutton;
	ImageButton recordbutton;

	ImageButton rewindbutton;
	ImageButton playbutton;
	//	ImageButton pausebutton;
	Button broadcastbutton;
	boolean paused = false;
	boolean isRecording = false;
	long elapsedTime=0;
	String currentTime="";
	long startTime=SystemClock.elapsedRealtime();
	Boolean resume=false;
	TextView amplitudeTextView;

	RecordAmplitude recordAmplitude;

	Chronometer myChronometer = null;

	MediaPlayer mp = null;// = new MediaPlayer();

	long startedElapsedRealTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcastrecordedvoicelayout2);

		settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");
		AppSettingsObject appSettingsObject = null;
		try {
			appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(appSettingsObject.getTitleBar());

		RelativeLayout titlelayoutholder = (RelativeLayout) findViewById(R.id.titlelayoutholder);

		ColorFixer cf = new ColorFixer();
		titleColor = cf.RBGStringToHexString(appSettingsObject.getTitleBarRed(), appSettingsObject.getTitleBarGreen(), appSettingsObject.getTitleBarBlue());

		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));

		amplitudeTextView = (TextView) findViewById(R.id.amp);
		mic = (ImageView) findViewById(R.id.microphone);
		mic_bright = (ImageView) findViewById(R.id.microphone_bright);

		Bundle extras = getIntent().getExtras();
		token = extras.getString("token");
		schoolId = extras.getString("schoolId");
		channelId = extras.getString("channelId");

		myChronometer = (Chronometer)findViewById(R.id.chronometer);
		myChronometer.setBase(SystemClock.elapsedRealtime());
		startedElapsedRealTime = SystemClock.elapsedRealtime();
		myChronometer.setOnChronometerTickListener(this);
		/*
		myChronometer.setOnChronometerTickListener(new OnChronometerTickListener(){
			@Override
			public void onChronometerTick(Chronometer chronometer) {

				if(getChronometerTimeInSeconds(myChronometer.getText().toString())>=60){
					myChronometer.stop();
					swapRecordStopButtons();
				}
			}
		});
		 */
		recordbutton = (ImageButton) findViewById(R.id.recordbutton);
		recordbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

					myChronometer.setBase(SystemClock.elapsedRealtime());
					startedElapsedRealTime = SystemClock.elapsedRealtime();

					myChronometer.start();
					try {
						startRecording();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Need SD Card", Toast.LENGTH_LONG).show();
				}
			}
		});

		stopbutton = (ImageButton) findViewById(R.id.stopbutton);
		stopbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				myChronometer.stop();
				stopRecording();

			}
		});
		stopbutton.setVisibility(View.GONE);


		rewindbutton = (ImageButton) findViewById(R.id.rewindbutton);
		rewindbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!audioPath.equals("")){
					if(mp.isPlaying()){
						mp.stop();
					}

					myChronometer.setText("00:00");
					mp = null;
					paused = false;

					//Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "not implemented yet", Toast.LENGTH_LONG).show();
				}
			}
		});

		playbutton = (ImageButton) findViewById(R.id.playbutton);
		playbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!audioPath.equals("")){
					swapPlayStopImageButtons();

					if(mp!=null&&paused){
						paused = false;
						mp.start();
						myChronometer.start();

					}else{
						paused = false;

						mp = null;
						myChronometer.setBase(SystemClock.elapsedRealtime());
						startedElapsedRealTime = SystemClock.elapsedRealtime();

						myChronometer.start();
						audioPlayer(audioPath);
					}

				}
			}
		});
		playbutton.setVisibility(View.VISIBLE);

		//		playbutton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

		broadcastbutton = (Button) findViewById(R.id.broadcastbutton);
		broadcastbutton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				byte[] recordedMessage = null;
				try {
					File file = new File(audiofile.getAbsolutePath());
					if(file.exists()){
						recordedMessage = getBytesFromFile(file);
					}
					else{
						Log.d("not a file", "not a file");
						Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Please make a recording first", Toast.LENGTH_LONG).show();
					}
				} catch (IOException e) {
					//e.printStackTrace();
					Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Please make a recording first", Toast.LENGTH_LONG).show();
				} 

				if(recordedMessage!=null){
					Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Be sure to uncomment code to actually broadcast the message.", Toast.LENGTH_LONG).show();

//					new AsyncTaskSendRecordingUploadBroadcast(SchoolAppBroadCastRecordVoiceActivity.this, channelId, token, recordedMessage).execute();
				}else{
					Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Please make a recording first", Toast.LENGTH_LONG).show();
				}
			}
		});
		//	broadcastbutton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

	}

	public void startRecording() throws IOException {

		isRecording=true;
		brightenRecordButton();
		recordAmplitude = new RecordAmplitude();
		recordAmplitude.execute();
		String fileName = "SchoolApp-"
			+ String.valueOf(System.currentTimeMillis())
			+ ".3gp";
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File sdCard = Environment.getExternalStorageDirectory();
			//File dir = new File (sdCard.getAbsolutePath() + "/schoolapp/broadcastrecordings/");
			audioPath = sdCard.getAbsolutePath()+"/schoolapp/broadcastrecordings/"+fileName;


			audiofile = new File (sdCard.getAbsolutePath() + "/schoolapp/broadcastrecordings/", fileName);
			File sampleDir = Environment.getExternalStorageDirectory();

			try {
				audiofile = File.createTempFile("RoboCall", ".3gp", sampleDir);
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "sdcard access error", Toast.LENGTH_LONG).show();
				Log.e(TAG, "sdcard access error");
				return;
			}

			Log.d("audiofile.getAbsolutePath()",audiofile.getAbsolutePath());
			audioPath = audiofile.getAbsolutePath();


			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(audiofile.getAbsolutePath());
			try{
				recorder.prepare();
				recorder.start();
			}catch(Exception e){
				Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Make sure the device has the ability to record audio", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(SchoolAppBroadCastRecordVoiceActivity.this, "Need SD Card", Toast.LENGTH_LONG).show();
		}
	}

	public void stopRecording() {
		recordAmplitude.cancel(true);
		isRecording=false;
		brightenRecordButton();
		swapPlayStopImageButtons();
		//Let the amp tracker kill itself
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		recorder.stop();
		recorder.release();

		//addRecordingToMediaLibrary();
	}

	protected void addRecordingToMediaLibrary() {
		/*
		ContentValues values = new ContentValues(4);
		long current = System.currentTimeMillis();
		values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
		Log.d("Audio File",newUri+"");
		Log.d("Audio File",newUri.getPath());
		 */
		Log.d("Absolute path",audiofile.getAbsolutePath());


		//	Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();

		/*
		byte[] recordedMessage = null;
		try {
			File file = new File(audiofile.getAbsolutePath());
			if(file.exists()){
				recordedMessage = getBytesFromFile(file);
			}
			else{
				Log.d("not a file", "not a file");
				Toast.makeText(this, "not a file", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 

		if(recordedMessage!=null)
			new AsyncTaskSendRecordingUploadBroadcast(SchoolAppBroadCastRecordVoiceActivity.this, channelId, token, recordedMessage).execute();
		 */
	}

	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	private void brightenRecordButton(){

		swapPlayStopImageButtons();
		
		if(isRecording){
			recordbutton.setImageDrawable(this.getResources().getDrawable(R.drawable.record2_nate_bright));
	//		mic.setVisibility(View.GONE);
	//		mic_bright.setVisibility(View.VISIBLE);
		}else{
			recordbutton.setImageDrawable(this.getResources().getDrawable(R.drawable.record2_nate));
	//		mic.setVisibility(View.VISIBLE);
	//		mic_bright.setVisibility(View.GONE);
		}

		/*
		if(recordbutton.getVisibility()==View.INVISIBLE){
			recordbutton.setVisibility(View.VISIBLE);
			stopbutton.setVisibility(View.INVISIBLE);
		}else{
			recordbutton.setVisibility(View.INVISIBLE);
			stopbutton.setVisibility(View.VISIBLE);
		}
		 */

	}

	private void swapPlayStopImageButtons(){

		if(playbutton.getVisibility()==View.GONE){
			playbutton.setVisibility(View.VISIBLE);
			stopbutton.setVisibility(View.GONE);
		}else{
			playbutton.setVisibility(View.GONE);
			stopbutton.setVisibility(View.VISIBLE);
		}

	}


	public void returnResult(String result){
		Log.d("Subscribe Response: ",result);
		if(result.contains("OK")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(SchoolAppBroadCastRecordVoiceActivity.this);
					helpBuilder.setTitle("Broadcast Message");
					helpBuilder.setMessage("Message Successful!!");

					helpBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog helpDialog = helpBuilder.create();
					helpDialog.show();
				}
			});
		}else if(result.contains("ERROR")){
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder helpBuilder = new AlertDialog.Builder(SchoolAppBroadCastRecordVoiceActivity.this);
					helpBuilder.setTitle("Error");
					helpBuilder.setMessage("Something went wrong, please try again later");

					helpBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog helpDialog = helpBuilder.create();
					helpDialog.show();
				}
			});
		}
	}

	public void audioPlayer(String path){



		//set up MediaPlayer    
		mp = new MediaPlayer();
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			mp.setDataSource(fis.getFD());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.setOnCompletionListener(this);
		mp.start();
	}

	//Format: MM:SS
	//Static function, not the best idea but the fastest
	private int getChronometerTimeInSeconds(String time){
		return (Integer.parseInt(time.substring(0,2))*60)+(Integer.parseInt(time.substring(3,5)));
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		myChronometer.stop();
	}

	@Override
	public void onChronometerTick(Chronometer chrono) {
		// TODO Auto-generated method stub		
		if(!paused)
		{
			long minutes=((SystemClock.elapsedRealtime()-chrono.getBase())/1000)/60;
			long seconds=((SystemClock.elapsedRealtime()-chrono.getBase())/1000)%60;
			currentTime=minutes+":"+seconds;
			chrono.setText(currentTime);
			elapsedTime=SystemClock.elapsedRealtime();
		}
		else
		{
			long minutes=((elapsedTime-chrono.getBase())/1000)/60;
			long seconds=((elapsedTime-chrono.getBase())/1000)%60;
			currentTime=minutes+":"+seconds;
			chrono.setText(currentTime);
			elapsedTime=elapsedTime+1000;
		}

	}

	private class RecordAmplitude extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while (isRecording) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				publishProgress(recorder.getMaxAmplitude());
			}
			return null;
		}
		protected void onProgressUpdate(Integer... progress) {
			if(progress[0]>6000){
				mic.setVisibility(View.GONE);
				mic_bright.setVisibility(View.VISIBLE);
			}else{
				mic.setVisibility(View.VISIBLE);
				mic_bright.setVisibility(View.GONE);
			}

			amplitudeTextView.setText(progress[0].toString());
		}
	}

}