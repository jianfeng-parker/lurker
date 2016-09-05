package cn.ubuilding.lurker.v2.rpc;

import cn.ubuilding.lurker.v2.rpc.api.Invoker;

/**
 * @author Wu Jianfeng
 * @since 16/8/5 18:20
 */

public interface Exporter<T> {

    Invoker<T> getInvoker();

    void unExport();

}
