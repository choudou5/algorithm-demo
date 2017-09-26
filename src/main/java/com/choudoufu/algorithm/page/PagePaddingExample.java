package com.choudoufu.algorithm.page;

import com.choudoufu.algorithm.entity.Page;
import com.choudoufu.algorithm.entity.SqlPage;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页填补
 * @author xuhaowen
 * @create 2017-09-下午 8:37
 **/
public class PagePaddingExample {

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
        int type1Total = 2;
        int type2Total = 26;
        int type3Total = 15;

        int queryTotal = type1Total+type2Total+type3Total;
        int currPageEnd = pageNo * pageSize;//当前页尾数
        int queryRange = queryTotal+pageSize;
        if(currPageEnd > queryRange)//超出范围--终止
            return null;

        //查询类型1数据
        int currTypeTotal = 0;
        queryDataByType(currTypeTotal, currPageEnd, type1Total, pageSize, result, "type1");

        //查询类型2数据
        currTypeTotal = type1Total;
        queryDataByType(currTypeTotal, currPageEnd, type2Total, pageSize, result, "type2");

        //查询类型3数据
        currTypeTotal = type1Total+type2Total;
        queryDataByType(currTypeTotal, currPageEnd, type3Total, pageSize, result, "type3");

        page.setResult(result);
        page.setTotalCount(queryTotal);
        System.out.println("pageNo:"+pageNo+" query result size:"+result.size()+"\r\n");
        return page;
    }

    /**
     * 查找 数据
     * @param beforeTypeTotal 之前类型总数
     * @param currPageEnd 当前页尾值
     * @param currTypeTotal 当前类型总数
     * @param pageSize
     * @param result 结果集
     * @param type 类型
     */
    private static void queryDataByType(int beforeTypeTotal, int currPageEnd, int currTypeTotal, int pageSize, List<String> result, String type){
        //是否 需要查询
        if(isNeedQuery(beforeTypeTotal, currPageEnd, currTypeTotal, pageSize)){
            //计算 分页
            SqlPage sqlPage = calculatePage(currPageEnd, beforeTypeTotal, pageSize);
            List<String> list = findList(sqlPage, type);
            if(CollectionUtils.isNotEmpty(list))
                result.addAll(list);
        }
    }

    /**
     * 是否 需要查询
     * @param beforeTypeTotal
     * @param currPageEnd
     * @param currTypeTotal
     * @param pageSize
     * @return
     */
    private static boolean isNeedQuery(int beforeTypeTotal, int currPageEnd,  int currTypeTotal, int pageSize){
        int beforeRange = beforeTypeTotal+currTypeTotal;
        return (currTypeTotal > 0 && beforeTypeTotal < currPageEnd && (currPageEnd-beforeRange) < pageSize);
    }

    /**
     * 计算 分页
     * @param currPageEnd
     * @param beforeTypeTotal
     * @param pageSize
     * @return
     */
    private static SqlPage calculatePage(int currPageEnd, int beforeTypeTotal, int pageSize){
        int remain = currPageEnd - beforeTypeTotal;//剩余
        int pageNo = remain/pageSize, append = remain%pageSize;
        if(remain < pageSize){
            return new SqlPage(pageNo, append);
        }else{
            return new SqlPage(((pageNo-1)*pageSize)+append, pageSize);
        }
    }

    private static List<String> findList(SqlPage sqlPage, String type){
        System.out.println(type+" query limit:"+sqlPage.getLimit()+", "+sqlPage.getOffset());
        List<String> result = new ArrayList<String>();
        int start = sqlPage.getLimit();
        for (int i = 0; i < sqlPage.getOffset(); i++) {
            result.add(type+"-"+start);
            start++;
        }
        return result;
    }

}
