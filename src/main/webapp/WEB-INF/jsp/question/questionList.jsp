<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>问题管理</TITLE>
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


//删除问题
function deleteQuestion(){
	if(validateIsSelect("questionId")){
		
		var parameter = "";
		var questionId = document.getElementsByName("questionId"); 
		if(questionId != null && questionId.length >0){
	    	for(var i=0;i<questionId.length;i++){
	    		if(questionId[i].checked){
	    			parameter += "&questionId="+questionId[i].value;
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
			"${config:url(pageContext.request)}control/question/manage${config:suffix()}?method=delete&visible=${param.visible}&timestamp=" + new Date().getTime(), true,parameter);
	}else{
		alert("请选择选项");
	}
}
//还原话题
function reductionQuestion(){
	if(validateIsSelect("questionId")){
		var parameter = "";
		var questionId = document.getElementsByName("questionId"); 
		if(questionId != null && questionId.length >0){
	    	for(var i=0;i<questionId.length;i++){
	    		if(questionId[i].checked){
	    			parameter += "&questionId="+questionId[i].value;
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
			"${config:url(pageContext.request)}control/question/manage${config:suffix()}?method=reduction&visible=${param.visible}&timestamp=" + new Date().getTime(), true,parameter);
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
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/question/list${config:suffix()}?visible=true'" value="返回">
</c:if>
<c:if test="${param.visible == null || param.visible == '' || param.visible == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/question/manage${config:suffix()}?method=add&isQuestionList=true'" value="添加问题">
</c:if>
<c:if test="${param.visible == null || param.visible == '' || param.visible == true}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/question/list${config:suffix()}?visible=false'" value="回收站">
</c:if>
<c:if test="${param.visible != null && param.visible != '' && param.visible == false}">
	<input class="functionButton" type="button" onClick="javascript:reductionQuestion();" value="还原">
</c:if>
<input class="functionButton" type="button" onClick="javascript:deleteQuestion();" value="删除">
</div>
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH><INPUT name="all" onclick="javascript:allSelect(this, 'questionId')" type="checkbox" ></TH>
    <TH>问题标题</TH>
    <TH>标签名称</TH>
    <TH>状态</TH>
    <TH>允许评论</TH>
   	<TH>会员/员工</TH>
    <TH>发布时间</TH>
    <TH>排序</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="3%"><INPUT type="checkbox" value="${entry.id}" id="questionId_${entry.id}" name="questionId" onclick="javascript:chooseSelectBox(this);"></TD>
	    <TD width="24%">${entry.title}</TD>
	    <TD width="13%">
	    	 <c:forEach items="${entry.questionTagAssociationList}" var="questionTagAssociation">
	    	 	${questionTagAssociation.questionTagName}&nbsp;&nbsp;
	    	 </c:forEach>
	    </TD>
	    <TD width="8%"><c:if test="${entry.status == 10 || entry.status == 110}"><span style="color:red;">待审核</span></c:if><c:if test="${entry.status == 20 || entry.status == 120}">已发布</c:if></TD>
	    <TD width="8%"><c:if test="${entry.allow == true}">允许</c:if><c:if test="${entry.allow == false}"><span style="color: red;">禁止</span></c:if></TD>
	    <TD width="13%">${entry.userName}<c:if test="${entry.isStaff == true}"><span style="color: green;">[员工]</span></c:if></TD>
	    <TD width="15%"><fmt:formatDate value="${entry.postTime}" pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD width="6%">${entry.sort}</TD>
	    <TD width="10%">
	    	<a href="${config:url(pageContext.request)}control/question/manage${config:suffix()}?method=view&questionId=${entry.id}&visible=${param.visible}&questionPage=${param.page}">查看</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){document.getElementById('questionId_${entry.id}').checked =true;deleteQuestion();return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
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
