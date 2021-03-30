package com.choudoufu.algorithm.level;

/**
 * XXX
 *
 * @author xuhaowende@sina.cn
 * @time 2020-04-09
 */
public class QQLevel {

    public static void main(String[] args) {
        System.out.println(getLevel(1, 0, 0, 2));
        System.out.println(getAllDays(65));
        System.out.println(getNextLevelNeedDayByCurrLevel(65));
        System.out.println(getNextLevelNeedDayByNextLevel(65));
        System.out.println(getLevel(4485));
    }

    public static int getLevel(int hg, int sun, int moon, int star){
        int level = 64 * hg + 16*sun + 4*moon + star;
        return level;
    }

    public static int getLevel(int days){
//        (等级 + 4) * 等级 = 活跃天数
        int i = 16 + 4 * days;
        double d = Math.floor(Math.sqrt(i));
        return (-4 + (int) d) >> 1;
//        double rank = Math.floor(-2 + Math.pow(16 - 4 * -days, 0.5) / 2);
//        return Double.valueOf(rank).intValue();
    }

    public static int getAllDays(int level){
        return (level + 4) * level;
    }

    public static int getNextLevelNeedDayByCurrLevel(int currLevel){
        return 2 * currLevel + 5;
    }

    public static int getNextLevelNeedDayByNextLevel(int nextLevel){
        return 2 * nextLevel + 3;
    }

}
