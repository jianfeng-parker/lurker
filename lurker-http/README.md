### 基于Netty实现的简易HTTP Server框架 [参考](https://github.com/caskdata/netty-http)

> * 创建Server实例

> * 设置监听端口

> * 设置Action实例

> * 设置其它属性

> * 启动Server

#### 使用示例

*  添加依赖

```xml

  <dependency>
      <groupId>cn.ubuilding.lurker</groupId>
      <artifactId>lurker-http</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```

* 用户自定义Controller实现

```java
    
   @Path("/v1/user") 
   public class UserController{
   
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
  
```

*  实例化HTTP Server

```java

   HttpServer server = new HttpServer();
   server.setKeepAlive(true).addActions(new ControllerTest(), new Controller2Test()).start();
   
```

* HTTP访问

```java
  
   http://{ip}/v1/user/hello1?name=xx&age=18

```



