package cn.ubuilding.lurker.v2.config.spring.schema;

import cn.ubuilding.lurker.v2.config.api.ServiceConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * @author Wu Jianfeng
 * @since 16/7/22 11:35
 */

public class ServiceConfigBeanDefinitionParser extends AbstractLurkerBeanDefinitionParser {

    public ServiceConfigBeanDefinitionParser() {
        super();
    }

    /**
     * 处理 service config配置元素中的特有属性
     */
    protected void handleCustomizedAttribute(String property, String value, ParserContext parserContext) {
        if ("implement".equals(property) && parserContext.getRegistry().containsBeanDefinition(value)) {
            BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
            if (!refBean.isSingleton()) {
                throw new IllegalStateException("The exported service implementation " + value + " must be singleton! Please set the " + value + " bean scope to singleton, eg: <bean id=\"" + value + "\" scope=\"singleton\" ...>");
            }
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return ServiceConfig.class;
    }
}
