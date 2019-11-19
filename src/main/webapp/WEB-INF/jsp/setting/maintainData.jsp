<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>维护数据</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

</HEAD>

<script language="javascript" type="text/javascript">
//重建话题索引
function rebuildTopicIndex(){
	var parameter = "";
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//设置按钮不可用
	document.getElementById("rebuildTopicIndex_button").disabled = true;
	post_request(function(value){
		if(value != ""){
			if(value == "1"){
				document.getElementById("rebuildTopicIndex_message").innerHTML="任务开始运行";	
				document.getElementById("rebuildTopicIndex_error").innerHTML="";
			}
			if(value == "2"){
				document.getElementById("rebuildTopicIndex_error").innerHTML="任务正在运行,请稍后再试";
				//设置按钮可用
				document.getElementById("rebuildTopicIndex_button").disabled = false;
			}
			if(value == "3"){
				document.getElementById("rebuildTopicIndex_error").innerHTML="索引运行过程中，不能执行创建";
				//设置按钮可用
				document.getElementById("rebuildTopicIndex_button").disabled = false;
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=rebuildTopicIndex&timestamp=" + new Date().getTime(), true,parameter);
} 


//重建问题索引
function rebuildQuestionIndex(){
	var parameter = "";
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//设置按钮不可用
	document.getElementById("rebuildQuestionIndex_button").disabled = true;
	post_request(function(value){
		if(value != ""){
			if(value == "1"){
				document.getElementById("rebuildQuestionIndex_message").innerHTML="任务开始运行";	
				document.getElementById("rebuildQuestionIndex_error").innerHTML="";
			}
			if(value == "2"){
				document.getElementById("rebuildQuestionIndex_error").innerHTML="任务正在运行,请稍后再试";
				//设置按钮可用
				document.getElementById("rebuildQuestionIndex_button").disabled = false;
			}
			if(value == "3"){
				document.getElementById("rebuildQuestionIndex_error").innerHTML="索引运行过程中，不能执行创建";
				//设置按钮可用
				document.getElementById("rebuildQuestionIndex_button").disabled = false;
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=rebuildQuestionIndex&timestamp=" + new Date().getTime(), true,parameter);
} 

//修改数据库密码页
function updateDatabasePasswordUI(){	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"<span class=\"span-text\">*</span>旧密码：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input type=\"password\" class=\"form-text\" id=\"oldPassword\" size=\"40\" value=\"\"/>&nbsp;<span id=\"oldPassword_error\" name=\"errorTag\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"<span class=\"span-text\">*</span>新密码：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input type=\"password\" class=\"form-text\" id=\"newPassword\" size=\"40\" value=\"\"/>&nbsp;<span id=\"newPassword_error\" name=\"errorTag\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"<span class=\"span-text\">*</span>重复密码：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input type=\"password\" class=\"form-text\" id=\"repeatPassword\" size=\"40\" value=\"\"/>&nbsp;<span id=\"repeatPassword_error\" name=\"errorTag\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" value=\"提交\" onClick=\"javascript:updateDatabasePassword()\"></span>&nbsp;<span id=\"updateDatabasePassword_error\" name=\"errorTag\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("修改数据库密码",html,700,200);//显示层

}
//修改数据库密码
function updateDatabasePassword(){
	var parameter = "";
	
	var oldPassword = document.getElementById("oldPassword").value;
	parameter += "&oldPassword="+oldPassword;
	var newPassword = document.getElementById("newPassword").value;
	parameter += "&newPassword="+newPassword;
	var repeatPassword = document.getElementById("repeatPassword").value;
	parameter += "&repeatPassword="+repeatPassword;
	
	//清空错误
	clearError();
	
	if($.trim(oldPassword) == ''){
		document.getElementById("oldPassword_error").innerHTML="旧密码不能为空";
	}
	if($.trim(newPassword) == ''){
		document.getElementById("newPassword_error").innerHTML="新密码不能为空";
	}
	if($.trim(newPassword) != $.trim(repeatPassword)){
		document.getElementById("repeatPassword_error").innerHTML="两次输入新密码不一致";
		return;
	}
	
	
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	
	
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	post_request(function(value){
		if(value != ""){
			
			if(value == "1"){
				document.getElementById("updateDatabasePassword_message").innerHTML="修改密码成功";
				systemLayerClose();//关闭层
			}
			if(value == "2"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="修改密码失败";
			}
			if(value == "3"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="读取数库配置文件错误";
			}
			if(value == "4"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="读取旧密码为空";
			}
			if(value == "5"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="旧密码错误";
			}
			if(value == "6"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="加密数据库密码错误";
			}
			if(value == "7"){
				document.getElementById("updateDatabasePassword_message").innerHTML="";	
				document.getElementById("updateDatabasePassword_error").innerHTML="写入配置文件错误";
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=updateDatabasePassword&timestamp=" + new Date().getTime(), true,parameter);
}

//清理错误提示
function clearError(){
	var errorTag = getElementsByName_pseudo("span", "errorTag");
	for(var i = 0; i < errorTag.length; i++){
		errorTag[i].innerHTML = ""; 
	}
}
// 取得表格的伪属性("类型:如tr;td ","name值")
var getElementsByName_pseudo = function(tag, name){
    var returns = document.getElementsByName(name);
    if(returns.length > 0) return returns;
    returns = new Array();
    var e = document.getElementsByTagName(tag);
    for(var i = 0; i < e.length; i++){
        if(e[i].getAttribute("name") == name){
            returns[returns.length] = e[i];
        }
    }
    return returns;
};

//清空系统所有缓存
function clearAllCache(){
	var parameter = "";

	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//设置按钮不可用
	document.getElementById("clearAllCache_button").disabled = true;
	post_request(function(value){
		if(value != ""){
			if(value == "1"){
				document.getElementById("clearAllCache_message").innerHTML="清空缓存成功";	
				document.getElementById("clearAllCache_error").innerHTML="";
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=clearAllCache&timestamp=" + new Date().getTime(), true,parameter);


}
//删除浏览量数据
function deletePageViewData(){
	var parameter = "";
	
	var beforeTime = document.getElementById("deletePageViewData_beforeTime").value;
	parameter += "&deletePageViewData_beforeTime="+beforeTime;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//设置按钮不可用
	document.getElementById("deletePageViewData_button").disabled = true;
	post_request(function(value){
		if(value != ""){
			if(value == "1"){
				document.getElementById("deletePageViewData_message").innerHTML="任务开始运行";	
				document.getElementById("deletePageViewData_error").innerHTML="";
			}
			if(value == "3"){
				document.getElementById("deletePageViewData_error").innerHTML="时间不能为空";
				//设置按钮可用
				document.getElementById("deletePageViewData_button").disabled = false;
			}
			if(value == "4"){
				document.getElementById("deletePageViewData_error").innerHTML="时间格式错误";
				//设置按钮可用
				document.getElementById("deletePageViewData_button").disabled = false;
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=deletePageViewData&timestamp=" + new Date().getTime(), true,parameter);
} 

//删除用户登录日志数据
function deleteUserLoginLogData(){
	var parameter = "";
	
	var beforeTime = document.getElementById("deleteUserLoginLogData_beforeTime").value;
	parameter += "&deleteUserLoginLogData_beforeTime="+beforeTime;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//设置按钮不可用
	document.getElementById("deleteUserLoginLogData_button").disabled = true;
	post_request(function(value){
		if(value != ""){
			if(value == "1"){
				document.getElementById("deleteUserLoginLogData_message").innerHTML="任务开始运行";	
				document.getElementById("deleteUserLoginLogData_error").innerHTML="";
			}
			if(value == "3"){
				document.getElementById("deleteUserLoginLogData_error").innerHTML="时间不能为空";
				//设置按钮可用
				document.getElementById("deleteUserLoginLogData_button").disabled = false;
			}
			if(value == "4"){
				document.getElementById("deleteUserLoginLogData_error").innerHTML="时间格式错误";
				//设置按钮可用
				document.getElementById("deleteUserLoginLogData_button").disabled = false;
			}
		}	
	},
		"${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=deleteUserLoginLogData&timestamp=" + new Date().getTime(), true,parameter);
} 


</script> 



<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<script type="text/javascript" src="backstage/lhgcalendar/lhgcore.lhgcalendar.min.js" language="javascript" ></script>

<form:form modelAttribute="systemSetting" method="post" >
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="30%">重建话题索引：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<INPUT id="rebuildTopicIndex_button" type="button" class="functionButton5" value="开始重建" onClick="javascript:rebuildTopicIndex()">&nbsp;<span id="rebuildTopicIndex_message" style="color: green;"></span>&nbsp;<span id="rebuildTopicIndex_error" style="color: red;"></span>&nbsp;<span class="span-help">需要时间取决于话题数量</span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="30%">重建问题索引：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<INPUT id="rebuildQuestionIndex_button" type="button" class="functionButton5" value="开始重建" onClick="javascript:rebuildQuestionIndex()">&nbsp;<span id="rebuildQuestionIndex_message" style="color: green;"></span>&nbsp;<span id="rebuildQuestionIndex_error" style="color: red;"></span>&nbsp;<span class="span-help">需要时间取决于话题数量</span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="30%">修改数据库密码：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<INPUT id="updateDatabasePassword_button" type="button" class="functionButton5" value="修改" onClick="javascript:updateDatabasePasswordUI()">&nbsp;<span id="updateDatabasePassword_message" style="color: green;"></span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="30%">清空系统所有缓存：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<INPUT id="clearAllCache_button" type="button" class="functionButton5" value="清空" onClick="javascript:clearAllCache()">&nbsp;<span id="clearAllCache_message" style="color: green;"></span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="30%">删除浏览量数据：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<input id="deletePageViewData_beforeTime" class="date-input" type="text" size="25" value="">之前的数据
    	<INPUT id="deletePageViewData_button" type="button" class="functionButton5" value="开始" onClick="javascript:deletePageViewData();">&nbsp;<span id="deletePageViewData_message" style="color: green;"></span>&nbsp;<span id="deletePageViewData_error" style="color: red;"></span>&nbsp;<span class="span-help">需要时间取决于数据数量</span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="30%">删除用户登录日志数据：</TD>
    <TD class="t-content" width="70%" colSpan="3">
    	<input id="deleteUserLoginLogData_beforeTime" class="date-input" type="text" size="25" value="">之前的数据
    	<INPUT id="deleteUserLoginLogData_button" type="button" class="functionButton5" value="开始" onClick="javascript:deleteUserLoginLogData();">&nbsp;<span id="deleteUserLoginLogData_message" style="color: green;"></span>&nbsp;<span id="deleteUserLoginLogData_error" style="color: red;"></span>&nbsp;<span class="span-help">需要时间取决于日志数量</span>
    </TD>
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
<script type="text/javascript">
//JS日期控件
$(function(){
    $('#deletePageViewData_beforeTime').calendar({format:'yyyy-MM-dd HH:mm'});
    $('#deleteUserLoginLogData_beforeTime').calendar({format:'yyyy-MM-dd HH:mm'});
});
</script>
</BODY></HTML>
