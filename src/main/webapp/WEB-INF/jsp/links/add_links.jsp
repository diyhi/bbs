<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加友情链接</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/jquery/jquery.min.js" language="JavaScript"></script>
<script type="text/javascript" src="backstage/js/ImagePreview.js"></script>

<script language="javascript" type="text/javascript">
//删除图片
function deleteImage(id){
	if(document.getElementById("preview_"+id+"New") != null){
		var newDIV = document.getElementById("preview_"+id+"New"); 
		newDIV.parentNode.removeChild(newDIV);

	}else{
		document.getElementById("preview_"+id).innerHTML="<img id='imghead_"+id+"' width='40px' border='0' src='backstage/images/null.gif' >";//用空白图片替换
	
	}
	//
	document.getElementById("imagePath").value="";

	document.getElementById("images").outerHTML += '';//清空IE的
	document.getElementById("images").value = "";//可以清空火狐的
	
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

<form:form modelAttribute="links" method="post" enctype="multipart/form-data">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/links/list${config:suffix()}?page=${param.page}'" value="返回">
</div>

<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>名称：</TD>
		    <TD class=t-content width="88%" colSpan=3>
		    	<input class="form-text" name="name" size="50" value="${links.name}"/>
			    &nbsp;<web:errors path="name" cssClass="span-text"/>
		    </TD>
	    </TR>
	    <TR>
		    <TD class="t-label t-label-h" width="15%"><SPAN class="span-text">*</SPAN>网址：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	<input class="form-text" name="website" size="50" value="${links.website}"/>
		      	&nbsp;<web:errors path="website" cssClass="span-text"/>
		    </TD>
		</TR>
    	<TR>
		    <TD class="t-label t-label-h" width="15%"><SPAN class="span-text">*</SPAN>排序：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	<INPUT class="form-text" name="sort" size="5" value="${links.sort}">
		      	&nbsp;<web:errors path="sort" cssClass="span-text"/>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">图片：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    
		    	<TABLE cellSpacing=0 cellPadding=0 width="200px" border=0>
		    		<tr align="left">
		    			<td width="46px"  height="46px"><div id="preview_1"><img id="imghead_1" width='40px' border='0' src='${imagePath}<c:if test="${imagePath == null || imagePath ==''}">backstage/images/null.gif</c:if>' ></div></td>
		    			<td width="80px">
		    				<a href="javascript:void(0);" class="fileButtonPic"><span><em>+</em>浏览...</span><input id="images" name="images" type="file" class="fileButton" size="12" onchange="previewImage(this,'imghead_1', 'preview_1')"/><input type="hidden" id="imagePath" name="imagePath" value='${imagePath}'/></a>
		    			</td>
		    			<td width="30px" >
		    				<input type="button" class="functionButton5" onClick="deleteImage(1); return false" value="删除" />
		    			</td>
		    		</tr>
		    	</TABLE>
		    	<span class="span-text">${error["images"]}</span>	
		    </TD>
		</TR>
		<TR>
		    <TD class="t-button" colSpan="4">
		       <span class="submitButton"><INPUT type="submit" value="提交" ></span>
		  	</TD>
		</TR>
	</TBODY>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
