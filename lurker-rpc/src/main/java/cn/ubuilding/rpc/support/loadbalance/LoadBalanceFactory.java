package cn.ubuilding.rpc.support.loadbalance;

import cn.ubuilding.rpc.support.annotation.LB;

import java.util.*;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 20:30
 */

public final class LoadBalanceFactory {

    public static final String RANDOM = "Random";

    public static final String ROUND_ROBIN = "RoundRobin";

    public static final String CONSISTENT_HASH = "ConsistentHash";

    private static Map<String, LoadBalance> loadBalances = new HashMap<String, LoadBalance>();

    static {
        ServiceLoader<LoadBalance> beans = ServiceLoader.load(LoadBalance.class);
        for (LoadBalance bean : beans) {
            LB annotation = bean.getClass().getAnnotation(LB.class);
            String value = annotation.value();
            loadBalances.putIfAbsent(value, bean);
        }
    }

    public static LoadBalance get(String name) {
        if (null == name || name.length() == 0) {
            throw new IllegalArgumentException("invalid name: " + name);
        }
        return loadBalances.get(name);
    }

    public static LoadBalance getDefault() {
        return get(RANDOM);
    }

//    public static void main(String[] args) {
//        List<String> list = new ArrayList<String>();
//        list.add("192.168.1.10:8899");
//        list.add("192.168.1.11:8899");
//        list.add("192.168.1.12:8899");
//
//        LoadBalance lb = LoadBalanceFactory.getDefault();
//        System.out.println(lb.select(list));
//    }
}
