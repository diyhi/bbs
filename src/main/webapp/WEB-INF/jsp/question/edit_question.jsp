<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}"></base>
<TITLE>修改问题</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link href="backstage/kindeditor/themes/default/default.css" rel="stylesheet"/>
<link href="backstage/kindeditor/plugins/hide/hide.css" rel="stylesheet"/>
<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>
<script charset="utf-8" src="backstage/kindeditor/lang/zh-CN.js"></script>

<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />


<script language="javascript" type="text/javascript">
//弹出查看标签层
function showTagDiv(){	
	get_request(function(value){
		if(value != ""){
			var nav_html = "";
			var childTag_html = "";

			var returnValue = JSON.parse(value);//JSON转为对象
			for(var i =0; i<returnValue.length; i++){
				var questionTag = returnValue[i];

				nav_html += "<li class='nav-item'>";
				nav_html += 	"<a class='nav-link' id='tag-"+i+"' childTagQuantity='"+questionTag.childTag.length+"' tagId='"+questionTag.id+"' tagName = '"+questionTag.name+"' href='javascript:;'>"+questionTag.name+"</a>";
				nav_html += "</li>";
				
				childTag_html += "<div class='tab-pane' id='tag-"+i+"-panel' >";
				for(var j =0; j<questionTag.childTag.length; j++){
					var childQuestionTag = questionTag.childTag[j];
					childTag_html += "<a class='child-tag' href='javascript:;' tagId='"+childQuestionTag.id+"' tagName = '"+childQuestionTag.name+"'>"+childQuestionTag.name+"</a>";
				}
				childTag_html += "</div>";
			}
			
			var html = "";
			html += "<div class='questionTagNavigation'>";
			html += 	"<ul class='nav'>";
			html += 		nav_html;
			html += 	"</ul>";
			html += 	"<div class='tab-content'>";
			html += 		childTag_html;
			html += 	"</div>";
			html += "</div>";

			layer.open({
				type: 4,//4: tips层
				content: [html, '#add-questionTag-btn'], //数组第二项即吸附元素选择器或者DOM
			//	closeBtn:0,//0不显示关闭按钮
	    	 	area: '500px',//宽，高度自适应
				tips: [3, '#f6f6f6'],
				success: function(layero, index){//层弹出后的成功回调方法
					//选择标签
					selectTag(index);

					//点击空白关闭弹窗
					$(document).mouseup(function(e){
						var _con = $(layero.selector);   // 设置目标区域
						if(!_con.is(e.target) && _con.has(e.target).length === 0){
					    	//关闭窗口
					    	layer.close(index);
						}
					});
				}
			
			});
		}else{
			layer.msg('还没有问题标签', 
				{
				  time: 3000 //3秒关闭（如果不配置，默认是3秒）
				},function(){
					//关闭后的操作
					
				}
			);
		}
	},
		 "${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=allTag&timestamp=" + new Date().getTime(), true);
}

//最后一次选中的根节点
var lastSelectedRootNode = "";
//选择标签
function selectTag(index){
	
	if(lastSelectedRootNode == ""){
		//第一次打开选第一项
		$(".nav-link:first").addClass("active");
		$(".tab-pane:first").addClass("active");
	}else{//选中上一次打开的节点
		$(".nav-link").removeClass("active"); 
		$(".tab-pane").removeClass("active"); 
		$("#"+lastSelectedRootNode).addClass("active");
		var panelId = lastSelectedRootNode + "-panel";
	 	$("#"+panelId).addClass("active");
	}

	

	//显示选中的标签
	$(".nav-link").on('click',function(e){
		e.preventDefault();
		$(".nav-link").removeClass("active"); 
		$(".tab-pane").removeClass("active"); 
	 	var panelId = $(this).attr("id") + "-panel";
	 	$(this).addClass("active");
	 	$("#"+panelId).addClass("active");
	 	
	 	lastSelectedRootNode = $(this).attr("id");//设置最后一次选中的根节点
	 	
	 	//子标签数量
	 	var childTagQuantity = $(this).attr("childTagQuantity");
	 	if(parseInt(childTagQuantity) ==0){//如果没有子标签则选择当前标签
	 		var tagId = $(this).attr("tagId");
			var tagName = $(this).attr("tagName");
			
			var html = "";
				html += "<a class='btn mr' data-tagId='"+tagId+"' href='javascript:;'>";
				html += 	tagName;
				html += 	"<span class='ml'>×</span>";
				html += 	"<input type='hidden' name='tagId' value='"+tagId+"'>";
				html += "</a>";
				
			//如果标签已选中	
			var tag = $(".questionTag a[data-tagId='"+tagId+"']");
			
			if(tag.length > 0){//已选中	
				//重复选中时显示闪烁动画
				tag.addClass("warning");//显示动画
				setTimeout(function() {
					tag.removeClass("warning");//删除动画
				}, 2000);
				
				
			}else{//未选中	
				$(".addTag").before(html);
				//动态添加元素时设置删除标签事件
				$(".questionTag a[data-tagId='"+tagId+"']").find("span").on('click',function(e){
					e.preventDefault();
					$(this).parent().remove();
				});
				
				layer.close(index); //关闭tips层 
			}
	 	}
	});
	
	//选择标签
	$(".child-tag").on('click',function(e){
		e.preventDefault();
		
		
		var tagId = $(this).attr("tagId");
		var tagName = $(this).attr("tagName");
		
		var html = "";
			html += "<a class='btn mr' data-tagId='"+tagId+"' href='javascript:;'>";
			html += 	tagName;
			html += 	"<span class='ml'>×</span>";
			html += 	"<input type='hidden' name='tagId' value='"+tagId+"'>";
			html += "</a>";
			
		//如果标签已选中	
		var tag = $(".questionTag a[data-tagId='"+tagId+"']");
		
		if(tag.length > 0){//已选中	
			//重复选中时显示闪烁动画
			tag.addClass("warning");//显示动画
			setTimeout(function() {
				tag.removeClass("warning");//删除动画
			}, 2000);
			
			
		}else{//未选中	
			$(".addTag").before(html);
			//动态添加元素时设置删除标签事件
			$(".questionTag a[data-tagId='"+tagId+"']").find("span").on('click',function(e){
				e.preventDefault();
				$(this).parent().remove();
			});
			
			layer.close(index); //关闭tips层 
		}

	});
}






$(document).ready(function(){
	//回显页面时设置删除标签事件
	$('.questionTag span').click(function(){
		//var tagId = $(this).parent().attr("data-tag-id");
		$(this).parent().remove();
	});
});

//提交表单
function sureSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "";
	//问题Id
	var questionId = getUrlParam("questionId");
	if(questionId != null){
		parameter += "&questionId="+questionId;
	}
	//标题
	var title = document.getElementById("title").value;
	if(title != ""){
		parameter += "&title="+encodeURIComponent(title);
	}
	
	
	//内容
	var content = document.getElementById("content").value;
	if(content != ""){
		parameter += "&content="+encodeURIComponent(content);
	}
	//标签Id
	var tagId = document.getElementsByName("tagId");  
	if(tagId.length >0){
		for(var i=0; i<tagId.length; i++) {  
	        parameter += "&tagId="+tagId[i].value;
	    } 
	}else{
		document.getElementById("submitForm").innerHTML = "请选择标签";
	}
    

	//排序
	var sort = document.getElementById("sort").value;
	if(sort != ""){
		parameter += "&sort="+encodeURIComponent(sort);
	}
	
	//允许评论
	var allow = document.getElementsByName("allow");  
    for(var i=0; i<allow.length; i++) {  
        if (allow[i].checked) { 
        	parameter += "&allow="+allow[i].value;
        }  
    }  
	//状态
	var status = document.getElementsByName("status");  
	if(status != null ){
		for(var i=0; i<status.length; i++) {  
	        if (status[i].checked) { 
	        	parameter += "&status="+status[i].value;
	        }  
	    }  
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
        				setTimeout("window.parent.callbackQuestion();",3000);//延迟3秒后刷新当前页面
					}			
				}else if(key == "error"){
					var errorValue = returnValue[key];
					for(var error in errorValue){
						document.getElementById(error+"_error").innerHTML=errorValue[error];		
					}
					//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;	
				}
			}
		}
		
	},
		"${config:url(pageContext.request)}control/question/manage${config:suffix()}?method=editQuestion&timestamp=" + new Date().getTime(), true,parameter);



} 
</script>



</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form>
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
  	<TD class="t-label t-label-h" width="12%">标题：</TD>
    <TD class="t-content" width="88%">
    	<input type="text" class="form-text" id="title" size="50" value="${question.title}">
		<SPAN class="span-text" id="title_error" name="error">${error["title"]}</SPAN>
    </TD>
   </TR>
  	<TR>
	  	<TD class="t-label t-label-h" width="12%">标签：</TD>
	    <TD class="t-content" width="88%">
	    	<div class="questionTag">
	    		
	    		<c:forEach items="${question.questionTagAssociationList}" var="questionTagAssociation">
	    			<a class="btn mr" data-tagId="${questionTagAssociation.questionTagId}" href="javascript:;">${questionTagAssociation.questionTagName}<span class="ml">×</span><input type="hidden" name="tagId" value="${questionTagAssociation.questionTagId}"></a>
	    		</c:forEach>
	    	
				<div class="addTag">
					<button type="button" class="btn" id="add-questionTag-btn" onClick="javaScript:showTagDiv();">+ 添加标签</button>
				</div>
			</div>
			<span class="span-text" id="tagId_error" name="error">${error["tagId"]}</span>
	    </TD>
	</TR>
	<TR>
  	<TD class="t-label t-label-h" width="12%">排序：</TD>
    <TD class="t-content" width="88%">
    	<input type="text" class="form-text" id="sort" size="8" maxlength="8" value="${question.sort}" >
		&nbsp;<SPAN class="span-text" id="sort_error" name="error">${error["sort"]}</SPAN>
		<SPAN class="span-help">数字越大越在前</SPAN>
    </TD>
   </TR>
	<TR>
  	<TD class="t-label t-label-h" width="12%">允许评论：</TD>
    <TD class="t-content" width="88%">
    	<label><input name="allow" type="radio" value="true" <c:if test="${question.allow == true}"> checked="checked"</c:if>>允许</label>
    	<label><input name="allow" type="radio" value="false" <c:if test="${question.allow == false}"> checked="checked"</c:if>>禁止</label>
    </TD>
   </TR>
   <TR>
  	<TD class="t-label t-label-h" width="12%">状态：</TD>
    <TD class="t-content" width="88%">
    	<label><input name="status" type="radio" value="10" <c:if test="${question.status == 10}"> checked="checked"</c:if>>待审核</label>
    	<label><input name="status" type="radio" value="20" <c:if test="${question.status == 20}"> checked="checked"</c:if>>已发布</label>
    </TD>
   </TR>
    <TR>
    <TD class="t-label t-label-h" width="12%">内容：</TD>
    <TD class="t-content" width="88%">
    	<textarea id="content" name="content" style="width:99%;height:400px;visibility:hidden;">${question.content}</textarea>
		<SPAN class="span-text" id="content_error" name="error">${error["content"]}</SPAN>
    </TD>
   </TR>
	<TR>
    <TD class="t-button" colspan="2">
    	 <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit()"></span>
    	 <span id="question_error" name="error" class="span-text"></span>
  	</TD>	
  </TR>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY>





<script>
var editor;
function initKindEditor(){

	// 指定编辑器iframe document的CSS数据，用于设置可视化区域的样式。 单冒号(:)用于CSS3伪类，双冒号(::)用于CSS3伪元素。伪元素由双冒号和伪元素名称组成。双冒号是在当前规范中引入的，用于区分伪类和伪元素。但是伪类兼容现存样式，浏览器需要同时支持旧的伪类，比如:first-line、:first-letter、:before、:after等
    KindEditor.options.cssData = "body {"+
		"font-size: 14px;"+//默认字体大小
	"}";
	KindEditor.ready(function(K) {
		
		editor = K.create('textarea[name="content"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
		//	autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			allowFlashUpload :true,
			uploadJson :"${config:url(pageContext.request)}control/question/manage.htm?method=upload&userName=${question.userName}&isStaff=${question.isStaff}&${_csrf.parameterName}=${_csrf.token}",//指定浏览远程图片的服务器端程序
	
			items : ['source', '|', 'preview', 'template', 'code',
        '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', 
        'formatblock', 'fontname', 'fontsize', '/', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
        'flash', 'media', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
         'link', 'unlink'],
			afterChange : function() {
				this.sync();
			}
			
		});
		
	});
	
}
initKindEditor();
</script>

</HTML>