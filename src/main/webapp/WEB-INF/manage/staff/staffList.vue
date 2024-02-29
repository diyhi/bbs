<!-- 员工列表 -->
<template id="staffList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/staff/manage/addStaff'});">添加员工</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column type="selection" v-if="false"></el-table-column>
					<el-table-column prop="userAccount" label="用户名" align="center"></el-table-column>
					<el-table-column prop="nickname" label="呢称" align="center"></el-table-column>
					<el-table-column prop="fullName" label="姓名" align="center"></el-table-column>
					<el-table-column label="头像" align="center" min-width="60">
						<template #default="scope">
				        	
			          		<div class="user-avatar-wrapper" >
								<div class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
									<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
								</div>
								<div class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
									<el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
								</div>
							</div>
				        	
				    	</template>
					</el-table-column>
					<el-table-column label="角色" align="center" width="200">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.issys==true" type="danger" class="tag-wrapper" >超级管理员</el-tag>
							<span v-if="scope.row.issys==false && rolesNameMap != ''" v-for="(value, key) in rolesNameMap">
								<span v-if="key ==scope.row.userAccount" v-for="(rolesName_value, rolesName_key) in value">
									<el-tag effect="dark" type="info" class="tag-wrapper tag-spacing" >{{rolesName_value}}</el-tag>
								</span>
							</span>
				    	</template>
					</el-table-column>
					<el-table-column label="状态" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.enabled == true" type="success" class="tag-wrapper">启用</el-tag>
							<el-tag effect="dark"  v-if="scope.row.enabled == false" type="warning" class="tag-wrapper">停用</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="300">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/staffLoginLog/list', query:{ userId : scope.row.userId,staffPage:($route.query.page != undefined ? $route.query.page:'')}})">登录日志</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/staff/manage/editStaff', query:{ userId : scope.row.userId,page:($route.query.page != undefined ? $route.query.page:'')}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteStaff($event,scope.row)">删除</el-button>
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
//员工列表
export default({
	name: 'staffList',//组件名称，keep-alive缓存需要本参数
	template : '#staffList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		    
		    rolesNameMap:'',
		    
		  
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

		this.queryStaffList();
		
		
	},
	
	methods : {
		//查询员工列表
		queryStaffList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/staff/list', {
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
			    		let mapData = returnValue.data;
			    		for(let key in mapData){
			    			if(key == "rolesNameMap"){
			    				_self.rolesNameMap = mapData[key];
			    				
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
				path: '/admin/control/staff/list', 
				query:{
					page : page
				}
			});
	    },
	  	//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    //删除员工
	    deleteStaff : function(event,row) {
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
		    	
	        	formData.append('userId', row.userId);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/staff/manage?method=deleteStaff',
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
				    		_self.queryStaffList();
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