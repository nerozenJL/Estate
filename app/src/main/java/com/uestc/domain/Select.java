package com.uestc.domain;


/**
 * 这是一般实体类，需要用到id 和一个String的实体类
 */
public class Select {

	private String id;
	private String text;
	
	public Select(String id,String text) {
		this.id=id;
		this.text=text;
	}
	public Select(){}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
