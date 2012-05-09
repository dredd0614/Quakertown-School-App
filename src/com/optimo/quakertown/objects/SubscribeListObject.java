package com.optimo.quakertown.objects;

public class SubscribeListObject {

	public String channelId;
	public String title;
	public int order;

	public SubscribeListObject(){
		this.channelId = "";
		this.title = "";
		this.order = 0;
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
