package cn.ubuilding.lurker.http.annotation;


import cn.ubuilding.lurker.http.core.HttpMethod;

import java.lang.annotation.*;

/**
 * @author Wu Jianfeng
 * @since 2016/11/27 00:08
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {

    String value();

    String consume() default "application/json";

    String produce() default "json";

    HttpMethod method() default HttpMethod.GET;

}
