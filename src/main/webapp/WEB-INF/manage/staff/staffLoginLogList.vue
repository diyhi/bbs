<!-- 登录日志列表 -->
<template id="staffLoginLogList-template">
	<div>
		<div class="main staffLoginLogModule">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/staff/list', query:{ page:($route.query.staffPage != undefined ? $route.query.staffPage:'')}})">返回</el-button>
			</div>
			<div class="headInfo">
				<div class="title">{{currentStaff.userAccount}}</div>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="ip" label="登录IP" align="center" width="280"></el-table-column>
					<el-table-column prop="ipAddress" label="IP归属地" align="center" ></el-table-column>
					<el-table-column prop="logonTime" label="登录时间" align="center" width="200"></el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>  

<script>
//登录日志列表
export default({
	name: 'staffLoginLogList',//组件名称，keep-alive缓存需要本参数
	template : '#staffLoginLogList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    currentStaff :'',//当前员工
		    
		  	userId :'',
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

		
		if(this.$route.query.userId != undefined && this.$route.query.userId != ''){
			this.userId = this.$route.query.userId;
			
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryStaffLoginLogList();
		
		
	},
	
	methods : {
		//查询登录日志列表
		queryStaffLoginLogList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/staffLoginLog/list', {
				 params: {
					userId :_self.userId,
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
			    			if(key == "currentStaff"){
			    				_self.currentStaff = mapData[key];
			    				
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
		//分页
		page: function(page) {
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/staffLoginLog/list', 
				query:{ 
					userId :this.userId,
					staffPage:(this.$route.query.staffPage != undefined ? this.$route.query.staffPage:''),
					page : page
				}
			});
	    },
	    
	}
});


</script>