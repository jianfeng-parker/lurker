package cn.ubuilding.lurker.support.loadbalance;


import cn.ubuilding.lurker.support.annotation.LB;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 20:24
 */

@LB(LoadBalanceFactory.CONSISTENT_HASH)
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> addresses) {
        return null;
    }
}
