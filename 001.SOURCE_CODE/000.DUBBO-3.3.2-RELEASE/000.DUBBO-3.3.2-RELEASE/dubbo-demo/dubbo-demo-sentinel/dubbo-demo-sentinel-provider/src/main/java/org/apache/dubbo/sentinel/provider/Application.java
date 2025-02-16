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
package org.apache.dubbo.sentinel.provider;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoSentinelService;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;

public class Application {

    private static final String REGISTRY_URL = "zookeeper://192.168.3.198:2181";

    public static void main(String[] args) {
        startWithBootstrap();
    }

    private static void startWithBootstrap() {
        ServiceConfig<DemoSentinelServiceImpl> service = new ServiceConfig<>();
        service.setInterface(DemoSentinelService.class);
        service.setRef(new DemoSentinelServiceImpl());
        {
            // service.setActives(9); // 并不会导入 Filter , 因为这是个消费端参数.
            service.setExecutes(9); // 设置最大并发数: 请求链路中会多一个Filter：org.apache.dubbo.rpc.filter.ExecuteLimitFilter
        }

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("dubbo-demo-sentinel-provider"))
                .registry(new RegistryConfig(REGISTRY_URL))
                .protocol(new ProtocolConfig(CommonConstants.DUBBO, -1))
                .service(service);
        DubboAdapterGlobalConfig.setConsumerFallback(new DubboSentinelSTUFallback());
        DubboAdapterGlobalConfig.setProviderFallback(new DubboSentinelSTUFallback());
        bootstrap.start().await();
    }
}
