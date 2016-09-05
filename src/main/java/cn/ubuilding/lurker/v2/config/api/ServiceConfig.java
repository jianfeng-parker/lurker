package cn.ubuilding.lurker.v2.config.api;


import cn.ubuilding.lurker.v2.common.Constants;
import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.common.extention.ExtensionLoader;
import cn.ubuilding.lurker.v2.common.utils.NetUtils;
import cn.ubuilding.lurker.v2.rpc.Exporter;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;
import cn.ubuilding.lurker.v2.rpc.Protocol;
import cn.ubuilding.lurker.v2.rpc.ProxyFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/7/14 22:16
 * 配置对外提供服务的信息
 */

public class ServiceConfig<T> extends AbstractConfig {

    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getDefaultInstance();

    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultInstance();

    private final List<Exporter<?>> exporters = new ArrayList<Exporter<?>>();

    /**
     * 接口类型
     */
    private Class<?> interfaceClass;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口实现类引用
     */
    private T implement;

    /**
     * 服务是否注册
     * 之所以有这个属性，是因为有的服务只暴露但不在注册中心进行注册
     */
    private boolean register;

    /**
     * 服务延时发布的时间:毫秒
     */
    private long delay;

    private String path;

    /**
     * 协议 端口
     */
    private int port;


    public ServiceConfig() {
    }

    public void export() {
        if (delay > 0) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(delay);
                    } catch (Throwable ignored) {
                    }
                    doExport();
                }
            });
            thread.setDaemon(true);
            thread.setName("DelayedExportServiceThread");
            thread.start();
        } else {
            doExport();
        }
    }

    public synchronized void unExport() {
        if (exporters != null && exporters.size() > 0) {
            for (Exporter<?> exporter : exporters) {
                try {
                    exporter.unExport();
                } catch (Throwable ignored) {

                }
            }
            exporters.clear();
        }
    }

    protected synchronized void doExport() {
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<lurker:service interface=\"\" /> interface not allow null!");
        }
        try {
            interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if (implement == null) {
            throw new IllegalStateException("implement is not allow null");
        }
        if (!interfaceClass.isInstance(implement)) {
            throw new IllegalStateException("the class " + implement.getClass().getName() + " unimplemented interface:" + interfaceClass);
        }
        if (applicationConfig == null) {
            throw new IllegalStateException("No such application config! Please add <lurker:application name=\"...\" /> to your spring config.");
        }
        if (registryConfig == null) {
            throw new IllegalStateException("No such registry config! Please add <lurker:registry address=\"...\" /> to your spring config");
        }
        // 获取暴露服务的主机地址
        String serviceHost;
        try {
            serviceHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
            serviceHost = NetUtils.getLocalHost();
        }

        if (port <= 0) {
            port = NetUtils.getAvailablePort(protocol.getDefaultPort());
        }

        // 注册中心URL
        URL registryURL = loadRegistry();
        // 服务暴露信息封装
        URL exportURL = new URL(Protocol.NAME, serviceHost, port, path, applicationConfig.toMap());

        Invoker<?> invoker = proxyFactory.getInvoker(implement, (Class) interfaceClass, registryURL.addAndEncodeParameter(Constants.EXPORT_KEY, exportURL.toString()));
        Exporter<?> exporter = protocol.export(invoker);
        exporters.add(exporter);

    }

    public Class<?> getInterfaceClass() {
        if (this.interfaceClass != null) {
            return interfaceClass;
        }
        try {
            if (this.interfaceName != null && this.interfaceName.length() > 0) {
                this.interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return this.interfaceClass;
    }

    public String getInterface() {
        return interfaceName;
    }

    public void setInterface(Class<?> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("the interface class:" + interfaceClass + " is not a interface");
        }
        this.interfaceClass = interfaceClass;
        setInterface(interfaceClass == null ? null : interfaceClass.getName());
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
        if (id == null || id.length() == 0) {
            id = interfaceName;
        }
    }

    public T getImplement() {
        return implement;
    }

    public void setImplement(T implement) {
        this.implement = implement;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
