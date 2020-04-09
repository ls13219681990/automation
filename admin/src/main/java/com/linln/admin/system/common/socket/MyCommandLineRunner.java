package com.linln.admin.system.common.socket;

import com.linln.component.actionLog.action.ConnectAction;
import com.linln.component.actionLog.action.RoleAction;
import com.linln.component.actionLog.annotation.ActionLog;
import com.linln.modules.system.Common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private Server server;



    @Override

    public void run(String... var1) throws Exception {
        System.out.println("tcp Server run");
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {


            Integer socketPort = Integer.valueOf(server.getSocketPort());

            //构造ServerSocket实例，指定端口监听客户端的连接请求
            serverSocket = new ServerSocket(socketPort);//8288
            while (true) {
                socket = serverSocket.accept();//有连接会返回一个Socket,其作为参数传给服务端线程类
                System.out.println("client successed ！");
                new ServerThread(socket).start();//有新的连接就建立一个对应的线程

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //操作结束，关闭socket
            serverSocket.close();
            socket.close();
        }
    }
}
