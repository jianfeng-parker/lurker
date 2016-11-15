package cn.ubuilding.lurker.rpc.support.loadbalance;

import cn.ubuilding.lurker.rpc.support.annotation.LB;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 20:24
 */

@LB(LoadBalanceFactory.ROUND_ROBIN)
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> addresses) {
        return null;
    }
}
