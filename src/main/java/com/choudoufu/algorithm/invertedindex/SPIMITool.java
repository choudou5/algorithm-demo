package com.choudoufu.algorithm.invertedindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * SPIMI�ڴ�ʽ����ɨ�蹹���㷨
 * @author lyq
 *
 */
public class SPIMITool {
	//������������ļ���ַ
	private String outputFilePath;
	// ������ĵ�����Ч���ļ���ַ
	private ArrayList<String> effectiveWordFiles;
	// �ڴ滺�������������ܹ������ӿռ�
	private ArrayList<String[]> buffers;
	
	public SPIMITool(ArrayList<String> effectiveWordFiles){
		this.effectiveWordFiles = effectiveWordFiles;
	}
	
	/**
	 * ���ļ��ж�ȡ����
	 * 
	 * @param filePath
	 *            �����ļ�
	 */
	private ArrayList<String> readDataFile(String filePath) {
		File file = new File(filePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();
		ArrayList<String> words = new ArrayList<>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		// ��ÿ�д�����ּ��뵽���б�������
		for (String[] array : dataArray) {
			for (String word : array) {
				words.add(word);
			}
		}

		return words;
	}
 
	
	/**
	 * �������е��ĵ����ݽ��е��������ļ��Ĺ���
	 * @param docs
	 * �ĵ�����
	 */
	private void writeInvertedIndex(ArrayList<Document> docs){
		ArrayList<String> datas;
		String[] recordData;
		
		buffers = new ArrayList<>();
		for(Document tempDoc: docs){
			datas = tempDoc.effectWords;
			
			for(String word: datas){
				recordData = new String[2];
				recordData[0] = word;
				recordData[1] = tempDoc.docId + "";
				
				addRecordToBuffer(recordData);
			}
		}
		
		//�������д����������
		writeOutOperation(buffers, outputFilePath);
	}
	
	/**
	 * ���¶�������ݼ�¼���뵽�ڴ滺���У������������뵽���ż�¼����
	 * @param insertedData
	 * �����������
	 */
	private void addRecordToBuffer(String[] insertedData){
		boolean isContained = false;
		String wordName;
		
		wordName = insertedData[0];
		for(String[] array: buffers){
			if(array[0].equals(wordName)){
				isContained = true;
				//��ӵ���������¼���ԣ�����
				array[1] += ":" + insertedData[1];
				
				break;
			}
		}
		
		//���û�а�������˵�����µ�����,ֱ�����
		if(!isContained){
			buffers.add(insertedData);
		}
	}
	
	/**
	 * ������д���������ļ�����������ļ��Ѿ����ڣ������ļ�β����������׷��
	 * @param buffer
	 * ��ǰд�����е�����
	 * @param filePath
	 * �����ַ
	 */
	private void writeOutOperation(ArrayList<String[]> buffer, String filePath) {
		StringBuilder strBuilder = new StringBuilder();
		
		//�������е���������ַ�д�뵽�ļ���
		for(String[] array: buffer){
			strBuilder.append(array[0]);
			strBuilder.append(" ");
			strBuilder.append(array[1]);
			strBuilder.append("\n");
		}
		
		try {
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.println(strBuilder.toString());// ���ļ���д���ַ���
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ���쵹�������ļ�
	 */
	public void createInvertedIndexFile(){
		int docId = 1;
		String baseFilePath;
		String fileName;
		String p;
		int index1 = 0;
		int index2 = 0;
		Document tempDoc;
		ArrayList<String> words;
		ArrayList<Document> docs;
		
		outputFilePath = "spimi";
		docs = new ArrayList<>();
		p = effectiveWordFiles.get(0);
		//��ȡ�ļ�����
		index1 = p.lastIndexOf("\\");
		baseFilePath = p.substring(0, index1+1);
		outputFilePath = baseFilePath + "spimi";
		
		for(String path: effectiveWordFiles){
			//��ȡ�ĵ���Ч��
			words = readDataFile(path);
			tempDoc = new Document(words, path, docId);
			
			docId++;
			docs.add(tempDoc);
			
			//��ȡ�ļ�����
			index1 = path.lastIndexOf("\\");
			index2 = path.lastIndexOf(".");
			fileName = path.substring(index1+1, index2);
			
			outputFilePath += "-" + fileName;
		}
		outputFilePath += ".txt";
		
		//�����ĵ����ݽ��е��������ļ��Ĵ���
		writeInvertedIndex(docs);
	}

}
