package com.jpg6.gulimall.product.feign;

import com.jpg6.common.to.SkuReductionTo;
import com.jpg6.common.to.SpuBoundTo;
import com.jpg6.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {


    /**
     * 指定远程接口
     * @param spuBoundTo
     */

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
