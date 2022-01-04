<!-- 支付日志详细 -->
<template id="paymentLogShow-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/paymentLog/list', query:{ page:$route.query.paymentLogPage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">返回</el-button>					
			</div>
			<div class="nav-user clearfix">
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
               			 <i class="tag">支付日志详细</i>
               		</div>
               	</div>
			</div>
			<div class="data-view" >
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">支付流水号：</div></el-col>
					<el-col :span="20"><div class="content">{{paymentLog.paymentRunningNumber}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">支付模块：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="paymentLog.paymentModule == 1">订单支付</span>
		    				<span v-if="paymentLog.paymentModule == 5">用户充值</span>
		    				<span v-if="paymentLog.paymentModule == 6">账户提现</span>
		    				<span v-if="paymentLog.paymentModule == 70">余额购买话题隐藏内容</span>
		    				<span v-if="paymentLog.paymentModule == 80">解锁话题隐藏内容分成</span>
		    				<span v-if="paymentLog.paymentModule == 90">悬赏金额</span>
		    				<span v-if="paymentLog.paymentModule == 100">采纳答案</span>
		    				<span v-if="paymentLog.paymentModule == 110">调整赏金</span>
		    				<span v-if="paymentLog.paymentModule == 120">话题发红包</span>
		    				<span v-if="paymentLog.paymentModule == 130">话题收红包</span>
		    				<span v-if="paymentLog.paymentModule == 140">话题返还红包</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">接口产品：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="paymentLog.interfaceProduct == -1">员工操作</span>
		    				<span v-if="paymentLog.interfaceProduct == 0">预存款支付</span>
		    				<span v-if="paymentLog.interfaceProduct == 1">支付宝即时到账</span>
		    				<span v-if="paymentLog.interfaceProduct == 4">支付宝手机网站</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex" v-if="paymentLog.operationUserType ==1">
					<el-col :span="4"><div class="name">操作员工名称：</div></el-col>
					<el-col :span="20"><div class="content">{{paymentLog.operationUserName}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">交易号：</div></el-col>
					<el-col :span="20"><div class="content">{{paymentLog.tradeNo}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">参数Id：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="paymentLog.paymentModule == 1">订单Id </span>
	    					<span v-if="paymentLog.paymentModule == 5">用户Id </span>
	    					<span v-if="paymentLog.paymentModule == 70 || paymentLog.paymentModule == 80">话题Id </span>
	    					<span v-if="paymentLog.paymentModule == 90">问题Id </span>
	    					<span v-if="paymentLog.paymentModule == 100">答案Id </span>
	    					<span v-if="paymentLog.paymentModule == 110">问题Id </span>
	    					<span v-if="paymentLog.paymentModule == 120">发红包Id </span>
	    					<span v-if="paymentLog.paymentModule == 130">收红包Id </span>
	    					<span v-if="paymentLog.paymentModule == 140">发红包Id </span>
	    					{{paymentLog.sourceParameterId}}
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">金额：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="paymentLog.amountState == 1">+</span>
	    					<span v-if="paymentLog.amountState == 2">-</span>
	    					{{paymentLog.amount}}
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">时间：</div></el-col>
					<el-col :span="20"><div class="content">{{paymentLog.times}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">备注：</div></el-col>
					<el-col :span="20"><div class="content">{{paymentLog.remark}}</div></el-col>
				</el-row>
			</div>
		</div>
	</div>
</template>  

<script>
//支付日志详细
export default({
	name: 'paymentLogShow',//组件名称，keep-alive缓存需要本参数
	template : '#paymentLogShow-template',
	inject:['reload'], 
	data : function data() {
		return {
		    currentUser :'',//当前用户
		    paymentRunningNumber :'',
		    paymentLog :'',
		    
		  	id :'',
			userName :'',//用户名称
		};
	},

	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		if(this.$route.query.paymentRunningNumber != undefined && this.$route.query.paymentRunningNumber != ''){
			this.paymentRunningNumber = this.$route.query.paymentRunningNumber;
			
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

		this.queryPaymentLogShow();
		
		
	},
	
	methods : {
		//查询支付日志详细
		queryPaymentLogShow : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/paymentLog/manage', {
				 params: {
					method :"show",
					paymentRunningNumber : _self.paymentRunningNumber,
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
			    				
			    			}else if(key == "paymentLog"){
			    				_self.paymentLog = mapData[key];
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
	    
	}
});


</script>