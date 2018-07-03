<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>页面浏览量管理</TITLE>
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
	var start_times = document.getElementById("start_times").value;
	if(start_times != ""){
		parameter += "&start_times="+encodeURIComponent(start_times);
	}
	//时间 结束
	var end_times = document.getElementById("end_times").value;
	if(end_times != ""){
		parameter += "&end_times="+encodeURIComponent(end_times);
	}
	

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	window.location.href = getUrl()+"?"+parameter;
	
}

//今天筛选提交
function nowSubmit(){
	var parameter = "";


	//时间 起始
	parameter += "&start_times="+encodeURIComponent(getDay(0)+" 00:00");
	
	//时间 结束
	parameter += "&end_times="+encodeURIComponent(getDay(0)+" 23:59");
	
	

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	window.location.href = getUrl()+"?"+parameter;

}
//昨天筛选提交
function yesterdaySubmit(){
	var parameter = "";

	//时间 起始
	parameter += "&start_times="+encodeURIComponent(getDay(-1)+" 00:00");
	
	//时间 结束
	parameter += "&end_times="+encodeURIComponent(getDay(-1)+" 23:59");
	
	

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	window.location.href = getUrl()+"?"+parameter;

}



//计算今天的前后日期  参数day: 天 如2为后天 0为今天 -1为昨天
function getDay(day){  
       var today = new Date();  
         
       var targetday_milliseconds=today.getTime() + 1000*60*60*24*day;          
  
       today.setTime(targetday_milliseconds); //注意，这行是关键代码    
         
       var tYear = today.getFullYear();  
       var tMonth = today.getMonth();  
       var tDate = today.getDate();  
       tMonth = doHandleMonth(tMonth + 1);  
       tDate = doHandleMonth(tDate);  
       return tYear+"-"+tMonth+"-"+tDate;  
}  
//补零
function doHandleMonth(month){  
       var m = month;  
       if(month.toString().length == 1){  
          m = "0" + month;  
       }  
       return m;  
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
	        <input id="start_times" class="date-input" type="text" size="25" value="${start_times}">
			<span class="span-text" >${error['start_times']}</span>
			&nbsp;&lt;=筛选日期&lt;=&nbsp;
			<input id="end_times" class="date-input" type="text" size="25" value="${end_times}">
			<span class="span-text" >${error['end_times']}</span>
			<input type="button" class="functionButton5" onClick="javascript:screenSubmit();" value="筛选">&nbsp;
			<input type="button" class="functionButton5" onClick="javascript:nowSubmit();" value="今天">&nbsp;
			<input type="button" class="functionButton5" onClick="javascript:yesterdaySubmit();" value="昨天">
		</TD>
	</TR>
	</TBODY>
</TABLE> 


<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="15%">浏览时间</TH>
  	<TH width="15%">受访</TH>
    <TH width="15%">页面来源</TH>
    <TH width="10%">IP</TH>
    <TH width="15%">IP归属地</TH>
    <TH width="10%">浏览器名称</TH>
    <TH width="10%">访问设备类型</TH>
    <TH width="10%">访问设备系统</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >	    
	  	<TD><fmt:formatDate value="${entry.times}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD>${entry.url}</TD>
		<TD>${entry.referrer}</TD>
		<TD>${entry.ip}</TD>
	    <TD>${entry.ipAddress}</TD>
	    <TD>${entry.browserName}</TD>
	    <TD>${entry.deviceType}</TD>
	    <TD>${entry.operatingSystem}</TD>
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
    $('#start_times').calendar({format:'yyyy-MM-dd HH:mm'});
    $('#end_times').calendar({format:'yyyy-MM-dd HH:mm'});
});
</script>
</DIV></BODY></HTML>
