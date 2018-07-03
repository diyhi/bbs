<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>员工列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/json3.js" type="text/javascript"></script>
</HEAD>
<script type="text/javascript">

//删除员工
function deleteStaff(id){
	var parameter = "";

	parameter += "&userId="+id;
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
   	post_request(function(value){
		if(value != ""){
			var data = JSON.parse(value);
					
			for(var returnValue in data){
				
				if(returnValue == "success"){
	       			if(data[returnValue] == "true"){
	       				window.location.reload();
	       			}
	       		}else if(returnValue == "error"){
	       			var error = data[returnValue];
	       			for(e in error){
	       				alert(error[e]);
					}
	       		}	
	       	}
		}	
	},
		"${config:url(pageContext.request)}control/staff/manage${config:suffix()}?method=deleteStaff&timestamp=" + new Date().getTime(), true,parameter);
}


</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
	<input type="button" class="functionButton"  onClick="javascript:window.location.href='${config:url(pageContext.request)}control/staff/manage${config:suffix()}?method=addStaff'" value="添加员工">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>用户名</TH>
    <TH>姓名</TH>
    <TH>角色</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR>
	    <TD width="40%">${entry.userAccount }</TD>
	    <TD width="10%">${entry.fullName }</TD>
		<TD width="30%">
			<c:if test="${entry.issys}"><span style="color: red;">超级管理员</span></c:if>
			<c:if test="${!entry.issys}">
				<c:forEach items="${rolesNameMap}" var="rolesName">
					<c:if test="${rolesName.key eq entry.userAccount}">
						
						<c:forEach items="${rolesName.value}" var="rolesName_entry">
							${rolesName_entry }<br>
						</c:forEach>
					</c:if>
				</c:forEach>
			</c:if>
		</TD>
	    <TD width="20%">
	    	<a href="${config:url(pageContext.request)}control/staffLoginLog/list${config:suffix()}?userId=${entry.userId}&staffPage=${param.page}">登录日志</a>
	    	<a href="${config:url(pageContext.request)}control/staff/manage${config:suffix()}?method=editStaff&userId=${entry.userId}&page=${param.page}">修改</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteStaff('${entry.userId}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</form:form>
</DIV></BODY></HTML>
