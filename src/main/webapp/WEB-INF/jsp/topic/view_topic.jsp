<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>查看话题</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>

<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script type="text/javascript" src="backstage/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<link rel="stylesheet" href="backstage/syntaxhighlighter/styles/shCoreDefault.css"  type="text/css" />
<script language="JavaScript" src="backstage/syntaxhighlighter/scripts/shCore.js" type="text/javascript"></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushXml.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushJScript.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushCss.js" type="text/javascript"></script>
<script language="javascript" src="backstage/syntaxhighlighter/scripts/shBrushJava.js" type="text/javascript"></script>

<script type="text/javascript" >
//审核话题
function auditTopic(topicId){
	var parameter = "&topicId="+topicId;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("审核失败");
		}
	},
		"${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=auditTopic&timestamp=" + new Date().getTime(), true,parameter);
		
	
}

//审核评论
function auditComment(commentId){
	var parameter = "&commentId="+commentId;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("审核失败");
		}
	},
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=auditComment&timestamp=" + new Date().getTime(), true,parameter);
		
	
}
//审核回复
function auditReply(replyId){
	var parameter = "&replyId="+replyId;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	post_request(function(value){
		if(value == "1"){
			window.location.reload();
		}else{
			alert("审核失败");
		}
	},
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=auditReply&timestamp=" + new Date().getTime(), true,parameter);
		
	
}


//显示修改话题页
function showUpdateTopic(topicId){
	var url ="${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=edit&topicId="+topicId+"&timestamp=" + new Date().getTime();
	window.parent.loadTopic("修改话题",url);

}
//删除话题
function deleteTopic(topicId){	
	if(window.confirm('确定删除吗?')){
		var parameter = "";
		
		parameter += "&topicId="+topicId;
		var csrf =  getCsrf();
		parameter += "&_csrf_token="+csrf.token;
		parameter += "&_csrf_header="+csrf.header;
	   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
		if(parameter.indexOf("&") == 0){
			parameter = parameter.substring(1,parameter.length);
		}
	   	post_request(function(value){
			if(value == "1"){
				window.location.href='${config:url(pageContext.request)}control/topic/list${config:suffix()}?visible=${param.visible}&page=${param.page}';
			}else{
				alert("删除失败");
			}
		},
			"${config:url(pageContext.request)}control/topic/manage${config:suffix()}?method=delete&visible=${param.visible}&timestamp=" + new Date().getTime(), true,parameter);
			
	}else{return false;};
}


function sureSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "";
	//话题Id
	var topicId = getUrlParam("topicId");
	if(topicId != null){
		parameter += "&topicId="+topicId;
	}
	//内容
	var content = document.getElementById("content").value;
	if(content != ""){
		parameter += "&content="+encodeURIComponent(content);
	}
	
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}

	//清空错误提示
	var error_span_object = getElementsByName_pseudo("span", "error");
	for(var i = 0;i < error_span_object.length; i++) {
		error_span_object[i].innerHTML="";
	
	}
	
	post_request(function(value){
		if(value != ""){
			var returnValue = JSON.parse(value);//JSON转为对象
			for(var key in returnValue){
				if(key == "success"){
					if(returnValue[key] == "true"){
						systemMsgShow("提交成功,3秒后自动刷新");//弹出提示层
        				setTimeout("window.location.reload();",3000);//延迟3秒后刷新当前页面
						return;
					}			
				}else if(key == "error"){
					var errorValue = returnValue[key];
					for(var error in errorValue){
						document.getElementById(error+"_error").innerHTML=errorValue[error];		
					}
				}
			}
		}	
		//按钮设置 disabled="disabled"
		document.getElementById("submitForm").disabled=true;
	},
		"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=add&timestamp=" + new Date().getTime(), true,parameter);
} 

//显示修改评论页
function showUpdateComment(commentId){
	var url ="${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=edit&commentId="+commentId+"&timestamp=" + new Date().getTime();
	window.parent.loadTopic("修改评论",url);

}
//删除评论页
function deleteComment(commentId){
	if(window.confirm('确定删除吗?')){
		var parameter = "&commentId="+commentId;
		var csrf =  getCsrf();
		parameter += "&_csrf_token="+csrf.token;
		parameter += "&_csrf_header="+csrf.header;
		//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
		if(parameter.indexOf("&") == 0){
			parameter = parameter.substring(1,parameter.length);
		}
		
		post_request(function(value){
			if(value == "1"){
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
			"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=delete&timestamp=" + new Date().getTime(), true,parameter);
		
	}else{return false;};
}

//显示引用添加页
function showQuote(commentId){
	var url ="${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=addQuote&commentId="+commentId+"&timestamp=" + new Date().getTime();
	window.parent.loadTopic("引用",url);

}


//显示添加回复评论页
function showAddReply(commentId){
	var url ="${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=addReply&commentId="+commentId+"&timestamp=" + new Date().getTime();
	window.parent.loadTopic("回复",url);
}




//显示修改回复评论页
function showUpdateReply(replyId){
	var url ="${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=editReply&replyId="+replyId+"&timestamp=" + new Date().getTime();
	window.parent.loadTopic("修改回复",url);
}
//删除回复评论页
function deleteReply(replyId){
	if(window.confirm('确定删除吗?')){
		var parameter = "&replyId="+replyId;
		var csrf =  getCsrf();
		parameter += "&_csrf_token="+csrf.token;
		parameter += "&_csrf_header="+csrf.header;
		//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
		if(parameter.indexOf("&") == 0){
			parameter = parameter.substring(1,parameter.length);
		}
		post_request(function(value){
			if(value == "1"){
				window.location.reload();
			}else{
				alert("删除失败");
			}
		},
			"${config:url(pageContext.request)}control/comment/manage${config:suffix()}?method=deleteReply&timestamp=" + new Date().getTime(), true,parameter);
		
	}else{return false;};
	
}

//滚动到描点(当上级跳转来后台'全部待审核评论' '全部待审核回复'时)
$(function() {
	var commentId = getUrlParam("commentId");//URL中的评论Id
	if(commentId != null && commentId != ""){
		var anchor = $("#anchor_" + commentId); //获得锚点   
		
	    if (anchor.length > 0) {//判断对象是否存在   
	        var pos = anchor.offset().top;  
	      //  var poshigh = anchor.height();  
	        $("html,body").animate({ scrollTop: pos }, 500);  
	    }
	}
     
 });  
</script>

</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<enhance:out escapeXml="false">
<input type="hidden" id="availableTag" value="<c:out value="${availableTag}"></c:out>">
</enhance:out>


<DIV class="d-box">
<div class="d-button">
	<c:if test="${param.origin == null || param.origin == '' || param.origin ==1}">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/topic/list${config:suffix()}?visible=${param.visible}&page=${param.topicPage}'" value="返回">
	</c:if>
	<c:if test="${param.origin == 2 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/topic/allAuditTopic${config:suffix()}?page=${param.topicPage}'" value="返回">
	</c:if>
	<c:if test="${param.origin == 3 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/topic/allAuditComment${config:suffix()}?page=${param.topicPage}'" value="返回">
	</c:if>
	<c:if test="${param.origin == 4 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/topic/allAuditReply${config:suffix()}?page=${param.topicPage}'" value="返回">
	</c:if>
	<!-- 来自用户发表的话题 -->
	<c:if test="${param.origin == 10 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allTopic&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.topicPage}'" value="返回">
	</c:if>
	<!-- 来自用户发表的评论 -->
	<c:if test="${param.origin == 20 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allComment&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.commentPage}'" value="返回">
	</c:if>
	<!-- 来自用户发表的回复 -->
	<c:if test="${param.origin == 30 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allReply&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.replyPage}'" value="返回">
	</c:if>
	<!-- 来自用户的收藏夹 -->
	<c:if test="${param.origin == 40 }">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/favorite/list${config:suffix()}?userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.favoritePage}'" value="返回">
	</c:if>
</div>

<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0" style="table-layout:fixed;">
	<THEAD class="t-list-thead"><!-- 表格使用table-layout:fixed;会让表的布局宽度以第一行为准 -->
		<TR style="height:0px;padding:0"> 
			<TH style="width:20%;height:0px;"></TH> 
			<TH style="width:80%;height:0px;"></TH>
		</TR> 
		<TR>
			<TH colspan="2">${topic.title}</TH>
		</TR>
	</THEAD>
	<TBODY class="t-list-tbody" align="center">
		<TR class="noDiscolor">
		  	<TD>
		  		${topic.userName}
		  		<c:if test="${topic.isStaff == true}"><span style="color: green;">[员工]</span></c:if>
		  	</TD>
		    <TD>
		    	 <TABLE  cellSpacing="2" cellPadding="0" width="99%"  border="0">
					<TR class="noDiscolor">
						<TD width="40%" style="BORDER-BOTTOM: #bfe3ff 1px dotted;" align="left"><fmt:formatDate value="${topic.postTime}" pattern="yyyy-MM-dd HH:mm:ss" /></TD>
						<TD width="60%" style="BORDER-BOTTOM: #bfe3ff 1px dotted" align="right">${topic.ip}&nbsp;${topic.ipAddress}</TD>
					</TR>	
				</TABLE>
				<enhance:out escapeXml="false">
					<div class="comment">${topic.content}</div>
				</enhance:out>
				<TABLE  cellSpacing="2" cellPadding="0" width="99%"  border="0">
					<TR class="noDiscolor">
						<TD width="50%" style="border-top: #bfe3ff 1px dotted;" align="left">
							查看总数：${topic.viewTotal}&nbsp;&nbsp;&nbsp;&nbsp;评论总数：${topic.commentTotal}
						</TD>
						<TD width="50%" style="border-top: #bfe3ff 1px dotted;" align="right">
							<c:if test="${topic.status == 10}">
								<A onclick="javascript:if(window.confirm('确定审核通过吗? ')){auditTopic('${topic.id}');return false;}else{return false};" hidefocus="true" href="#" ondragstart= "return false">立即审核</A>&nbsp;&nbsp;
							</c:if>
							<A hidefocus="true"  href="control/topicLike/list${config:suffix()}?topicId=${topic.id}&visible=${param.visible}&topicPage=${param.topicPage}&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&commentPage=${param.commentPage}&replyPage=${param.replyPage}" ondragstart= "return false">点赞用户</A>&nbsp;&nbsp;
							<A hidefocus="true"  href="control/topicFavorite/list${config:suffix()}?topicId=${topic.id}&visible=${param.visible}&topicPage=${param.topicPage}&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&commentPage=${param.commentPage}&replyPage=${param.replyPage}" ondragstart= "return false">收藏用户</A>&nbsp;&nbsp;
							<A hidefocus="true"  href="control/topic/topicUnhideList${config:suffix()}?topicId=${topic.id}&visible=${param.visible}&topicPage=${param.topicPage}&userName=${param.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&commentPage=${param.commentPage}&replyPage=${param.replyPage}" ondragstart= "return false">取消隐藏用户</A>&nbsp;&nbsp;
							<A hidefocus="true" onClick="showUpdateTopic('${topic.id}'); return false" href="#" ondragstart= "return false">修改</A>&nbsp;&nbsp;
							<A hidefocus="true" onClick="deleteTopic('${topic.id}'); return false;" href="#" ondragstart= "return false">删除</A>
						</TD>
					</TR>	
				</TABLE>	
		    </TD>
		</TR>
	</TBODY>
</TABLE>



<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${pageView.records}" var="entry" varStatus="status">
	  <TR id="anchor_${entry.id}" class="noDiscolor">
	  	<TD width="20%">
	  		${entry.userName}
	  		<c:if test="${entry.isStaff == true}"><span style="color: green;">[员工]</span></c:if>
	  	</TD>
	    <TD width="80%">
	    	 <TABLE  cellSpacing="2" cellPadding="0" width="99%"  border="0">
				<TR class="noDiscolor">
					<TD width="40%" style="BORDER-BOTTOM: #bfe3ff 1px dotted;" align="left"><fmt:formatDate value="${entry.postTime}" pattern="yyyy-MM-dd HH:mm:ss" /></TD>
					<TD width="60%" style="BORDER-BOTTOM: #bfe3ff 1px dotted" align="right">${entry.ip}&nbsp;${entry.ipAddress}&nbsp;&nbsp;&nbsp;&nbsp;${pageView.firstResult+status.count}楼 </TD>
				</TR>	
			</TABLE>
			<enhance:out escapeXml="false">
			<div class="quote">
			
			<c:set value="" var="content"></c:set>
			<c:forEach items="${entry.quoteList}" var="quote">
				<c:set value="<div>${content}<span>${quote.userName}&nbsp;的评论：</span><br />${quote.content}</div>" var="content"></c:set>				
			</c:forEach>
			${content}
			<!-- 
				<div>
					<div>
						<div><span>广州网友 的评论：</span><br />
							广州人民发来贺电
						</div>
						<span>四川网友 的评论：</span><br />
						四川人民发来贺电！
					</div>
					<span>陕西西安网友 的评论：</span><br />
					陕西网友发来贺电
				</div>
			 -->
			 
			</div>
			<div class="comment">${entry.content}</div>
			</enhance:out>
			<div>
				<span style="color: black;"><strong style="color: #666">--- 共有 ${entry.totalReply} 条回复 --- </strong></span>
				<TABLE  cellSpacing="2" cellPadding="0" width="99%"  border="0">
					<c:forEach items="${entry.replyList}" var="reply">
						<TR class="noDiscolor">
							<TD width="15%" align="right">
								${reply.userName}&nbsp;&nbsp;
							</TD>
							<TD width="15%" align="center">
								<fmt:formatDate value="${reply.postTime}" pattern="yyyy-MM-dd HH:mm:ss" />
							</TD>
							
							<TD width="50%" align="left">
								<span style="color: orange;">${reply.content}</span>
							</TD>
							<TD width="20%" style="" align="right">
								
								<c:if test="${reply.status == 10}">
									<A onclick="javascript:if(window.confirm('确定发布吗? ')){auditReply('${reply.id}');return false;}else{return false};" hidefocus="true" href="#" ondragstart= "return false">立即审核</A>&nbsp;&nbsp;
								</c:if>
								<A hidefocus="true" onClick="showUpdateReply('${reply.id}',${pageView.currentpage}); return false" href="#" ondragstart= "return false">修改</A>&nbsp;&nbsp;
								<A hidefocus="true" onClick="deleteReply('${reply.id}'); return false" href="#" ondragstart= "return false">删除</A>
							</TD>
						</TR>
					</c:forEach>
				</TABLE>
			</div>
			<TABLE  cellSpacing="2" cellPadding="0" width="99%"  border="0">
				<TR class="noDiscolor">
					<TD width="50%" style="border-top: #bfe3ff 1px dotted;" align="left">
						<A hidefocus="true" onClick="showAddReply('${entry.id}',${pageView.currentpage}); return false" href="#" ondragstart= "return false">回复</A>&nbsp;&nbsp;
						<A hidefocus="true" onClick="showQuote('${entry.id}'); return false" href="#" ondragstart= "return false">引用</A>
					</TD>
					<TD width="50%" style="border-top: #bfe3ff 1px dotted;" align="right">
						<c:if test="${entry.status == 10}">
							<A onclick="javascript:if(window.confirm('确定发布吗? ')){auditComment('${entry.id}');return false;}else{return false};" hidefocus="true" href="#" ondragstart= "return false">立即审核</A>&nbsp;&nbsp;
						</c:if>
						<A hidefocus="true" onClick="showUpdateComment('${entry.id}'); return false" href="#" ondragstart= "return false">修改</A>&nbsp;&nbsp;
						<A hidefocus="true" onClick="deleteComment('${entry.id}'); return false;" href="#" ondragstart= "return false">删除</A>
					</TD>
				</TR>	
			</TABLE>	
	    </TD>
	  </TR>
	 </c:forEach>
  </TBODY>
</TABLE>
<!-- 分页栏开始 -->
  	<%@ include file="/WEB-INF/jsp/common/page.jsp" %>
<!-- 分页栏结束 -->




<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0" style="margin-top: 34px;">
  <TBODY>

  <TR>
    <TD class="t-content" align="center">
    	<textarea id="content" name="content" style="width:99%;height:300px;visibility:hidden;"></textarea>
	  	<SPAN id="topicId_error" class="span-text" ></SPAN><br>
	  	<SPAN id="content_error" class="span-text" ></SPAN>
    </TD>
   </TR>
    <TR>
    <TD class="t-content" align="center" >
    	<span class="submitButton" ><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit()"></span>
    </TD>
   </TR>
</TBODY></TABLE>






</DIV>
</form:form>
</BODY>

<script>
	KindEditor.options.cssData = 'body { font-size: 14px; }';
	var availableTag = ['source', '|'];
	var editor;
	KindEditor.ready(function(K) {
		var availableTag = document.getElementById("availableTag").value;
		var availableTag_obj = null;//['source', '|','fontname','fontsize','emoticons'];
		if(availableTag != ""){
			var availableTag_obj = JSON.parse(availableTag);//JSON转为对象
		}
		
		
		
		var topicId = "${param.topicId}";
		
	
		editor = K.create('textarea[name="content"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
			themeType : 'style :darkGray',//深灰主题 加冒号的是主题样式文件名称同时也是主题目录
		//	autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			uploadJson :'${config:url(pageContext.request)}control/comment/manage.htm?method=uploadImage&topicId='+topicId+"&userName=${userName}&isStaff=true&${_csrf.parameterName}=${_csrf.token}",//指定浏览远程图片的服务器端程序
			items : availableTag_obj,
			
			afterChange : function() {
				this.sync();
			}
			
			/**
			items : [
				'source', '|',
				'fontname', 'fontsize', '|', 
				'forecolor', 'hilitecolor', 'bold', 'italic', 'underline','removeformat','link','unlink','|', 
				'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist','insertunorderedlist', '|', 
				'emoticons', 'image','flash']**/
		});
	});
	
	
	
</script>



<script type="text/javascript">
	$(".lang-xml").each(function(index, element) {
		$(this).attr("class","brush: xml");
	});
	$(".lang-css").each(function(index, element) {
		$(this).attr("class","brush: css");
	});
	$(".lang-html").each(function(index, element) {
		$(this).attr("class","brush: xml");
	});
	$(".lang-js").each(function(index, element) {
		$(this).attr("class","brush: js");
	});
	$(".lang-java").each(function(index, element) {
		$(this).attr("class","brush: java");
	});
	SyntaxHighlighter.all();
	SyntaxHighlighter.defaults['toolbar'] = false;//去掉右上角问号图标
	
</script>






</HTML>