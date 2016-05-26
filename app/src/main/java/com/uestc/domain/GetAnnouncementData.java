package com.uestc.domain;

import java.util.List;

/**
 * 
 * 这是获取
 *
 */
public class GetAnnouncementData {

	private boolean status;
	private List<Announcement> list ;
	private String description;
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public List<Announcement> getList() {
		return list;
	}
	public void setList(List<Announcement> list) {
		this.list = list;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
