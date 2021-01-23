package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 */
@Data
public class SearchParam {
    // 检索关键字
    private String keyword;
//    三级分类id
    private Long catalog3Id;
    /*
     * 几个查询条件只能选一个
     * sort=saleCount_asc/desc 销量
     * sort=skuPrice_asc/desc 价格
     * sort=hotScore_asc/desc 热度
     */
    private String sort;//排序条件

    /*
     * 好多的过滤条件
     * hasStock(是否有货)、skuPrice区间、brandId、catalog3Id、attrs
     * hasStock=0/1   1-有库存 其他或空均是无库存
     * skuPrice=1_500/_500/500_
     */
    private Integer hasStock;//是否只显示有货
    private String skuPrice;//价格区间查询
    private List<Long> brandId;//按照品牌进行查询，可以多选
    private List<String> attrs;//按照属性进行筛选
    private Integer pageNum = 1;//页码
}
