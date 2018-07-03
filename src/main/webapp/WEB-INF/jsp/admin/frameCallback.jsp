<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>框架回调页面</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
</head>
  
<body>
<div class="layui-layer layui-anim-01 layui-layer-dialog layui-layer-border layui-layer-msg" id="layui-layer2" type="dialog" contype="string" style="z-index: 19891016; top: 200px; left: 47%;"><div class="layui-layer-content layui-layer-padding"><i class="layui-layer-ico layui-layer-ico1"></i>提交完成</div><span class="layui-layer-setwin"></span></div>
</body>
<script language="javascript" type="text/javascript"> 
//回调
function callback(jumpStatus){ 
	  
	if(jumpStatus == -10){
		//页面回调
		window.parent.callbackFrame();
	}
	if(jumpStatus == -12){
		//刷新主框页面
		window.parent.refreshs();
		
		//页面回调
		window.parent.callbackFrame();
	}
}
setTimeout(function(){callback(${jumpStatus});},3000);//延迟3秒
</script>
</html>