<%@ page language="java" contentType="text/html; charset=GBK"
pageEncoding="GBK" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" 
    + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>结果页</title>
</head>
<body>
	<div id="main" style="width:1000px">
	<div>
		<a>操作成功</a>
	</div>
	<div>
		<a id="back" href="<%=basePath%>list">返回</a>
	</div>
</div>
</body>
</html>