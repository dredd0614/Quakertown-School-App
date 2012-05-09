package com.optimo.quakertown.objects;


import android.database.Cursor;

import com.optimo.quakertown.database.SchoolAppPhoneEmailDBAdapter;

public class PhoneEmailListObject {
	
	private SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail;
	
	public long rowId;
	public String value;
	public String type;
	public int isTextable;
	public int isCallable;
	public boolean checked;
	
	public PhoneEmailListObject(){
		this.rowId = 0l;
		this.value = "";
		this.type = "";
		this.isTextable = 0;
		this.isCallable = 0;
		this.checked = false;
	}
	
	public PhoneEmailListObject(Cursor c) {
		c.moveToFirst();
		this.rowId = c.getLong(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ROW_ID));
		this.value = c.getString(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.VALUE));
		this.type = c.getString(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.TYPE));
		this.isTextable = c.getInt(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISTEXTABLE));
		this.isCallable = c.getInt(c.getColumnIndex(SchoolAppPhoneEmailDBAdapter.ISCALLABLE));

		c.close();
	}
	
	public SchoolAppPhoneEmailDBAdapter getDbHelperPickup() {
		return dbHelperPhoneEmail;
	}

	public void setDbHelperHoldMail(SchoolAppPhoneEmailDBAdapter dbHelperPhoneEmail) {
		this.dbHelperPhoneEmail = dbHelperPhoneEmail;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIsTextable() {
		return isTextable;
	}

	public void setIsTextable(int isTextable) {
		this.isTextable = isTextable;
	}

	public int getIsCallable() {
		return isCallable;
	}

	public void setIsCallable(int isCallable) {
		this.isCallable = isCallable;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	
	
}
