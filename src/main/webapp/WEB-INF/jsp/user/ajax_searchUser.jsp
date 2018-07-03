<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<script type="text/javascript">
//到指定的分页页面
function topage(pages){
	searchSubmit(pages);
}
//设置查询参数
function siteQuery(obj){
	if(obj.value == "1"){//用户名
		document.getElementById("searchParameter_1").style.display="";//用户名
		document.getElementById("searchParameter_4").style.display="none";//已购买商品总金额
		document.getElementById("searchParameter_5").style.display="none";//注册日期
	}else if(obj.value == "2"){//筛选条件
		document.getElementById("searchParameter_1").style.display="none";//用户名
		document.getElementById("searchParameter_4").style.display="";//已购买商品总金额
		document.getElementById("searchParameter_5").style.display="";//注册日期
	}
}


function selectUserCondition(){
	
}





</script>

<DIV class="d-box">
<form:form>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
			<TD class="t-label t-label-h" width="12%">搜索类型：</TD>
			<TD class="t-content" width="88%" colSpan="2">
				<select id="searchType" onchange="siteQuery(this);">
					<option value="1" <c:if test="${searchType eq '1'}"> selected="selected"</c:if>>用户名</option>
					<option value="2" <c:if test="${searchType eq '2'}"> selected="selected"</c:if>>筛选条件</option>
				</select>
			</TD>
			
			
		</TR>
		<TR id="searchParameter_1" <c:if test="${searchType eq 2 }"> style="DISPLAY: none"</c:if>>
			<TD class="t-label t-label-h" width="12%">用户名：</TD>
			<TD class="t-content" width="88%" colSpan="2">
				<input id="userName" type="text" size="30" value="${userName}">
				<span class="span-text" >${error['userName']}</span>
			</TD>
		</TR>
		<TR id="searchParameter_4" <c:if test="${searchType eq 1}"> style="DISPLAY: none" </c:if> >
			<TD class="t-label t-label-h" width="12%">已购买商品总金额：</TD>
			<TD class="t-content" width="72%" >
				<input id="start_buyTotalAmount" type="text" size="20" value="${start_buyTotalAmount}">
				<span class="span-text" >${error['start_buyTotalAmount']}</span>
				&nbsp;&lt;=金额&lt;=&nbsp;
				<input id="end_buyTotalAmount" type="text" size="20" value="${end_buyTotalAmount}">&nbsp;<span class="span-help">不限制请留空</span>
				<span class="span-text" >${error['end_buyTotalAmount']}</span>
			</TD>
			<TD class="t-content" width="16%" rowspan="2">
			<a href='javascript:;' onClick="javascript:selectUserCondition();">全选当前条件参数用户</a>
			</TD>
		</TR>
		<TR id="searchParameter_5" <c:if test="${searchType eq 1 }"> style="DISPLAY: none" </c:if> >
			<TD class="t-label t-label-h" width="12%">注册日期：</TD>
			<TD class="t-content" width="72%" >
				<input id="start_registrationDate" class="date-input" type="text" size="25" value="${start_registrationDate}">
				<span class="span-text" >${error['start_registrationDate']}</span>
				&nbsp;&lt;=日期&lt;=&nbsp;
				<input id="end_registrationDate" class="date-input" type="text" size="25" value="${end_registrationDate}">&nbsp;<span class="span-help">不限制请留空</span>
				<span class="span-text" >${error['end_registrationDate']}</span>
			</TD>
		</TR>
		</TBODY>
	
		<TBODY>
		<TR>
			<TD class="t-button" colSpan="3">
        	<span class="submitButton2"><INPUT type="button" value="搜索" onClick="javascript:searchSubmit(1);return false;" ></span>
  	</TD>
		</TR>		
	</TBODY>
</TABLE>



<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="5%">选择</TH>
    <TH width="15%">用户名</TH>
    <TH width="14%">预存款</TH>
    <TH width="14%">积分</TH>
    <TH width="18%">已购买商品总金额</TH>
    <TH width="15%">注册日期</TH>
    <TH width="12%">用户状态</TH>
    <TH width="7%">操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD>
	  		<input type="checkbox" id="user_${entry.id }" name="user" value="${entry.id }" onClick="addUser_checkbox(this,'${entry.userName}')">
    	</TD>
	    <TD>${entry.userName}</TD>
	    <TD>${entry.deposit}</TD>
		<TD>${entry.point}</TD>
		<TD>${entry.buyTotalAmount}</TD>
	    <TD><fmt:formatDate value="${entry.registrationDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD>
	    	<c:if test="${entry.state == 1}">
	    		正常用户
	    	</c:if>
	    	<c:if test="${entry.state == 11}">
	    		<SPAN class="span-text">正常用户删除</SPAN>
	    	</c:if>
	    	<c:if test="${entry.state == 2}">
	    		禁止用户
	    	</c:if>
	    	<c:if test="${entry.state == 12}">
	    		<SPAN class="span-text">禁止用户删除</SPAN>
	    	</c:if>
	    </TD>
	    <TD>
			<a href='javascript:;' onClick="javascript:window.parent.loadFrame('${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${entry.id }&queryState=${param.queryState}&jumpStatus=-12&timestamp='+ new Date().getTime(),'用户查看')">查看</a>&nbsp;
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</form:form>
<script type="text/javascript">
//JS日期控件
$(function(){
    $('#start_registrationDate').calendar({format:'yyyy-MM-dd HH:mm',zIndex:'99999999'});
    $('#end_registrationDate').calendar({format:'yyyy-MM-dd HH:mm',zIndex:'99999999'});
});
</script>
</DIV>


