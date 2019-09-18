<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>会员卡列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script type="text/javascript">

//删除会员卡
function deleteMembershipCard(id){
	var parameter = "";

	parameter += "&membershipCardId="+id;

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
		"${config:url(pageContext.request)}control/membershipCard/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
}

</script>

</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<DIV class="d-box">
<div class="d-button">
<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/membershipCard/manage${config:suffix()}?method=add'" value="添加会员卡">
</div>

<form:form>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH>名称</TH>
    <TH>创建时间</TH>
    <TH>销售价</TH>
    <TH>积分</TH>
    <TH>是否上架</TH>
    <TH>排序</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	  	<TD width="20%">${entry.name}</TD>
	  	<TD width="20%"><fmt:formatDate value="${entry.createDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	  	<TD width="15%">
	    	<c:if test="${entry.lowestPrice == entry.highestPrice}">${entry.lowestPrice}</c:if>
	    	<c:if test="${entry.lowestPrice != entry.highestPrice}">${entry.lowestPrice} - ${entry.highestPrice}</c:if>
	    </TD>
	    <TD width="15%">
	    	<c:if test="${entry.lowestPoint == entry.highestPoint}">${entry.lowestPoint}</c:if>
	    	<c:if test="${entry.lowestPoint != entry.highestPoint}">${entry.lowestPoint} - ${entry.highestPoint}</c:if>
	    </TD>
	    <TD width="10%"><c:if test="${entry.state == 1 || entry.state == 11}">上架</c:if><c:if test="${entry.state == 2 || entry.state == 12}"><span style="color: red;">下架</span></c:if></TD>
	  	<TD width="10%">${entry.sort}</TD>
	  	<TD width="10%">
	    	<a href="${config:url(pageContext.request)}control/membershipCard/manage${config:suffix()}?method=edit&membershipCardId=${entry.id }">修改</a>
	    <a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteMembershipCard('${entry.id }');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
</form:form>
</DIV></BODY></HTML>
