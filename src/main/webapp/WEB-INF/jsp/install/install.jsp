<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config_url}">
<TITLE>安装轻论坛系统</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" type="image/x-icon" href="${config_url}backstage/images/favicon.ico" media="screen" />
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
</HEAD>
<script type="text/javascript">
	//选择缓存服务器
	function selectCache(obj){
		var cacheServer = document.getElementsByName("cacheServer");
		for(var i=0;i<cacheServer.length;i++){
			if(cacheServer[i].value == obj.value){
				document.getElementById("tr_memcacheIP").style.display = "";//显示
				document.getElementById("tr_memcachePort").style.display = "";//显示
			}else{
				document.getElementById("tr_memcacheIP").style.display = "none";//隐藏
				document.getElementById("tr_memcachePort").style.display = "none";//隐藏
			}	
		}
	}

	//显示服务协议
	function showAgreement(){
	
		var clause = document.getElementById("clause").value;
		layer.open({
			title:"服务协议",
			type: 0,
			area: ['800px', '500px'],
			closeBtn: 0,
			shadeClose: true,
			content: clause
		});
	
	
	}

	function sureSubmit(objForm){
		document.getElementById("password_error").innerHTML = "";
		document.getElementById("confirmPassword_error").innerHTML = "";
		var userPassword = document.getElementById("userPassword").value;
		if(userPassword == ""){
			document.getElementById("password_error").innerHTML = "密码不能为空";
			return;
		}
		var confirmPassword = document.getElementById("confirmPassword").value;
		if(userPassword != confirmPassword){
			document.getElementById("confirmPassword_error").innerHTML = "两次密码不相等";
			return;
		}
	
	
	
		var flag = true;
		var agreement = document.getElementsByName("agreement");
		for(var i=0;i<agreement.length;i++){
			if(!agreement[i].checked){
				flag = false;
				
			}
		}
		if(flag){
			objForm.submit();
		}else{
			alert("同意服务协议才能继续安装");
		}
	} 
</script>


<BODY>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form action="install" method="post">
<DIV class="d-box"> 

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="900px" border="0" align="center">
	<TBODY>
		<TR>
	    	<TD class="t-button" colSpan="4" height="32px"><span style="font-size: 16px; font-weight: bold;">安装轻论坛系统</span></TD>
	    </TR>
		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>数据库IP：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="databaseIP" value="${install.databaseIP}" size="50">
		    	<span class="span-text">${error['databaseIP']}</span>
		    </TD>
		</TR>
  		<TR>
			<TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>数据库端口：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="databasePort" value="${install.databasePort}" size="50">
		    	<span class="span-text">${error['databasePort']}</span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>数据库名称：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="databaseName" value="${install.databaseName}" size="50">
		    	<span class="span-text">${error['databaseName']}</span>
		    	&nbsp;<span class="span-help">编码为utf8mb4  
		    	<br><span style="line-height: 30px;">创建数据库语句参考：CREATE DATABASE `数据库名称` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;</span></span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>数据库用户名：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="databaseUser" value="${install.databaseUser}" size="50">
		    	<span class="span-text">${error['databaseUser']}</span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>数据库密码：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input type="password" name="databasePassword" value="${install.databasePassword}" size="50">
		    	<span class="span-text">${error['databasePassword']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>后台管理员账号：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="userAccount" value="${install.userAccount}" size="50">
		    	<span class="span-text">${error['userAccount']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>后台管理员密码：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input type="password" id="userPassword" name="userPassword" value="${install.userPassword}" size="50">
		    	<span id="password_error" class="span-text">${error['userPassword']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>后台管理员确认密码：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input type="password" id="confirmPassword" value="" size="50">
		    	<span id="confirmPassword_error" class="span-text"></span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" width="20%">缓存服务器：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<label><input type="radio" name="cacheServer" value="ehcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'ehcache'}">checked='checked'</c:if>>Ehcache</label>
		    	<label><input type="radio" name="cacheServer" value="memcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'memcache'}">checked='checked'</c:if>>Memcache</label>
		    </TD>
		</TR>
		<TR id="tr_memcacheIP" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>Memcache缓存服务器IP：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="memcacheIP" value="${install.memcacheIP}" size="50">
		    	<span class="span-text">${error['memcacheIP']}</span>
		    </TD>
		</TR>
		<TR id="tr_memcachePort" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <TD class="t-label t-label-h" width="20%"><SPAN class="span-text">*</SPAN>Memcache缓存服务器端口：</TD>
		    <TD class="t-content" width="80%" colSpan="3">
		    	<input name="memcachePort" value="${install.memcachePort}" size="50">
		    	<span class="span-text">${error['memcachePort']}</span>
		    </TD>
		</TR>
	<TR>
	    <TD class="t-button" colSpan="4">
	    	<input type="checkbox" name="agreement" value="true"/> <label for="agreement">同意<a href="" onclick="showAgreement(); return false;" style="color: #26a2ff;">服务协议</a></label>&nbsp;&nbsp;
	         <span class="submitButton"><INPUT type="button" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
	         <c:if test="${error['installSystem'] != null}">
	         	<p class="span-text">${error['installSystem']}</p>
	         </c:if>
	         <c:if test="${error['databaseLink'] != null}">
	         	<p class="span-text">${error['databaseLink']}</p>
	         </c:if>
	         <c:if test="${error['filePermissions'] != null}">
	         	<p class="span-text">${error['filePermissions']}</p>
	         </c:if>
	         <c:if test="${error['cacheServer'] != null}">
	         	<p class="span-text">${error['cacheServer']}</p>
	         </c:if>
	         
	  	</TD>
	</TR>
	</TBODY>
</TABLE>
</DIV>


</form>

<div  style="display: none;">
	<textarea id="clause" rows="10" cols="10">
		您一旦安装、复制或使用巡云轻论坛系统，表示您已经同意本协议条款。<br />
		<br />
		&nbsp;巡云轻论坛系统受软件版权法保护，无论个人或组织、盈利与否、用途如何（包括以学习和研究为目的），均需仔细阅读本协议，在理解、同意、并遵守本协议的全部条款后，方可开始开始安装和使用本软件。<br />
		<br />
		<br />
		一、许可<br />
		&nbsp; 1.1 如果您是个人用户，可将本软件用于非商业用途，而不必支付软件授权许可费。<br />
		&nbsp; 1.2 您可以在本协议的许可范围内，修改巡云轻论坛系统界面风格以适应您的网站要求。<br />
		&nbsp; 1.3 您拥有使用本软件构建的网站全部内容所有权，并独立承担与这些内容的相关法律义务。&nbsp;<br />
		&nbsp; 1.4 在获得商业授权之后，您可以将本软件用于商业用途。<br />
		&nbsp; 1.5 商业授权用户享有反映和提出意见的权力，并被优先考虑，但没有一定被采纳的承诺或保证。<br />
		<br />
		二、约束和限制<br />
		&nbsp; 2.1 在未获得商业授权之前，任何单位或个人不得将本软件用于商业用途（包括但不限于企业网站、政府单位网站、经营性网站、以盈利为目的的网站）和任何非个人所有的项目中。<br />
		&nbsp; 2.2 未经官方许可，禁止修改本软件的整体或任何部分用于重新发布第三方版本。<br />
		&nbsp; 2.3 不得对本软件或与之关联的商业授权进行出租、出售、抵押或发放子许可证。<br />
		&nbsp; 2.4 非商业用途在使用了巡云轻论坛系统时页面页脚处的 diyhi 版权标识必须完整保留。<br />
		<br />
		三、免责声明<br />
		&nbsp; 3.1 用户完全自愿使用本软件，您必须了解使用本软件的风险，且愿意承担使用本软件的风险。<br />
		&nbsp; 3.2 任何情况下，我们不就因使用或不能使用本软件所发生的特殊的、意外的、直接或间接的损失承担赔偿责任（包括但不限于，资料损失，资料执行不精确，或因由您或第三人承担的损失，或本程序无法与其他程序运作等）。即使已经被事先告知该损害发生的可能性。

		<div>
			<br />
		</div>
	</textarea>
</div>

</BODY>
</html>
