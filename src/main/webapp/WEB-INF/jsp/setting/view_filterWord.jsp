<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>过滤词</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript" ></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />



<script language="javascript" type="text/javascript">

//上传词库页面
function uploadFilterWordUI(){
	//弹出层
	var div = "<div id='divMessage'></div>";
	var html = "<TABLE class='t-table' cellSpacing='1' cellPadding='2' width='100%' border='0'>";
	html += "<TBODY>";
	html += "<TR>";
	html += "<TD class='t-label t-label-h' width='20%'>词库：</TD>";
	html += "<TD class='t-content' width='80%' align='left'>";
	html += "<a href='javascript:void(0);' class='fileButtonPic'><span><em>+</em>浏览...</span><input type='file' class='fileButton' id='file' name='file'></a>";
	html += "</TD>";
	html += "</TR>";
	html += "<TR>";
	html += "<TD class='t-button' colSpan='2'>";
	html += "<span class='submitButton'><INPUT type='button' id='submitForm' value='上传' onClick='javascript:uploadFilterWordSubmit()'></span>";
	html += "</TD>";
	html += "</TR>";
	html += "</TBODY>";
	html += "</TABLE>";

	systemLayerShow("上传词库",div,500,108);//显示层

	$("#divMessage").html(html);
	uploadChangeIcon();
}
//提交上传升级包
function uploadFilterWordSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	$("#filterWordForm").ajaxSubmit({
        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
        type:'POST',// 提交类型可以是"GET"或者"POST"
        url:'control/filterWord/manage.htm?method=uploadFilterWord',// 表单提交的路径
        beforeSend: function() {//表单提交前做表单验证
            
        },
        success: function(data) {//提交成功后调用
        	
        	for(var returnValue in data){
        		
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
						//刷新页面
						document.location.reload();
        				return;
        			}
        		}else if(returnValue == "error"){
        			var error = data[returnValue];
        			var errorValue = "";
        			for(var key in error){
        				errorValue +=error[key]+"\n";
     
        			}
        			alert(errorValue);
        			//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
        		}
        		
        	}
			
        }
    });

}

//删除词库
function deleteFilterWord(){
	var parameter = "";
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	post_request(function(value){
		if(value != ""){
			
			var data = JSON.parse(value);
			if(data == "1"){
				//刷新页面
				document.location.reload();
				
			}else{
				alert("删除文件失败");
			}

		}	
	},
		"${config:url(pageContext.request)}control/filterWord/manage${config:suffix()}?method=deleteFilterWord&timestamp=" + new Date().getTime(), true,parameter);


}


//上传已选择文件改变图标
function uploadChangeIcon(){
	$(".fileButtonPic").on("change","input[type='file']",function(){
	    var filePath=$(this).val();//路径
	 	$(this).attr("title",filePath);
	 	var path = getBasePath()+"backstage/images/tick_16.png";
		$(this).parent().find("em:eq(0)").css({"background":"url("+path+") 0 0"});
	});
}

</script>
</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="uploadFilterWordUI();" value="上传词库">
</div>
<form:form id="filterWordForm" enctype="multipart/form-data">

	<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
		<THEAD class="t-list-thead">
			<TR>
		  	<TH width="20%">过滤词数量</TH>
		  	<TH width="20%">词库大小</TH>
		    <TH width="20%">词库最后修改时间</TH>
		    <TH width="20%">前3个词</TH>
		    <TH width="20%">操作</TH>
			</TR>
		</THEAD>
		<c:if test="${filterWord != null }">
		<TBODY class="t-list-tbody" align="center">
			<TR>
			    <TD>${filterWord.wordNumber}</TD>
				<TD>${filterWord.size}</TD>
				<TD><fmt:formatDate value="${filterWord.lastModified}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
			    <TD>
			    	<c:forEach items="${filterWord.beforeWordList}" var="beforeWord">
			    		${beforeWord}<br>
			    	</c:forEach>
			    </TD>
			    <TD><a hidefocus="true" onClick="javascript:if(window.confirm('确定删除吗? ')){deleteFilterWord(); return false}else{return false}" href="#" ondragstart= "return false">删除</a></TD>
			</TR>
		</TBODY>
		</c:if>
	</TABLE>
</form:form>
<TABLE cellSpacing="2" cellPadding="0" width="100%"  border="0" align="center" rules="none" style="margin-top: 6px;">
	<TBODY>	
   		<tr>
   			<td height="16px" >
   				<span class="span-text" >词库必须为UTF-8格式</span>
   			</td>
   		</tr>
	</TBODY>
</TABLE>

</DIV></BODY></HTML>
