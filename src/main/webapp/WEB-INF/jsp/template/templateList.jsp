<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD><TITLE>模板</TITLE>
<base href="${config:url(pageContext.request)}">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript">
//删除模板
function deleteTemplate(id){
	var parameter = "";

	parameter += "&dirName="+id;
	
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
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}

//导出模板
function exportTemplate(dirName){

	var parameter = "";

	parameter += "&dirName="+dirName;
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	var exportMessage = document.getElementById("exportMessage_"+dirName);
	exportMessage.innerHTML= "备份中...";
	
   	post_request(function(value){
		if(value == "1"){
			exportMessage.innerHTML="备份完成";
		}else{
			alert("导出失败");
		}
	},
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=export&timestamp=" + new Date().getTime(), true,parameter);
}


//设为使用
function setTemplate(dirName){

	var parameter = "";

	parameter += "&dirName="+dirName;
	
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
			alert("设置失败");
		}
	},
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=setTemplate&timestamp=" + new Date().getTime(), true,parameter);
}
</script>

</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>

<DIV class="d-box">
<div class="d-button">
 	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=add'" value="添加模板">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=importTemplateList'" value="导入模板 ">
</div>

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH>模板名称</TH>
    <TH>模板缩略图</TH>
    <TH>目录名称</TH>
    <TH>当前使用中</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
  	<c:forEach items="${templatesDir}" var="templates">
   		<TR > 	
	   		<TD width="22%">${templates.name }</TD>
		    <TD width="18%">
		    	<c:if test="${templates.thumbnailSuffix != null && templates.thumbnailSuffix != ''}">
		    		<img src="${config:url(pageContext.request)}common/${templates.dirName}/templates.${templates.thumbnailSuffix}" width="140"/>
		    	</c:if>
		    </TD>
		    <TD width="24%">${templates.dirName}</TD>
			<TD width="11%">
				<c:if test="${templates.uses eq true}"><span style="color: green;">是</span></c:if>
				<c:if test="${templates.uses eq false}">否&nbsp;<a href="#" onclick="javascript:setTemplate('${templates.dirName}');return false;" hidefocus="true" ondragstart= "return false">[设为使用]</a></c:if>
			</TD>
			<TD width="25%">
				<a href="control/forumCode/list${config:suffix()}?dirName=${templates.dirName}">版块代码</a> 
				<a href="control/resource/list${config:suffix()}?dirName=${templates.dirName}">资源</a> 
				<a href="control/layout/list${config:suffix()}?dirName=${templates.dirName}">布局</a>
				<a href="#" onclick="javascript:exportTemplate('${templates.dirName}');return false;" hidefocus="true" ondragstart= "return false">导出</a>
				<a href="control/template/manage${config:suffix()}?method=edit&dirName=${templates.dirName}">修改</a> 
			  	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteTemplate('${templates.dirName}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
			  	<span id="exportMessage_${templates.dirName}" class="span-text"></span>
			</TD>   	
		</TR>
	</c:forEach>
  </TBODY>
</TABLE>
</DIV></BODY></HTML>
