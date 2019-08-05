<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD><TITLE>修改模板</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ImagePreview.js"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script type="text/javascript" language="javascript" >
//删除图片
function deleteImage(id){
	if(document.getElementById("preview_"+id+"New") != null){
		var newDIV = document.getElementById("preview_"+id+"New"); 
		newDIV.parentNode.removeChild(newDIV);

	}else{
		document.getElementById("preview_"+id).innerHTML="<img id='imghead_"+id+"' width='40px' border='0' src='backstage/images/null.gif' >";//用空白图片替换
	
	}
	//
	document.getElementById("imagePath_"+id).value="";

	document.getElementById("uploadImage_"+id).outerHTML += '';//清空IE的
	document.getElementById("uploadImage_"+id).value = "";//可以清空火狐的
	
}
$(document).ready(function(){ 
		//上传已选择文件改变图标
		$(".fileButtonPic").on("change","input[type='file']",function(){
		    var filePath=$(this).val();//路径
		 	$(this).attr("title",filePath);
		 	var path = getBasePath()+"backstage/images/tick_16.png";
			$(this).parent().find("em:eq(0)").css({"background":"url("+path+") 0 0"});
		//alert($(this).parent().find("em:eq(0)").html());
		
		});
	});
</script>
</HEAD>
<BODY>
<form:form modelAttribute="templates" enctype="multipart/form-data" method="post">
<DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TR>
	    <TD class="t-content" colSpan="5" height="28px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">
			&nbsp;>> &nbsp;修改
			
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>模板名称：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="name" maxlength="100" size="40" value="${templates.name}"/>
	    	&nbsp;<web:errors path="name" cssClass="span-text"/>
	    </TD>
    </TR>
   <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>模板目录：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${templates.dirName}
    </TD>
    </TR>
    <TR>
		<TD class="t-label t-label-h" width="12%">缩略图：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    <TABLE cellSpacing=0 cellPadding=0 width="180px" border=0 align="left">
    		<tr align="left">
    			<td width="50px" height="30px"><div id="preview_1"><img id="imghead_1" width='40px' border='0' src='${imagePath}<c:if test="${imagePath == null}">backstage/images/null.gif</c:if>' ></div></td>
    			<td width="80px" height="30px">
    				<a href="javascript:void(0);" class="fileButtonPic"><span><em>+</em>浏览...</span><input type="file" class="fileButton" id="uploadImage_1" name="uploadImage" onchange="previewImage(this,'imghead_1', 'preview_1')"></a>
    			</td>
    			<td width="50px" height="30px">
    			<input type="hidden" id="imagePath_1" name="imagePath_1" value='${imagePath}'/><a hidefocus="true" onClick="deleteImage(1); return false" href="#" ondragstart= "return false">删除</a></td>
    		</tr>
    	</TABLE>
    	<web:errors path="thumbnailSuffix" cssClass="span-text"/>
	    </TD>
    </TR>
   <TR>
    <TD class="t-label t-label-h" width="12%">模板简介：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<textarea class="form-textarea" name="introduction" rows="10" cols="80">${templates.introduction}</textarea>
    </TD></TR>
	<tr>
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="submit" value="提交" ></span> 
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>
