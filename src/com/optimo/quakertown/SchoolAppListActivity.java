package com.optimo.quakertown;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.optimo.quakertown.colors.ColorFixer;
import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.drawable.SchoolAppGradientDrawable;
import com.optimo.quakertown.jsonObjectExtracter.JSONObjectExtracter;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.MenuObject;
import com.optimo.quakertown.objects.NotificationListObject;

public class SchoolAppListActivity extends Activity{

	private static final String APP_SETTINGS_FILE = "AppSettingsFile";
	private static final String MENUFILE = "AppMenuFile";
	private static final String NOTIFICATIONFILE = "AppNotificationFile";

	SharedPreferences.Editor editor;
	SharedPreferences settings;

	//	private ListView lv = null;
	private String selectedId;
	//	private OrderAdapter m_adapter;
	public String menuId;
	ArrayList<MenuObject> globalArrayMenuList = null;
	ArrayList<MenuObject> globalMenuNavList = null;
	ArrayList<TextView> globalBreadCrumbList = null;

	ArrayList<NotificationListObject> globalArrayNotificationList = null;

	Stack<MenuObject> breadCrumb = new Stack<MenuObject>();
	String pathTrail = null;
	public String globalPath;
	private String titleColor;
	private String cellBackgroundColor;
	private String cellTextColor;
	
	HorizontalScrollView hScrollView;

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("GLobalPath:",globalPath);

			if(globalPath.equals("")){
				Log.d("GLobalPath:","blank");

				finish();
			}else if(globalPath.indexOf("_")==-1){
				Log.d("GLobalPath:","-1");
				breadCrumb.pop();
				//				breadCrumb.setText(breadCrumb.getText().toString().substring(0, breadCrumb.getText().toString().lastIndexOf(">")).trim());
				globalPath = "";
				generateUI();

				//m_adapter.notifyDataSetChanged();
			}else if(globalPath.indexOf("_")>0){
				Log.d("GLobalPath:",">0");
				breadCrumb.pop();

				globalPath = globalPath.substring(0, globalPath.lastIndexOf("_"));
				//				breadCrumb.setText(breadCrumb.getText().toString().substring(0, breadCrumb.getText().toString().lastIndexOf(">")).trim());

				generateUI();

				//m_adapter.notifyDataSetChanged();
			}else{
				Log.d("GLobalPath:","else");
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolappgridlistlayoutbreadcrumbs);

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
		cellBackgroundColor = cf.RBGStringToHexString(appSettingsObject.getCellBackgroundRed(), appSettingsObject.getCellBackgroundGreen(), appSettingsObject.getCellBackgroundBlue());
		cellTextColor = cf.RBGStringToHexString(appSettingsObject.getCellTextRed(), appSettingsObject.getCellTextGreen(), appSettingsObject.getCellTextBlue());


		StateListDrawable sd = (StateListDrawable)titlelayoutholder.getBackground();		
		titlelayoutholder.setBackgroundDrawable(SchoolAppGradientDrawable.generateGradientDrawable(titleColor));

		hScrollView = (HorizontalScrollView) findViewById(R.id.breadcrumbscrollhorizontally);

		
//		TextView actionTitle = (TextView) findViewById(R.id.actiontitle);
//		actionTitle.setText("Select a Category");

		Button loginButton = (Button) findViewById(R.id.loginbutton);
		ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsbutton);

		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(SchoolAppListActivity.this, SchoolAppLoginActivity.class);
				startActivity(myIntent);
			}

		});

		loginButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));
		if(appSettingsObject.getLevel().equals(Constants.WEBSITE_NAVIGATION_ONLY)){
			loginButton.setVisibility(View.GONE);
			settingsButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent myIntent = new Intent(SchoolAppListActivity.this, SchoolAppSettingsOptionsActivity.class);
					startActivity(myIntent);
				}

			});
			settingsButton.setBackgroundDrawable(SchoolAppGradientDrawable.generateStateListDrawable(titleColor));

			settingsButton.setVisibility(View.VISIBLE);

		}

		//String mainMenuJSONString = getMainMenu();

		settings = getSharedPreferences(MENUFILE, 0);

		String mainMenuJSONString = settings.getString(this.getString(R.string.JSONString), "");

		Log.d("mainMenuJSONString:", mainMenuJSONString+"a");

		JSONObjectExtracter jsonOE = new JSONObjectExtracter();
		try {
			globalArrayMenuList = jsonOE.parseMainMenuJSONString(mainMenuJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//String notificationJSONString = getNotificationMenu();

		settings = getSharedPreferences(NOTIFICATIONFILE, 0);

		String notificationJSONString = settings.getString(this.getString(R.string.JSONString), "");

		Log.d("mainMenuJSONString:", notificationJSONString+"a");

		jsonOE = new JSONObjectExtracter();
		try {
			globalArrayNotificationList = jsonOE.parseNotificationJSONString(notificationJSONString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalPath = "";
		generateUI();


	}
	
	private void generateUI(){
		generateBreadCrumbs();
		fillMainMenu(globalPath);
		updateMenuListUI();
	}

	private void generateBreadCrumbs(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.breadcrumblayout);
		ll.removeAllViews();

		//if(breadCrumb)
		if(breadCrumb.empty()){
			MenuObject homeMenuObject = new MenuObject();
			homeMenuObject.setTitle("Home"+"");
			homeMenuObject.setPath("");
			breadCrumb.push(homeMenuObject);
		}
		
		Iterator<MenuObject> i = breadCrumb.iterator();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

		while(i.hasNext()){
			MenuObject m = i.next();
			TextView t = new TextView(SchoolAppListActivity.this);
			t.setLayoutParams(params);
			t.setText(m.getTitle()+"");
			t.setTextColor(getResources().getColor(R.color.currentbreadcrumbgrey));
			t.setBackgroundDrawable(getResources().getDrawable(R.drawable.bread_crumb_background));
			if(!i.hasNext()){
				t.setTextColor(getResources().getColor(R.color.black));
			}
			t.setTextAppearance(SchoolAppListActivity.this, R.style.boldText);
			t.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					TextView pressedBreadCrumb = (TextView)v;
					int locationInBreadCrumb = findLocationInBreadCrumbStack(pressedBreadCrumb.getText().toString());
					locationInBreadCrumb++;//int i = 0;
					Log.d("locationInBreadCrumb",locationInBreadCrumb+"");

					int sizeOfBreadCrumbStack = breadCrumb.size();
					Log.d("sizeOfBreadCrumbStack",sizeOfBreadCrumbStack+"");

					while(locationInBreadCrumb<sizeOfBreadCrumbStack){
						Log.d("popping","Popping");

						breadCrumb.pop();
						locationInBreadCrumb++;
					}
					
					globalPath = breadCrumb.peek().getPath();
					//fillMainMenu(globalPath);
					generateUI();
				}

			});
			
			ll.addView(t);
			if(i.hasNext()){
				TextView arrow = new TextView(SchoolAppListActivity.this);
				arrow.setText(" > ");
				arrow.setLayoutParams(params);
				arrow.setTextColor(getResources().getColor(R.color.black));
				arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bread_crumb_arrow_background));
				ll.addView(arrow);
			}
		}
		Log.d("LL getChildCount", ll.getChildCount()+"");
		Log.d("breadCrumbSize", breadCrumb.size()+"");
	//	hScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
	}
	
	private int findLocationInBreadCrumbStack(String title){
		Iterator<MenuObject> i = breadCrumb.iterator();
		int location = 0;
		while(i.hasNext()){
			MenuObject m = i.next();
			if(title.equals(m.title))
				return location;
			location++;
		}
		return location;
	}

	private void updateMenuListUI() {
		generateBreadCrumbs();

		ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);

		LinearLayout rl = (LinearLayout) findViewById(R.id.listholder);
		rl.removeAllViews();
		//LinearLayout.LayoutParams paramsll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		//paramsll.gravity = Gravity.CENTER;
		//rl.setLayoutParams(paramsll);

		File sdCard = null;

		boolean sdcard = false;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			sdcard = true;
			sdCard = Environment.getExternalStorageDirectory();
		}

		TableLayout tl = null;//new TableLayout(this);
		TableRow tr = null;//new TableRow(this);
		boolean needNewTable = true;
		boolean needNewRow = true;

		//Need to program landscape and tablet layouts
		int columnNumber = 2;

		int ot = getResources().getConfiguration().orientation;
		if(ot==Configuration.ORIENTATION_LANDSCAPE){
			columnNumber = 3;
		}else if(ot==Configuration.ORIENTATION_PORTRAIT){
			columnNumber = 2;
		}

		Iterator<MenuObject> i = globalMenuNavList.iterator();
		while(i.hasNext()){
			final MenuObject m = i.next();


			if(!m.getImageLink().equals("")){

				Log.d("m.getImageLink()",m.getImageLink()+"a");

				if(tl==null&&needNewTable){
					tl = new TableLayout(SchoolAppListActivity.this);
					TableLayout.LayoutParams params = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					tl.setLayoutParams(params);
					tl.setStretchAllColumns(true);
					tl.setBackgroundColor(Color.parseColor("#"+cellBackgroundColor));
				}
				if(tr==null&&needNewRow){

					tr = new TableRow(SchoolAppListActivity.this);	
					//	TableRow.LayoutParams paramstr = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f);
					//	paramstr.gravity = Gravity.CENTER;
					//	tr.setLayoutParams(new TableLayout.LayoutParams(
					//	TableLayout.LayoutParams.FILL_PARENT,
					//	TableLayout.LayoutParams.FILL_PARENT));
					//	tr.setBackgroundColor(Color.parseColor("#"+"00ff00"));

				}

				Button ib = new Button(SchoolAppListActivity.this);
				TableRow.LayoutParams paramsib = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
				paramsib.gravity = Gravity.CENTER;
				ib.setLayoutParams(paramsib);
				String titleWithReturns = addReturnsToTitle(m.getTitle());
				ib.setText(titleWithReturns);
				ib.setTextColor(Color.parseColor("#"+cellTextColor));
				ib.setBackgroundColor(getResources().getColor(R.color.transparent));
				File f = new File (sdCard.getAbsolutePath() + "/com.optimo.quakertown/images/"+m.getImageLink());
				Drawable img = Drawable.createFromPath(f.getAbsolutePath());
				Rect r = new Rect();
				r.set(0, 0, 150, 150);
				img.setBounds(r);
				ib.setCompoundDrawables(null, img, null, null);//.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				ib.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {

						//						String updateBreadCrumb = breadCrumb.getText().toString();//+" > "+o.getTitle();
						//Log.d("MenuObject", o.toString());

						if(m.getType().equals("menu")){
							Log.d("GetThisPath", m.getPath());

							fillMainMenu(m.getPath());
							//							breadCrumb.setText(breadCrumb.getText().toString()+" > "+ m.getTitle());
							breadCrumb.push(m);

							globalPath = m.getPath();
							generateUI();


							//m_adapter.notifyDataSetChanged();
						}else{
							Intent myIntent = new Intent(SchoolAppListActivity.this, SchoolAppWebActivity.class);
							if(isInNotificationList(m.getChannel())){
								myIntent.putExtra("subscribe", true);
							}

							myIntent.putExtra("url", m.getLink());
							//myIntent.putExtra("breadCrumb", breadCrumb.getText().toString()+ " > "+ m.getTitle());
							myIntent.putExtra("id", m.getChannel());
							myIntent.putExtra("title", m.getTitle());
							startActivity(myIntent);
						}

					}

				});

				if(tr!=null){
					tr.addView(ib);
					needNewRow = false;
					needNewTable = false;
				}

				if(tr.getChildCount()>=columnNumber){
					if(tr.getParent()==null)
						tl.addView(tr);
					if(tl.getParent()==null)
						rl.addView(tl);
					tr = null;
					tl = null;		
					needNewRow = true;
					needNewTable = true;
				}

			}else{

				if(tl!=null){
					if(tr!=null){
						if(tr.getParent()==null)
							tl.addView(tr);
					}
					if(tl.getParent()==null)
						rl.addView(tl);
					tr = null;
					tl = null;		
					needNewRow = true;
					needNewTable = true;
				}


				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = vi.inflate(R.layout.listobject, null);
				//v.setId(a);

				v.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {

						//						String updateBreadCrumb = breadCrumb.getText().toString();//+" > "+o.getTitle();
						//Log.d("MenuObject", o.toString());

						if(m.getType().equals("menu")){
							Log.d("GetThisPath", m.getPath());

							fillMainMenu(m.getPath());
							//							breadCrumb.setText(breadCrumb.getText().toString()+" > "+ m.getTitle());
							breadCrumb.push(m);
							globalPath = m.getPath();
							Log.d("GLobalPath:",globalPath);
							generateUI();

							//m_adapter.notifyDataSetChanged();
						}else{
							Intent myIntent = new Intent(SchoolAppListActivity.this, SchoolAppWebActivity.class);
							if(isInNotificationList(m.getChannel())){
								myIntent.putExtra("subscribe", true);
							}

							myIntent.putExtra("url", m.getLink());
							//myIntent.putExtra("breadCrumb", breadCrumb.getText().toString()+ " > "+ m.getTitle());
							myIntent.putExtra("id", m.getChannel());
							myIntent.putExtra("title", m.getTitle());
							startActivity(myIntent);
						}

					}

				});

				TextView title = (TextView) v.findViewById(R.id.listItemTitle);
				title.setText(m.getTitle());

				CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);
				checkBox.setVisibility(View.GONE);

				rl.addView(v);
			}
			if(tr!=null&&tl!=null){
				if(tr.getParent()==null)
					tl.addView(tr);
				if(tl.getParent()==null)
					rl.addView(tl);
			}
		}
		sv.fullScroll(ScrollView.FOCUS_UP);
	}

	public void onClick(View v){

	}

	private String addReturnsToTitle(String title){
		String titleWithReturns = title.replace(" ", "\n");
		return titleWithReturns;
	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		//selectedId = m_adapter.getItem(pos).getId();

		//False will tell the parent that you didn't handle the onLongPress
		//Will load onCreateContextMenu
		return false;
	}

	public void onResume(){
		super.onResume();

		generateUI();


		//m_adapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("globalPath", globalPath);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		globalPath = savedInstanceState.getString("globalPath");

	}

	private void fillMainMenu(String path){
		//Log.d("fillMainMenuPath:", path);

		String[] a = null;
		if(path.equals(""))
			globalMenuNavList = globalArrayMenuList;
		else{
			a = collectPathFromPathString(path);
			Log.d("pathStringArraySize",a.length+"?");
			globalMenuNavList = findArrayListFromPath(a, globalArrayMenuList);
		}

		Collections.sort(globalMenuNavList, new Comparator<MenuObject>() {
			public int compare(MenuObject o1, MenuObject o2) {
				Integer a = (Integer)o1.getOrder();
				Integer b = (Integer)o2.getOrder();
				return a.compareTo(b); 
			}
		});
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

	public void returnResult(String result){

	}

	/*			KEEP FOR COOKIE TRAIL UPDATE
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
		//cookieTrail.setText(cookieTrail.getText().toString()+" > "+ o.getTitle());

		globalPath = o.getPath();
		Log.d("GLobalPath:",globalPath);

		//m_adapter.notifyDataSetChanged();
	}else{
		Intent myIntent = new Intent(this, SchoolAppWebActivity.class);
		if(isInNotificationList(o.getChannel())){
			myIntent.putExtra("subscribe", true);
		}

		myIntent.putExtra("url", o.getLink());
		//myIntent.putExtra("cookieTrail", cookieTrail.getText().toString()+ " > "+ o.getTitle());
		myIntent.putExtra("id", o.getChannel());
		myIntent.putExtra("title", o.getTitle());
		//myIntent.putExtra("pickupId", pickup.getId());
		startActivity(myIntent);
	}


}
	 */

	private boolean isInNotificationList(String channelId){

		int i = 0;
		while(i<globalArrayNotificationList.size()){
			if(channelId.equals(globalArrayNotificationList.get(i).getChannelId()))
				return true;
			i++;
		}
		return false;
	}


	public void returnImageResult(String result) {
		//TODO
		//Handle success or fail of getting images

		Toast.makeText(SchoolAppListActivity.this, result, Toast.LENGTH_LONG).show();

	}

}
