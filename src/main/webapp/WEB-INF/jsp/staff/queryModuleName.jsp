<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>  
<%@ page import="java.net.*" %> 
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>

<script language="javascript" type="text/javascript">
//到指定的分页页面
	function topage(pages){	
		modulePage(pages);
	}
</script> 

<DIV class="d-box">
<!-- 分页选择 -->
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="5%">选择</TH>
    <TH width="45%">模块名称</TH>
    <TH width="5%">选择</TH>
    <TH width="45%">模块名称</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status"> 
	   <c:if test="${status.count%2!=0 }"><TR></c:if>
	  	<TD>
	  		<INPUT TYPE="radio" name="type" value="${entry}" onClick="copyModuleName(this);return false;">
		</TD>
	    <TD align="left">
			&nbsp;${entry}
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