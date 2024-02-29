<!-- 导入模板列表 -->
<template id="importTemplateList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item>模板压缩包列表</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="uploadUI($event)">模板压缩包上传</el-button>
				
				<el-dialog title="模板压缩包上传" v-model="uploadForm">
					<el-form label-width="auto"  @submit.native.prevent>
						<el-form-item :error="error.uploadFile">
							<el-upload class="importTemplateModule" drag :http-request="upload" :file-list="fileList" :accept="'.zip'">
								<i class="el-icon-upload"></i>
								<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
								<template #tip>
								    <div class="el-upload__tip">
								    	只允许上传 zip 文件，且单文件不超过 200MB
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
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="fileName" label="文件名称" align="center" ></el-table-column>
					<el-table-column prop="name" label="模板名称" align="center" ></el-table-column>
					<el-table-column prop="dirName" label="目录名称" align="center" ></el-table-column>
					<el-table-column prop="introduction" label="模板简介" align="center" ></el-table-column>	
					<el-table-column label="操作" align="center" width="450">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="templateDownload($event,scope.row)">下载</el-button>
									<el-button type="primary" size="mini" @click="directoryRenameUI($event,scope.row)">目录重命名</el-button>
									<el-button type="primary" size="mini" @click="importTemplate($event,scope.row)">解压到模板列表</el-button>
									<el-button type="primary" size="mini" @click="deleteExport($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
			
			<el-dialog title="文件下载" v-model="downloadForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item>
						<el-progress :text-inside="true" :stroke-width="24" :percentage="downloadProgressPercent" />
			        </el-form-item>
  				</el-form>
			</el-dialog>
			<el-dialog title="目录重命名" v-model="directoryRenameForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="旧目录名称" >
						{{oldDirName}}
					</el-form-item>
					<el-form-item label="新目录名称" :required="true" :error="error.directoryName">
						<el-input v-model.trim="directoryName" maxlength="40" clearable="true" show-word-limit></el-input>
					</el-form-item>
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button type="primary" class="submitButton" @click="directoryRename_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
			
			
		</div>
	</div>
</template>

<script>
//导入模板列表
export default({
	name: 'importTemplateList',//组件名称，keep-alive缓存需要本参数
	template : '#importTemplateList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			
			oldDirName: '',
			fileName: '',
			directoryRenameForm :false,//是否显示目录重命名表单
			directoryName: '',//新目录名称
			downloadForm:false,//是否显示文件下载表单
			submitForm_disabled:false,//提交按钮是否禁用
			
			downloadProgressPercent: 0, // 下载进度条默认为0
			
			uploadForm:false,//是否显示上传表单
			fileList:[],//上传的文件列表
			progressPercent: 0, // 进度条默认为0
			error :{
				directoryName: '',
				uploadFile: '',
			}
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		//初始化
		this.queryImportTemplateList();
	},
	methods : {
		//模板压缩包上传UI
		uploadUI: function(event) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
        	
        	
        	this.uploadForm =true;//是否显示上传表单
        	this.progressPercent= 0; // 进度条默认为0
        	this.fileList=[];
		},
	
		//模板压缩包上传
		upload: function(param) {
			let _self = this;

			let formData = new FormData();
			formData.append('uploadFile', param.file);
			
	    	
			_self.$ajax({
		        method: 'post',
		        url: 'control/template/manage?method=upload',
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
			    		_self.queryImportTemplateList();
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
	
	
		//模板下载
		templateDownload : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
        	
        	/**
        	let access_token = "";
        	let oauth2Token = window.sessionStorage.getItem('oauth2Token');
			if(oauth2Token != null){
				let oauth2Object = JSON.parse(oauth2Token);
				access_token = oauth2Object.access_token;
			}
			const link = document.createElement('a'); // 生成一个a标签。
			link.href = "control/template/manage?method=templateDownload&fileName="+row.fileName+"&access_token="+access_token; // href属性指定下载链接
			link.download = row.fileName; // dowload属性指定文件名
			link.setAttribute("rel","opener");
			link.click(); // click()事件触发下载
			**/
			 
        	
			let _self = this;
			_self.downloadForm = true;
			_self.downloadProgressPercent =0;


			_self.$ajax.get('control/template/manage', {
			    params: {
			    	method:'templateDownload',
			    	fileName:row.fileName
			    },
			    headers: {
				  	'showLoading': false,//是否显示图标
				},
			    responseType: 'blob',// 表明返回服务器返回的数据类型
			    timeout: 0,// 定义请求超时时间
			    onDownloadProgress: (evt) => {
					// 对原生进度事件的处理
					_self.downloadProgressPercent = parseInt(evt.loaded / evt.total * 100);
				}
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				var blob = response.data;
				if(blob.type ==  "text/json"){
					_self.$message({
						duration :0,
			            showClose: true,
			            message: (response.headers.exception != undefined ? decodeURIComponent(response.headers.exception) :""),
			            type: 'error'
			        });
			        _self.downloadForm = false;
					return;
				}
				
				
			    var blob = response.data;
			    var a = document.createElement('a');
			    // blob.type = "application/octet-stream";
			    var url = window.URL.createObjectURL(blob);

				var fileName = row.fileName;

			    if(window.navigator.msSaveBlob){
			    	try {
			        	window.navigator.msSaveBlob(blob,fileName)
			    	} catch (e) {
			        	console.log(e);
			    	}
			    }else{
			    	a.href = url;
			    	a.download = fileName ;
			    	document.body.appendChild(a); // 火狐浏览器 必须把元素插入body中
			    	a.click();
			    	document.body.removeChild(a);
			   		//释放之前创建的URL对象
			    	window.URL.revokeObjectURL(url);
			    }
			     _self.downloadForm = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//目录重命名表单
		directoryRenameUI : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
        	
        	
        	this.directoryRenameForm =true;//是否显示目录重命名表单
        	this.oldDirName = row.dirName;
        	this.fileName = row.fileName;
        	this.directoryName= '';//新目录名称
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//目录重命名提交表单
		directoryRename_submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.directoryName != null){
				formData.append('directoryName', _self.directoryName);
				
			}
			if(_self.fileName != null){
				formData.append('fileName', _self.fileName);
				
			}
			_self.$ajax({
		        method: 'post',
		        url: 'control/template/manage?method=directoryRename&a=a',//a=a参数的作用是仅增加连接符&
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
			    		_self.$message.success("提交成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
			    		_self.directoryRenameForm = false;
			    		_self.queryImportTemplateList();
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			_self.error[key] = errorMap[key];
			    	    }
			    		
			    	}
			    }
			    _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
		//解压到模板列表
    	importTemplate : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
		
			
		
	    	let _self = this;
	    	this.$confirm('此操作将解压到模板列表, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('fileName', row.fileName);
		    	
		    	let msg = this.$message({
			        duration: 0,
			        type: 'warning',
			        message: '解压到模板列表中，请稍候....'
				});
				
				this.$ajax({
			        method: 'post',
			        url: 'control/template/manage?method=import&a=a',//a=a参数的作用是仅增加连接符&
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
				    		_self.$message.success("解压成功，请到“模板列表查看”");
				    		
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
	
		//查询模板列表
		queryImportTemplateList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/template/manage', {
			    params: {
			    	method:'importTemplateList',
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
			    		}
			    	}else if(returnValue.code === 500){
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
		//删除模板压缩包
	    deleteExport : function(event,row) {
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
		    	formData.append('fileName', row.fileName);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/template/manage?method=deleteExport&a=a',//a=a参数的作用是仅增加连接符&
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
				    		
				    		_self.queryImportTemplateList();
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
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/template/manage?method=export',
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
				    		_self.$message.success("导出成功，请到“导入模板列表查看”");
				    		
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
