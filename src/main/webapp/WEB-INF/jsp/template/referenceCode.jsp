<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>生成模块引用代码</TITLE>
<base href="${config:url(pageContext.request)}">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="backstage/css/list.css" type="text/css">
<link rel="stylesheet" href="backstage/css/table.css" type="text/css" >
<script language="javascript" src="backstage/js/clipboard.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/browserDetect.js" type="text/javascript"></script>

</HEAD>
<body> 
  
<div class="tipModule" >
    <div class="tipModule_head" align="center">
        <c:if test="${layout.returnData eq 0}">请复制引用代码到相应的页面</c:if>
        <c:if test="${layout.returnData eq 1}">请复制URL到相应的页面</c:if>
    </div>
    <div class="tipModule_content">
    	<span><h2>
    	
    	<c:if test="${layout.returnData eq 0}">		  
    		<!-- 调用方式  1.引用代码 -->
			<c:if test="${forum.invokeMethod == 1}">
				<span id="contents">
				 	&lt;@include action=&quot;&#36;&#123;${forum.referenceCode }&#125;&quot;/&gt;
				</span>
			</c:if>
			<!-- 调用方式  2.调用对象 -->
			<c:if test="${forum.invokeMethod == 2}">
				<span id="contents">
					&lt;@object action=&quot;&#36;&#123;${forum.referenceCode }&#125&quot;&gt; <br>
					&lt;#assign value = ${code}&gt;<br>
					&lt;/@object&gt;
				</span>
			</c:if>
		</c:if>
		<!-- JSON -->
		<c:if test="${layout.returnData eq 1}">
			
				<span id="contents">
					${config:url(pageContext.request)}${layout.referenceCode}${config:suffix()}
				</span>
			
		</c:if>
    	</h2></span>
    </div>
    <div class="tipModule_bottom">
	    <c:choose>  
		    <c:when test="${forum.layoutType eq 6 && layout.returnData eq 0}">
		        <!-- 公共页(引用版块值)跳转到布局  -->
		        <span class="submitButton"><INPUT type="button" class="btn" value="复制到布局" onclick="copyText();" data-clipboard-target="#contents" ></span>
		    	<input type="hidden" id="jumpUrl" value="${config:url(pageContext.request)}control/layout/list${config:suffix()}?dirName=${forum.dirName}">
		    </c:when>  
		    <c:when test="${layout.returnData eq 1}">  
		    	<!-- 空白页(json)跳转到布局列表 -->
		        <span class="submitButton"><INPUT type="button" class="btn" value="复制到布局列表" onclick="copyText();" data-clipboard-target="#contents" ></span>
		    	<input type="hidden" id="jumpUrl" value="${config:url(pageContext.request)}control/layout/list${config:suffix()}?&dirName=${forum.dirName}">
		    </c:when>  
		    <c:otherwise>  
		    	<!-- 跳转到布局编辑 -->
		    	<span class="submitButton"><INPUT type="button" class="btn" value="复制到布局" onclick="copyText();" data-clipboard-target="#contents" /></span>
		    	<input type="hidden" id="jumpUrl" value="${config:url(pageContext.request)}control/layout/manage${config:suffix()}?method=editLayoutCode&layoutId=${forum.layoutId}&dirName=${forum.dirName}">
		    </c:otherwise>  
		</c:choose>
	</div>
</div>


<script type="text/javascript">
function copyText(){ 
	if(BrowserDetect.browser == "Internet Explorer"){
		var contents = document.getElementById('contents');
		var rng = document.body.createTextRange();  
		rng.moveToElementText(contents);  
		rng.scrollIntoView();  
		rng.select();  
		rng.execCommand("Copy");  
		rng.collapse(false);
		window.location = document.getElementById("jumpUrl").value;
	}else{
		var clipboard = new Clipboard('.btn');
		//复制成功回调
		clipboard.on('success', function(e) {
		    window.location = document.getElementById("jumpUrl").value;
		});
	}	
} 
</script>
</body>
</HTML>