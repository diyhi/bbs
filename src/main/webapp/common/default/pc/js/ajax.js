/**
 * GET方式提交
 * @param callback 回调
 * @param urladdress 提交地址
 * @param async 是否异步 true:异步  false:同步
 */
function get_request(callback, urladdress, async){      
        var xmlhttp = getXMLHttpRequest();
        
        xmlhttp.onreadystatechange = function(){
            	if (xmlhttp.readyState == 4) {//readystate 
 				    try{
 				  //  	alert("pppp1"+xmlhttp.getResponseHeader("jumpPath"));
 				    	if(xmlhttp.status == 508){//服务器处理请求时检测到一个无限循环
 				    		return;
 				    	}
 				    	
 				    	
 				    	if(xmlhttp.getResponseHeader("jumpPath") != null && xmlhttp.getResponseHeader("jumpPath") != ""){//session登陆超时登陆页面响应http头
 				    		//收到未登陆标记，执行登陆页面跳转
 				    	//	window.location.href= xmlhttp.getResponseHeader("login");
 				    		
 				    	//	alert(getBasePath()+xmlhttp.getResponseHeader("jumpPath"));
 				    		window.location.href= getBasePath()+xmlhttp.getResponseHeader("jumpPath");
 				    		
 				    		return;
 				    	}
 				    	if(xmlhttp.status == 400){//请求错误
 				    		alert("请求错误");
 				    		return;
 				    	}
 				    	if(xmlhttp.status == 403){//权限不足
 				    		alert("权限不足");
 				    		return;
 				    	}
 				    	
				    	if(xmlhttp.status == 200){
							callback(xmlhttp.responseText);	
						}else{
							callback("没找到此页面:"+ urladdress +"");
						}
			        } catch(e){
			        	callback("发送请求失败，请重试" + e);
			        }
			   }
        };
        xmlhttp.open("GET", urladdress, async);
        xmlhttp.setRequestHeader("X-Requested-With","XMLHttpRequest");//标记报头为AJAX方式
        xmlhttp.send(null);
}

/**
 * POST方式提交
 * @param callback 回调
 * @param urladdress 提交地址
 * @param async 是否异步 true:异步  false:同步
 * @param params 提交参数 格式："v1=" + value1 + "&v2=" + value2
 */
function post_request(callback, urladdress, async,params){      
        var xmlhttp = getXMLHttpRequest();
        xmlhttp.open('POST', urladdress, async);
        //定义传输的文件HTTP头信息    
		xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded"); 
		xmlhttp.setRequestHeader("X-Requested-With","XMLHttpRequest");//标记报头为AJAX方式
        xmlhttp.onreadystatechange = function(){
        	if (xmlhttp.readyState == 4) {//readystate 
        	//	alert("ssa"+getBasePath()+xmlhttp.getResponseHeader("jumpPath"));
 				    try{
 				    	if(xmlhttp.status == 508){//服务器处理请求时检测到一个无限循环
 				    		return;
 				    	}
 				    	if(xmlhttp.getResponseHeader("jumpPath") != null && xmlhttp.getResponseHeader("jumpPath") != ""){//session登陆超时登陆页面响应http头
 				 //   		alert("ss"+getBasePath()+xmlhttp.getResponseHeader("jumpPath"));
 				    		//收到未登陆标记，执行登陆页面跳转
 				    		window.location.href= getBasePath()+xmlhttp.getResponseHeader("jumpPath");
 				    		return;
 				    	}
 				    	if(xmlhttp.status == 403){//权限不足
 				    		alert("权限不足");
 				    		return;
 				    	}
 				    	if(xmlhttp.status == 400){//请求错误
 				    		alert("请求错误");
 				    		return;
 				    	}
				    	if(xmlhttp.status == 200){
							
							callback(xmlhttp.responseText);
								
							
						}else{
							callback("没找到此页面:"+ urladdress +"");
						}
			        } catch(e){
			        	callback("发送请求失败，请重试" + e);
			        }
			   }
        };
        //发送POST数据    
 		xmlhttp.send(params);  
}

function getXMLHttpRequest() {
        var xmlhttp = null;
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
