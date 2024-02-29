<!-- 资源列表 -->
<template id="resourceList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>资源列表</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="uploadUI(null)">上传文件</el-button>
				<el-button type="primary" plain size="small" @click="newFolderUI(null)">新建文件夹</el-button>
			</div>
			<div class="data-table resourceListModule" >
				<el-table ref="multipleTable" :data="tableData" :indent="34" tooltip-effect="dark" row-key="id"  @cell-click="cellExpandRow" lazy :load="loadNode" :tree-props="{children: 'childNode', hasChildren: 'hasChildren'}" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="文件" >
						<template #default="scope">	
							<i class="icon icon-folder el-icon-folder" v-if="!scope.row.leaf"></i>
							<i class="icon icon-file el-icon-document" v-if="scope.row.leaf"></i>{{scope.row.name}}
				    	</template>
					</el-table-column>
					<el-table-column label="最后修改时间" align="center" width="200">
						<template #default="scope">	
							{{scope.row.lastModified}}
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="300">
						<template #default="scope">
							<div class="link-group-wrapper" >
						
								<el-link v-if="scope.row.leaf && (/\.(js|css|JS|CSS)$/.test(scope.row.id)) " type="primary" class="item" href="javascript:void(0);" @click="viewUI(scope.row)">编辑</el-link>
								<el-link v-if="scope.row.leaf&& !(/\.(js|css|JS|CSS)$/.test(scope.row.id)) " type="primary" class="item" href="javascript:void(0);" @click="viewUI(scope.row)">查看</el-link>
								<el-link v-if="scope.row.leaf" type="primary" class="item" href="javascript:void(0);" @click="resourceDownload(scope.row)">下载</el-link>
								<el-link v-if="!scope.row.leaf" type="primary" class="item" href="javascript:void(0);" @click="uploadUI(scope.row)">上传文件</el-link>
								<el-link v-if="!scope.row.leaf" type="primary" class="item" href="javascript:void(0);" @click="newFolderUI(scope.row)">新建文件夹</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="renameUI(scope.row)">重命名</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="deleteResource(scope.row)">删除</el-link>
							</div>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
			
			
			
			
			<el-dialog title="查看" v-model="viewWindow">
				<el-image :src="fullPath" :fit="['contain']"></el-image>
			</el-dialog>
			<el-dialog title="文件编辑" v-model="editFileForm" :before-close="handleEditFileFormClose" width="100%" fullscreen="true" :close-on-click-modal="false" :close-on-press-escape="false" :destroy-on-close="true">
				<div class="editFileModule">
					<el-row >
						<el-col :span="24"><div class="name">{{name}}</div></el-col>
					</el-row>
					
					<div class="code">
						<textarea ref="editFileContent" >{{fileContent}}</textarea>
					</div>
					
					<el-row>
						<el-col :span="24">
							 <el-button class="submitButton" type="primary" @click="editFile" :disabled="submitForm_disabled">提交</el-button>
						</el-col>
					</el-row>
				</div>
			</el-dialog>
			<el-dialog title="文件下载" v-model="downloadForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item>
						<el-progress :text-inside="true" :stroke-width="24" :percentage="downloadProgressPercent" />
			        </el-form-item>
  				</el-form>
			</el-dialog>
			
			<el-dialog title="上传文件" v-model="uploadForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item :error="error.uploadFile">
						<el-upload class="importTemplateModule" drag :http-request="upload" :file-list="fileList" :accept="'.zip,.gif,.jpg,.jpeg,.bmp,.png,.svg,.map,.txt,.css,.js'">
							<i class="el-icon-upload"></i>
							<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
							<template #tip>
							    <div class="el-upload__tip">
							    	只允许上传 gif、jpg、jpeg、bmp、png、svg、map、txt、css、js、ico、eot、ttf、woff 文件，且单文件不超过 200MB
							    </div>
							</template>
						</el-upload>
					</el-form-item>
					<el-form-item>
						<el-progress :text-inside="true" :stroke-width="20" :percentage="progressPercent" />
			        </el-form-item>
  				</el-form>
			</el-dialog>
			<el-dialog title="新建文件夹" v-model="newFolderForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="路径">
						根目录/common/{{dirName}}/{{id}}
					</el-form-item>
					<el-form-item label="新文件夹名称" :required="true" :error="error.folderName">
						<el-input v-model.trim="folderName" maxlength="35" clearable="true" show-word-limit></el-input>
						<div class="form-help" >不能含有\\&nbsp;/:*?\"<>|%..字符</div>
					</el-form-item>
					
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="newFolder_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
			<el-dialog title="重命名" v-model="renameForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="旧名称">
						{{name}}
					</el-form-item>
					<el-form-item label="新名称" :required="true" :error="error.rename">
						<el-input v-model.trim="rename" maxlength="35" clearable="true" show-word-limit></el-input>
						<div class="form-help" >不能含有\\&nbsp;/:*?\"<>|%..字符</div>
					</el-form-item>
					
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="rename_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
		</div>
	</div>
</template>

<script>
//资源列表
export default({
	name: 'resourceList',//组件名称，keep-alive缓存需要本参数
	template : '#resourceList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			

			dirName: '',
			templates: '',//模板
			
			id: '',//资源Id
			name: '',//文件名称
			rename: '',//重名命新文件名称
			folderName: '',//新文件夹名称
			fullPath: '',//文件路径
			renameForm:false, //是否显示重命名表单
			newFolderForm:false, //是否显示新建文件表单
			viewWindow:false, //是否显示查看文件窗口
			editFileForm:false,//是否显示文件编辑表单
			downloadForm:false,//是否显示文件下载表单
			submitForm_disabled:false,//提交按钮是否禁用
			
			downloadProgressPercent: 0, // 下载进度条默认为0
			
			uploadForm:false,//是否显示上传表单
			fileList:[],//上传的文件列表
			progressPercent: 0, // 进度条默认为0
			
			
			fileName: '',//文件名称
			fileType: '',//文件类型
			fileContent:'',//文件内容
			editor_editFileContent:'',
			
			error :{
				rename: '',
				folderName: '',
				uploadFile: '',
			},
			maps : new Map(),//缓存数据
			

		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		
		CodeMirror.commands.autocomplete = function(cm) {
			CodeMirror.simpleHint(cm, CodeMirror.javascriptHint); 
		};
		
		
		//初始化
		this.queryTemplate();
		this.queryChildNode(null);
	},
	methods : {
		//点击单元格展开行
		cellExpandRow : function(row,column,event,cell) {
			if(column.label=="文件"){
				let rawValue = this.$refs.multipleTable.store.states.treeData['_value'][row.id];//是否展开行
				
				if(rawValue != undefined && !rawValue.loaded){
					this.$refs.multipleTable.store.loadOrToggle(row);
				
				}
			//const keys = Object.keys(this.$refs.multipleTable.store.states.treeData);
            //for (let i = 0; i < keys.length; i++) {
            //	console.log("值",keys[i]);
            //}
				this.$refs.multipleTable.toggleRowExpansion(row);
			}
		},
		
		//加载子节点
		loadNode : async function (tree, treeNode, resolve) {
			let _self = this;
			
			await _self.$ajax.get('control/resource/query', {
			    params: {
			    	parentId :tree.id,
			    	dirName:_self.dirName,
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
			    		let resourceList = returnValue.data;
			    		if(resourceList != null && resourceList.length >0){
			    			for(let i=0; i<resourceList.length; i++){
			    				let resource = resourceList[i];
			    				if(resource.leaf == true){
			    					resource.hasChildren = false;
			    				}else{
			    					resource.hasChildren = true;
			    				}
			    				
			    			}
			    		}else{
				    		//清空已加载的数据
				    		_self.$refs.multipleTable.store.states.lazyTreeNodeMap.value[tree.id] = []; 
			    		
			    		}
			    		resolve(resourceList);
			    		_self.maps.set(tree.id, {tree, treeNode, resolve});
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
		
			resolve([]);
		},
		//查询资源子节点
		queryChildNode : async function(node) {
			let _self = this;

			let parentId = "";
			if(node != null){
				parentId = node.id;
			}else{
				//清空内容
				_self.tableData = []; 
			}
			let return_resourceList = null;

			await _self.$ajax.get('control/resource/query', {
			    params: {
			    	parentId :parentId,
			    	dirName:_self.dirName,
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
			    		let resourceList = returnValue.data;
			    		if(resourceList != null && resourceList.length >0){
			    			for(let i=0; i<resourceList.length; i++){
			    				let resource = resourceList[i];
			    				if(resource.leaf == true){
			    					resource.hasChildren = false;
			    				}else{
			    					resource.hasChildren = true;
			    				}
			    			}
			    		}
			    		if(node != null){
							return_resourceList = resourceList;
						}else{
							_self.tableData = resourceList;
						}
			    		_self.maps.set(0,resourceList)
			    		
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
			return return_resourceList;
		},
		//查询模板
		queryTemplate : function() {
			let _self = this;
			
			_self.$ajax.get('control/resource/list', {
			    params: {
			    	dirName:_self.dirName,
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
			    		_self.templates = returnValue.data;
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
		//刷新节点
	    refreshNode: function(id,name) { 
	    	let _self = this;
	        _self.$nextTick(() => {
			    //获取父节点
			    let rowId = id;
			    let symbol = rowId.lastIndexOf("/");
				let parentId = rowId.substring(0,symbol); //取前部分
	
			    //懒加载刷新父节点
			   	if (parentId && _self.maps.get(parentId)) {
			    	const { tree, treeNode, resolve } = _self.maps.get(parentId);
			        _self.loadNode(tree, treeNode, resolve);
			        
			    }
			    
		    	//如果为根节点
		    	if(parentId == ""){
		    		_self.queryChildNode(null);
		    	}
			    
			    
			    //懒加载刷新子节点
			    let rawValue = _self.$refs.multipleTable.store.states.treeData['_value'][id];//是否展开行
				if(rawValue != undefined && rawValue.expanded){
					const {  resolve } = _self.maps.get(id);
					_self.queryChildNode({id:parentId}).then(function (res) {
						if(res != null && res.length >0){
							for(let i=0; i<res.length; i++){
								let resource = res[i];
								if(resource.id = (parentId != null && parentId != ''? parentId+'/' : '')+name){
									_self.$refs.multipleTable.store.loadOrToggle(resource);
									break;
								}
							}
						}
						
					});
				}
		    });
	    },
	    
	    //刷新节点
	    refreshNode2: function(id,name) { 
	    	let _self = this;
	        _self.$nextTick(() => {
			    //获取父节点
			    let rowId = id;
			    let symbol = rowId.lastIndexOf("/");
				let parentId = rowId.substring(0,symbol); //取前部分
	
			    //懒加载刷新父节点
			   	if (parentId && _self.maps.get(parentId)) {
			    	const { tree, treeNode, resolve } = _self.maps.get(parentId);
			        _self.loadNode(tree, treeNode, resolve);
			        
			    }
			    
		    	//如果为根节点
		    	if(parentId == ""){
		    		_self.queryChildNode(null);
		    	}
			    
			    
			    //懒加载刷新子节点
			    let rawValue = _self.$refs.multipleTable.store.states.treeData['_value'][id];//是否展开行
			   
				if(rawValue != undefined && rawValue.expanded){
					const {  resolve } = _self.maps.get(id);
					_self.queryChildNode({id:parentId}).then(function (res) {
						if(res != null && res.length >0){
							for(let i=0; i<res.length; i++){
								let resource = res[i];
								if(resource.id = (parentId != null && parentId != ''? parentId+'/' : '')+name){
	 
								 _self.loadNode(resource, resource, resolve);//重新加载
									break;
								}
							}
						}
						
					});
				}
		    });
	    },
	    
	    
	    
	    
	    //查看文件UI
		viewUI: function(row) {
			let _self = this;
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
    	    
			this.id = row.id;
			this.name= row.name;
			this.fileContent = "";
			this.fileType = "";
			
			
			//如果为文本格式，则用查看器加载
			if (/\.(map|Map|txt|TXT|js|css|JS|CSS)$/.test(row.id)) {
				_self.editFileForm =true;
			  
				let mode = "text/html";
			  
			  	
			  
			  
				_self.$ajax.get('control/resource/manage', {
				    params: {
				    	method :'showFileUI',
				    	resourceId :row.id,
				    	dirName:_self.dirName,
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
				    			if(key == "fileType"){
				    				_self.fileType = mapData[key];
				    				
				    				if(_self.fileType == "js"){
										mode = "javascript";
									}else if(_self.fileType == "css"){
										mode = "css";
									}
				    				
				    			}else if(key == "fileContent"){
				    				_self.fileContent = mapData[key];	
				    				
				    				_self.$nextTick(function() {
					    				//markRaw 方法可以将原始数据标记为非响应式的
					    				const { markRaw } = Vue;
					    				_self.editor_editFileContent = markRaw(CodeMirror.fromTextArea(_self.$refs.editFileContent, {
									      	lineNumbers: true,
									        mode: mode,
									        indentUnit: 4,
									        extraKeys: {"Alt-/": "autocomplete"},
									        indentWithTabs: true,
									        autoCloseTags: true,
									 	}));
									    _self.editor_editFileContent.setSize("100%","100%");//设置自适应高度 
				    				
				    				});
				    			}
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
		    }else{
		    	this.fullPath = "common/"+this.dirName+"/"+row.id+"?timestamp="+ new Date().getTime();
		    	this.viewWindow =true;//是否显示查看文件窗口

		    }
		},
	    
		editFile: function() {
	    	let _self = this;
	    	
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.editor_editFileContent != null && _self.editor_editFileContent != ''){
				formData.append('content', _self.editor_editFileContent.getValue());	
			}
			if(_self.id != null){
				formData.append('resourceId', _self.id);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/resource/manage?method=editFile',
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
			    		_self.editFileForm = false;
			    		
	
						_self.handleEditFileFormClose(function(){});
			    		
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
			    _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	    //处理文件编辑表单关闭
    	handleEditFileFormClose : function(done) {
    		if(this.editor_editFileContent != ""){
    			this.editor_editFileContent.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			this.editor_editFileContent = "";
    		}
			done();
		},
		
		//下载
		resourceDownload : function(row) {
			let _self = this;
			_self.downloadForm = true;
			_self.downloadProgressPercent =0;

			_self.$ajax.get('control/resource/manage', {
			    params: {
			    	method:'download',
			    	resourceId:row.id,
			    	dirName:_self.dirName
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

				var fileName = row.name;

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
		
	    
	    
	    //文件上传UI
		uploadUI: function(row) {
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	if(row == null){//根目录
				this.id = "";
				this.name= "";
			}else{//子目录
				this.id = row.id;
				this.name= row.name;
			}
        	
        	this.uploadForm =true;//是否显示上传表单
        	this.progressPercent= 0; // 进度条默认为0
        	this.fileList=[];
		},
	
		//文件上传
		upload: function(param) {
			let _self = this;


			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.id != null){
				formData.append('resourceId', _self.id);	
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			formData.append('uploadFile', param.file);
			
	    	
			_self.$ajax({
		        method: 'post',
		        url: 'control/resource/manage?method=upload&a=a',//a=a参数的作用是仅增加连接符&
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
			    		_self.refreshNode2(_self.id,_self.name);
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
	    
	    //新建文件夹UI
		newFolderUI: function(row) { 
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	this.newFolderForm =true;//显示新建文件夹表单
        	
			if(row == null){//根目录
				this.id = "";
				this.name= "";
			}else{//子目录
				this.id = row.id;
				this.name= row.name;
			}
			
        	this.folderName = "";
        	
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//新建文件夹提交
		newFolder_submitForm: function() { 
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.id != null){
				formData.append('resourceId', _self.id);	
			}
			if(_self.folderName != null){
				formData.append('folderName', _self.folderName);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/resource/manage?method=newFolder',
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
			    		_self.newFolderForm = false;
			    		
			    		_self.refreshNode2(_self.id,_self.name);	    
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
			    _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
	    
		//重命名UI
		renameUI: function(row) { 
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	this.renameForm =true;//显示重命名表单
        	
			
			this.id = row.id;
			this.name= row.name;
        	this.rename = "";
        	
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//重命名提交
		rename_submitForm: function() { 
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.id != null){
				formData.append('resourceId', _self.id);	
			}
			if(_self.rename != null){
				formData.append('rename', _self.rename);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/resource/manage?method=rename',
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
			    		_self.renameForm = false;
			    		//刷新节点
	    				_self.refreshNode(_self.id, _self.rename);     
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
			    _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
		//删除
		deleteResource: function(row) { 
			let _self = this;
			
	    	_self.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
		        //清除错误
				for (let key in _self.error) { 
	    			_self.error[key] = "";
	    	    }
				let formData = new FormData();
				if(_self.id != null){
					formData.append('resourceId', row.id);	
				}
				if(_self.dirName != null){
					formData.append('dirName', _self.dirName);
				}
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/resource/manage?method=delete',
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
				    		
				    		//刷新节点
		    				_self.refreshNode(row.id, row.name);     
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
	        });
		},
	}
});
</script>

