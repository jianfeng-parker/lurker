package cn.ubuilding.lurker.registry.publish;

import cn.ubuilding.lurker.provider.Provider;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:51
 * 服务 <strong>发布</strong>接口
 */

public interface Publisher {

    void publish(String host, int port, List<Provider> providers);
}
