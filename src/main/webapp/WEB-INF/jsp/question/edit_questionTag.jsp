<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改问题标签</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css?2ddddddddddd" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">

<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script type="text/javascript" src="backstage/js/ImagePreview.js"></script>
<script language="javascript" src="backstage/jquery/jquery.min.js" type="text/javascript"></script>

<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript"></script>
<link href="backstage/jcrop/css/jquery.Jcrop.min.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/jcrop/js/jquery.Jcrop.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

<script type="text/javascript" language="javascript"> 
	
    //提交标签
	function tagSubmit(){
		//清空所有错误
        $("[name='error']").html("");
		
		
		//按钮设置 disabled="disabled"
		document.getElementById("tagButtonSubmit").disabled=true;
		
		var context = "";
		
		
		//标签名称
		var name = document.getElementById("name").value;
		if(name != ""){
			context += "&name="+encodeURIComponent(name);
		}
		//排序
		var sort = document.getElementById("sort").value;
		if(sort != ""){
			context += "&sort="+encodeURIComponent(sort);
		}
		
		var parentId = getUrlParam("parentId");//获取URL参数
		
		var tagId = getUrlParam("questionTagId");//获取URL参数
		if(tagId != ""){
			context += "&questionTagId="+tagId;
		}
		

		$("#tagForm").ajaxSubmit({
	        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
	        type:'POST',// 提交类型可以是"GET"或者"POST"
	        url:'control/questionTag/manage.htm?method=edit'+context,// 表单提交的路径
	        beforeSend: function() {//表单提交前做表单验证

	        },
	        success: function(data) {//提交成功后调用      	
	        	for(var returnValue in data){
	        		if(returnValue == "success"){
	        			if(data[returnValue] == "true"){
	        				layer.msg('提交标签成功,3秒后跳转到标签列表页', 
								{
								  time: 3000 //3秒关闭（如果不配置，默认是3秒）
								},function(){
									//关闭后的操作
									window.location.href = getBasePath()+"control/questionTag/list.htm?parentId="+parentId;//跳转
								}
							);
	        			}
	        		}else if(returnValue == "error"){
	        			var errorValue = data[returnValue];
						for(var error in errorValue){
							console.log(error+" - "+errorValue[error]);
							if(error != ""){	
								document.getElementById(error+"_error").innerHTML=errorValue[error];
							}
						}
	        		}
	        		
	        	}
				//按钮设置 disabled="disabled"
				document.getElementById("tagButtonSubmit").disabled=false;
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
<form:form modelAttribute="questionTag" id="tagForm" method="post" >
<DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;标签导航:</span>
			<span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/questionTag/list${config:suffix()}?parentId=0">全部标签</a></span>
			<span style="float:left;margin-top: 2px;">
			&nbsp;
				<c:forEach items="${navigation}" var="navigationMap" varStatus="status">
					>>&nbsp;<a href="${config:url(pageContext.request)}control/questionTag/list${config:suffix()}?parentId=${navigationMap.key}">${navigationMap.value}</a>
				</c:forEach>
			</span>
		</TD>
	</TR>
</TBODY>
</TABLE> 
<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
  <TBODY>
	<c:if test="${tag.parentId != null && tag.parentId >0 }">
		<TR>
		    <TD class="t-label t-label-h" width="12%">父标签名称：</TD>
		    <TD class="t-content" width="88%" colSpan=3>
		    	${parentName }
		    </TD>
		</TR>
	</c:if>
	<TR>
	    <TD class="t-label t-label-h" width="12%" ><SPAN class="span-text">*</SPAN>标签名称：</TD>
	    <TD class=t-content width="88%" colSpan=3>
	    	<input type="text" class="form-text" id="name" maxlength="190" size="50" value="${tag.name}">
		    &nbsp;<span id="name_error"  name="error" class="span-text"></span>
	    </TD>
    </TR>
	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>排序：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input type="text" class="form-text" id="sort" maxlength="10" size="10" value="${tag.sort}">
	    	&nbsp;<span id="sort_error"  name="error" class="span-text"></span>&nbsp;<SPAN class="span-help">数字越大越在前</SPAN>
	    </TD>
	</TR>
	
	<TR>
	    <TD class="t-button" colSpan="4">
	        <span class="submitButton"><INPUT type="button" id="tagButtonSubmit" value="提交"  onClick="javascript:tagSubmit();"></span>
	        &nbsp;<span id='progressBar'></span>&nbsp;<span id="tag_error" name="error" class="span-text"></span>
	  	</TD>
	</TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>