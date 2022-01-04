<!-- 解锁话题隐藏内容平台分成列表 -->
<template id="topicUnhidePlatformShareList-template">
	<div>
		<div class="main">
			<div class="search">
				<el-form :inline="true" label-width="auto" @keyup.enter.native="submitForm">
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.start_times" >
							<el-date-picker v-model="start_times_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="起始日期" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.end_times" >
							<el-date-picker v-model="end_times_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="结束日期"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" >筛选</el-button>
					    <el-button type="primary" class="submitButton" @click="nowSubmit" >今天</el-button>
					    <el-button type="primary" class="submitButton" @click="yesterdaySubmit" >昨天</el-button>
					</el-form-item>
					
				</el-form>
			</div> 
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column label="话题标题" align="center" >
						<template #default="scope">
							<el-link href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.topicId,commentId:''}})">{{scope.row.topicTitle}}</el-link>
						</template>
					</el-table-column>
					<el-table-column label="发布话题的用户" align="center" min-width="150">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<p v-if="scope.row.staff == false">呢称: {{scope.row.postNickname}}</p>
						            <p>账号: {{scope.row.postAccount}}</p>
					        	</template>
					        	<template #reference v-if="scope.row.staff == false">
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.postAvatarName == null || scope.row.postAvatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.postAvatarName != null && scope.row.postAvatarName != ''">
											<el-avatar :size="48" :src="scope.row.postAvatarPath+'100x100/'+scope.row.postAvatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.postAccount}}</div>
									</div>
					        	</template>
					        	
					        	<template #reference v-if="scope.row.staff == true">
					        		<div class="avatar-wrapper">
										<el-badge value="员工" type="warning" class="avatar-badge">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</el-badge>
										<div class="avatar-text">{{scope.row.postAccount}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column label="解锁话题的用户" align="center" min-width="150">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<p v-if="scope.row.staff == false">呢称: {{scope.row.unlockNickname}}</p>
						            <p>账号: {{scope.row.unlockAccount}}</p>
					        	</template>
					        	<template #reference v-if="scope.row.staff == false">
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.unlockAvatarName == null || scope.row.unlockAvatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.unlockAvatarName != null && scope.row.unlockAvatarName != ''">
											<el-avatar :size="48" :src="scope.row.unlockAvatarPath+'100x100/'+scope.row.unlockAvatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.unlockAccount}}</div>
									</div>
					        	</template>
					        	
					        	<template #reference v-if="scope.row.staff == true">
					        		<div class="avatar-wrapper">
										<el-badge value="员工" type="warning" class="avatar-badge">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</el-badge>
										<div class="avatar-text">{{scope.row.unlockAccount}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column prop="postUserShareRunningNumber" label="发布话题的用户分成流水号" align="center" width="260"></el-table-column>
					<el-table-column label="平台分成比例" align="center"  width="120">
						<template #default="scope">
							{{scope.row.platformShareProportion}}%
						</template>
					</el-table-column>
					<el-table-column prop="totalAmount" label="总金额" align="center" width="100"></el-table-column>
					<el-table-column prop="shareAmount" label="平台分成金额" align="center" width="120"></el-table-column>
					<el-table-column prop="unlockTime" label="解锁时间" align="center" width="170"></el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
//解锁话题隐藏内容平台分成列表
export default({
	name: 'topicUnhidePlatformShareList',//组件名称，keep-alive缓存需要本参数
	template : '#topicUnhidePlatformShareList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
			
			start_times :'',//日期 起始
			end_times :'',//日期 结束
			
			start_times_format :'',//日期 起始 格式化为适合el-date-picker标签的值
			end_times_format :'',//日期 结束  格式化为适合el-date-picker标签的值

			error :{
				start_times :'',
				end_times :'',
			},
		  
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
		
		if(this.$route.query.start_times != undefined && this.$route.query.start_times != ''){
			this.start_times_format = decodeURIComponent(this.$route.query.start_times);
			this.start_times = this.start_times_format;
		}
		if(this.$route.query.end_times != undefined && this.$route.query.end_times != ''){
			this.end_times_format = decodeURIComponent(this.$route.query.end_times);
			this.end_times = this.end_times_format;
		}
		
		//初始化
		this.queryTopicUnhidePlatformShareList();
	},
	methods : {
		//查询解锁话题隐藏内容平台分成列表
		queryTopicUnhidePlatformShareList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/topicUnhidePlatformShare/list', {
			    params: {
			    	start_times :_self.start_times,
					end_times :_self.end_times,
			    	page :  _self.currentpage
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
				path: '/admin/control/topicUnhidePlatformShare/list', 
				query:{
					start_times :encodeURIComponent(this.start_times),
					end_times :encodeURIComponent(this.end_times),
					page : page
				}
			});
	    },
	  	//提交表单
  		submitForm : function() {
  			let _self = this;
  			
  			//删除缓存
  			_self.$store.commit('setCacheNumber');

  			if(_self.start_times_format != null && _self.start_times_format != ''){
  				_self.start_times = dayjs(_self.start_times_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.start_times = '';
  			}
  			if(_self.end_times_format != null && _self.end_times_format != ''){
  				_self.end_times = dayjs(_self.end_times_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.end_times = '';
  			}
  			
  			
  			_self.$router.push({
  				path: '/admin/control/topicUnhidePlatformShare/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
					time: new Date().getTime()
  				}
  			});
  	    },
  		//今天筛选提交
  		nowSubmit : function() {
  			let _self = this;
  			//删除缓存
  			_self.$store.commit('setCacheNumber');
  			
  			
  			_self.start_times =  dayjs(_self.getDay(0)+" 00:00").format('YYYY-MM-DD HH:mm');
  			
  			_self.end_times =  dayjs(_self.getDay(0)+" 23:59").format('YYYY-MM-DD HH:mm');
  			
  			
  			
  			_self.$router.push({
  				path: '/admin/control/topicUnhidePlatformShare/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
					time: new Date().getTime()
  				}
  			});
  		},
  		//昨天筛选提交
  		yesterdaySubmit : function() {
  			let _self = this;
  			//删除缓存
  			_self.$store.commit('setCacheNumber');
  			
  			
  			_self.start_times =  dayjs(_self.getDay(-1)+" 00:00").format('YYYY-MM-DD HH:mm');
  			
  			_self.end_times =  dayjs(_self.getDay(-1)+" 23:59").format('YYYY-MM-DD HH:mm');
  			
  			
  			
  			_self.$router.push({
  				path: '/admin/control/topicUnhidePlatformShare/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
					time: new Date().getTime()
  				}
  			});
  		},
  		
  		//计算今天的前后日期  参数day: 天 如2为后天 0为今天 -1为昨天
  		getDay : function(day) {
  			let today = new Date();  
 		         
  			let targetday_milliseconds=today.getTime() + 1000*60*60*24*day;          
 		  
 		    today.setTime(targetday_milliseconds); //注意，这行是关键代码    
 		         
 			let tYear = today.getFullYear();  
 			let tMonth = today.getMonth();  
 			let tDate = today.getDate();  
 		    tMonth = this.doHandleMonth(tMonth + 1);  
 		    tDate = this.doHandleMonth(tDate);  
 		    return tYear+"-"+tMonth+"-"+tDate;  
  		},
  		//补零
  		doHandleMonth : function(month) {
  			let m = month;  
  		    if(month.toString().length == 1){  
  		    	m = "0" + month;  
  		    }  
  		    return m;  
  		},
	}
});
</script>
