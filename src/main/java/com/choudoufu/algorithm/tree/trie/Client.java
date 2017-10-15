package com.choudoufu.algorithm.tree.trie;

import com.choudoufu.algorithm.BaseClient;

/**
 * 
 * Trie树算法
 * 
 * @author lyq
 * 
 * 
 */
public class Client extends BaseClient{
	public static void main(String[] args) {
		String filePath = baseDataPath+"tree/trie/input.txt";
		
		TrieTool tool = new TrieTool(filePath);
		tool.constructTrieTree();
	}
}
