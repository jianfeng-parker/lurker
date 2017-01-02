package cn.ubuilding.lurker.http.core.router;

import cn.ubuilding.lurker.http.annotation.Path;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.core.processor.GetRequestProcessor;
import cn.ubuilding.lurker.http.core.processor.PostRequestProcessor;
import cn.ubuilding.lurker.http.core.processor.RequestProcessor;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Wu Jianfeng
 * @since 2016/11/28 22:01
 */

public final class Router {

    private static ConcurrentMap<String, ConcurrentMap<String, RequestProcessor>> mapper = new ConcurrentHashMap<String, ConcurrentMap<String, RequestProcessor>>();

    public Router(Object... controllers) {
        if (controllers != null && controllers.length > 0) {
            for (Object controller : controllers) {
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
                    if (method.getModifiers() == Modifier.PUBLIC && Render.class.equals(method.getReturnType())) {
                        String absolutePath = basePath;
                        Path annotation = method.getAnnotation(Path.class);
                        String relativePath = null == annotation ? "" : annotation.value();
                        if (null != relativePath && relativePath.trim().length() > 0) {
                            if (!relativePath.startsWith("/") && !basePath.endsWith("/")) {
                                relativePath = "/" + relativePath;
                            }
                            absolutePath += relativePath;
                        }
                        if (absolutePath.length() == 0) {
                            continue;
                        }
                        HttpMethod httpMethod = null == annotation ? HttpMethod.GET : annotation.method().getMethod();
                        if (HttpMethod.GET.equals(httpMethod)) {
                            putProcessor(httpMethod, absolutePath, new GetRequestProcessor(controller, method));
                        } else {
                            putProcessor(httpMethod, absolutePath, new PostRequestProcessor(controller, method));
                        }
                    }
                }
            }
        }
    }

    public void release() {
        mapper.clear();
    }

    public RequestProcessor getProcessor(HttpMethod method, String absolutePath) {
        ConcurrentMap<String, RequestProcessor> processorMapper = mapper.get(method.toString());
        if (null == processorMapper) {
            return null;
        } else {
            return processorMapper.get(absolutePath);
        }
    }

    private void putProcessor(HttpMethod method, String absolutePath, RequestProcessor processor) {
        String methodName = method.toString();
        ConcurrentMap<String, RequestProcessor> processorMapper = mapper.get(methodName);
        if (null == processorMapper) {
            processorMapper = new ConcurrentHashMap<String, RequestProcessor>();
            mapper.put(methodName, processorMapper);
        }
        RequestProcessor p = processorMapper.get(absolutePath);
        if (null != p) {
            throw new IllegalStateException("duplicate " + methodName + ":" + absolutePath);
        } else {
            processorMapper.put(absolutePath, processor);
        }
    }

}
