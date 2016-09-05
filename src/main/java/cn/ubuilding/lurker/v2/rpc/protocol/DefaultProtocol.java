package cn.ubuilding.lurker.v2.rpc.protocol;

import cn.ubuilding.lurker.v2.common.Constants;
import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.common.extention.Default;
import cn.ubuilding.lurker.v2.common.utils.ProtocolUtils;
import cn.ubuilding.lurker.v2.registry.Registry;
import cn.ubuilding.lurker.v2.registry.RegistryFactory;
import cn.ubuilding.lurker.v2.rpc.Exporter;
import cn.ubuilding.lurker.v2.rpc.Protocol;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;
import cn.ubuilding.lurker.v2.rpc.exporter.LurkerExporter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wu Jianfeng
 * @since 16/8/12 22:32
 */

@Default
public class DefaultProtocol implements Protocol {

    /**
     * 服务暴露默认端口
     */
    private static final int DEFAULT_PORT = 88888;

    private static final Map<String, Exporter<?>> exportMap = new ConcurrentHashMap<String, Exporter<?>>();

    private RegistryFactory registryFactory;

    public void setRegistryFactory(RegistryFactory registryFactory) {
        this.registryFactory = registryFactory;
    }

    public <T> Exporter<T> export(Invoker<T> invoker) {
        // 将服务进行本地发布
        final Exporter<T> exporter = doExport(invoker);

        // 将发布后的服务进行注册，供客户端订阅使用
        final Registry registry = getRegistry(invoker);
        final URL serviceURL = getServiceURL(invoker);
        registry.register(serviceURL);

        return new Exporter<T>() {
            public Invoker<T> getInvoker() {
                return exporter.getInvoker();
            }

            public void unExport() {
                exporter.unExport();
            }
        };
    }

    public <T> Invoker<T> refer(Class<T> clazz, URL url) {
        return null;
    }

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    public void destroy() {

    }

    private <T> Exporter<T> doExport(Invoker<T> invoker) {
        URL serviceURL = getServiceURL(invoker);
        String key = ProtocolUtils.serviceKey(serviceURL);
        Exporter<T> exporter = new LurkerExporter<T>(key, invoker, exportMap);
        exportMap.put(key, exporter);
        startServer(serviceURL);
        return exporter;
    }

    private void startServer(URL url) {

    }

    private Registry getRegistry(Invoker<?> invoker) {
        return registryFactory.getRegistry(invoker.getUrl());
    }

    private URL getServiceURL(Invoker<?> invoker) {
        String exportURL = invoker.getUrl().getAndDecodeParameter(Constants.EXPORT_KEY);
        if (null == exportURL || exportURL.length() == 0) {
            throw new IllegalArgumentException("export url is necessary in registry:" + invoker.getUrl());
        }
        return URL.toURL(exportURL);
    }

}
