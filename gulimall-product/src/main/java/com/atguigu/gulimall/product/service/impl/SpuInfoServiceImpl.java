package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.vo.SpuSaveVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    /**
     * //TODO 高级部分完善
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info

        //2、保存Spu的描述图片 pms_spu_info_desc

        //3、保存spu的图片集 pms_spu_images

        //4、保存spu的规格参数;pms_product_attr_value

        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds

        //5、保存当前spu对应的所有sku信息；
        //5.1）、sku的基本信息；pms_sku_info
        //5.2）、sku的图片信息；pms_sku_image
        //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
        //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder打折\sms_sku_full_reduction满减\sms_member_price会员价格
    }
}