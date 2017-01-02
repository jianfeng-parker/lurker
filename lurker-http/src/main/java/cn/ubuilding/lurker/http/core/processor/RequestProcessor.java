package cn.ubuilding.lurker.http.core.processor;

import cn.ubuilding.lurker.http.core.Render;

import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 10:11
 */

public interface RequestProcessor {

    Render process(Map<String, String> parameters);

    Render process(Map<String, String> parameters, String requestBody, String contentType);

}
