<!-- 会员卡赠送任务 -->
<template id="membershipCardGiftTaskView-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/membershipCardGiftTask/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-view" >
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">名称：</div></el-col>
					<el-col :span="20"><div class="content">{{membershipCardGiftTask.name}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">角色：</div></el-col>
					<el-col :span="20"><div class="content">{{membershipCardGiftTask.userRoleName}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">任务类型：</div></el-col>
					<el-col :span="20"><div class="content">
						<span v-if="membershipCardGiftTask.type ==10">长期</span>
						<span v-if="membershipCardGiftTask.type ==20">一次性</span>
					</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">任务创建时间：</div></el-col>
					<el-col :span="20"><div class="content">{{membershipCardGiftTask.createDate}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">用户注册时间范围：</div></el-col>
					<el-col :span="20"><div class="content">
						<span v-if="membershipCardGiftTask.restrictionGroup.registrationTime_start != null || membershipCardGiftTask.restrictionGroup.registrationTime_end != null">
							{{membershipCardGiftTask.restrictionGroup.registrationTime_start}} 至 {{membershipCardGiftTask.restrictionGroup.registrationTime_end}}
						</span>
						<span v-if="membershipCardGiftTask.restrictionGroup.registrationTime_start == null && membershipCardGiftTask.restrictionGroup.registrationTime_end == null">
							无限制
						</span>
					</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">活动期间积分达到数量：</div></el-col>
					<el-col :span="20"><div class="content">
						<span v-if="membershipCardGiftTask.restrictionGroup.totalPoint != null">
							{{membershipCardGiftTask.restrictionGroup.totalPoint}}
						</span>
						<span v-if="membershipCardGiftTask.restrictionGroup.totalPoint == null">
							无限制
						</span>
					</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">赠送时长：</div></el-col>
					<el-col :span="20"><div class="content">
						{{membershipCardGiftTask.duration}}
						<span v-if="membershipCardGiftTask.unit ==10">小时</span>
					  	<span v-if="membershipCardGiftTask.unit ==20">日</span>
					  	<span v-if="membershipCardGiftTask.unit ==30">月</span>
					  	<span v-if="membershipCardGiftTask.unit ==40">年</span>
					</div></el-col>
				</el-row>
				
				
				
				
				<!--
				
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">模块：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="pointLog.module == 100">发表话题</span>
		    				<span v-if="pointLog.module == 200">发表评论</span>
		    				<span v-if="pointLog.module == 300">发表回复</span>
		    				<span v-if="pointLog.module == 400">积分解锁话题隐藏内容</span>
		    				<span v-if="pointLog.module == 500">会员卡订单支付</span>
		    				<span v-if="pointLog.module == 600">充值</span>
		    				<span v-if="pointLog.module == 700">提交问题</span>
		    				<span v-if="pointLog.module == 800">提交答案</span>
		    				<span v-if="pointLog.module == 900">提交答案回复</span>
		    				<span v-if="pointLog.module == 1000">悬赏积分</span>
		    				<span v-if="pointLog.module == 1100">采纳答案</span>
		    				<span v-if="pointLog.module == 1200">调整赏金</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">操作用户名称：</div></el-col>
					<el-col :span="20">
						<div class="content">
							{{pointLog.operationUserName}}
							<el-tag effect="dark"  v-if="pointLog.operationUserType==0" class="tag-wrapper">系统</el-tag>
							<el-tag effect="dark"  v-if="pointLog.operationUserType==1" type="success" class="tag-wrapper" >会员</el-tag>
							<el-tag effect="dark"  v-if="pointLog.operationUserType==2" type="info" class="tag-wrapper" >员工</el-tag>
					
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">参数Id：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="pointLog.module == 100">话题Id：</span>
	    					<span v-if="pointLog.module == 200">评论Id：</span>
	    					<span v-if="pointLog.module == 300">回复Id：</span>
	    					<span v-if="pointLog.module == 400">话题Id：</span>
	    					<span v-if="pointLog.module == 500">订单号：</span>
	    					<span v-if="pointLog.module == 600">用户Id：</span>
	    					<span v-if="pointLog.module == 700">问题Id：</span>
	    					<span v-if="pointLog.module == 800">答案Id：</span>
	    					<span v-if="pointLog.module == 900">答案回复Id：</span>
	    					<span v-if="pointLog.module == 1000">悬赏积分：</span>
	    					<span v-if="pointLog.module == 1100">采纳答案：</span>
	    					<span v-if="pointLog.module == 1200">调整赏金：</span>
	    					{{pointLog.parameterId}}
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">积分：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="pointLog.pointState == 1">+</span>
	    					<span v-if="pointLog.pointState == 2">-</span>
	    					{{pointLog.point}}
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">时间：</div></el-col>
					<el-col :span="20"><div class="content">{{pointLog.times}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">备注：</div></el-col>
					<el-col :span="20"><div class="content">{{pointLog.remark}}</div></el-col>
				</el-row>
				-->
			</div>
		</div>
	</div>
</template>  

<script>
//会员卡赠送任务
export default({
	name: 'membershipCardGiftTaskView',//组件名称，keep-alive缓存需要本参数
	template : '#membershipCardGiftTaskView-template',
	inject:['reload'], 
	data : function data() {
		return {
		    membershipCardGiftTask :'',
		    
		  	membershipCardGiftTaskId :'',
		};
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

		this.queryMembershipCardGiftTask();
		
		
	},
	
	methods : {
		//查询会员卡赠送任务
		queryMembershipCardGiftTask : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/membershipCardGiftTask/manage', {
				 params: {
					method :"view",
					membershipCardGiftTaskId : _self.membershipCardGiftTaskId,
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
			    		_self.membershipCardGiftTask = returnValue.data;
			    		
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
		},
	    
	}
});


</script>