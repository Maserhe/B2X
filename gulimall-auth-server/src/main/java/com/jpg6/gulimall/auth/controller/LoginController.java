package com.jpg6.gulimall.auth.controller;


import com.alibaba.fastjson.TypeReference;
import com.jpg6.common.constant.AuthServerConstant;
import com.jpg6.common.exception.BizCodeEnum;
import com.jpg6.common.utils.R;
import com.jpg6.common.vo.MemberResponseVo;
import com.jpg6.gulimall.auth.feign.MemberFeignService;
import com.jpg6.gulimall.auth.vo.UserLoginVo;
import com.jpg6.gulimall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jpg6.common.constant.AuthServerConstant.LOGIN_USER;

@Controller
public class LoginController {


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private MemberFeignService memberFeignService;


    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 1, 接口 防 刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);

            if (System.currentTimeMillis() - l < 60 * 1000) {
                return R.error(BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getMsg());
            }
        }
        //2、验证码的再次效验 redis.存key-phone,value-code
        String redisStorage = "1234" + "_" + System.currentTimeMillis();
        //存入redis，防止同一个手机号在60秒内再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,
                redisStorage,10, TimeUnit.MINUTES);

        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // 校验出错, 返回到注册页
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(fieldErr -> {
                return fieldErr.getField();
            }, fieldError -> {
                return fieldError.getDefaultMessage();
            }));
            // model.addAttribute("errors", errors);
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        // 判断手机号 是否已经注册过

        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s) && code.equals(s.split("_")[0])) {

            redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
            // 验证码通过
            R r = memberFeignService.register(vo);
            if (r.getCode() == 0) {
                // 成功状态
                return "redirect:http://auth.gulimall.com/login.html";
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", r.getData(new TypeReference<String>(){}));
                redirectAttributes.addAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {

        //远程登录
        R login = memberFeignService.login(vo);

        if (login.getCode() == 0) {
            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
            session.setAttribute(LOGIN_USER,data);

            return "redirect:http://gulimall.com";
        } else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    @GetMapping(value = "/loguot.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_USER);
        request.getSession().invalidate();
        return "redirect:http://gulimall.com";
    }

}
