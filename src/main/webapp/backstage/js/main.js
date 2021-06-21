//element-plus中文
ElementPlus.locale(ElementPlus.lang.zhCn);


//创建 axios 实例
const ajax = axios.create({
	// 统一 url 配置，定义访问前缀 baseURL
	//baseURL: '/api',
	// 定义请求超时时间
	timeout: 20000,
	// 请求带上 cookie
	withCredentials: true,
	// 定义消息头
	headers: {
	    'X-Requested-With': 'XMLHttpRequest',
	    post: {
	        'Content-Type': 'application/x-www-form-urlencoded'
	    }
	},
	//设置csrf请求头
	xsrfCookieName : 'XSRF-TOKEN',
	xsrfHeaderName : 'X-XSRF-TOKEN'


});

//loading对象
let loading;

//当前正在请求的数量
let needLoadingRequestCount = 0;

//显示loading
function showLoading(target,mask) {
	//解决加载图标抖动问题. 关闭时loading对象可能还存在，但needLoadingRequestCount已经变成0.避免这种情况下会重新创建loading
	if (needLoadingRequestCount === 0 && !loading) {
		//console.log("消息",app.config.globalProperties.$message.success("成功"));
		
		loading = app.config.globalProperties.$loading({
		      lock: mask ? true :false,//加载时全屏幕锁定
		      text: '加载中',
		      fullscreen: mask ? true :false,//全屏遮罩
		      target: target || "document.body",
		      spinner: 'el-icon-loading',
		      background: 'rgba(255, 255, 255, 0.5)',
		      customClass : mask ? '' :'noMask'//遮罩层
			});
		
	}
	needLoadingRequestCount++;
}
//隐藏loading
function hideLoading() {
	needLoadingRequestCount--;
	needLoadingRequestCount = Math.max(needLoadingRequestCount, 0); //做个保护
	if (needLoadingRequestCount === 0) {
		//关闭loading
		toHideLoading();
	}
}
//防抖：将 300ms 间隔内的关闭 loading 便合并为一次。防止连续请求时， loading闪烁的问题。
let toHideLoading = _.debounce(()=>{
	if(loading){
		loading.close();
		loading = null;
	}
}, 300);
//添加请求拦截器
ajax.interceptors.request.use(config => {
	//判断当前请求是否设置了不显示Loading
	if(config.headers.showLoading !== false){
		showLoading(config.headers.loadingTarget,config.headers.loadingMask);
	}
	if (config.method === 'get'){
		//增加时间戳
	    Object.assign(config.params, { //如果get请求本身带有参数，给options.params 再添加一个key值_t,值为时间戳
	    	_t: new Date().getTime(),
	    })
	}
	//if (config.method === 'post'){
		//设置X-XSRF-TOKEN
		//config.headers.post[store.state.csrf_header] = store.state.csrf_token;
	//}
	
	
	//从sessionStorage中获取登录令牌
	let oauth2Token = window.sessionStorage.getItem('oauth2Token');
	if(oauth2Token != null){
		let oauth2Object = JSON.parse(oauth2Token);
		// 让每个请求携带会话token  ['Authorization']
		config.headers['Authorization'] = 'Bearer '+oauth2Object.access_token;//如果将过期的access_token提交到后端让令牌提取器解析，会返回401错误。注意:不需要登录后才能查看的页面不要携带本参数，如登录页和刷新令牌页不要提交此参数，目前无需登录的页面携带了本参数的请求由后端过滤去掉
	}
	

	
	
	//config.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
	return config;
}, err => {
	//判断当前请求是否设置了不显示Loading
	if(config.headers.showLoading !== false){
		hideLoading();
	}
	return Promise.resolve(err);
});


let isRefreshing = false // 标记是否正在刷新 token
let requests = [] // 存储待重发请求的数组

//响应拦截器
ajax.interceptors.response.use(
	response => {
	    //判断当前请求是否设置了不显示Loading
	    if(response.config.headers.showLoading !== false){
	    	hideLoading();
	    }
	    return response;
	},
	error => {
		if(error && error.response){
			switch (error.response.status) {
				case 400: 
					app.config.globalProperties.$message({
						duration :0,
			            showClose: true,
			            message: '请求无效（400）',
			            type: 'error'
			        });
					break;
				case 401: 
					if(!error.config.url.includes('admin/refreshToken')){//如果不是请求刷新令牌
						
						const { config } = error;
						
						if (!isRefreshing) {
							isRefreshing = true;
							return refreshToken().then(res=> {
					        	if(res){
					        		let result = res.data;
								    if(result){
								    	let returnValue = JSON.parse(res.data);
								    	
								    	if(returnValue.code === 200){//成功
								    		let oauth2Object = returnValue.data;
								    		// 让每个请求携带会话token  ['Authorization']
								    	    config.headers['Authorization'] = 'Bearer '+oauth2Object.access_token;
								    	    //写入sessionStorage中
							    			window.sessionStorage.setItem('oauth2Token', JSON.stringify(oauth2Object));
							    			
							    			 // token 刷新后将数组的方法重新执行
							                requests.forEach((cb) => cb(oauth2Object.access_token));
							                requests = []; // 重新请求完清空
								            return ajax(config)	
								    	}else if(returnValue.code === 500){//错误
								    		//删除会话token
											window.sessionStorage.removeItem('oauth2Token');
											app.config.globalProperties.$message({
									            showClose: true,
									            message: '身份验证会话已过期，请重新登录',
									            type: 'error'
									        });
											
											router.push({
												path : '/admin/login'
											});
								    		
								    	}
								    	
								    }
						    		
					        	}
					        }).catch(err => {
					        	
					        	//如果refreshToken()没读取到参数
					        	
					        	
					        	//删除会话token
								window.sessionStorage.removeItem('oauth2Token');
								router.push({
									path : '/admin/login'
								});
					        	
					        	//判断当前请求是否设置了不显示Loading
				        	    if(error.config.headers.showLoading !== false){
				        	    	hideLoading();
				        	    }
				                return Promise.reject(err)
				            }).finally(() => {
				                isRefreshing = false;
				                //判断当前请求是否设置了不显示Loading
				        	    if(error.config.headers.showLoading !== false){
				        	    	hideLoading();
				        	    }
				            });
						}else {
				            // 返回未执行 resolve 的 Promise
				            return new Promise(resolve => {
				                // 用函数形式将 resolve 存入，等待刷新后再执行
				                requests.push(token => {
				                	config.headers['Authorization'] = 'Bearer '+token;
				                    resolve(ajax(config))
				                });
				                if(error.config.headers.showLoading !== false){;
				        	    	hideLoading();
				        	    }
				            })
				        }
					}else{
						//删除会话token
						window.sessionStorage.removeItem('oauth2Token');
						app.config.globalProperties.$message({
				            showClose: true,
				            message: '请求刷新令牌出错，请重新登录',
				            type: 'error'
				        });
						
						router.push({
							path : '/admin/login'
						});
						
					}
					break;
				case 403: 
					app.config.globalProperties.$message({
						duration :0,
			            showClose: true,//Invalid CSRF Token
			            message: '拒绝访问（'+(error.response.headers.message != undefined ? decodeURIComponent(error.response.headers.message) :"")+'）',//XSRF-TOKEN验证未通过:Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-XSRF-TOKEN'； 会员角色没有访问当前URL的权限:没有权限访问
			            type: 'error'
			        });
					break;
				case 404: 
					app.config.globalProperties.$message({
						duration :0,
			            showClose: true,
			            message: '找不到您尝试加载的网页（404）'+error.response.config.url,
			            type: 'error'
			        });
					break;	
				case 500: 
					app.config.globalProperties.$message({
						duration :0,
			            showClose: true,
			            message: '服务器错误（500）',
			            type: 'error'
			        });
					break;	
				default: app.config.globalProperties.$message({
					duration :0,
		            showClose: true,
		            message: '请求错误'+error.response.status,
		            type: 'error'
		        });
			}
		}
		
	    //判断当前请求是否设置了不显示Loading
	    if(error.config.headers.showLoading !== false){
	    	hideLoading();
	    }
	    
		return Promise.reject(error);
	}
);


let refreshToken = function(){
	//刷新 access_token 的接口
	let formData = new FormData();

	
	//从sessionStorage中获取登录令牌
	let oauth2Token = window.sessionStorage.getItem('oauth2Token');
	if(oauth2Token != null){
		let oauth2Object = JSON.parse(oauth2Token);
	    formData.append('refresh_token',oauth2Object.refresh_token);
	}else{
	//	return new Promise(function (resolve) {});
		return Promise.reject("参数不存在");
	}

	return ajax({
	    method: 'post',
	    url: 'admin/refreshToken',
	    data: formData,
	    headers: {
	      	'showLoading': false,//是否显示图标
	      	'loadingMask':false// 是否显示遮罩层
	    }
	});
	//.then(function (response) {
	//	return Promise.resolve(response);

	//});
};
/**
关闭延迟加载图标示例
ajax.get('control/topic/list', {
  params: {
  	visible :_self.visible,
  	page :_self.currentpage
  },
  headers: {
  	'showLoading': false,//是否显示图标
  	'loadingTarget':"#test",//Loading 需要覆盖的 DOM 节点。可传入一个 DOM 对象或字符串；若传入字符串，则会将其作为参数传入 document.querySelector以获取到对应 DOM 节点 <div id="test"></div>
 	'loadingMask':false// 是否显示遮罩层
  }
})
**/


//后台管理框架页
var index_component = {
	template : '#index-template',
	provide () {    //父组件中通过provide来提供变量，在子组件中通过inject来注入变量。                                             
        return {
            reload: this.reload                                              
        }
    },
	data : function data() {
		return {
			mini:false,//是否进入迷你模式
			
			isRouterAlive: true, //控制视图是否显示的变量
			//顶部导航菜单默认选中项
			topNavigationActiveIndex: '1',
			//顶部链接标签
			linkTag: [],
			
			
	        //左侧菜单栏是否水平折叠收起菜单
			isLeftNavigationMenuCollapse:false,
	        //左侧菜单栏二级菜单当前激活选中项
	        leftNavigationMenuActive:"",
	        //左侧菜单栏一级菜单当前展开选中项
	        leftNavigationMenuOpeneds:["1-100100"],
	        //左侧导航菜单一级菜单是否展开 Map格式 key= 左侧菜单栏索引 value=是否展开(boolean类型)
	        leftNavigationMenuExpand :new Map([
	        		["1-100100", true], 
	        		["2-200100", true],
	        		["2-200200", true],
	        		["2-200300", true],
	        		["3-300100", true],
	        		["3-300200", true],
	        		["4-400100", true],
	        		["4-400200", true],
	        		["4-400300", true],
	        		["4-400400", true],
	        		["4-400500", true],
	        		["5-500100", true]]
	        ),
	        
	        sysUsers :'',
		};
	},
	
	created : function created() {
		
		this.miniMode();
		
		//初始化
		this.queryManageFramework();
		//设置缓存
		//this.$store.commit('setCacheComponents',  ['index']);
		
		let _topNavigationActiveIndex = window.sessionStorage.getItem('topNavigationActiveIndex');
		if(_topNavigationActiveIndex != null){
			this.topNavigationActiveIndex = _topNavigationActiveIndex;
			 //左侧导航菜单自动展开
			this.leftNavigationMenuAutoOpen();
		}
		let _linkTag = window.sessionStorage.getItem('linkTag');
		if(_linkTag != null){
			this.linkTag = JSON.parse(_linkTag);
		}
		let _leftNavigationMenuExpand = window.sessionStorage.getItem('leftNavigationMenuExpand');
		if(_leftNavigationMenuExpand != null){
			this.leftNavigationMenuExpand = objToStrMap(JSON.parse(_leftNavigationMenuExpand));
		}
		let _leftNavigationMenuOpeneds = window.sessionStorage.getItem('leftNavigationMenuOpeneds');
		if(_leftNavigationMenuOpeneds != null){
			this.leftNavigationMenuOpeneds = JSON.parse(_leftNavigationMenuOpeneds);
		}
		
		
		
		if(this.linkTag.length ==0){//如果标签为0
			//创建窗口
			this.createWindow("0-000000-0");
		}
		
		//当前路由是否和窗口最后打开的URL相同
		let sameRoute = false;
		
		//窗口最后打开的URL
		let _windowNewestURLList = window.sessionStorage.getItem('windowNewestURLList');
		if(_windowNewestURLList != null){
			let windowNewestURLList = JSON.parse(_windowNewestURLList);
			if(windowNewestURLList != null && windowNewestURLList.length >0){
				for(let i=0; i<windowNewestURLList.length; i++){
					let windowNewestURL = windowNewestURLList[i];
					
					//浏览URL全路径集合
			    	let fullPathList = new Array();
			    	let fullPath =new Object();
			    	fullPath.url = windowNewestURL.url;
			    	fullPath.scrollTop = 0;
			    	fullPathList.push(fullPath);
					//写入历史记录
			    	let obj =new Object();
				    obj.key = windowNewestURL.index;
				    obj.value = fullPathList;
				    this.$store.commit('addHistoryPath',obj);
				    
				    if(this.$route.fullPath == windowNewestURL.url){
				    	
				    	sameRoute = true;
				    }
				    
				}
			}
		}
		

		if(!sameRoute){
			let fullPath = this.$route.path;//当前的路由
			let linkList = this.$store.getters.allLinkList;
			for(let i=0; i<linkList.length; i++){
	    		let link = linkList[i];
	    		
	    		if(link.path == fullPath){
	    			let flag = false;
	    			if(link.query != undefined && link.query.method != undefined){
	    				if(link.query.method == this.$route.query.method){
	    					flag = true;
	    				}
	    				
	    			}else{
	    				flag = true;
	    			}
	    			if(flag){//如果请求的URL属于配置的URL
	    				
	    				//是否已有打开窗口
	    		    	let isOpenWindow = false;
	    		    	for(let j=0; j<this.linkTag.length; j++){
	    		    		let tag = this.linkTag[j];
	    		    		if(tag.index == link.meta.index || tag.index == link.meta.parent){
	    		    			
	    		    			isOpenWindow = true;
	    		    			
	    		    			//所有标签取消选中
	    		    	    	for(let k=0; k<this.linkTag.length; k++){
	    		    	    		let tag = this.linkTag[k];
	    		    	    		if(tag.active){
	    		    	    			let newTab =new Object();
	    		    	    	    	newTab.name = tag.name;
	    		    	    	    	newTab.index = tag.index;
	    		    	    	    	newTab.active = false;//是否激活
	    		    	    			this.linkTag.splice(k,1,newTab);
	    		    	    		}
	    		    	    	}
	    		    			
	    		    			
	    		    			//激活当前标签
	    		    			let newTab =new Object();
	    		    	    	newTab.name = tag.name;
	    		    	    	newTab.index = tag.index ;
	    		    	    	newTab.active = true;//是否激活	
	    		    			this.linkTag.splice(j,1,newTab);
	    		    			
	    		    			break;
	    		    		}
	    		    		
	    		    	}
	    		    	
	    		    	
	    		    	if(link.meta.index == undefined){//如果当前路由没有导航索引
	    		    		let parentIndex = link.meta.parent;
	    		    		for(let p=0; p<linkList.length; p++){
	    		        		let parent_link = linkList[p];
	    		        		if(parent_link.meta.index == parentIndex){
	    		        			
	    		        			
	    		        			if(!isOpenWindow){//如果还没打开窗口
	    	        					//所有标签取消选中
	    	    		    	    	for(let k=0; k<this.linkTag.length; k++){
	    	    		    	    		let tag = this.linkTag[k];
	    	    		    	    		if(tag.active){
	    	    		    	    			let newTab =new Object();
	    	    		    	    	    	newTab.name = tag.name;
	    	    		    	    	    	newTab.index = tag.index;
	    	    		    	    	    	newTab.active = false;//是否激活
	    	    		    	    			this.linkTag.splice(k,1,newTab);
	    	    		    	    		}
	    	    		    	    	}
	    	    		    	    	
	    	        					let tab =new Object();
	    	    		    	    	tab.name = parent_link.meta.title;
	    	    		    	    	tab.index = parent_link.meta.index;
	    	    		    	    	tab.active = true;//是否激活
	    	    		    	    	this.linkTag.push(tab);
	    	        				}
	    		        			
	    		        			
	    		        			break;
	    		        		}
	    		        		
	    		    		}
	    		    		
	    		    		
	    		    		
	    		    		
	    		    	}else{
	    		    		if(!isOpenWindow){//如果还没打开窗口
	        					//所有标签取消选中
	    		    	    	for(let k=0; k<this.linkTag.length; k++){
	    		    	    		let tag = this.linkTag[k];
	    		    	    		if(tag.active){
	    		    	    			let newTab =new Object();
	    		    	    	    	newTab.name = tag.name;
	    		    	    	    	newTab.index = tag.index;
	    		    	    	    	newTab.active = false;//是否激活
	    		    	    			this.linkTag.splice(k,1,newTab);
	    		    	    		}
	    		    	    	}
	    		    	    	
	        					let tab =new Object();
	    		    	    	tab.name = link.meta.title;
	    		    	    	tab.index = link.meta.index;
	    		    	    	tab.active = true;//是否激活
	    		    	    	this.linkTag.push(tab);
	        				}
	    		    	}
	    		    	
	    		    	
	    				
	    		    	
	    		    	
	    				break;
	    			}
	    		}
	    	}
		}
		
	},
	
	watch: {
		//监听单个值变化
		topNavigationActiveIndex: {
	　　　　handler(newValue, oldValue) {
				window.sessionStorage.setItem('topNavigationActiveIndex', this.topNavigationActiveIndex);
	　　　　}
	　　},
		//监听数组变化
	　　linkTag: {
	　　　　handler(newValue, oldValue) {
				window.sessionStorage.setItem('linkTag', JSON.stringify(this.linkTag));
	　　　　},
	　　　　deep: true
	　　},
	  	//监听数组变化
	　　leftNavigationMenuOpeneds: {
	　　　　handler(newValue, oldValue) {
				window.sessionStorage.setItem('leftNavigationMenuOpeneds', JSON.stringify(this.leftNavigationMenuOpeneds));
	　　　　},
	　　　　deep: true
	　　},
	  	//监听数组变化
	　　leftNavigationMenuExpand: {
	　　　　handler(newValue, oldValue) {
				window.sessionStorage.setItem('leftNavigationMenuExpand', JSON.stringify(strMapToObj(this.leftNavigationMenuExpand)));
	　　　　},
	　　　　deep: true
	　　},
	},

	methods : {
		//判断是否需要进入迷你模式
		miniMode : function() {
			if(window.screen.width <=600){//进入迷你模式
				this.mini = true;
				
				
			}
		},
		
		//查询后台管理框架页信息
		queryManageFramework : function() {
			let _self = this;
			
			_self.$ajax.get('control/manage/index', {
			    params: {
			    	
			    }
			})
			.then(function (response) {
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		let mapData = returnValue.data;
			    		for(let key in mapData){
			    			if(key == "sysUsers"){
			    				_self.sysUsers = mapData[key];
			    			}else if(key == "fileStorageSystem"){
			    				let fileStorageSystem = mapData[key];
			    				_self.$store.commit('setFileStorageSystem', fileStorageSystem);
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//顶部导航选择
		topNavigationSelect: function(key, keyPath) {
			let _self = this;
			_self.topNavigationActiveIndex = key;
	        //清空数组
			_self.leftNavigationMenuOpeneds = [];
	        
			_self.leftNavigationMenuExpand.forEach(function(value,key){
				if(value){
					_self.leftNavigationMenuOpeneds.push(key);
				}
	　　　　});
 
	     
	    },
	    //左侧导航菜单展开
	    leftNavigationMenuOpen: function(key, keyPath) {
	        this.leftNavigationMenuExpand.set(key, true);
	        
	    },
	    //左侧导航菜单收缩
	    leftNavigationMenuClose: function(key, keyPath) {
	        this.leftNavigationMenuExpand.set(key, false);
	    },
	    //左侧菜单栏水平折叠收起菜单
	    leftNavigationMenuCollapse: function() {
	    	let _self = this;
	    	if(_self.isLeftNavigationMenuCollapse){
	    		
	    		let index = _self.topNavigationActiveIndex;
	    		_self.isLeftNavigationMenuCollapse = false;
	    		//因为折叠收起菜单时一级节点不能自动展开，所以切换到不存在的节点再切换回来
	    		_self.topNavigationActiveIndex= '99999';//99999为不存在的节点
	    		
	    		const { nextTick } = Vue;
	    		nextTick(function(){
	    			_self.topNavigationActiveIndex= index;
	    			//清空数组
	    			_self.leftNavigationMenuOpeneds = [];
	    	        
	    			_self.leftNavigationMenuExpand.forEach(function(value,key){
	    				if(value){
	    					_self.leftNavigationMenuOpeneds.push(key);
	    				}
	    	　　　　});
	    		});
	    	}else{
	    		_self.isLeftNavigationMenuCollapse = true;
	    		
	    		
	    	}
	    	
	    },
	    
	    //左侧导航菜单自动展开
	    leftNavigationMenuAutoOpen: function() {
	    	let _self = this;
	    	
	    	let oldIndex = _self.topNavigationActiveIndex;
	    	
	    	//因为折叠收起菜单时一级节点不能自动展开，所以切换到不存在的节点再切换回来
    		_self.topNavigationActiveIndex= '99999';//99999为不存在的节点
    		
    		_self.$nextTick(() => {
    			_self.topNavigationActiveIndex= oldIndex;
    			//清空数组
    			_self.leftNavigationMenuOpeneds = [];
    	        
    			_self.leftNavigationMenuExpand.forEach(function(value,key){
    				if(value){
    					_self.leftNavigationMenuOpeneds.push(key);
    				}
    	　　　　});
    		});
	    	
	    },
	    
	    
	    
	    //左侧导航菜单点击
	    leftNavigationMenuSelect: function(key, keyPath) {
	        //创建窗口
	        this.createWindow(key);

	    },

	    
	    //顶部菜单首页链接
	    topNavigationHomeLink: function() {
	    	//创建窗口
	        this.createWindow("0-000000-0");
	    	
	    },
	    
	    
	    //创建窗口  index：左侧菜单栏二级菜单索引
	    createWindow: function(index) {
	    	//所有标签取消选中
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let tag = this.linkTag[i];
	    		if(tag.active){
	    			let newTab =new Object();
	    	    	newTab.name = tag.name;
	    	    	newTab.index = tag.index;
	    	    	newTab.active = false;//是否激活
	    			this.linkTag.splice(i,1,newTab);
	    		}
	    	}
	    	let flag = false;//是否已添加标签
	    	//如果已添加标签，则选中
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let tag = this.linkTag[i];
	    		if(tag.index == index){
	    			
	    			let newTab =new Object();
	    	    	newTab.name = tag.name;
	    	    	newTab.index = index;
	    	    	newTab.active = true;//是否激活	
	    			this.linkTag.splice(i,1,newTab);
	    			flag = true;
	    		}
	    	}
	    	
	    	//如果未添加
	    	if(!flag){
	    		let linkList = this.$store.getters.allLinkList;
	    		for(let i=0; i<linkList.length; i++){
		    		let link = linkList[i];
		    		if(link.meta.index == index){
		    			let tab =new Object();
		    	    	tab.name = link.meta.title;
		    	    	tab.index = index;
		    	    	tab.active = true;//是否激活
		    	    	this.linkTag.push(tab);
		    		}
		    	}
	    	}
	    	
	    	
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let tag = this.linkTag[i];
	    		if(tag.active){//激活的标签
	    			//跳转链接
	    	    	this.toLink(tag);
	    			
	    			break;
	    		}
	    	}
	    	
	    },
	    //选择窗口
	    selectWindow: function(tag) {
	    	//所有标签取消选中
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let _tag = this.linkTag[i];
	    		if(_tag.active){
	    			let newTab =new Object();
	    	    	newTab.name = _tag.name;
	    	    	newTab.index = _tag.index;
	    	    	newTab.active = false;//是否激活
	    	    	
	    			this.linkTag.splice(i,1,newTab);
	    		}
	    		
	    	}
	    	//选中当前项
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let _tag = this.linkTag[i];
	    		if(_tag.index == tag.index){
	    			
	    			let newTab =new Object();
	    	    	newTab.name = _tag.name;
	    	    	newTab.index = _tag.index;
	    	    	newTab.active = true;//是否激活
	    			
	    			this.linkTag.splice(i,1,newTab);
	    			
	    		}
	    	}
	    	
	    	//跳转链接
	    	this.toLink(tag);
	    	
	    },
	    //关闭窗口
	    closeWindow: function(tag) {
	    	//如果是最后一项，则不允许删除
	    	if(this.linkTag.length <=1){
	    		return;
	    	}
	    	let index = this.linkTag.indexOf(tag);
	    	
	    	this.linkTag.splice(index,1);
	    	
	    	//删除当前窗口对应的路由数据
	    	this.$store.commit('deleteHistoryPath',tag.index);
	    	
	    	
	    	//是否有激活
	    	let isActive = false;
	    	//所有标签取消选中
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let tag = this.linkTag[i];
	    		if(tag.active){
	    			isActive = tag.active;
	    		}
	    		
	    	}
	    	
	    	
	    	//窗口最新URL集合
		    let new_windowNewestURLList = new Array();
		    
		    //窗口旧URL集合
		    let old_windowNewestURLList = new Array();
		    
		    let _windowNewestURLList = window.sessionStorage.getItem('windowNewestURLList');
			if(_windowNewestURLList != null){
				let _old_windowNewestURLList = JSON.parse(_windowNewestURLList);
				if(_old_windowNewestURLList != null && _old_windowNewestURLList.length >0){
					old_windowNewestURLList = _old_windowNewestURLList;
				}
			}
			for(let i=0; i<this.linkTag.length; i++){
				let _tag = this.linkTag[i];

				for(let j=0; j<old_windowNewestURLList.length; j++){
					let windowNewestURL = old_windowNewestURLList[j];
					if(windowNewestURL.index == _tag.index){
						new_windowNewestURLList.push(windowNewestURL);
					}
				}

			}
		    //保存窗口最后打开的URL
	    	window.sessionStorage.setItem('windowNewestURLList', JSON.stringify(new_windowNewestURLList));

	    	
	    	if(isActive){
	    		return;
	    	}
	    	
	    	//新选中标签索引
	    	let newIndex = index-1;
	    	if(index==0){//如果是第一项
	    		newIndex = 0;
	    	}
	    	
	    	
	    	//删除后标签选中前一项
	    	for(let i=0; i<this.linkTag.length; i++){
	    		let tag = this.linkTag[i];
	    		if(i==newIndex){
	    			//tag.active = false;
	    			let newTab =new Object();
	    	    	newTab.name = tag.name;
	    	    	newTab.index = tag.index;
	    	    	newTab.active = true;//是否激活

	    			this.linkTag.splice(i,1,newTab);
	    			
	    			//跳转链接
	    	    	this.toLink(newTab);
	    		}
	    		
	    	}
	    	
	    	
	    	
	    	
	    	
	    },
	    //跳转链接
	    toLink: function(tag) {
	    	//URL集合
	    	let fullPathList = new Array();
			//获取当前窗口对应的URL集合
	    	this.$store.state.historyPath.forEach(function(value,key){
				if(key == tag.index){
					fullPathList = value;
				}
	　　　　});
	    	
	    	if(fullPathList != null && fullPathList.length >0){//如果是已打开的窗口
	    		//如果在窗口执行了后退按钮
	    		let windowBrowseStatus = this.$store.state.windowBrowseStatus.get(tag.index);
	    		if(windowBrowseStatus != null && windowBrowseStatus.step <0){//如果执行过"前进/后退"
			    	let _fullPath = "";
			    	let count = windowBrowseStatus.step*-1;
			    	for(let i = fullPathList.length-1; i>=0; i--){//倒序循环
		    			let fullPath = fullPathList[i];
		    			
						if(count == 0){
							_fullPath = fullPath;
							break;
						}
						count--;
						
						
			    	}
			    	
			    	
			    	//窗口浏览状态
			    	let _windowBrowseStatus = new Object();
			    	_windowBrowseStatus.isRecordPath = false;//是否记录路径 
			    	_windowBrowseStatus.step = windowBrowseStatus.step;	//步长 0为没有执行"前进/后退"命令
			    	
					
			    	let obj =new Object();
				    obj.key = tag.index;
				    obj.value = _windowBrowseStatus;
				    //添加窗口浏览状态
					this.$store.commit('addWindowBrowseStatus',obj);
			    	
	    			if(_fullPath != ""){
	    				//解析url参数
			    		let paramObject = analyzeUrlParam(_fullPath.url);
			    		
			    		
			    		if (Object.keys(paramObject).length === 0) {//如果为空
			    			this.$router.push({
			    				path : _fullPath.url
			    			});
			    		}else{
			    			//含有中文的值要解码
			    			for(let param in paramObject){
			    				paramObject[param] = decodeURIComponent(paramObject[param]);
			    			}
			    			
			    			this.$router.push({
				    			path : _fullPath.url,
				    			query : paramObject
				    		});
			    			
			    			
			    		}
	    				
	    			}
		    	}else{
		    		//最后一个URL
		    		let end_fullPath = fullPathList[fullPathList.length-1];
		    		
		    		//解析url参数
		    		let paramObject = analyzeUrlParam(end_fullPath.url);
		    		
		    		
		    		if (Object.keys(paramObject).length === 0) {//如果为空
		    			this.$router.push({
		    				path : end_fullPath.url
		    			});
		    			
		    		}else{
		    			//含有中文的值要解码
		    			for(let param in paramObject){
		    				paramObject[param] = decodeURIComponent(paramObject[param]);
		    			}
		    			
		    			
		    			this.$router.push({
		    				path : end_fullPath.url,
		    				query : paramObject
		    			});
		    		}
		    		
		    	}
	    		
	    	}else{//如果是未打开的窗口
	    		
	    		let linkList = this.$store.getters.allLinkList;
	    		
	    		
	    		for(let j=0; j<linkList.length; j++){
	    			let _link = linkList[j];
	    			if(_link.meta.index == tag.index){
	    				if(_link.query != undefined){//如果带有参数
	    					this.$router.push({
			    				path : _link.path,
			    				query : _link.query
			    			});
		    			}else{
		    				this.$router.push({
			    				path : _link.path,
			    			});
		    				
		    			}
		    			
		    			break;
		    		}
	    		}
	    		
	    	}
	    	
	    	
	    },
	    //后退
	    retreat: function() {
	    	//当前窗口索引
		    let currentWindowIndex = "";
		    
		    let _linkTag = window.sessionStorage.getItem('linkTag');
			if(_linkTag != null){
				let linkTag = JSON.parse(_linkTag);
				
				for(let i=0; i<linkTag.length; i++){
		    		let _tag = linkTag[i];
		    		if(_tag.active){//如果已激活
		    			currentWindowIndex = _tag.index;
		    			break;
		    		}
				}
			}

		    if(currentWindowIndex != ''){
		    	let old_windowBrowseStatus = this.$store.state.windowBrowseStatus.get(currentWindowIndex);
		    	let old_step = 0;//原来的步长
		    	if(old_windowBrowseStatus != null){
		    		old_step = old_windowBrowseStatus.step;
		    	}
		    	
		    	
		    	
		    	//浏览URL全路径集合
		    	let fullPathList = new Array();
		    	
		    	//获取当前窗口对应的URL集合
		    	this.$store.state.historyPath.forEach(function(value,key){
    				if(key == currentWindowIndex){
    					fullPathList = value;
    				}
    	　　　　});
		    	
		    	let step = old_step;//步长 0为没有执行"前进/后退"命令
		    	let _fullPath = "";
		    	let count = (old_step-1)*-1;
		    	for(let i = fullPathList.length-1; i>=0; i--){//倒序循环
	    			let fullPath = fullPathList[i];
	    			
					if(count == 0){
						_fullPath = fullPath;
						step = old_step-1;
						break;
					}
					count--;
					
					
		    	}
		    	
		    	/**
		    	let count = 0;
	    		for(let i = fullPathList.length-1; i>=0; i--){//倒序循环
	    			let fullPath = fullPathList[i];
	    			
					if(count == (old_step*(-1))+1 ){
						_fullPath = fullPath;
						step = old_step-1;
						break;
					}
					count++;
					
					
		    	}**/
	    		
		    	
		    	if(_fullPath != ""){
		    		//窗口浏览状态
			    	let windowBrowseStatus = new Object();
			    	windowBrowseStatus.isRecordPath = false;//是否记录路径 
			    	windowBrowseStatus.step = step;	//步长 0为没有执行"前进/后退"命令
			    	
					
			    	let obj =new Object();
				    obj.key = currentWindowIndex;
				    obj.value = windowBrowseStatus;
				    //添加窗口浏览状态
					this.$store.commit('addWindowBrowseStatus',obj);
					
					
					//解析url参数
		    		let paramObject = analyzeUrlParam(_fullPath.url);
		    		
		    		
		    		
		    		
		    		
		    		
		    		if (Object.keys(paramObject).length === 0) {//如果为空
		    			this.$router.push({
		    				path : _fullPath.url
		    			});
		    		}else{
		    			//含有中文的值要解码
		    			for(let param in paramObject){
		    				paramObject[param] = decodeURIComponent(paramObject[param]);
		    			}
		    			
		    			this.$router.push({
		    				path : _fullPath.url,
		    				query : paramObject
		    			});
		    		}
		    	}
		    	
		    			
		    	
		    	
		    }
	    	
	    	
	    	
	    },
	    //前进
	    advance: function() {
	    	//当前窗口索引
		    let currentWindowIndex = "";
		    
		    let _linkTag = window.sessionStorage.getItem('linkTag');
			if(_linkTag != null){
				let linkTag = JSON.parse(_linkTag);
				
				for(let i=0; i<linkTag.length; i++){
		    		let _tag = linkTag[i];
		    		if(_tag.active){//如果已激活
		    			currentWindowIndex = _tag.index;
		    			break;
		    		}
				}
			}

		    if(currentWindowIndex != ''){
		    	let old_windowBrowseStatus = this.$store.state.windowBrowseStatus.get(currentWindowIndex);
		    	let old_step = 0;//原来的步长
		    	if(old_windowBrowseStatus != null){
		    		old_step = old_windowBrowseStatus.step;
		    	}
		    	
		    	let step = old_step +1;//步长 0为没有执行"前进/后退"命令
		    	if(step >0){
		    		step = 0;
		    	}
		    	
		    	if(step  <= 0){
		    		//浏览URL全路径集合
			    	let fullPathList = new Array();
			    	
			    	//获取当前窗口对应的URL集合
			    	this.$store.state.historyPath.forEach(function(value,key){
	    				if(key == currentWindowIndex){
	    					fullPathList = value;
	    				}
	    	　　　　});
			    	
			    	
			    	let _fullPath = "";
			    	let count = step*(-1);
		    		for(let i = fullPathList.length-1; i>=0; i--){//倒序循环
		    			let fullPath = fullPathList[i];
		    			
		    			if(count == 0){
		    				_fullPath = fullPath;
		    				break;
		    			}
		    			count--;
			    	}
		    		
		    		if(step ==0 && _fullPath.url==this.$route.fullPath){//如果步长为0并且前进的URL和当前路由相同
			    		return;
			    	}
		    		
		    		
		    		if(_fullPath != ""){
		    			//窗口浏览状态
				    	let windowBrowseStatus = new Object();
				    	windowBrowseStatus.isRecordPath = false;//是否记录路径 
				    	windowBrowseStatus.step = step;	//步长 0为没有执行"前进/后退"命令
						
						let obj =new Object();
					    obj.key = currentWindowIndex;
					    obj.value = windowBrowseStatus;
					    //添加窗口浏览状态
						this.$store.commit('addWindowBrowseStatus',obj);
						
						
						//解析url参数
			    		let paramObject = analyzeUrlParam(_fullPath.url);
			    		
			    		
			    		if (Object.keys(paramObject).length === 0) {//如果为空
			    			this.$router.push({
			    				path : _fullPath.url
			    			});
			    		}else{
			    			//含有中文的值要解码
			    			for(let param in paramObject){
			    				paramObject[param] = decodeURIComponent(paramObject[param]);
			    			}
			    			this.$router.push({
			    				path : _fullPath.url,
			    				query : paramObject
			    			});
			    		}
		    		}
		    		
		    		
		    	}
		    	
		    	
		    	
		    }
	    	
	    },
	    //刷新
	    refresh: function() {
	    	this.$store.commit('setCacheNumber');

    		this.isRouterAlive = false; //先关闭，
            this.$nextTick(function () {
                this.isRouterAlive = true; //再打开
            });	
	    },
	    //退出登录
	    logout: function() {
	    	let _self = this;

			this.$ajax({
		        method: 'post',
		        url: 'admin/logout'
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				
				let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	
			    	if(returnValue.code === 200){//成功
		    			//删除sessionStorage中全部数据
		    			window.sessionStorage.clear(); 

						_self.$router.push("/admin/login");

			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap){
			    			_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    	    }
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		
	    	
	    	
	    }
	}
}


//首页组件
var home_component = {
	name: 'home',//组件名称，keep-alive缓存需要本参数
	template : '#home-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			auditTopicCount: 0,//待审核话题数量
			auditCommentCount: 0,//待审核评论数量
			auditCommentReplyCount: 0,//待审核评论回复数量	
			auditQuestionCount: 0,//待审核问题数量
			auditAnswerCount: 0,//待审核答案数量
			auditAnswerReplyCount: 0,//待审核答案回复数量	
			feedbackCount: 0,//留言数量
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		this.queryHome();
	},
	methods : {
		//查询首页
		queryHome : function() {
			let _self = this;
			
			this.$ajax.get('control/manage/home', {
			    params: {
			    }
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		let mapData = returnValue.data;
			    		for(let key in mapData){
			    			if(key == "staffLoginLogList"){//最新登录日志
			    				_self.tableData = mapData[key];	
			    			}else if(key == "auditTopicCount"){//待审核话题数量
			    				_self.auditTopicCount = mapData[key];
			    			}else if(key == "auditCommentCount"){//待审核评论数量
			    				_self.auditCommentCount = mapData[key];
			    			}else if(key == "auditCommentReplyCount"){//待审核评论回复数量
			    				_self.auditCommentReplyCount = mapData[key];
			    			}else if(key == "auditQuestionCount"){//待审核问题数量
			    				_self.auditQuestionCount = mapData[key];
			    			}else if(key == "auditAnswerCount"){//待审核答案数量
			    				_self.auditAnswerCount = mapData[key];
			    			}else if(key == "auditAnswerReplyCount"){//待审核答案回复数量
			    				_self.auditAnswerReplyCount = mapData[key];
			    			}else if(key == "feedbackCount"){//留言数量
			    				_self.feedbackCount = mapData[key];
			    			}
			    			
			    			
			    		}
			    	}
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
	}
};

//登录组件
var login_component = {
	template : '#login-template',
	data : function () {
		return {
			//用户名称
			username: '',
			//用户名称表单字段焦点
			username_focus : false,
			//用户名称表单字段样式
			username_field_label_class: 'form-field-label',
			//密码
			password: '',
			//密码表单字段焦点
			password_focus : false,
			//密码表单字段样式
			password_field_label_class: 'form-field-label',
			showCaptcha : false, //是否显示验证码
			//验证码值
			captchaValue: '',
			//验证码字段焦点
			captchaValue_focus : false,
			//验证码表单字段样式
			captchaValue_field_label_class: 'form-field-label',
			error : {
				username : '',
				password : '',
				captchaValue : '',
				userInfo : '',
				login : ''
			},
			allowSubmit:false,//提交按钮disabled状态
		};
	},
	
	created : function () {
		//加载登录页
		this.loadLogin();
	},
	directives: {
		"custom-focus": function(el, binding) {
		    if (binding.value) {
		    	el.focus();
		    }
		}
	},
	methods : {
		//选中字段
		selectedField : function(fieldName) {
			if(fieldName == "username"){
				this.username_focus = true;
				this.username_field_label_class = "form-field-label form-field-active";
			}
			if(fieldName == "password"){
				this.password_focus = true;
				this.password_field_label_class = "form-field-label form-field-active";
			}
			if(fieldName == "captchaValue"){
				this.captchaValue_focus = true;
				this.captchaValue_field_label_class = "form-field-label form-field-active";
			}
			
			
		},
		//字段失去焦点
		fieldBlur : function(fieldName) {
			if(fieldName == "username"){
				this.username_focus = false;
			}
			if(fieldName == "password"){
				this.password_focus = false;
			}
			if(fieldName == "captchaValue"){
				this.captchaValue_focus = false;
			}
			
			if(this.username == ''){
				//用户名称表单字段焦点
				this.username_focus = false;
				//用户名称表单字段样式
				this.username_field_label_class = 'form-field-label';
				
			}
			if(this.password == ''){
				//密码表单字段焦点
				this.password_focus = false;
				//密码表单字段样式
				this.password_field_label_class = 'form-field-label';
				
			}
			if(this.captchaValue == ''){
				//验证码表单字段焦点
				this.captchaValue_focus = false;
				//验证码表单字段样式
				this.captchaValue_field_label_class = 'form-field-label';
				
			}
		},
		
		//加载登录页
		loadLogin : function () {
			let _self = this;
			this.$ajax.get('admin/login', {
			    params: {
			    },
			    headers: {
			      	'showLoading': false,//是否显示图标
			      	'loadingMask':false// 是否显示遮罩层
			    }
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				let result = response.data;
			    if(result){
			    	if(result.code === 200){//成功
			    		let data = result.data;
			    		if(data.isCaptcha){//显示验证码
			    			_self.showCaptcha = true;
			    			_self.captchaKey = data.captchaKey;
			    			_self.imgUrl = "captcha/" + data.captchaKey + ".jpg";
			    			//设置验证码图片
							_self.replaceCaptcha();
		
			    		}
			    	}else if(result.code === 500){//错误
			    		
			    		
			    	}
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//更换验证码
		replaceCaptcha : function() {
			let _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},
		//验证验证码(同步)
		validation_captchaValue : async function(event) {
			let _self = this;
			let cv = this.captchaValue.trim();
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				//清除验证码错误
				_self.error.captchaValue = "";
				
				await _self.$ajax.get('userVerification', {
				    params: {
				    	captchaKey:_self.captchaKey,
				    	captchaValue:cv
				    },
				    headers: {
				      	'showLoading': false,//是否显示图标
				      	'loadingMask':false// 是否显示遮罩层
				    }
				})
				.then(function (response) {
					if(response == null){
						return;
					}
					let result = response.data;
				    
			    	if (result == "false") {
						_self.error.captchaValue = "验证码错误";
					}
				})
				.catch(function (error) {
					console.log(error);
				});
				
				
				
			}
		},
		
		
		//提交登录
		submitData : function() {
			let _self = this;
			_self.allowSubmit = true;//提交按钮disabled状态
			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			
			
			let formData = new FormData();
			
			
			//用户名
			let username = _self.username;
			if (username != "") {
				formData.append('username', encodeURIComponent(username));
			}
			//密码需SHA256加密
			let password = _self.password;
			if (password != "") {
				formData.append('password', CryptoJS.SHA256(password));
			}
			if(_self.captchaKey != ''){
				//验证码Key
				formData.append('captchaKey',encodeURIComponent(_self.captchaKey));
				//验证码值
				formData.append('captchaValue',encodeURIComponent(_self.captchaValue.trim()));
			}
			this.$ajax({
		        method: 'post',
		        url: 'admin/login',
		        data: formData,  
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	
			    	if(returnValue.code === 200){//成功
			    		
			    		let data = returnValue.data;
			    		if(data){
			    			//写入sessionStorage中
			    			window.sessionStorage.setItem('oauth2Token', JSON.stringify(data));
				    		
			    		}
			    		//清空历史路径记录
			    		_self.$store.commit('clearHistoryPath');
			    		//清空窗口浏览状态
			    		_self.$store.commit('clearWindowBrowseStatus');
			    		//删除窗口最后打开的URL
				    	window.sessionStorage.removeItem('windowNewestURLList');
				    	
				    	
					    _self.$router.push("/admin/control/manage/home");
					    
					    
			    		
					    
						
			    		
			    		
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		let showCaptcha = false;
			    		for (let key in errorMap) { 
			    			_self.error[key]= errorMap[key];
			    			
			    			if(key =="captchaKey" || key =="captchaValue"){
			    				showCaptcha = true;
			    			}
			    	    }
			    		if(showCaptcha && _self.showCaptcha){
			    			//设置验证码图片
							_self.replaceCaptcha();
			    		}
			    		if(showCaptcha && _self.showCaptcha == false){
			    			_self.loadLogin();
			    		}
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		}
	}
};






/**--------------------------------------------路由-------------------------------------------------**/


//定义路由
var routes = [ 
   {path : '/admin/control/manage/index',component : index_component,
	   children: [//嵌套一级子路由
	              	{path : '/admin/control/manage/home',component : home_component, name:'home',meta: {index:'0-000000-0',title:'首页',cacheNumber:'0'}}, //首页
	             	{path : '/admin/control/topic/list',component : () => loadModule('./admin/component/topic/topicList.vue', options), name:'topicList',meta: {index:'1-100100-1',title:'话题列表',cacheNumber:'0'}},//话题列表
	            	
	             	{path : '/admin/control/topic/manage/view',component : () => loadModule('./admin/component/topic/topicView.vue', options),name:'topicView',meta: {parent:'1-100100-1',title:'话题内容',}},//话题内容
	             	{path : '/admin/control/topic/manage/add',component : () => loadModule('./admin/component/topic/addTopic.vue', options),name:'addTopic',meta: {parent:'1-100100-1',title:'添加话题'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'topicList'){//如果来自话题列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加话题
	              	{path : '/admin/control/topic/topicUnhideList',component : () => loadModule('./admin/component/topic/topicUnhideList.vue', options),name:'topicUnhideList',meta: {parent:'1-100100-1',title:'解锁隐藏内容用户',}},//解锁隐藏内容用户
	             	
	             	{path : '/admin/control/tag/list',component : () => loadModule('./admin/component/topic/tagList.vue', options),name:'tagList',meta: {index:'1-100100-2',title:'标签列表',cacheNumber:'0'}},//标签列表
	             	
	              	{path : '/admin/control/tag/manage/add',component : () => loadModule('./admin/component/topic/addTag.vue', options),name:'addTag',meta: {parent:'1-100100-2',title:'添加标签'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'tagList'){//如果来自标签列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加标签
	              	{path : '/admin/control/tag/manage/edit',component : () => loadModule('./admin/component/topic/editTag.vue', options),name:'editTag',meta: {parent:'1-100100-2',title:'修改标签'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'tagList'){//如果来自标签列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改标签
	              	
	              	{path : '/admin/control/topic/allAuditTopic',component : () => loadModule('./admin/component/topic/allAuditTopic.vue', options), name:'allAuditTopic',meta: {index:'1-100100-3',title:'全部待审核话题',cacheNumber:'0'}},//全部待审核话题
	              	{path : '/admin/control/topic/allAuditComment',component : () => loadModule('./admin/component/topic/allAuditComment.vue', options), name:'allAuditComment',meta: {index:'1-100100-4',title:'全部待审核评论',cacheNumber:'0'}},//全部待审核评论
	              	{path : '/admin/control/topic/allAuditReply',component : () => loadModule('./admin/component/topic/allAuditReply.vue', options), name:'allAuditReply',meta: {index:'1-100100-5',title:'全部待审核回复',cacheNumber:'0'}},//全部待审核回复
	              	{path : '/admin/control/topic/search',component : () => loadModule('./admin/component/topic/topicSearch.vue', options), name:'topicSearch',meta: {index:'1-100100-6',title:'话题搜索',cacheNumber:'0'}},//话题搜索
	              	
	              	
	              	{path : '/admin/control/question/list',component : () => loadModule('./admin/component/question/questionList.vue', options), name:'questionList',meta: {index:'1-100200-1',title:'问题列表',cacheNumber:'0'}},//问题列表
	              	{path : '/admin/control/question/manage/view',component : () => loadModule('./admin/component/question/questionView.vue', options),name:'questionView', meta: {parent:'1-100200-1',title:'问题内容',}},//问题内容
		            {path : '/admin/control/question/manage/add',component : () => loadModule('./admin/component/question/addQuestion.vue', options),name:'addQuestion',meta: {parent:'1-100200-1',title:'添加问题'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'questionList'){//如果来自问题列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加问题
	              	{path : '/admin/control/questionTag/list',component : () => loadModule('./admin/component/question/questionTagList.vue', options), name:'questionTagList',meta: {index:'1-100200-2',title:'问题标签列表',cacheNumber:'0'}},//问题标签列表
	              	{path : '/admin/control/questionTag/manage/add',component : () => loadModule('./admin/component/question/addQuestionTag.vue', options),name:'addQuestionTag',meta: {parent:'1-100200-2',title:'添加问题标签'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'questionTagList'){//如果来自标签列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加问题标签
	              	{path : '/admin/control/questionTag/manage/edit',component : () => loadModule('./admin/component/question/editQuestionTag.vue', options),name:'editQuestionTag',meta: {parent:'1-100200-2',title:'修改问题标签'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'questionTagList'){//如果来自标签列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改问题标签
	              	{path : '/admin/control/question/allAuditQuestion',component : () => loadModule('./admin/component/question/allAuditQuestion.vue', options), name:'allAuditQuestion',meta: {index:'1-100200-3',title:'全部待审核问题',cacheNumber:'0'}},//全部待审核问题
	              	{path : '/admin/control/question/allAuditAnswer',component : () => loadModule('./admin/component/question/allAuditAnswer.vue', options), name:'allAuditAnswer',meta: {index:'1-100200-4',title:'全部待审核答案',cacheNumber:'0'}},//全部待审核答案
	              	{path : '/admin/control/question/allAuditAnswerReply',component : () => loadModule('./admin/component/question/allAuditAnswerReply.vue', options), name:'allAuditAnswerReply',meta: {index:'1-100200-5',title:'全部待审核回复',cacheNumber:'0'}},//全部待审核回复
	              	{path : '/admin/control/question/search',component : () => loadModule('./admin/component/question/questionSearch.vue', options), name:'questionSearch',meta: {index:'1-100200-6',title:'问题搜索',cacheNumber:'0'}},//问题搜索
	              	
	              	
	              	{path : '/admin/control/feedback/list',component : () => loadModule('./admin/component/feedback/feedbackList.vue',options), name:'feedbackList',meta: {index:'1-100300-1',title:'留言列表',cacheNumber:'0'}},//留言列表
	              	{path : '/admin/control/feedback/manage/view',component : () => loadModule('./admin/component/feedback/feedbackView.vue',options) ,name:'feedbackView',meta: {parent:'1-100300-1',title:'留言查看'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'feedbackList'){//如果来自留言列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//留言查看
	              	{path : '/admin/control/links/list',component : () => loadModule('./admin/component/links/linksList.vue',options), name:'linksList',meta: {index:'1-100400-1',title:'友情链接列表',cacheNumber:'0'}},//友情链接列表
	              	{path : '/admin/control/links/manage/add',component : () => loadModule('./admin/component/links/addLinks.vue',options) ,name:'addLinks',meta: {parent:'1-100400-1',title:'添加友情链接'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'linksList'){//如果来自友情链接列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加友情链接
	              	{path : '/admin/control/links/manage/edit',component : () => loadModule('./admin/component/links/editLinks.vue',options) ,name:'editLinks',meta: {parent:'1-100400-1',title:'修改友情链接'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'linksList'){//如果来自友情链接列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改友情链接
	              	
	              	{path : '/admin/control/user/search',component : () => loadModule('./admin/component/user/userSearch.vue',options), name:'userSearch',meta: {index:'2-200100-1',title:'会员搜索',cacheNumber:'0'}},//会员搜索
	              	{path : '/admin/control/user/list',component : () => loadModule('./admin/component/user/userList.vue',options), name:'userList',meta: {index:'2-200100-2',title:'会员列表',cacheNumber:'0'}},//会员列表
	              	{path : '/admin/control/user/manage/show',component : () => loadModule('./admin/component/user/userShow.vue',options) ,name:'userShow',meta: {parent:'2-200100-2',title:'用户查看'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userList' || from.name == 'userSearch'){//如果来自会员列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//用户查看
	              	{path : '/admin/control/user/manage/add',component : () => loadModule('./admin/component/user/addUser.vue',options) ,name:'addUser',meta: {parent:'2-200100-2',title:'添加会员'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userList'){//如果来自会员列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加会员
	              	{path : '/admin/control/user/manage/edit',component : () => loadModule('./admin/component/user/editUser.vue',options) ,name:'editUser',meta: {parent:'2-200100-2',title:'修改会员'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userList'){//如果来自会员列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员
	              	{path : '/admin/control/user/manage/allTopic',component : () => loadModule('./admin/component/user/allTopic.vue', options) ,name:'allTopic',meta: {parent:'2-200100-2',title:'发表的话题'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的话题
	              	{path : '/admin/control/user/manage/allComment',component : () => loadModule('./admin/component/user/allComment.vue', options) ,name:'allComment',meta: {parent:'2-200100-2',title:'发表的评论'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的评论
	              	{path : '/admin/control/user/manage/allReply',component : () => loadModule('./admin/component/user/allReply.vue', options) ,name:'allReply',meta: {parent:'2-200100-2',title:'发表的回复'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的回复
	              	{path : '/admin/control/user/manage/allQuestion',component : () => loadModule('./admin/component/user/allQuestion.vue', options) ,name:'allQuestion',meta: {parent:'2-200100-2',title:'发表的话题'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的问题
	              	{path : '/admin/control/user/manage/allAnswer',component : () => loadModule('./admin/component/user/allAnswer.vue', options) ,name:'allAnswer',meta: {parent:'2-200100-2',title:'发表的评论'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的答案
	              	{path : '/admin/control/user/manage/allAnswerReply',component : () => loadModule('./admin/component/user/allAnswerReply.vue', options),name:'allAnswerReply',meta: {parent:'2-200100-2',title:'发表的回复'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userShow'){//如果来自用户查看,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//发表的答案回复
	              	{path : '/admin/control/privateMessage/manage/privateMessageList',component : () => loadModule('./admin/component/message/privateMessageList.vue',options), name:'privateMessageList',meta: {parent:'2-200100-2',title:'私信列表'}},//私信列表
	              	{path : '/admin/control/privateMessage/manage/privateMessageChatList',component : () => loadModule('./admin/component/message/privateMessageChatList.vue',options), name:'privateMessageChatList',meta: {parent:'2-200100-2',title:'私信对话列表'}},//私信对话列表
	              	{path : '/admin/control/systemNotify/manage/subscriptionSystemNotifyList',component : () => loadModule('./admin/component/message/subscriptionSystemNotifyList.vue',options), name:'subscriptionSystemNotifyList',meta: {parent:'2-200100-2',title:'订阅系统通知列表'}},//订阅系统通知列表
	              	{path : '/admin/control/remind/manage/remindList',component : () => loadModule('./admin/component/message/remindList.vue',options), name:'remindList',meta: {parent:'2-200100-2',title:'提醒列表'}},//提醒列表
	              	{path : '/admin/control/favorite/list',component : () => loadModule('./admin/component/favorite/favoriteList.vue',options) , name:'favoriteList',meta: {parent:'2-200100-2',title:'收藏夹列表'}},//收藏夹列表
	              	{path : '/admin/control/topicFavorite/list',component : () => loadModule('./admin/component/favorite/topicFavoriteList.vue',options) , name:'topicFavoriteList',meta: {parent:'2-200100-2',title:'话题收藏夹列表'}},//话题收藏夹列表
	              	{path : '/admin/control/questionFavorite/list',component : () => loadModule('./admin/component/favorite/questionFavoriteList.vue',options) , name:'questionFavoriteList',meta: {parent:'2-200100-2',title:'问题收藏夹列表'}},//问题收藏夹列表
	              	{path : '/admin/control/like/list',component : () => loadModule('./admin/component/like/likeList.vue',options), name:'likeList',meta: {parent:'2-200100-2',title:'点赞列表'}},//点赞列表
	              	{path : '/admin/control/topicLike/list',component : () => loadModule('./admin/component/like/topicLikeList.vue',options), name:'topicLikeList',meta: {parent:'2-200100-2',title:'话题点赞列表'}},//话题点赞列表
	              	{path : '/admin/control/follow/list',component : () => loadModule('./admin/component/follow/followList.vue',options), name:'followList',meta: {parent:'2-200100-2',title:'关注列表'}},//关注列表
	              	{path : '/admin/control/follower/list',component : () => loadModule('./admin/component/follow/followerList.vue',options), name:'followerList',meta: {parent:'2-200100-2',title:'粉丝列表'}},//粉丝列表
	              	{path : '/admin/control/membershipCard/manage/membershipCardOrderList',component : () => loadModule('./admin/component/membershipCard/userMembershipCardOrderList.vue', options), name:'userMembershipCardOrderList',meta: {parent:'2-200100-2',title:'用户会员卡订单列表'}},//用户会员卡订单列表
	              	{path : '/admin/control/redEnvelope/giveRedEnvelope/list',component : () => loadModule('./admin/component/redEnvelope/giveRedEnvelopeList.vue',options), name:'giveRedEnvelopeList',meta: {parent:'2-200100-2',title:'发红包列表'}},//发红包列表
	              	{path : '/admin/control/redEnvelope/redEnvelopeAmountDistribution/list',component : () => loadModule('./admin/component/redEnvelope/redEnvelopeAmountDistributionList.vue',options), name:'redEnvelopeAmountDistributionList',meta: {parent:'2-200100-2',title:'发红包金额分配'}},//发红包金额分配
	              	{path : '/admin/control/redEnvelope/receiveRedEnvelope/list',component : () => loadModule('./admin/component/redEnvelope/receiveRedEnvelopeList.vue',options), name:'receiveRedEnvelopeList',meta: {parent:'2-200100-2',title:'收红包列表'}},//收红包列表
	              	{path : '/admin/control/pointLog/list',component : () => loadModule('./admin/component/user/pointLogList.vue', options), name:'pointLogList',meta: {parent:'2-200100-2',title:'积分日志列表'}},//积分日志列表
	              	{path : '/admin/control/pointLog/manage/show',component : () => loadModule('./admin/component/user/pointLogShow.vue', options), name:'pointLogShow',meta: {parent:'2-200100-2',title:'积分日志详细'}},//积分日志详细
	              	{path : '/admin/control/paymentLog/list',component : () => loadModule('./admin/component/payment/paymentLogList.vue', options), name:'paymentLogList',meta: {parent:'2-200100-2',title:'支付日志列表'}},//支付日志列表
	              	{path : '/admin/control/paymentLog/manage/show',component : () => loadModule('./admin/component/payment/paymentLogShow.vue', options), name:'paymentLogShow',meta: {parent:'2-200100-2',title:'支付日志详细'}},//支付日志详细
	              	{path : '/admin/control/userLoginLog/list',component : () => loadModule('./admin/component/user/userLoginLogList.vue', options), name:'userLoginLogList',meta: {parent:'2-200100-2',title:'登录日志列表'}},//登录日志列表
	              	
	              	
	              	
	              	
	              	{path : '/admin/control/userRole/list',component : () => loadModule('./admin/component/user/userRoleList.vue', options), name:'userRoleList',meta: {index:'2-200100-3',title:'会员角色',cacheNumber:'0'}},//会员角色
	              	{path : '/admin/control/userRole/manage/add',component : () => loadModule('./admin/component/user/addUserRole.vue', options) ,name:'addUserRole',meta: {parent:'2-200100-3',title:'添加会员角色'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userRoleList'){//如果来自会员角色列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加会员角色
	              	{path : '/admin/control/userRole/manage/edit',component : () => loadModule('./admin/component/user/editUserRole.vue', options) ,name:'editUserRole',meta: {parent:'2-200100-3',title:'修改会员角色'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userRoleList'){//如果来自会员角色列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员角色
	              	
	              	{path : '/admin/control/userGrade/list',component : () => loadModule('./admin/component/user/userGradeList.vue', options), name:'userGradeList',meta: {index:'2-200100-4',title:'会员等级',cacheNumber:'0'}},//会员等级
	              	{path : '/admin/control/userGrade/manage/add',component : () => loadModule('./admin/component/user/addUserGrade.vue', options),name:'addUserGrade',meta: {parent:'2-200100-4',title:'添加会员等级'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userGradeList'){//如果来自会员等级列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加会员等级
	              	{path : '/admin/control/userGrade/manage/edit',component : () => loadModule('./admin/component/user/editUserGrade.vue', options),name:'editUserGrade',meta: {parent:'2-200100-4',title:'修改会员等级'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userGradeList'){//如果来自会员等级列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员等级
	              	{path : '/admin/control/userCustom/list',component : () => loadModule('./admin/component/user/userCustomList.vue', options), name:'userCustomList',meta: {index:'2-200100-5',title:'会员注册项',cacheNumber:'0'}},//会员注册项
	              	{path : '/admin/control/userCustom/manage/add',component : () => loadModule('./admin/component/user/addUserCustom.vue', options),name:'addUserCustom',meta: {parent:'2-200100-5',title:'添加会员注册项'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userCustomList'){//如果来自会员注册项列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加会员注册项
	              	{path : '/admin/control/userCustom/manage/edit',component : () => loadModule('./admin/component/user/editUserCustom.vue', options),name:'editUserCustom',meta: {parent:'2-200100-5',title:'修改会员注册项'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'userCustomList'){//如果来自会员注册项列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员注册项
	              	
	              	{path : '/admin/control/disableUserName/list',component : () => loadModule('./admin/component/user/disableUserNameList.vue', options), name:'disableUserNameList',meta: {index:'2-200100-6',title:'会员注册禁止用户名称',cacheNumber:'0'}},//会员注册禁止用户名称
	              	{path : '/admin/control/disableUserName/manage/add',component : () => loadModule('./admin/component/user/addDisableUserName.vue', options),name:'addDisableUserName',meta: {parent:'2-200100-6',title:'添加会员注册禁止用户名称'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'disableUserNameList'){//如果来自会员注册禁止用户名称列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员注册禁止用户名称
	              	{path : '/admin/control/disableUserName/manage/edit',component : () => loadModule('./admin/component/user/editDisableUserName.vue', options),name:'editDisableUserName',meta: {parent:'2-200100-6',title:'修改会员注册禁止用户名称'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'disableUserNameList'){//如果来自会员注册禁止用户名称列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员注册禁止用户名称
	              	
	              	{path : '/admin/control/membershipCard/list',component : () => loadModule('./admin/component/membershipCard/membershipCardList.vue', options), name:'membershipCardList',meta: {index:'2-200200-1',title:'会员卡列表',cacheNumber:'0'}},//会员卡列表
	              	{path : '/admin/control/membershipCard/manage/add',component : () => loadModule('./admin/component/membershipCard/addMembershipCard.vue', options),name:'addMembershipCard',meta: {parent:'2-200200-1',title:'添加会员卡'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'membershipCardList'){//如果来自会员卡列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加会员卡
	              	{path : '/admin/control/membershipCard/manage/edit',component : () => loadModule('./admin/component/membershipCard/editMembershipCard.vue', options),name:'editMembershipCard',meta: {parent:'2-200200-1',title:'修改会员卡'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'membershipCardList'){//如果来自会员卡列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改会员卡
	              	{path : '/admin/control/membershipCardOrder/list',component : () => loadModule('./admin/component/membershipCard/membershipCardOrderList.vue', options), name:'membershipCardOrderList',meta: {index:'2-200200-2',title:'会员卡订单列表',cacheNumber:'0'}},//会员卡订单列表
	            	
	              	
	            	{path : '/admin/control/staff/list',component : () => loadModule('./admin/component/staff/staffList.vue', options), name:'staffList',meta: {index:'2-200300-1',title:'员工列表',cacheNumber:'0'}},//员工列表
	            	{path : '/admin/control/staff/manage/addStaff',component : () => loadModule('./admin/component/staff/addStaff.vue', options),name:'addStaff',meta: {parent:'2-200300-1',title:'添加员工'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'staffList'){//如果来自员工列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加员工
	              	{path : '/admin/control/staff/manage/editStaff',component : () => loadModule('./admin/component/staff/editStaff.vue', options),name:'editStaff',meta: {parent:'2-200300-1',title:'修改员工'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'staffList'){//如果来自员工列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改员工
	              	{path : '/admin/control/staffLoginLog/list',component : () => loadModule('./admin/component/staff/staffLoginLogList.vue', options),name:'staffLoginLogList',meta: {parent:'2-200300-1',title:'员工登录日志'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'staffList'){//如果来自员工列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//员工登录日志
	            	{path : '/admin/control/roles/list',component : () => loadModule('./admin/component/staff/rolesList.vue', options), name:'rolesList',meta: {index:'2-200300-2',title:'角色列表',cacheNumber:'0'}},//角色列表
	            	{path : '/admin/control/acl/manage/addRoles',component : () => loadModule('./admin/component/staff/addRoles.vue', options),name:'addRoles',meta: {parent:'2-200300-2',title:'添加角色'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'rolesList'){//如果来自角色列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加角色
	              	{path : '/admin/control/acl/manage/editRoles',component : () => loadModule('./admin/component/staff/editRoles.vue', options),name:'editRoles',meta: {parent:'2-200300-2',title:'修改角色'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'rolesList'){//如果来自角色列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改角色
	            	
	            	
	              	
	              	{path : '/admin/control/helpType/list',component : () => loadModule('./admin/component/help/helpTypeList.vue', options),name:'helpTypeList',meta: {index:'3-300100-1',title:'帮助分类',cacheNumber:'0'}},//帮助分类
	              	{path : '/admin/control/helpType/manage/add',component : () => loadModule('./admin/component/help/addHelpType.vue', options),name:'addHelpType',meta: {parent:'3-300100-1',title:'添加帮助分类'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'helpTypeList'){//如果来自帮助分类列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加帮助分类
	              	{path : '/admin/control/helpType/manage/edit',component : () => loadModule('./admin/component/help/editHelpType.vue', options),name:'editHelpType',meta: {parent:'3-300100-1',title:'修改帮助分类'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'helpTypeList'){//如果来自帮助分类列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改帮助分类
	              	{path : '/admin/control/helpType/manage/merger',component : () => loadModule('./admin/component/help/mergerHelpType.vue', options),name:'mergerHelpType',meta: {parent:'3-300100-1',title:'合并帮助分类'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'helpTypeList'){//如果来自帮助分类列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//合并帮助分类
	              	{path : '/admin/control/help/list',component : () => loadModule('./admin/component/help/helpList.vue', options), name:'helpList',meta: {index:'3-300100-2',title:'帮助列表',cacheNumber:'0'}},//帮助列表
	              	{path : '/admin/control/help/manage/view',component : () => loadModule('./admin/component/help/helpView.vue', options),name:'helpView',meta: {parent:'3-300100-2',title:'帮助内容',}},//帮助内容
	             	
	              	{path : '/admin/control/help/manage/add',component : () => loadModule('./admin/component/help/addHelp.vue', options),name:'addHelp',meta: {parent:'3-300100-2',title:'添加帮助'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'helpList'){//如果来自帮助列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加帮助
	              	{path : '/admin/control/help/manage/edit',component : () => loadModule('./admin/component/help/editHelp.vue', options),name:'editHelp',meta: {parent:'3-300100-2',title:'修改帮助'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'helpList'){//如果来自帮助列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改帮助
	              	
	              	
	              	{path : '/admin/control/template/list',component : () => loadModule('./admin/component/template/templateList.vue', options) ,name:'templateList',meta: {index:'3-300200-1',title:'模板列表',cacheNumber:'0'}},//模板列表
	              	{path : '/admin/control/template/manage/add',component : () => loadModule('./admin/component/template/addTemplate.vue', options) ,name:'addTemplate',meta: {parent:'3-300200-1',title:'添加模板'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加模板
	              	{path : '/admin/control/template/manage/edit',component : () => loadModule('./admin/component/template/editTemplate.vue', options) ,name:'editTemplate',meta: {parent:'3-300200-1',title:'修改模板'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改模板
	              	{path : '/admin/control/template/manage/importTemplateList',component : () => loadModule('./admin/component/template/importTemplateList.vue', options) ,name:'importTemplateList',meta: {parent:'3-300200-1',title:'导入模板列表'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//导入模板列表
	              	{path : '/admin/control/forumCode/list',component : () => loadModule('./admin/component/forumCode/forumCodeList.vue', options) ,name:'forumCodeList',meta: {parent:'3-300200-1',title:'版块代码'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//版块代码
	              	{path : '/admin/control/resource/list',component : () => loadModule('./admin/component/template/resourceList.vue', options),name:'resourceList',meta: {parent:'3-300200-1',title:'资源列表'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//资源列表
	              	{path : '/admin/control/layout/list',component : () => loadModule('./admin/component/template/layoutList.vue', options),name:'layoutList',meta: {parent:'3-300200-1',title:'布局列表'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//布局列表
	              	{path : '/admin/control/layout/manage/add',component : () => loadModule('./admin/component/template/addLayout.vue', options) ,name:'addLayout',meta: {parent:'3-300200-1',title:'添加布局'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'layoutList'){//如果来自布局列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加布局
	              	{path : '/admin/control/layout/manage/editLayout',component : () => loadModule('./admin/component/template/editLayout.vue', options),name:'editLayout',meta: {parent:'3-300200-1',title:'修改布局'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'layoutList'){//如果来自布局列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改布局
	              	{path : '/admin/control/layout/manage/editLayoutCode',component : () => loadModule('./admin/component/template/editLayoutCode.vue', options),name:'editLayoutCode',meta: {parent:'3-300200-1',title:'布局代码编辑'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'layoutList'){//如果来自布局列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//布局代码编辑
	              	{path : '/admin/control/column/manage/list',component : () => loadModule('./admin/component/template/columnList.vue', options),name:'columnList',meta: {parent:'3-300200-1',title:'栏目列表'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'templateList'){//如果来自模板列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//栏目列表
	              	{path : '/admin/control/forum/list',component : () => loadModule('./admin/component/template/forumList.vue', options),name:'forumList',meta: {parent:'3-300200-1',title:'版块列表'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'layoutList'){//如果来自布局列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//版块列表
	              	{path : '/admin/control/forum/manage/add',component : () => loadModule('./admin/component/template/addForum.vue', options) ,name:'addForum',meta: {parent:'3-300200-1',title:'添加版块'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'forumList'){//如果来自版块列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加版块
	              	{path : '/admin/control/forum/manage/edit',component : () => loadModule('./admin/component/template/editForum.vue', options) ,name:'editForum',meta: {parent:'3-300200-1',title:'修改版块'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'forumList'){//如果来自版块列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改版块
	              	
	              	{path : '/admin/control/pageView/list',component : () => loadModule('./admin/component/statistic/pageViewList.vue', options), name:'pageViewList',meta: {index:'4-400100-1',title:'浏览量列表',cacheNumber:'0'}},//浏览量列表
	              	{path : '/admin/control/filePackage/list',component : () => loadModule('./admin/component/filePackage/filePackageList.vue',options), name:'filePackageList',meta: {index:'4-400200-1',title:'压缩文件列表',cacheNumber:'0'}},//压缩文件列表
	              	{path : '/admin/control/filePackage/manage/package',component : () => loadModule('./admin/component/filePackage/package.vue',options) ,name:'package',meta: {parent:'4-400200-1',title:'压缩文件打包'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'filePackageList'){//如果来自压缩文件列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//压缩文件打包
	              	
	              	{path : '/admin/control/systemNotify/list',component : () => loadModule('./admin/component/message/systemNotifyList.vue',options), name:'systemNotifyList',meta: {index:'4-400300-1',title:'系统通知列表',cacheNumber:'0'}},//系统通知列表
	              	{path : '/admin/control/systemNotify/manage/add',component : () => loadModule('./admin/component/message/addSystemNotify.vue',options), name:'addSystemNotify',meta: {parent:'4-400300-1',title:'添加系统通知'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'systemNotifyList'){//如果来自系统通知列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加系统通知
	              	{path : '/admin/control/systemNotify/manage/edit',component : () => loadModule('./admin/component/message/editSystemNotify.vue',options), name:'editSystemNotify',meta: {parent:'4-400300-1',title:'修改系统通知'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'systemNotifyList'){//如果来自系统通知列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改系统通知
	              	
	              	{path : '/admin/control/topicUnhidePlatformShare/list',component : () => loadModule('./admin/component/platformShare/topicUnhidePlatformShareList.vue', options), name:'topicUnhidePlatformShareList',meta: {index:'4-400400-1',title:'解锁话题隐藏内容分成',cacheNumber:'0'}},//解锁话题隐藏内容分成
	             	{path : '/admin/control/questionRewardPlatformShare/list',component : () => loadModule('./admin/component/platformShare/questionRewardPlatformShareList.vue', options), name:'questionRewardPlatformShareList',meta: {index:'4-400400-2',title:'问答悬赏平台分成',cacheNumber:'0'}},//问答悬赏平台分成
	              	
	              	{path : '/admin/control/systemSetting/manage/edit',component : () => loadModule('./admin/component/setting/editSystemSetting.vue', options), name:'editSystemSetting',meta: {index:'5-500100-1',title:'基本设置',cacheNumber:'0'}},//基本设置
	              	{path : '/admin/control/systemSetting/manage/maintainData',component : () => loadModule('./admin/component/setting/maintainData.vue', options), name:'maintainData',meta: {index:'5-500100-2',title:'维护数据',cacheNumber:'0'}},//维护数据
	              	{path : '/admin/control/filterWord/manage/view',component : () => loadModule('./admin/component/setting/viewFilterWord.vue', options), name:'viewFilterWord',meta: {index:'5-500100-3',title:'敏感词',cacheNumber:'0'}},//敏感词
	              	{path : '/admin/control/dataBase/list',component : () => loadModule('./admin/component/data/dataBaseList.vue', options), name:'dataBaseList',meta: {index:'5-500100-4',title:'数据库备份/还原',cacheNumber:'0'}},//数据库备份/还原
	              	{path : '/admin/control/dataBase/manage/backup',component : () => loadModule('./admin/component/data/dataBackup.vue', options), name:'dataBackup',meta: {parent:'5-500100-4',title:'数据库备份'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'dataBaseList'){//如果来自数据库备份/还原列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//数据备份列表
	              	{path : '/admin/control/dataBase/manage/reset',component : () => loadModule('./admin/component/data/dataReset.vue', options), name:'dataReset',meta: {parent:'5-500100-4',title:'数据库还原'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'dataBaseList'){//如果来自数据库备份/还原列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//数据还原列表
	              	{path : '/admin/control/systemSetting/manage/nodeParameter',component : () => loadModule('./admin/component/setting/nodeParameter.vue', options), name:'nodeParameter',meta: {index:'5-500100-5',title:'服务器节点参数',cacheNumber:'0'}},//服务器节点参数
	              	{path : '/admin/control/upgrade/manage/upgradeSystemList',component : () => loadModule('./admin/component/upgrade/upgradeSystemList.vue', options), name:'upgradeSystemList',meta: {index:'5-500100-6',title:'升级',cacheNumber:'0'}},//升级
	              	
	              	
	              
	              	{path : '/admin/control/onlinePaymentInterface/list',component : () => loadModule('./admin/component/payment/onlinePaymentInterfaceList.vue', options), name:'onlinePaymentInterfaceList',meta: {index:'5-500200-1',title:'在线支付接口',cacheNumber:'0'}},//在线支付接口
	              	{path : '/admin/control/onlinePaymentInterface/manage/add',component : () => loadModule('./admin/component/payment/addOnlinePaymentInterface.vue', options), name:'addOnlinePaymentInterface',meta: {parent:'5-500200-1',title:'添加在线支付接口'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'onlinePaymentInterfaceList'){//如果来自在线支付接口列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加在线支付接口
	              	{path : '/admin/control/onlinePaymentInterface/manage/edit',component : () => loadModule('./admin/component/payment/editOnlinePaymentInterface.vue', options), name:'editOnlinePaymentInterface',meta: {parent:'5-500200-1',title:'修改在线支付接口'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'onlinePaymentInterfaceList'){//如果来自在线支付接口列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改在线支付接口
	              	{path : '/admin/control/smsInterface/list',component : () => loadModule('./admin/component/sms/smsInterfaceList.vue', options), name:'smsInterfaceList',meta: {index:'5-500300-1',title:'短信接口列表',cacheNumber:'0'}},//短信接口列表
	              	{path : '/admin/control/smsInterface/manage/add',component : () => loadModule('./admin/component/sms/addSmsInterface.vue', options), name:'addSmsInterface',meta: {parent:'5-500300-1',title:'添加短信接口'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'smsInterfaceList'){//如果来自短信接口列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//添加短信接口
	              	{path : '/admin/control/smsInterface/manage/edit',component : () => loadModule('./admin/component/sms/editSmsInterface.vue', options), name:'editSmsInterface',meta: {parent:'5-500300-1',title:'修改短信接口'},
	              		beforeEnter: (to, from, next) => {
	              			if(from.name == 'smsInterfaceList'){//如果来自短信接口列表,则删除缓存
	              				store.commit('setCacheNumber');
	              			}
	              			next();
	              		}
	              	},//修改短信接口
	              	{path : '/admin/control/sendSmsLog/list',component : () => loadModule('./admin/component/sms/sendSmsLogList.vue', options), name:'sendSmsLogList',meta: {index:'5-500300-2',title:'短信发送错误日志',cacheNumber:'0'}},//短信发送错误日志
	              	
	              	
	              	{path : '/admin/control/thirdPartyLoginInterface/list',component : () => loadModule('./admin/component/thirdParty/thirdPartyLoginInterfaceList.vue',options), name:'thirdPartyLoginInterfaceList',meta: {index:'5-500500-1',title:'第三方登录接口列表',cacheNumber:'0'}},//第三方登录接口列表
	              	{path : '/admin/control/thirdPartyLoginInterface/manage/add',component : () => loadModule('./admin/component/thirdParty/addThirdPartyLoginInterface.vue',options), name:'addThirdPartyLoginInterface',meta: {parent:'5-500400-1',title:'添加第三方登录接口'},
	              		beforeEnter: (to, from, next) => {
		              		if(from.name == 'thirdPartyLoginInterfaceList'){//如果来自第三方登录接口列表,则删除缓存
		              			store.commit('setCacheNumber');
		              		}
		              		next();
	              		}
		             },//添加第三方登录接口
		             {path : '/admin/control/thirdPartyLoginInterface/manage/edit',component : () => loadModule('./admin/component/thirdParty/editThirdPartyLoginInterface.vue',options), name:'editThirdPartyLoginInterface',meta: {parent:'5-500400-1',title:'修改第三方登录接口'},
		            	 beforeEnter: (to, from, next) => {
			              	if(from.name == 'thirdPartyLoginInterfaceList'){//如果来自第三方登录接口列表,则删除缓存
			              			store.commit('setCacheNumber');
			              	}
			              	next();
		            	 }
			          },//修改第三方登录接口
	              	
			          
			          
			          
			          {path : '/admin/control/thumbnail/list',component : () => loadModule('./admin/component/thumbnail/thumbnailList.vue',options), name:'thumbnailList',meta: {index:'5-500700-1',title:'缩略图列表',cacheNumber:'0'}},//缩略图列表
			          {path : '/admin/control/thumbnail/manage/add',component : () => loadModule('./admin/component/thumbnail/addThumbnail.vue',options), name:'addThumbnail',meta: {parent:'5-500700-1',title:'添加缩略图裁剪尺寸'},
		              		beforeEnter: (to, from, next) => {
			              		if(from.name == 'thumbnailList'){//如果来自缩略图列表,则删除缓存
			              			store.commit('setCacheNumber');
			              		}
			              		next();
		              		}
			           },//添加缩略图裁剪尺寸
			          
	             ]
   },
   
   {path : '/admin/login',component : login_component},//登录

  // {path : '/:pathMatch(.*)*', redirect:{name:'homeRoute'}}//其余路由重定向至后台首页。 用redirect : '/admin/control/manage/home'方式控制台会有警告
   { path: '/:pathMatch(.*)*', redirect: to => {

	   
	   return { name: 'home' };//重定向的 字符串路径/路径对象
   }}
   

   
];


var scrollBehavior = function (to, from, savedPosition) {
	if (savedPosition) {
		// savedPosition仅适用于popstate导航。
		return savedPosition;
	} else {
		if(document.querySelector(".el-main")){//默认滚动到顶部
			document.querySelector(".el-main").scrollTop = 0;
		}
		
		
		let validFullPath = null;//有效的
		A:for (let [key,value] of store.state.historyPath){
			if(value != null && value.length >0){//fullPathList
				for(let i=value.length-1;i>=0;i--){
					let fullPath = value[i];
					if(to.fullPath == fullPath.url){
						validFullPath = fullPath;
						break A;
					}
				}
			}
		}
		
		let to_path_index = '';//to参数路径索引
		let to_path_index_root = false;//to参数路径索引是否为根节点
		let from_path_index = '';//from参数路径索引
		let from_path_index_root = false;//from参数路径索引是否为根节点
		
		for(let i=0; i<routes.length; i++){
			let children = routes[i].children;
			if(children != null && children.length){
				for(let j=0; j<children.length; j++){
				   let route_children = children[j];
				   if(route_children.path == to.path){
					   if(route_children.meta.index != undefined){
						   to_path_index = route_children.meta.index;
						   to_path_index_root = true;
					   }else{
						   to_path_index = route_children.meta.parent;
						   to_path_index_root = false;
					   }
				   }
				   if(route_children.path == from.path){
					   if(route_children.meta.index != undefined){
						   from_path_index = route_children.meta.index;
						   from_path_index_root = true;
					   }else{
						   from_path_index = route_children.meta.parent;
						   from_path_index_root = false;
					   }
				   }
			   }
		   }   
		}
		
		//只有详细页返回列表页或不同标签页切换，才执行滚动
		if((from_path_index == to_path_index && from_path_index_root == false && to_path_index_root == true) || (from_path_index != to_path_index)){
			if(validFullPath != null){
				if(document.querySelector(".el-main")){
					const { nextTick } = Vue;
					nextTick(function(){
						setTimeout(() => {
							document.querySelector(".el-main").scrollTop = validFullPath.scrollTop;
					    }, 300)
					});
				}
			}
			
		}
	}
};

//创建路由对象
const router = VueRouter.createRouter({
	// html5模式 去掉锚点
	history: VueRouter.createWebHistory(),
	scrollBehavior : scrollBehavior, //记住页面的滚动位置 html5模式适用
	//base : getMetaTag().contextPath, //虚拟目录
	routes
});

//在跳转之前监听路由
router.beforeEach((to,from,next)=>{
	
	let scrollTop = 0;
	if(document.querySelector(".el-main")){
		scrollTop = document.querySelector(".el-main").scrollTop;
	}
	
	store.state.historyPath.forEach(function(value,key){
		
		let fullPathList = value;
		if(fullPathList != null && fullPathList.length >0){
			for(let i=0; i<fullPathList.length; i++){
				if(from.fullPath == fullPathList[i].url){
					fullPathList[i].scrollTop = scrollTop;
					let obj =new Object();
				    obj.key = key;
				    obj.value = fullPathList;
				    store.commit('addHistoryPath',obj);
				}
			}
		}
	});
	
	next();
})

//创建全局状态存储对象
const store = Vuex.createStore({
	//存储状态值
	state : {
		baseURL : '', //系统路径
		fileStorageSystem : 0, //文件存储系统 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS
		
		windowBrowseStatus :new Map(),//窗口浏览状态  key:导航索引 value:{isRecordPath:true,step:0}     isRecordPath:是否记录路径  step:步长 0为没有执行"前进/后退"命令
		historyPath :new Map(),//历史路径记录  key:导航索引 value: 浏览URL全路径集合
		
		cacheComponents: [],//keepAlive缓存组件
		
		
	},
	
	
	// 状态值的改变方法,操作状态值,mutations方法必须是同步方法
	// 提交mutations是更改Vuex状态的唯一方法
	mutations : {
		//设置系统路径
		setBaseURL : function (state, baseURL) {
			state.baseURL = baseURL;
		},
		//设置文件存储系统
		setFileStorageSystem : function (state, fileStorageSystem) {
			state.fileStorageSystem = fileStorageSystem;
		},
		
		//设置缓存组件
		setCacheComponents : function (state, data) {
			state.cacheComponents = data;
		},
		//添加历史路径记录
		addHistoryPath:  function (state, object){
			state.historyPath.set(object.key,object.value);
		},
		//删除历史路径记录 index:当前窗口导航索引
		deleteHistoryPath:  function (state, index){
			
			state.historyPath.delete(index);
			
		},
		//清空历史路径记录
		clearHistoryPath:  function (state){
			state.historyPath.clear();		
		},
		
		//添加窗口浏览状态
		addWindowBrowseStatus:  function (state, object){	
			state.windowBrowseStatus.set(object.key,object.value);
		},
		//删除窗口浏览状态 index:当前窗口导航索引
		deleteWindowBrowseStatus:  function (state, index){
			state.windowBrowseStatus.delete(index);
		},
		//清空窗口浏览状态
		clearWindowBrowseStatus:  function (state){
			state.windowBrowseStatus.clear();		
		},
		
		
		//设置缓存编号
		setCacheNumber : function (state) {
			//当前窗口索引
		    let currentWindowIndex = "";
		    
		    let _linkTag = window.sessionStorage.getItem('linkTag');
			if(_linkTag != null){
				let linkTag = JSON.parse(_linkTag);
				for(let i=0; i<linkTag.length; i++){
		    		let _tag = linkTag[i];
		    		if(_tag.active){//如果已激活
		    			currentWindowIndex = _tag.index;
		    			break;
		    		}
				}
			}
			for(let i=0; i<routes.length; i++){
				let _children = routes[i].children;
				if(_children != null && _children.length){
					for(let j=0; j<_children.length; j++){
					   let _route_children = _children[j];
					   if(_route_children.meta.index == currentWindowIndex){
							_route_children.meta.cacheNumber = new Date().getTime();
					   }
				   }
			   }   
			}
		}
	},
	// 在store中定义getters（可以认为是store的计算属性）。Getters接收state作为其第一个函数
	getters : {
		cacheComponents: function (state) {
			return state.cacheComponents;
		},
		//keep-alive缓存key
		cacheKey: function () {
			let cacheNumber = "";
			
			//当前窗口索引
		    let currentWindowIndex = "";
		    
		    let _linkTag = window.sessionStorage.getItem('linkTag');
			if(_linkTag != null){
				let linkTag = JSON.parse(_linkTag);
				for(let i=0; i<linkTag.length; i++){
		    		let _tag = linkTag[i];
		    		if(_tag.active){//如果已激活
		    			currentWindowIndex = _tag.index;
		    			break;
		    		}
				}
			}
			
			
			
			for(let i=0; i<routes.length; i++){
				let _children = routes[i].children;
				if(_children != null && _children.length){
					for(let j=0; j<_children.length; j++){
					   let _route_children = _children[j];
					  
					   if(_route_children.meta.index == currentWindowIndex){
							cacheNumber = _route_children.meta.cacheNumber;
					   }
				   }
			   }   
			}
			
			return currentWindowIndex+"::"+cacheNumber+"::"+app.config.globalProperties.$route.fullPath;
		},
		
		//后台管理框架--左侧菜单栏链接
		allLinkList: function (state) {
			let arr = new Array();
			for(let i=0; i<routes.length; i++){
				let _children = routes[i].children;
				if(_children != null && _children.length){
					for(let j=0; j<_children.length; j++){
					   let _route_children = _children[j];
					   arr.push(_route_children);
				   }
			   }   
			}
			return arr;
		},
	},
	//异步操作方法
	actions : {}
});

//vue3-sfc-loader v0.7.3
const options = {
	moduleCache: {
		vue: Vue
	},
	async getFile(url) {
		const res = await fetch(url);
	        if ( !res.ok )
	        	throw Object.assign(new Error(res.statusText + ' ' + url), { res });
	        	return await res.text();
	},
	addStyle(textContent) {
		const style = Object.assign(document.createElement('style'), { textContent });
	    const ref = document.head.getElementsByTagName('style')[0] || null;
	    document.head.insertBefore(style, ref);
	},
}
const { loadModule } = window['vue3-sfc-loader'];


/** vue3-sfc-loader v0.8.0
const options = {
	moduleCache: {
		vue: Vue
	},
	async getFile(url) {
		const res = await fetch(url);
	    if ( !res.ok )
	        throw Object.assign(new Error(res.statusText + ' ' + url), { res });
	        return {
	        	getContentData: asBinary => asBinary ? res.arrayBuffer() : res.text(),
	        }
	    },
	addStyle(textContent) {
	    const style = Object.assign(document.createElement('style'), { textContent });
	    const ref = document.head.getElementsByTagName('style')[0] || null;
	    document.head.insertBefore(style, ref);
	},
}
const { loadModule } = window['vue3-sfc-loader'];
**/
//创建实例
var app = Vue.createApp({
	data: function() {
		return {
			
		}
	},
	created : function () {
		this.init();
		
		/**
		this.$loading({
		      lock: true,
		      text: 'Loading',
		      spinner: 'el-icon-loading',
		      background: 'rgba(0, 0, 0, 0.7)'
		});**/
		
	},
	computed: {
		cacheComponents : function () {
			return this.$store.getters.cacheComponents;
		},

	},
	watch: {
		//监听路由
		'$route' (to, from) {
			this.$nextTick(function(){//渲染结束再监听，防止识别窗口错误
				//当前窗口索引
			    let currentWindowIndex = "";
			    let _linkTag = window.sessionStorage.getItem('linkTag');
				if(_linkTag != null){
					let linkTag = JSON.parse(_linkTag);
					
					for(let i=0; i<linkTag.length; i++){
			    		let _tag = linkTag[i];
			    		if(_tag.active){//如果已激活
			    			currentWindowIndex = _tag.index;
			    			break;
			    		}
					}
				}

			    if(currentWindowIndex != ''){//如果URL对应当前窗口 	
		    		let isRecordPath = true;//是否记录路径 
		    		let windowBrowseStatus = this.$store.state.windowBrowseStatus.get(currentWindowIndex);
		    		if(windowBrowseStatus != null){
		    			isRecordPath = windowBrowseStatus.isRecordPath;
			    	}
		    		//删除窗口浏览状态
				    this.$store.commit('deleteWindowBrowseStatus',currentWindowIndex);
		    		
		    		if(!isRecordPath){//如果不记录路径
		    			//窗口浏览状态
				    	let _windowBrowseStatus = new Object();
				    	_windowBrowseStatus.isRecordPath = true;//是否记录路径 
				    	_windowBrowseStatus.step = windowBrowseStatus.step;	//步长 0为没有执行"前进/后退"命令
						
				    	let obj =new Object();
					    obj.key = currentWindowIndex;
					    obj.value = _windowBrowseStatus;
					    //添加窗口浏览状态
						this.$store.commit('addWindowBrowseStatus',obj);
		    		}else{
		    			//浏览URL全路径集合
				    	let fullPathList = new Array();
				    	
				    	
				    	//获取当前窗口对应的URL集合
				    	this.$store.state.historyPath.forEach(function(value,key){
		    				if(key == currentWindowIndex){
		    					fullPathList = value;
		    				}
		    	　　　　});
				    	
				    	
				    	
				    	if(windowBrowseStatus != null && windowBrowseStatus.step <0){//如果执行过"前进/后退"
				    		let count = windowBrowseStatus.step*-1;
			    			for(let i = fullPathList.length-1; i>=0; i--){//倒序循环
			    				
			    				//删除"前进/后退"到的URL之后的URL
			    				if(count > 0){
			    					fullPathList.splice(i, 1);
			    				}
			    				count--;
			    			}
			    			let obj =new Object();
						    obj.key = currentWindowIndex;
						    obj.value = fullPathList;
						    this.$store.commit('addHistoryPath',obj);
						    
						   
				    	}
				    	
				    	
				    	
				    	let isSame = false;//是否和上一条记录相同
				    	//如果和上一条记录相同，则本条不记录
				    	if(fullPathList.length >0 && fullPathList[fullPathList.length-1]){
				    		let fullPath = fullPathList[fullPathList.length-1];
				    		if(fullPath.url == to.fullPath){
				    			isSame = true;
				    		}
				    	}
				    	if(!isSame){
				    		//如果当前组超出100条历史记录，则删除超出部分
					    	if(fullPathList.length >=100){
					    		fullPathList.shift();//删除第一个
					    	}
					    	
					    	
					    	let fullPath =new Object();
					    	fullPath.url = to.fullPath;
					    	fullPath.scrollTop = 0;
					    	
					    	fullPathList.push(fullPath);
					    	
					    	let obj =new Object();
						    obj.key = currentWindowIndex;
						    obj.value = fullPathList;
						    
						    this.$store.commit('addHistoryPath',obj);
						  
						    
				    	}
				    	
				    	
		    		}
		    		
				    
			    }
			    
			    //窗口最新URL集合
			    let new_windowNewestURLList = new Array();
			    
			    //窗口旧URL集合
			    let old_windowNewestURLList = new Array();
			    
			    let _windowNewestURLList = window.sessionStorage.getItem('windowNewestURLList');
				if(_windowNewestURLList != null){
					let _old_windowNewestURLList = JSON.parse(_windowNewestURLList);
					if(_old_windowNewestURLList != null && _old_windowNewestURLList.length >0){
						old_windowNewestURLList = _old_windowNewestURLList;
					}
				}
				
				
				
			    let new_linkTag = window.sessionStorage.getItem('linkTag');
				if(new_linkTag != null){
					let linkTag = JSON.parse(new_linkTag);
					if(linkTag != null && linkTag.length >0){
						for(let i=0; i<linkTag.length; i++){
							let _tag = linkTag[i];
							
							
							if(_tag.active){//如果已激活
								let flag = false;//是否已添加窗口URL
								for(let j=0; j<old_windowNewestURLList.length; j++){
									let windowNewestURL = old_windowNewestURLList[j];
									if(windowNewestURL.index == _tag.index){
										windowNewestURL.url = this.$route.fullPath;
										flag = true;
									}
								}
								
								if(!flag){
									old_windowNewestURLList.push({
		    							index:_tag.index,
		    							url: this.$route.fullPath,
		    						});
									
								}
				    		}
							
							
							for(let j=0; j<old_windowNewestURLList.length; j++){
								let windowNewestURL = old_windowNewestURLList[j];
								if(windowNewestURL.index == _tag.index){
									new_windowNewestURLList.push(windowNewestURL);
									break;
								}
							}
						}
						
					}
					
				}
			    //保存窗口最后打开的URL
		    	window.sessionStorage.setItem('windowNewestURLList', JSON.stringify(new_windowNewestURLList));
		    	
	        });
		}
	},
	methods : {
		//初始化数据
		init : function(){
			this.$store.commit('setBaseURL', getMetaTag().baseURL);
			
		},	
	}
});






app.use(ElementPlus);
app.use(router);
app.use(store);
app.config.globalProperties.$ajax = ajax;//添加全局属性
app.mount('#app');













/****************************************** 公共方法 ************************************************/

//获取meta标签内容
function getMetaTag() {
	let baseURL = "";
	
	let meta = document.getElementsByTagName("meta");
	for (let i = 0; i < meta.length; i++) {
		if (meta[i].name == "_baseURL") {
			baseURL = meta[i].getAttribute("content");
		}
		
	}

	let global = {
		//系统路径
		baseURL : baseURL,
	};
	return global;
}



/**
 *map转化为对象（map所有键都是字符串，可以将其转换为对象）
 */
function strMapToObj(strMap){
	let obj= Object.create(null);
    for (let[k,v] of strMap) {
        obj[k] = v;
    }
    return obj;
}

/**
 *对象转换为Map
 */
function objToStrMap(obj){
    let strMap = new Map();
    for (let k of Object.keys(obj)) {
        strMap.set(k,obj[k]);
    }
    return strMap;
}
/**
 * 查找URL参数
 * @param uri 格式: aa=1&bb=2
 * @param name 参数名称
 * @return
 */
function findUrlParam(uri,name) {
	let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	let r = uri.match(reg); //匹配目标参数
	if (r != null) return r[2];
	return null; //返回参数值
}

/**
 * 解析url参数
 * @param url 格式: /qq?aa=1&bb=2
 * @return 参数对象
 */
function analyzeUrlParam(url) {
	let params = {};
	let urls = url.split("?");
	if(urls.length ==2){
		let arr = urls[1].split("&");
		for (let i = 0, l = arr.length; i < l; i++) {
			let a = arr[i].split("=");
			if(a[1] == undefined){//参数和值都为空时也加上&
				params[a[0]] = "";
			}else{
				params[a[0]] = a[1];
			}
			
		} 
	}
	return params;
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
	let newAdditionalURL = "";
	let tempArray = url.split("?");
	let baseURL = tempArray[0];
	let additionalURL = tempArray[1];
	let temp = "";
    
    if (additionalURL) {
        tempArray = additionalURL.split("&");
        for (i=0; i<tempArray.length; i++){
            if(tempArray[i].split('=')[0] != param){
                newAdditionalURL += temp + tempArray[i];
                temp = "&";
            }
        }
    }

    let rows_txt = temp + "" + param + "=" + paramVal;
    return baseURL + "?" + newAdditionalURL + rows_txt;
}


/**
 * 读取Cookie
 * @param name 名称
 * @return 值
 */
function getCookie(name) {
	if (!document.cookie) {
	    return null;
	}

	const xsrfCookies = document.cookie.split(';')
	    .map(c => c.trim())
	    .filter(c => c.startsWith(name + '='));

	if (xsrfCookies.length === 0) {
	    return null;
	}
	return decodeURIComponent(xsrfCookies[0].split('=')[1]);
}

/**
 * 创建富文本编辑器
 * @param ref 文本框对象
 * @param availableTag 允许使用的标签 ['source', '|']
 * @param uploadPath 上传文件路径  例如control/topic/manage.htm?method=upload&userName=admin&isStaff=true
 * @param userGradeList 会员等级
 */
function createEditor(ref,availableTag,uploadPath,userGradeList ) {
	let baseURL = app.config.globalProperties.$store.state.baseURL;
	let fileStorageSystem = app.config.globalProperties.$store.state.fileStorageSystem;//使用的文件存储系统

	KindEditor.lang({
        hide : '隐藏'
    });
   
	// 指定编辑器iframe document的CSS数据，用于设置可视化区域的样式。 单冒号(:)用于CSS3伪类，双冒号(::)用于CSS3伪元素。伪元素由双冒号和伪元素名称组成。双冒号是在当前规范中引入的，用于区分伪类和伪元素。但是伪类兼容现存样式，浏览器需要同时支持旧的伪类，比如:first-line、:first-letter、:before、:after等
    KindEditor.options.cssData = ".ke-content hide {"+
		"border: 0;"+
		"border-left: 3px solid #06b5ff;"+
		"margin-left: 10px;"+
		"padding: 0.5em;"+
		"min-height:26px;"+
		"display: block;"+
		"margin: 30px 0px 0px 0px;"+
	"}"+
	".ke-content .inputValue_10:before {"+
		"content: '密码: ' attr(input-value) '';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_20:before {"+
		"content: '回复话题可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_30:before {"+
		"content: '达到等级 ' attr(description) ' 可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_40:before {"+
		"content: '需要支付 ' attr(input-value) ' 积分可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+
	".ke-content .inputValue_50:before {"+
		"content: '需要支付 ' attr(input-value) ' 元费用可见';"+
		" color: #06b5ff;"+
		"font-size:14px;"+
		"position: absolute;"+
		"margin-top: -30px;"+
		"line-height: 30px;"+
	"}"+//突出编辑框的代码
	".ke-content .prettyprint {"+
		"min-height:20px;"+
		"background:#f8f8f8;"+
		"border:1px solid #ddd;"+
		"padding:5px;"+
	"}"+//默认字体大小
	"body {"+
		"font-size: 14px;"+
	"}";


    //指定要保留的HTML标记和属性。Object的key为HTML标签名，value为HTML属性数组，”.”开始的属性表示style属性。 注意属性要全部小写
    KindEditor.options.htmlTags['hide'] = ['hide-type','input-value','class','description'];
    

    
    let editor = KindEditor.create(ref, {
    	basePath : baseURL+'backstage/kindeditor/',//指定编辑器的根目录路径
    	themeType : 'style :minimalist customization',//极简主题 加冒号的是主题样式文件名称同时也是主题目录
		autoHeightMode : true,//值为true，并引入autoheight.js插件时自动调整高度
		formatUploadUrl :false,//false时不会自动格式化上传后的URL
		resizeType : 1,//2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。默认值: 2 
		allowPreviewEmoticons : true,//true或false，true时鼠标放在表情上可以预览表情
		allowImageUpload : true,//true时显示图片上传按钮
		allowFlashUpload :true,
		uploadModule : parseInt(fileStorageSystem),//上传模块 0.本地 10.SeaweedFS 20.MinIO 30.阿里云OSS
		uploadJson :baseURL+uploadPath,//指定浏览远程图片的服务器端程序
		filePostName:'file',//文件上传字段 默认imgFile  第三方文件服务器不受本参数影响
		items : availableTag,
     	userGradeList:userGradeList,
		afterChange : function() {
			this.sync();
		},
		afterBlur:function(){
			this.sync();
		}

	});
    return editor;
}
/**
 * 移除编辑器实例
 * @param ref 文本框对象
*/
function removeEditor(ref) {
	KindEditor.remove(ref);
} 


/**
 * 获取代码语言
 * @param languageCode
 */
function getLanguageClassName(languageCode) {
	let language_arr = new Array();
	language_arr.push("lang-xml_language-xml");
	language_arr.push("lang-css_language-css");
	language_arr.push("lang-html_language-html");
	language_arr.push("lang-js_language-JavaScript");
	language_arr.push("lang-java_language-java");
	language_arr.push("lang-pl_language-perl");
	language_arr.push("lang-py_language-python");
	language_arr.push("lang-rb_language-ruby");
	language_arr.push("lang-go_language-Go");
	language_arr.push("lang-cpp_language-C++");
	language_arr.push("lang-cs_language-C#");
	language_arr.push("lang-bsh_language-Bash + Shell");
		
	for(let k=0; k<language_arr.length; k++){
		let language = language_arr[k];
		let class_arr = new Array();
		class_arr = language.split('_');
		if(languageCode == class_arr[0]){
			
			return class_arr[1];
		}
	}
	return "language-markup";
}


/** **************** define static table ***************** */
let encTable = new Array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
		'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
		'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0',
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/');

let decTable = new Array(256);
// initial decTable
for (i = 0; i < encTable.length; i++) {
	decTable[encTable[i]] = i;
}
/** **************** end static table ***************** */

function base62_encode(str) {
	value = "";
	pos = 0, val = 0;
	for ( let i = 0; i < str.length; i++) {
		val = (val << 8) | (str.charCodeAt(i) & 0xFF);
		pos += 8;
		while (pos > 5) {
			c = encTable[val >> (pos -= 6)];
			value += c == 'i' ? "ia" : c == '+' ? "ib" : c == '/' ? "ic" : c;
			val &= ((1 << pos) - 1);
		}
	}
	if (pos > 0) {
		value += encTable[val << (6 - pos)];
		if (pos == 2) {
			value += "==";
		} else if (pos == 4) {
			value += "=";
		}
	}
	return value;
}

function base62_decode(str) {

	pos = 0, val = 0;
	value = "";
	for ( let i = 0; i < str.length; i++) {
		c = str.charAt(i);
		if (c == '=') {
			break;
		}
		if (c == 'i') {
			c = str.charAt(++i);
			c = c == 'a' ? 'i' : c == 'b' ? '+' : c == 'c' ? '/' : str.charAt(--i);
		}
		val = (val << 6) | decTable[c];
		pos += 6;
		while (pos > 7) {
			value += String.fromCharCode((val >> (pos -= 8)) & 0xFF);
			val &= ((1 << pos) - 1);
		}
	}
	return value;
}
/**
 * 添加base62等号
 * @param base62_str
 */
function add_base62_equals(base62_str){
	//let base62_str = 'VGhpcyBpcyBhbiBhd2Vzb21lIHNjcmlwdA==';

	// make URL friendly:
	base62_str = base62_str.replace(/\+/g, '-').replace(/\//g, '_').replace(/\=+$/, '');

	// reverse to original encoding
	if (base62_str.length % 4 != 0){
		base62_str += ('===').slice(0, 4 - (base62_str.length % 4));
	}
	base62_str = base62_str.replace(/-/g, '+').replace(/_/g, '/');
	return base62_str;
}
/**
 * 删除base62等号
 * @param base62_str
 */
function delete_base62_equals(base62_str){
	let str = base62_str;
    for (let i = base62_str.length - 1; i >= 0; i--) {
    	let char = base62_str[i];
        if (char == '=') {//等号结尾
            str = base62_str.slice(0, i);
        } else {
            break;
        }
    }
    return str;
}
/**
 * 计算标签对象距顶部的距离
 * @param element
 */
function getElementTop(element){
	let actualTop = element.offsetTop;
　　let current = element.offsetParent;

　　while (current !== null){
　　	actualTop += current.offsetTop;
　　　　current = current.offsetParent;
　　}

　　return actualTop;
}
/**
 * 转义html
 */
function escapeHtml(html) {
	return _.escape(html);//引入lodash.js
	
}
/**
 * 判断对象类型 getType(new Map()) // "map"
 * obj instanceof Map
 * @param obj
 */
function getType(obj) {
	let type = Object.prototype.toString.call(obj).match(/^\[object (.*)\]$/)[1].toLowerCase();
    if(type === 'string' && typeof obj === 'object') return 'object'; // Let "new String('')" return 'object'
    if (obj === null) return 'null'; // PhantomJS has type "DOMWindow" for null
    if (obj === undefined) return 'undefined'; // PhantomJS has type "DOMWindow" for undefined
    return type;
}
//锚点跳转
function anchorJump(id){
	
	if(document.querySelector("[aria-label*='源码编辑']")){//版块代码 --> 源码编辑
		//获取滚动窗口
		let position = document.querySelector("[aria-label*='源码编辑']") 
		
		let height = document.querySelector(id).offsetTop;
		//装有源码编辑功能的容器高度
		let forumSource_container_height = document.querySelector(".forumSource-container").offsetHeight;

		//编辑器高度
		//let editorHeight = editor.getWrapperElement().offsetHeight;
		position.scrollTop = height +forumSource_container_height + 160;
		
	}
	if(document.querySelector(".editLayoutCodeModule")){//布局 --> 布局代码编辑
		let height = document.querySelector(id).offsetTop;
		//装有源码编辑功能的容器高度
		let forumSource_container_height = document.querySelector(".forumSource-container").offsetHeight;

		document.querySelector(".el-main").scrollTop = height +forumSource_container_height + 140;
		
	}
	
}
