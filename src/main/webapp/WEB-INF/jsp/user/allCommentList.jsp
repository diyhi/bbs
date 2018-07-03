<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>全部待审核评论</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />


</HEAD>
<script type="text/javascript">
//审核评论
function auditComment(commentId){
	var parameter = "&commentId="+commentId;
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
			alert("发布失败");
		}
	},
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=auditComment&timestamp=" + new Date().getTime(), true,parameter);
		
	
}




//删除评论
function deleteComment(commentId){

	var parameter = "&commentId="+commentId;

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
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);

}

</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id }&queryState=${param.queryState}&userPage=${param.userPage}'" value="返回">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>话题名称</TH>
    <TH>评论内容</TH>
   	<TH>会员/员工</TH>
    <TH>评论时间</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD width="25%">${entry.topicTitle}</TD>
	    <TD width="30%">${entry.content}</TD>
	    <TD width="15%">${entry.userName}<c:if test="${entry.isStaff == true}"><span style="color: green;">[员工]</span></c:if></TD>
	    <TD width="15%"><fmt:formatDate value="${entry.postTime}" pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="15%">
	    	<c:if test="${entry.status == 10}">
	    		<A onclick="javascript:if(window.confirm('确定发布吗? ')){auditComment('${entry.id}');return false;}else{return false};" hidefocus="true" href="#" ondragstart= "return false">立即审核</A>
	    	</c:if>
	    	<a href="${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.id}&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&commentPage=${param.page}&origin=20">查看</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteComment('${entry.id}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
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
