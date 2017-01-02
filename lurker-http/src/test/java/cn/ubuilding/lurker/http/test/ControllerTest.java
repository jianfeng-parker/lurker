package cn.ubuilding.lurker.http.test;

import cn.ubuilding.lurker.http.annotation.*;
import cn.ubuilding.lurker.http.core.HttpMethod;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.core.RenderType;
import cn.ubuilding.lurker.http.test.model.TestUser;

/**
 * @author Wu Jianfeng
 * @since 2016/11/30 23:59
 */

@Path("/v1/user")
public class ControllerTest {

    @Path(value = "/hello1", produce = "json", method = HttpMethod.GET)
    public Render hello1(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, String address) {

        return new Render("hello:" + name + "," + age, RenderType.TEXT);
    }

    @Path(value = "/hello2", produce = "json", method = HttpMethod.POST)
    public Render hello2(@RequestParam(name = "name") String name, @RequestParam(name = "age") int age, @RequestBody String body) {

        return new Render(body + "\n" + name + "." + age, RenderType.TEXT);
    }

    @Path(value = "/hello3", produce = "json", method = HttpMethod.POST)
    public Render hello3(@RequestParam(name = "type") String type, @RequestBody TestUser user) {

        return new Render(user.getName() + "." + user.getAge() + "." + user.getList() + "\n" + type, RenderType.TEXT);
    }


}
