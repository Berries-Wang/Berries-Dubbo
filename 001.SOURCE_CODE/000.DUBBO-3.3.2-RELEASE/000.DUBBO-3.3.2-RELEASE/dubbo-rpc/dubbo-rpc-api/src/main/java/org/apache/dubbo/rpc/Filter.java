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
package org.apache.dubbo.rpc;

import org.apache.dubbo.common.extension.ExtensionScope;
import org.apache.dubbo.common.extension.SPI;

/**
 * <p>
 *     需要结合 {@link  org.apache.dubbo.rpc.cluster.filter.DefaultFilterChainBuilder#buildInvokerChain(org.apache.dubbo.rpc.Invoker, java.lang.String, java.lang.String)}来分析
 * </p>
 *
 * Extension for intercepting the invocation for both service provider and consumer, furthermore, most of
 * functions in dubbo are implemented base on the same mechanism. Since every time when remote method is
 * invoked, the filter extensions will be executed too, the corresponding penalty should be considered before
 * more filters are added.(为服务提供者和服务消费者提供拦截调用的扩展，另外 dubbo 中的大部分功能都是基于同一种机制实现的。
 * 由于每次调用远程方法时，过滤器扩展也会被执行，所以在添加更多过滤器之前，需要考虑相应的惩罚。)
 * <pre>
 *  They way filter work from sequence point of view is(从序列的角度来看，过滤器的工作方式是)
 *    <b>
 *    ...code before filter ...
 *          invoker.invoke(invocation) //filter work in a filter implementation class
 *          ...code after filter ...
 *    </b>
 *    Caching is implemented in dubbo using filter approach. If cache is configured for invocation then before
 *    remote call configured caching type's (e.g. Thread Local, JCache etc) implementation invoke method gets called.
 * </pre>
 *
 * Starting from 3.0, Filter on consumer side has been refactored. There are two different kinds of Filters working at different stages
 * of an RPC request.
 * 1. Filter. Works at the instance level, each Filter is bond to one specific Provider instance(invoker).
 * 2. ClusterFilter. Newly introduced in 3.0, intercepts request before Loadbalancer picks one specific Filter(Invoker).
 *
 * Filter Chain in 3.x
 *
 *                                          -> Filter -> Invoker
 *
 * Proxy -> ClusterFilter -> ClusterInvoker -> Filter -> Invoker
 *
 *                                          -> Filter -> Invoker
 *
 *
 * Filter Chain in 2.x
 *
 *                            Filter -> Invoker
 *
 * Proxy -> ClusterInvoker -> Filter -> Invoker
 *
 *                            Filter -> Invoker
 *
 *
 * Filter. (SPI, Singleton, ThreadSafe)
 *
 * @see org.apache.dubbo.rpc.filter.GenericFilter
 * @see org.apache.dubbo.rpc.filter.EchoFilter
 * @see org.apache.dubbo.rpc.filter.TokenFilter
 * @see org.apache.dubbo.rpc.filter.TpsLimitFilter
 */
@SPI(scope = ExtensionScope.MODULE)
public interface Filter extends BaseFilter {}
