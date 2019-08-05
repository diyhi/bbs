<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改等级</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script language="javascript" type="text/javascript">


function sureSubmit(objForm){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	objForm.submit();
} 
</script>
  
</HEAD>
<BODY>
<form:form modelAttribute="userGrade" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userGrade/list${config:suffix()}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>等级名称：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input class="form-text" name="name" size="30" maxlength="45" value="${userGrade.name}"/>&nbsp;&nbsp;
    	<span class="span-text">${error['name']}</span>
    	
    	<form:errors path="name" class="span-text"/>
   		</TD></TR>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>需要积分：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input class="form-text" name="needPoint" size="10" maxlength="10" value="${userGrade.needPoint}"/>
    	&nbsp;&nbsp;<span class="span-text">${error['needPoint']}</span>
    	</TD>
  </TR>
	<tr>
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>
