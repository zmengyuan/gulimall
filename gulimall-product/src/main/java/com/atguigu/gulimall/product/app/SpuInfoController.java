package com.atguigu.gulimall.product.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.vo.Images;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.service.SpuInfoService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * spu信息
 *
 * @author zz
 * @email zmengyuan@126.com
 * @date 2021-01-09 11:34:12
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    @GetMapping("skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId){
        SpuInfoEntity entity = spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(entity);
    }

    /**
     * 商品上架
     * @param spuId
     * @return
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){
        /*
        //商品介绍
        List<String> list = Arrays.asList("https://img11.360buyimg.com/cms/jfs/t1/43665/32/14756/2824061/5d77f8b8E3e335a82/af0a3a0ac16b6bb2.png"
        ,"https://img11.360buyimg.com/cms/jfs/t1/43665/32/14756/2824061/5d77f8b8E3e335a82/af0a3a0ac16b6bb2.png");
        vo.setDecript(list);

        //商品图集
        vo.setImages(Arrays.asList("https://img11.360buyimg.com/cms/jfs/t1/43665/32/14756/2824061/5d77f8b8E3e335a82/af0a3a0ac16b6bb2.png"
        ,"https://img11.360buyimg.com/cms/jfs/t1/43665/32/14756/2824061/5d77f8b8E3e335a82/af0a3a0ac16b6bb2.png"
        ,"https://img11.360buyimg.com/cms/jfs/t1/43665/32/14756/2824061/5d77f8b8E3e335a82/af0a3a0ac16b6bb2.png"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"
        ,"https://img13.360buyimg.com/cms/jfs/t1/62964/9/10083/667577/5d7b1c64Ee24aac51/efc8083197de5b21.jpg"));


//        SKU的图片
        vo.getSkus().forEach(t -> {
            if (t.getSkuName().contains("紫色")) {
                Images i1 = new Images();
                i1.setImgUrl("https://img13.360buyimg.com/n5/s54x54_jfs/t1/143641/30/17904/94797/5fd32fb3Eb8607323/4c2af3d5b93c346c.jpg");
                i1.setDefaultImg(1);
                Images i2 = new Images();
                i2.setDefaultImg(0);
                i2.setImgUrl("https://img13.360buyimg.com/n5/s54x54_jfs/t1/139125/11/18022/56414/5fd34647E9eb6776d/fe658257b4618ec1.jpg");
                Images i3 = new Images();
                i3.setDefaultImg(0);
                i3.setImgUrl("https://img13.360buyimg.com/n5/s54x54_jfs/t1/154729/39/9425/55718/5fd34643E3e022ea0/2587e137e99ba414.jpg");
                List<Images> skuImages = new ArrayList<>();
                skuImages.add(i1);skuImages.add(i2);skuImages.add(i3);
                t.setImages(skuImages);
            }else if (t.getSkuName().contains("绿色")) {
                Images i1 = new Images();
                i1.setImgUrl("https://img12.360buyimg.com/n5/s54x54_jfs/t1/149546/39/17990/92486/5fd32fd0E8379f37e/072dd074563c4760.jpg");
                i1.setDefaultImg(1);
                Images i2 = new Images();
                i2.setDefaultImg(0);
                i2.setImgUrl("https://img12.360buyimg.com/n5/s54x54_jfs/t1/155482/27/9336/54074/5fd34670Ebc9b3207/9a5729a637650bc1.jpg");
                Images i3 = new Images();
                i3.setDefaultImg(0);
                i3.setImgUrl("https://img12.360buyimg.com/n5/s54x54_jfs/t1/142101/14/18061/57954/5fd34673E3472dbe7/27bad0c633bec831.jpg");
                List<Images> skuImages = new ArrayList<>();
                skuImages.add(i1);skuImages.add(i2);skuImages.add(i3);
                t.setImages(skuImages);
            }
        });

         */

        spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
