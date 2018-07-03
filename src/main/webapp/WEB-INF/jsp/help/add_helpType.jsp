<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加帮助分类</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
</HEAD>
<BODY>

<form:form modelAttribute="helpType" method="post" >
<DIV class="d-box">
<div class="d-button" style="height:28px;margin-left: auto; margin-right: auto;line-height: 28px;overflow: hidden ">
	<span style="font-weight:bold;float:left;">&nbsp;分类导航:</span>
	<span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/helpType/list${config:suffix()}?parentId=0">全部分类</a></span>
	<span style="float:left">
	&nbsp;
		<c:forEach items="${navigation}" var="navigationMap" varStatus="status">
			>>&nbsp;<a href="${config:url(pageContext.request)}control/helpType/list${config:suffix()}?parentId=${navigationMap.key}">${navigationMap.value}</a>
		</c:forEach>
	</span>
</div>
<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%">父类名称：</TD>
    <TD class="t-content" width="88%" colSpan=3>
    	${parentName } <c:if test="${parentHelpType.childNodeNumber == 0}"><SPAN class="span-text">(添加本分类后，上级分类帮助会自动转移到本分类)</SPAN></c:if>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>分类名称：</TD>
    <TD class=t-content width="88%" colSpan=3><form:input path="name" maxlength="255" size="50" />
	    &nbsp;<web:errors path="name" cssClass="span-text"/>
    </TD></TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">排序：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<form:input path="sort" maxlength="20" size="10" />
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
