<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>私信列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">

//删除私信
function deletePrivateMessage(userId,friendUserId){
	var parameter = "";
	parameter += "&userId="+userId;
	parameter += "&friendUserId="+friendUserId;
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
		"${config:url(pageContext.request)}control/privateMessage/manage${config:suffix()}?method=deletePrivateMessageChat&timestamp=" + new Date().getTime(), true,parameter);

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
		        <span style="font-weight:bold;float:left;">&nbsp;私信导航：</span>
				<span style="float:left;margin-top: 2px;">&nbsp;<a href="${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id}&userName=${param.userName}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}">${param.userName}</a></span>
			</TD>
		</TR>
	</TBODY>
</TABLE> 

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>对方用户名称</TH>
    <TH>最新私信内容</TH>
    <TH>发送时间</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD width="15%">${entry.friendUserName}</TD>
	    <TD width="55%"><enhance:out escapeXml="false">${entry.messageContent}</enhance:out></TD>
	    <TD width="15%"><fmt:formatDate value="${entry.sendTime}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="15%">
	    	<a href="${config:url(pageContext.request)}control/privateMessage/manage${config:suffix()}?method=privateMessageChatList&friendUserId=${entry.friendUserId}&id=${param.id}&userName=${param.userName}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&privateMessagePage=${param.page}">对话</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deletePrivateMessage('${entry.userId}','${entry.friendUserId}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
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
