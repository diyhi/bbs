<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>发红包列表</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>

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
		    <TH>类型</TH>
		    <TH>总金额</TH>
		    <TH>发放数量</TH>
		    <TH>已领取数量</TH>
		    <TH>话题</TH>
		    <TH>时间</TH>
		    <TH>操作</TH>
	    </TR>
    </THEAD>
	<TBODY class="t-list-tbody" align="center">
	<c:forEach items="${pageView.records}" var="entry">
		<TR >
		    <TD width="10%">
		    	<c:if test="${entry.type == 10}">个人定向红包</c:if>
		    	<c:if test="${entry.type == 20}">公共随机红包</c:if>
		    	<c:if test="${entry.type == 30}">公共定额红包</c:if>
		    </TD>
		    <TD width="20%">
		    	${entry.totalAmount}
		    	<c:if test="${entry.refundAmount >0}">
		    		<span style="color: red;">中止领取红包后返还金额￥${entry.refundAmount}</span>
		    	</c:if>
		    </TD>
		    <TD width="10%">${entry.giveQuantity}</TD>
		    <TD width="10%">${entry.giveQuantity - entry.remainingQuantity}</TD>
		    <TD width="25%">${entry.bindTopicTitle}</TD>
		    <TD width="15%"><fmt:formatDate value="${entry.giveTime}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
		    <TD width="10%">
		    	<a href="${config:url(pageContext.request)}control/redEnvelope/redEnvelopeAmountDistribution/list${config:suffix()}?giveRedEnvelopeId=${entry.id}&id=${param.id}&userName=${param.userName}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&giveRedEnvelopePage=${param.page}">金额分配</a>
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