<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加系统通知</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/jquery/jquery.min.js" language="JavaScript"></script>
<script type="text/javascript" src="backstage/js/ImagePreview.js"></script>

<script language="javascript" type="text/javascript">


</script> 

</HEAD>
<BODY>

<form:form modelAttribute="systemNotify" method="post">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/systemNotify/list${config:suffix()}?page=${param.page}'" value="返回">
</div>

<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>通知内容：</TD>
		    <TD class=t-content width="88%" colSpan=3>
		    	<textarea class="form-textarea" name="content" rows="10" cols="80">${systemNotify.content}</textarea>
			    &nbsp;<web:errors path="content" cssClass="span-text"/>
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
