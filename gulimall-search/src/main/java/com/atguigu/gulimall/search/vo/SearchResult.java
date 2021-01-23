package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SearchResult {
    /**
     * 查询到的商品信息
     */
    private List<SkuEsModel> products;
    /*
    以下是分页信息
     */
    //当前页码
    private Integer pageNum;
    //    总记录数
    private Long total;
    //总页码
    private Integer totalPages;
    //可遍历的导航页
    private List<Integer> pageNavs;

    //当前查询到的结果，所有涉及到的品牌
    private List<BrandVo> brands;

    //当前查询到的结果，所有涉及到的分类
    private List<CatalogVo> catalogs;

    private List<AttrVo> attrs;


//=====================以上是返给页面的信息==========================
    //面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;//导航名字
        private String navValue;//导航值
        private String link;//取消之后跳到哪里
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
