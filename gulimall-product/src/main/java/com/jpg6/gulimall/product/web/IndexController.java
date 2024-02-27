package com.jpg6.gulimall.product.web;

import com.jpg6.gulimall.product.entity.CategoryEntity;
import com.jpg6.gulimall.product.service.CategoryService;
import com.jpg6.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 映射 根目录
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        // 1, 查询所有1级 分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        model.addAttribute("categorys", categoryEntities);
        return "oldIndex";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();

        return map;
    }


    @Autowired
    RedissonClient client;

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {

        RLock lock = client.getLock("my-lock");

        lock.lock();

        try {
            System.out.println("加锁成功， 执行业务·····" + Thread.currentThread());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("释放锁" + Thread.currentThread());
            lock.unlock();
        }

        return "hello";
    }


    @ResponseBody
    @GetMapping("/write")
    public String write() {

        RReadWriteLock readWriteLock = client.getReadWriteLock("read-write-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = "";
        try {
            rLock.lock();
             s = UUID.randomUUID().toString();
             Thread.sleep(1000);
            redisTemplate.opsForValue().set("123", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        return s;
    }


    @ResponseBody
    @GetMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = client.getReadWriteLock("read-write-lock");

        RLock rLock = readWriteLock.readLock();
        String s = "";
        try {
            rLock.lock();
            s = redisTemplate.opsForValue().get("123");
        } finally {
            rLock.unlock();
        }
        return s;
    }

}
