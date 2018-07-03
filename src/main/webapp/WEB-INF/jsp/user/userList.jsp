<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户管理</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
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
    	elements = document.getElementsByName("all"); 
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


//删除用户
function deleteUser(){
	if(validateIsSelect("userId")){
		var parameter = "";
		var userId = document.getElementsByName("userId"); 
		if(userId != null && userId.length >0){
	    	for(var i=0;i<userId.length;i++){
	    		if(userId[i].checked){
	    			parameter += "&userId="+userId[i].value;
	    		}
	    	}
	   	}
		parameter += "&queryState=${param.queryState}";
		
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
			"${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
	}else{
		alert("请选择选项");
	}
}

//选择当前项
function selectItem(userId){
	var elements = document.getElementsByName("userId"); 
    if(elements != null && elements.length >0){
    	for(var i=0;i<elements.length;i++){
    		if(!elements[i].disabled){
    			//取消所有项
    			elements[i].checked=false;
    		}
    		if(elements[i].value == userId){
    			//选中已选项
    			elements[i].checked=true;
    		}
    	}
    }
	deleteUser();
}

//还原用户
function reductionUser(){
	if(validateIsSelect("userId")){
		var parameter = "";
		var userId = document.getElementsByName("userId"); 
		if(userId != null && userId.length >0){
	    	for(var i=0;i<userId.length;i++){
	    		if(userId[i].checked){
	    			parameter += "&userId="+userId[i].value;
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
			"${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=reduction&timestamp=" + new Date().getTime(), true,parameter);
	}else{
		alert("请选择选项");
	}
}
</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">

<div class="d-button">
<c:if test="${param.queryState != null && param.queryState != '' && param.queryState == false}">
	<input class="functionButton"  type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/list${config:suffix()}?queryState=true'" value="返回">
</c:if>
<c:if test="${param.queryState == null || param.queryState == '' || param.queryState == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=add'" value="添加会员 ">
</c:if>
<c:if test="${param.queryState == null || param.queryState == '' || param.queryState == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/list${config:suffix()}?queryState=false'" value="回收站">
</c:if>
<c:if test="${param.queryState != null && param.queryState != '' && param.queryState == false}">
	<input class="functionButton" type="button" onClick="javascript:reductionUser();" value="还原">
</c:if>
<input class="functionButton" type="button" onClick="javascript:deleteUser();" value="批量删除">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="3%"><INPUT name="all" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onclick="javascript:allSelect(this, 'userId')"  type="checkbox" ></TH>
    <TH>用户名称</TH>
    <TH>当前积分</TH>
    <TH>会员等级</TH>
    <TH>注册日期</TH>
    <TH>用户状态</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="3%"><INPUT type="checkbox" value="${entry.id }" name="userId" onclick="javascript:chooseSelectBox(this);"></TD>
	    <TD width="22%">${entry.userName }</TD>
	    <TD width="10%">${entry.point}</TD>
	    <TD width="20%">${entry.gradeName}</TD>
	    <TD width="15%"><fmt:formatDate value="${entry.registrationDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="10%">
			<c:if test="${entry.state == 1 || entry.state == 11}">
	    		正常用户
	    	</c:if>
	    	<c:if test="${entry.state == 2 || entry.state == 12}">
	    		禁止用户
	    	</c:if>
		</TD>
	    <TD width="20%">
	    	<a href="${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${entry.id }&queryState=${param.queryState}&userPage=${param.page}">查看</a>
	    	<a href="${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=edit&id=${entry.id }&queryState=${param.queryState}&userPage=${param.page}">修改</a>
	    	<A hidefocus="true" onClick="selectItem(${entry.id}); return false" href="#" ondragstart= "return false">删除</A>
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



