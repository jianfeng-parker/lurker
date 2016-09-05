package cn.ubuilding.lurker.v2.config.spring.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Wu Jianfeng
 * @since 16/7/21 07:33
 */

public abstract class AbstractLurkerBeanDefinitionParser implements BeanDefinitionParser {

    @SuppressWarnings("unchecked")
    public final BeanDefinition parse(Element element, ParserContext parserContext) {
        Class<?> beanClass = getBeanClass();
        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setBeanClass(beanClass);
        definition.setLazyInit(false);
        String id = element.getAttribute("id");
        if (StringUtils.isEmpty(id)) {
            String name = element.getAttribute("name");
            id = StringUtils.isEmpty(name) ? beanClass.getName() : name;
        }
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicate spring bean id " + id);
        }
        parserContext.getRegistry().registerBeanDefinition(id, definition);
        definition.getPropertyValues().addPropertyValue("id", id);
        Set<String> props = new HashSet<String>();
        ManagedMap parameters = null;
        for (Method method : beanClass.getMethods()) {
            String methodName = method.getName();
            if (methodName.length() > 3 && methodName.startsWith("set")
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 1) {
                // 通过setter方法获取参数类型
                Class<?> propertyType = method.getParameterTypes()[0];
                // 通过setter方法获取对应属性名
                String property = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                props.add(property);

                Method getter = null;
                try {
                    getter = beanClass.getMethod("get" + methodName.substring(3));
                } catch (NoSuchMethodException e1) {
                    try {
                        getter = beanClass.getMethod("is" + methodName.substring(3));
                    } catch (NoSuchMethodException ignored) {

                    }
                }
                if (getter == null || !Modifier.isPublic(getter.getModifiers()) || !propertyType.equals(getter.getReturnType())) {
                    continue;
                }
                String propertyValue = element.getAttribute(property);
                if (null != propertyValue) {
                    propertyValue = propertyValue.trim();
                    if (propertyValue.length() > 0) {
                        Object reference;
                        if (propertyType.isPrimitive()) {
                            reference = propertyValue;
                        } else {
                            handleCustomizedAttribute(property, propertyValue, parserContext);
                            reference = new RuntimeBeanReference(propertyValue);
                        }
                        definition.getPropertyValues().addPropertyValue(property, reference);
                    }
                }
            }
        }
        NamedNodeMap attributes = element.getAttributes();
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = attributes.item(i);
            String name = node.getLocalName();
            if (!props.contains(name)) {
                if (parameters == null) {
                    parameters = new ManagedMap();
                }
                String value = node.getNodeValue();
                parameters.put(name, new TypedStringValue(value, String.class));
            }
        }
        if (parameters != null) {
            definition.getPropertyValues().addPropertyValue("parameters", parameters);
        }
        return definition;
    }

    protected abstract Class<?> getBeanClass();


    /**
     * 不同的definition的对象有需要特殊处理的属性，将这样的逻辑交给各个definition自己去实现
     */
    protected void handleCustomizedAttribute(String property, String value, ParserContext parserContext) {

    }

}
