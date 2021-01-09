package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1\查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //2、树型结构
        //2.1 找到所有一级分类
        List<CategoryEntity> level1 = categoryEntities.stream().filter((t) -> {
            return t.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu,categoryEntities));
            return menu;
        }).sorted((o1,o2) -> {
            return (o1.getSort()==null?0:o1.getSort())-(o2.getSort()==null?0:o2.getSort());
        }).collect(Collectors.toList());
        return level1;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        if (null == root) {
            return null;
        }

        List<CategoryEntity> children = all.stream().filter(t -> {
            return t.getParentCid() == root.getCatId();
        }).map((categoryEntity) -> {
            categoryEntity.setChildren(getChildrens(categoryEntity,all));
            return categoryEntity;
        }).sorted((o1,o2) -> {
            return (o1.getSort()==null?0:o1.getSort())-(o2.getSort()==null?0:o2.getSort());
        }).collect(Collectors.toList());
        return children;

    }
}