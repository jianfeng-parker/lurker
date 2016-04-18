### 基于Netty实现的RPC框架

------
> * 网络通讯框架: Netty, Version: 4.0.35.Final

> * 服务注册中心: ZooKeeper, Version: 3.4.6

> * 序列化工具: protostuff, Version: 1.3.8

### Consumer(Client):

> * 通过创建Consumer实例生成目标服务接口的代理

> * 创建Consumer实例时允许调用者传入基于Discovery接口自定义的实现类用于发现远程服务地址

> * 动态监听远程服务地址的变化

> * 向远程服务发送请求并接受返回结果

### Provider(Server):

> * 调用者通过ServerBoot实例将需要对外提供服务的接口实现类注册到Server中；

> * 向注册中心(Zookeeper/Redis)注册服务地址等信息；

> * 接受客户端的连接；

#### 使用示例:

##### 公共接口定义:

```java

  /**
   *  公共服务接口定义
   *  客户端和服务端都依赖该接口
   */
   public interface HelloService{
       String say(String name);
   }
     
```

##### Consumer(Client)端:

```xml
  <!--添加依赖-->
  <dependency>
      <groupId>cn.ubuilding.lurker</groupId>
      <artifactId>lurker-rpc</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```
###### 远程接口调用：

```java
   
  /**
   * serviceKey: 由服务发布方提供，即可以唯一标识服务的值，Consumer端使用该key从注册中心获取服务相关信息
   */  
   public class XXService{
   
     // 初始化Consumer实例时传递一个key，即使用默认的Discover(from zookeeper)获取远程服务信息
     // 效果等同于new Consumer(new DefaultDiscovery(serviceKey), HelloService.class).instance();
     // 也可以使用从Redis获取服务信息Discovery:new Consumer(new RedisDiscovery(serviceKey), HelloService.class).instance()
     // 也可以使用自定义的Discovery，实现Discovery接口即可
     // 具体到哪里获取服务信息，取决于服务端将服务信息发布到哪里
     public String sayHello(String name){
         HelloService helloService = new Consumer(serviceKey, HelloService.class).instance();
         return helloService.say("Parker");
     }
     
   }
  
```
##### Provider(Server)端:

```xml
  
  <!--添加依赖-->
  <dependency>
      <groupId>cn.ubuilding.lurker</groupId>
      <artifactId>lurker-rpc</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```

```java

  /**
   * 接口实现
   */
   public class HelloServiceImpl implements HelloService{
     public String say(String name){
       return "hello "+name;
     }
   }
   
```

###### 接口发布方式1：

```java
  
  public class XXService{
  
       @Autowired
       private HelloServiceImpl helloService;
       
       /**
        * Java代码方式发布RPC服务
        */
       public void publish(){
           List<Provider> providers = new ArrayList<Provider>();
           providers.add(new Provider(serviceKey,helloService));
           // providers.add(发布的其它服务);
           new Server("127.0.0.1",providers).start();
       }
  }

```

###### 接口发布方式2：

```xml

  <bean id="helloService" class="cn.ubuilding.lurker.application.provider.impl.HelloServiceImpl"/>
  
  <bean id="doSomethingService" class="cn.ubuilding.lurker.application.provider.impl.DoSomethingServiceImpl"/>

  <!--spring配置文件的方式发布RPC服务-->
  <bean id="rpcServer" class="cn.ubuilding.lurker.provider.Server" init-method="start">
          <!--rpc服务暴露地址-->
          <constructor-arg index="0" value=" 127.0.0.1"/>
          <!--rpc服务暴露端口-->
          <constructor-arg index="1" value="8899"/>
          <!--暴露的rpc服务列表-->
          <constructor-arg index="2">
              <list>
                  <bean class="cn.ubuilding.lurker.provider.Provider">
                      <!--唯一标识服务的key,客户端根据该key从注册中心获取前面配置的rpc服务地址-->
                      <property name="serviceKey" value="helloService_1.0"/>
                      <!--rpc服饰实现者-->
                      <property name="implementation" ref="helloService"/>
                  </bean>
                  <bean class="cn.ubuilding.lurker.provider.Provider">
                      <property name="serviceKey" value="doSomethingService_1.0"/>
                      <property name="implementation" ref="doSomethingService"/>
                  </bean>
              </list>
          </constructor-arg>
          <!--注册中心地址(默认使用zookeeper)-->
          <constructor-arg index="3" value="127.0.0.1:2181"/>
  </bean>

```


