package cn.ubuilding.rpc.client.proxy;

import cn.ubuilding.rpc.support.protocol.ResponseFuture;

/**
 * @author Wu Jianfeng
 * @since 2016/11/9 21:28
 */

public interface AsyncRemoteProxy {

    ResponseFuture call(String methodName, Object... args);
}
