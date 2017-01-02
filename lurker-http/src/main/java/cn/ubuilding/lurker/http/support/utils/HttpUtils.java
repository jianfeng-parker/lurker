package cn.ubuilding.lurker.http.support.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 11:38
 */

public final class HttpUtils {

    public static Map<String, String> getParameters(Map<String, List<String>> map) {
        if (map == null) return new HashMap<String, String>(0);
        Map<String, String> parameters = new HashMap<String, String>(map.size());
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().get(0));
        }
        return parameters;
    }

    private HttpUtils() {
    }
}
