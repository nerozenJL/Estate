package com.uestc.domain;

import java.io.Serializable;

/**
 * 
 * 投诉的实体类
 * 
 */
public class MyComplain implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id, type, status;
	private String title, content, description, time, result, times;

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}
	private double star;

	public double getStar() {
		return star;
	}

	public void setStar(double star) {
		this.star = star;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
