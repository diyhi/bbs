<!-- 修改模板 -->
<template id="editTemplate-template">
	<div>
		<div class="main" >
			<div class="nav-breadcrumb">
			
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item>修改模板</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="模板名称" :required="true" :error="error.name">
						<el-row><el-col :span="12"><el-input v-model.trim="name" maxlength="100" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="模板目录" :required="true" :error="error.dirName">
						<el-row><el-col :span="12">{{dirName}}</el-col></el-row>
					</el-form-item>
					<el-form-item label="是否验证CSRF(跨站请求伪造)">
						<el-switch v-model="verifyCSRF" ></el-switch>
						<div class="form-help" >前后端一体模板必需启用CSRF(跨站请求伪造)验证，前后端分离模板可以关闭本项验证</div>
					</el-form-item>
					<el-form-item label="模板简介"  :error="error.introduction"  >
						<el-input type="textarea" v-model="introduction" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
					</el-form-item>
					<el-form-item label="缩略图" :error="error.thumbnailSuffix">
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
					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//修改模板
export default({
	name: 'editTemplate',//组件名称，keep-alive缓存需要本参数
	template : '#editTemplate-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			dirName : '',
			introduction : '',
			verifyCSRF : true,
			imagePath : '',
			
			fileList:[],//上传的文件列表
			localImageUrl: '',//本地图片地址 例如blob:http://127.0.0.1:8080/cfab3833-cbb0-4072-a576-feaf8fb19e5f
	        isImageViewer: false,//是否显示图片查看器
			error : {
				name :'',
				dirName :'',
				introduction : '',
				thumbnailSuffix: '',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		
		this.queryTemplate();
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
	
	
	
		//查询模板
		queryTemplate : function() {
			let _self = this;
			
			_self.$ajax.get('control/template/manage', {
			    params: {
			    	method : 'edit',
			    	dirName: _self.dirName,
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
			    		let templates = returnValue.data;
			    		if(templates != null){
			    			_self.name =templates.name;
							_self.introduction = templates.introduction;
							_self.verifyCSRF = templates.verifyCSRF;
			    			if(templates.thumbnailSuffix != null && templates.thumbnailSuffix != ''){
			    				_self.imagePath = "common/"+ templates.dirName +"/templates."+templates.thumbnailSuffix;
			    				_self.fileList.push({
				    				name: "/templates."+templates.thumbnailSuffix,
				    				url: _self.imagePath
				    			});
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
			if(_self.name != null){
				formData.append('name', _self.name);
				
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
				
			}
			if(_self.introduction != null){
				formData.append('introduction', _self.introduction);
				
			}
			if(_self.verifyCSRF != null){
				formData.append('verifyCSRF', _self.verifyCSRF);
			}
			
			if(_self.imagePath != null){
				formData.append('imagePath', _self.imagePath);
			}
			
			
			let fileList = _self.$refs.uploadImg.uploadFiles;
			if(fileList != null && fileList.length >0){
				for(let i=0; i<fileList.length; i++){
					let file = fileList[i];
					if(file.raw != undefined){
						formData.append('uploadImage', file.raw);
					}
					
				}
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/template/manage?method=edit',
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
							path : '/admin/control/template/list',
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