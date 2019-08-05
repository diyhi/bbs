<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>后台首页</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" type="image/x-icon" href="${config:url(pageContext.request)}backstage/images/favicon.ico" media="screen" />
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
</HEAD>

<style type="text/css">
body { margin:0 0 0 0;background:#f9fcff;}
html,body{
	height: 100%;
	margin:0;
	padding:0;
}

</style>
<BODY>

<div style="padding: 6px 7px 0px 7px;">
	<div style="background: #fff;border-radius: 2px;box-shadow: 0 1px 3px 0 rgba(0,0,0,.02),0 4px 8px 0 rgba(0,0,0,.02) ">
		<div style="margin-left: 10px;margin-right: 10px;padding-top: 4px; padding-bottom: 10px;" >
		<h2 style="font-size: 15px;color: #42a5f5;height: 20px;">最后5次登录日志</h2>
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
   			<thead style="color: #42a5f5; background-color: #ecf7fe; border-bottom: #BFE3FF 1px solid;">
    			<tr>
    				<th width="20%" height="32px" align="center">登录IP</th>
    				<th width="30%" align="center" >IP归属地</th>
    				<th width="50%" align="left" ><span style="margin-left: 36px;">登录时间</span></th>
    			</tr>
   			</thead>
		    <c:forEach items="${staffLoginLogList}" var="entry" > 
			    <tr>
			  		<td style="border-bottom:1px dashed #BFE3FF; line-height:30px;color: #999; text-align:center;">
			  			${entry.ip}
			    	</td>
			    	<td style="border-bottom:1px dashed #BFE3FF; line-height:30px;color: #999; text-align:center;">
			    		${entry.ipAddress}
			    	</td>
			    	<td style="border-bottom:1px dashed #BFE3FF; line-height:30px;color: #999; text-align:left;">
			    		<fmt:formatDate value="${entry.logonTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>
			    	</td>
			    </tr>
			</c:forEach>
		</table>
		</div>
	</div>
</div>
</BODY></HTML>