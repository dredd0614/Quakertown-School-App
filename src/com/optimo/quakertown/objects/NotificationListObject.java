package com.optimo.quakertown.objects;

public class NotificationListObject {

	public String channelId;
	public String title;
	public int order;
	
	public String toString(){
		
		return 
		"id:" + this.channelId + "\n" + 
		"title:" +this.title + "\n" + 
		"order:" +this.order + "\n";
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	
	
}
