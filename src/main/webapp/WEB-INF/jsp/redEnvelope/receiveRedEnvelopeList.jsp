<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>收红包列表</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script src="backstage/jquery/jquery.min.js" language="javascript" type="text/javascript"></script>
<script language="javascript" src="backstage/jquery/jquery.letterAvatar.js" type="text/javascript"></script>
</HEAD>
<BODY>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="返回">
</div>
<form:form>

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<THEAD class="t-list-thead">
		<TR>
		    <TH>发红包用户</TH>
		    <TH>金额</TH>
		    <TH>收取时间</TH>
	    </TR>
    </THEAD>
	<TBODY class="t-list-tbody followModule" align="center">
	<c:forEach items="${pageView.records}" var="entry">
		<TR >
		    <TD width="30%" style="text-align: left! important;">
		    	<div class="avatarBox">
					<c:if test="${entry.giveAvatarName != null && entry.giveAvatarName != ''}">
						<img src="${entry.giveAvatarPath}100x100/${entry.giveAvatarName}" >
					</c:if>
		            <c:if test="${entry.giveAvatarName == null || entry.giveAvatarName == ''}">
		            	<!-- 首字符头像-->
		         		<img avatar="${(entry.giveNickname != null && entry.giveNickname !='') ? entry.giveNickname : entry.giveUserName}" >
		            </c:if>
				</div>
				<span class="userName">
			    	${entry.giveUserName}
			    	<c:if test="${entry.giveNickname != null && entry.giveNickname != ''}">
			    		（呢称：${entry.giveNickname}）
			    	</c:if>
		    	</span>
		    </TD>
		    <TD width="25%">${entry.amount}</TD>
		    <TD width="30%">
		    	<fmt:formatDate value="${entry.receiveTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>
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