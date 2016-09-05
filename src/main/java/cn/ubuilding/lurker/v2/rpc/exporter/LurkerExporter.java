package cn.ubuilding.lurker.v2.rpc.exporter;

import cn.ubuilding.lurker.v2.rpc.Exporter;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;

import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/8/20 17:33
 */

public class LurkerExporter<T> implements Exporter<T> {

    private final String key;

    private final Invoker<T> invoker;

    private final Map<String, Exporter<?>> exporterMap;

    private volatile boolean exported = true;


    public LurkerExporter(String key, Invoker<T> invoker, Map<String, Exporter<?>> exporterMap) {
        if (null == invoker) {
            throw new IllegalArgumentException("service invoker must not be null");
        }
        if (null == invoker.getInterface()) {
            throw new IllegalArgumentException("service type must not be null");
        }
        if (null == invoker.getUrl()) {
            throw new IllegalArgumentException("service url must not be null");
        }
        this.key = key;
        this.exporterMap = exporterMap;
        this.invoker = invoker;
    }

    public Invoker<T> getInvoker() {
        return invoker;
    }

    public void unExport() {
        getInvoker().destroy();
        exporterMap.remove(key);
        exported = false;
    }

    public boolean isExported() {
        return exported;
    }
}
