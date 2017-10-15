package com.choudoufu.algorithm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 位置
 * @author xuhaowende
 * @date 2017年8月29日
 */
public class Location implements Serializable{
	
	private String name;	//名称
	private String code;	//编码
	
	private String pCode; //父编码


	public Location(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
	public Location(String name, String code, String pCode) {
		super();
		this.name = name;
		this.code = code;
		this.pCode = pCode;
	}

	public Location(Location location, String pCode) {
		super();
		this.name = location.getName();
		this.code = location.getCode();
		this.pCode = pCode;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getpCode() {
		return pCode;
	}

	public void setpCode(String pCode) {
		this.pCode = pCode;
	}

	@Override
	public String toString() {
		return "Location [name=" + name + ", code=" + code + "]";
	}
	
	
	
}
