package cn.ubuilding.lurker.http.test;

import cn.ubuilding.lurker.http.annotation.*;
import cn.ubuilding.lurker.http.core.Controller;
import cn.ubuilding.lurker.http.core.HttpMethod;

/**
 * @author Wu Jianfeng
 * @since 2016/11/30 23:59
 */

@Path("/v1/user")
public class ControllerTest implements Controller {

    @Path(value = "/hello", produce = "json", method = HttpMethod.GET)
    public void say(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age,String address) {

    }

    @Path(value = "/add", produce = "json", method = HttpMethod.POST)
    public void add(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, @RequestBody(name = "user") String body) {

    }
}
