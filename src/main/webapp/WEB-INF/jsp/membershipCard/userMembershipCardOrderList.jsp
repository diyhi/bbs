<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>用户会员卡订单列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">


</script>

</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="返回">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>订单号</TH>
    <TH>创建时间</TH>
    <TH>已支付金额</TH>
    <TH>已支付积分</TH>
    <TH>角色名称</TH>
    <TH>规格名称</TH>
    <TH>数量</TH>
    <TH>时长</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="20%">${entry.orderId}</TD>
	  	<TD width="20%"><fmt:formatDate value="${entry.createDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="10%">
	    	${entry.paymentAmount}
	    </TD>
	    <TD width="10%">${entry.paymentPoint}</TD>
	  	<TD width="10%">${entry.roleName}</TD>
	  	<TD width="15%">${entry.specificationName}</TD>
	  	<TD width="5%">${entry.quantity}</TD>
	  	<TD width="10%">
	  		${entry.duration}
	  		<c:if test="${entry.unit ==10}">小时</c:if>
	  		<c:if test="${entry.unit ==20}">日</c:if>
	  		<c:if test="${entry.unit ==30}">月</c:if>
	  		<c:if test="${entry.unit ==40}">年</c:if>
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
