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
		objForm.submit();
	} 
</script>


<BODY>
<form action="install" method="post">
<DIV class="d-box"> 

<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="900px" border="0" align="center">
	<TBODY>
		<TR>
	    	<TD class="t-button" colSpan="4" height="32px"><span style="font-size: 16px; font-weight: bold;">安装轻论坛系统</span></TD>
	    </TR>
		<TR>
		    <TD class="t-label t-label-h" width="25%"><SPAN class="span-text">*</SPAN>数据库IP：</TD>
		    <TD class="t-content" width="75%" colSpan="3">
		    	<input class="form-text" name="databaseIP" value="${install.databaseIP}" size="50">
		    	<span class="span-text">${error['databaseIP']}</span>
		    </TD>
		</TR>
  		<TR>
			<TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>数据库端口：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="databasePort" value="${install.databasePort}" size="50">
		    	<span class="span-text">${error['databasePort']}</span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>数据库名称：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="databaseName" value="${install.databaseName}" size="50">
		    	<span class="span-text">${error['databaseName']}</span>
		    	&nbsp;<span class="span-help">编码为utf8mb4  
		    	<br><span style="line-height: 30px;">创建数据库语句参考：CREATE DATABASE `数据库名称` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;</span></span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>数据库用户名：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="databaseUser" value="${install.databaseUser}" size="50">
		    	<span class="span-text">${error['databaseUser']}</span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>数据库密码：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input type="password" class="form-text" name="databasePassword" value="${install.databasePassword}" size="50">
		    	<span class="span-text">${error['databasePassword']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>后台管理员账号：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="userAccount" value="${install.userAccount}" size="50">
		    	<span class="span-text">${error['userAccount']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>后台管理员密码：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input type="password" class="form-text" id="userPassword" name="userPassword" value="${install.userPassword}" size="50">
		    	<span id="password_error" class="span-text">${error['userPassword']}</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>后台管理员确认密码：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input type="password" class="form-text" id="confirmPassword" value="" size="50">
		    	<span id="confirmPassword_error" class="span-text"></span>
		    </TD>
		</TR>
  		<TR>
		    <TD class="t-label t-label-h" >缓存服务器：</TD>
		    <TD class="t-content" colSpan="3">
		    	<label><input type="radio" name="cacheServer" value="ehcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'ehcache'}">checked='checked'</c:if>>Ehcache</label>
		    	<label><input type="radio" name="cacheServer" value="memcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'memcache'}">checked='checked'</c:if>>Memcache</label>
		    </TD>
		</TR>
		<TR id="tr_memcacheIP" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>Memcache缓存服务器IP：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="memcacheIP" value="${install.memcacheIP}" size="50">
		    	<span class="span-text">${error['memcacheIP']}</span>
		    </TD>
		</TR>
		<TR id="tr_memcachePort" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <TD class="t-label t-label-h" ><SPAN class="span-text">*</SPAN>Memcache缓存服务器端口：</TD>
		    <TD class="t-content" colSpan="3">
		    	<input class="form-text" name="memcachePort" value="${install.memcachePort}" size="50">
		    	<span class="span-text">${error['memcachePort']}</span>
		    </TD>
		</TR>
	<TR>
	    <TD class="t-button" colSpan="4">
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
</BODY>
</html>
