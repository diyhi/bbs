<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>资源列表</TITLE>
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
		expandable: true,  //false为全部展开
		clickableNodeNames: true,//单击节点名字是否打开，默认false;
		initialState: "collapsed",//默认是否展开和关闭： values: "expanded" or "collapsed"
		
		onNodeExpand: function() {// 分支展开后的回调函数	
			var node = this;

			//判断当前节点是否已经拥有子节点
			var childSize = $("#tableList").find("[data-tt-parent-id='" + node.id + "']").length;
			if (childSize > 0) { 
				 return; 
			}
			/**
			$("[data-tt-id='" + node.id + "']").each(function(){
				var td=$(this);
				alert(td.attr("id"));
				
			});**/
			
			//读取子节点
			getChildNode(node);
		}
	});
});



//读取子节点
function getChildNode(node) {
	
	var dirName = getUrlParam("dirName");//目录
	var resourceId = "";
	var nodeId ="";
	if(node != null){
		resourceId = $("[data-tt-id='" + node.id + "']").attr("resourceId");
		nodeId =  node.id;
	}
	
	var data = "dirName=" + dirName+"&parentId="+encodeURIComponent(resourceId);//获取URL参数
	
	// Render loader/spinner while loading 加载时渲染
	$.ajax({
		type:"GET",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/resource/query${config:suffix()}",
		data: data,
		success:function(result) {

			if(result != ""){
				var resourceList = $.parseJSON(result);
				for(var i =0; i<resourceList.length >0; i++){
					var resource = resourceList[i];
					var rowHtml = createRowHtml(nodeId+"-"+i,nodeId,resource,dirName);
				//	alert(rowHtml);		
					$("#tableList").treetable("loadBranch", node, rowHtml);// 向树中插入新行(<tr>s), 传入参数 node 为父节点，rows为待插入的行. 如果父节点node为null ，新行被作为父节点插入 
				//	if(node != null){
				//		$("#tableList").treetable("expandNode", nodeId);// 展开子节点
				//	}
					
					//unloadBranch(node)：删除节点/行，请注意，父（节点）将不会被删除。


				
				}
			}else{
				
			//	$("#tableList").treetable("collapseAll");

				

			}
		

			/**
			if(0 == result.code ){	
				if(!com.isNull(result.body)){
					if(0 == eval(result.body['chilPages']).length){//不存在子节点
						var $tr = $("#treetable").find("[data-tt-id='" + node.id + "']");
						$tr.attr("data-tt-branch","false");// data-tt-branch 标记当前节点是否是分支节点，在树被初始化的时候生效
						$tr.find("span.indenter").html("");// 移除展开图标
						return;
					}
					
					var rows = this.getnereateHtml(result.body['chilPages']);
					$("#treetable").treetable("loadBranch", node, rows);// 插入子节点
					$("#treetable").treetable("expandNode", node.id);// 展开子节点
				}
			}else{
				alert(result.tip);
			}	**/
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
	});

}

//生成行HTML
function createRowHtml(nodeId,parentId,resource,dirName){
	
	var rowHtml="";
	var branch = "";
	if(resource.leaf == true){
		branch =" data-tt-branch=\"false\" ";
	}else{
		branch =" data-tt-branch=\"true\" ";
	}
	
	rowHtml +="<tr data-tt-id=\""+nodeId+"\" data-tt-parent-id=\""+parentId+"\" "+ branch +"resourceId=\""+resource.id+"\">";
	rowHtml +="<td>";
	if(resource.leaf == true){
		rowHtml +="<span class=\"file\">"+resource.name+"</span>";
	}else{
		rowHtml +="<span class=\"folder\">"+resource.name+"</span>";
	}
	
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	rowHtml += resource.lastModified;
	rowHtml +="</td>";
	rowHtml +="<td align=\"center\">";
	if(resource.leaf == true){
		rowHtml +="<a href=\"#\" onclick=\"javascript:show('"+nodeId+"','"+resource.id+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">查看</a>&nbsp";
		rowHtml +="<a href=\"control/resource/manage.htm?method=download&resourceId="+resource.id+"&dirName="+dirName+"\"  hidefocus=\"true\" ondragstart= \"return false\">下载</a>&nbsp";
	}else{
		rowHtml +="<a href=\"#\" onclick=\"javascript:upload('"+nodeId+"','"+resource.id+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">上传文件</a>&nbsp";
		rowHtml +="<a href=\"#\" onclick=\"javascript:newFolder('"+nodeId+"','"+resource.id+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">新建文件夹</a>&nbsp";
	}
	rowHtml +="<a href=\"#\" onclick=\"javascript:rename('"+nodeId+"','"+resource.id+"','"+resource.name+"');return false;\" hidefocus=\"true\" ondragstart= \"return false\">重命名</a>&nbsp";
	rowHtml +="<a href=\"#\" onclick=\"javascript:if(window.confirm('确定删除吗? ')){deleteNode('"+nodeId+"','"+resource.id+"');return false;}else{return false};\" hidefocus=\"true\" ondragstart= \"return false\">删除</a>";
	rowHtml +="</td>";
	rowHtml +="</tr>";
	return rowHtml;
}


//显示UI
function show(nodeId,resourceId) {
	//如果为js和css格式，则用查看器加载
	if (/\.(js|css|JS|CSS)$/.test(resourceId)) {  
		var basePath = getBasePath();//获取系统base路径
		var data = "dirName=" + getUrlParam("dirName")+"&resourceId="+encodeURIComponent(resourceId);//获取URL参数

    	var url = basePath+"control/resource/manage.htm?method=showFileUI&"+data+"&timestamp="+ new Date().getTime();
    	//在全局显示
    	window.parent.loadFrame(url,"显示");
		/**
		var data = "dirName=" + getUrlParam("dirName")+"&resourceId="+encodeURIComponent(resourceId);//获取URL参数

		$.ajax({
			type:"GET",
			cache:false,
			async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
			url : "${config:url(pageContext.request)}control/resource/manage${config:suffix()}?method=showFileUI",
			data: data,
			success:function(result) {
				window.parent.loadData(result,"显示");
			}
		});**/
    }else{
    	var basePath = getBasePath();//获取系统base路径
    	//var url = basePath+"common/"+getUrlParam("dirName")+"/"+encodeURIComponent(resourceId)+"?timestamp="+ new Date().getTime();
    	var url = basePath+"common/"+getUrlParam("dirName")+"/"+resourceId+"?timestamp="+ new Date().getTime();;
    	//在全局显示
    	window.parent.loadFrame(url,"显示");
    	
    }
}

//上传文件UI
function upload(nodeId,resourceId) {	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"路径：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"根目录/common/"+getUrlParam("dirName")+"/"+resourceId;
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"文件：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input type=\"file\" name=\"uploadFile\" size=\"40\" value=\"\"/><span id=\"error_uploadFile_"+nodeId+"\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:uploadSubmit('"+nodeId+"','"+resourceId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("上传文件",html,600,168);//显示层
}
//上传文件
function uploadSubmit(nodeId,resourceId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var data = "dirName=" + getUrlParam("dirName")+"&resourceId="+encodeURIComponent(resourceId);//获取URL参数

//	$("#dialog").parent().appendTo("/html/body/form[0]");//将控件动态生成的HTML元素移到form元素内
	
//	$("#resourceForm").ajaxForm();
	$("#resourceForm").ajaxSubmit({
        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
        type:'POST',// 提交类型可以是"GET"或者"POST"
        url:'control/resource/manage.htm?method=upload&'+data,// 表单提交的路径
        beforeSend: function() {//表单提交前做表单验证
            //清空所有错误
        	$("[name='error']").html("");
        },
        success: function(data) {//提交成功后调用
          
          //  alert(data.message);
        	//alert(data);
        	for(var returnValue in data){
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
        				systemMsgShow("上传成功");//弹出提示层
        				if(nodeId == ""){//根节点
        	            	$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        	            	var node = null;
        	            	//读取子节点
            				getChildNode(node);
        	            	
        					return;
        	            }
        				
        				var node = $("#tableList").treetable("node", nodeId);//节点对象
        				$("#tableList").treetable("unloadBranch", node);//删除节点，请注意，父（节点）将不会被删除。
        				//读取子节点
        				getChildNode(node);
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

//新建文件夹UI
function newFolder(nodeId,resourceId) {	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"路径：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"根目录/common/"+getUrlParam("dirName")+"/"+resourceId;
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"新文件夹名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"folderName\" size=\"35\" value=\"\"/><span id=\"error_folderName_"+nodeId+"\" name=\"error\" class=\"span-text\"></span><span class=\"span-help\">不能含有\\&nbsp;/:*?\"<>|%..字符</span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:newFolderSubmit('"+nodeId+"','"+resourceId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("新建文件夹",html,600,168);//显示层
}
//新建文件夹
function newFolderSubmit(nodeId,resourceId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var folderName = $("#folderName").val();
	var data = "dirName=" + getUrlParam("dirName")+"&resourceId="+encodeURIComponent(resourceId)+"&folderName="+encodeURIComponent(folderName);//获取URL参数

	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/resource/manage${config:suffix()}?method=newFolder",
		data: data,
		beforeSend: function(request) {//表单提交前做表单验证
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
        				var node = null;
        				if(nodeId != ""){
        					node = $("#tableList").treetable("node", nodeId);//节点对象
            				$("#tableList").treetable("unloadBranch", node);//删除节点，请注意，父（节点）将不会被删除。
        				}else{//根节点
            	        	$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        				}
        				
        				//读取子节点
        				getChildNode(node);
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


//重命名UI
function rename(nodeId,resourceId,oldName) {	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"旧名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		oldName;
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"新名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"rename\" size=\"35\" value=\"\"/><span id=\"error_rename_"+nodeId+"\" name=\"error\" class=\"span-text\"></span><span class=\"span-help\">不能含有\\&nbsp;/:*?\"<>|%..字符</span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:renameSubmit('"+nodeId+"','"+resourceId+"')\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("重命名",html,600,168);//显示层
	

}
//提交重命名
function renameSubmit(nodeId,resourceId) {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var rename = $("#rename").val();
	var dirName = getUrlParam("dirName");
	var data = "dirName=" + dirName +"&resourceId="+encodeURIComponent(resourceId)+"&rename="+encodeURIComponent(rename);//获取URL参数

	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/resource/manage${config:suffix()}?method=rename",
		data: data,
		beforeSend: function(request) {//表单提交前做表单验证
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
			//alert(result);
			
			var data = $.parseJSON(result);
			
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
        				
        				systemMsgShow("提交成功");//弹出提示层
        				
        				var parentId = $("[data-tt-id='" + nodeId + "']").attr("data-tt-parent-id");
        				if(parentId == ""){//根节点
        					$("#tableList").find("[class='t-list-tbody']").html("");//删除所有节点
        					getChildNode();
        				}else{
        					var parentId_node = $("#tableList").treetable("node", parentId);//节点对象
            				
            				$("#tableList").treetable("unloadBranch", parentId_node);//删除节点，请注意，父（节点）将不会被删除。
            				
            				//读取子节点
            				getChildNode(parentId_node);
        				}
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


//删除节点
function deleteNode(nodeId,resourceId) {
	var dirName = getUrlParam("dirName");
	var data = "dirName=" + dirName +"&resourceId="+encodeURIComponent(resourceId);//获取URL参数

	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/resource/manage${config:suffix()}?method=delete",
		data: data,
		beforeSend: function(request) {//表单提交前做表单验证
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
		//	alert(result);
			if(result == "1"){
				systemMsgShow("删除成功");//弹出提示层
				//IE8下有删除第一个后表格没有自适应高度BUG,需要重新渲染样式
				$("#tableList").find("[class='t-list-tbody']").removeClass().addClass("t-list-tbody");
				$("#tableList").treetable("removeNode", nodeId);//从树中移除某个节点及其所有子节点 
			}else{
				alert("删除失败");
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
<form:form id="resourceForm"  enctype="multipart/form-data" method="post">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onclick="javascript:upload('','');" value="上传文件">
	
	<input class="functionButton" type="button" onClick="javascript:newFolder('','');return false;" value="新建文件夹">

</div>
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="28px">
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
	    <th width="50%">文件</th>
	    <th width="20%">最后修改时间</th>
	    <th width="30%">操作</th>
    </tr>
	</thead>
	<tbody class="t-list-tbody" >
   		
	</tbody>
</table>

</DIV>
</form:form>

<script language="javascript" type="text/javascript">
	//显示节点
	getChildNode();
</script>
</BODY></HTML>
