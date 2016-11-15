package cn.ubuilding.lurker.rpc.client.proxy;

import cn.ubuilding.lurker.rpc.client.Connection;
import cn.ubuilding.lurker.rpc.support.NetUtils;
import cn.ubuilding.lurker.rpc.support.loadbalance.LoadBalance;
import cn.ubuilding.lurker.rpc.support.loadbalance.LoadBalanceFactory;
import cn.ubuilding.lurker.rpc.support.protocol.Request;
import cn.ubuilding.lurker.rpc.support.protocol.ResponseFuture;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author Wu Jianfeng
 * @since 2016/10/26 08:00
 */

public class RemoteProxyInvocation implements InvocationHandler, AsyncRemoteProxy {

    private String interfaceClassName;

    private List<String> addressList;

    private String loadBalance;

    private Connection connection;

    public RemoteProxyInvocation(String interfaceClassName, List<String> addressList, String loadBalance) {
        this.interfaceClassName = interfaceClassName;
        this.addressList = addressList;
        this.loadBalance = loadBalance;
        initConnection();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = buildRequest(method.getName(), args);
        request.setParameterTypes(method.getParameterTypes());
        ResponseFuture future = connection.send(request);
        return future.get();
    }


    public ResponseFuture call(String methodName, Object... args) {
        Request request = buildRequest(methodName, args);
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = getClassType(args[i]);
        }
        request.setParameterTypes(types);
        return connection.send(request);
    }

    private Request buildRequest(String methodName, Object... args) {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceClassName(interfaceClassName);
        request.setMethodName(methodName);
        request.setParameters(args);
        return request;
    }

    public void updateConnection(List<String> addresses) {
        String currentAddress = connection.getHost() + NetUtils.ADDRESS_SEPARATOR + connection.getPort();
        this.addressList = addresses;
        if (!addresses.contains(currentAddress)) {
            connection.close();
            initConnection();
        }
    }

    /**
     * 设置远程连接对象
     * 从本地缓存的
     */
    private void initConnection() {
        if (null == this.addressList || this.addressList.size() == 0) {
            throw new IllegalStateException("not found any remote address for service:" + this.interfaceClassName);
        }
        LoadBalance lb = (this.loadBalance == null || this.loadBalance.length() == 0) ?
                LoadBalanceFactory.getDefault() : LoadBalanceFactory.get(this.loadBalance);
        String address = (null == lb) ? this.addressList.get(0) : lb.select(this.addressList);
        String[] adds = address.split(NetUtils.ADDRESS_SEPARATOR);
        if (adds.length != 2) {
            throw new IllegalArgumentException("invalid remote address: " + address);
        }
        this.connection = new Connection(adds[0], Integer.parseInt(adds[1]));
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        if (typeName.equals("java.lang.Integer")) {
            return Integer.TYPE;
        } else if (typeName.equals("java.lang.Long")) {
            return Long.TYPE;
        } else if (typeName.equals("java.lang.Float")) {
            return Float.TYPE;
        } else if (typeName.equals("java.lang.Double")) {
            return Double.TYPE;
        } else if (typeName.equals("java.lang.Character")) {
            return Character.TYPE;
        } else if (typeName.equals("java.lang.Boolean")) {
            return Boolean.TYPE;
        } else if (typeName.equals("java.lang.Short")) {
            return Short.TYPE;
        } else if (typeName.equals("java.lang.Byte")) {
            return Byte.TYPE;
        }
        return classType;
    }
}
