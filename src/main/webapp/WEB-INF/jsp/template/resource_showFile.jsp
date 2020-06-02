<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>资源编辑</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<style type="text/css">
.CodeMirror {border-top: 1px solid #BFE3FF; border-bottom: 1px solid #BFE3FF; }

</style>
<link href="backstage/codeMirror/lib/codemirror.css" rel="stylesheet" >
<script src="backstage/codeMirror/lib/codemirror.js"></script>
<script src="backstage/codeMirror/mode/xml/xml.js"></script>
<script src="backstage/codeMirror/mode/javascript/javascript.js"></script>
<script src="backstage/codeMirror/mode/css/css.js"></script>
<script src="backstage/codeMirror/mode/htmlmixed/htmlmixed.js"></script>
<script src="backstage/codeMirror/mode/vue/vue.js"></script>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript" charset="UTF-8"></script>

</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	<TR>
	    <TD class="t-content" width="100%" >
	    	<input type="hidden" id="fileType" value="${fileType}">
	    	<textarea id="code" >${fileContent}</textarea>
	    </TD>
    </TR>
	</TBODY>
</TABLE>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<tr>
		    <td class="t-button">
		        <span class="submitButton"><INPUT type="button" id="submitForm" value="修改" onClick="javascript:submitEditFile()"></span>
		  	</td>
		</tr>
	</TBODY>
</TABLE>
</DIV>
</BODY>
<script type="text/javascript">
CodeMirror.commands.autocomplete = function(cm) {
	CodeMirror.simpleHint(cm, CodeMirror.javascriptHint); 
};

var _fileType = document.getElementById("fileType").value;
var _mode = "text/html";
if(_fileType == "js"){
	_mode = "javascript";
}else if(_fileType == "css"){
	_mode = "css";
}
  
var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
  	lineNumbers: true,
    mode: _mode,
    indentUnit: 4,
    extraKeys: {"Alt-/": "autocomplete"},
    indentWithTabs: true,
    autoCloseTags: true,
});
editor.setSize("100%","100%");//设置自适应高度 


//提交编辑内容
function submitEditFile() {
	editor.save();//将编辑器中的数据同步到textarea
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var code = $("#code").val();
	var dirName = getUrlParam("dirName");
	var resourceId = getUrlParam("resourceId");
	var data = "dirName=" + dirName +"&resourceId="+encodeURIComponent(resourceId)+"&content="+encodeURIComponent(code);//获取URL参数

	$.ajax({
		type:"POST",
		cache:false,
		async: true,//默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "${config:url(pageContext.request)}control/resource/manage${config:suffix()}?method=editFile",
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
        				systemMsgShow("提交成功");//弹出提示层
        				setTimeout(function(){	
        					window.parent.callbackFrame();//关闭层
        				}, 3000 );
        			
        				
        				return;
        			}
        		}else if(returnValue == "error"){
        			var errorValue = data[returnValue];
					var htmlValue = "";
					var i = 0;
					for(var error in errorValue){
						if(error != ""){	
							i++;
							htmlValue += "&nbsp;&nbsp;"+i+"、"+errorValue[error]+"<br>";
						}
					}
					
					layer.open({
					  type: 1,
					  title: '错误', 
					  skin: 'layui-layer-rim', //加上边框
					  area: ['300px', '150px'], //宽高
					  content: "<div style='line-height: 36px; font-size: 15px; margin-left: 8px;margin-right: 8px;'>"+htmlValue+"</div>"
					});
        			
        			
        			
        			
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
</HTML>