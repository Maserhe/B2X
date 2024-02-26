package com.jpg6.gulimall.member.execption;

/**
 *
 */
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
