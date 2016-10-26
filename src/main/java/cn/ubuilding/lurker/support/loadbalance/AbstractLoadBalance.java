package cn.ubuilding.lurker.support.loadbalance;

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

    // todo
    protected int getWeight() {
        return 100;
    }

    protected abstract String doSelect(List<String> addresses);
}
