<!-- 积分日志列表 -->
<template id="pointLogList-template">
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
               			 <i class="tag">积分日志</i>
               		</div>
               	</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="模块" align="center" width="180">
						<template #default="scope">
							<span v-if="scope.row.module == 100">发表话题</span>
		    				<span v-if="scope.row.module == 200">发表评论</span>
		    				<span v-if="scope.row.module == 300">发表回复</span>
		    				<span v-if="scope.row.module == 400">积分解锁话题隐藏内容</span>
		    				<span v-if="scope.row.module == 500">会员卡订单支付</span>
		    				<span v-if="scope.row.module == 600">充值</span>
		    				<span v-if="scope.row.module == 700">提交问题</span>
		    				<span v-if="scope.row.module == 800">提交答案</span>
		    				<span v-if="scope.row.module == 900">提交答案回复</span>
		    				<span v-if="scope.row.module == 1000">悬赏积分</span>
		    				<span v-if="scope.row.module == 1100">采纳答案</span>
		    				<span v-if="scope.row.module == 1200">调整赏金</span>
						</template>
					</el-table-column>
					
					<el-table-column label="操作用户账号" align="center" >
						<template #default="scope">
							{{scope.row.operationAccount}}
							<el-tag effect="dark"  v-if="scope.row.operationUserType==0" class="tag-wrapper">系统</el-tag>
							<el-tag effect="dark"  v-if="scope.row.operationUserType==1" type="success" class="tag-wrapper" >员工</el-tag>
							<el-tag effect="dark"  v-if="scope.row.operationUserType==2" type="info" class="tag-wrapper" >会员</el-tag>
						</template>
					</el-table-column>
					<el-table-column label="积分" align="center" width="100">
						<template #default="scope">
							<span v-if="scope.row.pointState == 1">+</span>
	    					<span v-if="scope.row.pointState == 2">-</span>
	    					{{scope.row.point}}
						</template>
					</el-table-column>
					<el-table-column prop="times" label="时间" align="center" width="170"></el-table-column>
					<el-table-column label="操作" align="center" width="100">
						<template #default="scope">
							<el-button-group>
								<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/pointLog/manage/show', query:{ pointLogId : scope.row.id, pointLogPage:currentpage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">查看</el-button>
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
//积分日志列表
export default({
	name: 'pointLogList',//组件名称，keep-alive缓存需要本参数
	template : '#pointLogList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    currentUser :'',//当前用户
		    
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

		this.queryPointLogList();
		
		
	},
	
	methods : {
		//查询积分日志列表
		queryPointLogList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/pointLog/list', {
				 params: {
					id :_self.id,
					userName :_self.userName,
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
				path: '/admin/control/pointLog/list', 
				query:{ 
					id:this.id,
					userName :encodeURIComponent(this.userName),
					beforeUrl:(this.$route.query.beforeUrl != undefined ? this.$route.query.beforeUrl:''),
					page : page
				}
			});
	    },
	    
	}
});


</script>