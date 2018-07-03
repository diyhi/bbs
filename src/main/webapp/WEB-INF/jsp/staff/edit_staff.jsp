<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改员工</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">

<script language="javascript" type="text/javascript">
//显示角色
function showRoles(obj){
	if(obj.value == "true"){
		document.getElementById("roles_tr").style.display = "none";
	}else{
		document.getElementById("roles_tr").style.display = "";
	}
} 
function sureSubmit(objForm){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	objForm.submit();
} 
</script>
  
</HEAD>
<BODY>
<form:form modelAttribute="sysUsers" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/staff/list${config:suffix()}?page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>账号：</TD>
    <TD class="t-content" width="88%" colSpan="3">${sysUsers.userAccount}</TD>
   </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>姓名：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="fullName" size="20" maxlength="20"  value="${sysUsers.fullName}" <c:if test="${!sysUsers.logonUserPermission}">disabled="disabled"</c:if> />
   </TD>
   </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>密码：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="userPassword" type="password" size="20" maxlength="20"  <c:if test="${!sysUsers.logonUserPermission}">disabled="disabled"</c:if>/>
    &nbsp;
    <span class="span-text">${error["userPassword"]}</span>	
    <SPAN class="span-help">不修改请留空 </SPAN>
   	</TD></TR>
   <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>重复密码：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="repeatPassword" type="password" size="20" maxlength="20" <c:if test="${!sysUsers.logonUserPermission}">disabled="disabled"</c:if>/>
    &nbsp;<SPAN class="span-help">不修改请留空 </SPAN>
   	</TD></TR>
   	<TR>
	    <TD class="t-label t-label-h" width="12%">超级管理员：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<label><input type="radio" name="issys" value="true" <c:if test="${sysUsers.issys}"> checked="checked"</c:if> <c:if test="${!isSysAdmin}"> disabled="disabled"</c:if> onclick="showRoles(this);"/>是</label>
	    	<label><input type="radio" name="issys" value="false" <c:if test="${!sysUsers.issys}"> checked="checked"</c:if> <c:if test="${!isSysAdmin}"> disabled="disabled"</c:if> onclick="showRoles(this);"/>否</label>
	   	</TD>
	</TR>
	<TR>
    <TD class="t-label t-label-h" width="12%">备注：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="userDesc" size="50" maxlength="50"  value="${sysUsers.userDesc }" <c:if test="${!sysUsers.logonUserPermission}">disabled="disabled"</c:if>/>
   	</TD></TR>
   	<TR>
    <TD class="t-label t-label-h" width="12%">职位：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="userDuty" size="50" maxlength="50"  value="${sysUsers.userDuty }" <c:if test="${!sysUsers.logonUserPermission}">disabled="disabled"</c:if>/>
   	</TD></TR>
   	<TR>
    <TD class="t-label t-label-h" width="12%">是否使用：</TD>
    <TD class="t-content" width="88%" colSpan="3">
	    <label><input name="enabled" type="radio" value="true" checked="checked" <c:if test="${sysUsers.enabled}"> checked="checked"</c:if> <c:if test="${!sysUsers.logonUserPermission}"> disabled="disabled"</c:if>/>是</label>
	    <label><input name="enabled" type="radio" value="false" <c:if test="${!sysUsers.enabled}"> checked="checked"</c:if> <c:if test="${!sysUsers.logonUserPermission}"> disabled="disabled"</c:if>/>否</label>
   	</TD></TR>
	<TR id="roles_tr" style="<c:if test="${sysUsers.issys == true}"> display: none</c:if>">
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>用户角色：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<c:forEach items="${sysRolesList}" var="entry">
		    <label><input type="checkbox" name="sysRolesId" value="${entry.id}" <c:if test="${!entry.logonUserPermission}"> disabled="disabled"</c:if> <c:if test="${entry.selected}"> checked="checked"</c:if>/>${entry.name}</label>
	    	<br>
	    </c:forEach>
   	</TD></TR>
	
	<tr>
    <TD class="t-button" colSpan="4">
    	<span class="span-text">${error["permission"]}</span>	
    	<span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>
