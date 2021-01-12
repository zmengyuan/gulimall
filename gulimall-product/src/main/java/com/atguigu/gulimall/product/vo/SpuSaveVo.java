package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class SpuSaveVo {
    //商品名称
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    //重量
    private BigDecimal weight;
    //商品发布状态
    private int publishStatus;
    //商品描述图片
    private List<String> decript;
    //商品图集
    private List<String> images;
    //设置积分：  金币和成长值
    private Bounds bounds;
    //规格参数
    private List<BaseAttrs> baseAttrs;
    //销售属性
    private List<Skus> skus;

}
