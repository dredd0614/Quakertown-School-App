package com.optimo.quakertown.objects;

public class MenuIdTitlePathObject {

	//Might not need this object
	//Was gonna be used for creating a key/value pair for breadcrumbs
	
	public String id;
	public String title;
	public String path;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
