package cn.ubuilding.lurker.cusumer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Wu Jianfeng
 * @since 16/4/10 20:56
 */

final class ConnectionFactory {

    private static AtomicLong callTimes = new AtomicLong(0L);

    /**
     * 每一个远程服务地址存放多个连接对象
     */
    private static Map<String, List<Connection>> connections = new ConcurrentHashMap<String, List<Connection>>();

    static Connection get(String address) {
        List<Connection> list = connections.get(address);
        if (null == list || list.size() == 0) {
            create(address);
        }
        list = connections.get(address);
        // 获取连接对象并返回
        int d = (int) (callTimes.getAndIncrement() % (list.size() + 1));
        if (d == 0) {
            return list.get(0);
        } else {
            return list.get(d - 1);
        }

    }

    static void reset(String address) {
        connections.remove(address);
        create(address);
    }

    /**
     * 重新初始化连接
     *
     * @param address 远程服务地址
     */
    private static void create(String address) {
        if (address == null || address.length() == 0) {
            throw new NullPointerException("remote address");
        }
        if (address.split(":").length != 2) {
            throw new IllegalArgumentException("invalid address, it must be formatted by 'host:port'");
        }
        List<Connection> list = connections.get(address);
        // 若远程服务地址对应的连接对象不存在
        if (null == list || list.size() == 0) {
            String[] addr = address.split(":");
            int num = Runtime.getRuntime().availableProcessors() / 3 - 2;
            list = new ArrayList<Connection>(num);
            for (int i = 0; i < num; i++) { // 创建连接对象
                list.add(new Connection(addr[0], Integer.parseInt(addr[1])));
            }
            for (Connection conn : list) {
                conn.connect();
            }
            connections.put(address, list);
        }
    }


}
