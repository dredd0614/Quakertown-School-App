package com.optimo.quakertown.objects;

import android.database.Cursor;

import com.optimo.quakertown.database.SchoolAppChannelDBAdapter;

public class ChannelSubscribeObject {

	public long rowId;
	public String channelId;
	public String name;
	public long phoneEmailId;
	
	public ChannelSubscribeObject(Cursor c) {
		c.moveToFirst();
		this.rowId = c.getLong(c.getColumnIndex(SchoolAppChannelDBAdapter.ROW_ID));
		this.channelId = c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELID));
		this.name = c.getString(c.getColumnIndex(SchoolAppChannelDBAdapter.CHANNELNAME));
		this.phoneEmailId = c.getLong(c.getColumnIndex(SchoolAppChannelDBAdapter.PHONEEMAILID));
		c.close();
	}
	
	public ChannelSubscribeObject(){
		this.rowId = 0l;
		this.channelId = "";
		this.name = "";
		this.phoneEmailId = 0l;
	}
	
	public long getRowId() {
		return rowId;
	}
	public void setRowId(long rowId) {
		this.rowId = rowId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPhoneEmailId() {
		return phoneEmailId;
	}
	public void setPhoneEmailId(long phoneEmailId) {
		this.phoneEmailId = phoneEmailId;
	}
	
	
}
