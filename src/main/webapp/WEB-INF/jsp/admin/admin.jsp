<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<head>
<base href="${config:url(pageContext.request)}"></base>
<title>管理中心</title>
<meta http-equiv=content-type content="text/html; charset=utf-8"/>

<link rel="shortcut icon" type="image/x-icon" href="${config:url(pageContext.request)}backstage/images/favicon.ico" media="screen" />
<link href="backstage/css/admin.css" rel="stylesheet">
<link href="backstage/fontAwesome/style.css" rel="stylesheet">
<script src="backstage/jquery/jquery.min.js"></script>
<script language="javascript" src="backstage/jquery/jquery.ba-resize.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript" ></script>
<script language="javascript" src="backstage/js/Map.js" type="text/javascript" ></script>

	
	
<script type="text/javascript">

//加载弹出框架
function loadFrame(url,title){
	var dialog = $.dialog({ 
		id:'frame_1',
		title: title, 
	    lock: true, 
	    content: 'url: '+url, 
	    icon: 'error.gif', 
	    cancel: true,
	    drag:false
	    
	});
	dialog.max();
}
//弹出框架回调
function callbackFrame(){
	$.dialog({id:'frame_1'}).close();
}


//加载修改话题页面
function loadTopic(title,url){
	var dialog = $.dialog({ 
		id:'loadTopic_1',
		title: title, 
	    lock: true, 
	    content: 'url: '+url, 
	    icon: 'error.gif', 
	    cancel: true,
	    drag:false
	    
	});
	dialog.max();
}
//修改话题页面回调
function callbackTopic(){
	$.dialog({id:'loadTopic_1'}).close();
	//刷新框架选中窗口
	refreshs();
}

</script>

</head>
<body>
<script type="text/javascript" src="backstage/lhgdialog/lhgcore.lhgdialog.min.js"></script>

<table cellspacing="0" cellpadding="0" width="100%" border="0">  
	<tr > 
		<td colspan="3" class="top">
			<div class="box" >
				<div class="logo-wrap"><div class=logo></div></div>
				<div class="nav-wrap">
				    <div class="nav-ul">
				        <ul id="navigationMenu">
				            <li id="nav_1" class="nav-list-item on" onClick="editBackground(this.id,'内容管理');">
				                <div class="pos-rel" ><span class="nav-list-title">内容管理</span></div>
				            </li>
				            <li id="nav_2" class="nav-list-item" onClick="editBackground(this.id,'会员管理');">
				                <div class="pos-rel" ><span class="nav-list-title">会员管理</span></div>
				            </li>
				            <li id="nav_3" class="nav-list-item" onClick="editBackground(this.id,'页面管理');">
				                <div class="pos-rel" ><span class="nav-list-title">页面管理</span></div>
				            </li>
				            <li id="nav_4" class="nav-list-item" onClick="editBackground(this.id,'运营管理');">
				                <div class="pos-rel" ><span class="nav-list-title">运营管理</span></div>
				            </li>
				            <li id="nav_5" class="nav-list-item" onClick="editBackground(this.id,'系统设置');">
				                <div class="pos-rel" ><span class="nav-list-title">系统设置</span></div>
				            </li>
				            
				            <li class="nav-list-item ">
				                <div class="pos-rel" >
				                	<div class="nav-list-title2">
				                		<div class="browserButton" onclick ="retreat()" title="后退">
				                      		<div class="circle"><i class="fa fa-arrow-left" ></i></div>
				                      	</div>
				                	</div>
				                </div>
				            </li>
				            <li class="nav-list-item ">
				                <div class="pos-rel" >
				                	<div class="nav-list-title2" onclick ="advance()" title="前进">
				                		<div class="browserButton">
				                      		<div class="circle"><i class="fa fa-arrow-right" ></i></div>
				                      	</div>
				                	</div>
				                </div>
				            </li>
				            <li class="nav-list-item ">
				                <div class="pos-rel" >
				                	<div class="nav-list-title2">
				                		<div class="browserButton" onclick ="refreshs()" title="刷新">
				                      		<div class="circle"><i class="fa fa-redo-alt" ></i></div>
				                      	</div>
				                	</div>
				                </div>
				            </li>
				        </ul>
				    </div>
				    <div class="fr">
			            <ul>
			            	<li class="nav-list-item">
			                	<div class="pos-rel" title="网站首页">
			                		<div class="nav-list-title2" >
			                			<a class="link-icon" hidefocus="true"  href="" target="_blank"><i class="fa fa-home2" style="color:#ff9e00;"></i></a>
			                    	</div>
			                    </div>
			                </li>
			                <li class="nav-list-item">
			                	<div class="pos-rel" title="后台首页">
			                		<div class="nav-list-title2" >
			                			<div class="link-icon"  onclick="createWindow('home','首页','control/center/home${config:suffix()}');return false;" ><i class="fa fa-home"></i></div>	
			                    	</div>
			                    </div>
			                </li>
			                <li class="nav-list-item">
			                	<div class="pos-rel" title="联系我们">
			                		<div class="nav-list-title2">	
			                			<a class="link-icon" hidefocus="true" href="http://www.diyhi.com" target=_blank><i class="fa fa-phone" ></i></a> 
			                    	</div>
			                    </div>
			                </li>
			                <li class="nav-list-item hide">
			                    <div class="pos-rel">
			                      	<div class="nav-list-title3">
			                      		<div class="avatarImg">
			                      			<div class="circle"><i class="fa fa-user" ></i></div>
			                      		</div>
			                      		<div class="user-info">
											<span class="name">${sysUsers.fullName}</span>
											<span class="role">${sysUsers.userDuty}</span>
										</div>
										<div class="more"><i class="fa fa-angle-down"></i></div>
										
			                      	</div>
			                    </div>
			                    <div class="nav-pop-list" >
			                        <ul class="ul-navlist">
			                            <li onclick="createWindow('A_5_0','员工列表','control/staff/manage${config:suffix()}?method=editStaff&userId=${sysUsers.userId }&page=');return false;"><i class="fa fa-user-cog"></i>个人设置</li>
			                            <li onClick="javascript:submitLogout();return false;"><i class="fa fa-sign-out-alt" style="font-size: 13px;margin-right: 5px;"></i>退出登录</li>
			                        </ul>
			                    </div>
			                    <form:form id="logout" action="${config:url(pageContext.request)}admin/logout" method="post" ></form:form>
			                </li>
			            </ul>
			        </div>
				</div>
			</div>
		</td>
	</tr>
	
	<tr> 
		<!-- 左侧导航栏 -->
    	<td id="leftFrame_td"  width="172px" valign="top" class="left ">
    		<div id="main_nav" ></div>
    	</td> 
    	
   		<!-- 左侧显示/隐藏菜单  -->
    	<td width="9px" height="100%" valign="top" class="left_navigation ">
    		<div class="main leftArrow" id="main" onclick="menus();"></div>
    	</td>
    	<td valign="top" >
    		
    		<div class="iframeTab" >
	            <ul class="tabBar clearfix" id="tabBar">
	            </ul>
	        </div>
    		<div id="tabFrame">
    	 	</div>
    	</td>
    </tr>  
</table>
</body>  

<script type="text/javascript">
/**
$(function(){  
	var height = $(window).height();//浏览器当前窗口可视区域高度
	$("#mainFrame").height(height-60);//60为顶部占用位置
});**/

//左边显示/隐藏菜单
function menus(){
	var leftFrame = window.document.getElementById("leftFrame_td");
	if(leftFrame.style.display =="none"){
		leftFrame.style.display ="";
	}else{
		leftFrame.style.display ="none";
	}
}

/**
function getObject(objectId){
	if(document.getElementById&&document.getElementById(objectId)){
		return document.getElementById(objectId);
	}else if(document.all&&document.all(objectId)){
		return document.all(objectId);
	}else if(document.layers&&document.layers[objectId]){
		return document.layers[objectId];
	}else{
		return false;
	}
}**/
function outlook(){
	this.titlelist=new Array();
	this.itemlist=new Array();
	this.addtitle=addtitle;
	this.additem=additem;
	this.getLeftTree=getLeftTree;
}
function theitem(intitle,insort,inkey,inisdefault){
	this.menuTypeName=insort;
	this.key=inkey;
	this.title=intitle;
	this.isdefault=inisdefault;
}
function addtitle(intitle,menuTypeName,inisdefault){
	config.itemlist[config.titlelist.length]=new Array();
	config.titlelist[config.titlelist.length]=new theitem(intitle,menuTypeName,0,inisdefault);
	return(config.titlelist.length-1);
}
function additem(intitle,parentid,inkey){
	if(parentid>=0&&parentid<=config.titlelist.length){
		insort="item_"+parentid;config.itemlist[parentid][config.itemlist[parentid].length]=new theitem(intitle,insort,inkey,0);
		return(config.itemlist[parentid].length-1);
	}else additem=-1;
}
function getLeftTree(menuTypeName){
	var output="";
	
	for(i=0;i<config.titlelist.length;i++){
		if(config.titlelist[i].isdefault==1&&config.titlelist[i].menuTypeName==menuTypeName){
			output+="<DL id=sub_DL_"+i+">";
			output+="<DT onclick=\"hideorshow('sub_DL_"+i+"')\"><SPAN >"+config.titlelist[i].title+"</SPAN><i id=sub_I_"+i+" class='fa fa-angle-up' style='font-size: 12px;position: absolute;right: 10px;top: 10px; '></i></DT>";
			
			for(j=0;j<config.itemlist[i].length;j++){
				//如果内容为空,则输出空行
				if(config.itemlist[i][j].title == ""){
					output+="<DD id=sub_DD_"+i+'_'+j+"  style='height: 5px'>";	
					output+="&nbsp;";	
					output+="</DD>";
					
					continue;
				}
				output+="<DD id=sub_DD_"+i+'_'+j+">";
			//	output+="<A id='A_"+i+'_'+j+"' href='#' ondragstart= 'return false' onclick=\"hyperlink('"+config.itemlist[i][j].key+"','A_"+i+'_'+j+"')\; return false\"  hidefocus='true'>"+config.itemlist[i][j].title+"</A> ";
				output+="<A id='A_"+i+'_'+j+"' href='#' ondragstart= 'return false' onclick=\"hyperlink('"+config.itemlist[i][j].key+"','A_"+i+'_'+j+"','"+config.itemlist[i][j].title+"')\; return false\"  hidefocus='true'>"+config.itemlist[i][j].title+"</A> ";
							
				output+="</DD>";
			}
			output+="</DL>";
		}
	}
	document.getElementById('main_nav').innerHTML=output;

}
//超链接
function hyperlink(url,id,name){
	//清空所有的字体颜色
	var font = document.getElementById("main_nav").getElementsByTagName("a");
	for (var i=0;i<font.length;i++){
	//	font[i].className="";//有兼容问题		
		font[i].style.cssText="";
	}
	//设置当前选中选中字体颜色
//	document.getElementById(a).className="links";//有兼容问题#0099CC
	document.getElementById(id).style.cssText="color:#1a97e4";		
	//根据含有后缀字符返回参数
	var suffix= (url.indexOf("?") != -1 ? "&" : "?")+"jsTime=" + (new Date()).getTime();
//	window.top.frames['mainFrame'].location=url+suffix;

	createWindow(id,name,url);


}
//显示隐藏左边框导航分类
function hideorshow(divid){
	var sub_detail = document.getElementById(divid).getElementsByTagName("DD");
	for(i=0;i<sub_detail.length;i++){ 
		var array = new Array();
		array = sub_detail[i].id.split("_");
		var firstNumber = array[2]; //第一层编号
		
		if(document.getElementById(sub_detail[i].id).style.display=="none"){
			document.getElementById(sub_detail[i].id).style.display="block";
			//alert(sub_detail[i].id);
			document.getElementById("sub_I_"+firstNumber).className="fa fa-angle-up";
		}else{
			document.getElementById(sub_detail[i].id).style.display="none";
			document.getElementById("sub_I_"+firstNumber).className="fa fa-angle-down";
			
		}	 
	}
	
}
//系统初始化时执行
function initinav(menuTypeName){
	config.getLeftTree(menuTypeName);
}
// 导航栏配置文件
var config =new outlook();

//初始化配置文件
function initConfig(){
	var t;
	t=config.addtitle('系统设置','管理首页',1);
	config.additem('点击退出登录',t,'');
	
	
	t=config.addtitle('话题管理','内容管理',1);
	config.additem('话题列表',t,'${config:url(pageContext.request)}control/topic/list${config:suffix()}');
	config.additem('标签列表',t,'${config:url(pageContext.request)}control/tag/list${config:suffix()}');
	config.additem('全部待审核话题',t,'${config:url(pageContext.request)}control/topic/allAuditTopic${config:suffix()}');
	config.additem('全部待审核评论',t,'${config:url(pageContext.request)}control/topic/allAuditComment${config:suffix()}');
	config.additem('全部待审核回复',t,'${config:url(pageContext.request)}control/topic/allAuditReply${config:suffix()}');
	config.additem('话题搜索',t,'${config:url(pageContext.request)}control/topic/search${config:suffix()}');
	//config.additem('批量操作',t,'');
	t=config.addtitle('留言管理','内容管理',1);
	config.additem('留言列表',t,'${config:url(pageContext.request)}control/feedback/list${config:suffix()}');
	t=config.addtitle('友情链接管理','内容管理',1);
	config.additem('友情链接列表',t,'${config:url(pageContext.request)}control/links/list${config:suffix()}');
	
	
	

	t=config.addtitle('会员管理','会员管理',1);
	config.additem('会员搜索',t,'${config:url(pageContext.request)}control/user/search${config:suffix()}');
	config.additem('会员列表',t,'${config:url(pageContext.request)}control/user/list${config:suffix()}');
	config.additem('会员等级',t,'${config:url(pageContext.request)}control/userGrade/list${config:suffix()}');
	config.additem('会员注册项',t,'${config:url(pageContext.request)}control/userCustom/list${config:suffix()}');
	config.additem('会员注册禁止用户名称',t,'${config:url(pageContext.request)}control/disableUserName/list${config:suffix()}');
	
	t=config.addtitle('员工管理','会员管理',1);
	config.additem('员工列表',t,'${config:url(pageContext.request)}control/staff/list${config:suffix()}');
	config.additem('角色列表',t,'${config:url(pageContext.request)}control/roles/list${config:suffix()}');
	
	
	t=config.addtitle('在线帮助管理','页面管理',1);
	config.additem('在线帮助分类',t,'${config:url(pageContext.request)}control/helpType/list${config:suffix()}');
	config.additem('在线帮助列表',t,'${config:url(pageContext.request)}control/help/list${config:suffix()}?visible=true');
	
	t=config.addtitle('模板管理','页面管理',1);
	config.additem('模板列表',t,'${config:url(pageContext.request)}control/template/list${config:suffix()}');
	
	t=config.addtitle('浏览量管理','运营管理',1);
	config.additem('浏览量列表',t,'${config:url(pageContext.request)}control/pageView/list${config:suffix()}');
	t=config.addtitle('文件打包管理','运营管理',1);
	config.additem('压缩文件列表',t,'${config:url(pageContext.request)}control/filePackage/list${config:suffix()}');
	t=config.addtitle('系统通知管理','运营管理',1);
	config.additem('系统通知列表',t,'${config:url(pageContext.request)}control/systemNotify/list${config:suffix()}');

	t=config.addtitle('全站设置','系统设置',1);
	config.additem('基本设置',t,'${config:url(pageContext.request)}control/systemSetting/manage/edit${config:suffix()}');
	config.additem('维护数据',t,'${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=maintainData');
	config.additem('敏感词',t,'${config:url(pageContext.request)}control/filterWord/manage${config:suffix()}?method=view');
	config.additem('数据库备份/还原',t,'${config:url(pageContext.request)}control/dataBase/list${config:suffix()}');
	config.additem('服务器节点参数',t,'${config:url(pageContext.request)}control/systemSetting/manage${config:suffix()}?method=nodeParameter');
	config.additem('升级',t,'${config:url(pageContext.request)}control/upgrade/manage${config:suffix()}?method=upgradeSystemList');
	
	t=config.addtitle('短信管理','系统设置',1);
	config.additem('短信接口列表',t,'${config:url(pageContext.request)}control/smsInterface/list${config:suffix()}');
	config.additem('短信发送错误日志',t,'${config:url(pageContext.request)}control/sendSmsLog/list${config:suffix()}');
	
	t=config.addtitle('缩略图管理','系统设置',1);
	config.additem('缩略图列表',t,'${config:url(pageContext.request)}control/thumbnail/list${config:suffix()}');

}
initConfig();


//初始化执行
initinav('内容管理');



//改变顶部导航字体背景
function editBackground(id,sortname){
	var navigationMenus = document.getElementById("navigationMenu");
	var childs = navigationMenus.childNodes;    
  for(var i = childs.length - 1; i >= 0; i--) {  
		childs[i].className = "nav-list-item";
     
  }  
	//设置当前选中的导航的背景
	document.getElementById(id).className="nav-list-item on";
	//显示左侧导航栏菜单
	config.getLeftTree(sortname);
}		
//退出登录
function submitLogout() {
	var form = document.getElementById("logout")
	form.submit();	
}

//后退
function retreat() {
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		
		var _class = $("#tabId_"+data_id).parent().attr("class");
		if(_class == "cur"){//如果当前窗口选中
			//获取当前窗口的记录路径状态
		//	var isPath = $("#mainFrame_"+data_id).attr("isPath");
			//获取当前窗口的URL路径索引
			var pathIndex = $("#mainFrame_"+data_id).attr("pathIndex");
			var urlValue = urlMap.get(data_id);
			if(urlValue != null){
				for(var i = urlValue.length-1; i>=0; i--){//倒序循环
					var urlPath = urlValue[i];
					if(parseInt(pathIndex) == -1){//第一次后退
						if(i == urlValue.length-2){
						//	alert(urlPath);
						//	$('#mainFrame_'+data_id).attr('src', urlPath);
							document.getElementById('mainFrame_'+data_id).contentWindow.location.href = urlPath;
							//修改记录路径状态
							$("#mainFrame_"+data_id).attr("isPath","false");//设置不记录路径
							//修改URL路径索引
							$("#mainFrame_"+data_id).attr("pathIndex",i);
						}
					}
					if(parseInt(pathIndex) >0){//连续后退
						if(i == parseInt(pathIndex)-1){
						//	$('#mainFrame_'+data_id).attr('src', urlPath);
							document.getElementById('mainFrame_'+data_id).contentWindow.location.href = urlPath;
							//修改记录路径状态
							$("#mainFrame_"+data_id).attr("isPath","false");//设置不记录路径
							//修改URL路径索引
							$("#mainFrame_"+data_id).attr("pathIndex",i);
						}
					}
				}
			}
		
		}
		
	});
//	window.frames['mainFrame'].history.back();
}
//前进
function advance() {
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		
		var _class = $("#tabId_"+data_id).parent().attr("class");
		if(_class == "cur"){//如果当前窗口选中
			//获取当前窗口的URL路径索引
			var pathIndex = $("#mainFrame_"+data_id).attr("pathIndex");
			if(parseInt(pathIndex) != -1){//上一次有"后退"行为
				var urlValue = urlMap.get(data_id);
				if(urlValue != null){
					for(var i = urlValue.length-1; i>=0; i--){//倒序循环
						var urlPath = urlValue[i];
						if(i == parseInt(pathIndex)+1){//可以前进一步
						//	$('#mainFrame_'+data_id).attr('src', urlPath);
							document.getElementById('mainFrame_'+data_id).contentWindow.location.href = urlPath;
							//修改记录路径状态
							$("#mainFrame_"+data_id).attr("isPath","false");//设置不记录路径
							//修改URL路径索引
							$("#mainFrame_"+data_id).attr("pathIndex",i);
						}
					}
				}
			}	
		}
	});
//	window.frames['mainFrame'].history.forward();
}
//刷新 含有锚点的URL必须加上随机数才能刷新
function refreshs() {
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		
		var _class = $("#tabId_"+data_id).parent().attr("class");
		if(_class == "cur"){//如果当前窗口选中
			
			//当前窗口的URL
			var url = document.getElementById("mainFrame_"+data_id).contentWindow.location.href;
			
			//删除锚点
			var anchor = url.lastIndexOf('#');
			if(anchor != -1){
				url = url.substring(0,anchor);
			}
		//	$('#mainFrame_'+data_id).contents().location.href = url;
			document.getElementById("mainFrame_"+data_id).contentWindow.location.href = url;
		//	$('#mainFrame_'+data_id).attr('src', url);
			
			//修改记录路径状态
			$("#mainFrame_"+data_id).attr("isPath","false");//设置不记录路径

		}
		
	});


//	window.frames['mainFrame'].history.go(0);
}



//创建窗口
function createWindow(id,name,url) {
	var flag = false;
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		
	//	console.log(element);
		if(element.id == "mainFrame_"+id){
			$("#"+element.id).show();
			$("#tabId_"+data_id).parent().addClass("cur");
			flag = true;//相同
			//设置窗口高度
			windowHeight(id);
		}else{
			$("#"+element.id).hide();
			$("#tabId_"+data_id).parent().removeClass("cur");
		}
	});
	if(flag == false){
		//生成选项卡
		var tabHtml = "<li class='cur'><a id='tabId_"+id+"' href='javascript:void(0)' hidefocus='true' onclick=\"selectWindow('"+id+"');return false;\">"+name+"</a><i class='close' onclick=\"closeWindow('"+id+"');return false;\">×</i></li>";
		$("#tabBar").append(tabHtml); 
		
		//生成框架   isPath:记录路径状态(是否为"前进/后退"执行的路径)  pathIndex: URL路径索引数组下标 -1为没有执行"前进/后退"命令
		var iframeHtml = "<iframe id='mainFrame_"+id+"' name='mainFrame_"+id+"' data-id='"+id+"' style='width: 100%;' src='"+url+"' frameborder='no' noResize='noresize' onload=\"recordPath('"+id+"',this.contentWindow.location.href)\" isPath='true' pathIndex='-1'></iframe>";
		
		$("#tabFrame").append(iframeHtml); 	
		
		windowHeight(id);
	}
}

//选择窗口
function selectWindow(id) {
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		if(element.id == "mainFrame_"+id){
			$("#"+element.id).show();
			$("#tabId_"+data_id).parent().addClass("cur");
			//设置窗口高度
			windowHeight(id);
		}else{
			$("#"+element.id).hide();
			$("#tabId_"+data_id).parent().removeClass("cur");
		}
	});
}

//设置窗口高度
function windowHeight(id) {
	var height = $(window).height();//浏览器当前窗口可视区域高度
	
	//标签高度
	var tabBar_height = $("#tabBar").height();
	
	$("#mainFrame_"+id).height(height - tabBar_height -3 -60);//60为顶部占用位置
}

//获取当前窗口Id
function currentWindow() {
	//窗口Id
	var windowId = "";
	//获取所有生成的框架
	$("#tabFrame").children().each(function(index,element) {
		var data_id = $("#"+element.id).attr("data-id");
		
		var _class = $("#tabId_"+data_id).parent().attr("class");
		if(_class == "cur"){//如果当前窗口选中
		 	windowId = "mainFrame_"+data_id;
		}
	});
	return windowId;
}


//关闭窗口
function closeWindow(id) {
	//自动选中前一个窗口
	$("#tabFrame").children().each(function(index,element) {
		if(element.id == "mainFrame_"+id){
			var _class = $("#tabId_"+id).parent().attr("class");
			
			var data_id = "";
			if(index >0){
				
				//获取前一个窗口Id
				var before_data_id = $("#tabFrame").children()[index-1].id;
				
				data_id = $("#"+before_data_id).attr("data-id");	
			}else{
				var tabFrame = $("#tabFrame").children()[index+1];
				if(tabFrame != null){
					//获取后一个窗口Id
					data_id = $("#"+tabFrame.id).attr("data-id");
					
				}
			}
			if(data_id != ""){
				//删除
				$("#tabId_"+id).parent().remove();
				$("#mainFrame_"+id).remove();
				
				//删除当前窗口的路径记录
				urlMap.remove(id);
				
				if(_class == "cur"){//如果关闭的窗口当前选中
					//选中窗口
					$("#mainFrame_"+data_id).show();
					$("#tabId_"+data_id).parent().addClass("cur");
				}
				
				//设置窗口高度
				windowHeight(data_id);
			}
		}
	});
}


var urlMap = new Map();//历史路径记录  key:data-id  value: iframe每次重载触发onload事件的URL集合

//记录路径
function recordPath(id,url) {
	//判断是否要记录路径
	var isPath = $("#mainFrame_"+id).attr("isPath");
	if(isPath == "false"){//如果不用记录路径
		$("#mainFrame_"+id).attr("isPath","true");
		return;
	}
	//获取当前窗口的URL路径索引
	var pathIndex = $("#mainFrame_"+id).attr("pathIndex");
	
	var urlValue = urlMap.get(id);
	
	if(parseInt(pathIndex)  != -1){//如果执行过"前进/后退"
		if(urlValue != null){
			for(var i = urlValue.length-1; i>=0; i--){//倒序循环
				var urlPath = urlValue[i];
				//删除"前进/后退"到的URL之后的URL
				if(i > parseInt(pathIndex)){
					urlValue.splice(i, 1);
				}
			}
		}
	}
	if(urlValue != null){
		urlValue.push(url);
	}else{
		var arr = new Array();
		arr.push(url);
		urlMap.put(id,arr);   
	}
	//修改URL路径索引
	$("#mainFrame_"+id).attr("pathIndex","-1");
//	console.log(urlMap);
}

//默认初始添加后台首页
createWindow('home','首页','control/center/home${config:suffix()}');


$(function(){
	//监听标签大小变化
	$('#tabBar').resize(function(){
	
		//获取所有生成的框架
		$("#tabFrame").children().each(function(index,element) {
			var data_id = $("#"+element.id).attr("data-id");
			
			var _class = $("#tabId_"+data_id).parent().attr("class");
			if(_class == "cur"){//如果当前窗口选中
				//设置窗口高度
				windowHeight(data_id);
			}
		});
	
	});
});

</script>
</html>
