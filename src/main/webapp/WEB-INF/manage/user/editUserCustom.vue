<!-- 修改用户自定义注册功能项 -->
<template id="editUserCustom-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/userCustom/list'})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="注册项名称" :required="true" :error="error.name">
						<el-row><el-col :span="12"><el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="是否必填" :required="true" :error="error.required">
						<el-switch v-model="required" ></el-switch>
					</el-form-item>
					<el-form-item label="后台是否可搜索" :required="true" :error="error.search">
						<el-switch v-model="search" ></el-switch>
						<div class="form-help" >只对'单选按钮''多选按钮''下拉列表'有效</div>
					</el-form-item>
					<el-form-item label="是否显示" :required="true" :error="error.visible">
						<el-switch v-model="visible" ></el-switch>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right"  :min="1" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="提示" :error="error.tip" >
						<el-input type="textarea" v-model="tip" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
					</el-form-item>
					
					
					<el-form-item label="选择类型" :required="true" :error="error.chooseType" >
						<el-select v-model="chooseType"  no-match-text="还没有类型" placeholder="选择类型" disabled>
							<el-option v-for="item in chooseTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>

					<el-form-item label="输入框的宽度" :error="error.size" v-if="chooseType==1">
						<el-row><el-col :span="4"><el-input v-model.trim="size" maxlength="10"></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="输入框字符的最大长度" :error="error.maxlength" v-if="chooseType==1">
						<el-row><el-col :span="4"><el-input v-model.trim="maxlength" maxlength="10"></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="字段值过滤" :error="error.fieldFilter" v-if="chooseType==1">
						<el-select v-model="fieldFilter" no-match-text="还没有内容" placeholder="选择字段值过滤">
							<el-option v-for="item in fieldFilterOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="正则表达式" :required="true" :error="error.regular"  v-if="chooseType==1 && fieldFilter==5">
						<el-input type="textarea" v-model="regular" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
					</el-form-item>
					
					
					
					<el-form-item v-for="(formOption, index) in formOptions" label="选项"  :prop="'formOptions.' + index + '.value'" v-if="chooseType==2 || chooseType==3 || chooseType==4" >
						<el-row :gutter="10">
							<el-col :span="6">
								<el-input v-model.trim="formOption.value" maxlength="40"></el-input>
							</el-col>
							<el-col :span="6">
								<el-button icon="el-icon-top" circle @click.prevent="moveUp(formOption)" title="上移"></el-button>
								<el-button icon="el-icon-bottom" circle @click.prevent="moveDown(formOption)" title="下移"></el-button>
								<el-button icon="el-icon-delete" circle @click.prevent="removeItem(formOption)" title="删除"></el-button>
							</el-col>
						</el-row>
						
					</el-form-item>
					<el-form-item v-if="chooseType==2 || chooseType==3 || chooseType==4">
						<div class="form-error" v-text="error.itemValue"></div>
						<div class="item-button"><el-button icon="el-icon-plus" @click="addItem">添加项</el-button></div>
					</el-form-item>
					
					<el-form-item label="可选择多个选项" :required="true" :error="error.multiple" v-if="chooseType==4">
						<el-switch v-model="multiple" ></el-switch>
					</el-form-item>
					<el-form-item label="下拉列表中可见选项的数目" :error="error.selete_size" v-if="chooseType==4">
						<el-row><el-col :span="4"><el-input v-model.trim="selete_size" maxlength="9" clearable="true" ></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="文本域内的可见行数" :error="error.rows" v-if="chooseType==5">
						<el-row><el-col :span="4"><el-input v-model.trim="rows" maxlength="9" clearable="true" ></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="文本域内的可见宽度" :error="error.cols" v-if="chooseType==5">
						<el-row><el-col :span="4"><el-input v-model.trim="cols" maxlength="9" clearable="true" ></el-input></el-col></el-row>
					</el-form-item>
					
					
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
			</div>
		</div>
	</div>
</template>

<script>
//修改用户自定义注册功能项
export default({
	name: 'editUserCustom',//组件名称，keep-alive缓存需要本参数
	template : '#editUserCustom-template',
	inject:['reload'], 
	data : function data() {
		return {
			userCustom :'',
			
			id :'',
			name :'',
			required :false,
			search :false,
			visible :true,
			sort : 1,
			tip :'',
			chooseType :1,
			size :'',
			maxlength :'',
			fieldFilter :0,
			regular :'',
			multiple :false,
			selete_size :'',
			rows :'',
			cols :'',
			
			//选择类型
			chooseTypeOptions: [{
		    	value: 1,
		        label: '输入框'
		    }, {
		        value: 2,
		        label: '单选按钮'
		    }, {
		        value: 3,
		        label: '多选按钮'
		    }, {
		        value: 4,
		        label: '下拉列表'
		    }, {
		          value: 5,
		          label: '文本域'
		    }],
		    fieldFilterOptions: [{
		    	value: 0,
		        label: '无'
		    }, {
		        value: 1,
		        label: '只允许输入数字'
		    }, {
		        value: 2,
		        label: '只允许输入字母'
		    }, {
		        value: 3,
		        label: '只允许输入数字和字母'
		    }, {
		        value: 4,
		        label: '只允许输入汉字'
		    }, {
		        value: 5,
		        label: '正则表达式过滤'
		    }],
		    //2.单选按钮  3.多选按钮 4.下拉列表 选择项
		    formOptions: [],
			
			error : {
				name :'',
				required :'',
				search:'',
				visible :'',
				sort :'',
				tip :'',
				chooseType :'',
				size :'',
				maxlength :'',
				fieldFilter :'',
				regular :'',
				multiple :'',
				_selete_size :'',
				_rows :'',
				_cols :'',
				itemValue:'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.id != undefined && this.$route.query.id != ''){
			this.id = this.$route.query.id;	
		}
		
		
		this.queryUserCustom();
	},
	methods : {
		/**
		 //选中类型
	    selectedType: function(val) { 
	   
	        this.tagId = val;
	    },
		//选中字段值过滤
	    selectedFieldFilter: function(val) { 
	   
	        this.tagId = val;
	    },**/
	     //查询用户自定义注册功能项
	    queryUserCustom() {
	        let _self = this;
			
			this.$ajax.get('control/userCustom/manage', {
			    params: {
			    	method : 'edit',
			    	id: _self.id,
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
			    			if(key == "userCustom"){
			    				let userCustom = mapData[key];
			    				_self.userCustom = userCustom;
			    				if(userCustom.name != null){
			    					_self.name = userCustom.name;
			    				}
			    				if(userCustom.required != null){
									_self.required = userCustom.required;
								}
								if(userCustom.search != null){
									_self.search  = userCustom.search;
								}
								if(userCustom.visible != null){
									_self.visible = userCustom.visible;
								}
								if(userCustom.sort != null){
									_self.sort  = userCustom.sort;
								}
								if(userCustom.tip != null){
									_self.tip = userCustom.tip;
								}
								if(userCustom.chooseType != null){
									_self.chooseType = userCustom.chooseType;
								}
								if(userCustom.size != null){
									_self.size = userCustom.size;
								}
								if(userCustom.maxlength != null){
									_self.maxlength = userCustom.maxlength;
								}
								if(userCustom.fieldFilter != null){
									_self.fieldFilter = userCustom.fieldFilter;
								}
								if(userCustom.regular != null){
									_self.regular = userCustom.regular;
								}
								if(userCustom.multiple != null){
									_self.multiple  = userCustom.multiple;
								}
								if(userCustom.selete_size != null){
									_self.selete_size = userCustom.selete_size;
								}
								if(userCustom.rows != null){
									_self.rows = userCustom.rows;
								}
								if(userCustom.cols != null){
									_self.cols  = userCustom.cols;
								}
			    				
			    				
			    			
			    			}else if(key == "itemValue_map"){
			    				let itemValue_map = mapData[key];
			    				for (let itemKey in itemValue_map) {  
			    					let item = {
								    	key: itemKey,
										value: itemValue_map[itemKey]
						            }
			    				
			    					_self.formOptions.push(item);
			    				 
					    			
					    	    }
			    			
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
		
	    //添加项
	    addItem() {
	        this.formOptions.push({
	        	key: '',
	        	value: ''
	        });
		},
		//删除项
	    removeItem(formOption) {
	        let index = this.formOptions.indexOf(formOption);
	        if (index !== -1) {
	        	this.formOptions.splice(index, 1);
	        }
		},
		//上移
	    moveUp(formOption) {
	        let index = this.formOptions.indexOf(formOption);
	        if (index >0) {
	        	let upData = this.formOptions[index - 1];
	        	this.formOptions.splice(index - 1, 1);
                this.formOptions.splice(index, 0, upData);
	        }
		},
		//下移
		moveDown(formOption) {
	        let index = this.formOptions.indexOf(formOption);
	        if ((index + 1) < this.formOptions.length) {
	        	let downData = this.formOptions[index + 1];
                this.formOptions.splice(index + 1, 1);
                this.formOptions.splice(index, 0, downData);
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
			formData.append('id', _self.id);
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.required != null){
				formData.append('required', _self.required);
			}
			if(_self.search != null){
				formData.append('search', _self.search);
			}
			if(_self.visible != null){
				formData.append('visible', _self.visible);
			}
			if(_self.sort != null){
				formData.append('_sort', _self.sort);
			}
			if(_self.tip != null){
				formData.append('tip', _self.tip);
			}
			if(_self.chooseType != null){
				formData.append('chooseType', _self.chooseType);
			}
			
			if(_self.chooseType != null && _self.chooseType != ''){
				formData.append('_size', _self.size);
				formData.append('_maxlength', _self.maxlength);
				formData.append('fieldFilter', _self.fieldFilter);
				
				if(_self.regular != null && _self.regular != ''){
					formData.append('regular', _self.regular);
				}
				
			}
			
			if(_self.formOptions != null && _self.formOptions.length >0){
				for(let i=0; i<_self.formOptions.length; i++){
					let formOption = _self.formOptions[i];
					formData.append('itemKey', formOption.key);
					formData.append('_itemValue', formOption.value);
					
				}
			}
			
			
			if(_self.multiple != null){
				formData.append('multiple', _self.multiple);
			}
			if(_self.selete_size != null){
				formData.append('_selete_size', _self.selete_size);
			}
			if(_self.rows != null){
				formData.append('_rows', _self.rows);
			}
			if(_self.cols != null){
				formData.append('_cols', _self.cols);
			}

			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/userCustom/manage?method=edit',
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
							path : '/admin/control/userCustom/list',
						});
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
	    }
	}
});

</script>