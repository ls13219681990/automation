package com.linln.admin.system.common.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
/**
 * 
 * @author Allen 2017年7月19日
 *
 */
public class SocketServer {
	/**
	 * class constant
	 */
	static int POST = 8288;
	static String NOTE_FORMAT_INFO = "[%s:%s]<ID:%s> %s %s \n";
	static ExecutorService threads;
	static PrintWriter pw = null;
	static Map<Integer, Socket> alls = new HashMap<Integer, Socket>();//保存ClientSocket的容器,可以进行统计,定向广播等
 
	public static void main(String[] args) throws IOException {
                //初始化指定大小的线程池
		threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() << 3);
		SocketServer socketServer = new SocketServer();
		try {
			socketServer.openServer();
		} catch (BindException e) {
			System.out.printf("%s [%s] %s", "端口", POST, "被占用");
		}
	}
 
	public void openServer() throws BindException, IOException {
		ServerSocket server = null;
		int uid = 0;
		try {
			server = new ServerSocket(POST);
			System.out.println("服务器启动成功");
			while (true) {//为什么while(true)因为要不断的保证进入下面的阻塞来接收新的客户端
                               //SocketServer通过阻塞来获取一个new Socket
                               //java如何实现的accept阻塞？通过源码最终指向下面方法
                               //当看到native我们就不需要在去考虑了
                               //native是什么？native是调用本地方法
                               //static native int accept0(int fd, InetSocketAddress[] isaa) throws IOException;
				Socket socket = server.accept();
                               //当用户连接成功时我们给他发送一条欢迎消息
				sendHelp("连接成功 --  From SocketServer", null, socket); 
                               //通过线程池启动线程并把我们uid+1,当然这里的uid没有原子性
                               //原子性怎么做？synchronized/CAS/AtomicInteger皆可
				threads.execute(new ClientSocekt(socket, ++uid));
			}
		} finally {
			if (server != null && !server.isClosed())
				server.close();
 
		}
	}
 
	private void sendHelp(String msg, Socket nowSocket, Socket... sockets) throws IOException {
		if (sockets == null || sockets.length == 0)
                        //把alls的值通过toArray(T[] t)转成Socket[]
			sockets = alls.values().toArray(new Socket[0]);
		for (Socket s : sockets) {
                        //把消息广播到sockets[]
			pw = new PrintWriter(s.getOutputStream());
                        //如果不是println就一定要在后面加上"\n"
			pw.println(nowSocket != null && s.hashCode() == nowSocket.hashCode() ? "send success" : msg);
			pw.flush();
		}
 
	}
 
	class ClientSocekt extends Thread {
		Socket socket;
		Integer uid;
		BufferedReader br;
 
		public ClientSocekt(Socket socket, Integer uid) {
			this.socket = socket;
			alls.put(uid, socket);
			this.uid = uid;
		}
 
		@Override
		public void run() {
			try {
				while (true){
                                //通过bufferedReader从缓冲区读取数据
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = null;
                                //readline也是阻塞的
                                //本demo没有把粘包等问题考虑
                                //如果想解决粘包最简单的就是用read，创建一个Byte
                                //用byte去接,byte设定一个大小
                                //传输的文本改用 {Length|Context|Type|End} 这种方式
                                //当得到length > byte.length的时候继续去缓冲区获取数据，知道此数据获取完毕
				while ((line = br.readLine()) != null) {
					System.out.printf(NOTE_FORMAT_INFO, socket.getInetAddress().toString(), socket.getPort(), uid,
							"收到: ", line);
					sendHelp("来自: "+socket.getInetAddress().toString()+":"+socket.getPort()+"的消息: "+line, socket);
				}
				}
			} catch (SocketException e) {
				System.out.printf(NOTE_FORMAT_INFO, socket.getInetAddress().toString(), socket.getPort(), uid, "状态: ",
						"离开了");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				alls.remove(uid);
				clear();
			}
 
		}
 
		private void clear() {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
                        //销毁客户端线程
			this.interrupt();
		}
	}

}