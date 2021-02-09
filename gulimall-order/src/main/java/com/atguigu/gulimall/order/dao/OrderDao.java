package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author 
 * @email 
 * @date 2021-02-09 14:15:49
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
