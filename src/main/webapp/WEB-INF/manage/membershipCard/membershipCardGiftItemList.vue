<!-- 会员卡赠送项列表(获赠用户) -->
<template id="membershipCardGiftItemList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="用户" align="center" min-width="120">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<p>呢称: {{scope.row.nickname}}</p>
						            <p>账号: {{scope.row.account}}</p>
					        	</template>
					        	<template #reference >
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
					<el-table-column label="任务类型" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.type == 10" class="tag-wrapper tag-color-orange">长期</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type == 20" class="tag-wrapper tag-color-cyan">一次性</el-tag>
				    	</template>
					</el-table-column>
					
					<el-table-column label="限制条件" align="center">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.restrictionGroup.registrationTime_start != null || scope.row.restrictionGroup.registrationTime_end != null" class="tag-wrapper tag-color-purple">用户注册时间范围</el-tag>
				    		<el-tag effect="dark"  v-if="scope.row.restrictionGroup.totalPoint != null" class="tag-wrapper tag-color-yellow">积分达到数量</el-tag>
				    	
				    		<span v-if="scope.row.restrictionGroup.registrationTime_start == null && scope.row.restrictionGroup.registrationTime_end == null && scope.row.restrictionGroup.totalPoint == null">无限制</span>
				    	
				    	</template>
					</el-table-column>
					<el-table-column prop="postTime" label="赠送时间" align="center" width="170"></el-table-column>
					<el-table-column label="时长" align="center" >
						<template #default="scope">
							{{scope.row.duration}}
							<span v-if="scope.row.unit ==10">小时</span>
					  		<span v-if="scope.row.unit ==20">日</span>
					  		<span v-if="scope.row.unit ==30">月</span>
					  		<span v-if="scope.row.unit ==40">年</span>
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
//会员卡赠送项列表(获赠用户)
export default({
	name: 'membershipCardGiftItemList',//组件名称，keep-alive缓存需要本参数
	template : '#membershipCardGiftItemList-template',
	inject:['reload'], 
	data : function data() {
		return {
			membershipCardGiftTaskId:'',
			tableData: [],//表格内容
		    multipleSelection: [],
		  
		    totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
		};
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.beforeUrl == undefined || to.query.beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);


		if(this.$route.query.membershipCardGiftTaskId != undefined && this.$route.query.membershipCardGiftTaskId != ''){
			this.membershipCardGiftTaskId = this.$route.query.membershipCardGiftTaskId;
		}

		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		//上级路由解码
		if(this.$route.query.beforeUrl != undefined && this.$route.query.beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.beforeUrl));
			
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
		this.queryMembershipCardOrderList();
		
		
	},
	
	methods : {
		//查询会员卡赠送项列表(获赠用户)
		queryMembershipCardOrderList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/membershipCardGiftTask/manage', {
			   params: {
			   		method :'membershipCardGiftItemList',
			   		membershipCardGiftTaskId :_self.membershipCardGiftTaskId,
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
			    		let pageView = returnValue.data;
			    		let list = pageView.records;
			    		if(list != null && list.length >0){
			    			_self.tableData = list;
			    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
			    			_self.currentpage = pageView.currentpage;
							_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
							_self.maxresult = pageView.maxresult;
							_self.isShowPage = true;//显示分页栏
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
				path: '/admin/control/membershipCardGiftTask/manage/membershipCardGiftItemList', 
				query:{ 
					membershipCardGiftTaskId : this.membershipCardGiftTaskId,
					beforeUrl:(this.$route.query.beforeUrl != undefined ? this.$route.query.beforeUrl:''),
					page : page
				}
			});
	    },
	}
});


</script>