<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>添加用户自定义注册功能项</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="backstage/js/Tool.js" ></script>
<script language="javascript" type="text/javascript">
//选择类型
function selectType(obj){
	var index = obj.selectedIndex; // 选中索引
	var value = obj.options[index].value; // 选中值
	
	
	var all_table = new Array('1','2','3','4','5'); 
	for(var key in all_table) {
		var objs=document.getElementById("chooseType_"+all_table[key]); 
		if(objs != null){
			objs.style.display=all_table[key]==obj.options[obj.selectedIndex].value?'':'none';
		}
	}
	if(value == 2 || value == 3 || value == 4){
		document.getElementById("itemTable").style.display = "";
	}else{
		document.getElementById("itemTable").style.display = "none";
	}
}

//选择字段过滤
function selectfieldFilter(obj){
	var index = obj.selectedIndex; // 选中索引
	var value = obj.options[index].value; // 选中值
	if(value == 5){
		document.getElementById("regularExpression").style.display = "";
	}else{
		document.getElementById("regularExpression").style.display = "none";
	}

	
}

function sureSubmit(objForm){
	//按钮设置 disabled="disabled"
	document.getElementById("submitForm").disabled=true;
	objForm.submit();
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

//添加一行 
function addRow(){ //读取最后一行的行号，存放在txtLastIndex文本框中 
	var inputLastIndex = findObj("inputLastIndex",document); 
	var rowId = parseInt(inputLastIndex.value); 
	var input_table = findObj("input_table",document); 

	//添加行 
	var newTR = input_table.insertRow(input_table.rows.length); 
	newTR.id = "item" + rowId; 
	newTR.height="28px";
 
	//添加列
	var newColumn=newTR.insertCell(0); 
	//添加列内容 
	newColumn.innerHTML = "<input name='_itemValue' type='text' class='form-text' size='20' />"; 
	 
	var html = "";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"deleteRow('item" + rowId + "')\"><img src=\"backstage/images/x.gif\" width=\"18\" height=\"18\" alt=\"删除\" title=\"删除\" /></span>";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"moveDown('input_table','item" + rowId + "')\"><img src=\"backstage/images/down_arrows.gif\" width=\"18\" height=\"18\" alt=\"下移\" title=\"下移\" /></span>";
	html += "<span style=\"float:right; margin-left: 0px;\" onclick=\"moveUp('input_table','item" + rowId + "')\"><img src=\"backstage/images/up_arrows.gif\" width=\"18\" height=\"18\" alt=\"上移\" title=\"上移\" /></span>";
	//添加列:删除按钮 
	newColumn=newTR.insertCell(1); 
	//添加列内容 
	newColumn.innerHTML = html;
	 
	//将行号推进下一行 
	inputLastIndex.value = (rowId + 1).toString() ; 
	
} 

//删除指定行 
function deleteRow(rowId){ 
	var input_table = findObj("input_table",document);  
	var item = findObj(rowId,document); 
	 
	//获取将要删除的行的Index 
	var rowIndex = item.rowIndex; 
	 
	//删除指定Index的行 
	input_table.deleteRow(rowIndex); 
 
}


</script>
   
</HEAD>
<BODY>
<form:form modelAttribute="userCustom" method="post" >
<DIV class="d-box">
<div class="d-button">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userCustom/list${config:suffix()}'" value="返回">
</div>
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>注册项名称：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input class="form-text" name="name" size="50" maxlength="40" value="${userCustom.name}"/>&nbsp;&nbsp;
    	<span class="span-text">${error['name']}</span>
    </TD></TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">是否必填：</TD>
    <TD class="t-content" width="88%" colSpan="3">
		<label><INPUT type="radio" value="true" name="required" <c:if test="${userCustom.required == true}"> checked="checked"</c:if>>必填</label>
		<label><INPUT type="radio" value="false" name="required" <c:if test="${userCustom.required == false}"> checked="checked"</c:if>>可为空</label>
		<span class="span-text">${error['required']}</span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="15%">后台是否可搜索：</TD>
    <TD class="t-content" width="88%" colSpan="3">
		<label><INPUT type="radio" value="true" name="search" <c:if test="${userCustom.search == true}"> checked="checked"</c:if>>可搜索</label>
		<label><INPUT type="radio" value="false" name="search" <c:if test="${userCustom.search == false}"> checked="checked"</c:if>>不能搜索</label>
		<span class="span-text">${error['search']}</span>
		<SPAN class="span-help">只对'单选按钮''多选按钮''下拉列表'有效</SPAN>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="15%">是否显示：</TD>
    <TD class="t-content" width="88%" colSpan="3">
		<label><INPUT type="radio" value="true" name="visible" <c:if test="${userCustom.visible == true}"> checked="checked"</c:if>>显示</label>
		<label><INPUT type="radio" value="false" name="visible" <c:if test="${userCustom.visible == false}"> checked="checked"</c:if>>隐藏</label>
		<span class="span-text">${error['visible']}</span>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>注册项排序：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input class="form-text" name="_sort" size="10" maxlength="10" value="${userCustom.sort}" />&nbsp;&nbsp;
    	<span class="span-text">${error['sort']}</span>
    	<SPAN class="span-help">最大排在最前面</SPAN>
    </TD></TR>
    <TR>
    <TD class="t-label t-label-h" width="12%">提示：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	<input class="form-text" name="tip" size="60" maxlength="250" value="${userCustom.tip}" />&nbsp;&nbsp;
    	<span class="span-text">${error['tip']}</span>
    </TD></TR>
   </TBODY>
  <TR>
    <TD class="t-label t-label-h" width="12%"><SPAN class="span-text">*</SPAN>选择类型：</TD>
    <TD class="t-content" width="88%" colSpan="3">
	    <select class="form-select" id="chooseType" name="chooseType" onChange="selectType(this);" >
	   		 <option value="1" <c:if test="${userCustom.chooseType eq '1'}"> selected="selected"</c:if>>输入框</option>
	   		 <option value="2" <c:if test="${userCustom.chooseType eq '2'}"> selected="selected"</c:if>>单选按钮</option>
	   		 <option value="3" <c:if test="${userCustom.chooseType eq '3'}"> selected="selected"</c:if>>多选按钮</option>
	   		 <option value="4" <c:if test="${userCustom.chooseType eq '4'}"> selected="selected"</c:if>>下拉列表</option>
	   		 <option value="5" <c:if test="${userCustom.chooseType eq '5'}"> selected="selected"</c:if>>文本域</option>
	    </select>
  </TD></TR>
   <TBODY id="chooseType_1" <c:if test="${userCustom.chooseType ne '1'}"> style="DISPLAY: none"</c:if>>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >输入框的宽度：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="_size" size="10" maxlength="10" value="${userCustom.size}"/>
	    	&nbsp;<span class="span-text">${error['size']}</span>
	    </TD>
	  </TR>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >输入框字符的最大长度：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="_maxlength" size="10" maxlength="10" value="${userCustom.maxlength}"/>
	    	&nbsp;<span class="span-text">${error['maxlength']}</span>
	    </TD>
	  </TR>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >字段值过滤：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<select class="form-select" id="fieldFilter" name="fieldFilter" onChange="selectfieldFilter(this);" >
		   		 <option value="0" <c:if test="${userCustom.fieldFilter eq '0'}"> selected="selected"</c:if>>无</option>
		   		 <option value="1" <c:if test="${userCustom.fieldFilter eq '1'}"> selected="selected"</c:if>>只允许输入数字</option>
		   		 <option value="2" <c:if test="${userCustom.fieldFilter eq '2'}"> selected="selected"</c:if>>只允许输入字母</option>
		   		 <option value="3" <c:if test="${userCustom.fieldFilter eq '3'}"> selected="selected"</c:if>>只允许输入数字和字母</option>
		   		 <option value="4" <c:if test="${userCustom.fieldFilter eq '4'}"> selected="selected"</c:if>>只允许输入汉字</option>
		   		 <option value="5" <c:if test="${userCustom.fieldFilter eq '5'}"> selected="selected"</c:if>>正则表达式过滤</option>
		    </select>
	    </TD>
	  </TR>
	  <TR id="regularExpression" <c:if test="${userCustom.fieldFilter ne '5'}"> style="DISPLAY: none"</c:if>>
	    <TD class="t-label t-label-h" width="15%" >正则表达式：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="regular" size="80"  value="${userCustom.regular}"/>&nbsp;&nbsp;
    		<span class="span-text">${error['regular']}</span>
	    </TD>
	  </TR>
	  
	  </TBODY>
    <TBODY id="itemTable" <c:if test="${userCustom.chooseType ne '2' && userCustom.chooseType ne '3' && userCustom.chooseType ne '4'}"> style="DISPLAY: none"</c:if>>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >选项：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input type="button" class="functionButton5" value="添加项" onClick="addRow()" />
	    	<table width="250px" border="0" cellpadding="0" cellspacing="0" id="input_table" >  
	   		<TBODY>
	   			<c:set var="itemValue_count" value="0"></c:set>
	   			<c:forEach items="${itemValue_map}" var="itemValue" varStatus="status">
   					<TR id="item${status.count}" height=28>
						<TD><INPUT class="form-text" size=20 type=text name=_itemValue value="${itemValue.value}"><input type="hidden" name="itemKey" value="${itemValue.key}"></TD>
						<TD>
							<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="deleteRow('item${status.count}')"><IMG title=删除 alt=删除 src="backstage/images/x.gif" width=18 height=18></SPAN>
							<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="moveDown('input_table','item${status.count}')"><IMG title=下移 alt=下移 src="backstage/images/down_arrows.gif" width=18 height=18></SPAN>
							<SPAN style="FLOAT: right; MARGIN-LEFT: 0px" onclick="moveUp('input_table','item${status.count}')"><IMG title=上移 alt=上移 src="backstage/images/up_arrows.gif" width=18 height=18></SPAN>
						</TD>
					</TR>
					<c:set var="itemValue_count" value="${status.count}"></c:set>
   				</c:forEach>
			</TBODY>
		   	</table> 
		   	<input type='hidden' id='inputLastIndex' value="${itemValue_count+1}" /> 
			<span class="span-text">${error['itemValue']}</span>
	    </TD>
	  </TR>
	 </TBODY>
    <TBODY id="chooseType_4" <c:if test="${userCustom.chooseType ne '4'}"> style="DISPLAY: none"</c:if>>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >可选择多个选项：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<INPUT type="checkbox" value="true" name="multiple" <c:if test="${userCustom.multiple == true}"> checked="checked"</c:if>>
    		<span class="span-text">${error['multiple']}</span>
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >下拉列表中可见选项的数目：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="_selete_size" size="10" maxlength="10" value="${userCustom.selete_size}"/>
	    	&nbsp;<span class="span-text">${error['selete_size']}</span>
	    </TD>
	  </TR>
	  </TBODY>
	  <TBODY id="chooseType_5" <c:if test="${userCustom.chooseType ne '5'}"> style="DISPLAY: none"</c:if>>
	   <TR>
	    <TD class="t-label t-label-h" width="15%" >文本域内的可见行数：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="_rows" size="10" maxlength="10" value="${userCustom.rows}"/>
	    	&nbsp;<span class="span-text">${error['rows']}</span>
	    </TD>
	  </TR>
	  <TR>
	    <TD class="t-label t-label-h" width="15%" >文本域内的可见宽度：</TD>
	    <TD class="t-content" width="85%" colSpan="3">
	    	<input class="form-text" name="_cols" size="10" maxlength="10" value="${userCustom.cols}"/>
	    	&nbsp;<span class="span-text">${error['cols']}</span>
	    </TD>
	  </TR>
	  </TBODY>
	<tr>
    <TD class="t-button" colSpan="4">
        <span class="submitButton"><INPUT type="button" id="submitForm" value="提交" onClick="javascript:sureSubmit(this.form);"></span>
  	</TD>
  </TR>
</TABLE>
</DIV>
</form:form>
</BODY></HTML>
