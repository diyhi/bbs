<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>  
<%@ page import="java.net.*" %> 
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>

<script language="javascript" type="text/javascript">
//到指定的分页页面
	function topage(pages){
		paymentVerificationLogPage(pages,'${userName}','${parameterId}');
	}
</script> 
<DIV class="d-box">
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="8%">选择</TH>
    <TH width="34%">流水号</TH>
    <TH width="28%">支付金额</TH>
    <TH width="30%">时间</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status"> 
	  <TR>
	  	<TD>
	  		<INPUT type="radio" name="paymentVerificationLog" value="${entry.id}"  onclick="javascript:addPaymentRunningNumber('${entry.id}','${entry.paymentAmount}')">
		</TD>
	    <TD>
			${entry.id}
	    </TD> 
	    <TD>
			${entry.paymentAmount}
	    </TD>
	    <TD>
			<fmt:formatDate value="${entry.times}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	    </TD>
	  </TR> 
	</c:forEach> 
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</DIV>