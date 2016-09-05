package cn.ubuilding.lurker.v2.config.spring.schema;

import cn.ubuilding.lurker.v2.config.api.RegistryConfig;

/**
 * @author Wu Jianfeng
 * @since 16/7/22 17:20
 */

public class RegistryConfigBeanDefinitionParser extends AbstractLurkerBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass() {
        return RegistryConfig.class;
    }
}
