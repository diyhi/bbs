<!-- 提醒列表 -->
<template id="remindList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/user/manage/show', query:{ id : $route.query.id,beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">返回</el-button>
			</div>
			<div class="nav-user">
				<div class="avatar">
               		<el-popover effect="light" trigger="hover" placement="bottom">
			        	<template #default>
			        		<p >呢称: {{currentUser.nickname}}</p>
				            <p>账号: {{currentUser.account}}</p>
			        	</template>
			        	<template #reference>
			          		<div class="avatar-wrapper" >
								<div class="avatar-badge" v-if="currentUser.avatarName == null || currentUser.avatarName == ''">
									<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
								</div>
								<div class="avatar-badge" v-if="currentUser.avatarName != null && currentUser.avatarName != ''">
									<el-avatar shape="square" :size="64" :src="currentUser.avatarPath+'100x100/'+currentUser.avatarName"></el-avatar>
								</div>
							</div>
			        	</template>
			        </el-popover>
               	</div>
               	<div class="userName" title="账号">
               		{{currentUser.account}}
               		<div class="nickname" title="呢称">
               			{{currentUser.nickname}}
               			 <i class="tag">提醒</i>
               		</div>
               	</div>
			</div>
			<div class="data-table remindModule" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="发送用户" align="center" width="250">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
						            <p v-if="scope.row.senderNickname != null && scope.row.senderNickname != ''">呢称: {{scope.row.senderNickname}}</p>
						            <p>账号: {{scope.row.senderAccount}}</p>
					        	</template>
					        	<template #reference>
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.senderAvatarName == null || scope.row.senderAvatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.senderAvatarName != null && scope.row.senderAvatarName != ''">
											<el-avatar :size="48" :src="scope.row.senderAvatarPath+'100x100/'+scope.row.senderAvatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.senderAccount}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column label="提醒" align="center" >
						<template #default="scope">
							
							<span v-if="scope.row.typeCode == 10">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId}})">{{scope.row.topicTitle}}</el-link> 评论了我的话题
							</span>
							<span v-if="scope.row.typeCode == 20">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId, replyId:scope.row.friendTopicReplyId}})">{{scope.row.topicTitle}}</el-link> 回复了我的话题
							</span>
							<span v-if="scope.row.typeCode == 30">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId}})">{{scope.row.topicTitle}}</el-link> 引用了我的评论
							</span>
							<span v-if="scope.row.typeCode == 40">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.topicCommentId, replyId:scope.row.friendTopicReplyId}})">{{scope.row.topicTitle}}</el-link> 回复了我的评论
							</span>
							<span v-if="scope.row.typeCode == 50">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId, replyId:scope.row.friendTopicReplyId}})">{{scope.row.topicTitle}}</el-link> 回复了我回复过的评论
							</span>
							<span v-if="scope.row.typeCode == 60">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId}})">{{scope.row.topicTitle}}</el-link> 解锁了我的话题
							</span>
							<span v-if="scope.row.typeCode == 70">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId}})">{{scope.row.topicTitle}}</el-link> 赞了我的话题
							</span>
							<span v-if="scope.row.typeCode == 80">
								关注了我
							</span>
							<span v-if="scope.row.typeCode == 90">
								我关注的 {{scope.row.senderAccount}} 发表了话题 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId}})">{{scope.row.topicTitle}}</el-link>
							</span>
							<span v-if="scope.row.typeCode == 100">
								我关注的 {{scope.row.senderAccount}} 在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId}})">{{scope.row.topicTitle}}</el-link> 发表了评论
							</span>
							<span v-if="scope.row.typeCode == 110">
								我关注的 {{scope.row.senderAccount}} 在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:scope.row.friendTopicCommentId, replyId:scope.row.friendTopicReplyId}})">{{scope.row.topicTitle}}</el-link> 发表了回复
							</span>
							
							<span v-if="scope.row.typeCode == 120">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.questionId,answerId:scope.row.friendQuestionAnswerId}})">{{scope.row.questionTitle}}</el-link> 回答了我的问题
							</span>
							<span v-if="scope.row.typeCode == 130">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.questionId,answerId:scope.row.friendQuestionAnswerId, replyId:scope.row.friendQuestionReplyId}})">{{scope.row.questionTitle}}</el-link> 回复了我的问题
							</span>
							<span v-if="scope.row.typeCode == 140">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.questionId,answerId:scope.row.questionAnswerId, replyId:scope.row.friendQuestionReplyId}})">{{scope.row.questionTitle}}</el-link> 回复了我的答案
							</span>
							<span v-if="scope.row.typeCode == 150">
								在 <el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.questionId,answerId:scope.row.questionAnswerId, replyId:scope.row.friendQuestionReplyId}})">{{scope.row.questionTitle}}</el-link> 回复了我回复过的答案
							</span>
				    	</template>
					</el-table-column>
					<el-table-column prop="sendTime" label="发送时间" align="center" width="150"></el-table-column>
					<el-table-column prop="readTime" label="阅读时间" align="center" width="150"></el-table-column>
					<el-table-column label="操作" align="center" width="150">
						<template #default="scope">
							<el-button-group>
								<el-button type="primary" size="mini" v-if="scope.row.status >100" @click="reductionRemind($event,scope.row)">还原</el-button>
							</el-button-group>
				    	</template>
					
					</el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>  

<script>
//提醒列表
export default({
	name: 'remindList',//组件名称，keep-alive缓存需要本参数
	template : '#remindList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    currentUser:'',
		    
		  	id :'',
			userName :'',//用户名称
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

		if(this.$route.query.id != undefined && this.$route.query.id != ''){
			this.id = this.$route.query.id;
			
		}
		if(this.$route.query.userName != undefined && this.$route.query.userName != ''){
			this.userName = decodeURIComponent(this.$route.query.userName);
			
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryRemindList();
		
		
	},
	
	methods : {
		//查询提醒列表
		queryRemindList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/remind/manage', {
			   params: {
			    	method : 'remindList',
			    	id :_self.id,
			    	page :_self.currentpage
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
			    			if(key == "currentUser"){
			    				_self.currentUser = mapData[key];
			    				
			    			}else if(key == "pageView"){
			    				let pageView = mapData[key];
			    				let list = pageView.records;
					    		if(list != null && list.length >0){
					    			_self.tableData = list;
					 
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
				path: '/admin/control/remind/manage/remindList', 
				query:{ 
					id:this.id,
					userName :encodeURIComponent(this.userName),
					beforeUrl:(this.$route.query.beforeUrl != undefined ? this.$route.query.beforeUrl:''),
					page : page
				}
			});
	    },
	    //还原提醒
	    reductionRemind : function(event,row) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
	    	let _self = this;
	    	
	    	this.$confirm('此操作将还原该提醒, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
	        	formData.append('userId', row.receiverUserId);
	    		formData.append('remindId', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/remind/manage?method=reductionRemind',
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
				    		_self.queryRemindList();
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