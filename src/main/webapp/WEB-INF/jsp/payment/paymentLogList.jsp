<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>支付日志管理</TITLE>
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
    <TH>支付流水号</TH>
    <TH>支付模块</TH>
    <TH>接口产品</TH>
    <TH>金额</TH>
    <TH>时间</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD width="26%">${entry.paymentRunningNumber}</TD>
	    <TD width="25%">
	    	<c:if test="${entry.paymentModule == 1}">订单支付</c:if>
	    	<c:if test="${entry.paymentModule == 5}">用户充值</c:if>
	    	<c:if test="${entry.paymentModule == 6}">账户提现</c:if>
	    	<c:if test="${entry.paymentModule == 70}">余额购买话题隐藏内容</c:if>
	    	<c:if test="${entry.paymentModule == 80}">解锁话题隐藏内容分成</c:if>
	    </TD>
	    <TD width="12%">
	    	<c:if test="${entry.interfaceProduct == -1}">员工操作</c:if>
	    	<c:if test="${entry.interfaceProduct == 0}">预存款支付</c:if>
	    	<c:if test="${entry.interfaceProduct == 1}">支付宝即时到账</c:if>
	    	<c:if test="${entry.interfaceProduct == 4}">支付宝手机网站</c:if>
	    </TD>
	    <TD width="15%" >
	    	<c:if test="${entry.amountState ==1 }">+</c:if>
	    	<c:if test="${entry.amountState ==2 }">-</c:if>
	    	${entry.amount}
	    </TD>
	    <TD width="16%" ><fmt:formatDate value="${entry.times}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="6%">
	    	<a href="control/paymentLog/manage${config:suffix()}?method=show&userName=${entry.userName}&paymentRunningNumber=${entry.paymentRunningNumber}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}">查看</a>
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