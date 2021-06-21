<!-- 添加角色 -->
<template id="addRoles-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/roles/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userRoleModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="角色名" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="40"></el-input>
					</el-form-item>
					<el-form-item label="备注"  :error="error.remarks"  >
						<el-input type="textarea" v-model="remarks" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
					</el-form-item>
					<el-form-item label="权限" :error="error.permission" >
						<el-table ref="table" :data="permissionGroupList" @select="handleSelectionSelect" tooltip-effect="dark" default-expand-all="true"  :show-header="false" style="width: 100%" empty-text="没有内容">
							<el-table-column type="selection" :selectable="checkSelectable"></el-table-column>
							<el-table-column label="模块" >
								<template #default="scope">	
									{{scope.row.module}}
						    	</template>
							</el-table-column>
							<el-table-column type="expand" label="权限" ><!-- 因为type="expand"方式导致索引不连续,所以使用数据中设置的索引 -->
								<template #default="scope">
						        	<div class="userResource-box">
						        		<el-checkbox v-model="permissionIdGroup[scope.row.index][index]" :disabled="permissionObject.logonUserPermission == false" v-for="(permissionObject,index) in scope.row.permissionObjectList" @change="handleUserResourceChange(scope.row.index)">{{permissionObject.remarks}}</el-checkbox>
									</div>
						    	</template>
							</el-table-column>
						</el-table>
						<div class="form-help" >如果不选择[系统]--[后台框架]，则无法进入管理后台首页</div>
					</el-form-item>
					<el-form-item>
					    <el-button type="primary" class="submitButton"  @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//添加角色
export default({
	name: 'addRoles',//组件名称，keep-alive缓存需要本参数
	template : '#addRoles-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			remarks :'',
			permissionGroupList :'',
			permissionIdGroup:[],//权限Id组(二维数组)
			
			error : {
				name :'',
				remarks :'',
				permission :'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryAddRole();
	},
	methods : {
		//处理全选单元格
		handleSelectionSelect: function(selection, row) {
        	//是否选中
        	let isSelected = selection.length && selection.indexOf(row) !== -1;// true就是选中，0或者false是取消选中
        	
        	for (let i = 0; i <this.permissionGroupList.length; i++) {
				let permissionGroup = this.permissionGroupList[i];
				if(permissionGroup.module == row.module){
					
					if(isSelected == true){//选中
						let permissionId_arr = this.permissionIdGroup[i];
						if(permissionId_arr != null && permissionId_arr.length >0){
							for (let j = 0; j <permissionId_arr.length; j++) {
								permissionId_arr.splice(j,1,true);
							}
						}
					
					}else{//取消选中
						let permissionId_arr = this.permissionIdGroup[i];
						if(permissionId_arr != null && permissionId_arr.length >0){
							for (let j = 0; j <permissionId_arr.length; j++) {
								permissionId_arr.splice(j,1,false);
							}
						}
					}
					
					break;
				}					
			}
		},
	
	
		//处理用户权限选择
		handleUserResourceChange: function(permissionGroup_index){
			let permissionId_arr = this.permissionIdGroup[permissionGroup_index];
			if(permissionId_arr != null && permissionId_arr.length >0){
				let count = 0;
				for (let i = 0; i <permissionId_arr.length; i++) {
					if(permissionId_arr[i] == true){
						count++;
					}
				}
				if(count == permissionId_arr.length){//如果全选
					this.$refs.table.toggleRowSelection(this.permissionGroupList[permissionGroup_index],true);
				}else{
					this.$refs.table.toggleRowSelection(this.permissionGroupList[permissionGroup_index],false);
				}
			}
		},
		
		//处理用户权限全选
		handlePermissionSelectAll: function(){
			if(this.permissionGroupList != null && this.permissionGroupList.length >0){
				
				for (let i = 0; i <this.permissionGroupList.length; i++) {
					let permissionGroup = this.permissionGroupList[i];
					let count = 0;
					if(permissionGroup.permissionObjectList != null && permissionGroup.permissionObjectList.length >0){
						for (let j = 0; j <permissionGroup.permissionObjectList.length; j++) {
							let permissionObject = permissionGroup.permissionObjectList[j];
							if(permissionObject.selected){
								count++;
							}
						}
						if(count == permissionGroup.permissionObjectList.length){
							this.$refs.table.toggleRowSelection(this.permissionGroupList[permissionGroup.index],true);
						}
					}
				}
			}
		},
		//处理选择框禁止选中(若返回为 true， 则可以选中，否则禁止选中)
		checkSelectable: function(row) {
			let count = 0;
			if(row.permissionObjectList != null && row.permissionObjectList.length >0){
				for (let j = 0; j <row.permissionObjectList.length; j++) {
					let permissionObject = row.permissionObjectList[j];
					if(permissionObject.logonUserPermission == false){
						count++;
					}
				}
				if(count == row.permissionObjectList.length){
					return false;
				}		
			}
			return true;
		},
		
		
		 //查询添加角色 
	    queryAddRole: function(){
	        let _self = this;
			
			_self.permissionIdGroup.length = 0;
			
			_self.$ajax.get('control/acl/manage', {
			    params: {
			    	method : 'addRoles',
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
			    		let permissionObjectMap = returnValue.data;
			    		
			    		let permissionGroupList = [];
			    		
			    		
			    		let index =0;
		    			for(let permissionObject in permissionObjectMap){
		    				let permissionGroup =new Object();
    	    				permissionGroup.module = permissionObject;
    	    				permissionGroup.index = index;//因为表格使用type="expand"方式展示时，scope.$index获取的索引不连续,所以在数据里直接设置索引
		    				permissionGroup.permissionObjectList = permissionObjectMap[permissionObject];
		    				permissionGroupList.push(permissionGroup);
		    				
		    				
		    				//定义权限Id数组
							let permissionId_array = [];
							if(permissionGroup.permissionObjectList != null && permissionGroup.permissionObjectList.length >0){
								for (let j = 0; j <permissionGroup.permissionObjectList.length; j++) {
									let permissionObject = permissionGroup.permissionObjectList[j];
									permissionId_array.push(permissionObject.selected);
								}
							}
							_self.permissionIdGroup[index] = permissionId_array;//把数组permissionId_array作为permissionIdGroup数组的元素传入
		    				
		    				index++;
		    			}
			    		
			    		_self.permissionGroupList = permissionGroupList;
			    		_self.$nextTick(function(){
			    			_self.handlePermissionSelectAll();
			    		})
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			_self.error[key] = errorMap[key];
			    	    }
			    	}
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
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
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			formData.append('remarks', _self.remarks);
			
			if(_self.permissionIdGroup != null && _self.permissionIdGroup.length >0){
				for (let i = 0; i <_self.permissionIdGroup.length; i++) {
					let permissionId_arr = _self.permissionIdGroup[i];
			    	if(permissionId_arr != null && permissionId_arr.length >0){
			    		for (let j = 0; j <permissionId_arr.length; j++) {
			    			if(permissionId_arr[j]){
			    				let permissionGroup = _self.permissionGroupList[i];
			    				if(permissionGroup.permissionObjectList != null && permissionGroup.permissionObjectList.length >0){
			    					let permissionObject = permissionGroup.permissionObjectList[j];
			    					
			    					formData.append('sysPermissionId',permissionObject.permissionId);
			    					
			    				}
			    				
			    				
			    			}
			    		}
			    	}	
			    }
			}
			
			
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/acl/manage?method=addRoles',
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
							path : '/admin/control/roles/list'
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
