<!-- 帮助查看 -->
<template id="helpView-template">
	<div>
		<div class="main">
			<div class="navbar">
				<!-- 返回上级节点 -->
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			</div>
			<div class="helpViewModule" >
				<div class="help-wrap">
					<div class="helpTag">
						<span class="tag">{{help.helpTypeName}}</span>
					</div>
					<div class="operat">
						<el-link class="item" href="javascript:void(0);" @click="addMediaProcessQueue(help.id)">添加媒体处理任务</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/help/manage/edit', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),helpView_beforeUrl:($route.query.helpView_beforeUrl != undefined ? $route.query.helpView_beforeUrl:''),helpId :help.id, page:($route.query.page != undefined ? $route.query.page:''), helpPage:($route.query.page != undefined ? $route.query.page:'')}})">修改</el-link>
						<el-link class="item" href="javascript:void(0);" @click="deleteHelp(help.id)">删除</el-link>
					</div>
					<div class="head">
		            	<div class="title">
		                	{{help.name}}
		            	</div>
		           		<div class="helpInfo clearfix" >
		                	<div class="userName" title="员工名称">
		                		{{help.userName}}
		                	</div>
		                	<div class="postTime" title="发表时间">{{help.times}}</div>
		            		<div class="rightTag" >
		            			<div class="statusTagInfo">
									<span class="red-tag" v-if="help.visible == false" title="帮助状态">员工删除</span>		
								</div>
							</div>
		            	</div>
					</div>
					<div class="main" :ref="'help_'+help.id" >
						<component v-bind:is ="helpComponent(help.content)" v-bind="$props" /> 
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
//帮助查看
export default({
	name: 'helpView',//组件名称，keep-alive缓存需要本参数
	template : '#helpView-template',
	inject:['reload'], 
	data : function data() {
		return {
			id:'',
			help:'',
			
			sourceUrlObject:{},//来源URL对象
			
			error:{},
			
			
			playerIdList: [],//视频播放Id列表
		    playerObjectList: [],//视频播放对象集合
		    playerNodeList: [],//视频节点对象集合
		    
		};
	},
	
	//keep-alive 进入时
	activated: function () {
		let _self = this;
		
	},
	// keep-alive 离开时
	deactivated : function () {
	
	},
	//生命周期钩子 -- 响应数据修改时运行
	updated : function () {
		let _self = this;
		_self.$nextTick(function() {
			if(_self.id != ''){
				let helpRefValue = _self.$refs['help_'+_self.id];
				if(helpRefValue != undefined){
					_self.renderBindNode(helpRefValue); 
				}
	            
			}
		})
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.helpView_beforeUrl == undefined || to.query.helpView_beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'helpView_beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.helpView_beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.helpId != undefined && this.$route.query.helpId != ''){
			this.id = this.$route.query.helpId;
		}
		
		//上级路由解码
		if(this.$route.query.helpView_beforeUrl != undefined && this.$route.query.helpView_beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.helpView_beforeUrl));
			
			let decryptObject = JSON.parse(decrypt);
			
			let query = decryptObject.query;
			for(let q in query){
				query[q] = decodeURIComponent(query[q]);
			}
			this.sourceUrlObject = {
				path : decryptObject.path,
				query :query
			}
		}
		//初始化
		this.queryHelp();
	},
	computed: {
		//动态解析帮助模板数据
		helpComponent: function() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					
					data : function() {
						return {
						};
					},
					mounted :function () {
						this.resumePlayerNodeData();
					},
					props: this.$props, // re-use current props definitions
					methods: {
				        //恢复播放器节点数据(vue组件切换时会自动刷新数据，视频播放器框在组件生成数据内容之后插入，组件刷新数据时播放器框会消失，组件刷新后需要用之前的节点数据恢复)
				        resumePlayerNodeData : function(){
				        	let _self = this;
				        	_self.$nextTick(function() {
					        	if(_self.$parent.playerObjectList.length >0){
					        		for(let i=0; i< _self.$parent.playerNodeList.length; i++){
					        			let playerNode = _self.$parent.playerNodeList[i];
					        			let playerId = playerNode.getAttribute("id");
					        			let node = document.getElementById(playerId);
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
		//查询帮助
	    queryHelp: function(){
	        let _self = this;
			
			_self.help = '';
			
			_self.$ajax.get('control/help/manage', {
			    params: {
			    	method : 'view',
			    	helpId : _self.id,
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
			    		let help = returnValue.data;
			    		
			    		//清空播放器
						_self.clearVideoPlayer();
								
								
								
						//处理自定义标签
						let contentNode = document.createElement("div");
						contentNode.innerHTML = help.content;
								
						_self.bindNode(contentNode);
						help.content = escapeVueHtml(contentNode.innerHTML);
								
								
						_self.help = help;
									
						_self.$nextTick(function() {
							setTimeout(function() {
								_self.renderVideoPlayer();//渲染视频播放器
							}, 30);		
						});
			    		
			    		
			    		
			    		
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			if(_self.error[key] == undefined){
			    				_self.$message({
									duration :0,
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
			    			}else{
			    				_self.error[key] = errorMap[key];
			    			}
			    	    }
			    	}
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//清空播放器
		clearVideoPlayer : function() {
			let _self = this;
			
			for(let i=0; i< _self.playerObjectList.length; i++){
				let playerObject = _self.playerObjectList[i];
				
				playerObject.destroy();//销毁播放器
			}
			_self.playerObjectList.length = 0;//清空数组
			_self.playerIdList.length = 0;//清空数组
			_self.playerNodeList.length = 0;//清空数组
		},
		//渲染视频播放器
		renderVideoPlayer : function() {
			let _self = this;
			
			
			
			
			for(let i=0; i< _self.playerIdList.length; i++){
				let playerId = _self.playerIdList[i];
				let url = document.getElementById(playerId).getAttribute("url");
        		let cover = document.getElementById(playerId).getAttribute("cover");//封面
        		let thumbnail = document.getElementById(playerId).getAttribute("thumbnail");//缩略图
				
				let dp = null;
        		if(url == ""){//如果视频处理中
        			dp = new DPlayer({
            			container: document.getElementById(playerId),//播放器容器元素
            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
            			
            			video: {
            			    
            			}
            		});
					let dom = document.createElement('div');
					dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
					document.getElementById(playerId).appendChild(dom);
				}else{
					if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
	        			dp = new DPlayer({
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
	        			dp = new DPlayer({
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
				
				for(let i=0; i< _self.playerIdList.length; i++){
			    	let playerId = _self.playerIdList[i];
			    	let node = document.getElementById(playerId);//节点对象
			    	_self.playerNodeList.push(node);
			    }
			}
			
		},
		
		//递归绑定节点参数
		bindNode : function(node) {
			//先找到子节点
	        let nodeList = node.childNodes;
	        for(let i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            let childNode = nodeList[i];
	            let random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	
        	
	            	
	            	//处理图片
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		let src = childNode.getAttribute("src");
            			
            			childNode.removeAttribute("src");//将原节点src属性删除，防止多请求一次
            		
						let html = '';
						let style = '';
						if(childNode.getAttribute("width") != null){//如果是表情，表情图不放大
							style = 'style="width: '+childNode.getAttribute("width")+'; height: '+childNode.getAttribute("height")+'"';
							html = '<el-image src="'+src+'" '+style+' lazy ></el-image>';
						}else{
						
							html = '<el-image src="'+src+'" '+style+' :preview-src-list=["'+src+'"] lazy hide-on-click-modal ></el-image>';
						}
						//创建要替换的元素
					//	let html = '<el-image src="'+src+'" '+style+' lazy></el-image>';
					//	let html = '<el-image src="'+src+'" '+style+' :preview-src-list=["http://127.0.0.1:8080/cms/common/tttttt/templates.jpg"] lazy hide-on-click-modal ></el-image>';
						
					
					
					//	let html = '<el-image src="backstage/images/null.gif" lazy></el-image>';
						let placeholder = document.createElement('div');
						placeholder.innerHTML = html;
						let node = placeholder.childNodes[0];
					//	node.setAttribute("src",src);
            			childNode.parentNode.replaceChild(node,childNode);//替换节点	 
	            	}
	            	
	            	
	            	
	            	//处理视频标签
	            	if(childNode.nodeName.toLowerCase() == "player" ){
	            		
	            		let id = "player_"+random+"_"+i;
	            		childNode.setAttribute("id",id);//设置Id
	            		this.playerIdList.push(id);	
	            	}
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		let pre_html = childNode.innerHTML;
	            		let class_val = childNode.className;
	            		let lan_class = "";
	            		
	        	        let class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(let k=0; k<class_arr.length; k++){
	        	        	let className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	        	        let nodeHtml = "";

            			//删除code节点
            			let preChildNodeList = childNode.childNodes;
            			for(let p = 0;p < preChildNodeList.length;p++){
            				let preChildNode = preChildNodeList[p];
            				if(preChildNode.nodeName.toLowerCase() == "code" ){
            					nodeHtml += preChildNode.innerHTML;
            					preChildNode.parentNode.removeChild(preChildNode);
                			}
            				
            			}
            			
            			let dom = document.createElement('code');
            			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				//Prism.highlightElement(dom);
	            		
	            	}
	            	
	            	this.bindNode(childNode);
	            }
	        }
	    },
	    //递归渲染绑定节点
	    renderBindNode: function(node){	
	         //先找到子节点
	        let nodeList = node.childNodes;
	        for(let i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            let childNode = nodeList[i];
	            let random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	                //处理代码标签
	                if(childNode.nodeName.toLowerCase() == "pre" ){
	                    Prism.highlightAllUnder(childNode);
	                }
	                this.renderBindNode(childNode);
	            }
	        }
	    },
	    
	    //删除帮助
		deleteHelp : function(helpId) {
			let _self = this;
			
			this.$confirm('此操作将删除该帮助, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('helpId', helpId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/help/manage?method=delete',
			        data: formData
				})
				.then(function (response) {
					if(response == null){
						return;
					}
				    let result = response.data;
				    if(result){
				    	
				    	let returnValue = JSON.parse(result);
				    	if(returnValue.code === 200){//成功
				    		_self.$message.success("操作成功");
				    			
				    		_self.$router.push({
				    			path: _self.sourceUrlObject.path, 
				    			query:_self.sourceUrlObject.query
							});
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
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
	        }).catch((error) => {
	        	console.log(error);
	        });
			
			
			
		},
		//添加媒体处理任务
		addMediaProcessQueue : function(helpId) {
			let _self = this;
			this.$confirm('此操作将添加媒体处理任务, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('id', helpId);
		    	formData.append('module', 60);
		    	
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/mediaProcessQueue/manage?method=addMediaProcessQueue',
			        data: formData
				})
				.then(function (response) {
					if(response == null){
						return;
					}
				    let result = response.data;
				    if(result){
				    	
				    	let returnValue = JSON.parse(result);
				    	if(returnValue.code === 200){//成功
				    		_self.$message.success("操作成功");
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
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
	        }).catch((error) => {
	        	console.log(error);
	        });
		
		},
	}
});
</script>