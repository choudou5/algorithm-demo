package com.choudoufu.algorithm.redpacket;

import java.math.BigDecimal;
import java.util.*;

/**
 * 工程文件： choudoufu.algorithm/com.choudoufu.algorithm.redpacket.Demo.java
 * 版权所有：广东联结电子商务有限公司(@Copyright 2017-2020)
 * 功能描述：
 * 修改人员：haowen@lianj.com
 * 修改时间：2018-04-25 09:02
 * 修改内容：创建文件
 */
public class Demo {

    //发红包算法，金额参数以分为单位 (程序员小灰)
    public static List<Integer> divideRedPackage(Integer totalAmount, Integer totalPeopleNum){
        List<Integer> amountList = new ArrayList<Integer>();
        Integer restAmount = totalAmount;
        Integer restPeopleNum = totalPeopleNum;
        Random random = new Random ();
        for ( int i= 0 ; i<totalPeopleNum- 1 ; i++){
            //随机范围：[1，剩余人均金额的两倍)，左闭右开
            int amount = random.nextInt(restAmount / restPeopleNum *  2 -  1 ) + 1 ;
            restAmount -= amount;
            restPeopleNum --;
            amountList.add(amount);
        }
        amountList.add(restAmount);
        return amountList;
    }

    // 通过剪线算法获得红包（保留两位有效数字）
    public static List<Double> generateDoublePacketsByLineCutting(int person, int money) {
        // 定义一个treeset
        List<Double> packets = new ArrayList<>();
        Random random = new Random();
        Set<Double> points = new TreeSet<>();
        while (points.size() < person - 1) {
            // 找到n-1个点
            Double num = (int) Math.round(random.nextDouble() * (money - 1) * 100) / 100.0;
            points.add(num);
        }
        // 记录最后一个点
        points.add(Double.valueOf(money));
        Double proPoint = 0d;
        for (Double point : points) {
            // 最后进行求值取差计算
            Double num2 = (int) Math.round(random.nextDouble() * (point - proPoint) * 100) / 100.0;
            packets.add(num2);
            proPoint = point;
        }
        return packets;
    }


    /**
     * 微信红包
     * @param remainSize 剩余的红包数量
     * @param remainMoney 剩余的钱
     * @return
     */
    public static double getRandomMoney(int remainSize, double remainMoney) {
        if (remainSize == 1) {
            remainSize--;
            return (double) Math.round(remainMoney * 100) / 100;
        }
        Random r     = new Random();
        double min   = 0.01; //
        double max   = remainMoney / remainSize * 2;
        double money = r.nextDouble() * max;
        money = money <= min ? 0.01: money;
        money = Math.floor(money * 100) / 100;
        remainSize--;
        remainMoney -= money;
        return money;
    }


    public static void main(String[] args){
//        int totalAmount = 20;
//        int count = 5;
//        double sum = 0;
//        List<Integer> amountList = divideRedPackage(totalAmount ,  count );
//        for ( Integer amount : amountList){
//            sum += amount;
//            System.out.println("抢到金额：" + new BigDecimal(amount));
//        }
//        System.out.println(sum);
//        System.out.println("--------以下为微信-------");
//        double restAmount = totalAmount;
//        sum = 0;
//        for (int i = count; i > 0; i--) {
//            double money = getRandomMoney(i, restAmount);
//            restAmount -= money;
//            System.out.println("抢到金额：" + new BigDecimal(money).divide(new BigDecimal(100)));
//            sum += money;
//        }
//        System.out.println(sum);

        // 抢红包保留两位有效数字
//        List<Double> list = generateDoublePacketsByLineCutting(4, 20);
//        System.out.println(list);
        System.out.println("---------");
        Random rd = new Random();
        System.out.println("金额：1000, 人数：10, 平分：100");
        float min = 200f;
        for (int i = 0; i <5000; i++) {
            float last = BalanceRedpacket.spilt(10, 1000).get(9);
            if(last < min)
                min = last;
        }
        System.out.println(min);

    }



}
