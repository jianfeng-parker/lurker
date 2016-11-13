package cn.ubuilding.lurker.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 20:18
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Component
public @interface LB {
    String value();
}
