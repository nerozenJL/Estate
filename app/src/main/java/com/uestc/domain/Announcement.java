package com.uestc.domain;

import java.io.Serializable;

/**
 *
 */
public class Announcement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String noticeId;
	private String title, content, time,description;
	public Announcement() {

	}

	public Announcement(String noticeId, String title, String content,
			String time, String description) {
		super();
		this.noticeId = noticeId;
		this.title = title;
		this.content = content;
		this.time = time;
		this.description =description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
