package cn.lion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    private SocketChannel clientChannel;
    NIOClient(){
        try {
            this.clientChannel = SocketChannel.open();
            this.clientChannel.connect(new InetSocketAddress("127.0.0.1",9999));
            this.accessServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accessServer() {
        ByteBuffer buffer = ByteBuffer.allocate(50); // 开辟一个缓冲区
        boolean flag = true;
        try {
            while (flag) {
                buffer.clear(); // 清空缓冲区，因为该部分代码会重复执行
                String msg = InputUtil.getString("请输入要发送的内容：");
                buffer.put(msg.getBytes()); // 将此数据保存在缓冲区之中
                buffer.flip(); // 重置缓冲区
                this.clientChannel.write(buffer); // 发送数据内容
                // 当消息发送过去之后还需要进行返回内容的接收处理
                buffer.clear(); // 清空缓冲区，等待新的内容的输入
                int readCount = this.clientChannel.read(buffer); // 将内容读取到缓冲区之中，并且返回个数
                buffer.flip(); // 得到前需要进行重置
                System.out.println(new String(buffer.array(), 0, readCount)); // 输出信息
                if ("exit".equalsIgnoreCase(msg)) {
                    flag = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NIOClient();
    }
}
