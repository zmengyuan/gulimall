package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;


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

    //每一个需要缓存的数据，我们都需要来指定要放到哪个名字的缓存中。通常按照业务类型进行划分。category是缓存区的概念
    @Cacheable(value = {"category"})
    @Override
    public List<CategoryEntity> getLevel1Catagories() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJson() {
        /*
        1、空结果缓存，解决缓存穿透
        2、设置过期时间（随机值），解决缓存雪崩
        3、加锁，解决缓存击穿
         */

        //1、加入缓存逻辑
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJson)) {
            log.info("缓存不命中，查询数据库");
            //2、缓存中没有，查询数据库
            Map<String, List<Catalog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedissonLock();

            return catelogJsonFromDb;
        }
        log.info("缓存命中，直接返回");
        //转为我们指定的对象。
        Map<String,List<Catalog2Vo>> result = JSON.parseObject(catalogJson,new TypeReference<Map<String,List<Catalog2Vo>>>(){});
        return result;
    }

    /**
     * 缓存里面的数据如何与数据库保持一致
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatelogJsonFromDbWithRedissonLock() {
//        占用分布式锁，去redis占坑  锁的粒度，越细越块
//        锁的粒度： 具体缓存的是某个数据 ，11号商品，product-11-lock
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catalog2Vo>> dataFromDb;
        try{
            dataFromDb  = getDataFromDb();
        }finally {
            lock.unlock();
        }
        return dataFromDb;
    }


    public Map<String, List<Catalog2Vo>> getCatelogJsonFromDbWithRedisLock() {
//        占用分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,300,TimeUnit.SECONDS);
        if (lock) {
            log.info("获取分布式锁成功");
            Map<String, List<Catalog2Vo>> dataFromDb;
            try{
                //加锁成功  执行业务
                dataFromDb  = getDataFromDb();
            }finally {
                //获取对比值+对比成功删除=原子操作
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                //执行脚本 删除锁  因为是脚本，所以肯定是原子执行
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return dataFromDb;
        }else {
            //加锁失败  重试
            //休眠100mx
            log.info("获取分布式锁失败。。。。等待重试");
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            return getCatelogJsonFromDbWithLocalLock();//自旋的方式
        }
    }

    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空，返回
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
            return result;
        }
        log.info("真的查询了数据库");
        /*
        1、将数据库的所有分类一次性查询
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //查出所有1级分类
        List<CategoryEntity> level1Category = getParentCid(selectList, 0L);

        Map<String, List<Catalog2Vo>> parent_cid = level1Category.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //查询到当前的二级分类
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            //封装成返回需要的Catelog2Vo
            List<Catalog2Vo> catalog2Vos = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类封装vo
                    List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
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

        //3、查到的数据放入缓存，将对象转换为Json字符串
        stringRedisTemplate.opsForValue().set("catalogJSON", JSON.toJSONString(parent_cid), 1, TimeUnit.DAYS);
        return parent_cid;
    }


    //一次性查询全部；从数据查询并封装分类数据  解决缓存击穿，加锁
    public Map<String, List<Catalog2Vo>> getCatelogJsonFromDbWithLocalLock() {
        /**
         * 只能是同一把锁
         * 1、synchronized(this)，SpringBoot得所有组件在容器中是单例的，所以可以。
         * 2、直接方法加
         */

        //TODO 本地锁，在分布式情况下想要锁住所有，要使用分布式锁
        synchronized (this){
            //得到锁之后，应该再去缓存中确定一次
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parentCid;
        }).collect(Collectors.toList());
        return collect;
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

    /**
 * 逻辑是
 * （1）根据一级分类，找到对应的二级分类
 * （2）将得到的二级分类，封装到Catelog2Vo中
 * （3）根据二级分类，得到对应的三级分类
 * （3）将三级分类封装到Catalog3List
 * @return
*/
    public Map<String, List<Catalog2Vo>> oldgetCatelogJson() {
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