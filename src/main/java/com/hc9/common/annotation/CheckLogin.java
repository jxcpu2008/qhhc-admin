package com.hc9.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 拦截器字段
 * 
 * @author ransheng
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckLogin {

    /**
     * 前台登录value
     */
    public static final String WEB = "WEB";

    /**
     * 后台登录value
     */
    public static final String ADMIN = "ADMIN";

    /**
     * 默认前台登录
     */
    String value() default WEB;
}
