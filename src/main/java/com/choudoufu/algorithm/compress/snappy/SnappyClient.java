package com.choudoufu.algorithm.compress.snappy;

import com.choudoufu.algorithm.BaseClient;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Created by xuhaowende on 2017/10/16.
 */
public class SnappyClient extends BaseClient{

    public static void main(String[] args) throws IOException {

        String filePath = baseDataPath+ "compress/snappy/input.txt";

        String input = readFile(filePath);
        long begin = System.currentTimeMillis();
        byte[] compressed = Snappy.compress(input);
        byte[] compressed2 = Snappy.compress(compressed);
        print(compressed2);
        long end = System.currentTimeMillis();
        printTime(begin, end);
        br();
        byte[] uncompressed = Snappy.uncompress(compressed2);
        byte[] uncompressed2 = Snappy.uncompress(uncompressed);
        printTime(end);
        String result = new String(uncompressed2);
        print(result);
    }

    public static String encrypt2(String str) throws IOException {
        byte[] compressed = Snappy.compress(str);
        byte[] compressed2 = Snappy.compress(compressed);
        return new String(compressed2);
    }

    public static String decrypt2(String str) throws IOException {
        byte[] uncompressed = Snappy.uncompress(str.getBytes("utf-8"));
        String uncompressed2 = Snappy.uncompressString(uncompressed);
        return new String(uncompressed2);
    }


}
