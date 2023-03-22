<!-- 修改帮助分类 -->
<template id="editHelpType-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item @click="$router.push({path: '/admin/control/helpType/list'});">全部分类</el-breadcrumb-item>
					<el-breadcrumb-item v-for="(value, key) in navigation" @click="$router.push({path: '/admin/control/helpType/list',query:{parentId:key}});">{{value}}</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/helpType/list',query:{parentId:($route.query.sourceParentId != undefined ? $route.query.sourceParentId:''), page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="分类名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="缩略图" :error="error.images">
						<el-upload action="#" ref="uploadImg" list-type="picture-card" :file-list="fileList" :auto-upload="false" :on-change="handleChange" :accept="'image/*'">
						    <template #default>
						    	<i class="el-icon-plus"></i>
						    </template>
						    <template #file="{file}">
						    	<div>
						        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
						        <span class="el-upload-list__item-actions">
						        	<span class="el-upload-list__item-preview" @click="openImagePreview(file)">
						            	<i class="el-icon-zoom-in"></i>
						        	</span>
						        	<span v-if="!disabled" class="el-upload-list__item-delete" @click="handleImageRemove(file)">
						            	<i class="el-icon-delete"></i>
						        	</span>
						        </span>
						    	</div>
						    </template>
						</el-upload>
						<!-- 插入透明1像素图片占位 -->
						<el-image ref="imageViewer" style="width: 0px;height: 0px;" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" :preview-src-list="[localImageUrl]" hide-on-click-modal />
					</el-form-item>
					<el-form-item label="描述"  :error="error.description"  >
						<el-input type="textarea" v-model="description" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
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
//修改帮助分类
export default({
	name: 'editHelpType',//组件名称，keep-alive缓存需要本参数
	template : '#editHelpType-template',
	inject:['reload'], 
	data : function data() {
		return {
			id:'',
			parentId:'',
			name :'',
			sort : 0,
			description :'',
			
			helpType :'',
			navigation :'',
			imagePath : '',
			fileList:[],//上传的文件列表
			localImageUrl: '',//本地图片地址 例如blob:http://127.0.0.1:8080/cfab3833-cbb0-4072-a576-feaf8fb19e5f
	        isImageViewer: false,//是否显示图片查看器
			error : {
				
				name :'',
				sort :'',
				images: '',
				description :'',
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
		//处理上传图片
		handleChange(file, fileList) {
			if (fileList.length > 1) {
	      		fileList.splice(0, 1);
			}
			this.imagePath = "";
    	},
		//处理删除图片
		handleImageRemove(file) {
			let fileList = this.$refs.uploadImg.uploadFiles;
			let index = fileList.findIndex( fileItem => {
				return fileItem.uid === file.uid
			});
			fileList.splice(index, 1);
			this.imagePath = "";
	    },
	    //打开图片预览
		openImagePreview(file) {
			
	        this.localImageUrl = file.url;
	        this.$refs.imageViewer.showViewer = true;
		},
	
		//查询分类
		queryType : function() {
			let _self = this;

			this.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'edit',
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
			    		let imagePath = "";
			    		for(let key in mapData){
			    			if(key == "helpType"){
			    				_self.helpType = mapData[key];
			    				_self.name = _self.helpType.name;
			    				_self.sort = _self.helpType.sort;
			    				_self.description = _self.helpType.description;
			    			}else if(key == "navigation"){
			    				_self.navigation = mapData[key];
			    			}else if(key == "imagePath"){
			    				imagePath = mapData[key];
			    				
			    			}
			    		}
			    		
			    		if(_self.helpType.image != null && _self.helpType.image != ''){
			    			_self.imagePath = _self.helpType.image;
			    			_self.fileList.push({
				    			name: _self.helpType.image,
				    			url: imagePath
				    		});
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
				formData.append('typeId', _self.id);
				
			}
			if(_self.name != null){
				formData.append('name', _self.name);
				
			}
			if(_self.sort != null){
				formData.append('sort', _self.sort);
				
			}
			if(_self.imagePath != null){
				formData.append('imagePath', _self.imagePath);
			}
			if(_self.description != null){
				formData.append('description', _self.description);
				
			}
			let fileList = _self.$refs.uploadImg.uploadFiles;
			if(fileList != null && fileList.length >0){
				for(let i=0; i<fileList.length; i++){
					let file = fileList[i];
					if(file.raw != undefined){
						formData.append('images', file.raw);
					}
					
				}
			}
			_self.$ajax({
		        method: 'post',
		        url: 'control/helpType/manage?method=edit',
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