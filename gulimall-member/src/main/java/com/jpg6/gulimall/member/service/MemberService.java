package com.jpg6.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.member.entity.MemberEntity;
import com.jpg6.gulimall.member.vo.MemberUserLoginVo;
import com.jpg6.gulimall.member.vo.RegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-25 13:05:11
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(RegisterVo vo);

    void checkPhoneUnique(String phone);

    void checkUserNameUnique(String userName);

    MemberEntity login(MemberUserLoginVo vo);
}

