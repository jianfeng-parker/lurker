package cn.ubuilding.lurker.v2.common.extention;

/**
 * @author Wu Jianfeng
 * @since 16/8/21 17:45
 * 通过Factory方式获取扩展实例
 */

public interface ExtensionFactory {

    <T> T getExtenSion(Class<T> type, String name);
}
