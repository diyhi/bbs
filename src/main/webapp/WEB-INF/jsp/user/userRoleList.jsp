<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户角色列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">
//删除用户角色
function deleteRole(id){
	var parameter = "";
	parameter += "&id="+id;
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
   	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("删除失败");
		}
	},
		"${config:url(pageContext.request)}control/userRole/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);

}

//设为默认
function setAsDefault(id){

	var parameter = "";

	parameter += "&id="+id;
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
		
   	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("设置失败");
		}
	},
		"${config:url(pageContext.request)}control/userRole/manage${config:suffix()}?method=setAsDefault&timestamp=" + new Date().getTime(), true,parameter);
}
</script>

</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userRole/manage${config:suffix()}?method=add'" value="添加角色">
</div>
 
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>角色名称</TH>
    <TH>排序</TH>
    <TH>默认角色</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${userRoleList}" var="entry">
	  <TR >
	    <TD width="30%">${entry.name }</TD>
	    <TD width="20%">
	    	<c:if test="${entry.defaultRole eq true}"><s style="color: red;"><span style="color: #333333;">${entry.sort}</span></s></c:if> 
	    	<c:if test="${entry.defaultRole eq false}">${entry.sort}</c:if>
	    </TD>
	    <TD width="35%">
	    	<c:if test="${entry.defaultRole eq true}"><span style="color: green;">是</span></c:if>
			<c:if test="${entry.defaultRole eq false}">否&nbsp;<a href="#" onclick="javascript:setAsDefault('${entry.id}');return false;" hidefocus="true" ondragstart= "return false">[设为默认]</a></c:if>
	    </TD>
	    <TD width="15%">
	    	<a href="${config:url(pageContext.request)}control/userRole/manage${config:suffix()}?method=edit&userRoleId=${entry.id }">修改</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteRole('${entry.id }');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
</form:form>
</DIV></BODY></HTML>
