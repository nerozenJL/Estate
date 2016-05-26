package com.uestc.utils;

import android.content.Context;

import com.uestc.domain.Announcement;
import com.uestc.domain.GetAnnouncementData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是公告的json解析
 */
public class JsonTools {

	public static GetAnnouncementData getAnnouncement(Context context, String key,
			String jsonString) {
		List<Announcement> list = new ArrayList<Announcement>();
		GetAnnouncementData getAnnouncementData = new GetAnnouncementData();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			boolean statusString = jsonObject.getBoolean("status");
			getAnnouncementData.setStatus(statusString);
			if (statusString) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					Announcement announcement = new Announcement();
					announcement.setNoticeId(jsonObject2.getInt("id") + "");
					announcement.setTitle(jsonObject2.getString("title"));
					announcement.setDescription(jsonObject2.getString("description"));
					announcement.setTime(GetData.getdata(jsonObject2.getLong("createTime")));
					announcement.setContent(jsonObject2.getString("content"));
					list.add(announcement);
				}
			} else {
				getAnnouncementData.setDescription(jsonObject
						.getString("description"));
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		getAnnouncementData.setList(list);

		return getAnnouncementData;
	}


}
