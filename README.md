# mmOnline

## *功能*

	实现对电脑的远程控制和录屏播放

## *结构* 
	服务端:Web应用和Java应用
	用户端:Java应用

![enter image description here](https://github.com/ColdCodeing/mmOnline/blob/master/system.png)


 1. 服务端    
 （1） 启动Web应用
 （2） ServletContextListener监听器启动,加载Server和Recording对象 
 （3） 等待pc端client启动
 （4） Web应用和Server通过10001端口交换信息  
 2. 用户端 
 - 主线程(main) 
 	等待接收从Server传来的命令，并执行指令 
 - 等待键盘输入线程(ReceiveInput) 
	接受键盘输入，若是“closeClient”则关闭客户端

 3. 发送命令
  (1)  Web收到浏览器请求
 （2） 若收到控制用户机的请求,解析出IP和命令,发送给Server
 （3） Server遍历所有Socket用IP找出对应的Socket对象
 （4） 用指定Socket发送命令  
 
 4. 用户机IP获得
  (1)  Web收到浏览器请求
 （2） 若收到list请求则向Server发送ALLIP指令
 （3） Server返回当前所有已登陆用户机的IP
 （4） Web解析
 5. 屏幕截图
  (1)  Web收到浏览器请求
 （2） 从Server处取得屏幕截图
 （3） 前端显示
 

 
