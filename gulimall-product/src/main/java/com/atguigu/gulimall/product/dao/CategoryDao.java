package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zz
 * @email zmengyuan@126.com
 * @date 2020-12-21 22:40:58
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
