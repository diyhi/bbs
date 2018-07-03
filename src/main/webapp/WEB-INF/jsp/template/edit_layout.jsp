<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加布局</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">

<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>


<SCRIPT type="text/JavaScript">
	function sureSubmit(objForm){
		//按钮设置 disabled="disabled"
		document.getElementById("submitForm").disabled=true;
		objForm.submit();
	} 

	//校验url名称
	function checkUrlName(referenceCode){
		var error_referenceCode = document.getElementById("error_referenceCode");		
		if(error_referenceCode){	
			get_request(function(value){error_referenceCode.innerHTML=value;},
					 "${config:url(pageContext.request)}control/layout/manage${config:suffix()}?method=checkUrlName&referenceCode="+referenceCode+"&dirName=${param.dirName}"+"&timestamp=" + new Date().getTime(), true);
		}
	}
</script>

</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="layout">
<DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TR>
	    <TD class="t-content" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">

			
			&nbsp;>>&nbsp;<a href="control/layout/list${config:suffix()}?dirName=${param.dirName}">${templates.name}<span style="color: red">[${param.dirName }]</span> </a>
			
			&nbsp;>> &nbsp;修改布局
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%">类型：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<c:if test="${layout.type == 1}">默认页</c:if>
	    <c:if test="${layout.type == 3}">更多</c:if>
	    <c:if test="${layout.type == 4}">空白页</c:if>
	    <c:if test="${layout.type == 5}">公共页(生成新引用页)</c:if>
	    <c:if test="${layout.type == 6}">公共页(引用版块值)</c:if>
	    <c:if test="${layout.type == 7}">站点栏目详细页</c:if>
    </TD></TR>
   
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>布局名称：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input name="name" type="text" maxlength="80" size="50" value="${layout.name}">
    	<span class="span-text">${error['name']}</span>
    </TD>
    </TR>
    </TBODY>
    <TBODY id="style_3" <c:if test="${layout.type != 3}"> style="display: none;"</c:if>>
    	<TR>
			<TD class="t-label t-label-h" >版块数据：</TD>
			<TD class="t-content" colSpan="3">
				<label>
				<input type="radio" name="forumData" value="1" <c:if test="${layout.forumData == 1}"> checked='checked'</c:if>/>商品&nbsp;&nbsp;&nbsp;
				</label>
				<label>
				<input type="radio" name="forumData" value="2" <c:if test="${layout.forumData == 2}"> checked='checked'</c:if>/>资讯&nbsp;&nbsp;&nbsp;
				</label>
				<label>
				<input type="radio" name="forumData" value="3" <c:if test="${layout.forumData == 3}"> checked='checked'</c:if>/>在线帮助
				</label>
				<input type="hidden" name="_forumData">
			</TD>
		</TR>
	</TBODY>
    <TBODY id="style_4" <c:if test="${layout.type != 4}"> style="display: none;"</c:if>>
    	<TR>
			<TD class="t-label t-label-h">返回数据：</TD>
			<TD class="t-content" colSpan="3">
				<label>
				<input type="radio" name="returnData" value="0" <c:if test="${layout.returnData == 0}"> checked='checked'</c:if>/>html&nbsp;&nbsp;&nbsp;
				</label>
				<label>
				<input type="radio" name="returnData" value="1" <c:if test="${layout.returnData == 1}"> checked='checked'</c:if>/>json
				</label>
				<input type="hidden" name="_returnData">
			</TD>
		</TR>
        <TR>
        	<TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>URL名称：</TD>
        	<TD class="t-content" width="88%" colSpan="3">
        		<INPUT name="referenceCode" maxlength="40" size="40" value="${layout.referenceCode}" onBlur="checkUrlName(this.value)"/>
				&nbsp;<span id="error_referenceCode" class="span-text">${error['referenceCode']}</span>
        	</TD>
 		</TR>
 		 <TR>
 		<TD class="t-button" colSpan="4" height="25">
 		<SPAN class="span-help">备注：URL名称只能输入由数字、26个英文字母、下划线和或者左斜杆组成，左斜杆不能在最前面或最后面或连续出现。例一：aaa/ggg &nbsp;&nbsp; 例二：aaa </SPAN>
 		</TD> </TR>
	</TBODY>
	<TBODY>
	<tr>
    <TD class="t-button" colSpan="4">
  		<span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
  		
  	</TD>
  </TR>
  </TBODY>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
