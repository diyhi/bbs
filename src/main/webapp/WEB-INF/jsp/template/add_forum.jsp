<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加版块</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>

<script type="text/javascript" src="backstage/js/Tool.js" charset="utf-8"></script>	
<script type="text/javascript" src="backstage/js/Map.js"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/json3.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/js/ImagePreview.js"></script>

<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>
<script language="JavaScript" type="text/javascript">
     //定义了分类二维数组，里面的顺序跟分类的顺序是相同的。通过selectedIndex获得分类的下标值来得到相应的分类数组
     var type=[
     ["话题列表","话题内容","评论列表","标签列表","添加话题","添加评论","引用评论","回复评论","相似话题","话题取消隐藏","修改话题","修改评论","修改评论回复"],
     ["问题列表","问题内容","答案列表","问题标签列表","添加问题","追加问题","添加答案","回复答案","采纳答案","回答总数","相似问题"],
     ["加入收藏夹","话题会员收藏总数","用户是否已经收藏话题","问题会员收藏总数","用户是否已经收藏问题"],
     ["给话题点赞","话题点赞总数","用户是否已经点赞该话题"],
     ["关注用户","关注总数","粉丝总数","是否已经关注该用户"],
     ["会员卡列表","会员卡内容","购买会员卡"],
     ["添加在线留言"],
     ["友情链接列表"],
     ["图片广告"],
     ["在线帮助列表","推荐在线帮助","在线帮助分类","在线帮助导航","在线帮助内容"],
     ["站点栏目列表"],
     ["用户自定义HTML"],
     ["热门搜索词","第三方登录"]
     ];  
     
     function getAllChildForum(){
         //获得一级下拉框的对象
    	var firstLevel=document.forms[0].firstLevel;
         //获得二级下拉框的对象
   //      var sltCity=document.forumForm.city;
		var secondLevel=document.forms[0].secondLevel;

         //得到对应一级的二级数组
        var twoArray=type[firstLevel.selectedIndex - 1];
 
         //清空二级下拉框，仅留提示选项
         secondLevel.length=1;
 
         //解决当选中其它项后选==请选择版块==出错
         if(null != twoArray){
        	//将二级数组中的值填充到二级下拉框中
	         for(var i=0;i<twoArray.length;i++){
	             secondLevel[i+1]=new Option(twoArray[i],twoArray[i]);
	         }
         }
         //选择版块类型 一级节点隐藏域赋值
         document.getElementById('forumType').value= firstLevel.value; 
          
     }
     //保存选中值到隐藏域
     function saveSelectedValue(objs){
     	//选择版块类型 二级节点隐藏域赋值
		document.getElementById('forumChildType').value= objs.value; 
		
		//隐藏所有版块类型表格
		var forumType_div_object = getElementsByName_pseudo("div", "forumType_div");
		for(var i = 0; i < forumType_div_object.length; i++) {
			document.getElementById(forumType_div_object[i].id).style.display="none";//设为隐藏
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
    var lastSelection = "";//上次选择子版块类型参数
     //选择模板显示类型   
	function selectTemplateDisplayType(objs){
		var index = objs.selectedIndex; // 选中索引
		var value = objs.options[index].value; // 选中值
		var stringArray = value.split("_");
		var childType = "";
		var displayType = "单层";//模板显示类型参数 默认是单层
		if(stringArray != null && stringArray.length >1){
			childType = stringArray[1].toLowerCase(); //子版块类型参数  转为小写	
			if(stringArray[2] == "monolayer"){//单层
				displayType = "单层";
				lastSelection = childType;
			}else if(stringArray[2] == "multilayer"){//多层
				displayType = "多层";
				lastSelection = childType;
			}else if(stringArray[2] == "page"){//分页
				displayType = "分页";
				lastSelection = childType;
			}else if(stringArray[2] == "entityBean"){//实体对象
				displayType = "实体对象";
				lastSelection = childType;
			}else if(stringArray[2] == "collection"){//集合
				displayType = "集合";
				lastSelection = childType;
			}
		}
		
		if(value == "请选择"){
			displayType = "请选择";
			childType = lastSelection;
		}
		
		//先关闭所有层
		if(document.getElementById("related_"+childType+"_单层") != null){
			document.getElementById("related_"+childType+"_单层").style.display ="none";
		}
		if(document.getElementById("related_"+childType+"_多层") != null){
			document.getElementById("related_"+childType+"_多层").style.display ="none";
		}
		if(document.getElementById("related_"+childType+"_分页") != null){
			document.getElementById("related_"+childType+"_分页").style.display ="none";
		}
		if(document.getElementById("related_"+childType+"_实体对象") != null){
			document.getElementById("related_"+childType+"_实体对象").style.display ="none";
		}
		if(document.getElementById("related_"+childType+"_集合") != null){
			document.getElementById("related_"+childType+"_集合").style.display ="none";
		}
		if(displayType == "多层"){
			//如果没有层表单则添加一个层表单
			var table = findObj(childType+"Multilayer",document); 
			if(table.rows.length == 0){
				var functions = "add_"+childType+"_Div(null,null)";//函数
				eval(functions); //执行脚本
			}
		}

		//打开选中的层
		if(displayType != "请选择" && document.getElementById("related_"+childType+"_"+displayType) != null){
			
			
			document.getElementById("related_"+childType+"_"+displayType).style.display ="";
		}	
	}
 </script>
 
<!-- AJAX读取版块模板 -->
<script language="JavaScript" type="text/javascript">
	function getForumTemplate(childTypeName){
		var forumTemplate = document.getElementById("forumTemplate");
		var dirName = getUrlParam("dirName");//目录
		
		if(forumTemplate && childTypeName!=""){
			forumTemplate.innerHTML= "数据正在加载...";
			get_request(function(value){
				forumTemplate.innerHTML=value;
				//如果是错误返回，则自动选择
				var errorStatus = document.getElementById("errorStatus").value;
				if(errorStatus == "Error"){//如果有错误
				
					var return_Module = document.getElementById("return_Module").value;
					var module = document.getElementById("module");
					for(var i=0; i<module.length; i++){ 
				        if(module.options[i].value == return_Module){
				        	module.options[i].selected = true; 
				        	//执行onChange里的参数
				        	selectTemplateDisplayType(module);
				        	break;
				        }
				    } 
				}
			},
					 "${config:url(pageContext.request)}control/forum/manage${config:suffix()}?method=forumTemplateFileNameUI&dirName="+dirName+"&childNodeName="+encodeURIComponent(childTypeName)+"&timestamp=" + new Date().getTime(), true);
		}
	}
   
	
</script>
 

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
</script>

<script language="JavaScript" type="text/javascript">
	var more_map=null;

	//查询'更多'
	function queryMore(){
		var forumChildType = document.getElementById('forumChildType').value;//子版块

    	get_request(function(value){
    		if(value != ""){
    			more_map = JSON.parse(value);//JSON转为对象
    			siteMore(forumChildType);
    		}
		},
			"${config:url(pageContext.request)}control/layout/manage${config:suffix()}?method=ajax_more&dirName=${param.dirName}&forumChildType="+encodeURIComponent(encodeURIComponent(forumChildType))+"&timestamp=" + new Date().getTime(), true);
    }
	
	//设置更多
	function siteMore(forumChildType){
		if(more_map != null){
			if(forumChildType == "在线帮助列表"){
				//在线帮助部分--在线帮助--单层
				var more_select = document.getElementById("monolayer_help_more");
				if(more_select.length ==0){//如果还没有值
					var i = 0;
					for(var key in more_map){
						more_select.options[i]=new Option(more_map[key],key);
						i++;
					}
				}
				
			}
		}
		
	}
	
</script>

<!-- 话题分类 -->
<script language="JavaScript" type="text/javascript">
//弹出标签选择层
function showTagPageDiv(tableName){
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择标签",div,700,400);//显示层
	//显示标签
	allTag(tableName);
}

//显示标签
function allTag(tableName){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/tag/manage${config:suffix()}?method=allTag&module=forum&tableName="+tableName+"&timestamp=" + new Date().getTime(), true);
}
//添加标签
function addTag(id,name,tableName){
	document.getElementById(tableName+"_tagId").value= id; 
	document.getElementById(tableName+"_tagName").value = name;
	//关闭层
	systemLayerClose();

}
//取消标签
function cancelTag(tableName){
	document.getElementById(tableName+"_tagId").value= ""; 
	document.getElementById(tableName+"_tagName").value = "";
	//关闭层
	systemLayerClose();
}

//传递参数标签设置
function transferPrameterTagSite(parameter){
	var transferPrameter = document.getElementById(parameter+"_transferPrameter");
	if(transferPrameter.checked){
		document.getElementById(parameter+"_tagName").style.display='none';
		document.getElementById(parameter+"_select").style.display='none';
		document.getElementById(parameter+"_cancel").style.display='none';
	}else{
		document.getElementById(parameter+"_tagName").style.display='';
		document.getElementById(parameter+"_select").style.display='';
		document.getElementById(parameter+"_cancel").style.display='';
	}
}




</script>





<!-- 问题标签 -->
<script language="JavaScript" type="text/javascript">
//弹出标签选择层
function showQuestionTagPageDiv(tableName){
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择标签",div,700,400);//显示层
	//显示标签
	queryQuestionTagPage(1,-1,tableName);
}

//显示标签
function queryQuestionTagPage(pages,parentId,tableName){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/questionTag/manage${config:suffix()}?method=questionTagPage&module=forum&page="+pages+"&parentId="+parentId+"&tableName="+tableName+"&timestamp=" + new Date().getTime(), true);
}

//添加标签
function addQuestionTag(id,name,tableName){
	document.getElementById(tableName+"_tagId").value= id; 
	document.getElementById(tableName+"_tagName").value = name;
	//关闭层
	systemLayerClose();

}
//取消标签
function cancelQuestionTag(tableName){
	document.getElementById(tableName+"_tagId").value= ""; 
	document.getElementById(tableName+"_tagName").value = "";
	//关闭层
	systemLayerClose();
}

//传递参数标签设置
function transferPrameterQuestionTagSite(parameter){
	var transferPrameter = document.getElementById(parameter+"_transferPrameter");
	if(transferPrameter.checked){
		document.getElementById(parameter+"_tagName").style.display='none';
		document.getElementById(parameter+"_select").style.display='none';
		document.getElementById(parameter+"_cancel").style.display='none';
	}else{
		document.getElementById(parameter+"_tagName").style.display='';
		document.getElementById(parameter+"_select").style.display='';
		document.getElementById(parameter+"_cancel").style.display='';
	}
}
//传递过滤条件设置
function transferPrameterFilterConditionSetting(parameter){
	var transferPrameter = document.getElementById(parameter+"_transferPrameter");
	if(transferPrameter.checked){
		document.getElementById(parameter+"_select").style.display='none';
	}else{
		document.getElementById(parameter+"_select").style.display='';
	}

}
</script>







<!-- 广告部分 -->
<script language="JavaScript" type="text/javascript">

//添加图片广告
function add_image_Div(forum_AdvertisingRelated_Image,error_map){
	//从URL中获取模板目录名称
	var dirName = getUrlParam("dirName");
	//表Id
	var tableId = "imageCollection";
	////读取最后一行的行号，存放在productMultilayerIndex文本框中 
	var TRLastIndex = findObj(tableId+"Index",document); 
	var rowID = parseInt(TRLastIndex.value); 
	
	var table = findObj(tableId,document); 
	table.border="0";
	//改变表颜色
	//document.getElementById("SignFrameMany").style.backgroundColor="#B4CFCF";
	//添加行 
	var newTR = table.insertRow(table.rows.length); 
	newTR.id = tableId+"Item" + rowID;
	//alert(rowIDStyle);
	//改变表格颜色
	//document.getElementById(newTR.id).style.backgroundColor="#FFFFFF";

	//添加列 
	var newTD=newTR.insertCell(0);  

tableTxt = "<TABLE class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\" border=\"0\">";
tableTxt+="<TR>";
tableTxt+=	  "<TD class=\"t-button\" colSpan=\"4\" align=\"right\" height=\"20px\">";
tableTxt+=			"<div align=\"right\"><a href=\"javascript:;\" onclick=\"delete_image_Div('imageCollectionItem" + rowID + "')\">删除</a>\&nbsp;&nbsp;\</div>";
tableTxt+=    		"<input type='hidden' name='advertisingRelated_Image_Count' value='"+ rowID +"'>";
tableTxt+=	  "</TD>";
tableTxt+="</TR>";
tableTxt+="<TR>";
tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"16%\">图片名称：</TD>";
tableTxt+=    "<TD class=\"t-content\" width=\"34%\"> ";
tableTxt+=    	"<input type=\"text\" class=\"form-text\" name=\"collection_image_name\" size=\"40\" value=\""+(forum_AdvertisingRelated_Image != null && forum_AdvertisingRelated_Image.image_name != null ? forum_AdvertisingRelated_Image.image_name :"")+"\"/>\&nbsp;\&nbsp;";
tableTxt+=	  "<span class=\"span-text\">"+(error_map != null && error_map.get("collection_image_name_"+(rowID-1)) !=null ? error_map.get("collection_image_name_"+(rowID-1)) :"")+"</span>";
tableTxt+=	  "</TD>";
tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"16%\">图片：</TD>";
tableTxt+=    "<TD class=\"t-content\" width=\"34%\"> ";
tableTxt+=    "<TABLE cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">";
tableTxt+=       "<tr align=\"left\">";
tableTxt+=           "<td width=\"30px\" height=\"30px\">";
tableTxt+=               "<div id=\"preview_"+ rowID +"\"><img id=\"imghead_"+ rowID +"\" width='40px' border='0' src=\""+(forum_AdvertisingRelated_Image != null && forum_AdvertisingRelated_Image.image_fileName != null && forum_AdvertisingRelated_Image.image_fileName != "" ? forum_AdvertisingRelated_Image.image_filePath+dirName+'/'+forum_AdvertisingRelated_Image.image_fileName :"backstage/images/null.gif")+"\"></div>";
tableTxt+=               "<td><input  name=\"collection_image_uploadImage\" type=\"file\" size=\"12\" onchange=\"previewImage(this,'imghead_"+ rowID +"', 'preview_"+ rowID +"')\"/><input type=\"hidden\" name=\"collection_image_path\" value=\""+(forum_AdvertisingRelated_Image != null && forum_AdvertisingRelated_Image.image_fileName != null && forum_AdvertisingRelated_Image.image_fileName != "" ? forum_AdvertisingRelated_Image.image_filePath+dirName+'/'+forum_AdvertisingRelated_Image.image_fileName :"")+"\"/></td>";
tableTxt+=           "</td>";
tableTxt+=       "</tr>";
tableTxt+=    "</TABLE>";
tableTxt+=	              "<span class=\"span-text\">"+(error_map != null && error_map.get("collection_image_path_"+(rowID-1)) !=null ? error_map.get("collection_image_path_"+(rowID-1)) :"")+"</span>";
tableTxt+=	  "</TD>";
tableTxt+="</TR>";
tableTxt+="<TR>";
tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"16%\">图片链接：</TD>";
tableTxt+=    "<TD class=\"t-content\" width=\"84%\" colSpan=\"3\"> ";
tableTxt+=    	"<input type=\"text\" class=\"form-text\" name=\"collection_image_link\" size=\"80\" value=\""+(forum_AdvertisingRelated_Image != null && forum_AdvertisingRelated_Image.image_link != null ? forum_AdvertisingRelated_Image.image_link :"")+"\"/>\&nbsp;\&nbsp;";
tableTxt+=	  "<span class=\"span-text\">"+(error_map != null && error_map.get("collection_image_link_"+(rowID-1)) !=null ? error_map.get("collection_image_link_"+(rowID-1)) :"")+"</span>";
tableTxt+=	  "</TD>";
tableTxt+="</TR>";
tableTxt+="</TABLE>";

						
	//添加列内容 
	newTD.innerHTML = tableTxt;// alert(table.innerHTML);
	//将行号推进下一行 
	TRLastIndex.value = (rowID + 1).toString() ; 
} 
//删除指定行 
function delete_image_Div(rowId){ 
	var table = findObj("imageCollection",document); 
	var item = findObj(rowId,document); 
	 
	//获取将要删除的行的Index 
	var rowIndex = item.rowIndex; 
	 
	if(rowIndex == 0 && table.rows.length == 1){
		alert("至少要保留一个层");
	}else{
		//删除指定Index的行 
		table.deleteRow(rowIndex); 
	}
	//重新排列序号，如果没有序号，这一步省略 
	
	//for(i=rowIndexStyle;i<signFrameStyle.rows.length;i++){ 
	//  signFrameMany.rows[i].cells[0].innerHTML = i.toString(); 
	//} 
}

</script>


<!-- 在线帮助分类 -->
<script language="JavaScript" type="text/javascript">
//弹出在线帮助分类选择层
function showHelpTypePageDiv(tableName){
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择帮助分类",div,700,400);//显示层
	//显示在线帮助分类分页
	helpTypePage(1,0,tableName);
}

//显示在线帮助分类分页
function helpTypePage(pages,parentId,tableName){
	get_request(function(value){$("#divMessage").html(value);},
		 "${config:url(pageContext.request)}control/helpType/manage${config:suffix()}?method=helpTypePageSelect&parentId="+parentId+"&module=forum&page="+pages+"&tableName="+tableName+"&timestamp=" + new Date().getTime(), true);
}
//添加在线帮助分类
function addHelpType(id,name,tableName){
	document.getElementById(tableName+"_helpTypeId").value= id; 
	document.getElementById(tableName+"_helpTypeName").value = name;
	//关闭层
	systemLayerClose();

}
//取消在线帮助分类
function cancelHelpType(tableName){
	document.getElementById(tableName+"_helpTypeId").value= ""; 
	document.getElementById(tableName+"_helpTypeName").value = "";
	//关闭层
	systemLayerClose();
}

//传递参数在线帮助分类设置
function transferPrameterHelpTypeSite(parameter){
	var transferPrameter = document.getElementById(parameter+"_transferPrameter");
	if(transferPrameter.checked){
		document.getElementById(parameter+"_helpTypeName").style.display='none';
		document.getElementById(parameter+"_select").style.display='none';
		document.getElementById(parameter+"_cancel").style.display='none';
	}else{
		document.getElementById(parameter+"_helpTypeName").style.display='';
		document.getElementById(parameter+"_select").style.display='';
		document.getElementById(parameter+"_cancel").style.display='';
	}
}


// 弹出查看在线帮助层
function showHelpPageDiv(tableName){	
	var div = "<div id='divMessage'></div>";
	systemLayerShow("选择帮助",div,700,400);//显示层
	//显示商品分类分页显示	
	helpPage(1,tableName);
}
//显示在线帮助分页
function helpPage(pages,tableName){
	var searchName = "";
	var searchName_str = document.getElementById('searchName');
	if(searchName_str != null){
		searchName = searchName_str.value;
	}
	
	get_request(function(value){
		$("#divMessage").html(value);
		
		//选中已勾选的帮助
		var helpId = document.getElementsByName("help");
		var help_map = allHelpId(tableName);
		for(var i=0;i<helpId.length;i++){
			if(help_map.get(helpId[i].value) != null){
				helpId[i].checked = true;
			}
		}
	},
		"${config:url(pageContext.request)}control/help/manage${config:suffix()}?method=ajax_searchHelpPage&tableName="+tableName+"&searchName="+encodeURIComponent(encodeURIComponent(searchName))+"&page="+pages+"&timestamp=" + new Date().getTime(), true);

}
//选择在线帮助
function selectHelp_checkbox(obj,name,tableName){
	
	if(obj.checked==true){//选中
		add_help_row(tableName,obj.value,name);  
	}else{//取消
		var rowId = document.getElementById(tableName+"_rowId_"+obj.value).value;
		delete_help_row(rowId,tableName);
		
	}
}

//所有在线帮助Id
function allHelpId(tableName){
	var help_map=new Map();//在线帮助  key:在线帮助Id  value:在线帮助名称

	var helpId = document.getElementsByName(tableName+"_helpId");
	for(var i=0;i<helpId.length;i++){
	//	var productName = document.getElementById("productName_"+productId[i].value).value;   
		help_map.put(helpId[i].value,"");   
	}
	return help_map;
}

//添加一行
function add_help_row(tableName,id,name){ //读取最后一行的行号，存放在txtTRLastIndex文本框中 
	var lastIndex = findObj(tableName+"_LastIndex",document); 
	var rowID = parseInt(lastIndex.value); 
	
	var tables = findObj(tableName+"_table",document); 
	//添加行 
	var newTR = tables.insertRow(tables.rows.length); 
	newTR.id = tableName+"_SignItem_" + rowID; 
	
	//添加列:序号 
	var newNameTD=newTR.insertCell(0);
	newNameTD.width="10%"; 
	newNameTD.height="26px";
	newNameTD.align = "center";
	newNameTD.style.cssText="BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom: #BFE3FF 1px dotted;";
	//添加列内容 
	newNameTD.innerHTML = (newTR.rowIndex+1).toString(); 
	
	//添加列:参数名 
	var newNameTD=newTR.insertCell(1); 
	newNameTD.width="80%";
	newNameTD.align = "center";
	newNameTD.style.cssText="BORDER-RIGHT: #BFE3FF 1px dotted;border-bottom: #BFE3FF 1px dotted;";
	//添加列内容 
	newNameTD.innerHTML = "<input type='hidden' name='" + tableName + "_helpId' value='"+id+"' /><input type='hidden' id='" + tableName + "_rowId_"+id+"' value='" + rowID + "' />"+name; 
//	alert("delete_Row('" + tableName + "SignItem" + rowID + "','" + tableName + "','" + id + "')");
	 //monolayer_recommend_productId
	//添加列:删除按钮 
	var newDeleteTD=newTR.insertCell(2); 
	newDeleteTD.width="10%";
	newDeleteTD.style.cssText="border-bottom: #BFE3FF 1px dotted;";
	newDeleteTD.align = "center";
	//添加列内容 
	newDeleteTD.innerHTML = "<a href='javascript:;' onclick=\"delete_help_row('" + rowID + "','" + tableName + "')\">删除</a>"; 
	
	//将行号推进下一行 
	lastIndex.value = (rowID + 1).toString(); 
} 

//删除指定行 
function delete_help_row(rowId,tableName){ 
//	productType_map.remove(id);//删除

	var tables = findObj(tableName+"_table",document); 
	var signItem = findObj(tableName+"_SignItem_"+rowId,document); 
	//获取将要删除的行的Index 
	var rowIndex = signItem.rowIndex; 
	 
	//删除指定Index的行 
	tables.deleteRow(rowIndex); 
	 
	//重新排列序号，如果没有序号，这一步省略 
	for(var i=rowIndex;i<tables.rows.length;i++){ 
		tables.rows[i].cells[0].innerHTML = (i+1).toString(); 
	} 
}


</script>





<!-- 系统  -->
<script language="JavaScript" type="text/javascript">
//热门搜索词 添加参数
function add_searchWord_row(word){
	//读取最后一行的行号，存放在txtTRLastIndex文本框中 
	var lastIndex = findObj("searchWordLastIndex",document); 
	var rowID = parseInt(lastIndex.value); 
	var tables = findObj("searchWordParameter",document); 
	//添加行 
	var newTR = tables.insertRow(tables.rows.length); 
	newTR.id = "searchWordParameterItem" + rowID; 
	newTR.align = "center";

	//添加列:参数名 
	var newNameTD=newTR.insertCell(0); 
	newNameTD.width="70%";
	newNameTD.className ="t-content";
	//添加列内容 
	newNameTD.innerHTML = "<input name='collection_searchWord' type='text' class='form-text' size='22' value='"+word+"'/>"; 
	
	 
	//添加列:删除按钮 
	var newDeleteTD=newTR.insertCell(1); 
	newDeleteTD.width="30%";
	newDeleteTD.align = "left";
	newDeleteTD.className ="t-content";
	
	
	var deleteHtml = "";
	deleteHtml += "<span style=\"margin-left:auto; margin-right:auto;line-height:18px; text-align:center; \" onclick=\"moveUp('searchWordParameter','" + newTR.id + "')\"><img src=\"backstage/images/up_arrows.gif\" width=\"18\" height=\"18\" alt=\"上移\" title=\"上移\" /></span>";
	deleteHtml += "<span style=\"margin-left:auto; margin-right:auto;line-height:18px; text-align:center; \" onclick=\"moveDown('searchWordParameter','" + newTR.id + "')\"><img src=\"backstage/images/down_arrows.gif\" width=\"18\" height=\"18\" alt=\"下移\" title=\"下移\" /></span>";
	deleteHtml += "<span style=\"margin-left:auto; margin-right:auto;line-height:18px; text-align:center; \" onclick=\"delete_searchWord_row('" + newTR.id + "')\"><img src=\"backstage/images/x.gif\" width=\"18\" height=\"18\" alt=\"删除\" title=\"删除\" /></span>";
	deleteHtml += "";
	newDeleteTD.innerHTML = deleteHtml;
	
	//将行号推进下一行 
	lastIndex.value = (rowID + 1).toString(); 
} 

//删除指定行 
function delete_searchWord_row(rowId){ 
	var tables = findObj("searchWordParameter",document); 
	var signItem = findObj(rowId,document); 
	 
	//获取将要删除的行的Index 
	var rowIndex = signItem.rowIndex; 
	 
	//删除指定Index的行 
	tables.deleteRow(rowIndex); 

}
</script>










<script language="JavaScript" type="text/javascript">
	function sureSubmit(objForm){
		//按钮设置 disabled="disabled"
		document.getElementById("submitForm").disabled=true;
		objForm.submit();
	} 
</script>
</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="forum" method="post" enctype="multipart/form-data">

<input type="hidden" id="errorStatus" value="${errorStatus}"/>
<enhance:out escapeXml="false">
<input type="hidden" id="more" value="<c:out value="${more}"></c:out>"/></enhance:out>
<DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="28px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">

			&nbsp;>>&nbsp;<a href="control/layout/list${config:suffix()}?dirName=${param.dirName}">${templates.name}<span style="color: red">[${param.dirName }]</span> </a>

			&nbsp;>>&nbsp;<a href="control/forum/list${config:suffix()}?layoutId=${param.layoutId}&dirName=${param.dirName}">${layout.name}</a>
			&nbsp;>> &nbsp;添加版块
			
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 


	<DIV class="d-head">
	<DIV class="d-location">
		选择版块类型：
		<SELECT class="form-select" id="firstLevel" onChange="getAllChildForum();" >
			<OPTION VALUE="0">==请选择版块==</OPTION>
			<OPTION VALUE="话题">话题</OPTION>
			<OPTION VALUE="问答">问答</OPTION>
			<OPTION VALUE="收藏夹">收藏夹</OPTION>
			<OPTION VALUE="点赞">点赞</OPTION>
			<OPTION VALUE="关注">关注</OPTION>
			<OPTION VALUE="会员卡">会员卡</OPTION>
			<OPTION VALUE="在线留言">在线留言</OPTION>
			<OPTION VALUE="友情链接">友情链接</OPTION>
            <OPTION VALUE="广告">广告</OPTION>
            <OPTION VALUE="在线帮助">在线帮助</OPTION>
            <OPTION VALUE="站点栏目">站点栏目</OPTION>
            <OPTION VALUE="自定义版块">自定义版块</OPTION>
            <OPTION VALUE="系统">系统</OPTION>
	    </SELECT>
	    <input type="hidden" id="forumType" name="forumType" value="${forum.forumType}"/>
	    <SELECT class="form-select" id="secondLevel" onChange="saveSelectedValue(this);getForumTemplate(this.value);queryMore();">
	        <OPTION VALUE="0">==请选择子版块 ==</OPTION>
	    </SELECT>
	</DIV>
	<input type="hidden" id="forumChildType" name="forumChildType" value="${forum.forumChildType}"/>
	<DIV class="d-clear"></DIV>
	</DIV> 
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
	 	<TR>
	 		<TD class="t-label t-label-h" width="16%"><SPAN class="span-text">*</SPAN>版块标题：</TD>
		    <TD class="t-content" width="84%" colSpan="5">
		    	<input name="name" type="text" class="form-text" size="30" maxlength="30" value="${forum.name }"/>&nbsp;&nbsp;<web:errors path="name" cssClass="span-text"/>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="16%"><SPAN class="span-text">*</SPAN>版块模板：</TD>
		    <TD class="t-content" width="84%" colSpan="5">	
		    	<div id="forumTemplate">
		    	
				</div>
				<web:errors path="module" cssClass="span-text"/>
				<input type="hidden" id="return_Module" value="${forum.module}"/>
			</TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="16%"><SPAN class="span-text">*</SPAN>调用方式：</TD>
		    <TD class="t-content" width="84%" colSpan="5">	
		    	<c:set var="invokeMethod_disabled"  value=""></c:set>
		   		<c:if test="${(layout.type == 4 && layout.returnData == 1) || layout.type == 6}">
		   			<c:set var="invokeMethod_disabled"  value=" disabled='disabled'"></c:set>
		   		</c:if>
		    	<label><input type="radio" name="invokeMethod" value="1" <c:if test="${forum.invokeMethod == 1}"> checked='checked'</c:if> ${invokeMethod_disabled}>引用代码</label>
		    	<label><input type="radio" name="invokeMethod" value="2" <c:if test="${forum.invokeMethod == 2}"> checked='checked'</c:if> ${invokeMethod_disabled}>调用对象</label>
				<web:errors path="invokeMethod" cssClass="span-text"/>
				<c:if test='${(layout.type == 4 && layout.returnData == 1) || layout.type == 6}'>
					<SPAN class="span-help">空白页json方式返回数据或公共页(引用版块值) 这两种布局方式不能选择'调用对象'</SPAN>
				</c:if>
			</TD>
		</TR>
		</TBODY>
	</TABLE>
	<enhance:out escapeXml="false">
	<input type="hidden" id="forumError_Json" value="<c:out value="${forumError}"></c:out>"/>
	</enhance:out>

	<!-- 话题部分--话题列表  分页-->
	<div id="related_topic_分页" name="forumType_div" style="DISPLAY: none">
		<enhance:out escapeXml="false">
		<input type="hidden" id="page_Forum_TopicRelated_Topic_Json" value="<c:out value="${page_Forum_TopicRelated_Topic}"></c:out>">
		</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
		 		<TR>
					<TD class="t-label t-label-h" width="16%">选择标签：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<input type="hidden" id="page_topic_tag_tagId" name="page_topic_tagId" value=""/>
	    				<input type="text" class="form-text" id="page_topic_tag_tagName" disabled="true" size="20" value=""/> 
	    				<input type="button" class="functionButton5" id="page_topic_tag_select"  value="选择..." onClick="showTagPageDiv('page_topic_tag');">
						<input type="button" class="functionButton5" id="page_topic_tag_cancel" onclick="cancelTag('page_topic_tag');" value="取消标签" >
						<label><input type="checkbox" id="page_topic_tag_transferPrameter" name="page_topic_tag_transferPrameter" onclick="transferPrameterTagSite('page_topic_tag');" value="true" >传递标签参数</label>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">排序：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<SELECT class="form-select" id=page_topic_sort name=page_topic_sort>
						<OPTION value="1" >按发表时间 新-&gt;旧</OPTION>
						<OPTION value="2" >按发表时间 旧-&gt;新</OPTION>
						<OPTION value="3" >按回复时间 新-&gt;旧</OPTION>
						<OPTION value="4" >按回复时间 旧-&gt;新</OPTION>
						</SELECT>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">每页显示记录数：</TD>
					<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=page_topic_maxResult name=page_topic_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_page_topic_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">页码显示总数：</TD>
					<TD class=t-content width="84%" colSpan=3>
					<INPUT class="form-text" maxLength=9 size=9 id=page_topic_pageCount name=page_topic_pageCount value="">
					&nbsp;&nbsp;
					<SPAN id="error_page_topic_pageCount" class="span-text"></SPAN>
					</TD>
				</TR>
				
			</TBODY>
		</TABLE>
	</div>
	
	<!-- 话题部分--相似话题  集合-->
	<div id="related_liketopic_集合" name="forumType_div" style="DISPLAY: none">
		<enhance:out escapeXml="false">
		<input type="hidden" id="collection_Forum_TopicRelated_LikeTopic_Json" value="<c:out value="${collection_Forum_TopicRelated_LikeTopic}"></c:out>">
		</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
				<TR>
					<TD class="t-label t-label-h" width="16%">显示记录数：</TD>
					<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=collection_likeTopic_maxResult name=collection_likeTopic_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_collection_likeTopic_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					</TD>
				</TR>	
			</TBODY>
		</TABLE>
	</div>
	
	
	<!-- 评论部分--评论列表  分页-->
	<div id="related_comment_分页" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="page_Forum_CommentRelated_Comment_Json" value="<c:out value="${page_Forum_CommentRelated_Comment}"></c:out>">
	</enhance:out>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
			<TR>
				<TD class="t-label t-label-h" width="16%">排序：</TD>
				<TD class=t-content width="84%" colSpan=3>
					<SELECT class="form-select" id=page_comment_sort name=page_comment_sort>
					<OPTION value="1" >按发布时间 新-&gt;旧</OPTION>
					<OPTION value="2" >按发布时间 旧-&gt;新</OPTION>
					</SELECT>
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">每页显示记录数：</TD>
				<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=page_comment_maxResult name=page_comment_maxResult value="">
				&nbsp;&nbsp;
				<SPAN id= "error_page_comment_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">页码显示总数：</TD>
				<TD class=t-content width="84%" colSpan=3>
				<INPUT class="form-text" maxLength=9 size=9 id=page_comment_pageCount name=page_comment_pageCount value="">
				&nbsp;&nbsp;
				<SPAN id="error_page_comment_pageCount" class="span-text"></SPAN>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
	</div>
	
	<!-- 问题部分--问题列表  分页-->
	<div id="related_question_分页" name="forumType_div" style="DISPLAY: none">
		<enhance:out escapeXml="false">
		<input type="hidden" id="page_Forum_QuestionRelated_Question_Json" value="<c:out value="${page_Forum_QuestionRelated_Question}"></c:out>">
		</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
		 		<TR>
					<TD class="t-label t-label-h" width="16%">选择标签：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<input type="hidden" id="page_question_tag_tagId" name="page_question_tagId" value=""/>
	    				<input type="text" class="form-text" id="page_question_tag_tagName" disabled="true" size="20" value=""/> 
	    				<input type="button" class="functionButton5" id="page_question_tag_select"  value="选择..." onClick="showQuestionTagPageDiv('page_question_tag');">
						<input type="button" class="functionButton5" id="page_question_tag_cancel" onclick="cancelQuestionTag('page_question_tag');" value="取消标签" >
						<label><input type="checkbox" id="page_question_tag_transferPrameter" name="page_question_tag_transferPrameter" onclick="transferPrameterQuestionTagSite('page_question_tag');" value="true" >传递标签参数</label>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">过滤条件：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<select class="form-select" id=page_question_filterCondition_select name=page_question_filterCondition>
							<option value="10" >全部</option>
							<option value="20" >未解决</option>
							<option value="30" >已解决</option>
							<option value="40" >积分悬赏</option>
							<option value="50" >现金悬赏</option>
						</select>
						<label><input type="checkbox" id="page_question_filterCondition_transferPrameter" name="page_question_filterCondition_transferPrameter" onclick="transferPrameterFilterConditionSetting('page_question_filterCondition');" value="true" >传递过滤参数</label>
						<SPAN class="span-help">选择'传递过滤参数'时接收参数字段为filterCondition，全部：值为空或10 &nbsp; 未解决：20 &nbsp; 已解决：30 &nbsp; 积分悬赏：40 &nbsp; 现金悬赏：50</SPAN>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">排序：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<SELECT class="form-select" id=page_question_sort name=page_question_sort>
						<OPTION value="1" >按发表时间 新-&gt;旧</OPTION>
						<OPTION value="2" >按发表时间 旧-&gt;新</OPTION>
						<OPTION value="3" >按回答时间 新-&gt;旧</OPTION>
						<OPTION value="4" >按回答时间 旧-&gt;新</OPTION>
						</SELECT>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">每页显示记录数：</TD>
					<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=page_question_maxResult name=page_question_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_page_question_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">页码显示总数：</TD>
					<TD class=t-content width="84%" colSpan=3>
					<INPUT class="form-text" maxLength=9 size=9 id=page_question_pageCount name=page_question_pageCount value="">
					&nbsp;&nbsp;
					<SPAN id="error_page_question_pageCount" class="span-text"></SPAN>
					</TD>
				</TR>
				
			</TBODY>
		</TABLE>
	</div>
	<!-- 答案部分--答案列表  分页-->
	<div id="related_answer_分页" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="page_Forum_AnswerRelated_Answer_Json" value="<c:out value="${page_Forum_AnswerRelated_Answer}"></c:out>">
	</enhance:out>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
			<TR>
				<TD class="t-label t-label-h" width="16%">排序：</TD>
				<TD class=t-content width="84%" colSpan=3>
					<SELECT class="form-select" id=page_answer_sort name=page_answer_sort>
					<OPTION value="1" >按回答时间 新-&gt;旧</OPTION>
					<OPTION value="2" >按回答时间 旧-&gt;新</OPTION>
					</SELECT>
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">每页显示记录数：</TD>
				<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=page_answer_maxResult name=page_answer_maxResult value="">
				&nbsp;&nbsp;
				<SPAN id= "error_page_answer_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">页码显示总数：</TD>
				<TD class=t-content width="84%" colSpan=3>
				<INPUT class="form-text" maxLength=9 size=9 id=page_answer_pageCount name=page_answer_pageCount value="">
				&nbsp;&nbsp;
				<SPAN id="error_page_answer_pageCount" class="span-text"></SPAN>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
	</div>
	<!-- 问题部分--相似问题  集合-->
	<div id="related_likequestion_集合" name="forumType_div" style="DISPLAY: none">
		<enhance:out escapeXml="false">
		<input type="hidden" id="collection_Forum_QuestionRelated_LikeQuestion_Json" value="<c:out value="${collection_Forum_QuestionRelated_LikeQuestion}"></c:out>">
		</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
				<TR>
					<TD class="t-label t-label-h" width="16%">显示记录数：</TD>
					<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=collection_likeQuestion_maxResult name=collection_likeQuestion_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_collection_likeQuestion_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					</TD>
				</TR>	
			</TBODY>
		</TABLE>
	</div>
	
	<!-- 广告部分--图片广告 -->
	<div id="related_image_集合" name="forumType_div" style="DISPLAY: none">
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
			<TR>
				<TD class="t-label t-label-h" width="16%">图片：</TD>
			    <TD class="t-content" width="84%" colSpan="5">	
			    	<input type="button" class="functionButton5" value="添加一个图片" onClick="add_image_Div(null,null)" />
				</TD>
			</TR>
			</TBODY>
		</TABLE>
		<enhance:out escapeXml="false">
		<input type="hidden" id="collection_Forum_AdvertisingRelated_ImageList_Json" value="<c:out value="${collection_Forum_AdvertisingRelated_imageList}"></c:out>">
		</enhance:out>
     	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="imageCollection">  
     		
     	</table>
     	<input type='hidden' id='imageCollectionIndex' value="1" /> 
	</div>
	
	<!-- 在线帮助部分--在线帮助列表  单层-->
	<div id="related_help_单层" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="monolayer_Forum_HelpRelated_Help_Json" value="<c:out value="${monolayer_Forum_HelpRelated_Help}"></c:out>">
	</enhance:out>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
	 		<TR>
				<TD class="t-label t-label-h" width="16%">选择在线帮助分类：</TD>
				<TD class=t-content width="84%" colSpan=3>
					<input type="hidden" id="monolayer_help_helpType_helpTypeId" name="monolayer_help_helpTypeId" value=""/>
    				<input type="text" class="form-text" id="monolayer_help_helpType_helpTypeName" disabled="true" size="20" value=""/> 
    				<input type="button" class="functionButton5" id="monolayer_help_helpType_select"  value="选择..." onClick="showHelpTypePageDiv('monolayer_help_helpType');">
					<input type="button" class="functionButton5" id="monolayer_help_helpType_cancel" onclick="cancelHelpType('monolayer_help_helpType');" value="取消在线帮助分类" >
					<label><input type="checkbox" id="monolayer_help_helpType_transferPrameter" name="monolayer_help_helpType_transferPrameter" onclick="transferPrameterHelpTypeSite('monolayer_help_helpType');" value="true" >传递在线帮助分类参数</label>
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">商品展示数量：</TD>
				<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=monolayer_help_quantity name=monolayer_help_quantity value="">
				&nbsp;&nbsp;
				<SPAN id= "error_monolayer_help_quantity" class="span-text"></SPAN>
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">更多：</TD>
				<TD class=t-content width="84%" colSpan=3>
					<select class="form-select" id="monolayer_help_more" name="monolayer_help_more"></select>
					
					&nbsp;&nbsp;每页显示记录数：<INPUT class="form-text" maxLength=9 size=9 id=monolayer_help_maxResult name=monolayer_help_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_monolayer_help_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					页码显示总数：<INPUT class="form-text" maxLength=9 size=9 id=monolayer_help_pageCount name=monolayer_help_pageCount value="">
					&nbsp;&nbsp;
					<SPAN id="error_monolayer_help_pageCount" class="span-text"></SPAN>
				</TD>
			</TR>
			<TR>
				<TD class="t-label t-label-h" width="16%">排序：</TD>
				<TD class=t-content width="84%" colSpan=3>
					<SELECT class="form-select" id=monolayer_help_sort name=monolayer_help_sort>
					<OPTION value="1" >按发布时间 新-&gt;旧</OPTION>
					<OPTION value="2" >按发布时间 旧-&gt;新</OPTION>
					</SELECT>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
	</div>
	<!-- 在线帮助部分--在线帮助列表  分页-->
	<div id="related_help_分页" name="forumType_div" style="DISPLAY: none">
		<enhance:out escapeXml="false">
		<input type="hidden" id="page_Forum_HelpRelated_Help_Json" value="<c:out value="${page_Forum_HelpRelated_Help}"></c:out>">
		</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
		 		<TR>
					<TD class="t-label t-label-h" width="16%">选择在线帮助分类：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<input type="hidden" id="page_help_helpType_helpTypeId" name="page_help_helpTypeId" value=""/>
	    				<input type="text" class="form-text" id="page_help_helpType_helpTypeName" disabled="true" size="20" value=""/> 
	    				<input type="button" class="functionButton5" id="page_help_helpType_select"  value="选择..." onClick="showHelpTypePageDiv('page_help_helpType');">
						<input type="button" class="functionButton5" id="page_help_helpType_cancel" onclick="cancelHelpType('page_help_helpType');" value="取消在线帮助分类" >
						<label><input type="checkbox" id="page_help_helpType_transferPrameter" name="page_help_helpType_transferPrameter" onclick="transferPrameterHelpTypeSite('page_help_helpType');" value="true" >传递在线帮助分类参数</label>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">排序：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<SELECT class="form-select" id=page_help_sort name=page_help_sort>
						<OPTION value="1" >按发布时间 新-&gt;旧</OPTION>
						<OPTION value="2" >按发布时间 旧-&gt;新</OPTION>
						</SELECT>
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">每页显示记录数：</TD>
					<TD class=t-content width="84%" colSpan=3><INPUT class="form-text" maxLength=9 size=9 id=page_help_maxResult name=page_help_maxResult value="">
					&nbsp;&nbsp;
					<SPAN id= "error_page_help_maxResult" class="span-text"></SPAN>&nbsp;&nbsp;
					</TD>
				</TR>
				<TR>
					<TD class="t-label t-label-h" width="16%">页码显示总数：</TD>
					<TD class=t-content width="84%" colSpan=3>
					<INPUT class="form-text" maxLength=9 size=9 id=page_help_pageCount name=page_help_pageCount value="">
					&nbsp;&nbsp;
					<SPAN id="error_page_help_pageCount" class="span-text"></SPAN>
					</TD>
				</TR>
				
			</TBODY>
		</TABLE>
	</div>
	<!-- 在线帮助部分--推荐在线帮助  集合-->
	<div id="related_recommendhelp_集合" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="collection_Forum_HelpRelated_RecommendHelp_Json" value="<c:out value="${collection_Forum_HelpRelated_RecommendHelp}"></c:out>">
	</enhance:out>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
			<TR>
				<TD class="t-label t-label-h" width="16%">选择推荐在线帮助：</TD>
				<TD class="t-content" width="84%" colSpan="3">
			    	<INPUT type="button" class="functionButton5" value="添加在线帮助" onclick="showHelpPageDiv('collection_recommendHelp')">
			    	<TABLE id="collection_recommendHelp_table" cellSpacing="2" cellPadding="0" width="99%"  border="0" align="left">
					</TABLE>
		    		<input type='hidden' id='collection_recommendHelp_LastIndex' value="1" /> 
			    </TD>
			 </TR>
		</TBODY>
	</TABLE>
	</div>
	
	
	
	<!-- 自定义版块 -- 用户自定义HTML--实体对象 -->
	<div id="related_customhtml_实体对象" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="entityBean_Forum_CustomForumRelated_CustomHTML_Json" value="<c:out value="${entityBean_Forum_CustomForumRelated_CustomHTML}"></c:out>">
	</enhance:out>
		<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		 	<TBODY>
				<TR>
					<TD class="t-label t-label-h" width="16%">内容：</TD>
					<TD class=t-content width="84%" colSpan=3>
						<textarea id="entityBean_customForum_htmlContent" name="entityBean_customForum_htmlContent" style="width:99%;height:300px;visibility:hidden;"></textarea>
						<SPAN id="error_page_consult_pageCount" class="span-text"></SPAN>
					</TD>
				</TR>	
			</TBODY>
		</TABLE>
	</div>
	
	
	
	
	<!-- 系统--热门搜索词  集合-->
	<div id="related_searchword_集合" name="forumType_div" style="DISPLAY: none">
	<enhance:out escapeXml="false">
	<input type="hidden" id="collection_Forum_SystemRelated_SearchWord_Json" value="<c:out value="${collection_Forum_SystemRelated_SearchWord}"></c:out>">
	</enhance:out>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	 	<TBODY>
			<TR>
				<TD class="t-label t-label-h" width="16%">搜索词：</TD>
				<TD class="t-content" width="84%" colSpan="3">
			    	<INPUT type="button" class="functionButton5" value="添加行" onclick="add_searchWord_row('');">
			    	<input type="hidden" id="searchWordLastIndex" value="1"/>
			    	<table id="searchWordParameter" width="300px" border="0" cellpadding="2" cellspacing="1" align="left">
			    	
			    	</table>
			    </TD>
			 </TR>
		</TBODY>
	</TABLE>
	</div>
	<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
		    <TD class="t-button" colSpan="6">
		  		<span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span> 		
		  	</TD>
	  	</TR>
	</TABLE>  	
</div>	
	
		


</form:form>
<!-- 错误返回时自动选择 -->
<script language="JavaScript" type="text/javascript">
//选择版块类型
function selectForumType(){
	var forumType = document.getElementById("forumType").value; //一级版块类型参数
	var forumChildType = document.getElementById("forumChildType").value; //二级版块类型参数
	//选择一级节点
	var firstLevel = document.getElementById("firstLevel"); 
    for(var i=0; i<firstLevel.length; i++){ 
        if(firstLevel.options[i].value == forumType){
        	firstLevel.options[i].selected = true; 
        	//执行onChange里的参数
        	getAllChildForum();
        }
    } 
    //选择二级节点
    var secondLevel = document.getElementById("secondLevel"); 
    for(var i=0; i<secondLevel.length; i++){ 
        if(secondLevel.options[i].value == forumChildType){
        	secondLevel.options[i].selected = true; 
        	//执行onChange里的参数
        	getForumTemplate(secondLevel.options[i].value);
        }
    }
}

//回显  话题部分--话题列表--分页
function echo_topic_page(error_map){
	var page_Forum_TopicRelated_Topic_Json = document.getElementById("page_Forum_TopicRelated_Topic_Json").value;
	if(page_Forum_TopicRelated_Topic_Json != ""){
		var page_Forum_TopicRelated_Topic = JSON.parse(page_Forum_TopicRelated_Topic_Json);
		if(page_Forum_TopicRelated_Topic != null){
			//标签Id
			if(page_Forum_TopicRelated_Topic.topic_tagId != null){
				document.getElementById("page_topic_tag_tagId").value = page_Forum_TopicRelated_Topic.topic_tagId;
			}
			//标签名称
			if(page_Forum_TopicRelated_Topic.topic_tagName != null){
				document.getElementById("page_topic_tag_tagName").value = page_Forum_TopicRelated_Topic.topic_tagName;
			}
			//是否传递标签参数
			if(page_Forum_TopicRelated_Topic.topic_tag_transferPrameter == true){
				//选中
				document.getElementById("page_topic_tag_transferPrameter").checked = true;
				transferPrameterTagSite('page_topic_tag');
			}

			//每页显示记录数
			if(page_Forum_TopicRelated_Topic.topic_maxResult != null){
				document.getElementById("page_topic_maxResult").value = page_Forum_TopicRelated_Topic.topic_maxResult;
			}
			//页码显示总数
			if(page_Forum_TopicRelated_Topic.topic_pageCount != null){
				document.getElementById("page_topic_pageCount").value = page_Forum_TopicRelated_Topic.topic_pageCount;
			}
			
			//排序
			var page_topic_sort = document.getElementById("page_topic_sort");
			for(var i =0; i<page_topic_sort.length; i++){    
				if(page_topic_sort[i].value == page_Forum_TopicRelated_Topic.topic_sort){
					page_topic_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("page_topic_maxResult") != null){
				document.getElementById("error_page_topic_maxResult").innerHTML = error_map.get("page_topic_maxResult");
			}
			//页码显示总数
			if(error_map.get("page_topic_pageCount") != null){
				document.getElementById("error_page_topic_pageCount").innerHTML = error_map.get("page_topic_pageCount");
			}
		}
	}
	
}

//回显  话题部分--相似话题--集合
function echo_likeTopic_collection(error_map){
	var collection_Forum_TopicRelated_LikeTopic_Json = document.getElementById("collection_Forum_TopicRelated_LikeTopic_Json").value;
	if(collection_Forum_TopicRelated_LikeTopic_Json != ""){
		var collection_Forum_TopicRelated_LikeTopic = JSON.parse(collection_Forum_TopicRelated_LikeTopic_Json);
		if(collection_Forum_TopicRelated_LikeTopic != null){
			
			//显示记录数
			if(collection_Forum_TopicRelated_LikeTopic.likeTopic_maxResult != null){
				document.getElementById("collection_likeTopic_maxResult").value = collection_Forum_TopicRelated_LikeTopic.likeTopic_maxResult;
			}
			
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("collection_likeTopic_maxResult") != null){
				document.getElementById("error_collection_likeTopic_maxResult").innerHTML = error_map.get("collection_likeTopic_maxResult");
			}
		}
	}


}




//回显  评论部分--评论列表--集合
function echo_comment_page(error_map){
	var page_Forum_CommentRelated_Comment_Json = document.getElementById("page_Forum_CommentRelated_Comment_Json").value;
	
	if(page_Forum_CommentRelated_Comment_Json != ""){
		
		var page_Forum_CommentRelated_Comment = JSON.parse(page_Forum_CommentRelated_Comment_Json);
		
		if(page_Forum_CommentRelated_Comment != null){

			//每页显示记录数
			if(page_Forum_CommentRelated_Comment.comment_maxResult != null){
				document.getElementById("page_comment_maxResult").value = page_Forum_CommentRelated_Comment.comment_maxResult;
			}
			//页码显示总数
			if(page_Forum_CommentRelated_Comment.comment_pageCount != null){
				document.getElementById("page_comment_pageCount").value = page_Forum_CommentRelated_Comment.comment_pageCount;
			}
			
			//排序
			var page_comment_sort = document.getElementById("page_comment_sort");
			for(var i =0; i<page_comment_sort.length; i++){    
				if(page_comment_sort[i].value == page_Forum_CommentRelated_Comment.comment_sort){
					page_comment_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("page_comment_maxResult") != null){
				document.getElementById("error_page_comment_maxResult").innerHTML = error_map.get("page_comment_maxResult");
			}
			//页码显示总数
			if(error_map.get("page_comment_pageCount") != null){
				document.getElementById("error_page_comment_pageCount").innerHTML = error_map.get("page_comment_pageCount");
			}
		}
	}
	
}

//回显  问题部分--问题列表--分页
function echo_question_page(error_map){
	var page_Forum_QuestionRelated_Question_Json = document.getElementById("page_Forum_QuestionRelated_Question_Json").value;
	if(page_Forum_QuestionRelated_Question_Json != ""){
		var page_Forum_QuestionRelated_Question = JSON.parse(page_Forum_QuestionRelated_Question_Json);
		if(page_Forum_QuestionRelated_Question != null){
			//标签Id
			if(page_Forum_QuestionRelated_Question.question_tagId != null){
				document.getElementById("page_question_tag_tagId").value = page_Forum_QuestionRelated_Question.question_tagId;
			}
			//标签名称
			if(page_Forum_QuestionRelated_Question.question_tagName != null){
				document.getElementById("page_question_tag_tagName").value = page_Forum_QuestionRelated_Question.question_tagName;
			}
			//是否传递标签参数
			if(page_Forum_QuestionRelated_Question.question_tag_transferPrameter == true){
				//选中
				document.getElementById("page_question_tag_transferPrameter").checked = true;
				transferPrameterTagSite('page_question_tag');
			}
			//过滤条件
			var page_question_filterCondition = document.getElementById("page_question_filterCondition_select");
			for(var i =0; i<page_question_filterCondition.length; i++){    
				if(page_question_filterCondition[i].value == page_Forum_QuestionRelated_Question.question_filterCondition){
					page_question_filterCondition[i].selected = true;
				}   
    		}
			
			//是否传递过滤条件参数
			if(page_Forum_QuestionRelated_Question.question_filterCondition_transferPrameter == true){
				//选中
				document.getElementById("page_question_filterCondition_transferPrameter").checked = true;
				transferPrameterFilterConditionSetting('page_question_filterCondition');
			}

			//每页显示记录数
			if(page_Forum_QuestionRelated_Question.question_maxResult != null){
				document.getElementById("page_question_maxResult").value = page_Forum_QuestionRelated_Question.question_maxResult;
			}
			//页码显示总数
			if(page_Forum_QuestionRelated_Question.question_pageCount != null){
				document.getElementById("page_question_pageCount").value = page_Forum_QuestionRelated_Question.question_pageCount;
			}
			
			//排序
			var page_question_sort = document.getElementById("page_question_sort");
			for(var i =0; i<page_question_sort.length; i++){    
				if(page_question_sort[i].value == page_Forum_QuestionRelated_Question.question_sort){
					page_question_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("page_question_maxResult") != null){
				document.getElementById("error_page_question_maxResult").innerHTML = error_map.get("page_question_maxResult");
			}
			//页码显示总数
			if(error_map.get("page_question_pageCount") != null){
				document.getElementById("error_page_question_pageCount").innerHTML = error_map.get("page_question_pageCount");
			}
		}
	}
	
}
//回显  答案部分--答案列表--集合
function echo_answer_page(error_map){
	var page_Forum_AnswerRelated_Answer_Json = document.getElementById("page_Forum_AnswerRelated_Answer_Json").value;
	
	if(page_Forum_AnswerRelated_Answer_Json != ""){
		
		var page_Forum_AnswerRelated_Answer = JSON.parse(page_Forum_AnswerRelated_Answer_Json);
		
		if(page_Forum_AnswerRelated_Answer != null){

			//每页显示记录数
			if(page_Forum_AnswerRelated_Answer.answer_maxResult != null){
				document.getElementById("page_answer_maxResult").value = page_Forum_AnswerRelated_Answer.answer_maxResult;
			}
			//页码显示总数
			if(page_Forum_AnswerRelated_Answer.answer_pageCount != null){
				document.getElementById("page_answer_pageCount").value = page_Forum_AnswerRelated_Answer.answer_pageCount;
			}
			
			//排序
			var page_answer_sort = document.getElementById("page_answer_sort");
			for(var i =0; i<page_answer_sort.length; i++){    
				if(page_answer_sort[i].value == page_Forum_AnswerRelated_Answer.answer_sort){
					page_answer_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("page_answer_maxResult") != null){
				document.getElementById("error_page_answer_maxResult").innerHTML = error_map.get("page_answer_maxResult");
			}
			//页码显示总数
			if(error_map.get("page_answer_pageCount") != null){
				document.getElementById("error_page_answer_pageCount").innerHTML = error_map.get("page_answer_pageCount");
			}
		}
	}
	
}

//回显  问题部分--相似问题--集合
function echo_likeQuestion_collection(error_map){
	var collection_Forum_QuestionRelated_LikeQuestion_Json = document.getElementById("collection_Forum_QuestionRelated_LikeQuestion_Json").value;
	if(collection_Forum_QuestionRelated_LikeQuestion_Json != ""){
		var collection_Forum_QuestionRelated_LikeQuestion = JSON.parse(collection_Forum_QuestionRelated_LikeQuestion_Json);
		if(collection_Forum_QuestionRelated_LikeQuestion != null){
			
			//显示记录数
			if(collection_Forum_QuestionRelated_LikeQuestion.likeQuestion_maxResult != null){
				document.getElementById("collection_likeQuestion_maxResult").value = collection_Forum_QuestionRelated_LikeQuestion.likeQuestion_maxResult;
			}
			
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("collection_likeQuestion_maxResult") != null){
				document.getElementById("error_collection_likeQuestion_maxResult").innerHTML = error_map.get("collection_likeQuestion_maxResult");
			}
		}
	}


}

//回显  广告部分--轮播广告--单层
function echo_image_collection(error_map){

	//读取回显参数
	var collection_Forum_AdvertisingRelated_imageList_Json = document.getElementById("collection_Forum_AdvertisingRelated_ImageList_Json").value;
	if(collection_Forum_AdvertisingRelated_imageList_Json != ""){
		
		
		var collection_Forum_AdvertisingRelated_imageList = JSON.parse(collection_Forum_AdvertisingRelated_imageList_Json);
		if(collection_Forum_AdvertisingRelated_imageList != null && collection_Forum_AdvertisingRelated_imageList.length >0){
			for(var i = 0; i < collection_Forum_AdvertisingRelated_imageList.length; i++){
			
				//版块---广告部分--轮播广告--单层
				var forum_AdvertisingRelated_image = collection_Forum_AdvertisingRelated_imageList[i];
				add_image_Div(forum_AdvertisingRelated_image,error_map);
			}
		}
	}
}





//回显  在线帮助部分--在线帮助列表--单层
function echo_help_monolayer(error_map){
	var monolayer_Forum_HelpRelated_Help_Json = document.getElementById("monolayer_Forum_HelpRelated_Help_Json").value;
	if(monolayer_Forum_HelpRelated_Help_Json != ""){
		var monolayer_Forum_HelpRelated_Help = JSON.parse(monolayer_Forum_HelpRelated_Help_Json);
		if(monolayer_Forum_HelpRelated_Help != null){
			//在线帮助分类Id
			if(monolayer_Forum_HelpRelated_Help.help_helpTypeId != null){
				document.getElementById("monolayer_help_helpType_helpTypeId").value = monolayer_Forum_HelpRelated_Help.help_helpTypeId;
			}
			//在线帮助分类名称
			if(monolayer_Forum_HelpRelated_Help.help_helpTypeName != null){
				document.getElementById("monolayer_help_helpType_helpTypeName").value = monolayer_Forum_HelpRelated_Help.help_helpTypeName;
			}
			//是否传递在线帮助分类参数
			if(monolayer_Forum_HelpRelated_Help.help_helpType_transferPrameter == true){
				//选中
				document.getElementById("monolayer_help_helpType_transferPrameter").checked = true;
				transferPrameterHelpTypeSite('monolayer_help_helpType');
			}
			
			//商品展示数量
			if(monolayer_Forum_HelpRelated_Help.help_quantity != null){
				document.getElementById("monolayer_help_quantity").value = monolayer_Forum_HelpRelated_Help.help_quantity;
			}
			//'更多' 每页显示记录数
			if(monolayer_Forum_HelpRelated_Help.help_maxResult != null){
				document.getElementById("monolayer_help_maxResult").value = monolayer_Forum_HelpRelated_Help.help_maxResult;
			}
			//'更多' 页码显示总数
			if(monolayer_Forum_HelpRelated_Help.help_pageCount != null){
				document.getElementById("monolayer_help_pageCount").value = monolayer_Forum_HelpRelated_Help.help_pageCount;
			}
			
			//排序
			var monolayer_help_sort = document.getElementById("monolayer_help_sort");
			for(var i =0; i<monolayer_help_sort.length; i++){    
				if(monolayer_help_sort[i].value == monolayer_Forum_HelpRelated_Help.help_sort){
					monolayer_help_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//商品展示数量
			if(error_map.get("monolayer_help_quantity") != null){
				document.getElementById("error_monolayer_help_quantity").innerHTML = error_map.get("monolayer_help_quantity");
			}
			//每页显示记录数
			if(error_map.get("monolayer_help_maxResult") != null){
				document.getElementById("error_monolayer_help_maxResult").innerHTML = error_map.get("monolayer_help_maxResult");
			}
			//页码显示总数
			if(error_map.get("monolayer_help_pageCount") != null){
				document.getElementById("error_monolayer_help_pageCount").innerHTML = error_map.get("monolayer_help_pageCount");
			}
			
			var forumChildType = document.getElementById('forumChildType').value;//子版块
			//'更多'
			siteMore(forumChildType);
			//选择‘更多’
			var more_select = document.getElementById("monolayer_help_more");
			for (var i=0; i<more_select.options.length; i++){
				if(more_select.options[i].value == monolayer_Forum_HelpRelated_Help.help_more){
					more_select.options[i].selected = true;
				}
			}
		}
	}
	
}
//回显  在线帮助部分--在线帮助列表--分页
function echo_help_page(error_map){
	var page_Forum_HelpRelated_Help_Json = document.getElementById("page_Forum_HelpRelated_Help_Json").value;
	if(page_Forum_HelpRelated_Help_Json != ""){
		var page_Forum_HelpRelated_Help = JSON.parse(page_Forum_HelpRelated_Help_Json);
		if(page_Forum_HelpRelated_Help != null){
			//在线帮助分类Id
			if(page_Forum_HelpRelated_Help.help_helpTypeId != null){
				document.getElementById("page_help_helpType_helpTypeId").value = page_Forum_HelpRelated_Help.help_helpTypeId;
			}
			//在线帮助分类名称
			if(page_Forum_HelpRelated_Help.help_helpTypeName != null){
				document.getElementById("page_help_helpType_helpTypeName").value = page_Forum_HelpRelated_Help.help_helpTypeName;
			}
			//是否传递在线帮助分类参数
			if(page_Forum_HelpRelated_Help.help_helpType_transferPrameter == true){
				//选中
				document.getElementById("page_help_helpType_transferPrameter").checked = true;
				transferPrameterHelpTypeSite('page_help_helpType');
			}

			//'更多' 每页显示记录数
			if(page_Forum_HelpRelated_Help.help_maxResult != null){
				document.getElementById("page_help_maxResult").value = page_Forum_HelpRelated_Help.help_maxResult;
			}
			//'更多' 页码显示总数
			if(page_Forum_HelpRelated_Help.help_pageCount != null){
				document.getElementById("page_help_pageCount").value = page_Forum_HelpRelated_Help.help_pageCount;
			}
			
			//排序
			var page_help_sort = document.getElementById("page_help_sort");
			for(var i =0; i<page_help_sort.length; i++){    
				if(page_help_sort[i].value == page_Forum_HelpRelated_Help.help_sort){
					page_help_sort[i].selected = true;
				}   
    		}
    		
    		//显示错误提示
			//每页显示记录数
			if(error_map.get("page_help_maxResult") != null){
				document.getElementById("error_page_help_maxResult").innerHTML = error_map.get("page_help_maxResult");
			}
			//页码显示总数
			if(error_map.get("page_help_pageCount") != null){
				document.getElementById("error_page_help_pageCount").innerHTML = error_map.get("page_help_pageCount");
			}
		}
	}
	
}
//回显  在线帮助部分--推荐在线帮助--集合
function echo_recommendHelp_collection(error_map){
	var collection_Forum_HelpRelated_RecommendHelp_Json = document.getElementById("collection_Forum_HelpRelated_RecommendHelp_Json").value;
	if(collection_Forum_HelpRelated_RecommendHelp_Json != ""){
		var collection_Forum_HelpRelated_RecommendHelp = JSON.parse(collection_Forum_HelpRelated_RecommendHelp_Json);
		if(collection_Forum_HelpRelated_RecommendHelp != null){
			//选择推荐在线帮助
			if(collection_Forum_HelpRelated_RecommendHelp.help_recommendHelpList != null){
				var recommendHelpList = collection_Forum_HelpRelated_RecommendHelp.help_recommendHelpList;
				for(var id in recommendHelpList){ 
					add_help_row("collection_recommendHelp",id,recommendHelpList[id]); 
				}
			}
		}
	}
}
			




//回显  自定义版块 -- 用户自定义HTML--实体对象
function echo_customHTML_page(error_map){
	var entityBean_Forum_CustomForumRelated_CustomHTML_Json = document.getElementById("entityBean_Forum_CustomForumRelated_CustomHTML_Json").value;
	if(entityBean_Forum_CustomForumRelated_CustomHTML_Json != ""){
		var entityBean_Forum_CustomForumRelated_CustomHTML = JSON.parse(entityBean_Forum_CustomForumRelated_CustomHTML_Json);
		if(entityBean_Forum_CustomForumRelated_CustomHTML != null){
			//自定义HTML内容
			if(entityBean_Forum_CustomForumRelated_CustomHTML.html_content != null){
				document.getElementById("entityBean_customForum_htmlContent").value = entityBean_Forum_CustomForumRelated_CustomHTML.html_content;
			}	
		}
	}
}

//回显  系统 部分--热门搜索词--集合
function echo_searchWord_collection(error_map){
	var collection_Forum_SystemRelated_SearchWord_Json = document.getElementById("collection_Forum_SystemRelated_SearchWord_Json").value;
	if(collection_Forum_SystemRelated_SearchWord_Json != ""){
		var collection_Forum_SystemRelated_SearchWord = JSON.parse(collection_Forum_SystemRelated_SearchWord_Json);
		if(collection_Forum_SystemRelated_SearchWord != null){
			//搜索词
			var searchWordList = collection_Forum_SystemRelated_SearchWord.searchWordList;
			if(searchWordList != null && searchWordList.length>0){
				for(var i =0; i<searchWordList.length; i++){
					add_searchWord_row(searchWordList[i]);
				}
			}
			
		}
	}
}


function init(){
	//检测是否有错误
	var errorStatus = document.getElementById("errorStatus").value;
	if(errorStatus == "Error"){
		selectForumType();
		var more = document.getElementById("more").value;
		if(more != ""){
			more_map = JSON.parse(more);
		}
		
	//	queryMore();
	}

	var error_map=new Map();//错误  key:错误Id  value:错误提示	
	var forumError_Json = document.getElementById("forumError_Json").value;
	if(forumError_Json != ""){
		var forumError_Map = JSON.parse(forumError_Json);
		if(forumError_Map != null){
		
			for(var key in forumError_Map){ 
				error_map.put(key,forumError_Map[key]);   
			}
		}
		
	}
	//回显  话题部分--话题列表--分页
	echo_topic_page(error_map);
	//回显  话题部分--相似话题--集合
	echo_likeTopic_collection(error_map);
	
	//回显  评论部分--评论列表--分页
	echo_comment_page(error_map);
	
	//回显  问题部分--问题列表--分页
	echo_question_page(error_map);
	
	//回显  答案部分--答案列表--分页
	echo_answer_page(error_map);
	//回显  问题部分--相似问题--集合
	echo_likeQuestion_collection(error_map);
	
	//回显  广告部分--图片广告--集合
	echo_image_collection(error_map);
	//回显  在线帮助部分--在线帮助列表--单层
	echo_help_monolayer(error_map);
	//回显  在线帮助部分--在线帮助列表--分页
	echo_help_page(error_map);
	//回显  在线帮助部分--推荐在线帮助--集合
	echo_recommendHelp_collection(error_map);
	
	
	
	//回显  自定义版块 -- 用户自定义HTML--实体对象
	echo_customHTML_page(error_map);
	//回显  系统 部分--热门搜索词--集合
	echo_searchWord_collection(error_map);
	
}
init();

</script>



<script language="JavaScript" type="text/javascript">
	var editor;
	KindEditor.options.cssData = 'body { font-size: 14px; }';
	KindEditor.ready(function(K) {
		editor = K.create('textarea[name="entityBean_customForum_htmlContent"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
			themeType : 'style :minimalist',//极简主题 加冒号的是主题样式文件名称同时也是主题目录
			autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 2,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			allowFlashUpload :true,
			uploadJson :'${config:url(pageContext.request)}control/forum/manage.htm?method=upload&layoutId=${param.layoutId}&${_csrf.parameterName}=${_csrf.token}',//指定浏览远程图片的服务器端程序
		//	fileManagerJson : '${config:url(pageContext.request)}control/customComment/manage.htm?method=uploadImage',//指定浏览远程图片的服务器端程序

			items : ['source', '|', 'preview', 'template',  
		        '|', 'justifyleft', 'justifycenter', 'justifyright',
		        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
		        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
		        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
		        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
		         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
		         'link', 'unlink'],
			afterChange : function() {
				this.sync();
			}
			
		});
		
	});
</script>


</BODY></HTML>
