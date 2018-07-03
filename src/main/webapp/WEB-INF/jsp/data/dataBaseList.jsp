<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>数据还原列表</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet"></HEAD>

<BODY>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/dataBase/manage${config:suffix()}?method=backup'" value="数据备份">
</div>


<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>文件名称</TH>
    <TH>文件大小</TH>
    <TH>数据库版本</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
  	<c:forEach items="${dataBaseFileList}" var="entry">
	  <TR >
	    <TD width="30%">${entry.fileName}</TD>
	    <TD width="30%">${entry.fileSize}</TD>
	    <TD width="20%">${entry.version}</TD>
	    <TD width="20%" ><a href="${config:url(pageContext.request)}control/dataBase/manage${config:suffix()}?method=reset&dateName=${entry.fileName}">还原</a></TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
</form:form>
</DIV></BODY></HTML>
