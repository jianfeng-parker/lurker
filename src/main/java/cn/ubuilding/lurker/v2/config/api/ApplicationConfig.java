package cn.ubuilding.lurker.v2.config.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/7/15 07:19
 * 被暴露服务所属应用的配置信息
 */

public class ApplicationConfig extends AbstractConfig {

    /**
     * 应用名
     */
    private String name;

    /**
     * 应用版本
     */
    private String version;

    /**
     * 应用负责人
     */
    private String owner;

    /**
     * 组织、部门
     */
    private String organization;

    private boolean isDefault;

    public Map<String, String> toMap() {
        // TODO bean转换成Map
        return new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
