<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>结果页</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="backstage/css/list.css" type="text/css">
<link rel="stylesheet" href="backstage/css/table.css" type="text/css" >
</HEAD>
<body> 
  
<div class="tipModule" >
    <div class="tipModule_head">
        <span class="prompt">提示</span>
    </div>
    <div class="tipModule_content">
    	<span><h4><c:out value="${message}" escapeXml="false"/></h4></span>
    </div>
    
    <c:if test="${urladdress != null && urladdress != ''}">
    <div class="tipModule_bottom">
    	<span class="submitButton">
    		<!-- 路径上不能同时出现两个左斜杆,否则spring 5.0.4之后版本会报错 The request was rejected because the URL was not normalized. -->
    		<input type="button"  value="确 定" onclick="javascript:window.location.href='${config:url(pageContext.request)}${fn:substring(urladdress, 1, -1)}'">
    		
    		<!-- 
			<input type="button"  value="确 定" onclick="javascript:window.location.href='${config:url(pageContext.request)}${urladdress}'">-->
		</span>
	</div>
	</c:if>
</div>

</body>
</HTML>