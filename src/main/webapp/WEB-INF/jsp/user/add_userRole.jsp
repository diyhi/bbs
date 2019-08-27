<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加角色</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script language="javascript" type="text/javascript">

//选择资源组
function selectResourceGroup(obj){
	var index = obj.value;
	var resourceCode_arr = document.getElementsByName("resourceCode");
	for(var j=0;j<resourceCode_arr.length;j++){
    	var resourceCode_index = resourceCode_arr[j].attributes['index'].nodeValue;
    	if(parseInt(resourceCode_index) == parseInt(index)){
    		if(obj.checked == true){
    			resourceCode_arr[j].checked = true;
    		}else{
    			resourceCode_arr[j].checked = false;
    		}
    	}
    }
}

//选择资源
function selectResource(obj){
	var index = obj.attributes['index'].nodeValue;

	var resourceCode_arr = document.getElementsByName("resourceCode");
	var index_count = 0;
	var checked_count = 0;
	
	for(var j=0;j<resourceCode_arr.length;j++){
    	var resourceCode_index = resourceCode_arr[j].attributes['index'].nodeValue;
    	if(parseInt(resourceCode_index) == parseInt(index)){
    		index_count++;
    		if(resourceCode_arr[j].checked == true){
    			checked_count++;
    		}
    	}
    }
	
	if(index_count == checked_count){
		document.getElementById("resourceGroup_"+index).checked = true;
	}else{
		document.getElementById("resourceGroup_"+index).checked = false;
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
<form:form modelAttribute="userGrade" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userRole/list${config:suffix()}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>角色名称：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<input class="form-text" name="name" size="40" maxlength="50" value="${userRole.name }"/>
		    	<span class="span-text">${error["name"]}</span>	
		   	</TD>
	   	</TR>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>排序：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<input class="form-text" name="sort" size="8" maxlength="8" value="${userRole.sort}"/>
		    	<span class="span-text">${error["sort"]}</span>	
		    	<span class="span-help">如果选中"默认角色" 则优先级最高，本排序参数无效</span>
		   	</TD>
	   	</TR>
	   	<TR>
		    <TD class="t-label t-label-h" width="12%">权限：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<TABLE cellSpacing="2" cellPadding="0" width="99%"  border="0">
			   		 <c:forEach items="${userResourceGroupList}" var="userResourceGroup" varStatus="status">			    	
				    	<TR>
							<TD width="100%"  height="24px" align="left" colspan="2">
								<label><input type="checkbox" id="resourceGroup_${status.index}" <c:if test="${userResourceGroup.selected}"> checked="checked"</c:if>  value="${status.index}" onclick="selectResourceGroup(this);"/>${userResourceGroup.name}</label>
								&nbsp;&nbsp;<c:if test="${userResourceGroup.type == 20}"><span class="span-help">${userResourceGroup.tagName}</span></c:if>
							</TD>
						</TR>
						<TR>
							<TD width="5%" height="26px" style="BORDER-BOTTOM: #bfe3ff 1px dotted;" >&nbsp;</TD>
							<TD width="95%" style="BORDER-BOTTOM: #bfe3ff 1px dotted;line-height: 22px" align="left">
							<c:forEach items="${userResourceGroup.userResourceList}" var="userResource">
								<label><input type="checkbox" name="resourceCode" value="${userResourceGroup.code}_${userResourceGroup.tagId}_${userResource.code}" <c:if test="${userResource.selected}"> checked="checked"</c:if> onclick="selectResource(this);" index="${status.index}"/>${userResource.name}</label>
							</c:forEach>
							</TD>
						</TR>	
			    	</c:forEach>
			    </TABLE>
		    	
		    	
		    	<span class="span-text">${error["userResourceGroup"]}</span>	
		    	<br><span class="span-help">如果默认角色允许‘查看话题内容’，则未登录用户也可以查看</span>
		    	
		   	</TD>
	   	</TR>
		<TR>
		    <TD class="t-button" colSpan="4">
		        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
		  	</TD>
		</TR>
	</TBODY>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
