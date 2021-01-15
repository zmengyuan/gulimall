package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;


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

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查能否被删除
        baseMapper.deleteBatchIds(asList);
    }


    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return  paths.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Catagories() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

/**
 * 逻辑是
 * （1）根据一级分类，找到对应的二级分类
 * （2）将得到的二级分类，封装到Catelog2Vo中
 * （3）根据二级分类，得到对应的三级分类
 * （3）将三级分类封装到Catalog3List
 * @return
*/
    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJson() {
        //查出所有1级分类
        List<CategoryEntity> level1Category = getLevel1Catagories();

        Map<String, List<Catalog2Vo>> parent_cid = level1Category.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //查询到当前的二级分类
            List<CategoryEntity> categoryEntities =
                    baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            //封装成返回需要的Catelog2Vo
            List<Catalog2Vo> catalog2Vos = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(),null,l2.getCatId().toString(),l2.getName());
                    //1、找当前二级分类的三级分类封装vo
                    List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3Catelog != null) {
                        List<Catalog2Vo.Catalog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(collect);
                    }

                        return catalog2Vo;
                }).collect(Collectors.toList());
            }

            return catalog2Vos;
        }));


        return parent_cid;
    }
}