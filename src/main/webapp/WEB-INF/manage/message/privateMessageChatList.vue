<!-- 私信对话列表 -->
<template id="privateMessageChatList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/privateMessage/manage/privateMessageList', query:{ page:$route.query.privateMessagePage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">返回</el-button>					
			</div>
			
			
			<div class="privateMessageChatModule">
				<div class="headInfo">
					<div class="title">与 {{chatUser.account}} <span v-if="chatUser.nickname != null && chatUser.nickname != ''">({{chatUser.nickname}})</span> 的对话</div>
				</div>
			
				<div v-for="privateMessage in privateMessageChatList" >
					<div class="friend" v-if="privateMessage.friendUserId == privateMessage.senderUserId">
						<el-popover effect="light" trigger="hover" placement="top">
				        	<template #default>
					            <p v-if="privateMessage.senderNickname != null && privateMessage.senderNickname != ''">呢称: {{privateMessage.senderNickname}}</p>
					            <p>账号: {{privateMessage.senderAccount}}</p>
				        	</template>
				        	<template #reference>
				          		<div class="avatar-wrapper" >
									<div class="avatar-badge" v-if="privateMessage.senderAvatarName == null || privateMessage.senderAvatarName == ''">
										<el-avatar :size="64" icon="el-icon-user-solid"></el-avatar>
									</div>
									<div class="avatar-badge" v-if="privateMessage.senderAvatarName != null && privateMessage.senderAvatarName != ''">
										<el-avatar :size="64" :src="privateMessage.senderAvatarPath+'100x100/'+privateMessage.senderAvatarName"></el-avatar>
									</div>
								</div>
				        	</template>
				        </el-popover>
						<div class="chat-container">
				            <span class="chat-item">
				            	<div class="topInfo">
				            		<span class="time">{{privateMessage.sendTime}}</span>
				            		
					            	<span class="tag-color-gray" v-if="privateMessage.status == 10">未读</span>
					            	<span class="tag-color-green" v-if="privateMessage.status == 20">已读</span>
					            	<span class="tag-color-pink" v-if="privateMessage.status == 110">未读删除</span>
					            	<span class="tag-color-pink" v-if="privateMessage.status == 120">已读删除</span>
					            	<el-link class="reduction" href="javascript:void(0);" v-if="privateMessage.status >100" @click="reductionPrivateMessage(privateMessage)" title="还原用户已删除的私信">还原</el-link>
				            	</div>
				            	<i></i>
				            	<em v-html="privateMessage.messageContent"></em>
				            	<!-- <div class="msg-del" title="删除"></div> -->
							</span>
						</div>
					</div>
					<div class="self" v-if="privateMessage.friendUserId != privateMessage.senderUserId">
						<el-popover effect="light" trigger="hover" placement="top">
				        	<template #default>
					            <p v-if="privateMessage.senderNickname != null && privateMessage.senderNickname != ''">呢称: {{privateMessage.senderNickname}}</p>
					            <p>账号: {{privateMessage.senderAccount}}</p>
				        	</template>
				        	<template #reference>
				          		<div class="avatar-wrapper" >
									<div class="avatar-badge" v-if="privateMessage.senderAvatarName == null || privateMessage.senderAvatarName == ''">
										<el-avatar :size="64" icon="el-icon-user-solid"></el-avatar>
									</div>
									<div class="avatar-badge" v-if="privateMessage.senderAvatarName != null && privateMessage.senderAvatarName != ''">
										<el-avatar :size="64" :src="privateMessage.senderAvatarPath+'100x100/'+privateMessage.senderAvatarName"></el-avatar>
									</div>
								</div>
				        	</template>
				        </el-popover>
						<div class="chat-container">
				            <span class="chat-item">
				            	<div class="topInfo">
				            		<el-link class="reduction" href="javascript:void(0);" v-if="privateMessage.status >100" @click="reductionPrivateMessage(privateMessage)" title="还原用户已删除的私信">还原</el-link>
					            	
					            	<span class="tag-color-pink" v-if="privateMessage.status > 110">删除</span>
					            	<span class="time">{{privateMessage.sendTime}}</span>
				            	</div>
				            	<i></i>
				            	<em v-html="privateMessage.messageContent"></em>
				            	<!-- 
				            	<div class="msg-del" title="删除"></div> -->
							</span>
						</div>
					</div>
				</div>
			</div>
			<div class="data-table" >
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>

			
		</div>
	</div>
</template>  

<script>
//私信对话列表
export default({
	name: 'privateMessageChatList',//组件名称，keep-alive缓存需要本参数
	template : '#privateMessageChatList-template',
	inject:['reload'], 
	data : function data() {
		return {
			privateMessageChatList:[],
		    
		    friendUserId :'',
		  	id :'',
			userName :'',//用户名称
			chatUser :'',//对话用户
		    totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
		};
	},

	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		
		if(this.$route.query.friendUserId != undefined && this.$route.query.friendUserId != ''){
			this.friendUserId = this.$route.query.friendUserId;
			
		}
		if(this.$route.query.id != undefined && this.$route.query.id != ''){
			this.id = this.$route.query.id;
			
		}
		if(this.$route.query.userName != undefined && this.$route.query.userName != ''){
			this.userName = decodeURIComponent(this.$route.query.userName);
			
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryPrivateMessageChatList();
		
		
	},
	
	methods : {
		//查询私信对话列表
		queryPrivateMessageChatList : function() {
			let _self = this;
			
			//清空内容
			_self.privateMessageChatList = []; 
			
			
			 _self.$ajax.get('control/privateMessage/manage', {
			   params: {
			    	method : 'privateMessageChatList',
			    	id :_self.id,
			    	friendUserId : _self.friendUserId,
			    	page : (this.$route.query.page == undefined || this.$route.query.page == ''? '':_self.currentpage),
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
			    			if(key == "chatUser"){
			    				_self.chatUser = mapData[key];
			    				
			    			}else if(key == "pageView"){
			    				let pageView = mapData[key];
			    				let list = pageView.records;
					    		if(list != null && list.length >0){
					    			_self.privateMessageChatList = list;
					 
					    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = pageView.currentpage;
									_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = pageView.maxresult;
									_self.isShowPage = true;//显示分页栏
					    		}
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
		//分页
		page: function(page) {
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/privateMessage/manage/privateMessageChatList', 
				query:{ 
					friendUserId : this.friendUserId,
					privateMessagePage:this.$route.query.privateMessagePage,
					id:this.id,
					userName :encodeURIComponent(this.userName),
					beforeUrl:(this.$route.query.beforeUrl != undefined ? this.$route.query.beforeUrl:''),
					page : page
				}
			});
	    },
	  	
	    //还原用户已删除的私信
	    reductionPrivateMessage : function(privateMessage) {
	    	let _self = this;
	    	
	    	
	    	this.$confirm('此操作将还原该私信, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
	        	formData.append('userId', privateMessage.userId);
	        	formData.append('privateMessageId', privateMessage.id);
	        	
				this.$ajax({
			        method: 'post',
			        url: 'control/privateMessage/manage?method=reductionPrivateMessage',
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
				    		_self.$message.success("还原成功");
				    		_self.queryPrivateMessageChatList();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		let htmlContent = "";
				    		let count = 0;
				    		for (let key in errorMap) {   
				    			count++;
				    			htmlContent += "<p>"+count + ". " + errorMap[key]+"</p>";
				    			
				    	    }
				    		
				    		
				    		_self.$alert(htmlContent, '错误', {
				    			showConfirmButton :false,
				    			dangerouslyUseHTMLString: true
				    		})
				    		.catch(function (error) {
								console.log(error);
							});
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
	    	
	    }
	}
});


</script>