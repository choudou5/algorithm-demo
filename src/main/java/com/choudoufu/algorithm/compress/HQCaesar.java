package com.choudoufu.algorithm.compress;

/**
 * Created by xuhaowende on 2017/10/17.
 */
public class HQCaesar {

    public static void main(String[] args) {
        String data = "asdfasdfasdfasdfasdfasdfasdfa";
        int k = 4;
        String result = encrypt(data, k);
        System.err.println("加密后：" + result);
        System.err.println("解密后：" + decrypt(result, k));
    }

    public static String encrypt(String str, int k)
    {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray())
        {
            // 如果字符串中的某个字符是小写字母
            if (c >= 'a' && c <= 'z')
            {
                c += k % 26; // 移动key%26位
                if (c < 'a')
                    c += 26; // 向左超界
                if (c > 'z')
                    c -= 26; // 向右超界
            }
            // 如果字符串中的某个字符是大写字母
            else if (c >= 'A' && c <= 'Z')
            {
                c += k % 26; // 移动key%26位
                if (c < 'A')
                    c += 26;// 同上
                if (c > 'Z')
                    c -= 26;// 同上
            }
            result.append(c);
        }
        return result.toString();
    }

    public static String decrypt(String str, int k)
    {
        // 取相反数
        k = 0 - k;
        return encrypt(str, k);
    }
}
