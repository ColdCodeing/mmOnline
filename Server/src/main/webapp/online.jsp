<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>播放中</title>
<script src="http://cdn.static.runoob.com/libs/jquery/1.10.2/jquery.min.js">
</script>
<script>

function get_data(){
	var url = "live";
	var xhr = new XMLHttpRequest();   
    xhr.open("get", url, true);
    xhr.responseType = "blob";
    xhr.onload = function() {
        if (this.status == 200) {
            var blob = this.response;
            var img = document.createElement("img");
            img.onload = function(e) {
              window.URL.revokeObjectURL(img.src); 
            };
            img.src = window.URL.createObjectURL(blob);
　　　　　　	document.getElementsByTagName('img')[0].setAttribute('src',img.src);
 	}}
 	xhr.send();
}


setInterval("get_data()", 160);//0.120秒

</script>
</head>
<body>
<div id="imgcontainer">
	<img src=""/>
</div>
</body>
</html>