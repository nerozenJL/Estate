package com.uestc.domain;

/**
 * 
 * 用户信息数据
 *
 */
public class Person {


	private static String phone="18144240528", password="01234567", userName;

	public static String getUserName() {
		return Person.userName;
	}

	public static void setUserName(String userName) {
		Person.userName = userName;
	}

	public static String getPhone() {
		return Person.phone;
	}

	public static void setPhone(String phone) {
		Person.phone = phone;
	}

	public static String getPassword() {
		return Person.password;
	}

	public static void setPassword(String password) {
		Person.password = password;
	}

}
