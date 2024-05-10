<!-- 标签列表 -->
<template id="tagList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/tag/manage/add'});">添加标签</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="name" label="标签名称" align="center" min-width="200"></el-table-column>
					<el-table-column label="图片" align="center" width="200">
						<template #default="scope">
			          		<el-image v-if="scope.row.image != null && scope.row.image != ''" style="width: 120px; height: 120px" fit="contain" :src="scope.row.image+'?time='+new Date().getTime()" :preview-src-list="[scope.row.image+'?time='+new Date().getTime()]" hide-on-click-modal ></el-image>
				    	</template>
					</el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="80"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/tag/manage/edit', query:{ tagId : scope.row.id}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteTag($event,scope.row)">删除</el-button>
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
//标签列表
export default({
	name: 'tagList',//组件名称，keep-alive缓存需要本参数
	template : '#tagList-template',
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
		this.queryTagList();
	},
	methods : {
		//查询标签列表
		queryTagList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/tag/list', {
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
			    		let tagList = returnValue.data;
			    		if(tagList != null && tagList.length >0){
			    			_self.tableData = tagList;
			    		}
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//删除标签
	    deleteTag : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	this.$confirm('此操作将删除该标签, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('tagId', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/tag/manage?method=delete',
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
				    		_self.queryTagList();
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