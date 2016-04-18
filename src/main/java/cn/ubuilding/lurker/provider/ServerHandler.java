package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.protocol.Request;
import cn.ubuilding.lurker.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/4/1 22:09
 * <p>
 * 处理客户端Channel的 Handler
 */

public final class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static Map<String, Map<String, Object>> ThreadLocalMap = new HashMap<String, Map<String, Object>>();

    private final Map<String, Provider> handlerMap;

    public ServerHandler(Map<String, Provider> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Request) {
            Request request = (Request) msg;
            Response response = handle(request);
            ctx.writeAndFlush(response);
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        // close the connection when an exception is raised
        logger.error("server handle error:\n" + cause);
        context.close();
    }

    private Response handle(Request request) {
        try {

            Provider provider = handlerMap.get(request.getServiceKey());
            if (null == provider) {
                return new Response(request.getId(), null, new ClassNotFoundException("not found any services by serviceKey:" + request.getServiceKey()));
            }
            Object serviceBean = provider.getImplementation();
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
