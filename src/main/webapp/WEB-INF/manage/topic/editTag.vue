<!-- 修改标签 -->
<template id="editTag-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/tag/list'})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="标签名称" :required="true" :error="error.name" >
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

					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					    <div class="form-error" v-text="error.tag"></div>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//修改标签
export default({
	name: 'editTag',//组件名称，keep-alive缓存需要本参数
	template : '#editTag-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			sort : 0,
			imagePath : '',
			fileList:[],//上传的文件列表
			localImageUrl: '',//本地图片地址 例如blob:http://127.0.0.1:8080/cfab3833-cbb0-4072-a576-feaf8fb19e5f
	        isImageViewer: false,//是否显示图片查看器
			error : {
				name :'',
				sort :'',
				tag :'',
				images: '',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		this.queryTag();
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
		//查询标签
		queryTag : function() {
			let _self = this;
			
			if(this.$route.query.tagId == undefined){
				_self.$message({
						duration :0,
			            showClose: true,
			            message: 'tagId不能为空',
			            type: 'error'
			        });
			    
			}
			
			
			this.$ajax.get('control/tag/manage', {
			    params: {
			    	method : 'edit',
			    	tagId: this.$route.query.tagId,
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
                        let _tag = null;
			    		for(let key in mapData){
			    			if(key == "tag"){
			    				let tag = mapData[key];
                                _tag = tag;
			    				_self.name = tag.name;
			    		        _self.sort = tag.sort;
			    			}else if(key == "imagePath"){
			    				imagePath = mapData[key];
			    				
			    			}
			    		}
			    		
			    		
                        if(_tag != null && _tag.image != null && _tag.image != ''){
                        	_self.imagePath = _tag.image;
			    			_self.fileList.push({
				    			name: _tag.image,
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
			if(this.$route.query.tagId != undefined){
				formData.append('tagId', this.$route.query.tagId);
				
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
		        url: 'control/tag/manage?method=edit',
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
							path : '/admin/control/tag/list',
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