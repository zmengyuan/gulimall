package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {
    //在支付宝创建的应用的id TODO
    private   String app_id = "2016102600763190";

    // 商户私钥，您的PKCS8格式RSA2私钥 TODO
    private String merchant_private_key = "MjXN6Hnj8k2GAriRFt0BS9gjihbl9Rt38VMNbBi3Vt3Cy6TOwANLLJ/DfnYjRqwCG81fkyKlDqdsamdfCiTysCa0gQKBgQDYQ45LSRxAOTyM5NliBmtev0lbpDa7FqXL0UFgBel5VgA1Ysp0+6ex2n73NBHbaVPEXgNMnTdzU3WF9uHF4Gj0mfUzbVMbj/YkkHDOZHBggAjEHCB87IKowq/uAH/++Qes2GipHHCTJlG6yejdxhOsMZXdCRnidNx5yv9+2JI37QKBgQCw0xn7ZeRBIOXxW7xFJw1WecUV7yaL9OWqKRHat3lFtf1Qo/87cLl+KeObvQjjXuUe07UkrS05h6ijWyCFlBo2V7Cdb3qjq4atUwScKfTJONnrF+fwTX0L5QgyQeDX5a4yYp4pLmt6HKh34sI5S/RSWxDm7kpj+/MjCZgp6Xc51g==";

    // TODO 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjA74UKxt2F8VMIRKrRAAAuIMuawIsl4Ye+G12LK8P1ZLYy7ZJpgZ+Wv5nOs3DdoEazgCERj/ON8lM1KBHZOAV+TkrIcyi7cD1gfv4a1usikrUqm8/qhFvoiUfyHJFv1ymT7C4BI6aHzQ2zcUlSQPGoPl4C11tgnSkm3DlH2JZKgaIMcCOnNH+qctjNh9yIV9zat2qUiXbxmrCTtxAmiI3I+eVsUNwvwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // TODO  支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http://**.natappfree.cc/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //TODO 同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    //TODO  支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
