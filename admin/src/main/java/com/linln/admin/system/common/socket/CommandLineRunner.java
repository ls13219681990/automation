package com.linln.admin.system.common.socket;

import com.linln.modules.system.Common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

    //线程安全
    public static  List<SocketChannel> channels = Collections.synchronizedList(new ArrayList<SocketChannel>());

    @Autowired
    private Server serverPort;



    @Override
    public void  run(String... args)  {
        HandlerSelectionKey handler = new HandlerHandlerSelectionKeyImpl();

        try {


            //创建 ServerSocketChannel
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            Integer socketPort = Integer.valueOf(serverPort.getSocketPort());
            server.bind(new InetSocketAddress(socketPort));
            //创建 Selector
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            //死循环，持续接收 客户端连接
            while (true) {
                //selector.select(); 是阻塞方法
                int keys = selector.select();
                if (keys > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {

                        SelectionKey key = it.next();
                        it.remove();
                        //处理 SelectionKey
                        handler.handler(key, selector);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //run();
        }
    }
}
