<%@ page language="java" contentType="text/html; charset=GBK"
pageEncoding="GBK" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>������</title>
</head>
<body>
<div id="main" style="width:1000px">
	<table id="menu" style="font-size: 15px">
		<c:forEach items="${clientList}" var="client">
		<tr >
			<td width="80">
            	<a>${client}</a>           
			</td>
			<td width="60">
				<a id="close" href="shutdown/${client}">�ر�</a>
			</td>
			<td width="60">
				<a id="screen" href="screen/${client}">��ͼ</a>
			</td>
			<td width="60">
				<a id="online" href="recording/${client}">¼��</a>
			</td>
		</tr>
		</c:forEach>
	</table>
</div>
</body>
</html>