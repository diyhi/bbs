<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>布局编辑  修改</TITLE>
<META http-equiv="Content-Type" content="text/html" charset="UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">

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

<style type="text/css">
.CodeMirror {border-top: 1px solid #BFE3FF; border-bottom: 1px solid #BFE3FF; }

</style>
<link rel="stylesheet" href="backstage/codeMirror/lib/util/simple-hint.css">
<link rel="stylesheet" href="backstage/codeMirror/lib/codemirror.css">
<script src="backstage/codeMirror/lib/codemirror.js"></script>
<script src="backstage/codeMirror/lib/util/closetag.js"></script>
<script src="backstage/codeMirror/mode/xml/xml.js"></script>
<script src="backstage/codeMirror/mode/javascript/javascript.js"></script>
<script src="backstage/codeMirror/mode/css/css.js"></script>
<script src="backstage/codeMirror/mode/htmlmixed/htmlmixed.js"></script>
<script src="backstage/codeMirror/lib/util/simple-hint.js"></script>
<script src="backstage/codeMirror/lib/util/javascript-hint.js"></script>

<script language="JavaScript" type="text/javascript">
	function sureSubmit(objForm){
		//按钮设置 disabled="disabled"
		document.getElementById("submitForm").disabled=true;
		objForm.submit();
	} 
</script>
</HEAD>
  <body>
   <form:form> 
   <DIV class="d-box">
<!-- 导航 -->
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
		<TR>
	    <TD class="t-content" colSpan="5" height="25px">
	        <span style="font-weight:bold;float:left;">&nbsp;模板：</span>
	        <span style="float:left;">&nbsp;<a href="${config:url(pageContext.request)}control/template/list${config:suffix()}">全部模板</a></span>
			<span style="float:left">
			
			&nbsp;>>&nbsp;<a href="control/layout/list${config:suffix()}?&dirName=${param.dirName}">${templates.name}<span style="color: red">[${param.dirName }]</span> </a>
	
			&nbsp;>>&nbsp;<a href="control/forum/list${config:suffix()}?layoutId=${param.layoutId}&dirName=${param.dirName}">${layout.name}</a>
			&nbsp;>> &nbsp;布局编辑
			</span>
		</TD>
	</TR>
</TBODY></TABLE> 
    <h1 style="font-size:15px;text-align:center;margin:4px !important;margin:3px ;padding:4px">
	    ${layout.layoutFile}
	    <c:if test="${layout_error != null}">
	    	<span style="color: red;">(${layout_error})</span>
	    </c:if>
	    
    </h1>

<!--把下面代码加到<body>与</body>之间-->
<div class="item clearfix" id="tabs">
	<div class="itemTab active" onclick="put_css(1)">
    	<span>电脑版</span>
    </div>
	<div class="itemTab" onclick="put_css(2)">
    	<span>移动版</span>
    </div>
</div>
<div class="clear"></div>

<div id="html_1" style="">
  	<textarea id="pc_code" name="pc_code" >${pc_html}</textarea>

</div>
<div id="html_2" style="">
   	<textarea id="wap_code" name="wap_code" >${wap_html}</textarea>
</div>


	
	
    <TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	  <TBODY>
		<tr>
	    <TD class="t-button">
	        <span class="submitButton"><INPUT type="button" id="submitForm" value="修改" onClick="javascript:sureSubmit(this.form)"></span>
	  	</TD>
	  </TR>
	</TBODY></TABLE>
	
	<div class="item clearfix" id="example">
		<div class="itemTab active" onclick="put_example(1)">
	    	文档参数
	    </div>
	    <div class="itemTab" onclick="put_example(2)">
	    	公共API
	    </div>
	</div>
	<div class="clear"></div>
	<div id="example_1" style="">
		<enhance:out escapeXml="false">${example}</enhance:out>
	</div>
	<div id="example_2" style="">
		<enhance:out escapeXml="false">${common}</enhance:out>
	</div>
	<div class="clear"></div>
</DIV>

    </form:form>
  </body>

  
  <script>
  /**
      var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        lineNumbers: true,
        mode: "application/x-ejs",
        indentUnit: 4,
        indentWithTabs: true,
        enterMode: "keep",
        tabMode: "shift"
      });
    //  editor.toTextArea();  //经过转义的数据,有错
	//	editor.getTextArea().value;//未经过转义的字符,有错
//	alert(document.getElementById("code").value);**/


	  CodeMirror.commands.autocomplete = function(cm) {
        CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);
     //  CodeMirror.simpleHint(cm, CodeMirror.coffeescriptHint); 
     	
        
      };
      
      var editor = CodeMirror.fromTextArea(document.getElementById("pc_code"), {
      	lineNumbers: true,
        mode: 'text/html',
        indentUnit: 4,
        extraKeys: {"Alt-/": "autocomplete"},
        indentWithTabs: true,
        autoCloseTags: true
      });
      
      CodeMirror.fromTextArea(document.getElementById("wap_code"), {
        	lineNumbers: true,
          mode: 'text/html',
          indentUnit: 4,
          extraKeys: {"Alt-/": "autocomplete"},
          indentWithTabs: true,
          autoCloseTags: true
        });
    </script>
    <script type="text/javascript">
	
		var exampleCode_arr = document.getElementsByName("exampleCode")
		if(exampleCode_arr != null && exampleCode_arr.length >0){
			for(var i=0;i<exampleCode_arr.length;i++){
				var exampleCode = exampleCode_arr[i];
				//演示代码高亮
				 var editor = CodeMirror.fromTextArea(exampleCode, {
			   	      	lineNumbers: true,/*是否在编辑器左侧显示行号*/
			   	        mode: 'text/html',
			   	        indentUnit: 4,/* 缩进单位，值为空格数，默认为2 */
			   	        extraKeys: {"Alt-/": "autocomplete"},/*给编辑器绑定快捷键*/
			   	        indentWithTabs: true,/* 在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符，默认为false  */
			   	        autoCloseTags: true,
			   	        readOnly:true/* 只读 */
			   	      });
			}
		}
	</script>
      <script type="text/javascript"> 
	var tab = document.getElementById('tabs').getElementsByTagName('div');
	function put_css(id){
		for(var k=0;k<tab.length;k++){
			if(id-1 == k){
				tab[k].className = 'itemTab active';
				document.getElementById("html_"+id).style.display = "block";
				
			}else{
				tab[k].className = 'itemTab';
				document.getElementById("html_"+(k+1)).style.display = "none";
			}
		}
	}
	put_css(1);
	
	var example_tab = null;
    //演示程序选项卡
    function put_example(id){
		for(var k=0;k<example_tab.length;k++){
  			if(id-1 == k){
  				example_tab[k].className = 'itemTab active';
  				document.getElementById("example_"+id).style.display = "block";
  				
  			}else{
  				example_tab[k].className = 'itemTab';
  				document.getElementById("example_"+(k+1)).style.display = "none";
  			}
  		}
  	}
     if(document.getElementById('example') != null){
    	example_tab = document.getElementById('example').getElementsByTagName('div');
    	
    	put_example(1);
    	
    }
    
    
    //锚点跳转
	function anchorJump(id){
		location.hash="";
		location.hash=id;   
    }
	</script>

	
</html>
