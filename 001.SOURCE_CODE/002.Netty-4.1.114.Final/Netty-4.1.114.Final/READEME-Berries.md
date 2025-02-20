# Netty 中一些概念
## Server创建流程
1. 套接字创建，端口监听: socket套接字 ， 端口绑定，套接字监听: sun.nio.ch.ServerSocketChannelImpl#bind