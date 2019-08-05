<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>合并分类</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

</HEAD>

<script type="text/javascript" language="javascript">
// 弹出查看分类层
function showHelpTypePageDiv(){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择分类",div,700,400);//显示层
	//显示商品分类分页显示	
	helpTypePage(1,-1);
}
//显示帮助分类
function helpTypePage(pages,parentId){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/helpType/manage${config:suffix()}?method=helpTypePageSelect&page="+pages+"&parentId="+parentId+"&template=any&timestamp=" + new Date().getTime(), true);

}

//添加分类
function addType(id,name,tableName){
	document.getElementById("mergerTypeId").value= id; 
	document.getElementById("typeName").value = name;
	//关闭层
	systemLayerClose();
}
</script>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="helpType" method="post" >
<DIV class="d-box">
<div class="d-button" style="height:30px;margin-left: auto; margin-right: auto;line-height: 30px;overflow: hidden ">
	<span style="font-weight:bold;float:left;">&nbsp;分类导航:</span>
	<span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/helpType/list${config:suffix()}?parentId=0">全部分类</a></span>
	<span style="float:left">
	&nbsp;
		<c:forEach items="${navigation}" var="navigationMap" varStatus="status">
			>>&nbsp;<a href="${config:url(pageContext.request)}control/helpType/list${config:suffix()}?parentId=${navigationMap.key}">${navigationMap.value}</a>
		</c:forEach>
	</span>
</div>
<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="18%">主分类：</TD>
    <TD class=t-content width="82%" colSpan=3>${helpType.name }
    </TD></TR>
  <TR>
    <TD class="t-label t-label-h" width="18%">选择分类合并到主分类：</TD>
    <TD class="t-content" width="82%" colSpan="3">
    	<input type="hidden" id="mergerTypeId" name="mergerTypeId" value="">
    	<input type="text" class="form-text" id="typeName" disabled="true" size="20"/> 
    	&nbsp;<INPUT type="button" class="functionButton5" value="选择" onClick="javaScript:showHelpTypePageDiv();"> 
    	&nbsp;<SPAN class="span-text">选择其他分类合并到主分类不能选择主分类的父类或子类进行合并</SPAN>
    </TD>
  </TR>
 
	<tr>
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="submit" value="提交"></span>
  	</TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>
