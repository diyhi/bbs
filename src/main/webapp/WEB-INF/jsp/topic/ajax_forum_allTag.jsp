<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>  
<%@ page import="java.net.*" %> 
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>

<DIV class="d-box">
<!-- 选择 -->
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="5%">选择</TH>
    <TH width="45%">标签名称</TH>
    <TH width="5%">选择</TH>
    <TH width="45%">标签名称</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${tagList}" var="entry" varStatus="status"> 
	   <c:if test="${status.count%2!=0 }"><TR></c:if>
	  	<TD>
	  		<INPUT TYPE="radio" name="tag" value="${entry.id}" onclick="javascript:addTag('${entry.id}','${entry.name }','${tableName}')">
		</TD>
	    <TD align="left">
			&nbsp;${entry.name }
	    </TD>   
	   <c:if test="${status.last && status.count%2!=0}"><TD></TD><TD></TD></c:if> 
	   <c:if test="${status.count%2==0 }"></TR></c:if>
	</c:forEach> 
  </TBODY>
</TABLE>
</DIV>
