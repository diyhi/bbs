<!-- 修改员工 -->
<template id="editStaff-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/staff/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="账号" :error="error.userAccount">
						{{userAccount}}
					</el-form-item>
					<el-form-item label="姓名" :error="error.fullName">
						<el-row><el-col :span="12"><el-input v-model.trim="fullName" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="呢称" :error="error.nickname">
	                    <el-row><el-col :span="12"><el-input v-model.trim="nickname" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
	                </el-form-item>
					<el-form-item label="密码" :error="error.userPassword">
						<el-row><el-col :span="12"><el-input v-model.trim="userPassword" type="password" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
						<div class="form-help" >不修改请留空</div>
					</el-form-item>
					<el-form-item label="重复密码" :error="error.repeatPassword">
						<el-row><el-col :span="12"><el-input v-model.trim="repeatPassword" type="password" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="超级管理员" :required="true" :error="error.issys" v-if="isSysAdmin == true">
						<el-radio-group v-model="issys">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="头像" :error="error.avatarName">
	                    <el-col :span="4">
	                        <el-image class="avatar" v-if="avatarName != null && avatarName != ''" style="width: 120px; height: 120px;" fit="contain" :src="avatarPath + avatarName" :preview-src-list="[avatarPath+avatarName]" hide-on-click-modal ></el-image>
	                    
	                        <span v-else>
	                            <div ref="confirmAvatarRef"  class="confirmAvatar-preview" v-show="confirmAvatar_file != undefined && Object.keys(confirmAvatar_file).length > 0" ></div> 
	                            <el-avatar class="defaultAvatar" v-show="confirmAvatar_file == undefined || Object.keys(confirmAvatar_file).length == 0" :size="120" shape="square" icon="el-icon-user-solid"/> 
	                        </span>
	                    </el-col>
	                    <el-col :span="20">
	                        <el-button type="primary" plain @click="settingAvatarUI()" style="margin-left: 15px;">设置</el-button>
	                    </el-col>
	                </el-form-item>
					<el-form-item label="备注"  :error="error.userDesc"  >
						<el-input v-model.trim="userDesc" maxlength="150" clearable="true" show-word-limit></el-input>					
					</el-form-item>
					<el-form-item label="职位" :error="error.userDuty">
						<el-row><el-col :span="12"><el-input v-model.trim="userDuty" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="是否使用" :required="true" :error="error.enabled">
						<el-radio-group v-model="enabled">
						    <el-radio :label="true">启用</el-radio>
						    <el-radio :label="false">停用</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="用户角色" >
						<el-checkbox-group v-model="sysRolesId">
						    <el-checkbox :label="roles.id" :disabled="roles.logonUserPermission == false" :checked="roles.selected" v-for="roles in rolesList">{{roles.name}}</el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					
					
					
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
			</div>
			<div class="settingAvatarModule">
				<el-dialog title="设置头像" width="630px" v-model="popup_settingAvatar" @close="closeChangeAvatarWindow">
					<div class="original-box">
						<img ref="originalImageRef" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" alt="原图"/>
					</div>
					<div class="preview-pane-square">
						<div ref="previewSquareRef" class="preview-square"></div> 
				    </div>
				    <div class="preview-pane-round">
						<div ref="previewRoundRef" class="preview-round"></div> 
				    </div>
			        <div class="bottomInfo">
			          <div class="button-box">
			               	<div class="container">
								<div>
									<el-upload ref="selectImageRef" action="#" :auto-upload="false" :show-file-list="false" :on-change="handleChange" :accept="'image/*'">
										<el-button type="primary" class="selectImage" plain >选择图片</el-button>
									</el-upload>
								</div>
								<div>
									<el-button type="primary" class="settingImage" @click="confirmAvatar" :disabled="submitForm_disabled">确定</el-button>
								</div>
							</div>
			            </div>
			        </div>
				</el-dialog>
			</div>
		</div>
	</div>
</template>

<script>
//修改员工
export default({
	name: 'editStaff',//组件名称，keep-alive缓存需要本参数
	template : '#editStaff-template',
	inject:['reload'], 
	data : function data() {
		return {
			isSysAdmin:false,//是否是超级管理员
			rolesList:'',
			
			userId :'',
			userAccount :'',
			fullName :'',
			nickname :'',
			avatarPath :'',
        	avatarName :'',
			userPassword :'',
			repeatPassword :'',
			issys :false,
			userDesc :'',
			userDuty :'',
			enabled :true,
			sysRolesId:[],
			
			popup_settingAvatar:false,//是否弹出更换头像窗口
	        avatarCropper:null,//头像Cropper
	
	
	        confirmAvatar_file : {},
	        confirmAvatar_width : 0,
	        confirmAvatar_height : 0,
	        confirmAvatar_x : 0,
	        confirmAvatar_y : 0,
	        confirmAvatar_cropBoxData:{},//裁剪框数据
			
			
			error : {
				fullName :'',
				nickname :'',
				avatarName :'',
				userPassword :'',
				repeatPassword :'',
				issys :'',
				userDesc :'',
				userDuty :'',
				enabled :'',
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.userId != undefined && this.$route.query.userId != ''){
			this.userId = this.$route.query.userId;
		}
		
		
		
		this.queryEditStaff();
	},
	methods : {
		 //查询修改员工
	    queryEditStaff: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/staff/manage', {
			    params: {
			    	method : 'editStaff',
			    	userId : _self.userId,
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
			    			if(key == "isSysAdmin"){
			    				_self.isSysAdmin = mapData[key];
			    			}else if(key == "sysUsers"){
			    				let sysUsers = mapData[key];
			    				_self.userAccount= sysUsers.userAccount;
			    				_self.fullName = sysUsers.fullName;
			    				_self.issys = sysUsers.issys;
			    				_self.userDesc = sysUsers.userDesc;
			    				_self.userDuty = sysUsers.userDuty;
			    				_self.enabled  = sysUsers.enabled;
			    				_self.nickname = sysUsers.nickname;
                                _self.avatarPath = sysUsers.avatarPath;
                                _self.avatarName = sysUsers.avatarName;
			    			}else if(key == "sysRolesList"){
			    				_self.rolesList = mapData[key];
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
		
		
		//设置头像UI
		settingAvatarUI : function() {
			let _self = this;
		
			_self.popup_settingAvatar=true;
	    
	        if(_self.avatarCropper != null){
	            _self.avatarCropper.destroy();//销毁裁剪器并从图像中删除实例。
	            _self.avatarCropper = null;
	        }
	        _self.$nextTick(() => {
	
	            _self.createCropper();
	        });
		},
		
		//创建Cropper
 		createCropper: function() { 
 			let _self = this;
 			_self.avatarCropper = new Cropper(_self.$refs.originalImageRef, {	
    			viewMode: 1,//视图模式
		        dragMode: 'crop',//拖拽模式
		        initialAspectRatio: 1,//定义裁切框的初始宽高比。默认情况下，它与画布（图像包装器）的纵横比相同。这个值只有在aspectRatio值不进行设置的时候生效
		        aspectRatio: 1,//裁剪框的宽高比
		        preview:[ _self.$refs.previewSquareRef,
	                        _self.$refs.previewRoundRef],//添加额外的元素(容器)以供预览
		        background: true,//显示容器的网格背景
		        autoCropArea: 0.6,//定义自动裁剪面积大小(百分比)和图片进行对比。 就是裁剪框显示的大小
		        zoomOnWheel: false,//是否可以通过移动鼠标来放大图像
		        
		       
		        minContainerWidth:400,
                minContainerHeight:400,
                
                ready: function() {
                    if(Object.keys(_self.confirmAvatar_cropBoxData).length != 0){
                        _self.avatarCropper.setCropBoxData(_self.confirmAvatar_cropBoxData);//设置裁剪框
                    }
                }
			});
 			
 	    },
 	  
 	  	//关闭更换头像弹出框
 	  	closeSettingAvatarWindow : function() {
	        this.popup_settingAvatar=false;
	        
	        if(this.avatarCropper != null){
	            this.avatarCropper.destroy();//销毁裁剪器并从图像中删除实例。
	            this.avatarCropper = null;
	        }
	    },
	    
	    //处理上传图片
 		handleChange(file, fileList) {
 	    	let _self = this;
 	    	if (fileList.length > 1) {
	      		fileList.splice(0, 1);
			}
 	    	var reader = new FileReader();
 	    	//readAsDataURL方法可以将File对象转化为data:URL格式的字符串（base64编码）
 	        reader.readAsDataURL(file.raw);
 	        reader.onload = (e)=>{
 	            let dataURL = reader.result;
 	            //将img的src改为刚上传的文件的转换格式
 	           	_self.$refs.originalImageRef.src = dataURL;  
 
 	          
 	           	if(_self.avatarCropper != ""){
 	           		_self.avatarCropper.destroy();//销毁裁剪器并从图像中删除实例。
 	           		_self.avatarCropper = "";
 	 			}
 	          
 	          	_self.$nextTick(() => {
 	          		_self.createCropper();
 	        	});
 	        }

     	},
	    
	    
	    //确定头像
	    confirmAvatar : function() {
	    	let _self = this;
	        if(_self.avatarCropper == "" || _self.$refs.originalImageRef.src == "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"){
  				_self.$message({
		            showClose: true,
		            message: "请先选择图片",
		            type: 'error'
		        });
 				return;
 			}
	
	        let fileList = _self.$refs.selectImageRef.uploadFiles;
  			if(fileList != null && fileList.length >0){
  				_self.confirmAvatar_file = fileList[0].raw;
  			}
			_self.avatarName = '';
	
	        // - x裁切框距离左边的距离 
	        // - y裁切框距离顶部的距离 
	        // - width裁切框的宽度 
	        // - height裁切框的高度 
	        // - rotate裁切框的旋转的角度 
	        // - scaleX缩放图像的横坐标 
	        // - scaleY缩放图像的纵坐标 
	        let dataObject = _self.avatarCropper.getData(true);//返回裁剪区域基于原图片!原尺寸!的位置和尺寸 rounded默认为false 表示是否显示四舍五入后的数据
	        
	        _self.confirmAvatar_width = dataObject.width;
	        _self.confirmAvatar_height = dataObject.height;
	        _self.confirmAvatar_x = dataObject.x;
	        _self.confirmAvatar_y = dataObject.y;
	
	        let firstChild = _self.$refs.previewRoundRef.children[0];
	
			_self.$nextTick(() => {
 	        	 _self.$refs.confirmAvatarRef.innerHTML = firstChild.outerHTML
	
		        _self.confirmAvatar_cropBoxData = _self.avatarCropper.getCropBoxData();
		
		
		        _self.popup_settingAvatar=false;
 	        });
			
	       
	    },
	    
	    
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			if(getType(_self.error[key]) == 'map'){
    				_self.error[key].clear();
    			}else{
    				_self.error[key] = "";
    			}
    	    }
			let formData = new FormData();
			
			formData.append('userId', _self.userId);
			formData.append('fullName', _self.fullName);
			if(_self.nickname != null){
				formData.append('nickname', _self.nickname);
			}
			formData.append('userPassword', _self.userPassword);
			formData.append('repeatPassword', _self.repeatPassword);
			formData.append('issys', _self.issys);
			formData.append('userDesc', _self.userDesc);
			formData.append('userDuty', _self.userDuty);
			formData.append('enabled', _self.enabled);
		

			
			for(let i=0; i<_self.sysRolesId.length; i++){
	    		let rolesId = _self.sysRolesId[i];
	    		formData.append('sysRolesId', rolesId);
	    	}
	    	
	    	if(_self.confirmAvatar_file != undefined && Object.keys(_self.confirmAvatar_file).length > 0){
		    	formData.append('file', _self.confirmAvatar_file);
		    	formData.append('width', _self.confirmAvatar_width);
	  			formData.append('height', _self.confirmAvatar_height);
	  			formData.append('x', _self.confirmAvatar_x); 
	  			formData.append('y', _self.confirmAvatar_y);
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/staff/manage?method=editStaff',
		        timeout: 0,// 定义请求超时时间
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
							path : '/admin/control/staff/list',
							query:{ page:(_self.$route.query.page != undefined ? _self.$route.query.page:'')}
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