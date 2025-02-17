/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.sentinel.consumer;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoSentinelService;
import org.apache.dubbo.rpc.service.Destroyable;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    public static final String REGISTRY_URL = "zookeeper://192.168.3.198:2181";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {
        runWithBootstrap();
    }

    private static void runWithBootstrap() throws InterruptedException {
        System.setProperty("Java_Assist_Class_Path",
                "/Users/wang/WorkSpace/OpenSource/Berries-Dubbo/001.SOURCE_CODE/000.DUBBO-3.3.2-RELEASE/000.DUBBO-3.3.2-RELEASE/logs");
        ReferenceConfig<DemoSentinelService> reference = new ReferenceConfig<>();
        reference.setInterface(DemoSentinelService.class);
        reference.setGeneric("true");
        reference.setTimeout(Integer.MAX_VALUE);
        reference.setActives(5); // 调用链路中会注册: org.apache.dubbo.rpc.filter.ActiveLimitFilter

        RegistryConfig registryConfig = new RegistryConfig(REGISTRY_URL);
        registryConfig.setSimplified(true);

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("dubbo-demo-sentinel-consumer"))
                .registry(registryConfig)
                .protocol(new ProtocolConfig(CommonConstants.DUBBO, -1))
                .reference(reference)
                .start();

        DemoSentinelService demoService = null;
        Destroyable destroyable = null;
        Object genericInvokeResult = null;
        for (int times = 0; times < Integer.MAX_VALUE; times++) {
            try {
                demoService = bootstrap.getCache().get(reference);
                String message = demoService.sayHello("dubbo");
                System.out.println(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

            TimeUnit.MILLISECONDS.sleep(500);
            // generic invoke
            try {
                GenericService genericService = (GenericService)demoService;
                genericInvokeResult = genericService.$invoke("sayHello", new String[] {String.class.getName()},
                        new Object[] {"dubbo generic invoke"});
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

            TimeUnit.MILLISECONDS.sleep(500);
        }

        destroyable = (Destroyable)demoService;
        destroyable.$destroy();
        System.out.println(genericInvokeResult.toString());
    }
}
