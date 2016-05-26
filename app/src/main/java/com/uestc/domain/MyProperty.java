package com.uestc.domain;


/**
 * 
 * 我的物业的实体类
 *
 */
public class MyProperty {

	private  String  code,type,status,propertySquare,location,property_status,buildingName;
	private int id,role;


	public String getBuildingName(){
		return  buildingName;
	}

	public void setBuildingName(String buildingName){
		this.buildingName = buildingName;
	}
	public String getProperty_status(){
		return  property_status;
	}

	public void setProperty_status(String status){
		this.property_status = status;
	}
	public int getRole(){
		return role;
	}

	public void setRole(int role){
		this.role = role;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPropertySquare() {
		return propertySquare;
	}

	public void setPropertySquare(String propertySquare) {
		this.propertySquare = propertySquare;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
