package cn.ubuilding.lurker.http.annotation;


import java.lang.annotation.*;

/**
 * @author Wu Jianfeng
 * @since 2016/11/30 22:50
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String name();

    boolean required() default true;

}
