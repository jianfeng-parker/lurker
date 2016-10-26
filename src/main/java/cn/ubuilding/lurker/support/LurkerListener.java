package cn.ubuilding.lurker.support;

/**
 * @author Wu Jianfeng
 * @since 16/4/13 22:18
 */

public interface LurkerListener<T> {


    void onChange(T event);
}
