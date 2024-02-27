package com.jpg6.gulimall.product.web;


import com.jpg6.gulimall.product.service.SkuInfoService;
import com.jpg6.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo itemVo = skuInfoService.item(skuId);
        model.addAttribute("item", itemVo);
        return "oldItem";
    }
}
