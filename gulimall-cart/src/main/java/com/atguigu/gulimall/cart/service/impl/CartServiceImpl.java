package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.config.MyThreadConfig;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;


    /*
    cart
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        // 1、添加新商品到购物车（购物车无此商品）
        if (StringUtils.isEmpty(res)){
            CartItem cartItem = new CartItem();

            /**
             * 异步查询
             */
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                //1.1、远程查询要添加的商品信息
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });

                cartItem.setCheck(true);
                cartItem.setCount(1);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            },executor);
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                //1.2、远程查询sku的组合信息
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);
            CompletableFuture.allOf(getSkuInfo,getSkuSaleAttrValues).get();
            String jsonString = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),jsonString);
            return cartItem;
        }else {
            //2、购物车有此商品，将数据取出修改数量即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }
    }
    /**
     * 获取我们要操作的购物车，临时购物车、用户购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //得到用户信息 账号用户 、临时用户
        UserInfoVo userInfoTo = CartInterceptor.threadLocal.get();
        //1、userInfoTo.getUserId()不为空表示账号用户，反之临时用户  然后决定用临时购物车还是用户购物车
        //放入缓存的key
        String cartKey = "";
        if (userInfoTo.getUserId() != null){
            //gulimall:cart:1
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        return operations;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        //1、登录
        UserInfoVo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null){
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();

            //1.1、如果临时购物车的数据还没有合并【合并购物车】
            String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempsCartItems = getCartItems(tempCartKey);
            if (tempsCartItems != null){
                //临时购物车有数据，需要合并
                for (CartItem item : tempsCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清除临时购物车的数据
                clearCart(tempCartKey);
            }

            //1.2、获取登录后的购物车数据【包含合并过来的临时购物车的数据，和登录后的购物车数据 】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            //2、没登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车的所有购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }else {
            return null;
        }

    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1 ? true : false);
        String jsonString = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),jsonString);
    }
}
