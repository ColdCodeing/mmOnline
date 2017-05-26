package org.mm.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class Server extends Thread{
	
	static String heatbeatMsg = "heartbeating";
	private ServerSocket clientserver = null;
	private ServerSocket webServer = null;
	private Socket webClient = null;
	private static Properties pro = null;
	private List<Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
	
	//建立监听端口
	public Server() throws IOException {
		//加载配置文件
		pro = new Properties();
		pro.load(Server.class.getResourceAsStream("Server.properties"));
	}
	
	public void run() {
		try {
			//建立线程池
			ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
			System.out.println("Server已经启动");
			System.out.println("wait for Web part...");
			this.clientserver = new ServerSocket(Integer.parseInt(pro.getProperty("serverPort")));
			this.webServer = new ServerSocket(Integer.parseInt(pro.getProperty("webPort")));
			this.acceptWeb();//等待Web连接
			//Web服务
			new Thread(new WebMonitor(this.sockets,this.webClient)).start();

			System.out.println("BULTER Service Start Up Normally......");
			//轮询启动
			service.scheduleAtFixedRate(new ScheduledExecutorClient(this.sockets), 
					0, 5, TimeUnit.SECONDS);
			
			while(true){
				this.acceptPC();//等待PC连接
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	//等待PC连接
	public void acceptPC() throws IOException{
		Socket socket = clientserver.accept();
		socket.setSoTimeout(750);
		sockets.add(socket);
		System.out.println("PC :" + socket.getInetAddress() + " connect");
	}
	//等待Web应用连接
	public void acceptWeb() throws IOException{
		webClient = webServer.accept();
		webClient.setSoTimeout(5000);
		System.out.println("Web :"+webClient.getInetAddress()+" connect");
	}
}

//命令收发
class WebMonitor implements Runnable{
	
	private List<Socket> sockets = null;
	private Socket Websocket = null;
	
	public WebMonitor(List<Socket> sockets, Socket Websocket) {
		this.sockets = sockets;
		this.Websocket = Websocket;
	}
	
	public void run() {
		
			while(true)
			{	
				try {	
					String msg = null;
					msg = receiveOrder();
					if(msg != null){
						//执行ALLIP命令,返回所有连接的client ip
						if(msg.startsWith("ALLIP")){
							OutputStream os = Websocket.getOutputStream();
							Iterator<Socket> iterator = sockets.iterator();
							while(iterator.hasNext()){
								Socket s = iterator.next();
								String ip = s.getInetAddress().toString();
								ip = ip.replaceAll("/", "\n");
								os.write((ip + "\n").getBytes());
							}
							os.write("/".getBytes());
							os.flush();
						}
						else{
							String[] order = msg.split("\n");
							Iterator<Socket> iterator = sockets.iterator();
							while(iterator.hasNext()){
								Socket socket = iterator.next();
								if(socket.getInetAddress().toString().contains(order[0])){
									sendOrder(order[1], socket);
								}
								//sendOrder(order[0], socket);
							}
						}	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}
	
	/**
	 * 读取来自web的命令
	 * @return
	 * @throws IOException
	 */
	public String receiveOrder() throws IOException{
		InputStream is = this.Websocket.getInputStream();
		byte[] buf = new byte[1024];
		Integer len = null;
		StringBuilder msg = new StringBuilder();
		while(true){
			try{
				if((len=is.read(buf))!=-1){
					msg.append(new String(buf, 0, len));
					if(msg.toString().endsWith("\n\n"))
						return msg.toString();
				}
		    }
			catch (SocketTimeoutException e) {
				//System.out.println(msg.toString());
				return null;
			}
		}
		
	}
	
	/**
	 * 用于发送命令至client
	 * @param msg
	 * @param socket
	 * @throws IOException
	 */
	public void sendOrder(String msg, Socket socket) throws IOException{
		OutputStream os = socket.getOutputStream();
		os.write(msg.getBytes());
		os.flush();
	}	
}



/**
 * ScheduledExecutorClient
 * 用来轮询全部用户机,检查用户机是否离线,离线则将用户机移除列表
 *
 */
class ScheduledExecutorClient implements Runnable {
	//用于通信的Socket
	private List<Socket> Clientsockets = null;
	private static String heartbeatMsg= "heartbeating";
	
	public ScheduledExecutorClient(List<Socket> Clientsockets){
		this.Clientsockets = Clientsockets;
	}
	
	public void run() {
		Iterator<Socket> iterator = Clientsockets.iterator();
		while(iterator.hasNext()){
			Socket socket = iterator.next();
			//连接失败时移除一个socket
			if(isConnected(socket) == false){
				iterator.remove();
			}	
		}
	}
	
	public static boolean isConnected(Socket socket){
	    try{
	    	/*
	    	 * 此发送紧急数据的方法在Windows环境下会出现异常
	    	 * this.pcClient.sendUrgentData(0xff);
	    	 * */
	    	socket.getOutputStream().write(heartbeatMsg.getBytes());
	    	return true;
	    }catch(Exception e){
	    	System.out.println("客户端: " + socket.getInetAddress() + "已经断开");
	        return false;
	    }
    }
}