package cn.ubuilding.lurker.v2.rpc.api;

/**
 * @author Wu Jianfeng
 * @since 16/8/10 18:09
 */

public interface Invocation {

    String getMethod();

    Class<?>[] getParameterTypes();

    Object[] getArguments();


}
