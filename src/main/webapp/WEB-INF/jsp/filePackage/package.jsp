<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>打包文件</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/js/json3.js" language="javascript"></script>
<script type="text/javascript" src="backstage/js/Map.js"></script>
<script type="text/javascript" src="backstage/jquery/jquery.min.js" language="JavaScript"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<link href="backstage/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/zTree/js/jquery.ztree.all.min.js"></script>
<script language="javascript" type="text/javascript">


$(document).ready(function(){  
    var setting = {
		async:{  
	        autoParam:["id=parentId"],//异步加载时自动提交父节点参数  
	        dataType:"json",//提交参数的数据类型  
	        enable:true,//是否开启异步加载模式  
	        dataFilter: ajaxDataFilter,
	        type:"get",//提交方式  
	        url:"${config:url(pageContext.request)}control/filePackage/manage${config:suffix()}?method=querySubdirectory&timestamp=" + new Date().getTime(),//服务端加载类  
	    },
		check: {
			enable: true,//设置 zTree 的节点上是否显示 checkbox / radio
			
		},
		data: {

			simpleData: {
				enable: true, //true / false 分别表示 使用 / 不使用 简单数据模式
			}
		},
		callback: {
			//节点展开的事件回调函数
		//	onExpand: zTreeOnExpand,
			//用于捕获节点被点击的事件回调函数  
		//	onCheck:selectRegion,
			//用于捕获异步加载正常结束的事件回调函数  
		//	onAsyncSuccess: autoSelectRegion
			
		}
	};
	get_request(function(value){
		if(value != null && value != ""){
			var fileResourceList = JSON.parse(value);
			if(fileResourceList != null && fileResourceList.length >0){	
				$.fn.zTree.init($("#divMessage"), setting, fileResourceList);	
			}
		}
	},
		"${config:url(pageContext.request)}control/filePackage/manage${config:suffix()}?method=querySubdirectory&timestamp=" + new Date().getTime(), true);
}) 



//对 Ajax 返回数据进行预处理
function ajaxDataFilter(treeId, parentNode, responseData) {
    if (responseData) {
    	if(parentNode.halfCheck == false && parentNode.getCheckStatus().checked == true){//如果当前节点为全选
    		for(var i =0; i < responseData.length; i++) {
            	var node = responseData[i];
				node["checked"] = true;//增加全选参数
        	}
    	}
    }
    return responseData;
};

//所有全选节点
function allSelectedNode() {
	var treeObj = $.fn.zTree.getZTreeObj("divMessage");
	var nodes = treeObj.getCheckedNodes(true);
	
	for(var i=0;i<nodes.length;i++){  
		var node = nodes[i];
		if(node.getCheckStatus().half == false){//如果节点为全选
			alert(node.halfCheck+" "+node.name+"  "+node.getCheckStatus().half);
		}
    } 
}



//提交
function sureSubmit() {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;

	var data = "";
	var treeObj = $.fn.zTree.getZTreeObj("divMessage");
	var nodes = treeObj.getCheckedNodes(true);
	
	for(var i=0;i<nodes.length;i++){  
		var node = nodes[i];
		if(node.getCheckStatus().half == false){//如果节点为全选
			data += "&idGroup="+encodeURIComponent(node.id);
		}
    } 
	
	if(data == ""){
		alert("请选择要打包的目录或文件");
		//按钮设置 disabled="disabled"
		document.getElementById("submitForm").disabled=false;
		return;
	}
	
	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/filePackage/manage${config:suffix()}?method=package",
		data: data, 
		beforeSend: function(request) {//表单提交前做表单验证
        	var csrf =  getCsrf();
        	request.setRequestHeader(csrf.header, csrf.token);
        },
		success:function(result) {
			var data = $.parseJSON(result);
				
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			if(data[returnValue] == "true"){
        				systemMsgShow("提交成功,正在异步打包");//弹出提示层
        				//按钮设置 disabled="disabled"
						document.getElementById("submitForm").disabled=false;
        				return;
        			}
        		}else if(returnValue == "error"){
        			
        			var error = data[returnValue];
        			for(var key in error){
        				alert(error[key]);
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



</script>
  
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form method="post" >
<DIV class="d-box">
<div class="d-button">
	<input type="button" class="functionButton" value="返回" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/filePackage/list${config:suffix()}'">
</div>


<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
	<THEAD class="t-list-thead">
		<TR><TH>选择要打包的目录或文件</TH></TR>
	</THEAD>
	<TBODY class="t-list-tbody" align="center">
		<TR class="noDiscolor">
		    <TD width="100%">
		    	<div id='divMessage' class='ztree'></div>
		    </TD>
		</TR>
		<TR class="noDiscolor">
		    <TD width="100%" height="40px">
		    	<span class="submitButton"><INPUT type="button" id="submitForm" value="打包" onClick="javascript:sureSubmit();return false;"></span>
		    </TD>
		</TR>
	</TBODY>
</TABLE>
<div style="color: red; margin-top: 8px;">注意：根目录/WEB-INF/data/filePackage  下的文件将不打包</div>
</DIV>
</form:form>
</BODY></HTML>
