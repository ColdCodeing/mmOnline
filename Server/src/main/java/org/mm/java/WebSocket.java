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
		//���������ļ�
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
	 * ����Server
	 * @throws Exception ����Server�쳣
	 */
	@SuppressWarnings("static-access")
	public WebSocket() throws Exception{
		try {
			this.socket = new Socket(InetAddress.getByName(
					pro.getProperty("serverIP")), Integer.parseInt(pro.getProperty("webPort")));
		} catch (UnknownHostException e) {
			throw new Exception("host����");
		} catch (IOException e) {
			throw new Exception("���ӷ�����localhost:" + pro.getProperty("webPort"));
		}
	}
	/**
	 * 
	 * @param Msg ��Ҫ���͵���Ϣ
	 * @return �ɹ�����true
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
	 * @return �û���IP�б�
	 */
	public ArrayList<String> RequestClientIP(){
		ArrayList<String> res = new ArrayList<String>();
		//��Server����ALLIP����,Server�������ѵ�¼�û���IP�б�
		if(SendMsg("ALLIP\n\n") == false)
			return null;
		InputStream is = null;
		
		try {
			is = socket.getInputStream();
		} catch (IOException e) {
			System.out.println("����IPʧ��");
			e.printStackTrace();
			return null;
		}
		ArrayList<Byte> ByteList = new ArrayList<Byte>();
		//��������Server����Ϣ,�յ�'/'��Ϣ����
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
		//ת��Ϊ�ַ���
        byte[] tmpByteArr = new byte[ByteList.size()]; 
        for (int i = 0; i < ByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) ByteList.get(i)).byteValue();  
        }  
        ByteList.clear(); 
		String tmpStr = new String(tmpByteArr);
		//���������ʽ����ȫ��IP,����IP�б�
		String[] msgArray = tmpStr.split("\n"); 
		for(int i = 0;i < msgArray.length; i++){
			if(isIP(msgArray[i]))
				res.add(msgArray[i] + ".ip");
		}
		return res;
	}
	
    /** 
     * ����Ƿ�ΪIP 
     */  
	 public boolean isIP(String addr)  
     {  
         if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  
         {  
             return false;  
         }  
         //�����ж�
         String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"
         		+ "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  
         Pattern pat = Pattern.compile(rexp);    
         Matcher mat = pat.matcher(addr);    
         boolean ipAddress = mat.find();  
         return ipAddress;  
     }  
	
}
















