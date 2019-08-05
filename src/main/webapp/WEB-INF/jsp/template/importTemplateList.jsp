<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<TITLE>导入模板列表</TITLE>
<base href="${config:url(pageContext.request)}">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/json3.js" type="text/javascript"></script>
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript" charset="UTF-8"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
</HEAD>


<script type="text/javascript" language="javascript">

//上传文件UI
function upload() {	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"文件：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input type=\"file\" name=\"uploadFile\" size=\"40\" value=\"\"/><span id=\"error_uploadFile\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:uploadSubmit()\"></span>";
	html += "&nbsp;&nbsp;<span id='progressBar'></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	
	systemLayerShow("上传文件",html,600,138);//显示层
}
//上传文件
function uploadSubmit() {
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	$("#importTemplateForm").ajaxSubmit({
        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
        type:'POST',// 提交类型可以是"GET"或者"POST"
        url:'control/template/manage.htm?method=upload',// 表单提交的路径
        beforeSend: function() {//表单提交前做表单验证
            //清空所有错误
        	$("[name='error']").html("");
        },
        //进度条的监听器
        xhr: function(){
			var xhr = $.ajaxSettings.xhr();
            if(onprogress && xhr.upload) {
            	xhr.upload.addEventListener("progress" , onprogress, false);
                return xhr;
            }
        },
        success: function(data) {//提交成功后调用
          
          //  alert(data.message);
        	//alert(data);
        	for(var returnValue in data){
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
        				window.location.reload();
        				return;
        			}
        		}else if(returnValue == "error"){
        			var error = data[returnValue];
        			for(var key in error){
        				
        				$("#error_"+key).html(error[key]);
        			}
        			//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
        		}
        		
        	}
        	
        },
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//jquery请求session过期跳转
			timeoutJump(XMLHttpRequest);
		}
    });
	
}

//侦查当前文件上传情况
function onprogress(evt){
    //侦查附件上传情况
    //通过事件对象侦查
    //该匿名函数表达式大概0.05-0.1秒执行一次
   // console.log(evt.loaded);  //已经上传大小情况
    //evt.total; 附件总大小
    var loaded = evt.loaded;
    var tot = evt.total;
    var per = Math.floor(100*loaded/tot);  //已经上传的百分比
    var son =  document.getElementById('progressBar');
    son.innerHTML = per+"%";
    son.style.width=per+"%";
}

//目录重命名UI
function directoryRenameUI(fileName,oldDirectory) {	
	var html = "";
	html += "<table class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"99%\" border=\"0\" align=\"center\">";
	html += "<tbody>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"旧目录名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		oldDirectory;
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-label t-label-h\" width=\"20%\">";
	html += 		"<span class=\"span-text\">*</span>新目录名称：";
	html += 	"</td>";
	html += 	"<td class=\"t-content\" width=\"80%\">";
	html += 		"<input class=\"form-text\" id=\"directoryName\" size=\"40\" value=\"\"/>&nbsp;<span id=\"error_directoryName\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "<tr>";
	html += 	"<td class=\"t-button\" colSpan=\"2\">";
	html += 		"<span class=\"submitButton\"><input type=\"button\" id=\"submitForm\" value=\"提交\" onClick=\"javascript:directoryRename('"+fileName+"')\"></span><span id=\"error_directoryRename\" name=\"error\" class=\"span-text\"></span>";
	html += 	"</td>";
	html += "</tr>";
	html += "</tbody>";
	html += "</table>";
	systemLayerShow("目录重命名",html,600,148);//显示层
}

//目录重命名
function directoryRename(fileName){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	var parameter = "";
	var directoryName = document.getElementById("directoryName").value; 
	if(directoryName != ""){
		parameter += "&directoryName="+encodeURIComponent(directoryName);
	}
	parameter += "&fileName="+encodeURIComponent(fileName);
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	//清除错误
	document.getElementById("error_directoryName").innerHTML="";	
	document.getElementById("error_directoryRename").innerHTML="";
	
   	post_request(function(value){
		if(value != ""){
			var returnValue = JSON.parse(value);//JSON转为对象
			for(var key in returnValue){
				if(key == "success"){	
					var success = returnValue[key];	
					if(success == "true"){
						systemLayerClose();//关闭层
						window.location.reload();
					}
				}else if(key == "error"){
					var errorValue = returnValue[key];
					for(var error in errorValue){
						if(error != ""){
							document.getElementById("error_"+error).innerHTML=errorValue[error];	
						}
					}
					//按钮设置 disabled="disabled"
					document.getElementById("submitForm").disabled=false;
				}
			}
			
		}
		
	},
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=directoryRename&timestamp=" + new Date().getTime(), true,parameter);
}








//导入模板
function importTemplate(templateFileName){
	var parameter = "";

	parameter += "&templateFileName="+templateFileName;

	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;

   	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	
	var importMessage = document.getElementById("importMessage_"+templateFileName);

	importMessage.innerHTML= "导入中...";
   	post_request(function(value){
		if(value == "-1"){
			importMessage.innerHTML="模板名称错误";
		}else if(value == "-2"){
			importMessage.innerHTML="模板不存在";
		}else if(value == "-3"){
			importMessage.innerHTML="模板目录已存在";
		}else if(value == "1"){
			importMessage.innerHTML="导入完成";
		}
	},
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=import&timestamp=" + new Date().getTime(), true,parameter);
}

//删除
function deleteExport(fileName){
	var parameter = "";

	parameter += "&fileName="+fileName;

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
		"${config:url(pageContext.request)}control/template/manage${config:suffix()}?method=deleteExport&timestamp=" + new Date().getTime(), true,parameter);
}

</script>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form id="importTemplateForm" enctype="multipart/form-data" method="post">
<DIV class="d-box">
<div class="d-button">
 	<input class="functionButton" type="button" onclick="javascript:upload();" value="模板上传">
</div>

<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TR>
	    <TD class="t-content" colSpan="5" height="28px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">
			&nbsp;>> &nbsp;导入模板
			
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH>文件名称</TH>
  	<TH>模板名称</TH>
    <TH>目录名称</TH>
    <TH>模板简介</TH>
    <TH>操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
  	<c:forEach items="${templatesList}" var="templates">
   		<TR > 	
   			<TD width="30%">${templates.fileName}</TD>
	   		<TD width="15%">${templates.name}</TD>
		    <TD width="15%">${templates.dirName}</TD>
			<TD width="25%">${templates.introduction}</TD>
			<TD width="15%">
				
				<a href="control/template/manage${config:suffix()}?method=templateDownload&fileName=${templates.fileName}">下载 </a>
				<a href="" onclick="directoryRenameUI('${templates.fileName}','${templates.dirName}'); return false;" hidefocus="true" ondragstart= "return false">目录重命名 </a>
				<a href="#" onclick="javascript:if(window.confirm('确定删除吗? ')){deleteExport('${templates.fileName}');return false;}else{return false};" hidefocus="true" ondragstart= "return false">删除</a>
				<a href="" onclick="importTemplate('${templates.fileName}'); return false;" hidefocus="true" ondragstart= "return false">导入</a>
				<span id="importMessage_${templates.fileName}" class="span-text"></span>
				
			</TD>   	
		</TR>
	</c:forEach>
  </TBODY>
  
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
