<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>系统设置</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link href="backstage/kindeditor/themes/default/icon.css" rel="stylesheet"/>

<!--选项卡-->
<style type="text/css">
.clearfix:after{
content:".";display:block;height:0;clear:both;visibility:hidden;
}
.clearfix{
display:inline-block;
}

.clearfix{
display:block;
}
.clear{
border-top:1px solid transparent!important;border-top:0;clear:both;line-height:0;font-size:0;height:0;height:1%;
}
.item{
margin:13px 0 0 0;background:url(backstage/images/tab_1.gif) repeat-x 0 1px;padding-left:5px;overflow:visible;
}
.item div{
cursor:pointer;background:url(backstage/images/tab_2.gif) no-repeat;color:#333333;display:block;float:left;height:17px;padding:4px 2px;text-align:center;width:91px;
}
.item .active{
background:url(backstage/images/tab_3.gif) no-repeat;color:#000;display:block;font-size:14px;font-weight:bold;height:28px;position:relative;margin-bottom:-4px;margin-top:-8px;padding-top:6px;width:117px;
}
</style>

<script language="javascript" type="text/javascript">
function findObj(theObj, theDoc){
	var p, i, foundObj;
    if(!theDoc) theDoc = document; 
        if((p = theObj.indexOf("?")) > 0 && parent.frames.length){   
            theDoc = parent.frames[theObj.substring(p+1)].document; 
			theObj = theObj.substring(0,p); 
        }  
        if(!(foundObj = theDoc[theObj]) && theDoc.all)
               foundObj = theDoc.all[theObj]; 
        for (i=0; !foundObj && i < theDoc.forms.length; i++)
               foundObj = theDoc.forms[i][theObj];
        for(i=0; !foundObj && theDoc.layers && i < theDoc.layers.length; i++) 
               foundObj = findObj(theObj,theDoc.layers[i].document);  
        if(!foundObj && document.getElementById) 
               foundObj = document.getElementById(theObj);   
        return foundObj;
} 
//添加一行
function add_Row(){ //读取最后一行的行号，存放在txtTRLastIndex文本框中 
	var lastIndex = findObj("supportBankLastIndex",document); 
	var rowID = parseInt(lastIndex.value); 
	var tables = findObj("supportBank_table",document); 
	//添加行 
	var newTR = tables.insertRow(tables.rows.length); 
	newTR.id = "supportBank_item" + rowID; 
	
		//添加列:参数名 
	var newNameTD=newTR.insertCell(0); 
	newNameTD.width="80%";
	newNameTD.align = "center";
	newNameTD.style.cssText="BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom: #BFE3FF 1px dotted;";
	//添加列内容 
	newNameTD.innerHTML = "<input type='text' class='form-text' name='supportBank' value='' size='80'/>"; 
	//添加列:删除按钮 
	var newDeleteTD=newTR.insertCell(1); 
	newDeleteTD.width="20%";
	newDeleteTD.style.cssText="border-bottom: #BFE3FF 1px dotted;";
	newDeleteTD.align = "center";
	//添加列内容 
	newDeleteTD.innerHTML = "<a href='javascript:;' onclick=\"delete_Row('supportBank_item" + rowID + "')\">删除</a>"; 
	
	//将行号推进下一行 
	lastIndex.value = (rowID + 1).toString(); 
} 

//删除指定行 
function delete_Row(rowId){ 
//	productType_map.remove(id);//删除

	var tables = findObj("supportBank_table",document); 
	var signItem = findObj(rowId,document); 

	//获取将要删除的行的Index 
	var rowIndex = signItem.rowIndex; 
	 
	//删除指定Index的行 
	tables.deleteRow(rowIndex); 
}
//选择图片参数
function selectImage(module,value){
	if(value == "true"){
	
		document.getElementById(module+"ImageFormat_tr").style.display = "";
		document.getElementById(module+"ImageSize_tr").style.display = "";
		
	}else{
		document.getElementById(module+"ImageFormat_tr").style.display = "none";
		document.getElementById(module+"ImageSize_tr").style.display = "none";
	}
}

//选择文件参数
function selectFile(module,value){
	if(value == "true"){
	
		document.getElementById(module+"FileFormat_tr").style.display = "";
		document.getElementById(module+"FileSize_tr").style.display = "";
		
	}else{
		document.getElementById(module+"FileFormat_tr").style.display = "none";
		document.getElementById(module+"FileSize_tr").style.display = "none";
	}
}
//选择上传视频参数
function selectUploadVideo(module,value){
	if(value == "true"){
	
		document.getElementById(module+"UploadVideoFormat_tr").style.display = "";
		document.getElementById(module+"UploadVideoSize_tr").style.display = "";
		
	}else{
		document.getElementById(module+"UploadVideoFormat_tr").style.display = "none";
		document.getElementById(module+"UploadVideoSize_tr").style.display = "none";
	}
}


//关闭站点提示
function setCloseSitePrompt(obj){
	if(obj.value == '1'){
		document.getElementById("td_closeSitePrompt").style.display = "none";
	}else if(obj.value == '2'){
		document.getElementById("td_closeSitePrompt").style.display = "none";
	}else if(obj.value == '3'){
		document.getElementById("td_closeSitePrompt").style.display = "";
	}
}

//敏感词过滤
function setAllowFilterWord(obj){
	if(obj.value == 'true'){
		document.getElementById("td_allowFilterWord").style.display = "";
	}else if(obj.value == 'false'){
		document.getElementById("td_allowFilterWord").style.display = "none";
	}
}

</script>

</HEAD>



<BODY>
<form:form modelAttribute="systemSetting" method="post" >
<DIV class="d-box">

<div class="item clearfix" id="tabs">
	<div class="itemTab active" onclick="put_css(1)">
    	<span>基本设置</span>
    </div>
    <div class="itemTab" onclick="put_css(2)">
    	<span>话题编辑器</span>
    </div>
    <div class="itemTab" onclick="put_css(3)">
    	<span>评论编辑器</span>
    </div>
    <div class="itemTab" onclick="put_css(4)">
    	<span>问题编辑器</span>
    </div>
    <div class="itemTab" onclick="put_css(5)">
    	<span>答案编辑器</span>
    </div>
</div>
<div class="clear"></div>
<div id="card">
    <div style="">
    	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
			<TBODY>
			<TR>
			    <TD class="t-label t-label-h" width="20%">站点名称：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="title" maxlength="200" size="60" value="${systemSetting.title}"/>
			    	<web:errors path="title" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">站点关键词(keywords)：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="keywords" maxlength="200" size="60" value="${systemSetting.keywords}"/>
			    	<web:errors path="keywords" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">站点描述(description)：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="description" maxlength="200" size="60" value="${systemSetting.description}"/>
			    	<web:errors path="description" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">关闭站点：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="closeSite" value="1" onclick="setCloseSitePrompt(this);"/>打开
			    	</label>
			    	<label>
			    		<form:radiobutton path="closeSite" value="2" onclick="setCloseSitePrompt(this);"/>只读模式
			    	</label>
			    	<label>
			    		<form:radiobutton path="closeSite" value="3" onclick="setCloseSitePrompt(this);"/>全站关闭
			    	</label>
			    	<web:errors path="closeSite" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="td_closeSitePrompt" <c:if test="${systemSetting.closeSite == 1 || systemSetting.closeSite == 2}"> style='display: none;'</c:if>>
			    <TD class="t-label t-label-h" width="20%">关闭站点提示信息：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<textarea class="form-textarea" name="closeSitePrompt" rows="8" cols="80">${systemSetting.closeSitePrompt}</textarea>
			    	<web:errors path="closeSitePrompt" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">支持访问设备：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="supportAccessDevice" value="1"/>自动识别终端
			    	</label>
			    	<label>
			    		<form:radiobutton path="supportAccessDevice" value="2"/>电脑端
			    	</label>
			    	<label>
			    		<form:radiobutton path="supportAccessDevice" value="3"/>移动端
			    	</label>
			    	<web:errors path="supportAccessDevice" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">允许注册账号类型：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:checkbox path="allowRegisterAccountObject.local" />本地账号密码用户</label>
			    	<label><form:checkbox path="allowRegisterAccountObject.mobile" />手机用户</label>
			    	<label><form:checkbox path="allowRegisterAccountObject.weChat" />微信用户</label>

			    	<web:errors path="allowRegisterAccount" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">注册是否需要验证码：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="registerCaptcha" value="true"/>需要
			    	</label>
			    	<label>
			    		<form:radiobutton path="registerCaptcha" value="false"/>不需要    	
			    	</label>
			    	<web:errors path="registerCaptcha" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">登录密码每分钟连续错误：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="login_submitQuantity" maxlength="10" size="10" value="${systemSetting.login_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	
			    	<web:errors path="login_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表话题每分钟提交超过：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="topic_submitQuantity" maxlength="10" size="10" value="${systemSetting.topic_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	<web:errors path="topic_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表评论每分钟提交超过：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="comment_submitQuantity" maxlength="10" size="10" value="${systemSetting.comment_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	<web:errors path="comment_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交问题每分钟提交超过：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="question_submitQuantity" maxlength="10" size="10" value="${systemSetting.question_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	<web:errors path="question_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交答案每分钟提交超过：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="answer_submitQuantity" maxlength="10" size="10" value="${systemSetting.answer_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	<web:errors path="answer_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表私信每分钟提交超过：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="privateMessage_submitQuantity" maxlength="10" size="10" value="${systemSetting.privateMessage_submitQuantity}"/>&nbsp;次以上出现验证码
			    	<span class="span-help">0为每次都出现验证码</span>
			    	<web:errors path="privateMessage_submitQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交问题最多可选择标签数量：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="maxQuestionTagQuantity" maxlength="10" size="10" value="${systemSetting.maxQuestionTagQuantity}"/>
			    	<web:errors path="maxQuestionTagQuantity" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表话题奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="topic_rewardPoint" maxlength="10" size="10" value="${systemSetting.topic_rewardPoint}"/>
			    	<web:errors path="topic_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表评论奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="comment_rewardPoint" maxlength="10" size="10" value="${systemSetting.comment_rewardPoint}"/>
			    	<web:errors path="comment_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发表回复奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="reply_rewardPoint" maxlength="10" size="10" value="${systemSetting.reply_rewardPoint}"/>
			    	<web:errors path="reply_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交问题奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="question_rewardPoint" maxlength="10" size="10" value="${systemSetting.question_rewardPoint}"/>
			    	<web:errors path="question_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交答案奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="answer_rewardPoint" maxlength="10" size="10" value="${systemSetting.answer_rewardPoint}"/>
			    	<web:errors path="answer_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">提交答案回复奖励积分：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="answerReply_rewardPoint" maxlength="10" size="10" value="${systemSetting.answerReply_rewardPoint}"/>
			    	<web:errors path="answerReply_rewardPoint" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台发表话题审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="topic_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="topic_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="topic_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="topic_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台发表评论审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="comment_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="comment_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="comment_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="comment_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台发表回复审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="reply_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="reply_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="reply_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="reply_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台提交问题审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="question_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="question_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="question_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="question_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台提交答案审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="answer_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="answer_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="answer_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="answer_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台提交答案回复审核：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="answerReply_review" value="10"/>全部审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="answerReply_review" value="30"/>特权会员免审核
			    	</label>
			    	<label>
			    		<form:radiobutton path="answerReply_review" value="50"/>无需审核
			    	</label>
			    	<web:errors path="answerReply_review" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">允许提交在线留言：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowFeedback" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowFeedback" value="false"/>否
			    	</label>
			    	<web:errors path="allowFeedback" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全局允许提交话题：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowTopic" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowTopic" value="false"/>否
			    	</label>
			    	<web:errors path="allowTopic" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全局允许提交评论：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowComment" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowComment" value="false"/>否
			    	</label>
			    	<web:errors path="allowComment" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全局允许提交问题：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowQuestion" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowQuestion" value="false"/>否
			    	</label>
			    	<web:errors path="allowQuestion" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全局允许提交答案：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowAnswer" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowAnswer" value="false"/>否
			    	</label>
			    	<web:errors path="allowAnswer" cssStyle="color: red;"/>
			    </TD>
			</TR>
			
			
			<TR>
			    <TD class="t-label t-label-h" width="20%">实名用户才允许提交话题：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="realNameUserAllowTopic" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="realNameUserAllowTopic" value="false"/>否
			    	</label>
			    	<web:errors path="realNameUserAllowTopic" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">实名用户才允许提交评论：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="realNameUserAllowComment" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="realNameUserAllowComment" value="false"/>否
			    	</label>
			    	<web:errors path="realNameUserAllowComment" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">实名用户才允许提交问题：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="realNameUserAllowQuestion" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="realNameUserAllowQuestion" value="false"/>否
			    	</label>
			    	<web:errors path="realNameUserAllowQuestion" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">实名用户才允许提交答案：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="realNameUserAllowAnswer" value="true"/>是
			    	</label>
			    	<label>
			    		<form:radiobutton path="realNameUserAllowAnswer" value="false"/>否
			    	</label>
			    	<web:errors path="realNameUserAllowAnswer" cssStyle="color: red;"/>
			    </TD>
			</TR>

			<TR>
			    <TD class="t-label t-label-h" width="20%">问题悬赏积分下限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="questionRewardPointMin" maxlength="15" size="15" value="${systemSetting.questionRewardPointMin}"/>
			    	<span class="span-help">0至999999999999999之间的整数</span>
			    	<web:errors path="questionRewardPointMin" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">问题悬赏积分上限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="questionRewardPointMax" maxlength="15" size="15" value="${systemSetting.questionRewardPointMax}"/>
			    	<span class="span-help">空为无限制 0则不允许悬赏积分</span>
			    	<web:errors path="questionRewardPointMax" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">问题悬赏金额下限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="questionRewardAmountMin" maxlength="12" size="12" value="${systemSetting.questionRewardAmountMin}"/>
			    	<span class="span-help">0至99999999之间的金额</span>
			    	<web:errors path="questionRewardAmountMin" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">问题悬赏金额上限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="questionRewardAmountMax" maxlength="12" size="12" value="${systemSetting.questionRewardAmountMax}"/>
			    	<span class="span-help">空为无限制 0则不允许悬赏金额</span>
			    	<web:errors path="questionRewardAmountMax" cssStyle="color: red;"/>
			    </TD>
			</TR>
			
			<TR>
			    <TD class="t-label t-label-h" width="20%">悬赏问答平台分成比例：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="questionRewardPlatformShareProportion" maxlength="3" size="3" value="${systemSetting.questionRewardPlatformShareProportion}"/>&nbsp;%
			    	<span class="span-help">0至100之间的整数</span>
			    	<web:errors path="questionRewardPlatformShareProportion" cssStyle="color: red;"/>
			    </TD>
			</TR>
			
			<TR>
			    <TD class="t-label t-label-h" width="20%">发红包总金额下限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="giveRedEnvelopeAmountMin" maxlength="12" size="12" value="${systemSetting.giveRedEnvelopeAmountMin}"/>
			    	<span class="span-help">0.01至99999999之间的金额</span>
			    	<web:errors path="giveRedEnvelopeAmountMin" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">发红包总金额上限：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="giveRedEnvelopeAmountMax" maxlength="12" size="12" value="${systemSetting.giveRedEnvelopeAmountMax}"/>
			    	<span class="span-help">空为无限制 0则不允许发红包</span>
			    	<web:errors path="giveRedEnvelopeAmountMax" cssStyle="color: red;"/>
			    </TD>
			</TR>
			
			
			<TR>
			    <TD class="t-label t-label-h" width="20%">敏感词过滤：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label>
			    		<form:radiobutton path="allowFilterWord" value="true" onclick="setAllowFilterWord(this);"/>打开
			    	</label>
			    	<label>
			    		<form:radiobutton path="allowFilterWord" value="false" onclick="setAllowFilterWord(this);"/>关闭
			    	</label>
			    	<web:errors path="allowFilterWord" cssStyle="color: red;"/>
			    	<span class="span-help">前台发表话题/评论/回复时过滤</span>
			    </TD>
			</TR>
			<TR id="td_allowFilterWord" <c:if test="${systemSetting.allowFilterWord == false}"> style='display: none;'</c:if>>
			    <TD class="t-label t-label-h" width="20%">敏感词替换为：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input type="text" class="form-text" name="filterWordReplace" value="${systemSetting.filterWordReplace}">
			    	<web:errors path="filterWordReplace" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文件防盗链密钥：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="fileSecureLinkSecret" maxlength="16" size="16" value="${systemSetting.fileSecureLinkSecret}"/>
			    	<web:errors path="fileSecureLinkSecret" cssStyle="color: red;"/>
			    	<span class="span-help">必须和Nginx的Secure Link模块密钥一致，并且为16个字符</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文件防盗链过期时间：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<input class="form-text" name="fileSecureLinkExpire" maxlength="10" size="10" value="${systemSetting.fileSecureLinkExpire}"/> 秒
			    	<web:errors path="fileSecureLinkExpire" cssStyle="color: red;"/>
			    </TD>
			</TR>
			
			<TR>
			    <TD class="t-label t-label-h" width="20%">前台分页数量：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="forestagePageNumber" maxlength="8" size="5" />
			    	<web:errors path="forestagePageNumber" cssStyle="color: red;"/>
			    	<span class="span-help">空为默认20条</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">后台分页数量：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="backstagePageNumber" maxlength="8" size="5" />
			    	<web:errors path="backstagePageNumber" cssStyle="color: red;"/>
			    	<span class="span-help">空为默认20条</span>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">上传临时文件有效期：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	文件上传&nbsp;<form:input class="form-text" path="temporaryFileValidPeriod" maxlength="8" size="5" />&nbsp;分钟内未提交表单由定时任务自动删除
			    	<web:errors path="temporaryFileValidPeriod" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">每用户每24小时内发送短信最大限制次数：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="userSentSmsCount" maxlength="8" size="5" />
			    	<web:errors path="userSentSmsCount" cssStyle="color: red;"/>
			    	<span class="span-help">空为无限制 &nbsp;短信发送最大数量受短信服务商限制</span>
			    </TD>
			</TR>
			
			</TBODY>
		</TABLE>
    </div>
    <!-- 话题编辑器 -->
    <div style="">
    <TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TBODY>
			<TR>
		    	<TD class="t-label t-label-h" width="20%">字体<span class="toolbar-icon-url icon-fontname"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.fontname" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.fontname" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字大小<span class="toolbar-icon-url icon-fontsize"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.fontsize" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.fontsize" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字颜色<span class="toolbar-icon-url icon-forecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.forecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.forecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字背景<span class="toolbar-icon-url icon-hilitecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hilitecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hilitecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">粗体<span class="toolbar-icon-url icon-bold"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.bold" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.bold" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">斜体<span class="toolbar-icon-url icon-italic"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.italic" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.italic" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">下划线<span class="toolbar-icon-url icon-underline"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.underline" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.underline" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">删除格式<span class="toolbar-icon-url icon-removeformat"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.removeformat" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.removeformat" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">超级链接<span class="toolbar-icon-url icon-link"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.link" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.link" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">取消超级链接<span class="toolbar-icon-url icon-unlink"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.unlink" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.unlink" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">左对齐<span class="toolbar-icon-url icon-justifyleft"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.justifyleft" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.justifyleft" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">居中<span class="toolbar-icon-url icon-justifycenter"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.justifycenter" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.justifycenter" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">右对齐<span class="toolbar-icon-url icon-justifyright"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.justifyright" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.justifyright" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">编号<span class="toolbar-icon-url icon-insertorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.insertorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.insertorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">项目符号<span class="toolbar-icon-url icon-insertunorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.insertunorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.insertunorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">代码<span class="toolbar-icon-url icon-code"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.code" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.code" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">插入表情<span class="toolbar-icon-url icon-emoticons"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.emoticons" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.emoticons" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">输入密码可见<span class="icon-hide" style="width: 16px;height: 16px;"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hidePassword" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hidePassword" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">评论话题可见<span class="icon-hide" style="width: 16px;height: 16px;"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hideComment" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hideComment" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">达到等级可见<span class="icon-hide" style="width: 16px;height: 16px;"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hideGrade" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hideGrade" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">积分购买可见<span class="icon-hide" style="width: 16px;height: 16px;"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hidePoint" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hidePoint" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">余额购买可见<span class="icon-hide" style="width: 16px;height: 16px;"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.hideAmount" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.hideAmount" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全屏显示<span class="toolbar-icon-url icon-fullscreen"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.fullscreen" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.fullscreen" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">图片<span class="toolbar-icon-url icon-image"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.image" value="true" onclick="selectImage('topic',this.value);"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.image" value="false" onclick="selectImage('topic',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="topicImageFormat_tr" <c:if test="${systemSetting.topicEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<c:forEach items="${imageUploadFormatList}" var="imageUploadFormat">
			    		<label><form:checkbox path="topicEditorTagObject.imageFormat" value="${imageUploadFormat}"/>${imageUploadFormat}</label>
			    	</c:forEach>
			    	<web:errors path="topicEditorTagObject.imageFormat" cssStyle="color: red;"/>
			    </TD> 
			</TR>
			<TR id="topicImageSize_tr" <c:if test="${systemSetting.topicEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="topicEditorTagObject.imageSize" size="10"/>&nbsp;K
			    	<web:errors path="topicEditorTagObject.imageSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文件<span class="toolbar-icon-url icon-insertfile"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.file" value="true" onclick="selectFile('topic',this.value);"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.file" value="false" onclick="selectFile('topic',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="topicFileFormat_tr" <c:if test="${systemSetting.topicEditorTagObject.file == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传文件格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	
			    	<c:forEach items="${fileUploadFormatList}" var="fileUploadFormat">
			    		<label><form:checkbox path="topicEditorTagObject.fileFormat" value="${fileUploadFormat}"/>${fileUploadFormat}</label>
			    	
			    	</c:forEach>
			    	<web:errors path="topicEditorTagObject.fileFormat" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="topicFileSize_tr" <c:if test="${systemSetting.topicEditorTagObject.file == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传文件大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="topicEditorTagObject.fileSize" size="10"/>&nbsp;K
			    	<web:errors path="topicEditorTagObject.fileSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">嵌入视频<span class="toolbar-icon-url icon-media"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.embedVideo" value="true"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.embedVideo" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">上传视频<span class="toolbar-icon-url icon-media"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="topicEditorTagObject.uploadVideo" value="true" onclick="selectUploadVideo('topic',this.value);"/>打开</label>
			    	<label><form:radiobutton path="topicEditorTagObject.uploadVideo" value="false" onclick="selectUploadVideo('topic',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="topicUploadVideoFormat_tr" <c:if test="${systemSetting.topicEditorTagObject.uploadVideo == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传视频格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	
			    	<c:forEach items="${videoUploadFormatList}" var="videoUploadFormat">
			    		<label><form:checkbox path="topicEditorTagObject.videoFormat" value="${videoUploadFormat}"/>${videoUploadFormat}</label>
			    	
			    	</c:forEach>
			    	<web:errors path="topicEditorTagObject.videoFormat" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="topicUploadVideoSize_tr" <c:if test="${systemSetting.topicEditorTagObject.uploadVideo == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传视频大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="topicEditorTagObject.videoSize" size="10"/>&nbsp;K
			    	<web:errors path="topicEditorTagObject.videoSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
		</TBODY>
	</TABLE>
	
	
    </div>
    <!-- 评论编辑器 -->
    <div style="">
    <TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TBODY>
			<TR>
		    	<TD class="t-label t-label-h" width="20%">字体<span class="toolbar-icon-url icon-fontname"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.fontname" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.fontname" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字大小<span class="toolbar-icon-url icon-fontsize"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.fontsize" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.fontsize" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字颜色<span class="toolbar-icon-url icon-forecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.forecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.forecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字背景<span class="toolbar-icon-url icon-hilitecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.hilitecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.hilitecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">粗体<span class="toolbar-icon-url icon-bold"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.bold" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.bold" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">斜体<span class="toolbar-icon-url icon-italic"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.italic" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.italic" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">下划线<span class="toolbar-icon-url icon-underline"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.underline" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.underline" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">删除格式<span class="toolbar-icon-url icon-removeformat"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.removeformat" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.removeformat" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">超级链接<span class="toolbar-icon-url icon-link"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.link" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.link" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">取消超级链接<span class="toolbar-icon-url icon-unlink"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.unlink" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.unlink" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">左对齐<span class="toolbar-icon-url icon-justifyleft"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.justifyleft" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.justifyleft" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">居中<span class="toolbar-icon-url icon-justifycenter"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.justifycenter" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.justifycenter" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">右对齐<span class="toolbar-icon-url icon-justifyright"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.justifyright" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.justifyright" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">编号<span class="toolbar-icon-url icon-insertorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.insertorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.insertorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">项目符号<span class="toolbar-icon-url icon-insertunorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.insertunorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.insertunorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">代码<span class="toolbar-icon-url icon-code"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.code" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.code" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">插入表情<span class="toolbar-icon-url icon-emoticons"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.emoticons" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.emoticons" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全屏显示<span class="toolbar-icon-url icon-fullscreen"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.fullscreen" value="true"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.fullscreen" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">图片<span class="toolbar-icon-url icon-image"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="editorTagObject.image" value="true" onclick="selectImage('comment',this.value);"/>打开</label>
			    	<label><form:radiobutton path="editorTagObject.image" value="false" onclick="selectImage('comment',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="commentImageFormat_tr" <c:if test="${systemSetting.editorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<c:forEach items="${imageUploadFormatList}" var="imageUploadFormat">
			    		<label><form:checkbox path="editorTagObject.imageFormat" value="${imageUploadFormat}"/>${imageUploadFormat}</label>
			    	</c:forEach>
			    	<web:errors path="editorTagObject.imageFormat" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="commentImageSize_tr" <c:if test="${systemSetting.editorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="editorTagObject.imageSize" size="10"/>&nbsp;K
			    	<web:errors path="editorTagObject.imageSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
		</TBODY>
	</TABLE>
	
	
    </div>
    <!-- 问题编辑器 -->
    <div style="">
    <TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TBODY>
			<TR>
		    	<TD class="t-label t-label-h" width="20%">字体<span class="toolbar-icon-url icon-fontname"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.fontname" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.fontname" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字大小<span class="toolbar-icon-url icon-fontsize"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.fontsize" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.fontsize" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字颜色<span class="toolbar-icon-url icon-forecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.forecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.forecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字背景<span class="toolbar-icon-url icon-hilitecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.hilitecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.hilitecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">粗体<span class="toolbar-icon-url icon-bold"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.bold" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.bold" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">斜体<span class="toolbar-icon-url icon-italic"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.italic" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.italic" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">下划线<span class="toolbar-icon-url icon-underline"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.underline" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.underline" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">删除格式<span class="toolbar-icon-url icon-removeformat"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.removeformat" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.removeformat" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">超级链接<span class="toolbar-icon-url icon-link"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.link" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.link" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">取消超级链接<span class="toolbar-icon-url icon-unlink"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.unlink" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.unlink" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">左对齐<span class="toolbar-icon-url icon-justifyleft"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.justifyleft" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.justifyleft" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">居中<span class="toolbar-icon-url icon-justifycenter"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.justifycenter" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.justifycenter" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">右对齐<span class="toolbar-icon-url icon-justifyright"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.justifyright" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.justifyright" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">编号<span class="toolbar-icon-url icon-insertorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.insertorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.insertorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">代码<span class="toolbar-icon-url icon-code"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.code" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.code" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">项目符号<span class="toolbar-icon-url icon-insertunorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.insertunorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.insertunorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">插入表情<span class="toolbar-icon-url icon-emoticons"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.emoticons" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.emoticons" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全屏显示<span class="toolbar-icon-url icon-fullscreen"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.fullscreen" value="true"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.fullscreen" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">图片<span class="toolbar-icon-url icon-image"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="questionEditorTagObject.image" value="true" onclick="selectImage('question',this.value);"/>打开</label>
			    	<label><form:radiobutton path="questionEditorTagObject.image" value="false" onclick="selectImage('question',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="questionImageFormat_tr" <c:if test="${systemSetting.questionEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<c:forEach items="${imageUploadFormatList}" var="imageUploadFormat">
			    		<label><form:checkbox path="questionEditorTagObject.imageFormat" value="${imageUploadFormat}"/>${imageUploadFormat}</label>
			    	</c:forEach>
			    	<web:errors path="questionEditorTagObject.imageFormat" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="questionImageSize_tr" <c:if test="${systemSetting.questionEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="questionEditorTagObject.imageSize" size="10"/>&nbsp;K
			    	<web:errors path="questionEditorTagObject.imageSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="questionFileSize_tr" <c:if test="${systemSetting.questionEditorTagObject.file == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传文件大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="questionEditorTagObject.fileSize" size="10"/>&nbsp;K
			    	<web:errors path="questionEditorTagObject.fileSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
		</TBODY>
	</TABLE>
    </div>
    
    <!-- 答案编辑器 -->
    <div style="">
    <TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TBODY>
			<TR>
		    	<TD class="t-label t-label-h" width="20%">字体<span class="toolbar-icon-url icon-fontname"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.fontname" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.fontname" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字大小<span class="toolbar-icon-url icon-fontsize"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.fontsize" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.fontsize" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字颜色<span class="toolbar-icon-url icon-forecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.forecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.forecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">文字背景<span class="toolbar-icon-url icon-hilitecolor"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.hilitecolor" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.hilitecolor" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">粗体<span class="toolbar-icon-url icon-bold"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.bold" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.bold" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">斜体<span class="toolbar-icon-url icon-italic"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.italic" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.italic" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">下划线<span class="toolbar-icon-url icon-underline"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.underline" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.underline" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">删除格式<span class="toolbar-icon-url icon-removeformat"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.removeformat" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.removeformat" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">超级链接<span class="toolbar-icon-url icon-link"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.link" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.link" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">取消超级链接<span class="toolbar-icon-url icon-unlink"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.unlink" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.unlink" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">左对齐<span class="toolbar-icon-url icon-justifyleft"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.justifyleft" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.justifyleft" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">居中<span class="toolbar-icon-url icon-justifycenter"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.justifycenter" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.justifycenter" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">右对齐<span class="toolbar-icon-url icon-justifyright"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.justifyright" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.justifyright" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">编号<span class="toolbar-icon-url icon-insertorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.insertorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.insertorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">项目符号<span class="toolbar-icon-url icon-insertunorderedlist"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.insertunorderedlist" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.insertunorderedlist" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">代码<span class="toolbar-icon-url icon-code"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.code" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.code" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">插入表情<span class="toolbar-icon-url icon-emoticons"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.emoticons" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.emoticons" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">全屏显示<span class="toolbar-icon-url icon-fullscreen"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.fullscreen" value="true"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.fullscreen" value="false"/>关闭</label>
			    </TD>
			</TR>
			<TR>
			    <TD class="t-label t-label-h" width="20%">图片<span class="toolbar-icon-url icon-image"></span>：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<label><form:radiobutton path="answerEditorTagObject.image" value="true" onclick="selectImage('answer',this.value);"/>打开</label>
			    	<label><form:radiobutton path="answerEditorTagObject.image" value="false" onclick="selectImage('answer',this.value);"/>关闭</label>
			    </TD>
			</TR>
			<TR id="answerImageFormat_tr" <c:if test="${systemSetting.answerEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片格式：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<c:forEach items="${imageUploadFormatList}" var="imageUploadFormat">
			    		<label><form:checkbox path="answerEditorTagObject.imageFormat" value="${imageUploadFormat}"/>${imageUploadFormat}</label>
			    	</c:forEach>
			    	<web:errors path="answerEditorTagObject.imageFormat" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="answerImageSize_tr" <c:if test="${systemSetting.answerEditorTagObject.image == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传图片大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="answerEditorTagObject.imageSize" size="10"/>&nbsp;K
			    	<web:errors path="answerEditorTagObject.imageSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
			<TR id="answerFileSize_tr" <c:if test="${systemSetting.answerEditorTagObject.file == false}"> style="display: none;"</c:if>>
			    <TD class="t-label t-label-h" width="20%">允许上传文件大小：</TD>
			    <TD class="t-content" width="80%" colSpan="3">
			    	<form:input class="form-text" path="answerEditorTagObject.fileSize" size="10"/>&nbsp;K
			    	<web:errors path="answerEditorTagObject.fileSize" cssStyle="color: red;"/>
			    </TD>
			</TR>
		</TBODY>
	</TABLE>
    </div>
    
 </div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
			<TBODY> 
				<TR>
				    <TD class="t-button" colSpan="4">
				    	<span class="submitButton"><INPUT type="submit" value="提交"></span>
				  	</TD>
				</TR>
			</TBODY>
		</TABLE>
</DIV>

</form:form>

<script type="text/javascript">
var tab = document.getElementById('tabs').getElementsByTagName('div');
var cards = document.getElementById('card').getElementsByTagName('div');
function put_css(id){
	for(var k=0;k<tab.length;k++){
		if(id-1 == k){
			tab[k].className = 'itemTab active';
			cards[k].style.display = 'block';
		}else{
			tab[k].className = 'itemTab';
			cards[k].style.display = 'none';
		}
	}
}
put_css(1);


//锚点跳转
function anchorJump(id){
	location.hash="";
	location.hash=id;   
}
</script>
</BODY></HTML>
