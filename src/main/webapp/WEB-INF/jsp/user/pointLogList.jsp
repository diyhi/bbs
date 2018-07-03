<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>积分日志管理</TITLE>
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
    <TH>模块</TH>
    <TH>会员</TH>
    <TH>操作用户名称</TH>
    <TH>积分</TH>
    <TH>时间</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR > 
	    <TD width="25%">
	    	<c:if test="${entry.module == 100}">发表话题</c:if>
	    	<c:if test="${entry.module == 200}">发表评论</c:if>
	    	<c:if test="${entry.module == 300}">发表回复</c:if>
	    </TD>
	    <TD width="15%">
	    	${entry.userName}
	    </TD>
	    
	    <TD width="20%" >
	    	<c:if test="${entry.operationUserType ==0 }">[系统]</c:if>
	    	<c:if test="${entry.operationUserType ==1 }">[员工]</c:if>
	    	<c:if test="${entry.operationUserType ==2 }">[会员]</c:if>
	    	${entry.operationUserName}
	    </TD>
	    <TD width="15%">
	    	<c:if test="${entry.pointState == 1}">+</c:if>
	    	<c:if test="${entry.pointState == 2}">-</c:if>
	    	${entry.point}
	    </TD>
	    <TD width="17%" ><fmt:formatDate value="${entry.times}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="8%">
	    	<a href="control/pointLog/manage${config:suffix()}?method=show&userName=${entry.userName}&pointLogId=${entry.id}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}">查看</a>
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