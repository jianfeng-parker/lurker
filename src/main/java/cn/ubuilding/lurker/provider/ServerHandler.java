package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.common.Request;
import cn.ubuilding.lurker.common.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/4/1 22:09
 * <p>
 * 处理客户端Channel的 Handler
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Map<String, Map<String, Object>> ThreadLocalMap = new HashMap<String, Map<String, Object>>();

    private final Map<String, ProviderInfo> handlerMap;

    public ServerHandler(Map<String, ProviderInfo> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Request request = (Request) msg;
        // discard the received data silently
//        ((ByteBuf) msg).release();
//        context.writeAndFlush(msg);
        Response response = handle(request);
        ctx.writeAndFlush(response);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        // close the connection when an exception is raised
        // TODO logging...
        context.close();
    }

    // TODO 缓存
    private Response handle(Request request) {
        try {

            ProviderInfo providerInfo = handlerMap.get(request.getServiceKey());
            if (null == providerInfo) {
                return new Response(request.getId(), null, new ClassNotFoundException("not found any services by key:" + request.getServiceKey()));
            }
            Object serviceBean = providerInfo.getImplementation();
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod fastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            return new Response(request.getId(), fastMethod.invoke(serviceBean, parameters), null);
        } catch (Throwable t) {
            return new Response(request.getId(), null, t);
        }
    }
}
