<!-- 数据库备份 -->
<template id="dataBackup-template">
	<div>
		<div class="main dataBackupModule">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/dataBase/list'});">返回</el-button>
			</div>
			<div class="headInfo">
				<div class="container">
					<el-form label-width="auto"  @submit.native.prevent>
						<el-form-item>
						    <div class="singleRowTable">
								<div class="leftCell" v-if="backupProgress != ''">
									备份进度
								</div>
								<div style="color: #67C23A;">
									{{backupProgress}}
								</div>
							</div>
						</el-form-item>
						<el-form-item>
						    <el-button type="primary" class="submitButton" @click="submitBackup($event)" :disabled="submitForm_disabled">立即备份</el-button>
							<div class="form-help" style="color: #F56C6C;">备份过程不能中断</div>
						</el-form-item>
					</el-form>
				</div>
			</div>
			
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" show-summary :summary-method="getSummaries" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="name" label="表名称" align="center" ></el-table-column>
					<el-table-column prop="rows" label="记录条数" align="center" width="250"></el-table-column>
					<el-table-column prop="indexSize" label="索引大小" align="center" width="250"></el-table-column>
					<el-table-column prop="dataSize" label="数据大小" align="center" width="250"></el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>

<script>
//数据库备份
export default({
	name: 'dataBackup',//组件名称，keep-alive缓存需要本参数
	template : '#dataBackup-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
			countIndexSize:0,
			countDataSize:0,
			countRow:0,
			
			backupProgress:'',
			error:{
				
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		//初始化
		this.queryDataBackup();
	},
	methods : {
		
		//查询数据库备份
		queryDataBackup : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/dataBase/manage', {
			    params: {
			    	method :  'backup'
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
			    			if(key == "showTable"){
			    				_self.tableData = mapData[key];
			    			}else if(key == "countIndexSize"){
			    				_self.countIndexSize = mapData[key];
			    			}else if(key == "countDataSize"){
			    				_self.countDataSize = mapData[key];
			    			}else if(key == "countRow"){
			    				_self.countRow = mapData[key];
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
		//合并总数
		getSummaries(param) {
			const { columns, data } = param;
	        const sums = [];
	        columns.forEach((column, index) => {
		        if (index === 0) {
		            sums[index] = '总大小';
		            return;
		        }
		        if (index === 1) {
		            sums[index] = this.countRow;
		            return;
		        }
		        if (index === 2) {
		            sums[index] = this.countIndexSize;
		            return;
		        }
		        if (index === 3) {
		            sums[index] = this.countDataSize;
		            return;
		        }
	        });

	        return sums;
		},
		
		//查询备份进度
		queryBackupProgress : function() {
			let _self = this;
			
			_self.$ajax.get('control/dataBase/manage', {
			    params: {
			    	method :  'queryBackupProgress'
			    },
			    headers: {
			      	'showLoading': false,//是否显示图标
			      	'loadingMask':false// 是否显示遮罩层
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
			    		_self.backupProgress = returnValue.data;
			    		
			    	
			    		setTimeout(function(){
							if(_self.backupProgress != "备份完成"){
								_self.queryBackupProgress();
							}else{
								_self.submitForm_disabled = false;
							}
							
					    }, 3000);//3秒钟刷新 
			    	
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//备份数据库
	    submitBackup : function(event) {
	    	this.submitForm_disabled = true;
	    	
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	_self.$confirm('此操作将备份数据库, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
	        	_self.$ajax({
			        method: 'post',
			        url: 'control/dataBase/manage?method=backup',
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
				    		_self.$message.success("备份任务开始执行");
				    		_self.queryBackupProgress();
				    	}else if(returnValue.code === 500){//错误
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
				    			if(_self.error[key] == undefined){
				    				_self.$message({
										duration :0,
							            showClose: true,
							            message: errorMap[key],
							            type: 'error'
							        });
				    			}else{
				    				_self.error[key] = errorMap[key];
				    			}
				    	    }
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        	
	        }).catch((error) => {
	        	console.log(error);
	        	_self.submitForm_disabled = false;
	        });
	    },
	  	
	}
});
</script>
