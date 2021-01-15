package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * 1、R设计的时候可以添加泛型。但是现在再加不是很方便了
     * 2、直接返回我们想要的结果。对于不通过前端调用的
     * 3、自己封装解析结果
     * @param skuIds
     * @return
     */
    @PostMapping(value = "/ware/waresku/hasstock")
    R<List<SkuHasStockVo>> getSkusHasStock(@RequestBody List<Long> skuIds);
}
