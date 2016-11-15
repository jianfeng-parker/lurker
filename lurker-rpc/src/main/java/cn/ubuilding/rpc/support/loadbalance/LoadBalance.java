package cn.ubuilding.rpc.support.loadbalance;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 15:00
 */

public interface LoadBalance {

    String select(List<String> addresses);
}
