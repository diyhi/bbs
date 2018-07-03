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

<script language="javascript" type="text/javascript">

</script> 
<BODY>

<DIV class="d-box">

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="15%">职位：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	${sysUsers.userDuty }
		    </TD>
	    </TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">最后5次登录日志：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
	    		<table style="width:100%;" >
	    			<thead>
		    			<tr>
		    				<th width="20%" height="26px" align="left">登录IP</th>
		    				<th width="30%" align="left">IP归属地</th>
		    				<th align="left">登录时间</th>
		    			</tr>
	    			</thead>
				    <c:forEach items="${staffLoginLogList}" var="entry" > 
					    <tr>
					  		<td style="border-top:1px dashed #BFE3FF; border-right:1px dashed #BFE3FF; line-height:24px;color: #999; text-align:left;">
					  			${entry.ip}
					    	</td>
					    	<td style="border-top:1px dashed #BFE3FF; line-height:24px;color: #999; text-align:left;">
					    		${entry.ipAddress}
					    	</td>
					    	<td style="border-top:1px dashed #BFE3FF; line-height:24px;color: #999; text-align:left;">
					    		<fmt:formatDate value="${entry.logonTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>
					    	</td>
					    </tr>
				    
				    
				   
					</c:forEach>
				</table>
		    </TD>
		</TR>
  		
	</TBODY>
</TABLE>
</DIV>
</BODY></HTML>