//本地图像预览
/**
function previewImage(file,divId,imgId){
  var MAXWIDTH  = 100;
  var MAXHEIGHT = 100;
  var div = document.getElementById('preview');
  if (file.files && file.files[0]){
  	div.innerHTML = '<img id=imghead>';
  	var img = document.getElementById('imghead');
  	img.onload = function(){
  	  var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
  	  img.width = rect.width;
  	  img.height = rect.height;
  	  img.style.marginLeft = rect.left+'px';
  	  img.style.marginTop = rect.top+'px';
  	};
  	var reader = new FileReader();
  	reader.onload = function(evt){img.src = evt.target.result;}
  	reader.readAsDataURL(file.files[0]);
  }else{
	 
    var sFilter='filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';
    file.select();
    var src = document.selection.createRange().text;
    div.innerHTML = '<img id=imghead>';
    var img = document.getElementById('imghead');
 //   alert(img);
    alert(img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src);
    img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src = src;
    
    
    var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
    status =('rect:'+rect.top+','+rect.left+','+rect.width+','+rect.height);
    div.innerHTML = "<div id=divhead style='width:"+rect.width+"px;height:"+rect.height+"px;margin-top:"+rect.top+"px;margin-left:"+rect.left+"px;"+sFilter+src+"\"'></div>";
  }
}
function clacImgZoomParam( maxWidth, maxHeight, width, height ){
	var param = {top:0, left:0, width:width, height:height};
	if( width>maxWidth || height>maxHeight ){
		rateWidth = width / maxWidth;
		rateHeight = height / maxHeight;
		
		if( rateWidth > rateHeight ){
			param.width =  maxWidth;
			param.height = Math.round(height / rateWidth);
		}else{
			param.width = Math.round(width / rateHeight);
			param.height = maxHeight;
		}
	}
	
	param.left = Math.round((maxWidth - param.width) / 2);
	param.top = Math.round((maxHeight - param.height) / 2);
	return param;
}**/

//js本地图片预览，兼容ie[6-8]、火狐、Chrome17+、Opera11+、Maxthon3
function previewImage(fileObj,imgPreviewId,divPreviewId){
 //   var allowExtention=document.getElementById("hfAllowPicSuffix").value;//.jpg,.bmp,.gif,.png,允许上传文件的后缀名
	var allowExtention=".jpg,.jpeg,.bmp,.gif,.png,";
	var extention=fileObj.value.substring(fileObj.value.lastIndexOf(".")+1).toLowerCase();            
    var browserVersion= window.navigator.userAgent.toUpperCase();
    if(allowExtention.indexOf(extention)>-1){  
    	
    	
        if (browserVersion.indexOf("MSIE")>-1){
            if(browserVersion.indexOf("MSIE 6.0")>-1){//ie6
                document.getElementById(imgPreviewId).setAttribute("src",fileObj.value);
            }else{//ie[7-8]、ie9未测试
            	
            	/**
                fileObj.select();
                var newPreview =document.getElementById(divPreviewId+"New");
                if(newPreview==null){
                    newPreview =document.createElement("div");
                    newPreview.setAttribute("id",divPreviewId+"New");
                    newPreview.style.width = document.getElementById(imgPreviewId).style.width;
                    newPreview.style.height = document.getElementById(imgPreviewId).style.height;
                    newPreview.style.border="solid 1px #d2e2e2";
                }
                
                newPreview.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + document.selection.createRange().text + "')";                            
               var tempDivPreview=document.getElementById(divPreviewId);
               alert(tempDivPreview.id);
                 tempDivPreview.parentNode.insertBefore(newPreview,tempDivPreview);
                 tempDivPreview.style.display="none";**/
                
            	fileObj.select();                         
            	if (browserVersion.indexOf("MSIE 9") > -1)                            
            		fileObj.blur(); //不加上document.selection.createRange().text在ie9会拒绝访问                        
            	var newPreview = document.getElementById(divPreviewId + "New");                 
            	
            	if (newPreview == null) {                       
            		newPreview = document.createElement("div");               
            		newPreview.setAttribute("id", divPreviewId + "New");           
            		newPreview.style.width = document.getElementById(imgPreviewId).width + "px";    
            		newPreview.style.height = document.getElementById(imgPreviewId).height + "px";   
            		newPreview.style.border = "solid 1px #d2e2e2";                       
            	} 
            	
            	newPreview.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + document.selection.createRange().text + "')";              
            	var tempDivPreview = document.getElementById(divPreviewId);                     
            	tempDivPreview.parentNode.insertBefore(newPreview, tempDivPreview);            
            	tempDivPreview.style.display = "none"; 
 

              
            }
        }else if(browserVersion.indexOf("FIREFOX")>-1){//firefox
            var firefoxVersion= parseFloat(browserVersion.toLowerCase().match(/firefox\/([\d.]+)/)[1]);
            if(firefoxVersion<7){//firefox7以下版本
                document.getElementById(imgPreviewId).setAttribute("src",fileObj.files[0].getAsDataURL());
            }else{//firefox7.0+                    
                document.getElementById(imgPreviewId).setAttribute("src",window.URL.createObjectURL(fileObj.files[0]));
            }
        }else if(fileObj.files){               
            //兼容chrome、火狐等，HTML5获取路径                   
            if(typeof FileReader !== "undefined"){
                var reader = new FileReader(); 
                reader.onload = function(e){
                    document.getElementById(imgPreviewId).setAttribute("src",e.target.result);
                }  
                reader.readAsDataURL(fileObj.files[0]);
            }else if(browserVersion.indexOf("SAFARI")>-1){
            	alert("不支持Safari6.0以下浏览器的图片预览!"); 
            }
        }else{
            document.getElementById(divPreviewId).setAttribute("src",fileObj.value);
        }         
    }else{
        alert("仅支持"+allowExtention+"为后缀名的文件!");
        fileObj.value="";//清空选中文件
        if(browserVersion.indexOf("MSIE")>-1){                        
            fileObj.select();
            document.selection.clear();
        }                
        fileObj.outerHTML=fileObj.outerHTML;
    }
}

