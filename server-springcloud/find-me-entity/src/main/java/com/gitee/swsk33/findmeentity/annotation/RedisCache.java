package com.gitee.swsk33.findmeentity.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用于表示Redis缓存操作层注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RedisCache {

	@AliasFor(annotation = Component.class) String value() default "";

}