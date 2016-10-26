package cn.ubuilding.lurker.support.loadbalance;

import cn.ubuilding.lurker.support.annotation.LB;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 15:02
 */
@LB(LoadBalanceFactory.RANDOM)
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> addresses) {
        return null;
    }
}
