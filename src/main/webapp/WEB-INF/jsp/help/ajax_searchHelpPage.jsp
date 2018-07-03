<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>  
<%@ page import="java.net.*" %> 
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>

<script language="javascript" type="text/javascript">
//到指定的分页页面
	function topage(pages){
		helpPage(pages,'${tableName}');
	}
</script>
<DIV class="d-box"> 

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0" >
		<TR>
	    <TD class="t-content" colSpan="5" height="25px">

	        <span style="font-weight:bold;float:left;">
	        	<input id="searchName" type="text" size="20" value="${searchName}" style="margin-top: 2px;">
	        </span>
	        <span style="font-weight:bold;float:left;margin-left:2px; margin-top: 1px;">
	        	
				<input type="button" class="functionButton5" onclick="helpPage(1,'${tableName}');" value="查询" >
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 

<!-- 帮助分页选择 -->
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="8%">选择</TH>
    <TH width="92%">帮助名称</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status"> 
	  <TR>
	  	<TD>
	  		<input type="checkbox" name="help" id="help_${entry.id}" value="${entry.id}" onclick="javascript:selectHelp_checkbox(this,'${entry.name }','${tableName}')">
		</TD>
	    <TD align="left">
			&nbsp;${entry.name }
	    </TD> 
	  </TR> 
	</c:forEach> 
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</DIV>