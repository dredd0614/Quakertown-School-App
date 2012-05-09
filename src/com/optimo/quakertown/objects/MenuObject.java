package com.optimo.quakertown.objects;

import java.util.ArrayList;

public class MenuObject {

	public String name;
	public String id;
	public String path;
	public String title;
	public String channel;
	public String type;
	public String imageLink;
	public byte[] image;

	public int order;
	public String link;
	public ArrayList<MenuObject> menuObjectArrayList = new ArrayList<MenuObject>();

	public MenuObject(){

	}

	public String toString(){

		if(this.type.equals("menu")){
			return 
			"Name: " + this.name + "\n" + 
			"id: " + this.id + "\n" + 
			"path: " + this.path + "\n" +
			"title: " +this.title + "\n" + 
			"channel: " + this.channel + "\n" +
			"type: " +this.type + "\n"  + 
			"imageLink: " +this.imageLink + "\n"  + 
			"link: " +this.link + "\n"  + 
			"order: " +this.order + "\n"+
			"MenuArrayList: " + this.menuObjectArrayList.get(0).toString()+ "/n";
		}else{
			return 
			"Name:" + this.name + "\n" + 
			"id:" + this.id + "\n" + 
			"path:" + this.path + "\n" +
			"title:" +this.title + "\n" +
			"channel: " + this.channel + "\n" +
			"type:" +this.type + "\n"  + 
			"imageLink: " +this.imageLink + "\n"  + 
			"link:" +this.link + "\n"  + 
			"order:" +this.order + "\n";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public ArrayList<MenuObject> getMenuObjectArrayList() {
		return menuObjectArrayList;
	}

	public void setMenuObjectArrayList(ArrayList<MenuObject> menuObjectArrayList) {
		this.menuObjectArrayList = menuObjectArrayList;
	}


}
