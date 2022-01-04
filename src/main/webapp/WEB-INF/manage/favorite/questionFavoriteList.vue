<!-- 问题收藏夹列表 -->
<template id="questionFavoriteList-template">
	<div>
		<div class="main questionFavoriteModule">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/question/manage/view', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :$route.query.questionId, answerId:($route.query.answerId != undefined ? $route.query.answerId:''), page:($route.query.questionPage != undefined ? $route.query.questionPage:'')}})">返回</el-button>
			</div>
			
			<div class="headInfo">
				<div class="title">{{currentQuestion.title}}</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="用户" align="center" min-width="120">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<p v-if="scope.row.nickname != null &&  scope.row.nickname != ''">呢称: {{scope.row.nickname}}</p>
						            <p>账号: {{scope.row.account}}</p>
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
					<el-table-column prop="addtime" label="加入时间" align="center" width="170"></el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>  

<script>
//问题收藏夹列表
export default({
	name: 'questionFavoriteList',//组件名称，keep-alive缓存需要本参数
	template : '#questionFavoriteList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    questionId :'',
		    currentQuestion :'',
		    
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

		
		if(this.$route.query.questionId != undefined && this.$route.query.questionId != ''){
			this.questionId = this.$route.query.questionId;
			
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

		this.queryFavoriteList();
		
		
	},
	
	methods : {
		//查询收藏夹
		queryFavoriteList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/questionFavorite/list', {
				 params: {
					 questionId :_self.questionId,
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
			    			if(key == "currentQuestion"){
			    				_self.currentQuestion = mapData[key];
			    				
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
				path: '/admin/control/questionFavorite/list', 
				query:{ 
					visible:(this.$route.query.visible != undefined ? this.$route.query.visible:''),
					questionView_beforeUrl:(this.$route.query.questionView_beforeUrl != undefined ? this.$route.query.questionView_beforeUrl:''),
					questionId :this.questionId,
					answerId :(this.$route.query.answerId != undefined ? this.$route.query.answerId:''),
					questionPage :(this.$route.query.questionPage != undefined ? this.$route.query.questionPage:''),
					page : page
				}
			});
	    },
	    
	}
});


</script>