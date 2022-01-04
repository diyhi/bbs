<!-- 积分日志详细 -->
<template id="pointLogShow-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/pointLog/list', query:{ page:$route.query.pointLogPage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">返回</el-button>					
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
               			 <i class="tag">积分日志详细</i>
               		</div>
               	</div>
			</div>
			<div class="data-view" >
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
					<el-col :span="4"><div class="name">操作用户账号：</div></el-col>
					<el-col :span="20">
						<div class="content">
							{{pointLog.operationAccount}}
							<el-tag effect="dark"  v-if="pointLog.operationUserType==0" class="tag-wrapper">系统</el-tag>
							<el-tag effect="dark"  v-if="pointLog.operationUserType==1" type="success" class="tag-wrapper" >员工</el-tag>
							<el-tag effect="dark"  v-if="pointLog.operationUserType==2" type="info" class="tag-wrapper" >会员</el-tag>
					
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
			</div>
		</div>
	</div>
</template>  

<script>
//积分日志详细
export default({
	name: 'pointLogShow',//组件名称，keep-alive缓存需要本参数
	template : '#pointLogShow-template',
	inject:['reload'], 
	data : function data() {
		return {
		    currentUser :'',//当前用户
		    pointLogId :'',
		    pointLog :'',
		    
		  	id :'',
			userName :'',//用户名称
		};
	},

	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		if(this.$route.query.pointLogId != undefined && this.$route.query.pointLogId != ''){
			this.pointLogId = this.$route.query.pointLogId;
			
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

		this.queryPointLogShow();
		
		
	},
	
	methods : {
		//查询积分日志详细
		queryPointLogShow : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/pointLog/manage', {
				 params: {
					method :"show",
					pointLogId : _self.pointLogId,
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
			    				
			    			}else if(key == "pointLog"){
			    				_self.pointLog = mapData[key];
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