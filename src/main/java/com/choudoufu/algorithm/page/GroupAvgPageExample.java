package com.choudoufu.algorithm.page;

import com.choudoufu.algorithm.entity.Page;
import com.choudoufu.algorithm.entity.SqlPage;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组平均分页查询
 * @author xuhaowen
 * @create 2017-09-下午 8:29
 **/
public class GroupAvgPageExample {

    public static void main(String[] args) {
        int pageSize = 10;
        for (int pageNo = 1; pageNo < 8; pageNo++) {
            findPage(pageNo, pageSize);
        }
    }

    public static Page<String> findPage(Integer pageNo, Integer pageSize) {
        Page<String> page = new Page<String>(pageSize, pageNo);
        List<String> result = new ArrayList<String>();

        //各自类型 总数
        int type1Total = 22;
        int type2Total = 2;

        int avgPageSize = pageSize/2;
        int queryTotal = type1Total+type2Total;
        int pageTotal = pageNo * pageSize;//当前页尾数
        int queryRange = queryTotal+pageSize;
        if(pageTotal > queryRange)//超出范围--终止
            return null;

        //查询类型1数据
        SqlPage needPad = null;
        SqlPage nextNeedPad = getNextNeedPad(type2Total, pageNo, avgPageSize);
        if(nextNeedPad != null)
            needPad = nextNeedPad;
        needPad = queryDataByType(type1Total, pageTotal, pageNo, avgPageSize, result, 1, needPad);

        //查询类型2数据
        needPad = queryDataByType(type2Total, pageTotal, pageNo, avgPageSize, result, 2, needPad);

        page.setResult(result);
        page.setTotalCount(queryTotal);
        System.out.println("pageNo:"+pageNo+" query result size:"+result.size()+"\r\n");
        return page;
    }

    /**
     * 查找数据
     * @param currGroupTotal
     * @param pageTotal
     * @param pageNo
     * @param avgPageSize
     * @param result
     * @param groupNo
     * @param needPad
     * @return
     */
    private static SqlPage queryDataByType(int currGroupTotal, int pageTotal, int pageNo, int avgPageSize, List<String> result, int groupNo, SqlPage needPad){
        //计算 分页
        SqlPage sqlPage = null;
        int currGroupNeedTotal = pageNo*avgPageSize;
        if(currGroupTotal >= currGroupNeedTotal){
            int limit = (pageNo-1)*avgPageSize;
            //是否可填充
            if(needPad != null){
                limit += needPad.getLimit();
                avgPageSize += needPad.getOffset();
            }
            sqlPage = new SqlPage(limit, avgPageSize);
            List<String> list = findList(sqlPage, groupNo);
            if(CollectionUtils.isNotEmpty(list))
                result.addAll(list);
            return null;
        }else{
            int blankNum = currGroupNeedTotal - currGroupTotal;//空缺
            if(blankNum <= avgPageSize){
                sqlPage = new SqlPage(0, blankNum-1);
                List<String> list = findList(sqlPage, groupNo);
                if(CollectionUtils.isNotEmpty(list)) {
                    result.addAll(list);
                }
            }
            return calculatePage(blankNum, avgPageSize);
        }
    }


    //下一种类型 是否需要填充
    private static SqlPage getNextNeedPad(int currGroupTotal, int pageNo, int avgPageSize){
        SqlPage sqlPage = null;
        int currGroupNeedTotal = pageNo*avgPageSize;
        if(currGroupTotal >= currGroupNeedTotal){
            return null;
        }else{
            int blankNum = currGroupNeedTotal - currGroupTotal;//空缺
            return calculatePage(blankNum, avgPageSize);
        }
    }

    /**
     * 计算 分页
     * @param blankNum 空缺数
     * @param avgPageSize
     * @return
     */
    private static SqlPage calculatePage(int blankNum, int avgPageSize){
        int appendPageNo = blankNum/avgPageSize;
        int append = blankNum % avgPageSize;
        return appendPageNo>0? new SqlPage(blankNum-avgPageSize, avgPageSize) : new SqlPage(0, append);
    }

    private static List<String> findList(SqlPage sqlPage, int groupNo){
        System.out.println(groupNo+" query limit:"+sqlPage.getLimit()+", "+sqlPage.getOffset());
        List<String> result = new ArrayList<String>();
        int start = sqlPage.getLimit();
        for (int i = 0; i < sqlPage.getOffset(); i++) {
            result.add(groupNo+"-"+start);
            start++;
        }
        return result;
    }

}
