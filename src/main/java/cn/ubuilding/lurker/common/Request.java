package cn.ubuilding.lurker.common;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 10:11
 */

public class Request {

    private String id;

    private String serviceKey;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    public Request(String id, String serviceKey, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.id = id;
        this.serviceKey = serviceKey;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    public Request(String serviceKey, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this(null, serviceKey, methodName, parameterTypes, parameters);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
