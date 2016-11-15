package cn.ubuilding.lurker.rpc.support.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Wu Jianfeng
 * @since 16/10/11 22:53
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Component
public @interface Rpc {
    Class<?> value();
}
