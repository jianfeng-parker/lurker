package cn.ubuilding.lurker.v2.config.api;

import cn.ubuilding.lurker.v2.rpc.api.Invoker;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 07:21
 */

public class ReferenceConfig {

    /**
     * 引用的接口类型
     */
    private Class<?> interfaceClass;

    /**
     * 接口名称(全路径名)
     */
    private String interfaceName;

    /**
     * 点对点的服务地址
     */
    private String url;

    private transient volatile Invoker<?> invoker;

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
