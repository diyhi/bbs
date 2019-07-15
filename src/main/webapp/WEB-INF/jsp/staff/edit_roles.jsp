<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改角色</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">

<script language="javascript" type="text/javascript">
//选择权限
function selectPermission(obj){
	var permission_index = obj.attributes['index'].nodeValue;
	var permissionId_arr = document.getElementsByName("permissionId_"+permission_index);
        
    var count = 0;
    for(var j=0;j<permissionId_arr.length;j++){
    	var permissionId = permissionId_arr[j].value;
    	
    	if(document.getElementById("sysPermissionId_"+permission_index+"_"+permissionId).checked == true){
    		count++;
    	}
    }
    if(permissionId_arr.length == count){
		document.getElementById("permissionGroup_"+permission_index).checked = true;
	}else{
		document.getElementById("permissionGroup_"+permission_index).checked = false;
	}
    
}

//全选权限
function selectAllPermission(obj){
	var permission_index = obj.value;
	var permissionId_arr = document.getElementsByName("permissionId_"+permission_index);
    for(var j=0;j<permissionId_arr.length;j++){
    	var permissionId = permissionId_arr[j].value;
    	if(obj.checked == true){
    		document.getElementById("sysPermissionId_"+permission_index+"_"+permissionId).checked = true;
    	}else{
    		document.getElementById("sysPermissionId_"+permission_index+"_"+permissionId).checked = false;
    	}
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
<form:form modelAttribute="sysRoles" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/roles/list${config:suffix()}?page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>角色名：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="name" size="40" maxlength="50" value="${roles.name }" <c:if test="${!roles.logonUserPermission}">disabled="disabled"</c:if>/>
   	<span class="span-text">${error["name"]}</span>	
   </TD>
   </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">备注：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    <input name="remarks" size="50" maxlength="50" value="${roles.remarks }" <c:if test="${!roles.logonUserPermission}">disabled="disabled"</c:if>/>
   	</TD></TR>
   	
	<TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>角色权限：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<TABLE cellSpacing="2" cellPadding="0" width="99%"  border="0">
	   		 <c:forEach items="${permissionObjectMap}" var="entry" varStatus="status">
		    	<TR>
					<TD width="100%"  height="22px" align="left" colspan="2">
						<label><input type="checkbox" id="permissionGroup_${status.index}" name="permissionGroup" value="${status.index}" onclick="selectAllPermission(this);"/>${entry.key}</label>
					</TD>
				</TR>
				<TR>
					<TD width="5%" height="26px" style="BORDER-BOTTOM: #bfe3ff 1px dotted;" >&nbsp;</TD>
					<TD width="95%" style="BORDER-BOTTOM: #bfe3ff 1px dotted;line-height: 22px" align="left">
					<c:forEach items="${entry.value}" var="entryValue">
						<input type="hidden" name="permissionId_${status.index}" value="${entryValue.permissionId}">
						<label><input type="checkbox" id="sysPermissionId_${status.index}_${entryValue.permissionId}" name="sysPermissionId" value="${entryValue.permissionId}" <c:if test="${!entryValue.logonUserPermission}"> disabled="disabled"</c:if> <c:if test="${entryValue.selected}"> checked="checked"</c:if> onclick="selectPermission(this);" index="${status.index}"/>${entryValue.remarks}</label>
					</c:forEach>
					</TD>
				</TR>	
	    	</c:forEach>
	    </TABLE>
   	</TD></TR>	
	<tr>
    <TD class="t-button" colSpan="4">
    	<span class="span-text">${error["id"]}</span>	
    	<span class="span-text">${error["permission"]}</span>	
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
<script language="javascript" type="text/javascript">
//选中权限组
function selectedPermissionGroup(){
	var permissionGroup = document.getElementsByName("permissionGroup");
	for(var i=0;i<permissionGroup.length;i++){
        var permission_index = permissionGroup[i].value;
        
        var permissionId_arr = document.getElementsByName("permissionId_"+permission_index);
        
        var count = 0;
        for(var j=0;j<permissionId_arr.length;j++){
        	var permissionId = permissionId_arr[j].value;
        	
        	if(document.getElementById("sysPermissionId_"+permission_index+"_"+permissionId).checked == true){
        		count++;
        	}
        }
		if(permissionId_arr.length == count){
			document.getElementById("permissionGroup_"+permission_index).checked = true;
		}
    }
} 
selectedPermissionGroup();
</script>
</BODY></HTML>
