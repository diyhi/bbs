<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户详细</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

<script language="javascript" type="text/javascript">

//提交积分
function submitPoint(type){
	var parameter = "";
	
	parameter += "&id="+getUrlParam("id");
	
	//增加/减少预存款符号
	parameter += "&point_symbol="+encodeURIComponent(encodeURIComponent(document.getElementById("point_symbol").value));
	//预存款
	parameter += "&point="+encodeURIComponent(encodeURIComponent(document.getElementById("point").value));
	//备注
	var remark = document.getElementById("point_remark").value;
	if(remark != "备注内容"){
		parameter += "&point_remark="+encodeURIComponent(encodeURIComponent(remark));
	}
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	post_request(function(value){
		//清除所有错误显示
		var errorTag_object = getElementsByName_pseudo("span", "errorTag");
		for(i = 0; i < errorTag_object.length; i++) {	
			if(errorTag_object[i].id != ""){
				document.getElementById(errorTag_object[i].id).innerHTML = "";
			}	
		}
		
		if(value != ""){
			var returnValue = JSON.parse(value);//返回JSON信息.Map<String,Object>格式
			for(message in returnValue){	
				if(message == "error"){
					var error = returnValue[message];
					for(e in error){
						document.getElementById("error_"+e).innerHTML = error[e];
					}
			    }else if(message == "success"){
			    	if(returnValue[message] == "true"){
			    		document.getElementById("success_point").innerHTML = "提交成功,3秒后自动刷新本页";
			    		refreshURL();
			    	}
			    	
			    }
			}
		}
	},
		"${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=payment&type="+type+"&timestamp=" + new Date().getTime(), true,parameter);

} 

//刷新URL   
function refreshURL() { 
	setTimeout(function(){
		window.location.reload();
	},3000);     
} 

</script>
  
</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<form:form modelAttribute="user" method="post" >
<input type="hidden" name="userVersion" value="${user.userVersion}">
<DIV class="d-box">
<div class="d-button">
	<c:if test="${param.jumpStatus == null || param.jumpStatus == '' || param.jumpStatus >0}">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/list${config:suffix()}?queryState=${param.queryState}&page=${param.userPage}'" value="返回">
	</c:if>
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allTopic&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=10'" value="发表的话题">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allComment&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=20'" value="发表的评论">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allReply&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=30'" value="发表的回复">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/pointLog/list${config:suffix()}?userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="积分日志">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userLoginLog/list${config:suffix()}?userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="登录日志">
</div>


<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
   <TR>
    <TD class="t-label t-label-h" width="12%">会员用户名：</TD>
    <TD class="t-content" width="38%">
    	${user.userName}
    </TD>
    <TD class="t-label t-label-h" width="12%">等级：</TD>
    <TD class="t-content" width="38%">
    	${user.gradeName}
    </TD></TR> 
    
  <TR>
    <TD class="t-label t-label-h" width="12%">Email地址：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${user.email}
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">密码提示问题：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${user.issue}
    </TD>
  </TR>
  
  <TR>
    <TD class="t-label t-label-h" width="12%">用户状态：</TD>
    <TD class="t-content" width="88%" colSpan="3">
		<c:if test="${ user.state eq 1 || user.state eq 11}">正常用户</c:if>
		<c:if test="${ user.state eq 2 || user.state eq 12}">禁止用户</c:if>
    </TD>
  </TR>
    <TD class="t-label t-label-h" width="12%">当前积分：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${user.point}
    </TD></TR>
	<TR>
    <TD class="t-label t-label-h" width="12%">备注：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${user.remarks }
    </TD>
  </TR>
	<!-- 用户自定义注册功能项 -->
	<c:forEach items="${userCustomList}" var="entry" varStatus="status">
		<TR>
	    <TD class="t-label t-label-h" width="12%">${entry.name}：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			<c:if test="${entry.chooseType ==1}">
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>${userInputValue.content}</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==2}">
				
				<c:forEach items="${entry.itemValue}" var="itemValue" varStatus="status">
					<!-- 选中项 -->
					<c:set var="_checked" value=""></c:set>
					<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
						<c:if test="${userInputValue.options == itemValue.key}">
							<c:set var="_checked" value=" checked='checked'"></c:set>
						</c:if>
					</c:forEach>
				
					<!-- 默认选第一项 -->				
					<input type="radio" name="userCustom_${entry.id}" disabled="disabled" value="${itemValue.key}" ${_checked} <c:if test="${fn:length(entry.userInputValueList)==0 && status.count == 1}"> checked='checked'</c:if>>${itemValue.value}
				</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==3}">
				<c:forEach items="${entry.itemValue}" var="itemValue">
					<c:set var="_checked" value=""></c:set>
					<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
						<c:if test="${userInputValue.options == itemValue.key}">
							<c:set var="_checked" value=" checked='checked'"></c:set>
						</c:if>
					</c:forEach>
				
				
					<input type="checkbox" name="userCustom_${entry.id}" disabled="disabled" value="${itemValue.key}" ${_checked}>${itemValue.value}
				</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==4}">
			
				<select name="userCustom_${entry.id}" disabled="disabled" <c:if test="${entry.multiple == true}"> multiple='multiple'</c:if> <c:if test="${entry.selete_size != null}"> size='${entry.selete_size}'</c:if>>
					<c:forEach items="${entry.itemValue}" var="itemValue">
						<c:set var="_selected" value=""></c:set>
						<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
							<c:if test="${userInputValue.options == itemValue.key}">
								<c:set var="_selected" value=" selected='selected'"></c:set>
							</c:if>
						</c:forEach>
					
						<option value="${itemValue.key}" ${_selected}>${itemValue.value}</option>		
					</c:forEach>	
				</select>
			</c:if>
			<c:if test="${entry.chooseType ==5}">
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>${userInputValue.content}</c:forEach>		
			</c:if>
	    </TD>
	  </TR>
	  </c:forEach>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>



