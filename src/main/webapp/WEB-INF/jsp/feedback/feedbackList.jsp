<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>在线留言管理</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

</HEAD>
<script type="text/javascript">

//筛选提交
function screenSubmit(){
	var parameter = "";

	//时间 起始
	var start_createDate = document.getElementById("start_createDate").value;
	if(start_createDate != ""){
		parameter += "&start_createDate="+encodeURIComponent(start_createDate);
	}
	//时间 结束
	var end_createDate = document.getElementById("end_createDate").value;
	if(end_createDate != ""){
		parameter += "&end_createDate="+encodeURIComponent(end_createDate);
	}
	

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	window.location.href = getUrl()+"?"+parameter;
	
}


//删除在线留言
function deleteFeedback(id){
	var parameter = "";

	parameter += "&feedbackId="+id;

	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
   	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("删除失败");
		}
	},
		"${config:url(pageContext.request)}control/feedback/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}


</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script type="text/javascript" src="backstage/lhgcalendar/lhgcore.lhgcalendar.min.js" language="javascript" ></script>
<DIV class="d-box">
<form:form>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <input id="start_createDate" class="date-input" type="text" size="25" value="${start_createDate}">
			<span class="span-text" >${error['start_createDate']}</span>
			&nbsp;&lt;=筛选日期&lt;=&nbsp;
			<input id="end_createDate" class="date-input" type="text" size="25" value="${end_createDate}">
			<span class="span-text" >${error['end_createDate']}</span>
			<input type="button" class="functionButton5" onClick="javascript:screenSubmit();" value="筛选">
		</TD>
	</TR>
	</TBODY>
</TABLE> 


<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH width="20%">称呼</TH>
    <TH width="20%">联系方式</TH>
    <TH width="30%">内容</TH>
    <TH width="15%">日期</TH>
    <TH width="15%">操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >	    
	    <TD>
	    	${entry.name}
		</TD>
		<TD>${entry.contact}</TD>
		<TD>${entry.content}</TD>
	    <TD><fmt:formatDate value="${entry.createDate }"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD>
	    	<a href="${config:url(pageContext.request)}control/feedback/manage${config:suffix()}?method=view&feedbackId=${entry.id}&start_createDate=${start_createDate}&end_createDate=${end_createDate}&page=${param.page}">查看</a>
	    	<a hidefocus="true" onClick="javascript:if(window.confirm('确定删除吗? ')){deleteFeedback('${entry.id}'); return false}else{return false}" href="#" ondragstart= "return false">删除</a>
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
    $('#start_createDate').calendar({format:'yyyy-MM-dd HH:mm'});
    $('#end_createDate').calendar({format:'yyyy-MM-dd HH:mm'});
});
</script>
</DIV></BODY></HTML>
