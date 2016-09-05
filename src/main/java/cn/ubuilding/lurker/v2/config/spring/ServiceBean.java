package cn.ubuilding.lurker.v2.config.spring;

import cn.ubuilding.lurker.v2.config.api.ApplicationConfig;
import cn.ubuilding.lurker.v2.config.api.RegistryConfig;
import cn.ubuilding.lurker.v2.config.api.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/7/18 12:29
 * 为了便于在Spring容器启动时发布Service，所以使用该类并继承ServiceConfig
 * 使用待发布服务的配置信息
 */

public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanNameAware {

    private ApplicationContext applicationContext;

    private String beanName;


    public ServiceBean() {
        super();
    }

    public void afterPropertiesSet() throws Exception {

        // 将ApplicationConfig 对象设置给ServiceConfig
        if (getApplicationConfig() == null) {
            Map<String, ApplicationConfig> applicationConfigMap = this.applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("duplicate default application config:" + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    setApplicationConfig(applicationConfig);
                }
            }
        }

        // 将RegistryConfig 对象设置给ServiceConfig
        if (getRegistryConfig() == null) {
            Map<String, RegistryConfig> registryConfigMap = this.applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);
            if (registryConfigMap == null || registryConfigMap.size() == 0) {
                throw new IllegalStateException("not found registry config");
            }
            RegistryConfig registryConfig = null;
            for (RegistryConfig config : registryConfigMap.values()) {
                if (config.isDefault()) {
                    if (registryConfig != null) {
                        throw new IllegalStateException("duplicate default registry config:" + registryConfig + " and " + config);
                    }
                    registryConfig = config;
                }
            }
            if (registryConfig != null) {
                setRegistryConfig(registryConfig);
            }
        }

        if (getPath() == null || getPath().length() == 0) {
            if (beanName != null && beanName.length() > 0 && getInterface() != null
                    && getInterface().length() > 0 && beanName.startsWith(getInterface())) {
                setPath(beanName);
            }
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        export();
    }

    public void destroy() throws Exception {
        unExport();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }
}
