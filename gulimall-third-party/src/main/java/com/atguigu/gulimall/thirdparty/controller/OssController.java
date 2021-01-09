package com.atguigu.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("thirdparty")
public class OssController {

  @Autowired
  OSS ossClient;

  @Value("${spring.cloud.alicloud.oss.endpoint}")
  private String endpoint;

  @Value("${spring.cloud.alicloud.oss.bucket}")
  private String bucket;

  @Value("${spring.cloud.alicloud.access-key}")
  private String accessId;


  @RequestMapping("/oss/policy")
  public R policy(){
    return new R().put("oss","oss");

//    以下为真实代码，我没有oss,，所以先注释了
//
//    // host的格式为 bucketname.endpoint
//    String host = "https://" + bucket + "." + endpoint;
//
//    // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
////    String callbackUrl = "http://88.88.88.88:8888";
//
//    // 用户上传文件时指定的前缀，也就是目录
//    String format = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
//    String dir = format;
//
//    // 创建OSSClient实例,引入自动依赖注入，这里不需要再new了
//    // OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
//
//    Map<String, String> respMap = null;
//
//    try {
//      long expireTime = 30;
//      long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
//      Date expiration = new Date(expireEndTime);
//      // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
//      PolicyConditions policyConds = new PolicyConditions();
//      policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
//      policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
//
//      String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
//      byte[] binaryData = postPolicy.getBytes("utf-8");
//      String encodedPolicy = BinaryUtil.toBase64String(binaryData);
//      String postSignature = ossClient.calculatePostSignature(postPolicy);
//
//      respMap = new LinkedHashMap<String, String>();
//      respMap.put("accessid", accessId);
//      respMap.put("policy", encodedPolicy);
//      respMap.put("signature", postSignature);
//      respMap.put("dir", dir);
//      respMap.put("host", host);
//      respMap.put("expire", String.valueOf(expireEndTime / 1000));
//      // respMap.put("expire", formatISO8601Date(expiration));
//
//      /*
//      JSONObject jasonCallback = new JSONObject();
//      jasonCallback.put("callbackUrl", callbackUrl);
//      jasonCallback.put("callbackBody",
//          "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//      jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//      String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//      respMap.put("callback", base64CallbackBody);
//       */
//
//    } catch (Exception e) {
//      // Assert.fail(e.getMessage());
//      System.out.println(e.getMessage());
//    } finally {
//      ossClient.shutdown();
//    }
//
//    return R.ok().put("data", respMap);
//
//
  }

}
