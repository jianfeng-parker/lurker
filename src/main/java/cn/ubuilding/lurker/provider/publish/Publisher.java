package cn.ubuilding.lurker.provider.publish;

import cn.ubuilding.lurker.provider.Provider;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:51
 * <p>
 * 将Provider所表示的服务对外发布
 * 这里默认将其发布到zookeeper上
 */

public interface Publisher {

    void publish(String host, int port, List<Provider> providers);
}
