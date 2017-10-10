package com.hc9.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举汉化注解
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.FIELD})
public @interface FieldConfig {
    
    /**
     * 中文�?
     * @return
     */
    String value() default "";
    
    /**
     * 数字�?
     * @return
     */
    int ordinal() default 0;
}

