<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>话题搜索列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/jquery/jquery.min.js" language="JavaScript"></script>

<style type="text/css">
.highlight B{color: #fc0012;
}
</style>



<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
</HEAD>
<script type="text/javascript">
//搜索提交
function searchSubmit(){
	var parameter = "";
	//数据源
	var radio_dataSource = document.getElementsByName("dataSource");
	if(radio_dataSource != null){
		for(var j = 0; j < radio_dataSource.length; j++){ 
			if(radio_dataSource[j].checked){
				parameter += "&dataSource="+encodeURIComponent(radio_dataSource[j].value);
			}
		 }
	}
	
	//关键词
	var keyword = document.getElementById("keyword").value;
	if(keyword != ""){
		parameter += "&keyword="+encodeURIComponent(keyword);
	}
	//标签Id
	var tagId = document.getElementById("tagId").value;
	if(tagId != ""){
		parameter += "&tagId="+encodeURIComponent(tagId);
	}
	//标签名称
	var tagName = document.getElementById("tagName").value;
	if(tagName != ""){
		parameter += "&tagName="+encodeURIComponent(tagName);
	}
	//用户名称
	var userName = document.getElementById("userName").value;
	if(userName != ""){
		parameter += "&userName="+encodeURIComponent(userName);
	}
	//发表日期 起始
	var start_postTime = document.getElementById("start_postTime").value;
	if(start_postTime != ""){
		parameter += "&start_postTime="+encodeURIComponent(start_postTime);
	}
	//发表日期 结束
	var end_postTime = document.getElementById("end_postTime").value;
	if(end_postTime != ""){
		parameter += "&end_postTime="+encodeURIComponent(end_postTime);
	}

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	
	window.location.href = getUrl()+"?"+parameter;
	
};

//删除话题
function deleteTopic(topicId){
	var parameter = "";

	parameter += "&topicId="+topicId;
	
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
		"${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}


//弹出查看标签层
function showTagDiv(){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择标签",div,700,400);//显示层
	//显示标签
	allTag();
}
//显示标签
function allTag(){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/tag/manage${config:suffix()}?method=allTag&timestamp=" + new Date().getTime(), true);

}
//添加标签
function addTag(id,name){
	document.getElementById("tagId").value=id;
	document.getElementById("tagName").value=name;
	//关闭层
	systemLayerClose();
}
//取消标签
function cancelTag(){
	document.getElementById('tagId').value= ""; 
	document.getElementById('tagName').value = "";
}

</script>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<script type="text/javascript" src="backstage/lhgcalendar/lhgcore.lhgcalendar.min.js" language="javascript" ></script>
<DIV class="d-box">

<form:form>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
			<TD class="t-label t-label-h" width="12%">数据源：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="dataSource_1" name="dataSource" type="radio" value="1" <c:if test="${dataSource eq '1'}"> checked="checked"</c:if>><label for="dataSource_1">全文索引</label>
				<input id="dataSource_2" name="dataSource" type="radio" value="2" <c:if test="${dataSource eq '2'}"> checked="checked"</c:if>><label for="dataSource_2">数据库</label>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">关键词：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="keyword" type="text" class="form-text" size="30" value="${keyword}">
				<span class="span-text" >${error['keyword']}</span>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">标签：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input type="hidden" id="tagId" name="tagId" value="${tagId}"/>
    			<input type="text" class="form-text" id="tagName" name="tagName" disabled="true" size="20" value="${tagName}"/> 
    			<input type="button" class="functionButton5" value="选择..." onClick="javaScript:showTagDiv();">
				<input type="button" class="functionButton5" value="取消" onClick="javaScript:cancelTag();">
				
				
				<span class="span-text" >${error['tagId']}</span>
				&nbsp;<span class="span-help">不限制请留空</span>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">用户名称：</TD>
			<TD class="t-content" width="88%" colSpan="3">
    			<input type="text" class="form-text" id="userName" name="userName" size="20" value="${userName}"/> 
				<span class="span-text" >${error['userName']}</span>
				&nbsp;<span class="span-help">不限制请留空</span>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">发表日期：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="start_postTime" class="date-input" type="text" size="25" value="${start_postTime}">
				<span class="span-text" >${error['start_postTime']}</span>
				&nbsp;&lt;=日期&lt;=&nbsp;
				<input id="end_postTime" class="date-input" type="text" size="25" value="${end_postTime}">&nbsp;<span class="span-help">不限制请留空</span>
				<span class="span-text" >${error['end_postTime']}</span>
			</TD>
		</TR>
		</TBODY>
		<TBODY>
		<TR>
			<TD class="t-button" colSpan="4">
			<span class="submitButton2"><INPUT type="button" value="搜索" onClick="javascript:searchSubmit();return false;"></span>
			
  	</TD>
		</TR>		
	</TBODY>
</TABLE>

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="3%"><INPUT name="all" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onclick="javascript:allSelect(this, 'topicId')"  type="checkbox" ></TH>
    <TH>标题</TH>
    <TH>内容</TH>
    <TH>标签</TH>
    <TH>允许评论</TH>
    <TH>时间</TH>
    <TH>状态</TH>
    <TH>会员/员工</TH>
    <TH>排序</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="3%"><INPUT type="checkbox" value="${entry.id }" name="topicId" onclick="javascript:chooseSelectBox(this);"></TD>
	    <TD width="17%" class="highlight"><enhance:out escapeXml="false">${entry.title}</enhance:out></TD>
		<TD width="19%" class="highlight"><enhance:out escapeXml="false">${entry.content}</enhance:out></TD>
		<TD width="10%">${entry.tagName}</TD>
		<TD width="8%"><c:if test="${entry.allow == true}">允许</c:if><c:if test="${entry.allow == false}"><span style="color: red;">禁止</span></c:if></TD>
	    <TD width="12%">
	    	<fmt:formatDate value="${entry.postTime}" pattern="yyyy-MM-dd HH:mm:ss" />
	    </TD>
	    <TD width="8%">
	    	<c:if test="${entry.status == 10}"><span style="color:red;">待审核</span></c:if>
	    	<c:if test="${entry.status == 20}">已发布</c:if>
	    	<c:if test="${entry.status ==110}"><span style="color: red;">(待审核已删除)</span></c:if>
	    	<c:if test="${entry.status ==120}"><span style="color: red;">(已发布已删除)</span></c:if>
	    </TD>
	    <TD width="9%">
	    	${entry.userName}
	    	<c:if test="${entry.isStaff == true}"><span style="color: green;">(员工)</span></c:if>
	    	
	    </TD>
	    <TD width="6%">${entry.sort}</TD>
	    <TD width="8%">	    		
			<a href='javascript:;' onClick="javascript:window.parent.loadTopic('话题修改','${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=edit&topicId=${entry.id }&timestamp='+ new Date().getTime())">修改</a>&nbsp;
			<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteTopic('${entry.id }');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
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
    $('#start_postTime').calendar({format:'yyyy-MM-dd HH:mm'});
    $('#end_postTime').calendar({format:'yyyy-MM-dd HH:mm'});
});
</script>

</DIV></BODY></HTML>
