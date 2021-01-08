package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zz
 * @email zmengyuan@126.com
 * @date 2021-01-08 21:49:53
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
