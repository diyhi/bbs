<!-- 发红包金额分配列表 -->
<template id="redEnvelopeAmountDistributionList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" v-if="$route.query.id != undefined && $route.query.id !=''" @click="$router.push({path: '/admin/control/redEnvelope/giveRedEnvelope/list', query:{ page:$route.query.giveRedEnvelopePage, id : $route.query.id,userName : encodeURIComponent(userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">返回</el-button>					
				<el-button type="primary" plain size="small" v-if="$route.query.topicId != undefined && $route.query.topicId !=''" @click="$router.push({path: '/admin/control/topic/manage/view', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :$route.query.topicId, commentId:($route.query.commentId != undefined ? $route.query.commentId:''), page:($route.query.topicPage != undefined ? $route.query.topicPage:'')}})">返回</el-button>
			</div>
			<div class="nav-user clearfix" v-if="$route.query.id != undefined && $route.query.id !=''">
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
               			 <i class="tag">发红包分配金额</i>
               		</div>
               	</div>
			</div>
			<div class="data-view" >
				<el-row :gutter="10" type="flex" v-if="$route.query.topicId != undefined && $route.query.topicId !=''">
					<el-col :span="4"><div class="name">话题：</div></el-col>
					<el-col :span="20">
						<div class="content">
							{{currentTopic.title}}
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">类型：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="giveRedEnvelope.type == 10">个人定向红包</span>
							<span v-if="giveRedEnvelope.type == 20">公共随机红包</span>
							<span v-if="giveRedEnvelope.type == 30">公共定额红包</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">总金额：</div></el-col>
					<el-col :span="20">
						<div class="content">
						{{giveRedEnvelope.totalAmount}}
							<span v-if="giveRedEnvelope.refundAmount >0" style="color: red;">中止领取红包后返还金额￥{{giveRedEnvelope.refundAmount}}</span>
						
					
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">发放数量：</div></el-col>
					<el-col :span="20"><div class="content">{{giveRedEnvelope.giveQuantity}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">已领取数量：</div></el-col>
					<el-col :span="20"><div class="content">{{giveRedEnvelope.giveQuantity - giveRedEnvelope.remainingQuantity}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">时间：</div></el-col>
					<el-col :span="20"><div class="content">{{giveRedEnvelope.giveTime}}</div></el-col>
				</el-row>
			</div>
			<div class="data-table" >
				
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="收红包用户" align="center" >
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
						            <p v-if="scope.row.receiveNickname != null && scope.row.receiveNickname != ''">呢称: {{scope.row.receiveNickname}}</p>
						            <p v-if="scope.row.receiveUserName != null && scope.row.receiveUserName != ''">账号: {{scope.row.receiveAccount}}</p>
					        	</template>
					        	<template #reference>
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.receiveAvatarName == null || scope.row.receiveAvatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.receiveAvatarName != null && scope.row.receiveAvatarName != ''">
											<el-avatar :size="48" :src="scope.row.receiveAvatarPath+'100x100/'+scope.row.receiveAvatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.receiveAccount}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column prop="amount" label="金额" align="center" width="150"></el-table-column>
					<el-table-column label="收取时间" align="center" width="200">
						<template #default="scope">
							{{scope.row.receiveTime}}
							<span v-if="scope.row.receiveUserId == null">本红包未被领取</span>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>  

<script>
//发红包金额分配列表
export default({
	name: 'redEnvelopeAmountDistributionList',//组件名称，keep-alive缓存需要本参数
	template : '#redEnvelopeAmountDistributionList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    currentUser :'',//当前用户
		    currentTopic :'',//当前话题
		    giveRedEnvelope :'',//发红包

		    giveRedEnvelopeId :'',
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

		if(this.$route.query.giveRedEnvelopeId != undefined && this.$route.query.giveRedEnvelopeId != ''){
			this.giveRedEnvelopeId = this.$route.query.giveRedEnvelopeId;
			
		}
		if(this.$route.query.id != undefined && this.$route.query.id != ''){
			this.id = this.$route.query.id;
			
		}
		if(this.$route.query.userName != undefined && this.$route.query.userName != ''){
			this.userName = decodeURIComponent(this.$route.query.userName);
			
		}
		if(this.$route.query.topicId != undefined && this.$route.query.topicId != ''){
			this.topicId = this.$route.query.topicId;
			
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryRedEnvelopeAmountDistributionList();
		
		
	},
	
	methods : {
		//查询发红包金额分配列表
		queryRedEnvelopeAmountDistributionList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/redEnvelope/redEnvelopeAmountDistribution/list', {
				 params: {
					giveRedEnvelopeId :_self.giveRedEnvelopeId,
					id :_self.id,
					userName :_self.userName,
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
			    			if(key == "currentUser"){
			    				_self.currentUser = mapData[key];
			    				
			    			}if(key == "currentTopic"){
			    				_self.currentTopic = mapData[key];
			    				
			    			}if(key == "giveRedEnvelope"){
			    				_self.giveRedEnvelope = mapData[key];
			    				
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
	    
	}
});


</script>