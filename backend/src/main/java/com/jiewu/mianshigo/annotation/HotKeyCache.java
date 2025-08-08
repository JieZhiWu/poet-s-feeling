package com.jiewu.mianshigo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HotKeyCache {
    // 缓存key前缀（如"bank_detail_"）
    String keyPrefix();
    // 参数索引（用于从方法参数中获取生成key的ID，如0表示第一个参数）
    int idParamIndex();
    // 缓存过期时间（毫秒，默认10分钟）
//    long expireTime() default 10 * 60 * 1000;
}
