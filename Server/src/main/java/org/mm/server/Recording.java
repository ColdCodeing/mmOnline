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
		//���������ļ�
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
	    //�������
        if (pictureSocket == null)
            return;
		InputStream inputStream = pictureSocket.getInputStream();
		DataInputStream inputData = new DataInputStream(inputStream);
        //���Լ����������socket�ж��������������
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        long picLeng;
		
		while(true){
	        //��־ͷ״̬
            boolean isHead = true;
            //ѭ����ȡ4���ֽ�
            for (int i = 0; i < PICTURE_PACKAGE_HEAD.length; ++i) {
                byte head = (byte) inputStream.read();
                //û�ж�ȡ��
                if (head != PICTURE_PACKAGE_HEAD[i]) {
                	isHead = false;
                    break;
                }
            }
            if (isHead) {   		               
                //��ȡͼƬ���ݳ���
                picLeng = inputData.readLong();
                int len = -1;
                //ѭ����ȡ
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
