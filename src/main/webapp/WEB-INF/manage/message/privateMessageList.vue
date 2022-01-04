<!-- 私信列表 -->
<template id="privateMessageList-template">
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
               			 <i class="tag">私信</i>
               		</div>
               	</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column type="selection" v-if="false"></el-table-column>
					<el-table-column label="对方用户" align="center" width="250">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
						            <p v-if="scope.row.friendNickname != null && scope.row.friendNickname != ''">呢称: {{scope.row.friendNickname}}</p>
						            <p>账号: {{scope.row.friendAccount}}</p>
					        	</template>
					        	<template #reference>
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.friendAvatarName == null || scope.row.friendAvatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.friendAvatarName != null && scope.row.friendAvatarName != ''">
											<el-avatar :size="48" :src="scope.row.friendAvatarPath+'100x100/'+scope.row.friendAvatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.friendAccount}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column prop="messageContent" label="最新私信内容" align="center"></el-table-column>
					<el-table-column prop="sendTime" label="发送时间" align="center" width="150"></el-table-column>
					<el-table-column label="操作" align="center" width="150">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/privateMessage/manage/privateMessageChatList', query:{ friendUserId : scope.row.friendUserId, privateMessagePage:currentpage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">对话</el-button>
									<el-button type="primary" size="mini" @click="deletePrivateMessage($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
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
//私信列表
export default({
	name: 'privateMessageList',//组件名称，keep-alive缓存需要本参数
	template : '#privateMessageList-template',
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

		this.queryPrivateMessageList();
		
		
	},
	
	methods : {
		//查询私信列表
		queryPrivateMessageList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/privateMessage/manage', {
			   params: {
			    	method : 'privateMessageList',
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
				path: '/admin/control/privateMessage/manage/privateMessageList', 
				query:{ 
					id:this.id,
					userName :encodeURIComponent(this.userName),
					beforeUrl:(this.$route.query.beforeUrl != undefined ? this.$route.query.beforeUrl:''),
					page : page
				}
			});
	    },
	  	//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    //删除私信
	    deletePrivateMessage : function(event,row) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
	    	let _self = this;
	    	if (row) {//选中行
	    		this.$refs.multipleTable.toggleRowSelection(row,true);
	    	}
	    	
	    	if(this.multipleSelection.length <1){
	    		this.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	this.$confirm('此操作将删除该私信, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('userId', rowData.userId);
		    		formData.append('friendUserId', rowData.friendUserId);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/privateMessage/manage?method=deletePrivateMessageChat',
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
				    		_self.$message.success("删除成功");
				    		_self.queryPrivateMessageList();
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
	        	//取消选中行
	        	this.$refs.multipleTable.toggleRowSelection(row,false);
	        	
	        	console.log(error);
	        });
	    	
	    }
	}
});


</script>