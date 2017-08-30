package com.choudoufu.algorithm.graph.entity;

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
	
	private List<Location> neighbors = new ArrayList<Location>();	//邻居
	
	public Location(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
	public Location(String name, String code, String pCode, List<Location> neighbors) {
		super();
		this.name = name;
		this.code = code;
		this.pCode = pCode;
		addNeighbor(neighbors);
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

	public List<Location> getNeighbors() {
		return neighbors;
	}

	public Location addNeighbor(Location e) {
		//堆存储缘故 所以新new个
		this.neighbors.add(new Location(e.getName(), e.getCode(), this.code, e.getNeighbors()));
		return this;
	}
	
	private void addNeighbor(List<Location> neighbors) {
		this.neighbors.addAll(neighbors);
	}
	

	@Override
	public String toString() {
		return "Location [name=" + name + ", code=" + code + "]";
	}
	
	
	
}
