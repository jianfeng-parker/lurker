package cn.ubuilding.lurker.util;

/**
 * @author Wu Jianfeng
 * @since 16/4/12 22:12
 */

public final class Constant {

    public static final int ZK_CONNECTION_TIMEOUT = 10000;

    public static final String ZK_REGISTRY_ROOT_PATH = "/lurker";

    public static String fullPathForZk(String serviceKey) {
        return ZK_REGISTRY_ROOT_PATH + "/" + serviceKey;
    }

}