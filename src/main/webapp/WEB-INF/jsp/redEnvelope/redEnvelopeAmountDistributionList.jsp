<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>发红包金额分配列表</TITLE>
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
	<c:if test="${param.origin == null || param.origin == ''}"><!-- 来源 -->
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/redEnvelope/giveRedEnvelope/list${config:suffix()}?userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.giveRedEnvelopePage}'" value="返回">
	</c:if>
	<c:if test="${param.origin == 10}"><!-- 来源 -->
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=view&topicId=${param.topicId}&visible=${param.visible}&topicPage=${param.topicPage}&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&commentPage=${param.commentPage}&replyPage=${param.replyPage}'" value="返回">
	</c:if>
</div>
<form:form>


<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h">类型：</TD>
		    <TD class="t-content" >
		    	<c:if test="${giveRedEnvelope.type == 10}">个人定向红包</c:if>
		    	<c:if test="${giveRedEnvelope.type == 20}">公共随机红包</c:if>
		    	<c:if test="${giveRedEnvelope.type == 30}">公共定额红包</c:if>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">总金额：</TD>
		    <TD class="t-content" >
		    	${giveRedEnvelope.totalAmount}
		    	<c:if test="${giveRedEnvelope.refundAmount >0}">
		    		<span style="color: red;">中止领取红包后返还金额￥${giveRedEnvelope.refundAmount}</span>
		    	</c:if>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">发放数量：</TD>
		    <TD class="t-content" >
		    	${giveRedEnvelope.giveQuantity}
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">已领取数量：</TD>
		    <TD class="t-content" >
		    	${giveRedEnvelope.giveQuantity - giveRedEnvelope.remainingQuantity}
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">时间：</TD>
		    <TD class="t-content" >
		    	<fmt:formatDate value="${giveRedEnvelope.giveTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>
		    </TD>
		</TR>
	</TBODY>
</TABLE>
		
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<THEAD class="t-list-thead">
		<TR>
		    <TH>收红包用户</TH>
		    <TH>金额</TH>
		    <TH>收取时间</TH>
	    </TR>
    </THEAD>
	<TBODY class="t-list-tbody followModule" align="center">
	<c:forEach items="${pageView.records}" var="entry">
		<TR >
		    <TD width="30%" style="text-align: left! important;">
		    	<div class="avatarBox">
					<c:if test="${entry.receiveAvatarName != null && entry.receiveAvatarName != ''}">
						<img src="${entry.receiveAvatarPath}100x100/${entry.receiveAvatarName}" >
					</c:if>
		            <c:if test="${entry.receiveAvatarName == null || entry.receiveAvatarName == ''}">
		            	<!-- 首字符头像-->
		         		<img avatar="${(entry.receiveNickname != null && entry.receiveNickname !='') ? entry.receiveNickname : entry.receiveUserName}" >
		            </c:if>
				</div>
				<span class="userName">
			    	${entry.receiveUserName}
			    	<c:if test="${entry.receiveNickname != null && entry.receiveNickname != ''}">
			    		（呢称：${entry.receiveNickname}）
			    	</c:if>
		    	</span>
		    </TD>
		    <TD width="25%">${entry.amount}</TD>
		    <TD width="30%">
		    	<c:if test="${entry.receiveUserId == null}">
					本红包未被领取
				</c:if>
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