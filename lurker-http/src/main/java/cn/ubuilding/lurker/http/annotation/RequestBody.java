package cn.ubuilding.lurker.http.annotation;

import java.lang.annotation.*;

/**
 * @author Wu Jianfeng
 * @since 2016/12/24 15:26
 */

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

    String name();

    boolean required() default true;
}
