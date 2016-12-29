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

    // TODO 如果可以通过反射获取方法形式参数名，此处就无需开发者显示指定参数名
    String name();

    boolean required() default true;

}
