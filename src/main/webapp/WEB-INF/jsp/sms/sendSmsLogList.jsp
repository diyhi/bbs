<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>短信发送错误日志列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>

<script type="text/javascript">

</script>
</HEAD>

<BODY>
<DIV class="d-box">
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>接口产品</TH>
    <TH>发送服务</TH>
    <TH>发送时间</TH>
    <TH>平台用户Id</TH>
    <TH>手机</TH>
    <TH>状态码</TH>
    <TH>状态码描述</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD width="15%">
	    	<c:if test="${entry.interfaceProduct == 1}">阿里大于</c:if>
	    </TD>
	    <TD width="10%">
	    	<c:if test="${entry.serviceId == 1}">绑定手机</c:if>
	    </TD>
	    <TD width="15%">
	    	<fmt:formatDate value="${entry.createDate }"  pattern="yyyy-MM-dd HH:mm:ss"/>
	    </TD>
	    <TD width="10%">${entry.platformUserId}</TD>
	    <TD width="10%" >${entry.mobile}</TD>
	    <TD width="15%">
	    	${entry.code}
	    </TD>
	    <TD width="25%">
	    	${entry.message}
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
