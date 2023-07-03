<!-- 修改问题标签 -->
<template id="editQuestionTag-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item @click="$router.push({path: '/admin/control/questionTag/list'});">全部标签</el-breadcrumb-item>
					<el-breadcrumb-item v-for="(value, key) in navigation" @click="$router.push({path: '/admin/control/questionTag/list',query:{parentId:key}});">{{value}}</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/questionTag/list',query:{parentId:($route.query.sourceParentId != undefined ? $route.query.sourceParentId:''), page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="父标签名称" v-if="parentTag != ''">
						{{parentTag.name}}
					</el-form-item>
					<el-form-item label="分类名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>


					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//修改问题标签
export default({
	name: 'editQuestionTag',//组件名称，keep-alive缓存需要本参数
	template : '#editQuestionTag-template',
	inject:['reload'], 
	data : function data() {
		return {
			id:'',
			parentId:'',
			name :'',
			sort : 0,
			
			parentTag :'',
			navigation :'',
			
			error : {
				
				name :'',
				sort :'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.tagId != undefined && this.$route.query.tagId != ''){
			this.id = this.$route.query.tagId;
		}
		if(this.$route.query.parentId != undefined && this.$route.query.parentId != ''){
			this.parentId = this.$route.query.parentId;
		}
		
		this.queryQuestionTag();
	},
	methods : {
		//查询标签
		queryQuestionTag : function() {
			let _self = this;

			this.$ajax.get('control/questionTag/manage', {
			    params: {
			    	method : 'edit',
			    	questionTagId: _self.id,
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
			    			if(key == "tag"){
			    				let tag = mapData[key];
			    				_self.name = tag.name;
			    				_self.sort = tag.sort;
			    			}else if(key == "parentTag"){
			    				_self.parentTag = mapData[key];
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
				formData.append('questionTagId', _self.id);
				
			}
			if(_self.name != null){
				formData.append('name', _self.name);
				
			}
			if(_self.sort != null){
				formData.append('sort', _self.sort);
				
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/questionTag/manage?method=edit',
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
			    		
			    		let parentId = _self.parentId;
                        if(_self.parentId == '0'){
                            parentId = ''
                        }
			    		_self.$router.push({
							path : '/admin/control/questionTag/list',
							query:{
								parentId: parentId,
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