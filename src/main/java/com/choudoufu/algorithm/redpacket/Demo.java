package com.choudoufu.algorithm.redpacket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        int totalAmount = 5000;
        int count = 10;
        double sum = 0;
        List< Integer> amountList = divideRedPackage(totalAmount ,  count );
        for ( Integer amount : amountList){
            sum += amount;
            System.out.println("抢到金额：" + new BigDecimal(amount).divide(new BigDecimal(100)));
        }
        System.out.println(sum);
        System.out.println("---------------");
        double restAmount = totalAmount;
        sum = 0;
        for (int i = count; i > 0; i--) {
            double money = getRandomMoney(i, restAmount);
            restAmount -= money;
            System.out.println("抢到金额：" + new BigDecimal(money).divide(new BigDecimal(100)));
            sum += money;
        }
        System.out.println(sum);
    }

}
