package cn.ubuilding.lurker.v2.rpc.api;

import java.io.Serializable;

/**
 * @author Wu Jianfeng
 * @since 16/8/11 21:18
 */

public class RpcResult implements Result, Serializable {

    private Object value;

    private Throwable exception;

    public RpcResult() {
    }

    public RpcResult(Object value) {
        this.value = value;
    }

    public RpcResult(Throwable exception) {
        this.exception = exception;
    }

    public Object getValue() {
        return value;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }
}
