<!-- 修改举报 -->
<template id="editReport-template">
	<div>
		<div class="main" >
			<div class="navbar">
	            <el-button type="primary" plain @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
	        
	        </div>
			<div class="data-form label-width-blank" >
	            <el-form label-width="auto" @submit.native.prevent>
	                <el-form-item label="状态">
	                    <el-radio-group v-model="status" class="radio">
	                        <el-radio :label="10" disabled>待处理</el-radio>
	                        <el-radio :label="40">投诉失败</el-radio>
	                        <el-radio :label="50">投诉成功</el-radio>
	                        <el-radio :label="1010" disabled>待处理【用户删除】</el-radio>
	                        <el-radio :label="1040" disabled>投诉失败【用户删除】</el-radio>
	                        <el-radio :label="1050" disabled>投诉成功【用户删除】</el-radio>
	                        <el-radio :label="100010" disabled>待处理【员工删除】</el-radio>
	                        <el-radio :label="100040" disabled>投诉失败【员工删除】</el-radio>
	                        <el-radio :label="100050" disabled>投诉成功【员工删除】</el-radio>
	                    </el-radio-group>
	                </el-form-item>
	                <el-form-item label="分类" :required="true" :error="error.reportTypeId">
	                    <el-select ref="type_ref" size="large" v-model="reportTypeId" @focus="loadType"  style="width: 100%;" :placeholder="select_placeholder" >
	                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
	                    </el-select>
	                </el-form-item>
	                <el-form-item label="理由" :error="error.reason">
	                    <el-input type="textarea" :autosize="{minRows: 5}" placeholder="请填写举报理由" v-model="reason"></el-input>
	                    <div class="form-help" >需要举报分类中设置了显示举报理由，前台才显示本项</div>
	                </el-form-item>
	                <el-form-item :error="error.image">
	                    <el-upload ref="selectImage" v-model:file-list="fileList" action="#" :auto-upload="false" list-type="picture-card" :on-preview="openImagePreview" :accept="'.jpg,.jpeg,.gif,.png,.bmp'" >
	                        <i class="el-icon-plus"></i>
	                    </el-upload>
	                    <!-- 图片预览 -->
	                    <el-image-viewer v-if="isImageViewer" @close="closeImagePreview" :url-list="[localImageUrl]" />
	
	                </el-form-item>
	                <el-form-item label="处理结果">
	                    <el-input type="textarea" :autosize="{minRows: 5}" v-model="processResult"></el-input>
	                </el-form-item>
	                <el-form-item label="备注(本属性值不在前台显示)">
	                    <el-input type="textarea" :autosize="{minRows: 5}" v-model="remark"></el-input>
	                </el-form-item>
	                <el-form-item label=" ">
	                    <el-button type="primary" size="large" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
	                </el-form-item>
	            </el-form>
	            
	            <div class="selectReportTypeModule">
	                <el-dialog title="选择分类" v-model="popup_allType" @close="closeType">
	                    <div class="reportType-container">
	                        <div class="reportType-group" v-for="reportType in reportTypeList">
	                            
	                            <!--  仅有一级分类 -->
	                            <div v-if="reportType.childType.length ==0">
	                                <ul class="reportType-list">
	                                    <li class="reportType-item">
	                                        <el-radio-group v-model="reportTypeId" @change="selectReportType(reportTypeList)">
		                                        <el-radio :label="reportType.id" size="large">{{reportType.name}}</el-radio>
		                                    </el-radio-group>
	                                    </li>
	                                </ul>
	                                
	                            </div>
	                            <div v-else><!-- 含有多级分类 -->
	                                <p class="reportType-name">{{reportType.name}}</p>
	                                <ul class="reportType-list">
	                                    <el-radio-group class="multiple-radio" v-model="reportTypeId" @change="selectReportType(reportTypeList)">
	                                    <li class="reportType-item" v-for="childReportType in reportType.childType">
	                                        <el-radio :label="childReportType.id" size="large">{{childReportType.name}}</el-radio>
	                                    </li>
	                                </el-radio-group>
	                                </ul>
	                            </div>
	                        </div>
	                    </div>
	                </el-dialog>
	            </div>
	        </div>
		</div>
	</div>
</template>

<script>
//修改举报
export default({
	name: 'editReport',//组件名称，keep-alive缓存需要本参数
	template : '#editReport-template',
	inject:['reload'], 
	data : function data() {
		return {
			reportId :'',
	        reportTypeId :'',
	
	        options: [],//Select 选择器分类数据
	        popup_allType :false,//是否弹出分类表单
	        reportTypeList: [],//全部分类
	        select_placeholder: '请选择',
	
	        status :10,
	        reason : '',
	        processResult : '',//处理结果
	        remark : '',//备注(本属性值不在前台显示)
	        version:0,//版本号
	
	        fileList:[],//上传的文件列表
	        localImageUrl: '',//本地图片地址 例如blob:http://127.0.0.1:8080/cfab3833-cbb0-4072-a576-feaf8fb19e5f
	        isImageViewer: false,//是否显示图片查看器
	
	        submitForm_disabled:false,//提交按钮是否禁用
	
	        sourceUrlObject:{},//来源URL对象
	        
	        error:{
	        	reportTypeId:'',//举报分类Id
		        reason:'',//举报理由
		        image:'',//举报图片
		        report:'',//举报
	        }
		};
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.editReport_beforeUrl == undefined || to.query.editReport_beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'editReport_beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.editReport_beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.reportId != undefined && this.$route.query.reportId != ''){
			this.reportId = this.$route.query.reportId;
		}
		
		//上级路由解码
		if(this.$route.query.editReport_beforeUrl != undefined && this.$route.query.editReport_beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.editReport_beforeUrl));
			
			let decryptObject = JSON.parse(decrypt);
			
			let query = decryptObject.query;
			for(let q in query){
				query[q] = decodeURIComponent(query[q]);
			}
			this.sourceUrlObject = {
				path : decryptObject.path,
				query :query
			}
		}
		
		this.queryReport();
	},
	methods : {
		
	    //打开图片预览
		openImagePreview(file) {
			
	        this.localImageUrl = file.url;
	        this.isImageViewer = true;
	        //this.$refs.imageViewer.showViewer = true;
		},
		//关闭图片预览
		closeImagePreview() {
			this.localImageUrl = '';
			this.isImageViewer = false;
	        //this.$refs.imageViewer.showViewer = false;
		},
	
	
		//查询举报
		queryReport : function() {
			let _self = this;
			
			_self.$ajax.get('control/report/manage', {
			    params: {
			    	method : 'edit',
			    	reportId: _self.reportId,
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
		    			let report = null;
			    		for(let key in mapData){
			    			if(key == "report"){
			    				report = mapData[key];
			    				
			    			}
			    		}
			    		
			    		if(report != null){
                            
                            if(report.reportTypeId != ''){
                                let o = new Object();
                                o.value = report.reportTypeId;
                                o.label = report.reportTypeName;
                                _self.options.push(o);
                                _self.reportTypeId = report.reportTypeId;
                                _self.select_placeholder = "";
                            }
                            
                            _self.status = report.status;
                            _self.reason = report.reason;
                            _self.processResult = report.processResult;
                            _self.remark = report.remark;
                            _self.version = report.version;

                            for(let i=0; i< report.imageInfoList.length; i++){
			    				let imageInfo = report.imageInfoList[i];
			    				_self.fileList.push({
				    				name: imageInfo.name,
				    				url: imageInfo.path+imageInfo.name,
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
		
		//加载分类
		loadType : function() {
	        this.popup_allType = true;
	        this.$refs.type_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	        if(this.reportTypeId == ''){
	            this.options.length = 0;//清空	
	        }
	        
	        this.queryAllType();
	    },
	
		//关闭分类弹出框
		closeType : function() {
	        this.popup_allType = false;
	
	    },
	    //选择举报分类
	    selectReportType : function(reportTypeList) {
	    	let _self = this;
	        _self.$nextTick(function() {
	            let o = new Object();
	            for(let i =0; i<reportTypeList.length; i++){
	                let reportType = reportTypeList[i];
	                if(reportType.id == _self.reportTypeId){
	                    o.value = reportType.id;
	                    o.label = reportType.name;
	                    _self.options.push(o);
	                    _self.select_placeholder = "";
	                    break;
	                }
	                
	                for(let j =0; j<reportType.childType.length; j++){
	                    let childReportType = reportType.childType[j];
	                    if(childReportType.id == _self.reportTypeId){
	                        o.value = childReportType.id;
	                        o.label = childReportType.name;
	                        _self.options.push(o);
	                        _self.select_placeholder = "";
	                        break;
	                    }
	                }
	            }
	            
	            _self.closeType();
	        })
	    },
    
    	//查询所有分类
	    queryAllType : function() {
	    	let _self = this;
			
			_self.$ajax.get('control/reportType/manage', {
			    params: {
			    	 method : 'allType'
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
			    		_self.reportTypeList = returnValue.data;
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
			formData.append('reportId', _self.reportId);
			
	        if(_self.reportTypeId != null){
	            formData.append('reportTypeId', _self.reportTypeId);
	            
	        }
	        if(_self.status != null && _self.status == 40 || _self.status == 50){
	            formData.append('status', _self.status);
	        }
	
	        if(_self.reason != null){
	            formData.append('reason', _self.reason);
	        }
	        if(_self.processResult != null){
	            formData.append('processResult', _self.processResult);
	        }
	        if(_self.remark != null){
	            formData.append('remark', _self.remark);
	        }
	
	        formData.append('version', _self.version);
	        
	        let fileList = _self.$refs.selectImage.uploadFiles;
	        
	        if(fileList != null && fileList.length >0){
	            for(let i=0; i<fileList.length; i++){
	                let file = fileList[i];
	                if(file.raw != undefined){
	                    formData.append('imageFile', file.raw);
	                }else{
	                    formData.append('imageFile',  new Blob([], { type: "text/html" }));//空白Blob
	                    formData.append('imagePath', file.url);
	                }
	            }
	        }
	        
	        
        
			_self.$ajax({
		        method: 'post',
		        url: 'control/report/manage?method=edit',
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
							path: _self.sourceUrlObject.path, 
			    			query:_self.sourceUrlObject.query
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