package com.choudoufu.algorithm.entity;

/**
 * SQL分页参数
 * @author xuhaowen
 * @create 2017-09-下午 8:34
 **/
public class SqlPage {

    private int limit;
    private int offset;

    public SqlPage(int limit, int offset){
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
