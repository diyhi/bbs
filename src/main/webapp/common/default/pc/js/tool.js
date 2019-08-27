

//获取系统base路径
function getBasePath(){
	var paras = document.getElementsByTagName("base");
	return paras[0]["href"];
}


//jquery请求session过期跳转
function timeoutJump(XMLHttpRequest){
	if(XMLHttpRequest.status == 508){//服务器处理请求时检测到一个无限循环
		return;
	}
	if(XMLHttpRequest.status == 403){//权限不足
		alert("权限不足");
		return;
	}
	if(XMLHttpRequest.status == 400){//请求错误
		alert("请求错误");
		return;
	}
	if(XMLHttpRequest.getResponseHeader("jumpPath") != null && XMLHttpRequest.getResponseHeader("jumpPath") != ""){//session登陆超时登陆页面响应http头
 		//收到未登陆标记，执行登陆页面跳转
 		window.location.href= getBasePath()+XMLHttpRequest.getResponseHeader("jumpPath");
 		
 		return;
 	}
}


/**
 * 获取URL参数
 * @param name 参数名称
 * @returns
 */
function getUrlParam(name){
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r!=null) return unescape(r[2]); return null; //返回参数值
} 
/**
 * 删除URL参数
 */
function deleteUrlParam(url,paramKey){
  var urlParam = url.substr(url.indexOf("?")+1);
  var beforeUrl = url.substr(0,url.indexOf("?"));
  var nextUrl = "";
   
  var arr = new Array();
  if(urlParam!=""){
      var urlParamArr = urlParam.split("&");
    
      for(var i=0;i<urlParamArr.length;i++){
          var paramArr = urlParamArr[i].split("=");
          if(paramArr[0]!=paramKey){
              arr.push(urlParamArr[i]);
          }
      }
  }
   
  if(arr.length>0){
      nextUrl = "?"+arr.join("&");
  }
  url = beforeUrl+nextUrl;
  return url;
}

/**
 * 读取Csrf参数
 * <meta name="csrfToken" content="${token}"/>
 */
function getCsrf(){
	var meta = document.getElementsByTagName("meta");
	for(var i=0;i <meta.length;i++){  
		if(meta[i].name == "csrfToken"){
			return meta[i].getAttribute("content");
		}
	}  
	//var token = document.getElementsByTagName("meta")["csrfToken"].getAttribute("content");//IE7获取不到信息
	//$('meta[name="csrfToken"]').attr("content")
	return "";  
}



//构造新URI   url:url中"?"符后的字串    replace:替换字符     value:值
function newURI(uri,replace,value) {
	var newURL = "";
	//是否存在
	var isExist = false;
	if (uri.indexOf("?") != -1) {
		var str = uri.substr(1);
		var strs = str.split("&");
		for(var i = 0; i < strs.length; i ++) {
			if(strs[i] !=""){
				if(replace == strs[i].split("=")[0]){
					isExist = true;
					newURL= newURL+ "&"+strs[i].split("=")[0]+"="+value;
				}else{
					newURL= newURL+ "&"+strs[i].split("=")[0]+"="+strs[i].split("=")[1];
				}
			}
		}
	}
	if(isExist == false){
		newURL= newURL+ "&"+replace+"="+value;
	}
	//删除第一个&
	if(newURL.length >0){
		newURL= newURL.substr(1);
	}
 	if(newURL.length >0){
 		return "?"+newURL;
 	}else{
 		return newURL;
 	}
}

//到指定的分页页面
function topage(page){
	var url = "";
	//通讯协议
	var protocol = window.location.protocol;
	url = url+protocol+"//";
	//主机
	var host = window.location.host;  
	url = url+host;
	//路径部分
	var pathname = window.location.pathname;  
	url = url+pathname;
	
	var uri = location.search; //获取url中"?"符后的字串
	//参数
	var parameters = newURI(uri,"page",page);
	url = url+parameters;

	window.location.href = url;
}

//UTF8字符集实际长度计算 
function getStringLeng(str){    
	var realLength = 0;     
	var len = str.length;     
	var charCode = -1;     
	for(var i = 0; i < len; i++){         
		charCode = str.charCodeAt(i);         
		if (charCode >= 0 && charCode <= 128) {              
			realLength += 1;        
		}else{   
			// 如果是中文则长度加3             
			realLength += 3;       
		}    
	}     
	return realLength; 
} 
//去掉字符串前后空格
function trim(str){   

    str = str.replace(/^(\s|\u00A0)+/,'');   
    for(var i=str.length-1; i>=0; i--){   
        if(/\S/.test(str.charAt(i))){   
            str = str.substring(0, i+1);   
            break;   
        }   
    }   
    return str; 
}  
		
		
//取得表格的伪属性("类型:如tr;td ","name值")
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



/**
 * 精确计算
 */
function getDigits(num) {
	var digits = 0,
		parts = num.toString().split(".");
	if (parts.length === 2) {
		digits = parts[1].length;
	}
	return digits;
}

function toFixed(num, digits) {
	if (typeof digits == 'undefined') {
		return num;
	}
	return Number(num).toFixed(digits);
}
/**
 * 加法函数
 * arg1：加数；arg2加数；digits要保留的小数位数（可以为空，为空则不处理小数位数）
 */
function calc_add(arg1, arg2, digits) {
	arg1 = arg1.toString(), arg2 = arg2.toString();
	var maxLen = Math.max(getDigits(arg1), getDigits(arg2)),
		m = Math.pow(10, maxLen),
		result = Number(((arg1 * m + arg2 * m) / m).toFixed(maxLen));
	return toFixed(result, digits);
}
;
/**
 * 减法函数
 * arg1：减数；arg2：被减数；digits要保留的小数位数（可以为空，为空则不处理小数位数）
 */
function calc_sub(arg1, arg2, digits) {
	return calc_add(arg1, -Number(arg2), digits);
}
;
/**
 * 乘法函数
 * arg1：乘数；arg2乘数；digits要保留的小数位数（可以为空，为空则不处理小数位数）
 */
function calc_multiply(arg1, arg2, digits) {
	// 数字化
	var num1 = parseFloat(arg1).toString(),
		num2 = parseFloat(arg2).toString(),
		m = getDigits(num1) + getDigits(num2),
		result = num1.replace(".", "") * num2.replace(".", "") / Math.pow(10, m);
	return toFixed(result, digits);
}
;
/**
 * 除法函数
 * arg1：除数；arg2被除数；digits要保留的小数位数（可以为空，为空则不处理小数位数）
 */
function calc_div(arg1, arg2, digits) {
	// 数字化
	var num1 = parseFloat(arg1).toString(),
		num2 = parseFloat(arg2).toString(),
		t1 = getDigits(num1),
		t2 = getDigits(num2),
		result = num1.replace(".", "") / num2.replace(".", "") * Math.pow(10, t2 - t1)
	return toFixed(result, digits);
}
