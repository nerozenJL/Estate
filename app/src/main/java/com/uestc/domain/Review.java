package com.uestc.domain;
/**
 * 
 * 业主审核的实体类
 *
 */
public class Review {
	private String name,address,status,phone,code,role;
	
	private int id;
	public   Review(String name,String address,String status,String phone,int id,String code,String role) {
		// TODO Auto-generated constructor stub
		super();
		this.name=name;
		this.address=address;

		this.phone=phone;
		this.id=id;
		this.code = code;
		this.role = role;
		this.status = status;
	}


	public String getStatus(){
		return  this.status;
	}

	public void setStatus(String status){
		this.status = status;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Review(){
		
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}


	
}
