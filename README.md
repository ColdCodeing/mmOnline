# mmOnline

## *功能*

	实现对电脑的远程控制和录屏播放

## *结构*
	服务端:Web应用和Java应用
	用户端:Java应用

![enter image description here](https://github.com/ColdCodeing/mmOnline/blob/master/system.png)


 ######服务端
 1. 启动Web应用
 2. ServletContextListener监听器启动, 加载Server和Recording对象(会启动三个线程ScheduledExecutorClient, WebMonitor, Recoring)
 3. 等待pc端client启动
 4. Web应用和Server通过10001端口交换信息

ScheduledExecutorClient线程定时给所有用户机发送心跳信息, 检查是否离线
WebMonitor用于接收Web命令, 发送至用户机
Recoring用于建立屏幕截图的传输

 ######用户端
 - 主线程(main)
 	等待接收从Server传来的命令，并执行指令
 - 等待键盘输入线程(ReceiveInput)
	接受键盘输入，若是“closeClient”则关闭客户端
 - Recording线程
 	收到录屏指令时, 线程启动, 请求连接Server, 建立连接后持续发送截图至Server

######发送命令
 1. Web收到浏览器请求
 2. 若收到控制用户机的请求,解析出IP和命令,发送给Server
 3. Server遍历所有Socket用IP找出对应的Socket对象
 4. 用指定Socket发送命令

######用户机IP获得
1. Web收到浏览器请求
2. 若收到list请求则向Server发送ALLIP指令
3. Server返回当前所有已登陆用户机的IP
4. Web解析用于前端显示

######屏幕截图
1. Web收到浏览器请求,命令发送至Server
2. server与client Recoring部分建立链接
3. client发送屏幕截图至Server, 截图一帧传送一帧无缓存
4. Server每次接受一帧则更新最新的图像, 不建立缓存队列
5. 前端获得最新的图像, 显示


##还没有解决的问题
前端显示层采用一个定时器, 间隔200ms发起一次更新图像的请求, 因为存在延迟抖动可能会导致后请求的图像会先到达浏览器。这会导致图像显示的跳跃。
可能的解决办法:
* 定时发起请求, 维持一个一定长度的队列对返回的多张图像进行排序, 前端显示层使用定时器来更新图像, 每隔一段时间从队列中取出一张图片用来更新画面, 当队列长度小于某个值时则进入缓冲并停止更新图像。
* 使用WebSocket技术, 由服务器来主动更新图像, 服务器同时负责检查图像是否变化, 无变化则不更新, 以此减小网络需求。

