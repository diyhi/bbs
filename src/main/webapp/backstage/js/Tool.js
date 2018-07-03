
//获取系统base路径
function getBasePath(){
	var paras = document.getElementsByTagName("base");
	return paras[0]["href"];
}


//jquery请求session过期跳转
function timeoutJump(XMLHttpRequest){
	if(XMLHttpRequest.status == 403){
		window.location.href = getBasePath()+"admin/login.htm";
	}
	if(XMLHttpRequest.status == 400){
		alert("请求错误");
	}
}

//读取Csrf参数
function getCsrf(){
	var token = "";
	var header = "";
	var meta = document.getElementsByTagName("meta");
	for(var i=0;i <meta.length;i++){  
		if(meta[i].name == "_csrf_token"){
			token = meta[i].getAttribute("content");
		}
		if(meta[i].name == "_csrf_header"){
			header = meta[i].getAttribute("content");
		}
	}
	var obj = new Object();
	obj.token = token;
	obj.header = header;
    return obj;  
    /**
	var token = document.getElementsByTagName("meta")["_csrf_token"].getAttribute("content");
	var header = document.getElementsByTagName("meta")["_csrf_header"].getAttribute("content");
	var obj = new Object();
	obj.token = token;
	obj.header = header;
    return obj;  **/
}

/**
//判断IE6到IE9版本
function getIeVersion(){
	var isIE = function(ver){
	    var b = document.createElement('b')
	    b.innerHTML = '<!--[if IE ' + ver + ']><i></i><![endif]-->'
	    return b.getElementsByTagName('i').length === 1
	}
	if(isIE(6)){// IE6
	    return "IE6";
	}
	if(isIE(7)){// IE7
		return "IE7";
	}
	if(isIE(8)){// IE8
		return "IE8";
	}
	if(isIE(9)){// IE9
		return "IE9";
	}
	if(isIE()){// //如果只想检测是不是IE，而不关心浏览器版本，那只需要在调用函数的时候，不传递参数即可
		return "IE";
	}
}
**/







/**
 * 获取URL (问号前面部分)
 * @returns
 */
function getUrl(){
	var url = "";
	url += window.location.protocol+"//";//协议
	url += window.location.host;//域名当端口不是 80 时，location.host 还会包含端口
	url += window.location.pathname;//路径
	return url;
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
 * 删除url参数
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

//分页URL replace:替换字符  page:页数
function pageURL(replace,page) {
	var url = location.search; //获取url中"?"符后的字串
	var newURL = "";
	//是否存在
	var isExist = false;
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		var strs = str.split("&");
		for(var i = 0; i < strs.length; i ++) {
			if(strs[i] !=""){
				if(replace == strs[i].split("=")[0]){
					isExist = true;
					newURL= newURL+ "&"+strs[i].split("=")[0]+"="+page;
				}else{
					newURL= newURL+ "&"+strs[i].split("=")[0]+"="+strs[i].split("=")[1];
				}
			}
		}
	}
	if(isExist == false){
		newURL= newURL+ "&"+replace+"="+page;
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

/**
 * 替换URL参数
 * url 目标url
 * param 需要替换的参数名称
 * paramVal 替换后的参数的值
 * 返回值为新的url
 * example: updateURLParameter('www.baidu.com?id=12','id','13') 返回结果为 'www.baidu.com?id=13'
 * @returns
 */
function updateURLParameter(url, param, paramVal){
    var newAdditionalURL = "";
    var tempArray = url.split("?");
    var baseURL = tempArray[0];
    var additionalURL = tempArray[1];
    var temp = "";
    
    if (additionalURL) {
        tempArray = additionalURL.split("&");
        for (i=0; i<tempArray.length; i++){
            if(tempArray[i].split('=')[0] != param){
                newAdditionalURL += temp + tempArray[i];
                temp = "&";
            }
        }
    }

    var rows_txt = temp + "" + param + "=" + paramVal;
    return baseURL + "?" + newAdditionalURL + rows_txt;
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

/**  
 * 对日期进行格式化(使用方法dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss'))
 * @param date 要格式化的日期  *
 * @param format 进行格式化的模式字符串 
 * 支持的模式字母有：  y:年, M:年中的月份(1-12), d:月份中的天(1-31),  h:小时(0-23), m:分(0-59),  s:秒(0-59),  S:毫秒(0-999),q:季度(1-4) 
 * @return String 
 * */
function dateFormat(date, format) {    
	if(format === undefined){       
		format = date;        
		date = new Date();    
	}    
	var map = {        
		"M": date.getMonth() + 1, //月份        
		"d": date.getDate(), //日        
		"h": date.getHours(), //小时        
		"m": date.getMinutes(), //分        
		"s": date.getSeconds(), //秒         
		"q": Math.floor((date.getMonth() + 3) / 3), //季度      
		"S": date.getMilliseconds() //毫秒     
	};    
	format = format.replace(/([yMdhmsqS])+/g, function(all, t){        
		var v = map[t];        
		if(v !== undefined){           
			if(all.length > 1){               
				v = '0' + v;               
				v = v.substr(v.length-2);          
			}            
			return v;      
		}else if(t === 'y'){           
			return (date.getFullYear() + '').substr(4 - all.length);       
		}        
		return all;    
	});    
	return format;
}
	
//比较时间 格式 yyyy-MM-dd HH:mm:ss
function compTime(beginTime,endTime){   
  
	var beginTimes=beginTime.substring(0,10).split('-');   
	var endTimes=endTime.substring(0,10).split('-');   
  
	beginTime=beginTimes[1]+'-'+beginTimes[2]+'-'+beginTimes[0]+' '+beginTime.substring(10,19);   
	endTime=endTimes[1]+'-'+endTimes[2]+'-'+endTimes[0]+' '+endTime.substring(10,19);   
  
	var a =(Date.parse(endTime)-Date.parse(beginTime))/3600/1000;   
	if(a<0){   
		//endTime小
		return 1;
	}else if (a>0){   
		//endTime大
		return -1;
	}else if (a==0){   
		//时间相等
		return 0;
	}else{
		return '错误';
	}   
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
 * 弹出层Frame 封装
 * 框架 layer-v1.9.0 引入jQuery1.8或以上版本
 * title:标题
 * url:网址
 * width:宽
 * height:高

function systemLayerFrameShow(title,url,width,height){
	layer.open({
	    type: 2, 
	    title:title,
	    content: url,
	    moveOut:true,//是否允许拖拽到窗口外
	    area: [width+'px', height+'px']//宽高
	   
	});
	//将弹出层放入form标签内,以支持form方式提交
	$("body>div:last-child").appendTo($("form:first"));
	$("body>div:last-child").appendTo($("form:first"));
//	alert($("body>div:last-child").attr("class"));
} */

/**
 * 弹出层 封装
 * 框架 layer-v1.9.0 引入jQuery1.8或以上版本
 * title:标题
 * content:内容
 * width:宽
 * height:高
 */
function systemLayerShow(title,content,width,height){
	
	layer.open({
	    type: 1, 
	    title:title,
	    content: content, //这里content是一个普通的String
	    moveOut:true,//是否允许拖拽到窗口外
	    area: [width+'px', height+'px']//宽高

	});
	
	
	//将弹出层放入form标签内,以支持form方式提交
	$("body>div:last-child").appendTo($("form:first"));
	$("body>div:last-child").appendTo($("form:first"));
	
//	alert($("body>div:last-child").attr("class"));
}
/**
 * 弹出提示层 封装
 * 框架 layer-v1.9.0 引入jQuery1.8或以上版本
 * content:内容
 * width:宽
 * height:高
 */
function systemMsgShow(content){
	layer.msg(content, {
	    icon: 1,
	    time: 3000 //3秒关闭（如果不配置，默认是3秒）
	}, function(){
	    //do something
	});
}



/**
 * 弹出层关闭 封装
 * 框架 layer-v1.9.0 引入jQuery1.8或以上版本
 * intex:层索引
 */
function systemLayerClose(){
//	layer.closeAll(); //关闭所有层
	layer.closeAll('dialog'); //关闭信息框
	layer.closeAll('page'); //关闭所有页面层
//	layer.closeAll('iframe'); //关闭所有的iframe层
	layer.closeAll('loading'); //关闭加载层
	layer.closeAll('tips'); //关闭所有的tips层 
	
}



//使表格行上移，接收参数为链接对象
function moveUp(table,tr) {
	//获得表格对象
	var _table = document.getElementById(table);
	cleanWhitespace(_table);
	var _row = document.getElementById(tr);
	
	//如果不是第一行 交换顺序
	if (_row.previousSibling)
	swapNode(_row, _row.previousSibling);
}

//使表格行下移 接收参数为链接对象
function moveDown(table,tr) {
	//获得表格对象
	var _table = document.getElementById(table);

	cleanWhitespace(_table);

	var _row = document.getElementById(tr);

	//如果不是最后一行 则与下一行交换顺序
	if (_row.nextSibling)
	swapNode(_row, _row.nextSibling);
}

///移动行
function cleanWhitespace(element) {
	//遍历element的子节点
	for (var i = 0; i < element.childNodes.length; i++) {
		var node = element.childNodes[i];
		if (node.nodeType == 3 && !/\s/.test(node.nodue)){
		
			node.parentNode.removeChild(node);
		}
	}
}

//定义通用的函数交换两个节点的位置
function swapNode(node1, node2) {
	//获取父节点
	var _parent = node1.parentNode;
	//获取两个节点的相应位置
	var _t1 = node1.nextSibling;
	var _t2 = node2.nextSibling;
	//将node2插入到原来node1的位置
	if (_t1)
		_parent.insertBefore(node2, _t1);
	else
		_parent.appendChild(node2);
	//将node1插入到原来ndoe2的位置
	if (_t2)
		_parent.insertBefore(node1, _t2);
	else
		_parent.appendChild(node1);
}


//回调页面
function callbackWeb(jumpStatus){ 
	if(jumpStatus == -10){
		//页面回调
		window.parent.callbackFrame();
	}
	if(jumpStatus == -12){
		//刷新主框页面
		window.parent.refreshs();
		//页面回调
		window.parent.callbackFrame();
	}
}

 

