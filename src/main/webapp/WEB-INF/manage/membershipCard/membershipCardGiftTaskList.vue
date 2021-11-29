<!-- 会员卡赠送任务列表 -->
<template id="membershipCardGiftTaskList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/membershipCardGiftTask/manage/add'});">添加会员卡赠送任务</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column type="selection" v-if="false"></el-table-column>
					<el-table-column prop="name" label="名称" align="center"></el-table-column>
					<el-table-column prop="userRoleName" label="角色名称" align="center" ></el-table-column>
					<el-table-column label="任务类型" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.type == 10" class="tag-wrapper tag-color-orange">长期</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type == 20" class="tag-wrapper tag-color-cyan">一次性</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column prop="createDate" label="创建时间" align="center" width="150"></el-table-column>
					<el-table-column label="限制条件" align="center" width="120">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.restrictionGroup.registrationTime_start != null || scope.row.restrictionGroup.registrationTime_end != null" class="tag-wrapper tag-color-purple">用户注册时间范围</el-tag>
				    		<el-tag effect="dark"  v-if="scope.row.restrictionGroup.totalPoint != null" class="tag-wrapper tag-color-yellow">积分达到数量</el-tag>
				    	
				    		<span v-if="scope.row.restrictionGroup.registrationTime_start == null && scope.row.restrictionGroup.registrationTime_end == null && scope.row.restrictionGroup.totalPoint == null">无限制</span>
				    	
				    	</template>
					</el-table-column>
					<el-table-column label="任务有效期范围" align="center" width="300">
						<template #default="scope">
							<span v-if="scope.row.type == 10 &&(scope.row.expirationDate_start != null || scope.row.expirationDate_end != null)">{{scope.row.expirationDate_start}} 至 {{scope.row.expirationDate_end}}</span>
							<span v-if="scope.row.type == 10 &&(scope.row.expirationDate_start == null && scope.row.expirationDate_end == null)">无限制</span>
				    	</template>
					</el-table-column>
					<el-table-column label="赠送时长" align="center" width="120">
						<template #default="scope">
							{{scope.row.duration}}
							<span v-if="scope.row.unit ==10">小时</span>
					  		<span v-if="scope.row.unit ==20">日</span>
					  		<span v-if="scope.row.unit ==30">月</span>
					  		<span v-if="scope.row.unit ==40">年</span>
						</template>
					</el-table-column>
					<el-table-column label="是否启用" align="center" width="80">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.type == 10 && scope.row.enable == true" class="tag-wrapper tag-color-green-2">启用</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type == 10 && scope.row.enable == false" type="danger" class="tag-wrapper tag-color-grey">禁用</el-tag>
				    	</template>
					</el-table-column>
					
					
					<el-table-column label="操作" align="center" width="240">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/membershipCardGiftTask/manage/membershipCardGiftItemList', query:{ membershipCardGiftTaskId : scope.row.id}})">获赠用户</el-button>
									<el-button type="primary" size="mini" v-if="scope.row.type == 20" @click="$router.push({path: '/admin/control/membershipCardGiftTask/manage/view', query:{ membershipCardGiftTaskId : scope.row.id,page:($route.query.page != undefined ? $route.query.page:'')}})">查看</el-button>
									<el-button type="primary" size="mini" v-if="scope.row.type == 10" @click="$router.push({path: '/admin/control/membershipCardGiftTask/manage/edit', query:{ membershipCardGiftTaskId : scope.row.id,page:($route.query.page != undefined ? $route.query.page:'')}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteMembershipCardGiftTask($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
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
//会员卡赠送任务列表
export default({
	name: 'membershipCardGiftTaskList',//组件名称，keep-alive缓存需要本参数
	template : '#membershipCardGiftTaskList-template',
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

		this.queryMembershipCardGiftTaskList();
		
		
	},
	
	methods : {
		//查询会员卡赠送任务列表
		queryMembershipCardGiftTaskList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/membershipCardGiftTask/list', {
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
				path: '/admin/control/membershipCardGiftTask/list', 
				query:{
					page : page
				}
			});
	    },
	  	//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    //删除会员卡赠送任务
	    deleteMembershipCardGiftTask : function(event,row) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
	    	let _self = this;
	    	if (row) {//选中行
	    		this.$refs.multipleTable.toggleRowSelection(row,true);
	    	}
	    	
	    	if(this.multipleSelection.length <1){
	    		this.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	this.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('membershipCardGiftTaskId',row.id);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/membershipCardGiftTask/manage?method=delete',
			        data: formData
				})
				.then(function (response) {
					if(response == null){
						return;
					}
				    let result = response.data;
				    if(result){
				    	
				    	let returnValue = JSON.parse(result);
				    	if(returnValue.code === 200){//成功
				    		_self.$message.success("删除成功");
				    		_self.queryMembershipCardGiftTaskList();
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
	        }).catch((error) => {
	        	//取消选中行
	        	this.$refs.multipleTable.toggleRowSelection(row,false);
	        	
	        	console.log(error);
	        });
	    	
	    }
	}
});


</script>