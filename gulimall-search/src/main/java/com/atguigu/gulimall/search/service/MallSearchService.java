package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;

public interface MallSearchService {
    /**
     *
     * @param searchParam 检索的所有参数
     * @return 返回的结果
     */
    Object search(SearchParam searchParam);
}
