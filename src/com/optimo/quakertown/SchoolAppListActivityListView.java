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
import android.os.Bundle;
import android.os.Environment;
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

import com.optimo.quakertown.asynctasks.AsyncTaskMainMenuGetImage;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.MenuObject;
import com.optimo.quakertown.objects.NotificationListObject;

public class SchoolAppListActivityListView extends ListActivity{

	private static final String PREFS_NAME = "AppSettingsFile";
	SharedPreferences.Editor editor;
	SharedPreferences settings;

	private ListView lv = null;
	//private String selectedId;
	private OrderAdapter m_adapter;
	public String menuId;
	ArrayList<MenuObject> globalArrayMenuList = null;
	ArrayList<NotificationListObject> globalArrayNotificationList = null;

	//	TextView cookieTrail = null;
	String pathTrail = null;
	public String globalPath;

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("GLobalPath:",globalPath);

			if(globalPath.equals("")){
				Log.d("GLobalPath:","blank");

				finish();
			}else if(globalPath.indexOf("_")==-1){
				Log.d("GLobalPath:","-1");
				//				cookieTrail.setText(cookieTrail.getText().toString().substring(0, cookieTrail.getText().toString().lastIndexOf(">")).trim());
				globalPath = "";
				fillMainMenu(globalPath);
				m_adapter.notifyDataSetChanged();
			}else if(globalPath.indexOf("_")>0){
				Log.d("GLobalPath:",">0");

				globalPath = globalPath.substring(0, globalPath.lastIndexOf("_"));
				//				cookieTrail.setText(cookieTrail.getText().toString().substring(0, cookieTrail.getText().toString().lastIndexOf(">")).trim());

				fillMainMenu(globalPath);
				m_adapter.notifyDataSetChanged();
			}else{
				Log.d("GLobalPath:","else");
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolapplistlayout);

		settings = getSharedPreferences(PREFS_NAME, 0);

		JSONObjectExtracter jsonOESettings = new JSONObjectExtracter();

		String settingsJSON = settings.getString(this.getString(R.string.JSONString), "");
		AppSettingsObject appSettingsObject = null;
		try {
			appSettingsObject = jsonOESettings.parseSettingsJSONString(settingsJSON);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RelativeLayout titlelayoutholder = (RelativeLayout) findViewById(R.id.titlelayoutholder);

		String titleBarRed = appSettingsObject.getTitleBarRed();
		String titleBarGreen = appSettingsObject.getTitleBarGreen();
		String titleBarBlue = appSettingsObject.getTitleBarBlue();

		titleBarRed = Integer.toHexString(Integer.parseInt(titleBarRed));
		titleBarGreen = Integer.toHexString(Integer.parseInt(titleBarGreen));
		titleBarBlue = Integer.toHexString(Integer.parseInt(titleBarBlue));
		
		
		if(titleBarRed.length()==1){
			titleBarRed = "0"+titleBarRed;
		}
		if(titleBarGreen.length()==1){
			titleBarRed = "0"+titleBarGreen;
		}
		if(titleBarBlue.length()==1){
			titleBarRed = "0"+titleBarBlue;
		}
		
		String titleColor = titleBarRed + titleBarGreen + titleBarBlue;
		
		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));

		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
		actionTitle.setText("Select a Category");

		Button loginButton = (Button) findViewById(R.id.loginbutton);
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppListActivityListView.this, SchoolAppLoginActivity.class);
				startActivity(myIntent);
			}

		});

		loginButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		lv = getListView();

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return onLongListItemClick(v,pos,id);
			}
		});

		String mainMenuJSONString = getMainMenu();
		Log.d("mainMenuJSONString:", mainMenuJSONString+"a");

		JSONObjectExtracter jsonOE = new JSONObjectExtracter();
		try {
			globalArrayMenuList = jsonOE.parseMainMenuJSONString(mainMenuJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String notificationJSONString = getNotificationMenu();
		jsonOE = new JSONObjectExtracter();
		try {
			globalArrayNotificationList = jsonOE.parseNotificationJSONString(notificationJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Button testGrabImageButton = (Button) findViewById(R.id.testGrabImageButton);
		testGrabImageButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				new AsyncTaskMainMenuGetImage(SchoolAppListActivityListView.this, "DallasHighSchool.png" ).execute();
			}
			
		});
		
		//printArrayNotificatonList(globalArrayNotificationList);

		//		cookieTrail = (TextView) findViewById(R.id.cookieTrail); 
		//		cookieTrail.setText("Home");

		globalPath = "";
		fillMainMenu(globalPath);

		registerForContextMenu(lv);

	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		//selectedId = m_adapter.getItem(pos).getId();

		//False will tell the parent that you didn't handle the onLongPress
		//Will load onCreateContextMenu
		return false;
	}

	public void onResume(){
		super.onResume();

		fillMainMenu(globalPath);

		m_adapter.notifyDataSetChanged();
	}

	private void fillMainMenu(String path){
		//		dbHelperPickupListObjects.createPickupListObject(3, "PickupName", "cancel", "nickname");
		ArrayList<MenuObject> mArrayList = new ArrayList<MenuObject>();

		Log.d("fillMainMenuPath:", path);

		String[] a = null;
		if(path.equals(""))
			mArrayList = globalArrayMenuList;
		else{
			a = collectPathFromPathString(path);
			Log.d("pathStringArraySize",a.length+"");
			mArrayList = findArrayListFromPath(a, globalArrayMenuList);
		}

		printArrayMenuList(mArrayList);
		//	Comparator n = new Comparator();
		Collections.sort(mArrayList, new Comparator<MenuObject>() {
			public int compare(MenuObject o1, MenuObject o2) {
				Integer a = (Integer)o1.getOrder();
				Integer b = (Integer)o2.getOrder();
				return a.compareTo(b); 
			}
		});

		m_adapter = new OrderAdapter(this, R.layout.listobject, mArrayList);

		setListAdapter(m_adapter);
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

	public ArrayList<MenuObject> findArrayListFromPath(String[] pathArray, ArrayList<MenuObject> mList){
		ArrayList<MenuObject> mArrayList = globalArrayMenuList;
		int i = 0;

		while(i<pathArray.length){
			int j=0;
			while(j<mArrayList.size()){
				if(mArrayList.get(j).getId().equals(pathArray[i])){
					mArrayList = mArrayList.get(j).getMenuObjectArrayList();
					break;
				}
				j++;
			}

			i++;
		}


		return mArrayList;
	}

	public String[] collectPathFromPathString(String path){
		String[] pathArray = new String[0];
		Log.d("path:", path);
		if(path.contains("_")){
			int i=0;
			int numOfPaths = 0;
			while(i<path.length()){
				if(path.charAt(i)=='_')
					numOfPaths++;
				i++;
			}
			numOfPaths++;
			pathArray = new String[numOfPaths];
			i=0;
			String temp="";
			int pathArrayIterator=0;
			while(i<path.length()){
				if(path.charAt(i)=='_'){
					pathArray[pathArrayIterator] = temp;
					pathArrayIterator++;
					temp = "";
				}else{
					temp += path.charAt(i);
				}
				i++;
			}
			//Add the last string
			pathArray[pathArrayIterator] = temp;
		}else{
			pathArray = new String[1];
			pathArray[0] = path;
		}

		return pathArray;
	}

	public void printArrayMenuList(ArrayList<MenuObject> mList){
		Iterator<MenuObject> i = mList.iterator();
		while(i.hasNext()){
			MenuObject m = (MenuObject) i.next();
			if(m.getType().equals("menu"))
				printArrayMenuList(m.getMenuObjectArrayList());
			Log.d("MenuObject:", m.toString());
		}
	}

	private String getMainMenu() {
		BufferedReader in = null;
		String page = "";
		String url = Constants.ROOT_SCHOOLAPP_URL + "app/menu/";
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
		Log.d("getMainMenu",page);
		return page;
	}

	public void returnResult(String result){

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked		
		MenuObject o = (MenuObject) this.getListAdapter().getItem(position);

		//String updateCookieTrail = cookieTrail.getText().toString();//+" > "+o.getTitle();
		//Log.d("MenuObject", o.toString());

		if(o.getType().equals("menu")){
			Log.d("GetThisPath", o.getPath());

			fillMainMenu(o.getPath());
			//			cookieTrail.setText(cookieTrail.getText().toString()+" > "+ o.getTitle());

			globalPath = o.getPath();
			Log.d("GLobalPath:",globalPath);

			m_adapter.notifyDataSetChanged();
		}else{
			Intent myIntent = new Intent(this, SchoolAppWebActivity.class);
			if(isInNotificationList(o.getChannel())){
				myIntent.putExtra("subscribe", true);
			}

			myIntent.putExtra("url", o.getLink());
			//			myIntent.putExtra("cookieTrail", cookieTrail.getText().toString()+ " > "+ o.getTitle());
			myIntent.putExtra("id", o.getChannel());
			myIntent.putExtra("title", o.getTitle());
			//myIntent.putExtra("pickupId", pickup.getId());
			startActivity(myIntent);
		}


	}

	private boolean isInNotificationList(String channelId){

		int i = 0;
		while(i<globalArrayNotificationList.size()){
			if(channelId.equals(globalArrayNotificationList.get(i).getChannelId()))
				return true;
			i++;
		}
		return false;
	}


	private class OrderAdapter extends ArrayAdapter<MenuObject> {

		private ArrayList<MenuObject> items;

		public OrderAdapter(Context context, int textViewResourceId, ArrayList<MenuObject> items) {
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
			MenuObject o = items.get(position);
			if (o != null) {

				TextView title = (TextView) v.findViewById(R.id.listItemTitle);
				title.setText(o.getTitle());

				CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);
				checkBox.setVisibility(View.GONE);

			}
			menuId = o.getId();
			return v;
		}
	}


	public void returnImageResult(String result) {
		//TODO
		//Handle success or fail of getting images
		
		Toast.makeText(SchoolAppListActivityListView.this, result, Toast.LENGTH_LONG).show();
		
	}

}
