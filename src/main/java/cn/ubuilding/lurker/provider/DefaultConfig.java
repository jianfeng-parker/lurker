package cn.ubuilding.lurker.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 22:33
 * 该类用于解析配置参数或提供默认参数
 */

public final class DefaultConfig {

    private Properties properties;

    private static DefaultConfig config;

    private static final Object lock = new Object();

    public static DefaultConfig getInstance() {
        if (config == null) {
            synchronized (lock) {
                if (config == null) {
                    config = new DefaultConfig();
                }
            }
        }
        return config;
    }


    /**
     * 获取RPC服务端口
     */
    public int getPort() {
        String portConfig = getProperty("rpc.port");
        return null != portConfig ? Integer.parseInt(portConfig) : 8899;
    }

    public String getHost() {
        String hostConfig = getProperty("rpc.host");
        return null != hostConfig ? hostConfig : "";// TODO 使用默认服务发布地址
    }

    /**
     * 服务是否使用SSL
     */
    public boolean isSSL() {
        return null != getProperty("use.ssl") && Boolean.parseBoolean(getProperty("use.ssl"));
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    private DefaultConfig() {
        loadProperties("lurker.properties");
    }

    private void loadProperties(String fileName) {
        if (null == fileName)
            throw new NullPointerException("file must not be null to load");
        if (null == properties) properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(" load config file(" + fileName + ") failure:" + e.getMessage());
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
