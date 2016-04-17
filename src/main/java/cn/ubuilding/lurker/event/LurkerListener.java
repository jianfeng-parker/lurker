package cn.ubuilding.lurker.event;

/**
 * @author Wu Jianfeng
 * @since 16/4/13 22:18
 */

public interface LurkerListener<T> {

    /**
     * 注册中心数据变化后触发的事件
     *
     * @param event 变更事件
     */
    void onChange(T event);
}
