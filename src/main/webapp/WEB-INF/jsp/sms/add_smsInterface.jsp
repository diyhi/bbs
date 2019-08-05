<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加短信接口</TITLE>
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
	styles=new Array('1','10'); 
	for(key in styles) {
		var obj=document.getElementById('dynamicParameter_'+styles[key]); 
		obj.style.display=styles[key]==objs.options[objs.selectedIndex].value?'':'none';
		
		//设置发送服务
		var sendService = getElementsByName_pseudo("table", 'sendService_'+styles[key]);
		if(sendService != null && sendService.length >0){
			for(var i=0;i<sendService.length;i++){
				sendService[i].style.display=styles[key]==objs.options[objs.selectedIndex].value?'':'none';
			}
		}
		/**
		var sendService =document.getElementById('sendService_'+styles[key]); 
		if(sendService != null){
			sendService.style.display=styles[key]==objs.options[objs.selectedIndex].value?'':'none';
		}
		**/
	}
}
//设置提交服务
//function setSendService(){
//	getUrlParam();

//}




</script>
</HEAD>
<BODY> 
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="smsInterface">
<DIV class="d-box">
<div class="d-button">
	<input type="button" class="functionButton" value="返回" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/smsInterface/list${config:suffix()}'">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>名称：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="name" size="40" maxlength="80" value="${smsInterface.name}">
	    	&nbsp;<web:errors path="name" cssClass="span-text"/>
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >接口产品 ：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<select class="form-select" id="interfaceProduct" name="interfaceProduct" onChange="selectInterfaceProduct()">
	    		<option value="0" >选择</option>
	    		<c:forEach items="${smsInterfaceProductList}" var="smsInterfaceProduct">
	    			<c:set var="smsInterfaceProductValue" value=""></c:set>
	    			<c:if test="${smsInterfaceProduct == 1}">
	    				<c:set var="smsInterfaceProductValue" value="阿里大于"></c:set>
	    			</c:if>
	    			<c:if test="${smsInterfaceProduct == 10}">
	    				<c:set var="smsInterfaceProductValue" value="云片"></c:set>
	    			</c:if>
	    			
	    			<option value="${smsInterfaceProduct}" <c:if test="${smsInterface.interfaceProduct == smsInterfaceProduct}"> selected='selected'</c:if>>${smsInterfaceProductValue }</option>
	    		</c:forEach>
	    	</select>
	    	<c:if test="${fn:length(smsInterfaceProductList) == 0}">
	    		&nbsp;<span class="span-text">接口已添加完</span>
	    	</c:if>
	    	
	    	&nbsp;<web:errors path="interfaceProduct" cssClass="span-text"/>
	    	&nbsp;<span class="span-help">每种接口只能添加一次</span>
	    </TD>
	  </TR>	
	  <TBODY id="dynamicParameter_1" <c:if test="${smsInterface.interfaceProduct ne '1'}"> style="DISPLAY: none"</c:if>>
		  <TR>
			    <TD class="t-label t-label-h" width="15%" > <SPAN class="span-text">*</SPAN>用户密钥Id(accessKeyId)：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	
			    	<input class="form-text" name="alidayu_accessKeyId"  type="text" size="50" maxlength="200" value="${alidayu.accessKeyId}" />
			    	&nbsp;
			    	<a href="backstage/images/help/alidayu_accessKey.jpg" target="_blank">获取方法演示</a>
			    	&nbsp;<span  class="span-text" >${error['alidayu_accessKeyId']}</span>
			    	&nbsp;<span class="span-help"></span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="15%" ><SPAN class="span-text">*</SPAN>用户密钥(accessKeySecret)：</TD>
			    <TD class="t-content" width="85%" colSpan="3">
			    	<input class="form-text" name="alidayu_accessKeySecret"  type="text" size="50" maxlength="200" value="${alidayu.accessKeySecret}" />
			    	&nbsp;
			    	<a href="backstage/images/help/alidayu_accessKey.jpg" target="_blank">获取方法演示</a>
			    	&nbsp;<span  class="span-text" >${error['alidayu_accessKeySecret']}</span>
			    </TD>
			</TR>
	  </TBODY>
	 
	<TBODY id="dynamicParameter_10" <c:if test="${smsInterface.interfaceProduct ne '10'}"> style="DISPLAY: none"</c:if>>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >APPID：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    	
		    </TD>
		</TR>
		
		<TR>
		    <TD class="t-label t-label-h" width="15%" >商户的私钥(pkcs8格式)：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%" >公钥：</TD>
		    <TD class="t-content" width="85%" colSpan="3">
		    	
		    	
		    </TD>
		</TR>
	</TBODY>
	<TBODY>
 	<TR>
	    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>排序：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
	    	<input class="form-text" name="sort" size="8" maxlength="8" value="${smsInterface.sort}">
	    	&nbsp;<web:errors path="sort" cssClass="span-text"/>
	    </TD>
	  </TR>
	</TBODY>
</TABLE>
	
	<c:forEach items="${sendServiceList}" var="sendService">
		<c:if test="${sendService.interfaceProduct == 1}">
		<TABLE name="sendService_${sendService.interfaceProduct}" <c:if test="${smsInterface.interfaceProduct ne '1'}"> style="DISPLAY: none"</c:if> class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
			<TBODY>
				<TR>
					<TD class="t-button" colSpan="4" height="30px;">
						${sendService.serviceName}
					</TD>
				</TR>
				<TR>
			    	<TD class="t-label t-label-h" width="12%">短信签名：</TD>
			    	<TD class="t-content" width="88%" colSpan="3">
			    		<input class="form-text" name="alidayu_signName_${sendService.interfaceProduct}_${sendService.serviceId}" size="40" value="${sendService.alidayu_signName}">
			    		&nbsp;<span class="span-help">阿里云管理控制台审核通过的短信签名</span>
			    		<c:set value="alidayu_signName_${sendService.interfaceProduct}_${sendService.serviceId}" var="alidayu_signName_error"></c:set>
						&nbsp;<SPAN class="span-text" >${error[alidayu_signName_error]}</SPAN>
			    	</TD>
			    </TR>
				<TR>
				    <TD class="t-label t-label-h" width="15%" >短信模板代码 ：</TD>
				    <TD class="t-content" width="85%" colSpan="3">
				    	<input class="form-text" name="alidayu_templateCode_${sendService.interfaceProduct}_${sendService.serviceId}" size="40" value="${sendService.alidayu_templateCode}">
				    	&nbsp;<span class="span-help">例如：SMS_1000000</span>
				    	<c:set value="alidayu_templateCode_${sendService.interfaceProduct}_${sendService.serviceId}" var="alidayu_templateCode_error"></c:set>
						&nbsp;<SPAN class="span-text" >${error[alidayu_templateCode_error]}</SPAN>
				    </TD>  
				</TR>
				<TR>
				    <TD class="t-label t-label-h" width="15%" >支持变量 ：</TD>
				    <TD class="t-content" width="85%" colSpan="3">
					    <table style="width:100%;" >
						    <c:forEach items="${sendService.alidayu_variable}" var="entry" > 
						    <tr>
						  		<td style="border-bottom:1px dashed #BFE3FF; border-right:1px dashed #BFE3FF; line-height:26px;color: #999; text-align:left;width: 100px;">
						  			${entry.key}
						    	</td>
						    	<td style="border-bottom:1px dashed #BFE3FF; line-height:26px;color: #999; text-align:left;">
						    		${entry.value}
						    	</td>
						    	</tr>
						    
							</c:forEach>
						</table>
				    </TD> 
				</TR>
			</TBODY>
		</TABLE>	
		</c:if>
	</c:forEach>	
	
	
	
	
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>	
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:SureSubmit(this.form)"></span>
  	</TD>
  </TR></TBODY>
</TABLE>
</DIV>
</form:form>

</BODY></HTML>
