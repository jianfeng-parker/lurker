package cn.ubuilding.lurker.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 22:33
 */

public final class LurkerConfig {

    private Properties properties;

    private static LurkerConfig config;

    private static final Object lock = new Object();

    public static LurkerConfig getInstance() {
        if (config == null) {
            synchronized (lock) {
                if (config == null) {
                    config = new LurkerConfig();
                }
            }
        }
        return config;
    }


    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    private LurkerConfig() {
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
