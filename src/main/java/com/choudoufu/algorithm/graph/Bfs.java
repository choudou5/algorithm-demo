package com.choudoufu.algorithm.graph;

import com.choudoufu.algorithm.entity.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
		bfSearch("cs", "gz");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void bfSearch(String srcCode, String targetCode){
		List<Location> neighbors = getNeighbors(srcCode);//查找出发地 的邻居
		Queue<Location> queue = new ArrayBlockingQueue<Location>(20);
		queue.addAll(neighbors);	//将邻居添加到 队列
		List checked = new ArrayList();//已检查过的
		checked.add(srcCode);
		while(!queue.isEmpty()){
			Location location = queue.poll();
			String locaCode = location.getCode(); 
			if(!isChecked(checked, locaCode)){//仅当 这个地点未被检查过
				checked.add(location);
				if(targetCode.equals(locaCode)){
					System.out.println("找到目标地址了.");
					break;
				}else{
					queue.addAll(location.getNeighbors());//将其邻居加入到 队列尾
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
			for (Object t : checked) {
				if(t instanceof Location){
					Location loc = (Location)t;
					if(last.getpCode().equals(loc.getCode())){
						System.out.print(loc.getName()+"--->");
						pCode = loc.getpCode();
						lastPCode = loc.getpCode();
						break;
					}
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
	
	/**
	 * 获取邻居
	 * @param code
	 * @return
	 */
	private static List<Location> getNeighbors(String code){
		List<Location> locations = getLocations();
		for (Location location : locations) {
			if(location.getCode().equals(code))
				return location.getNeighbors();
		}
		return null;
	}
	
	/**
	 * 获取地址 列表
	 * @return
	 */
	private static List<Location> getLocations(){
		List<Location> list = new ArrayList<Location>(20);
		Location cs = new Location("长沙", "cs");
		Location yz = new Location("永州", "yz");
		Location gl = new Location("桂林", "gl");
		Location sg = new Location("韶关", "sg");
		Location gz = new Location("广州", "gz");
		
		//添加邻居
		yz.addNeighbor(cs).addNeighbor(gl);
		sg.addNeighbor(cs).addNeighbor(gz);
		gl.addNeighbor(yz).addNeighbor(gz);
		gz.addNeighbor(gl).addNeighbor(sg);
		cs.addNeighbor(yz).addNeighbor(sg);
		
		list.add(cs);
		list.add(yz);
		list.add(gl);
		list.add(sg);
		list.add(gz);
		return list;
	}
	
}
