<!-- 模板列表 -->
<template id="templateList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/template/manage/add'});">添加模板</el-button>
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/template/manage/importTemplateList'});">模板压缩包列表</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="name" label="模板名称" align="center" ></el-table-column>
					<el-table-column label="模板缩略图" align="center" min-width="120">
						<template #default="scope">
			          		<el-image v-if="scope.row.thumbnailSuffix != null && scope.row.thumbnailSuffix != ''" style="width: 120px; height: 120px" fit="contain" :src="'common/'+scope.row.dirName+'/templates.'+scope.row.thumbnailSuffix+'?time='+new Date().getTime()" :preview-src-list="['common/'+scope.row.dirName+'/templates.'+scope.row.thumbnailSuffix+'?time='+new Date().getTime()]" hide-on-click-modal ></el-image>
				    	</template>
					</el-table-column>
					<el-table-column prop="dirName" label="目录名称" align="center" min-width="100"></el-table-column>
					<el-table-column prop="uses" label="当前使用中" align="center" min-width="100">
						<template #default="scope">	
							<el-switch v-model="uses[scope.$index]" @change="handleTemplateChange(scope.row,scope.$index)"></el-switch>
				    	</template>
					</el-table-column>

					<el-table-column label="操作" align="center" width="480">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/forumCode/list', query:{ dirName : scope.row.dirName}})">版块代码</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/resource/list', query:{ dirName : scope.row.dirName}})">资源</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/layout/list', query:{ dirName : scope.row.dirName}})">布局</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/column/manage/list',query:{ dirName : scope.row.dirName}});">栏目</el-button>
									<el-button type="primary" size="mini" @click="exportTemplate($event,scope.row)">导出</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/template/manage/edit', query:{ dirName : scope.row.dirName}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteTemplate($event,scope.row)">删除</el-button>
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
//模板列表
export default({
	name: 'templateList',//组件名称，keep-alive缓存需要本参数
	template : '#templateList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			uses:[],
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		//初始化
		this.queryTemplateList();
	},
	methods : {

		//处理选择模板
		handleTemplateChange: function(row,index) {
			let _self = this;
			
			if(row.uses && !_self.uses[index]){//如果不选中
				_self.uses[index] = true;
				return;
			}
			
			
			
			let formData = new FormData();
	    	formData.append('dirName', row.dirName);
	    	

			_self.$ajax({
		        method: 'post',
		        url: 'control/template/manage?method=setTemplate',
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
			    		_self.$message.success("设置成功");
			    		_self.queryTemplateList();
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
		//查询模板列表
		queryTemplateList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			_self.uses = [];
			
			_self.$ajax.get('control/template/list', {
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
			    		let templateList = returnValue.data;
			    		if(templateList != null && templateList.length >0){
			    			_self.tableData = templateList;
			    			for(let i=0; i<templateList.length; i++){
			    				_self.uses.push(templateList[i].uses);
			    			}
			    			
			    			
			    			
			    		}
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//删除模板
	    deleteTemplate : function(event,row) {
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
		    	formData.append('dirName', row.dirName);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/template/manage?method=delete&a=a',//a=a参数的作用是仅增加连接符&
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
				    		
				    		_self.queryTemplateList();
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
	    	
	    },
    	//导出模板
    	exportTemplate : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
		
			
		
	    	let _self = this;
	    	this.$confirm('此操作将导出该模板, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('dirName', row.dirName);
		    	
		    	let msg = this.$message({
			        duration: 0,
			        type: 'warning',
			        message: '导出中，请稍候....'
				});
				
				this.$ajax({
			        method: 'post',
			        url: 'control/template/manage?method=export',
			        data: formData
				})
				.then(function (response) {
					if(response == null){
						return;
					}
					msg.close();//关闭
					
				    let result = response.data;
				    if(result){
				    	let returnValue = JSON.parse(result);
				    	if(returnValue.code === 200){//成功
				    		_self.$message.success("导出成功，请到“模板压缩包列表查看”");
				    		
				    		_self.queryTemplateList();
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