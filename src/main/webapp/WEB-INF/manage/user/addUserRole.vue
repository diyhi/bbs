<!-- 添加角色 -->
<template id="addUserRole-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/userRole/list/'})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userRoleModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="40"></el-input>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >如果选中"默认角色" 则优先级最高，本排序参数无效</div>
					</el-form-item>
					<el-form-item label="权限" :error="error.userResourceGroup" >
						<el-table ref="table" :data="userResourceGroupList" @select="handleSelectionSelect" tooltip-effect="dark" default-expand-all="true"  :show-header="false" style="width: 100%" empty-text="没有内容">
							<el-table-column type="selection" ></el-table-column>
							<el-table-column label="名称" >
								<template #default="scope">	
									{{scope.row.name}}
									<span class="form-help" style="margin-left: 6px" v-if="scope.row.type == 20">{{scope.row.tagName}}</span>
									
						    	</template>
							</el-table-column>
							<el-table-column type="expand" label="资源组" ><!-- 因为type="expand"方式导致索引不连续,所以使用数据中设置的索引 -->
								<template #default="scope">
						        	<div class="userResource-box">
						        		
						        		<el-checkbox v-model="resourceCodeGroup[scope.row.index][index]" v-for="(userResource,index) in scope.row.userResourceList" @change="handleUserResourceChange(userResource,scope.row.index,index)">{{userResource.name}}</el-checkbox>
									</div>
						    	</template>
							</el-table-column>
						</el-table>
						<div class="form-help" >如果默认角色允许‘查看话题内容’，则未登录用户也可以查看</div>
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
	name: 'addUserRole',//组件名称，keep-alive缓存需要本参数
	template : '#addUserRole-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			sort :0,
			userResourceGroupList :'',
			resourceCodeGroup:[],//资源代码组(二维数组)
			
			error : {
				name :'',
				sort :'',
				userResourceGroup :'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryAddUserRole();
	},
	methods : {
		//处理全选单元格
		handleSelectionSelect: function(selection, row) {
        	//是否选中
        	let isSelected = selection.length && selection.indexOf(row) !== -1;// true就是选中，0或者false是取消选中
        	
        	for (let i = 0; i <this.userResourceGroupList.length; i++) {
				let userResourceGroup = this.userResourceGroupList[i];
				if(userResourceGroup.code == row.code && userResourceGroup.tagId == row.tagId){
					
					if(isSelected == true){//选中
						let resourceCode_arr = this.resourceCodeGroup[i];
						if(resourceCode_arr != null && resourceCode_arr.length >0){
							for (let j = 0; j <resourceCode_arr.length; j++) {
								resourceCode_arr.splice(j,1,true);
							}
						}
					
					}else{//取消选中
						let resourceCode_arr = this.resourceCodeGroup[i];
						if(resourceCode_arr != null && resourceCode_arr.length >0){
							for (let j = 0; j <resourceCode_arr.length; j++) {
								resourceCode_arr.splice(j,1,false);
							}
						}
					}
					
					break;
				}					
			}
		},
	
	
		//处理用户资源选择
		handleUserResourceChange: function(userResource,userResourceGroup_index,userResource_index){
			let resourceCode_arr = this.resourceCodeGroup[userResourceGroup_index];
			if(resourceCode_arr != null && resourceCode_arr.length >0){
				let count = 0;
				for (let i = 0; i <resourceCode_arr.length; i++) {
					if(resourceCode_arr[i] == true){
						count++;
					}
				}
				if(count == resourceCode_arr.length){//如果全选
					this.$refs.table.toggleRowSelection(this.userResourceGroupList[userResourceGroup_index],true);
				}else{
					this.$refs.table.toggleRowSelection(this.userResourceGroupList[userResourceGroup_index],false);
				}
			}
			
			
			
		
		},
		
		 //查询添加用户角色 
	    queryAddUserRole: function(){
	        let _self = this;
			
			_self.resourceCodeGroup.length = 0;
			
			_self.$ajax.get('control/userRole/manage', {
			    params: {
			    	method : 'add',
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
			    			if(key == "userResourceGroupList"){
			    				let userResourceGroupList = mapData[key];
                                if(userResourceGroupList != null && userResourceGroupList.length >0){
					    			for (let i = 0; i <userResourceGroupList.length; i++) {
										let userResourceGroup = userResourceGroupList[i];
										
										
										//定义资源代码数组
										let resourceCode_array = [];
											
										if(userResourceGroup.userResourceList != null && userResourceGroup.userResourceList.length >0){
											for (let j = 0; j <userResourceGroup.userResourceList.length; j++) {
												let userResource = userResourceGroup.userResourceList[j];
												resourceCode_array.push(userResource.selected);
											}
										}
										_self.resourceCodeGroup[i] = resourceCode_array;//把数组resourceCode_array作为resourceCodeGroup数组的元素传入
									
										userResourceGroup.index = i;//因为表格使用type="expand"方式展示时，scope.$index获取的索引不连续,所以在数据里直接设置索引
									}
					    		}
					    		_self.userResourceGroupList = userResourceGroupList;
			    			}
			    		}
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
			if(_self.name != null && _self.name != ''){
				formData.append('name', _self.name);
				
			}
			formData.append('sort', _self.sort);
			
			if(_self.resourceCodeGroup != null && _self.resourceCodeGroup.length >0){
				for (let i = 0; i <_self.resourceCodeGroup.length; i++) {
					let resourceCode_arr = _self.resourceCodeGroup[i];
			    	if(resourceCode_arr != null && resourceCode_arr.length >0){
			    		for (let j = 0; j <resourceCode_arr.length; j++) {
			    			if(resourceCode_arr[j]){
			    				let userResourceGroup = _self.userResourceGroupList[i];
			    				if(userResourceGroup.userResourceList != null && userResourceGroup.userResourceList.length >0){
			    					let userResource = userResourceGroup.userResourceList[j];
			    					let tagId = (userResourceGroup.tagId == null ? '' : userResourceGroup.tagId);
			    					
			    					formData.append('resourceCode',userResourceGroup.code +"_"+tagId+"_"+userResource.code);
			    					
			    				}
			    				
			    				
			    			}
			    		}
			    	}	
			    }
			}
			
			
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/userRole/manage?method=add',
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
							path : '/admin/control/userRole/list'
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
