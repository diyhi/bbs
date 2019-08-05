<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>查看帮助</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>
<form:form>
<input type="hidden" id="helpTypeId" value="${param.helpTypeId}">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/help/list${config:suffix()}?visible=${param.visible}&page=${param.page}'" value="返回">
</div>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR class="noDiscolor">
    <TH>${help.name}</TH>
  </TR></THEAD>
  <TBODY class="t-list-tbody">
    <TR>
    <TD class="t-content" height="28px">
    <enhance:out escapeXml="false">
    	${help.content}
    </enhance:out>    	
    </TD>
   </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>