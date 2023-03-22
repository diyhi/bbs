<!-- 合并帮助分类 -->
<template id="mergerHelpType-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item @click="$router.push({path: '/admin/control/helpType/list'});">全部分类</el-breadcrumb-item>
					<el-breadcrumb-item v-for="(value, key) in navigation" @click="$router.push({path: '/admin/control/helpType/list',query:{parentId:key}});" @click="queryHelpTag(1,key)">{{value}}</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/helpType/list',query:{parentId:($route.query.sourceParentId != undefined ? $route.query.sourceParentId:''),page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="主分类" >
						{{helpType.name}}
					</el-form-item>
					<el-form-item label="选择分类合并到主分类" :required="true" :error="error.typeId">
						<el-select ref="type_ref" v-model="typeIdGroup" @focus="loadType" @remove-tag="removeType" multiple placeholder="请选择">
							<el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
						<div class="form-help" >需要选择其它分类合并到主分类，不能选择主分类的父类或子类进行合并</div>
					</el-form-item>


					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				<el-dialog title="选择分类" v-model="type_form">
					<div class="dialog-nav-breadcrumb">
						<el-breadcrumb separator-class="el-icon-arrow-right">
							<el-breadcrumb-item @click="queryTypePage(1,'')">全部标签</el-breadcrumb-item>
							<el-breadcrumb-item v-for="(value, key) in navigation" @click="queryTypePage(1,key)">{{value}}</el-breadcrumb-item>
							
						</el-breadcrumb>
					</div>
					<div class="dialog-data-table" >
						<el-table ref="multipleTable" :data="tableData" @cell-click="cellExpandRow" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
							<el-table-column label="选择" align="right" width="50">
								<template #default="scope">
									<el-radio v-model="mergerTypeId" v-if="scope.row.childNodeNumber ==0" :label="typeIdList[scope.$index]" >&nbsp;</el-radio>
						    	</template>
							</el-table-column>
							<el-table-column label="分类名称">
								<template #default="scope">
									<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
									<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
						    	</template>
							</el-table-column>
						</el-table>
						<div class="pagination-wrapper" v-if="isShowPage">
							<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
						</div>
					</div>
				</el-dialog>
			</div>
		</div>
	</div>
</template>

<script>
//合并帮助分类
export default({
	name: 'mergerHelpType',//组件名称，keep-alive缓存需要本参数
	template : '#mergerHelpType-template',
	inject:['reload'], 
	data : function data() {
		return {
			id:'',
			parentId:'',
			mergerTypeId :'',//选中待合并分类Id
			typeIdGroup:[],
			typeOptions:[],
			tableData: [],//表格内容
			
			
			typeIdList: [],//可选择Id集合
			dialogParentId:'',//分类选择父Id
			type_form:false,//是否显示分类选择表单
			dialogNavigation :'',
			
			
			
			
			totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			
			helpType :'',
			navigation :'',
			
			error : {
				typeId :'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.typeId != undefined && this.$route.query.typeId != ''){
			this.id = this.$route.query.typeId;
		}
		if(this.$route.query.parentId != undefined && this.$route.query.parentId != ''){
			this.parentId = this.$route.query.parentId;
		}
		
		this.queryType();
	},
	methods : {
		//查询分类
		queryType : function() {
			let _self = this;

			this.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'merger',
			    	typeId: _self.id,
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
			    			if(key == "helpType"){
			    				_self.helpType = mapData[key];
			    				_self.name = _self.helpType.name;
			    			}else if(key == "navigation"){
			    				_self.navigation = mapData[key];
			    			}
			    		}
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
		
		//加载分类
	    loadType: function() { 
	    	
	    	this.type_form = true;
	       	this.$refs.type_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	       	
	       	if(this.typeIdGroup.length ==0){
	       		this.mergerTypeId = "";
	       		this.typeOptions.length = 0;//清空	 		
	       	}
	       	
	       	
	       	//清空数据
			this.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.currentpage = 1;
			this.totalpage = 1;//服务器返回的long类型已转为String类型
			this.maxresult = 12;
			this.isShowPage = false;//显示分页栏
			this.queryParentId = '';
	       	
	        this.queryTypePage(1,'');
		},
		//删除分类回调
		removeType: function(val) { 
			this.mergerTypeId = "";
			this.typeOptions.length = 0;//清空	 
			this.typeIdGroup.length =0;
			this.queryParentId = '';
			this.typeIdList.length =0;
		},
		
		//问题分类分页
		page : function(page) {
			
			this.queryTypePage(page, this.dialogParentId);
		},
		//查询分类分页
		queryTypePage : function(page,parentId) {
			let _self = this;
			
			_self.tableData = [];
			_self.typeIdList = [];
			_self.dialogNavigation = '';
			
			_self.dialogParentId = parentId;
			_self.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'helpTypePageSelect',
			    	parentId : parentId,
			    	page : page
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
			    			if(key == "pageView"){
			    				let tagView = mapData[key];
					    		let tagList = tagView.records;
					    		if(tagList != null && tagList.length >0){
					    			for(let i = 0; i<tagList.length; i++){
					    				let tag = tagList[i];
					    				_self.typeIdList.push(tag.id);
					    			
					    			}
					    		
					    			_self.tableData = tagList;
					 
					    			_self.totalrecord = parseInt(tagView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = tagView.currentpage;
									_self.totalpage = parseInt(tagView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = tagView.maxresult;
									_self.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.dialogNavigation = mapData[key];
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
		//点击单元格选择
		cellExpandRow : function(row,column,event,cell) {
			if(column.label=="选择"){
				if(row.childNodeNumber ==0){
					this.typeIdGroup.push(row.id);
					
			        this.mergerTypeId  = row.id;
			        
					let obj =new Object();
					obj.value = row.id;
					obj.label = row.name;
					this.typeOptions.length = 0;//清空
					this.typeOptions.push(obj);
					
					
					//因为只允许选中一个，所以将已选择的清空
			    	if (Object.keys(this.typeIdGroup).length >1) {
			        	this.typeIdGroup.shift();//从Array 头部移除元素
			        }
			        
			        this.type_form = false;
				}
			}else if(column.label=="分类名称"){
				if(row.childNodeNumber >0){
					this.dialogParentId = row.id;
					this.queryTypePage(1,row.id);
				}
			}
		},
		
		
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.id != null){
				formData.append('typeId', _self.id);
				
			}
			if(_self.mergerTypeId != null && _self.mergerTypeId != ''){
				formData.append('mergerTypeId', _self.mergerTypeId);
				
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/helpType/manage?method=merger',
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
			    		_self.$router.push({
							path : '/admin/control/helpType/list',
							query:{
								parentId: _self.parentId,
								page:(_self.$route.query.page != undefined ? _self.$route.query.page:'')
							}
						});
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
	    }
	}
});

</script>