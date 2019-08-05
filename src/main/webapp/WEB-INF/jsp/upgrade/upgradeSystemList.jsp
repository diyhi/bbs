<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>升级列表</TITLE>
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
//说明弹出层
function showExplanation(id){
	var div = "<div id='divMessage'></div>";
	systemLayerShow(id+"说明",div,700,400);//显示层
	//查询说明
	queryExplanation(id);
}

//查询说明
function queryExplanation(id){	
	get_request(function(value){
		if(value != ""){
			var upgradeSystem = JSON.parse(value);//JSON转为对象
			
			var html = "<DIV class='d-box'>";
			html += "<TABLE class='t-table' cellSpacing='1' cellPadding='2' width='100%' border='0' height='30px'>";
			html += "<TBODY>";
			html += "<TR>";
			html += "<TD class='t-leftContent'>";
			html += upgradeSystem.explanation;
			html += "</TD>";
			html += "</TR>";
			html += "</TBODY>";
			html += "</TABLE>";
			html += "</DIV>";
			$("#divMessage").html(html);
		}
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=queryUpgrade&upgradeSystemId="+encodeURIComponent(id)+"&timestamp=" + new Date().getTime(), true);
	
}

//伸缩升级日志
function scalabilityUpgradeLog(id){
	var log_div = document.getElementById("log_"+id);
	if(log_div.style.display == "none"){
		log_div.style.display="";
		document.getElementById("extend_"+id).style.display="none";
		document.getElementById("shrink_"+id).style.display="";
	}else{
		log_div.style.display="none";
		document.getElementById("extend_"+id).style.display="";
		document.getElementById("shrink_"+id).style.display="none";
	}
	
	
}


//立即升级
function upgradeNow(updatePackageName){
	//获取提交的参数
	var parameter = "&updatePackageName="+updatePackageName;

	//隐藏‘立即升级’按钮 disabled="disabled"
	document.getElementById("upgradeNowButton").style.display="none";
//	layer.msg('这是最常用的吧',{time: 200000000 });
//	var index = layer.tips('升级包解压中', '#upgradeNowButton', {
//	  tips: [4, '#78BA32'],time: 900000000
//	});
	var index = layer.msg('升级任务开始运行',{time: 0 ,shade: [0.3, '#393D49']});
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//alert(parameter);
	post_request(function(value){
		if(value != ""){

			var index = layer.msg('升级任务开始运行',{shade: [0.3, '#393D49']});
		
			var data = JSON.parse(value);
			
			//清除所有错误
			clearError();
			
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			
        			if(data[returnValue] == true){
        				//读取当前升级
        				queryCurrentUpgrade(data["upgradeId"],false);
        				//显示继续升级按钮
						document.getElementById("continueUpgradeButton").style.display="";
        				
        				//执行继续升级
        				continueUpgrade(data["upgradeId"]);
        				return;
        			}
        		}else if(returnValue == "error"){
        			
        			var error = data[returnValue];
        			for(var key in error){
        				document.getElementById("error_"+key).innerHTML = error[key]; 
        			}
        		}
        		
        	}

		}	
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=upgradeNow&timestamp=" + new Date().getTime(), true,parameter);

	
}
//继续升级
function continueUpgrade(id){
	//获取提交的参数
	var parameter = "&upgradeId="+id;
	//按钮设为不可用 disabled="disabled"
	document.getElementById("continueUpgradeButton").disabled=true;
	var csrf =  getCsrf();
	parameter += "&_csrf_token="+csrf.token;
	parameter += "&_csrf_header="+csrf.header;
	//删除第一个&号,防止因为多了&号而出现警告: Parameters: Invalid chunk ignored.信息
	if(parameter.indexOf("&") == 0){
		parameter = parameter.substring(1,parameter.length);
	}
	//alert(parameter);
	post_request(function(value){
		if(value != ""){
			//执行定时刷新	
			queryCurrentUpgrade(id,true);
			
			var data = JSON.parse(value);
			
			//清除所有错误
			clearError();
			
			for(var returnValue in data){
				
        		if(returnValue == "success"){
        			
        			if(data[returnValue] == true){
        				
        				return;
        			}
        		}else if(returnValue == "error"){
        			//按钮设为可选 disabled="disabled"
					document.getElementById("continueUpgradeButton").disabled=false;
        		}
        		
        	}

		}	
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=continueUpgrade&timestamp=" + new Date().getTime(), true,parameter);
	
	
}



/**
var lock = 1;//用于判断某个请求响应是否成功
//定时获取当前升级
function extendOrderLock(orderId){
	
		
		get_request(function(value){ls.innerHTML=value;
			lock = value;
			if(lock = 1){
				setTimeout(function(){
			        extendOrderLock(orderId);
			    }, 10000);//10秒钟刷新 
			}
		},
			"${config:url(pageContext.request)}control/order/manage${config:suffix()}?method=orderLock&orderId="+orderId+"&timestamp=" + new Date().getTime(), true);
	
}**/




//查询当前升级 timing：是否定时执行
function queryCurrentUpgrade(id,timing){	
	get_request(function(value){
		if(value != ""){
			var upgradeSystem = JSON.parse(value);//JSON转为对象
			var upgradeLogList = upgradeSystem.upgradeLogList;
			var html = "";
			for(var i = 0; i <upgradeLogList.length; i++){
				var upgradeLog = upgradeLogList[i];
				//刷新升级日志
				html += "<TR>";
				html += "<TD style='BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom : #BFE3FF 1px dotted;' width='20%' height='26px' align='center'>"+upgradeLog.time+"</TD>";
				var color = "";
				if(upgradeLog.grade == 2){
					color = " color: red;";
				}
				html += "<TD style='border-bottom: #BFE3FF 1px dotted;"+color+" ' width='80%' align='left' >&nbsp;"+upgradeLog.content+"</TD>";
				html += "</TR>";
			}
			
			if(upgradeSystem.runningStatus == 9999){
				//隐藏继续升级按钮
				document.getElementById("continueUpgradeButton").style.display="none";
				//显示完成升级
				document.getElementById("upgradeComplete").style.display="";
			}
			//显示中断状态
			if(upgradeSystem.interruptStatus == 0){//0:正常
				document.getElementById("interruptError").style.display="none";
				document.getElementById("interruptRestart").style.display="none";
			}
			if(upgradeSystem.interruptStatus == 1){//1:错误
				document.getElementById("interruptError").style.display="";
				//按钮设为可用 disabled="disabled"
				document.getElementById("continueUpgradeButton").disabled=false;
    
			}
        	if(upgradeSystem.interruptStatus == 2){//2:待重启
				document.getElementById("interruptRestart").style.display="";
			}
			
			$("#current_upgradeLog").html(html);
			
			if(timing == true && upgradeSystem.runningStatus <9999 && upgradeSystem.interruptStatus < 2){
				setTimeout(function(){
				        queryCurrentUpgrade(id,timing)
				    }, 5000);//5秒钟刷新 
			}
			
			
		}
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=queryUpgrade&upgradeSystemId="+encodeURIComponent(id)+"&timestamp=" + new Date().getTime(), true);
	
}
/**
//查询当前升级
function queryCurrentUpgrade(id){	
	get_request(function(value){
		if(value != ""){
			var upgradeSystem = JSON.parse(value);//JSON转为对象
			var upgradeLogList = upgradeSystem.upgradeLogList;
			var html = "";
			for(var i = 0; i <upgradeLogList.length; i++){
				var upgradeLog = upgradeLogList[i];
				//刷新升级日志
				html += "<TR>";
				html += "<TD style='BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom : #BFE3FF 1px dotted;' width='20%' height='25px' align='center'>"+upgradeLog.time+"</TD>";
				var color = "";
				if(upgradeLog.grade == 2){
					color = " color: red;";
				}
				html += "<TD style='border-bottom: #BFE3FF 1px dotted;"+color+" ' width='80%' align='left' >&nbsp;"+upgradeLog.content+"</TD>";
				html += "</TR>";
			}
			
			if(upgradeSystem.runningStatus == 9999){
				//隐藏继续升级按钮
				document.getElementById("continueUpgradeButton").style.display="none";
				//显示完成升级
				document.getElementById("upgradeComplete").style.display="";
			}
			
        	
			
			$("#current_upgradeLog").html(html);
		}
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=queryUpgrade&upgradeSystemId="+encodeURIComponent(id)+"&timestamp=" + new Date().getTime(), true);
	
}**/




//清理错误提示
function clearError(){
	var errorTag = getElementsByName_pseudo("span", "errorTag");
	for(var i = 0; i < errorTag.length; i++){
		errorTag[i].innerHTML = ""; 
	}
}

//查询升级包列表
function queryUpgradePackageList(){
	get_request(function(value){
		if(value != ""){
			
			var upgradePackageList = JSON.parse(value);//JSON转为对象
			
			var html = "<TABLE class='t-list-table' cellSpacing='1' cellPadding='0' width='100%' border='0'>";
			html += "<THEAD class='t-list-thead'>";
			html += "<TR>";
			html += "<TH width='50%'>升级包名称</TH>";
			html += "<TH width='20%'>文件大小</TH>";
			html += "<TH width='20%'>上传时间</TH>";
			html += "<TH width='10%'>操作</TH>";
			html += "</TR>";
			html += "</THEAD>";
			html += "<TBODY class='t-list-tbody' align='center'>";
			for(var i =0; i< upgradePackageList.length; i++){
				var upgradePackage = upgradePackageList[i];
				html += "<TR>";
				html += "<TD>"+upgradePackage.name+"</TD>";
				html += "<TD>"+upgradePackage.size+"</TD>";
				html += "<TD>"+upgradePackage.lastModifiedTime+"</TD>";
				html += "<TD><a href='#' onClick=\"deleteUpgradePackage('"+upgradePackage.name+"');return false\" hidefocus='true' ondragstart='return false'>删除</a></TD>";
				html += "</TR>";
			}
			
			html += "</TBODY>";
			html += "</TABLE>";
			$("#upgradePackage").html(html);	
		}
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=queryUpgradePackageList&timestamp=" + new Date().getTime(), true);
}


//上传升级包页面
function uploadUpgradePackageUI(){
	//弹出层
	var div = "<div id='divMessage'></div>";
	var html = "<TABLE class='t-table' cellSpacing='1' cellPadding='2' width='100%' border='0'>";
	html += "<TBODY>";
	html += "<TR>";
	html += "<TD class='t-label t-label-h' width='20%'>升级包：</TD>";
	html += "<TD class='t-content' width='80%' align='left'>";
	html += "<a href='javascript:void(0);' class='fileButtonPic'><span><em>+</em>浏览...</span><input type='file' class='fileButton' id='file' name='file'></a>";
	html += "</TD>";
	html += "</TR>";
	html += "<TR>";
	html += "<TD class='t-button' colSpan='2'>";
	html += "<span class='submitButton'><INPUT id='uploadSubmit' type='button' value='上传' onClick='javascript:uploadUpgradePackageSubmit()'></span>";
	html += "&nbsp;&nbsp;<span id='progressBar'></span>";
	html += "</TD>";
	html += "</TR>";
	html += "</TBODY>";
	html += "</TABLE>";

	systemLayerShow("上传升级包",div,500,108);//显示层

	$("#divMessage").html(html);
	uploadChangeIcon();
}
//提交上传升级包
function uploadUpgradePackageSubmit(){
	//按钮设置 disabled="disabled"
	document.getElementById("uploadSubmit").disabled=true;

	$("#upgradeForm").ajaxSubmit({
        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
        type:'POST',// 提交类型可以是"GET"或者"POST"
        url:'control/upgrade/manage.htm?method=uploadUpgradePackage',// 表单提交的路径
        beforeSend: function() {//表单提交前做表单验证
            
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
        	
        	for(var returnValue in data){
        		
        		if(returnValue == "success"){
        			if(data[returnValue] == true){
        				systemLayerClose();//关闭层
						queryUpgradePackageList();
        				return;
        			}
        		}else if(returnValue == "error"){
        			var error = data[returnValue];
        			var errorValue = "";
        			for(var key in error){
        				errorValue +=error[key]+"\n";
     
        			}
        			alert(errorValue);
        		}
        		
        	}
			//按钮设置 disabled="disabled"
			document.getElementById("uploadSubmit").disabled=false;
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
//删除升级包
function deleteUpgradePackage(fileName){
	var parameter = "fileName="+fileName;
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
				queryUpgradePackageList();
			}else{
				alert("删除文件失败");
			}

		}	
	},
		"${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=deleteUpgradePackage&timestamp=" + new Date().getTime(), true,parameter);


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
	<input class="functionButton" type="button" onClick="uploadUpgradePackageUI();" value="上传升级包">
</div>
<form:form id="upgradeForm" enctype="multipart/form-data">

<div class="item clearfix" id="tabs">
	<div class="itemTab active" onclick="put_css(1)">
    	<span>升级</span>
    </div>
    <div class="itemTab" onclick="put_css(2);queryUpgradePackageList();">
    	<span>升级包列表</span>
    </div>
</div>
<div class="clear"></div>
<div id="card">
    <div style="">
<c:if test="${notCompletedUpgrade == null}">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="15%">当前BBS版本：</TD>
		    <TD class="t-content" width="85%" colspan="3">
		    	<enhance:out escapeXml="false">
		    	<pre>${currentVersion}</pre>
		    	</enhance:out>
		    </TD>
		    
		</TR>
	</TBODY>
</TABLE>
</c:if>

<c:if test="${notCompletedUpgrade != null}">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-content" width="100%" colSpan="4" align="center">
		    	<TABLE cellSpacing="2" cellPadding="0" width="99%"  border="0" align="center" rules="none">
				<TBODY>	
		    		<tr>
		    			<td height="50px" >
		    				<c:if test="${notCompletedUpgrade.runningStatus == 0}">
					    		<span class="submitButton"><INPUT id="upgradeNowButton" type="button" value="立即升级" onClick="upgradeNow('${notCompletedUpgrade.updatePackageName}');return false"></span>
					    	</c:if>
		    	
					    	<span class="submitButton" ><INPUT id="continueUpgradeButton" type="button"  value="继续升级" onClick="continueUpgrade('${notCompletedUpgrade.id }');return false" <c:if test="${notCompletedUpgrade.runningStatus == 0}"> style='display: none;'</c:if> <c:if test="${notCompletedUpgrade.interruptStatus == 0 && notCompletedUpgrade.runningStatus > 20}"> disabled='disabled'</c:if>></span>
					    	
					    	<span id="upgradeComplete" style="font-size: 16px;font-weight:bold; display: none; color: green;">升级完成</span>
					    </td>
		    		</tr>
		    		<tr>
		    			<td height="20px" >
		    				<span id="error_upgradeNow" name="errorTag" class="span-text" ></span>
		    				
		    				<span id="interruptError" class="span-text" style="display: none;">出现错误升级已中断,原因请查看日志</span>
		    				<span id="interruptRestart" class="span-text" style="display: none;display: none;">需重启应用服务器才能继续升级</span>		
		    			</td>
		    		</tr>
				</TBODY>
				</TABLE>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">升级包文件名称：</TD>
		    <TD class="t-content" width="35%" >
		    	${notCompletedUpgrade.updatePackageName}
		    </TD>
		    <TD class="t-label t-label-h" width="15%">升级包上传时间：</TD>
		    <TD class="t-content" width="35%" >
		    	<fmt:formatDate value="${notCompletedUpgrade.updatePackageTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>
		    	
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">旧BBS版本：</TD>
		    <TD class="t-content" width="35%" >
		    	${notCompletedUpgrade.oldSystemVersion}
		    </TD>
		    <TD class="t-label t-label-h" width="15%">升级包版本：</TD>
		    <TD class="t-content" width="35%" >
		    	${notCompletedUpgrade.updatePackageVersion}
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">升级日志：</TD>
		    <TD class="t-content" width="85%" colspan="3">
		    	<TABLE cellSpacing="2" cellPadding="0" width="99%"  border="0" align="center" rules="none">
				<TBODY id="current_upgradeLog">	
		    	<c:forEach items="${notCompletedUpgrade.upgradeLogList}" var="upgradeLog">
					<TR>
						 <TD style="BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom : #BFE3FF 1px dotted;" width="20%" height="26px" align="center"><fmt:formatDate value="${upgradeLog.time}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
						 <TD style="border-bottom: #BFE3FF 1px dotted; <c:if test="${upgradeLog.grade == 2}"> color: red;</c:if>" width="80%" align="left" >&nbsp;${upgradeLog.content }</TD>
					</TR>
				</c:forEach>
				</TBODY></TABLE>
		    </TD>
		    
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="15%">说明：</TD>
		    <TD class="t-content" width="85%" colspan="3">
		    	<enhance:out escapeXml="false">
		    	<pre>${notCompletedUpgrade.explanation}</pre>
		    	</enhance:out>
		    </TD>
		    
		</TR>
	</TBODY>
</TABLE>
</c:if>
<TABLE class="t-list-table" cellSpacing="1" cellPadding="0" width="100%" border="0">
  <THEAD class="t-list-thead">
  <TR>
  	<TH width="20%">当前版本</TH>
    <TH width="20%">旧BBS版本</TH>
    <TH width="20%">升级包版本</TH>
    <TH width="20%">升级时间</TH>
    <TH width="20%">操作</TH>
    </TR></THEAD>
  <TBODY class="t-list-tbody" align="center">
   <c:forEach items="${upgradeSystemList}" var="entry">
   <c:if test="${entry.runningStatus ==9999}">
	  <TR >
	  	<TD>${entry.id}</TD>
	    <TD>${entry.oldSystemVersion}</TD>
	    <TD>${entry.updatePackageVersion}</TD>
	    <TD><fmt:formatDate value="${entry.upgradeTime}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
	    <TD>
	    	<a href="#" onClick="showExplanation('${entry.id }');return false" hidefocus="true" ondragstart="return false">说明</a>
	    	<a href="#" onClick="scalabilityUpgradeLog('${entry.id }');return false" hidefocus="true" ondragstart="return false">升级日志</a><span id="extend_${entry.id }">↓</span><span id="shrink_${entry.id }" style="display: none;">↑</span>
	    </TD>
	  </TR>
	  <TR id="log_${entry.id }" style="display: none;">
	  	<TD colSpan="5" >
	  		
	  		<TABLE cellSpacing="2" cellPadding="0" width="99%"  border="0" align="center" rules="none">
				<TBODY>	
					<TR align="center" height="28px">
				  		<TD colspan="2">升级日志</TD>
				    </TR>  	
					<c:forEach items="${entry.upgradeLogList}" var="upgradeLog">
						<TR>
							 <TD style="BORDER-RIGHT: #BFE3FF 1px dotted;BORDER-TOP: #BFE3FF 1px dotted;" width="20%" align="center"><fmt:formatDate value="${upgradeLog.time}"  pattern="yyyy-MM-dd HH:mm:ss"/></TD>
							 <TD style="BORDER-TOP: #BFE3FF 1px dotted; <c:if test="${upgradeLog.grade == 2}"> color: red;</c:if>" width="80%" align="left" >&nbsp;${upgradeLog.content }</TD>
						</TR>
					</c:forEach>
				</TBODY>
			</TABLE>
	
		</TD>
	  </TR>
	  </c:if>
	 </c:forEach>
  </TBODY>
</TABLE>
    </div>
    <div id="upgradePackage" style="">
    </div>
</div>

<TABLE cellSpacing="2" cellPadding="0" width="100%"  border="0" align="center" rules="none">
	<TBODY>	
   		<tr>
   			<td height="16px" >
   				<span class="span-text" >1.升级前请先备份所有数据</span>
   			</td>
   		</tr>
   		<tr>
   			<td height="16px" >
   				<span class="span-text" >2.升级过程不能中断</span>
   			</td>
   		</tr>
	</TBODY>
</TABLE>
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
</script>
</DIV></BODY></HTML>
