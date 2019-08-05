<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>栏目列表</TITLE>
<base href="${config:url(pageContext.request)}">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<link rel="stylesheet" href="backstage/jquery-treetable/css/jquery.treetable.css"  type="text/css" />
<link rel="stylesheet" href="backstage/jquery-treetable/css/jquery.treetable.theme.default.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="JavaScript" src="backstage/jquery-treetable/jquery.treetable.js" ></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript" charset="UTF-8"></script>

<script language="javascript" type="text/javascript">
$(document).ready(function(){
	$("#tableList").treetable({ 
		expandable: true,  //树是否可以展开
		clickableNodeNames: false,//单击节点名字是否打开，默认false;
		initialState: "collapsed",//默认是否展开和关闭： values: "expanded" or "collapsed"

	});
	
	queryColumn();
});


//读取栏目
function queryColumn(){
	var dirName = getUrlParam("dirName");//目录
	
	var data = "dirName=" + dirName;//获取URL参数
	
	// Render loader/spinner while loading 加载时渲染
	$.ajax({
		type:"GET",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/column/manage${config:suffix()}?method=queryColumn",
		data: data,
		success:function(result) {
		//	alert(result);
			if(result != ""){
				var columnList = $.parseJSON(result);
				recursionColumn(columnList);
			}

		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}

//递归循环栏目
function recursionColumn(columnList){
	for(var i = 0 ; i < columnList.length; i++){
		var column = columnList[i];
		
		var rowHtml = createRowHtml(column.id,column.parentId,column);
		$("#tableList").treetable("loadBranch", null, rowHtml);// 向树中插入新行(<tr>s), 传入参数 node 为父节点，rows为待插入的行. 如果父节点node为null ，新行被作为父节点插入 
		
		
		if(column.childColumn!= null && column.childColumn.length >0){
			recursionColumn(column.childColumn);
		}
	}
	
}



//生成行HTML
function createRowHtml(nodeId,parentId,column){
	
	var rowHtml="";
	var branch = "";
	if(column.childColumn.length >0){
		branch =" data-tt-branch=\"true\" ";
	}else{
		branch =" data-tt-branch=\"false\" ";
		
	}

	rowHtml +="<tr data-tt-id=\""+nodeId+"\" data-tt-parent-id=\""+parentId+"\" "+ branch +"columnId=\""+column.id+"\">";
	rowHtml +="<td>";
	if(column.childColumn.length >0){
		rowHtml +="<span class=\"folder\">"+column.name+"</span>";
		
	}else{
		rowHtml +="<span class=\"file\">"+column.name+"</span>";
	}
	
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	if(column.linkMode == 1){
		rowHtml += "无";
	}else if(column.linkMode == 2){
		rowHtml += "外部URL";
	}else if(column.linkMode == 3){
		rowHtml += "内部URL";
	}else if(column.linkMode == 4){
		rowHtml += "空白页";
	}
	rowHtml +="</td>";;
	rowHtml +="<td align=\"center\">";
	rowHtml += column.sort;
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	rowHtml +="<a href=\"#\" onclick=\"javascript:addColumnUI('"+nodeId+"','"+column.id+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">添加下级栏目</a>&nbsp";
	rowHtml +="<a href=\"#\" onclick=\"javascript:updateColumnUI('"+nodeId+"','"+column.id+"','"+column.name+"','"+column.sort+"','"+column.linkMode+"','"+column.url+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">修改</a>&nbsp";
	rowHtml +="<a href=\"#\" onclick=\"javascript:if(window.confirm('确定删除吗? ')){deleteColumnSubmit('"+nodeId+"','"+column.id+"');return false;}else{return false};\" hidefocus=\"true\" ondragstart= \"return false\">删除</a>";

	rowHtml +="</td>";
	rowHtml +="</tr>";
	return rowHtml;
}




//添加栏目UI
function addColumnUI(nodeId,columnId){
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"栏目名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input id=\"name\" class=\"form-text\" size=\"30\" value=\"\"/>&nbsp;<span id=\"error_name_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"排序：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"sort\" size=\"8\" maxlength=\"8\" value=\"\"/>&nbsp;<span class=\"span-help\">数字越大越在前</span><span id=\"error_sort_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"链接方式：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"1\" checked=\"checked\" onclick=\"siteUrl_div(this)\"/>无</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"2\" onclick=\"siteUrl_div(this)\"/>外部URL</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"3\" onclick=\"siteUrl_div(this)\"/>内部URL</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"4\" onclick=\"siteUrl_div(this)\"/>空白页</label>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr id=\"linkURL_tr\" style=\"display: none;\">";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"<span id=\"url_name\"></span>：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"url\" size=\"50\" value=\"\"/><br><span id=\"url_tip\" class=\"span-help\"></span><span id=\"error_url_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";

	
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:addColumnSubmit('"+nodeId+"','"+columnId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("添加栏目",html,600,250);//显示层
}
//添加栏目
function addColumnSubmit(nodeId,columnId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "dirName=" + getUrlParam("dirName")+"&parentId="+encodeURIComponent(columnId);//获取URL参数

	var name = $("#name").val();
	if(name != ""){
		parameter += "&name="+encodeURIComponent(name);
	}
	
	var sort = $("#sort").val();
	if(sort != ""){
		parameter += "&sort="+encodeURIComponent(sort);
	}
	
	var linkMode = $('input[name="linkMode"]:checked ').val();
	if(linkMode != ""){
		parameter += "&linkMode="+encodeURIComponent(linkMode);
	}
	var url = $("#url").val();
	if(url != ""){
		parameter += "&url="+encodeURIComponent(url);
	}
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/column/manage${config:suffix()}?method=add",
		data: parameter,
		beforeSend: function(request) {//表单提交前做表单验证
            //清空所有错误
        	$("[name='error']").html("");
        	
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
        
		success:function(result) {
			
			var data = $.parseJSON(result);
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
        				$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        				queryColumn();
        				return;
        			}
        		}else if(returnValue == "error"){
        			
        			var error = data[returnValue];
        			for(var key in error){
        				$("#error_"+key+"_"+nodeId).html(error[key]);
        			}
        			//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
        		}
        		
        	}
        	
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}


//修改栏目UI
function updateColumnUI(nodeId,columnId,columnName,columnSort,columnLinkMode,columnURL){
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"栏目名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"name\" size=\"30\" value=\""+columnName+"\"/>&nbsp;<span id=\"error_name_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"排序：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"sort\" size=\"8\" maxlength=\"8\" value=\""+columnSort+"\"/>&nbsp;<span class=\"span-help\">数字越大越在前</span><span id=\"error_sort_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"链接方式：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"1\" "+(columnLinkMode == '1' ? ' checked=checked' :'')+" onclick=\"siteUrl_div(this)\"/>无</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"2\" "+(columnLinkMode == '2' ? ' checked=checked' :'')+" onclick=\"siteUrl_div(this)\"/>外部URL</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"3\" "+(columnLinkMode == '3' ? ' checked=checked' :'')+" onclick=\"siteUrl_div(this)\"/>内部URL</label>&nbsp;";
	html += 		"<label><input type=\"radio\" name=\"linkMode\" value=\"4\" "+(columnLinkMode == '4' ? ' checked=checked' :'')+" onclick=\"siteUrl_div(this)\"/>空白页</label>";
	html += 	"</td>";
	html += "</tr>";
	
	var display = "";
	var url_name = "";
	var tip = "";//提示内容
	
	if(columnLinkMode == '1' || columnLinkMode == '4'){
		display = "style=\"display: none;\"";
		url_name = "<span id=\"url_name\"></span>";
	}
	if(columnLinkMode == '2'){
		url_name = "<span id=\"url_name\">外部URL</span>";
		tip = "请填写http或https开头的网址";
	}
	if(columnLinkMode == '3'){
		url_name = "<span id=\"url_name\">内部URL</span>";
		tip = "标识资源的字符串,不能以/为开头。如：productInfo/3.htm";
	}
	
	if(columnLinkMode == '4'){//如果保存的为空白页，则不显示URL
		columnURL = "";
	}
	
	html += "<tr id=\"linkURL_tr\" "+display+">";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		""+url_name+"：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"url\" size=\"50\" value=\""+columnURL+"\"/><br><span id=\"url_tip\" class=\"span-help\">"+tip+"</span><span id=\"error_url_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";

	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:updateColumnSubmit('"+nodeId+"','"+columnId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("修改栏目",html,600,250);//显示层
}
//修改栏目
function updateColumnSubmit(nodeId,columnId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "dirName=" + getUrlParam("dirName")+"&columnId="+encodeURIComponent(columnId);//获取URL参数
	var name = $("#name").val();
	if(name != ""){
		parameter += "&name="+encodeURIComponent(name);
	}
	var sort = $("#sort").val();
	if(sort != ""){
		parameter += "&sort="+encodeURIComponent(sort);
	}
	var linkMode = $('input[name="linkMode"]:checked ').val();
	if(linkMode != ""){
		parameter += "&linkMode="+encodeURIComponent(linkMode);
	}
	var url = $("#url").val();
	if(url != ""){
		parameter += "&url="+encodeURIComponent(url);
	}
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/column/manage${config:suffix()}?method=edit",
		data: parameter,
        beforeSend: function(request) {//表单提交前做表单验证
            //清空所有错误
        	$("[name='error']").html("");
        	
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
			
			var data = $.parseJSON(result);
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
        				$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        				queryColumn();
        				return;
        			}
        		}else if(returnValue == "error"){
        			
        			var error = data[returnValue];
        			for(var key in error){
        				$("#error_"+key+"_"+nodeId).html(error[key]);
        			}
        			//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
        		}
        		
        	}
        	
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}

//删除栏目
function deleteColumnSubmit(nodeId,columnId) {
	var parameter = "dirName=" + getUrlParam("dirName")+"&columnId="+encodeURIComponent(columnId);//获取URL参数
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/column/manage${config:suffix()}?method=delete",
		data: parameter,
		beforeSend: function(request) {//表单提交前做表单验证
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
			
			var data = $.parseJSON(result);
			for(var returnValue in data){
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        				queryColumn();
        				return;
        			}
        		}
        	}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}


//设置URL层
function siteUrl_div(obj){
	//链接方式 
	var linkMode = obj.value;	
	if(linkMode == 2){
		document.getElementById("linkURL_tr").style.display = "";
		document.getElementById("url_name").innerHTML = "外部URL";
		document.getElementById("url_tip").innerHTML = "请填写http或https开头的网址";
	}else if(linkMode == 3){
		document.getElementById("linkURL_tr").style.display = "";
		document.getElementById("url_name").innerHTML = "内部URL";
		document.getElementById("url_tip").innerHTML = "标识资源的字符串,不能以/为开头。如：productInfo/3.htm";
	}else{
		document.getElementById("linkURL_tr").style.display = "none";
	}
}


</script>
</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>


<DIV class="body-box">
<div class="d-button" style="width: 99%;margin-left: auto; margin-right: auto; ">

<input class="functionButton" type="button" onclick="javascript:addColumnUI('','');" value="添加栏目">
</div>
<DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TR>
	    <TD class="t-content" colSpan="5" height="28px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">

			&nbsp;>>&nbsp;<a href="control/layout/list${config:suffix()}?dirName=${param.dirName}">${templates.name}<span style="color: red">[${param.dirName }]</span> </a>
			&nbsp;>> &nbsp;栏目列表
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 

<table id="tableList" class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<thead class="t-list-thead">
	<tr>
	    <th width="65%">栏目名称</th>
	    <th width="8%">链接方式</th>
	    <th width="10%">排序</th>
	    <th width="17%">操作</th>
    </tr>
	</thead>
	<tbody class="t-list-tbody" >
	</tbody>
</table>

</DIV>

</DIV></BODY></HTML>
