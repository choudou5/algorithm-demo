package com.choudoufu.algorithm.dynamicplan;

import com.alibaba.fastjson.JSON;
import com.choudoufu.algorithm.entity.Goods;
import org.apache.commons.collections.MapUtils;

import java.util.*;

/**
 * Desc: 背包问题（背包盗取价值最高的商品 ）
 * User: xuhaowende
 * Time: 2017/11/28
 */
public class PackageQuestion {

    private static List<Goods> goosList = new ArrayList<>();

    static{                                   //价格         //重量
        goosList.add(new Goods("吉他", 1500, 1));
        goosList.add(new Goods("音响", 3000, 4));
        goosList.add(new Goods("笔记本", 2000, 3));
        goosList.add(new Goods("Iphone", 1800, 1));
        goosList.add(new Goods("Mp3", 1000, 1));
    }

    final static float packageCapacity = 4; //背包容量

    public static void main(String[] args) {
        //1、根据容量 划分出表格
        Map<String, Map<Integer, Float>> table = new LinkedHashMap();
        String lastGoodName = null;
        for (Goods goods : goosList) {
            Map<Integer, Float> row = new LinkedHashMap<>();
            Map<Integer, Float> lastRow = lastGoodName!=null?table.get(lastGoodName):null;
            //获取 商品行 最大价值
            getGoodsRowMaxPrice(goods, row, lastRow);
            table.put(goods.getName(), row);
            lastGoodName = goods.getName();
        }
        //打印表格
        System.out.println("问题最小化开始......");
        System.out.println("-------------------------------------------------");
        System.out.println(getHead("商品名")+"|"+getHead("1-价格")+" | "+getHead("2-价格")+"|"+getHead("3-价格")+" | "+getHead("4-价格  |"));

        for (String goodName : table.keySet()) {
            Map<Integer, Float> row = table.get(goodName);
            System.out.println("-------------------------------------------------");
            System.out.print(getHead(goodName));
            for (Integer index : row.keySet()) {
                System.out.print("| "+getHead(row.get(index)+""));
            }
            System.out.print("|\r\n");
        }
        System.out.println("-------------------------------------------------");
        //输出 最佳偷取方案
        int len = table.size()-1;
        System.out.println("容量为"+packageCapacity+"包的最大装载价值："+table.get(lastGoodName).get(4)+"元");
        System.out.println("最佳偷取方案：你知道？");
    }

    /**
     * 获取 商品行 最大价值
     * @param goods
     * @param row
     * @param lastRow
     */
    private static void getGoodsRowMaxPrice(Goods goods, Map<Integer, Float> row, Map<Integer, Float> lastRow){
        List<Float> weightGrid = getGoodsWeightRow();
        int dex = 1;
        for (Float weight : weightGrid) {
            Float maxPrice = lastRow!=null?lastRow.get(dex):0f;
            //是否 单元格是否装得下
            if(goods.getWeight() <= weight){
                Float remainWeigthMaxPrice = getRemainWeigthMaxPrice(weight-goods.getWeight(), lastRow);
                if(maxPrice < remainWeigthMaxPrice+goods.getPrice()){
                    maxPrice = remainWeigthMaxPrice+goods.getPrice();
                }else{
                    maxPrice = goods.getPrice();
                }
            }
            row.put(dex, maxPrice);
            dex++;
        }
    }

    private static Float getRemainWeigthMaxPrice(Float remainWeight, Map<Integer, Float> lastRow){
        Float remainWeigthMaxPrice = 0f;
        if(lastRow == null)
            return remainWeigthMaxPrice;
        int dex = 0;
        List<Float> weightRow = getGoodsWeightRow();
        Float tdStarandWeight = 0f;

        for (Map.Entry<Integer, Float> entry : lastRow.entrySet()) {
            tdStarandWeight = weightRow.get(dex);
            if(remainWeight >= tdStarandWeight){
                remainWeigthMaxPrice = entry.getValue();
            }else{
                break;
            }
            dex++;
        }
        return remainWeigthMaxPrice;
    }


    /**
     * 获取 重量行
     * @return
     */
    private static List<Float> getGoodsWeightRow(){
        List<Float> grid = new ArrayList<>();
        for (float i = 1; i <= packageCapacity; i++) {
            grid.add(i);
        }
        return grid;
    }

    private static String getHead(String name){
        int len = 0;
        char[] chars = name.toCharArray();
        for (char c : chars) {
            len += c>128?2:1;
        }
        StringBuilder sb = new StringBuilder();
        if(8 > len){
            int diff = 8 - len;
            for (int i = 0; i < diff; i++) {
                sb.append(" ");
            }
        }
        return name+sb.toString();
    }
}
