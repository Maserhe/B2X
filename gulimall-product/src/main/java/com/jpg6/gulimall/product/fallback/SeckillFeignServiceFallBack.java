package com.jpg6.gulimall.product.fallback;


import com.jpg6.common.exception.BizCodeEnum;
import com.jpg6.common.utils.R;
import com.jpg6.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 熔断 服务
 */
@Component
@Slf4j
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckilInfo(Long skuId) {
        log.info("远程服务 调用 。。。。。。，熔断 降级");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
