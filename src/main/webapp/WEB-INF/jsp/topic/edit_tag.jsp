<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改标签</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<form:form modelAttribute="tag" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/tag/list${config:suffix()}'" value="返回">
</div>
<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>标签名称：</TD>
    <TD class=t-content width="88%" colSpan=3><form:input class="form-text" path="name" maxlength="255" size="50"/>
	    &nbsp;<web:errors path="name" cssClass="span-text"/>
    </TD></TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">排序：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<form:input class="form-text" path="sort" maxlength="20" size="10" />
    	&nbsp;<web:errors path="sort" cssClass="span-text"/>
    	<SPAN class="span-help">数字越大越在前</SPAN>
    </TD>
  </TR>
	<tr>
    <TD class="t-button" colSpan="4">
       <span class="submitButton"><INPUT type="submit" value="提交" ></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>
