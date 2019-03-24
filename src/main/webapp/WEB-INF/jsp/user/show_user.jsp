<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>用户详细</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf_token" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<link href="backstage/css/list.css" type="text/css" rel="stylesheet"/>
<link href="backstage/css/table.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/js/Tool.js" type="text/javascript"></script>
<script language="javascript" src="backstage/js/ajax.js" type="text/javascript"></script>
<script type="text/javascript" src="backstage/js/json3.js"></script>
<script language="javascript" src="backstage/jquery/jquery.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="backstage/layer/skin/layer.css"  type="text/css" />


<script language="javascript" src="backstage/jquery/jquery.form.js" type="text/javascript"></script>
<link href="backstage/jcrop/css/jquery.Jcrop.min.css" type="text/css" rel="stylesheet"/>
<script language="javascript" src="backstage/jcrop/js/jquery.Jcrop.min.js" type="text/javascript"></script>


<script type="text/javascript" language="javascript"> 
	//空白图片
	var blankImage = "backstage/images/null.gif";
	//头像图层
	var avatarDiv = "<div id='avatarTag'>";
		avatarDiv += "        <div id='originalImage_box' class='original-box'><div id='image_crop_original_div' class='image-crop-original-div'></div><img id='originalImage' src='"+blankImage+"' alt='原图'></div>";
		avatarDiv += "        <div id='preview-pane' class='preview-pane'>";
		avatarDiv += "            <div id='previewImage_box' class='preview-container'>";
		avatarDiv += "                <div id='image_crop_preview_div' class='image-crop-preview-div'></div>";
		avatarDiv += "                <img id='previewImage' src='"+blankImage+"' alt='缩略图'>";
		avatarDiv += "            </div>";
		avatarDiv += "        </div>";
		avatarDiv += "        <div id='preview-pane2' class='preview-pane2'>";
		avatarDiv += "            <div id='previewImage_box2' class='preview-container2'>";
		avatarDiv += "                <div id='image_crop_preview_div2' class='image-crop-preview-div'></div>";
		avatarDiv += "                <img id='previewImage2' src='"+blankImage+"' alt='缩略图'>";
		avatarDiv += "            </div>";
		avatarDiv += "        </div>";
		avatarDiv += "</div>";

    //头像表单
//	var avatarForm = "<form id='uploadImageForm' enctype='multipart/form-data'>";
	var avatarForm = "    <div class='avatarModule'>";
		avatarForm +=          avatarDiv;
		avatarForm += "        <div class='bottomInfo'>";
		avatarForm += "            <div class='avatar-submit-button'>";
		avatarForm += "                <span class='submitButton'><INPUT type='button' id='uploadSubmit' value='上传' onClick='javascript:uploadImageSubmit();'></span>";
		avatarForm += "            </div>";
		avatarForm += "            <div class='avatar-upload-button'>";
		avatarForm += "                <span id='progressBar'></span>&nbsp;&nbsp;";
		avatarForm += "                <span class='uploadButton' >选择文件";
		avatarForm += "                    <input type='file' id='imgFile' name='imgFile' onchange='imgForm(this)'/>";
		avatarForm += "                </span>&nbsp;&nbsp;";
		avatarForm += "            </div>";
		avatarForm += "        </div>";
		avatarForm += "   </div>";
//    	avatarForm += "</form>";
    
    
    //更换头像弹出层
	function avatarLayer(){
		systemLayerShow("更换头像",avatarForm,666,520);//显示层
	}
    
    
    var width;// 裁剪框的宽度  
    var height;// 裁剪框的高度  
    var x;// 相对于裁剪图片x左边  
    var y;// 相对于裁剪图片y左边  
    
    //创建变量(在这个生命周期)的API和图像大小  
	var jcrop_api,
		boundx,//原图的宽
		boundy,//原图的高
		originalImage_realSrc,//原图路径 IE7-IE9使用
		$preview,
		$pcnt,
		$pimg,
		xsize,//预览窗口的宽
		ysize,//预览窗口的高
		
		$preview2,
		$pcnt2,
		$pimg2,
		xsize2,//预览窗口的宽
		ysize2,//预览窗口的高
		scaleFactor;//缩放比例
		
    //图像裁剪
	function imageCropp(bgColor){
		//获取预览窗格相关信息  
		$preview = $('#preview-pane'),
		$pcnt = $('#preview-pane .preview-container'),
		$pimg = $('#preview-pane .preview-container img'),
		xsize = $pcnt.width(),
		ysize = $pcnt.height();
		
		$preview2 = $('#preview-pane2'),
		$pcnt2 = $('#preview-pane2 .preview-container2'),
		$pimg2 = $('#preview-pane2 .preview-container2 img'),
		xsize2 = $pcnt2.width(),
		ysize2 = $pcnt2.height();
		$('#originalImage').Jcrop({
			onChange: updatePreview,//选框改变时的事件
		//	onSelect: updatePreview,//创建选框，参数格式为：[x,y,x2,y2]
			setSelect:[0,0,200,200],//创建默认选框，参数格式为：[x,y,x2,y2]
			aspectRatio: 1,//选框宽高比。说明：width/height
			boxWidth: 400,//画布宽度
			boxHeight: 400,//画布高度
			minSize: [200,200],//选框最小尺寸 格式： [0,0]
			bgColor: bgColor,//背景颜色  默认"black"	 透明#00000000
			allowSelect :false,//允许新选框
			allowResize :true//允许选框缩放
			
		} ,function(){
			//使用API来获得真实的图像大小  
			var bounds = this.getBounds();//获取图片实际尺寸，格式为：[w,h]
			boundx = bounds[0];
			boundy = bounds[1];
			
			//初始化预览图
			updatePreview(this.tellSelect());//tellSelect()获取选框的值（实际尺寸）。例子：console.log(jcrop_api.tellSelect())
				
				
			scaleFactor = this.getScaleFactor();//获取图片缩放的比例，格式为：[w,h]
			//jcrop_api变量中存储API  
			jcrop_api = this;
			//预览进入jcrop容器css定位  
			$preview.appendTo(jcrop_api.ui.holder);
			$preview2.appendTo(jcrop_api.ui.holder);
		});
		
	
	}

	
	/**
	 * 更新预览
	 * @param select 选区 选框的值（实际尺寸）
	 */
    function updatePreview(select){
		var browserVersion = window.navigator.userAgent.toUpperCase();
		if (browserVersion.indexOf("MSIE") > -1 && browserVersion.indexOf("MSIE 6") <= -1) {//IE7-IE9
			thumbnailPreview(select);
		}else{
			//设置预览
			if (parseInt(select.w) > 0) {
			 
				var rx = xsize / select.w;//select.w 代表选区的宽
				var ry = ysize / select.h;//select.h 代表选区的高
				$pimg.css({
					width: Math.round(rx * boundx) + 'px',
					height: Math.round(ry * boundy) + 'px', 
					marginLeft: '-' + Math.round(rx * select.x) + 'px',
					marginTop: '-' + Math.round(ry * select.y) + 'px'
				});
				
				var rx2 = xsize2 / select.w;//select.w 代表选区的宽
				var ry2 = ysize2 / select.h;//select.h 代表选区的高
				$pimg2.css({
					width: Math.round(rx2 * boundx) + 'px',
					height: Math.round(ry2 * boundy) + 'px', 
					marginLeft: '-' + Math.round(rx2 * select.x) + 'px',
					marginTop: '-' + Math.round(ry2 * select.y) + 'px'
				});
				
			}
		}


    	
		// 赋值  
        x = select.x;  
		y = select.y;  
        width = select.w;  
        height = select.h;  
	};
	/**
	 * ie7+缩略图预览
	 * @param select 选区
	 * @param bounds 实际内容
	 */
	function thumbnailPreview(select){
	
		//设置预览
		if (parseInt(select.w) > 0) {
			var rx = xsize / select.w;//select.w 代表选区的宽
			var ry = ysize / select.h;//select.h 代表选区的高
			$("#image_crop_preview_div").css(
				"filter",
				"progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + originalImage_realSrc + "')"
			);
			
			$("#image_crop_preview_div").css({
				width: Math.round(rx * boundx) + 'px',
				height: Math.round(ry * boundy) + 'px', 
				marginLeft: '-' + Math.round(rx * select.x) + 'px',
				marginTop: '-' + Math.round(ry * select.y) + 'px'
			});
			
			var rx2 = xsize2 / select.w;//select.w 代表选区的宽
			var ry2 = ysize2 / select.h;//select.h 代表选区的高
			
			$("#image_crop_preview_div2").css(
				"filter",
				"progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + originalImage_realSrc + "')"
			);

			$("#image_crop_preview_div2").css({
				width: Math.round(rx2 * boundx) + 'px',
				height: Math.round(ry2 * boundy) + 'px', 
				marginLeft: '-' + Math.round(rx2 * select.x) + 'px',
				marginTop: '-' + Math.round(ry2 * select.y) + 'px'
			});
		}
	}
	
	
	//选择文件
	function imgForm(file) { 
		if(!verificationImageFormat(file)){
			return;
		}
		if(jcrop_api){
			jcrop_api.destroy();//移除 Jcrop
		}
		
		readImagePath(file, function(src) {

			$("#avatarTag").html(avatarDiv); 

			var browserVersion = window.navigator.userAgent.toUpperCase();
			if (browserVersion.indexOf("MSIE") > -1 && browserVersion.indexOf("MSIE 6") <= -1) {//IE7-IE9
				originalImage_realSrc = src;
				
				//设置原图预览滤镜
				$("#image_crop_original_div").css(
						"filter",
						"progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + src + "')"
					);
				//获取图片实际大小（由于使用缩放滤镜scale必须指定div宽高，无法做到自适应，因此先通过image滤镜获取实际大小）
				var bounds = getImageBounds(src);
				//按比例缩放图片
				var param = clacImgZoomParam(400, 400, bounds.w, bounds.h);
				
			 	$("#image_crop_original_div").width(param.width);
				$("#image_crop_original_div").height(param.height);
				
				//需要对透明的img使用绝对定位并设置offset为预览div的offset，避免jcrop无法覆盖在预览div上面
				$("#originalImage").css("position","absolute");
				$("#originalImage").css("display","inline-block");
				$("#originalImage").offset($("#image_crop_original_div").offset());
				$("#originalImage").width(bounds.w);
				$("#originalImage").height(bounds.h);
				$("#originalImage").attr("src", blankImage);
				$("#originalImage").attr("realSrc", src);

				imageCropp("#00000000");
			}else{//支持html5浏览器
				$('#originalImage').attr('src', src);  
				$('#previewImage').attr('src', src);  
				$('#previewImage2').attr('src', src); 
				
				imageCropp("black");
			}
		});
	}

	/**
	 * 获取图片的实际大小
	 * @param tmpSrc 图片源
	 * @return {w,h} 宽和高
	 */
	function getImageBounds(tmpSrc){
		var imgObj = new Image();
		imgObj.src = tmpSrc;
		var width = imgObj.width;
		var height = imgObj.height;
		if((typeof width=="undefined" || width==0) && (typeof height=="undefined" || height==0)){
			var picpreview=document.getElementById("originalImage_box");
		    var tempDiv=document.createElement("div");
		    picpreview.appendChild(tempDiv);
		    tempDiv.style.width="10px";
		    tempDiv.style.height="10px";
		    tempDiv.style.diplay="none";
		    tempDiv.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src='" + tmpSrc + "');";
		    tempDiv.ID="previewTemp" + new Date().getTime();
		    width=tempDiv.offsetWidth;
		    height=tempDiv.offsetHeight;
		    picpreview.removeChild(tempDiv);
		}
		return {w:width,h:height};
	};
	
	/**
	 * 按比例缩放图片
	 * @param maxWidth 图片最大宽度
	 * @param maxHeight 图片最大高度
	 * @param width 图片实际宽度
	 * @param height 图片实际高度
	 * @return {width,height} 宽和高
	 */
	function clacImgZoomParam (maxWidth, maxHeight, width, height) {
		var param = {
			width : width,
			height : height
		};
		if (width > maxWidth || height > maxHeight) {
			var rateWidth = width / maxWidth;
			var rateHeight = height / maxHeight;

			if (rateWidth > rateHeight) {
				param.width = maxWidth;
				param.height = Math.round(height / rateWidth);
			} else {
				param.width = Math.round(width / rateHeight);
				param.height = maxHeight;
			}
		}

		param.left = Math.round((maxWidth - param.width) / 2);
		param.top = Math.round((maxHeight - param.height) / 2);
		return param;
	}
	
	
	//读取图片路径  file：file控件 prvid: 图片预览容器
	function readImagePath(file, callback) {
		var supportHTML5 = false;
		var browserVersion = window.navigator.userAgent.toUpperCase();
		if(file.files){
			supportHTML5 = true;
		}else{
			supportHTML5 = false;
		}
		//进行浏览器判断，选择对应的实现方式
		if(supportHTML5){
			for (var i = 0, f; f = file.files[i]; i++) {
				var fr = new FileReader();
				fr.onload = function(e) {
					var src = e.target.result;
					showPrvImg(src);
				}
				fr.readAsDataURL(f);
			}

		} else if (browserVersion.indexOf("MSIE") > -1 && browserVersion.indexOf("MSIE 6") > -1) {//IE6
			showPrvImg(file.value);
		} else if (browserVersion.indexOf("MSIE") > -1 && browserVersion.indexOf("MSIE 6") <= -1) {//IE7-IE9
			file.select();
			//如果是ie9需要触发blur事件，避免被安全规则阻拦
            if (browserVersion.indexOf("MSIE 9") > -1) {
                file.blur(); //不加上document.selection.createRange().text在ie9会拒绝访问
              //  document.getElementById(divPreviewId).focus(); //如果当前页面被嵌在框架中，则file.blur()之后，file控件中原本被选中的文本将会失去选中的状态  参考http://gallop-liu.iteye.com/blog/1344778
            }
			
			var reallocalpath = document.selection.createRange().text//IE下获取实际的本地文件路径
			showPrvImg(reallocalpath);
		} else if (browserVersion.indexOf("FIREFOX") > -1) {//FIREFOX
			var firefoxVersion = parseFloat(browserVersion.toLowerCase().match(/firefox\/([\d.]+)/)[1]);
			var src;
			if (firefoxVersion < 7) {// firefox7以下版本
				src = file.files[0].getAsDataURL();
			
			} else {// firefox7.0+
				src = window.URL.createObjectURL(file.files[0]);
			}
			showPrvImg(src);
		}

		function showPrvImg(src) {
			callback(src);
		}
	}
	
	
	
	//验证图片格式
    function verificationImageFormat(file) {
        var array = new Array('gif','jpg', 'jpeg', 'png', 'bmp'); //可以上传的文件类型
        if (file.value == '') {
	        layer.open({
			    type: 1, 
			    title:"错误",
			    content: "文件不能为空", 
			    area: ['260px', '120px'],//宽高
				success: function(layero, index){
					 return false;
				}
			});
        }else {
        	//获取最后一个.的位置
			var index= file.value.lastIndexOf(".");
			//获取后缀
			var ext = file.value.substr(index+1);
			
            //循环判断图片的格式是否正确
            for (var i in array) {
                if (ext.toLowerCase() == array[i].toLowerCase()) {
                    return true;
                }
            }
            layer.open({
			    type: 1, 
			    title:"错误",
			    content: "<div style='line-height: 30px; font-size: 14px; margin-left: 8px;margin-right: 8px;'>只允许上传gif、jpg、jpeg、bmp、png格式</div>", 
			    area: ['260px', '120px'],//宽高
				success: function(layero, index){
					 return false;
				}
			});
           
        }
    }

    //提交上传图片
	function uploadImageSubmit(){
		
		//按钮设置 disabled="disabled"
		document.getElementById("uploadSubmit").disabled=true;
		if(width == null){
			layer.open({
			    type: 1, 
			    title:"错误",
			    content: "<div style='line-height: 30px; font-size: 14px; margin-left: 8px;margin-right: 8px;'>请先选择图片</div>", 
			    area: ['260px', '120px'],//宽高
				success: function(layero, index){
					 
				}
			});
			//按钮设置 disabled="disabled"
			document.getElementById("uploadSubmit").disabled=false;
			return false;
			
		}
		//Math.round(number)四舍五入取整
		var _width = Math.round(width);
		var _height = Math.round(height);
		var _x = Math.round(x);
		var _y = Math.round(y);
		if(_x <0){
		 	_x =0;
		}
		if(_y <0){
			_y =0;
		}
		
		var context = "&width="+_width +"&height="+_height+"&x="+_x+"&y="+_y;

		var userId = getUrlParam("id");//获取URL参数
		context += "&id="+userId;

		$("#uploadImageForm").ajaxSubmit({
	        dataType:'json',//提交成功后返回的数据格式，可选值包括xml，json或者script
	        type:'POST',// 提交类型可以是"GET"或者"POST"
	        url:'control/user/manage.htm?method=updateAvatar'+context,// 表单提交的路径
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
	        			if(data[returnValue] == "true"){
	        				layer.msg('更新头像成功,3秒后刷新页面', 
								{
								  time: 3000 //3秒关闭（如果不配置，默认是3秒）
								},function(){
									//关闭后的操作
						        	document.location.reload();//刷新页面
								}
							);
	        			}
	        		}else if(returnValue == "error"){
	        			var errorValue = data[returnValue];
						var htmlValue = "";
						var i = 0;
						for(var error in errorValue){
							if(error != ""){	
								i++;
								htmlValue += "&nbsp;&nbsp;"+i+"、"+errorValue[error]+"<br>";
							}
						}
						
						layer.open({
						  type: 1,
						  title: '错误', 
						  skin: 'layui-layer-rim', //加上边框
						  area: ['300px', '150px'], //宽高
						  content: htmlValue
						});
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
</script>


  
</HEAD>

<BODY>
<!-- IE6 会弹出'已终止操作'错误，本JS要放在Body标签下面 -->
<script type="text/javascript" src="backstage/spin/spin.min.js" ></script>
<script language="JavaScript" src="backstage/layer/layer.js" ></script>

<form:form id="uploadImageForm" enctype="multipart/form-data" method="post" >

<input type="hidden" name="userVersion" value="${user.userVersion}">
<DIV class="d-box">
<div class="d-button">
	<c:if test="${param.jumpStatus == null || param.jumpStatus == '' || param.jumpStatus >0}">
		<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/list${config:suffix()}?queryState=${param.queryState}&page=${param.userPage}'" value="返回">
	</c:if>
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allTopic&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=10'" value="发表的话题">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allComment&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=20'" value="发表的评论">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/user/manage${config:suffix()}?method=allReply&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}&page=${param.page}&origin=30'" value="发表的回复">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/privateMessage/manage${config:suffix()}?method=privateMessageList&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="私信">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/systemNotify/manage${config:suffix()}?method=subscriptionSystemNotifyList&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="系统通知">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/remind/manage${config:suffix()}?method=remindList&userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="提醒">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/favorite/list${config:suffix()}?userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="收藏夹">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/pointLog/list${config:suffix()}?userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="积分日志">
	<input class="functionButton" type="button" onClick="javascript:window.location.href='${config:url(pageContext.request)}control/userLoginLog/list${config:suffix()}?userName=${user.userName}&id=${param.id}&queryState=${param.queryState}&jumpStatus=${param.jumpStatus}&userPage=${param.userPage}'" value="登录日志">
	<input class="functionButton" type="button" onclick="javascript: avatarLayer(); return false;" value="更换头像">
</div>


<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
  <TBODY>
   <TR>
    <TD class="t-label t-label-h" width="12%">会员用户名：</TD>
    <TD class="t-content" width="63%" colSpan="2">
    	${user.userName}
    </TD>
    <TD class="t-content"  rowspan="6">
    	<c:if test="${user.avatarName != null}">
    		<img  src="${user.avatarPath}${user.avatarName}?${user.userVersion}" width="180px" height="180px"/>
    	</c:if>
    </TD>
    </TR> 
    <TR>
    <TD class="t-label t-label-h" width="12%">等级：</TD>
    <TD class="t-content" width="63%" colSpan="2">
    	${user.gradeName}
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">Email地址：</TD>
    <TD class="t-content" width="63%" colSpan="2">
    	${user.email}
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">密码提示问题：</TD>
    <TD class="t-content" width="63%" colSpan="2">
    	${user.issue}
    </TD>
  </TR>
  
  <TR>
    <TD class="t-label t-label-h" width="12%">用户状态：</TD>
    <TD class="t-content" width="63%" colSpan="2">
		<c:if test="${ user.state eq 1 || user.state eq 11}">正常用户</c:if>
		<c:if test="${ user.state eq 2 || user.state eq 12}">禁止用户</c:if>
    </TD>
  </TR>
  <TR>
    <TD class="t-label t-label-h" width="12%">当前积分：</TD>
    <TD class="t-content" width="88%" colSpan="2">
    	${user.point}
    </TD></TR>
	<TR>
    <TD class="t-label t-label-h" width="12%">备注：</TD>
    <TD class="t-content" width="88%" colSpan="3">
    	${user.remarks }
    </TD>
  </TR>
	<!-- 用户自定义注册功能项 -->
	<c:forEach items="${userCustomList}" var="entry" varStatus="status">
		<TR>
	    <TD class="t-label t-label-h" width="12%">${entry.name}：</TD>
	    <TD class="t-content" width="88%" colSpan="3">
			<c:if test="${entry.chooseType ==1}">
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>${userInputValue.content}</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==2}">
				
				<c:forEach items="${entry.itemValue}" var="itemValue" varStatus="status">
					<!-- 选中项 -->
					<c:set var="_checked" value=""></c:set>
					<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
						<c:if test="${userInputValue.options == itemValue.key}">
							<c:set var="_checked" value=" checked='checked'"></c:set>
						</c:if>
					</c:forEach>
				
					<!-- 默认选第一项 -->				
					<input type="radio" name="userCustom_${entry.id}" disabled="disabled" value="${itemValue.key}" ${_checked} <c:if test="${fn:length(entry.userInputValueList)==0 && status.count == 1}"> checked='checked'</c:if>>${itemValue.value}
				</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==3}">
				<c:forEach items="${entry.itemValue}" var="itemValue">
					<c:set var="_checked" value=""></c:set>
					<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
						<c:if test="${userInputValue.options == itemValue.key}">
							<c:set var="_checked" value=" checked='checked'"></c:set>
						</c:if>
					</c:forEach>
				
				
					<input type="checkbox" name="userCustom_${entry.id}" disabled="disabled" value="${itemValue.key}" ${_checked}>${itemValue.value}
				</c:forEach>
			</c:if>
			<c:if test="${entry.chooseType ==4}">
			
				<select name="userCustom_${entry.id}" disabled="disabled" <c:if test="${entry.multiple == true}"> multiple='multiple'</c:if> <c:if test="${entry.selete_size != null}"> size='${entry.selete_size}'</c:if>>
					<c:forEach items="${entry.itemValue}" var="itemValue">
						<c:set var="_selected" value=""></c:set>
						<c:forEach items='${entry.userInputValueList}' var='userInputValue'>
							<c:if test="${userInputValue.options == itemValue.key}">
								<c:set var="_selected" value=" selected='selected'"></c:set>
							</c:if>
						</c:forEach>
					
						<option value="${itemValue.key}" ${_selected}>${itemValue.value}</option>		
					</c:forEach>	
				</select>
			</c:if>
			<c:if test="${entry.chooseType ==5}">
				<c:forEach items='${entry.userInputValueList}' var='userInputValue'>${userInputValue.content}</c:forEach>		
			</c:if>
	    </TD>
	  </TR>
	  </c:forEach>
</TBODY></TABLE>
</DIV>
</form:form>
</BODY></HTML>



