<!-- 数据库还原 -->
<template id="dataReset-template">
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
								<div class="leftCell" v-if="resetProgress != ''">
									还原进度
								</div>
								<div style="color: #67C23A;">
									{{resetProgress}}
								</div>
							</div>
						</el-form-item>
						<el-form-item>
						    <el-button type="primary" class="submitButton" @click="submitReset($event)" :disabled="submitForm_disabled">立即还原</el-button>
							<div class="form-help" style="color: #F56C6C;">还原过程不能中断</div>
						</el-form-item>
					</el-form>
				</div>
			</div>
			
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark"style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="key" label="表文件名称" align="center" ></el-table-column>
					<el-table-column prop="value" label="文件大小" align="center" width="250"></el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>

<script>
//数据库还原
export default({
	name: 'dataReset',//组件名称，keep-alive缓存需要本参数
	template : '#dataReset-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			
			dateName:'',
			resetProgress:'',
			error:{
				
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		if(this.$route.query.dateName != undefined && this.$route.query.dateName != ''){
			this.dateName = this.$route.query.dateName;
		}
		//初始化
		this.queryDataReset();
	},
	methods : {
		
		//查询数据库还原
		queryDataReset : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/dataBase/manage', {
			    params: {
			    	method :  'reset',
			    	dateName :  _self.dateName
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
			    			if(key == "fileMap"){
			    				let fileMap = mapData[key];
			    				let fileList = [];
	    						for(let key in fileMap){//Map转为List
	    							let obj =new Object();
					    	    	obj.key = key;
					    	    	obj.value = fileMap[key];
					    	    	fileList.push(obj);
	    						}
	    						_self.tableData = fileList;
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
		
		//查询还原进度
		queryResetProgress : function() {
			let _self = this;
			
			_self.$ajax.get('control/dataBase/manage', {
			    params: {
			    	method :  'queryResetProgress'
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
			    		_self.resetProgress = returnValue.data;
			    		
			    	
			    		setTimeout(function(){
							if(_self.resetProgress != "还原完成"){
								_self.queryResetProgress();
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
		
		//还原数据库
	    submitReset : function(event) {
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
	    	_self.$confirm('此操作将还原数据库, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
	        	formData.append('dateName', _self.dateName);
	        	
	        	_self.$ajax({
			        method: 'post',
			        url: 'control/dataBase/manage?method=reset&a=a',//a=a参数的作用是仅增加连接符&
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
				    		_self.$message.success("还原任务开始执行");
				    		_self.queryResetProgress();
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
