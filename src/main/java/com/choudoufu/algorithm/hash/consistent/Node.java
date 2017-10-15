package com.choudoufu.algorithm.hash.consistent;

/**
 * 鑺傜偣绫?
 * @author lyq
 *
 */
public class Node implements Comparable<Node>{
	//鑺傜偣鍚嶇О
	String name;
	//鏈哄櫒鐨処P鍦板潃
	String ip;
	//鑺傜偣鐨刪ash鍊?
	Long hashValue;
	
	public Node(String name, String ip, long hashVaule){
		this.name = name;
		this.ip = ip;
		this.hashValue = hashVaule;
	}

	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		return this.hashValue.compareTo(o.hashValue);
	}
}
