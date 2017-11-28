package com.choudoufu.algorithm.entity;

import java.io.Serializable;

/**
 * Desc: 商品
 * User: xuhaowende
 * Time: 2017/11/28
 */
public class Goods implements Serializable {

    private String name;
    private float price;
    private float weight;

    public Goods() {
    }

    public Goods(String name, float price, float weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "name='" + name + '\'' +
                ", price=" + price +
                "元, weight=" + weight +
                '}';
    }
}
