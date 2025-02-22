import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 先注册，再绑定 ， 再设置监听类型 ， 最后处理请求
 * 
 * <pre>
 * 在 Java NIO 中，SelectableChannel#register(Selector sel, int ops, Object att) 方法的第二个参数 ops 表示 感兴趣的事件集合，
 * 它是一个位掩码（bitmask），用于指定 Channel 希望监听的事件类型。如果传入 0，则表示 不监听任何事件。
 * </pre>
 * 
 * telnet localhost 8080
 * 就可以进行交互了
 */
public class NioEchoServerV2 {

    public static void main(String[] args) throws IOException {
        // 创建一个 Selector
        Selector selector = Selector.open();

        // 创建一个 ServerSocketChannel 并绑定端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式

        // 将 ServerSocketChannel 注册到 Selector，监听 ACCEPT 事件
        SelectionKey key_no_interest = serverSocketChannel.register(selector, 0, null);

        System.out.println("Server started on port 8080...");
        // 绑定到 8080 端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 设置需要监听的事件类型
        key_no_interest.interestOps(SelectionKey.OP_ACCEPT);

        while (true) {
            // 阻塞等待就绪的 Channel
            selector.select();

            // 获取就绪的 SelectionKey 集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // 处理新的客户端连接
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    // 处理客户端数据读取
                    handleRead(key);
                }

                // 移除已处理的 SelectionKey
                keyIterator.remove();
            }
        }
    }

    // 处理新的客户端连接
    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept(); // 接受客户端连接
        socketChannel.configureBlocking(false); // 设置为非阻塞模式

        // 将 SocketChannel 注册到 Selector，监听 READ 事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + socketChannel.getRemoteAddress());
    }

    // 处理客户端数据读取
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 分配缓冲区

        int bytesRead = socketChannel.read(buffer); // 读取数据到缓冲区
        if (bytesRead == -1) {
            // 客户端关闭连接
            System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
            socketChannel.close();
            return;
        }

        // 处理读取的数据
        buffer.flip(); // 切换为读模式
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data); // 将数据从缓冲区复制到字节数组
        String message = new String(data); // 将字节数组转换为字符串
        System.out.println("Received from client: " + message);

        // 将数据原样返回给客户端
        buffer.flip(); // 切换为写模式
        socketChannel.write(buffer); // 将数据写回客户端
    }
}