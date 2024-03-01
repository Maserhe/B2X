package com.jpg6.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}
