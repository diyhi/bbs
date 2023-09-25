<!-- 敏感词 -->
<template id="viewFilterWord-template">
	<div>
		<div class="main" >
			<div class="data-view label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent >
					<div v-if="filterWord != null">
						<el-form-item label="过滤词数量">
							{{filterWord.wordNumber}}
						</el-form-item>
						<el-form-item label="词库大小">
							{{filterWord.size}}
						</el-form-item>
						<el-form-item label="词库最后修改时间">
							{{filterWord.lastModified}}
						</el-form-item>
						<el-form-item label="前3个词">
							<span v-for="word in filterWord.beforeWordList" >	
								<span class="blue-tag" >{{word}}</span>
							</span>
						</el-form-item>
					</div>
					
					<el-form-item>
						<span style="color: #F56C6C;">注意：词库必须为UTF-8格式。  词库必须到 ‘系统设置’ -- ‘基本设置’ 里打开 ‘敏感词过滤’ 开关才能生效</span>
					</el-form-item>
					
					<el-form-item>
						<el-button type="primary" class="submitButton" plain @click="uploadFilterWordUI($event)" >上传词库</el-button>
					    <el-button type="primary" class="submitButton" @click="deleteFilterWord($event)" :disabled="submitForm_disabled">删除</el-button>
					</el-form-item>
				</el-form>
				<el-dialog title="上传词库" v-model="uploadForm">
					<el-form label-width="auto"  @submit.native.prevent>
						<el-form-item :error="error.file">
							<el-upload class="filterWordModule" drag :http-request="uploadFilterWord" :show-file-list="false" :accept="'.txt'">
								<i class="el-icon-upload"></i>
								<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
								<template #tip>
								    <div class="el-upload__tip">
								    	只允许上传 txt 文件
								    </div>
								</template>
							</el-upload>
						</el-form-item>
						<el-form-item>
							<el-progress :text-inside="true" :stroke-width="20" :percentage="progressPercent" />
				        </el-form-item>
	  				</el-form>
				</el-dialog>
				
			</div>
		</div>
	</div>
</template>

<script>
//敏感词
export default({
	name: 'viewFilterWord',//组件名称，keep-alive缓存需要本参数
	template : '#viewFilterWord-template',
	inject:['reload'], 
	data : function data() {
		return {
			filterWord :'',
			
			uploadForm:false,//是否显示上传表单
			progressPercent: 0, // 进度条默认为0
			
			error : {
				file :'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryViewFilterWord();
	},
	methods : {
		
				
		 //查询敏感词
	    queryViewFilterWord: function(){
	        let _self = this;
			
	        
			_self.$ajax.get('control/filterWord/manage', {
			    params: {
			    	method : 'view',
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
			    		_self.filterWord = returnValue.data;
			    		
			    	}else if(returnValue.code === 500){//错误
			    		
			    	}
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		
		
		//上传词库UI
		uploadFilterWordUI: function(row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
	    	target.blur();
			
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	
        	this.uploadForm =true;//是否显示上传表单
        	this.progressPercent= 0; // 进度条默认为0
		},
		//文件上传
		uploadFilterWord: function(param) {
			let _self = this;


			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('file', param.file);
			
	    	
			_self.$ajax({
		        method: 'post',
		        url: 'control/filterWord/manage?method=uploadFilterWord',
		        data: formData,
		        timeout: 0,// 定义请求超时时间
		        onUploadProgress: progressEvent => {
		        	if (progressEvent.lengthComputable) {
                    	let rate = progressEvent.loaded / progressEvent.total;  //已上传的比例
                        if (rate < 1) {
                        	//这里的进度只能表明文件已经上传到后台，但是后台有没有处理完还不知道
                            //因此不能直接显示为100%，不然用户会误以为已经上传完毕，关掉浏览器的话就可能导致上传失败
                            //等响应回来时，再将进度设为100%
                            // progressEvent.loaded:已上传文件大小
		            		// progressEvent.total:被上传文件的总大小
                            _self.progressPercent = (progressEvent.loaded / progressEvent.total * 100).toFixed(2);
                        }
                    }
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
			    		_self.progressPercent = 100;
			    		_self.$message.success("上传成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
			    		
			    		_self.uploadForm = false;
			    		_self.fileList=[];
			    		_self.queryViewFilterWord();
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
		},
	
		//删除词库
		deleteFilterWord : function(event) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
	    	target.blur();
	    	
	    	let _self = this;
	    
	    	this.$confirm('此操作将删除该词库, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/filterWord/manage?method=deleteFilterWord',
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
				    		_self.queryViewFilterWord();
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