package com.choudoufu.algorithm.graph;

import com.choudoufu.algorithm.entity.ListBuilder;
import com.choudoufu.algorithm.entity.Location;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 最短路径算法  (比如 求 长沙~广州的最短路线)
 *------------------长沙---------
 *------------------/-|---------
 *-----------------/--|---------
 *--------------永州---|---------
 *------------/-------|---------
 *------桂林—— --------|---------
 *------- | ----------|---------
 *------- —— -------韶关---------
 *----------|  --- / -----------
 *---------- —— 广州 --------------
 * @author xuhaowen
 * @date 2017年8月29日
 */
public class Bfs {
	
	public static void main(String[] args) {
		init();
		bfSearch("cs", "gz");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void bfSearch(String srcCode, String targetCode){
		List<Location> neighbors = getNeighbors(srcCode);//查找出发地 的邻居
		Queue<Location> queue = new ArrayBlockingQueue<Location>(20);
		queue.addAll(neighbors);	//将邻居添加到 队列
		List<Location> checked = new ArrayList();//已检查过的
		checked.add(getLocation(srcCode));
		while(!queue.isEmpty()){
			Location location = queue.poll();
			String locaCode = location.getCode(); 
			if(!isChecked(checked, locaCode)){//仅当 这个地点未被检查过
				checked.add(location);
				if(targetCode.equals(locaCode)){
					System.out.println("找到目标地址了.");
					break;
				}else{
					queue.addAll(getNeighbors(location.getCode()));//将其邻居加入到 队列尾
				}
			}
		}
		
		//打印
		System.out.println("最短路径为：");
		Location last = (Location)checked.get(checked.size()-1);
		String lastPCode = last.getpCode();
		String pCode = lastPCode;
		System.out.print(srcCode+"--->");
		while(true){
			for (Location loc : checked) {
				if(last.getpCode().equals(loc.getCode())){
					System.out.print(loc.getName()+"--->");
					pCode = loc.getpCode();
					lastPCode = loc.getpCode();
					break;
				}
			}
			if(pCode.equals(lastPCode)){
				break;
			}
		}
		System.out.print(targetCode);
	}
	
	private static <T extends Serializable> boolean isChecked(List<T> checked, String code){
		for (T t : checked) {
			if(t instanceof String){
				if(code.equals(t)){
					return true;
				}
			}else if(t instanceof Location){
				if(code.equals(((Location)t).getCode())){
					return true;
				}
			}
		}
		return false;
		
	}

	private static List<Location> locationList = null;
	private static Map<String, List> neighbor_Map = new HashMap<>();

	/**
	 * 初始化 列表
	 * @return
	 */
	private static void init(){
		List<Location> list = new ArrayList<Location>(20);
		Location cs = new Location("长沙", "cs");
		Location yz = new Location("永州", "yz");
		Location gl = new Location("桂林", "gl");
		Location sg = new Location("韶关", "sg");
		Location gz = new Location("广州", "gz");

		//添加邻居
		addNeighbor(cs, yz, sg);
		addNeighbor(yz, cs, gl);
		addNeighbor(sg, yz, gz);
		addNeighbor(gl, yz, gz);
		addNeighbor(gz, gl, sg);
		addNeighbor(gz, gl, sg);

		locationList = new ListBuilder().a(cs).a(yz).a(gl).a(sg).a(gz).getList();
	}

	/**
	 * 获取地址 列表
	 * @return
	 */
	private static List<Location> getLocations(){
		return locationList;
	}

	/**
	 * 获取地址
	 * @return
	 */
	private static Location getLocation(String code){
		for (Location location : locationList) {
			if(location.getCode().equals(code))
				return location;
		}
		return null;
	}

	private static void addNeighbor(Location location, Location ... neighbors){
		List<Location> neighborList = new ArrayList<>(neighbors.length);
		for (Location neighbor : neighbors) {
			neighborList.add(new Location(neighbor, location.getCode()));
		}
		neighbor_Map.put(location.getCode(), neighborList);
	}

	/**
	 * 获取邻居
	 * @param locationCode
	 * @return
	 */
	private static List<Location> getNeighbors(String locationCode){
		return neighbor_Map.get(locationCode);
	}
}
