<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>帮助管理</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />


</HEAD>
<script type="text/javascript">


//全选框
function allSelect(allobj,itemName){
	var elements = document.getElementsByName(itemName); 
    var state = allobj.checked;
    if(elements != null && elements.length >0){
    	for(var i=0;i<elements.length;i++){
    		if(!elements[i].disabled) elements[i].checked=state;
    	}
    }else{
    	if(!elements.disabled) elements.checked=state;
    }
}
//验证是否选中
function validateIsSelect(itemName){
	var elements = document.getElementsByName(itemName); 
	if(elements != null && elements.length >0){
    	for(var i=0;i<elements.length;i++){
    		if(elements[i].checked) return true;
    	}
    }
    return false;
}
//选择全选框
function chooseSelectBox(obj){
	var quantity = 0;//选中数量
	var elements = document.getElementsByName(obj.name); 
	if(elements != null && elements.length >0){
    	for(var i=0;i<elements.length;i++){
    		if(elements[i].checked){
    			quantity++;
    		}
    	}
    }
    if(quantity == elements.length){
    	var elements = document.getElementsByName("all"); 
		if(elements != null && elements.length >0){
	    	for(var i=0;i<elements.length;i++){
	    		elements[i].checked = true;
	    	}
	    }
    }
    if(quantity == 0 || quantity <elements.length){
    	var elements = document.getElementsByName("all"); 
		if(elements != null && elements.length >0){
	    	for(var i=0;i<elements.length;i++){
	    		elements[i].checked = false;
	    	}
	    }
    }
}


function showMove(){
	if(validateIsSelect("helpId")){
		showHelpTypePageDiv();
	}else{
		alert("请选择选项");
	}
}


// 弹出查看分类层
function showHelpTypePageDiv(){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择分类",div,700,400);//显示层
	//显示商品分类分页显示	
	helpTypePage(1,-1);
}
//显示帮助分类
function helpTypePage(pages,parentId){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/helpType/manage${config:suffix()}?method=helpTypePageSelect_move&page="+pages+"&parentId="+parentId+"&timestamp=" + new Date().getTime(), true);

}


//添加分类
function addType(id,name){
	updateMove(id);
	//关闭层
	closeDiv();
}

//移动
function updateMove(helpTypeId){
	var parameter = "&helpTypeId="+helpTypeId;
	var helpId = document.getElementsByName("helpId"); 
	if(helpId != null && helpId.length >0){
    	for(var i=0;i<helpId.length;i++){
    		if(helpId[i].checked){
    			parameter += "&helpId="+helpId[i].value;
    		}
    	}
   	}
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
			alert("移动失败");
		}
	},
		"${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=move&timestamp=" + new Date().getTime(), true,parameter);
}

//删除帮助
function deleteHelp(){
	if(validateIsSelect("helpId")){
		
		var parameter = "";
		var helpId = document.getElementsByName("helpId"); 
		if(helpId != null && helpId.length >0){
	    	for(var i=0;i<helpId.length;i++){
	    		if(helpId[i].checked){
	    			parameter += "&helpId="+helpId[i].value;
	    		}
	    	}
	   	}
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
			"${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=delete&visible=${param.visible}&timestamp=" + new Date().getTime(), true,parameter);
	}else{
		alert("请选择选项");
	}
}
//还原帮助
function reductionHelp(){
	if(validateIsSelect("helpId")){
		var parameter = "";
		var helpId = document.getElementsByName("helpId"); 
		if(helpId != null && helpId.length >0){
	    	for(var i=0;i<helpId.length;i++){
	    		if(helpId[i].checked){
	    			parameter += "&helpId="+helpId[i].value;
	    		}
	    	}
	   	}
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
				alert("还原失败");
			}
		},
			"${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=reduction&visible=${param.visible}&timestamp=" + new Date().getTime(), true,parameter);
	}else{
		alert("请选择选项");
	}
}
</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<DIV class="d-box">
<div class="d-button">
<c:if test="${param.visible != null && param.visible != '' && param.visible == false}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/help/list${config:suffix()}?visible=true'" value="返回">
</c:if>
<c:if test="${param.visible == null || param.visible == '' || param.visible == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=add&isHelpList=true'" value="添加帮助">
</c:if>
<c:if test="${param.visible == null || param.visible == '' || param.visible == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/help/list${config:suffix()}?visible=false'" value="回收站">
</c:if>
<c:if test="${param.visible != null && param.visible != '' && param.visible == false}">
	<input class="functionButton" type="button" onClick="javascript:reductionHelp();" value="还原">
</c:if>
<c:if test="${param.visible == null || param.visible == '' || param.visible == true}">
<input class="functionButton" type="button" onClick="javaScript:showMove();" value="移动">
</c:if>
<input class="functionButton" type="button" onClick="javascript:deleteHelp();" value="删除">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH><INPUT name="all" onclick="javascript:allSelect(this, 'helpId')" type="checkbox" ></TH>
    <TH>帮助名称</TH>
    <TH>分类名称</TH>
   	<TH>发布员工</TH>
    <TH>创建时间</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="3%"><INPUT type="checkbox" value="${entry.id}" id="helpId_${entry.id}" name="helpId" onclick="javascript:chooseSelectBox(this);"></TD>
	    <TD width="40%">${entry.name}</TD>
	    <TD width="17%">${entry.helpTypeName}</TD>
	    <TD width="10%">${entry.userName}</TD>
	    <TD width="18%"><fmt:formatDate value="${entry.times}" pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="12%">
	    	<a href="${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=view&helpId=${entry.id}">查看</a>
	    	<a href="${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=edit&visible=${param.visible}&helpId=${entry.id}&isHelpList=true&page=${param.page}">修改</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){document.getElementById('helpId_${entry.id}').checked =true;deleteHelp();return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
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
