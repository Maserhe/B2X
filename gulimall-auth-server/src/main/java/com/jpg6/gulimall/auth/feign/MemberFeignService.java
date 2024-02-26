package com.jpg6.gulimall.auth.feign;

import com.jpg6.common.utils.R;
import com.jpg6.gulimall.auth.vo.UserLoginVo;
import com.jpg6.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(UserLoginVo vo);
}
