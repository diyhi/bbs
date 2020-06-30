<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>修改答案</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>


</HEAD>

<script>
	// 指定编辑器iframe document的CSS数据，用于设置可视化区域的样式。 单冒号(:)用于CSS3伪类，双冒号(::)用于CSS3伪元素。伪元素由双冒号和伪元素名称组成。双冒号是在当前规范中引入的，用于区分伪类和伪元素。但是伪类兼容现存样式，浏览器需要同时支持旧的伪类，比如:first-line、:first-letter、:before、:after等
    KindEditor.options.cssData = ""+
		//突出编辑框的代码
	".ke-content .prettyprint {"+
		"min-height:20px;"+
		"background:#f8f8f8;"+
		"border:1px solid #ddd;"+
		"padding:5px;"+
	"}"+//默认字体大小
	"body {"+
		"font-size: 14px;"+
	"}";
	var availableTag = ['source', '|'];
	var editor;
	KindEditor.ready(function(K) {
		var availableTag = document.getElementById("availableTag").value;
		var availableTag_obj = null;//['source', '|','fontname','fontsize','emoticons'];
		if(availableTag != ""){
			var availableTag_obj = JSON.parse(availableTag);//JSON转为对象
		}
		var questionId = "${answer.questionId}";
		
		editor = K.create('textarea[name="content"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
			themeType : 'style :minimalist',//极简主题 加冒号的是主题样式文件名称同时也是主题目录
			autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			uploadJson :"${config:url(pageContext.request)}control/answer/manage.htm?method=uploadImage&questionId="+questionId+"&userName=${answer.userName}&isStaff=${answer.isStaff}&${_csrf.parameterName}=${_csrf.token}",//指定浏览远程图片的服务器端程序
			items : availableTag_obj,
			afterChange : function() {
				this.sync();
			}
		});
	});
	
	
	
</script>


<script language="javascript" type="text/javascript">
function sureSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	
	var parameter = "";
	//答案Id
	var answerId = document.getElementById("answerId");
	if(answerId != null){
		parameter += "&answerId="+answerId.value;
	}
	//状态
	var status = document.getElementsByName("status");  
	if(status != null ){
		for(var i=0; i<status.length; i++) {  
	        if (status[i].checked) { 
	        	parameter += "&status="+status[i].value;
	        }  
	    }  
	}
	//内容
	var content = document.getElementById("content").value;
	if(content != ""){
		parameter += "&content="+encodeURIComponent(content);
	}
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//清空错误提示
	var error_span_object = getElementsByName_pseudo("SPAN", "error");
	for(var i = 0;i < error_span_object.length; i++) {
		//样式图标设为隐藏
		error_span_object[i].innerHTML="";
	
	}

	post_request(function(value){
		if(value != ""){
			var returnValue = JSON.parse(value);//JSON转为对象
			for(var key in returnValue){
				if(key == "success"){
					if(returnValue[key] == "true"){
						systemMsgShow("提交成功,3秒后自动刷新");//弹出提示层
        				setTimeout("window.parent.callbackTopic();",3000);//延迟3秒后刷新当前页面
						return;
					}			
				}else if(key == "error"){
					var errorValue = returnValue[key];
					for(var error in errorValue){
						document.getElementById(error+"_error").innerHTML=errorValue[error];		
					}
					//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
				}
			}

		}	
		
		
	},
		"${config:url(pageContext.request)}control/answer/manage${config:suffix()}?method=edit&timestamp=" + new Date().getTime(), true,parameter);




} 



// 取得表格的伪属性("类型:如tr;td ","name值")
var getElementsByName_pseudo = function(tag, name){
    var returns = document.getElementsByName(name);
    if(returns.length > 0) return returns;
    returns = new Array();
    var e = document.getElementsByTagName(tag);
    for(var i = 0; i < e.length; i++){
        if(e[i].getAttribute("name") == name){
            returns[returns.length] = e[i];
        }
    }
    return returns;
};

</script> 
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<input type="hidden" id="answerId" value="${answer.id}">
<enhance:out escapeXml="false">
	<input type="hidden" id="availableTag" value="<c:out value="${availableTag}"></c:out>">
</enhance:out>
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
   <TR>
    <TD class="t-content" style="height: 30px;">
    	状态：<label><input name="status" type="radio" value="10" <c:if test="${answer.status == 10}"> checked="checked"</c:if>>待审核</label>
    	<label><input name="status" type="radio" value="20" <c:if test="${answer.status == 20}"> checked="checked"</c:if>>已发布</label>
    </TD>
   </TR>
    <TR>
    <TD class="t-content" >
    	<textarea id="content" name="content" style="width:99%;height:300px;visibility:hidden;">${answer.content}</textarea>
    </TD>
   </TR>
	<TR>
    <TD class="t-button">
    	<SPAN id="answerId_error" name="error" class="span-text" ></SPAN>
	  	<SPAN id="content_error" name="error" class="span-text" ></SPAN>
	  	<span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit();"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>