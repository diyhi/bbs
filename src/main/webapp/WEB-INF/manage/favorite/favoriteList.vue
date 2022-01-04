<!-- 收藏夹列表 -->
<template id="favoriteList-template">
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
               			 <i class="tag">收藏夹</i>
               		</div>
               	</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="标题" align="center" >
						<template #default="scope">
							<span v-if="scope.row.module==10" >
								<el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">{{scope.row.topicTitle}}</el-link>
							</span>
							<span v-if="scope.row.module==20" >
								<el-link class="sourceTitle" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.questionId,id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">{{scope.row.questionTitle}}</el-link>
							</span>
						</template>
					</el-table-column>
					<el-table-column label="模块" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.module==10" class="tag-wrapper">话题</el-tag>
							<el-tag effect="dark"  v-if="scope.row.module==20" type="success" class="tag-wrapper" >问题</el-tag>
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
//收藏夹列表
export default({
	name: 'favoriteList',//组件名称，keep-alive缓存需要本参数
	template : '#favoriteList-template',
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

		this.queryFavoriteList();
		
		
	},
	
	methods : {
		//查询收藏夹
		queryFavoriteList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/favorite/list', {
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
				path: '/admin/control/favorite/list', 
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