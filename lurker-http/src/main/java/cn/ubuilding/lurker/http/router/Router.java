package cn.ubuilding.lurker.http.router;

import cn.ubuilding.lurker.http.annotation.Path;
import cn.ubuilding.lurker.http.core.Controller;
import cn.ubuilding.lurker.http.core.ControllerHandler;
import cn.ubuilding.lurker.http.core.HttpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Wu Jianfeng
 * @since 2016/11/28 22:01
 */

public final class Router {

    private static ConcurrentMap<String, ControllerHandler> getMapping = new ConcurrentHashMap<String, ControllerHandler>();

    private static ConcurrentMap<String, ControllerHandler> postMapping = new ConcurrentHashMap<String, ControllerHandler>();

    public Router(Controller... controllers) {
        if (controllers != null && controllers.length > 0) {
            for (Controller controller : controllers) {
                Path controllerPath = controller.getClass().getAnnotation(Path.class);
                String basePath = "";
                if (null != controllerPath) {
                    basePath = controllerPath.value();
                    if (!basePath.startsWith("/")) {
                        basePath = "/" + basePath;
                    }
                }
                Method[] methods = controller.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getModifiers() == Modifier.PUBLIC && null != method.getAnnotation(Path.class)) {
                        Path methodPath = method.getAnnotation(Path.class);
                        String relativePath = methodPath.value();
                        // TODO 方法上没有配置 Path标签属正常Case
                        // TODO 校验get不能有RequestBody,Post方法参数只能有一个@RequestBody标签
                        // TODO 校验方法返回类型是否Render;
                        if (null != relativePath && relativePath.trim().length() > 0) {
                            if (!relativePath.startsWith("/")) {
                                relativePath = "/" + relativePath;
                            }
                            String absolutePath = basePath + relativePath;
                            HttpMethod httpMethod = methodPath.method();
                            if (HttpMethod.GET.equals(httpMethod)) {
                                if (null != getMapping.get(absolutePath)) {
                                    throw new IllegalStateException("duplicate methodPath with method 'GET'':" + absolutePath);
                                }
                                getMapping.put(absolutePath, new ControllerHandler(controller, method));

                            } else if (HttpMethod.POST.equals(httpMethod)) {
                                if (null != postMapping.get(absolutePath)) {
                                    throw new IllegalStateException("duplicate methodPath with method 'POST'':" + absolutePath);
                                }
                                postMapping.put(absolutePath, new ControllerHandler(controller, method));
                            } else {
                                throw new IllegalStateException("unsupported http method " + httpMethod.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    public ControllerHandler getGetHandler(String path) {
        return getMapping.get(path);
    }

    public ControllerHandler getPostHandler(String path) {
        return postMapping.get(path);
    }

    public void release() {
        getMapping.clear();
        postMapping.clear();
    }

}
