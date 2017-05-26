package org.mm.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


public class Recording extends Thread{

	private static Properties pro = null;
	private static byte[] image = null;
	public final static byte[] PICTURE_PACKAGE_HEAD = {(byte) 0xFF, (byte) 0xCF,
	            (byte) 0xFA, (byte) 0xBF,};

	
	public Recording() throws IOException{
		//加载配置文件
		pro = new Properties();
		pro.load(Server.class.getResourceAsStream("Server.properties"));
	}
	
	@SuppressWarnings("resource")
	public void run() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(Integer.parseInt(pro.getProperty("recordingPort")));
		} catch (NumberFormatException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Socket pictureSocket = null;
		while(true){
	    	try {
				pictureSocket = socket.accept();
				receivePicture(pictureSocket);
	    	} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
    }
	
	public void receivePicture(Socket pictureSocket) throws IOException {
	    //参数检查
        if (pictureSocket == null)
            return;
		InputStream inputStream = pictureSocket.getInputStream();
		DataInputStream inputData = new DataInputStream(inputStream);
        //用自己输出流将从socket中读出来的数据输出
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        long picLeng;
		
		while(true){
	        //标志头状态
            boolean isHead = true;
            //循环读取4个字节
            for (int i = 0; i < PICTURE_PACKAGE_HEAD.length; ++i) {
                byte head = (byte) inputStream.read();
                //没有读取到
                if (head != PICTURE_PACKAGE_HEAD[i]) {
                	isHead = false;
                    break;
                }
            }
            if (isHead) {   		               
                //读取图片数据长度
                picLeng = inputData.readLong();
                int len = -1;
                //循环读取
                while (picLeng > 0
                        && (len = inputData.read(buffer, 0,
                        picLeng < buffer.length ? (int) picLeng
                                : buffer.length)) != -1) {
                    fos.write(buffer, 0, len);
                    picLeng -= len;
                }
                Recording.image = fos.toByteArray();
                fos.reset();
            }
        }
	}

	public static byte[] getImage() {
		return image;
	}
}
