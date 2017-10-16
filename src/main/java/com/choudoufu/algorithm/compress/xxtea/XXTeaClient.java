package com.choudoufu.algorithm.compress.xxtea;

import com.choudoufu.algorithm.BaseClient;
import com.choudoufu.algorithm.compress.FastLZ;
import com.choudoufu.algorithm.compress.snappy.SnappyClient;
import org.apache.commons.lang.StringUtils;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaowende on 2017/10/17.
 */
public class XXTeaClient extends BaseClient{

    /** 签名key */
    private static final String SIG_KEY = "demo";
    private static final int SIG_COUNT = 1;


    public static void main(String[] args) throws Exception {

        String filePath = baseDataPath+ "compress/snappy/input.txt";

        String input = readFile(filePath);
        long begin = System.currentTimeMillis();
        String compressed = XXTea.encrypt(input, SIG_KEY);
        print(compressed);
        len(compressed);
        byte[] codes = FastLZ.compress(compressed);
        print(codes);
        len(codes);
        long end = System.currentTimeMillis();
        printTime(begin, end);
        br();
        byte[] codes2 = FastLZ.decompress(codes, FastLZ.calcLength(compressed.length()));
        String uncompressed = XXTea.decrypt(codes2, SIG_KEY);
        printTime(end);
        String result = new String(uncompressed);
        print(result);
    }



    /**
     * 对称加密解密算法
     * @author xuhaowen
     * @date 2017年2月15日
     */
    private static class XXTea {

        private static long DELTA = 2654435769L;
        private static int MIN_LENGTH = 32;
        private static char SPECIAL_CHAR = '\0';

        public static String encrypt(String data, String key) {
            return toHexString(teaEncrypt(
                    toLongArray(padRight(data, MIN_LENGTH).getBytes(Charset.forName("UTF8"))),
                    toLongArray(padRight(key, MIN_LENGTH).getBytes(Charset.forName("UTF8")))));
        }

        public static String encrypt(byte[] data, String key) {
            return encrypt(new String(data), key);
        }

        public static String decrypt(String data, String key) {
            if (data == null || data.length() < MIN_LENGTH) {
                return data;
            }
            byte[] code = toByteArray(teaDecrypt(
                    toLongArray(data),
                    toLongArray(padRight(key, MIN_LENGTH).getBytes(Charset.forName("UTF8")))));
            return new String(code, Charset.forName("UTF8"));
        }

        public static String decrypt(byte[] data, String key) {
          return decrypt(new String(data), key);
        }

        private static long[] teaEncrypt(long[] data, long[] key) {
            int n = data.length;
            if (n < 1) {
                return data;
            }

            long z = data[data.length - 1], y = data[0], sum = 0, e, p, q;
            q = 6 + 52 / n;
            while (q-- > 0) {
                sum += DELTA;
                e = (sum >> 2) & 3;
                for (p = 0; p < n - 1; p++) {
                    y = data[(int) (p + 1)];
                    z = data[(int) p] += (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)
                            ^ (sum ^ y) + (key[(int) (p & 3 ^ e)] ^ z);
                }
                y = data[0];
                z = data[n - 1] += (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)
                        ^ (sum ^ y) + (key[(int) (p & 3 ^ e)] ^ z);
            }

            return data;
        }

        private static long[] teaDecrypt(long[] data, long[] key) {
            int n = data.length;
            if (n < 1) {
                return data;
            }

            long z = data[data.length - 1], y = data[0], sum = 0, e, p, q;
            q = 6 + 52 / n;
            sum = q * DELTA;
            while (sum != 0) {
                e = (sum >> 2) & 3;
                for (p = n - 1; p > 0; p--) {
                    z = data[(int) (p - 1)];
                    y = data[(int) p] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)
                            ^ (sum ^ y) + (key[(int) (p & 3 ^ e)] ^ z);
                }
                z = data[n - 1];
                y = data[0] -= (z >> 5 ^ y << 2) + (y >> 3 ^ z << 4) ^ (sum ^ y)
                        + (key[(int) (p & 3 ^ e)] ^ z);
                sum -= DELTA;
            }

            return data;
        }

        private static long[] toLongArray(byte[] data) {
            int n = (data.length % 8 == 0 ? 0 : 1) + data.length / 8;
            long[] result = new long[n];

            for (int i = 0; i < n - 1; i++) {
                result[i] = bytes2long(data, i * 8);
            }

            byte[] buffer = new byte[8];
            for (int i = 0, j = (n - 1) * 8; j < data.length; i++, j++) {
                buffer[i] = data[j];
            }
            result[n - 1] = bytes2long(buffer, 0);

            return result;
        }

        private static byte[] toByteArray(long[] data) {
            List<Byte> result = new ArrayList<Byte>();

            for (int i = 0; i < data.length; i++) {
                byte[] bs = long2bytes(data[i]);
                for (int j = 0; j < 8; j++) {
                    result.add(bs[j]);
                }
            }

            while (result.get(result.size() - 1) == SPECIAL_CHAR) {
                result.remove(result.size() - 1);
            }

            byte[] ret = new byte[result.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = result.get(i);
            }
            return ret;
        }

        public static byte[] long2bytes(long num) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            buffer.putLong(num);
            return buffer.array();
        }

        public static long bytes2long(byte[] b, int index) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            buffer.put(b, index, 8);
            return buffer.getLong(0);
        }

        private static String toHexString(long[] data) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                sb.append(padLeft(Long.toHexString(data[i]), 16));
            }
            return sb.toString();
        }

        private static long[] toLongArray(String data) {
            int len = data.length() / 16;
            long[] result = new long[len];
            for (int i = 0; i < len; i++) {
                result[i] = new BigInteger(data.substring(i * 16, i * 16 + 16), 16).longValue();
            }
            return result;
        }

        private static String padRight(String source, int length) {
            while (source.length() < length) {
                source += SPECIAL_CHAR;
            }
            return source;
        }

        private static String padLeft(String source, int length) {
            while (source.length() < length) {
                source = '0' + source;
            }
            return source;
        }
    }
}
