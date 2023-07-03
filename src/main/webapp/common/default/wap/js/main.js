	/** Vue组件 **/
/**
 * zepto ajax全局设置
 * ajax 的回调函数只能有一个, 如果你在业务逻辑里 $.ajax({}) 里面写了 error 的回调函数, 则会 覆盖掉 全局设置的 error 回调函数
 * 想要再调用全局的 error 回调, 需要手动调用
 * $.ajaxSettings.beforeSend(XMLHttpRequest);
 * 
 * console.log();
 **/

"use strict";

$.ajaxSettings = $.extend($.ajaxSettings, {
	beforeSend : function beforeSend(XMLHttpRequest) {
		//发送请求前
		Vue.$indicator.open({
			spinnerType : 'fading-circle'
		}); //显示旋转进度条
	//	XMLHttpRequest.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
		
		
	},
	complete : function complete(XMLHttpRequest, textStatus) {
		if(XMLHttpRequest.status == 508){
			//设置登录用户
			store.commit('setSystemUserId', '');
			store.commit('setSystemUserName', '');
		}

		//关闭网站提示参数
		if(XMLHttpRequest.status == 503){
			//弹出提示内容
			Vue.$messagebox('系统维护', XMLHttpRequest.responseText);
		}
		//关闭网站提示参数
		if(XMLHttpRequest.status == 403){
			//弹出提示内容
			Vue.$messagebox('权限不足', '您没有当前功能的操作权限');
		}
		//XMLHttpRequest.getAllResponseHeaders().indexOf("jumppath") >= 0解决跨域上传文件时出现错误 Refused to get unsafe header "jumpPath" 注意参数小写
		//请求完成后回调函数 (请求成功或失败时均调用)
		if (XMLHttpRequest.getAllResponseHeaders().toLowerCase().indexOf("jumppath") >= 0 && XMLHttpRequest.getResponseHeader("jumpPath") != null && XMLHttpRequest.getResponseHeader("jumpPath") != "") {
			//session登陆超时登陆页面响应http头
			//收到未登录标记，执行登录页面跳转
			router.push({
				path : "/" + XMLHttpRequest.getResponseHeader("jumpPath")
			});
		}
		
		setTimeout(function () {
			Vue.$indicator.close(); //关闭旋转进度条
		}, 20);
		
	}
});
Vue.component('v-select', VueSelect.VueSelect);

//Vue.component('vue-cropper', window['vue-cropper'].default);
//图片预览组件
Vue.use(VueViewer.default);

//延迟加载(因为mint-ui自带的版本不支持v-lazy-container属性，所以重新引入)
Vue.use(VueLazyload);

//气泡提示
Vue.use(VTooltip);

//定时查询消息
Vue.prototype.unreadMessageCount = function (){
	if(store.state.user.userId != ""){//如果在登录状态
		$.ajax({
			type : "GET",
			cache : false,
			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
			url : "user/control/unreadMessageCount",
			success : function success(result) {
				if (result != "") {
					var unreadMessage = $.parseJSON(result);
					var privateMessageCount = parseInt(unreadMessage.privateMessageCount);
		        	var systemNotifyCount = parseInt(unreadMessage.systemNotifyCount);
		        	var remindCount = parseInt(unreadMessage.remindCount);
		        	if(privateMessageCount >0){
		        		//设置私信状态
						store.commit('setPrivateMessage_badge', true);
		        	}else{
		        		//设置私信状态
						store.commit('setPrivateMessage_badge', false);
		        	}
		        	if(systemNotifyCount >0){
		        		//设置系统通知状态
		        		store.commit('setSystemNotify_badge', true);
		        	}else{
		        		//设置系统通知状态
		        		store.commit('setSystemNotify_badge', false);
		        	}
		        	if(remindCount >0){
		        		//设置提醒状态
		        		store.commit('setRemind_badge', true);
		        	}else{
		        		//设置提醒状态
		        		store.commit('setRemind_badge', false);
		        	}
		        	
		        	if((privateMessageCount + systemNotifyCount + remindCount)>0){
		        		//所有消息状态
		        		store.commit('setAllMessage_badge', true);
		        	}else{
		        		//所有消息状态
		        		store.commit('setAllMessage_badge', false);
		        	}
		        	
				}
			},
			beforeSend : function beforeSend(XMLHttpRequest) {
				//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				
			},
			complete : function complete(XMLHttpRequest, textStatus) {
				//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
			}
		});
	}
	
	
}




/**------------------------------------------- 页面组件 ------------------------------------------------**/










//首页组件
var index_component = Vue.extend({
	name: 'index',//组件名称，keep-alive缓存需要本参数
	template : '#index-template',
	data : function data() {
		return {
			popup_allTag :false,
			
			topicList : [], //话题列表
			currentpage : 1, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
			tagId :'',//标签Id
			tagName :'',//标签名称
			//popup_topic :false,//发表话题弹出层
			show_topic:true,//显示话题页
			show_editor:false,//显示话题富文本编辑器
			popup_tag : false, //'标签选择'弹出层
			topicTitle:'',//发表话题标题
			topicTagId:'',//发表话题标签Id
			topicTagName:'',//发表话题标签名称
			topicContent:'',//发表话题内容
			showCaptcha : false, //发表话题是否显示验证码
			imgUrl : '', //发表话题验证码图片
			captchaKey : '', //发表话题验证码key
			captchaValue : '', //发表话题验证码value
			error : {
				topicTitle : '',
				topicTagId : '',
				topicContent: '',
				captchaValue : '',
				topic: '',
				
				totalAmount: '',
				singleAmount: '',
				giveQuantity: '',
				redEnvelopeLimit: ''
			},
			
			
			pictureOptions: {//放大图片配置
		        url: 'data-src',//定义从何处获取原始图像URL进行查看
		        navbar:true, //导航栏
		        title:false, //标题
		        toolbar:false, //工具栏
		        loop:false, //是否启用循环查看
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
			
			tagList:'',//标签集合
			
			//标签数据
			tagSlots : [
				{
					flex : 1,
					values : [],
					className : 'slot1',
					textAlign : 'center'
				}
			],
			topicEditor:'',//富文本编辑器
			
			
			videoPlayerBind: [],//视频播放器绑定Id集合
			videoPlayerList: [],//视频播放器集合
			
			placeholder_DPlayer:'',//占位播放器
			lastPlayerId:'',//最后运行的播放Id

			showRedEnvelope:false,//显示发红包
			
			deposit:'',//用户共有预存款
			giveRedEnvelopeAmountMin:'',//发红包金额下限
			giveRedEnvelopeAmountMax:'',//发红包金额上限 空为无限制 0则不允许发红包
			
			redEnvelope_layer:false,//显示/隐藏红包层
			giveRedEnvelope_type:'', //发红包类型
		    giveRedEnvelope_totalAmount:'',//红包总金额
		    giveRedEnvelope_singleAmount:'',//红包单个金额
		    giveRedEnvelope_giveQuantity:'',//红包数量
		    giveRedEnvelope_totalAmountView:'0'//红包合计总金额
		    
		};
	},
	
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['index']);

	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
		
		for(var i =0; i<this.videoPlayerList.length; i++){
			var videoPlayer = this.videoPlayerList[i];
			videoPlayer.destroy();//销毁播放器
			
		}
		if(this.placeholder_DPlayer != ""){
			this.placeholder_DPlayer.destroy();//销毁播放器
		}
		
	},
	
	
	beforeMount: function beforeMount() {
		//监听vue-lazyload插件 已加载状态
	    this.$Lazyload.$on('loaded', function (_ref, formCache) {
	    	var bindType = _ref.bindType,
	        	el = _ref.el,
	        	naturalHeight = _ref.naturalHeight,
	        	naturalWidth = _ref.naturalWidth,
	        	$parent = _ref.$parent,
	        	src = _ref.src,
	        	loading = _ref.loading,
	        	error = _ref.error;
	    	//  console.log(el, src);
	    	//将延迟加载的img标签由空白图片改为缩略图
	    	//子节点
	    	var nodeList = el.childNodes;

	    	for (var i = 0; i < nodeList.length; i++) {
		        var childNode = nodeList[i]; //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	
		        if (childNode.nodeType == 1) {
			        //处理子img标签
			        if (childNode.nodeName.toLowerCase() == "img") {
			        	childNode.setAttribute("src", src); //标记不显示放大图
			        }
		        }
	    	}
	    });
	    /**
	    //设置mint-ui自带的vue-lazyload插件参数
	    this.$Lazyload.config({ loading: 'your-loading-spin.svg' });
	    **/
	},
	mounted : function mounted(){
		//挂载完成后，判断浏览器是否支持popstate
		if (window.history && window.history.pushState) {//监听浏览器前进后退按钮
			
			window.addEventListener('popstate', this.goBack, false);
		}
	},
	destroyed : function destroyed() {//离开当前页面
		//页面销毁时，取消监听
		window.removeEventListener('popstate', this.goBack, false);
	},
	deactivated: function() {//keep-alive组件停用时调用 (keepAlive缓存机制才会触发的钩子函数)
		this.showTopicList(false);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/thread') {//前往话题内容页的时候需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['index']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
	
		//显示话题列表
		showTopicList : function(back){
		    this.show_topic = true;//显示话题页
		    this.show_editor = false;//隐藏话题富文本编辑器
		    //判断浏览器是否支持popstate
			if (window.history && window.history.pushState && back) {
				if(back){
					//后退
					history.go(-1);
				}
			}
		},
		
		//页面前进后退
		goBack : function(){
		    if(this.show_topic == false){
		    	this.showTopicList(false);
		    }
		},
		
		//创建占位播放器(有部分浏览器会对video标签进行劫持，使播放器显示在最顶层,上下滚动时会破坏界面结构)
		createPlaceholderPlayer : function(){
			var _self = this;
			if(_self.placeholder_DPlayer == ""){
				var dp_placeholder = new DPlayer({
	    			container: _self.$refs.placeholderVideo,//播放器容器元素
	    			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	    			
	    			video: {
	    			    url: _self.$store.state.baseURL+_self.$store.state.commonPath+'js/DPlayer/placeholderVideo.mp4',//占位用，1帧的mp4视频。
	    			}
	    		});
				
				_self.placeholder_DPlayer = dp_placeholder;
				
			}
			
		},
		//占位播放器播放
		playPlaceholderPlayer : function(playerId){
			var _self = this;
			if(_self.placeholder_DPlayer != ""){
				if(_self.lastPlayerId == playerId){
					_self.placeholder_DPlayer.play();
				}
			}
		},
		
		
		
		
		//创建视频播放器
		createVideoPlayer : function(url,cover,thumbnail,topicId,index) {
			var _self = this;
			
			//创建占位播放器
			_self.createPlaceholderPlayer();
			
			
			
			if(_self.videoPlayerBind.contains(topicId+'_'+index)){
				return;
			}
			var player = _self.$refs['player_'+topicId+'_'+index][0];
			
			player.setAttribute('id','player_'+topicId+'_'+index);
			
			if(url == ""){
				var dom = document.createElement('div');
				dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
				player.appendChild(dom);
			}else{
				
				
				_self.videoPlayerBind.push(topicId+'_'+index);//视频播放器绑定Id
				
				
				if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
					var dp = new DPlayer({
		    			container: player,//播放器容器元素
		    			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
		    			
		    			video: {
		    			    url: url,
		    			    type: 'hls',
		    			    pic: cover,//视频封面
		    			    thumbnails: thumbnail//视频预览图
		    			}
		    		});
					dp.play();//播放视频
					
					dp.on('play', function () {//播放事件
						_self.lastPlayerId = dp.container.getAttribute('id');
        	        });
					dp.on('pause', function () {//暂停事件
        				_self.playPlaceholderPlayer(dp.container.getAttribute('id'));
        	        });
          			
          		}else{
          			var dp = new DPlayer({
          				container: player,//播放器容器元素
              			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
              			
              			video: {
              			    url: url
              			}
              		});
          			dp.play();//播放视频
          			
          			dp.on('play', function () {//播放事件
						_self.lastPlayerId = dp.container.getAttribute('id');
        	        });
					dp.on('pause', function () {//暂停事件
        				_self.playPlaceholderPlayer(dp.container.getAttribute('id'));
        	        });
          		}
				
				_self.videoPlayerList.push(dp);//视频播放器
				
			}

		},

		
		//查询话题列表
		queryTopicList : function() {
			var _self = this;
			var data = "";
			if(_self.tagId != null && _self.tagId != ""){
				data += "tagId=" + _self.tagId;
			}
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryTopicList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_topicList = pageView.records;
						if (new_topicList != null && new_topicList.length > 0) {
							_self.topicList = new_topicList;
							
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
						
						//生成首字符头像
						_self.$nextTick(function() {
							if (_self.topicList != null && _self.topicList.length > 0) {
								for(var i=0;i<_self.topicList.length; i++){
									var topic = _self.topicList[i];
									if(topic.avatarName == null || topic.avatarName == ''){
										var char = (topic.nickname != null && topic.nickname !="") ? topic.nickname : topic.account;
										//元素的实际宽度
										var width= _self.$refs['avatarData_'+topic.id][0].offsetWidth;
										_self.$refs['avatarData_'+topic.id][0].src = letterAvatar(char, width);	
									}
								}
							}
						});
						
						
					}
				}
			});
			
		},
		//查询标签
		queryTag : function() {
			var _self = this;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "allTag",
				success : function success(result) {
					if (result != "") {
						var tagList = $.parseJSON(result);
						if (tagList != null && tagList.length > 0) {
							var tagNameList = new Array(); //标签名称集合
							for (var j = 0; j < tagList.length; j++) {
								tagNameList.push(tagList[j].name);
								if(_self.tagId == tagList[j].id){
									_self.tagName = tagList[j].name;
									break;
								}
							}
							_self.tagList = tagList;
							//滚动
							_self.$nextTick(function() {
								if (!_self.scroll) {
									_self.initScroll();
						        }else{
						        	// 复用Bscroll
						        	_self.scroll.refresh();
						        }
							});
							
							if(_self.tagSlots[0].values == null || _self.tagSlots[0].values.length == 0){//第一次打开时设置值
								_self.tagSlots[0].values = tagList;
							}
							
						}
						
						
						
					}
				}
			});
		},
		
		
		//显示/隐藏红包表单层
		redEnvelopeFormLayer : function() {
			var _self = this;
			
			if(_self.redEnvelope_layer == false){
				_self.redEnvelope_layer = true;
				
				//如果没选中值，则默认选第一个
				if(_self.giveRedEnvelope_type == ""){
					_self.giveRedEnvelope_type = 20;
				}
				
			}else{
				_self.redEnvelope_layer = false;
			}
		},
		
		//选择红包类型
		selectRedEnvelopeType : function() {
			var _self = this;
			
			if(_self.giveRedEnvelope_type == 20){//随机
				_self.totalAmountCalculate();
				
			}else if(_self.giveRedEnvelope_type == 30){//固定
				_self.singleAmountCalculate();
				
			}
			
		},
		//总金额计算
		totalAmountCalculate : function() {
			var _self = this;
			
			if(_self.giveRedEnvelope_type == 20){//随机
				var exp = /(^[1-9]([0-9]+)?(\.[0-9]{1,10})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
				if(exp.test(_self.giveRedEnvelope_totalAmount.trim())) {
					var amount = calc_add(_self.giveRedEnvelope_totalAmount.trim(), 0, 2);
					_self.giveRedEnvelope_totalAmountView = amount;
				}else{
					_self.giveRedEnvelope_totalAmountView = 0.00;
				}
				
			}
		},
		//单个红包金额计算
		singleAmountCalculate : function() {
			var _self = this;
			
			if(_self.giveRedEnvelope_type == 30){//固定
				
				//金额 只允许正数  正则判断最多10位小数
				var exp_singleAmount = /(^[1-9]([0-9]+)?(\.[0-9]{1,10})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
				//数量 正整数
				var exp_giveQuantity = /^\+?[1-9][0-9]*$/;
				if(exp_singleAmount.test(_self.giveRedEnvelope_singleAmount.trim()) && exp_giveQuantity.test(_self.giveRedEnvelope_giveQuantity.trim())) {
					var amount = calc_multiply(_self.giveRedEnvelope_singleAmount.trim(), _self.giveRedEnvelope_giveQuantity.trim(), 2);
					_self.giveRedEnvelope_totalAmountView = amount;
				}else{
					_self.giveRedEnvelope_totalAmountView = 0.00;
				}
			}
			
		},
		
		//发表话题界面
		addTopicUI : function() {
			var _self = this;
			
			if(this.$store.state.user.userId == ""){//如果未登录
                this.$router.push({
                    path : '/login'
                });
                return;
            }
			
			_self.show_topic = false;//隐藏话题页
			_self.show_editor = true;//显示话题富文本编辑器

			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			//查询添加话题页
			_self.queryAddTopic(function (returnValue){
				//等级
				var userGradeList = null;
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//话题编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "media"){//视频
								editorIconList.push("video");
							}else if(_availableTag == "embedVideo"){//嵌入视频
								editorIconList.push("embedVideo");
							}else if(_availableTag == "uploadVideo"){//上传视频
								editorIconList.push("uploadVideo");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}else if(_availableTag == "hidePassword"){//输入密码可见
								editorIconList.push("hidePassword");
							}else if(_availableTag == "hideComment"){//评论话题可见
								editorIconList.push("hideComment");
							}else if(_availableTag == "hideGrade"){//达到等级可见
								editorIconList.push("hideGrade");
							}else if(_availableTag == "hidePoint"){//积分购买可见
								editorIconList.push("hidePoint");
							}else if(_availableTag == "hideAmount"){//余额购买可见
								editorIconList.push("hideAmount");
							}
						}
						
						
						
					}else if(key == "userGradeList"){//会员等级
						userGradeList = $.parseJSON(returnValue[key]);
					}
				}
				
				for(var i=0; i< editorIconList.length; i++){
					var editorIcon = editorIconList[i];
					if(editorIcon == "hidePassword" || editorIcon == "hideComment" ||
							editorIcon == "hideGrade" || editorIcon == "hidePoint" || editorIcon == "hideAmount"){
						editorIconList.splice(i, 0, 'hide');//在指定索引处插入元素
						break;
					}
				}
				_self.$refs.topicContentEditorToolbar.innerHTML = "";
				_self.$refs.topicContentEditorText.innerHTML = "";
				//创建编辑器
				_self.topicEditor = createEditor(_self.$refs.topicContentEditorToolbar,_self.$refs.topicContentEditorText,_self.$store.state.commonPath,editorIconList,userGradeList,'user/control/topic/upload',_self,"topicContent");
		
			});
			
			
			
			
	
		},
		//查询添加话题页
		queryAddTopic : function(callback) {
			var _self = this;

			//清空表单
			_self.topicTitle = ''; //发表话题标题
			_self.topicTagId = ''; //发表话题标签Id
			_self.topicTagName= ''; //发表话题标签名称
			_self.topicContent = ''; //发表话题内容
			_self.showCaptcha = false, //发表话题是否显示验证码
			_self.imgUrl = ''; //发表话题验证码图片
			_self.captchaKey = ''; //发表话题验证码key
			_self.captchaValue = ''; //发表话题验证码value
			
			_self.giveRedEnvelope_type = ''; //发红包类型
			_self.giveRedEnvelope_totalAmount = '';//红包总金额
			_self.giveRedEnvelope_singleAmount = '';//红包单个金额
			_self.giveRedEnvelope_giveQuantity = '';//红包数量
			_self.giveRedEnvelope_totalAmountView = 0;//红包合计总金额
			
			
			

			//清空标签数据
			_self.tagSlots[0].values = [];
			_self.tagSlots[0].defaultIndex = 0;
			
			
			//清空所有错误
			_self.error.topicTitle = "";
			_self.error.topicTagId = "";
			_self.error.topicContent = "";
			_self.error.captchaValue = "";
			_self.error.topic = "";
			
			_self.totalAmount = "";
			_self.singleAmount = "";
			_self.giveQuantity = "";
			_self.redEnvelopeLimit = "";
			
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddTopic",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowTopic") {
									if(returnValue[key] == false){
										_self.showTopicList(true);//显示话题列表
										_self.$toast({
											message : "发表话题功能未开放",
											duration : 3000,
										});
									}
								}else if (key == "deposit") {	
									_self.deposit = returnValue[key];
								}else if (key == "giveRedEnvelopeAmountMin") {	
									_self.giveRedEnvelopeAmountMin = returnValue[key];
								}else if (key == "giveRedEnvelopeAmountMax") {	
									var giveRedEnvelopeAmountMax = returnValue[key];
									_self.giveRedEnvelopeAmountMax = giveRedEnvelopeAmountMax;
									if(giveRedEnvelopeAmountMax == null || (giveRedEnvelopeAmountMax != null && giveRedEnvelopeAmountMax >0)){
										//显示发红包
										_self.showRedEnvelope = true;
									}
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
								
							}
							//回调
							callback(returnValue);
						}
						
					//	//滚动
					//	_self.$nextTick(function() {
					//		_self.initScroll(_self.$refs.addTopicFormScroll);
					//	});
					}
				}
			});
		},
		//添加话题
		addTopic : function(event) {

			var _self = this;
			//清空所有错误
			_self.error.topicTitle = "";
			_self.error.topicTagId = "";
			_self.error.topicContent = "";
			_self.error.topic = "";
			_self.error.captchaValue = "";
			
			_self.error.totalAmount = "";
			_self.error.singleAmount = "";
			_self.error.giveQuantity = "";
			_self.error.redEnvelopeLimit = "";
			
		//	_self.topicContent = "<div style='text-align: center; height: expression(alert('test xss'));'>fdfd</div>";//
		//	_self.topicContent = "<div style='text-align: center; height: expre\ssion(alert('test xss'));'>fdfd</div>";//
			
			var parameter = "&tagId=" + _self.topicTagId; //提交参数
			if (_self.topicTitle != null && _self.topicTitle != "") {
				parameter += "&title=" + encodeURIComponent(_self.topicTitle);
			}
			//if (_self.topicContent != null && _self.topicContent != "") {
			//	parameter += "&content=" + encodeURIComponent(_self.topicContent);
			//}
			
			parameter += "&content=" + encodeURIComponent(_self.topicEditor.txt.html());
			
			//如果显示红包表单
			if(_self.redEnvelope_layer){
				parameter += "&type=" + _self.giveRedEnvelope_type;//发红包类型
				
				parameter += "&totalAmount=" + encodeURIComponent(_self.giveRedEnvelope_totalAmount);//红包总金额
				parameter += "&singleAmount=" + encodeURIComponent(_self.giveRedEnvelope_singleAmount);//单个红包金额
				parameter += "&giveQuantity=" + encodeURIComponent(_self.giveRedEnvelope_giveQuantity);//红包数量

			}
			
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/topic/add",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showTopicList(true);//显示话题列表

							//清空分页数据
							_self.topicList = []; //话题列表
							_self.topicContent = '';//清空话题内容
							_self.topicEditor.txt.clear();//清空编辑器内容
							
							
							//查询话题列表
							_self.queryTopicList();

						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "title") {
											_self.error.topicTitle = value_error[error];
										}else if (error == "tagId") {
											_self.error.topicTagId = value_error[error];
										}else if (error == "content") {
											_self.error.topicContent = value_error[error];
										}else if (error == "topic") {
											_self.error.topic = value_error[error];
										}else if (error == "totalAmount") {
											_self.error.totalAmount = value_error[error];
										}else if (error == "singleAmount") {
											_self.error.singleAmount = value_error[error];
										}else if (error == "giveQuantity") {
											_self.error.giveQuantity = value_error[error];
										}else if (error == "redEnvelopeLimit") {
											_self.error.redEnvelopeLimit = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
							

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//显示标签选择
		displayTag : function(event) {
			this.popup_tag = true;
			
			//查询标签
			this.queryTag();
			
			
		},
		//标签选择改变时触发
		onTagChange : function(picker, values) {
			if(values[0] != null){
				this.topicTagId = values[0].id;//发表话题标签Id
				this.topicTagName = values[0].name;//发表话题标签名称
			}
		},
		
		//更换验证码
		replaceCaptcha : function(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},
		//验证验证码
		validation_captchaValue : function(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		
		//显示所有标签选择
		displayAllTag : function(event) {
			//显示
			this.popup_allTag = true;
			
			//查询标签列表
			this.queryTag();
		},
		
		//初始化
		initialization : function() {
			
			var tagId = getUrlParam("tagId");//当前标签
			if(tagId != null){
				this.tagId = tagId;
				//查询标签列表
				this.queryTag();
				
				
			}
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询话题列表
			this.queryTopicList();
			
			
		},
		
		
		//初始化BScroll滚动插件
		initScroll : function initScroll() {
			this.scroll = new BScroll(this.$refs.tagScroll, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				HWCompositing : true, //是否启用硬件加速
			});
		}
	}
});
				


//话题内容组件
var thread_component = Vue.extend({
	template : '#thread-template',
	data : function data() {
		return {
			topicId :'',//话题Id
			commentId:'',//评论Id
			replyId:'',//回复Id
			topic : '', //话题
			commentList : '', //评论列表
			currentpage : 1, //当前评论页码
			totalpage : 1, //评论总页数
			on: '',//上一页
			next: '',//下一页
			quoteData : [],//引用数据 map格式 key:评论Id value:引用html数据
			replyExpandOrShrink : [], //回复展开/收缩 map格式 key:评论Id value:是否展开
			
			viewNumber:2,//回复伸缩 展示数量
			
			show_topic:true,//话题内容显示/隐藏
			show_commentEditor:false,//添加评论富文本编辑器显示/隐藏
			show_quoteEditor:false,//引用评论富文本编辑器显示/隐藏
			show_editCommentEditor:false,//修改评论富文本编辑器显示/隐藏
			show_editReplyEditor:false,//修改回复富文本编辑器显示/隐藏
			
			popup_reply : false, //发表回复弹出层
			popup_editReply : false, //修改回复弹出层
			popup_addReplyFriend : false, //发表回复对方弹出层
			commentContent:'',//发表评论内容
			quoteCommentId:'',//引用评论Id
			quoteCommentContent : '',//引用评论内容
			quoteContent:'',//发表引用评论内容
			replyCommentId :'',//回复评论Id
			replyContent:'',//发表回复内容
			
			
			editCommentId:'',//修改评论Id
			editCommentContent : '',//修改评论内容
			editReplyId :'',//修改回复Id
			editReplyContent:'',//修改回复内容
			
			friendUser:'',//发表回复对方用户
			addReplyFriendCommentId:'',//发表回复对方评论Id
			addReplyFriendId :'',//发表回复对方Id
			addReplyFriendContent:'',//发表回复对方内容
			
			showCaptcha : false, //发表评论/引用评论/发表回复是否显示验证码
			imgUrl : '', //验证码图片
			captchaKey : '', //验证码key
			captchaValue : '', //验证码value
			error : {
				commentContent : '',
				quoteContent : '',
				replyContent: '',
				captchaValue : '',
				comment: '',
				quote: '',
				reply: '',
				
				editComment: '',
				editReply: '',
				editCommentContent : '',
				editReplyContent: '',
				
				addReplyFriendContent:'',//发表回复对方内容
					
				reportTypeId:'',//举报分类Id
			    reason:'',//理由
			    imageFile:'',
			    report:''
			},
			commentEditor : '',//评论富文本编辑器
			quoteEditor: '',//引用评论富文本编辑器
			
			editCommentEditor : '',//修改评论富文本编辑器

			alreadyCollected :false,//用户是否已经收藏话题
			favoriteCount : 0,//话题用户收藏总数
			alreadyLiked:false,//用户是否已经点赞该话题
			likeCount : 0,//话题用户点赞总数
			hidePasswordIndex:0,//隐藏标签密码框索引
			
			pictureOptions: {//放大图片配置
		        url: 'src',//定义从何处获取原始图像URL进行查看
		        navbar : true, //导航栏
		        title : false, //标题
		        toolbar : false, //工具栏
		        loop:false, //是否启用循环查看
		        filter : function(image) {
		        	return image.dataset.enable != 'false';//将不允许放大的表情图片过滤
		        },
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
		    
		    playerIdList: [],//视频播放Id列表
		    playerObjectList: [],//视频播放对象集合
		    playerNodeList: [],//视频节点对象集合
		    
		    following:false,//是否已经关注该用户
		    
		    giveRedEnvelope: '',//发红包
		    receiveRedEnvelopeList:[],//领取红包用户集合
		    selectedReceiveRedEnvelopeId:'',//选中领取红包用户
		    receiveRedEnvelopeCurrentPage:0,//领取红包用户当前页
		    receiveRedEnvelope_more:false,//是否显示更多领取红包用户
		    
		    topicOperating: false,//话题是否显示操作页面
		    topicOperatingData: '',//话题弹出操作菜单
		    commentOperating: false,//评论是否显示操作页面
		    commentOperatingData: '',//评论弹出操作菜单
		    replyOperating: false,//回复是否显示操作页面
		    replyOperatingData: '',//回复弹出操作菜单
		    
		    show_report:false,//添加举报弹出层 显示/隐藏
		    reportTypeList:[],
		    reportTypeId:'',//举报分类Id
		    reason:'',//理由
		    image:[],
		    fileList:[],//上传表单图片列表
		    reportMaxImageUpload:0,//图片允许最大上传数量
		    parameterId:'',//举报参数Id
			module:'',//举报模块
			show_giveReason:false,//是否显示说明理由表单
			
			replyElementNodes:[],//回复列表项Element节点集合
		};
	},
	
	created : function created() {
		//初始化
		this.initialization();
		
	},
	mounted : function mounted(){
		//挂载完成后，判断浏览器是否支持popstate
		if (window.history && window.history.pushState) {//监听浏览器前进后退按钮
			
			window.addEventListener('popstate', this.goBack, false);
		}
	},
	destroyed : function destroyed() {//离开当前页面
		this.showTopic(false);
		//页面销毁时，取消监听
		window.removeEventListener('popstate', this.goBack, false);
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
		
		for(var i =0; i<this.playerObjectList.length; i++){
			var playerObject = this.playerObjectList[i];
			playerObject.destroy();//销毁播放器
			
		}
	},

	computed: {
		
		//动态解析评论引用模板数据
		quoteDataComponent: function quoteDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					props: this.$options.props, // re-use current props definitions
					
				};
			};	
		},
		//动态解析评论模板数据
		commentDataComponent: function commentDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					props: this.$options.props, // re-use current props definitions
					
				};
			};	
		},
		//动态解析隐藏标签模板数据
		hideTagComponent: function hideTagComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					
					data : function data() {
						return {
							hide_passwordList :[],//话题隐藏密码
							escapeChar:'{{'//富文本转义字符
						};
					},
					mounted :function () {
						this.resumePlayerNodeData();
					},
					props: this.$options.props, // re-use current props definitions
					methods: {
				        //模板中将点击事件绑定到本函数，作用域只限在这个子组件中
				        topicUnhide : function(hideType,hidePasswordIndex){
				        	var _self = this;
				        	
				        	var parameter = "&topicId=" + getUrlParam("topicId");
				        	parameter += "&hideType="+hideType;//获取URL参数
				        	
				        	if(hideType == 10){
				        		var hide_password = _self.hide_passwordList[hidePasswordIndex];
					        	if(hide_password != undefined && hide_password != ""){
					        		parameter += "&password="+encodeURIComponent(hide_password);//获取URL参数
					        	}else{
					        		_self.$toast({
										message : "密码不能为空",
										duration : 3000,
										className : "mint-ui-toast",
									});
					        		return;
					        	}
				        	}
				        	
				        	_self.$messagebox.confirm('确定解锁?').then(function (action) {
				        		//令牌
								parameter += "&token=" + _self.$store.state.token;
								$.ajax({
									type : "POST",
									cache : false,
									async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
									url : "user/control/topic/unhide",
									data : parameter,
									success : function success(result) {
										if (result != "") {

											var returnValue = $.parseJSON(result);

											var value_success = "";
											var value_error = null;

											for (var key in returnValue) {
												if (key == "success") {
													value_success = returnValue[key];
												} else if (key == "error") {
													value_error = returnValue[key];
												}
											}

											//加入成功
											if (value_success == "true") {
												_self.$toast({
													message : "话题取消隐藏成功，3秒后自动刷新页面",
													duration : 3000,
													className : "mint-ui-toast",
												});

												setTimeout(function() {
													//查询话题
													_self.$parent.queryTopic();//调用父组件方法
												}, 3000);
												
											} else {
												//显示错误
												if (value_error != null) {


													var htmlContent = "";
													var count = 0;
													for (var errorKey in value_error) {
														var errorValue = value_error[errorKey];
														count++;
														htmlContent += count + ". " + errorValue + "<br>";
													}
													_self.$messagebox('提示', htmlContent);

												}
											}
										}
									}
								});
							},function (action) {//取消回调
								
							}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
							    console.log(err);
							});

				        }, 
				        //恢复播放器节点数据(vue组件切换时会自动刷新数据，视频播放器框在组件生成数据内容之后插入，组件刷新数据时播放器框会消失，组件刷新后需要用之前的节点数据恢复)
				        resumePlayerNodeData : function(){
				        	var _self = this;
				        	_self.$nextTick(function() {
					        	if(_self.$parent.playerObjectList.length >0){
					        		for(var i=0; i< _self.$parent.playerNodeList.length; i++){
					        			var playerNode = _self.$parent.playerNodeList[i];
					        			var playerId = playerNode.getAttribute("id");
					        			var node = document.getElementById(playerId);
					        			if(node != null){
					        				node.parentNode.replaceChild(playerNode,node);
					        			}
					        			
					        		}
					        	}
				        	});
				        }
				    }
				};
			};	
		},
	},
	
	methods : {
		//显示话题内容界面
		showTopic : function(back){
		    this.show_topic = true;//显示话题页
		    this.show_commentEditor = false;//隐藏话题富文本编辑器
		    this.show_quoteEditor = false;//隐藏引用评论富文本编辑器
		    this.show_editCommentEditor = false;//隐藏修改评论富文本编辑器
		    this.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
		    this.show_report = false;//隐藏举报
		    //判断浏览器是否支持popstate
			if (window.history && window.history.pushState && back) {
				//后退
				history.go(-1);
			}
		},
		//页面前进后退
		goBack : function(){
		    if(this.show_topic == false){
		    	this.showTopic(false);
		    }
		},
		
		//查询话题
		queryTopic : function(event) {
			var _self = this;
			var _topic =new Object();//请求数据前显示
			_topic.content = "";
			_self.topic = _topic;
			
			var data = "topicId=" + _self.topicId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryTopicContent",
				data : data,
				success : function success(result) {
					if (result != "") {
						var topic = $.parseJSON(result);
						if (topic != null) {
							//清空播放器
							_self.clearVideoPlayer();
							
							
							
							//处理隐藏标签
							var contentNode = document.createElement("div");
							contentNode.innerHTML = topic.content;
							
							_self.hidePasswordIndex = 0;
							_self.bindNode(contentNode);
							topic.content = escapeVueHtml(contentNode.innerHTML);
							
						
							_self.topic = topic;
							
							
							
							_self.$nextTick(function() {
								setTimeout(function() {
									_self.renderVideoPlayer();//渲染视频播放器
								}, 30);
								
								
							});
							
							//生成首字符头像
							_self.$nextTick(function() {
								if(_self.topic.avatarName == null || _self.topic.avatarName == ''){
									var char = (_self.topic.nickname != null && _self.topic.nickname !="") ? _self.topic.nickname : _self.topic.account;
									//元素的实际宽度
									var width= _self.$refs.topicUserAvatar.offsetWidth;
									_self.$refs.topicUserAvatar.src = letterAvatar(char, width);	
									
									
								}
							});
							
							//查询是否已经关注该用户
							_self.queryFollowing();
							
							//查询发红包
							if(topic.giveRedEnvelopeId != null && topic.giveRedEnvelopeId !=''){
								_self.queryGiveRedEnvelope(topic.giveRedEnvelopeId);
								
							}
						}
					}
				}
			});
		},
		
		//查询发红包
		queryGiveRedEnvelope : function(giveRedEnvelopeId) {
			var _self = this;
			
			var data = "giveRedEnvelopeId=" + giveRedEnvelopeId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryGiveRedEnvelope",
				data : data,
				success : function success(result) {
					if (result != "") {
						var giveRedEnvelope = $.parseJSON(result);
						if (giveRedEnvelope != null) {
							_self.giveRedEnvelope  = giveRedEnvelope;
							
							//生成首字符头像
							_self.$nextTick(function() {
								if(_self.giveRedEnvelope.avatarName == null || _self.giveRedEnvelope.avatarName == ''){
									var char = (_self.giveRedEnvelope.nickname != null && _self.giveRedEnvelope.nickname !="") ? _self.giveRedEnvelope.nickname : _self.giveRedEnvelope.account;
									//元素的实际宽度
									var width= _self.$refs.giveRedEnvelopeUserAvatar.offsetWidth;
									_self.$refs.giveRedEnvelopeUserAvatar.src = letterAvatar(char, width);	
									
									
								}
							});
							
							//查询领取红包用户列表
							_self.queryReceiveRedEnvelopeUserList(1);
						}
					}
				}
			})
		},
		//查询领取红包用户列表(追加)
		queryReceiveRedEnvelopeUserList : function(page) {
			var _self = this;
			if(_self.giveRedEnvelope == null || _self.giveRedEnvelope == ''){
				return;
			}
			
			var data = "giveRedEnvelopeId=" + _self.giveRedEnvelope.id+"&page="+page; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryReceiveRedEnvelopeUser",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						if (pageView != null) {
							if(pageView.records != null && pageView.records.length >0){
								var receiveRedEnvelopeList = pageView.records;
								if (receiveRedEnvelopeList != null && receiveRedEnvelopeList.length > 0) {
									for(var i=0;i<receiveRedEnvelopeList.length; i++){
										_self.receiveRedEnvelopeList.push(receiveRedEnvelopeList[i]);
									}
								}
								
								
								
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (receiveRedEnvelopeList != null && receiveRedEnvelopeList.length > 0) {
										for(var i=0;i<receiveRedEnvelopeList.length; i++){
											var receiveRedEnvelope = receiveRedEnvelopeList[i];
											if(receiveRedEnvelope.receiveAvatarName == null || receiveRedEnvelope.receiveAvatarName == ''){
												var char = (receiveRedEnvelope.receiveNickname != null && receiveRedEnvelope.receiveNickname !="") ? receiveRedEnvelope.receiveNickname : receiveRedEnvelope.receiveAccount;
												//元素的实际宽度
												var width= _self.$refs['redEnvelopeAvatarData_'+receiveRedEnvelope.id][0].offsetWidth;
												_self.$refs['redEnvelopeAvatarData_'+receiveRedEnvelope.id][0].src = letterAvatar(char, width);
												
												
											}
											
										}
									}
								});
								//显示更多
								if(parseInt(pageView.totalrecord) != _self.receiveRedEnvelopeList.length){
									
									_self.receiveRedEnvelope_more = true;
								}else{
									_self.receiveRedEnvelope_more = false;
								}
								_self.receiveRedEnvelopeCurrentPage = pageView.currentpage;
								
							}
							
						}
					}
				}
			})
		
		},
		//选择领取红包用户
		selectReceiveRedEnvelopeUser : function(receiveRedEnvelopeId) {
			var _self = this;
			_self.selectedReceiveRedEnvelopeId = receiveRedEnvelopeId;
		},
		//抢红包
		grabRedEnvelope : function(giveRedEnvelopeId) {
			var _self = this;
			
			var parameter = "&giveRedEnvelopeId=" + giveRedEnvelopeId;

			//	alert(parameter);
			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/redEnvelope/addReceiveRedEnvelope",
				data : parameter,
				success : function success(result) {
					if (result != "") {

						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_receiveRedEnvelopeAmount = 0;
						
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "receiveRedEnvelopeAmount") {
								value_receiveRedEnvelopeAmount = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							}
						}

						//抢红包成功
						if (value_success == "true") {
							_self.$toast({
								message : "抢到 "+value_receiveRedEnvelopeAmount+" 元红包",
								duration : 5000,
								className : "mint-ui-toast",
							});

							setTimeout(function() {
								_self.receiveRedEnvelopeList.length = 0;//清空数组
								//查询领取红包用户列表
								_self.queryReceiveRedEnvelopeUserList(1);
							}, 1000);
							
						} else {
							//显示错误
							if (value_error != null) {


								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									if(errorKey == "systemInfo"){//如果弹出系统繁忙则不显示
										return;
									}
									
									
									var errorValue = value_error[errorKey];
									count++;
									htmlContent += count + ". " + errorValue + "<br>";
								}
								_self.$messagebox('提示', htmlContent);

							}
						}
					}
				}
			});
		},
		//清空播放器
		clearVideoPlayer : function() {
			var _self = this;
			
			for(var i=0; i< _self.playerObjectList.length; i++){
				var playerObject = _self.playerObjectList[i];
				
				playerObject.destroy();//销毁播放器
			}
			_self.playerObjectList.length = 0;//清空数组
			_self.playerIdList.length = 0;//清空数组
			_self.playerNodeList.length = 0;//清空数组
		},
		//渲染视频播放器
		renderVideoPlayer : function() {
			var _self = this;
			
			
			
			
			for(var i=0; i< _self.playerIdList.length; i++){
				var playerId = _self.playerIdList[i];
				var url = document.getElementById(playerId).getAttribute("url");
        		var cover = document.getElementById(playerId).getAttribute("cover");//封面
        		var thumbnail = document.getElementById(playerId).getAttribute("thumbnail");//缩略图
				
        		if(url == ""){//如果视频处理中
        			var dp = new DPlayer({
            			container: document.getElementById(playerId),//播放器容器元素
            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
            			
            			video: {
            			    
            			}
            		});
					var dom = document.createElement('div');
					dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
					document.getElementById(playerId).appendChild(dom);
				}else{
					if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
	        			var dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url,
	            			    type: 'hls',
	            			    pic: cover,//视频封面
	            			    thumbnails: thumbnail//视频预览图
	            			}
	            		});
	    				
	        		}else{
	        			var dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url
	            			}
	            		});
	        		}
					
				}
				_self.playerObjectList.push(dp);
			}
			
			
			//添加播放器节点数据
			if(_self.playerObjectList.length >0){
				
				for(var i=0; i< _self.playerIdList.length; i++){
			    	var playerId = _self.playerIdList[i];
			    	var node = document.getElementById(playerId);//节点对象
			    	_self.playerNodeList.push(node);
			    }
			}
			
		},
		
		//递归绑定节点参数
		bindNode : function(node) {
			//先找到子节点
	        var nodeList = node.childNodes;
	        for(var i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            var childNode = nodeList[i];
	            var random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	//处理隐藏内容
	            	if(childNode.nodeName.toLowerCase() == "hide" ){
	            		if(childNode.getAttribute("hide-type") == "10"){//输入密码可见
	            			var nodeHtml = "";
	    					nodeHtml += '<div class="hide-box">';
	    					nodeHtml += 	'<div class="background-image cms-lock"></div>';
	    					nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，输入密码可见</div>';
	    					nodeHtml += 	'<div class="input-box">';
	    					nodeHtml += 		'<input type="password" v-model.trim="hide_passwordList['+this.hidePasswordIndex+']" class="text" maxlength="30"  placeholder="密码" value="">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(10,'+this.hidePasswordIndex+');">';
	    					nodeHtml += 	'</div>';
	    					nodeHtml += '</div>';
	    					childNode.innerHTML = nodeHtml;
	    					
	    					this.hidePasswordIndex++;
	            		}else if(childNode.getAttribute("hide-type") == "20"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，评论话题可见</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "30"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，等级达到‘'+escapeHtml(childNode.getAttribute("description"))+'’可见</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "40"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，支付‘'+childNode.getAttribute("input-value")+'’积分可见</div>';
	            			nodeHtml += 	'<div class="submit-box">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(40);">';
	    					nodeHtml += 	'</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "50"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，支付 ￥<span class="highlight">'+childNode.getAttribute("input-value")+'</span> 元可见</div>';
	            			nodeHtml += 	'<div class="submit-box">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(50);">';
	    					nodeHtml += 	'</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}
	            	}
	            	//处理图片放大
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		var src = childNode.getAttribute("src");
	            		
	            		if(childNode.getAttribute("width") == null){//不是表情图片
	            			childNode.setAttribute("key",random+"_"+i);
	            		}else{
	            			childNode.setAttribute("data-enable","false");//标记不显示放大图
	            		}
	            		
	            		//延迟加载 表情图片也使用<img>标签，也执行延迟加载
            			childNode.setAttribute("src",this.$store.state.commonPath+'images/null.gif');
            			childNode.setAttribute("data-src",src);
	            	}
	            	
	            	//处理视频标签
	            	if(childNode.nodeName.toLowerCase() == "player" ){
	            		
	            		var id = "player_"+random+"_"+i;
	            		childNode.setAttribute("id",id);//设置Id
	            		this.playerIdList.push(id);	
	            	}
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		var pre_html = childNode.innerHTML;
	            		var class_val = childNode.className;
	            		var lan_class = "";
	            		
	        	        var class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(var k=0; k<class_arr.length; k++){
	        	        	var className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	        	        var nodeHtml = "";

            			//删除code节点
            			var preChildNodeList = childNode.childNodes;
            			for(var p = 0;p < preChildNodeList.length;p++){
            				var preChildNode = preChildNodeList[p];
            				if(preChildNode.nodeName.toLowerCase() == "code" ){
            					nodeHtml += preChildNode.innerHTML;
            					preChildNode.parentNode.removeChild(preChildNode);
                			}
            				
            			}
            			
            			var dom = document.createElement('code');
            			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				Prism.highlightElement(dom);
	            		
	            	}
	            	
	            	this.bindNode(childNode);
	            }
	        }
	    },
	    
	    
		
	    //查询是否已经关注该用户
  		queryFollowing : function() {
  			var _self = this;
  			if(_self.topic != ''){
  				var data = "userName=" + _self.topic.userName; //提交参数
  				$.ajax({
  					type : "GET",
  					cache : false,
  					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
  					url : "queryFollowing",
  					data : data,
  					success : function success(result) {
  						if (result != "") {
  							var following = $.parseJSON(result);
  							if (following != null) {
  								_self.following = following;
  							}
  						}
  					}
  				});
  				
  			}
  		},
	    //添加关注
  		addFollow : function(userName) {
  			var _self = this;
  			if(_self.following == false){
  				_self.$messagebox.confirm('确定关注?').then(function (action) {
  					var parameter = "&userName=" + userName;

  					//	alert(parameter);
  					//令牌
  					parameter += "&token=" + _self.$store.state.token;
  					$.ajax({
  						type : "POST",
  						cache : false,
  						async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
  						url : "user/control/follow/add",
  						data : parameter,
  						success : function success(result) {
  							if (result != "") {

  								var returnValue = $.parseJSON(result);

  								var value_success = "";
  								var value_error = null;

  								for (var key in returnValue) {
  									if (key == "success") {
  										value_success = returnValue[key];
  									} else if (key == "error") {
  										value_error = returnValue[key];
  									}
  								}

  								//加入成功
  								if (value_success == "true") {
  									_self.$toast({
  										message : "关注成功",
  										duration : 3000,
  										className : "mint-ui-toast",
  									});

  									setTimeout(function() {
  										//查询是否已经关注该用户
  										_self.queryFollowing();
  									}, 3000);
  									
  								} else {
  									//显示错误
  									if (value_error != null) {


  										var htmlContent = "";
  										var count = 0;
  										for (var errorKey in value_error) {
  											var errorValue = value_error[errorKey];
  											count++;
  											htmlContent += count + ". " + errorValue + "<br>";
  										}
  										_self.$messagebox('提示', htmlContent);

  									}
  								}
  							}
  						}
  					});
  				}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
  				    console.log(err);
  				});
  				
  			}
  		},
		
		//查询用户是否已经收藏话题
		queryAlreadyCollected : function() {
			var _self = this;
			var data = "topicId=" + _self.topicId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAlreadyCollected",
				data : data,
				success : function success(result) {
					if (result != "") {
						var alreadyCollected = $.parseJSON(result);
						if (alreadyCollected != null) {
							_self.alreadyCollected = alreadyCollected;
						}
					}
				}
			});
		},
		//查询话题用户收藏总数
		queryFavoriteCount : function() {
			var _self = this;
			var data = "topicId=" + _self.topicId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryFavoriteCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							_self.favoriteCount = count;
						}
					}
				}
			});
		},
		//加入收藏夹
		addFavorite : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定加入收藏夹?').then(function (action) {
				var parameter = "&topicId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/favorite/add",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "加入成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//刷新用户是否已经收藏话题
									_self.queryAlreadyCollected();
									
									//刷新话题用户收藏总数
									_self.queryFavoriteCount();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		//查询用户是否已经点赞该话题
		queryAlreadyLiked : function() {
			var _self = this;
			var data = "topicId=" + _self.topicId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAlreadyLiked",
				data : data,
				success : function success(result) {
					if (result != "") {
						var alreadyLiked = $.parseJSON(result);
						if (alreadyLiked != null) {
							_self.alreadyLiked = alreadyLiked;
						}
					}
				}
			});
		},
		//查询话题点赞总数
		queryLikeCount : function() {
			var _self = this;
			var data = "topicId=" + _self.topicId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryLikeCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							_self.likeCount = count;
						}
					}
				}
			});
		},
		//给话题点赞
		addLike : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定点赞?').then(function (action) {
				var parameter = "&topicId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/like/add",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "点赞成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//刷新用户是否已经点赞该话题
									_self.queryAlreadyLiked();
									//刷新话题点赞总数
									_self.queryLikeCount();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		//查询评论列表
		queryCommentList : function() {
			var _self = this;
			var data = "";
			if(_self.topicId != null && _self.topicId != ""){
				data += "topicId=" + _self.topicId;
			}
			if(_self.commentId != null && _self.commentId != ""){//提交评论Id时不能提交分页数
				data += "&commentId=" + _self.commentId;
			}else{
				data += "&page=" + _self.currentpage; //提交参数
			}
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryCommentList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_commentList = pageView.records;
						if (new_commentList != null && new_commentList.length > 0) {
							
							for (var i = 0; i <new_commentList.length; i++) {
								var comment = new_commentList[i];
								_self.$set(_self.replyExpandOrShrink, comment.id, false); //是否展开
								
								//组装引用数据
								if(comment.quoteList != null && comment.quoteList.length >0){
									var quoteContent = "";
									for (var j = 0; j <comment.quoteList.length; j++) {
										var quote = comment.quoteList[j];
										//quoteContent = "<div>"+quoteContent+"<span><router-link tag=\"span\" :to=\"{path: '/user/control/home', query: {userName: '"+quote.userName+"'}}\">"+(quote.nickname != null && quote.nickname != '' ? quote.nickname : quote.userName)+"</router-link>&nbsp;的评论：</span><br/>"+quote.content+"</div>";
										
										
										var avatarHtml = "<router-link class=\"avatarBox\"  tag=\"div\" :to=\"{path: '/user/control/home', query: {userName: '"+quote.userName+"'}}\">";
										if(quote.avatarName != undefined && quote.avatarName != null && quote.avatarName != ''){
											avatarHtml += "<img src=\""+quote.avatarPath+"100x100/"+quote.avatarName+"\">";
											
										}
										if(quote.avatarName == undefined || quote.avatarName == null || quote.avatarName == ''){
											var char = (quote.nickname != null && quote.nickname != '') ? quote.nickname : quote.account;
											var width = 16;//头像宽
											avatarHtml += "<img src=\""+letterAvatar(char, width)+"\">";
											
										}
											
										avatarHtml += "</router-link>";
										if(quote.account == null || quote.account == ''){
											avatarHtml += "<span class='cancelNickname'>已注销</span>";
											
										}
										
										var cancelAccount = "";
										if(quote.account == null || quote.account == ''){
											
											cancelAccount += "<div class='cancelAccount'>此用户账号已注销</div>";
										}
										
										
										
										quoteContent = "<div class=\"quoteComment\">"+quoteContent+"<span class=\"userName\">"+avatarHtml+"<router-link tag=\"span\" :to=\"{path: '/user/control/home', query: {userName: '"+quote.userName+"'}}\">"+(quote.nickname != null && quote.nickname != '' ? escapeHtml(quote.nickname) : escapeHtml(quote.account))+"</router-link>&nbsp;的评论：</span><br/>"+quote.content+cancelAccount+"</div>";
	
									}
									_self.$set(_self.quoteData, comment.id, escapeVueHtml(quoteContent));
								}
								
								
								var cancelAccount = "";
								if(comment.account == null || comment.account == ''){
									
									cancelAccount += "<div class='cancelAccount'>此用户账号已注销</div>";
								}
								
								
								//处理图片放大标签
								var contentNode = document.createElement("div");
								contentNode.innerHTML = comment.content;
								_self.bindNode(contentNode);
								comment.content = escapeVueHtml(contentNode.innerHTML)+cancelAccount;
								
							}

							_self.commentList = new_commentList;
							
							
							//生成首字符头像
							_self.$nextTick(function() {
								if (_self.commentList != null && _self.commentList.length > 0) {
									for(var i=0;i<_self.commentList.length; i++){
										var comment = _self.commentList[i];
										if(comment.avatarName == null || comment.avatarName == ''){
											var char = (comment.nickname != null && comment.nickname !="") ? comment.nickname : comment.account;
											//元素的实际宽度
											var width= _self.$refs['commentAvatarData_'+comment.id][0].offsetWidth;
											_self.$refs['commentAvatarData_'+comment.id][0].src = letterAvatar(char, width);
											
											//引用评论头像由生成引用html时直接插入
											
										}
										
										if(comment.replyList != null && comment.replyList.length > 0){
											for(var j=0;j<comment.replyList.length; j++){
												var reply = comment.replyList[j];
												if(reply.avatarName == null || reply.avatarName == ''){
													var char = (reply.nickname != null && reply.nickname !="") ? reply.nickname : reply.account;
													//元素的实际宽度
													var width= _self.$refs['replyAvatarData_'+reply.id][0].offsetWidth;
													_self.$refs['replyAvatarData_'+reply.id][0].src = letterAvatar(char, width);	
												}
												if(reply.friendUserName != null && reply.friendUserName != ''){
													if(_self.$refs['replyFriendAvatarData_'+reply.id]){
														//元素的实际宽度
														var width= _self.$refs['replyFriendAvatarData_'+reply.id][0].offsetWidth;
						                                if(reply.friendNickname != null && reply.friendNickname !=''){
						                                	
															_self.$refs['replyFriendAvatarData_'+reply.id][0].src = letterAvatar(reply.friendNickname, width);
						                                }else{
						                                    _self.$refs['replyFriendAvatarData_'+reply.id][0].src = letterAvatar(reply.friendAccount, width);
						                                }
													}
													
					                            }
											}
										}
										
									}
								}
							});
							
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
						_self.$nextTick(function() {
							//跳转到锚点
							
							//跳转到评论
							if(_self.commentId != null && _self.commentId != "" && (_self.replyId == null || _self.replyId == '')){
								
								var anchor = _self.$el.querySelector("#anchor_"+_self.commentId);
								if(anchor != null){
									document.body.scrollTop = anchor.offsetTop; // chrome
							        document.documentElement.scrollTop = anchor.offsetTop; // firefox
								}
							}
							
							//跳转到回复
		                    if(_self.replyId != null && _self.replyId != ''){
		                    	
		                    	
		                    	if (_self.commentList != null && _self.commentList.length > 0) {
									for(var i=0;i<_self.commentList.length; i++){
										var comment = _self.commentList[i];
										if(comment.replyList != null && comment.replyList.length > 0){
											for(var j=0;j<comment.replyList.length; j++){
												var reply = comment.replyList[j];
												if(reply.id==_self.replyId && j >=_self.viewNumber){//如果在收缩层
													_self.telescopicReply(comment.id);//展开
												}
											}
										}
									}
		                    	}
		                    	
		                    	_self.$nextTick(function() {
		                    		
		                    		var replyElement = _self.$refs['replyData_'+_self.replyId][0];
		                    		let _replyId = replyElement.getAttribute("replyId");
		                    		window.scrollTo(0,replyElement.getBoundingClientRect().top-40);
		                    	});
		                    	
		                    }
						});
						
					}
				}
			});
		},
		
		//展示/隐藏回复
		telescopicReply : function(commentId) {
			for (var data in this.replyExpandOrShrink) {
				if(commentId == data){
					var status = this.replyExpandOrShrink[data];
					if(status){
						this.$set(this.replyExpandOrShrink,commentId, false); //收缩
					}else{
						this.$set(this.replyExpandOrShrink,commentId, true); //伸展
					}
					break;
				}
			}	
		},
		
		//话题操作界面
		topicOperatingUI : function(userName,topicId) {
			var _self = this;
			_self.topicOperating = true;
			var operatingData = new Array();
			
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '编辑', 
					method : _self.topicOperatingAction,	// 调用methods中的函数
					module : 10, //模块
					topicId : topicId
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.topicOperatingAction,	// 调用methods中的函数
				module : 20, //模块
				topicId : topicId
			});
			_self.topicOperatingData = operatingData;
			
		},
		//话题操作动作
		topicOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var topicId = obj.topicId;//话题Id
			
			if(module == 10){
				_self.$router.push({ path: '/user/editTopic',query: {topicId: topicId}});
			}else if(module == 20){
				_self.addReportUI(topicId,10);
				
			}
		},
		//评论操作界面
		commentOperatingUI : function(userName,commentId) {
			var _self = this;
			_self.commentOperating = true;
			var operatingData = new Array();

			operatingData.push({
				name: '回复', 
				method : _self.commentOperatingAction,	// 调用methods中的函数
				module : 10, //模块
				commentId : commentId
			});
			operatingData.push({
				name: '引用', 
				method : _self.commentOperatingAction,	// 调用methods中的函数
				module : 20, //模块
				commentId : commentId
			});
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '编辑', 
					method : _self.commentOperatingAction,	// 调用methods中的函数
					module : 30, //模块
					commentId : commentId
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.commentOperatingAction,	// 调用methods中的函数
				module : 40, //模块
				commentId : commentId
			});
			
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '删除', 
					method : _self.commentOperatingAction,	// 调用methods中的函数
					module : 50, //模块
					commentId : commentId
				});
			}
			
			_self.commentOperatingData = operatingData;
		},
		
		//评论操作动作
		commentOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var commentId = obj.commentId;//评论Id
			
			if(module == 10){
				_self.addReplyUI(commentId);
			}else if(module == 20){
				_self.addQuoteUI(commentId);
				
			}else if(module == 30){
				_self.editCommentUI(commentId);
				
			}else if(module == 40){
				_self.addReportUI(commentId,20);
				
			}else if(module == 50){
				_self.deleteComment(commentId);
				
			}
			
		},
		
		
		//回复操作界面
		replyOperatingUI : function(reply) {
			var _self = this;
			_self.replyOperating = true;
			var operatingData = new Array();

			if(_self.$store.state.user.userName){
				operatingData.push({
					name: '回复', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 10, //模块
					reply : reply
				});
			
			}
			
			
			if(reply.userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '编辑', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 20, //模块
					reply : reply
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.replyOperatingAction,	// 调用methods中的函数
				module : 30, //模块
				reply : reply
			});
			
			if(reply.userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '删除', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 40, //模块
					reply : reply
				});
			}
			
			_self.replyOperatingData = operatingData;
		},
		
		//回复操作动作
		replyOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var reply = obj.reply;//回复
			
			if(module == 10){
				_self.addReplyFriendUI(reply);
			}else if(module == 20){
				_self.editReplyUI(reply.id);
				
			}else if(module == 30){
				_self.addReportUI(reply.id,30);
				
			}else if(module == 40){
				_self.deleteReply(reply.id);
				
			}
		},
		
		
		//发表评论界面
		addCommentUI : function() {
			var _self = this;
			
			_self.show_topic = false;//隐藏话题页
			_self.show_commentEditor = true;//显示话题富文本编辑器
			_self.show_quoteEditor = false;//隐藏引用评论富文本编辑器
			_self.show_editCommentEditor = false;//隐藏修改评论富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = false;//隐藏举报
			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			
			//查询添加评论页
			_self.queryAddComment(function (returnValue){
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//话题编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}
						}
					}
				}
				
				_self.$refs.commentContentEditorToolbar.innerHTML = "";
				_self.$refs.commentContentEditorText.innerHTML = "";
				//创建编辑器
				_self.commentEditor = createEditor(_self.$refs.commentContentEditorToolbar,_self.$refs.commentContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/comment/uploadImage?topicId='+_self.topicId,_self,"commentContent");
				
				
			});
			
			
		},
		
		//查询添加评论页
		queryAddComment : function(callback) {
			var _self = this;
			//清空表单
			_self.commentContent = ''; //发表评论内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.commentContent = "";
			_self.error.captchaValue = "";
			_self.error.comment = "";
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddComment",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowComment") {
									if(returnValue[key] == false){
										_self.showTopic(true);//显示话题内容界面
										_self.$toast({
											message : "发表评论功能未开放",
											duration : 3000,
										});
									}
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//回调
							callback(returnValue);
						}
						
						//滚动
					//	_self.$nextTick(function() {
					//		_self.initScroll(_self.$refs.addCommentFormScroll);

					//	});
					}
				}
			});
		},
		
		//添加评论
		addComment : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			
		
			
			//清空所有错误
			_self.error.commentContent = "";
			_self.error.comment = "";
			_self.error.captchaValue = "";

			
			var parameter = "&topicId=" + _self.topicId; //提交参数
		//	if (_self.commentContent != null && _self.commentContent != ""){	
			//	parameter += "&content=" + encodeURIComponent(_self.commentContent);
		//	}
			parameter += "&content=" + encodeURIComponent(_self.commentEditor.txt.html());
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/add",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showTopic(true);//显示话题内容界面

							
							//清空分页数据
							_self.commentList = []; //话题列表
							
							_self.commentContent = '';//清空评论内容
							_self.commentEditor.txt.clear();//清空编辑器内容
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.commentContent = value_error[error];
										} else if (error == "comment") {
											_self.error.comment = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//引用评论界面
		addQuoteUI : function(commentId) {
			var _self = this;
			_self.show_topic = false;//隐藏话题页
			_self.show_commentEditor = false;//隐藏话题富文本编辑器
			_self.show_quoteEditor = true;//显示引用评论富文本编辑器
			_self.show_editCommentEditor = false;//隐藏修改评论富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = false;//隐藏举报
			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			//查询引用评论页
			_self.queryAddQuote(commentId,function (returnValue){
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//话题编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}
						}
					}
				}
				_self.$refs.quoteContentEditorToolbar.innerHTML = "";
				_self.$refs.quoteContentEditorText.innerHTML = "";
				//创建编辑器
				_self.quoteEditor = createEditor(_self.$refs.quoteContentEditorToolbar,_self.$refs.quoteContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/comment/uploadImage?topicId='+_self.topicId,_self,"quoteContent");
				
			});
			
			
		},
		//查询引用评论页
		queryAddQuote : function(commentId,callback) {
			var _self = this;

			//清空表单
			_self.quoteCommentId = '';//引用评论Id
			_self.quoteCommentContent = '';//引用评论内容
			_self.quoteContent = ''; //发表评论内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.quoteContent = "";
			_self.error.captchaValue = "";
			_self.error.quote = "";
			
			
			var parameter = "&commentId=" + commentId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddQuote",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "quoteContent") {
									
									_self.quoteCommentId = commentId;
									_self.quoteCommentContent =  returnValue[key];
								}if (key == "allowComment") {
									if(returnValue[key] == false){
										_self.showTopic(true);//显示话题内容界面
										_self.$toast({
											message : "发表评论功能未开放",
											duration : 3000,
										});
									}	
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//回调
							callback(returnValue);
						}
						
						//滚动
					//	_self.$nextTick(function() {
					//		_self.initScroll(_self.$refs.addQuoteFormScroll);

					//	});
					}
				}
			});
		},
		
		//添加引用评论
		addQuote : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.quoteContent = "";
			_self.error.quote = "";
			_self.error.captchaValue = "";

			
			var parameter = "&commentId=" + _self.quoteCommentId; //提交参数
			//if (_self.quoteContent != null && _self.quoteContent != "") {
			//	parameter += "&content=" + encodeURIComponent(_self.quoteContent);	
			//}
			parameter += "&content=" + encodeURIComponent(_self.quoteEditor.txt.html());
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/addQuote",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showTopic(true);//显示话题内容界面

							
							//清空分页数据
							_self.commentList = []; //话题列表
							_self.quoteContent = '';//清空引用评论内容
							_self.quoteEditor.txt.clear();//清空编辑器内容
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.quoteContent = value_error[error];
										} else if (error == "quote") {
											_self.error.quote = value_error[error];
											
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		
		//举报选项
		reportOptions : function(childReportTypeList) {
			var options = [];
			
			for(var i=0; i< childReportTypeList.length; i++){
				var childReportType = childReportTypeList[i];
				
				options.push({
				    label: childReportType.name,
				    value: childReportType.id
				});
			}
			return options;
		},
		
		
		//举报界面
		addReportUI : function(parameterId,module) {
			var _self = this;
			
			_self.show_topic = false;//隐藏话题页
			_self.show_commentEditor = false;//显示话题富文本编辑器
			_self.show_quoteEditor = false;//隐藏引用评论富文本编辑器
			_self.show_editCommentEditor = false;//隐藏修改评论富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = true;//隐藏举报

			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			//查询回复页
			_self.queryAddReport(parameterId,module);
		
		},
		//查询举报页
		queryAddReport : function(parameterId,module) {
			var _self = this;
			
			
			//清空表单
			_self.reportTypeList = [];
			_self.reportTypeId = '';//举报分类Id
			_self.reason = '';//理由
			_self.image = [];
			_self.fileList = [];
			_self.reportMaxImageUpload = 0;//图片允许最大上传数量
			_self.parameterId = '',//举报参数Id
			_self.module = '',//举报模块
			_self.show_giveReason = false,//是否显示说明理由表单
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value
			
			//清空所有错误
			_self.error.reportTypeId= "";//举报分类Id
			_self.error.reason= "";//理由
			_self.error.imageFile = "";
			_self.error.report= "";
			
			
			
			var parameter = ""; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/queryAddReport",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReport") {
									if(returnValue[key] == false){
										_self.popup_report = false;
										_self.$toast({
											message : "举报功能已关闭",
											duration : 3000,
										});
									}else{
										_self.parameterId = parameterId;//举报参数Id
										_self.module = module;//举报模块
									}
								}else if (key == "reportTypeList") {
									_self.reportTypeList = returnValue[key];
								}else if (key == "reportMaxImageUpload") {
									_self.reportMaxImageUpload = returnValue[key];
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
					}
				}
			});
		},
		//选择举报分类
		selectReportType : function(reportTypeList) {
			var _self = this;
			_self.$nextTick(function() {
				
				for(var i =0; i<reportTypeList.length; i++){
					var reportType = reportTypeList[i];
					if(reportType.id == _self.reportTypeId && reportType.giveReason){
						_self.show_giveReason = true;
						return;
					}
				}
				
				_self.show_giveReason = false;

			});
			
		},
		//弹出选择文件窗口
		selectFileWindow : function() {
			var _self = this;
			
			
			if(_self.reportMaxImageUpload >0  && _self.fileList.length < _self.reportMaxImageUpload){
				_self.$refs.imageInput.click(); 
			}else{
				_self.$toast({
					message : "已达到图片允许上传数量",
					duration : 3000,
				});
			}
			
			
			
		},
		//选择文件
		selectFile : function() {
			var _self = this;
			var files = _self.$refs.imageInput.files;
			
			if(files.length > 0){
				_self.fileList.push(files[0]);
				
				_self.$refs.imageInput.value = null;
			}
		},
		
		//本地图片预览
		preview : function(file) {
			return URL.createObjectURL(file);
		},
		//删除图片
		deleteImage : function(index) {
			var _self = this;
			_self.fileList.splice(index,1);
		},
		
		//添加举报
		addReport : function(event) {
			var _self = this;
			//清空所有错误
			_self.error.reportTypeId = "";
			_self.error.reason = "";
			_self.error.imageFile = "";
			_self.error.report = "";
			_self.error.captchaValue = "";
			
			
			var formData = new FormData();
			
			
			formData.append("parameterId", _self.parameterId);
			formData.append("module", _self.module);
		
			if(_self.reportTypeId){
				formData.append("reportTypeId", _self.reportTypeId);
			}
			
			if(_self.reason){
				formData.append("reason", _self.reason);
			}
			//图片
			for(var i=0; i<_self.fileList.length; i++){
				var file = _self.fileList[i];
				
				formData.append("imageFile", file);
			}
			
			//验证码Key
			formData.append("captchaKey", _self.captchaKey);

			//验证码值
			formData.append("captchaValue", _self.captchaValue.trim());

			//令牌
			formData.append("token", _self.$store.state.token);
			
			
			
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/report/add",
				data : formData,
				contentType : false, // 不设置内容类型
				processData : false, // 不处理数据
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
	
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
	
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}
						//提交成功
						if (value_success == "true") {
							
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showTopic(true);//显示话题内容界面
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "reportTypeId") {
											_self.error.reportTypeId = value_error[error];
										} else if (error == "reason") {
											_self.error.reason = value_error[error];
										} else if (error == "imageFile") {
											_self.error.imageFile = value_error[error];
										} else if (error == "report") {
											_self.error.report = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
	
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//回复界面
		addReplyUI : function(commentId) {
			this.popup_reply = true;

			//查询回复页
			this.queryAddReply(commentId);
		},
		//查询回复页
		queryAddReply : function(commentId) {
			var _self = this;

			//清空表单
			_self.replyCommentId = '';//回复评论Id
			_self.replyContent = ''; //发表回复内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.replyContent = "";
			_self.error.captchaValue = "";
			_self.error.reply = "";
			
			
			var parameter = "&commentId=" + commentId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						_self.replyCommentId = commentId;
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_reply = false;
										_self.$toast({
											message : "发表回复功能未开放",
											duration : 3000,
										});
									}	
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addReplyFormScroll);
						});
					}
				}
			});
		},
		//添加回复
		addReply : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.replyContent = "";
			_self.error.reply = "";
			_self.error.captchaValue = "";

			
			var parameter = "&commentId=" + _self.replyCommentId; //提交参数
			if (_self.replyContent != null && _self.replyContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.replyContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/addReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_reply = false;

							
							//清空分页数据
							_self.commentList = []; //话题列表
							
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.replyContent = value_error[error];
										} else if (error == "reply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		
		//修改评论界面
		editCommentUI : function(commentId) {
			var _self = this;
			_self.show_topic = false;//隐藏话题页
			_self.show_commentEditor = false;//隐藏话题富文本编辑器
			_self.show_quoteEditor = false;//显示引用评论富文本编辑器
			_self.show_editCommentEditor = true;//隐藏修改评论富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = false;//隐藏举报
			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			
			//查询引用评论页
			_self.queryEditComment(commentId,function (returnValue){
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//话题编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}
						}
					}
				}
				_self.$refs.editCommentContentEditorToolbar.innerHTML = "";
				_self.$refs.editCommentContentEditorText.innerHTML = "";
				//创建编辑器
				_self.editCommentEditor = createEditor(_self.$refs.editCommentContentEditorToolbar,_self.$refs.editCommentContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/comment/uploadImage?topicId='+_self.topicId,_self,"editCommentContent");
				_self.editCommentEditor.txt.html(_self.editCommentContent);//初始化内容
			});
			
			
		},
		
		//查询修改评论页
		queryEditComment : function(commentId,callback) {
			var _self = this;

			//清空表单
			_self.editCommentId = '';//修改评论Id
			_self.editCommentContent = ''; //修改评论内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.editCommentContent = "";
			_self.error.captchaValue = "";
			_self.error.editComment = "";
			
			
			var parameter = "&commentId=" + commentId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryEditComment",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "comment") {
									
									_self.editCommentId = commentId;
									_self.editCommentContent =  returnValue[key].content;
								}if (key == "allowComment") {
									if(returnValue[key] == false){
										_self.showTopic(true);//显示话题内容界面
										_self.$toast({
											message : "不允许修改评论",
											duration : 3000,
										});
									}	
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//回调
							callback(returnValue);
						}
						
					}
				}
			});
		},
		
		//修改评论
		editComment : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.editCommentContent = "";
			_self.error.editComment = "";
			_self.error.captchaValue = "";

			
			var parameter = "&commentId=" + _self.editCommentId; //提交参数

			parameter += "&content=" + encodeURIComponent(_self.editCommentEditor.txt.html());
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/edit",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showTopic(true);//显示话题内容界面

							
							//清空分页数据
							_self.commentList = []; //话题列表
							_self.editCommentContent = '';//清空引用评论内容
							_self.editCommentEditor.txt.clear();//清空编辑器内容
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.editCommentContent = value_error[error];
										} else if (error == "editComment") {
											_self.error.editComment = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//删除评论
		deleteComment : function(commentId) {
			var _self = this;
			_self.$messagebox.confirm('确定删除评论?').then(function (action) {
				var parameter = "&commentId=" + commentId;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/comment/delete",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除评论成功，3秒后自动刷新当前页面",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//清空分页数据
									_self.commentList = []; //评论列表
									//查询评论列表
									_self.queryCommentList();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		//添加回复对方界面
		addReplyFriendUI : function(reply) {
			this.popup_addReplyFriend = true;

			//添加回复对方页
			this.queryReplyFriend(reply);
		},
		
		//添加回复对方页
		queryReplyFriend : function(reply) {
			var _self = this;

			//清空表单
			_self.friendUser = '';
			_self.addReplyFriendCommentId = '';//回复对方评论Id
			_self.addReplyFriendId = '';//回复对方Id
			_self.addReplyFriendContent = ''; //回复对方内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.addReplyFriendContent = "";
			_self.error.captchaValue = "";
			_self.error.reply = "";
			
			
			var parameter = "&commentId=" + reply.commentId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_addReplyFriend = false;
										_self.$toast({
											message : "不允许回复对方",
											duration : 3000,
										});
									}else{
										//对方用户
					                    let friendUser = '';
					                    if(reply.nickname != null && reply.nickname != ''){
					                        friendUser = reply.nickname;
					                    }
					                    if(reply.nickname == null || reply.nickname == ''){
					                        friendUser = reply.account;
					                    }
					                    _self.friendUser = friendUser;
					                    _self.addReplyFriendId = reply.id;
					                    _self.addReplyFriendCommentId = reply.commentId;
									}
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addReplyFriendFormScroll);

						});
					}
				}
			});
		},
		
		//添加回复对方
		addReplyFriend : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.addReplyFriendContent = "";
			_self.error.reply = "";
			_self.error.captchaValue = "";

			
			
			var parameter = "&commentId=" + _self.addReplyFriendCommentId; //提交参数
			parameter += "&friendReplyId=" + _self.addReplyFriendId;
			if (_self.addReplyFriendContent != null && _self.addReplyFriendContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.addReplyFriendContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/addReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_addReplyFriend = false;

							
							//清空分页数据
							_self.commentList = []; //评论列表
							
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.addReplyFriendContent = value_error[error];
										} else if (error == "friendReplyId") {
											_self.error.reply = value_error[error];
										} else if (error == "reply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		
		//修改回复界面
		editReplyUI : function(replyId) {
			this.popup_editReply = true;

			//修改回复页
			this.queryEditReply(replyId);
		},
		//修改回复页
		queryEditReply : function(replyId) {
			var _self = this;

			//清空表单
			_self.editReplyId = '';//回复Id
			_self.editReplyContent = ''; //修改回复内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.editReplyContent = "";
			_self.error.captchaValue = "";
			_self.error.editReply = "";
			
			
			var parameter = "&replyId=" + replyId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryEditCommentReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_reply = false;
										_self.$toast({
											message : "不允许修改回复",
											duration : 3000,
										});
									}	
								}else if (key == "reply") {
									_self.editReplyId = replyId;
									_self.editReplyContent =  returnValue[key].content;
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.editReplyFormScroll);

						});
					}
				}
			});
		},
		//修改回复
		editReply : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.editReplyContent = "";
			_self.error.editReply = "";
			_self.error.captchaValue = "";

			
			var parameter = "&replyId=" + _self.editReplyId; //提交参数
			if (_self.editReplyContent != null && _self.editReplyContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.editReplyContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/comment/editReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_editReply = false;

							
							//清空分页数据
							_self.commentList = []; //评论列表
							
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询评论列表
							_self.queryCommentList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.replyContent = value_error[error];
										} else if (error == "reply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//删除回复
		deleteReply : function(replyId) {
			var _self = this;
			_self.$messagebox.confirm('确定删除回复?').then(function (action) {
				var parameter = "&replyId=" + replyId;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/comment/deleteReply",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除回复成功，3秒后自动刷新当前页面",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//清空分页数据
									_self.commentList = []; //评论列表
									//查询评论列表
									_self.queryCommentList();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		
		//初始化
		initialization : function() {
			
			
			
			var topicId = getUrlParam("topicId");//话题Id
			if(topicId != null){
				this.topicId = topicId;
			}
			var commentId = getUrlParam("commentId");//评论Id
			if(commentId != null){
				this.commentId = commentId;
			}
			
			var replyId = getUrlParam("replyId");//回复Id
			if(replyId != null){
				this.replyId = replyId;
			}
			
			
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询话题
			this.queryTopic();
			//查询评论列表
			this.queryCommentList();
			
			//查询用户是否已经收藏话题
			this.queryAlreadyCollected();
			//查询话题用户收藏总数
			this.queryFavoriteCount();
			//查询用户是否已经点赞该话题
			this.queryAlreadyLiked();
			//查询话题点赞总数
			this.queryLikeCount();
			
		},
		//初始化BScroll滚动插件//this.$refs.addCommentFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				//preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT|HIDE)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
				//eventPassthrough :'horizontal',//解决文本无法复制
				HWCompositing : true, //是否启用硬件加速

			});
		},
	}
});

//问答列表组件
var askList_component = Vue.extend({
	name: 'askList',//组件名称，keep-alive缓存需要本参数
	template : '#askList-template',
	data : function data() {
		return {
			popup_allTag :false,
			allTagList: '',//全部标签
			
			questionTagId :'',//标签Id
			questionTagName :'',//标签名称
			filterCondition:'',//过滤条件
			questionList : [], //问题列表
			currentpage : 1, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
			
		};
	},
	
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['askList']);
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},
	
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/question') {//前往问题内容页的时候需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['askList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		//发表问题界面跳转
        addQuestionJump : function() {
            if(this.$store.state.user.userId == ""){//如果未登录
                this.$router.push({
                    path : '/login'
                });
                return;
            }
             
             
            this.$router.push({
                path : '/user/addQuestion'
            });
        },
		//查询问题列表
		queryQuestionList : function() {
			var _self = this;
			var data = "";
			if(_self.questionTagId != null && _self.questionTagId != ""){
				data += "&questionTagId=" + _self.questionTagId;
			}
			if(_self.filterCondition != null){
				data += "&filterCondition=" + _self.filterCondition;
			}
			
			
			
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryQuestionList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_questionList = pageView.records;
						if (new_questionList != null && new_questionList.length > 0) {
							_self.questionList = new_questionList;
							
							//生成首字符头像
							_self.$nextTick(function() {
								if (_self.questionList != null && _self.questionList.length > 0) {
									for(var i=0;i<_self.questionList.length; i++){
										var question = _self.questionList[i];
										if(question.avatarName == null || question.avatarName == ''){
											var char = (question.nickname != null && question.nickname !="") ? question.nickname : question.account;
											//元素的实际宽度
											var width= _self.$refs['questionAvatarData_'+question.id][0].offsetWidth;
											_self.$refs['questionAvatarData_'+question.id][0].src = letterAvatar(char, width);	
										}
									}
								}
							});
							
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		
		
		//显示所有标签
		showAllTag : function() {
			var _self = this;
			//显示
			_self.popup_allTag = true;
			var tagId = getUrlParam("questionTagId");//当前标签
			if(tagId != null){
				_self.questionTagId = tagId;
			}
			
			_self.queryAllTag();
		},
		
		
		//查询所有标签
		queryAllTag : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAllQuestionTag",
				success : function success(result) {
					if (result != "") {
						var tagList = $.parseJSON(result);
						if (tagList != null && tagList.length > 0) {
							_self.allTagList = tagList;
							
							for (var i= 0; i < tagList.length; i++) {
								var questionTag = tagList[i];
								if(_self.questionTagId == questionTag.id){
									_self.questionTagName = questionTag.name;
									break;
								}
								if (questionTag.childTag != null && questionTag.childTag.length > 0) {
									for (var j = 0; j < questionTag.childTag.length; j++) {
										var childQuestionTag = questionTag.childTag[j];
										if(_self.questionTagId == childQuestionTag.id){
											_self.questionTagName = childQuestionTag.name;
											break;
										}
									}
									
								}
							}
						}
						
					}
					//滚动
					_self.$nextTick(function() {
						if (!_self.scroll) {
							_self.initScroll();
				        }else{
				        	// 复用Bscroll
				        	_self.scroll.refresh();
				        }
					});
				}
			});
		},
		
		//初始化
		initialization : function() {

			var tagId = getUrlParam("questionTagId");//当前标签
			if(tagId != null){
				this.questionTagId = tagId;
				//查询所有标签列表
				this.queryAllTag();
			}
			var filterCondition = getUrlParam("filterCondition");//过滤条件
			if(filterCondition != null){
				this.filterCondition = filterCondition;
			}
			var page = getUrlParam("page");//当前页码
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询问题列表
			this.queryQuestionList();
		},
		
		
		//初始化BScroll滚动插件
		initScroll : function initScroll() {
			this.scroll = new BScroll(this.$refs.questionTagScroll, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				HWCompositing : true, //是否启用硬件加速
			});
		}
	}
});


//问题内容组件
var question_component = Vue.extend({
	template : '#question-template',
	data : function data() {
		return {
			questionId :'',//问题Id
			answerId:'',//答案Id
			replyId:'',//回复Id
			question : '', //问题
			answerList : '', //答案列表
			currentpage : 1, //当前答案页码
			totalpage : 1, //答案总页数
			on: '',//上一页
			next: '',//下一页
			replyExpandOrShrink : [], //回复展开/收缩 map格式 key:答案Id value:是否展开
			
			show_answerEditor:false,//添加答案富文本编辑器显示/隐藏
			show_editAnswerEditor:false,//修改答案富文本编辑器显示/隐藏
			show_editReplyEditor:false,//修改回复富文本编辑器显示/隐藏
			show_question:true,//问题内容显示/隐藏
			
			viewNumber:2,//回复伸缩 展示数量
			
			popup_reply : false, //发表回复弹出层
			popup_editReply : false, //修改回复弹出层
			popup_addReplyFriend : false, //发表回复对方弹出层
			answerContent:'',//发表答案内容
			replyAnswerId :'',//回复答案Id
			replyContent:'',//发表回复内容
			editReplyId :'',//修改回复Id
			editReplyContent:'',//修改回复内容
			
			friendUser:'',//发表回复对方用户
			addReplyFriendCommentId:'',//发表回复对方评论Id
			addReplyFriendId :'',//发表回复对方Id
			addReplyFriendContent:'',//发表回复对方内容
			
			showCaptcha : false, //发表答案/发表回复是否显示验证码
			imgUrl : '', //验证码图片
			captchaKey : '', //验证码key
			captchaValue : '', //验证码value
			error : {
				answerContent : '',
				replyContent: '',
				addReplyFriendContent:'',//发表回复对方内容
				captchaValue : '',
				answer: '',
				reply: '',
				reportTypeId:'',//举报分类Id
			    reason:'',//理由
			    imageFile:'',
			    report:''
			},
			answerEditor : '',//答案富文本编辑器
			editAnswerEditor : '',//修改答案富文本编辑器
			
			alreadyFavoriteQuestion :false,//用户是否已经收藏问题
			questionFavoriteCount : 0,//问题用户收藏总数
			
			pictureOptions: {//放大图片配置
		        url: 'src',//定义从何处获取原始图像URL进行查看
		        navbar : true, //导航栏
		        title : false, //标题
		        toolbar : false, //工具栏
		        loop:false, //是否启用循环查看
		        filter : function(image) {
		        	return image.dataset.enable != 'false';//将不允许放大的表情图片过滤
		        },
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
		    
		    following :false,//是否已经关注该用户
		    
		    questionOperating: false,//问题是否显示操作页面
		    questionOperatingData: '',//问题弹出操作菜单
		    answerOperating: false,//答案是否显示操作页面
		    answerOperatingData: '',//答案弹出操作菜单
		    replyOperating: false,//回复是否显示操作页面
		    replyOperatingData: '',//回复弹出操作菜单
		    
		    show_report:false,//添加举报弹出层 显示/隐藏
		    reportTypeList:[],
		    reportTypeId:'',//举报分类Id
		    reason:'',//理由
		    image:[],
		    fileList:[],//上传表单图片列表
		    reportMaxImageUpload:0,//图片允许最大上传数量
		    parameterId:'',//举报参数Id
			module:'',//举报模块
			show_giveReason:false,//是否显示说明理由表单
			
		};
	},
	
	created : function created() {
		//初始化
		this.initialization();
	},
	mounted : function mounted(){
		//挂载完成后，判断浏览器是否支持popstate
		if (window.history && window.history.pushState) {//监听浏览器前进后退按钮
			
			window.addEventListener('popstate', this.goBack, false);
		}
	},
	destroyed : function destroyed() {//离开当前页面
		this.showQuestion(false);
		//页面销毁时，取消监听
		window.removeEventListener('popstate', this.goBack, false);
	},
	deactivated: function() {//keep-alive组件停用时调用 (keepAlive缓存机制才会触发的钩子函数)
		this.showQuestion(false);
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},
	computed: {
		//动态解析模板数据
		analyzeDataComponent: function analyzeDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					mounted :function () {
					
					},
					props: this.$options.props, // re-use current props definitions
					methods: {
						
				    }
				};
			};	
		},
	},
	methods : {
		//显示问题内容界面
		showQuestion : function(back){
		    this.show_question = true;//显示问题页
		    this.show_answerEditor = false;//隐藏答案富文本编辑器
		    this.show_editAnswerEditor = false;//隐藏修改答案富文本编辑器
		    this.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
		    this.show_report = false;//隐藏举报
		    
		    
		    
		    //判断浏览器是否支持popstate
			if (window.history && window.history.pushState && back) {
				//后退
				history.go(-1);
			}
		},

		//查询问题
		queryQuestion : function() {
			var _self = this;

			var data = "questionId=" + _self.questionId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryQuestionContent",
				data : data,
				success : function success(result) {
					if (result != "") {
						var question = $.parseJSON(result);
						if (question != null) {
							
							var cancelAccount = "";
							if(question.account == null || question.account == ''){
								
								cancelAccount += "<div class='cancelAccount'>此用户账号已注销</div>";
							}
							
							//处理隐藏标签
							var contentNode = document.createElement("div");
							contentNode.innerHTML = question.content;
							
							_self.bindNode(contentNode);
							question.content = escapeVueHtml(contentNode.innerHTML)+cancelAccount;
					
							if(question.appendQuestionItemList != null && question.appendQuestionItemList.length >0){
								for(var i=0; i<question.appendQuestionItemList.length; i++){
									var appendQuestionItem = question.appendQuestionItemList[i];
									
									//处理图片放大标签
									var contentNode2 = document.createElement("div");
									contentNode2.innerHTML = appendQuestionItem.content;
									_self.bindNode(contentNode2);
									appendQuestionItem.content = escapeVueHtml(contentNode2.innerHTML);
									
									
								}
								
							}
							
							
							_self.question = question;
				
							
							//生成首字符头像
							_self.$nextTick(function() {
								if(_self.question.avatarName == null || _self.question.avatarName == ''){
									var char = (_self.question.nickname != null && _self.question.nickname !="") ? _self.question.nickname : _self.question.account;
									//元素的实际宽度
									var width= _self.$refs.questionUserAvatar.offsetWidth;
									_self.$refs.questionUserAvatar.src = letterAvatar(char, width);	
									
									
								}
							});
							
							//查询是否已经关注该用户
							_self.queryFollowing();
						}
					}
				}
			});
			
		},

		
		//递归绑定节点参数
		bindNode : function(node) {
			//先找到子节点
	        var nodeList = node.childNodes;
	        for(var i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	        	var childNode = nodeList[i];
	            var random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		var src = childNode.getAttribute("src");
	            		
	            		if(childNode.getAttribute("width") == null){//不是表情图片
	            			childNode.setAttribute("key",random+"_"+i);
	            		}else{
	            			childNode.setAttribute("data-enable","false");//标记不显示放大图
	            		}
	            		
	            		//延迟加载 表情图片也使用<img>标签，也执行延迟加载
            			childNode.setAttribute("src",this.$store.state.commonPath+'images/null.gif');
            			childNode.setAttribute("data-src",src);
	            	}
	            	
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		var pre_html = childNode.innerHTML;
	            		var class_val = childNode.className;
	            		var lan_class = "";
	            		
	        	        var class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(var k=0; k<class_arr.length; k++){
	        	        	var className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	            		
	        	        var nodeHtml = "";

            			//删除code节点
            			var preChildNodeList = childNode.childNodes;
            			for(var p = 0;p < preChildNodeList.length;p++){
            				var preChildNode = preChildNodeList[p];
            				if(preChildNode.nodeName.toLowerCase() == "code" ){
            					nodeHtml += preChildNode.innerHTML;
            					preChildNode.parentNode.removeChild(preChildNode);
                			}
            				
            			}
            			
            			var dom = document.createElement('code');
            			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				Prism.highlightElement(dom);
	            		
	            	}
	            	this.bindNode(childNode);
	            }
	        }
		},
		
		
		//查询是否已经关注该用户
  		queryFollowing : function() {
  			var _self = this;
  			if(_self.question != ''){
  				var data = "userName=" + _self.question.userName; //提交参数
  				$.ajax({
  					type : "GET",
  					cache : false,
  					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
  					url : "queryFollowing",
  					data : data,
  					success : function success(result) {
  						if (result != "") {
  							var following = $.parseJSON(result);
  							if (following != null) {
  								_self.following = following;
  							}
  						}
  					}
  				});
  				
  			}
  		},
	    //添加关注
  		addFollow : function(userName) {
  			var _self = this;
  			if(_self.following == false){
  				_self.$messagebox.confirm('确定关注?').then(function (action) {
  					var parameter = "&userName=" + userName;

  					//	alert(parameter);
  					//令牌
  					parameter += "&token=" + _self.$store.state.token;
  					$.ajax({
  						type : "POST",
  						cache : false,
  						async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
  						url : "user/control/follow/add",
  						data : parameter,
  						success : function success(result) {
  							if (result != "") {

  								var returnValue = $.parseJSON(result);

  								var value_success = "";
  								var value_error = null;

  								for (var key in returnValue) {
  									if (key == "success") {
  										value_success = returnValue[key];
  									} else if (key == "error") {
  										value_error = returnValue[key];
  									}
  								}

  								//加入成功
  								if (value_success == "true") {
  									_self.$toast({
  										message : "关注成功",
  										duration : 3000,
  										className : "mint-ui-toast",
  									});

  									setTimeout(function() {
  										//查询是否已经关注该用户
  										_self.queryFollowing();
  									}, 3000);
  									
  								} else {
  									//显示错误
  									if (value_error != null) {


  										var htmlContent = "";
  										var count = 0;
  										for (var errorKey in value_error) {
  											var errorValue = value_error[errorKey];
  											count++;
  											htmlContent += count + ". " + errorValue + "<br>";
  										}
  										_self.$messagebox('提示', htmlContent);

  									}
  								}
  							}
  						}
  					});
  				}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
  				    console.log(err);
  				});
  				
  			}
  		},
		
		//查询用户是否已经收藏问题
		queryAlreadyFavoriteQuestion : function() {
			var _self = this;
			var data = "questionId=" + _self.questionId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAlreadyFavoriteQuestion",
				data : data,
				success : function success(result) {
					if (result != "") {
						var alreadyFavoriteQuestion = $.parseJSON(result);
						if (alreadyFavoriteQuestion != null) {
							_self.alreadyFavoriteQuestion = alreadyFavoriteQuestion;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		//查询问题用户收藏总数
		queryQuestionFavoriteCount : function() {
			var _self = this;
			var data = "questionId=" + _self.questionId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryQuestionFavoriteCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							_self.questionFavoriteCount = count;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		//加入收藏夹
		addFavorite : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定加入收藏夹?').then(function (action) {
				var parameter = "&questionId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/favorite/add",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "加入成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//刷新用户是否已经收藏问题
									_self.queryAlreadyFavoriteQuestion();
									
									//刷新问题用户收藏总数
									_self.queryQuestionFavoriteCount();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
	
		//查询答案列表
		queryAnswerList : function() {
			var _self = this;
			var data = "";
			if(_self.questionId != null && _self.questionId != ""){
				data += "questionId=" + _self.questionId;
			}
			if(_self.answerId != null && _self.answerId != ""){//提交答案Id时不能提交分页数
				data += "&answerId=" + _self.answerId;
			}else{
				data += "&page=" + _self.currentpage; //提交参数
			}
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAnswerList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_answerList = pageView.records;
						if (new_answerList != null && new_answerList.length > 0) {
							
							for (var i = 0; i <new_answerList.length; i++) {
								var answer = new_answerList[i];
								_self.$set(_self.replyExpandOrShrink, answer.id, false); //是否展开	
								
								
								var cancelAccount = "";
								if(answer.account == null || answer.account == ''){
									
									cancelAccount += "<div class='cancelAccount'>此用户账号已注销</div>";
								}
								
								//处理图片放大标签
								var contentNode = document.createElement("div");
								contentNode.innerHTML = answer.content;
								_self.bindNode(contentNode);
								answer.content = escapeVueHtml(contentNode.innerHTML)+cancelAccount;
							}
							_self.answerList = new_answerList;
							
							
							//生成首字符头像
							_self.$nextTick(function() {
								if (_self.answerList != null && _self.answerList.length > 0) {
									for(var i=0;i<_self.answerList.length; i++){
										var answer = _self.answerList[i];
										if(answer.avatarName == null || answer.avatarName == ''){
											var char = (answer.nickname != null && answer.nickname !="") ? answer.nickname : answer.account;
											//元素的实际宽度
											var width= _self.$refs['answerAvatarData_'+answer.id][0].offsetWidth;
											_self.$refs['answerAvatarData_'+answer.id][0].src = letterAvatar(char, width);	
										}
										
										if(answer.answerReplyList != null && answer.answerReplyList.length > 0){
											for(var j=0;j<answer.answerReplyList.length; j++){
												var reply = answer.answerReplyList[j];
												if(reply.avatarName == null || reply.avatarName == ''){
													var char = (reply.nickname != null && reply.nickname !="") ? reply.nickname : reply.account;
													//元素的实际宽度
													var width= _self.$refs['answerReplyAvatarData_'+reply.id][0].offsetWidth;
													_self.$refs['answerReplyAvatarData_'+reply.id][0].src = letterAvatar(char, width);	
												}
												if(reply.friendUserName != null && reply.friendUserName != ''){
													if(_self.$refs['answerReplyFriendAvatarData_'+reply.id]){
														//元素的实际宽度
														var width= _self.$refs['answerReplyFriendAvatarData_'+reply.id][0].offsetWidth;
						                                if(reply.friendNickname != null && reply.friendNickname !=''){
						                                	
															_self.$refs['answerReplyFriendAvatarData_'+reply.id][0].src = letterAvatar(reply.friendNickname, width);
						                                }else{
						                                    _self.$refs['answerReplyFriendAvatarData_'+reply.id][0].src = letterAvatar(reply.friendAccount, width);
						                                }
													}
													
					                            }
											}
										}
										
									}
								}
							});
							
							
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
						_self.$nextTick(function() {
							//跳转到锚点
							
							//跳转到答案
							if(_self.answerId != null && _self.answerId != "" && (_self.replyId == null || _self.replyId == '')){
								
								var anchor = _self.$el.querySelector("#anchor_"+_self.answerId);
								if(anchor != null){
									document.body.scrollTop = anchor.offsetTop; // chrome
							        document.documentElement.scrollTop = anchor.offsetTop; // firefox
								}
							}
							
							//跳转到回复
		                    if(_self.replyId != null && _self.replyId != ''){
		                    	if (_self.answerList != null && _self.answerList.length > 0) {
									for(var i=0;i<_self.answerList.length; i++){
										var answer = _self.answerList[i];
										if(answer.answerReplyList != null && answer.answerReplyList.length > 0){
											for(var j=0;j<answer.answerReplyList.length; j++){
												var reply = answer.answerReplyList[j];
												if(reply.id==_self.replyId && j >=_self.viewNumber){//如果在收缩层
													_self.telescopicReply(answer.id);//展开
												}
											}
										}
									}
		                    	}
		                    	
		                    	_self.$nextTick(function() {
		                    		
		                    		var replyElement = _self.$refs['replyData_'+_self.replyId][0];
		                    		let _replyId = replyElement.getAttribute("replyId");
		                    		window.scrollTo(0,replyElement.getBoundingClientRect().top-40);
		                    	});
		                    	
		                    }
							
							
							
							
							
							
						});
						
					}
				}
			});
		},
		
		//展示/隐藏回复
		telescopicReply : function(answerId) {
			for (var data in this.replyExpandOrShrink) {
				if(answerId == data){
					var status = this.replyExpandOrShrink[data];
					if(status){
						this.$set(this.replyExpandOrShrink,answerId, false); //收缩
					}else{
						this.$set(this.replyExpandOrShrink,answerId, true); //伸展
					}
					break;
				}
			}	
		},
		
		//问题操作界面
		questionOperatingUI : function(userName,questionId) {
			var _self = this;
			_self.questionOperating = true;
			var operatingData = new Array();
			
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '追加', 
					method : _self.questionOperatingAction,	// 调用methods中的函数
					module : 10, //模块
					questionId : questionId
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.questionOperatingAction,	// 调用methods中的函数
				module : 20, //模块
				questionId : questionId
			});
			_self.questionOperatingData = operatingData;
			
		},
		//问题操作动作
		questionOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var questionId = obj.questionId;//问题Id
			
			if(module == 10){
				_self.$router.push({ path: '/user/appendQuestion',query: {questionId: questionId}});
			}else if(module == 20){
				_self.addReportUI(questionId,40);
				
			}
		},
		//答案操作界面
		answerOperatingUI : function(userName,answerId,adoptionAnswerId) {
			var _self = this;
			_self.answerOperating = true;
			var operatingData = new Array();
			
			if(userName == _self.$store.state.user.userName && adoptionAnswerId == 0){
				operatingData.push({
					name: '采纳', 
					method : _self.answerOperatingAction,	// 调用methods中的函数
					module : 10, //模块
					answerId : answerId
				});
			}
			operatingData.push({
				name: '回复', 
				method : _self.answerOperatingAction,	// 调用methods中的函数
				module : 20, //模块
				answerId : answerId
			});
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '编辑', 
					method : _self.answerOperatingAction,	// 调用methods中的函数
					module : 30, //模块
					answerId : answerId
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.answerOperatingAction,	// 调用methods中的函数
				module : 40, //模块
				answerId : answerId
			});
			
			if(userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '删除', 
					method : _self.answerOperatingAction,	// 调用methods中的函数
					module : 50, //模块
					answerId : answerId
				});
			}
			
			_self.answerOperatingData = operatingData;
		},
		
		//答案操作动作
		answerOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var answerId = obj.answerId;//答案Id
			
			if(module == 10){
				_self.adoptionAnswer(answerId);
			}else if(module == 20){
				_self.addReplyUI(answerId);
				
			}else if(module == 30){
				_self.editAnswerUI(answerId);
				
			}else if(module == 40){
				_self.addReportUI(answerId,50);
				
			}else if(module == 50){
				_self.deleteAnswer(answerId);
				
			}
			
		},
		
		
		//回复操作界面
		replyOperatingUI : function(reply) {
			var _self = this;
			_self.replyOperating = true;
			var operatingData = new Array();
			
			if(_self.$store.state.user.userName){
				operatingData.push({
					name: '回复', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 10, //模块
					reply : reply
				});
			
			}

			if(reply.userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '编辑', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 20, //模块
					reply : reply
				});
			}
			
			operatingData.push({
				name: '举报', 
				method : _self.replyOperatingAction,	// 调用methods中的函数
				module : 30, //模块
				reply : reply
			});
			
			if(reply.userName == _self.$store.state.user.userName){
				operatingData.push({
					name: '删除', 
					method : _self.replyOperatingAction,	// 调用methods中的函数
					module : 40, //模块
					reply : reply
				});
			}
			
			_self.replyOperatingData = operatingData;
		},
		
		//回复操作动作
		replyOperatingAction : function(obj) {
			var _self = this;
			
			var module = obj.module;//模块
			var reply = obj.reply;//回复
			
			if(module == 10){
				_self.addReplyFriendUI(reply);
			}else if(module == 20){
				_self.editReplyUI(reply.id);
				
			}else if(module == 30){
				_self.addReportUI(reply.id,60);
				
			}else if(module == 40){
				_self.deleteReply(reply.id);
				
			}
		},
		
		
		//发表答案界面
		addAnswerUI : function() {
			var _self = this;
			
			_self.show_question = false;//隐藏问题页
			_self.show_answerEditor = true;//显示问题富文本编辑器
			_self.show_editAnswerEditor = false;//隐藏修改答案富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = false;//隐藏举报
			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			
			//查询添加答案页
			_self.queryAddAnswer(function (returnValue){
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//答案编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}
						}
					}
				}
				
				_self.$refs.answerContentEditorToolbar.innerHTML = "";
				_self.$refs.answerContentEditorText.innerHTML = "";
				//创建编辑器
				_self.answerEditor = createEditor(_self.$refs.answerContentEditorToolbar,_self.$refs.answerContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/answer/uploadImage?questionId='+_self.questionId,_self,"answerContent");
				
				
			});
			
			
		},
	
		//查询添加答案页
		queryAddAnswer : function(callback) {
			var _self = this;

			//清空表单
			_self.answerContent = ''; //发表答案内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.answerContent = "";
			_self.error.captchaValue = "";
			_self.error.answer = "";
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddAnswer",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowAnswer") {
									if(returnValue[key] == false){
										_self.showQuestion(true);//显示话题内容界面
										_self.$toast({
											message : "发表答案功能未开放",
											duration : 3000,
										});
									}
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//回调
							callback(returnValue);
						}
						
					}
				}
			});
		},
		
		//添加答案
		addAnswer : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			
		
			
			//清空所有错误
			_self.error.answerContent = "";
			_self.error.answer = "";
			_self.error.captchaValue = "";

			
			var parameter = "&questionId=" + _self.questionId; //提交参数
	
			parameter += "&content=" + encodeURIComponent(_self.answerEditor.txt.html());
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answer/add",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showQuestion(true);//显示问题内容界面

							
							//清空分页数据
							_self.answerList = []; //答案列表
							
							_self.answerContent = '';//清空答案内容
							_self.answerEditor.txt.clear();//清空编辑器内容
						
							//查询答案列表
							_self.queryAnswerList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.answerContent = value_error[error];
										} else if (error == "answer") {
											_self.error.answer = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		//修改答案界面
		editAnswerUI : function(answerId) {
			var _self = this;
			_self.show_question = false;//隐藏问题页
			_self.show_answerEditor = false;//隐藏问题富文本编辑器
			_self.show_editAnswerEditor = true;//隐藏修改答案富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = false;//隐藏举报
			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			
			//查询引用答案页
			_self.queryEditAnswer(answerId,function (returnValue){
				
				//编辑器图标
				var editorIconList = new Array();
				for (var key in returnValue) {
					if (key == "availableTag") {//问题编辑器允许使用标签
						var availableTagList = $.parseJSON(returnValue[key]);
						for(var i=0; i<availableTagList.length; i++){
							var _availableTag = availableTagList[i];
							
							if(_availableTag == "code"){//代码
								editorIconList.push("code");
							}else if(_availableTag == "forecolor"){//文字颜色
							//	editorIconList.push("foreColor");
							}else if(_availableTag == "hilitecolor"){//文字背景
							//	editorIconList.push("backColor");
							}else if(_availableTag == "bold"){//粗体
								editorIconList.push("bold");
							}else if(_availableTag == "italic"){//斜体
								editorIconList.push("italic");
							}else if(_availableTag == "underline"){//下划线
								editorIconList.push("underline");
							}else if(_availableTag == "link"){//插入链接
								editorIconList.push("link");
							}else if(_availableTag == "emoticons"){//插入表情
								editorIconList.push("emoticon");
							}else if(_availableTag == "image"){//图片
								editorIconList.push("image");
							}else if(_availableTag == "insertfile"){//文件
								editorIconList.push("file");
							}
						}
					}
				}
				_self.$refs.editAnswerContentEditorToolbar.innerHTML = "";
				_self.$refs.editAnswerContentEditorText.innerHTML = "";
				//创建编辑器
				_self.editAnswerEditor = createEditor(_self.$refs.editAnswerContentEditorToolbar,_self.$refs.editAnswerContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/answer/uploadImage?questionId='+_self.questionId,_self,"editAnswerContent");
				_self.editAnswerEditor.txt.html(_self.editAnswerContent);//初始化内容
			});
			
			
		},
		
		//查询修改答案页
		queryEditAnswer : function(answerId,callback) {
			var _self = this;

			//清空表单
			_self.editAnswerId = '';//修改答案Id
			_self.editAnswerContent = ''; //修改答案内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.editAnswerContent = "";
			_self.error.captchaValue = "";
			_self.error.editAnswer = "";
			
			
			var parameter = "&answerId=" + answerId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryEditAnswer",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "answer") {
									
									_self.editAnswerId = answerId;
									_self.editAnswerContent =  returnValue[key].content;
								}if (key == "allowAnswer") {
									if(returnValue[key] == false){
										_self.showQuestion(true);//显示问题内容界面
										_self.$toast({
											message : "不允许修改答案",
											duration : 3000,
										});
									}	
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//回调
							callback(returnValue);
						}
						
					}
				}
			});
		},
		
		//修改答案
		editAnswer : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.editAnswerContent = "";
			_self.error.editAnswer = "";
			_self.error.captchaValue = "";

			
			var parameter = "&answerId=" + _self.editAnswerId; //提交参数

			parameter += "&content=" + encodeURIComponent(_self.editAnswerEditor.txt.html());
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answer/edit",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}
						

						//提交成功
						if (value_success == "true") {
							_self.show_question = true;//隐藏问题页
							_self.show_answerEditor = false;//隐藏问题富文本编辑器
							_self.show_editAnswerEditor = false;//隐藏修改答案富文本编辑器
							_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
							_self.show_report = false;//隐藏举报
							
							
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showQuestion(true);//显示问题内容界面

							
							//清空分页数据
							_self.answerList = []; //问题列表
							_self.editAnswerContent = '';//清空引用答案内容
							_self.editAnswerEditor.txt.clear();//清空编辑器内容
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询答案列表
							_self.queryAnswerList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.editAnswerContent = value_error[error];
										} else if (error == "editAnswer") {
											_self.error.editAnswer = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		//删除答案
		deleteAnswer : function(answerId) {
			var _self = this;
			_self.$messagebox.confirm('确定删除答案?').then(function (action) {
				var parameter = "&answerId=" + answerId;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/answer/delete",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除答案成功，3秒后自动刷新当前页面",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//清空分页数据
									_self.answerList = []; //答案列表
									//查询答案列表
									_self.queryAnswerList();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		
	
		//举报选项
		reportOptions : function(childReportTypeList) {
			var options = [];
			
			for(var i=0; i< childReportTypeList.length; i++){
				var childReportType = childReportTypeList[i];
				
				options.push({
				    label: childReportType.name,
				    value: childReportType.id
				});
			}
			return options;
		},
		
		
		//举报界面
		addReportUI : function(parameterId,module) {
			var _self = this;
			
			_self.show_question = false;//隐藏问题页
			_self.show_answerEditor = false;;//显示问题富文本编辑器
			_self.show_editAnswerEditor = false;//隐藏修改答案富文本编辑器
			_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
			_self.show_report = true;//隐藏举报

			//判断浏览器是否支持popstate
			if (window.history && window.history.pushState) {
				// 向历史记录中插入了当前页
		        history.pushState(null, null, document.URL);
			}
			//查询回复页
			_self.queryAddReport(parameterId,module);
		
		},
		//查询举报页
		queryAddReport : function(parameterId,module) {
			var _self = this;
			
			
			//清空表单
			_self.reportTypeList = [];
			_self.reportTypeId = '';//举报分类Id
			_self.reason = '';//理由
			_self.image = [];
			_self.fileList = [];
			_self.reportMaxImageUpload = 0;//图片允许最大上传数量
			_self.parameterId = '',//举报参数Id
			_self.module = '',//举报模块
			_self.show_giveReason = false,//是否显示说明理由表单
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value
			
			//清空所有错误
			_self.error.reportTypeId= "";//举报分类Id
			_self.error.reason= "";//理由
			_self.error.imageFile = "";
			_self.error.report= "";
			
			
			
			var parameter = ""; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/queryAddReport",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReport") {
									if(returnValue[key] == false){
										_self.popup_report = false;
										_self.$toast({
											message : "举报功能已关闭",
											duration : 3000,
										});
									}else{
										_self.parameterId = parameterId;//举报参数Id
										_self.module = module;//举报模块
									}
								}else if (key == "reportTypeList") {
									_self.reportTypeList = returnValue[key];
								}else if (key == "reportMaxImageUpload") {
									_self.reportMaxImageUpload = returnValue[key];
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
					}
				}
			});
		},
		//选择举报分类
		selectReportType : function(reportTypeList) {
			var _self = this;
			_self.$nextTick(function() {
				
				for(var i =0; i<reportTypeList.length; i++){
					var reportType = reportTypeList[i];
					if(reportType.id == _self.reportTypeId && reportType.giveReason){
						_self.show_giveReason = true;
						return;
					}
				}
				
				_self.show_giveReason = false;

			});
			
		},
		//弹出选择文件窗口
		selectFileWindow : function() {
			var _self = this;
			
			
			if(_self.reportMaxImageUpload >0  && _self.fileList.length < _self.reportMaxImageUpload){
				_self.$refs.imageInput.click(); 
			}else{
				_self.$toast({
					message : "已达到最大图片允许上传数量",
					duration : 3000,
				});
			}
			
			
			
		},
		//选择文件
		selectFile : function() {
			var _self = this;
			var files = _self.$refs.imageInput.files;
			
			if(files.length > 0){
				_self.fileList.push(files[0]);
				
				_self.$refs.imageInput.value = null;
			}
		},
		
		//本地图片预览
		preview : function(file) {
			return URL.createObjectURL(file);
		},
		//删除图片
		deleteImage : function(index) {
			var _self = this;
			_self.fileList.splice(index,1);
		},
		
		//添加举报
		addReport : function(event) {
			var _self = this;
			//清空所有错误
			_self.error.reportTypeId = "";
			_self.error.reason = "";
			_self.error.imageFile = "";
			_self.error.report = "";
			_self.error.captchaValue = "";
			
			
			var formData = new FormData();
			
			
			formData.append("parameterId", _self.parameterId);
			formData.append("module", _self.module);
		
			if(_self.reportTypeId){
				formData.append("reportTypeId", _self.reportTypeId);
			}
			
			if(_self.reason){
				formData.append("reason", _self.reason);
			}
			//图片
			for(var i=0; i<_self.fileList.length; i++){
				var file = _self.fileList[i];
				
				formData.append("imageFile", file);
			}
			
			//验证码Key
			formData.append("captchaKey", _self.captchaKey);

			//验证码值
			formData.append("captchaValue", _self.captchaValue.trim());

			//令牌
			formData.append("token", _self.$store.state.token);
			
			
			
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/report/add",
				data : formData,
				contentType : false, // 不设置内容类型
				processData : false, // 不处理数据
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
	
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
	
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}
						//提交成功
						if (value_success == "true") {
							_self.show_question = true;//隐藏问题页
							_self.show_answerEditor = false;//隐藏问题富文本编辑器
							_self.show_editAnswerEditor = false;//隐藏修改答案富文本编辑器
							_self.show_editReplyEditor = false;//隐藏修改回复富文本编辑器
							_self.show_report = false;//隐藏举报
							
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.showQuestion(true);//显示问题内容界面
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "reportTypeId") {
											_self.error.reportTypeId = value_error[error];
										} else if (error == "reason") {
											_self.error.reason = value_error[error];
										} else if (error == "imageFile") {
											_self.error.imageFile = value_error[error];
										} else if (error == "report") {
											_self.error.report = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
	
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		
		//回复界面
		addReplyUI : function(answerId) {
			this.popup_reply = true;

			//查询回复页
			this.queryAddReply(answerId);
		},
		//查询回复页
		queryAddReply : function(answerId) {
			var _self = this;

			//清空表单
			_self.replyAnswerId = '';//回复答案Id
			_self.replyContent = ''; //发表回复内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.replyContent = "";
			_self.error.captchaValue = "";
			_self.error.reply = "";
			
			
			var parameter = "&answerId=" + answerId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddAnswerReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						_self.replyAnswerId = answerId;
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_reply = false;
										_self.$toast({
											message : "发表回复功能未开放",
											duration : 3000,
										});
									}	
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addReplyFormScroll);

						});
					}
				}
			});
		},
		
		//添加回复
		addReply : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.replyContent = "";
			_self.error.reply = "";
			_self.error.captchaValue = "";

			
			var parameter = "&answerId=" + _self.replyAnswerId; //提交参数
			if (_self.replyContent != null && _self.replyContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.replyContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answer/addAnswerReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_reply = false;

							
							//清空分页数据
							_self.answerList = []; //答案列表
							
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询答案列表
							_self.queryAnswerList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.replyContent = value_error[error];
										} else if (error == "answerReply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//添加回复对方界面
		addReplyFriendUI : function(reply) {
			this.popup_addReplyFriend = true;

			//添加回复对方页
			this.queryReplyFriend(reply);
		},
		
		//添加回复对方页
		queryReplyFriend : function(reply) {
			var _self = this;

			//清空表单
			_self.friendUser = '';
			_self.addReplyFriendAnswerId = '';//回复对方答案Id
			_self.addReplyFriendId = '';//回复对方Id
			_self.addReplyFriendContent = ''; //回复对方内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.addReplyFriendContent = "";
			_self.error.captchaValue = "";
			_self.error.reply = "";
			
			
			var parameter = "&answerId=" + reply.answerId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddAnswerReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_addReplyFriend = false;
										_self.$toast({
											message : "不允许回复对方",
											duration : 3000,
										});
									}else{
										//对方用户
					                    let friendUser = '';
					                    if(reply.nickname != null && reply.nickname != ''){
					                        friendUser = reply.nickname;
					                    }
					                    if(reply.nickname == null || reply.nickname == ''){
					                        friendUser = reply.account;
					                    }
					                    _self.friendUser = friendUser;
					                    _self.addReplyFriendId = reply.id;
					                    _self.addReplyFriendAnswerId = reply.answerId;
									}
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addReplyFriendFormScroll);

						});
					}
				}
			});
		},
		
		//添加回复对方
		addReplyFriend : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.addReplyFriendContent = "";
			_self.error.reply = "";
			_self.error.captchaValue = "";

			
			
			var parameter = "&answerId=" + _self.addReplyFriendAnswerId; //提交参数
			parameter += "&friendReplyId=" + _self.addReplyFriendId;
			if (_self.addReplyFriendContent != null && _self.addReplyFriendContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.addReplyFriendContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answer/addAnswerReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_addReplyFriend = false;

							
							//清空分页数据
							_self.answerList = []; //答案列表
						
							//查询答案列表
							_self.queryAnswerList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.addReplyFriendContent = value_error[error];
										} else if (error == "friendReplyId") {
											_self.error.reply = value_error[error];
										} else if (error == "reply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//修改回复界面
		editReplyUI : function(replyId) {
			this.popup_editReply = true;

			//修改回复页
			this.queryEditReply(replyId);
		},
		//修改回复页
		queryEditReply : function(replyId) {
			var _self = this;

			//清空表单
			_self.editReplyId = '';//回复Id
			_self.editReplyContent = ''; //修改回复内容
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value


			//清空所有错误
			_self.error.editReplyContent = "";
			_self.error.captchaValue = "";
			_self.error.editReply = "";
			
			
			var parameter = "&replyId=" + replyId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryEditAnswerReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowReply") {
									if(returnValue[key] == false){
										_self.popup_reply = false;
										_self.$toast({
											message : "不允许修改回复",
											duration : 3000,
										});
									}	
								}else if (key == "reply") {
									_self.editReplyId = replyId;
									_self.editReplyContent =  returnValue[key].content;
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.editReplyFormScroll);

						});
					}
				}
			});
		},
		//修改回复
		editReply : function(event) {
		//	if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
		//		return;
		//	}
			var _self = this;
			//清空所有错误
			_self.error.editReplyContent = "";
			_self.error.editReply = "";
			_self.error.captchaValue = "";

			
			var parameter = "&replyId=" + _self.editReplyId; //提交参数
			if (_self.editReplyContent != null && _self.editReplyContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.editReplyContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answer/editReply",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_editReply = false;

							
							//清空分页数据
							_self.answerList = []; //问题列表
							
						//	_self.currentpage = 1; //当前页码
						//	_self.totalpage = 1; //总页数
						//	_self.on = '';//上一页
						//	_self.next = '';//下一页
						
							//查询答案列表
							_self.queryAnswerList();
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.replyContent = value_error[error];
										} else if (error == "reply") {
											_self.error.reply = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		
		//删除回复
		deleteReply : function(replyId) {
			var _self = this;
			_self.$messagebox.confirm('确定删除回复?').then(function (action) {
				var parameter = "&replyId=" + replyId;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/answer/deleteReply",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除回复成功，3秒后自动刷新当前页面",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									//清空分页数据
									_self.answerList = []; //答案列表
									//查询答案列表
									_self.queryAnswerList();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		//采纳答案
		adoptionAnswer : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定采纳?').then(function (action) {
				var parameter = "&answerId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/question/adoptionAnswer",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "采纳当前答案成功，3秒后自动刷新当前页面",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.question.adoptionAnswerId = 1;
									
									//清空分页数据
									_self.answerList = []; //答案列表
									
									//查询答案列表
									_self.queryAnswerList();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		},
		
		
		

		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		
		//初始化
		initialization : function() {
			var questionId = getUrlParam("questionId");//问题Id
			if(questionId != null){
				this.questionId = questionId;
			}
			var answerId = getUrlParam("answerId");//答案Id
			if(answerId != null){
				this.answerId = answerId;
			}
			var replyId = getUrlParam("replyId");//回复Id
			if(replyId != null){
				this.replyId = replyId;
			}
			
			var page = getUrlParam("page");
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询问题
			this.queryQuestion();
			//查询答案列表
			this.queryAnswerList();
			//查询用户是否已经收藏问题
			this.queryAlreadyFavoriteQuestion();
			//查询问题用户收藏总数
			this.queryQuestionFavoriteCount();
		},
		//初始化BScroll滚动插件//this.$refs.addAnswerFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				//preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT|HIDE)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
				//eventPassthrough :'horizontal',//解决文本无法复制
				HWCompositing : true, //是否启用硬件加速
			});
		},
	}
});

//提问题
var addQuestion_component = Vue.extend({
	template : '#addQuestion-template',
	data : function data() {
		return {
			question: '',//问题
			questionTitle:'',//发表问题标题
			questionAmount: '',//悬赏金额
			questionPoint: '',//悬赏积分
			maxDeposit: '',//用户共有预存款
			maxPoint: '',//用户共有积分
			questionRewardPointMin: '',//问题悬赏积分下限
			questionRewardPointMax: '',//问题悬赏积分上限
			questionRewardAmountMin: '',//问题悬赏金额下限
			questionRewardAmountMax: '',//问题悬赏金额上限
			
			
			questionContent:'',//发表问题内容
			maxQuestionTagQuantity:0,//标签最多可选数量
			currentQuestionTagQuantity:0,//标签已选择数量
			
			popup_allTag :false,
			allTagList: '',//全部标签
			selectedFirstTagId: '',//选中的一级标签Id
			childTagList: '',//选中一级标签的子标签集合
			selectedTagList: [],//选中标签对象集合 id:标签Id name:标签名称
			
			
			showCaptcha : false, //发表问题是否显示验证码
			imgUrl : '', //发表问题验证码图片
			captchaKey : '', //发表问题验证码key
			captchaValue : '', //发表问题验证码value
			error : {
				questionTitle : '',
				questionAmount: '',
				questionPoint: '',
				questionContent: '',
				tagId: '',
				captchaValue : '',
				question: '',
			},
			questionEditor:'',//富文本编辑器
		};
	},	
	created : function created() {
		this.queryAddQuestion();
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},
	methods : {
		//显示标签界面
		showTagUI : function() {
			var _self = this;
			_self.popup_allTag = true;
			_self.queryAllTag();
			
		},
		

		//查询所有标签
		queryAllTag : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAllQuestionTag",
				success : function success(result) {
					if (result != "") {
						var tagList = $.parseJSON(result);
						if (tagList != null && tagList.length > 0) {
							_self.allTagList = tagList;
							
							for (var i= 0; i < tagList.length; i++) {
								var questionTag = tagList[i];
								//第一次选中第一个标签
								if(_self.selectedFirstTagId == ''){
									_self.childTagList = questionTag.childTag;
									_self.selectedFirstTagId = questionTag.id;
									break;
								}else{
									if(_self.selectedFirstTagId == questionTag.id){
										_self.childTagList = questionTag.childTag;
										break;
									}
								}
							}
						}
						
					}
					//滚动
					_self.$nextTick(function() {
						if (!_self.scroll) {
							_self.initScroll();
				        }else{
				        	// 复用Bscroll
				        	_self.scroll.refresh();
				        }
						
					});
				}
			});
		},
		//选择子标签
		selectChildTag : function(questionTagId) {
			var _self = this;
			_self.selectedFirstTagId = questionTagId;
			
			if(_self.allTagList != '' && _self.allTagList.length >0){
				for (var i= 0; i < _self.allTagList.length; i++) {
					var questionTag = _self.allTagList[i];
					
					if(questionTagId == questionTag.id){
						_self.childTagList = questionTag.childTag;
						
						//如果只有一个节点，则允许选择本标签
						if(questionTag.childNodeNumber == 0){
							_self.selectedTag(questionTag);
						}

						break;
					}
					
				}
				
			}
			
		},
		//选中标签
		selectedTag : function(childQuestionTag) {
			//判断是否重复选择,如果重复则取消选择
			if(this.selectedTagList != null && this.selectedTagList.length >0){
				for(var i=0; i<this.selectedTagList.length; i++){
					var selectedTag = this.selectedTagList[i];
					if(selectedTag.id == childQuestionTag.id){
						//删除标签
						this.deleteTag(selectedTag.id);
						return;
					}
				}
				
			}
			
			//如果超过最大允许选择数量
			if(this.selectedTagList.length >= this.maxQuestionTagQuantity){
				this.$toast({
					message : "已超过允许选择的最大标签数量",
					duration : 3000,
					className : "mint-ui-toast",
				});
				return;
			}

			var o = new Object();
			o.id = childQuestionTag.id;
			o.name = childQuestionTag.name;

			this.selectedTagList.push(o);
			
			
			
		},
		//删除标签
		deleteTag : function(questionTagId) {
			if(this.selectedTagList != null && this.selectedTagList.length >0){
				for(var i=0; i<this.selectedTagList.length; i++){
					var selectedTag = this.selectedTagList[i];
					if(selectedTag.id == questionTagId){
						this.selectedTagList.splice(i, 1);
						return;
					}
				}
				
			}
		
		},
		//选中一级标签样式
		selectedTagClass : function(questionTagId) {
			var className = "nav-link";
			if(this.selectedFirstTagId == questionTagId){
				className += " active";
				
			}
			if(this.selectedTagList != null && this.selectedTagList.length >0){
				for(var i=0; i<this.selectedTagList.length; i++){
					var selectedTag = this.selectedTagList[i];
					if(selectedTag.id == questionTagId){
						className += " selected";
					}
				}
				
			}
			return className;
		},
		
		//选中二级标签样式
		selectedChildTagClass : function(questionTagId) {
			if(this.selectedTagList != null && this.selectedTagList.length >0){
				for(var i=0; i<this.selectedTagList.length; i++){
					var selectedTag = this.selectedTagList[i];
					if(selectedTag.id == questionTagId){
						return "child-tag selected";
					}
				}
				
			}
			return "child-tag";
		},
		
		
		//查询添加问题页
		queryAddQuestion : function() {
			var _self = this;
			var data = "";
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAddQuestion",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							var availableTag_value = null;
							for (var key in returnValue) {
								if (key == "allowQuestion") {
									if(returnValue[key] == false){
										_self.$toast({
											message : "提问题功能未开放",
											duration : 3000,
										});
									}
								}else if (key == "availableTag") {
									availableTag_value = returnValue[key];
								}else if (key == "maxDeposit") {
									_self.maxDeposit = returnValue[key];
								}else if (key == "maxPoint") {
									_self.maxPoint = returnValue[key];
								}else if (key == "questionRewardPointMin") {
									_self.questionRewardPointMin = parseInt(returnValue[key]);
								}else if (key == "questionRewardPointMax") {
									var questionRewardPointMax_value = returnValue[key];
									if(questionRewardPointMax_value != null){
										_self.questionRewardPointMax = parseInt(returnValue[key]);
									}else{
										_self.questionRewardPointMax = null;
									}
								}else if (key == "questionRewardAmountMin") {
									_self.questionRewardAmountMin = parseFloat(returnValue[key]);
								}else if (key == "questionRewardAmountMax") {
									var questionRewardAmountMax_value = returnValue[key];
									if(questionRewardAmountMax_value != null){
										_self.questionRewardAmountMax = parseFloat(returnValue[key]);
									}else{
										_self.questionRewardAmountMax = null;
									}
								}else if (key == "maxQuestionTagQuantity") {
									_self.maxQuestionTagQuantity = parseInt(returnValue[key]);
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//编辑器图标
							var editorIconList = new Array();
							
							if(availableTag_value != null){//问题编辑器允许使用标签
								var availableTagList = $.parseJSON(availableTag_value);
								for(var i=0; i<availableTagList.length; i++){
									var _availableTag = availableTagList[i];
									
									if(_availableTag == "code"){//代码
										editorIconList.push("code");
									}else if(_availableTag == "forecolor"){//文字颜色
									//	editorIconList.push("foreColor");
									}else if(_availableTag == "hilitecolor"){//文字背景
									//	editorIconList.push("backColor");
									}else if(_availableTag == "bold"){//粗体
										editorIconList.push("bold");
									}else if(_availableTag == "italic"){//斜体
										editorIconList.push("italic");
									}else if(_availableTag == "underline"){//下划线
										editorIconList.push("underline");
									}else if(_availableTag == "link"){//插入链接
										editorIconList.push("link");
									}else if(_availableTag == "emoticons"){//插入表情
										editorIconList.push("emoticon");
									}else if(_availableTag == "image"){//图片
										editorIconList.push("image");
									}
								}
							}
							
							//创建编辑器
							_self.questionEditor = createEditor(_self.$refs.questionContentEditorToolbar,_self.$refs.questionContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/question/upload',_self,"questionContent");
							//_self.questionEditor.txt.clear();//清空
							
						}
						
					}
				}
			});
		},
		//添加问题
		addQuestion : function() {
			var _self = this;
			//清空所有错误
			_self.error.questionTitle = "";
			_self.error.questionAmount = "";
			_self.error.questionPoint = "";
			_self.error.questionContent = "";
			_self.error.tagId = "";
			_self.error.question = "";
			_self.error.captchaValue = "";
			

			var parameter = ""; //提交参数
			if (_self.questionTitle != null && _self.questionTitle != "") {
				parameter += "&title=" + encodeURIComponent(_self.questionTitle);
			}
			if (_self.questionAmount != null && _self.questionAmount != "") {
				parameter += "&amount=" + encodeURIComponent(_self.questionAmount);
			}
			if (_self.questionPoint != null && _self.questionPoint != "") {
				parameter += "&point=" + encodeURIComponent(_self.questionPoint);
			}
			
			if(_self.selectedTagList != null && _self.selectedTagList.length >0){
				for(var i=0; i<_self.selectedTagList.length; i++){
					var selectedTag = _self.selectedTagList[i];
					parameter += "&tagId=" + selectedTag.id;
				}
				
			}
			

			parameter += "&content=" + encodeURIComponent(_self.questionEditor.txt.html());
			
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/question/add",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
						//	console.log(_self.$route.meta);
							//_self.$route.meta.keepAlive=false;
							_self.$toast({
								message : "提交成功，3秒后跳转到问答页",
								duration : 3000,
							});
							setTimeout(function() {
								//跳转到问答页
								_self.$router.push({
									path : '/askList',
								});
							}, 3000);
							
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "title") {
											_self.error.questionTitle = value_error[error];
										}else if (error == "amount") {
											_self.error.questionAmount = value_error[error];
										} else if (error == "point") {
											_self.error.questionPoint = value_error[error];
										} else if (error == "content") {
											_self.error.questionContent = value_error[error];
										} else if (error == "tagId") {
											_self.error.tagId = value_error[error];
										} else if (error == "question") {
											_self.error.question = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
										
										
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		//更换验证码
		replaceCaptcha : function(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},
		//验证验证码
		validation_captchaValue : function() {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		//初始化BScroll滚动插件
		initScroll : function initScroll() {
			this.scroll = new BScroll(this.$refs.questionTagScroll, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				HWCompositing : true, //是否启用硬件加速
			});
		}
	},
});

//追加问题
var appendQuestion_component = Vue.extend({
	template : '#appendQuestion-template',
	data : function data() {
		return {
			questionId:'',//问题Id
			appendQuestionContent:'',//追加问题内容
			showCaptcha : false, //追加问题是否显示验证码
			imgUrl : '', //追加问题验证码图片
			captchaKey : '', //追加问题验证码key
			captchaValue : '', //追加问题验证码value
			error : {
				appendQuestionContent: '',
				captchaValue : '',
				question: '',
			},
			questionEditor:'',//富文本编辑器
		};
	},	
	created : function created() {
		this.questionId = getUrlParam("questionId");//问题Id
		this.queryAppendQuestion();
	},
	methods : {
		//查询追加问题页
		queryAppendQuestion : function() {
			var _self = this;
			var data = "";
			
			data = "&questionId=" + _self.questionId;
			
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryAppendQuestion",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							var availableTag_value = null;
							
							for (var key in returnValue) {
								if (key == "allowQuestion") {
									if(returnValue[key] == false){
										_self.$toast({
											message : "追加问题功能未开放",
											duration : 3000,
										});
									}	
								}else if (key == "availableTag") {
									availableTag_value = returnValue[key];
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							//编辑器图标
							var editorIconList = new Array();
							
							if(availableTag_value != null){//问题编辑器允许使用标签
								var availableTagList = $.parseJSON(availableTag_value);
								for(var i=0; i<availableTagList.length; i++){
									var _availableTag = availableTagList[i];
									
									if(_availableTag == "code"){//代码
										editorIconList.push("code");
									}else if(_availableTag == "forecolor"){//文字颜色
									//	editorIconList.push("foreColor");
									}else if(_availableTag == "hilitecolor"){//文字背景
									//	editorIconList.push("backColor");
									}else if(_availableTag == "bold"){//粗体
										editorIconList.push("bold");
									}else if(_availableTag == "italic"){//斜体
										editorIconList.push("italic");
									}else if(_availableTag == "underline"){//下划线
										editorIconList.push("underline");
									}else if(_availableTag == "link"){//插入链接
										editorIconList.push("link");
									}else if(_availableTag == "emoticons"){//插入表情
										editorIconList.push("emoticon");
									}else if(_availableTag == "image"){//图片
										editorIconList.push("image");
									}else if(_availableTag == "insertfile"){//文件
										editorIconList.push("file");
									}else if(_availableTag == "hidePassword"){//输入密码可见
										editorIconList.push("hidePassword");
									}else if(_availableTag == "hideComment"){//评论话题可见
										editorIconList.push("hideComment");
									}else if(_availableTag == "hideGrade"){//达到等级可见
										editorIconList.push("hideGrade");
									}else if(_availableTag == "hidePoint"){//积分购买可见
										editorIconList.push("hidePoint");
									}else if(_availableTag == "hideAmount"){//余额购买可见
										editorIconList.push("hideAmount");
									}
								}
							}
							
						//	_self.$refs.topicContentEditorToolbar.innerHTML = "";
						//	_self.$refs.topicContentEditorText.innerHTML = _self.topicContent;
							
							
							//创建编辑器
							_self.questionEditor = createEditor(_self.$refs.appendQuestionContentEditorToolbar,_self.$refs.appendQuestionContentEditorText,_self.$store.state.commonPath,editorIconList,null,'user/control/question/upload',_self,"appendQuestionContent");
							_self.questionEditor.txt.html(_self.appendQuestionContent);//初始化内容
							//_self.questionEditor.txt.clear();//清空
							
						}
						
					}
				}
			});
		},
		//保存追加问题
		appendQuestion : function() {
			var _self = this;
			//清空所有错误
			_self.error.appendQuestionContent = "";
			_self.error.captchaValue = "";

			var parameter = "&questionId="+ _self.questionId; //提交参数
			parameter += "&content=" + encodeURIComponent(_self.questionEditor.txt.html());
			
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/question/appendQuestion",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							setTimeout(function() {
								//跳转到问答内容页
								_self.$router.push({
									path : '/question',
									query : {
										questionId : _self.questionId
									}
								});
								
							}, 3000);
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "content") {
											_self.error.appendQuestionContent = value_error[error];
										} else if (error == "question") {
											_self.error.question = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		//更换验证码
		replaceCaptcha : function(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},
		//验证验证码
		validation_captchaValue : function() {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
	},
});


//搜索框
var searchBar_component = Vue.extend({
	template : '#searchBar-template',
	data : function data() {
		return {
			keyword : '', //搜索内容
		};
	},
	created : function created() {
		var keyword = getUrlParam("keyword");
		if (keyword != null && keyword != "") {
			this.keyword = decodeURIComponent(keyword);
		}
	},
	methods : {
		//提交搜索
		submitSearch : function() {
			if (this.keyword != null && this.keyword != "") {
				this.$refs.searchInput.$refs.input.blur();//取消焦点，让软键盘隐藏
				this.$router.push({
					path : '/search',
					query : {
						keyword : this.keyword
					}
				});
			}

		},
	}
});
Vue.component('searchbar-component', searchBar_component); //组件名必须小写

//搜索列表
var search_component = Vue.extend({
	template : '#search-template',
	data : function data() {
		return {
			keyword : '', //搜索内容
			searchResultList : [], //搜索结果列表
			currentpage : 1, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		};
	},
	created : function created() {
		//初始化
		this.initialization();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		//搜索话题
		searchTopic : function() {
			var _self = this;
			if(_self.keyword == ""){
				return;
			}
			var data = "&keyword=" + (encodeURIComponent(_self.keyword)); //提交参数
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "search",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						
						var value_success = null;
						var pageView = null;
						var value_error = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							}else if (key == "searchResultPage") {
								pageView = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							}
						}
						
						if (value_success == "true") {
							_self.searchResultList = pageView.records;
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
							if(pageView.currentpage != 1){
								_self.on = pageView.currentpage-1;
							}else{
								_self.on = '';
							}
							if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
								
								_self.next = pageView.currentpage+1;
							}else{
								_self.next = '';
							}
							
							
							//生成首字符头像
							_self.$nextTick(function() {
								if (_self.searchResultList != null &&_self.searchResultList.length > 0) {
									for(var i=0;i<_self.searchResultList.length; i++){
										var searchResult = _self.searchResultList[i];
										if(searchResult.indexModule == 10){
											if(searchResult.topic.avatarName == null || searchResult.topic.avatarName == ''){
												var char = (searchResult.topic.nickname != null && searchResult.topic.nickname !="") ? searchResult.topic.nickname : searchResult.topic.account;
												//元素的实际宽度
												var width= _self.$refs['searchResultTopicAvatarData_'+searchResult.topic.id][0].offsetWidth;
												_self.$refs['searchResultTopicAvatarData_'+searchResult.topic.id][0].src = letterAvatar(char, width);	
											}
											
										}else if(searchResult.indexModule == 20){
											if(searchResult.question.avatarName == null || searchResult.question.avatarName == ''){
												var char = (searchResult.question.nickname != null && searchResult.question.nickname !="") ? searchResult.question.nickname : searchResult.question.account;
												//元素的实际宽度
												var width= _self.$refs['searchResultQuestionAvatarData_'+searchResult.question.id][0].offsetWidth;
												_self.$refs['searchResultQuestionAvatarData_'+searchResult.question.id][0].src = letterAvatar(char, width);	
											}
											
										}
									}
								}
							});
							
							
							
						} else {
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									count++;
									htmlContent += count + ". " + errorValue + "<br>";
								}
								_self.$messagebox('提示', htmlContent);
							}
						}
						

					}
				}
			});

			

		},
		//初始化数据
		initialization : function() {
			var keyword = getUrlParam("keyword");
			if (keyword != null && keyword != "") {
				this.keyword = decodeURIComponent(keyword);
			}
			var page = getUrlParam("page");
			if (page != null && page != "") {
				this.currentpage = decodeURIComponent(page);
			}
			//搜索话题和问题
			this.searchTopic();
			
		},

	}
});


//帮助中心
var help_component = Vue.extend({
	template : '#help-template',
	data : function data() {
		return {
			helpTypeList: '',//帮助分类
		};
	},
	created : function created() {
		this.queryHelpTypeList();
	},
	methods : {
		//查询帮助分类
		queryHelpTypeList : function() {
			var _self = this;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryHelpTypeList",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null && returnValue.length > 0) {
							_self.helpTypeList = returnValue;
						}
					}
				}
			});
		},
	},
});

//帮助内容
var helpDetail_component = Vue.extend({
	template : '#helpDetail-template',
	data : function data() {
		return {
			helpTypeId : '',//帮助分类Id
			helpId : '',//帮助Id
			help : '', //帮助
			helpList : '', //帮助列表
			helpNavigationName : '', //帮助导航名称(导航最后一个节点名称)
			show_helpDetail:true,//帮助内容显示/隐藏
			show_helpList:false,//帮助列表显示/隐藏
			
			pictureOptions: {//放大图片配置
		        url: 'src',//定义从何处获取原始图像URL进行查看
		        navbar : true, //导航栏
		        title : false, //标题
		        toolbar : false, //工具栏
		        loop:false, //是否启用循环查看
		        filter : function(image) {
		        	return image.dataset.enable != 'false';//将不允许放大的表情图片过滤
		        },
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
		    
		    playerIdList: [],//视频播放Id列表
		    playerObjectList: [],//视频播放对象集合
		    playerNodeList: [],//视频节点对象集合
		};
	},
	created : function created() {
		//初始化
		this.initialization();
	},
	mounted : function mounted(){
		//挂载完成后，判断浏览器是否支持popstate
		if (window.history && window.history.pushState) {//监听浏览器前进后退按钮
			
			window.addEventListener('popstate', this.goBack, false);
		}
	},
	destroyed : function destroyed() {//离开当前页面
		this.showHelp(false);
		//页面销毁时，取消监听
		window.removeEventListener('popstate', this.goBack, false);
	},
	deactivated: function() {//keep-alive组件停用时调用 (keepAlive缓存机制才会触发的钩子函数)
		this.showHelp(false);
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	beforeDestroy : function() {
		for(var i =0; i<this.playerObjectList.length; i++){
			var playerObject = this.playerObjectList[i];
			playerObject.destroy();//销毁播放器
			
		}
	},
	computed: {
		//动态解析模板数据
		analyzeDataComponent: function analyzeDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					mounted :function () {
						this.resumePlayerNodeData();
					},
					props: this.$options.props, // re-use current props definitions
					methods: {
						//恢复播放器节点数据(vue组件切换时会自动刷新数据，视频播放器框在组件生成数据内容之后插入，组件刷新数据时播放器框会消失，组件刷新后需要用之前的节点数据恢复)
				        resumePlayerNodeData : function(){
				        	var _self = this;
				        	_self.$nextTick(function() {
					        	if(_self.$parent.playerObjectList.length >0){
					        		for(var i=0; i< _self.$parent.playerNodeList.length; i++){
					        			var playerNode = _self.$parent.playerNodeList[i];
					        			var playerId = playerNode.getAttribute("id");
					        			var node = document.getElementById(playerId);
					        			if(node != null){
					        				node.parentNode.replaceChild(playerNode,node);
					        			}
					        			
					        		}
					        	}
				        	});
				        }
				    }
				};
			};	
		},	
	},
	methods : {
		//显示帮助内容界面
		showHelp : function(back){
		    this.show_helpDetail = true;//显示问题页
		    this.show_helpList = false;//隐藏帮助列表
		    
		    //判断浏览器是否支持popstate
			if (window.history && window.history.pushState && back) {
				//后退
				history.go(-1);
			}
		},
		//显示帮助列表界面
		showHelpList : function(){
		    this.show_helpDetail = false;//隐藏问题页
		    this.show_helpList = true;//显示帮助列表
		},
		
		
		//查询帮助
		queryHelp : function() {
			var _self = this;

			var data = "helpId=" + _self.helpId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryHelpContent",
				data : data,
				success : function success(result) {
					if (result != "") {
						var help = $.parseJSON(result);
						if (help != null) {
							//清空播放器
							_self.clearVideoPlayer();
							
							//处理隐藏标签
							var contentNode = document.createElement("div");
							contentNode.innerHTML = help.content;
							
							_self.bindNode(contentNode);
							help.content = escapeVueHtml(contentNode.innerHTML);
					
							
							_self.help = help;
		
							
							
							_self.$nextTick(function() {
								setTimeout(function() {
									_self.renderVideoPlayer();//渲染视频播放器
								}, 30);
								
								
							});
							
						}
					}
				}
			});
			
		},

		//清空播放器
		clearVideoPlayer : function() {
			var _self = this;
			
			for(var i=0; i< _self.playerObjectList.length; i++){
				var playerObject = _self.playerObjectList[i];
				
				playerObject.destroy();//销毁播放器
			}
			_self.playerObjectList.length = 0;//清空数组
			_self.playerIdList.length = 0;//清空数组
			_self.playerNodeList.length = 0;//清空数组
		},
		//渲染视频播放器
		renderVideoPlayer : function() {
			var _self = this;
			
			
			
			
			for(var i=0; i< _self.playerIdList.length; i++){
				var playerId = _self.playerIdList[i];
				var url = document.getElementById(playerId).getAttribute("url");
        		var cover = document.getElementById(playerId).getAttribute("cover");//封面
        		var thumbnail = document.getElementById(playerId).getAttribute("thumbnail");//缩略图
				
        		if(url == ""){//如果视频处理中
        			var dp = new DPlayer({
            			container: document.getElementById(playerId),//播放器容器元素
            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
            			
            			video: {
            			    
            			}
            		});
					var dom = document.createElement('div');
					dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
					document.getElementById(playerId).appendChild(dom);
				}else{
					if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
	        			var dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url,
	            			    type: 'hls',
	            			    pic: cover,//视频封面
	            			    thumbnails: thumbnail//视频预览图
	            			}
	            		});
	    				
	        		}else{
	        			var dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url
	            			}
	            		});
	        		}
					
				}
				_self.playerObjectList.push(dp);
			}
			
			
			//添加播放器节点数据
			if(_self.playerObjectList.length >0){
				
				for(var i=0; i< _self.playerIdList.length; i++){
			    	var playerId = _self.playerIdList[i];
			    	var node = document.getElementById(playerId);//节点对象
			    	_self.playerNodeList.push(node);
			    }
			}
			
		},
				
		//递归绑定节点参数
		bindNode : function(node) {
			//先找到子节点
	        var nodeList = node.childNodes;
	        for(var i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	        	var childNode = nodeList[i];
	            var random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		var src = childNode.getAttribute("src");
	            		
	            		if(childNode.getAttribute("width") == null){//不是表情图片
	            			childNode.setAttribute("key",random+"_"+i);
	            		}else{
	            			childNode.setAttribute("data-enable","false");//标记不显示放大图
	            		}
	            		
	            		//延迟加载 表情图片也使用<img>标签，也执行延迟加载
	        			childNode.setAttribute("src",this.$store.state.commonPath+'images/null.gif');
	        			childNode.setAttribute("data-src",src);
	            	}
	            	//处理视频标签
	            	if(childNode.nodeName.toLowerCase() == "player" ){
	            		
	            		var id = "player_"+random+"_"+i;
	            		childNode.setAttribute("id",id);//设置Id
	            		this.playerIdList.push(id);	
	            	}
	            	
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		var pre_html = childNode.innerHTML;
	            		var class_val = childNode.className;
	            		var lan_class = "";
	            		
	        	        var class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(var k=0; k<class_arr.length; k++){
	        	        	var className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	            		
	        	        var nodeHtml = "";

	        			//删除code节点
	        			var preChildNodeList = childNode.childNodes;
	        			for(var p = 0;p < preChildNodeList.length;p++){
	        				var preChildNode = preChildNodeList[p];
	        				if(preChildNode.nodeName.toLowerCase() == "code" ){
	        					nodeHtml += preChildNode.innerHTML;
	        					preChildNode.parentNode.removeChild(preChildNode);
	            			}
	        				
	        			}
	        			
	        			var dom = document.createElement('code');
	        			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				Prism.highlightElement(dom);
	            		
	            	}
	            	this.bindNode(childNode);
	            }
	        }
		},

		//查询帮助列表
		queryHelpList : function(callback) {
			var _self = this;
			var data = "helpTypeId=" + _self.helpTypeId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryHelpList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null && returnValue.length > 0) {
							_self.helpList = returnValue;
						}
						callback();
					}
				}
			});
		},
		
		//查询帮助导航
		queryHelpNavigation : function() {
			var _self = this;
			var data = "helpTypeId=" + _self.helpTypeId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryHelpNavigation",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						
						for(var key in returnValue){
							_self.helpNavigationName = returnValue[key];
						}
					}
				}
			});
		},
		
		
		
		
		
		//初始化
		initialization : function() {
			var _self = this;
			var helpTypeId = getUrlParam("helpTypeId");//帮助分类Id
			if(helpTypeId != null){
				_self.helpTypeId = helpTypeId;
			}
			var helpId = getUrlParam("helpId");//帮助Id
			if(helpId != null){
				_self.helpId = helpId;
			}
			//查询帮助列表
			_self.queryHelpList(function (){
				if(helpId == null || helpId == ''){
					//查询第一个帮助
					if(_self.helpList != null && _self.helpList.length >0){
						for(var i=0; i<_self.helpList.length; i++){
							var help = _self.helpList[i];
							
							_self.helpId = help.id;
							break;
						}
					}
				}
				//查询帮助
				_self.queryHelp();
				
			});
			//查询帮助导航
			_self.queryHelpNavigation();
		},
		
	}
	
	
});



//注册组件
var register_component = Vue.extend({
	template : '#register-template',


	data : function() {
		return {
			registerType_tab_10 : 'active',//注册类型选中样式
			registerType_tab_20 : '',//注册类型选中样式
			
			account_field : true,//表单账号
			mobile_field : false,//表单手机号字段
			issue_field : true,//表单密码提示问题字段
			answer_field : true,//表单密码提示答案字段
			email_field : true,//表单邮箱字段
			smsCode_field : false,//表单手机号验证码字段
			
			type : 10,//用户类型
			userCustomList : [], //用户自定义注册功能项
			allowRegisterAccount : '',//允许注册账号类型
			captchaKey : '', //验证码编号
			captchaValue : '',
			showCaptcha : false,
			imgUrl : '',
			account : '', //账号
			password : '', //密码
			confirmPassword : '', //确认密码
			issue : '', //密码提示问题
			answer : '', //密码提示答案
			email : '', //邮箱
			smsCode: '', //手机验证码
			userBoundField : [], //用户自定义注册功能项绑定
			mobile : '', //手机号
			successInfo:'', //验证码发送成功信息
			time: 0,//倒计时间
			button: {//获取短信校验码按钮
				disabled : false,
				text : '获取短信校验码',
			},
			error : {
				account : '', //账号
				password : '', //密码
				confirmPassword : '', //确认密码
				issue : '', //密码提示问题
				answer : '', //密码提示答案
				email : '', //邮箱
				captchaValue : '', //验证码值
				register: '', //注册错误	
				mobile : '',//手机号
				smsCode : ''//手机验证码
			},
			customError : [], //用户自定义注册功能项错误提示	

			agreement : true, //是否同意服务协议
			allowSubmit:false,//提交按钮disabled状态
		};
	},
	created : function() {
		if(isWeiXinBrowser() && this.$store.state.weixin_oa_appid != ''){
			//处理微信公众号自动登录
			var weixin_openid_value = sessionStorage.getItem("weixin_openid"); 
			if(weixin_openid_value == null || weixin_openid_value == ""){//如果来自微信内置浏览器
				
				//获取微信openid
				getWeiXinOpenId();

				sessionStorage.setItem("pushState", "true"); //标记添加URL记录
			}
		}
		
		
		this.initialization();
	},
	methods : {
		
		//选择注册用户类型
		selectRegisterAccountType : function(type) {
			if(type == 10){
				this.type =10;//用户类型
				this.registerType_tab_10 = "active";
				this.registerType_tab_20 = "";
				
				this.account_field = true;
				this.mobile_field = false;
				this.issue_field = true;
				this.answer_field = true;
				this.email_field = true;
				this.smsCode_field = false;
			}else if(type == 20){
				this.type =20;//用户类型
				this.registerType_tab_10 = "";
				this.registerType_tab_20 = "active";
				
				this.account_field = false;
				this.mobile_field = true;
				this.issue_field = false;
				this.answer_field = false;
				this.email_field = false;
				this.smsCode_field = true;
			}
			
		},
		//获取短信验证码
		getSmsCode : function () {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			//设置按钮禁用状态
			_self.button.disabled = true;
			
			
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			parameter += "&module=100";
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "smsCode",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							//倒计时
							_self.countdown();
							_self.successInfo = "短信验证码已发送";	
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}	
						}
					}
				}
			});
		},
		//倒计时
		countdown : function () {
			var _self = this;
			_self.time = 60;
			var interval = window.setInterval(function() {
				_self.time--;
				_self.button.disabled = true;
				_self.button.text="重新发送("+_self.time+")";
				
				if(_self.time <= 0) {
					window.clearInterval(interval);
					_self.button.disabled = false;
					_self.button.text="获取短信校验码";
				}
			}, 1000);

		},		
		//查询注册页
		queryRegister : function() {
			var _self = this;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "register",
				success : function success(result) {
					var returnValue = $.parseJSON(result);
					for (var key in returnValue) {
						if (key == "userCustomList") {
							var userCustomList = returnValue[key];
							_self.userCustomList = userCustomList;
							if (userCustomList != null && userCustomList.length > 0) {
								for (var i = 0; i < userCustomList.length; i++) {
									var userCustom = userCustomList[i];
									_self.boundField(userCustom);
									_self.customError.push('');
								}

							}
						} else if (key == "allowRegisterAccount") {
							_self.allowRegisterAccount = returnValue[key];
							
							if(_self.allowRegisterAccount.local){
								_self.selectRegisterAccountType(10);
								
							}else{
								if(_self.allowRegisterAccount.mobile){
									_self.selectRegisterAccountType(20);
								}
							}
							
						
						}else if (key == "captchaKey") {
							var captchaKey = returnValue[key];
							_self.captchaKey = captchaKey;

							if (captchaKey != '') {
								_self.showCaptcha = true;
								_self.imgUrl = "captcha/" + captchaKey + ".jpg";
							}


						}
					}
				}
			});

		},
		//单选项/复选框/下拉菜单 选项内容
		checkbox : function checkbox(userCustom) {
			var options = new Array();

			for (var itemValue in userCustom.itemValue) {
				var o = new Object();
				o.label = userCustom.itemValue[itemValue];
				o.value = itemValue;

				options.push(o);
			}
			return options;
		},
		//绑定字段
		boundField : function boundField(userCustom) {
			if (userCustom.chooseType == 1) { //文本框
				var content = "";
				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					content = userCustom.userInputValueList[i].content;
				}
				this.userBoundField.push(content);
			} else if (userCustom.chooseType == 2) { //单选框
				var checked = "";

				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					var userInputValue = userCustom.userInputValueList[i];
					checked = userInputValue.options;

				}
				//默认选第一项 
				if (checked == "") {
					for (var itemValue in userCustom.itemValue) {
						checked = itemValue;
						break;
					}
				}
				this.userBoundField.push(checked);
			} else if (userCustom.chooseType == 3) { //多选框
				var checked = new Array();

				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					var userInputValue = userCustom.userInputValueList[i];
					checked.push(userInputValue.options);
				}
				this.userBoundField.push(checked);
			} else if (userCustom.chooseType == 4) { //下拉列表
				var checked = new Array();

				A:for (var itemValue in userCustom.itemValue) {
					for (var i = 0; i < userCustom.userInputValueList.length; i++) {
						var userInputValue = userCustom.userInputValueList[i];
						if (itemValue == userInputValue.options) {
							var o = new Object();
							o.label = userCustom.itemValue[itemValue];
							o.value = itemValue;
							checked.push(o);
							continue A;
						}
					}
				}
				this.userBoundField.push(checked);
				/**
				if (userCustom.multiple == true) { //允许多选
					var checked = new Array();

					A:
					for (var itemValue in userCustom.itemValue) {
						for (var i = 0; i < userCustom.userInputValueList.length; i++) {
							var userInputValue = userCustom.userInputValueList[i];
							if (itemValue == userInputValue.options) {
								var o = new Object();
								o.label = userCustom.itemValue[itemValue];
								o.value = itemValue;
								checked.push(o);
								continue A;
							}
						}
					}
					this.userBoundField.push(checked);

				} else {
					var o = new Object();

					A:
					for (var itemValue in userCustom.itemValue) {
						for (var i = 0; i < userCustom.userInputValueList.length; i++) {
							var userInputValue = userCustom.userInputValueList[i];
							if (itemValue == userInputValue.options) {
								o.label = userCustom.itemValue[itemValue];
								o.value = itemValue;
								break A;
							}
						}
					}
					this.userBoundField.push(o);
				}
				**/

			} else if (userCustom.chooseType == 5) { //文本域
				var content = "";
				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					content = userCustom.userInputValueList[i].content;
				}
				this.userBoundField.push(content);
			}
		},
		//提交用户
		addUser : function() {
			var _self = this;
			_self.allowSubmit = true;//提交按钮disabled状态
			
			//清除错误
			_self.error.account = '';//账号
			_self.error.password = ''; //密码
			_self.error.confirmPassword  = '';//确认密码
			_self.error.issue = '';//密码提示问题
			_self.error.answer = '';//密码提示答案
			_self.error.email = '';//邮箱
			_self.error.captchaValue  = '';//验证码值
			_self.error.register = '';//注册错误
			_self.customError =[];//用户自定义注册功能项错误提示
			_self.error.smsCode = ''; //手机验证码
			_self.error.mobile = '' //手机号

			if (_self.agreement == false) { //如果不同意服务协议
				_self.$messagebox('提示', '必须同意本站服务协议才能注册用户');
				return;
			}


			//验证密码
			if (_self.verificationPassword() == false) {
				_self.$messagebox('提示', '请填好资料再提交');
				return;
			}

			var parameter = "";
			
			if(_self.type == 10){//10:本地账号密码用户
				parameter += "&type=10";
				
				//账号
				var account = _self.account;
				if (account != "") {
					parameter += "&account=" + encodeURIComponent(account);
				}
				
			}else if(_self.type == 20){//20: 手机用户
				parameter += "&type=20";
				
				//手机号
				var mobile = _self.mobile;
				if (mobile != "") {
					parameter += "&mobile=" + encodeURIComponent(mobile);
				}
				//手机验证码
				var smsCode = _self.smsCode;
				if (smsCode != "") {
					parameter += "&smsCode=" + encodeURIComponent(smsCode);
				}
				
			}
			
			//密码需SHA256加密
			var password = _self.password.trim();
			if (password != "") { //密码
				parameter += "&password=" + CryptoJS.SHA256(password);
			}

			//密码提示问题
			var issue = _self.issue;
			if (issue != "") {
				parameter += "&issue=" + encodeURIComponent(issue);
			}
			//密码提示答案
			var answer = _self.answer;
			if (answer != "") {
				parameter += "&answer=" + CryptoJS.SHA256(answer);
			}

			//邮箱
			var email = _self.email;
			if (email != "") {
				parameter += "&email=" + encodeURIComponent(email);
			}

			//自定义表单
			if (_self.userCustomList != null && _self.userCustomList.length > 0) {
				for (var i = 0; i < _self.userCustomList.length; i++) {
					var userCustom = _self.userCustomList[i];

					var fieldValue = _self.userBoundField[i];

					if (userCustom.chooseType == 1) { //文本框
						parameter += "&userCustom_" + userCustom.id + "=" + encodeURIComponent(fieldValue);
					} else if (userCustom.chooseType == 2) { //单选框
						parameter += "&userCustom_" + userCustom.id + "=" + fieldValue;

					} else if (userCustom.chooseType == 3) { //多选框
						for (var value in fieldValue) {
							parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value];
						}
					} else if (userCustom.chooseType == 4) { //下拉列表
						if (userCustom.multiple == true) { //允许多选
							for (var value in fieldValue) {
								parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value].value;
							}
						}else{
							for (var value in fieldValue) {
								parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value];
							}
						}
					} else if (userCustom.chooseType == 5) { //文本域
						parameter += "&userCustom_" + userCustom.id + "=" + encodeURIComponent(fieldValue);
					}
				}
			}
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			
			if(isWeiXinBrowser() && sessionStorage.getItem("weixin_openid") != null){//如果来自微信内置浏览器
				//微信openid
				parameter += "&thirdPartyOpenId=" + sessionStorage.getItem("weixin_openid"); 
			}
			
			//令牌
			parameter += "&token=" + _self.$store.state.token;

			//alert(parameter);

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "register",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_jumpUrl = "";
						var value_captchaKey = null;
						var value_systemUser = null;//登录用户
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "jumpUrl") {
								value_jumpUrl = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							} else if (key == "systemUser") {
								value_systemUser = returnValue[key];
							}
						}
						if (value_success == "true") { //成功
							//设置登录用户
							_self.$store.commit('setSystemUserId', value_systemUser.userId);
							_self.$store.commit('setSystemUserName', value_systemUser.userName);
							
							_self.$toast({
								message : "注册成功，3秒后自动跳转到首页",
								duration : 3000,
							});
							setTimeout(function() {
								//跳转到首页
								window.location.href = _self.$store.state.baseURL + "index";
							}, 3000);

						} else { //失败
							for (var error in value_error) {
								var errorValue = value_error[error];
								if (error == "account") { //账号
									_self.error.account = errorValue;
								} else if (error == "password") { //密码
									_self.error.password = errorValue;
								} else if (error == "issue") { //密码提示问题
									_self.error.issue = errorValue;
								} else if (error == "answer") { //密码提示答案
									_self.error.answer = errorValue;
								} else if (error == "email") { //邮箱
									_self.error.email = errorValue;
								} else if (error == "register") { //注册错误
									_self.error.register = errorValue;
								} else if (error == "captchaValue") { //验证码错误
									_self.error.captchaValue = errorValue;
								} else if (error == "smsCode") { //手机验证码
									_self.error.smsCode = errorValue;
								} else if (error == "mobile") { //手机
									_self.error.mobile = errorValue;
								}else {
									var number = error.split("_")[1];
									for (var i = 0; i < _self.userCustomList.length; i++) {
										var userCustom = _self.userCustomList[i];
										if (userCustom.id == number) {
											_self.$set(_self.customError, i, errorValue);
											break;
										}
									}
								}
							}
							
							_self.allowSubmit = false;//提交按钮disabled状态
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}

						}

					}
				}
			});
		},
		//验证账号
		verificationUserName : function() {
			var _self = this;
			var parameter = "&account=" + encodeURIComponent(_self.account);
			_self.error.account = "";

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "userVerification",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						if (result == "true") {
							_self.error.account = "账号已存在";
						}
					}
				}
			});
		},
		//验证密码
		verificationPassword : function() {
			var flag = true;
			var password = this.password.trim();
			var confirmPassword = this.confirmPassword.trim();
			if (password != "" || confirmPassword != "") {
				if (password != null) {
					if (password.length < 6) {
						this.error.password = "密码长度不能小于6位";
						flag = false;
					}
					if (password.length > 20) {
						this.error.password = "密码长度不能大于20位";
						flag = false;
					}

				} else {
					this.error.password = "密码不能为空";
					flag = false;
				}

				if (confirmPassword != "") {
					if (password != confirmPassword) {
						this.error.confirmPassword = "两次密码不相等";
						flag = false;
					}

				} else {
					this.error.confirmPassword = "确认密码不能为空";
					flag = false;
				}

			}
			return flag;
		},
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		//清除所有错误
		clearError : function() {
			for (var e in this.error) {
				this.error[e] = '';
			}
			this.customError = [];
		},
		//初始化数据
		initialization : function() {
			//查询注册页
			this.queryRegister();
		},
	}
});


//服务协议
var agreement_component = Vue.extend({
	template : '#agreement-template',
	data : function data() {
		return {
			content : '', //服务协议内容
		};
	},
	created : function created() {
		this.queryAgreement();
	},
	methods : {
		//查询服务协议内容
		queryAgreement : function() {
			var _self = this;
			;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "termsService",
				success : function success(result) {
					if (result != "") {
						var customHTML = $.parseJSON(result);
						_self.content = customHTML.content;
					}
				}
			});
		},
	}
});


//找回密码第一步组件
var findPassWord_step1_component = Vue.extend({
	template : '#findPassWord_step1-template',
	data : function data() {
		return {
			type_tab_10 : 'active',//用户类型选中样式
			type_tab_20 : '',//用户类型选中样式
			
			account_field : true,//表单账号字段
			mobile_field : false,//表单手机号字段
			
			type : 10,//用户类型
			mobile : '',//手机号
			account : '',
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				mobile : '',//手机号
				account : '',
				captchaValue : ''
			},
			
			allowSubmit:false,//提交按钮disabled状态
		};
	},
	created : function created() {
		this.queryFindPassWord_step1();
	},
	methods : {
		//选择用户类型
		selectAccountType : function(type) {
			if(type == 10){
				this.type =10;//用户类型
				this.type_tab_10 = "active";
				this.type_tab_20 = "";
				
				this.account_field = true;
				this.mobile_field = false;
			}else if(type == 20){
				this.type =20;//用户类型
				this.type_tab_10 = "";
				this.type_tab_20 = "active";
				
				this.account_field = false;
				this.mobile_field = true;
			}
			
		},
		
		//查询找回密码第一步
		queryFindPassWord_step1 : function() {
			var _self = this;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "findPassWord/step1",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						for (var key in returnValue) {
							if (key == "captchaKey") {
								var captchaKey = returnValue[key];
								_self.captchaKey = captchaKey;
								_self.imgUrl = "captcha/" + captchaKey + ".jpg";
							}
						}
					}
				}
			});
		},

		//提交数据
		submitData : function() {
			var _self = this;
			
			_self.allowSubmit = true;//提交按钮disabled状态
			//清除所有错误
			_self.clearError();

			var parameter = "";

			if(_self.type == 10){//10:本地账号密码用户
				parameter += "&type=10";
				
				//账号
				var account = _self.account;
				if (account != "") {
					parameter += "&account=" + encodeURIComponent(account);
				}
				
			}else if(_self.type == 20){//20: 手机用户
				parameter += "&type=20";
				
				//手机号
				var mobile = _self.mobile;
				if (mobile != "") {
					parameter += "&mobile=" + encodeURIComponent(mobile);
				}
				
			}

			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;


			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "findPassWord/step1",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_jumpUrl = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "jumpUrl") {
								//跳转URL
								value_jumpUrl = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//成功
						if (value_success == "true") {
							//跳转
							if (value_jumpUrl != null) {
								_self.$router.push({
									path : '/' + value_jumpUrl
								});
							}
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "account") {
											_self.error.account = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
							_self.allowSubmit = false;//提交按钮disabled状态
							if (value_captchaKey != null) {
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});

		},


		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
	},
});
//找回密码第二步组件
var findPassWord_step2_component = Vue.extend({
	template : '#findPassWord_step2-template',
	data : function data() {
		return {
			account_field : true,//表单账号字段
			mobile_field : false,//表单手机号字段
			issue_field : true,//表单密码提示问题字段
			answer_field : true,//表单密码提示答案字段
			smsCode_field : false,//表单手机号验证码字段

			type : 10,
			userName : '',
			account : '',
			mobile: '',
			issue : '',
			answer : '',
			password : '', //新密码
			confirmPassword : '', //确认密码
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				mobile : '',//手机号
				smsCode : '',//手机验证码
				account : '',
				password : '',
				confirmPassword : '',
				answer : '',
				captchaValue : ''
			},
			smsCode:'',//手机号
			successInfo:'', //验证码发送成功信息
			time: 0,//倒计时间
			button: {//获取短信校验码按钮
				disabled : false,
				text : '获取短信校验码',
			},
			allowSubmit:false,//提交按钮disabled状态
			allowSmsCodeSubmit:false,//提交按钮disabled状态
		};
	},
	created : function created() {
		//获取用户名
		var userName = getUrlParam("userName");
		if (userName != null && userName != "") {
			this.userName = userName;
		}
		
		//获取手机号
		var mobile = getUrlParam("mobile");
		if (mobile != null && mobile != "") {
			this.mobile = mobile;
		}
		this.queryFindPassWord_step2();
	},
	methods : {
		//查询找回密码第二步
		queryFindPassWord_step2 : function() {
			var _self = this;
			var parameter = "&userName=" + _self.userName;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "findPassWord/step2",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_user = null;
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "user") {
								value_user = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}
						//成功
						if (value_success == "true") {
							_self.issue = value_user.issue;
							_self.type = value_user.type;
							_self.account = value_user.account;
							
							if(_self.type == 10){
								_self.account_field = true;
								_self.mobile_field = false;
								_self.issue_field = true;
								_self.answer_field = true;
								_self.smsCode_field = false;
							}else if(_self.type == 20){
								_self.account_field = false;
								_self.mobile_field = true;
								_self.issue_field = false;
								_self.answer_field = false;
								_self.smsCode_field = true;
							}
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "account") {
											_self.error.account = value_error[error];
										}
									}
								}
							}
						}
						if (value_captchaKey != null) {
							_self.captchaKey = value_captchaKey;
							_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
							//设置验证码图片
							_self.replaceCaptcha();
						}
					}
				}
			});
		},

		//提交数据
		submitData : function() {
			var _self = this;
			
			_self.allowSubmit = true;//提交按钮disabled状态
			
			//清除所有错误
			_self.clearError();
			//验证密码
			if (_self.verificationPassword() == false) {
				_self.$messagebox('提示', '请填好资料再提交');
				return;
			}
			var parameter = "";
			
			if(_self.type == 10){//10:本地账号密码用户
				parameter += "&type=10";		
			}else if(_self.type == 20){//20: 手机用户
				parameter += "&type=20";
				//手机验证码
				var smsCode = _self.smsCode;
				if (smsCode != "") {
					parameter += "&smsCode=" + encodeURIComponent(smsCode);
				}
				
			}
			
			//用户名
			var userName = _self.userName;
			if (userName != "") {
				parameter += "&userName=" + encodeURIComponent(userName);
			}


			//密码需SHA256加密
			var password = _self.password.trim();
			if (password != "") { //密码
				parameter += "&password=" + CryptoJS.SHA256(password);
			}

			//密码提示答案
			var answer = _self.answer;
			if (answer != "") {
				parameter += "&answer=" + CryptoJS.SHA256(answer);
			}

			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;


			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "findPassWord/step2",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//成功
						if (value_success == "true") {
							//跳转
							_self.$toast({
								message : "找回密码成功，3秒后自动跳转到登录页",
								duration : 3000,
							});
							setTimeout(function() {
								_self.$router.push({
									path : '/login'
								});
							}, 3000);


						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "userName") {
											_self.error.userName = value_error[error];
										} else if (error == "answer") {
											_self.error.answer = value_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "smsCode") {
											_self.error.smsCode = value_error[error];
										}else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
							_self.allowSubmit = false;//提交按钮disabled状态
							if (value_captchaKey != null) {
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});

		},

		//验证参数
		verificationPassword : function verification() {
			var flag = true;
			var password = this.password.trim();
			var confirmPassword = this.confirmPassword.trim();

			if (password != null && password != "") {
				if (password.length < 6) {
					this.error.password = "密码长度不能小于6位";
					flag = false;
				}
				if (password.length > 20) {
					this.error.password = "密码长度不能大于20位";
					flag = false;
				}

			} else {
				this.error.password = "密码不能为空";
				flag = false;
			}

			if (confirmPassword != "") {
				if (password != confirmPassword) {
					this.error.confirmPassword = "两次密码不相等";
					flag = false;
				}

			} else {
				this.error.confirmPassword = "确认密码不能为空";
				flag = false;
			}

			return flag;
		},
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false" && _self.allowSmsCodeSubmit == false) {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
						
					}
				});
			}
		},

		//获取短信验证码
		getSmsCode : function () {
			var _self = this;
			
			_self.allowSmsCodeSubmit = true;//提交按钮disabled状态
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			//设置按钮禁用状态
			_self.button.disabled = true;
			
			
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			parameter += "&module=300";
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "smsCode",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							//倒计时
							_self.countdown();
							_self.successInfo = "短信验证码已发送";	
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}	
						}
						_self.allowSmsCodeSubmit = false;//提交按钮disabled状态
					}
				}
			});
		},
		//倒计时
		countdown : function () {
			var _self = this;
			_self.time = 60;
			var interval = window.setInterval(function() {
				_self.time--;
				_self.button.disabled = true;
				_self.button.text="重新发送("+_self.time+")";
				
				if(_self.time <= 0) {
					window.clearInterval(interval);
					_self.button.disabled = false;
					_self.button.text="获取短信校验码";
				}
			}, 1000);

		},	

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
	},
});

//登录页组件
var login_component = Vue.extend({
	template : '#login-template',
	data : function data() {
		return {
			type_tab_10 : 'active',//用户类型选中样式
			type_tab_20 : '',//用户类型选中样式
			
			account_field : true,//表单账号字段
			mobile_field : false,//表单手机号字段
			
			type : 10,//用户类型
			mobile : '',//手机号
			account : '',
			password : '',
			rememberMe:false,
			showCaptcha : false,
			show_loginModule : false,//是否显示登录页
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				mobile : '',
				account : '',
				password : '',
				captchaValue : ''
			},
			supportLoginInterfaceList: '',//支持登录接口集合
			jumpUrl : '',
			
			allowSubmit:false,//提交按钮disabled状态
		};
	},
	
	mounted : function mounted(){
		if(isWeiXinBrowser()){//如果来自微信内置浏览器
			//挂载完成后，判断浏览器是否支持popstate
			if (window.history && window.history.pushState && this.$store.state.weixin_oa_appid != '') {//监听浏览器前进后退按钮
				window.addEventListener('popstate', this.goBack, false);
				
			}
		}
		
	},
	destroyed : function destroyed() {//离开当前页面
		if(isWeiXinBrowser() && this.$store.state.weixin_oa_appid != ''){//如果来自微信内置浏览器
			//页面销毁时，取消监听
			window.removeEventListener('popstate', this.goBack, false);
		}
	},
	created : function created() {
		var jumpUrl = getUrlParam("jumpUrl");
		if(jumpUrl != null){
			this.jumpUrl = jumpUrl;
		}
		
		
		if(isWeiXinBrowser() && this.$store.state.weixin_oa_appid != ''){
			//处理微信公众号自动登录
			var weixin_openid_value = sessionStorage.getItem("weixin_openid"); 
			if(weixin_openid_value == null || weixin_openid_value == ""){//如果来自微信内置浏览器
				
				//获取微信openid
				getWeiXinOpenId();

				sessionStorage.setItem("pushState", "true"); //标记添加URL记录
			}
			window.history.pushState(null, null, "");
		}
		
		
		this.show_loginModule = true;
		this.loadLogin();
		
	
		//加载第三方登录
		this.queryThirdPartyLogin();
		
	},
	methods : {
		//页面前进后退
		goBack : function(){
			
		    if(isWeiXinBrowser() && this.$store.state.weixin_oa_appid != ''){//如果来自微信内置浏览器
		    	var pushStateValue = sessionStorage.getItem("pushState"); 
		    	if(pushStateValue != null && pushStateValue == "true"){
			    	sessionStorage.removeItem("pushState");
			    	this.$router.go(-3);
		    	}else{
		    		var pathname = window.location.pathname;
		    		var jumpUrl = getUrlParam("jumpUrl");
		    		if (jumpUrl != null && jumpUrl != "" && pathname == store.state.contextPath + "/login") {
		    			this.$router.go(-2);
		    		} else {
		    			this.$router.back();
		    		}
		    	}	
		    }else{
		    	this.$router.back();
		    }
		},
		
		//选择用户类型
		selectAccountType : function(type) {
			if(type == 10){
				this.type =10;//用户类型
				this.type_tab_10 = "active";
				this.type_tab_20 = "";
				
				this.account_field = true;
				this.mobile_field = false;
			}else if(type == 20){
				this.type =20;//用户类型
				this.type_tab_10 = "";
				this.type_tab_20 = "active";
				
				this.account_field = false;
				this.mobile_field = true;
			}
			
		},
		
		//加载登录页
		loadLogin : function loadLogin(event) {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "login",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var formCaptcha = $.parseJSON(result);
						if (formCaptcha.showCaptcha == true) {
							//alert(result);
							_self.showCaptcha = true;
							_self.captchaKey = formCaptcha.captchaKey;
							_self.imgUrl = "captcha/" + formCaptcha.captchaKey + ".jpg";
						}
					}
				}
			});
		},
		
		//查询第三方登录
		queryThirdPartyLogin : function queryThirdPartyLogin(event) {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryThirdPartyLogin",
				//data: data,
				success : function success(result) {
					if (result != "") {
						var supportLoginInterfaceList = $.parseJSON(result);
						if (supportLoginInterfaceList != null && supportLoginInterfaceList.length > 0) {
							_self.supportLoginInterfaceList = supportLoginInterfaceList;
						}
					}
				}
			});
		},
			
		
		//跳转第三方登录链接
		loginLink : function loginLink(interfaceProduct) {
			window.location.href = this.$store.state.baseURL + "thirdParty/loginLink?interfaceProduct="+interfaceProduct+"&jumpUrl="+this.jumpUrl;
		},
		
		
		//提交数据
		submitData : function submitData(event) {
			var _self = this;

			_self.allowSubmit = true;//提交按钮disabled状态
			//清除错误
			_self.error.account = "";
			_self.error.password = "";
			_self.error.mobile = "";
			_self.error.captchaValue = "";
			
			
			
			var parameter = "";


			if(_self.type == 10){//10:本地账号密码用户
				parameter += "&type=10";
				
				//账号
				var account = _self.account;
				if (account != "") {
					parameter += "&account=" + encodeURIComponent(account);
				}
				
			}else if(_self.type == 20){//20: 手机用户
				parameter += "&type=20";
				
				//手机号
				var mobile = _self.mobile;
				if (mobile != "") {
					parameter += "&mobile=" + encodeURIComponent(mobile);
				}
				
			}
			//密码需SHA256加密
			var password = _self.password;
			if (password != "") {
				parameter += "&password=" + CryptoJS.SHA256(password);
			}

			//url跳转参数
			var jumpUrl = getUrlParam("jumpUrl");
			if (jumpUrl != null) {
				parameter += "&jumpUrl=" + encodeURIComponent(jumpUrl);
			}

			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());
			
			//自动登录
			parameter += "&rememberMe=" + _self.rememberMe;
			
			
			if(isWeiXinBrowser() && sessionStorage.getItem("weixin_openid") != null){//如果来自微信内置浏览器
				//微信openid
				parameter += "&thirdPartyOpenId=" + sessionStorage.getItem("weixin_openid"); 
			}
			
			//令牌
			parameter += "&token=" + _self.$store.state.token;

			//	alert(parameter);

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "login",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var key_success = "";
						var key_error = null;
						var key_jumpUrl = null;
						var key_captchaKey = null;
						var value_systemUser = null;//登录用户
						
						for (var key in returnValue) {
							if (key == "success") {
								key_success = returnValue[key];
							} else if (key == "error") {
								key_error = returnValue[key];
							} else if (key == "jumpUrl") {
								//跳转URL
								key_jumpUrl = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								key_captchaKey = returnValue[key];
							} else if (key == "systemUser") {
								value_systemUser = returnValue[key];
							}
						}

						//登录成功
						if (key_success == "true") {
							//设置登录用户
							_self.$store.commit('setSystemUserId', value_systemUser.userId);
							_self.$store.commit('setSystemUserName', value_systemUser.userName);
							
							if (key_jumpUrl != null) {
								_self.$router.push(key_jumpUrl);
							} else {
								_self.$router.push("index");
							}
						} else {
							//显示错误
							if (key_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in key_error) {
									if (error != "") {
										if (error == "account") {
											_self.error.account = key_error[error];
										} else if (error == "password") {
											_self.error.password = key_error[error];
										} else if (error == "mobile") {
											_self.error.mobile = key_error[error];
										} else if (error == "captchaValue") {
											_self.error.captchaValue = key_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}
							_self.allowSubmit = false;//提交按钮disabled状态
							if (key_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = key_captchaKey;
								_self.imgUrl = "captcha/" + key_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});

		// 跳转页面
		//     this.$router.push({path: '/detail/', query: '11'});
		//	this.$router.push('home')
		//重定向到首页
		//	this.$router.push({ path: '/index', redirect: { name: 'foo' }})
		},
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
	}
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

//用户中心页
var home_component = Vue.extend({
	name: 'home',//组件名称，keep-alive缓存需要本参数
	template : '#home-template',
	data : function data() {
		return {
			userName : '', //用户名称 从URL中获取
			user : '', //用户
			avatarUrl: '',//头像URL
			followerCount:0,//用户粉丝总数
			
			popup_userSetting : false, //'用户设置'弹出层
			popup_updateAvatar : false, //'更换头像'弹出层
			updateAvatar_button: false,//'头像上传'按钮是否禁用
			option: {
				img: '',//裁剪图片的地址
				outputSize: 1,//裁剪生成图片的质量 0.1 - 1
				outputType: 'png',//裁剪生成图片的质量
				full: false,//是否输出原图比例的截图
				canMove: true,//上传图片是否可以移动
				original: false,//上传图片按照原始比例渲染
				fixedBox: true,//固定截图框大小 不允许改变
				canMoveBox: false,//截图框能否拖动
				autoCrop: true,//是否默认生成截图框
				autoCropWidth: 200,//默认生成截图框宽度 只有自动截图开启 宽度才生效
				autoCropHeight: 200,//默认生成截图框高度 只有自动截图开启 宽度才生效
				centerBox: false,//截图框是否被限制在图片里面
				high: false,//是否按照设备的dpr 输出等比例图片
				maxImgSize: 4000 //限制图片最大宽度和高度
			},
		
			userDynamicList : [], //用户动态集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			hidePasswordIndex:0,//隐藏标签密码框索引
			favoriteCountGroup:[],//话题收藏总数组
			likeCountGroup:[],//话题点赞总数组
			questionFavoriteCountGroup:[],//问题收藏总数组
			following:false,//是否已经关注该用户
			
			pictureOptions: {//放大图片配置
		        url: 'src',//定义从何处获取原始图像URL进行查看
		        navbar : true, //导航栏
		        title : false, //标题
		        toolbar : false, //工具栏
		        loop:false, //是否启用循环查看
		        filter : function(image) {
		        	return image.dataset.enable != 'false';//将不允许放大的表情图片过滤
		        },
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
		    
		    videoPlayerBind: [],//视频播放器绑定Id集合
			videoPlayerList: [],//视频播放器集合
			
		    playerNodeList: []//视频节点对象集合
			
			
		}
	},
	created : function created() {
		//用户中心页由this.scrollQueryUserDynamicList()进行初始化

		this.userName = getUrlParam("userName");
		
		//this.loadHome(function (){});
		//设置缓存
		//this.$store.commit('setCacheComponents',  ['home']);
		//查询消息
		this.unreadMessageCount();
		
	}, 
	beforeRouteLeave: function (to, from, next) {
		//if (to.path == '/thread' || to.path == '/question') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
		//	this.$store.commit('setCacheComponents',  ['home']);
		//} else {
		//	this.$store.commit('setCacheComponents',  []);
		//}
		next();
	},
	activated: function () {//当再次进入（前进或者后退）时，触发 (keepAlive缓存机制才会触发的钩子函数)
		
		this.loading=false;//打开自动加载
    },
    deactivated: function () {//退出时触发 (keepAlive缓存机制才会触发的钩子函数)
    	this.loading=true;//关闭自动加载  从话题详细页或问题详细页返回时会自动执行v-infinite-scroll,需预先关闭自动加载
    },
	beforeDestroy : function() {
		for(var i =0; i<this.videoPlayerList.length; i++){
			var videoPlayer = this.videoPlayerList[i];
			videoPlayer.destroy();//销毁播放器
			
		}
	},
	components: {
	    'vue-cropper': window['vue-cropper'].default
	},
	computed: {
		//动态解析模板数据
		analyzeDataComponent: function analyzeDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					props: this.$options.props, // re-use current props definitions
					
				};
			};	
		},
		
		//动态解析评论引用模板数据
		quoteDataComponent: function quoteDataComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					data : function data() {
						return {
							escapeChar:'{{'//富文本转义字符
						};
					},
					props: this.$options.props, // re-use current props definitions
					
				};
			};	
		},
		
		//动态解析隐藏标签模板数据
		hideTagComponent: function hideTagComponent() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					
					data : function data() {
						return {
							hide_passwordList :[],//话题隐藏密码
							escapeChar:'{{'//富文本转义字符
						};
					},
					props: this.$options.props, // re-use current props definitions
					methods: {
				        //模板中将点击事件绑定到本函数，作用域只限在这个子组件中
				        topicUnhide : function(hideType,hidePasswordIndex,topicId){
				        	var _self = this;
				        	
				        	var parameter = "&topicId=" +topicId;
				        	parameter += "&hideType="+hideType;//获取URL参数
				        	
				        	if(hideType == 10){
				        		var hide_password = _self.hide_passwordList[hidePasswordIndex];
					        	if(hide_password != undefined && hide_password != ""){
					        		parameter += "&password="+encodeURIComponent(hide_password);//获取URL参数
					        	}else{
					        		_self.$toast({
										message : "密码不能为空",
										duration : 3000,
										className : "mint-ui-toast",
									});
					        		return;
					        	}
				        	}
				        	
				        	
				        	_self.$messagebox.confirm('确定解锁?').then(function (action) {
				        		//令牌
								parameter += "&token=" + _self.$store.state.token;
								$.ajax({
									type : "POST",
									cache : false,
									async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
									url : "user/control/topic/unhide",
									data : parameter,
									success : function success(result) {
										if (result != "") {

											var returnValue = $.parseJSON(result);

											var value_success = "";
											var value_error = null;

											for (var key in returnValue) {
												if (key == "success") {
													value_success = returnValue[key];
												} else if (key == "error") {
													value_error = returnValue[key];
												}
											}

											//加入成功
											if (value_success == "true") {
												_self.$toast({
													message : "话题取消隐藏成功，3秒后自动刷新页面",
													duration : 3000,
													className : "mint-ui-toast",
												});

												setTimeout(function() {
													//刷新动态列表
													_self.$parent.$parent.refreshUserDynamicList();//调用父组件方法
													
												}, 3000);
												
											} else {
												//显示错误
												if (value_error != null) {


													var htmlContent = "";
													var count = 0;
													for (var errorKey in value_error) {
														var errorValue = value_error[errorKey];
														count++;
														htmlContent += count + ". " + errorValue + "<br>";
													}
													_self.$messagebox('提示', htmlContent);

												}
											}
										}
									}
								});
							},function (action) {//取消回调
								
							}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
							    console.log(err);
							});
				        	
				        	
				        	
				        },
				        //创建视频播放器
				  		createVideoPlayer : function(url,cover,thumbnail,playerId) {
				  			var _self = this;
				  			
				  			if(_self.$parent.$parent.videoPlayerBind.contains(playerId)){
				  				return;
				  			}
				  			
				  			var player = document.getElementById(playerId);
				  			
				  			if(url == ""){
				  				var dom = document.createElement('div');
				  				dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
				  				player.appendChild(dom);
				  			}else{
				  				_self.$parent.$parent.videoPlayerBind.push(playerId);//视频播放器绑定Id
				  				
				  				
				  				if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
				  					var dp = new DPlayer({
				  		    			container: player,//播放器容器元素
				  		    			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
				  		    			
				  		    			video: {
				  		    			    url: url,
				  		    			    type: 'hls',
				  		    			    pic: cover,//视频封面
				  		    			    thumbnails: thumbnail//视频预览图
				  		    			}
				  		    		});
				  					dp.play();//播放视频
				  					
				            			
				            	}else{
			            			var dp = new DPlayer({
			            				container: player,//播放器容器元素
			                			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
			                			
			                			video: {
			                			    url: url
			                			}
			                		});
			            			dp.play();//播放视频
				            	}
				  				
				  				_self.$parent.$parent.videoPlayerList.push(dp);//视频播放器
				  				
				  			}
				  			
				  			//添加播放器节点数据
							if(_self.$parent.$parent.videoPlayerList.length >0){
								
								for(var i=0; i< _self.$parent.$parent.videoPlayerBind.length; i++){
							    	var playerId = _self.$parent.$parent.videoPlayerBind[i];
							    	var node = document.getElementById(playerId);//节点对象
							    	_self.$parent.$parent.playerNodeList.push(node);
							    }
							}
				  		},
				  		
				  		
				  		
				    }
				};
			};	
		},
	},
	methods : {
		
		//选择剪裁图片
		selectImage: function selectImage(e) {
			var _self = this;
		    //上传图片
		    // this.option.img
		    var file = e.target.files[0];
		    if (!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG)$/.test(e.target.value)) {
		        alert('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种');
		        return false;
		    }
		    
		    var reader = new FileReader();
		    reader.onload = function (e) {
		        var data = void 0;
		        if (_typeof(e.target.result) === 'object') {
		          // 把Array Buffer转化为blob 如果是base64不需要
		          data = window.URL.createObjectURL(new Blob([e.target.result]));
		        } else {
		          data = e.target.result;
		        }
		        _self.option.img = data;
		    };
		      // 转化为base64
		   //  reader.readAsDataURL(file)
		      // 转化为blob
		    reader.readAsArrayBuffer(file);
		},
		//提交上传图片
		uploadImageSubmit : function() {
			var _self = this;
			_self.updateAvatar_button = true;//禁用'头像上传'按钮
			
			if(_self.option.img == ''){
				_self.$toast({
					message : "请先选择图片",
					duration : 3000,
					className : "mint-ui-toast",
				});
				_self.updateAvatar_button = false;//启用'头像上传'按钮
				return;
			}
			var formData = new FormData();
			
			_self.$refs.cropper.startCrop();//开始截图
			
			//获取截图的blob数据
			_self.$refs.cropper.getCropBlob(function (data) {

				formData.append("imgFile", data);
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/updateAvatar",
					data : formData,
					contentType : false, // 不设置内容类型
					processData : false, // 不处理数据
					success : function success(result) {
						if (result != "") {
							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//修改成功
							if (value_success == "true") {
								_self.$toast({
									message : "更换头像成功",
									duration : 3000,
									className : "mint-ui-toast",
								});
								
								_self.loadHome(function (){});
							//	_self.option.img ='';
								//清空表单值
							//	_self.$refs.selectImgFile_ref.value ='';
								
								
								
							} else {
								//显示错误
								if (value_error != null) {

									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);
								}
								
							}
							_self.updateAvatar_button = false;//启用'头像上传'按钮
						}
					}
				});
		    });
			
			
			
		},
		
		//查询是否已经关注该用户
		queryFollowing : function() {
			var _self = this;
			if(_self.user != ''){
				var data = "userName=" + _self.user.userName; //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "queryFollowing",
					data : data,
					success : function success(result) {
						if (result != "") {
							var following = $.parseJSON(result);
							if (following != null) {
								_self.following = following;
							}
						}
					}
				});
				
			}
		},

		//添加关注
		addFollow : function(userName) {
			var _self = this;
			if(_self.following == false){
				_self.$messagebox.confirm('确定关注?').then(function (action) {
					var parameter = "&userName=" + userName;

					//	alert(parameter);
					//令牌
					parameter += "&token=" + _self.$store.state.token;
					$.ajax({
						type : "POST",
						cache : false,
						async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
						url : "user/control/follow/add",
						data : parameter,
						success : function success(result) {
							if (result != "") {

								var returnValue = $.parseJSON(result);

								var value_success = "";
								var value_error = null;

								for (var key in returnValue) {
									if (key == "success") {
										value_success = returnValue[key];
									} else if (key == "error") {
										value_error = returnValue[key];
									}
								}

								//加入成功
								if (value_success == "true") {
									_self.$toast({
										message : "关注成功",
										duration : 3000,
										className : "mint-ui-toast",
									});

									setTimeout(function() {
										//查询是否已经关注该用户
										_self.queryFollowing();
									}, 3000);
									
								} else {
									//显示错误
									if (value_error != null) {


										var htmlContent = "";
										var count = 0;
										for (var errorKey in value_error) {
											var errorValue = value_error[errorKey];
											count++;
											htmlContent += count + ". " + errorValue + "<br>";
										}
										_self.$messagebox('提示', htmlContent);

									}
								}
							}
						}
					});
				}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
				    console.log(err);
				});
				
			}
		},
		
		//加载用户中心页
		loadHome : function(callback) {
			var _self = this;
			var data = "";
			if(_self.userName != null && _self.userName != ""){
				data = "&userName=" + _self.userName;
			}
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/home",
				data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						for (var key in returnValue) {
							if (key == "user") {
								_self.user = returnValue[key];
								
								_self.avatarUrl = _self.user.avatarPath+""+_self.user.avatarName+"?"+Math.random().toString().slice(-6);
								_self.queryFollowing();//查询是否已经关注该用户
								//查询用户粉丝总数
								_self.queryFollowerCount();
							}
						}
						//回调
						callback();
					}
				}
			});
		},
		
		//查询用户粉丝总数
		queryFollowerCount : function() {
			var _self = this;
			var data = "";
			if(_self.userName != null && _self.userName != ""){//用户名称
				data = "&userName=" + _self.userName;
			}else{
				data = "userName=" + _self.user.userName; //提交参数
			}
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryFollowerCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							_self.followerCount = count;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		
		//刷新动态列表
		refreshUserDynamicList : function() {
			this.userDynamicList =[]; //用户动态集合
			this.loading = false; //加载中
			this.currentpage = 0; //当前页码
			this.totalpage= 1; //总页数
			this.hidePasswordIndex=0;//隐藏标签密码框索引	
			this.favoriteCountGroup = [];//话题收藏总数组
			this.likeCountGroup = [];//话题点赞总数组
			this.questionFavoriteCountGroup = [];//问题收藏总数组
			//查询动态列表
			this.queryUserDynamicList();
		},
		
		//滚动加载查询动态列表
		scrollQueryUserDynamicList : function() {
			var _self = this;
			if(_self.user != null && _self.user != '' && _self.user.userName == _self.$store.state.user.userName){//如果查询用户自已的动态，则不显示
				return;
			}
			
			_self.loadHome(function (){
				if(_self.user != null && _self.user != ''){
					if(_self.user.userName == _self.$store.state.user.userName){//如果查询用户自已的动态，则不显示
						return;
					}
					//因为v-infinite-scroll标签会在打开页面立即运行，并且动态列表需要用户信息，所以在这里调用查询用户中心页面
					_self.queryUserDynamicList();
				}
				
				
				
			});
		},
		
		
		//查询动态列表
		queryUserDynamicList : function() {
			var _self = this;
			
			if(_self.totalpage == _self.currentpage){//如果是最后一页，则不再加载
				_self.loading = true;
				return;
			}
			
			if (_self.user.userName != undefined && _self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				if(_self.userName != null && _self.userName != ""){//用户名称
					data += "&userName=" + _self.userName;
				}
				
				
				
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/userDynamicList",
					data : data,
					success : function success(result) {
						if (result != "") {	
							var pageView = $.parseJSON(result);
							var new_userDynamicList = pageView.records;
							if (new_userDynamicList != null && new_userDynamicList.length > 0) {
								
								for(var i=0; i< new_userDynamicList.length;i++){
									var userDynamic =  new_userDynamicList[i];
									_self.favoriteCountGroup.push(0);
									_self.likeCountGroup.push(0);
									_self.questionFavoriteCountGroup.push(0);
									if(userDynamic.module == 100){
										//处理隐藏标签
										var contentNode = document.createElement("div");
										contentNode.innerHTML = userDynamic.topicContent;
										_self.hidePasswordIndex = 0;
										_self.bindNode(contentNode,userDynamic.topicId);
										userDynamic.topicContent = escapeVueHtml(contentNode.innerHTML);
										
										var favoriteObject = new Object(); 
										favoriteObject.topicId = userDynamic.topicId; 
										favoriteObject.index = _self.favoriteCountGroup.length-1; 
										
										
										setTimeout(function(favoriteObject) {
											//话题收藏数量
											_self.queryFavoriteCount(favoriteObject.topicId,favoriteObject.index,function(index,count) {
												if(count != null && count != ''){
													_self.$set(_self.favoriteCountGroup, index, parseInt(count));
												}
												
											});
										}, 0,favoriteObject);
										
										
										var likeObject = new Object(); 
										likeObject.topicId = userDynamic.topicId; 
										likeObject.index = _self.likeCountGroup.length-1;
										_self.$nextTick(function() {
											setTimeout(function(likeObject) {
												//点赞数量
												_self.queryLikeCount(likeObject.topicId,likeObject.index,function(index,count) {
													if(count != null && count != ''){
														_self.$set(_self.likeCountGroup, index, parseInt(count));
													}
													
												});
											
											}, 0,likeObject);
										});
										
									}else if(userDynamic.module == 200){
										//处理图片标签
										var contentNode = document.createElement("div");
										contentNode.innerHTML = userDynamic.commentContent;
										_self.bindNode(contentNode,null);
										userDynamic.commentContent = escapeVueHtml(contentNode.innerHTML);
									}else if(userDynamic.module == 300){
										//处理图片标签
										var contentNode = document.createElement("div");
										contentNode.innerHTML = userDynamic.commentContent;
										_self.bindNode(contentNode,null);
										userDynamic.commentContent = escapeVueHtml(contentNode.innerHTML);
									}else if(userDynamic.module == 500){
										var favoriteObject = new Object(); 
										favoriteObject.question = userDynamic.questionId; 
										favoriteObject.index = _self.questionFavoriteCountGroup.length-1;

										setTimeout(function(favoriteObject) {
											//问题收藏数量
											_self.queryQuestionFavoriteCount(favoriteObject.questionId,favoriteObject.index,function(index,count) {
												if(count != null && count != ''){
													_self.$set(_self.questionFavoriteCountGroup, index, parseInt(count));
												}
												
											});
										}, 0,favoriteObject);
										
										//处理图片标签
										var contentNode = document.createElement("div");
										contentNode.innerHTML = userDynamic.questionContent;
										_self.bindNode(contentNode,null);
										userDynamic.questionContent = escapeVueHtml(contentNode.innerHTML);
									}else if(userDynamic.module == 600){
										//处理图片标签
										var contentNode = document.createElement("div");
										contentNode.innerHTML = userDynamic.answerContent;
										_self.bindNode(contentNode,null);
										userDynamic.answerContent = escapeVueHtml(contentNode.innerHTML);
									}
									
									
									
									
									
								}
								
								_self.userDynamicList.push.apply(_self.userDynamicList, new_userDynamicList); //合并两个数组
								
							
								//生成首字符头像
								_self.$nextTick(function() {
									if (new_userDynamicList != null && new_userDynamicList.length > 0) {
										for(var i=0;i<new_userDynamicList.length; i++){
											var userDynamic = new_userDynamicList[i];
											if(userDynamic.avatarName == null || userDynamic.avatarName == ''){
												var char = (userDynamic.nickname != null && userDynamic.nickname !="") ? userDynamic.nickname : userDynamic.account;
												//元素的实际宽度
												var width= _self.$refs['userDynamicAvatarData_'+userDynamic.id][0].offsetWidth;
												_self.$refs['userDynamicAvatarData_'+userDynamic.id][0].src = letterAvatar(char, width);	
											}
										}
									}
								});
								
								
							}
							
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}	
		},
		//递归获取所有的子节点
		bindNode : function(node,topicId) {
			//先找到子节点
	        var nodeList = node.childNodes;
	        for(var i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            var childNode = nodeList[i];
	            var random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	if(childNode.nodeName.toLowerCase() == "hide" ){
	            		if(childNode.getAttribute("hide-type") == "10"){//输入密码可见
	            			var nodeHtml = "";
	    					nodeHtml += '<div class="hide-box">';
	    					nodeHtml += 	'<div class="background-image cms-lock"></div>';
	    					nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，输入密码可见</div>';
	    					nodeHtml += 	'<div class="input-box">';
	    					nodeHtml += 		'<input type="password" v-model.trim="hide_passwordList['+this.hidePasswordIndex+']" class="text" maxlength="30"  placeholder="密码" value="">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(10,'+this.hidePasswordIndex+','+topicId+');">';
	    					nodeHtml += 	'</div>';
	    					nodeHtml += '</div>';
	    					childNode.innerHTML = nodeHtml;
	    					
	    					this.hidePasswordIndex++;
	            		}else if(childNode.getAttribute("hide-type") == "20"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，评论话题可见</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "30"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，等级达到‘'+_.escape(childNode.getAttribute("description"))+'’可见</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "40"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，支付‘'+childNode.getAttribute("input-value")+'’积分可见</div>';
	            			nodeHtml += 	'<div class="submit-box">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(40,'+null+','+topicId+');">';
	    					nodeHtml += 	'</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}else if(childNode.getAttribute("hide-type") == "50"){
	            			var nodeHtml = "";
	            			nodeHtml += '<div class="hide-box">';
	            			nodeHtml += 	'<div class="background-image cms-lock"></div>';
	            			nodeHtml += 	'<div class="background-prompt">此处内容已被隐藏，支付 ￥<span class="highlight">'+childNode.getAttribute("input-value")+'</span> 元可见</div>';
	            			nodeHtml += 	'<div class="submit-box">';
	    					nodeHtml += 		'<input type="button" value="提交" class="button" @click="topicUnhide(50,'+null+','+topicId+');">';
	    					nodeHtml += 	'</div>';
	            			nodeHtml += '</div>';
	            			childNode.innerHTML = nodeHtml;
	    				}
	            	}
	            	//处理图片放大
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		
            			var src = childNode.getAttribute("src");
	            		
	            		if(childNode.getAttribute("width") == null){//不是表情图片
	            			childNode.setAttribute("key",random+"_"+i);
	            		}else{
	            			childNode.setAttribute("data-enable","false");//标记不显示放大图
	            		}
	            		
	            		//延迟加载 表情图片也使用<img>标签，也执行延迟加载
            			childNode.setAttribute("src",this.$store.state.commonPath+'images/null.gif');
            			childNode.setAttribute("data-src",src);
            			
	            		var param = childNode.getAttribute("cover");//视频封面参数
		            	if(param != ""){//如果不是视频封面图	
		            		childNode.setAttribute("data-enable","false");//标记不显示放大图
	            		}
	            		
	            	}
	            	//处理视频标签
	            	if(childNode.nodeName.toLowerCase() == "player" ){
	            		var id = "player_"+random+"_"+i;
	            		
	            		childNode.setAttribute("id",id);//设置Id
	            		var url = childNode.getAttribute("url");
	              		var cover = childNode.getAttribute("cover");//封面
	              		var thumbnail = childNode.getAttribute("thumbnail");//缩略图
	            		
	              		if(cover == null){
	              			cover = "";
	              		}
	              		if(thumbnail == null){
	              			thumbnail = "";
	              		}
	              		
	              		
	            		var dom = document.createElement('span');
	    				dom.innerHTML="<img class='cover' src='"+cover+"' @click=\"createVideoPlayer('"+url+"','"+cover+"','"+thumbnail+"','"+id+"')\"/>" +
	    						"<span class='buttonCircle' @click=\"createVideoPlayer('"+url+"','"+cover+"','"+thumbnail+"','"+id+"')\"><span class='iconBox'><i class='cms-control-play playIcon'></i></span></span>";
	    				childNode.appendChild(dom);
	    			
	            	}
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		var pre_html = childNode.innerHTML;
	            		var class_val = childNode.className;
	            		var lan_class = "";
	            		
	        	        var class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(var k=0; k<class_arr.length; k++){
	        	        	var className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	            		
	            		var nodeHtml = "";

            			//删除code节点
            			var preChildNodeList = childNode.childNodes;
            			for(var p = 0;p < preChildNodeList.length;p++){
            				var preChildNode = preChildNodeList[p];
            				if(preChildNode.nodeName.toLowerCase() == "code" ){
            					nodeHtml += preChildNode.innerHTML;
            					preChildNode.parentNode.removeChild(preChildNode);
                			}
            				
            			}
            			
            			var dom = document.createElement('code');
            			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				Prism.highlightElement(dom);
	    		
	            	}
	            	
	            	
	            	this.bindNode(childNode,topicId);
	            }
	        }
	    },

	    
		//查询话题用户收藏总数
		queryFavoriteCount : function(topicId,index,callback) {
			var _self = this;
			var data = "topicId=" + topicId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryFavoriteCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							
							callback(index,count);
						//	_self.favoriteCount = count;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		
		//查询话题用户点赞总数
		queryLikeCount : function(topicId,index,callback) {
			var _self = this;
			var data = "topicId=" + topicId; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryLikeCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							
							callback(index,count);
						//	_self.favoriteCount = count;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		
		//查询问题用户收藏总数
		queryQuestionFavoriteCount : function(questionId,index,callback) {
			var _self = this;
			var data = "questionId=" + questionId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryQuestionFavoriteCount",
				data : data,
				success : function success(result) {
					if (result != "") {
						var count = $.parseJSON(result);
						if (count != null) {
							
							callback(index,count);
						//	_self.favoriteCount = count;
						}
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
		},
		
		
		
		
		//显示'用户设置'			
		displayUserSetting : function() {
			this.popup_userSetting = true;
		},

		//会员退出登录
		logout : function() {
			var _self = this;
			//令牌
			var data = "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "logout",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_jumpUrl = "";
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "jumpUrl") {
								value_jumpUrl = returnValue[key];
							}
						}
						if (value_success == "true") {
							//清理登录信息
							_self.$store.commit('setSystemUserId', '');
							_self.$store.commit('setSystemUserName', '');
							
							sessionStorage.clear();//清空sessionStorage中所有信息
							
							_self.$router.replace({ path: '/'+value_jumpUrl });
						} else {
							for (var error in value_error) {
								if (error != "") {
									if (error == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}
								}
							}

						}
					}
				}
			});
		}
	}
});

//我的话题
var topicList_component = Vue.extend({
	name: 'topicList',//组件名称，keep-alive缓存需要本参数
	template : '#topicList-template',
	data : function data() {
		return {
			topicList : [], //话题集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
			
			operating: false,//是否显示操作页面
			operatingData: '',//弹出操作菜单
	
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['topicList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/thread'
			|| to.path == '/user/control/topicUnhideList' 
			|| to.path == '/user/control/topicFavoriteList' 
			|| to.path == '/user/control/topicLikeList' 
			|| to.path == '/user/control/redEnvelopeAmountDistributionList' 
			|| to.path == '/user/editTopic') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['topicList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},

	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		//操作界面
		operatingUI : function(topicId,giveRedEnvelopeId,title) {
			var _self = this;
			_self.operating = true;
			var operatingData = new Array();
			operatingData.push({
							name: title,
							module : 0, //模块 标题
							id : topicId
				   	});
			
			operatingData.push({
				        name: '解锁隐藏标签用户', 
				        method : _self.operatingAction,	// 调用methods中的函数
							module : 10, //模块
							id : topicId
						});
			
			operatingData.push({
							name: '收藏用户', 
							method : _self.operatingAction,	// 调用methods中的函数
							module : 20, //模块
							id : topicId
						});
			
			operatingData.push({
							name: '点赞用户', 
							method : _self.operatingAction,	// 调用methods中的函数
							module : 30, //模块
							id : topicId
						});
			if(giveRedEnvelopeId != null && giveRedEnvelopeId != ''){
				operatingData.push({
					name: '红包', 
					method : _self.operatingAction,	// 调用methods中的函数
					module : 40, //模块
					id : topicId,
					giveRedEnvelopeId : giveRedEnvelopeId
				});
			}
			
			operatingData.push({
							name: '编辑', 
							method : _self.operatingAction,	// 调用methods中的函数
							module : 50, //模块
							id : topicId
						});
			
			_self.operatingData = operatingData;

		},
		
		
		//操作动作
		operatingAction : function(obj) {
			var _self = this;
			var module = obj.module;//模块
			var topicId = obj.id;//话题Id
			var giveRedEnvelopeId = obj.giveRedEnvelopeId;//红包Id
			
			if(module == 10){//
				_self.$router.push({ path: '/user/control/topicUnhideList',query: {topicId: topicId}});
				
			}else if(module == 20){
				_self.$router.push({ path: '/user/control/topicFavoriteList',query: {topicId: topicId}});
				
			}else if(module == 30){
				_self.$router.push({ path: '/user/control/topicLikeList',query: {topicId: topicId}});
				
			}else if(module == 40){
				
				_self.$router.push({ path: '/user/control/redEnvelopeAmountDistributionList',query: {giveRedEnvelopeId: giveRedEnvelopeId}});
			}else if(module == 50){
				
				_self.$router.push({ path: '/user/editTopic',query: {topicId: topicId}});
			}
			
		},
		
		//查询话题列表
		queryTopicList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/topicList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_topicList = pageView.records;
						if (new_topicList != null && new_topicList.length > 0) {
							_self.topicList = new_topicList;
							
							//for(var i=0; i<new_topicList.length; i++){
							//	var topic = new_topicList[i];
								
							//}
							
							
							
							
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询话题列表
			this.queryTopicList();
		},
		
	},
});

//我的评论
var commentList_component = Vue.extend({
	name: 'commentList',//组件名称，keep-alive缓存需要本参数
	template : '#commentList-template',
	data : function data() {
		return {
			commentList : [], //评论集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['commentList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/thread') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['commentList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		
		//查询评论列表
		queryCommentList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/commentList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_commentList = pageView.records;
						if (new_commentList != null && new_commentList.length > 0) {
							_self.commentList = new_commentList;
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询评论列表
			this.queryCommentList();
		},
		
	},
});
//我的回复
var replyList_component = Vue.extend({
	name: 'replyList',//组件名称，keep-alive缓存需要本参数
	template : '#replyList-template',
	data : function data() {
		return {
			replyList : [], //回复集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['replyList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/thread') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['replyList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		
		//查询回复列表
		queryReplyList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/replyList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_replyList = pageView.records;
						if (new_replyList != null && new_replyList.length > 0) {
							_self.replyList = new_replyList;
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询回复列表
			this.queryReplyList();
		},
		
	},
});

//我的问题
var questionList_component = Vue.extend({
	name: 'questionList',//组件名称，keep-alive缓存需要本参数
	template : '#questionList-template',
	data : function data() {
		return {
			questionList : [], //问题集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['questionList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/question' || to.path == '/user/appendQuestion') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['questionList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		
		//查询问题列表
		queryQuestionList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/questionList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_questionList = pageView.records;
						if (new_questionList != null && new_questionList.length > 0) {
							_self.questionList = new_questionList;
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询问题列表
			this.queryQuestionList();
		},
		
	},
});

//我的答案
var answerList_component = Vue.extend({
	name: 'answerList',//组件名称，keep-alive缓存需要本参数
	template : '#answerList-template',
	data : function data() {
		return {
			answerList : [], //答案集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['answerList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/question') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['answerList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},

	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		
		//查询答案列表
		queryAnswerList : function() {
			var _self = this;
			
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answerList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_answerList = pageView.records;
						if (new_answerList != null && new_answerList.length > 0) {
							_self.answerList = new_answerList;
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询答案列表
			this.queryAnswerList();
		},
		
	},
});
//我的答案回复
var answerReplyList_component = Vue.extend({
	name: 'answerReplyList',//组件名称，keep-alive缓存需要本参数
	template : '#answerReplyList-template',
	data : function data() {
		return {
			answerReplyList : [], //答案回复集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['answerReplyList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/question') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['answerReplyList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {
		
		//查询回复列表
		queryAnswerReplyList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/answerReplyList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_answerReplyList = pageView.records;
						if (new_answerReplyList != null && new_answerReplyList.length > 0) {
							_self.answerReplyList = new_answerReplyList;
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询回复列表
			this.queryAnswerReplyList();
		},
		
	},
});

//积分记录
var point_component = Vue.extend({
	template : '#point-template',
	data : function data() {
		return {
			pointLogList : [], //积分日志集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			rewardPointInfo:''
		}
	},
	created : function created() {
		this.queryPoint();
	},
	methods : {
		//查询积分页
		queryPoint : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/point",
					data : data,
					success : function success(result) {
						if (result != "") {
							var returnValue = $.parseJSON(result);
							for (var key in returnValue) {
								if (key == "pageView") {
									var pageView = returnValue[key];

									var new_pointLogList = pageView.records;
									if (new_pointLogList != null && new_pointLogList.length > 0) {
										_self.pointLogList.push.apply(_self.pointLogList, new_pointLogList); //合并两个数组
									}
									_self.currentpage = pageView.currentpage;
									_self.totalpage = pageView.totalpage;

								}else if(key == "rewardPointInfo"){
									_self.rewardPointInfo = returnValue[key];
									
								}
							}
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		}
	}
});



//修改个人信息
var editUser_component = Vue.extend({
	template : '#editUser-template',
	data : function data() {
		return {
			nickname : '',
			allowNickname:true,//是否禁用修改呢称输入框
			allowUserDynamic:true,//是否允许显示用户动态
			oldPassword : '',
			password : '',
			confirmPassword : '',
			user : null,
			userRoleList : [], //用户角色集合
			userCustomList : [], //用户自定义注册功能项
			userBoundField : [], //用户自定义注册功能项绑定
			error : {
				nickname : '',
				allowUserDynamic : '',
				oldPassword : '',
				password : '',
				confirmPassword : '',
				user : '',
			},
			customError : [] //用户自定义注册功能项错误提示
		}
	},
	created : function created() {
		this.loadUser();
	},
	methods : {
		//加载用户信息
		loadUser : function loadUser(event) {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/editUser",
				//	data: data,
				success : function success(result) {

					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							_self.user = returnValue['user'];
							_self.nickname = _self.user.nickname;
							if(_self.nickname == null || _self.nickname == ''){
								_self.allowNickname = false;
							}
							
							_self.allowUserDynamic = _self.user.allowUserDynamic;//是否允许显示用户动态
							
							_self.userCustomList = returnValue['userCustomList'];
							if (_self.userCustomList != null && _self.userCustomList.length > 0) {
								for (var i = 0; i < _self.userCustomList.length; i++) {
									var userCustom = _self.userCustomList[i];
									_self.boundField(userCustom);
									_self.customError.push('');
								}

							}
							
							_self.userRoleList = returnValue['userRoleList'];
							
						}


					}
				}
			});
		},
		//单选项/复选框/下拉菜单 选项内容
		checkbox : function checkbox(userCustom) {
			var options = new Array();

			for (var itemValue in userCustom.itemValue) {
				var o = new Object();
				o.label = userCustom.itemValue[itemValue];
				o.value = itemValue;
				options.push(o);
			}
			return options;
		},
		//绑定字段
		boundField : function boundField(userCustom) {
			if (userCustom.chooseType == 1) { //文本框
				var content = "";
				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					content = userCustom.userInputValueList[i].content;
				}
				this.userBoundField.push(content);
			} else if (userCustom.chooseType == 2) { //单选框
				var checked = "";

				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					var userInputValue = userCustom.userInputValueList[i];
					checked = userInputValue.options;

				}
				//默认选第一项 
				if (checked == "") {
					for (var itemValue in userCustom.itemValue) {
						checked = itemValue;
						break;
					}
				}
				this.userBoundField.push(checked);
			} else if (userCustom.chooseType == 3) { //多选框
				var checked = new Array();

				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					var userInputValue = userCustom.userInputValueList[i];
					checked.push(userInputValue.options);
				}
				this.userBoundField.push(checked);
			} else if (userCustom.chooseType == 4) { //下拉列表
				var checked = new Array();

				A:for (var itemValue in userCustom.itemValue) {
					for (var i = 0; i < userCustom.userInputValueList.length; i++) {
						var userInputValue = userCustom.userInputValueList[i];
						if (itemValue == userInputValue.options) {
							var o = new Object();
							o.label = userCustom.itemValue[itemValue];
							o.value = itemValue;
							checked.push(o);
							continue A;
						}
					}
				}
				this.userBoundField.push(checked);
				/**
				if (userCustom.multiple == true) { //允许多选
					var checked = new Array();

					A:
					for (var itemValue in userCustom.itemValue) {
						for (var i = 0; i < userCustom.userInputValueList.length; i++) {
							var userInputValue = userCustom.userInputValueList[i];
							if (itemValue == userInputValue.options) {
								var o = new Object();
								o.label = userCustom.itemValue[itemValue];
								o.value = itemValue;
								checked.push(o);
								continue A;
							}
						}
					}
					this.userBoundField.push(checked);

				} else {
					var o = new Object();

					A:
					for (var itemValue in userCustom.itemValue) {
						for (var i = 0; i < userCustom.userInputValueList.length; i++) {
							var userInputValue = userCustom.userInputValueList[i];
							if (itemValue == userInputValue.options) {
								o.label = userCustom.itemValue[itemValue];
								o.value = itemValue;
								break A;
							}
						}
					}
					this.userBoundField.push(o);
				}
				**/

			} else if (userCustom.chooseType == 5) { //文本域
				var content = "";
				for (var i = 0; i < userCustom.userInputValueList.length; i++) {
					content = userCustom.userInputValueList[i].content;
				}
				this.userBoundField.push(content);
			}
		},
		//提交数据
		submitData : function submitData(event) {
			var _self = this;

			_self.clearError();
			//验证密码
			if (_self.verificationPassword() == false) {
				_self.$messagebox('提示', '请填好资料再提交');
				return;
			}
			
			

			var parameter = "";
			if(_self.nickname != null && _self.nickname != "" && _self.allowNickname == false){
				parameter += "&nickname=" +  encodeURIComponent(_self.nickname);
			}
			parameter += "&allowUserDynamic=" +  _self.allowUserDynamic;
			
			//密码需SHA256加密
			var password = _self.password.trim();
			var oldPassword = _self.oldPassword.trim();
			if (password != "") {
				parameter += "&password=" + CryptoJS.SHA256(password);
				//旧密码
				parameter += "&oldPassword=" + CryptoJS.SHA256(oldPassword);
			}

			//自定义表单
			if (_self.userCustomList != null && _self.userCustomList.length > 0) {
				for (var i = 0; i < _self.userCustomList.length; i++) {
					var userCustom = _self.userCustomList[i];

					var fieldValue = _self.userBoundField[i];

					if (userCustom.chooseType == 1) { //文本框
						parameter += "&userCustom_" + userCustom.id + "=" + encodeURIComponent(fieldValue);
					} else if (userCustom.chooseType == 2) { //单选框
						parameter += "&userCustom_" + userCustom.id + "=" + fieldValue;

					} else if (userCustom.chooseType == 3) { //多选框
						for (var value in fieldValue) {
							parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value];
						}
					} else if (userCustom.chooseType == 4) { //下拉列表
						if (userCustom.multiple == true) { //允许多选
							for (var value in fieldValue) {
								parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value].value;
							}
						}else{
							for (var value in fieldValue) {
								parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value];
							}
						}
					} else if (userCustom.chooseType == 5) { //文本域
						parameter += "&userCustom_" + userCustom.id + "=" + encodeURIComponent(fieldValue);
					}
				}
			}

			//令牌
			parameter += "&token=" + _self.$store.state.token;

			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/editUser",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var key_success = "";
						var key_error = null;

						for (var key in returnValue) {
							if (key == "success") {
								key_success = returnValue[key];
							} else if (key == "error") {
								key_error = returnValue[key];
							}
						}
						if (key_success == "true") { //成功
							//	_self.$messagebox.alert('操作成功');
							_self.$messagebox.alert('修改成功').then(function(action) {
								//刷新当前页
								//	_self.$router.go(0);
								//	_self.$router.push({ path: '/user/control/editUser' });

								//重新调用data方法,即可重新初始化data
								Object.assign(_self.$data, _self.$options.data());

								//刷新当前页
								_self.loadUser();

							});

						//		_self.$messagebox('提示', '修改成功');
						//刷新当前页
						//		_self.$router.go(0);
						/**
						_self.$toast({
							duration :-1,
							message: '修改成功',
							 iconClass: 'ti-themify-favicon'
						});
						**/
						} else { //失败
							for (var error in key_error) {
								var errorValue = key_error[error];
								var number = error.split("_")[1];
								if (error == "nickname") {
									_self.error.nickname = errorValue;
								}else if(error == "allowUserDynamic"){
									_self.error.allowUserDynamic = errorValue;
								}else if(error == "oldPassword"){
									_self.error.oldPassword = errorValue;
								}else if(error == "password"){
									_self.error.password = errorValue;
								}else if(error == "user"){
									_self.error.user = errorValue;
								}
								
								for (var i = 0; i < _self.userCustomList.length; i++) {
									var userCustom = _self.userCustomList[i];
									if (userCustom.id == number) {
										_self.$set(_self.customError, i, errorValue);
										break;
									}

								}


							//	alert(error+"   "+errorValue);
							}
						}

					}
				}
			});




		},
		//验证参数
		verificationPassword : function verification() {
			var flag = true;
			var oldPassword = this.oldPassword.trim();
			var password = this.password.trim();
			var confirmPassword = this.confirmPassword.trim();
			if (oldPassword != "" || password != "" || confirmPassword != "") {
				if (oldPassword == "") {
					this.error.oldPassword = "旧密码不能为空";
					flag = false;
				}
				if (password != null) {
					if (password.length < 6) {
						this.error.password = "密码长度不能小于6位";
						flag = false;
					}
					if (password.length > 20) {
						this.error.password = "密码长度不能大于20位";
						flag = false;
					}

				} else {
					this.error.password = "密码不能为空";
					flag = false;
				}

				if (confirmPassword != "") {
					if (password != confirmPassword) {
						this.error.confirmPassword = "两次密码不相等";
						flag = false;
					}

				} else {
					this.error.confirmPassword = "确认密码不能为空";
					flag = false;
				}

			}
			return flag;
		},
		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
			for (var i = 0; i < this.customError.length; i++) {
				this.$set(this.customError, i, '');
			}
		}
	}
});

//实名认证
var realNameAuthentication_component = Vue.extend({
	template : '#realNameAuthentication-template',
	data : function data() {
		return {
			user: '', //用户
		}
	},
	created : function () {
		this.queryRealNameAuthentication();
	},
	methods : {
		//查询实名认证页
		queryRealNameAuthentication : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/realNameAuthentication",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							_self.user = returnValue['user'];
							
						}
					}
				}
			});
		}
	}
});

//绑定手机
var phoneBinding_component = Vue.extend({
	template : '#phoneBinding-template',
	data : function data() {
		return {
			mobile: '', //手机号
			smsCode: '', //手机校验码
			successInfo:'', //验证码发送成功信息
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			time: 0,//倒计时间
			error : {
				mobile : '',
				smsCode : '',
				captchaValue : ''
			},
			button: {//获取短信校验码按钮
				disabled : false,
				text : '获取短信校验码',
			}
		}
	},
	created : function () {
		this.queryPhoneBinding();
	},
	methods : {
		//查询绑定手机页
		queryPhoneBinding : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/phoneBinding",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							var captchaKey = returnValue['captchaKey'];
							_self.captchaKey = captchaKey;
							_self.imgUrl = "captcha/" + captchaKey + ".jpg";
						}
					}
				}
			});
		},
		
		//获取短信验证码
		getSmsCode : function () {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			//设置按钮禁用状态
			_self.button.disabled = true;
			
			
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			parameter += "&module=1";
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/getSmsCode",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							//倒计时
							_self.countdown();
							_self.successInfo = "短信验证码已发送";	
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}	
						}
					}
				}
			});
		},
		//提交数据
		submitData : function submitData(event) {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			
			if(_self.smsCode != null && _self.smsCode !=''){
				parameter += "&smsCode=" + encodeURIComponent(_self.smsCode);
				
			}
			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/phoneBinding",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							_self.$toast({
								message : "绑定手机成功，3秒后自动跳转到实名认证页",
								duration : 3000,
								className : "mint-ui-toast",
							});
							setTimeout(function() {
								_self.$router.push({
									path : '/user/control/realNameAuthentication'
								});
							}, 3000);
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
							
							
						}
					}
				}
			});
		},
		//倒计时
		countdown : function () {
			var _self = this;
			_self.time = 60;
			var interval = window.setInterval(function() {
				_self.time--;
				_self.button.disabled = true;
				_self.button.text="重新发送("+_self.time+")";
				
				if(_self.time <= 0) {
					window.clearInterval(interval);
					_self.button.disabled = false;
					_self.button.text="获取短信校验码";
				}
			}, 1000);

		},
		
		
		
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
	}
});

//更换手机绑定第一步
var updatePhoneBinding_step1_component = Vue.extend({
	template : '#updatePhoneBinding_step1-template',
	data : function data() {
		return {
			mobile: '', //手机号
			smsCode: '', //手机校验码
			successInfo:'', //验证码发送成功信息
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			time: 0,//倒计时间
			error : {
				mobile : '',
				smsCode : '',
				captchaValue : ''
			},
			button: {//获取短信校验码按钮
				disabled : false,
				text : '获取短信校验码',
			}
		}
	},
	created : function () {
		this.queryUpdatePhoneBinding_step1();
	},
	methods : {
		//查询更换绑定手机页
		queryUpdatePhoneBinding_step1 : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/updatePhoneBinding/step1",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						for (var key in returnValue) {
							if (key == "mobile") {
								var mobile = returnValue[key];
								_self.mobile = mobile;
							} else if (key == "captchaKey") {
								var captchaKey = returnValue[key];
								_self.captchaKey = captchaKey;
								_self.imgUrl = "captcha/" + captchaKey + ".jpg";
							}
						}
					}
				}
			});
		},
		
		//获取短信验证码
		getSmsCode : function () {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			//设置按钮禁用状态
			_self.button.disabled = true;
			
			
			var parameter = "";
			parameter += "&module=2";
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/getSmsCode",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							//倒计时
							_self.countdown();
							_self.successInfo = "短信验证码已发送";	
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}	
						}
					}
				}
			});
		},
		//提交数据
		submitData : function submitData(event) {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			var parameter = "";
			if(_self.smsCode != null && _self.smsCode !=''){
				parameter += "&smsCode=" + encodeURIComponent(_self.smsCode);
				
			}
			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/updatePhoneBinding/step1",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_jumpUrl = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "jumpUrl") {
								value_jumpUrl = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							_self.$router.push({
								path : '/'+value_jumpUrl
							});
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
							
							
						}
					}
				}
			});
		},
		//倒计时
		countdown : function () {
			var _self = this;
			_self.time = 60;
			var interval = window.setInterval(function() {
				_self.time--;
				_self.button.disabled = true;
				_self.button.text="重新发送("+_self.time+")";
				
				if(_self.time <= 0) {
					window.clearInterval(interval);
					_self.button.disabled = false;
					_self.button.text="获取短信校验码";
				}
			}, 1000);

		},
		
		
		
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
	}
});

//更换手机绑定第二步
var updatePhoneBinding_step2_component = Vue.extend({
	template : '#updatePhoneBinding_step2-template',
	data : function data() {
		return {
			mobile: '', //手机号
			smsCode: '', //手机校验码
			successInfo:'', //验证码发送成功信息
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			time: 0,//倒计时间
			error : {
				mobile : '',
				smsCode : '',
				captchaValue : ''
			},
			button: {//获取短信校验码按钮
				disabled : false,
				text : '获取短信校验码',
			}
		}
	},
	created : function () {
		this.queryUpdatePhoneBinding_step2();
	},
	methods : {
		//查询更换绑定新手机页
		queryUpdatePhoneBinding_step2 : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/updatePhoneBinding/step2",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						for (var key in returnValue) {
							if (key == "captchaKey") {
								var captchaKey = returnValue[key];
								_self.captchaKey = captchaKey;
								_self.imgUrl = "captcha/" + captchaKey + ".jpg";
							}
						}
					}
				}
			});
		},
		//获取短信验证码
		getSmsCode : function () {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			//设置按钮禁用状态
			_self.button.disabled = true;
			
			
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			parameter += "&module=3";
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/getSmsCode",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							//倒计时
							_self.countdown();
							_self.successInfo = "短信验证码已发送";	
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}	
						}
					}
				}
			});
		},
		//提交数据
		submitData : function submitData(event) {
			var _self = this;
			//清除所有错误
			_self.clearError();
			_self.successInfo = '';
			var parameter = "";
			if(_self.mobile != null && _self.mobile !=''){
				parameter += "&mobile=" + encodeURIComponent(_self.mobile);
			}
			
			if(_self.smsCode != null && _self.smsCode !=''){
				parameter += "&smsCode=" + encodeURIComponent(_self.smsCode);
				
			}
			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/updatePhoneBinding/step2",
				data: parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if(value_success == "true"){
							_self.$toast({
								message : "更换绑定手机成功，3秒后自动跳转到实名认证页",
								duration : 3000,
								className : "mint-ui-toast",
							});
							setTimeout(function() {
								_self.$router.push({
									path : '/user/control/realNameAuthentication'
								});
							}, 3000);
						}else{
							//设置按钮启用状态
							_self.button.disabled = false;
							
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									if (errorKey == "mobile") {
										_self.error.mobile = errorValue;
									}else if (errorKey == "smsCode") {
										_self.error.smsCode = errorValue;
									} else if (errorKey == "captchaValue") {
										_self.error.captchaValue = errorValue;
									} else if (errorKey == "token") {
										//如果令牌错误
										_self.$toast({
											message : "页面已过期，3秒后自动刷新",
											duration : 3000,
											className : "mint-ui-toast",
										});
										setTimeout(function() {
											//刷新当前页面
											window.location.reload();
										}, 3000);
									}else{
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									
								}
								if (htmlContent != "") {
									_self.$messagebox('提示', htmlContent);
								}
							}
							

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
							
							
						}
					}
				}
			});
		},
		//倒计时
		countdown : function () {
			var _self = this;
			_self.time = 60;
			var interval = window.setInterval(function() {
				_self.time--;
				_self.button.disabled = true;
				_self.button.text="重新发送("+_self.time+")";
				
				if(_self.time <= 0) {
					window.clearInterval(interval);
					_self.button.disabled = false;
					_self.button.text="获取短信校验码";
				}
			}, 1000);

		},
		
		
		
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},

		//清除所有错误
		clearError : function clearError(event) {
			for (var e in this.error) {
				this.error[e] = '';
			}
		}
		
		
		
	}
});



//登录日志
var userLoginLog_component = Vue.extend({
	template : '#userLoginLog-template',
	data : function data() {
		return {
			userLoginLogList : [], //登录日志集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		//this.queryUserLoginLog();
	},
	methods : {
		//查询登录日志页
		queryUserLoginLog : function() {
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/userLoginLogList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);

							var new_userLoginLogList = pageView.records;
							if (new_userLoginLogList != null && new_userLoginLogList.length > 0) {
								_self.userLoginLogList.push.apply(_self.userLoginLogList, new_userLoginLogList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
			
		}
	}
});


//私信
var privateMessage_component = Vue.extend({
	name: 'privateMessage',//组件名称，keep-alive缓存需要本参数
	template : '#privateMessage-template',
	data : function data() {
		return {
			privateMessageList : [], //私信集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryPrivateMessage();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['privateMessage']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/user/control/privateMessageChatList') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['privateMessage']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	methods : {
		//查询私信列表
		queryPrivateMessage : function() {
			
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/privateMessageList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);

							var new_privateMessageList = pageView.records;
							if (new_privateMessageList != null && new_privateMessageList.length > 0) {
								_self.privateMessageList.push.apply(_self.privateMessageList, new_privateMessageList); //合并两个数组
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (new_privateMessageList != null && new_privateMessageList.length > 0) {
										for(var i=0;i<new_privateMessageList.length; i++){
											var privateMessage = new_privateMessageList[i];
											if(privateMessage.friendAvatarName == null || privateMessage.friendAvatarName == ''){
												var char = (privateMessage.friendNickname != null && privateMessage.friendNickname !="") ? privateMessage.friendNickname : privateMessage.friendAccount;
												//元素的实际宽度
												var width= _self.$refs['privateMessageAvatarData_'+privateMessage.id][0].offsetWidth;
												_self.$refs['privateMessageAvatarData_'+privateMessage.id][0].src = letterAvatar(char, width);	
											}
										}
									}
								});
								
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除私信
		deletePrivateMessage : function(friendUserName) {
			var _self = this;
			_self.$messagebox.confirm('确定删除私信?').then(function (action) {
				var parameter = "&friendUserName=" + friendUserName;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deletePrivateMessage",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//修改成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});
								setTimeout(function() {
									_self.privateMessageList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新所有私信
									_self.queryPrivateMessage();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			});
		}
	}
});


//私信对话
var privateMessageChat_component = Vue.extend({
	template : '#privateMessageChat-template',
	data : function data() {
		return {
			privateMessageChatList : [], //私信对话集合
			friendUserName:'',//对方用户名称
			friendUserNameTitle:'',//对方用户名称标题
			loading : false, //加载中
			currentpage : null, //当前页码
			totalrecord : 0, //对话总数
			topText:'下拉加载更多',//下拉显示文本信息
			
			popup_privateMessage :false,//发私信弹出层
			messageContent:'',//发私信内容

			showCaptcha : false, //发表评论/引用评论/发表回复是否显示验证码
			imgUrl : '', //验证码图片
			captchaKey : '', //验证码key
			captchaValue : '', //验证码value
			error : {
				messageContent : '',
				captchaValue : '',
				privateMessage: '',
			},
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},
	methods : {
		//查询私信对话列表
		queryPrivateMessageChat : function(callback) {
			var _self = this;
			if (_self.currentpage ==null || _self.currentpage >0) {
				_self.loading = true;
				var data = "";
				if(_self.currentpage >1){
					data = "page=" + (_self.currentpage - 1); //提交参数
				}
				
				data += "&friendUserName=" + _self.friendUserName;
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/privateMessageChatList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var returnValue = $.parseJSON(result);
							
							var chatUser = null;
							var pageView = null;

							for (var key in returnValue) {
								if (key == "chatUser") {
									chatUser = returnValue[key];
								}else if (key == "pageView") {
									pageView = returnValue[key];
								}
							}
							if(chatUser != null && (chatUser.nickname == null || chatUser.nickname != '')){
								_self.friendUserNameTitle = "与 "+chatUser.account+" 的对话";
							}
							if(chatUser != null && chatUser.nickname != null && chatUser.nickname != ''){
								_self.friendUserNameTitle = "与 "+chatUser.account+" ("+chatUser.nickname+") 的对话";
							}
							
							//第一个Id
							var lastId = "";
							var new_privateMessageChatList = pageView.records;
							if (new_privateMessageChatList != null && new_privateMessageChatList.length > 0) {
								_self.privateMessageChatList.unshift.apply(_self.privateMessageChatList, new_privateMessageChatList); //合并两个数组
								
								for(var i=0; i<new_privateMessageChatList.length; i++){
									var privateMessageChat = new_privateMessageChatList[i];
									lastId = privateMessageChat.id;
								}
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (_self.privateMessageChatList != null && _self.privateMessageChatList.length > 0) {
										for(var i=0;i<_self.privateMessageChatList.length; i++){
											var privateMessageChat = _self.privateMessageChatList[i];
											if((privateMessageChat.image == undefined || privateMessageChat.image == null) && (privateMessageChat.senderAvatarName == null || privateMessageChat.senderAvatarName == '')){

												var char = (privateMessageChat.senderNickname != null && privateMessageChat.senderNickname !="") ? privateMessageChat.senderNickname : privateMessageChat.senderAccount;
												//元素的实际宽度
												var width= _self.$refs['privateMessageChatAvatarData_'+privateMessageChat.id][0].offsetWidth;
												//_self.$refs['privateMessageChatAvatarData_'+privateMessageChat.id][0].src = letterAvatar(char, width);	
												//设置base64图片进image属性
												privateMessageChat.image = letterAvatar(char, width);
												_self.$set(_self.privateMessageChatList, i, privateMessageChat);
											}
										}
									}
								});
							}
							
							_self.totalrecord = pageView.totalrecord;
							_self.currentpage = pageView.currentpage;
							if(pageView.currentpage ==1){//如果为第一页，则下次不再查询
								_self.currentpage = 0;
							}
							
							_self.$nextTick(function() {
								//跳转到锚点
								if(lastId != null && lastId != ""){
									var anchor = _self.$el.querySelector("#anchor_"+lastId);
									if(anchor != null){
										document.body.scrollTop = anchor.offsetTop; // chrome
								        document.documentElement.scrollTop = anchor.offsetTop; // firefox
									}
								}
							});
							
							if(typeof callback === 'function') {
								//回调
								callback();
							}
							
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						_self.$refs.loadmore.onTopLoaded();//查询完要调用一次，用于重新定位
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}else{
				_self.$refs.loadmore.onTopLoaded();//查询完要调用一次，用于重新定位
			}
		},
		

		//消息滚动到底部
		messageScrollBottom : function() {
			var _self = this;
			_self.$nextTick(function() {
				setTimeout(function() {
	                document.scrollingElement.scrollTop = document.scrollingElement.scrollHeight;
	            }, 100);
			});
		},
		//添加私信UI
		addPrivateMessageUI : function(friendUserName) {
			var _self = this;
			this.popup_privateMessage = true;

			//查询添加评论页
			this.queryAddPrivateMessage();
		},
		//添加私信
		addPrivateMessage : function() {
			/**
			if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
				return;
			}**/
			var _self = this;
			//清空所有错误
			_self.error.messageContent = "";
			_self.error.privateMessage = "";
			_self.error.captchaValue = "";
			
			var parameter = "&friendUserName=" + _self.friendUserName; //提交参数
			if (_self.messageContent != null && _self.messageContent != "") {
				parameter += "&messageContent=" + encodeURIComponent(_self.messageContent);
			}
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/addPrivateMessage",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							_self.popup_privateMessage = false;

							
							//清空分页数据
							_self.privateMessageChatList = []; //私信对话列表
							_self.currentpage = null; //当前页码
							_self.messageContent = '';//发私信内容
							//查询私信对话列表
							_self.queryPrivateMessageChat(function (returnValue){
								//消息滚动到底部
								_self.messageScrollBottom();
							});
							
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "messageContent") {
											_self.error.messageContent = value_error[error];
										} else if (error == "privateMessage") {
											_self.error.privateMessage = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});

		},
		//查询添加私信页
		queryAddPrivateMessage : function() {
			var _self = this;

			//清空表单
			_self.showCaptcha = false, //是否显示验证码
			_self.imgUrl = ''; //验证码图片
			_self.captchaKey = ''; //验证码key
			_self.captchaValue = ''; //验证码value
	
			var parameter = "&friendUserName=" + _self.friendUserName; //提交参数
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/addPrivateMessage",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							for (var key in returnValue) {
								if (key == "allowSendPrivateMessage") {
									if(returnValue[key] == false){
										_self.popup_reply = false;
										_self.$toast({
											message : "不允许发私信",
											duration : 3000,
										});
									}	
								}else if (key == "formCaptcha") {
									//显示验证码
									var formCaptcha = returnValue[key];
									if (formCaptcha.showCaptcha == true) {
										//alert(result);
										_self.showCaptcha = true;
										_self.captchaKey = formCaptcha.captchaKey;
										_self.imgUrl = "captcha/" + formCaptcha.captchaKey + ".jpg";
										//设置验证码图片
										_self.replaceCaptcha();
									}
									
									
									
								}
							}
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addPrivateMessageFormScroll);
						});
					}
				}
			});
		},
		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
		
		//初始化
		initialization : function() {
			
			var friendUserName = getUrlParam("friendUserName");
			if(friendUserName != null){
				this.friendUserName = friendUserName;
			//	this.friendUserNameTitle = "与 "+friendUserName+" 的对话";
			}
			this.queryPrivateMessageChat();
			
			//消息滚动到底部
			this.messageScrollBottom();
		},
		//初始化BScroll滚动插件//this.$refs.addPrivateMessageFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				//preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT|HIDE)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
				//eventPassthrough :'horizontal',//解决文本无法复制
				HWCompositing : true, //是否启用硬件加速
			});
		},
	}
});


//系统通知
var systemNotify_component = Vue.extend({
	template : '#systemNotify-template',
	data : function data() {
		return {
			subscriptionSystemNotifyList : [], //订阅系统通知集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.querySystemNotify();
		
	},
	methods : {
		//查询系统通知列表
		querySystemNotify : function() {
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/systemNotifyList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);

							var new_subscriptionSystemNotifyList = pageView.records;
							if (new_subscriptionSystemNotifyList != null && new_subscriptionSystemNotifyList.length > 0) {
								_self.subscriptionSystemNotifyList.push.apply(_self.subscriptionSystemNotifyList, new_subscriptionSystemNotifyList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除系统通知
		deleteSystemNotify : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定删除系统通知?').then(function (action) {
				var parameter = "&subscriptionSystemNotifyId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deleteSystemNotify",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//修改成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.subscriptionSystemNotifyList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新系统通知
									_self.querySystemNotify();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			});
		}
	}
});


//提醒
var remind_component = Vue.extend({
	name: 'remind',//组件名称，keep-alive缓存需要本参数
	template : '#remind-template',
	data : function data() {
		return {
			remindList : [], //提醒集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryRemind();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['remind']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/user/control/home' 
			|| to.path == '/thread'
			|| to.path == '/question') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['remind']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	methods : {
		//查询提醒列表
		queryRemind : function() {
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/remindList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);

							var new_remindList = pageView.records;
							if (new_remindList != null && new_remindList.length > 0) {
								_self.remindList.push.apply(_self.remindList, new_remindList); //合并两个数组
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (_self.remindList != null && _self.remindList.length > 0) {
										for(var i=0;i<_self.remindList.length; i++){
											var remind = _self.remindList[i];
											if((remind.image == undefined || remind.image == null) && (remind.senderAvatarName == null || remind.senderAvatarName == '')){
												var char = (remind.senderNickname != null && remind.senderNickname !="") ? remind.senderNickname : remind.senderAccount;
												//元素的实际宽度
												var width= _self.$refs['remindSenderAvatarData_'+remind.id][0].offsetWidth;
												//_self.$refs['remindSenderAvatarData_'+remind.id][0].src = letterAvatar(char, width);
												remind.image = letterAvatar(char, width);
												_self.$set(_self.remindList, i, remind);
											}
										}
									}
								});
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除提醒
		deleteRemind : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定删除提醒?').then(function (action) {
				var parameter = "&remindId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deleteRemind",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//修改成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.remindList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新提醒
									_self.queryRemind();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		}
	}

});


//收藏夹
var favorite_component = Vue.extend({
	template : '#favorite-template',
	data : function data() {
		return {
			favoriteList : [], //收藏夹集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryFavorite();
	},
	methods : {
		//查询收藏夹分页
		queryFavorite : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/favoriteList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_favoriteList = pageView.records;
							if (new_favoriteList != null && new_favoriteList.length > 0) {
								_self.favoriteList.push.apply(_self.favoriteList, new_favoriteList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除收藏夹
		deleteFavorite : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定删除收藏?').then(function (action) {
				var parameter = "&favoriteId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deleteFavorite",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//删除成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.favoriteList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新系统通知
									_self.queryFavorite();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		}
	}
});



//话题收藏列表
var topicFavorite_component = Vue.extend({
	name: 'topicFavorite',//组件名称，keep-alive缓存需要本参数
	template : '#topicFavorite-template',
	data : function data() {
		return {
			favoriteList : [], //收藏集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryTopicFavorite();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['topicFavorite']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/user/control/home') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['topicFavorite']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},

	methods : {
		//查询收藏分页
		queryTopicFavorite : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				data += "&topicId="+getUrlParam("topicId");
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/topicFavoriteList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_favoriteList = pageView.records;
							if (new_favoriteList != null && new_favoriteList.length > 0) {
								_self.favoriteList.push.apply(_self.favoriteList, new_favoriteList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		
		//跳转到用户
		toUser : function(userName) {
			this.$router.push({
				path : '/user/control/home',
				query : {
					userName : userName
				}
			});
		}
	}
});

//点赞
var like_component = Vue.extend({
	template : '#like-template',
	data : function data() {
		return {
			likeList : [], //点赞集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryLike();
	},
	methods : {
		//查询点赞分页
		queryLike : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/likeList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_likeList = pageView.records;
							if (new_likeList != null && new_likeList.length > 0) {
								_self.likeList.push.apply(_self.likeList, new_likeList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除点赞
		deleteLike : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定删除点赞?').then(function (action) {
				var parameter = "&likeId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deleteLike",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//删除成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.likeList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新
									_self.queryLike();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		}
	}
});

//话题点赞列表
var topicLike_component = Vue.extend({
	name: 'topicLike',//组件名称，keep-alive缓存需要本参数
	template : '#topicLike-template',
	data : function data() {
		return {
			likeList : [], //点赞集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryTopicLike();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['topicLike']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/user/control/home') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['topicLike']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	methods : {
		//查询点赞分页
		queryTopicLike : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				data += "&topicId="+getUrlParam("topicId");
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/topicLikeList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_likeList = pageView.records;
							if (new_likeList != null && new_likeList.length > 0) {
								_self.likeList.push.apply(_self.likeList, new_likeList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		
		//跳转到用户
		toUser : function(userName) {
			this.$router.push({
				path : '/user/control/home',
				query : {
					userName : userName
				}
			});
		}
	}
});

//关注
var follow_component = Vue.extend({
	template : '#follow-template',
	data : function data() {
		return {
			followList : [], //关注集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryFollow();
	},
	methods : {
		//查询关注分页
		queryFollow : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/followList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_followList = pageView.records;
							if (new_followList != null && new_followList.length > 0) {
								_self.followList.push.apply(_self.followList, new_followList); //合并两个数组
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (new_followList != null && new_followList.length > 0) {
										for(var i=0;i<new_followList.length; i++){
											var follow = new_followList[i];
											if(follow.friendAvatarName == null || follow.friendAvatarName == ''){
												var char = (follow.friendNickname != null && follow.friendNickname !="") ? follow.friendNickname : follow.friendAccount;
												//元素的实际宽度
												var width= _self.$refs['followUserAvatarData_'+follow.id][0].offsetWidth;
												_self.$refs['followUserAvatarData_'+follow.id][0].src = letterAvatar(char, width);	
											}
										}
									}
								});
								
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//删除关注
		deleteFollow : function(id) {
			var _self = this;
			_self.$messagebox.confirm('确定删除关注?').then(function (action) {
				var parameter = "&followId=" + id;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/deleteFollow",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//删除成功
							if (value_success == "true") {
								_self.$toast({
									message : "删除成功",
									duration : 3000,
									className : "mint-ui-toast",
								});

								setTimeout(function() {
									_self.followList = [];
									_self.currentpage = 0;
									_self.totalpage = 1;
									//刷新
									_self.queryFollow();
								}, 3000);
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		}
	}
});

//粉丝
var follower_component = Vue.extend({
	template : '#follower-template',
	data : function data() {
		return {
			followerList : [], //粉丝集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		this.queryFollower();
	},
	methods : {
		//查询粉丝分页
		queryFollower : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/followerList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_followerList = pageView.records;
							if (new_followerList != null && new_followerList.length > 0) {
								_self.followerList.push.apply(_self.followerList, new_followerList); //合并两个数组
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (new_followerList != null && new_followerList.length > 0) {
										for(var i=0;i<new_followerList.length; i++){
											var follower = new_followerList[i];
											if(follower.friendAvatarName == null || follower.friendAvatarName == ''){
												var char = (follower.friendNickname != null && follower.friendNickname !="") ? follower.friendNickname : follower.friendAccount;
												//元素的实际宽度
												var width= _self.$refs['followerUserAvatarData_'+follower.id][0].offsetWidth;
												_self.$refs['followerUserAvatarData_'+follower.id][0].src = letterAvatar(char, width);	
											}
										}
									}
								});
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
	}
});

//话题取消隐藏用户列表
var topicUnhide_component = Vue.extend({
	name: 'topicUnhide',//组件名称，keep-alive缓存需要本参数
	template : '#topicUnhide-template',
	data : function data() {
		return {
			topicUnhideList : [], //取消隐藏用户集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
	//	this.queryTopicUnhide();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['topicUnhide']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/user/control/home') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['topicUnhide']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},
	methods : {
		//查询取消隐藏用户分页
		queryTopicUnhide : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				data += "&topicId="+getUrlParam("topicId");
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/topicUnhideList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_topicUnhideList = pageView.records;
							if (new_topicUnhideList != null && new_topicUnhideList.length > 0) {
								_self.topicUnhideList.push.apply(_self.topicUnhideList, new_topicUnhideList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		
		//跳转到用户
		toUser : function(userName) {
			this.$router.push({
				path : '/user/control/home',
				query : {
					userName : userName
				}
			});
		}
	}
});


//会员卡订单
var membershipCardOrderList_component = Vue.extend({
	template : '#membershipCardOrderList-template',
	data : function data() {
		return {
			membershipCardOrderList : [], //会员卡订单集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
	},
	methods : {
		//查询余额
		queryMembershipCardOrderList : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/membershipCardOrderList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							var new_membershipCardOrderList = pageView.records;
							if (new_membershipCardOrderList != null && new_membershipCardOrderList.length > 0) {
								_self.membershipCardOrderList.push.apply(_self.membershipCardOrderList, new_membershipCardOrderList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		}
		
	}
	
	
	
	
	
});


//余额
var balance_component = Vue.extend({
	template : '#balance-template',
	data : function data() {
		return {
			paymentLogList : [], //支付日志集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			deposit :0.0 //预存款
		}
	},
	created : function created() {
	},
	methods : {
		//查询会员卡订单分页
		queryBalance : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/balance",
					data : data,
					success : function success(result) {
						if (result != "") {
							var returnValue = $.parseJSON(result);
							//预存款
							var deposit = null;
							var pageView = null;
							

							for (var key in returnValue) {
								if (key == "deposit") {
									deposit = returnValue[key];
								}else if (key == "pageView") {
									pageView = returnValue[key];
								}
							}
							
							if(deposit != null){
								_self.deposit = deposit;
							}
							if(pageView != null){
								var new_paymentLogList = pageView.records;
								if (new_paymentLogList != null && new_paymentLogList.length > 0) {
									_self.paymentLogList.push.apply(_self.paymentLogList, new_paymentLogList); //合并两个数组
								}
								_self.currentpage = pageView.currentpage;
								_self.totalpage = pageView.totalpage;
								
							}
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		//跳转充值页
		jumpRecharge : function () {
			// 跳转页面
			this.$router.push({
				path : '/user/control/payment',
				query : {
					paymentModule : 5
				}
			});
		}
		
		
	}
});

//支付组件
var payment_component = Vue.extend({
	template : '#payment-template',
	data : function() {
		return {
			paymentModule : '', //支付模块
			
			onlinePaymentInterfaceList : [], //在线支付接口集合

			rechargeAmount : '', //充值金额
			paymentBank_radio : '', //选中银行(格式：接口产品_银行简码)

			error : {
				rechargeAmount : '', //充值金额
			},
		};
	},
	created : function() {
		this.initialization();
	},
	methods : {
		//查询支付页
		queryPayment : function() {
			var _self = this;

			var parameter = "&paymentModule=" + _self.paymentModule;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/payment",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						var value_onlinePaymentInterfaceList = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							}else if (key == "onlinePaymentInterfaceList") {
								value_onlinePaymentInterfaceList = returnValue[key];
							}
						}
						if (value_success == "true") { //成功
							if (value_onlinePaymentInterfaceList != null) {
								_self.onlinePaymentInterfaceList = value_onlinePaymentInterfaceList;
							}


						} else { //失败
							//显示错误
							if (value_error != null) {
								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];

									count++;
									htmlContent += count + ". " + errorValue + "<br>";
								}
								_self.$messagebox('提示', htmlContent);
							}

						}
					}

				}
			});
		},

		//选择支付银行
		selectPayment : function(interfaceProduct, code) {
			this.paymentBank_radio = interfaceProduct + "_" + code;
		},
		//支付校验
		paymentVerification : function() {
			var _self = this;
			//清除所有错误
			_self.clearError();

			var parameter = "";
			parameter += "&paymentModule=" + _self.paymentModule; //支付模块
			parameter += "&rechargeAmount=" + _self.rechargeAmount; //充值金额
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/paymentVerification",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							}
						}
						if (value_success == "false") { //失败
							//显示错误
							if (value_error != null) {

								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];
									count++;
									htmlContent += count + ". " + errorValue + "<br>";
								}
								_self.$messagebox('提示', htmlContent);
							}
						}
					}
				}
			});
		},

		//付款
		pay : function() {
			var _self = this;

			//清除所有错误
			_self.clearError();

			var parameter = "";
			parameter += "&paymentModule=" + _self.paymentModule; //支付模块
			parameter += "&rechargeAmount=" + _self.rechargeAmount; //充值金额
			if (_self.paymentBank_radio != null && _self.paymentBank_radio != "") {
				parameter += "&paymentBank=" + _self.paymentBank_radio; //支付银行(格式：接口产品_银行简码)
			}


			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/payment",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						var value_success = "";
						var value_error = null;
						
						var value_onlinePaymentInterfaceList = null;

						var value_redirect = null;
						var value_callbackData = null;
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "onlinePaymentInterfaceList") {
								value_onlinePaymentInterfaceList = returnValue[key];
							} else if (key == "redirect") { //支付完成后跳转成功页
								value_redirect = returnValue[key];
							} else if (key == "callbackData") { //跳转到第三方支付页回调数据
								value_callbackData = returnValue[key];
							}
						}
						if (value_success == "true") { //成功
							if (value_redirect != null && value_redirect != "") {
								//跳转到支付完成通知页
								_self.$router.push({
									path : '/' + value_redirect
								});
							}
							if (value_callbackData != null && value_callbackData != "") {
								//根据第三方支付返回的回调数据跳转到第三方支付
								var div = document.createElement('div');
								div.innerHTML = value_callbackData;
								document.body.appendChild(div);
								document.forms[0].submit();
							}
						} else { //失败

							//显示错误
							if (value_error != null) {

								var htmlContent = "";
								var count = 0;
								for (var errorKey in value_error) {
									var errorValue = value_error[errorKey];

									count++;
									htmlContent += count + ". " + errorValue + "<br>";
								}
								_self.$messagebox('提示', htmlContent);
							}
							
							if (value_onlinePaymentInterfaceList != null) {
								_self.onlinePaymentInterfaceList = value_onlinePaymentInterfaceList;
							}
						}
					}
				}
			});
		},


		//清除所有错误
		clearError : function() {
			for (var e in this.error) {
				this.error[e] = '';
			}
		},



		//初始化数据
		initialization : function() {
			var paymentModule = getUrlParam("paymentModule");
			if (paymentModule != null && paymentModule != "") {
				this.paymentModule = paymentModule;
			}
			//查询支付页
			this.queryPayment();
		},
	}
});


//支付完成通知组件
var paymentCompleted_component = Vue.extend({
	template : '#paymentCompleted-template',
	data : function() {
		return {
			paymentModule : '', //支付模块
		};
	},
	created : function() {
		//获取URL中的参数组
		var pathName = window.location.pathname;
		var before = pathName.indexOf("/paymentCompleted/");
		var point = pathName.indexOf(".");
		if (point == -1) {
			point = pathName.length;
		}
		var parameterGroup = pathName.substring(before + 18, point);
		var parameter_arr = parameterGroup.split("/");
		if (parameter_arr != null && parameter_arr.length == 3) {
			var interfaceProduct = parameter_arr[0];
			var paymentModule = parameter_arr[1];
			var parameterId = parameter_arr[2];
			this.paymentModule = parseInt(paymentModule);
		}
	},
	methods : {
		//前往用户中心
		queryHome : function() {
			this.$router.replace({
				path : '/user/control/home'
			});
		}
	}
});

//会员卡列表
var membershipCardList_component = Vue.extend({
	template : '#membershipCardList-template',
	data : function data() {
		return {
			membershipCardList: '',//会员卡列表
		};
	},
	created : function created() {
		this.queryMembershipCardList();
	},
	methods : {
		//查询会员卡列表
		queryMembershipCardList : function() {
			var _self = this;
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryMembershipCardList",
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null && returnValue.length > 0) {
							_self.membershipCardList = returnValue;
						}
					}
				}
			});
		},
	},
});

//会员卡
var membershipCard_component = Vue.extend({
	template : '#membershipCard-template',
	data : function data() {
		return {
			membershipCardId :'',//会员卡Id
			membershipCard: '',//会员卡
			buttonDisabled:[],//提交按钮是否允许点击
		};
	},
	created : function created() {
		var membershipCardId = getUrlParam("membershipCardId");//会员卡Id
		if(membershipCardId != null){
			this.membershipCardId = membershipCardId;
		}
		this.queryMembershipCard();
	},
	methods : {
		//查询会员卡
		queryMembershipCard : function() {
			var _self = this;
			var data = "membershipCardId=" + _self.membershipCardId; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryMembershipCard",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							_self.membershipCard = returnValue;
							if(_self.membershipCard != null){
								for (var i = 0; i < _self.membershipCard.specificationList.length; i++) {
									var specification = _self.membershipCard.specificationList[i];
									if(specification.stock >=0){
										_self.buttonDisabled.push(false);
										
									}else{
										_self.buttonDisabled.push(true);
									}
								}
							}
							
							
						}
					}
				}
			});
		},
		//购买会员卡
		addMembershipCardOrder : function(index,specificationId) {
			var _self = this;
			_self.buttonDisabled.splice(index,1,true);//修改为不允许点击按钮
			
			_self.$messagebox.confirm('确定购买?').then(function (action) {
				var parameter = "&specificationId=" + specificationId;

				//	alert(parameter);
				//令牌
				parameter += "&token=" + _self.$store.state.token;
				$.ajax({
					type : "POST",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/membershipCard/add",
					data : parameter,
					success : function success(result) {
						if (result != "") {

							var returnValue = $.parseJSON(result);

							var value_success = "";
							var value_error = null;

							for (var key in returnValue) {
								if (key == "success") {
									value_success = returnValue[key];
								} else if (key == "error") {
									value_error = returnValue[key];
								}
							}

							//加入成功
							if (value_success == "true") {
								_self.$toast({
									message : "购买会员卡成功",
									duration : 3000,
									className : "mint-ui-toast",
								});
								/**
								setTimeout(function() {
									//刷新用户是否已经点赞该话题
									_self.queryAlreadyLiked();
									//刷新话题点赞总数
									_self.queryLikeCount();
								}, 3000);**/
								
							} else {
								//显示错误
								if (value_error != null) {


									var htmlContent = "";
									var count = 0;
									for (var errorKey in value_error) {
										var errorValue = value_error[errorKey];
										count++;
										htmlContent += count + ". " + errorValue + "<br>";
									}
									_self.$messagebox('提示', htmlContent);

								}
							}
						}
					}
				});
			},function (action) {//取消回调
				_self.buttonDisabled.splice(index,1,false);//修改为允许点击按钮
			}).catch(function (err){//不捕获 Promise 的异常,若用户点击了取消按钮,会出现警告
			    console.log(err);
			});
		}
	},
});





//修改话题
var editTopic_component = Vue.extend({
	template : '#editTopic-template',
	data : function data() {
		return {
			topicId: '',//话题Id
			topic: '',//话题
			topicTitle:'',//发表话题标题
			topicContent:'',//发表话题内容
			showCaptcha : false, //发表话题是否显示验证码
			imgUrl : '', //发表话题验证码图片
			captchaKey : '', //发表话题验证码key
			captchaValue : '', //发表话题验证码value
			error : {
				topicTitle : '',
				topicContent: '',
				captchaValue : '',
				topic: '',
			},
			topicEditor:'',//富文本编辑器
		};
	},	
	created : function created() {
		this.topicId = getUrlParam("topicId");//话题Id
		this.queryEditTopic();
	},
	methods : {
		//查询修改话题页
		queryEditTopic : function() {
			var _self = this;
			var data = "";
			var topicId = _self.topicId;//话题Id
			data = "&topicId=" + topicId;
			
			
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "queryEditTopic",
				data : data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if (returnValue != null) {
							var availableTag_value = null;
							var userGradeList_value = null;
							
							for (var key in returnValue) {
								if (key == "allowTopic") {
									if(returnValue[key] == false){
										_self.$toast({
											message : "不允许修改话题",
											duration : 3000,
										});
									}	
								}else if (key == "topic") {
									_self.topic = returnValue[key];
									if(_self.topic != ""){
										_self.topicTitle = _self.topic.title;
										_self.topicContent = _self.topic.content;
									}
									
									
								}else if (key == "availableTag") {
									availableTag_value = returnValue[key];
								}else if (key == "userGradeList") {
									userGradeList_value = returnValue[key];
								}else if (key == "captchaKey") {
									//显示验证码
									var value_captchaKey = returnValue[key];
									_self.showCaptcha = true;
									_self.captchaKey = value_captchaKey;
									_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
									//设置验证码图片
									_self.replaceCaptcha();
								}
							}
							
							//等级
							var userGradeList = null;
							
							//编辑器图标
							var editorIconList = new Array();
							
							if(availableTag_value != null){//话题编辑器允许使用标签
								var availableTagList = $.parseJSON(availableTag_value);
								for(var i=0; i<availableTagList.length; i++){
									var _availableTag = availableTagList[i];
									
									if(_availableTag == "code"){//代码
										editorIconList.push("code");
									}else if(_availableTag == "forecolor"){//文字颜色
									//	editorIconList.push("foreColor");
									}else if(_availableTag == "hilitecolor"){//文字背景
									//	editorIconList.push("backColor");
									}else if(_availableTag == "bold"){//粗体
										editorIconList.push("bold");
									}else if(_availableTag == "italic"){//斜体
										editorIconList.push("italic");
									}else if(_availableTag == "underline"){//下划线
										editorIconList.push("underline");
									}else if(_availableTag == "link"){//插入链接
										editorIconList.push("link");
									}else if(_availableTag == "emoticons"){//插入表情
										editorIconList.push("emoticon");
									}else if(_availableTag == "image"){//图片
										editorIconList.push("image");
									}else if(_availableTag == "media"){//视频
										editorIconList.push("video");
									}else if(_availableTag == "embedVideo"){//嵌入视频
										editorIconList.push("embedVideo");
									}else if(_availableTag == "uploadVideo"){//上传视频
										editorIconList.push("uploadVideo");
									}else if(_availableTag == "insertfile"){//文件
										editorIconList.push("file");
									}else if(_availableTag == "hidePassword"){//输入密码可见
										editorIconList.push("hidePassword");
									}else if(_availableTag == "hideComment"){//评论话题可见
										editorIconList.push("hideComment");
									}else if(_availableTag == "hideGrade"){//达到等级可见
										editorIconList.push("hideGrade");
									}else if(_availableTag == "hidePoint"){//积分购买可见
										editorIconList.push("hidePoint");
									}else if(_availableTag == "hideAmount"){//余额购买可见
										editorIconList.push("hideAmount");
									}
								}
							}
							
							for(var i=0; i< editorIconList.length; i++){
								var editorIcon = editorIconList[i];
								if(editorIcon == "hidePassword" || editorIcon == "hideComment" ||
										editorIcon == "hideGrade" || editorIcon == "hidePoint" || editorIcon == "hideAmount"){
									editorIconList.splice(i, 0, 'hide');//在指定索引处插入元素
									break;
								}
							}
							
							if(userGradeList_value != null){//会员等级
								userGradeList = $.parseJSON(userGradeList_value);
							}
							
						//	_self.$refs.topicContentEditorToolbar.innerHTML = "";
						//	_self.$refs.topicContentEditorText.innerHTML = _self.topicContent;
							
							
							//创建编辑器
							_self.topicEditor = createEditor(_self.$refs.topicContentEditorToolbar,_self.$refs.topicContentEditorText,_self.$store.state.commonPath,editorIconList,userGradeList,'user/control/topic/upload',_self,"topicContent");
							_self.topicEditor.txt.html(_self.topicContent);//初始化内容
							//_self.topicEditor.txt.clear();//清空
							
						}
						
					}
				}
			});
		},
		//修改话题
		editTopic : function() {
			var _self = this;
			//清空所有错误
			_self.error.topicTitle = "";
			_self.error.topicContent = "";
			_self.error.topic = "";
			_self.error.captchaValue = "";

			var parameter = "&topicId="+_self.topicId; //提交参数
			if (_self.topicTitle != null && _self.topicTitle != "") {
				parameter += "&title=" + encodeURIComponent(_self.topicTitle);
			}
			
			
			parameter += "&content=" + encodeURIComponent(_self.topicEditor.txt.html());
			
			
			//验证码Key
			parameter += "&captchaKey=" + encodeURIComponent(_self.captchaKey);

			//验证码值
			parameter += "&captchaValue=" + encodeURIComponent(_self.captchaValue.trim());

			//令牌
			parameter += "&token=" + _self.$store.state.token;
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/topic/edit",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						var value_success = "";
						var value_error = null;
						var value_captchaKey = null;

						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "captchaKey") {
								//显示验证码
								value_captchaKey = returnValue[key];
							}
						}

						//提交成功
						if (value_success == "true") {
							_self.$toast({
								message : "提交成功",
								duration : 3000,
							});
							setTimeout(function() {
								//跳转到问答内容页
								_self.$router.push({
									path : '/thread',
									query : {
										topicId : _self.topicId
									}
								});
								
							}, 3000);
						} else {
							//显示错误
							if (value_error != null) {
								//有错误时清除验证码
								_self.captchaValue = "";
								var error_html = "";
								for (var error in value_error) {
									if (error != "") {
										if (error == "title") {
											_self.error.topicTitle = value_error[error];
										}else if (error == "content") {
											_self.error.topicContent = value_error[error];
										} else if (error == "topic") {
											_self.error.topic = value_error[error];
										}  else if (error == "captchaValue") {
											_self.error.captchaValue = value_error[error];
										} else if (error == "token") {
											//如果令牌错误
											_self.$toast({
												message : "页面已过期，3秒后自动刷新",
												duration : 3000,
											});
											setTimeout(function() {
												//刷新当前页面
												window.location.reload();
											}, 3000);
										}
									}
								}
							}

							if (value_captchaKey != null) {
								_self.showCaptcha = true;
								_self.captchaKey = value_captchaKey;
								_self.imgUrl = "captcha/" + value_captchaKey + ".jpg";
								//设置验证码图片
								_self.replaceCaptcha();
							}
						}
					}
				}
			});
		},
		//更换验证码
		replaceCaptcha : function(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},
		//验证验证码
		validation_captchaValue : function() {
			var _self = this;
			var cv = this.captchaValue.trim();
			_self.error.captchaValue = "";
			if (cv.length < 4) {
				_self.error.captchaValue = "请填写完整验证码";
			}
			if (cv.length >= 4) {
				var parameter = "";
				parameter += "&captchaKey=" + _self.captchaKey;
				parameter += "&captchaValue=" + cv;

				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "userVerification",
					data : parameter,
					success : function success(result) {
						if (result == "false") {
							_self.error.captchaValue = "验证码错误";
						}
					},
					beforeSend : function beforeSend(XMLHttpRequest) {
						//发送请求前
						_self.$indicator.open({
							spinnerType : 'fading-circle'
						}); //显示旋转进度条

						//清除验证码错误
						_self.error.captchaValue = "";
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						//请求完成后回调函数 (请求成功或失败时均调用)
						_self.$indicator.close(); //关闭旋转进度条
					}
				});
			}
		},
	},
});


//发红包话题
var giveRedEnvelopeList_component = Vue.extend({
	name: 'giveRedEnvelopeList',//组件名称，keep-alive缓存需要本参数
	template : '#giveRedEnvelopeList-template',
	data : function data() {
		return {
			giveRedEnvelopeList : [], //话题集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
			
			operating: false,//是否显示操作页面
			operatingData: '',//弹出操作菜单
	
		}
	},
	created : function created() {
		//初始化
		this.initialization();
		//设置缓存
		this.$store.commit('setCacheComponents',  ['giveRedEnvelopeList']);
	},
	beforeRouteLeave: function (to, from, next) {
		if (to.path == '/thread'
			|| to.path == '/user/control/redEnvelopeAmountDistributionList') {//前往的URI路径需要缓存组件，其他情况下不需要缓存
			this.$store.commit('setCacheComponents',  ['giveRedEnvelopeList']);
		} else {
			this.$store.commit('setCacheComponents',  []);
		}
		next();
	},

	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.initialization();
	},
	methods : {	
		//查询发红包列表
		queryGiveRedEnvelopeList : function() {
			var _self = this;
			var data = "";
			data += "&page=" + _self.currentpage; //提交参数
			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/giveRedEnvelopeList",
				data : data,
				success : function success(result) {
					if (result != "") {
						var pageView = $.parseJSON(result);
						var new_giveRedEnvelopeList = pageView.records;
						if (new_giveRedEnvelopeList != null && new_giveRedEnvelopeList.length > 0) {
							_self.giveRedEnvelopeList = new_giveRedEnvelopeList;
					
						}
						_self.currentpage = pageView.currentpage;
						_self.totalpage = pageView.totalpage;
						if(pageView.currentpage != 1){
							_self.on = pageView.currentpage-1;
						}else{
							_self.on = '';
						}
						if(pageView.pageindex.endindex >0 && pageView.currentpage != pageView.totalpage && pageView.records.length > 0){
							
							_self.next = pageView.currentpage+1;
						}else{
							_self.next = '';
						}
						
					}
				}
			});
			
		},
		//跳转红包金额分配
		toRedEnvelopeAmountDistribution : function(giveRedEnvelopeId) {
			//我的
			this.$router.push({
				path : '/user/control/redEnvelopeAmountDistributionList', 
				query: {giveRedEnvelopeId: giveRedEnvelopeId}
			});
			
		},
		//初始化
		initialization : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询发红包列表
			this.queryGiveRedEnvelopeList();
		},
		
	},
});



//发红包金额分配
var redEnvelopeAmountDistributionList_component = Vue.extend({
	template : '#redEnvelopeAmountDistributionList-template',
	data : function data() {
		return {
			receiveRedEnvelopeList : [], //发红包金额分配集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			giveRedEnvelopeId: '',//发红包Id
			giveRedEnvelope: ''//发红包
		}
	},
	created : function created() {
		var giveRedEnvelopeId = getUrlParam("giveRedEnvelopeId");//当前标签
		if(giveRedEnvelopeId != null){
			this.giveRedEnvelopeId = giveRedEnvelopeId;//发红包Id
		}
		this.queryRedEnvelopeAmountDistributionList();
	},
	methods : {
		//查询发红包金额分配分页
		queryRedEnvelopeAmountDistributionList : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1)+"&giveRedEnvelopeId="+_self.giveRedEnvelopeId; //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/redEnvelopeAmountDistributionList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var returnValue = $.parseJSON(result);
							var giveRedEnvelope = null;
							var pageView = null;
							
							if(returnValue != null){
								for (var key in returnValue) {
									if(key == "giveRedEnvelope"){
										giveRedEnvelope = returnValue[key];
										
									}else if(key == "pageView"){
										pageView = returnValue[key];
										
									}
									
									
								}
							}
							if(giveRedEnvelope != null){
								_self.giveRedEnvelope = giveRedEnvelope;
							}
							if(pageView != null){
								
								var new_receiveRedEnvelopeList = pageView.records;
								if (new_receiveRedEnvelopeList != null && new_receiveRedEnvelopeList.length > 0) {
									_self.receiveRedEnvelopeList.push.apply(_self.receiveRedEnvelopeList, new_receiveRedEnvelopeList); //合并两个数组
									
									//生成首字符头像
									_self.$nextTick(function() {
										if (new_receiveRedEnvelopeList != null && new_receiveRedEnvelopeList.length > 0) {
											for(var i=0;i<new_receiveRedEnvelopeList.length; i++){
												var receiveRedEnvelope = new_receiveRedEnvelopeList[i];
												if(receiveRedEnvelope.receiveAvatarName == null || receiveRedEnvelope.receiveAvatarName == ''){
													var char = (receiveRedEnvelope.receiveNickname != null && receiveRedEnvelope.receiveNickname !="") ? receiveRedEnvelope.receiveNickname : receiveRedEnvelope.receiveAccount;
													//元素的实际宽度
													var width= _self.$refs['receiveRedEnvelopeAvatarData_'+receiveRedEnvelope.id][0].offsetWidth;
													_self.$refs['receiveRedEnvelopeAvatarData_'+receiveRedEnvelope.id][0].src = letterAvatar(char, width);	
												}
											}
										}
									});
									
								}
								_self.currentpage = pageView.currentpage;
								_self.totalpage = pageView.totalpage;
							}
							
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		
		
		
		
		
	}
});

//收红包
var receiveRedEnvelopeList_component = Vue.extend({
	template : '#receiveRedEnvelopeList-template',
	data : function data() {
		return {
			receiveRedEnvelopeList : [], //收红包集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
		}
	},
	created : function created() {
		
	},
	methods : {
		//查询收红包分页
		queryReceiveRedEnvelopeList : function() {
			var _self = this;
			
			if (_self.currentpage < _self.totalpage) {
				
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/receiveRedEnvelopeList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);
							
							var new_receiveRedEnvelopeList = pageView.records;
							if (new_receiveRedEnvelopeList != null && new_receiveRedEnvelopeList.length > 0) {
								_self.receiveRedEnvelopeList.push.apply(_self.receiveRedEnvelopeList, new_receiveRedEnvelopeList); //合并两个数组
								
								//生成首字符头像
								_self.$nextTick(function() {
									if (new_receiveRedEnvelopeList != null && new_receiveRedEnvelopeList.length > 0) {
										for(var i=0;i<new_receiveRedEnvelopeList.length; i++){
											var receiveRedEnvelope = new_receiveRedEnvelopeList[i];
											if(receiveRedEnvelope.giveAvatarName == null || receiveRedEnvelope.giveAvatarName == ''){
												var char = (receiveRedEnvelope.giveNickname != null && receiveRedEnvelope.giveNickname !="") ? receiveRedEnvelope.giveNickname : receiveRedEnvelope.giveAccount;
												
												//元素的实际宽度
												var width= _self.$refs['receiveRedEnvelopeAvatarData_'+receiveRedEnvelope.id][0].offsetWidth;
												_self.$refs['receiveRedEnvelopeAvatarData_'+receiveRedEnvelope.id][0].src = letterAvatar(char, width);	
											}
										}
									}
								});
								
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
							
							
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
		},
		
		
		
		
		
	}
});


//举报
var reportList_component = Vue.extend({
	template : '#reportList-template',
	data : function data() {
		return {
			reportList : [], //举报集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			
			pictureOptions: {//放大图片配置
		        url: 'data-src',//定义从何处获取原始图像URL进行查看
		        navbar:true, //导航栏
		        title:false, //标题
		        toolbar:false, //工具栏
		        loop:false, //是否启用循环查看
		        viewed : function(e) {
		        //	this.viewer.zoomTo(1);
		        }
		    },
		}
	},
	created : function created() {
		//this.queryUserLoginLog();
	},
	methods : {
		//查询举报页
		queryReport : function() {
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
				$.ajax({
					type : "GET",
					cache : false,
					async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
					url : "user/control/reportList",
					data : data,
					success : function success(result) {
						if (result != "") {
							var pageView = $.parseJSON(result);

							var new_reportList = pageView.records;
							if (new_reportList != null && new_reportList.length > 0) {
								_self.reportList.push.apply(_self.reportList, new_reportList); //合并两个数组
							}
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
						}
					},
					complete : function complete(XMLHttpRequest, textStatus) {
						_self.loading = false;
						//需手动调用设置的全局complete
						$.ajaxSettings.complete(XMLHttpRequest, textStatus);
					}
				});
			}
			
		}
	}
});



/**------------------------------------------- 公共组件 ------------------------------------------------**/

//底部选项卡
var bottomTab_component = Vue.extend({
	template : '#bottomTab-template',
	data : function data() {
		return {
			bottomTab : 'index',
			popup_find : false,//发现弹出层
		};
	},
	created : function created() {
		this.defaultSelected();
		
		
	},
	methods : {
		//提交数据
		selected : function selected(event) {
			if ('index' == this.bottomTab) {
				//首页
				// 跳转页面
				this.$router.push({
					path : '/index'
				});
			} else if ('askList' == this.bottomTab) {
				//问答
				this.$router.push({
					path : '/askList'
				});
				/**
				//全部标签
				this.popup_tag = true;
				this.queryTag();
				var tagId = getUrlParam("tagId");//当前标签
				if(tagId != null){
					this.selectedTagId = tagId;
				}**/
				
			} else if ('find' == this.bottomTab) {
				this.popup_find = true;

			} else if ('mine' == this.bottomTab) {
				//我的
				this.$router.push({
					path : '/user/control/home'
				});
			}
		},
		//默认选中
		defaultSelected : function defaultSelected() {
			//获取URL中的参数组
			var pathName = window.location.pathname;
			var last = pathName.substring(pathName.length - 1, pathName.length); //URL的最后一个字符
			if (last != "/") {
				pathName += "/";
			}
			//如果以/shop/index/开头
			if (pathName.indexOf(this.$store.state.contextPath + "/index/") >= 0) {
				this.bottomTab = "index";
			} else if (pathName.indexOf(this.$store.state.contextPath + "/askList/") >= 0) {
				this.bottomTab = "askList";
		//	} else if (pathName.indexOf(this.$store.state.contextPath + "/find/") >= 0) {
		//		this.bottomTab = "find";
			} else if (pathName.indexOf(this.$store.state.contextPath + "/user/control/") >= 0) {
				this.bottomTab = "mine";
			}
		},
		//关闭弹出层遮罩
		closePopup : function() {
			this.popup_find = false;
			
		}
	},
	
	
});
Vue.component('bottomtab-component', bottomTab_component); //组件名必须小写

/**--------------------------------------------路由-------------------------------------------------**/


//定义路由
var routes = [ 
    {path : '/',redirect : '/index'}, //router的重定向方法
	{path : '/index',component : index_component}, //首页
	{path : '/thread',component : thread_component}, //话题内容
	{path : '/askList',component : askList_component}, //问答列表
	{path : '/question',component : question_component}, //问题内容
	{path : '/user/addQuestion',component : addQuestion_component}, //提问题
	{path : '/user/appendQuestion',component : appendQuestion_component}, //追加问题
	{path : '/search',component : search_component}, //搜索列表
	{path : '/help',component : help_component}, //帮助中心
	{path : '/helpDetail',component : helpDetail_component}, //帮助内容
	{path : '/register',component : register_component}, //注册组件
	{path : '/agreement',component : agreement_component}, //服务协议
	{path : '/findPassWord/step1',component : findPassWord_step1_component}, //找回密码第一步组件
	{path : '/findPassWord/step2',component : findPassWord_step2_component}, //找回密码第二步组件
	{path : '/login',component : login_component}, //登录
	{path : '/user/control/home',component : home_component}, //用户中心
	{path : '/user/control/topicList',component : topicList_component}, //我的话题
	{path : '/user/control/commentList',component : commentList_component}, //我的评论
	{path : '/user/control/replyList',component : replyList_component}, //我的回复
	{path : '/user/control/questionList',component : questionList_component}, //我的问题
	{path : '/user/control/answerList',component : answerList_component}, //我的答案
	{path : '/user/control/answerReplyList',component : answerReplyList_component}, //我的答案回复
	{path : '/user/control/point',component : point_component}, //积分记录
	{path : '/user/control/editUser',component : editUser_component}, //修改个人信息
	{path : '/user/control/realNameAuthentication',component : realNameAuthentication_component}, //实名认证
	{path : '/user/control/phoneBinding',component : phoneBinding_component}, //绑定手机
	{path : '/user/control/updatePhoneBinding/step1',component : updatePhoneBinding_step1_component}, //更换手机绑定第一步
	{path : '/user/control/updatePhoneBinding/step2',component : updatePhoneBinding_step2_component}, //更换手机绑定第二步
	
	{path : '/user/control/userLoginLogList',component : userLoginLog_component}, //登录日志
	{path : '/user/control/privateMessageList',component : privateMessage_component}, //私信
	{path : '/user/control/privateMessageChatList',component : privateMessageChat_component}, //私信对话
	{path : '/user/control/systemNotifyList',component : systemNotify_component}, //系统通知
	{path : '/user/control/remindList',component : remind_component}, //提醒
	{path : '/user/control/favoriteList',component : favorite_component}, //收藏夹
	{path : '/user/control/topicFavoriteList',component : topicFavorite_component}, //话题收藏列表
	{path : '/user/control/likeList',component : like_component}, //点赞
	{path : '/user/control/topicLikeList',component : topicLike_component}, //话题点赞列表
	{path : '/user/control/followList',component : follow_component}, //关注
	{path : '/user/control/followerList',component : follower_component}, //粉丝
	{path : '/user/control/topicUnhideList',component : topicUnhide_component}, //话题取消隐藏用户列表
	{path : '/user/control/membershipCardOrderList',component : membershipCardOrderList_component}, //会员卡订单
	{path : '/user/control/balance',component : balance_component}, //余额
	{path : '/user/control/payment',component : payment_component}, //支付组件
	{path : '/paymentCompleted/:interfaceProduct/:paymentModule/:parameterId',component : paymentCompleted_component}, //支付完成通知组件
	{path : '/membershipCardList',component : membershipCardList_component}, //会员卡列表
	{path : '/membershipCard',component : membershipCard_component}, //会员卡
	{path : '/user/editTopic',component : editTopic_component}, //修改话题
	{path : '/user/control/giveRedEnvelopeList',component : giveRedEnvelopeList_component}, //发红包
	{path : '/user/control/redEnvelopeAmountDistributionList',component : redEnvelopeAmountDistributionList_component}, //发红包金额分配
	{path : '/user/control/receiveRedEnvelopeList',component : receiveRedEnvelopeList_component}, //收红包
	{path : '/user/control/reportList',component : reportList_component}, //举报列表
	
	
	{path : '*',redirect : '/index'} //其余路由重定向至首页
];

/**-------------------------------------------------------------------------------------------------**/

//创建全局状态存储对象
var store = new Vuex.Store({
	//存储状态值
	state : {
		baseURL : '', //系统路径
		commonPath : '', //资源路径
		contextPath : '', //系统虚拟目录
		token : '', //令牌标记
		weixin_oa_appid : '', //微信公众号应用唯一标识
		fileStorageSystem : 0, //文件存储系统 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS
		user : {
			userId : '',
			userName : ''
		},
		privateMessage_badge: false,
		systemNotify_badge: false,
		remind_badge: false,
		allMessage_badge: false,
		
		cacheComponents: [],//keepAlive缓存组件
	},
	
	
	// 状态值的改变方法,操作状态值,mutations方法必须是同步方法
	// 提交mutations是更改Vuex状态的唯一方法
	mutations : {
		//设置系统路径
		setBaseURL : function setBaseURL(state, baseURL) {
			state.baseURL = baseURL;
		},
		//设置资源路径
		setCommonPath : function setCommonPath(state, commonPath) {
			state.commonPath = commonPath;
		},
		//设置系统虚拟目录
		setContextPath : function setContextPath(state, contextPath) {
			state.contextPath = contextPath;
		},
		//设置令牌标记
		setToken : function setToken(state, token) {
			state.token = token;
		},
		//设置登录用户Id
		setSystemUserId : function setSystemUserId(state, userId) {
			state.user.userId = userId;
		},
		//设置登录用户名称
		setSystemUserName : function setSystemUserName(state, userName) {
			state.user.userName = userName;
		},
		//设置微信公众号应用唯一标识
		setWeixin_oa_appid : function setWeixin_oa_appid(state, appid) {
			state.weixin_oa_appid = appid;
		},
		//设置文件存储系统
		setFileStorageSystem : function setFileStorageSystem(state, fileStorageSystem) {
			state.fileStorageSystem = fileStorageSystem;
		},
		
		//设置私信未读状态
		setPrivateMessage_badge : function setPrivateMessage_badge(state, isShow) {
			state.privateMessage_badge = isShow;
		},
		//设置系统通知未读状态
		setSystemNotify_badge : function setSystemNotify_badge(state, isShow) {
			state.systemNotify_badge = isShow;
		},
		//设置提醒未读状态
		setRemind_badge : function setRemind_badge(state, isShow) {
			state.remind_badge = isShow;
		},
		//设置消息未读状态
		setAllMessage_badge : function setAllMessage_badge(state, isShow) {
			state.allMessage_badge = isShow;
		},
		
		//设置缓存组件
		setCacheComponents : function setCacheComponents(state, data) {
			state.cacheComponents = data;
		},
	},
	// 在store中定义getters（可以认为是store的计算属性）。Getters接收state作为其第一个函数
	getters : {
		cacheComponents: function (state) {
			return state.cacheComponents;
		}
	},
	//异步操作方法
	actions : {}
});

var scrollBehavior = function scrollBehavior(to, from, savedPosition) {
	if (savedPosition) {
		// savedPosition仅适用于popstate导航。
		return savedPosition;
	} else {
		// new navigation.
		// 滚动到锚点
		if (to.hash) {
			return {
	          // 通过 to.hash 的值來找到对应的元素
	          // 例如你按下 #3 的连接，就会变成 querySelector('#3')，自然会找到 id = 3 的元素
	          selector: to.hash
	        };
			
			
		//	return {
		//		anchor : true
		//	};
		}
		
		// 明确控制滚动位置
		// 检查匹配的路由配置是否具有需要滚动到顶部的元数据
		if (to.matched.some(function(m) {
				return m.meta.scrollToTop;
			})) {
			return {
				x : 0,
				y : 0
			};
		}
	}
};


//访问统计
function statistic(url,referrer){
	var data = ""; //提交参数
	data += "&url="+encodeURIComponent(getMetaTag().baseURL+url.substr(1,url.length)); //删除第一个左斜杆
	if(referrer != '' && referrer != '/'){
		data += "&referrer="+encodeURIComponent(getMetaTag().baseURL+referrer.substr(1,referrer.length)); //提交参数
	}
	$.ajax({
		type : "GET",
		cache : false,
		async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
		url : "statistic/add",
		data : data,
		success : function success(result) {
		},
		beforeSend : function beforeSend(XMLHttpRequest) {
			//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
		},
	});
}

//创建路由对象
var router = new VueRouter({
	mode : 'history', //html5模式 去掉锚点
	scrollBehavior : scrollBehavior, //记住页面的滚动位置 html5模式适用
	base : getMetaTag().contextPath, //虚拟目录
	routes : routes,
/**
 routes:[
 	{ path: '/', redirect: '/index' }, //router的重定向方法
 	{ path: '/index', component: { template: '#index-template', components: {'index-component' : index_component}}},
 	// { path: '/login', component: { template: '#login-template', components: {'login-component' : index_component}}},
 	// { path: '/bar', component: { template: '<div>barssss</div>' }}
 ]**/
});
router.beforeEach(function(to, from, next) {
    if (to.path) {
    	statistic(to.fullPath,from.fullPath);
    }
	next(true);
});

//router.beforeEach((to, from, next) => {
//	store.commit('setClickLink', true);
//	next(true);
//});

//	router.redirect({//定义全局的重定向规则。全局的重定向会在匹配当前路径之前执行。
//	    '*':"/index"//重定向任意未匹配路径到/index
//	});

//创建实例
var vue = new Vue({
	el : '#wap',
	store : store, //将store实例注入到根组件下的所有子组件中,子组件通过this.$store来访问store
	router : router, //通过vue配置中的router挂载router实例
	created : function created() {
		this.initialization();
	},
	
	computed: {
		cacheComponents : function () {
			return this.$store.getters.cacheComponents;
		}
	},
	methods : {
		//初始化数据
		initialization : function initialization(event) {
			var _self = this;
			_self.$store.commit('setBaseURL', getMetaTag().baseURL);
			_self.$store.commit('setCommonPath', getMetaTag().commonPath);
			_self.$store.commit('setContextPath', getMetaTag().contextPath);
			_self.$store.commit('setToken', getMetaTag().token);
			_self.$store.commit('setWeixin_oa_appid', getMetaTag().weixin_oa_appid);
			_self.$store.commit('setFileStorageSystem', getMetaTag().fileStorageSystem);
			_self.$store.commit('setSystemUserId', getMetaTag().userId);
			_self.$store.commit('setSystemUserName', getMetaTag().userName);
			
			_self.$store.commit('setPrivateMessage_badge', false);
			_self.$store.commit('setSystemNotify_badge', false);
			_self.$store.commit('setAllMessage_badge', false);
			
			
			
			//启动定时查询消息
			_self.timerUnreadMessage();
			
			//	alert("初始化数据"+getMetaTag().contextPath);
			//	alert("初始化数据"+this.$store.state.contextPath);

			//根据URL路由

			//路径部分
			//	var pathname = window.location.pathname; 

			//	var param = location.search; //获取url中"?"符后的字串

		//	alert(pathname+" -- "+param);
			
			
		},
		
		
		//定时查询消息
		timerUnreadMessage: function timerUnreadMessage() {
			var _self = this;
			_self.unreadMessageCount();
			
			setTimeout(function () {
				_self.timerUnreadMessage();
			}, 15000);//15秒
		}
	}
});

/****************************************** 公共方法 ************************************************/

//获取meta标签内容
function getMetaTag() {
	var baseURL = "";
	var commonPath = "";
	var contextPath = "";
	var token = "";
	var weixin_oa_appid = "";
	var fileStorageSystem = "";
	var userId = "";
	var userName = "";
	
	var meta = document.getElementsByTagName("meta");
	for (var i = 0; i < meta.length; i++) {
		if (meta[i].name == "_baseURL") {
			baseURL = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_commonPath") {
			commonPath = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_contextPath") {
			contextPath = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_token") {
			token = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_weixin_oa_appid") {
			weixin_oa_appid = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_fileStorageSystem") {
			fileStorageSystem = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_userId") {
			userId = meta[i].getAttribute("content");
		}
		if (meta[i].name == "_userName") {
			userName = meta[i].getAttribute("content");
		}
	}

	var global = {
		//系统路径
		baseURL : baseURL,
		//资源路径
		commonPath : commonPath,
		//系统虚拟目录
		contextPath : contextPath,
		//令牌
		token : token,
		//微信公众号应用唯一标识
		weixin_oa_appid : weixin_oa_appid,
		//文件存储系统
		fileStorageSystem : fileStorageSystem,
		//登录用户Id
		userId : userId,
		//登录用户名称
		userName : userName,
	};
	return global;
}

//返回上一页
function back() {
	//如果由登录页自动跳转，则返回多一步
	var pathname = window.location.pathname;
	var jumpUrl = getUrlParam("jumpUrl");
	if (jumpUrl != null && jumpUrl != "" && pathname == store.state.contextPath + "/login") {
		router.go(-2);
	} else {
		router.back();
	}
}
/**
 * 获取URL参数
 * @param name 参数名称
 * @returns
 */
function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg); //匹配目标参数
	if (r != null) return r[2];
	return null; //返回参数值
}

/**
 * 去除重复数组
 */
function repeat(array) {
	var r = [];
	for (var i = 0, l = array.length; i < l; i++) {
		for (var j = i + 1; j < l; j++)
			if (array[i] === array[j])
				j = ++i;
		r.push(array[i]);
	}
	return r;
}
/**
 * 数组是否含有指定元素
 */
Array.prototype.contains = function(item){
	  return RegExp("\\b"+item+"\\b").test(this);
};
/**
 * 删除数组内指定值元素
 * array 数组
 * val值
 */
function arrayRemove(array, val) {
	var index = -1;
	for (var i = 0; i < array.length; i++) {
		if (array[i] == val) {
			index = i;
		}
	}
	if (index > -1) {
		array.splice(index, 1);
	}
}
/**
 * 数组2是否包含数组1(不能含重复元素)
 * array1 数组
 * array2 数组
 */
function arrayContain(array1, array2) {
	var count = 0;
	for (var i = 0; i < array2.length; i++) {
		for (var j = 0; j < array1.length; j++) {
			if (array2[i] == array1[j]) {
				count++;
				break;
			}
		}
	}
	if (array1.length == count) {
		return true;
	} else {
		return false;
	}
}
/**
 * 比较两个数组是否相等
 */
function compare(a1, a2) {
	var exists = false;
	if (a1 instanceof Array && a2 instanceof Array) {
		for (var i = 0, iLen = a1.length; i < iLen; i++) {
			for (var j = 0, jLen = a2.length; j < jLen; j++) {
				if (a1[i] === a2[j]) {
					return true;
				}
			}
		}
	}
	return exists;
}
;

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
//判断字符串是数值
function isNumeric(n) {
	var s = String(n)
	if (!/^[0-9.eE+-]+$/.test(s)) return false;
	var v = Number(s)
	return Number.isFinite(v)
}
//建立一个可存取到该file的url  
function getObjectURL(file) {
	var url = null;
	// 下面函数执行的效果是一样的，只是需要针对不同的浏览器执行不同的 js 函数
	if (window.createObjectURL != undefined) { // basic  
		url = window.createObjectURL(file) ;
	} else if (window.URL != undefined) { // mozilla(firefox)  
		url = window.URL.createObjectURL(file) ;
	} else if (window.webkitURL != undefined) { // webkit or chrome  
		url = window.webkitURL.createObjectURL(file) ;
	}
	return url;
}
//生成字母头像
function letterAvatar (name, size, color) {
	name  = name || '';
	size  = size || 60;
	var colours = [
			"#eccc68", "#ff7f50", "#ff6b81", "#ffa502", "#747d8c", "#ff4757", "#7bed9f", "#70a1ff", "#8e44ad", "#2ed573", 
			"#1e90ff", "#a4b0be", "#ff6348", "#ff9ff3", "#f368e0", "#48dbfb", "#badc58", "#6ab04c", "#0fbcf9", "#f9ca24"
		],
		nameSplit = String(name).split(' '),
		initials, charIndex, colourIndex, canvas, context, dataURI;
	if (nameSplit.length == 1) {
		initials = nameSplit[0] ? nameSplit[0].charAt(0):'?';
	} else {
		initials = nameSplit[0].charAt(0) + nameSplit[1].charAt(0);
	}
	if (window.devicePixelRatio) {
		size = (size * window.devicePixelRatio);
	}
	
	
	charIndex     = (initials == '?' ? 72 : initials.charCodeAt(0)) - 64;
	colourIndex   = charIndex % 20;
	canvas        = document.createElement('canvas');
	canvas.width  = size;
	canvas.height = size;
	context       = canvas.getContext("2d");
	 
	//偏移
	var offset = 0;
	if(/^[\u4E00-\u9FA5]+$/.test(initials)){ //如果是中文
		offset = 1;
	}
	context.fillStyle = color ? color : colours[colourIndex - 1];
	context.fillRect (0, 0, canvas.width, canvas.height);
	context.font = Math.round(canvas.width/2)+"px Arial";
	context.textAlign = "center";
	context.fillStyle = "#FFF";
	context.fillText(initials, (size / 2)+offset, (size / 1.5)+offset);
	dataURI = canvas.toDataURL();
	canvas  = null;
	return dataURI;
}


//是否为微信浏览器
function isWeiXinBrowser() {
	var ua = window.navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) == 'micromessenger') {
		return true;
	} else {
		return false;
	}
}
//删除url参数
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
 
  if(url.indexOf("?") == 0){//删除第一个问号
	  url = url.substring(url.indexOf("?") + 1, url.length)
	  
  }
  return url;
}
//获取微信公众号code
function getWeiXinCode() {

	var appid = store.state.weixin_oa_appid;
	//获取URL中的微信code
	var code = getUrlParam("code");
			
	if((appid != null && appid != "") && (code == null || code == "")){
		var state = new Date().getTime();
		
		var url = deleteUrlParam(window.location.href,"code");//删除code参数
		url = deleteUrlParam(url,"state");//删除state参数
		var redirect_uri = encodeURIComponent(url);
		window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"&connect_redirect=1#wechat_redirect";
	
		//让后面的代码停止执行
		throw new Error();
		
	}
}

//获取微信openid
function getWeiXinOpenId() {
	if(isWeiXinBrowser()){//如果来自微信内置浏览器

		//获取微信公众号code
		getWeiXinCode();
		
		//获取微信openid
		var code = getUrlParam("code");
		if(code != null && code != ""){
			
			//删除浏览器地址的指定参数
			var url = deleteUrlParam(window.location.pathname + window.location.search,"code");//删除code参数
			url = deleteUrlParam(url,"state");//删除state参数
			history.replaceState({}, document.title, url);
		//	history.pushState({}, document.title, url);
			
			
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "thirdParty/queryWeiXinOpenId",
				data : "code="+code,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if(returnValue.errorCode == ""){
							//返回openid
							sessionStorage.setItem("weixin_openid",returnValue.openId); 
						}else{
							Vue.$messagebox('错误'+returnValue.errorCode, returnValue.errorMessage);
						}
						
			        	
					}
				},
				beforeSend : function beforeSend(XMLHttpRequest) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
					
				},
				complete : function complete(XMLHttpRequest, textStatus) {
					//本空方法不要删除，用来覆盖全局回调默认的旋转进度条
				}
			});
			
		}
		
		
	}
	
}
//获取代码语言
function getLanguageClassName(languageCode) {
	var language_arr = new Array();
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
		
	for(var k=0; k<language_arr.length; k++){
		var language = language_arr[k];
		var class_arr = new Array();
		class_arr = language.split('_');
		if(languageCode == class_arr[0]){
			
			return class_arr[1];
		}
	}
	return "language-markup";
}


//富文本自定义上传  uploadImgServer：editor.customConfig.uploadImgServer  uploadFileName：editor.customConfig.uploadFileName  dir：image、file、media
function editorCustomUpload(files, insert,uploadImgServer,uploadFileName,dir) {
	if(store.state.fileStorageSystem == 10){//10.SeaweedFS
		var fileServer = uploadImgServer;
		
		if (fileServer.indexOf('?') > 0) {
			fileServer += '&';
        } else {
        	fileServer += '?';
        }
		fileServer +=  'dir='+dir;
		fileServer +=  '&token='+store.state.token;
        
        
        Array.prototype.forEach.call(files, function(file) {
       // files.forEach(function(file) {
        	fileServer +=  '&fileName='+encodeURIComponent(file.name);
        	
        	$.ajax({
    			type : "POST",
    			cache : false,
    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			url : fileServer,
    			success : function success(result) {
    				if (result != "") {
    					var returnValue = $.parseJSON(result);
    					if(returnValue.error ==0){
    				        var signingUrl = returnValue.url;
    				        
    				        
    				        var beforeUrl = signingUrl.substring(0,signingUrl.indexOf("?"));
    						//URL参数部分
    						var urlParam = signingUrl.substring(signingUrl.indexOf("?")+1,signingUrl.length);

    						var newFileName = "";
    						//获取提交的参数
    					    var data = new FormData();
    						var urlParamArr = urlParam.split("&");
    					    for(var i=0;i<urlParamArr.length;i++){
    					        var paramArr = urlParamArr[i].split("=");
    					        data.append(paramArr[0], decodeURIComponent(paramArr[1]));
    					        if(paramArr[0] == "key"){
    					        	newFileName = decodeURIComponent(paramArr[1]);
    					        }
    					    }
    					    data.append("file", file);
    				        
    					    $.ajax({
    			    			type : "POST",
    			    			cache : false,
    			    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			    			url : beforeUrl,
    			    			data : data,
    			    			contentType : false, // 不设置内容类型
    			    			processData : false, // 不处理数据
    			    			success : function success(result) {
    			    				var url = beforeUrl+newFileName;
    			    				
    			    				var title = file.name;
    			    				
    							    // 上传代码返回结果之后，将文件插入到编辑器中
    							    insert(url,title);
    			    			}
    			    		});
    				    
    			    	}else{
    			    		//弹出提示内容
    						Vue.$messagebox('错误', returnValue.message);
    			    	} 
    					
    				}
    			}
    		});
    	});
	}else if(store.state.fileStorageSystem == 20){//20.MinIO
		var fileServer = uploadImgServer;
		
		if (fileServer.indexOf('?') > 0) {
			fileServer += '&';
        } else {
        	fileServer += '?';
        }
		fileServer +=  'dir='+dir;
		fileServer +=  '&token='+store.state.token;
        
        
        Array.prototype.forEach.call(files, function(file) {
       // files.forEach(function(file) {
        	fileServer +=  '&fileName='+encodeURIComponent(file.name);
        	
        	$.ajax({
    			type : "POST",
    			cache : false,
    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			url : fileServer,
    			success : function success(result) {
    				if (result != "") {
    					var returnValue = $.parseJSON(result);
    					if(returnValue.error ==0){
    				        var signingUrl = returnValue.url;
    				        
    				        
    				        var beforeUrl = signingUrl.substring(0,signingUrl.indexOf("?"));
    						//URL参数部分
    						var urlParam = signingUrl.substring(signingUrl.indexOf("?")+1,signingUrl.length);

    						var newFileName = "";
    						//获取提交的参数
    					    var data = new FormData();
    						var urlParamArr = urlParam.split("&");
    					    for(var i=0;i<urlParamArr.length;i++){
    					        var paramArr = urlParamArr[i].split("=");
    					        data.append(paramArr[0], decodeURIComponent(paramArr[1]));
    					        if(paramArr[0] == "key"){
    					        	newFileName = decodeURIComponent(paramArr[1]);
    					        }
    					    }
    					    data.append("file", file);
    				        
    					    $.ajax({
    			    			type : "POST",
    			    			cache : false,
    			    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			    			url : beforeUrl,
    			    			data : data,
    			    			contentType : false, // 不设置内容类型
    			    			processData : false, // 不处理数据
    			    			success : function success(result) {
    			    				var url = beforeUrl+newFileName;
    			    				
    			    				var title = file.name;
    			    				
    							    // 上传代码返回结果之后，将文件插入到编辑器中
    							    insert(url,title);
    			    			}
    			    		});
    				    
    			    	}else{
    			    		//弹出提示内容
    						Vue.$messagebox('错误', returnValue.message);
    			    	} 
    					
    				}
    			}
    		});
        	
        	
    	});
	}else if(store.state.fileStorageSystem == 30){//30.阿里云OSS
		var fileServer = uploadImgServer;
		
		if (fileServer.indexOf('?') > 0) {
			fileServer += '&';
        } else {
        	fileServer += '?';
        }
		fileServer +=  'dir='+dir;
		fileServer +=  '&token='+store.state.token;
        
        
        Array.prototype.forEach.call(files, function(file) {
       // files.forEach(function(file) {
        	fileServer +=  '&fileName='+encodeURIComponent(file.name);
        	
        	$.ajax({
    			type : "POST",
    			cache : false,
    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			url : fileServer,
    			success : function success(result) {
    				if (result != "") {
    					var returnValue = $.parseJSON(result);
    					if(returnValue.error ==0){
    				        var signingUrl = returnValue.url;
    				        
    				        
    				        var beforeUrl = signingUrl.substring(0,signingUrl.indexOf("?"));
    						//URL参数部分
    						var urlParam = signingUrl.substring(signingUrl.indexOf("?")+1,signingUrl.length);

    						var newFileName = "";
    						//获取提交的参数
    					    var data = new FormData();
    						var urlParamArr = urlParam.split("&");
    					    for(var i=0;i<urlParamArr.length;i++){
    					        var paramArr = urlParamArr[i].split("=");
    					        data.append(paramArr[0], decodeURIComponent(paramArr[1]));
    					        if(paramArr[0] == "key"){
    					        	newFileName = decodeURIComponent(paramArr[1]);
    					        }
    					    }
    					    data.append("file", file);
    				        
    					    $.ajax({
    			    			type : "POST",
    			    			cache : false,
    			    			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
    			    			url : beforeUrl,
    			    			data : data,
    			    			contentType : false, // 不设置内容类型
    			    			processData : false, // 不处理数据
    			    			success : function success(result) {
    			    				var url = beforeUrl+newFileName;
    			    				
    			    				var title = file.name;
    			    				
    							    // 上传代码返回结果之后，将文件插入到编辑器中
    							    insert(url,title);
    			    			}
    			    		});
    				    
    			    	}else{
    			    		//弹出提示内容
    						Vue.$messagebox('错误', returnValue.message);
    			    	} 
    					
    				}
    			}
    		});
        	
        	
    	});
	}else{//本地
		Array.prototype.forEach.call(files, function(file) {
    	//files.forEach(function(file) {
    		var formData = new FormData();
    		formData.append(uploadFileName, file);//editor.customConfig.uploadFileName
    		//令牌
    		//	formData.append("token", store.state.token);
    	    
    		var fileServer = uploadImgServer;
    		
	    	if (fileServer.indexOf('?') > 0) {
	    		fileServer += '&';
	        } else {
	        	fileServer += '?';
	        }
	    	fileServer +=  'dir='+dir;
	    	
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : fileServer,
				data : formData,
				contentType : false, // 不设置内容类型
				processData : false, // 不处理数据
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if(returnValue.error ==0){
				    		// 举例：假如上传图片成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
					        var url = returnValue.url;
					     // 上传代码返回结果之后，将图片插入到编辑器中
					        var title = file.name;
		    				
						    // 上传代码返回结果之后，将文件插入到编辑器中
						    insert(url,title);
				    		// result 必须是一个 JSON 格式字符串！！！否则报错
				    	}else{
				    		//弹出提示内容
							Vue.$messagebox('错误', returnValue.message);
				    	} 
						
					}
				}
			});
    		
    	});
    	
		
	}
	
}


//创建富文本编辑器
function createEditor(editorToolbar,editorText,commonPath,menus,userGradeList,imgPath,self,param) {
	var E = window.wangEditor;
	
    var editor = new E(editorToolbar,editorText);
    /**
    editor.customConfig.menus = [
							  //   'head',  // 标题
							     'bold',  // 粗体
							     'italic',  // 斜体
							     'underline',  // 下划线
							     'strikeThrough',  // 删除线
							 //    'foreColor',  // 文字颜色
							 //    'backColor',  // 背景颜色
							     'link',  // 插入链接
							//     'list',  // 列表
							//     'justify',  // 对齐方式
							//     'quote',  // 引用
							     'emoticon',  // 表情
							     'image',  // 插入图片
							 //    'table',  // 表格
							 //    'video',  // 插入视频
							 //    'code',  // 插入代码
							  //   'undo',  // 撤销
							 //   'redo'  // 重复
							     'hide',  // 插入隐藏栏
							     ];**/
    editor.customConfig.menus = menus;
    editor.customConfig.uploadImgServer = imgPath;
    editor.customConfig.onchange = function (html) {
		Vue.set(self, param, html);
    };
    //等级
    editor.customConfig.userGradeList=userGradeList;
    
    // 编辑区域的 z-index
    editor.customConfig.zIndex= 0;
//    editor.customConfig.uploadImgHeaders = {
 //   	'X-Requested-With': 'XMLHttpRequest'
 //   };
    
    //使用kindeditor表情目录下的文件
    var new_commonPath = commonPath.substring(0, commonPath.length - 4);
    
    
    var emoticonList_1 = new Array();
    for(var i=0; i<35; i++){
    	var o=new Object();
    	o.alt = '';
    	o.width = '32px';
    	o.height = '32px';
    	o.src = new_commonPath+'pc/js/kindeditor/plugins/emoticons/twemoji/'+i+'.svg';
    	emoticonList_1.push(o);
    }
    var emoticonList_2 = new Array();
    for(var i=35; i<70; i++){
    	var o=new Object();
    	o.alt = '';
    	o.width = '32px';
    	o.height = '32px';
    	o.src = new_commonPath+'pc/js/kindeditor/plugins/emoticons/twemoji/'+i+'.svg';
    	emoticonList_2.push(o);
    }
    var emoticonList_3 = new Array();
    for(var i=70; i<105; i++){
    	var o=new Object();
    	o.alt = '';
    	o.width = '32px';
    	o.height = '32px';
    	o.src = new_commonPath+'pc/js/kindeditor/plugins/emoticons/twemoji/'+i+'.svg';
    	emoticonList_3.push(o);
    }
    var emoticonList_4 = new Array();
    for(var i=105; i<140; i++){
    	var o=new Object();
    	o.alt = '';
    	o.width = '32px';
    	o.height = '32px';
    	o.src = new_commonPath+'pc/js/kindeditor/plugins/emoticons/twemoji/'+i+'.svg';
    	emoticonList_4.push(o);
    }
    var emoticonList_5 = new Array();
    for(var i=140; i<152; i++){
    	var o=new Object();
    	o.alt = '';
    	o.width = '32px';
    	o.height = '32px';
    	o.src = new_commonPath+'pc/js/kindeditor/plugins/emoticons/twemoji/'+i+'.svg';
    	emoticonList_5.push(o);
    }
    // 表情面板可以有多个 tab ，因此要配置成一个数组。数组每个元素代表一个 tab 的配置
    editor.customConfig.emotions = [
        {
            // tab 的标题
            title: '表情1',
            // type -> 'emoji' / 'image'
            type: 'image',
            // content -> 数组
            content: emoticonList_1
        },
        {
            // tab 的标题
            title: '表情2',
            // type -> 'emoji' / 'image'
            type: 'image',
            // content -> 数组
            content: emoticonList_2
        },
        {
            // tab 的标题
            title: '表情3',
            // type -> 'emoji' / 'image'
            type: 'image',
            // content -> 数组
            content: emoticonList_3
        },
        {
            // tab 的标题
            title: '表情4',
            // type -> 'emoji' / 'image'
            type: 'image',
            // content -> 数组
            content: emoticonList_4
        },
        {
            // tab 的标题
            title: '表情5',
            // type -> 'emoji' / 'image'
            type: 'image',
            // content -> 数组
            content: emoticonList_5
        }
    ]
    
    
    
    //后台代码接收文件的字段名称
    editor.customConfig.uploadFileName = "file";
    //上传图片大小,默认小于5M(5 * 1024 * 1024)
    editor.customConfig.uploadImgMaxSize = 200 * 1024 * 1024;
    
    //自定义 timeout 时间 默认的 timeout 时间是 10 秒钟
    editor.customConfig.uploadImgTimeout = 30000;
	// 隐藏“网络图片”tab
    editor.customConfig.showLinkImg = false;
    
    //图片上传
    editor.customConfig.customUploadImg = function (files, insert) {
        // files 是 input 中选中的文件列表
        // insert 是获取图片 url 后，插入到编辑器的方法
    	
    	editorCustomUpload(files, insert,editor.customConfig.uploadImgServer,editor.customConfig.uploadFileName,"image");
    	
    	
    }
    
    //文件上传
    editor.customConfig.customUploadFile = function (files, insert) {
    	// files 是 input 中选中的文件列表
        // insert 是获取图片 url 后，插入到编辑器的方法
    	
    	
    	editorCustomUpload(files, insert,editor.customConfig.uploadImgServer,editor.customConfig.uploadFileName,"file");
    	
    	/**
    	if(store.state.fileStorageSystem == 10){//10.SeaweedFS
    	
	    }else if(store.state.fileStorageSystem == 20){//20.MinIO
			
		}else if(store.state.fileStorageSystem == 30){//30.阿里云OSS
			
		}else{//本地
			var formData = new FormData();
	    	Array.prototype.forEach.call(files, function(file) {
	    		formData.append(editor.customConfig.uploadFileName, file);
	    		
	    	});
			//令牌
		//	formData.append("token", store.state.token);
	    	
	    	var uploadImgServer = editor.customConfig.uploadImgServer;
	    	if (uploadImgServer.indexOf('?') > 0) {
	            uploadImgServer += '&';
	        } else {
	            uploadImgServer += '?';
	        }
	        uploadImgServer +=  'dir=file';
	    	
	    	
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : uploadImgServer,
				data : formData,
				contentType : false, // 不设置内容类型
				processData : false, // 不处理数据
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if(returnValue.error ==0){
				    		// 举例：假如上传文件成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
					        var url = returnValue.url;
					        var title = returnValue.title;
					     // 上传代码返回结果之后，将文件插入到编辑器中
					        insert(url,title);
				    		// result 必须是一个 JSON 格式字符串！！！否则报错
				    	}else{
				    		//弹出提示内容
							Vue.$messagebox('错误', returnValue.message);
				    	} 
						
					}
				}
			});
		}
    	**/
    	
    	
    	
    	
    },
    //视频上传
    editor.customConfig.customUploadVideo = function (files, insert) {
    	// files 是 input 中选中的文件列表
        // insert 是获取图片 url 后，插入到编辑器的方法
    	
    	editorCustomUpload(files, insert,editor.customConfig.uploadImgServer,editor.customConfig.uploadFileName,"media");
    	
    	/**
    	if(store.state.fileStorageSystem == 10){//10.SeaweedFS
        	
	    }else if(store.state.fileStorageSystem == 20){//20.MinIO
			
		}else if(store.state.fileStorageSystem == 30){//30.阿里云OSS
			
		}else{//本地
			var formData = new FormData();
	    	Array.prototype.forEach.call(files, function(file) {
	    		formData.append(editor.customConfig.uploadFileName, file);
	    		
	    	});
			//令牌
		//	formData.append("token", store.state.token);
	    	
	    	var uploadImgServer = editor.customConfig.uploadImgServer;
	    	if (uploadImgServer.indexOf('?') > 0) {
	            uploadImgServer += '&';
	        } else {
	            uploadImgServer += '?';
	        }
	        uploadImgServer +=  'dir=media';
	    	
	    	
			$.ajax({
				type : "POST",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : uploadImgServer,
				data : formData,
				contentType : false, // 不设置内容类型
				processData : false, // 不处理数据
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);
						if(returnValue.error ==0){
				    		// 举例：假如上传文件成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
					        var url = returnValue.url;
					        var title = returnValue.title;
					     // 上传代码返回结果之后，将文件插入到编辑器中
					        insert(url,title);
				    		// result 必须是一个 JSON 格式字符串！！！否则报错
				    	}else{
				    		//弹出提示内容
							Vue.$messagebox('错误', returnValue.message);
				    	} 
						
					}
				}
			});
		}
    	
    	**/
    	
    	
    	
    	
    }
    /**
    editor.customConfig.uploadImgHooks = {
	    // 如果服务器端返回的不是 {errno:0, data: [...]} 这种格式，可使用该配置
	    // （但是，服务器端返回的必须是一个 JSON 格式字符串！！！否则会报错）
	    customInsert: function (insertImg, result, editor) {
	        // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
	        // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果
	    	if(result.error ==0){
	    		// 举例：假如上传图片成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
		        var url = result.url;
		        insertImg(url)
	    		// result 必须是一个 JSON 格式字符串！！！否则报错
	    	}else{
	    		//弹出提示内容
				Vue.$messagebox('错误', result.message);
	    	}  
	    },
	    
	    error: function (xhr, editor) {
	        // 图片上传出错时触发
	        // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象
	    	console.log(xhr.status+"  错误  "+xhr.responseText);
	    	
	    	if(xhr.status == 508){
				//设置登录用户
				store.commit('setSystemUserId', '');
				store.commit('setSystemUserName', '');
				
			}
	    	
			//关闭网站提示参数
			if(xhr.status == 503){
				//弹出提示内容
				Vue.$messagebox('系统维护', xhr.responseText);
				
			}
			
			//请求完成后回调函数 (请求成功或失败时均调用)
			if (xhr.getResponseHeader("jumpPath") != null && xhr.getResponseHeader("jumpPath") != "") {
				//session登陆超时登陆页面响应http头
				//收到未登录标记，执行登录页面跳转
				router.push({
					path : "/" + xhr.getResponseHeader("jumpPath")
				});
			}
	    	
	    },
    };**/
    editor.create();
	return editor;
}

/**
 * 转义html
 */
function escapeHtml(html) {
	return _.escape(html);//引入lodash.js
	
}
/**
 * vue文本转义
 */
function escapeVueHtml(html) {
	return html.replace(/{{/g, "<span v-html='escapeChar'></span>");//{{大括号转义
}