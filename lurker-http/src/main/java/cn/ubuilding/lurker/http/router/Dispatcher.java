package cn.ubuilding.lurker.http.router;

import cn.ubuilding.lurker.http.core.ControllerHandler;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.core.RenderType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2016/11/27 20:58
 */

public class Dispatcher extends ChannelInboundHandlerAdapter {

    private Router router;

    private boolean keepAlive;

    public Dispatcher(Router router, boolean keepAlive) {
        if (null == router) {
            throw new NullPointerException("router");
        }
        this.router = router;
        this.keepAlive = keepAlive;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            Render render;
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpMethod method = request.method();
            if (!(HttpMethod.GET.equals(method) || HttpMethod.POST.equals(method))) { // 判断HTTP Method
                render = new Render(method + " not supported", RenderType.TEXT, HttpResponseStatus.METHOD_NOT_ALLOWED);// 目前仅支持GET/POST
                ctx.write(render.rendering()).addListener(ChannelFutureListener.CLOSE);
            } else {
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                String path = decoder.path();
                Map<String, String> parameters = getParameters(decoder.parameters());
                if (HttpMethod.GET.equals(method)) {
                    ControllerHandler handler = router.getGetHandler(path);
                    if (null == handler) {
                        render = new Render("404 not found", RenderType.TEXT, HttpResponseStatus.NOT_FOUND);
                        ctx.write(render.rendering()).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        render = handler.handle(parameters);
                        response(ctx, render);
                    }
                } else { // POST
                    ControllerHandler handler = router.getPostHandler(path);
                    if (null == handler) {
                        render = new Render("404 not found", RenderType.TEXT, HttpResponseStatus.NOT_FOUND);
                        ctx.write(render.rendering()).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
                        Map<String, List<String>> requestBody = requestBody(contentType, request.content().toString(Charset.defaultCharset()));
                        render = handler.handle(parameters, requestBody);
                        response(ctx, render);
                    }
                }
            }
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private Map<String, String> getParameters(Map<String, List<String>> map) {
        if (map == null) return new HashMap<String, String>(0);
        Map<String, String> parameters = new HashMap<String, String>(map.size());
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().get(0));
        }
        return parameters;
    }

    private void response(ChannelHandlerContext ctx, Render render) {
        FullHttpResponse response = render.rendering();
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> requestBody(String contentType, String content) {
        Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
        if (contentType.equals("application/json")) {
            JSONObject obj = JSON.parseObject(content);
            for (Map.Entry<String, Object> item : obj.entrySet()) {
                List<String> valueList;
                String key = item.getKey();
                Object value = item.getValue();
                Class<?> valueType = value.getClass();

                if (paramMap.containsKey(key)) {
                    valueList = paramMap.get(key);
                } else {
                    valueList = new ArrayList<String>();
                }

                if (valueType.isPrimitive()) {
                    valueList.add(value.toString());
                    paramMap.put(key, valueList);

                } else if (valueType.isArray()) {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        String arrayItem = String.valueOf(Array.get(value, i));
                        valueList.add(arrayItem);
                    }
                    paramMap.put(key, valueList);

                } else if (List.class.isAssignableFrom(valueType)) {
                    if (valueType.equals(JSONArray.class)) {
                        JSONArray jArray = JSONArray.parseArray(value.toString());
                        for (int i = 0; i < jArray.size(); i++) {
                            valueList.add(jArray.getString(i));
                        }
                    } else {
                        valueList = (ArrayList<String>) value;
                    }
                    paramMap.put(key, valueList);

                } else if (Map.class.isAssignableFrom(valueType)) {
                    Map<String, String> tempMap = (Map<String, String>) value;
                    for (String tempKey : tempMap.keySet()) {
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(tempMap.get(tempKey));
                        paramMap.put(tempKey, tempList);
                    }
                }
            }

        } else if (contentType.equals("application/x-www-form-urlencoded")) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(content, false);
            paramMap = queryDecoder.parameters();
        }

        return paramMap;
    }

}
