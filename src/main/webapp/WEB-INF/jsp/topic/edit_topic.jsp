<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>修改话题</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link href="backstage/kindeditor/themes/default/default.css" rel="stylesheet"/>
<link href="backstage/kindeditor/plugins/hide/hide.css" rel="stylesheet"/>
<script charset="utf-8" src="backstage/kindeditor/kindeditor.js"></script>
<script charset="utf-8" src="backstage/kindeditor/lang/zh-CN.js"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

<!--[if (IE 6)|(IE 7)|(IE 8)]>
<script type="text/javascript">
	//让ie6/ie7/ie8支持自定义标签。这段代码必须放在页面头部<head>标签内
	(function() {
		var a = ['hide'/* 其他HTML5元素 */];
		for (var i = 0, j = a.length; i < j; i++) {
			document.createElement(a[i]);
		}	
	})();
</script>
<![endif]-->

<script language="javascript" type="text/javascript">
//弹出查看标签层
function showTagDiv(){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择标签",div,700,400);//显示层
	//显示标签
	allTag();
}
//显示标签
function allTag(){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/tag/manage${config:suffix()}?method=allTag&timestamp=" + new Date().getTime(), true);

}


//添加标签
function addTag(id,name){
	document.getElementById("tagId").value=id;
	document.getElementById("tagName").value=name;
	document.getElementById("_tagName").value=name;
	//关闭层
	systemLayerClose();
}



function sureSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "";
	//话题Id
	var topicId = getUrlParam("topicId");
	if(topicId != null){
		parameter += "&topicId="+topicId;
	}
	
	//标题
	var title = document.getElementById("title").value;
	if(title != ""){
		parameter += "&title="+encodeURIComponent(title);
	}
	
	
	//内容
	var content = document.getElementById("content").value;
	if(content != ""){
		parameter += "&content="+encodeURIComponent(content);
	}
	//标签Id
	var tagId = document.getElementById("tagId").value;
	if(tagId != ""){
		parameter += "&tagId="+encodeURIComponent(tagId);
	}
	//排序
	var sort = document.getElementById("sort").value;
	if(sort != ""){
		parameter += "&sort="+encodeURIComponent(sort);
	}
	
	//允许评论
	var allow = document.getElementsByName("allow");  
    for(var i=0; i<allow.length; i++) {  
        if (allow[i].checked) { 
        	parameter += "&allow="+allow[i].value;
        }  
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
    
    
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	//清空错误提示
	var error_span_object = getElementsByName_pseudo("span", "error");
	for(var i = 0;i < error_span_object.length; i++) {
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
		"${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=edit&timestamp=" + new Date().getTime(), true,parameter);




} 

</script> 
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<enhance:out escapeXml="false">
<input type="hidden" id="userGradeList" value="<c:out value="${userGradeList}"></c:out>">
</enhance:out>
<DIV class="d-box">

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
  	<TD class="t-label t-label-h" width="12%">标题：</TD>
    <TD class="t-content" width="88%">
    	<input type="text" class="form-text" id="title" size="50" value="${topic.title}">
		<span id="title_error" name="error" class="span-text"></span>
    </TD>
   </TR>
  	<TR>
	  	<TD class="t-label t-label-h" width="12%">标签：</TD>
	    <TD class="t-content" width="88%">
	    	<input type="hidden" id="tagId" name="tagId" value="${topic.tagId}"/>
	    	<input type="text" class="form-text" id="_tagName" disabled="true" size="20" value="${topic.tagName}"/> 
	    	<input type="hidden" id="tagName" name="tagName" value="${topic.tagName}">
	    	<input type="button" class="functionButton5" value="选择..." onClick="javaScript:showTagDiv();">
	    	&nbsp;<span id="tagId_error" name="error" class="span-text"></span>
	    </TD>
	</TR>
	<TR>
  	<TD class="t-label t-label-h" width="12%">排序：</TD>
    <TD class="t-content" width="88%">
    	<input type="text" class="form-text" id="sort" name="sort" size="8" maxlength="8" value="${topic.sort}">
    	<span id="sort_error" name="error" class="span-text"></span>
		<SPAN class="span-help">数字越大越在前</SPAN>
    </TD>
   </TR>
	<TR>
	<TD class="t-label t-label-h" width="12%">允许评论：</TD>
    <TD class="t-content" width="88%">
    	<label><input name="allow" type="radio" value="true" <c:if test="${topic.allow == true}"> checked="checked"</c:if>>允许</label>
    	<label><input name="allow" type="radio" value="false" <c:if test="${topic.allow == false}"> checked="checked"</c:if>>禁止</label>
    </TD>
   </TR>
   <c:if test="${topic.status <100 }">
   	<TR>
  	<TD class="t-label t-label-h" width="12%">状态：</TD>
    <TD class="t-content" width="88%">
    	<label><input name="status" type="radio" value="10" <c:if test="${topic.status == 10}"> checked="checked"</c:if>>待审核</label>
    	<label><input name="status" type="radio" value="20" <c:if test="${topic.status == 20}"> checked="checked"</c:if>>已发布</label>
    </TD>
   </TR>
   </c:if>
   
    <TR>
    <TD class="t-label t-label-h" width="12%">内容：</TD>
    <TD class="t-content" width="88%">
    	<textarea id="content" name="content" style="width:99%;height:300px;visibility:hidden;">${topic.content}</textarea>
		<span id="content_error" name="error" class="span-text"></span>
    </TD>
   </TR>
	<TR>
    <TD class="t-button" colspan="2">
    
    	 <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit()"></span>
    	 <span id="topic_error" name="error" class="span-text"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY>

<script>
var editor;
function initKindEditor(){
	KindEditor.lang({
        hide : '隐藏'
    });
    
	// 指定编辑器iframe document的CSS数据，用于设置可视化区域的样式。 单冒号(:)用于CSS3伪类，双冒号(::)用于CSS3伪元素。伪元素由双冒号和伪元素名称组成。双冒号是在当前规范中引入的，用于区分伪类和伪元素。但是伪类兼容现存样式，浏览器需要同时支持旧的伪类，比如:first-line、:first-letter、:before、:after等
    KindEditor.options.cssData = ".ke-content hide {"+
		"border: 0;"+
		"border-left: 3px solid #06b5ff;"+
		"margin-left: 10px;"+
		"padding: 0.5em;"+
		"min-height:26px;"+
		"display: block;"+
		"margin: 30px 0px 0px 0px;"+
	"}"+
	".ke-content .inputValue_10:before {"+
		"content: '密码: ' attr(input-value) '';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_20:before {"+
		"content: '回复话题可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_30:before {"+
		"content: '达到等级 ' attr(description) ' 可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_40:before {"+
		"content: '需要支付 ' attr(input-value) ' 积分可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_50:before {"+
		"content: '需要支付 ' attr(input-value) ' 元费用可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+//突出编辑框的代码
	".ke-content .prettyprint {"+
		"background:#f8f8f8;"+
		"border:1px solid #ddd;"+
		"padding:5px;"+
	"}"+//默认字体大小
	"body {"+
		"font-size: 14px;"+
	"}";

   //指定要保留的HTML标记和属性。Object的key为HTML标签名，value为HTML属性数组，”.”开始的属性表示style属性。 注意属性要全部小写
    KindEditor.options.htmlTags['hide'] = ['hide-type','input-value','class','description'];
    
    //等级
	var userGradeList = document.getElementById("userGradeList").value;
	var userGradeList_obj = null;//['source', '|','fontname','fontsize','emoticons'];
	if(userGradeList != ""){
		userGradeList_obj = JSON.parse(userGradeList);//JSON转为对象
	}
    

	KindEditor.ready(function(K) {
		editor = K.create('textarea[name="content"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
		//	autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			allowFlashUpload :true,
			uploadJson :"${config:url(pageContext.request)}control/topic/manage.htm?method=upload&userName=${topic.userName}&isStaff=${topic.isStaff}&${_csrf.parameterName}=${_csrf.token}",//指定浏览远程图片的服务器端程序
	
			items : ['source', '|', 'preview', 'template', 'code',
        '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', 
        'formatblock', 'fontname', 'fontsize', '/', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
        'flash', 'media', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
         'link', 'unlink','hide','hidePassword','hideComment','hideGrade','hidePoint'],
         	userGradeList:userGradeList_obj,
			afterChange : function() {
				this.sync();
			}
			
		});
		
	});
	
}
initKindEditor();
</script>

</HTML>