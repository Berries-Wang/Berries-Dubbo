/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.util.concurrent.OrderedEventExecutor;

/**
 * Will handle all the I/O operations for a {@link Channel} once registered.One {@link EventLoop} instance will usually
 * handle more than one {@link Channel} but this may depend on implementation details and
 * internals.（一旦注册，将处理通道的所有I/O操作。一个EventLoop实例通常会处理多个Channel，但这可能取决于实现细节和内部。）
 *
 * <pre>
 *  a. I/O事件处理：
 *      处理Channel的I/O事件，如读、写、连接和关闭。
 *      通过ChannelPipeline将事件传递给对应的ChannelHandler。
 *  b. 线程模型：
 *      每个EventLoop绑定一个线程，负责处理多个Channel的事件，确保线程安全。
 *      一个Channel的生命周期内通常只由一个EventLoop处理，避免多线程竞争。
 *  c. 事件循环：
 *      持续运行事件循环，检查并处理I/O事件和任务队列中的任务。
 *      高效利用系统资源，避免线程频繁切换。
 *
 *   d. 资源管理：
 *       管理Channel的生命周期，包括注册、激活和关闭。
 *       确保资源如文件描述符和内存的正确释放。
 * </pre>
 */
public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}
