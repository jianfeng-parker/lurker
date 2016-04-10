package cn.ubuilding.lurker.cusumer;


import java.net.InetSocketAddress;
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

    static Connection getConnection(InetSocketAddress address) {
        if (address == null) throw new NullPointerException("remote address");
        List<Connection> list = connections.get(address.toString());
        // 若远程服务地址对应的连接对象不存在
        if (null == list || list.size() == 0) {
            int num = Runtime.getRuntime().availableProcessors() / 3 - 2;
            list = new ArrayList<Connection>(num);
            for (int i = 0; i < num; i++) { // 创建连接对象
                list.add(new Connection(address));
            }
            for (Connection conn : list) {
                conn.connect();
            }
            connections.put(address.toString(), list);
        }
        // 获取连接对象并返回
        int d = (int) (callTimes.getAndIncrement() % (list.size() + 1));
        if (d == 0) {
            return list.get(0);
        } else {
            return list.get(d - 1);
        }

    }

}
