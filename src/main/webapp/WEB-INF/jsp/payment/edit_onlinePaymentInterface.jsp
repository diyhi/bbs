<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改在线支付接口</TITLE>
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
</script>
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<form:form modelAttribute="onlinePaymentInterface">
<DIV class="d-box" >
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/onlinePaymentInterface/list${config:suffix()}?page=${param.page}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>名称：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="name" size="40" maxlength="80" value="${onlinePaymentInterface.name}">
	    	&nbsp;<shop:errors path="name" cssClass="span-text"/>
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >接口产品 ：</TD>
	    <TD class="t-content" width="85%" colSpan="3">	    
	    	<c:if test="${onlinePaymentInterface.interfaceProduct == 1}">支付宝即时到账</c:if>
	    	<c:if test="${onlinePaymentInterface.interfaceProduct == 4}">支付宝手机网站(alipay.trade.wap.pay)</c:if>
	    </TD>
	  </TR>	
	  <TBODY id="style_1" <c:if test="${onlinePaymentInterface.interfaceProduct ne '1'}"> style="DISPLAY: none"</c:if>>
		<TR>
			    <TD class="t-label t-label-h" width="15%" >APPID：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<input class="form-text" name="direct_app_id"  type="text" size="50" maxlength="200" value="${alipayDirect.app_id }" />
			    	
			    	&nbsp;<span  class="span-text" >${error['direct_app_id']}</span>
			    	&nbsp;<span class="span-help">应用的APPID</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >商户签名算法类型：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	RSA2(2048位)
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >商户的私钥(pkcs8格式)：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<textarea class="form-textarea" id="alipay_pc"  name="direct_rsa_private_key" rows="10" cols="70">${alipayDirect.rsa_private_key}</textarea>
			    	
			    	&nbsp;<span  class="span-text" >${error['direct_rsa_private_key']}</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" >支付宝公钥：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<textarea class="form-textarea" name="direct_alipay_public_key" rows="10" cols="70">${alipayDirect.alipay_public_key}</textarea>
			    	
			    	&nbsp;<span  class="span-text" >${error['direct_alipay_public_key']}</span>
			    	&nbsp;<span class="span-help">查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥</span>
			    </TD>
			</TR>
	  </TBODY>
	  
	<TBODY id="style_4" <c:if test="${onlinePaymentInterface.interfaceProduct ne '4'}"> style="DISPLAY: none"</c:if>>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >APPID：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    	<input name="mobile_app_id"  type="text" class="form-text" size="50" maxlength="200" value="${alipayMobile.app_id }" />
		    	
		    	&nbsp;<span  class="span-text" >${error['mobile_app_id']}</span>
		    	&nbsp;<span class="span-help">应用的APPID</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >商户签名算法类型：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	RSA2(2048位)
		    	
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >商户的私钥(pkcs8格式)：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    	<textarea class="form-textarea" id="alipay_mobile" name="mobile_rsa_private_key" rows="10" cols="70">${alipayMobile.rsa_private_key}</textarea>
		    	<br>
		    	
		    	&nbsp;<span  class="span-text" >${error['mobile_rsa_private_key']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >支付宝公钥：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    	<textarea class="form-textarea" name="mobile_alipay_public_key" rows="10" cols="70">${alipayMobile.alipay_public_key}</textarea>
		    	
		    	&nbsp;<span  class="span-text" >${error['mobile_alipay_public_key']}</span>
		    	&nbsp;<span class="span-help">查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥</span>
		    </TD>
		</TR>
	</TBODY>
	</TBODY>
	  
	  
 	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>排序：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="sort" size="8" maxlength="8" value="${onlinePaymentInterface.sort}">
	    	&nbsp;<shop:errors path="sort" cssClass="span-text"/>
	    </TD>
	  </TR>
	  
	  <TR>
	    <TD class="t-label t-label-h" width="12%">启用：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<label><input type="radio" name="enable" value="true" <c:if test="${onlinePaymentInterface.enable}"> checked="checked"</c:if>/>是</label>
			<label><input type="radio" name="enable" value="false" <c:if test="${!onlinePaymentInterface.enable}"> checked="checked"</c:if>/>否</label>
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
