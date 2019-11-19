<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>提醒列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">

//还原提醒
function reductionRemind(id,userId){
	var parameter = "";
	parameter += "&remindId="+id;
	parameter += "&userId="+userId;
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
			alert("还原失败");
		}
	},
		"${config:url(pageContext.request)}control/remind/manage${config:suffix()}?method=reductionRemind&timestamp=" + new Date().getTime(), true,parameter);

}
</script>

</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id}&userName=${param.userName}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="返回">
</div>
 
<form:form>

<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-content" colSpan="5" height="28px">
		        <span style="font-weight:bold;float:left;">&nbsp;提醒导航：</span>
				<span style="float:left;margin-top: 2px;">&nbsp;<a href="${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id}&userName=${param.userName}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}">${param.userName}</a></span>
			</TD>
		</TR>
	</TBODY>
</TABLE> 

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>发送用户</TH>
    <TH>内容</TH>
    <TH>发送时间</TH>
    <TH>阅读时间</TH> 
    <TH>状态</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD width="15%">${entry.senderUserName}</TD>
	    <TD width="30%">
	    	<c:if test="${entry.typeCode == 10}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				评论了我的话题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 20}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				回复了我的话题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 30}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				引用了我的评论
	    	</c:if>
	    	<c:if test="${entry.typeCode == 40}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.topicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				回复了我的评论
	    	</c:if>
	    	<c:if test="${entry.typeCode == 50}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				回复了我回复过的评论
	    	</c:if>
	    	<c:if test="${entry.typeCode == 60}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				解锁了我的话题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 70}">
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				赞了我的话题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 80}">
	    		关注了我
	    	</c:if>
	    	<c:if test="${entry.typeCode == 90}">
	    		我关注的 ${entry.senderUserName} 
	    		发表了话题
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}" style="color: #33a3dc;">${entry.topicTitle}</a>
	    	</c:if>
	    	<c:if test="${entry.typeCode == 100}">
	    		我关注的 ${entry.senderUserName} 
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				发表了评论
	    	</c:if>
	    	<c:if test="${entry.typeCode == 110}">
	    		我关注的 ${entry.senderUserName} 
	    		在
				<a href="control/topic/manage${config:suffix()}?method=view&topicId=${entry.topicId}&commentId=${entry.friendTopicCommentId}" style="color: #33a3dc;">${entry.topicTitle}</a>
				发表了回复
	    	</c:if>
	    	<c:if test="${entry.typeCode == 120}">
	    		在
				<a href="control/question/manage${config:suffix()}?method=view&questionId=${entry.questionId}&answerId=${entry.friendQuestionAnswerId}" style="color: #33a3dc;">${entry.questionTitle}</a>
				回答了我的问题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 130}">
	    		在
				<a href="control/question/manage${config:suffix()}?method=view&questionId=${entry.questionId}&answerId=${entry.friendQuestionAnswerId}" style="color: #33a3dc;">${entry.questionTitle}</a>
				回复了我的问题
	    	</c:if>
	    	<c:if test="${entry.typeCode == 140}">
	    		在
				<a href="control/question/manage${config:suffix()}?method=view&questionId=${entry.questionId}&answerId=${entry.questionAnswerId}" style="color: #33a3dc;">${entry.questionTitle}</a>
				回复了我的答案
	    	</c:if>
	    	<c:if test="${entry.typeCode == 150}">
	    		在
				<a href="control/question/manage${config:suffix()}?method=view&questionId=${entry.questionId}&answerId=${entry.friendQuestionAnswerId}" style="color: #33a3dc;">${entry.questionTitle}</a>
				回复了我回复过的答案
	    	</c:if>
	    </TD>
	    <TD width="15%"><fmt:formatDate value="${entry.sendTime}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="15%"><fmt:formatDate value="${entry.readTime}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="15%">
	    	<c:if test="${entry.status == 10}">未读</c:if>
	    	<c:if test="${entry.status == 20}"><span style="color: green;">已读</span></c:if>
	    	<c:if test="${entry.status == 110}"><span style="color: red;">未读删除</span></c:if>
	    	<c:if test="${entry.status == 120}"><span style="color: red;">已读删除</span></c:if>
	    </TD>
	    <TD width="10%">
	    	<c:if test="${entry.status >100}">
	    		<a href="#" onclick="javascript:if(window.confirm('确定还原吗? ')){reductionRemind('${entry.id}','${entry.receiverUserId}');return false;}else{return false};" hidefocus="true" ondragstart= "return false" title="还原用户已删除的提醒">还原</a>
	    	</c:if>
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
