package org.mm.server;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Start implements ServletContextListener{
	private static Server  server = null;
	private static Recording recording  = null;
	public void contextDestroyed(ServletContextEvent arg0) {
		
		
	}
	/**
	 * 用于启动Server线程和Recording线程
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		if(server == null && recording ==null){
			try {
				server = new Server();
				recording = new Recording();
				server.start();
				recording.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
