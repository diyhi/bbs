<!-- 栏目列表 -->
<template id="columnList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>栏目列表</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="addColumnUI(null)">添加栏目</el-button>
			</div>
			<div class="data-table columnListModule" >
				<el-table ref="multipleTable" :data="tableData" :indent="34" tooltip-effect="dark" row-key="id" default-expand-all @cell-click="cellExpandRow"  :tree-props="{children: 'childColumn', hasChildren: 'hasChildren'}" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column label="栏目名称" >
						<template #default="scope">	
							<i class="icon icon-folder el-icon-folder" v-if="scope.row.childColumn.length >0"></i>
							<i class="icon icon-file el-icon-document" v-if="scope.row.childColumn.length ==0"></i>{{scope.row.name}}
				    	</template>
					</el-table-column>
					<el-table-column label="链接方式" align="center" width="200">
						<template #default="scope">	
							<span v-if="scope.row.linkMode == 1">无</span>
							<span v-if="scope.row.linkMode == 2">外部URL</span>
							<span v-if="scope.row.linkMode == 3">内部URL</span>
							<span v-if="scope.row.linkMode == 4">空白页</span>
				    	</template>
					</el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="80"></el-table-column>
					<el-table-column label="操作" align="center" width="230">
						<template #default="scope">
							<div class="link-group-wrapper" >
								<el-link type="primary" class="item" href="javascript:void(0);" @click="addColumnUI(scope.row)">添加下级栏目</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="editColumnUI(scope.row)">修改</el-link>
								<el-link type="primary" class="item" href="javascript:void(0);" @click="deleteColumn(scope.row)">删除</el-link>
							</div>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
			<el-dialog title="添加下级栏目" v-model="addColumnForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="栏目名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="30" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="链接方式" :required="true" :error="error.linkMode">
						<el-radio-group v-model="linkMode">
						    <el-radio :label="1">无</el-radio>
						    <el-radio :label="2">外部URL</el-radio>
						    <el-radio :label="3">内部URL</el-radio>
						    <el-radio :label="4">空白页</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="URL地址" :required="true" :error="error.url" v-if="linkMode == 2 || linkMode == 3">
						<el-input type="textarea" v-model.trim="url" :autosize="{minRows: 3}" placeholder="请输入网址" ></el-input>
						<div class="form-help" v-if="linkMode == 2">请填写http或https开头的网址</div>
						<div class="form-help" v-if="linkMode == 3">标识资源的字符串,不能以/为开头。如：productInfo/3.htm</div>
					</el-form-item>
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="addColumn_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>
			
			<el-dialog title="修改下级栏目" v-model="editColumnForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="栏目名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="30" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="链接方式" :required="true" :error="error.linkMode">
						<el-radio-group v-model="linkMode">
						    <el-radio :label="1">无</el-radio>
						    <el-radio :label="2">外部URL</el-radio>
						    <el-radio :label="3">内部URL</el-radio>
						    <el-radio :label="4">空白页</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="URL地址" :required="true" :error="error.url" v-if="linkMode == 2 || linkMode == 3">
						<el-input type="textarea" v-model.trim="url" :autosize="{minRows: 3}" placeholder="请输入网址" ></el-input>		
						<div class="form-help" v-if="linkMode == 2">请填写http或https开头的网址</div>
						<div class="form-help" v-if="linkMode == 3">标识资源的字符串,不能以/为开头。如：productInfo/3.htm</div>
					</el-form-item>
  				</el-form>
  				<template #footer>
				    <span class="dialog-footer">
				    	<el-button class="submitButton" type="primary" @click="editColumn_submitForm" :disabled="submitForm_disabled">提交</el-button>
				    </span>
				</template>
			</el-dialog>

			
		</div>
	</div>
</template>

<script>
//栏目列表
export default({
	name: 'columnList',//组件名称，keep-alive缓存需要本参数
	template : '#columnList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			

			dirName: '',
			templates: '',//模板
			
			id : '',
			parentId: '',
			name: '',
			sort: 0,
			linkMode: 1,
			url: '',
			error :{
				name: '',
				sort: '',
				linkMode: '',
				url: '',
			},
			
			addColumnForm:false, //是否显示添加下级栏目表单
			editColumnForm:false, //是否显示修改下级栏目表单
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		
		//初始化
		this.queryTemplate();
		this.queryColumnList();
	},
	methods : {
	
		
		//点击单元格展开行
		cellExpandRow : function(row,column,event,cell) {
			if(column.label=="栏目名称"){
				this.$refs.multipleTable.toggleRowExpansion(row);
			}
		},
		
		
		//查询模板
		queryTemplate : function() {
			let _self = this;
			
			_self.$ajax.get('control/column/manage', {
			    params: {
			    	method:'list',
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
			    			if(key == "templates"){
			    				_self.templates = mapData[key];
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
		//查询栏目
		queryColumnList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/column/manage', {
			    params: {
			    	method:'queryColumn',
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
			    		let list = returnValue.data;
			    		if(list != null && list.length >0){
			    			_self.tableData = list;
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

	    
	 
	    
	    //添加下级栏目UI
		addColumnUI: function(row) { 
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	this.addColumnForm =true;//显示添加下级栏目表单
        	
        	
        	this.name = '';
			this.sort = 0;
			this.linkMode = 1;
			this.url = '';
			if(row == null){//根目录
				this.parentId = "";
			}else{//子目录
				this.parentId = row.id; 
			}
			
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//添加下级栏目提交
		addColumn_submitForm: function() { 
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.name != null){
				formData.append('name', _self.name);	
			}
			if(_self.sort != null){
				formData.append('sort', _self.sort);
			}
			if(_self.linkMode != null){
				formData.append('linkMode', _self.linkMode);
			}
			if(_self.url != null){
				formData.append('url', _self.url);
			}
			if(_self.parentId != null){
				formData.append('parentId', _self.parentId);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/column/manage?method=add',
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
			    		_self.addColumnForm = false;
			    		
			    		_self.queryColumnList();	    
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
		
		//修改栏目UI
		editColumnUI: function(row) { 
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	this.editColumnForm =true;//显示添加下级栏目表单
        	
        	this.id = row.id;
        	this.name = row.name;
			this.sort = row.sort;
			this.linkMode = row.linkMode;
			this.url = row.url;
			
	
			
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//修改栏目提交
		editColumn_submitForm: function() { 
			let _self = this;
			_self.submitForm_disabled = true;
			
			
			
			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.name != null){
				formData.append('name', _self.name);	
			}
			if(_self.sort != null){
				formData.append('sort', _self.sort);
			}
			if(_self.linkMode != null){
				formData.append('linkMode', _self.linkMode);
			}
			if(_self.url != null){
				formData.append('url', _self.url);
			}
			if(_self.id != null){
				formData.append('columnId', _self.id);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/column/manage?method=edit',
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
			    		_self.editColumnForm = false;
			    		
			    		_self.queryColumnList();	    
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
		deleteColumn: function(row) { 
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
					formData.append('columnId', row.id);	
				}
				if(_self.dirName != null){
					formData.append('dirName', _self.dirName);
				}
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/column/manage?method=delete',
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
				    		
				    		_self.queryColumnList();
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

