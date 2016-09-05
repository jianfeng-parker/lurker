package cn.ubuilding.lurker.v2.config.api;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 17:30
 * 注册中心配置信息
 */

public class RegistryConfig extends AbstractConfig {

    /**
     * 注册中心类型:zookeeper,redis,...
     */
    private String protocol;

    /**
     * 注册中心地址
     */
    private String address;

    /**
     * 注册中心端口
     */
    private int port;

    /**
     * 注册中心登录用户名
     */
    private String username;

    /**
     * 注册中心登录密码
     */
    private String password;

    private boolean isDefault;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
