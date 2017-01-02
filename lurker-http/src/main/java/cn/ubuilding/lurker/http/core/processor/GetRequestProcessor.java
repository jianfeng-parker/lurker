package cn.ubuilding.lurker.http.core.processor;

import java.lang.reflect.Method;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 09:36
 */

public class GetRequestProcessor extends AbstractRequestProcessor {

    public GetRequestProcessor(Object controller, Method method) {
        super(controller, method);
    }

}
