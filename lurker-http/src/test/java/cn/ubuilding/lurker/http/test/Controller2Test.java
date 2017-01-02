package cn.ubuilding.lurker.http.test;

import cn.ubuilding.lurker.http.annotation.RequestParam;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.core.RenderType;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 21:36
 */

public class Controller2Test {

    public Render hello1(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, String address) {

        return new Render("hello:" + name + "," + age, RenderType.TEXT);
    }
}
