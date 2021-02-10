package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        //展示订单确认的数据
        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    /**
     * 下单
     * @param submitVo
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes redirectAttributes){
// 下单 去创建订单 验证令牌 核算价格 锁定库存
        SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
        log.info("下单提交的数据{}",submitVo.toString());
        if (responseVo.getCode() == 0) {
            // 下单成功到选择支付方式页面
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        } else {
            // 订单失败返回到订单确认页面
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}