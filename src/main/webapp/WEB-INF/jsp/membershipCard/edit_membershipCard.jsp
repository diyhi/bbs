<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>修改会员卡</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" charset="UTF-8"></script>
<script type="text/javascript" src="backstage/js/ajax.js" language="javascript" ></script>
<script type="text/javascript" src="backstage/jquery/jquery.min.js" language="JavaScript"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />

<script charset="utf-8" src="backstage/kindeditor/kindeditor-min.js"></script>

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

//添加规格行
function addSpecificationRow(){ //读取最后一行的行号，存放在LastIndex文本框中 
	var specificationLastIndex = findObj("specificationLastIndex",document); 

	var tables = findObj("specificationTable",document); 
	tables.border="0";

	var rowID = parseInt(specificationLastIndex.value); 
	//添加行 
	var newTR = tables.insertRow(tables.rows.length); 
	newTR.id = "specificationItem" + rowID;

	//添加列 
	var newTD=newTR.insertCell(0);  
	
	tableTxt = "<TABLE class=\"t-table\" cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\" border=\"0\">";
	tableTxt+="<TR>";
	tableTxt+=	  "<TD class=\"t-header\" colSpan=\"4\" height=\"26px\">";
	tableTxt+=	  "<span style=\"float:left; margin-left: 5px;margin-top: 2px;\">规格编号："+rowID+"</span>";

	tableTxt+=			"<span style=\"float:right; margin-left: 5px;margin-top: 2px;\" onclick=\"deleteSpecificationRow('specificationItem" + rowID + "')\"><img src=\"backstage/images/x.gif\" width=\"18\" height=\"18\" alt=\"删除\" title=\"删除\" /></span>";
	tableTxt+=			"<span style=\"float:right; margin-left: 5px;margin-top: 2px;\" onclick=\"moveDown('specificationTable','"+newTR.id+"')\"><img src=\"backstage/images/down_arrows.gif\" width=\"18\" height=\"18\" alt=\"下移\" title=\"下移\" /></span>";
	tableTxt+=			"<span style=\"float:right; margin-left: 5px;margin-top: 2px;\" onclick=\"moveUp('specificationTable','"+newTR.id+"')\"><img src=\"backstage/images/up_arrows.gif\" width=\"18\" height=\"18\" alt=\"上移\" title=\"上移\" /></span>";
	
	tableTxt+=	  "</TD>";
	tableTxt+="</TR>";
	
	
	tableTxt+="<TR>";
	tableTxt+=	  "<TD class=\"t-label t-label-h\" width=\"12%\"><span class=\"span-text\">*</span>规格名称：</TD>";
	tableTxt+=	  "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=    	  "<input class=\"form-text\" type=\"text\" name=\"specificationName\" size=\"30\" maxlength=\"50\"/>\&nbsp;\&nbsp;";
	tableTxt+=	  "</TD>";
	tableTxt+=	  "<TD class=\"t-label t-label-h\" width=\"12%\">启用规格：</TD>";
	tableTxt+=	  "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<label for=\"enable_"+rowID+"_true\">";
	tableTxt+=            "<input id=\"enable_"+rowID+"_true\"  name=\"enable_"+rowID+"\" type=\"radio\" value=\"true\" checked=\"checked\">启用";
	tableTxt+=        "</label>";
	tableTxt+=        "<label for=\"enable_"+rowID+"_false\">";
	tableTxt+=            "<input id=\"enable_"+rowID+"_false\"  name=\"enable_"+rowID+"\" type=\"radio\" value=\"false\" >禁用";
	tableTxt+=        "</label>";
	tableTxt+=	  "</TD>";
	tableTxt+="</TR>";
	
	tableTxt+="<TR>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\"><span class=\"span-text\">*</span>库存：</TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<input class=\"form-text\" type=\"text\" name=\"stock\" size=\"10\" maxlength=\"10\"/>";
	tableTxt+=    "</TD>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\">支付积分：</TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<input class=\"form-text\" type=\"text\" name=\"point\" size=\"10\" maxlength=\"10\"/>";
	tableTxt+=    "</TD>";
	tableTxt+="</TR>";
	tableTxt+="<TR>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\">市场价：</TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<input type=\"text\" class=\"form-text\" name=\"marketPrice\" size=\"10\" maxlength=\"10\"/>";
	tableTxt+=    "</TD>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\">销售价：</TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<input type=\"text\" class=\"form-text\" name=\"sellingPrice\" size=\"10\" maxlength=\"10\"/>";
	tableTxt+=    "</TD>";
	tableTxt+="</TR>";
	tableTxt+="<TR>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\"><span class=\"span-text\">*</span>时长：</TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=        "<div style=\"float: left;\">";
	tableTxt+=            "<input type=\"text\" class=\"form-text\" name=\"duration\" size=\"6\" maxlength=\"8\"/>&nbsp;";
	tableTxt+=        "</div>";
	tableTxt+=        "<div style=\"float: left;\">";
	tableTxt+=            "<select class=\"form-select\" name=\"unit\">";
	tableTxt+=                "<option value=\"10\">小时</option>";
	tableTxt+=                "<option value=\"20\">日</option>";
	tableTxt+=                "<option value=\"30\">月</option>";
	tableTxt+=                "<option value=\"40\">年</option>";
	tableTxt+=            "</select>";
	tableTxt+=        "</div>";
	tableTxt+=    "</TD>";
	tableTxt+=    "<TD class=\"t-label t-label-h\" width=\"12%\"></TD>";
	tableTxt+=    "<TD class=\"t-content\" width=\"38%\">";
	tableTxt+=    "</TD>";
	tableTxt+="</TR>";
	tableTxt+= "<input type='hidden' name='specificationRowTable' id='specificationRowTable" + rowID + "' value='"+ specificationLastIndex.value +"'>";
	tableTxt+= "<input type='hidden' name='specificationId' value=''>";
	tableTxt+="</TABLE>";
	
	//添加列内容 
	newTD.innerHTML = tableTxt; 
	

	//将行号推进下一行 
	specificationLastIndex.value = (rowID + 1).toString() ; 
} 


//删除规格指定行
function deleteSpecificationRow(rowID){ 
	var tables = findObj("specificationTable",document); 
	var styleItem = findObj(rowID,document); 
	 
	//获取将要删除的行的Index 
	var rowIndex = styleItem.rowIndex; 
	 
	if(rowIndex == 0 && tables.rows.length == 1){
		alert("至少要保留一个规格");
	}else{
		//删除指定Index的行 
		tables.deleteRow(rowIndex); 
	}
}




//添加说明标签行 
function addDescriptionTagRow(){ //读取最后一行的行号，存放在txtLastIndex文本框中 
	var descriptionTagLastIndex = findObj("descriptionTagLastIndex",document); 
	var rowId = parseInt(descriptionTagLastIndex.value); 
	var descriptionTagTable = findObj("descriptionTagTable",document); 

	//添加行 
	var newTR = descriptionTagTable.insertRow(descriptionTagTable.rows.length); 
	newTR.id = "descriptionTagItem" + rowId; 
	newTR.height="28px";
 
	//添加列
	var newColumn=newTR.insertCell(0); 
	//添加列内容 
	newColumn.innerHTML = "<input name='descriptionTag' type='text' class='form-text' size='50' />"; 
	 
	var html = "";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"deleteDescriptionTagRow('descriptionTagItem" + rowId + "')\"><img src=\"backstage/images/x.gif\" width=\"18\" height=\"18\" alt=\"删除\" title=\"删除\" /></span>";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"moveDown('descriptionTagTable','descriptionTagItem" + rowId + "')\"><img src=\"backstage/images/down_arrows.gif\" width=\"18\" height=\"18\" alt=\"下移\" title=\"下移\" /></span>";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"moveUp('descriptionTagTable','descriptionTagItem" + rowId + "')\"><img src=\"backstage/images/up_arrows.gif\" width=\"18\" height=\"18\" alt=\"上移\" title=\"上移\" /></span>";
	//添加列:删除按钮 
	newColumn=newTR.insertCell(1); 
	//添加列内容 
	newColumn.innerHTML = html;
	 
	//将行号推进下一行 
	descriptionTagLastIndex.value = (rowId + 1).toString() ; 
	
} 

//删除说明标签指定行 
function deleteDescriptionTagRow(rowId){ 
	var descriptionTagTable = findObj("descriptionTagTable",document);  
	var item = findObj(rowId,document); 
	 
	//获取将要删除的行的Index 
	var rowIndex = item.rowIndex; 
	 
	//删除指定Index的行 
	descriptionTagTable.deleteRow(rowIndex); 
 
}













</script> 

</HEAD>
<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>
<form:form modelAttribute="membershipCard" method="post">
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/membershipCard/list${config:suffix()}?page=${param.page}'" value="返回">
</div>

<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><span class="span-text">*</span>名称：</TD>
		    <TD class=t-content width="88%" colSpan=3>
		    	<input class="form-text" name="name" size="50" value="${membershipCard.name}"/>
			    &nbsp;<span class="span-text">${error["name"]}</span>	
		    </TD>
	    </TR>
	    <TR>
		    <TD class="t-label t-label-h" width="12%">副标题：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<input class="form-text" name="subtitle" size="50" value="${membershipCard.subtitle}"/>
		      	&nbsp;<span class="span-text">${error["subtitle"]}</span>	
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="12%">用户角色：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
			    <table cellSpacing="0" cellPadding="0" width="100%" border="0">
				    <c:forEach items="${userRoleList}" var="entry" > 
					    <tr>
					  		<td style="border-bottom:1px dashed #BFE3FF; line-height:30px;color: #999; text-align:left;width: 200px;">
					  			<label style="line-height: 28px;margin-right: 8px;"><input type="radio" name="userRoleId" value="${entry.id}" <c:if test="${entry.selected == true}"> checked="checked"</c:if>/>${entry.name}</label>
					    	</td>
					    </tr>
					</c:forEach>
			    </table>
			    <c:if test="${error['userRoleId'] != null}">
			    	&nbsp;<span class="span-text">${error["userRoleId"]}</span>
			    </c:if>
		    </TD>
		</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">规格：</TD>
		    <TD class="t-content" width="88%" colSpan="3">
		    	<input type="button" class="functionButton5" value="添加一个规格" onClick="addSpecificationRow()" />
		      	&nbsp;<span class="span-text">${error["specification"]}</span>	
		    </TD>
		</TR>
	</TBODY>	
</TABLE>	

	<c:set value="0" var="specificationCount"></c:set>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="specificationTable">  
		
		<c:forEach items="${specificationList}" var="specification" varStatus="status">
	     	<tr id="specificationItem${status.count}"><td>
     			<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
    				<TR>
						<TD class="t-header" colSpan="4" height="26px">
							<span style="float:left; margin-left: 5px;margin-top: 2px;">规格编号：${status.count}</span>
							<span style="float:right; margin-left: 5px;margin-top: 2px;" onclick="deleteSpecificationRow('specificationItem${status.count}')"><img src="backstage/images/x.gif" width="18" height="18" alt="删除" title="删除" /></span>
							<span style="float:right; margin-left: 5px;margin-top: 2px;" onclick="moveDown('specificationTable','specificationItem${status.count}')"><img src="backstage/images/down_arrows.gif" width="18" height="18" alt="下移" title="下移" /></span>
							<span style="float:right; margin-left: 5px;margin-top: 2px;" onclick="moveUp('specificationTable','specificationItem${status.count}')"><img src="backstage/images/up_arrows.gif" width="18" height="18" alt="上移" title="上移" /></span>

							<c:set value="specification_${status.count}" var="specification_error"></c:set>
							<span name="tr_specification_error" class="span-text" >${error[specification_error]}</span>
						</TD>
					</TR>
					
	  				<TR>
					    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>规格名称：</TD>
					    <TD class="t-content" width="38%">
					    	<input type="text" class="form-text" name="specificationName" size="30" maxlength="30" value="${specification.specificationName }"/>&nbsp;&nbsp;
	    					<c:set value="specificationName_${status.count}" var="specificationName_error"></c:set>
							<SPAN class="span-text">${error[specificationName_error]}</SPAN>&nbsp;&nbsp;
					    </TD>
					    <TD class="t-label t-label-h" width="12%">启用规格：</TD>
					    <TD class="t-content" width="38%">
					    	<label for="enable_${status.count}_true">
					    		<input id="enable_${status.count}_true"  name="enable_${status.count}" type="radio" value="true" <c:if test="${specification.enable == true}"> checked='checked'</c:if>>启用
					    	</label>
					    	<label for="enable_${status.count}_false">
					    		<input id="enable_${status.count}_false"  name="enable_${status.count}" type="radio" value="false" <c:if test="${specification.enable == false}"> checked='checked'</c:if>>禁用
					    	</label>
					    </TD>
					</TR>


	    			<TR>
					    <TD class="t-label t-label-h" width="12%"><span class="span-text">*</span>库存：</TD>
					    <TD class="t-content" width="38%" >
					    	
		    				<c:if test="${specification.stockStatus == 0}">
		    					<input type="text" class="form-text" name="stock" size="10" maxlength="10" value="${specification.stock}"/>&nbsp;&nbsp;
		    				</c:if>
		    				
		    				<c:if test="${specification.stockStatus > 0}">
		    					<input type="hidden" name="stock" value="0"/>
		    					<div style="float: left;">
				    				${specification.stock}
				    				&nbsp;&nbsp;
				    				<select class="form-select" name="stockStatus_${status.count}">
							    		<option value="1" <c:if test="${specification.stockStatus == 1}"> selected="selected"</c:if>>增加</option>
							    		<option value="2" <c:if test="${specification.stockStatus == 2}"> selected="selected"</c:if>>减少</option>
							    	</select>
						    	</div>
						    	<input type="text" class="form-text" name="changeStock_${status.count}" size="10" maxlength="10" value="${specification.changeStock}"/>&nbsp;&nbsp;
		    				</c:if>
		    				
		    				已出售数量:${specification.stockOccupy}
	
		    				<c:set value="stock_${status.count}" var="stock_error"></c:set>
							<span class="span-text">${error[stock_error]}</span>
					    </TD>
					     <TD class="t-label t-label-h" width="12%">支付积分：</TD>
					    <TD class="t-content" width="38%" >
					    	<input type="text" class="form-text" name="point" size="10" maxlength="10" value="${specification.point}"/>&nbsp;&nbsp;
		    				<c:set value="point_${status.count}" var="point_error"></c:set>
							<span class="span-text">${error[point_error]}</span>
					    	
					    </TD>
					</TR>
					
					<TR>
					    <TD class="t-label t-label-h" width="12%">市场价：</TD>
					    <TD class="t-content" width="38%" >
					    	<input class="form-text" name="marketPrice" size="10" maxlength="10" value="${specification.marketPrice}" />&nbsp;元
					    	<c:set value="marketPrice_${status.count}" var="marketPrice_error"></c:set>
							&nbsp;<span class="span-text">${error[marketPrice_error]}</span>
					    </TD>
					    <TD class="t-label t-label-h" width="12%">销售价：</TD>
					    <TD class="t-content" width="38%" >
					    	<input class="form-text" name="sellingPrice" size="10" maxlength="10" value="${specification.sellingPrice}" />&nbsp;元
					    	<c:set value="sellingPrice_${status.count}" var="sellingPrice_error"></c:set>
							&nbsp;<span class="span-text">${error[sellingPrice_error]}</span>
					    </TD>
					</TR>
					<TR>
					    <TD class="t-label t-label-h" width="12%"><span class="span-text">*</span>时长：</TD>
					    <TD class="t-content" width="38%" >
					    	<div style="float: left;">
						    	<input class="form-text" name="duration" size="8" maxlength="8" value="${specification.duration}" />&nbsp;
					    	</div>
					    	<div style="float: left;">
		    				<select class="form-select" name="unit">
					    		<option value="10" <c:if test="${specification.unit == 10}"> selected="selected"</c:if>>小时</option>
					    		<option value="20" <c:if test="${specification.unit == 20}"> selected="selected"</c:if>>日</option>
					    		<option value="30" <c:if test="${specification.unit == 30}"> selected="selected"</c:if>>月</option>
					    		<option value="40" <c:if test="${specification.unit == 40}"> selected="selected"</c:if>>年</option>
					    	</select>
					    	</div>
					    	<div style="float: left;margin-top: 7px;">
					    	<c:set value="duration_${status.count}" var="duration_error"></c:set>
							&nbsp;<span class="span-text">${error[duration_error]}</span>
							<c:set value="unit_${status.count}" var="unit_error"></c:set>
							&nbsp;<span class="span-text">${error[unit_error]}</span>
							</div>
					    </TD>
					    <TD class="t-label t-label-h" width="12%"></TD>
					    <TD class="t-content" width="38%" >
					    	
					    </TD>
					</TR>
	    		</TABLE>
	    		<input type="hidden" name="specificationRowTable" id="specificationRowTable${status.count}" value="${status.count}">
	    		<input type="hidden" name="specificationId" value="${specification.id}">
     		</td></tr>
     		
     		<c:set value="${status.count}" var="specificationCount"></c:set>
     	</c:forEach>
    </table> 
	<input type='hidden' id='specificationLastIndex' value="${specificationCount+1}" /> 
		
<TABLE class=t-table cellSpacing=1 cellPadding=2 width="100%" border=0>		
	<TBODY>
		<TR>
		    <TD class="t-label t-label-h" width="12%"><span class="span-text">*</span>排序：</TD>
		    <TD class="t-content" width="38%" >
		    	<INPUT class="form-text" name="sort" size="5" value="${membershipCard.sort}">
		      	&nbsp;<span class="span-text">${error["sort"]}</span>	
		    </TD>
		    <TD class="t-label t-label-h" width="12%">是否上架：</TD>
		    <TD class="t-content" width="38%" >
		    	<input id="shelve_1" name="state" type="radio" value="1" <c:if test="${membershipCard.state == 1}"> checked="checked"</c:if>><LABEL for="shelve_1">是</LABEL>
    			<input id="shelve_2" name="state" type="radio" value="2" <c:if test="${membershipCard.state == 2}"> checked="checked"</c:if>><LABEL for="shelve_2">否</LABEL>
    		</TD>
		</TR>
	
    	<TR>
			<TD class="t-label t-label-h" width="12%">说明标签：</TD>
			<TD class="t-content" width="88%" colSpan="3">
    			<input type="button" class="functionButton5" value="添加项" onClick="addDescriptionTagRow()" />
		    	<table width="450px" border="0" cellpadding="0" cellspacing="0" id="descriptionTagTable" >  
		   		<TBODY>
		   			<c:set var="descriptionTag_count" value="0"></c:set>
		   			<c:forEach items="${membershipCard.descriptionTagList}" var="descriptionTag" varStatus="status">
	   					<TR id="descriptionTagItem${status.count}" height=28>
							<TD><INPUT class="form-text" size="50" type="text" name="descriptionTag" value="${descriptionTag}"></TD>
							<TD>
								<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="deleteDescriptionTagRow('descriptionTagItem${status.count}')"><IMG title=删除 alt=删除 src="backstage/images/x.gif" width=18 height=18></SPAN>
								<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="moveDown('descriptionTagTable','descriptionTagItem${status.count}')"><IMG title=下移 alt=下移 src="backstage/images/down_arrows.gif" width=18 height=18></SPAN>
								<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="moveUp('descriptionTagTable','descriptionTagItem${status.count}')"><IMG title=上移 alt=上移 src="backstage/images/up_arrows.gif" width=18 height=18></SPAN>
							</TD>
						</TR>
						<c:set var="descriptionTag_count" value="${status.count}"></c:set>
	   				</c:forEach>
				</TBODY>
			   	</table> 
			   	<input type='hidden' id='descriptionTagLastIndex' value="${descriptionTag_count+1}" /> 
				<span class="span-text">${error['descriptionTag']}</span>
    		</TD>
    	</TR>
		<TR>
			<TD class="t-label t-label-h" width="12%">简介：</TD>
    		<TD class="t-content" width="88%" colSpan="3">
    			<textarea rows="10" cols="10" id="introduction" name="introduction" style="width:99%;height:300px;visibility:hidden;">${membershipCard.introduction}</textarea>
    		</TD>
		</TR>
		
		<TR>
		    <TD class="t-button" colSpan="4">
		       <span class="submitButton"><INPUT type="submit" value="提交" ></span>
		  	</TD>
		</TR>	
	</TBODY>
</TABLE>
</DIV>
</form:form>

<script>
	KindEditor.options.cssData = 'body { font-size: 14px; }';
	var availableTag = ['source', '|'];
	var editor;
	KindEditor.ready(function(K) {
		editor = K.create('textarea[name="introduction"]', {
			basePath : '${config:url(pageContext.request)}backstage/kindeditor/',//指定编辑器的根目录路径
			themeType : 'style :minimalist',//极简主题 加冒号的是主题样式文件名称同时也是主题目录
			autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
			formatUploadUrl :false,//false时不会自动格式化上传后的URL
			resizeType : 2,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
			allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
			allowImageUpload : true,//true时显示图片上传按钮
			allowFlashUpload :true,
			uploadJson :'${config:url(pageContext.request)}control/membershipCard/manage.htm?method=upload&${_csrf.parameterName}=${_csrf.token}',//指定浏览远程图片的服务器端程序
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
