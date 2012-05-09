
package com.optimo.quakertown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.NotificationListObject;

public class SchoolAppUserNotificationChannelList extends ListActivity {

	String TAG = "SchoolAppUserNotificationChannelList";

	private ListView lv = null;
	//private String selectedId;
	private OrderAdapter m_adapter;
	public String menuId;
	ArrayList<NotificationListObject> globalArrayNotificationList = null;
	//	TextView cookieTrail = null;
	String token = "";
	String pathTrail = null;
	public String globalPath;

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";

	SharedPreferences.Editor editor;
	SharedPreferences settings;

	private String titleColor;
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


	public void onCreate(Bundle savedInstanceState){
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
		actionTitle.setText("Select Activity for Notification");

		Bundle extras = getIntent().getExtras();

		Button loginButton = (Button) findViewById(R.id.loginbutton);
		loginButton.setVisibility(View.INVISIBLE);

		token = extras.getString("token");
		Log.d("Token", token+"a");

		lv = getListView();

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return onLongListItemClick(v,pos,id);
			}
		});

		String notificationJSONString = getNotificationChannelList(Constants.ACTIVE_ID, token);
		Log.d("Title", notificationJSONString.toString());


		JSONObjectExtracter jsonOE = new JSONObjectExtracter();
		try {
			globalArrayNotificationList = jsonOE.parseNotificationJSONString(notificationJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalPath = "";
		fillMainMenu("");

		registerForContextMenu(lv);

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

	private String getNotificationChannelList(String schoolId, String token) {

		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/getChannels/";
		url += schoolId;
		url += "/"+token;

		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
		HttpGet request = new HttpGet();
		request.setHeader("Content-Type", "text/plain; charset=utf-8");

		try {
			request.setURI(new URI(url));
			HttpResponse response;

			response = client.execute(request);
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
			Log.d(TAG,page+"page");

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return page;
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);


		if(appSettingsObject.getLevel().equals(Constants.PUSH_NOTIFICATION)){

			NotificationListObject o = (NotificationListObject) this.getListAdapter().getItem(position);

			Intent myIntent = new Intent(this, SchoolAppBroadCastTextActivity.class);
			myIntent.putExtra("channelId", o.getChannelId());
			myIntent.putExtra("token", token);
			myIntent.putExtra("schoolId", Constants.ACTIVE_ID);
			startActivity(myIntent);
		}else{


			NotificationListObject o = (NotificationListObject) this.getListAdapter().getItem(position);

			Intent myIntent = new Intent(this, SchoolAppUserMessageTypeActivity.class);
			myIntent.putExtra("channelId", o.getChannelId());
			myIntent.putExtra("token", token);
			myIntent.putExtra("schoolId", Constants.ACTIVE_ID);
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
			NotificationListObject o = items.get(position);
			if (o != null) {

				TextView title = (TextView) v.findViewById(R.id.listItemTitle);
				title.setText(o.getTitle());

				CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);
				checkBox.setVisibility(View.GONE);

			}
			menuId = o.getChannelId();
			return v;
		}
	}
}
