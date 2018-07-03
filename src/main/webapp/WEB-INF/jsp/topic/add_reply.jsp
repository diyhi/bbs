<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>添加回复</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
</HEAD>

<script language="javascript" type="text/javascript">
function sureSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "";
	//评论Id
	var commentId = document.getElementById("commentId");
	if(commentId != null){
		parameter += "&commentId="+commentId.value;
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
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=addReply&timestamp=" + new Date().getTime(), true,parameter);




} 

</script> 
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<input type="hidden" id="commentId" value="${param.commentId}">
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
    <TR>
    <TD class="t-content" >
    	<textarea id="content" name="content" style="width:99%;height:300px;"></textarea>
    </TD>
   </TR>
	<TR>
    <TD class="t-button">
    	<SPAN id="commentId_error" name="error" class="span-text" ></SPAN>
	  	<SPAN id="content_error" name="error" class="span-text" ></SPAN>
	  	<span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit();"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>