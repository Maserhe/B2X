package com.jpg6.gulimall.member.feign;


import com.jpg6.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {


    @RequestMapping("coupon/coupon/coupon")
    public R getCoupon();

    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();


}
