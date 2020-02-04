package cn.lion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {
    private ServerSocketChannel serverChannel;
    private SocketChannel clientChannel;
    private Selector selector;
    private ExecutorService executorService;

    public NIOServer() {
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.bind(new InetSocketAddress(9999));
            this.selector = Selector.open();
            this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            this.executorService = Executors.newFixedThreadPool(2);
            System.out.println("服务器端程序启动，该程序在" + 9999 + "端口上进行监听...");
            this.clientHandle();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clientHandle() throws IOException {
        int keySelect = 0;
        while((keySelect = this.selector.select()) > 0){
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isAcceptable()){
                    this.clientChannel = this.serverChannel.accept();
                    if (this.clientChannel != null) {  // 当前有连接
                        this.executorService.submit(new SocketClientChannelThread(this.clientChannel)) ;
                    }
                }
                iterator.remove();
            }
        }
    }

    private class SocketClientChannelThread implements Runnable {
        private SocketChannel clientChannel;
        private boolean flag = true;
        public SocketClientChannelThread(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
            System.out.println("服务器端连接成功，可以与服务器端进行数据的交互操作...");
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(50);
            while(flag){
                buffer.clear();
                int readCount = 0;
                try {
                    readCount = clientChannel.read(buffer);
                    String readMessage = new String(buffer.array(),0,readCount).trim() ;
                    System.out.println("【服务器端接收消息】" + readMessage); // 输出一下提示信息
                    String writeMessage = "【ECHO】" + readMessage + "\n"; // 进行消息的回应处理
                    if ("exit".equalsIgnoreCase(readMessage)) {
                        writeMessage = "【ECHO】Bye Byte ... kiss"  ; // 结束消息
                        this.flag = false ; // 要结束当前的循环操作
                    }   // 现在的数据是在字符串之中，如果要回应内容，需要将内容保存在Buffer之中
                    buffer.clear() ; // 将已经保存的内容（内容已经处理完毕）清除
                    buffer.put(writeMessage.getBytes()) ; // 保存回应信息
                    buffer.flip() ; // 重置缓冲区
                    this.clientChannel.write(buffer) ;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            try {
                this.clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new NIOServer();
    }
}
