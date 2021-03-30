package com.choudoufu.algorithm.redpacket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 平衡红包拆分
 *@author xuhaowen
 *@time 2020-04-08
 */
public class BalanceRedpacket {

    private static boolean OPEN_LOG = false;

    // 通过剪线算法获得红包（保留两位有效数字）
    public static List<Float> spilt(int count, int money) {
        List<Float> packets = new ArrayList();
        float totalAvg = new BigDecimal(money).divide(new BigDecimal(count), 3, BigDecimal.ROUND_HALF_DOWN).floatValue();
        float sum = 0f, avg = totalAvg;
        int restPerson = count;
        float remain = Float.valueOf(money+"");
        while (packets.size() < count) {
            if(packets.size() == count -1){ //最后一个红包
                sum += remain;
                packets.add(remain);
                break;
            }
            float begin = avg - (avg*0.1f);
            float end = avg + (avg*0.1f);
            float num = getRandom(begin, end);
            sum += num;
            remain -= num;
            packets.add(num);
            restPerson--;
            avg = new BigDecimal(remain).divide(new BigDecimal(restPerson), 3, BigDecimal.ROUND_HALF_DOWN).floatValue();
        }
        if(OPEN_LOG)
            System.out.println("拆出："+sum+"，红包数："+packets.size() + "  列表："+packets);
        return packets;
    }

    private static float getRandom(float min, float max){
        Random random = new Random();
        int intMax = Math.round(max * 100);
        int intMin = Math.round(min * 100);
        float s = random.nextInt(intMax) % (intMax - intMin + 1) + intMin;
        return s/100;
    }

}
