package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zz
 * @email zmengyuan@126.com
 * @date 2021-01-08 21:53:08
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
