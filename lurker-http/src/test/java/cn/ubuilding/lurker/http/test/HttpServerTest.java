package cn.ubuilding.lurker.http.test;

import cn.ubuilding.lurker.http.HttpServer;


/**
 * @author Wu Jianfeng
 * @since 2016/11/22 21:24
 */

public class HttpServerTest {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.setKeepAlive(true).addActions(new ControllerTest()).start();
    }

}
