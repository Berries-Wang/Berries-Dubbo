# Dubbo 泛化调用
> 先阅读:[超越接口：探索Dubbo的泛化调用机制](./developer.aliyun.com_article_1479623.png)

在 Dubbo 中，泛化调用是指客户端调用服务端的方法时(客户端泛化)，可以`不依赖于服务端接口的具体定义`，而是`通过指定方法名和参数来实现调用`。这种泛化调用的实现原理涉及到 Dubbo 的`动态代理机制`以及`序列化`与`反序列化`过程。

Dubbo 中泛化调用的实现原理主要涉及动态代理、序列化与反序列化以及泛化调用的实现类。通过这些机制，Dubbo 可以实现对服务端接口定义的解耦，使得客户端可以通过指定方法名和参数来进行调用，从而实现泛化调用的功能。

## 使用场景
泛化调用可通过一个通用的 [GenericService](../../001.SOURCE_CODE/000.DUBBO-3.3.2-RELEASE/000.DUBBO-3.3.2-RELEASE/dubbo-common/src/main/java/org/apache/dubbo/rpc/service/GenericService.java) 接口对所有服务发起请求

```java
    package org.apache.dubbo.rpc.service;
    
    import java.util.concurrent.CompletableFuture;
    
    /**
     * Generic service interface
     *
     * @export
     */
    public interface GenericService {
    
        /**
         * Generic invocation
         *
         * @param method         Method name, e.g. findPerson. If there are overridden methods, parameter info is
         *                       required, e.g. findPerson(java.lang.String)
         * @param parameterTypes Parameter types
         * @param args           Arguments
         * @return invocation return value
         * @throws GenericException potential exception thrown from the invocation
         */
        Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException;
    
        default CompletableFuture<Object> $invokeAsync(String method, String[] parameterTypes, Object[] args)
                throws GenericException {
            Object object = $invoke(method, parameterTypes, args);
            if (object instanceof CompletableFuture) {
                return (CompletableFuture<Object>) object;
            }
            return CompletableFuture.completedFuture(object);
        }
    }
```
1. 网关服务
    - 如果要搭建一个网关服务，那么服务网关要作为所有 RPC 服务的调用端。但是网关本身不应该依赖于服务提供方的接口 API（这样会导致每有一个新的服务发布，就需要修改网关的代码以及重新部署），所以需要泛化调用的支持。
2. 测试平台: 
    - 如果要搭建一个可以测试 RPC 调用的平台，用户输入分组名、接口、方法名等信息，就可以测试对应的 RPC 服务。那么由于同样的原因（即会导致每有一个新的服务发布，就需要修改网关的代码以及重新部署），所以平台本身不应该依赖于服务提供方的接口 API。所以需要泛化调用的支持。

---

### consumer 启动向zookeeper注册
```java
   package org.apache.dubbo.demo;
   
   import java.util.concurrent.CompletableFuture;
   
   public interface DemoService {
   
       String sayHello(String name);
   
       default CompletableFuture<String> sayHelloAsync(String name) {
           return CompletableFuture.completedFuture(sayHello(name));
       }
   }
   
    // 泛化调用方法
    GenericService genericService = (GenericService)demoService;
    Object genericInvokeResult = genericService.$invoke("sayHello", new String[] {String.class.getName()},
                new Object[] {"dubbo generic invoke"});

// 21:42:35.809 |-INFO  [main] bbo.registry.zookeeper.ZookeeperRegistry:425 -|  [DUBBO] Register: dubbo://192.168.3.7:20880/org.apache.dubbo.demo.DemoService?application=dubbo-demo-api-provider&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=org.apache.dubbo.demo.DemoService&methods=sayHello,sayHelloAsync&prefer.serialization=hessian2,fastjson2&release=3.3.2&service-name-mapping=true&side=provider&timestamp=1739454081130, dubbo version: 3.3.2, current host: 192.168.3.7
```
