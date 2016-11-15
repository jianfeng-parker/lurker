package cn.ubuilding.lurker.rpc.support.common;

/**
 * @author Wu Jianfeng
 * @since 2016/11/9 22:16
 */

public interface AsyncCallback {

    void success(Object obj);

    void fail(Throwable t);
}
