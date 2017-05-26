package org.mm.mail;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.mail.EmailException;  
import org.apache.commons.mail.HtmlEmail;  

import org.mm.util.Util;

  
  
/**  
 * �ʼ����͹���ʵ����  
 */  
public class MailUtil {  
	public static void sendScreenShot() throws FileNotFoundException, IOException{
		Properties pro = Util.getProperties();
		Mail mail = new Mail();  
	    mail.setHost(pro.getProperty("mailHost")); // �����ʼ�������  
	    mail.setSender(pro.getProperty("mailSender"));  
	    mail.setReceiver(pro.getProperty("mailReceiver")); // ������  
	    mail.setUsername(pro.getProperty("mailUsername")); // ��¼�˺�  
	    mail.setPassword(pro.getProperty("mailPassword")); // ����������ĵ�¼����  
	    mail.setSubject("ScreenShot");  

	    
	    mail.setMessage(Util.getDate());
	    mail.setAttachment(pro.getProperty("RobotWorkPlace")+"/screen.jpg", "", "");
	    new MailUtil().send(mail);
	}
	
    @SuppressWarnings("deprecation")
	public boolean send(Mail mail) {  
        // ����email  
        HtmlEmail email = new HtmlEmail();  
        try {  
            // ������SMTP���ͷ����������֣�163�����£�"smtp.163.com"  
            email.setHostName(mail.getHost());  
            // �ַ����뼯������  
            email.setCharset(Mail.ENCODEING);  
            // �ռ��˵�����  
            email.addTo(mail.getReceiver());  
            email.setSSL(true);
            email.setSslSmtpPort("465"); // �趨SSL�˿�
            // �����˵�����  
            email.setFrom(mail.getSender(), mail.getName());  
            // �����Ҫ��֤��Ϣ�Ļ���������֤���û���-���롣�ֱ�Ϊ���������ʼ��������ϵ�ע�����ƺ�����  
            email.setAuthentication(mail.getUsername(), mail.getPassword());  
            // Ҫ���͵��ʼ�����  
            email.setSubject(mail.getSubject());  
            // Ҫ���͵���Ϣ������ʹ����HtmlEmail���������ʼ�������ʹ��HTML��ǩ  
            email.setMsg(mail.getMessage());  
            //��Ӹ���
            if(mail.getAttachment()!=null)
            	email.attach(mail.getAttachment());
            // ����  
            email.send();  
            System.out.println(mail.getSender() + " �����ʼ��� " + mail.getReceiver());  
            return true;  
        } catch (EmailException e) {  
            e.printStackTrace();  
            System.out.println(mail.getSender() + " �����ʼ��� " + mail.getReceiver()  
                    + " ʧ��");  
            return false;  
        }  
    }  
  
}  