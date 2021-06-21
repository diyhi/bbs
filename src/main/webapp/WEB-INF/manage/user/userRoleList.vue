<!-- 会员角色列表 -->
<template id="userRoleList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/userRole/manage/add'});">添加会员角色</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="name" label="角色名称" align="center" min-width="200"></el-table-column>
					<el-table-column prop="sort" label="排序" align="center" min-width="200">
						<template #default="scope">	
							<span v-if="!scope.row.defaultRole" >{{scope.row.sort}}</span>
				    	</template>
					</el-table-column>
					<el-table-column prop="defaultRole" label="是否默认角色" align="center"  min-width="200">
						<template #default="scope">	
							<el-switch v-model="scope.row.defaultRole" @change="handleDefaultRoleChange(scope.row.id,scope.row.defaultRole)"></el-switch>
				    	</template>
					</el-table-column>

					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/userRole/manage/edit', query:{ userRoleId : scope.row.id}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteUserRole($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
				    	</template>
					
					</el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>

<script>
//会员角色列表
export default({
	name: 'userRoleList',//组件名称，keep-alive缓存需要本参数
	template : '#userRoleList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		//初始化
		this.queryUserRoleList();
	},
	methods : {
		//处理选择默认角色
		handleDefaultRoleChange: function(id,defaultRole) {
			let _self = this;
			
			let formData = new FormData();
	    	formData.append('id', id);
	    	formData.append('defaultRole',defaultRole);

			_self.$ajax({
		        method: 'post',
		        url: 'control/userRole/manage?method=setAsDefault',
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
			    		_self.$message.success("设置成功，默认角色自动排到最前面");
			    		_self.queryUserRoleList();
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
	
		//查询会员角色列表
		queryUserRoleList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			_self.$ajax.get('control/userRole/list', {
			    params: {
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
			    		let userRoleList = returnValue.data;
			    		if(userRoleList != null && userRoleList.length >0){
			    			_self.tableData = userRoleList;
			    		}
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//删除会员角色
	    deleteUserRole : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	this.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('id', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/userRole/manage?method=delete',
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
				    		_self.queryUserRoleList();
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