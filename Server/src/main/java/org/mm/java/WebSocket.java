package org.mm.java;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class WebSocket {
	private static Socket socket = null;
	private static Properties pro = null;
	
	static{
		//加载配置文件
		pro = new Properties();
		try {
			pro.load(WebSocket.class.getResourceAsStream("WebServer.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 连接Server
	 * @throws Exception 连接Server异常
	 */
	@SuppressWarnings("static-access")
	public WebSocket() throws Exception{
		try {
			this.socket = new Socket(InetAddress.getByName(
					pro.getProperty("serverIP")), Integer.parseInt(pro.getProperty("webPort")));
		} catch (UnknownHostException e) {
			throw new Exception("host错误");
		} catch (IOException e) {
			throw new Exception("连接服务器localhost:" + pro.getProperty("webPort"));
		}
	}
	/**
	 * 
	 * @param Msg 需要发送的信息
	 * @return 成功返回true
	 */
	public boolean SendMsg(String Msg){
		if(socket == null)
			return false;
		try {
			OutputStream  out = socket.getOutputStream();
			out.write(Msg.getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return 用户机IP列表
	 */
	public ArrayList<String> RequestClientIP(){
		ArrayList<String> res = new ArrayList<String>();
		//向Server发送ALLIP命令,Server将返回已登录用户机IP列表
		if(SendMsg("ALLIP\n\n") == false)
			return null;
		InputStream is = null;
		
		try {
			is = socket.getInputStream();
		} catch (IOException e) {
			System.out.println("请求IP失败");
			e.printStackTrace();
			return null;
		}
		ArrayList<Byte> ByteList = new ArrayList<Byte>();
		//接收来自Server的消息,收到'/'消息结束
		byte readByte = 0;
		do {  
            try {
				readByte = (byte) is.read();
			} catch (IOException e) {
				return null;
			}  
            if(readByte == -1)
            	break;
            ByteList.add(Byte.valueOf(readByte));  
        } while (readByte != '/'); 	 
		//转化为字符串
        byte[] tmpByteArr = new byte[ByteList.size()]; 
        for (int i = 0; i < ByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) ByteList.get(i)).byteValue();  
        }  
        ByteList.clear(); 
		String tmpStr = new String(tmpByteArr);
		//配合正则表达式解析全部IP,构成IP列表
		String[] msgArray = tmpStr.split("\n"); 
		for(int i = 0;i < msgArray.length; i++){
			if(isIP(msgArray[i]))
				res.add(msgArray[i] + ".ip");
		}
		return res;
	}
	
    /** 
     * 检查是否为IP 
     */  
	 public boolean isIP(String addr)  
     {  
         if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  
         {  
             return false;  
         }  
         //正则判断
         String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"
         		+ "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  
         Pattern pat = Pattern.compile(rexp);    
         Matcher mat = pat.matcher(addr);    
         boolean ipAddress = mat.find();  
         return ipAddress;  
     }  
	
}
















