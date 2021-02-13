package com.atguigu.gulimall.order.web;


import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1、展示支付宝支付页面
     * 2、支付成功以后，要跳到用户的订单列表里面
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

//        PayVo payVo = new PayVo();
//        payVo.setBody();//订单备注
//        payVo.setOut_trade_no();//订单号
//        payVo.setSubject();//订单主题
//        payVo.setTotal_amount();//订单金额
        PayVo payVo = orderService.getOrderPay(orderSn);
        //返回的是一个页面。将此页面直接交给浏览器就行
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
         /*
        支付宝的响应：<form name="punchout_form" method="post" action="https://openapi.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=YdraUOF%2Bu9lnoN9WVg22AQhniZXf28ffZf5V5vb7ajRtZ5I76lCZNCiH8%2BKJ0lCLLfb6PIvXXAQQFbiO9P89xou%2B11I%2FUm51ysptIsR7rzIFOiGQfSH2TpCjKIIZifPFAgZI8V7AKShdL6ejq0kcW%2FqMG0Jj14H0l1KqyfcGi6aPAc8JPJ3gXc8irUAzDkE5qNq7kzoZOjKIy%2FEv63L4lvBa8aDCRuV4dABti%2BhglYKaOj0IhDSh5BumWnrBll%2F%2FDuG1UDiXjILL5ddKGSE%2FIXPv3ZbNTneqD6OdGYuKXMDT0yEX4MiuZncrqThlJ2tMFmE5%2BLHX%2B6%2FROpoCZPL7iQ%3D%3D&version=1.0&app_id=2021000116660265&sign_type=RSA2&timestamp=2020-12-05+15%3A17%3A57&alipay_sdk=alipay-sdk-java-dynamicVersionNo&format=json">
<input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;202012051517520571335121191551672321&quot;,&quot;total_amount&quot;:&quot;5800.00&quot;,&quot;subject&quot;:&quot;华为 HUAWEI Mate 30 5G 麒麟990 4000万超感光徕卡影像双超级快充白色 6GB&quot;,&quot;body&quot;:&quot;颜色:白色;内存:6GB&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
<input type="submit" value="立即支付" style="display:none" >
</form>
<script>document.forms[0].submit();</script>

以上数据被浏览器一渲染，就直接访问到支付宝付款页面了
         */
        return pay;
    }
}
