<!-- 数据库备份/还原列表 -->
<template id="dataBaseList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/dataBase/manage/backup'});">数据备份</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="fileName" label="文件名称" align="center" ></el-table-column>
					<el-table-column prop="fileSize" label="文件大小" align="center" width="200"></el-table-column>
					<el-table-column prop="version" label="数据库版本" align="center" width="200"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini"  @click="$router.push({path: '/admin/control/dataBase/manage/reset', query:{ dateName : scope.row.fileName}});">还原</el-button>
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
//数据库备份/还原列表
export default({
	name: 'dataBaseList',//组件名称，keep-alive缓存需要本参数
	template : '#dataBaseList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
		  
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		//初始化
		this.queryDataBaseList();
	},
	methods : {
		
		//查询数据库备份/还原列表
		queryDataBaseList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			_self.enable = [];
			
			
			_self.$ajax.get('control/dataBase/list', {
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
			    		_self.tableData = returnValue.data;
			    		
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
	  	
	}
});
</script>
