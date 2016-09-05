package cn.ubuilding.lurker.v2.config.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Wu Jianfeng
 * @since 16/7/17 22:25
 */

public class LurkerNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("application", new ApplicationConfigBeanDefinitionParser());
        registerBeanDefinitionParser("registry", new RegistryConfigBeanDefinitionParser());
        registerBeanDefinitionParser("reference", new ApplicationConfigBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ServiceConfigBeanDefinitionParser());


    }
}
