<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>  
<%@ page import="java.net.*" %> 
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>

<script language="javascript" type="text/javascript">
	//到指定的分页页面
	function topage(pages){	
		var parentId = document.getElementById("parentId").value;
		helpTypePage(pages,parentId,'${tableName}');
	}
</script> 
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;分类导航:</span>
	        <span style="float:left;">&nbsp;<a href="javascript:helpTypePage(1,0,'${tableName}')">全部分类</a></span>
			<span style="float:left">
			&nbsp;
			<c:forEach items="${navigation}" var="navigationMap" varStatus="status">
				>>&nbsp;<a href="javascript:helpTypePage(1,'${navigationMap.key}','${tableName}')">${navigationMap.value}</a>
			</c:forEach>
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 
<input type="hidden" id="parentId" value="${param.parentId }">
<!-- 帮助分页选择 -->
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="5%">选择</TH>
    <TH width="45%">分类名称</TH>
    <TH width="5%">选择</TH>
    <TH width="45%">分类名称</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status"> 
	   <c:if test="${status.count%2!=0 }"><TR></c:if>
	  	<TD>
	  		<c:if test="${entry.childNodeNumber ==0}"> <INPUT TYPE="radio" name="type" value="${entry.id}" onclick="javascript:addType('${entry.id}','${entry.name }')"></c:if>
		</TD>
	    <TD align="left">
			<c:if test="${entry.childNodeNumber >0}"><a href="javascript:helpTypePage(1,'${entry.id}')">&nbsp;${entry.name }</a></c:if>
	    	<c:if test="${entry.childNodeNumber ==0}">&nbsp;${entry.name }</c:if>
	    </TD>   
	   <c:if test="${status.last && status.count%2!=0}"><TD></TD><TD></TD></c:if> 
	   <c:if test="${status.count%2==0 }"></TR></c:if>
	</c:forEach> 
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</DIV>
