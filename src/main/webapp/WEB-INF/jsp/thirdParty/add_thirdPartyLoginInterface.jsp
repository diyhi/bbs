<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加第三方登录接口</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="javascript" src="backstage/js/browserDetect.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript">



function SureSubmit(objForm){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	objForm.submit();
} 


//选择接口产品
function selectInterfaceProduct(){
	var objs = document.getElementById("interfaceProduct");
	var styles, key;
	styles=new Array('10'); 
	for(key in styles) {
		var obj=document.getElementById('style_'+styles[key]); 
		obj.style.display=styles[key]==objs.options[objs.selectedIndex].value?'':'none';
	}
}

</script>
</HEAD>
<BODY> 
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="thirdPartyLoginInterface">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/thirdPartyLoginInterface/list${config:suffix()}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
	  	<TR>
		    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>名称：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<input class="form-text" name="name" size="40" maxlength="80" value="${thirdPartyLoginInterface.name}">
		    	&nbsp;<web:errors path="name" cssClass="span-text"/>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >接口产品 ：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	<select class="form-select" id="interfaceProduct" name="interfaceProduct" onChange="selectInterfaceProduct()">
		    		<option value="0" >选择</option>
		    		<c:forEach items="${interfaceProductList}" var="interfaceProduct">
		    			<c:set var="interfaceProductValue" value=""></c:set>
		    			<c:if test="${interfaceProduct == 10}">
		    				<c:set var="interfaceProductValue" value="微信"></c:set>
		    			</c:if>
		    			
		    			<option value="${interfaceProduct}" <c:if test="${thirdPartyLoginInterface.interfaceProduct == interfaceProduct}"> selected='selected'</c:if>>${interfaceProductValue }</option>
		    		</c:forEach>
		    	</select>
		    	<c:if test="${fn:length(interfaceProductList) == 0}">
		    		&nbsp;<span class="span-text">接口已添加完</span>
		    	</c:if>
		    	
		    	&nbsp;<web:errors path="interfaceProduct" cssClass="span-text"/>
		    	&nbsp;<span class="span-help">每种接口只能添加一次</span>
		    </TD>
		</TR>	
		<TBODY id="style_10" <c:if test="${thirdPartyLoginInterface.interfaceProduct ne '10'}"> style="DISPLAY: none"</c:if>>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >微信开放平台 AppID：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<input name="weixin_op_appID"  type="text" class="form-text" size="50" maxlength="200" value="${weChatConfig.op_appID}" />
			    	
			    	&nbsp;<span  class="span-text" >${error['weixin_op_appID']}</span>
			    	&nbsp;<span class="span-help">微信开放平台应用唯一标识</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >微信开放平台 AppSecret：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	<input name="weixin_op_appSecret"  type="text" class="form-text" size="50" maxlength="200" value="${weChatConfig.op_appSecret}" />
			    	&nbsp;<span  class="span-text" >${error['weixin_op_appSecret']}</span>
			    	&nbsp;<span class="span-help">应用密钥</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >微信公众号 AppID：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<input name="weixin_oa_appID"  type="text" class="form-text" size="50" maxlength="200" value="${weChatConfig.oa_appID}" />
			    	
			    	&nbsp;<span  class="span-text" >${error['weixin_oa_appID']}</span>
			    	&nbsp;<span class="span-help">微信公众平台服务号的应用唯一标识</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >微信公众号 AppSecret：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	<input name="weixin_oa_appSecret"  type="text" class="form-text" size="50" maxlength="200" value="${weChatConfig.oa_appSecret}" />
			    	&nbsp;<span  class="span-text" >${error['weixin_oa_appSecret']}</span>
			    	&nbsp;<span class="span-help">应用密钥</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >注意：</TD>
			    <TD class="t-content" width="85%" colSpan="4">
			    	1.需要在微信公众平台的“开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息”的配置选项中，修改授权回调域名 (域名格式不需要加http://协议头)<br>
			    	2.需要在微信开放平台绑定公众号
			    </TD>
			</TR>
		</TBODY>
	 

	</TBODY>
 	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>排序：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="sort" size="8" maxlength="8" value="${thirdPartyLoginInterface.sort}">
	    	&nbsp;<web:errors path="sort" cssClass="span-text"/>
	    </TD>
	  </TR>
	  
	  <TR>
	    <TD class="t-label t-label-h" width="12%">启用：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<label><input type="radio" name="enable" value="true" <c:if test="${thirdPartyLoginInterface.enable}"> checked="checked"</c:if>/>是</label>
			<label><input type="radio" name="enable" value="false" <c:if test="${!thirdPartyLoginInterface.enable}"> checked="checked"</c:if>/>否</label>
   			<input name="_enable" type="hidden" >
	    </TD>
	  </TR>

	<TR>
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:SureSubmit(this.form)"></span>
  	</TD>
  </TR></TBODY>
		</TABLE>
</DIV>
</form:form>

</BODY></HTML>
