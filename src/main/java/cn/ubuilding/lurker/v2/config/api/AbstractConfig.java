package cn.ubuilding.lurker.v2.config.api;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.registry.Registry;

import java.io.Serializable;

/**
 * @author Wu Jianfeng
 * @since 16/7/15 07:19
 */

public abstract class AbstractConfig implements Serializable {

    protected String id;

    /**
     * 应用配置信息
     */
    protected ApplicationConfig applicationConfig;

    /**
     * 注册中心配置信息
     */
    protected RegistryConfig registryConfig;

    protected URL loadRegistry() {
        return new URL(registryConfig.getProtocol(), registryConfig.getAddress(), registryConfig.getPort(), Registry.class.getName(), applicationConfig.toMap());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
}
