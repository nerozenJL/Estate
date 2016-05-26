package com.uestc.domain;

/**
 * 
 * 个人数据对象
 * 
 */

public class MyData {
	private int type,sex;
	
	private String name, born, emergency_people, emergency_contact,identityCode;

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getBorn() {
		return born;
	}

	public void setBorn(String born) {
		this.born = born;
	}

	public String getEmergency_people() {
		return emergency_people;
	}

	public void setEmergency_people(String emergency_people) {
		this.emergency_people = emergency_people;
	}

	public String getEmergency_contact() {
		return emergency_contact;
	}

	public void setEmergency_contact(String emergency_contact) {
		this.emergency_contact = emergency_contact;
	}

	public String getIdentityCode() {
		return identityCode;
	}

	public void setIdentityCode(String identityCode) {
		this.identityCode = identityCode;
	}

	


}
