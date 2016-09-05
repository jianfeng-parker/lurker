package cn.ubuilding.lurker.v2.config.spring.schema;


import cn.ubuilding.lurker.v2.config.api.ApplicationConfig;

/**
 * @author Wu Jianfeng
 * @since 16/7/18 11:55
 */

public class ApplicationConfigBeanDefinitionParser extends AbstractLurkerBeanDefinitionParser {

    public ApplicationConfigBeanDefinitionParser() {
        super();
    }

    @Override
    public Class<?> getBeanClass() {
        return ApplicationConfig.class;
    }


}
