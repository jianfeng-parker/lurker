package cn.ubuilding.lurker.v2.common.utils;

import cn.ubuilding.lurker.v2.common.URL;

/**
 * @author Wu Jianfeng
 * @since 16/8/22 21:50
 */

public class ProtocolUtils {

    public static String serviceKey(URL url) {
        return serviceKey(url.getPath(), url.getPort());
    }

    // TODO 后续可丰富serviceKey的组成元素
    public static String serviceKey(String serviceName, int port) {
        return serviceName + ":" + port;
    }
}
