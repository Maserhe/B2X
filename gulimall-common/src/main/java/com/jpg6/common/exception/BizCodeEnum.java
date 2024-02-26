package com.jpg6.common.exception;

public enum BizCodeEnum {

    UNKNOWN_EXCEPTION(10000, "系统未知错误"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),

    VALID_SMS_CODE_EXCEPTION(10002, "短信验证码频率异常"),

    // 商品product
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),

    USER_EXIST_EXCEPTION(15001, "用户存在异常"),

    LOGINACCT_PASSWORD_EXCEPTION(15003, "密码校验异常"),
    PHONE_EXIST_EXCEPTION(15002, "手机号存在异常");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }


}
