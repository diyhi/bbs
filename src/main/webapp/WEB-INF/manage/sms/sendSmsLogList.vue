<!-- 短信接口列表 -->
<template id="sendSmsLogList-template">
	<div>
		<div class="main">
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column label="接口产品" align="center" width="150">
						<template #default="scope">
							<span v-if="scope.row.interfaceProduct == 1">阿里云短信</span>
						</template>
					</el-table-column>
					<el-table-column label="发送服务" align="center" width="150">
						<template #default="scope">
							<span v-if="scope.row.serviceId == 1">绑定手机</span>
						</template>
					</el-table-column>
					<el-table-column prop="createDate" label="发送时间" align="center" width="170"></el-table-column>
					<el-table-column prop="platformUserId" label="平台用户Id" align="center" min-width="170"></el-table-column>
					<el-table-column prop="mobile" label="手机" align="center" width="100"></el-table-column>
					<el-table-column prop="code" label="状态码" align="center" width="200"></el-table-column>
					<el-table-column prop="message" label="状态码描述" align="center"></el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
//短信接口列表
export default({
	name: 'sendSmsLogList',//组件名称，keep-alive缓存需要本参数
	template : '#sendSmsLogList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
		  
			enable:[],
			
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
		
		//初始化
		this.querySendSmsLogList();
	},
	methods : {
		
		//查询短信接口列表
		querySendSmsLogList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			_self.enable = [];
			
			
			_self.$ajax.get('control/sendSmsLog/list', {
			    params: {
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
			    			for(let i=0; i<list.length; i++){
			    				_self.enable.push(list[i].enable);
			    			}
			    			
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
				path: '/admin/control/sendSmsLog/list', 
				query:{ page : page}
			});
	    },
		
	  	
	}
});
</script>
