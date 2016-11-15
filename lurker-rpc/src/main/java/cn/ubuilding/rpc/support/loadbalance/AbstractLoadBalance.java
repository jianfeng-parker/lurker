package cn.ubuilding.rpc.support.loadbalance;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 15:04
 */

public abstract class AbstractLoadBalance implements LoadBalance {

    public String select(List<String> addresses) {
        if (null == addresses || addresses.size() == 0) {
            return null;
        } else if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return doSelect(addresses);
        }
    }

    protected int getWeight(String address) {
        return 0; // TODO
    }

    protected abstract String doSelect(List<String> addresses);
}
