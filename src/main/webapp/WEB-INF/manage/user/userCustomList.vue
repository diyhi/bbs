<!-- 用户自定义注册功能项列表 -->
<template id="userCustomList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/userCustom/manage/add'});">添加会员注册项</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="name" label="注册项名称" align="center" ></el-table-column>
					<el-table-column prop="name" label="选项框" align="center" >
						<template #default="scope">
							<el-input v-model="input" placeholder="请输入内容" v-if="scope.row.chooseType==1" ></el-input>
							<el-radio v-model="radio" v-if="scope.row.chooseType==2" ></el-radio>
							<el-checkbox v-model="checked" v-if="scope.row.chooseType==3" ></el-checkbox>
							<el-select v-model="value" placeholder="请选择" v-if="scope.row.chooseType==4"></el-select>
							<el-input type="textarea" :rows="1" placeholder="请输入内容" v-model="textarea" v-if="scope.row.chooseType==5"></el-input>
				    	</template>
					</el-table-column>
					<el-table-column prop="tip" label="提示" align="center" ></el-table-column>
					<el-table-column label="注册项类型" align="center" >
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.chooseType==1" type="warning" class="tag-wrapper">输入框</el-tag>
							<el-tag effect="dark"  v-if="scope.row.chooseType==2" type="warning" class="tag-wrapper" >单选按钮</el-tag>
							<el-tag effect="dark"  v-if="scope.row.chooseType==3" type="warning" class="tag-wrapper" >多选按钮</el-tag>
							<el-tag effect="dark"  v-if="scope.row.chooseType==4" type="warning" class="tag-wrapper" >下拉列表</el-tag>
							<el-tag effect="dark"  v-if="scope.row.chooseType==5" type="warning" class="tag-wrapper" >文本域</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column label="是否显示" align="center" width="80">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.visible==true" type="success" class="tag-wrapper">显示</el-tag>
							<el-tag effect="dark"  v-if="scope.row.visible==false" type="danger" class="tag-wrapper" >隐藏</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="50"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/userCustom/manage/edit', query:{ id : scope.row.id}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteUserCustom($event,scope.row)">删除</el-button>
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
//用户自定义注册功能项列表
export default({
	name: 'userCustomList',//组件名称，keep-alive缓存需要本参数
	template : '#userCustomList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			
			
			checked: true,
			
			
			
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		
		//初始化
		this.queryUserCustomList();
	},
	methods : {
		//查询用户自定义注册功能项列表
		queryUserCustomList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/userCustom/list', {
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
			    		let userCustomList = returnValue.data;
			    		if(userCustomList != null && userCustomList.length >0){
			    			_self.tableData = userCustomList;
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//删除用户自定义注册功能项
	    deleteUserCustom : function(event,row) {
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
			        url: 'control/userCustom/manage?method=delete',
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
				    		_self.queryUserCustomList();
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
