<!-- 解锁隐藏内容用户列表 -->
<template id="topicUnhideList-template">
	<div>
		<div class="main topicUnhideModule">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/topic/manage/view', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :$route.query.topicId, commentId:($route.query.commentId != undefined ? $route.query.commentId:''), page:($route.query.topicPage != undefined ? $route.query.topicPage:'')}})">返回</el-button>
			</div>
			<div class="headInfo">
				<div class="title">{{currentTopic.title}}</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="用户" align="center" >
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
						            <p v-if="scope.row.nickname != null && scope.row.nickname != ''">呢称: {{scope.row.nickname}}</p>
						            <p v-if="scope.row.userName != null && scope.row.userName != ''">账号: {{scope.row.account}}</p>
					        	</template>
					        	<template #reference>
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
											<el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.account}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column label="类型" align="center" width="200">
						<template #default="scope">
							<span v-if="scope.row.hideTagType == 10">输入密码</span>
	    					<span v-if="scope.row.hideTagType == 40">积分购买</span>
	    					<span v-if="scope.row.hideTagType == 50">余额购买</span>
				    	</template>
					</el-table-column>
					
					<el-table-column label="解锁时间" align="center" width="200">
						<template #default="scope">
							{{scope.row.cancelTime}}
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
//解锁隐藏内容用户列表
export default({
	name: 'topicUnhideList',//组件名称，keep-alive缓存需要本参数
	template : '#topicUnhideList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    currentTopic :'',//当前话题
		    topicId :'',
		  	
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

		if(this.$route.query.topicId != undefined && this.$route.query.topicId != ''){
			this.topicId = this.$route.query.topicId;
			
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryTopicUnhideList();
		
		
	},
	
	methods : {
		//查询解锁隐藏内容用户列表
		queryTopicUnhideList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/topic/topicUnhideList', {
				 params: {
					topicId :_self.topicId,
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
			    			if(key == "currentTopic"){
			    				_self.currentTopic = mapData[key];
			    				
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
				path: '/admin/control/topic/topicUnhideList', 
				query:{ 
					visible:(this.$route.query.visible != undefined ? this.$route.query.visible:''),
					topicView_beforeUrl:(this.$route.query.topicView_beforeUrl != undefined ? this.$route.query.topicView_beforeUrl:''),
					topicId :this.topicId,
					commentId :(this.$route.query.commentId != undefined ? this.$route.query.commentId:''),
					topicPage :(this.$route.query.topicPage != undefined ? this.$route.query.topicPage:''),
					page : page
				}
			});
	    },
	}
});


</script>