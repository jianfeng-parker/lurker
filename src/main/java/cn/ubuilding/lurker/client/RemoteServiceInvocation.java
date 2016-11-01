package cn.ubuilding.lurker.client;

import cn.ubuilding.lurker.support.NetUtils;
import cn.ubuilding.lurker.support.loadbalance.LoadBalance;
import cn.ubuilding.lurker.support.loadbalance.LoadBalanceFactory;
import cn.ubuilding.lurker.support.rpc.protocol.Request;
import cn.ubuilding.lurker.support.rpc.protocol.Response;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author Wu Jianfeng
 * @since 2016/10/26 08:00
 */

class RemoteServiceInvocation implements InvocationHandler {

    private String serviceName;

    private List<String> addressList;

    private String loadBalance;

    // TODO 定时检查Connection的可用性
    private Connection connection;

    public RemoteServiceInvocation(String serviceName, List<String> addressList, String loadBalance) {
        this.serviceName = serviceName;
        this.addressList = addressList;
        this.loadBalance = loadBalance;
        initConnection();
    }

    public Response invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString());
        request.setServiceName(serviceName);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        Response response = connection.send(request);
        if (null != response) {
            if (null != response.getError()) {
                throw response.getError();
            }
            return response;
        } else {
            return null;
        }
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
            throw new IllegalStateException("not found any remote address for service:" + this.serviceName);
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
}
