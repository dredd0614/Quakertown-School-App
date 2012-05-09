package com.optimo.quakertown.objects;

public class ListObject extends MenuObject{

	public String link;
	
	public ListObject(){
		super.type="link";
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	
	
}
