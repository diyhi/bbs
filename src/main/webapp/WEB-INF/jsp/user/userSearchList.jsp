<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户搜索列表</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
</HEAD>
<script type="text/javascript">

//设置查询参数
function siteQuery(obj){
	if(obj.value == "1"){//用户名
		document.getElementById("searchParameter_1").style.display="";//用户名
		document.getElementById("searchParameter_3").style.display="none";//积分
		document.getElementById("searchParameter_5").style.display="none";//注册日期
		document.getElementById("searchParameter_6").style.display="none";//用户自定义注册功能项
	}else if(obj.value == "2"){//筛选条件
		document.getElementById("searchParameter_1").style.display="none";//用户名
		document.getElementById("searchParameter_3").style.display="";//积分
		document.getElementById("searchParameter_5").style.display="";//注册日期
		document.getElementById("searchParameter_6").style.display="";//用户自定义注册功能项
	}
}

//搜集提交
function searchSubmit(){
	var parameter = "";
	//查询类型
	var searchType_obj = document.getElementById("searchType");
	var index = searchType_obj.selectedIndex; // 选中索引
	var searchType = searchType_obj.options[index].value; // 选中值

	//查询类型
	parameter += "&searchType="+searchType;
	
	//用户名
	var userName = document.getElementById("userName").value;
	if(userName != ""){
		parameter += "&userName="+encodeURIComponent(userName);
	}
	//积分 起始
	var start_point = document.getElementById("start_point").value;
	if(start_point != ""){
		parameter += "&start_point="+encodeURIComponent(start_point);
	}
	//积分 结束
	var end_point = document.getElementById("end_point").value;
	if(end_point != ""){
		parameter += "&end_point="+encodeURIComponent(end_point);
	}
	//注册日期 起始
	var start_registrationDate = document.getElementById("start_registrationDate").value;
	if(start_registrationDate != ""){
		parameter += "&start_registrationDate="+encodeURIComponent(start_registrationDate);
	}
	//注册日期 结束
	var end_registrationDate = document.getElementById("end_registrationDate").value;
	if(end_registrationDate != ""){
		parameter += "&end_registrationDate="+encodeURIComponent(end_registrationDate);
	}
	parameter += userCustomParameter();

	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	
	window.location.href = getUrl()+"?"+parameter;
};

//获取自定义表单参数
function userCustomParameter(){ 
	var parameter = "";
	//自定义参数Id
	var userCustomIdList = document.getElementsByName("userCustomId");
	if(userCustomIdList != null){
		for(var i = 0; i< userCustomIdList.length; i++){
			var userCustomObj = userCustomIdList[i];
			
			//选框类型
			var chooseType = userCustomObj.getAttribute("chooseType");
		
			if(chooseType == 1){//输入框
				var value = document.getElementById("userCustom_"+userCustomObj.value).value;
				if(value != ""){
					parameter += "&userCustom_"+userCustomObj.value+"="+encodeURIComponent(value);
				}
			}else if(chooseType == 2){//单选按钮		
				var radio_obj = document.getElementsByName("userCustom_"+userCustomObj.value);
				if(radio_obj != null){
					for(var j = 0; j < radio_obj.length; j++){ 
						if(radio_obj[j].checked){
							parameter += "&userCustom_"+userCustomObj.value+"="+encodeURIComponent(radio_obj[j].value);
						}
					 }
				}
			}else if(chooseType == 3){//多选按钮	
				var checkbox_obj = document.getElementsByName("userCustom_"+userCustomObj.value);
				for(var j = 0; j < checkbox_obj.length; j++){ 	
					if(checkbox_obj[j].checked){
						parameter += "&userCustom_"+userCustomObj.value+"="+encodeURIComponent(checkbox_obj[j].value);
					}
			    }
			}else if(chooseType == 4){//下拉列表
				var select_obj = document.getElementById("userCustom_"+userCustomObj.value);
				if(select_obj != null){
					for(var j = 0; j < select_obj.length; j++){ 
						if(select_obj[j].selected){
							parameter += "&userCustom_"+userCustomObj.value+"="+encodeURIComponent(select_obj[j].value);
						}
					 }
				}
			}else if(chooseType == 5){//文本域
				var value = document.getElementById("userCustom_"+userCustomObj.value).value;
				if(value != ""){
					parameter += "&userCustom_"+userCustomObj.value+"="+encodeURIComponent(value);
				}
			}
			
		}
	}
	return parameter;
}



//删除用户
function deleteUser(userId,state){ 
	var parameter = "";

	parameter += "&userId="+userId;
	
	if(state == 1 || state == 2){//正常页面
		parameter += "&queryState=true";
	}
	if(state == 11 || state == 12){//回收站
		parameter += "&queryState=false";
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
		"${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);

}


</script>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script type="text/javascript" src="backstage/lhgcalendar/lhgcore.lhgcalendar.min.js" language="javascript" ></script>
<DIV class="d-box">
<form:form>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
			<TD class="t-label t-label-h" width="12%">搜索类型：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<select id="searchType" onchange="siteQuery(this);">
					<option value="1" <c:if test="${searchType eq '1'}"> selected="selected"</c:if>>用户名</option>
					<option value="2" <c:if test="${searchType eq '2'}"> selected="selected"</c:if>>筛选条件</option>
				</select>
			</TD>
		</TR>
		<TR id="searchParameter_1" <c:if test="${searchType eq 2 }"> style="DISPLAY: none"</c:if>>
			<TD class="t-label t-label-h" width="12%">用户名：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="userName" type="text" size="30" value="${userName}">
				<span class="span-text" >${error['userName']}</span>
			</TD>
		</TR>
		<TR id="searchParameter_3" <c:if test="${searchType eq 1}"> style="DISPLAY: none"</c:if>>
			<TD class="t-label t-label-h" width="12%">积分：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="start_point" type="text" size="20" value="${start_point}">
				<span class="span-text" >${error['start_point']}</span>
				&nbsp;&lt;=积分&lt;=&nbsp;
				<input id="end_point" type="text" size="20" value="${end_point}">&nbsp;<span class="span-help">不限制请留空</span>
				<span class="span-text" >${error['end_point']}</span>
			</TD>
		</TR>
		<TR id="searchParameter_5" <c:if test="${searchType eq 1 }"> style="DISPLAY: none"</c:if>>
			<TD class="t-label t-label-h" width="12%">注册日期：</TD>
			<TD class="t-content" width="88%" colSpan="3">
				<input id="start_registrationDate" class="date-input" type="text" size="25" value="${start_registrationDate}">
				<span class="span-text" >${error['start_registrationDate']}</span>
				&nbsp;&lt;=日期&lt;=&nbsp;
				<input id="end_registrationDate" class="date-input" type="text" size="25" value="${end_registrationDate}">&nbsp;<span class="span-help">不限制请留空</span>
				<span class="span-text" >${error['end_registrationDate']}</span>
			</TD>
		</TR>
		</TBODY>
		<!-- 用户自定义注册功能项 -->
		<TBODY id="searchParameter_6" <c:if test="${searchType eq 1 }"> style="DISPLAY: none"</c:if>>
	<c:forEach items="${userCustomList}" var="entry" varStatus="status">
	<TR >
    <TD class="t-label t-label-h" width="12%">${entry.name}：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input type="hidden" name="userCustomId" value="${entry.id}" chooseType="${entry.chooseType}">
		<c:if test="${entry.chooseType ==2}">
			<input type="radio" name="userCustom_${entry.id}" value="0" <c:if test="${fn:length(entry.userInputValueList)==0 }"> checked='checked'</c:if>>无
			<c:forEach items="${entry.itemValue}" var="itemValue" varStatus="status">
				<!-- 选中项 -->
				<c:set var="_checked" value=""></c:set>
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
					<c:if test="${userInputValue.options == itemValue.key}">
						<c:set var="_checked" value=" checked='checked'"></c:set>
					</c:if>
				</c:forEach>
			
				<!-- 默认选第一项 -->				
				<input type="radio" name="userCustom_${entry.id}" value="${itemValue.key}" ${_checked} >${itemValue.value}
			</c:forEach>
		</c:if>
		<c:if test="${entry.chooseType ==3}">
			<c:forEach items="${entry.itemValue}" var="itemValue">
				<c:set var="_checked" value=""></c:set>
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
					<c:if test="${userInputValue.options == itemValue.key}">
						<c:set var="_checked" value=" checked='checked'"></c:set>
					</c:if>
				</c:forEach>
			
			
				<input type="checkbox" name="userCustom_${entry.id}" value="${itemValue.key}" ${_checked}>${itemValue.value}
			</c:forEach>
		</c:if>
		<c:if test="${entry.chooseType ==4}">
		
			<select id="userCustom_${entry.id}" <c:if test="${entry.multiple == true}"> multiple='multiple'</c:if> <c:if test="${entry.selete_size != null}"> size='${entry.selete_size}'</c:if>>
				<option value="0">请选择</option>	
				<c:forEach items="${entry.itemValue}" var="itemValue">
					<c:set var="_selected" value=""></c:set>
					<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
						<c:if test="${userInputValue.options == itemValue.key}">
							<c:set var="_selected" value=" selected='selected'"></c:set>
						</c:if>
					</c:forEach>
				
					<option value="${itemValue.key}" ${_selected}>${itemValue.value}</option>		
				</c:forEach>	
			</select>
		</c:if>
		<c:set var="userCustom_id" value="userCustom_${entry.id}"></c:set>
		<SPAN class="span-text">${error[userCustom_id]}</SPAN>
		<SPAN class="span-help">${entry.tip}</SPAN>
    </TD>
  </TR>
  </c:forEach>
		</TBODY>
		<TBODY>
		<TR>
			<TD class="t-button" colSpan="4">
        	<span class="submitButton2"><INPUT type="button" value="搜索" onClick="javascript:searchSubmit(this.form);return false;" ></span>
  	</TD>
		</TR>		
	</TBODY>
</TABLE>



<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
    <TH width="25%">用户名</TH>
    <TH width="15%">呢称</TH>
    <TH width="15%">积分</TH>
    <TH width="15%">注册日期</TH>
    <TH width="10%">用户状态</TH>
    <TH width="20%">操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry">
	  <TR >
	    <TD>${entry.userName}</TD>
	    <TD>${entry.nickname}</TD>
		<TD>${entry.point}</TD>
	    <TD><fmt:formatDate value="${entry.registrationDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD>
	    	<c:if test="${entry.state == 1}">
	    		正常用户
	    	</c:if>
	    	<c:if test="${entry.state == 11}">
	    		<SPAN class="span-text">正常用户删除</SPAN>
	    	</c:if>
	    	<c:if test="${entry.state == 2}">
	    		禁止用户
	    	</c:if>
	    	<c:if test="${entry.state == 12}">
	    		<SPAN class="span-text">禁止用户删除</SPAN>
	    	</c:if>
	    </TD>
	    <TD>
			<a href='javascript:;' onClick="javascript:window.parent.loadFrame('${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=show&id=${entry.id }&queryState=${param.queryState}&jumpStatus=-12&timestamp='+ new Date().getTime(),'用户查看')">查看</a>&nbsp;
			<a href='javascript:;' onClick="javascript:window.parent.loadFrame('${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=edit&id=${entry.id }&jumpStatus=-10&timestamp='+ new Date().getTime(),'用户修改')">修改</a>&nbsp;
			<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteUser('${entry.id }','${entry.state}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>	
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
//JS日期控件
$(function(){
    $('#start_registrationDate').calendar({format:'yyyy-MM-dd HH:mm'});
    $('#end_registrationDate').calendar({format:'yyyy-MM-dd HH:mm'});
});
</script>
</BODY></HTML>
