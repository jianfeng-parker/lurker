## 基于Netty实现的网络通讯框架项目

### LurkerRPC

> * 网络通讯框架: Netty, Version: 4.1.16.Final

> * 服务注册中心: ZooKeeper, Version: 3.4.6

> * 序列化工具: protostuff, Version: 1.3.8

#### Client:

> * 创建Client实例用于生成远程服务代理:同步/异步

> * 创建Client实例时传入Registry用于发现远程服务地址

> * 监听远程服务地址变化,更新连接

> * 向远程服务发送请求并接受返回结果

#### Server:

> * 创建Server实例

> * 监听端口

> * 发布RPC服务实例

> * 向注册中心(Zookeeper/Redis)注册服务

> * 接受客户端的连接，并返回服务执行结果

#### 使用示例:

* 公共接口定义:

```java

  /**
   *  公共服务接口定义
   *  客户端和服务端都依赖该接口
   */
   public interface HelloService{
       String say(String name);
   }
     
```

* Client端:

1. 添加依赖

```xml
  <!--添加依赖-->
  <dependency>
      <groupId>cn.ubuilding.lurker</groupId>
      <artifactId>lurker-rpc</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```
2. 远程接口调用：

```java
    
   public class XXBizService{
   
     public String bizMethod(){
         // 初始化Client实例，传入Registry地址
         Client client = new Client("127.0.0.1:2181");
         
         // 创建服务代理，执行同步请求
         HelloService helloService = client.create(HelloService.class);
         return helloService.say("Parker");
         
         // 创建异步代理，执行异步请求
         AsyncRemoteProxy proxy = client.createAsync(HelloService.class);
         // 方法名，参数值
         ResponseFuture future = proxy.call("say","Parker");
         String result = (String)future.get(); // get(3000, TimeUnit.MILLISECONDS);
         
         // 异步执行对调
         AsyncRemoteProxy proxy2 = client.createAsync(HelloService.class);
         // 方法名，参数值
         ResponseFuture future2 = proxy2.call("say","Parker");
         future2.addCallback(new AsyncCallback(){
             public void success(Object result){
                 // TODO ...
             }
             public void fail(Throwable t){
                 // TODO ...
             }
         });
     }
     
   }
  
```
* Server端:

1. 添加依赖

```xml
  
  <dependency>
      <groupId>cn.ubuilding.lurker</groupId>
      <artifactId>lurker-rpc</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```

2. 服务实现:

```java

  /**
   * 加 @Rpc标签 表示该类需要发布为RPC服务
   * 标签值为必填项，用于指定RPC服务接口类，因为一个类可能实现多个接口
   */
   @Rpc(HelloService.class)
   public class HelloServiceImpl implements HelloService{
     public String say(String name){
       return "hello "+name;
     }
   }
   
   @Rpc(DoSomethingService.class)
   public class DoSomethingServiceImpl implements DoSomethingService {
       public void doSomething() {
           System.out.println("do something...");
       }
   }
   
```

3. 服务发布方式1：

```java
  
  /*
   * 初始化一个Server实例，指定注册中心
   * 其它初始化参数见代码
   */ 
  Server server = new Server("127.0.0.1:2181");
  server.addService(helloService);
  // or
  List<Object> services = new ArrayList<Object>();
  services.add(helloService);
  services.add(somethingService);
  
  server.addService(services);
  
  server.start();
  

```

4. 服务发布方式2：

```xml

  <bean id="helloService" class="cn.ubuilding.lurker.application.provider.impl.HelloServiceImpl"/>
  
  <bean id="doSomethingService" class="cn.ubuilding.lurker.application.provider.impl.DoSomethingServiceImpl"/>
  
  <bean id="zkRegistry" class="cn.ubuilding.lurker.support.registry.ZookeeperRegistry">
        <constructor-arg value="localhost:2181"/>
  </bean>
  
  <bean id="rpcServer" class="cn.ubuilding.lurker.server.Server" init-method="start">
        <!--服务暴露端口-->
        <property name="port" value="8899"/>
        <!--服务注册中心-->
        <property name="registry" ref="zkRegistry"/>
        <!--RPC服务-->
        <property name="services">
            <list>
                <ref bean="helloService"/>
                <ref bean="doSomethingService"/>
            </list>
        </property>
  </bean>

```

### LurkerHTTP



