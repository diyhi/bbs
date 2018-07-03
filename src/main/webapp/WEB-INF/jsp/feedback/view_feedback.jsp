<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>查看在线留言</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
</HEAD>


<BODY>

<form:form>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/feedback/list${config:suffix()}?&start_createDate=${param.start_createDate}&end_createDate=${param.end_createDate}&page=${param.page}'" value="返回">
</div>


<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
	<TBODY>
	<TR>
	    <TD class="t-label t-label-h" width="12%">称呼：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			${feedback.name}
	    </TD>
	</TR>
	<TR>
	    <TD class="t-label t-label-h" width="12%">留言时间：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			<fmt:formatDate value="${feedback.createDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	    </TD>
	</TR>
	<TR>
	    <TD class="t-label t-label-h" width="12%">IP：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			${feedback.ip}&nbsp;&nbsp;&nbsp;&nbsp;${feedback.ipAddress}
	    </TD>
	</TR>
	<TR>
	    <TD class="t-label t-label-h" width="12%">联系方式：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			${feedback.contact}
	    </TD>
	</TR>
	<TR>
	    <TD class="t-label t-label-h" width="12%">内容：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			${feedback.content}
	    </TD>
	</TR>
	</TBODY>
</TABLE>
</DIV>
</form:form>

</BODY></HTML>
