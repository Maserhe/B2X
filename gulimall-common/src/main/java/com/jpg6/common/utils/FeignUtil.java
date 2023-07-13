package com.jpg6.common.utils;


import com.mysql.cj.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FeignUtil {

    public static <T> T formatClass(R r, Class<T> tClass) {
        String jsonString = getResultJson(r);
        if (StringUtils.isNullOrEmpty(jsonString)) {
            return null;
        }
        return JsonUtil.parseObject(jsonString, tClass);
    }

    private static String getResultJson(R r) {
        if (!r.success()) {
            throw new RRException(r.get("msg").toString());
        }
        return JsonUtil.toJsonString(r.get("data"));
    }

    public static <T> List<T> formatListClass(R r, Class<T> tClass) {
        String jsonString = getResultJson(r);
        if (StringUtils.isNullOrEmpty(jsonString)) {
            return new ArrayList<>();
        }
        return JsonUtil.parseList(jsonString, tClass);
    }
}

