package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        //1、接口防刷
        String prefixPhone = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String redisCode = stringRedisTemplate.opsForValue().get(prefixPhone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() -l < 60000){
                //60秒内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //2、验证码再次验证 redis
        String code = String.valueOf((int)((Math.random() + 1) * 100000));
        //redis缓存验证码   防止同一个phone在60s内再次发送验证码  set(K var1, V var2, long var3, TimeUnit var5)
        stringRedisTemplate.opsForValue().set(prefixPhone,code + "_" + System.currentTimeMillis(),10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }

    /**
     * RedirectAttributes实际上是模拟session储存的，TODO 分布式下的session
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            Map<String, String> errors =result.getFieldErrors().stream().collect(Collectors.toMap(fieldError -> {
                 return fieldError.getField();
             },(fieldError) ->{
                 return fieldError.getDefaultMessage();
             }));
//            model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors",errors);


            /**
             * 使用 return "forward:/reg.html"; 会出现
             * 问题：Request method 'POST' not supported的问题
             * 原因：用户注册-> /regist[post] ------>转发/reg.html (路径映射默认都是get方式访问的)
             * 校验出错转发到注册页。
             */
//            return "forward:/reg.html";//这种是重新走viewController
//            转发模式会造成重复提交表单的问题，所以改成重定向
//            return "reg";//这种是转发直接到视图
//            使用重定向  解决重复提交的问题。但面临着数据不能携带的问题，就用RedirectAttributes
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //1、校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //TODO 验证验证码 10分钟校验
                //验证码通过,删除缓存中的验证码；令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //真正注册调用远程服务注册
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData(new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        return "redirect:http://auth.gulimall.com/login.html";
    }
}
