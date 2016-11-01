package cn.ubuilding.lurker.support.loadbalance;


import cn.ubuilding.lurker.support.annotation.LB;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
