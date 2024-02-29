<!-- 版块代码 -->
<template id="forumCodeList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>版块代码</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="data-table forumCodeListModule" >
				<el-table ref="multipleTable" :data="tableData" :indent="34" tooltip-effect="dark" row-key="nodeId" :expand-row-keys="defaultExpandRow" @cell-click="cellExpandRow" :tree-props="{children: 'childNode', hasChildren: 'hasChildren'}" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="nodeName" label="版块" ></el-table-column>
					<el-table-column label="最后修改时间" align="center" width="200">
						<template #default="scope">	
							<div v-if="scope.row.pc_lastTime != null && scope.row.pc_lastTime != ''">电脑版 {{scope.row.pc_lastTime}}</div>
							<div v-if="scope.row.wap_lastTime != null && scope.row.wap_lastTime != ''">手机版 {{scope.row.wap_lastTime}}</div>
				    	</template>
					</el-table-column>
					<el-table-column label="添加版块代码" align="center" width="130">
						<template #default="scope">	
							<el-link type="primary" href="javascript:void(0);" @click="addForumCodeUI($event,scope.row)" v-if="scope.row.prefix != null && scope.row.prefix != ''">添加版块代码</el-link>
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="220">
						<template #default="scope">
							<div class="link-group-wrapper" v-if="(scope.row.prefix == null || scope.row.prefix == '') && scope.row.childNode.length ==0">
								<el-link type="primary" class="item" href="javascript:void(0);" @click="editForumSourceUI($event,scope.row)">源码编辑</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="editForumCodeUI($event,scope.row)">修改</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="deleteForumCode($event,scope.row)">删除</el-link>
							</div>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
			
			
			<el-dialog title="添加版块代码" v-model="addForumCodeForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="模板名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="模板显示类型" :required="true" :error="error.displayType">
						<el-select v-model="displayType" @change="selectedTag"  placeholder="选择标签">
							<el-option v-for="item in displayType_options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="备注" :error="error.remark">
						<el-input type="textarea" v-model="remark" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>
					</el-form-item>
					
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="addForumCode_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
			
			<el-dialog title="修改版块代码" v-model="editForumCodeForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="模板名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="模板显示类型" :required="true" :error="error.displayType">
						<el-select v-model="displayType" @change="selectedTag" placeholder="选择标签">
							<el-option v-for="item in displayType_options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="备注" :error="error.remark">
						<el-input type="textarea" v-model="remark" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>
					</el-form-item>
					
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="editForumCode_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
			
			<el-dialog title="源码编辑" v-model="editForumSourceForm" :before-close="handleEditForumSourceFormClose" width="100%" fullscreen="true" :close-on-click-modal="false" :close-on-press-escape="false" :destroy-on-close="true">
				<div class="editForumSourceModule">
					<div class="forumSource-container">
						<el-row >
							<el-col :span="24"><div class="nodeName">{{nodeName}}.html</div></el-col>
						</el-row>
						
						
						<el-tabs v-model="activeName" @tab-click="handleEditForumSourceFormClick">
						    <el-tab-pane label="电脑版" name="pc">
						    	<div class="code" >
									<textarea ref="pc_code" v-if="isPCHtmlExist">{{pc_html}}</textarea>
									<div class="notExistPrompt" v-if="!isPCHtmlExist">电脑版 {{nodeName}}.html 文件不存在</div>
								</div>
							</el-tab-pane>
						    <el-tab-pane label="移动版" name="wap">
						    	<div class="code">
									<textarea ref="wap_code" v-if="isWapHtmlExist">{{wap_html}}</textarea>
									<div class="notExistPrompt" v-if="!isWapHtmlExist">移动版 {{nodeName}}.html 文件不存在</div>
								</div>
						    </el-tab-pane>
						</el-tabs>
						
						
						<el-row>
							<el-col :span="24">
								 <el-button class="submitButton" type="primary" @click="editForumSource_submitForm" :disabled="submitForm_disabled">提交</el-button>
							</el-col>
						</el-row>
					</div>
					<el-row >
						<el-col :span="24">
							<div class="doc">
								<el-tabs v-model="activeDoc" type="card"  @tab-click="handleDocClick">
								    <el-tab-pane label="文档参数" name="example">
									    <div ref="example_doc" v-html="example"></div>
								    </el-tab-pane>
								    <el-tab-pane label="公共API" name="common">
								    	<div ref="common_doc" v-html="common"></div>
								    </el-tab-pane>
								</el-tabs>
							</div>
						</el-col>
					</el-row>
				</div>
			</el-dialog>
		</div>
	</div>
</template>

<script>
//版块代码
export default({
	name: 'forumCodeList',//组件名称，keep-alive缓存需要本参数
	template : '#forumCodeList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			defaultExpandRow: [],//默认展开行

			dirName: '',
			templates: '',//模板
			
			addForumCodeForm:false, //是否显示添加版块代码表单
			editForumCodeForm:false, //是否显示修改版块代码表单
			editForumSourceForm:false,//是否显示源码编辑表单
			parentId: '',
			parentNodeName: '',
			nodeName: '',
			name: '',//模板名称
			oldFileName: '',//旧模板名称
			displayType: '',//模板显示类型
			displayType_options:[],
			remark: '',//备注
			submitForm_disabled:false,//提交按钮是否禁用
			
			error :{
				name: '',
				displayType: '',
				remark: '',
			},
			
		//	pc_code:'',//代码
		//	wap_code:'',//代码
			activeName : 'pc',//源码表单标签选择
			activeDoc : 'example',//文档选择common
			example: '',
			common: '',
			pc_html: '',
			wap_html: '',
			editor_pc:'',
			editor_wap:'',
			isPCHtmlExist:false,//是否存在电脑版html文件
			isWapHtmlExist:false,//是否存在手机版html文件
			example_editor:[],
			common_editor:[],
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
		this.queryForumDirectory();
	},
	methods : {
		//点击单元格展开行
		cellExpandRow : function(row,column,event,cell) {
			if(column.label=="版块"){
				this.$refs.multipleTable.toggleRowExpansion(row);
			}
		},
		//选中标签
	    selectedTag: function(val) { 
	        this.tagId = val;
	    },
		//添加版块代码UI
		addForumCodeUI : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
        	
        	
        	this.addForumCodeForm =true;//显示添加版块代码表单
        	this.displayType_options = [];
        	if(row.displayType != null && row.displayType.length >0){
				for(var i = 0 ; i < row.displayType.length; i++){
					//默认选中第一个
					if(i == 0){
						this.displayType = row.displayType[i];
					}
					
					this.displayType_options.push({
			          value: row.displayType[i],
			          label: row.displayType[i]
			        });
				}
			}else{
				this.displayType = "";
				this.displayType_options = [];
			}
			this.parentId = row.nodeId;
			this.nodeName= row.nodeName;
        	
        	this.name = "";
        	this.remark = "";
        	
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//添加版块代码
		addForumCode_submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.parentId != null){
				formData.append('parentId', _self.parentId);	
			}
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.displayType != null){
				formData.append('displayType', _self.displayType);
			}
			if(_self.remark != null){
				formData.append('remark', _self.remark);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/forumCode/manage?method=add',
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
			    		_self.addForumCodeForm = false;
			    		_self.queryForumCode( _self.nodeName);
			    		
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
	    
	    //修改版块代码UI
		editForumCodeUI : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
        	
        	var name_arr = new Array(); //定义一数组 
			name_arr = row.nodeName.split("_");
			var name_arr_length = name_arr.length;	
			var displayType = name_arr[name_arr_length-2];
			var displayTypeValue = "";
			if(displayType == "monolayer"){
				displayTypeValue = "单层";
			}else if(displayType == "multilayer"){
				displayTypeValue = "多层";
			}else if(displayType == "page"){
				displayTypeValue = "分页";
			}else if(displayType == "entityBean"){
				displayTypeValue = "实体对象";
			}else if(displayType == "collection"){
				displayTypeValue = "集合";
			}
			
        	this.name = name_arr[name_arr_length-1];
        	this.remark = row.remark;
        	this.parentId = "";
        	this.parentNodeName = "";
        	
        	this.editForumCodeForm =true;//显示添加版块代码表单
        	this.displayType = "";
			this.displayType_options = [];
        	let parent_displayType = [];
        	for(let i=0; i<this.tableData.length; i++){
				let table = this.tableData[i];
				if(table.childNode != null && table.childNode.length >0){
					for(let j=0; j<table.childNode.length; j++){
						let childTable = table.childNode[j];
						for(let k=0; k<childTable.childNode.length; k++){
							let forumCodeTable = childTable.childNode[k];
							
							if(forumCodeTable.nodeId == row.nodeId){
								parent_displayType = childTable.displayType;
								this.parentId = childTable.nodeId;
								this.parentNodeName = childTable.nodeName;
								break;
							}
						}
					}
				}
			}
        	
        	
        	
        	if(parent_displayType != null && parent_displayType.length >0){
				for(var i = 0; i < parent_displayType.length; i++){
					if(parent_displayType[i] == displayTypeValue){
						this.displayType = displayTypeValue;
					}
				
					this.displayType_options.push({
			        	value: parent_displayType[i],
			        	label: parent_displayType[i]
			        });
				}
			}
			
			this.nodeId = row.nodeId;
			this.oldFileName= row.nodeName;
        	
        	
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
	    
	    //修改版块代码
		editForumCode_submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.nodeId != null){
				formData.append('nodeId', _self.nodeId);	
			}
			if(_self.parentId != null){
				formData.append('parentId', _self.parentId);	
			}
			if(_self.oldFileName != null){
				formData.append('oldFileName', _self.oldFileName);	
			}
			
			
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.displayType != null){
				formData.append('displayType', _self.displayType);
			}
			if(_self.remark != null){
				formData.append('remark', _self.remark);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/forumCode/manage?method=edit',
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
			    		_self.editForumCodeForm = false;
			    		_self.queryForumCode( _self.parentNodeName);
			    		
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
	    
	    
	    
		//查询版块目录
		queryForumDirectory : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/forumCode/query', {
			    params: {
			    	method :'directory',
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
			    		let forumCodeNodeList = returnValue.data;
			    		if(forumCodeNodeList != null && forumCodeNodeList.length >0){
			    			for(let i=0; i<forumCodeNodeList.length; i++){
			    				let forumCodeNode = forumCodeNodeList[i];
			    				
			    				_self.defaultExpandRow.push(String(forumCodeNode.nodeId));
			    				
			    			}
			    		}
			    		_self.tableData = forumCodeNodeList;
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
		//查询版块代码
		queryForumCode : function(childNodeName) {
			let _self = this;

			_self.$ajax.get('control/forumCode/query', {
			    params: {
			    	method :'forumCode',
			    	dirName:_self.dirName,
			    	childNodeName:childNodeName
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
			    		let forumCodeNodeList = returnValue.data;
			    		for(let i=0; i<_self.tableData.length; i++){
							let table = _self.tableData[i];
							if(table.childNode != null && table.childNode.length >0){
								for(let j=0; j<table.childNode.length; j++){
									let childTable = table.childNode[j];
									if(childTable.nodeName == childNodeName){
										childTable.childNode = forumCodeNodeList;
										
									}
								}
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
		},
	
		//查询模板
		queryTemplate : function() {
			let _self = this;
			
			_self.$ajax.get('control/forumCode/list', {
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
	
	
		
		
		//删除版块代码
	    deleteForumCode : function(event,row) {
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
		    	formData.append('nodeName', row.nodeName);
		    	formData.append('dirName', _self.dirName);
	
				this.$ajax({
			        method: 'post',
			        url: 'control/forumCode/manage?method=delete',
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
				    		
				    		
				    		
				    		for(let i=0; i<_self.tableData.length; i++){
								let table = _self.tableData[i];
								if(table.childNode != null && table.childNode.length >0){
									for(let j=0; j<table.childNode.length; j++){
										let childTable = table.childNode[j];
										for(let k=0; k<childTable.childNode.length; k++){
											let forumCodeTable = childTable.childNode[k];
											
											if(forumCodeTable.nodeId == row.nodeId){
												_self.queryForumCode(childTable.nodeName);
												break;
											}
										}
									}
								}
							}
				    		
				    		
				    		
				    		
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
    	//源码编辑UI
	    editForumSourceUI : function(event,row) {
	    	let _self = this;
    		_self.editForumSourceForm = true;
    		_self.nodeName = row.nodeName;
    	
    	
    		_self.$ajax.get('control/forumCode/manage', {
			    params: {
			    	method:'forumSource',
			    	dirName:_self.dirName,
			    	nodeName:row.nodeName,
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
			    			if(key == "example"){
			    				_self.example = mapData[key];   				
			    			}else if(key == "common"){
			    				_self.common = mapData[key];
			    			}else if(key == "pc_html"){
			    				_self.pc_html = mapData[key];
			    			}else if(key == "wap_html"){
			    				_self.wap_html = mapData[key];
			    			}else if(key == "isPCHtmlExist"){
			    				_self.isPCHtmlExist = mapData[key];
			    			}else if(key == "isWapHtmlExist"){
			    				_self.isWapHtmlExist = mapData[key];
			    			}
			    		}
			    		
			    		_self.$nextTick(function() {
	    					if(_self.activeDoc == "example"){
	    						//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
	    						let exampleCodeList = _self.$refs.example_doc.querySelectorAll("[name*='exampleCode']");
		    					for(let i = 0; i < exampleCodeList.length; ++i) {
									let exampleCode = exampleCodeList[i];
									let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
								      	lineNumbers: true,
								        mode: 'text/html',
								        indentUnit: 4,
								        extraKeys: {"Alt-/": "autocomplete"},
								        indentWithTabs: true,
								        autoCloseTags: true,
								 	}));
								    editor.setSize("100%","100%");//设置自适应高度 
									
									_self.example_editor.push(editor);
								}
	    					}
	    				});
			    		_self.$nextTick(function() {
	    					if(_self.activeDoc == "common"){
	    						//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
	    						let exampleCodeList = _self.$refs.common_doc.querySelectorAll("[name*='exampleCode']");
		    					for(let i = 0; i < exampleCodeList.length; ++i) {
									let exampleCode = exampleCodeList[i];
									let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
								      	lineNumbers: true,
								        mode: 'text/html',
								        indentUnit: 4,
								        extraKeys: {"Alt-/": "autocomplete"},
								        indentWithTabs: true,
								        autoCloseTags: true,
								 	}));
								    editor.setSize("100%","100%");//设置自适应高度 
									
									_self.common_editor.push(editor);
								}
	    					}
	    				});
			    		if(_self.activeName == "pc" && _self.isPCHtmlExist){
			    			_self.$nextTick(function() {
			    				//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
			    				_self.editor_pc = markRaw(CodeMirror.fromTextArea(_self.$refs.pc_code, {
							      	lineNumbers: true,
							        mode: 'text/html',
							        indentUnit: 4,
							        extraKeys: {"Alt-/": "autocomplete"},
							        indentWithTabs: true,
							        autoCloseTags: true,
							 	}));
							 //	_self.editor_pc.setValue(_self.pc_html);
							    _self.editor_pc.setSize("100%","100%");//设置自适应高度 
		    				
		    				});
					    }
			    		if(_self.activeName == "wap" && _self.isWapHtmlExist){
	    					_self.$nextTick(function() {
			    				//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
			    				_self.editor_wap = markRaw(CodeMirror.fromTextArea(_self.$refs.wap_code, {
							      	lineNumbers: true,//是否在编辑器左侧显示行号
							        mode: 'text/html',
							        indentUnit: 4,//缩进单位，值为空格数，默认为2 
							        extraKeys: {"Alt-/": "autocomplete"},//给编辑器绑定快捷键
							        indentWithTabs: true,//在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符，默认为false
							        autoCloseTags: true,
							    }));
							    _self.editor_wap.setSize("100%","100%");//设置自适应高度 
		    				});
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
    	//提交源码编辑
		editForumSource_submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.editor_pc != null && _self.editor_pc != ''){
				formData.append('pc_code', _self.editor_pc.getValue());	
			}else{
				formData.append('pc_code', _self.pc_html);	
			}
			if(_self.editor_wap != null && _self.editor_wap != ''){
				formData.append('wap_code', _self.editor_wap.getValue());
			}else{
				formData.append('wap_code', _self.wap_html);
			}
			if(_self.nodeName != null){
				formData.append('nodeName', _self.nodeName);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/forumCode/manage?method=forumSource&a=a',//a=a参数的作用是仅增加连接符&
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
			    		_self.editForumSourceForm = false;
			    		
			    		
			    		for(let i=0; i<_self.tableData.length; i++){
							let table = _self.tableData[i];
							if(table.childNode != null && table.childNode.length >0){
								for(let j=0; j<table.childNode.length; j++){
									let childTable = table.childNode[j];
									for(let k=0; k<childTable.childNode.length; k++){
										let forumCodeTable = childTable.childNode[k];
										
										if(forumCodeTable.nodeName == _self.nodeName){
											_self.queryForumCode(childTable.nodeName);
											break;
										}
									}
								}
							}
						}
						
						_self.handleEditForumSourceFormClose(function(){});
			    		
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
		//处理源码编辑表单选择
		handleEditForumSourceFormClick(tab, event) {
			let _self = this;

        	if(tab.props.name == "pc" && _self.isPCHtmlExist){
        		_self.$nextTick(function() {
        			if(_self.editor_pc == ""){
        				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
        				_self.editor_pc = markRaw(CodeMirror.fromTextArea(_self.$refs.pc_code, {
					      	lineNumbers: true,
					        mode: 'text/html',
					        indentUnit: 4,
					        extraKeys: {"Alt-/": "autocomplete"},
					        indentWithTabs: true,
					        autoCloseTags: true,
					 	}));
					    _self.editor_pc.setSize("100%","100%");//设置自适应高度 
        			}
   				});
        	
        	}
        	if(tab.props.name == "wap" && _self.isWapHtmlExist){
        		_self.$nextTick(function() {
    				if(_self.editor_wap == ""){
    					//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
	    				_self.editor_wap = markRaw(CodeMirror.fromTextArea(_self.$refs.wap_code, {
					      	lineNumbers: true,//是否在编辑器左侧显示行号
					        mode: 'text/html',
					        indentUnit: 4,//缩进单位，值为空格数，默认为2 
					        extraKeys: {"Alt-/": "autocomplete"},//给编辑器绑定快捷键
					        indentWithTabs: true,//在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符，默认为false
					        autoCloseTags: true,
					    }));
					    _self.editor_wap.setSize("100%","100%");//设置自适应高度 
				    }
   				});
        	}
    	},
    	//处理文档表单选择
		handleDocClick(tab, event) {
			let _self = this;

			if(tab.props.name == "example"){
        		_self.$nextTick(function() {
        			if(_self.example_editor.length ==0){
        				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
        				let exampleCodeList = _self.$refs.example_doc.querySelectorAll("[name*='exampleCode']");
    					for(let i = 0; i < exampleCodeList.length; ++i) {
							let exampleCode = exampleCodeList[i];
							let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
						      	lineNumbers: true,
						        mode: 'text/html',
						        indentUnit: 4,
						        extraKeys: {"Alt-/": "autocomplete"},
						        indentWithTabs: true,
						        autoCloseTags: true,
						 	}));
						    editor.setSize("100%","100%");//设置自适应高度 
							
							_self.example_editor.push(editor);
						}
        			}
   				});
        	
        	}
        	if(tab.props.name == "common"){
	        	_self.$nextTick(function() {
	        		if(_self.common_editor.length ==0){
	       				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
	       				let exampleCodeList = _self.$refs.common_doc.querySelectorAll("[name*='exampleCode']");
	   					for(let i = 0; i < exampleCodeList.length; ++i) {
							let exampleCode = exampleCodeList[i];
							let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
						      	lineNumbers: true,
						        mode: 'text/html',
						        indentUnit: 4,
						        extraKeys: {"Alt-/": "autocomplete"},
						        indentWithTabs: true,
						        autoCloseTags: true,
						 	}));
						    editor.setSize("100%","100%");//设置自适应高度 
							
							_self.common_editor.push(editor);
						}
	       			}
       			});
        	}
		},
    	//处理源码编辑表单关闭
    	handleEditForumSourceFormClose : function(done) {
    		if(this.editor_pc != ""){
    			this.editor_pc.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			this.editor_pc = "";
    		}
    		if(this.editor_wap != ""){
    			this.editor_wap.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			this.editor_wap = "";
    		}
    		this.activeName = "pc";
    		this.activeDoc='example';
    		
    		if(this.example_editor != null && this.example_editor.length >0){
    			for(let i =0; i <this.example_editor.length; i++ ){
    				let editor = this.example_editor[i];
    				editor.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			}
    			this.example_editor = [];
    		}
    		if(this.common_editor != null && this.common_editor.length >0){
    			for(let i =0; i <this.common_editor.length; i++ ){
    				let editor = this.common_editor[i];
    				editor.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			}
    			this.common_editor = [];
    		}
    		
			done();
		},
	}
});
</script>
