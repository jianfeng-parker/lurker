package cn.ubuilding.lurker.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author Wu Jianfeng
 * @since 16/8/12 21:58
 */

public class NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final int MIN_PORT = 0;

    public static final int MAX_PORT = 65535;

    public static final String LOCAL_HOST = "127.0.0.1";

    public static final String ANY_HOST = "0.0.0.0";

    public static final String ADDRESS_SEPARATOR = ":";


    /**
     * 判断是否为本地地址
     */
    public static boolean isInvalidHost(String address) {
        return address == null || address.length() == 0
                || address.equalsIgnoreCase("localhost")
                || address.equals("0.0.0.0") || LOCAL_IP_PATTERN.matcher(address).matches();

    }

    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return null == address ? null : address.getHostAddress();
    }

    public static int getAvailablePort(int port) {
        if (port <= MIN_PORT || port > MAX_PORT) {
            getAvailablePort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(i);
                return i;
            } catch (IOException ignored) {
                // 继续尝试下一个端口
            } finally {
                if (null != ss) {
                    try {
                        ss.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return port;
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    /**
     * 遍历本地网卡，返回第一个有效的IP
     */
    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (UnknownHostException e) {
            logger.error("failed to getting local address:" + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            if (null != networks) {
                try {
                    while (networks.hasMoreElements()) {
                        NetworkInterface network = networks.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable t) {
                                logger.warn("Failed to retrying ip address, " + t.getMessage(), t);
                            }
                        }
                    }
                } catch (Throwable t) {
                    logger.warn("Failed to retrying ip address, " + t.getMessage(), t);
                }
            }
        } catch (Throwable t) {
            logger.warn("Failed to retrying ip address, " + t.getMessage(), t);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (null == address || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (null != name && !ANY_HOST.equals(name) && !LOCAL_HOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }
}
