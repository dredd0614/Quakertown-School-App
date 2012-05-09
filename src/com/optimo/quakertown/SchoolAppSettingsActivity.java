package com.optimo.quakertown;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
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


public class SchoolAppSettingsActivity extends ListActivity {


	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	private SchoolAppChannelDBAdapter dbHelperChannel;

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	SharedPreferences settings;
	private String titleColor;
	
	Resources res = null;
	
	private ListView lv = null;
	private PhoneEmailListObject phoneEmailListObject;
	private OrderAdapter m_adapter;
	public long menuId;
	ArrayList<NotificationListObject> globalArrayNotificationList = null;
	//	TextView cookieTrail = null;
	String pathTrail = null;
	public String globalPath;

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

		dbHelperPhoneEmail = new SchoolAppPhoneEmailDBAdapter(SchoolAppSettingsActivity.this);
		dbHelperPhoneEmail.open();

		dbHelperChannel = new SchoolAppChannelDBAdapter(SchoolAppSettingsActivity.this);
		dbHelperChannel.open();

		setContentView(R.layout.settingslayout);
		
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


		//		TextView title = (TextView) findViewById(R.id.cookieTrail);
		//		title.setText("Notification List");
		//		Button loginButton = (Button) findViewById(R.id.topBarLoginButton);
		//		loginButton.setVisibility(View.GONE);

		final Button settingsButton = (Button) findViewById(R.id.settingsbutton);
		settingsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SchoolAppSettingsActivity.this, SchoolAppSettingsOptionsActivity.class);
				startActivity(myIntent);
			}

		});
		settingsButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));


		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
		actionTitle.setText(res.getString(R.string.phonenumbersandemailsactiontitle));


		lv = getListView();

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				return onLongListItemClick(v,pos,id);
			}
		});

		globalPath = "";
		fillAllPhoneEmails();

		registerForContextMenu(lv);

	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		phoneEmailListObject = m_adapter.getItem(pos);

		//False will tell the parent that you didn't handle the onLongPress
		//Will load onCreateContextMenu
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==lv.getId()) {
			//			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("What would you like to do with this record?");

			String[] menuItems = getResources().getStringArray(R.array.phoneNumberEmailAddressMenu);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		String[] menuItems = getResources().getStringArray(R.array.phoneNumberEmailAddressMenu);
		String menuItemName = menuItems[menuItemIndex];

		if(menuItemName.equals(getResources().getString(R.string.delete))){

			deletePhoneEmail(phoneEmailListObject);

		}else if(menuItemName.equals(getResources().getString(R.string.edit))){

			editPhoneEmail(phoneEmailListObject);

		}

		fillAllPhoneEmails();
		m_adapter.notifyDataSetChanged();

		return true;
	}

	private void deletePhoneEmail(PhoneEmailListObject phoneEmailListObject) {
		if(dbHelperPhoneEmail.deletePhoneEmail(phoneEmailListObject.getRowId())){

			ArrayList<ChannelSubscribeObject> channelListFromPhoneEmailId = new ArrayList<ChannelSubscribeObject>();

			Cursor c = dbHelperChannel.getChannelSubscribersListFromPhoneEmailId(phoneEmailListObject.getRowId());
//			startManagingCursor(c);

			if(!(c.moveToFirst())){
				c.close();
			}else{

				while(!c.isAfterLast()) {
					ChannelSubscribeObject row = new ChannelSubscribeObject();
					row.setRowId(c.getInt(c.getColumnIndex(SchoolAppChannelDBAdapter.ROW_ID)));
					row.setChannelId(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELID)));
					row.setName(c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELNAME)));
					row.setPhoneEmailId(c.getLong(c.getColumnIndex(SchoolAppChannelDBAdapter.PHONEEMAILID)));
					channelListFromPhoneEmailId.add(row);
					c.moveToNext();
				}
			}
			c.close();

			//			getChannelSubscribersListFromPhoneEmailId(selectedId2);
			new AsyncTaskPhoneNumberChannelUnsubscribe(SchoolAppSettingsActivity.this, 
					Constants.ACTIVE_ID, channelListFromPhoneEmailId, phoneEmailListObject).execute();

			Toast.makeText(this, "Deleted Record", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(this, "Error deleting Record", Toast.LENGTH_SHORT).show();
		}
	}  

	public void returnResult(String result, ArrayList<ChannelSubscribeObject> channelListFromPhoneEmailId, PhoneEmailListObject phoneEmailListObject) {
		//Add array list to database
		//dbHelperChannel
		int i = 0;
		Cursor c = null;
		ChannelSubscribeObject channelSubscribeObject = null;
		while(i<channelListFromPhoneEmailId.size()){
			c = dbHelperChannel.getChannelConnection(channelListFromPhoneEmailId.get(i).getChannelId(), channelListFromPhoneEmailId.get(i).getName(), phoneEmailListObject.getRowId());
			if(c.moveToFirst()){
				channelSubscribeObject = new ChannelSubscribeObject(c);	
				dbHelperChannel.deleteChannelConnection(channelSubscribeObject.getRowId());
			}
			i++;
		}
	}

	private void editPhoneEmail(PhoneEmailListObject phoneEmailListObject) {
		Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
		myIntent.putExtra("id", phoneEmailListObject.getRowId());
		myIntent.putExtra("type", phoneEmailListObject.getType());
		startActivity(myIntent);
	}

	public void onResume(){
		super.onResume();

		fillAllPhoneEmails();

		m_adapter.notifyDataSetChanged();
	}

	private void fillAllPhoneEmails() {

		//		dbHelperHoldMailListObjects.createHoldMailListObject(3, "HoldMailName", "cancel", "nickname");

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


				System.out.println(row.getValue());

				mArrayList.add(row);
				c.moveToNext();
			}
		}

		PhoneEmailListObject row = new PhoneEmailListObject();
		row.setRowId(0l);
		row.setValue(SchoolAppSettingsActivity.this.getResources().getString(R.string.addPhoneNumber));
		row.setType(Constants.PHONENUMBER);

		//System.out.println(row.getPhoneNumber());

		mArrayList.add(row);
		
		row = new PhoneEmailListObject();
		row.setRowId(0l);
		row.setValue(SchoolAppSettingsActivity.this.getResources().getString(R.string.addEmail));
		row.setType(Constants.EMAIL);

		//System.out.println(row.getPhoneNumber());

		mArrayList.add(row);

		c.close();

		m_adapter = new OrderAdapter(this, R.layout.phoneandemaillistobject, mArrayList);

		setListAdapter(m_adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked		
		Log.d("Did","WE GET HERE?@?#@?$#?%");
		PhoneEmailListObject o = (PhoneEmailListObject) this.getListAdapter().getItem(position);

		//String updateCookieTrail = cookieTrail.getText().toString();//+" > "+o.getTitle();


		if(o.getValue().equals(SchoolAppSettingsActivity.this.getResources().getString(R.string.addPhoneNumber))){
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", 0l);
			myIntent.putExtra("type", Constants.PHONENUMBER);
			startActivity(myIntent);
		}else if(o.getValue().equals(SchoolAppSettingsActivity.this.getResources().getString(R.string.addEmail))){
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", 0l);
			myIntent.putExtra("type", Constants.EMAIL);
			startActivity(myIntent);
		}else{
			Intent myIntent = new Intent(this, SchoolAppEnterPhoneEmailActivity.class);
			myIntent.putExtra("id", o.getRowId());
			myIntent.putExtra("type", o.getType());
			startActivity(myIntent);
		}

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
			PhoneEmailListObject o = items.get(position);
			if (o != null) {

				TextView value = (TextView) v.findViewById(R.id.value);
				value.setText(o.getValue());

				CheckBox listItemCheckBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);
				listItemCheckBox.setVisibility(View.GONE);

			}
			menuId = o.getRowId();
			return v;
		}
	}



}
