<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户自定义注册功能项</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">
//删除用户自定义注册功能项
function deleteCustom(id){
	var parameter = "";
	parameter += "&id="+id;
	
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
		"${config:url(pageContext.request)}control/userCustom/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);

}
</script>

</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userCustom/manage${config:suffix()}?method=add'" value="添加会员注册项 ">
</div>
 
<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>注册项名称</TH>
    <TH>选项框</TH>
    <TH>提示</TH>
    <TH>注册项类型</TH>
    <TH>是否显示</TH>
    <TH>排序</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${userCustom}" var="entry">
	  <TR >
	    <TD width="20%">${entry.name }</TD>
	    <TD width="25%">
	    	<c:if test="${entry.chooseType == 1}">
				<input type="text"/>
			</c:if>
		    <c:if test="${entry.chooseType == 2}">
				<input type="radio"/>
			</c:if>
		    <c:if test="${entry.chooseType == 3}">
				<input type="checkbox"/>
			</c:if>
		    <c:if test="${entry.chooseType == 4}">
				<select>
					<option></option>
				</select>
			</c:if>
		    <c:if test="${entry.chooseType == 5}">
		    	<textarea rows="1" cols="15" ></textarea>
			</c:if>
	    </TD>
	     <TD width="23%">
	    	
	    </TD>
	    <TD width="10%">
	    	<c:if test="${entry.chooseType == 1}">输入框</c:if>
		    <c:if test="${entry.chooseType == 2}">单选按钮</c:if>
		    <c:if test="${entry.chooseType == 3}">多选按钮</c:if>
		    <c:if test="${entry.chooseType == 4}">下拉列表</c:if>
		    <c:if test="${entry.chooseType == 5}">文本域</c:if>
	    </TD>
	    <TD width="10%">
	    	<c:if test="${entry.visible == true}">显示</c:if>
	    	<c:if test="${entry.visible == false}">隐藏</c:if>
	    </TD>
	    <TD width="5%">${entry.sort}</TD>
	    <TD width="7%">
	    	<a href="${config:url(pageContext.request)}control/userCustom/manage${config:suffix()}?method=edit&id=${entry.id }">修改</a>
	    	<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteCustom('${entry.id }');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
</form:form>
</DIV></BODY></HTML>
