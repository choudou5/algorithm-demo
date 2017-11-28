package com.choudoufu.algorithm;

import com.alibaba.fastjson.JSON;
import com.choudoufu.algorithm.category.randomforest.Client;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by xuhaowende on 2017/10/14.
 */
public class BaseClient {

    public static String baseDataPath = BaseClient.class.getResource("/data/").getPath();

    public static String readFile(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), "UTF-8");
    }

    protected static void print(byte[] strs) throws UnsupportedEncodingException {
        System.out.println(new String(strs, "UTF-8"));
    }

    protected static void print(String str){
        System.out.println(str);
    }

    protected static void len(String str){
        System.out.println("length:"+str.length());
    }

    protected static void len(byte[] str){
        System.out.println("length:"+str.length);
    }

    protected static void br(){
        System.out.println("\n");
    }

    protected static void printTime(long begin){
        printTime(begin, System.currentTimeMillis());
    }

    protected static void printTime(long begin, long end){
        System.out.println("time:"+(end-begin));
    }

    protected static void toJsonPrint(Object object){
        System.out.println(JSON.toJSONString(object));
    }

}
