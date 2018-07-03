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



//拦截所有的ajax请求
hookAjax({
	onreadystatechange:function(XMLHttpRequest){
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
	},
	onload:function(XMLHttpRequest){
		
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
	},
	//拦截函数
	open:function(arg,xhr){

	},
	send:function(arg,xhr){
		xhr.setRequestHeader("X-Requested-With","XMLHttpRequest");//标记报头为AJAX方式
  }
});







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
			if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
				return;
			}
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
							_self.currentpage = 1; //当前页码
							_self.totalpage = 1; //总页数
							_self.on = '';//上一页
							_self.next = '';//下一页
							
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
										quoteContent = "<div>"+quoteContent+"<span>"+quote.userName+"&nbsp;的评论：</span><br/>"+quote.content+"</div>";
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
			if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
				return;
			}
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
							_self.currentpage = 1; //当前页码
							_self.totalpage = 1; //总页数
							_self.on = '';//上一页
							_self.next = '';//下一页
						
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
			if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
				return;
			}
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
							_self.currentpage = 1; //当前页码
							_self.totalpage = 1; //总页数
							_self.on = '';//上一页
							_self.next = '';//下一页
						
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
			if (!event._constructed) { //如果不存在这个属性,则不执行下面的函数
				return;
			}
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
							_self.currentpage = 1; //当前页码
							_self.totalpage = 1; //总页数
							_self.on = '';//上一页
							_self.next = '';//下一页
						
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
						for (var key in returnValue) {
							if (key == "success") {
								value_success = returnValue[key];
							} else if (key == "error") {
								value_error = returnValue[key];
							} else if (key == "jumpUrl") {
								value_jumpUrl = returnValue[key];
							} else if (key == "captchaKey") {
								value_captchaKey = returnValue[key];
							}
						}
						if (value_success == "true") { //成功
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
							}
						}

						//登录成功
						if (key_success == "true") {
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

//用户中心页
var home_component = Vue.extend({
	template : '#home-template',
	data : function data() {
		return {
			user : '', //用户
			waitingPaymentOrderQuantity : '', //待付款数量
			shippedOrderQuantity : '', //已发货数量
			popup_userSetting : false, //'用户设置'弹出层
		}
	},
	created : function created() {
		this.loadHome();
	},
	methods : {
		//加载用户中心页
		loadHome : function() {
			var _self = this;

			$.ajax({
				type : "GET",
				cache : false,
				async : true, //默认值: true。默认设置下，所有请求均为异步请求。如果需要发送同步请求，请将此选项设置为 false。
				url : "user/control/home",
				//	data: data,
				success : function success(result) {
					if (result != "") {
						var returnValue = $.parseJSON(result);

						for (var key in returnValue) {
							if (key == "user") {
								_self.user = returnValue[key];
							} else if (key == "waitingPaymentOrderQuantity") {
								_self.waitingPaymentOrderQuantity = returnValue[key];
							} else if (key == "shippedOrderQuantity") {
								_self.shippedOrderQuantity = returnValue[key];
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
		}
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
			//	alert("初始化数据"+getMetaTag().contextPath);
			//	alert("初始化数据"+this.$store.state.contextPath);

			//根据URL路由

			//路径部分
			//	var pathname = window.location.pathname; 

			//	var param = location.search; //获取url中"?"符后的字串

		//	alert(pathname+" -- "+param);
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
	}

	var global = {
		//系统路径
		baseURL : baseURL,
		//资源路径
		commonPath : commonPath,
		//系统虚拟目录
		contextPath : contextPath,
		//令牌
		token : token
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
    };
	return editor.create();
}
