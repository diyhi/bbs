<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>显示支付日志</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script language="JavaScript" type="text/javascript">

function SureSubmit(objForm){
//	if (verifyForm(objForm)) objForm.submit();
	objForm.submit();
} 
</script>
</HEAD>
<BODY>
<form:form modelAttribute="payment">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/paymentLog/list${config:suffix()}?userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  	<TR>
	    <TD class="t-label t-label-h" width="12%">支付流水号：</TD>
	    <TD class="t-content" width="88%" >
	    	${paymentLog.paymentRunningNumber}
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >支付模块：</TD>
	    <TD class="t-content" width="85%" >
	    	<c:if test="${paymentLog.paymentModule == 1}">会员卡订单支付</c:if>
	    	<c:if test="${paymentLog.paymentModule == 5}">用户充值</c:if>
	    	<c:if test="${paymentLog.paymentModule == 6}">账户提现</c:if>
	    	<c:if test="${paymentLog.paymentModule == 70}">余额购买话题隐藏内容</c:if>
	    	<c:if test="${paymentLog.paymentModule == 80}">解锁话题隐藏内容分成</c:if>
	    	<c:if test="${paymentLog.paymentModule == 90}">悬赏金额</c:if>
	    	<c:if test="${paymentLog.paymentModule == 100}">采纳答案</c:if>
	    	<c:if test="${paymentLog.paymentModule == 110}">调整赏金</c:if>
	    </TD>
	  </TR>	
 	<TR>
	    <TD class="t-label t-label-h" width="12%">接口产品：</TD>
	    <TD class="t-content" width="88%" >
	    	<c:if test="${paymentLog.interfaceProduct == -1}">员工操作</c:if>
	    	<c:if test="${paymentLog.interfaceProduct == 0}">预存款支付</c:if>
	    	<c:if test="${paymentLog.interfaceProduct == 1}">支付宝即时到账</c:if>
	    	<c:if test="${paymentLog.interfaceProduct == 4}">支付宝手机网站</c:if>
	    </TD>
	  </TR>
	   <TR>
	    <TD class="t-label t-label-h" width="12%">用户名称：</TD>
	    <TD class="t-content" width="88%" >
	    	${paymentLog.userName}
	    </TD>
	  </TR>
	  <c:if test="${paymentLog.operationUserType ==1}">
	  <TR>
	    <TD class="t-label t-label-h" width="12%">操作员工名称：</TD>
	    <TD class="t-content" width="88%" >
	    	${paymentLog.operationUserName}
	    </TD>
	  </TR>
	  </c:if>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">交易号：</TD>
	    <TD class="t-content" width="88%" >
	    	${paymentLog.tradeNo}
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">参数Id：</TD>
	    <TD class="t-content" width="88%" >
	    	<c:if test="${paymentLog.paymentModule == 1}">订单Id: ${paymentLog.parameterId}</c:if>
	    	<c:if test="${paymentLog.paymentModule == 5}">用户Id: ${paymentLog.parameterId}</c:if>
	    	<c:if test="${paymentLog.paymentModule == 70 || paymentLog.paymentModule == 80}">话题Id: ${paymentLog.parameterId}</c:if>	
	    	<c:if test="${paymentLog.paymentModule == 90}">问题Id: ${paymentLog.parameterId}</c:if>	
	    	<c:if test="${paymentLog.paymentModule == 100}">答案Id: ${paymentLog.parameterId}</c:if>	
	    	<c:if test="${paymentLog.paymentModule == 110}">问题Id: ${paymentLog.parameterId}</c:if>	
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">金额：</TD>
	    <TD class="t-content" width="88%" >
	    	<c:if test="${paymentLog.amountState ==1 }">+</c:if>
	    	<c:if test="${paymentLog.amountState ==2 }">-</c:if>
	    	${paymentLog.amount}
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">时间：</TD>
	    <TD class="t-content" width="88%" >
	    	<fmt:formatDate value="${paymentLog.times}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	    </TD>
	  </TR>
	  
	   <TR>
	    <TD class="t-label t-label-h" width="12%">备注：</TD>
	    <TD class="t-content" width="88%" >
	    	${paymentLog.remark}
	    </TD>
	  </TR>
</TBODY>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
