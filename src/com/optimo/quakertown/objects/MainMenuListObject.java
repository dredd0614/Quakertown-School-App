package com.optimo.quakertown.objects;

public class MainMenuListObject {

	
	//Might not need this Object
	
	public String name;
	public String id;
	public String title;
	public String type;
	public int order;
	public String link;
	public MenuObject menu;
	
	public MainMenuListObject(){
		
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public MenuObject getMenu() {
		return menu;
	}

	public void setMenu(MenuObject menu) {
		this.menu = menu;
	}
	
}
