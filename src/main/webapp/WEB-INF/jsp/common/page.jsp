<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!-- 分页栏开始 -->
<div class="pg_div">
<DIV class="pg">
	<span class="count">共${pageView.totalrecord}条</span> 
    <c:if test="${pageView.pageindex.startindex > 1}">  
       <A class=last href="javascript:topage('1');" title="第1页">1 ...</A>
    </c:if>
    <c:forEach begin="${pageView.pageindex.startindex}" end="${pageView.pageindex.endindex}" var="index">
		<c:if test="${index > 0}">
			<c:if test="${pageView.currentpage == index}">
				<STRONG>${index}</STRONG>  
			</c:if>
			<c:if test="${pageView.currentpage != index}">
				<A href="javascript:topage('${index}')" title="第${index}页">${index}</A>  
			</c:if>
		</c:if>
	</c:forEach>
    <c:if test="${pageView.pageindex.endindex < pageView.totalpage}">    
       <A class=last href="javascript:topage('${pageView.totalpage}')" title="第${pageView.totalpage}页">... ${pageView.totalpage}</A>
    </c:if>
    
    <LABEL>
		<INPUT onkeydown="if(event.keyCode==13) {topage(this.value);return false;}" class="pg_input" title="输入页码，按回车跳转" value="${pageView.currentpage}" size="2">
		<SPAN title="共${pageView.totalpage}页"> / ${pageView.totalpage}页</SPAN>
	</LABEL> 
</DIV>
</div>
<!-- 分页栏结束 -->

