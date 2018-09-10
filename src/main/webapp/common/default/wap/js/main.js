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
		
		//请求完成后回调函数 (请求成功或失败时均调用)
		if (XMLHttpRequest.getResponseHeader("jumpPath") != null && XMLHttpRequest.getResponseHeader("jumpPath") != "") {
			//session登陆超时登陆页面响应http头
			//收到未登录标记，执行登录页面跳转
			router.push({
				path : "/" + XMLHttpRequest.getResponseHeader("jumpPath")
			});
		}
		
		Vue.$indicator.close(); //关闭旋转进度条
	}
});
Vue.component('v-select', VueSelect.VueSelect);

//Vue.component('vue-cropper', window['vue-cropper'].default);


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
		        	if((privateMessageCount + systemNotifyCount)>0){
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
		});
	}
	
	
}




/**------------------------------------------- 页面组件 ------------------------------------------------**/

//首页组件
var index_component = Vue.extend({
	template : '#index-template',
	data : function data() {
		return {
			topicList : [], //话题列表
			currentpage : 1, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
			tagId :'',//标签Id
			tagName :'',//标签名称
			popup_topic :false,//发表话题弹出层
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
		};
	},
	
	created : function created() {
		//初始化
		this.init();

	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
	},
	methods : {

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
							if(_self.tagSlots[0].values == null || _self.tagSlots[0].values.length == 0){//第一次打开时设置值
								_self.tagSlots[0].values = tagList;
							}
							
						}
					}
				}
			});
		},
		//发表话题界面
		addTopicUI : function() {
			this.popup_topic = true;

			//查询添加话题页
			this.queryAddTopic();
			
			//创建编辑器
			this.topicEditor = createEditor(this.$refs.topicContentEditorToolbar,this.$refs.topicContentEditorText,'user/control/topic/upload',this,"topicContent");
			
	
		},
		//查询添加话题页
		queryAddTopic : function() {
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

			//清空标签数据
			_self.tagSlots[0].values = [];
			_self.tagSlots[0].defaultIndex = 0;
			
			
			//清空所有错误
			_self.error.topicTitle = "";
			_self.error.topicTagId = "";
			_self.error.topicContent = "";
			_self.error.captchaValue = "";
			_self.error.topic = "";
			
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
										_self.popup_topic = false;
										_self.$toast({
											message : "发表话题功能未开放",
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
							_self.initScroll(_self.$refs.addTopicFormScroll);
						});
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
			
		//	_self.topicContent = "<div style='text-align: center; height: expression(alert('test xss'));'>fdfd</div>";//
		//	_self.topicContent = "<div style='text-align: center; height: expre\ssion(alert('test xss'));'>fdfd</div>";//
			
			
			var parameter = "&tagId=" + _self.topicTagId; //提交参数
			if (_self.topicTitle != null && _self.topicTitle != "") {
				parameter += "&title=" + encodeURIComponent(_self.topicTitle);
			}
			if (_self.topicContent != null && _self.topicContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.topicContent);
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
							_self.popup_topic = false;

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
		init : function() {
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
		//初始化BScroll滚动插件//this.$refs.addTopicFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
				HWCompositing : true, //是否启用硬件加速
			});
		},
	}
});

//话题内容组件
var thread_component = Vue.extend({
	template : '#thread-template',
	data : function data() {
		return {
			topicId :'',//话题Id
			commentId:'',//评论Id
			topic : '', //话题
			commentList : '', //评论列表
			currentpage : 1, //当前评论页码
			totalpage : 1, //评论总页数
			on: '',//上一页
			next: '',//下一页
			quoteData : [],//引用数据 map格式 key:评论Id value:引用html数据
			replyExpandOrShrink : [], //回复展开/收缩 map格式 key:评论Id value:是否展开
			
			popup_comment :false,//发表评论弹出层
			popup_quote :false,//引用评论弹出层
			popup_reply : false, //发表回复弹出层
			commentContent:'',//发表评论内容
			quoteCommentId:'',//引用评论Id
			quoteCommentContent : '',//引用评论内容
			quoteContent:'',//发表引用评论内容
			replyCommentId :'',//回复评论Id
			replyContent:'',//发表回复内容
			
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
			},
			commentEditor : '',//评论富文本编辑器
			quoteEditor: '',//引用评论富文本编辑器

		};
	},
	created : function created() {
		//初始化
		this.init();
	},
	mounted: function() {
		
	
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
	},

	computed: {
		//动态解析评论引用模板数据
		quoteDataComponent: function quoteDataComponent() {
			return function (quoteContentData) {
				return {
					template: quoteContentData, // use content as template for this component
					props: this.$options.props, // re-use current props definitions
					
				};
			};
		
			
		},
	},
	
	methods : {
		//查询话题
		queryTopic : function() {
			var _self = this;
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
							_self.topic = topic;
						}
					}
				}
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
							_self.commentList = new_commentList;
							
							for (var i = 0; i <new_commentList.length; i++) {
								var comment = new_commentList[i];
								_self.$set(_self.replyExpandOrShrink, comment.id, false); //是否展开
								
								//组装引用数据
								if(comment.quoteList != null && comment.quoteList.length >0){
									var quoteContent = "";
									for (var j = 0; j <comment.quoteList.length; j++) {
										var quote = comment.quoteList[j];
										quoteContent = "<div>"+quoteContent+"<span><router-link tag=\"span\" :to=\"{path: '/user/control/home', query: {userName: '"+quote.userName+"'}}\">"+quote.userName+"</router-link>&nbsp;的评论：</span><br/>"+quote.content+"</div>";
									}
									
									
									
									_self.$set(_self.quoteData, comment.id, quoteContent);
								}
								
							}
							
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
							if(_self.commentId != null && _self.commentId != ""){
								var anchor = _self.$el.querySelector("#anchor_"+_self.commentId);
								if(anchor != null){
									document.body.scrollTop = anchor.offsetTop; // chrome
							        document.documentElement.scrollTop = anchor.offsetTop; // firefox
								}
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
		
		//发表评论界面
		addCommentUI : function() {
			var _self = this;
			this.popup_comment = true;

			//查询添加评论页
			this.queryAddComment();
			
			
			//创建编辑器
			this.commentEditor = createEditor(this.$refs.commentContentEditorToolbar,this.$refs.commentContentEditorText,'user/control/comment/uploadImage?topicId='+this.topicId,_self,"commentContent");
			
		},
		
		//查询添加评论页
		queryAddComment : function() {
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
										_self.popup_comment = false;
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
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addCommentFormScroll);

						});
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
			if (_self.commentContent != null && _self.commentContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.commentContent);
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
							_self.popup_comment = false;

							
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
			this.popup_quote = true;

			//查询引用评论页
			this.queryAddQuote(commentId);
			//创建编辑器
			this.quoteEditor = createEditor(this.$refs.quoteContentEditorToolbar,this.$refs.quoteContentEditorText,'user/control/comment/uploadImage?topicId='+this.topicId,this,"quoteContent");
			
		},
		//查询引用评论页
		queryAddQuote : function(commentId) {
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
										_self.popup_quote = false;
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
						}
						
						//滚动
						_self.$nextTick(function() {
							_self.initScroll(_self.$refs.addQuoteFormScroll);

						});
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
			if (_self.quoteContent != null && _self.quoteContent != "") {
				parameter += "&content=" + encodeURIComponent(_self.quoteContent);
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
							_self.popup_quote = false;

							
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
											_self.error.commentContent = value_error[error];
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
											_self.error.commentContent = value_error[error];
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

		//更换验证码
		replaceCaptcha : function replaceCaptcha(event) {
			var _self = this;
			_self.imgUrl = "captcha/" + _self.captchaKey + ".jpg?" + Math.random();
		},

		//验证验证码
		validation_captchaValue : function validation_captchaValue(event) {
			var _self = this;
			var cv = this.captchaValue.trim();
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
		init : function() {
			var topicId = getUrlParam("topicId");//话题Id
			if(topicId != null){
				this.topicId = topicId;
			}
			var commentId = getUrlParam("commentId");//评论Id
			if(commentId != null){
				this.commentId = commentId;
			}
			
			
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			//查询话题
			this.queryTopic();
			//查询评论列表
			this.queryCommentList();
		},
		//初始化BScroll滚动插件//this.$refs.addCommentFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
			
				HWCompositing : true, //是否启用硬件加速
			});
		},
	}
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
				this.$router.push({
					path : '/search',
					query : {
						keyword : encodeURIComponent(this.keyword)
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
			topicList : [], //话题列表
			currentpage : 1, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		};
	},
	created : function created() {
		//初始化
		this.init();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
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
							}else if (key == "topicPage") {
								pageView = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							}
						}
						
						if (value_success == "true") {
							_self.topicList = pageView.records;
							_self.currentpage = pageView.currentpage;
							_self.totalpage = pageView.totalpage;
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
		init : function() {
			var keyword = getUrlParam("keyword");
			if (keyword != null && keyword != "") {
				this.keyword = decodeURIComponent(keyword);
			}
			var page = getUrlParam("page");
			if (page != null && page != "") {
				this.currentpage = decodeURIComponent(page);
			}
			//搜索话题
			this.searchTopic();
			
		},

	}
});



//注册组件
var register_component = Vue.extend({
	template : '#register-template',


	data : function() {
		return {
			userCustomList : [], //用户自定义注册功能项
			captchaKey : '', //验证码编号
			captchaValue : '',
			showCaptcha : false,
			imgUrl : '',
			userName : '', //用户名
			password : '', //密码
			confirmPassword : '', //确认密码
			issue : '', //密码提示问题
			answer : '', //密码提示答案
			email : '', //邮箱

			userBoundField : [], //用户自定义注册功能项绑定
			error : {
				userName : '', //用户名
				password : '', //密码
				confirmPassword : '', //确认密码
				issue : '', //密码提示问题
				answer : '', //密码提示答案
				email : '', //邮箱
				captchaValue : '', //验证码值
				register: '', //注册错误
			},
			customError : [], //用户自定义注册功能项错误提示	

			agreement : true, //是否同意服务协议
		};
	},
	created : function() {
		this.init();
	},
	methods : {
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
						} else if (key == "captchaKey") {
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
						for (var value in fieldValue) {
							parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value].value;
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
								if (error == "userName") { //用户名
									_self.error.userName = errorValue;
								} else if (error == "password") { //密码
									_self.error.password = errorValue;
								} else if (error == "issue") { //密码提示问题
									_self.error.issue = errorValue;
								} else if (error == "answer") { //密码提示答案
									_self.error.answer = errorValue;
								} else if (error == "email") { //邮箱
									_self.error.email = errorValue;
								}  else if (error == "register") { //注册错误
									_self.error.register = errorValue;
								} else {
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
		//验证用户名
		verificationUserName : function() {
			var _self = this;
			var parameter = "&userName=" + _self.userName;
			_self.error.userName = "";

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "userVerification",
				data : parameter,
				success : function success(result) {
					if (result != "") {
						if (result == "true") {
							_self.error.userName = "用户名已存在";
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
		init : function() {
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
			userName : '',
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				userName : '',
				captchaValue : ''
			}
		};
	},
	created : function created() {
		this.queryFindPassWord_step1();
	},
	methods : {
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
			//清除所有错误
			_self.clearError();

			var parameter = "";

			var userName = _self.userName;
			if (userName != null && userName != '') {
				parameter += "&userName=" + encodeURIComponent(userName);
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
										if (error == "userName") {
											_self.error.userName = value_error[error];
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
			userName : '',
			issue : '',
			answer : '',
			password : '', //新密码
			confirmPassword : '', //确认密码
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				userName : '',
				password : '',
				confirmPassword : '',
				answer : '',
				captchaValue : ''
			}
		};
	},
	created : function created() {
		//获取用户名
		var userName = getUrlParam("userName");
		if (userName != null && userName != "") {
			this.userName = userName;
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
			//清除所有错误
			_self.clearError();
			//验证密码
			if (_self.verificationPassword() == false) {
				_self.$messagebox('提示', '请填好资料再提交');
				return;
			}
			var parameter = "";
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

//登录页组件
var login_component = Vue.extend({
	template : '#login-template',
	data : function data() {
		return {
			userName : '',
			password : '',
			rememberMe:false,
			showCaptcha : false,
			imgUrl : '',
			captchaKey : '',
			captchaValue : '',
			error : {
				userName : '',
				password : '',
				captchaValue : ''
			}
		};
	},
	created : function created() {
		this.loadLogin();
	},
	methods : {

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
		//提交数据
		submitData : function submitData(event) {
			var _self = this;

			var parameter = "";

			//用户名
			var userName = _self.userName;

			parameter += "&userName=" + encodeURIComponent(userName);
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
										if (error == "userName") {
											_self.error.userName = key_error[error];
										} else if (error == "password") {
											_self.error.password = key_error[error];
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
	template : '#home-template',
	data : function data() {
		return {
			user : '', //用户
			avatarUrl: '',//头像URL
			
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
			}
		}
	},
	created : function created() {
		this.loadHome();
		
		//查询消息
		this.unreadMessageCount();
		
	}, 
	components: {
	    'vue-cropper': window['vue-cropper'].default
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
								
								_self.loadHome();
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
		//加载用户中心页
		loadHome : function() {
			var _self = this;
			var data = "";
			var userName = getUrlParam("userName");//用户名称
			if(userName != null){
				data = "&userName=" + userName;
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
								
							}
						}

					}
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
	template : '#topicList-template',
	data : function data() {
		return {
			topicList : [], //话题集合
			loading : false, //加载中
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			on: '',//上一页
			next: '',//下一页
		}
	},
	created : function created() {
		//初始化
		this.init();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
	},
	methods : {
		
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
		init : function() {
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
		this.init();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
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
		init : function() {
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
		this.init();
	},
	//在当前路由改变，但是该组件被复用时调用
	beforeRouteUpdate : function beforeRouteUpdate(to, from, next) {
		next(true);
		//重置data
		Object.assign(this.$data, this.$options.data());
		//初始化
		this.init();
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
		init : function() {
			var page = getUrlParam("page");//当前标签
			if(page != null){
				this.currentpage = parseInt(page);//当前页码
			}
			
			//查询回复列表
			this.queryReplyList();
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
			oldPassword : '',
			password : '',
			confirmPassword : '',
			user : null,
			userCustomList : [], //用户自定义注册功能项
			userBoundField : [], //用户自定义注册功能项绑定
			error : {
				oldPassword : '',
				password : '',
				confirmPassword : '',
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
							_self.userCustomList = returnValue['userCustomList'];
							if (_self.userCustomList != null && _self.userCustomList.length > 0) {
								for (var i = 0; i < _self.userCustomList.length; i++) {
									var userCustom = _self.userCustomList[i];
									_self.boundField(userCustom);
									_self.customError.push('');
								}

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
						if(userCustom.multiple){//如果可选择多个选项
							for (var value in fieldValue) {
								
								parameter += "&userCustom_" + userCustom.id + "=" + fieldValue[value].value;
							}
						}else{
							parameter += "&userCustom_" + userCustom.id + "=" + fieldValue.value;
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
					//	alert(result);
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
		this.queryUserLoginLog();
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
			currentpage : 0, //当前页码
			totalpage : 1, //总页数
			
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
		this.init();
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
		queryPrivateMessageChat : function() {
			var _self = this;
			if (_self.currentpage < _self.totalpage) {
				//先改总页数为0，避免请求为空时死循环
				_self.totalpage = 0;
				_self.loading = true;
				var data = "page=" + (_self.currentpage + 1); //提交参数
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
							var new_privateMessageChatList = pageView.records;
							if (new_privateMessageChatList != null && new_privateMessageChatList.length > 0) {
								_self.privateMessageChatList.push.apply(_self.privateMessageChatList, new_privateMessageChatList); //合并两个数组
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
							_self.currentpage = 0; //当前页码
							_self.totalpage = 1; //总页数
							_self.messageContent = '';//发私信内容
							//查询私信对话列表
							_self.queryPrivateMessageChat();
							
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
		init : function() {
				
			var friendUserName = getUrlParam("friendUserName");
			if(friendUserName != null){
				this.friendUserName = friendUserName;
				this.friendUserNameTitle = "与 "+friendUserName+" 的对话";
			}
			this.queryPrivateMessageChat();
			
			
		},
		//初始化BScroll滚动插件//this.$refs.addPrivateMessageFormScroll
		initScroll : function initScroll(ref) {
			this.scroll = new BScroll(ref, {
				scrollY : true, //滚动方向为 Y 轴
				click : true, //是否派发click事件
				autoBlur:false,//默认值：true 在滚动之前会让当前激活的元素（input、textarea）自动失去焦点
				preventDefault : true, //是否阻止默认事件
				preventDefaultException:{ tagName: /^(INPUT|TEXTAREA|BUTTON|SELECT)$/ ,className:/(^|\s)(editor-toolbar|w-e-menu|editor-text|w-e-text)(\s|$)/},//列出哪些元素不屏蔽默认事件 className必须是最里层的元素
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



/**------------------------------------------- 公共组件 ------------------------------------------------**/

//底部选项卡
var bottomTab_component = Vue.extend({
	template : '#bottomTab-template',
	data : function data() {
		return {
			bottomTab : 'index',
			popup_tag :false,
			tagList: '',//全部标签
			selectedTagId :'',//已选中标签Id
		};
	},
	created : function created() {
		this.defaultSelected();
		
		
		
	},
	beforeDestroy : function() {
		//销毁滚动条
		if (this.scroll != null) {
			this.scroll.destroy();
			this.scroll = null;
		}
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
			} else if ('tag' == this.bottomTab) {
				//全部标签
				this.popup_tag = true;
				this.queryTag();
				var tagId = getUrlParam("tagId");//当前标签
				if(tagId != null){
					this.selectedTagId = tagId;
				}
				
			} else if ('search' == this.bottomTab) {
				//搜索
				this.$router.push({
					path : '/search'
				});

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
			//} else if (pathName.indexOf(this.$store.state.contextPath + "/allType/") >= 0) {
			//	this.bottomTab = "allTag";
			} else if (pathName.indexOf(this.$store.state.contextPath + "/search/") >= 0) {
				this.bottomTab = "search";
			} else if (pathName.indexOf(this.$store.state.contextPath + "/user/control/") >= 0) {
				this.bottomTab = "mine";
			}
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
						var dataView = $.parseJSON(result);
						if (dataView != null && dataView.length > 0) {
							_self.tagList = dataView;
							
						}
					}
					//滚动
					_self.$nextTick(function() {
						_self.initScroll();
					});
				}
			});
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
		},
	},
	
	
});
Vue.component('bottomtab-component', bottomTab_component); //组件名必须小写

/**--------------------------------------------路由-------------------------------------------------**/


//定义路由
var routes = [ 
    {path : '/',redirect : '/index'}, //router的重定向方法
	{path : '/index',component : index_component,meta:{keepAlive: true}}, //首页  keepAlive true:缓存数据 false:不缓存数据
	{path : '/thread',component : thread_component}, //话题内容
	{path : '/search',component : search_component}, //搜索列表
	{path : '/register',component : register_component}, //注册组件
	{path : '/agreement',component : agreement_component}, //服务协议
	{path : '/findPassWord/step1',component : findPassWord_step1_component}, //找回密码第一步组件
	{path : '/findPassWord/step2',component : findPassWord_step2_component}, //找回密码第二步组件
	{path : '/login',component : login_component}, //登录
	{path : '/user/control/home',component : home_component}, //用户中心
	{path : '/user/control/topicList',component : topicList_component}, //我的话题
	{path : '/user/control/commentList',component : commentList_component}, //我的评论
	{path : '/user/control/replyList',component : replyList_component}, //我的回复
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
		token : '', //令牌标记,
		user : {
			userId : '',
			userName : ''
		},
		privateMessage_badge: false,
		systemNotify_badge: false,
		allMessage_badge: false,
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
		
		
		//设置私信未读状态
		setPrivateMessage_badge : function setPrivateMessage_badge(state, isShow) {
			state.privateMessage_badge = isShow;
		},
		//设置系统通知未读状态
		setSystemNotify_badge : function setSystemNotify_badge(state, isShow) {
			state.systemNotify_badge = isShow;
		},
		//设置消息未读状态
		setAllMessage_badge : function setAllMessage_badge(state, isShow) {
			state.allMessage_badge = isShow;
		},
	},
	// 在store中定义getters（可以认为是store的计算属性）。Getters接收state作为其第一个函数
	getters : {},
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
		this.init();
	},
	methods : {
		//初始化数据
		init : function init(event) {
			var _self = this;

			_self.$store.commit('setBaseURL', getMetaTag().baseURL);
			_self.$store.commit('setCommonPath', getMetaTag().commonPath);
			_self.$store.commit('setContextPath', getMetaTag().contextPath);
			_self.$store.commit('setToken', getMetaTag().token);
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
	if (r != null) return unescape(r[2]);
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


//创建富文本编辑器
function createEditor(editorToolbar,editorText,imgPath,self,param) {
	var E = window.wangEditor;
    var editor = new E(editorToolbar,editorText);
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
							     ];
    editor.customConfig.uploadImgServer = imgPath;
    editor.customConfig.onchange = function (html) {
		Vue.set(self, param, html);

		
    };
//    editor.customConfig.uploadImgHeaders = {
 //   	'X-Requested-With': 'XMLHttpRequest'
 //   };
    //后台代码接收文件的字段名称
    editor.customConfig.uploadFileName = "imgFile";
    
    //自定义 timeout 时间 默认的 timeout 时间是 10 秒钟
    editor.customConfig.uploadImgTimeout = 30000;
	// 隐藏“网络图片”tab
    editor.customConfig.showLinkImg = false;
    
    //图片上传
    editor.customConfig.customUploadImg = function (files, insert) {
        // files 是 input 中选中的文件列表
        // insert 是获取图片 url 后，插入到编辑器的方法

    	var formData = new FormData();
    	files.forEach(function(file) {
    		formData.append(editor.customConfig.uploadFileName, file);
    	});
    	
		//令牌
	//	formData.append("token", store.state.token);
		$.ajax({
			type : "POST",
			cache : false,
			async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
			url : editor.customConfig.uploadImgServer,
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
				        insert(url);
			    		// result 必须是一个 JSON 格式字符串！！！否则报错
			    	}else{
			    		//弹出提示内容
						Vue.$messagebox('错误', returnValue.message);
			    	} 
					
				}
			}
		});
    	
    	
    	
        
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


