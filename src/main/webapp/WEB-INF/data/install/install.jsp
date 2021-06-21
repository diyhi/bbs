<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="enhance" uri="http://pukkaone.github.com/jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config_url}">
<TITLE>安装轻论坛系统</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" type="image/x-icon" href="${config_url}backstage/images/favicon.ico" media="screen" />
<link href="backstage/css/common.css" type="text/css" rel="stylesheet">
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


<body>
<form action="install" method="post">


<table class="installModule" > 
	<thead> 
		<tr> 
			<th colspan="2">安装轻论坛系统</th>
		</tr> 
	</thead> 
	 <tbody> 
		<tr> 
			<td class="name"><span class="required">*</span>数据库IP：</td> 
			<td>
				<input class="form-text" name="databaseIP" value="${install.databaseIP}" size="50">
		    	<span class="error">${error['databaseIP']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>数据库端口：</td> 
			<td>
				<input class="form-text" name="databasePort" value="${install.databasePort}" size="50">
		    	<span class="error">${error['databasePort']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>数据库名称：</td> 
			<td>
				<input class="form-text" name="databaseName" value="${install.databaseName}" size="50">
		    	<span class="error">${error['databaseName']}</span>
		    	&nbsp;<span class="help">编码为utf8mb4  
		    	<br><span style="line-height: 30px;">创建数据库语句参考：CREATE DATABASE `数据库名称` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;</span></span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>数据库用户名：</td> 
			<td>
				<input class="form-text" name="databaseUser" value="${install.databaseUser}" size="50">
		    	<span class="error">${error['databaseUser']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>数据库密码：</td> 
			<td>
				<input type="password" class="form-text" name="databasePassword" value="${install.databasePassword}" size="50">
		    	<span class="error">${error['databasePassword']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>后台管理员账号：</td> 
			<td>
				<input class="form-text" name="userAccount" value="${install.userAccount}" size="50">
		    	<span class="error">${error['userAccount']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>后台管理员密码：</td> 
			<td>
				<input type="password" class="form-text" id="userPassword" name="userPassword" value="${install.userPassword}" size="50">
		    	<span id="password_error" class="error">${error['userPassword']}</span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>后台管理员确认密码：</td> 
			<td>
				<input type="password" class="form-text" id="confirmPassword" value="" size="50">
		    	<span id="confirmPassword_error" class="error"></span>
			</td> 
		</tr> 
		<tr> 
			<td class="name"><span class="required">*</span>缓存服务器</td> 
			<td>
				<label><input type="radio" name="cacheServer" value="ehcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'ehcache'}">checked='checked'</c:if>>Ehcache</label>
		    	<label><input type="radio" name="cacheServer" value="memcache" onclick="selectCache(this);" <c:if test="${install.cacheServer == 'memcache'}">checked='checked'</c:if>>Memcache</label>
			</td> 
		</tr> 
		<tr id="tr_memcacheIP" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <td class="name" ><span class="required">*</span>Memcache缓存服务器IP：</TD>
		    <td>
		    	<input class="form-text" name="memcacheIP" value="${install.memcacheIP}" size="50">
		    	<span class="error">${error['memcacheIP']}</span>
		    </td>
		</tr>
		<tr id="tr_memcachePort" <c:if test="${install.cacheServer == 'ehcache'}">style='display: none;'</c:if>>
		    <td class="name" ><span class="required">*</span>Memcache缓存服务器端口：</TD>
		    <td>
		    	<input class="form-text" name="memcachePort" value="${install.memcachePort}" size="50">
		    	<span class="error">${error['memcachePort']}</span>
		    </td>
		</tr>
		<tr> 
			<td colSpan="2" class="button">
				 <span class="submitButton"><INPUT type="button" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
		         <c:if test="${error['installSystem'] != null}">
		         	<p class="error">${error['installSystem']}</p>
		         </c:if>
		         <c:if test="${error['databaseLink'] != null}">
		         	<p class="error">${error['databaseLink']}</p>
		         </c:if>
		         <c:if test="${error['filePermissions'] != null}">
		         	<p class="error">${error['filePermissions']}</p>
		         </c:if>
		         <c:if test="${error['cacheServer'] != null}">
		         	<p class="error">${error['cacheServer']}</p>
		         </c:if>
			</td> 
		</tr> 
		
	</tbody> 
</table>
</form>
</body>
</html>
