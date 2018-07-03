<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>权限错误</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="backstage/css/list.css" type="text/css">
<link rel="stylesheet" href="backstage/css/table.css" type="text/css" >
</HEAD>
<body> 
  
<div class="tipModule" >
    <div class="tipModule_head">
        <span class="error">错误</span>
    </div>
    <div class="tipModule_content">
    	<span><h4><c:out value="${error}" escapeXml="false"/></h4></span>
    </div>
</div>

</body>
</HTML>