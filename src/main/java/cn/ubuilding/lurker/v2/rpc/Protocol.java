package cn.ubuilding.lurker.v2.rpc;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 16:59
 */

public interface Protocol {

    public static final String NAME = "lurker";

    /**
     * 暴露远程服务
     *
     * @param invoker 被暴露服务的封装
     * @return 对暴露服务的结果进行的封装，用于对暴露的服务进行取消等操作
     */
    <T> Exporter<T> export(Invoker<T> invoker);

    /**
     * 引用远程服务
     * 调用此API获的一个远程服务的封装Invoker，执行invoke(...)方法即发起对远程服务的调用，即发起一个网络连接
     *
     * @param <T>   服务类型
     * @param clazz 服务类型
     * @param url   远程服务地址
     */
    <T> Invoker<T> refer(Class<T> clazz, URL url);

    int getDefaultPort();

    void destroy();
}
