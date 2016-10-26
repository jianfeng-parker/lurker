package cn.ubuilding.lurker.client;

import cn.ubuilding.lurker.support.LurkerListener;
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

class RemoteServiceProxy implements InvocationHandler, LurkerListener<List<String>> {

    private String serviceName;

    public RemoteServiceProxy(String serviceName) {
        this.serviceName = serviceName;
    }

    public Response invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString());
        request.setServiceName(serviceName);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        Connection connection = Client.this.getConnection();
        if (null == connection) throw new RuntimeException("not connect to remote service for " + serviceName);

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

    /**
     * 远程服务地址变更后，重新设置该Consumer对象中的connection
     *
     * @param addresses 变更后的远程服务地址
     */
    public void onChange(List<String> addresses) {
        // 若注册中心无可用地址，则判断本地缓存地址是否有效
        if (null == addresses || addresses.size() == 0) {

        } else {
            this.addresses = addresses;
            setConnection();
        }
    }
}
