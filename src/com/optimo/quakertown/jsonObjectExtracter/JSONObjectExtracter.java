package com.optimo.quakertown.jsonObjectExtracter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

import com.optimo.quakertown.constants.Constants;
import com.optimo.quakertown.objects.AppSettingsObject;
import com.optimo.quakertown.objects.MenuObject;
import com.optimo.quakertown.objects.NotificationListObject;
import com.optimo.quakertown.objects.SubscribeListObject;

public class JSONObjectExtracter {

	public String mainMenuJSONString;
	public MenuObject m;
	public ArrayList<MenuObject> mArrayList = new ArrayList<MenuObject>();

	public ArrayList<MenuObject> parseMainMenuJSONString(String menuList) throws JSONException{
		JSONObject jsonObject = new JSONObject(menuList);
		return parseMainMenuJSONObject(jsonObject);
	}
	
	public ArrayList<MenuObject> parseMainMenuJSONObject(JSONObject jsonObject) throws JSONException{
		
		ArrayList<MenuObject> menuObjectArrayList = new ArrayList<MenuObject>();
		
		Iterator<?> i = jsonObject.keys();
		while(i.hasNext()){
			String key = (String) i.next();
			JSONObject js = jsonObject.getJSONObject(key);
			MenuObject m = new MenuObject();
			m.setId(js.getString("id"));
			m.setPath(js.getString("path"));
			m.setTitle(js.getString("title"));
			try{
				m.setImageLink(js.getString("image"));
				File sdCard = Environment.getExternalStorageDirectory();
				File doesFileExist = new File(sdCard.getAbsolutePath()+"/com.optimo.quakertown/images/"+m.getImageLink());
				if (!doesFileExist.exists()) {
					getImage(m.getImageLink());
				}
			}catch(Exception e){
				m.setImageLink("");
			}
			m.setOrder(js.getInt("order"));
			m.setType(js.getString("type"));
			if(m.getType().equals("menu")){
			
				m.setMenuObjectArrayList(parseMainMenuJSONObject(js.getJSONObject("menu")));
			
			}else{
				m.setLink(js.getString("link"));
				m.setChannel(js.getString("channel"));
			}

			Log.d("Key", key);
			menuObjectArrayList.add(m);
		}
		return menuObjectArrayList;
	}
	
	public ArrayList<SubscribeListObject> parseSubscriptionListJSONString(String subscriptionList) throws JSONException{
		JSONArray jsonArray = new JSONArray(subscriptionList);
		return parseSubscriptionListJSONObject(jsonArray);
	}
	
	public ArrayList<SubscribeListObject> parseSubscriptionListJSONObject(JSONArray jsonArray) throws JSONException{
		
		ArrayList<SubscribeListObject> subscribeList = new ArrayList<SubscribeListObject>();
		
		int i = 0;
		
		while(i<jsonArray.length()){
			JSONObject js = (JSONObject) jsonArray.get(i);
			SubscribeListObject m = new SubscribeListObject();
			m.setChannelId(js.getString("id"));
			m.setTitle(js.getString("title"));
			m.setOrder(js.getInt("order"));
			subscribeList.add(m);
			Log.d("title",m.getTitle());
			i++;
		}
		return subscribeList;
	}
	
	private String getImage(String imageLink){

		if(!imageLink.equals(""))
		
		try{
			String urlBase = Constants.ROOT_SCHOOLAPP_URL + "img/"+Constants.ACTIVE_ID+"/"+imageLink;


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
	
	public ArrayList<NotificationListObject> parseNotificationJSONString(String menuList) throws JSONException{
		JSONObject jsonObject = new JSONObject(menuList);
		return parseNotificationJSONObject(jsonObject);
	}
	
	public ArrayList<NotificationListObject> parseNotificationJSONObject(JSONObject jsonObject) throws JSONException{
		
		ArrayList<NotificationListObject> menuObjectArrayList = new ArrayList<NotificationListObject>();
		
		Iterator<?> i = jsonObject.keys();
		while(i.hasNext()){
			String key = (String) i.next();
			JSONObject js = jsonObject.getJSONObject(key);
			NotificationListObject m = new NotificationListObject();
			m.setChannelId(js.getString("id"));
			m.setTitle(js.getString("title"));
			m.setOrder(js.getInt("order"));

			//Log.d("Key", key);
			menuObjectArrayList.add(m);
		}
		return menuObjectArrayList;
	}
	
	public AppSettingsObject parseSettingsJSONString(String settingsList) throws JSONException{
		JSONObject jsonObject = new JSONObject(settingsList);
		return parseSettingsJSONObject(jsonObject);
	}
	
	public AppSettingsObject parseSettingsJSONObject(JSONObject jsonObject) throws JSONException{
		

			AppSettingsObject m = new AppSettingsObject();
			
			m.setTitleBar(jsonObject.getString("titleBar"));
			m.setTitleBarRed(jsonObject.getString("titleBarRed"));
			m.setTitleBarGreen(jsonObject.getString("titleBarGreen"));
			m.setTitleBarBlue(jsonObject.getString("titleBarBlue"));
			m.setCellBackgroundRed(jsonObject.getString("cellBackgroundRed"));
			m.setCellBackgroundGreen(jsonObject.getString("cellBackgroundGreen"));
			m.setCellBackgroundBlue(jsonObject.getString("cellBackgroundBlue"));
			m.setCellTextRed(jsonObject.getString("cellTextRed"));
			m.setCellTextGreen(jsonObject.getString("cellTextGreen"));
			m.setCellTextBlue(jsonObject.getString("cellTextBlue"));
			m.setReplyto(jsonObject.getString("replyto"));
			m.setVersion(jsonObject.getString("version"));
			//m.setLevel(jsonObject.getString("level"));
			m.setLevel("2");

			//Log.d("Key", key);
		
		return m;
	}

}
