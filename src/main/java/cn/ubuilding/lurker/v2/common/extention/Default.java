package cn.ubuilding.lurker.v2.common.extention;

import java.lang.annotation.*;

/**
 * @author Wu Jianfeng
 * @since 16/8/9 21:20
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Default {

//    boolean value() default true;
}
