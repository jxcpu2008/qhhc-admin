package com.hc9.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户登录切入</br>
 * 针对需要用户登录状态的所有操作</br>
 * 在方法上@CheckLoginOnMethod</br>
 * @author frank
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckLoginOnMethod {

    String value() default "All";
}
