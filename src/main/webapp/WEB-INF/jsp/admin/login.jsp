<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml">
<base href="${config:url(pageContext.request)}"></base>
<head>
	<title>员工登录</title>
	<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="shortcut icon" type="image/x-icon" href="${config:url(pageContext.request)}backstage/images/favicon.ico" media="screen" />
	<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
	<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
	<script type="text/javascript" src="backstage/cryptoJS/core-min.js" language="javascript" ></script>
	<script type="text/javascript" src="backstage/cryptoJS/sha256-min.js" language="javascript" ></script>
	<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
	<script type="text/javascript" src="backstage/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="backstage/jquery/jquery.placeholder.min.js"></script>
	<script type="text/javascript">  
		$(function() {  
        	$('input, textarea').placeholder();//实现HTML5中placeholder特效
        });  
    </script> 
	
	<script language="javascript" type="text/javascript">
	
		/**
		if (top.location !== self.location) {
		    top.location = "${config:url(pageContext.request)}admin/login.htm";//跳出框架，并回到登录页
		}**/
		
		//跳出框架
		if(window != top){
		//	top.location.href = location.href; 
			top.location = "${config:url(pageContext.request)}admin/login.htm";//跳出框架，并回到登录页
		}

	
		function sureSubmit(objForm){
			//清空错误
			document.getElementById("j_username_error").innerHTML = "";
			document.getElementById("j_password_error").innerHTML = "";
			document.getElementById("j_captchaValue_error").innerHTML = "";
			document.getElementById("login_error").innerHTML = "";

			//帐号
			var username = document.getElementById("j_username").value;
			//密码
			var password = document.getElementById("j_password").value;
	
			if("帐号" == username || trim(username) == ""){
			
				document.getElementById("j_username_error").innerHTML = "账号不能为空";
				return;
			}
			if("密码" == password){
				document.getElementById("j_password_error").innerHTML = "密码不能为空";
				return;
			}else{
				if(password != "" && trim(password) != ""){
					//密码需SHA256加密
					document.getElementById("j_password").value = CryptoJS.SHA256(trim(password));
				}else{
					document.getElementById("j_password_error").innerHTML = "密码不能为空";
					return;
				}
			}
		
		
			objForm.submit();	
		}
		//回车提交
		function enterSubmit(){
			if(window.event.keyCode == 13){
				document.getElementById("submitData").click();
			}
		
		}
		
		//验证参数
		function verification(field){
			if(field == "j_captchaValue"){//验证码
				if(document.getElementById("j_captchaKey") != ""){
					var captchaKey = document.getElementById("j_captchaKey").value;
					var parameter = document.getElementById(field).value;
					if(parameter != "" && parameter != "请输入验证码"){
						var parameter_trim = trim(parameter);
						if(parameter_trim != ""){
							get_request(function(value){
								
				            	if(value == "false"){
					            	document.getElementById(field+"_error").innerHTML = "验证码错误";
				            	}
				            },
						 		"userVerification.htm?captchaKey="+captchaKey+"&captchaValue="+parameter_trim+"&timestamp=" + new Date().getTime(), true);
				            	
						}else{
							document.getElementById(field+"_error").innerHTML = "验证码不能为空";
							return false;
						}
					}else{
						document.getElementById(field+"_error").innerHTML = "验证码不能为空";
						return false;
					}
					
				}
			}
			if(document.getElementById(field+"_error") != null){
				document.getElementById(field+"_error").innerHTML = "";
			}
			
			return true;
		}
		//去掉字符串前后空格
		function trim(str){   
		    str = str.replace(/^(\s|\u00A0)+/,'');   
		    for(var i=str.length-1; i>=0; i--){   
		        if(/\S/.test(str.charAt(i))){   
		            str = str.substring(0, i+1);   
		            break;   
		        };
		    }   
		    return str; 
		}  
		//更换验证码
		function replaceCaptcha(){
			var captchaKey = document.getElementById("j_captchaKey").value;
			
			document.getElementById("captcha").src = "captcha/"+captchaKey+".jpg?" + Math.random(); 
		}
		
		
	</script>
</head>
<body style="background: #f5f5f5" onkeydown="enterSubmit();">
	<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
	<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
	<div class="wrapper">
		<div class="loginForm">
			<div class="inner">
				<form action="${config:url(pageContext.request)}admin/loginVerification.htm" method="post">
					<div class="head">
                    	<h2>管理登录</h2>
                    </div>
                    
					<div class="formField">
                    	<input id="j_username" name="j_username" type="text" placeholder="帐号" autocomplete="off"  value="${SPRING_SECURITY_LAST_USERNAME}" autofocus>
                        <div id="j_username_error" class="err-tip"></div>
                    </div>
                    <div class="formField">
                        <input id="j_password" name="j_password" type="password" placeholder="密码" autocomplete="off" >
                        <div id="j_password_error" class="err-tip"></div>
                    </div>
                    <div class="formField" <c:if test="${isCaptcha == false}"> style='display: none;'</c:if>>
                        <div class="clearfix">
                        	<input id="j_captchaValue" name="j_captchaValue" type="text" placeholder="验证码" autocomplete="off" maxlength="4" class="captchaInput" onchange="verification(this.id);">
                            <span class="captchaImage"><img id="captcha" src="captcha/${captchaKey}.jpg" onClick="replaceCaptcha();" title="点击换一张" alt="点击换一张"></span>
                            
                        </div>
                        <div id="j_captchaValue_error" class="err-tip clearfix"></div>
                    </div>
                     
                    <div class="loginButton">
                        <input type="button" id="submitData"  onClick="javascript:sureSubmit(this.form);return false;" value="登 录">
                        <input type="hidden" id="j_captchaKey" name="j_captchaKey" value="${captchaKey}">
                         <!-- 令牌标记  -->
						<input type="hidden" name="j_token" value="${_csrf.token}">
						<!-- 错误信息 -->
						<div id="login_error" class="err-tip clearfix"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"></c:out></div>
                    </div>
				</form>
				<div class="connect">
					<p class="prompt">为保证您的账号安全，退出系统时请注销登录</p>
				</div>                   
			</div>
		</div>	
	</div>
	</body>
</html>
