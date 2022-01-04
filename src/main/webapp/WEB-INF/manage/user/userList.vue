<!-- 用户列表 -->
<template id="userList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" v-if="visible == 'false'" @click="$router.push({path: '/admin/control/user/list', query:{ visible : true}})">返回</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'true'" @click="$router.push({path: '/admin/control/user/manage/add', query:{ page:($route.query.page != undefined ? $route.query.page:'')}});">添加会员</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'true'" @click="$router.push({path: '/admin/control/user/list', query:{ visible : false}})">回收站</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'false'" @click="reductionUser($event)">还原</el-button>
				<el-button type="primary" plain size="small"  @click="deleteUser($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column label="账号" align="center">
						<template #default="scope">
				        	
			          		<div class="multipleInfo-wrapper" >
								<div>{{scope.row.account}}</div>
								<div class="multipleInfo-wrapper-blue" v-if="scope.row.type ==20">
									手机号{{scope.row.mobile}}
								</div>
								<div class="multipleInfo-wrapper-green" v-if="scope.row.type !=20 && scope.row.mobile !=null && scope.row.mobile !='' ">
									绑定手机{{scope.row.mobile}}
								</div>
								<el-tag effect="dark"  v-if="scope.row.cancelAccountTime !='-1'" type="danger" class="tag-wrapper">已注销</el-tag>
							</div>
				        	
				    	</template>
					</el-table-column>
					<el-table-column prop="nickname" label="呢称" align="center" ></el-table-column>
					<el-table-column label="头像" align="center" min-width="100">
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
					<el-table-column label="类型" align="center" width="110">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.type==10" type="warning" class="tag-wrapper">账号密码用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==20" class="tag-wrapper" >手机用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==30" type="info" class="tag-wrapper" >邮箱用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==40" type="success" class="tag-wrapper" >微信用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==80" class="tag-wrapper-purple" >其他用户</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column prop="point" label="积分" align="center" ></el-table-column>
					<el-table-column prop="gradeName" label="会员等级" align="center" width="100"></el-table-column>
					<el-table-column prop="registrationDate" label="注册日期" align="center" width="150"></el-table-column>
					<el-table-column label="状态" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.state == 1" type="success" class="tag-wrapper">启用</el-tag>
							<el-tag effect="dark"  v-if="scope.row.state == 2" type="info" class="tag-wrapper">停用</el-tag>
				    		<el-tag effect="dark"  v-if="scope.row.state ==11" type="warning" class="tag-wrapper">启用时删除</el-tag>
							<el-tag effect="dark"  v-if="scope.row.state ==12" type="danger" class="tag-wrapper">停用时删除</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/user/manage/show', query:{ id : scope.row.id}})">查看</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/user/manage/edit', query:{ id : scope.row.id}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteUser($event,scope.row)">删除</el-button>
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
//用户列表
export default({
	name: 'userList',//组件名称，keep-alive缓存需要本参数
	template : '#userList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
			
			visible :'true',//是否显示 true:未删除用户 false:已删除用户
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

		if(this.$route.query.visible != undefined && this.$route.query.visible != ''){
			this.visible = this.$route.query.visible;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		//初始化
		this.queryUserList();
	},
	methods : {
		//查询用户列表
		queryUserList : function() {
			let _self = this;
			
			
			//清空内容
			_self.tableData = []; 
			
			
			_self.$ajax.get('control/user/list', {
			    params: {
			    	visible :_self.visible,
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
			    		let topicList = pageView.records;
			    		if(topicList != null && topicList.length >0){
			    			_self.tableData = topicList;
			 
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
				path: '/admin/control/user/list', 
				query:{visible : this.visible,page : page}
			});
	    },
		//删除用户
	    deleteUser : function(event,row) {
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
	        	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('userId', rowData.id);
		    	}
		    	formData.append('visible', _self.visible);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/user/manage?method=delete',
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
				    		//删除缓存
				  			_self.$store.commit('setCacheNumber');
				    		_self.queryUserList();
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
	    },
	  	//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	  	//还原用户
	    reductionUser : function(event) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
	    	target.blur();
	    	
	    	let _self = this;
	    	
	    	if(this.multipleSelection.length <1){
	    		this.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	this.$confirm('此操作将还原该用户, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('userId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/user/manage?method=reduction',
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
				    		_self.$message.success("还原成功");
				    		_self.queryUserList();
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
	        	
	        	console.log(error);
	        });
	    }
	}
});
</script>
