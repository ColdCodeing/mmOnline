package org.mm.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.mm.mail.MailUtil;



public class Util {
	//��ȡ�����ļ�
	public static Properties getProperties() 
			throws FileNotFoundException, IOException{
		Properties pro = new Properties();
		pro.load(new FileInputStream("JARVIS.properties"));
		return pro;
	}
	//��ȡ��ǰʱ��
	public static String getDate() 
			throws FileNotFoundException, IOException {
		Properties pro = null;
		String date = null;
		pro = getProperties();
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				pro.getProperty("dateFormat"));
		date = sdf.format(d);
		return date;
	}
	//��ͼ
	public static void screenShot() throws IOException, AWTException {
			Properties pro = getProperties();
			String filePath = pro.getProperty("RobotWorkPlace")+"/screen.jpg";
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			BufferedImage bim = new Robot().createScreenCapture(
					new Rectangle(0, 0, dim.width, dim.height));
			ImageIO.write(bim, "jpg", new File(filePath));
			//�ʼ����ͽ�ͼ
			MailUtil.sendScreenShot();
	}

	//�ػ�
	public static void shutdown() throws IOException, AWTException{
		Runtime.getRuntime().exec("shutdown -s -t 60");
		screenShot();
	}
	
	public static void recording() throws IOException{
		Properties pro = getProperties();
		String serverIP = pro.getProperty("serverIP");
		String recordingPort = pro.getProperty("recordingPort");;
		new Thread(new SendImage(serverIP, recordingPort)).start();
	}
}
//��ͼ�����͸�Server
class SendImage implements Runnable{

	private String ip = null;
	private int port = 1;
	private double scale = 0.55;
	public final static byte[] PICTURE_PACKAGE_HEAD = {(byte) 0xFF, (byte) 0xCF,
	            (byte) 0xFA, (byte) 0xBF,};
	
	public SendImage(String ip, String port) {
		this.ip = ip;
		this.port = Integer.parseInt(port);
	}
	
	@Override
	public void run() {
		//Socket����
		@SuppressWarnings("resource")
		Socket client = new Socket();
		DataOutputStream out = null;
		try {
			System.out.println("��������");
			client.connect(new InetSocketAddress(InetAddress.getByName(this.ip), this.port));
			System.out.println("���ӳɹ�");
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		//��Ļ��ͼ
		Robot robot = null;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ByteArrayOutputStream os=new ByteArrayOutputStream();
        //gc����
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
        		.getDefaultScreenDevice().getDefaultConfiguration();
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		Rectangle rc =  new Rectangle(0, 0, dim.width, dim.height);
		BufferedImage bim = robot.createScreenCapture(rc);
		BufferedImage bufferedImage = null;
		
		//��Ļ����������
		int width = (int) (bim.getWidth() * scale); 
		int height = (int)(bim.getHeight() * scale);
		bufferedImage = gc.createCompatibleImage(width, 
				height, Transparency.OPAQUE);
		while(true){
			try {	
				//��ͼ֮������
				Image image =  robot.createScreenCapture(rc).getScaledInstance(
						width, height, Image.SCALE_SMOOTH);
				//image ת��bufferImage
				Graphics graphics = bufferedImage.createGraphics(); 
				graphics.drawImage(image, 0, 0, null);
			    graphics.dispose(); 
				//��JPEGѹ������
		        ImageIO.write(bufferedImage, "jpg", os);
		        int length = os.toByteArray().length;
		        //����ͼƬͷ
				out.write(PICTURE_PACKAGE_HEAD);
				//����ͼƬ���ݳ���
				out.writeLong(length);
				//����ͼƬ
				out.write(os.toByteArray(), 0, length);
	    		os.reset();
			}catch (IOException e) {
				e.printStackTrace();
				return;
			} 
		}
	}
	
}
