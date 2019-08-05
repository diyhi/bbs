<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>版块代码管理</TITLE>
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
		clickableNodeNames: true,//单击节点名字是否打开，默认false;
		initialState: "collapsed",//默认是否展开和关闭： values: "expanded" or "collapsed"
		
		
		onNodeExpand: function() {// 分支展开后的回调函数	
			var node = this;

			//判断当前节点是否已经拥有子节点
			var childSize = $("#tableList").find("[data-tt-parent-id='" + node.id + "']").length;
			if (childSize > 0) { 
				 return; 
			}
		
			//判断当前节点是否是二级目录
			var tr = $("#tableList").find("[data-tt-id='" + node.id + "']");
			if(tr.attr("isLeaf") == "true"){
				
			//	alert(tr.attr("forumCodeName"));
				//查询版块代码
				queryForumCode(node,tr.attr("forumCodeName"));
				
			}
			
			
			
			/**
			$("[data-tt-id='" + node.id + "']").each(function(){
				var td=$(this);
				alert(td.attr("id"));
				
			});**/
			
			//读取子节点
			
		}
		
	});
	queryForumDirectory();
});

//查询版块目录
function queryForumDirectory(){
	var dirName = getUrlParam("dirName");//目录
	
	var data = "dirName=" + dirName;//获取URL参数
	
	// Render loader/spinner while loading 加载时渲染
	$.ajax({
		type:"GET",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/forumCode/query${config:suffix()}?method=directory",
		data: data,
		success:function(result) {
		//	alert(result);
			if(result != ""){
				var forumCodeNodeList = $.parseJSON(result);
				traversedForumDirectory(forumCodeNodeList);
			}

		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}

//遍历循环版块目录
function traversedForumDirectory(forumCodeNodeList){
	for(var i = 0 ; i < forumCodeNodeList.length; i++){
		var forumCode = forumCodeNodeList[i];
		var rowHtml = createDirectoryRowHtml(forumCode.nodeId,0,false,forumCode);
		$("#tableList").treetable("loadBranch", null, rowHtml);// 向树中插入新行(<tr>s), 传入参数 node 为父节点，rows为待插入的行. 如果父节点node为null ，新行被作为父节点插入 

		var node = $("#tableList").treetable("node", forumCode.nodeId);//节点对象
		
		if(forumCode.childNode!= null && forumCode.childNode.length >0){
			for(var j = 0 ; j < forumCode.childNode.length; j++){
				var childForumCode = forumCode.childNode[j];
				var rowHtml = createDirectoryRowHtml(childForumCode.nodeId,forumCode.nodeId,true,childForumCode);
				$("#tableList").treetable("loadBranch", node, rowHtml);// 向树中插入新行(<tr>s), 传入参数 node 为父节点，rows为待插入的行. 如果父节点node为null ，新行被作为父节点插入 

			}
		}
	}
}

//生成目录行HTML
function createDirectoryRowHtml(nodeId,parentId,isLeaf,forumCode){
	
	var rowHtml="";
	var branch = "";
	if(forumCode.childNode.length >0){
		
		branch =" data-tt-branch=\"true\" ";
	}else{
		branch =" data-tt-branch=\"false\" ";
		
		
	}
	
	var displayType_str = "";
	if(forumCode.displayType != null && forumCode.displayType.length >0){
		displayType_str = forumCode.displayType.join(",");
	}
	
	rowHtml +="<tr data-tt-id=\""+nodeId+"\" data-tt-parent-id=\""+parentId+"\" "+ branch +" forumCodeName=\""+forumCode.nodeName+"\" isLeaf=\""+isLeaf+"\" displayType=\""+displayType_str+"\">";
	rowHtml +="<td>";
	rowHtml +="<span class=\"folder\">"+forumCode.nodeName+"</span>";
	
	
	rowHtml +="</td>";
	rowHtml +="</td>";
	
	rowHtml +="<td align=\"center\">";	
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";	
	if(isLeaf == true){
		rowHtml +="<a href=\"#\" onclick=\"javascript:addForumCodeUI('"+nodeId+"','"+displayType_str+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">添加版块代码</a>";
	}
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	rowHtml +="</td>";
	rowHtml +="</tr>";
	return rowHtml;
}


//查询版块代码
function queryForumCode(node,childNodeName){
	var dirName = getUrlParam("dirName");//目录
	
	var data = "dirName=" + dirName;//获取URL参数
	data += "&childNodeName="+encodeURIComponent(childNodeName);
	
	
	// Render loader/spinner while loading 加载时渲染
	$.ajax({
		type:"GET",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/forumCode/query${config:suffix()}?method=forumCode",
		data: data,
		success:function(result) {
		//	alert(result);
			if(result != ""){
				var forumCodeNodeList = $.parseJSON(result);
				traversedForumCode(node,forumCodeNodeList);
			}

		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
}


//遍历循环版块代码
function traversedForumCode(node,forumCodeNodeList){	
	for(var i = 0 ; i < forumCodeNodeList.length; i++){
		var forumCode = forumCodeNodeList[i];
		var rowHtml = createCodeRowHtml(forumCode.nodeId,node.id,forumCode);
		$("#tableList").treetable("loadBranch", node, rowHtml);// 向树中插入新行(<tr>s), 传入参数 node 为父节点，rows为待插入的行. 如果父节点node为null ，新行被作为父节点插入 

	//	var node = $("#tableList").treetable("node", forumCode.nodeId);//节点对象
		
	}
}

//生成版块代码行HTML
function createCodeRowHtml(nodeId,parentId,forumCode){
	
	var rowHtml="";
	var branch =" data-tt-branch=\"false\" ";

	rowHtml +="<tr data-tt-id=\""+nodeId+"\" data-tt-parent-id=\""+parentId+"\" "+ branch +" forumCodeName=\""+forumCode.nodeName+"\">";
	rowHtml +="<td>";
	rowHtml +="<span class=\"file\">"+forumCode.nodeName+"</span>";
	
	rowHtml +="</td>";
	
	rowHtml +="<td align=\"center\">";	
	rowHtml +="电脑版："+forumCode.pc_lastTime+"";
	if(forumCode.wap_lastTime != null){
		rowHtml +="</br>移动版："+forumCode.wap_lastTime+"";
	}
	
	rowHtml +="</td>";
	
	rowHtml +="<td align=\"center\">";	
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	
//	var node = $("#tableList").treetable("node", nodeId);//节点对象
//	var tr = $("#tableList").find("[data-tt-id='" + nodeId + "']");
//	var parentId = $("[data-tt-id='" + nodeId + "']").attr("data-tt-parent-id");	
//	alert(parentId);
	var tr = $("#tableList").find("[data-tt-id='" + parentId + "']");
	var parent_displayType = tr.attr("displayType");
	
//	var displayType_str = "";
//	if(forumCode.displayType != null && forumCode.displayType.length >0){
//		displayType_str = forumCode.displayType.join(",");
//	}




	rowHtml +="<a href=\"#\" onclick=\"javascript:window.parent.loadFrame('${config:url(pageContext.request)}control/forumCode/manage${config:suffix()}?method=forumSource&module="+forumCode.nodeName+"&dirName=${param.dirName}&timestamp='+ new Date().getTime(),'源码编辑');return false;\" hidefocus=\"true\" ondragstart= \"return false\">源码编辑</a>&nbsp";
	rowHtml +="<a href=\"#\" onclick=\"javascript:updateForumCodeUI('"+nodeId+"','"+parentId+"','"+forumCode.nodeName+"','"+forumCode.remark+"','"+parent_displayType+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">修改</a>&nbsp";
	rowHtml +="<a href=\"#\" onclick=\"javascript:if(window.confirm('确定删除吗? ')){deleteForumCodeSubmit('"+nodeId+"','"+forumCode.nodeName+"');return false;}else{return false};\" hidefocus=\"true\" ondragstart= \"return false\">删除</a>";

	rowHtml +="</td>";
	rowHtml +="</tr>";
	return rowHtml;
}


//添加版块代码UI
function addForumCodeUI(nodeId,forumCode_displayType_str){
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"模板名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"name\" size=\"30\" value=\"\"/>&nbsp;<span id=\"error_name_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"模板显示类型：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<select class=\"form-select\" id=\"displayType\">";
	
	var forumCode_displayType_arr = forumCode_displayType_str.split(",");
	for(var i = 0 ; i < forumCode_displayType_arr.length; i++){
		html += 		"<option value=\""+forumCode_displayType_arr[i]+"\">"+forumCode_displayType_arr[i]+"</option>";
	}
	html += 		"</select>";
	html += 		"&nbsp;<span id=\"error_displayType_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"备注：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"remark\" size=\"50\" value=\"\"/>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:addForumCodeSubmit('"+nodeId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("添加版块代码",html,600,200);//显示层
}

//添加版块代码
function addForumCodeSubmit(nodeId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "dirName=" + getUrlParam("dirName")+"&parentId="+encodeURIComponent(nodeId);//获取URL参数

	var name = $("#name").val();
	
	if(name != ""){
		parameter += "&name="+encodeURIComponent(name);
	}
	
	var displayType = $("#displayType").val();
	if(displayType != ""){
		parameter += "&displayType="+encodeURIComponent(displayType);
	}
	var remark = $("#remark").val();
	if(remark != ""){
		parameter += "&remark="+encodeURIComponent(remark);
	}
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/forumCode/manage${config:suffix()}?method=add",
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
        				systemMsgShow("提交成功");//弹出提示层
        				var node = $("#tableList").treetable("node", nodeId);//节点对象
       					
        				$("#tableList").treetable("unloadBranch", node);//删除节点，请注意，父（节点）将不会被删除。
        				var tr = $("#tableList").find("[data-tt-id='" + nodeId + "']");
           				
           				queryForumCode(node,tr.attr("forumCodeName"));
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

//修改版块代码UI
function updateForumCodeUI(nodeId,parentId,name,remark,parent_displayType){
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"模板名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	
	var name_arr = new Array(); //定义一数组 
	name_arr = name.split("_");
	var name_arr_length = name_arr.length;	
	var displayType = name_arr[name_arr_length-2];
	var displayTypeValue = "";
	if(displayType == "monolayer"){
		displayTypeValue = "单层";
	}else if(displayType == "multilayer"){
		displayTypeValue = "多层";
	}else if(displayType == "page"){
		displayTypeValue = "分页";
	}else if(displayType == "entityBean"){
		displayTypeValue = "实体对象";
	}else if(displayType == "collection"){
		displayTypeValue = "集合";
	}
	
	html += 		"<input class=\"form-text\" id=\"name\" size=\"30\" value=\""+name_arr[name_arr_length-1]+"\"/>&nbsp;<span id=\"error_name_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"模板显示类型：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<select class=\"form-select\" id=\"displayType\">";
	
	var forumCode_displayType_arr = parent_displayType.split(",");
	for(var i = 0 ; i < forumCode_displayType_arr.length; i++){
		var selected = "";
		if(forumCode_displayType_arr[i] == displayTypeValue){
			selected = " selected='selected'";
		}
		html += 		"<option value=\""+forumCode_displayType_arr[i]+"\" "+selected+">"+forumCode_displayType_arr[i]+"</option>";
	}
	html += 		"</select>";
	html += 		"&nbsp;<span id=\"error_displayType_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"备注：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"remark\" size=\"50\" value=\""+remark+"\"/>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:updateForumCodeSubmit('"+nodeId+"','"+parentId+"','"+name+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("修改版块代码",html,600,200);//显示层
}
//修改版块代码
function updateForumCodeSubmit(nodeId,parentId,oldFileName) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "dirName=" + getUrlParam("dirName")+"&nodeId="+encodeURIComponent(nodeId)+"&parentId="+encodeURIComponent(parentId);//获取URL参数

	var name = $("#name").val();
	if(name != ""){
		parameter += "&name="+encodeURIComponent(name);
	}
	parameter += "&oldFileName="+encodeURIComponent(oldFileName);
	
	
	
	var displayType = $("#displayType").val();
	
	if(displayType != ""){
		parameter += "&displayType="+encodeURIComponent(displayType);
	}
	var remark = $("#remark").val();
	if(remark != ""){
		parameter += "&remark="+encodeURIComponent(remark);
	}
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/forumCode/manage${config:suffix()}?method=edit",
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
        				systemMsgShow("提交成功");//弹出提示层
        				var node = $("#tableList").treetable("node", parentId);//节点对象
       					
        				$("#tableList").treetable("unloadBranch", node);//删除节点，请注意，父（节点）将不会被删除。
        				var tr = $("#tableList").find("[data-tt-id='" + parentId + "']");
        				queryForumCode(node,tr.attr("forumCodeName"));
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

//删除版块代码
function deleteForumCodeSubmit(nodeId,fileName) {
	var data = "dirName=" + getUrlParam("dirName")+"&fileName="+encodeURIComponent(fileName);//获取URL参数

	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/forumCode/manage${config:suffix()}?method=delete",
		data: data,
		beforeSend: function(request) {//表单提交前做表单验证
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
		//	alert(result);
			var data = $.parseJSON(result);
			for(var returnValue in data){
				if(returnValue == "success"){
	    			if(data[returnValue] == true){
	    				systemMsgShow("删除成功");//弹出提示层
	    				//IE8下有删除第一个后表格没有自适应高度BUG,需要重新渲染样式
	    				$("#tableList").find("[class='t-list-tbody']").removeClass().addClass("t-list-tbody");
	    				$("#tableList").treetable("removeNode", nodeId);//从树中移除某个节点及其所有子节点 
	    			}
	    		}else{
	    			alert("删除失败");
	    		}
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});
	
	
}
</script>
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<DIV class="body-box">
<DIV class="d-box">
<!-- 导航 -->
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

<table id="tableList" class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<thead class="t-list-thead">
	<tr>
	    <th width="53%">版块</th>
	    <th width="22%">最后修改时间</th>
	    <th width="11%">添加版块代码</th>
	    <th width="14%">操作</th>
    </tr>
	</thead>
	<tbody class="t-list-tbody" >
	</tbody>
</table>
</DIV></DIV></BODY></HTML>
