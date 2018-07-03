<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD><TITLE>资源查看</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">


<link rel="stylesheet" href="backstage/syntaxhighlighter/styles/shCoreDefault.css"  type="text/css" />
<script language="JavaScript" src="backstage/syntaxhighlighter/scripts/shCore.js" type="text/javascript"></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushJScript.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushCss.js" type="text/javascript"></script>
<script type="text/javascript">
	SyntaxHighlighter.all();
	SyntaxHighlighter.defaults['toolbar'] = false;//去掉右上角问号图标
</script>
</HEAD>
<BODY>
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	<TR>
	    <TD class="t-content" width="100%" >
		    <pre class="brush: ${fileType };">
				${fileContent }
			</pre>
	    </TD>
    </TR>
	</TBODY>
</TABLE>
</DIV>
</BODY>
</HTML>