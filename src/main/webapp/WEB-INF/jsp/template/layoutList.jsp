<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>布局管理</TITLE>
<base href="${config:url(pageContext.request)}">
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
/**
function copyText(objs){ 
alert(objs.value);
	var obj= objs;
  	obj.select(); 
  	js=obj.createTextRange(); 
  	js.execCommand("Copy");
} **/

//跳转路径
function jump(url){
	window.location.href = url;
}

//删除布局
function deleteLayout(layoutId,dirName){
	var parameter = "";

	parameter += "&layoutId="+layoutId;
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
			alert("删除失败");
		}
	},
		"${config:url(pageContext.request)}control/layout/manage${config:suffix()}?method=deleteLayout&timestamp=" + new Date().getTime(), true,parameter);
}
</script> 
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<form:form>	
<div class="d-button">
	
 	<input class="functionButton" type="button" onClick="javascript:jump('${config:url(pageContext.request)}control/layout/manage${config:suffix()}?method=add&dirName=${param.dirName}')" value="添加布局">	
	<input class="functionButton" type="button" onClick="javascript:jump('${config:url(pageContext.request)}control/column/manage${config:suffix()}?method=list&dirName=${param.dirName}')" value="栏目列表">	
	
</div>

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">
			&nbsp;>>&nbsp;${templates.name }<span style="color: red">[${param.dirName }]</span>
			
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">

  <THEAD class="t-list-thead">
  <TR>
    <TH>布局名称</TH>
    <TH>布局文件</TH>
    <TH>最后修改时间</TH>
    <TH>类型</TH>
    <TH>引用代码/URL</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status">
	  <TR >
	    <TD width="14%">${entry.name }
	    </TD>
	    <TD width="17%"><font color="red">${entry.layoutFile}</font></TD>
	    <TD width="21%">
	    <c:if test="${entry.layoutFile != null && entry.layoutFile != ''}">
		    <c:set var="layoutFile" value="${entry.layoutFile}"></c:set>
		    <c:if test="${pc_lastModified[layoutFile] != null}">
	    		电脑版：<fmt:formatDate value="${pc_lastModified[layoutFile]}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	   		</c:if>
	   		<c:if test="${wap_lastModified[layoutFile] != null}">
	   			<br>移动版：<fmt:formatDate value="${wap_lastModified[layoutFile]}"  pattern="yyyy-MM-dd HH:mm:ss"/>
	   		</c:if>
	    </c:if>
	    </TD>
	    <TD width="15%">
	    	<c:if test="${entry.type == 1}">默认页</c:if>
	    	<c:if test="${entry.type == 2}">商品分类</c:if>
	    	<c:if test="${entry.type == 3}">更多</c:if>
	    	<c:if test="${entry.type == 4}">空白页</c:if>
	    	<c:if test="${entry.type == 5}">公共页(生成新引用页)</c:if>
	    	<c:if test="${entry.type == 6}">公共页(引用版块值)</c:if>
	    	<c:if test="${entry.type == 7}">站点栏目详细页</c:if>
	    	<c:if test="${entry.type == 4}">
	    		<c:if test="${entry.returnData == 0}">
	    			(html)
	    		</c:if>
	    		<c:if test="${entry.returnData == 1}">
	    			(json)
	    		</c:if>
	    	</c:if>
	    </TD>
	    <TD width="11%">
	    	<!-- 1.默认页-->
	    	<c:if test="${entry.type == 1}">
	    		
	    	</c:if>
	    	<!--4.空白页 -->
	    	<c:if test="${entry.type == 4}">
	    		<input type="hidden" id="referenceCode_${status.count}" value="${config:url(pageContext.request)}${entry.referenceCode}">
	    		<input type="button" name="Submit" onClick="copyText(document.getElementById('referenceCode_${status.count}'))" value="URL">
	    	</c:if>
	    	<!-- 5.公共页(生成新引用页)  6.公共页(引用版块值)  -->
	    	<c:if test="${entry.type == 5 || entry.type ==6}">
	    	
	    		<input type="hidden" id="referenceCode_${status.count}" value="&lt;@include action=&quot;&#36;&#123;${entry.referenceCode}&#125;&quot;/&gt;">
	    		<input type="button" name="Submit" onClick="copyText(document.getElementById('referenceCode_${status.count}'))" value="代码">
	    	</c:if>
	    </TD>
	    <TD width="22%">
	   		<c:if test="${entry.type eq 6}"><a href="control/forum/list${config:suffix()}?layoutId=${entry.id}&dirName=${entry.dirName}">浏览版块</a></c:if>
	   		<c:if test="${entry.type ne 6}"><a href="control/forum/list${config:suffix()}?layoutId=${entry.id}&dirName=${entry.dirName}">浏览版块</a></c:if>
	   		
			<c:if test="${entry.type eq 6 || entry.returnData eq 1}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;----&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
	    
	    	<c:if test="${entry.type ne 6 && entry.returnData ne 1}"><a href="control/layout/manage${config:suffix()}?method=editLayoutCode&layoutId=${entry.id}&dirName=${entry.dirName}&page=${param.page}">布局代码编辑</a></c:if>
	    	
			<a href="control/layout/manage${config:suffix()}?method=editLayout&layoutId=${entry.id}&dirName=${entry.dirName}&page=${param.page}">修改</a>
	  		<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteLayout('${entry.id }','${entry.dirName}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
  
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->
</form:form>
</DIV>
<script type="text/javascript">
function copyText(obj){ 
	if(BrowserDetect.browser == "Internet Explorer"){
	
		var rng = obj.createTextRange(); 
		obj.select();  
		rng.scrollIntoView();  
		rng.execCommand("Copy");  
		rng.collapse(false);
		
	}else{
		setClipboard(obj.value);
//		var clipboard = new Clipboard('.btn');
//		alert("dd");
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


</script>
</BODY>
</HTML>
