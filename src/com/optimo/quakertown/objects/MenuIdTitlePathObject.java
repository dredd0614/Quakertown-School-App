package com.optimo.quakertown.objects;


import android.os.Parcel;
import android.os.Parcelable;

public class MenuIdTitlePathObject implements Parcelable {
	
	public String id;
	public String title;
	public String path;
	
	public MenuIdTitlePathObject(){
		this.id="";
		this.title="";
		this.path="";
	}
	
	public MenuIdTitlePathObject(MenuObject m){
		this.id=m.getId();
		this.title=m.getTitle();
		this.path=m.getPath();		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public MenuIdTitlePathObject(Parcel in){
		readFromParcel(in);
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(path);

	}

	private void readFromParcel(Parcel in) {
		id = in.readString();
		title = in.readString();
		path = in.readString();

	}
	
    public static final Parcelable.Creator CREATOR =
    	new Parcelable.Creator() {
            public MenuIdTitlePathObject createFromParcel(Parcel in) {
                return new MenuIdTitlePathObject(in);
            }
 
            public MenuIdTitlePathObject[] newArray(int size) {
                return new MenuIdTitlePathObject[size];
            }
        };


	
}
