<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>模板管理版块表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/browserDetect.js" type="text/javascript"></script>
</HEAD>

<script language="javascript" type="text/javascript"> 
function copyText(obj){ 
	if(BrowserDetect.browser == "Internet Explorer"){
		var rng = obj.createTextRange(); 
		obj.select();  
		rng.scrollIntoView();  
		rng.execCommand("Copy");  
		rng.collapse(false);
		
	}else{
		setClipboard(obj.value);
	}	
} 

function setClipboard(value) {
    var tempInput = document.createElement("input");
    tempInput.style = "position: absolute; left: -1000px; top: -1000px";
    tempInput.value = value;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand("copy");
    document.body.removeChild(tempInput);
}
  
//删除版块
function deleteForum(forumId){
	var parameter = "";

	parameter += "&forumId="+forumId;
	
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
		"${config:url(pageContext.request)}control/forum/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}
</script> 
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">

	
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/forum/manage${config:suffix()}?method=add&layoutId=${param.layoutId}&dirName=${param.dirName}'" value="添加版块" <c:if test="${publicForum eq true}"> disabled='disabled'</c:if>>
</div>
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">
			&nbsp;>>&nbsp;<a href="control/layout/list${config:suffix()}?dirName=${param.dirName}">${templates.name}<span style="color: red">[${param.dirName }]</span> </a>
			&nbsp;>> &nbsp;${layout.name }
			
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 

<form:form>

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH>版块名称</TH>
    <TH>选择模块</TH>
    <TH>调用方式</TH>
    <TH>调用代码</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status">
	  <TR >
	    <TD width="30%">${entry.name }</TD>
	    <TD width="40%">${entry.module}</TD>
	    <TD width="8%">
	    	<c:if test="${entry.invokeMethod == 1}">引用代码</c:if>
	    	<c:if test="${entry.invokeMethod == 2}">调用对象</c:if>
	    </TD>
	    <TD width="8%">	    
	    	<c:if test="${layout.returnData == 0 && layout.type != 6 && entry.invokeMethod == 1}">
	    		<input type="hidden" id="code_${status.count}" value="&lt;@include action=&quot;&#36;&#123;${entry.referenceCode }&#125;&quot;/&gt;">
	    		<input type="button" class="functionButton5"  onClick="copyText(document.getElementById('code_${status.count}'))" value="复制" title="&lt;@include action=&quot;&#36;&#123;${entry.referenceCode }&#125;&quot;/&gt;">
	    		
	    	</c:if>
	    	<c:if test="${entry.invokeMethod == 2}">
	    		<input type="hidden" id="module_${status.count}" name="module" value="${entry.module}">
	    		<input type="hidden" id="referenceCode_${status.count}" value="${entry.referenceCode }">
	    		<input type="hidden" id="code_${status.count}" value="">
	    		<input type="button" id="copyButton_${status.count}" class="functionButton5"  onClick="copyText(document.getElementById('code_${status.count}'))" value="复制" title="">
	    		
	    	</c:if>
	    	
	    </TD>
	    <TD width="14%">
	    
	    	<c:if test="${layout.returnData == 0}">
		    	<a href="control/forumCode/manage${config:suffix()}?method=source&layoutId=${param.layoutId}&dirName=${param.dirName}&page=${param.page}&forumId=${entry.id}">源码编辑</a>
	    	</c:if>
	    	<c:if test="${layout.returnData == 1}">
		    	<a href="control/forumCode/manage${config:suffix()}?method=source&layoutId=${param.layoutId}&dirName=${param.dirName}&page=${param.page}&forumId=${entry.id}">示例程序</a>
	    	</c:if>
	    	<a href="control/forum/manage${config:suffix()}?method=edit&layoutId=${param.layoutId}&dirName=${param.dirName}&page=${param.page}&forumId=${entry.id}">修改</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteForum('${entry.id}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
	 
	 
  </TBODY>
	<script language="javascript" type="text/javascript"> 
	   	function init(){
	   		var moduleList = document.getElementsByName("module");
	   		for(var i = 0; i < moduleList.length; i++){
	   			var module = moduleList[i].value;
	   			var pos=module.lastIndexOf("_");
	   			//引用代码
	   			var moduleCode = module.substr(0,pos);
	   			var number = moduleList[i].id.split("_")[1];
	   			var referenceCode = document.getElementById("referenceCode_"+number).value;
	   			var code = "<@object action=\"\${"+referenceCode+"}\"><#assign value = "+moduleCode+"></@object>";
	   			document.getElementById("code_"+number).value = code;
	   			document.getElementById("copyButton_"+number).title = code;
	   	    }
	   	}
	   	
	   	init();
	   	
	</script>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</form:form>
</DIV></BODY></HTML>
