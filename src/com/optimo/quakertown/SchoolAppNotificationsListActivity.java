package com.optimo.quakertown;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelSubscribe;
import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelUnsubscribe;
import com.optimo.quakertown.asynctasks.AsyncTaskPushNotificationSubscribe;
import com.optimo.quakertown.asynctasks.AsyncTaskPushNotificationUnsubscribe;
import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;
import com.optimo.quakertown.database.SchoolAppPhoneEmailDBAdapter;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.ChannelSubscribeObject;
import com.optimo.quakertown.objects.NotificationListObject;
import com.optimo.quakertown.objects.PhoneEmailListObject;

public class SchoolAppNotificationsListActivity extends ListActivity {



	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static String KEY = "c2dmPref";
	private static String REGISTRATION_KEY = "registrationKey";

	SharedPreferences settings;
	private String titleColor;

	Resources res = null;

	private ListView lv = null;
	//private String selectedId;
	private OrderAdapter m_adapter;
	public String menuId;
	ArrayList<NotificationListObject> globalArrayNotificationList = null;
	ArrayList<PhoneEmailListObject> globalPhoneNumberList = new ArrayList<PhoneEmailListObject>();
	ArrayList<ChannelSubscribeObject> globalChannelList = new ArrayList<ChannelSubscribeObject>();
	//	TextView cookieTrail = null;
	String pathTrail = null;
	public String globalPath;
	AppSettingsObject appSettingsObject = null;

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		/*
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("GLobalPath:",globalPath);

			if(globalPath.equals("")){
				Log.d("GLobalPath:","blank");

				finish();
			}else if(globalPath.indexOf("_")==-1){
				Log.d("GLobalPath:","-1");
				cookieTrail.setText(cookieTrail.getText().toString().substring(0, cookieTrail.getText().toString().lastIndexOf(">")).trim());
				globalPath = "";
				fillMainMenu(globalPath);
				m_adapter.notifyDataSetChanged();
			}else if(globalPath.indexOf("_")>0){
				Log.d("GLobalPath:",">0");

				globalPath = globalPath.substring(0, globalPath.lastIndexOf("_"));
				cookieTrail.setText(cookieTrail.getText().toString().substring(0, cookieTrail.getText().toString().lastIndexOf(">")).trim());

				fillMainMenu(globalPath);
				m_adapter.notifyDataSetChanged();
			}else{
				Log.d("GLobalPath:","else");
			}
			return true;
		}*/

		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		super.onDestroy();

		if (dbHelperPhoneEmail != null) {
			dbHelperPhoneEmail.close();
		}
		if (dbHelperChannel != null) {
			dbHelperChannel.close();
		}

	}


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		res = getResources();

		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppNotificationsListActivity.this);
		dbHelperPhoneEmail.open();

		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppNotificationsListActivity.this);
		dbHelperChannel.open();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolapplistlayout);

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

		ColorFixer cf = new ColorFixer();
		titleColor = cf.RBGStringToHexString(appSettingsObject.getTitleBarRed(), appSettingsObject.getTitleBarGreen(), appSettingsObject.getTitleBarBlue());

		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));


		//		TextView title = (TextView) findViewById(R.id.cookieTrail);
		//		title.setText("Notification List");
		//		Button loginButton = (Button) findViewById(R.id.topBarLoginButton);
		//		loginButton.setVisibility(View.GONE);

		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
		actionTitle.setText("Modify your Notifications");

		Button loginButton = (Button) findViewById(R.id.loginbutton);
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppNotificationsListActivity.this, SchoolAppLoginActivity.class);
				startActivity(myIntent);
			}

		});


		lv = getListView();

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return onLongListItemClick(v,pos,id);
			}
		});

		String notificationJSONString = getNotificationMenu();


		JSONObjectExtracter jsonOE = new JSONObjectExtracter();
		try {
			globalArrayNotificationList = jsonOE.parseNotificationJSONString(notificationJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalPath = "";
		fillMainMenu("");
		fillArrayList();


		registerForContextMenu(lv);

	}

	public void fillArrayList(){

		fillPhoneEmailArrayList();

		fillChannelSubscribeArrayList();

	}

	private void fillPhoneEmailArrayList() {
		Cursor c = dbHelperPhoneEmail.getAllPhoneEmails();
		//cursor = dbHelperPhoneEmail.getAllPhoneEmails();
		//		startManagingCursor(c);
		globalPhoneNumberList.clear();
		if(!(c.moveToFirst())){
			c.close();
		}else{

			while(!c.isAfterLast()) {
				PhoneEmailListObject row = new PhoneEmailListObject();
				row.setRowId(c.getInt(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ROW_ID)));
				row.setValue(c.getString(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.VALUE)));
				row.setType(c.getString(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.TYPE)));		
				row.setIsTextable(c.getInt(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISTEXTABLE)));		
				row.setIsCallable(c.getInt(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISCALLABLE)));		

				globalPhoneNumberList.add(row);
				c.moveToNext();
			}
		}
		c.close();
	}

	private void fillChannelSubscribeArrayList() {
		Cursor c = dbHelperChannel.getAllChannelConnections();
		//		startManagingCursor(c);
		globalChannelList.clear();

		if(!(c.moveToFirst())){
			c.close();
		}else{

			while(!c.isAfterLast()) {
				ChannelSubscribeObject row = new ChannelSubscribeObject();
				row.setRowId(c.getInt(c.getColumnIndex(SchoolAppChannelDBAdapter.ROW_ID)));
				row.setChannelId(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELID)));
				row.setName(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELNAME)));
				row.setPhoneEmailId(c.getLong(c.getColumnIndex(SchoolAppChannelDBAdapter.PHONEEMAILID)));
				globalChannelList.add(row);
				c.moveToNext();
			}
		}
		c.close();
	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		//selectedId = m_adapter.getItem(pos).getChannelId();

		//False will tell the parent that you didn't handle the onLongPress
		//Will load onCreateContextMenu
		return false;
	}

	public void onResume(){
		super.onResume();

		fillMainMenu("");
		fillArrayList();
		m_adapter.notifyDataSetChanged();
	}

	private void fillMainMenu(String path){
		//		dbHelperPickupListObjects.createPickupListObject(3, "PickupName", "cancel", "nickname");
		ArrayList<NotificationListObject> mArrayList = new ArrayList<NotificationListObject>();

		Log.d("Filling List", path+"sadf");
		/*
		String[] a = null;
		if(path.equals(""))
			mArrayList = globalArrayNotificationList;
		else{
			a = collectPathFromPathString(path);
			mArrayList = findArrayListFromPath(a, globalArrayNotificationList);
		}
		 */
		mArrayList = globalArrayNotificationList;

		Collections.sort(mArrayList, new Comparator<NotificationListObject>() {
			public int compare(NotificationListObject o1, NotificationListObject o2) {
				Integer a = (Integer)o1.getOrder();
				Integer b = (Integer)o2.getOrder();
				return a.compareTo(b); 
			}
		});

		m_adapter = new OrderAdapter(this, R.layout.listobject, mArrayList);

		setListAdapter(m_adapter);
	}

	public void printArrayNotificatonList(ArrayList<NotificationListObject> mList){
		Iterator<NotificationListObject> i = mList.iterator();
		while(i.hasNext()){
			NotificationListObject m = (NotificationListObject) i.next();
			Log.d("NotificationListObject:", m.toString());
		}
	}

	public void returnResult(String result){

	}

	public void returnUnsubscribeResult(String result, NotificationListObject notificationListObject, ArrayList<PhoneEmailListObject> phoneEmailListArrayList){
		//Add array list to database
		//dbHelperChannel
		int i = 0;
		Cursor c = null;
		//		startManagingCursor(c);
		if(phoneEmailListArrayList==null||phoneEmailListArrayList.size()<1){
			//Toast.makeText(SchoolAppNotificationsListActivity.this, "You have no saved phone numbers or email addresses.", Toast.LENGTH_SHORT).show();
		}else{
			ChannelSubscribeObject channelSubscribeObject = null;
			while(i<phoneEmailListArrayList.size()){
				c = dbHelperChannel.getChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), phoneEmailListArrayList.get(i).getRowId());
				if(c.moveToFirst()){
					channelSubscribeObject = new ChannelSubscribeObject(c);	
					dbHelperChannel.deleteChannelConnection(channelSubscribeObject.getRowId());
				}
				i++;
			}
			if(c!=null)
				c.close();
		}
	}

	public void returnSubscribeResult(String result, NotificationListObject notificationListObject, ArrayList<PhoneEmailListObject> phoneEmailListArrayList){
		//Add array list to database
		//dbHelperChannel
		int i = 0;
		if(phoneEmailListArrayList==null||phoneEmailListArrayList.size()<1){
			//Toast.makeText(SchoolAppNotificationsListActivity.this, "You have no saved phone numbers or email addresses.", Toast.LENGTH_SHORT).show();
		}else{
			while(i<phoneEmailListArrayList.size()){
				dbHelperChannel.createChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), phoneEmailListArrayList.get(i).getRowId());
				i++;
			}
		}

	}

	public void returnPushUnsubscribeResult(String result, NotificationListObject notificationListObject){
		ChannelSubscribeObject channelSubscribeObject = null;
		Cursor c = dbHelperChannel.getChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), 0l);
		if(c.moveToFirst()){
			channelSubscribeObject = new ChannelSubscribeObject(c);	
			dbHelperChannel.deleteChannelConnection(channelSubscribeObject.getRowId());
		}
		c.close();
	}

	public void returnPushSubscribeResult(String result, NotificationListObject notificationListObject){
		dbHelperChannel.createChannelConnection(notificationListObject.getChannelId(), notificationListObject.getTitle(), 0l);
	}

	private String getNotificationMenu() {
		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/notifications/";
		url += Constants.ACTIVE_ID;

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
		return page;
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if(appSettingsObject.getLevel().equals(Constants.ALL_OPEN)){

			// Get the item that was clicked		
			Log.d("Did","WE GET HERE?@?#@?$#?%");
			NotificationListObject o = (NotificationListObject) this.getListAdapter().getItem(position);

			//String updateCookieTrail = cookieTrail.getText().toString();//+" > "+o.getTitle();

			Intent myIntent = new Intent(this, SchoolAppChannelSubscribeActivity.class);
			myIntent.putExtra("channelId", o.getChannelId());
			myIntent.putExtra("name", o.getTitle());
			startActivity(myIntent);

		}

	}


	private class OrderAdapter extends ArrayAdapter<NotificationListObject> {

		private ArrayList<NotificationListObject> items;

		public OrderAdapter(Context context, int textViewResourceId, ArrayList<NotificationListObject> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listobject, null);
			}
			final NotificationListObject o = items.get(position);
			if (o != null) {

				final TextView title = (TextView) v.findViewById(R.id.listItemTitle);
				title.setText(o.getTitle());

				final CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);

				//containsChannelSubscribers
				if(containsChannelSubscribers(o)){
					checkBox.setChecked(true);
				}else{
					checkBox.setChecked(false);
				}

				checkBox.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {

						if(checkBox.isChecked()){
							if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
								settings = getSharedPreferences(KEY, 0);

								String registration = settings.getString(REGISTRATION_KEY, "");
								if(!registration.equals(""))
									new AsyncTaskPushNotificationSubscribe(SchoolAppNotificationsListActivity.this, 
											Constants.ACTIVE_ID, o, registration).execute();
							}else{
								if(globalPhoneNumberList==null||globalPhoneNumberList.size()<1){
									Toast.makeText(SchoolAppNotificationsListActivity.this, "You have no saved phone numbers or email addresses.", Toast.LENGTH_SHORT).show();
									checkBox.setChecked(false);
								}else{
									new AsyncTaskPhoneNumberChannelSubscribe(SchoolAppNotificationsListActivity.this, 
											Constants.ACTIVE_ID, o, globalPhoneNumberList).execute();
								}
							}
						}else{
							if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){
								settings = getSharedPreferences(KEY, 0);

								String registration = settings.getString(REGISTRATION_KEY, "");
								if(!registration.equals(""))

									new AsyncTaskPushNotificationUnsubscribe(SchoolAppNotificationsListActivity.this, 
											Constants.ACTIVE_ID, o, registration).execute();
							}else{
								if(globalPhoneNumberList==null||globalPhoneNumberList.size()<1){
									Toast.makeText(SchoolAppNotificationsListActivity.this, "You have no saved phone numbers or email addresses.", Toast.LENGTH_SHORT).show();
									checkBox.setChecked(false);
								}else{
									new AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppNotificationsListActivity.this, 
											Constants.ACTIVE_ID, o, globalPhoneNumberList).execute();
								}
							}
						}

					}

				});

				/*.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked){
							new AsyncTaskPhoneNumberChannelSubscribe(SchoolAppNotificationsListActivity.this, 
									Constants.ACTIVE_ID, o, globalPhoneNumberList).execute();
						}else{
							new AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppNotificationsListActivity.this, 
									Constants.ACTIVE_ID, o, globalPhoneNumberList).execute();
						}
						Toast.makeText(SchoolAppNotificationsListActivity.this, title.getText().toString(), Toast.LENGTH_SHORT).show();
					}
				});
				 */

			}
			menuId = o.getChannelId();
			return v;
		}
	}

	public boolean containsChannelSubscribers(NotificationListObject o){

		Cursor c = dbHelperChannel.getChannelSubscribersList(o.getChannelId());
		//		startManagingCursor(c);

		if(!(c.moveToFirst())){
			c.close();
			return false;
		}else{
			c.close();
			return true;
		}

	}
}
