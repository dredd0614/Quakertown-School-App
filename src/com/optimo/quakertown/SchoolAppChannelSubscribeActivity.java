package com.optimo.quakertown;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelSubscribe;
import com.optimo.quakertown.asynctasks.AsyncTaskPhoneNumberChannelUnsubscribe;
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


public class SchoolAppChannelSubscribeActivity extends ListActivity {


	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	SharedPreferences settings;
	private String titleColor;

	Resources res = null;

	private ListView lv = null;
	//private long selectedId;
	private OrderAdapter m_adapter;
	public long menuId;
	ArrayList<NotificationListObject> globalArrayNotificationList = null;

	ArrayList<PhoneEmailListObject> phoneEmailArrayList = new ArrayList<PhoneEmailListObject>();
	ArrayList<ChannelSubscribeObject> channelSubscribeArrayList = new ArrayList<ChannelSubscribeObject>();

	//	TextView cookieTrail = null;
	private String name;
	private String channelId;

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

		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppChannelSubscribeActivity.this);
		dbHelperPhoneEmail.open();

		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppChannelSubscribeActivity.this);
		dbHelperChannel.open();

		Bundle extras = getIntent().getExtras();

		name = extras.getString("name");
		channelId = extras.getString("channelId");

		setContentView(R.layout.channelsubscribelayout);
		
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


		//RelativeLayout mainRelativelayout = (RelativeLayout) findViewById(R.id.mainRelativelayout);

		//		TextView title = (TextView) findViewById(R.id.cookieTrail);
		//		title.setText("Notification List");
		//		Button loginButton = (Button) findViewById(R.id.topBarLoginButton);
		//		loginButton.setVisibility(View.GONE);

		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
		actionTitle.setText(name);

		lv = getListView();

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return onLongListItemClick(v,pos,id);
			}
		});

		fillAllPhoneEmails();
		fillArrayList();

		registerForContextMenu(lv);

		//		View v = addPhoneAndEmailRow();
		//		lv.getId();

		//		mainRelativelayout.addView(v);
	}

	public void fillArrayList(){

		fillPhoneEmailArrayList();

		fillChannelSubscribeArrayList();

	}

	private void fillPhoneEmailArrayList() {
		Cursor c  = dbHelperPhoneEmail.getAllPhoneEmails();
		//		startManagingCursor(c);

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

				phoneEmailArrayList.add(row);
				c.moveToNext();
			}
		}
		c.close();
	}

	private void fillChannelSubscribeArrayList() {
		Cursor c = dbHelperChannel.getAllChannelConnections();
		//		startManagingCursor(c);

		if(!(c.moveToFirst())){
			c.close();
		}else{

			while(!c.isAfterLast()) {
				ChannelSubscribeObject row = new ChannelSubscribeObject();
				row.setRowId(c.getInt(c.getColumnIndex(SchoolAppChannelDBAdapter.ROW_ID)));
				row.setChannelId(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELID)));
				row.setName(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELNAME)));
				row.setPhoneEmailId(c.getLong(c.getColumnIndex(SchoolAppChannelDBAdapter.PHONEEMAILID)));
				channelSubscribeArrayList.add(row);
				c.moveToNext();
			}
		}
		c.close();
	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		//selectedId = m_adapter.getItem(pos).getRowId();

		//False will tell the parent that you didn't handle the onLongPress
		//Will load onCreateContextMenu
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		/*
		if (v.getId()==lv.getId()) {
			//			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("What would you like to do with this record?");

			String[] menuItems = getResources().getStringArray(R.array.phoneNumberEmailAddressMenu);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
		 */
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		/*
		int menuItemIndex = item.getItemId();

		String[] menuItems = getResources().getStringArray(R.array.phoneNumberEmailAddressMenu);
		String menuItemName = menuItems[menuItemIndex];

		if(menuItemName.equals(getResources().getString(R.string.delete))){

			deletePhoneEmail(selectedId);

		}else if(menuItemName.equals(getResources().getString(R.string.edit))){

			editPhoneEmail(selectedId);

		}

		fillAllPhoneEmails();
		m_adapter.notifyDataSetChanged();
		 */

		return true;
	}

	/*
	private void deletePhoneEmail(long selectedId2) {
		if(dbHelperPhoneEmail.deletePhoneEmail(selectedId2)){
			//Unsubscribe 
			Toast.makeText(this, "Deleted Record", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(this, "Error deleting Record", Toast.LENGTH_SHORT).show();
		}
	}

	private void editPhoneEmail(long selectedId2) {
		Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
		myIntent.putExtra("id", selectedId2);
		startActivity(myIntent);
	}
	*/

	public void onResume(){
		super.onResume();

		fillAllPhoneEmails();
		fillArrayList();

		m_adapter.notifyDataSetChanged();
	}

	private void fillAllPhoneEmails() {

		ArrayList<PhoneEmailListObject> mArrayList = new ArrayList<PhoneEmailListObject>();

		Cursor c = dbHelperPhoneEmail.getAllPhoneEmails();
		//		startManagingCursor(c);

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


				//System.out.println(row.getPhoneNumber());

				mArrayList.add(row);
				c.moveToNext();
			}
		}

		PhoneEmailListObject row = new PhoneEmailListObject();
		row.setRowId(0l);
		row.setValue(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addPhoneNumber));
		row.setType(Constants.PHONENUMBER);

		//System.out.println(row.getPhoneNumber());

		mArrayList.add(row);

		row = new PhoneEmailListObject();
		row.setRowId(0l);
		row.setValue(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addEmail));
		row.setType(Constants.EMAIL);

		//System.out.println(row.getPhoneNumber());

		mArrayList.add(row);

		c.close();

		m_adapter = new OrderAdapter(this, R.layout.phoneandemaillistobject, mArrayList);

		setListAdapter(m_adapter);
	}

	/*
	private View addPhoneAndEmailRow() {
		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.addphoneemaillistobject, null);

		RelativeLayout relativelayoutaddphonenumber = (RelativeLayout) v.findViewById(R.id.relativelayoutaddphonenumber);
		relativelayoutaddphonenumber.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

			}

		});

		RelativeLayout relativelayoutaddemailaddress = (RelativeLayout) v.findViewById(R.id.relativelayoutaddemailaddress);
		relativelayoutaddemailaddress.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

			}

		});

		return v;
	}
	*/

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked		
		Log.d("Did","WE GET HERE?@?#@?$#?%");
		PhoneEmailListObject o = (PhoneEmailListObject) this.getListAdapter().getItem(position);

		//String updateCookieTrail = cookieTrail.getText().toString();//+" > "+o.getTitle();


		if(o.getValue().equals(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addPhoneNumber))){
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", 0l);
			myIntent.putExtra("type", Constants.PHONENUMBER);
			myIntent.putExtra("channelId", channelId );
			myIntent.putExtra("name", name);
			startActivity(myIntent);
		}else if(o.getValue().equals(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addEmail))){
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", 0l);
			myIntent.putExtra("type", Constants.EMAIL);
			myIntent.putExtra("channelId", channelId );
			myIntent.putExtra("name", name);
			startActivity(myIntent);
		}else{
			if(o.isChecked()){
				//It is checked so we remove it from the database
				//Difficulty making the checkbox this corresponds to to be checked or not
			}else{
				//It is not checked so we must add it to the database
				//Difficulty making the checkbox this corresponds to to be checked or not
			}
		}

		/*
		if(o.getValue().equals(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addnewnumberandemailtitle))){
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", 0l);
			myIntent.putExtra("channelId", channelId );
			myIntent.putExtra("name", name);
			startActivity(myIntent);
		}else{

		}
		 */

	}


	private class OrderAdapter extends ArrayAdapter<PhoneEmailListObject> {

		private ArrayList<PhoneEmailListObject> items;

		public OrderAdapter(Context context, int textViewResourceId, ArrayList<PhoneEmailListObject> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.phoneandemaillistobject, null);
			}
			final PhoneEmailListObject o = items.get(position);
			if (o != null) {

				TextView value = (TextView) v.findViewById(R.id.value);
				value.setText(o.getValue());

				final CheckBox listItemCheckBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);

				if(o.getValue().equals(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addPhoneNumber))||
						o.getValue().equals(SchoolAppChannelSubscribeActivity.this.getResources().getString(R.string.addEmail))){
					listItemCheckBox.setVisibility(View.GONE);
					//For somereason this is setting it to all checkboxes

				}else{
					listItemCheckBox.setVisibility(View.VISIBLE);
				}

				//Check with channelsubscribelist to see if the checkbox 
				//should be selected or not	
				if(inChannelSubscribeList(o)){
					listItemCheckBox.setChecked(true);
				}else{
					listItemCheckBox.setChecked(false);
				}

				listItemCheckBox.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						if(listItemCheckBox.isChecked()){
							new AsyncTaskPhoneNumberChannelSubscribe(SchoolAppChannelSubscribeActivity.this, 
									Constants.ACTIVE_ID, o, channelId).execute();
						}else{
							new AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppChannelSubscribeActivity.this, 
									Constants.ACTIVE_ID, o, channelId).execute();
						}

					}

				});

				Log.d("","");
			}
			menuId = o.getRowId();
			return v;
		}
	}

	public boolean inChannelSubscribeList(PhoneEmailListObject o){

		int i = 0;
		while(i<channelSubscribeArrayList.size()){
			if(channelSubscribeArrayList.get(i).getName().equals(name)&&
					channelSubscribeArrayList.get(i).getPhoneEmailId() == o.getRowId()){
				return true;
			}
			i++;
		}
		return false;
	}

	public void returnResult(String result) {
		//TODO Handle response
		fillChannelSubscribeArrayList();

	}

	public void returnUnsubscribeResult(String result, long phoneEmailId ){
		if(result.contains("ERROR")){
			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppChannelSubscribeActivity.this);
			builder.setMessage("ERROR")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.setTitle("There was an error");
			AlertDialog alert = builder.create();
			alert.show();

		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppChannelSubscribeActivity.this);
			builder.setMessage("Unsubscribed")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.setTitle("You have been unsubscribed from "+name);
			AlertDialog alert = builder.create();
			alert.show();

			ChannelSubscribeObject channelSubscribeObject = new ChannelSubscribeObject(dbHelperChannel.getChannelConnection(channelId, name, phoneEmailId));
			dbHelperChannel.deleteChannelConnection(channelSubscribeObject.getRowId());

			fillChannelSubscribeArrayList();
		}

	}

	public void returnSubscribeResult(String result, long phoneEmailId){

		if(result.contains("ERROR")){
			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppChannelSubscribeActivity.this);
			builder.setMessage("ERROR")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.setTitle("There was an error");
			AlertDialog alert = builder.create();
			alert.show();

		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(SchoolAppChannelSubscribeActivity.this);
			builder.setMessage("Subscribed")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.setTitle("You have been subscribed to "+name);
			AlertDialog alert = builder.create();
			alert.show();
			dbHelperChannel.createChannelConnection(channelId, name, phoneEmailId);
			fillChannelSubscribeArrayList();
		}

	}
}
