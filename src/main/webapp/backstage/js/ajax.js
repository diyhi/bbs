/**
 * GET方式提交
 * @param callback 
 * @param urladdress 提交URL
 * @param async true（异步）或 false（同步）
 */
function get_request(callback, urladdress, async){      
	startLoading();//显示加载层
	var xmlhttp = getXMLHttpRequest();
        
        xmlhttp.onreadystatechange = function(){
            	if (xmlhttp.readyState == 4) {//readystate 
            		stopLoading();//关闭加载层
 				    try{
 				    	
 				    	if(xmlhttp.getResponseHeader("login") != null && xmlhttp.getResponseHeader("login") != ""){//session登陆超时登陆页面响应http头
 				    		
 				    		//收到未登录标记，执行登录页面跳转
 				    		window.location.href= xmlhttp.getResponseHeader("login");
 				    		return;
 				    	}
 				    	
 				    	if(xmlhttp.status == 400){//请求错误
 				    		alert("请求错误");
 				    		return;
 				    	}
				    	if(xmlhttp.status == 200){
							callback(xmlhttp.responseText);		
							
						}else if(xmlhttp.status == 403){
							alert("没有权限");					
						}else{
							callback("没找到此页面:"+ urladdress +"");
						}
			        } catch(e){
			        	callback("发送请求失败，请重试" + e);
			        }
			   }
        };
        xmlhttp.open("GET", urladdress, async);
        xmlhttp.setRequestHeader("X-Requested-With","XMLHttpRequest");//标记报头为AJAX
        xmlhttp.send(null);
}
/**
 * POST方式提交
 * @param callback
 * @param urladdress 提交URL
 * @param async true（异步）或 false（同步）
 * @param formContent 表单内容
 */
function post_request(callback, urladdress, async,formContent){     
	
	startLoading();//显示加载层
	
    var xmlhttp = getXMLHttpRequest();
    xmlhttp.open('POST', urladdress, async);
    //定义传输的文件HTTP头信息    
	xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded"); 
	xmlhttp.setRequestHeader("X-Requested-With","XMLHttpRequest");//标记报头为AJAX
	//如果含有X-CSRF-TOKEN参数
	if(formContent != ""){
		var csrf_token = getSubmitParam(formContent,"_csrf_token");
		var csrf_header = getSubmitParam(formContent,"_csrf_header");
		if(csrf_token != null && csrf_token != "" && csrf_header != null && csrf_header != ""){
			xmlhttp.setRequestHeader(csrf_header,csrf_token);//将csrf相关参数添加到报头
		}
	}

	xmlhttp.onreadystatechange = function(){
        if (xmlhttp.readyState == 4) {//readystate 
        		
        		stopLoading();//关闭加载层
        		if(xmlhttp.getResponseHeader("login") != null && xmlhttp.getResponseHeader("login") != ""){//session登陆超时登陆页面响应http头
        			
		    		//收到未登陆标记，执行登陆页面跳转
		    		window.location.href= xmlhttp.getResponseHeader("login");
		    		return;
		    	}
        		if(xmlhttp.status == 400){//请求错误
		    		alert("请求错误");
		    		return;
		    	}
			    try{
			    	
			    	
			    	
			    	if(xmlhttp.status == 200){
						
							
						callback(xmlhttp.responseText);
							
						
					}else if(xmlhttp.status == 403){
						alert("没有权限");					
					}else{
						callback("没找到此页面:"+ urladdress +"");
					}
		        } catch(e){
		        	callback("发送请求失败，请重试" + e);
		        }
		}
	};
   //     alert(div_Form);
    //发送POST数据    
	xmlhttp.send(formContent);  
}


/**
 * 获取提交参数值
 * @param submitContent 提交内容
 * @param name 参数名称
 * @return
 */
function getSubmitParam(submitContent,name){
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = submitContent.match(reg);  //匹配目标参数
	if (r!=null) return unescape(r[2]); return null; //返回参数值
} 


function getXMLHttpRequest() {
        var xmlhttp;
		if (window.XMLHttpRequest) {
			try {
				xmlhttp = new XMLHttpRequest();
				xmlhttp.overrideMimeType("text/html;charset=UTF-8");//
			} catch (e) {}
		} else if (window.ActiveXObject) {
			try {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {
				try {
					xmlhttp = new ActiveXObject("Msxml2.XMLHttp");
				} catch (e) {
					try {
						xmlhttp = new ActiveXObject("Msxml3.XMLHttp");
					} catch (e) {}
				}
			}
		}
        return xmlhttp;
}


var spinner = null;

//显示加载中
function startLoading(){ 
	if(spinner != null){
		return;
	}
	var height = getClientHeight()/2+getScrollTop();//图标在浏览器的高度

	//加载中图标配置
	var spinnerOpts = {
		lines:11, // 圆圈中线条的数量
		length: 5, // 每条线的长度
		width: 6, //每条线的宽度
		radius: 10, //每条线的圆角半径
		corners: 1, //角落圆度，从0到1
		rotate: 0, //旋转偏移量
		direction: 1, //旋转方向，其中1表示顺时针，0表示逆时针
		color: '#3C3E44', // 颜色
		speed: 1, //旋转速率，单位为转速/秒
		trail: 60, //余晖的百分比，即颜色变化的百分比
		shadow: false, //是否显示阴影
		hwaccel: false, //是否使用硬件加速
		className: 'spinner', // 绑定到spinner上的class名称
//		position:'relative',  // 定义spinner的位置类型，和css里的position一样
		zIndex: 2e9, //定位层，默认值是2000000000
		top: height+'px', // 相对父元素的向上定位，单位是px
		left: '50%' // 相对父元素的向左定位，单位是px
	};
	spinner = new Spinner(spinnerOpts).spin();
	
	var spinTarget = document.getElementById('loadingBody');
	if(spinTarget == null){
		//添加DIV层到Body
		var div =document.createElement("div");
		document.body.appendChild(div);
		div.id = "loadingBody";
		spinTarget = document.getElementById('loadingBody');
	}
//	spinner.spin(spinTarget);
	spinTarget.appendChild(spinner.el);
}  
//关闭加载中
function stopLoading(){ 
	if(spinner == null){
		return;
	}
    spinner.stop(); 
    spinner = null;
} 

//获取窗口可视范围的高度
function getClientHeight(){   
    var clientHeight=0;   
    if(document.body.clientHeight&&document.documentElement.clientHeight){   
        clientHeight=(document.body.clientHeight<document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;           
    }else{   
        clientHeight=(document.body.clientHeight>document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;       
    }   
    return clientHeight;   
}
//获取窗口滚动条高度
function getScrollTop(){   
    var scrollTop=0;   
    if(document.documentElement&&document.documentElement.scrollTop){   
        scrollTop=document.documentElement.scrollTop;   
    }else if(document.body){   
        scrollTop=document.body.scrollTop;   
    }   
    return scrollTop;   
}
/**
//获取文档内容实际高度(本方法未用)
function getScrollHeight(){   
    return Math.max(document.body.scrollHeight,document.documentElement.scrollHeight);   
}**/