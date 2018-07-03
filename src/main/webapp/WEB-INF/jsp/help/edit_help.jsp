<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>修改帮助</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link href="backstage/kindeditor/themes/default/default.css" rel="stylesheet"/>
<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>
<script charset="utf-8" src="backstage/kindeditor/lang/zh-CN.js"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />



<script language="javascript" type="text/javascript">
//弹出查看分类层
function showHelpTypePageDiv(){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择分类",div,700,400);//显示层
	//显示商品分类分页显示	
	helpTypePage(1,-1);
}
//显示帮助分类
function helpTypePage(pages,parentId){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/helpType/manage${config:suffix()}?method=helpTypePageSelect_move&page="+pages+"&parentId="+parentId+"&timestamp=" + new Date().getTime(), true);

}

//添加分类
function addType(id,name){
	document.getElementById("helpTypeId").value=id;
	document.getElementById("helpTypeName").value=name;
	document.getElementById("_helpTypeName").value=name;
	//修改kindeditor的上传参数
	updateUploadJson(id);
	//关闭层
	systemLayerClose();
}

</script>
<script language="javascript" type="text/javascript">
function sureSubmit(objForm){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	objForm.submit();
} 
</script> 
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/help/list${config:suffix()}?visible=${param.visible}&page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
  	<TD class="t-label t-label-h" width="12%">名称：</TD>
    <TD class="t-content" width="88%">
    	<input type="text" name="name" size="50" value="${help.name}">
		<SPAN class="span-text">${error["name"]}</SPAN>
    </TD>
   </TR>
   <c:if test="${param.isHelpList != null && param.isHelpList== true}">
   		<TR>
		  	<TD class="t-label t-label-h" width="12%">分类：</TD>
		    <TD class="t-content" width="88%">
		    	<input type="hidden" id="helpTypeId" name="helpTypeId" value="${help.helpTypeId}"/>
		    	<input type="text" id="_helpTypeName" disabled="true" size="20" value="${help.helpTypeName}"/> 
		    	<input type="hidden" id="helpTypeName" name="helpTypeName" value="${help.helpTypeName}">
		    	<input type="button" class="functionButton5" value="选择..." onClick="javaScript:showHelpTypePageDiv();">
		    	&nbsp;<SPAN class="span-text">${error["helpTypeId"]}</SPAN>
		    </TD>
		</TR>
   </c:if>
    <TR>
    <TD class="t-label t-label-h" width="12%">内容：</TD>
    <TD class="t-content" width="88%">
    	<textarea id="content" name="content" style="width:99%;height:300px;visibility:hidden;">${help.content}</textarea>
		<SPAN class="span-text">${error["content"]}</SPAN>
    </TD>
   </TR>
	<TR>
    <TD class="t-button" colspan="2">
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form)"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY>
<script>
var editor;
function initKindEditor(id){
	KindEditor.ready(function(K) {
		
		editor = K.create('textarea[name="content"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
		//	autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			allowFlashUpload :true,
			uploadJson :'${config:url(pageContext.request)}control/help/manage.htm?method=upload&helpTypeId='+id+"&${_csrf.parameterName}=${_csrf.token}",//指定浏览远程图片的服务器端程序
		//	fileManagerJson : '${config:url(pageContext.request)}control/customComment/manage.htm?method=uploadImage',//指定浏览远程图片的服务器端程序

			items : ['source', '|', 'preview', 'template',  
        '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', 
        'formatblock', 'fontname', 'fontsize', '/', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
        'flash', 'media', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
         'link', 'unlink'],
			afterChange : function() {
				this.sync();
			}
			
		});
	});
	
}
initKindEditor(${help.helpTypeId});

	
	
	
	//修改上传路径
	function updateUploadJson(id){
	
	//	editor.uploadJson= "${config:url(pageContext.request)}control/help/manage.htm?method=upload"+parameter;
		editor.remove();
		initKindEditor(id);

	}
	
</script>

</HTML>