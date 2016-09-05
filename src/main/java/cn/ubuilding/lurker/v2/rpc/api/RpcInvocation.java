package cn.ubuilding.lurker.v2.rpc.api;

/**
 * @author Wu Jianfeng
 * @since 16/8/11 21:32
 */

public class RpcInvocation implements Invocation {

    public String getMethod() {
        return null;
    }

    public Class<?>[] getParameterTypes() {
        return new Class<?>[0];
    }

    public Object[] getArguments() {
        return new Object[0];
    }
}
