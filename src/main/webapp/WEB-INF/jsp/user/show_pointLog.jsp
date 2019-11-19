<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>显示积分日志</TITLE>
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
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/pointLog/list${config:suffix()}?userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  	<TR>
	    <TD class="t-label t-label-h" width="12%">Id：</TD>
	    <TD class="t-content" width="88%" >
	    	${pointLog.id}
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >模块：</TD>
	    <TD class="t-content" width="85%" >    	
	    	<c:if test="${pointLog.module == 100}">发表话题</c:if>
	    	<c:if test="${pointLog.module == 200}">发表评论</c:if>
	    	<c:if test="${pointLog.module == 300}">发表回复</c:if>
	    	<c:if test="${pointLog.module == 400}">积分购买话题</c:if>
	    	<c:if test="${pointLog.module == 500}">会员卡订单支付</c:if>
	    	<c:if test="${pointLog.module == 600}">充值</c:if>
	    	<c:if test="${pointLog.module == 700}">提交问题</c:if>
	    	<c:if test="${pointLog.module == 800}">提交答案</c:if>
	    	<c:if test="${pointLog.module == 900}">提交答案回复</c:if>
	    </TD>
	  </TR>	
	   <TR>
	    <TD class="t-label t-label-h" width="12%">用户名称：</TD>
	    <TD class="t-content" width="88%" >
	    	${pointLog.userName}
	    </TD>
	  </TR>
	  <c:if test="${pointLog.operationUserType ==1}">
	  <TR>
	    <TD class="t-label t-label-h" width="12%">操作员工名称：</TD>
	    <TD class="t-content" width="88%" >
	    	${pointLog.operationUserName}
	    </TD>
	  </TR>
	  </c:if>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">参数Id：</TD>
	    <TD class="t-content" width="88%" >
	    	<c:if test="${pointLog.module == 100}">话题Id：${pointLog.parameterId}</c:if>
	    	<c:if test="${pointLog.module == 200}">评论Id：${pointLog.parameterId}</c:if>
	    	<c:if test="${pointLog.module == 300}">回复Id：${pointLog.parameterId}</c:if>
	    	<c:if test="${pointLog.module == 400}">话题Id：${pointLog.parameterId}</c:if>
	    	<c:if test="${pointLog.module == 500}">订单号：${pointLog.parameterId}</c:if>
	    	<c:if test="${pointLog.module == 600}">用户Id：${pointLog.parameterId}</c:if>
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">金额：</TD>
	    <TD class="t-content" width="88%" >
	    	<c:if test="${pointLog.pointState ==1 }">+</c:if>
	    	<c:if test="${pointLog.pointState ==2 }">-</c:if>
	    	${pointLog.point}
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="12%">时间：</TD>
	    <TD class="t-content" width="88%" >
	    	<fmt:formatDate value="${pointLog.times}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	    </TD>
	  </TR>
	  
	   <TR>
	    <TD class="t-label t-label-h" width="12%">备注：</TD>
	    <TD class="t-content" width="88%" >
	    	${pointLog.remark}
	    </TD>
	  </TR>
</TBODY>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
