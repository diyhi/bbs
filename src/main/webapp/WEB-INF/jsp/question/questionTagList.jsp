<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>问题标签管理</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript">
//删除问题标签
function deleteQuestionTag(id){
	var parameter = "";

	parameter += "&questionTagId="+id;

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
		"${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}

</script>
</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">

<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=add&parentId=${param.parentId }'" value="添加问题标签">
</div>

<form:form>

<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;标签导航:</span>
			<span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/questionTag/list${config:suffix()}?parentId=0">全部标签</a></span>
			<span style="float:left;margin-top: 2px;">
			&nbsp;
				<c:forEach items="${navigation}" var="navigationMap" varStatus="status">
					>>&nbsp;<a href="${config:url(pageContext.request)}control/questionTag/list${config:suffix()}?parentId=${navigationMap.key}">${navigationMap.value}</a>
				</c:forEach>
			</span>
		</TD>
	</TR>
</TBODY>
</TABLE>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<THEAD class="t-list-thead">
		<TR>
	  		<TH>标签名称</TH>
		    <TH>排序</TH>
		    <TH>下级标签数量</TH>
		    <TH>添加子标签</TH>
		    <TH>操作</TH>
	    </TR>
    </THEAD>
	<TBODY class="t-list-tbody" align="center">
	<c:forEach items="${pageView.records}" var="entry">
		<TR >
		    <TD width="50%">
		    
			    <c:if test="${entry.childNodeNumber >0}">
			    	 <span style="float:left; margin-left: 3px;"><img src="backstage/images/folder.gif" width="18" height="18" /></span>
			    	<span style="line-height:25px;overflow:hidden;"><a href='${config:url(pageContext.request)}control/questionTag/list${config:suffix()}?parentId=${entry.id }'>${entry.name }</a></span>
			    </c:if>
			    <c:if test="${entry.childNodeNumber ==0}">
			    	 <span style="float:left; margin-left: 3px;"><img src="backstage/images/leaf.gif" width="18" height="18" /></span>
			    	<span style="line-height:15px;overflow:hidden;">${entry.name }</span>
			    </c:if>
		    </TD>
			<TD width="10%">${entry.sort}</TD>
			<TD width="10%">
				<c:if test="${param.parentId == null || param.parentId  == '' || param.parentId  == 0}">
					<font color=red>${entry.childNodeNumber} </font>
				</c:if>
			</TD>
		    <TD width="15%">
		    	<c:if test="${param.parentId == null || param.parentId  == '' || param.parentId  == 0}">
		    		<a href="${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=add&parentId=${entry.id}&childNodeNumber=${entry.childNodeNumber}">添加子标签</a>
		    	</c:if>
		    </TD>
			<TD width="15%">
		    	<a href="${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=edit&questionTagId=${entry.id}&parentId=${param.parentId}">修改</a>
		    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteQuestionTag('${entry.id }');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
			</TD>
		</TR>
	</c:forEach>
	</TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</form:form>
</DIV></BODY></HTML>