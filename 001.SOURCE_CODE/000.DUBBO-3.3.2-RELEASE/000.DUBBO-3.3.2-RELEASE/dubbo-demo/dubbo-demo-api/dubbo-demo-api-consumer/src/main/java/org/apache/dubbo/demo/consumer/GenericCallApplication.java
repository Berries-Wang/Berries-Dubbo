package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 泛化调用
 */
public class GenericCallApplication {
    private static final Logger log = LoggerFactory.getLogger(GenericCallApplication.class);

    public static void main(String[] args) {
        { // 泛化调用
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("generic-call-consumer");
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress(Application.REGISTRY_URL);

            ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setInterface("org.apache.dubbo.demo.DemoService");
            applicationConfig.setRegistry(registryConfig);
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setGeneric("true");

            referenceConfig.setAsync(false);
            referenceConfig.setTimeout(7000);

            GenericService genericService = referenceConfig.get();
            Object sayHelloRes = genericService.$invoke("sayHello", new String[] {String.class.getName()},
                    new Object[] {"dubbo generic invoke"});
            log.info("泛化调用结果: " + sayHelloRes);
        }
    }
}
