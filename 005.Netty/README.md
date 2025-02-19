# Netty
Netty 之所以性能高，主要是因为:
1. 基于NIO的非阻塞I/O模型
2. 事件驱动模型（Reactor模式）
3. 内存池化技术： 通过内存池复用ByteBuf , 减少了频繁的内存分配和回收带来的性能开销.
4. 零拷贝技术： 使用DirectBuffer和FileChannel实现零拷贝， 减少内存复制，提高数据传输效率。
5. 高效的线程管理： Netty的线程模型通过EventLoopGroup高效管理线程，充分利用多核CPU。
6. 高效的Pipeline机制： Netty的Pipeline机制处理链非常灵活可插拔各种Handler进行数据处理.
7. 实例代码:[]()

