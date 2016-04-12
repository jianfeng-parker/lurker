## 基于Netty4.0.35.Final实现的简易RPC框架

### Consumer(Client):

> * 1. 通过创建Consumer实例生成目标服务接口的代理

> * 2. 创建Consumer实例时允许调用者传入基于Discovery接口自定义的实现类用于发现远程服务地址

> * 3. 动态监听远程服务地址的变化

> * 4. 向远程服务发情请求并接受返回结果

### Provider(Server):

> * 1. 调用者通过ServerBoot实例将需要对外提供服务的接口实现类注册到Server中；

> * 2. 向注册中心(Zookeeper/Redis)注册服务地址等信息；

> * 3. 接受客户端的连接；

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

  <dependency>
      <groupId>cn.ubuilding.platform</groupId>
      <artifactId>lurker-rpc</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>

```

```java
   
  /**
   * key: 由服务发布方提供，即可以唯一标识服务的值，Consumer端使用该key从注册中心获取服务相关信息
   */  
   public class XXService{
     public String sayHello(String name){
         // 初始化Consumer实例时传递一个key，即使用默认的Discover(from zookeeper)获取远程服务信息
         // 效果等同于new Consumer(new DefaultDiscovery(key), HelloService.class).instance();
         // 也可以使用从Redis获取服务信息Discovery:new Consumer(new RedisDiscovery(key), HelloService.class).instance()
         // 也可以使用自定义的Discovery，实现Discovery接口即可
         // 具体到哪里获取服务信息，取决于服务端将服务信息发布到哪里
         HelloService helloService = new Consumer(key, HelloService.class).instance();
         return helloService.say("Parker");
     }
   }
  
```
##### Provider(Server)端:

```xml
  
  <!--添加依赖-->
  <dependency>
      <groupId>cn.ubuilding.platform</groupId>
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

```xml
  
  <bean id="helloService" class="xx.xx.xx.HelloServiceImpl"/>
  
``` 

###### 接口发布方式1：

```java
  
  public class XXService{
       @Autowired
       private HelloServiceImpl helloService;
       
       public void publish(){
           List<ProviderInfo> providers = new ArrayList<ProviderInfo>();
           providers.add(new ProviderInfo(key,"1.0.0",helloService));
           // providers.add(发布的其它服务);
           new Server("127.0.0.1",providers).start();
       }
  }

```

###### 接口发布方式2：

```xml

  <bean id="rpcPublisher" class="cn.ubuilding.lurker.provider.Server" init-method="start">
     <constructor-arg index="0" value="127.0.0.1"/>
     <constructor-arg index="1">
       <list>
          <bean class="cn.ubuilding.lurker.provider.ProviderInfo">
             <property name="key" value="xxx"/>
             <property name="version" value="1.0.0"/>
             <property name="implementation" ref="helloService"/>
          </bean>
       </list>
     </constructor-arg>
  </bean>

```


