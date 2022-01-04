<!-- 支付日志列表 -->
<template id="paymentLogList-template">
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
               			 <i class="tag">支付日志</i>
               		</div>
               	</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="paymentRunningNumber" label="支付流水号" align="center" width="270"></el-table-column>
					
					<el-table-column label="支付模块" align="center" width="200">
						<template #default="scope">
							<span v-if="scope.row.paymentModule == 1">订单支付</span>
		    				<span v-if="scope.row.paymentModule == 5">用户充值</span>
		    				<span v-if="scope.row.paymentModule == 6">账户提现</span>
		    				<span v-if="scope.row.paymentModule == 70">余额购买话题隐藏内容</span>
		    				<span v-if="scope.row.paymentModule == 80">解锁话题隐藏内容分成</span>
		    				<span v-if="scope.row.paymentModule == 90">悬赏金额</span>
		    				<span v-if="scope.row.paymentModule == 100">采纳答案</span>
		    				<span v-if="scope.row.paymentModule == 110">调整赏金</span>
		    				<span v-if="scope.row.paymentModule == 120">话题发红包</span>
		    				<span v-if="scope.row.paymentModule == 130">话题收红包</span>
		    				<span v-if="scope.row.paymentModule == 140">话题返还红包</span>
						</template>
					</el-table-column>
					
					<el-table-column label="接口产品" align="center" >
						<template #default="scope">
							<span v-if="scope.row.interfaceProduct == -1">员工操作</span>
		    				<span v-if="scope.row.interfaceProduct == 0">预存款支付</span>
		    				<span v-if="scope.row.interfaceProduct == 1">支付宝即时到账</span>
		    				<span v-if="scope.row.interfaceProduct == 4">支付宝手机网站</span>
						</template>
					</el-table-column>
					<el-table-column label="金额" align="center" width="100">
						<template #default="scope">
							<span v-if="scope.row.amountState == 1">+</span>
	    					<span v-if="scope.row.amountState == 2">-</span>
	    					{{scope.row.amount}}
						</template>
					</el-table-column>
					<el-table-column prop="times" label="时间" align="center" width="170"></el-table-column>
					<el-table-column label="操作" align="center" width="100">
						<template #default="scope">
							<el-button-group>
								<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/paymentLog/manage/show', query:{ paymentRunningNumber : scope.row.paymentRunningNumber, paymentLogPage:currentpage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">查看</el-button>
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
//支付日志列表
export default({
	name: 'paymentLogList',//组件名称，keep-alive缓存需要本参数
	template : '#paymentLogList-template',
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

		this.queryPaymentLogList();
		
		
	},
	
	methods : {
		//查询支付日志列表
		queryPaymentLogList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/paymentLog/list', {
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
				path: '/admin/control/paymentLog/list', 
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