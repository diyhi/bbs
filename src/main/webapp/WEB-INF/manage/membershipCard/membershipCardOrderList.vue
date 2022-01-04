<!-- 会员卡订单列表 -->
<template id="membershipCardOrderList-template">
	<div>
		<div class="main">
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="orderId" label="订单号" align="center" width="170"></el-table-column>
					<el-table-column prop="createDate" label="创建时间" align="center" width="170"></el-table-column>
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
					<el-table-column prop="paymentAmount" label="已支付金额" align="center" width="120"></el-table-column>
					<el-table-column prop="paymentPoint" label="已支付积分" align="center" width="120"></el-table-column>
					<el-table-column prop="roleName" label="角色名称" align="center" ></el-table-column>
					<el-table-column prop="specificationName" label="规格名称" align="center" ></el-table-column>
					<el-table-column prop="quantity" label="数量" align="center" width="80"></el-table-column>
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
//会员卡订单列表
export default({
	name: 'membershipCardOrderList',//组件名称，keep-alive缓存需要本参数
	template : '#membershipCardOrderList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		  
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

		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryMembershipCardOrderList();
		
		
	},
	
	methods : {
		//查询会员卡订单列表
		queryMembershipCardOrderList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/membershipCardOrder/list', {
			   params: {
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
				path: '/admin/control/membershipCardOrder/list', 
				query:{
					page : page
				}
			});
	    },
	}
});


</script>