package com.optimo.quakertown;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelSubscribe;
import com.optimo.quakertown.asynctasks.AsyncTaskPushNotificationSubscribe;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.NotificationListObject;

public class SchoolAppWebActivity extends Activity {

	WebView webView;
	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static String KEY = "c2dmPref";
	private static String REGISTRATION_KEY = "registrationKey";
	private SchoolAppChannelDBAdapter dbHelperChannel;

	SharedPreferences settings;
	private String titleColor;
	AppSettingsObject appSettingsObject = null;


	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.websitemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuforward:
			if(webView.canGoForward()){
				webView.goForward();
			}
			Toast.makeText(this, "You pressed the icon!", Toast.LENGTH_LONG).show();
			break;
		case R.id.menushare:
			Toast.makeText(this, "You pressed the icon and text!",Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}
	 */

	protected void onDestroy() {
		super.onDestroy();

		if (dbHelperChannel != null) {
			dbHelperChannel.close();
		}

	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.websitelayout);
	
		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppWebActivity.this);
		dbHelperChannel.open();
		
		
		this.getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		settings = getSharedPreferences(APP_SETTINGS_FILE, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");

		try {
			appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(appSettingsObject.getTitleBar());

		RelativeLayout titlelayoutholder = (RelativeLayout) findViewById(R.id.titlelayoutholder);
		RelativeLayout footerlayoutholder = (RelativeLayout) findViewById(R.id.footerlayoutholder);



		ColorFixer cf = new ColorFixer();
		titleColor = cf.RBGStringToHexString(appSettingsObject.getTitleBarRed(), appSettingsObject.getTitleBarGreen(), appSettingsObject.getTitleBarBlue());

		titlelayoutholder.getBackground();		
		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));
		footerlayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));



		Bundle extras = getIntent().getExtras();

		// final String cookieTrail = extras.getString("cookieTrail");
		final String id = extras.getString("id");
		final String actionTitle = extras.getString("title");

		final String url = extras.getString("url");
		boolean subscribe = extras.getBoolean("subscribe");


		final ProgressBar websiteLoader = (ProgressBar) findViewById(R.id.websiteLoader);
		websiteLoader.setVisibility(View.VISIBLE);
		//		TextView topBarCookieTrail = (TextView) findViewById(R.id.topBarCookieTrail);
		// topBarCookieTrail.setText(cookieTrail);


		Button hiddenloginbutton = (Button) findViewById(R.id.hiddenloginbutton);
		hiddenloginbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});

		Button subscribeButton = (Button) findViewById(R.id.subscribebutton);
		if(subscribe){
			subscribeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
						settings = getSharedPreferences(KEY, 0);

						String registration = settings.getString(REGISTRATION_KEY, "");
						if(!registration.equals("")){
							//		dbHelperChannel.createChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), 0l);
							NotificationListObject notificationListObject = new 	NotificationListObject();
							notificationListObject.setTitle(actionTitle);
							notificationListObject.setChannelId(id);
							new AsyncTaskPushNotificationSubscribe(SchoolAppWebActivity.this, 
									Constants.ACTIVE_ID, notificationListObject, registration).execute();
						}
						
					}else{
						Intent myIntent = new Intent(SchoolAppWebActivity.this, SchoolAppChannelSubscribeActivity.class);
						myIntent.putExtra("channelId", id);
						myIntent.putExtra("name", actionTitle);
						startActivity(myIntent);
					}
				}

			});
		}else{
			subscribeButton.setVisibility(View.INVISIBLE);
		}
		subscribeButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		if(appSettingsObject.getLevel().equals(Constants.WEBSITE_NAVIGATION_ONLY)){
			subscribeButton.setVisibility(View.GONE);
		}


		ImageButton backbutton = (ImageButton) findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(webView.canGoBack()){
					webView.goBack();
				}
			}

		});
		backbutton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		ImageButton forwardbutton = (ImageButton) findViewById(R.id.forwardbutton);
		forwardbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(webView.canGoForward()){
					webView.goForward();
				}
			}

		});		
		forwardbutton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		ImageButton sharebutton = (ImageButton) findViewById(R.id.sharebutton);
		sharebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = url;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}

		});	
		sharebutton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		if(url.equals(Constants.ABOUT_EDULERT)){
			subscribeButton.setVisibility(View.GONE);
			title.setText(getResources().getString(R.string.edulert));
			hiddenloginbutton.setVisibility(View.GONE);
			forwardbutton.setVisibility(View.GONE);
			backbutton.setVisibility(View.GONE);
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		//		webView.setWebViewClient(new HelloWebViewClient());
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				websiteLoader.setVisibility(View.VISIBLE);

				// Activities and WebViews measure progress with different scales.
				// The progress meter will automatically disappear when we reach 100%
				SchoolAppWebActivity.this.setProgress(progress * 100);
				if(progress == 100)
					websiteLoader.setVisibility(View.GONE);

			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(SchoolAppWebActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setVerticalScrollBarEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true); 
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.loadUrl(url);

	}
	
	public void returnPushSubscribeResult(String result, NotificationListObject notificationListObject){
		dbHelperChannel.createChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), 0l);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			//webView.goBack();
			//return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	*/
}
