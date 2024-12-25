<!-- 添加会员卡 -->
<template id="addMembershipCard-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/membershipCard/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-row><el-col :span="18"><el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="副标题" :error="error.subtitle">
						<el-row><el-col :span="18"><el-input v-model.trim="subtitle" maxlength="50" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					
					<el-form-item label="用户角色" :error="error.userRoleId">
						<el-radio-group v-model="userRoleId">
						    <el-radio :label="roles.id" v-for="roles in rolesList">{{roles.name}}</el-radio>
						</el-radio-group>
					</el-form-item>
					
					<div class="table-container">
						<el-table ref="specification_table" :data="specification.tableList" row-key="key" tooltip-effect="dark" :show-header="false" stripe style="width: 100%" empty-text="没有项">
							<el-table-column label="表单" >
								<template #default="scope">	
									<el-row :gutter="30">
										<el-col :span="12">
											<el-form-item label="规格名称" :required="true" :error="error.specificationName.get(scope.$index)">
												<el-input v-model.trim="specification.specificationName[scope.$index]" maxlength="40" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="启用规格" :error="error.enable.get(scope.$index)">
												<el-switch v-model="specification.enable[scope.$index]">
											</el-form-item>
										</el-col>
									</el-row>
									<el-row :gutter="30">
										<el-col :span="12">
											<el-form-item label="库存" :required="true" :error="error.stock.get(scope.$index)">
												<el-input v-model.trim="specification.stock[scope.$index]" maxlength="10" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
									</el-row>
									<el-row :gutter="30">
										<el-col :span="12">
											<el-form-item label="市场价" :error="error.marketPrice.get(scope.$index)">
												<el-input v-model.trim="specification.marketPrice[scope.$index]" maxlength="10" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
										<el-col :span="12">
											<el-form-item label="销售价" :error="error.sellingPrice.get(scope.$index)">
												<el-input v-model.trim="specification.sellingPrice[scope.$index]" maxlength="10" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
									</el-row>
									
									<el-row>
										<el-col :span="12">
											<el-form-item label="支付积分" :error="error.point.get(scope.$index)">
												<el-input v-model.trim="specification.point[scope.$index]" maxlength="10" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
										<el-col :span="8">
											<el-form-item label="时长" :required="true" :error="error.duration.get(scope.$index)">
												<el-input v-model.trim="specification.duration[scope.$index]" maxlength="8" clearable="true" show-word-limit></el-input>
											</el-form-item>
										</el-col>
										<el-col :span="4">
											<el-form-item :error="error.unit.get(scope.$index)" style="margin-left: -70px;">
												<el-select v-model="specification.unit[scope.$index]"  no-match-text="还没有单位" placeholder="选择单位">
													<el-option v-for="item in unitOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
												</el-select>
											</el-form-item>
										</el-col>
									</el-row>
									
									
									
									
						    	</template>
							</el-table-column>
							<el-table-column label="操作" align="right" width="200">
								<template #default="scope">	
									<el-button icon="el-icon-top" circle @click.prevent="specification_moveUp(scope.row,scope.$index)" title="上移"></el-button>
									<el-button icon="el-icon-bottom" circle @click.prevent="specification_moveDown(scope.row,scope.$index)" title="下移"></el-button>
									<el-button icon="el-icon-delete" circle @click.prevent="specification_removeItem(scope.row,scope.$index)" title="删除"></el-button>
						    	</template>
							</el-table-column>
							
						</el-table>
						<el-form-item>
							<div class="item-button" style="margin-left: 10px;"><el-button icon="el-icon-plus" @click="specification_addItem">添加规格项</el-button></div>
							</el-form-item>
						
					</div>
					
					<el-row :gutter="30">
						<el-col :span="12">
							<el-form-item label="排序" :required="true" :error="error.sort">
								<el-row><el-col :span="10"><el-input v-model.trim="sort" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="是否上架" :error="error.state">
							<el-switch v-model="upper" ></el-switch>
						</el-form-item>
						</el-col>
					</el-row>
					<el-form-item v-for="(formOption, index) in formOptions" label="说明标签"  :prop="'formOptions.' + index + '.value'">
						<el-row :gutter="10">
							<el-col :span="16">
								<el-input v-model.trim="formOption.value" maxlength="50" clearable="true" show-word-limit></el-input>
							</el-col>
							<el-col :span="6">
								<el-button icon="el-icon-top" circle @click.prevent="moveUp(formOption)" title="上移"></el-button>
								<el-button icon="el-icon-bottom" circle @click.prevent="moveDown(formOption)" title="下移"></el-button>
								<el-button icon="el-icon-delete" circle @click.prevent="removeItem(formOption)" title="删除"></el-button>
							</el-col>
						</el-row>
					</el-form-item>
					<el-form-item>
						<div class="form-error" v-text="error.descriptionTag"></div>
						<div class="item-button"><el-button icon="el-icon-plus" @click="addItem">添加说明标签项</el-button></div>
					</el-form-item>
					<el-form-item label="简介" :error="error.introduction">
						<textarea ref="introduction" style="width:99%;height:400px;visibility:hidden;"></textarea>
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
//添加会员卡
export default({
	name: 'addMembershipCard',//组件名称，keep-alive缓存需要本参数
	template : '#addMembershipCard-template',
	inject:['reload'], 
	data : function data() {
		return {
			rolesList:'',
			
			name:'',
			subtitle:'',
			userRoleId:'',
			sort:0,
			upper:true,//是否上架

		    formOptions: [{
		    	key: '',
				value: ''
            }],
			introductionEditor :'',//简介编辑器
			introductionEditorCreateParameObject :{},//简介编辑器的创建参数
			
			specification :{//规格
				tableList:[],
				specificationName: [],
				enable: [],
				stock: [],
				point: [],
				marketPrice: [],
				sellingPrice: [],
				duration: [],
				unit: [],
			},
			
			unitOptions: [{
		    	value: 10,
		        label: '小时'
		    }, {
		        value: 20,
		        label: '日'
		    }, {
		        value: 30,
		        label: '月'
		    }, {
		        value: 40,
		        label: '年'
		    }],
			
			error : {
				name:'',
				subtitle:'',
				userRoleId:'',
				sort:'',
				state:'',
				introduction:'',
				descriptionTag:'',
				
				specificationName:new Map(),
				enable:new Map(),
				stock:new Map(),
				point:new Map(),
				marketPrice:new Map(),
				sellingPrice:new Map(),
				duration:new Map(),
				unit:new Map(),
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	//keep-alive 进入时
	activated: function () {
		if (Object.keys(this.introductionEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			this.introductionEditor = createEditor(
				this.introductionEditorCreateParameObject.ref, 
				this.introductionEditorCreateParameObject.availableTag, 
				this.introductionEditorCreateParameObject.uploadPath, 
				this.introductionEditorCreateParameObject.userGradeList
			);
		}
	},
	// keep-alive 离开时
	deactivated : function () {
		if(this.introductionEditor != ''){
			this.introductionEditor.remove();
		}
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		this.queryAddMembershipCard();
	},
	methods : {
		 //查询添加会员卡
	    queryAddMembershipCard: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/membershipCard/manage', {
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
			    			if(key == "userRoleList"){
			    				let rolesList = mapData[key];
			    				if(rolesList != null && rolesList.length >0){
			    					for(let i=0; i<rolesList.length; i++){
			    						let roles = rolesList[i];
			    						if(roles.selected){
			    							_self.userRoleId = roles.id;
			    						}
			    					}
			    				}
			    				_self.rolesList = rolesList;
			    			}
			    		}
			    		
			    		
			    		let availableTag = ['source', '|', 'preview', 'template',  
			    				        '|', 'justifyleft', 'justifycenter', 'justifyright',
			    				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
			    				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
			    				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
			    				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
			    				         'media','embedVideo','uploadVideo', 'insertfile','emoticons', 'table', 'hr',   'pagebreak',
			    				         'link', 'unlink'];
		    			let uploadPath = "control/membershipCard/manage?method=upload&userName="+_self.userName+"&isStaff=true";
			    		//创建富文本编辑器
						_self.introductionEditor = createEditor(_self.$refs.introduction, availableTag, uploadPath, null);
						_self.introductionEditorCreateParameObject = {
			    				ref:_self.$refs.introduction,
			    				availableTag:availableTag,
			    				uploadPath:uploadPath,
			    				userGradeList:null
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
		
		//规格 添加项
	    specification_addItem: function() {
	    	this.specification.specificationName.push("");
	    	this.specification.enable.push(true);
	    	this.specification.stock.push("");
	    	this.specification.point.push("");
	    	this.specification.marketPrice.push("");
	    	this.specification.sellingPrice.push("");
	    	this.specification.duration.push("");
	    	this.specification.unit.push(10);
	    	
	    	
	        this.specification.tableList.push({
	        	key: Math.random().toString().slice(2),//随机数
	        });
		},
		//上移
		specification_moveUp : function(row,index) {
	        if (index >0) {
	        	let upData = this.specification.tableList[index - 1];
	        	this.specification.tableList.splice(index - 1, 1);
                this.specification.tableList.splice(index, 0, upData);
               
                
                let specificationName_upData = this.specification.specificationName[index - 1];
	        	this.specification.specificationName.splice(index - 1, 1);
                this.specification.specificationName.splice(index, 0, specificationName_upData);
                
                let enable_upData = this.specification.enable[index - 1];
	        	this.specification.enable.splice(index - 1, 1);
                this.specification.enable.splice(index, 0, enable_upData);
                
                let stock_upData = this.specification.stock[index - 1];
	        	this.specification.stock.splice(index - 1, 1);
                this.specification.stock.splice(index, 0, stock_upData);
                
                let point_upData = this.specification.point[index - 1];
	        	this.specification.point.splice(index - 1, 1);
                this.specification.point.splice(index, 0, point_upData);
                
                let marketPrice_upData = this.specification.marketPrice[index - 1];
	        	this.specification.marketPrice.splice(index - 1, 1);
                this.specification.marketPrice.splice(index, 0, marketPrice_upData);
                
                let sellingPrice_upData = this.specification.sellingPrice[index - 1];
	        	this.specification.sellingPrice.splice(index - 1, 1);
                this.specification.sellingPrice.splice(index, 0, sellingPrice_upData);
                
                let duration_upData = this.specification.duration[index - 1];
	        	this.specification.duration.splice(index - 1, 1);
                this.specification.duration.splice(index, 0, duration_upData);
                
                let unit_upData = this.specification.unit[index - 1];
	        	this.specification.unit.splice(index - 1, 1);
                this.specification.unit.splice(index, 0, unit_upData);
                
                
                
                //错误信息移动
                let up_specificationName_error = this.error.specificationName.get(index - 1);
                let current_specificationName_error = this.error.specificationName.get(index);
                this.error.specificationName.delete(index - 1);
                this.error.specificationName.delete(index);
                if(up_specificationName_error != null){
                	this.error.specificationName.set(index,up_specificationName_error);
                }
                if(current_specificationName_error != null){
                	this.error.specificationName.set(index - 1,current_specificationName_error);
                }
                
                let up_enable_error = this.error.enable.get(index - 1);
                let current_enable_error = this.error.enable.get(index);
                this.error.enable.delete(index - 1);
                this.error.enable.delete(index);
                if(up_enable_error != null){
                	this.error.enable.set(index,up_enable_error);
                }
                if(current_enable_error != null){
                	this.error.enable.set(index - 1,current_enable_error);
                }
               
                let up_stock_error = this.error.stock.get(index - 1);
                let current_stock_error = this.error.stock.get(index);
                this.error.stock.delete(index - 1);
                this.error.stock.delete(index);
                if(up_stock_error != null){
                	this.error.stock.set(index,up_stock_error);
                }
                if(current_stock_error != null){
                	this.error.stock.set(index - 1,current_stock_error);
                }
                
                let up_point_error = this.error.point.get(index - 1);
                let current_point_error = this.error.point.get(index);
                this.error.point.delete(index - 1);
                this.error.point.delete(index);
                if(up_point_error != null){
                	this.error.point.set(index,up_point_error);
                }
                if(current_point_error != null){
                	this.error.point.set(index - 1,current_point_error);
                }
                
                let up_marketPrice_error = this.error.marketPrice.get(index - 1);
                let current_marketPrice_error = this.error.marketPrice.get(index);
                this.error.marketPrice.delete(index - 1);
                this.error.marketPrice.delete(index);
                if(up_marketPrice_error != null){
                	this.error.marketPrice.set(index,up_marketPrice_error);
                }
                if(current_marketPrice_error != null){
                	this.error.marketPrice.set(index - 1,current_marketPrice_error);
                }
                
                let up_sellingPrice_error = this.error.sellingPrice.get(index - 1);
                let current_sellingPrice_error = this.error.sellingPrice.get(index);
                this.error.sellingPrice.delete(index - 1);
                this.error.sellingPrice.delete(index);
                if(up_sellingPrice_error != null){
                	this.error.sellingPrice.set(index,up_sellingPrice_error);
                }
                if(current_sellingPrice_error != null){
                	this.error.sellingPrice.set(index - 1,current_sellingPrice_error);
                }
                
                let up_duration_error = this.error.duration.get(index - 1);
                let current_duration_error = this.error.duration.get(index);
                this.error.duration.delete(index - 1);
                this.error.duration.delete(index);
                if(up_duration_error != null){
                	this.error.duration.set(index,up_duration_error);
                }
                if(current_duration_error != null){
                	this.error.duration.set(index - 1,current_duration_error);
                }
                
                let up_unit_error = this.error.unit.get(index - 1);
                let current_unit_error = this.error.unit.get(index);
                this.error.unit.delete(index - 1);
                this.error.unit.delete(index);
                if(up_unit_error != null){
                	this.error.unit.set(index,up_unit_error);
                }
                if(current_unit_error != null){
                	this.error.unit.set(index - 1,current_unit_error);
                }
	        }
		
		},
		//下移
		specification_moveDown : function(row,index) {
	        if ((index + 1) < this.specification.tableList.length) {
	        	let downData = this.specification.tableList[index + 1];
                this.specification.tableList.splice(index + 1, 1);
                this.specification.tableList.splice(index, 0, downData);
           
                let specificationName_downData = this.specification.specificationName[index + 1];
                this.specification.specificationName.splice(index + 1, 1);
                this.specification.specificationName.splice(index, 0, specificationName_downData);
                
                let enable_downData = this.specification.enable[index + 1];
                this.specification.enable.splice(index + 1, 1);
                this.specification.enable.splice(index, 0, enable_downData);
                
                let stock_downData = this.specification.stock[index + 1];
                this.specification.stock.splice(index + 1, 1);
                this.specification.stock.splice(index, 0, stock_downData);
                
                let point_downData = this.specification.point[index + 1];
                this.specification.point.splice(index + 1, 1);
                this.specification.point.splice(index, 0, point_downData);
                
                let marketPrice_downData = this.specification.marketPrice[index + 1];
                this.specification.marketPrice.splice(index + 1, 1);
                this.specification.marketPrice.splice(index, 0, marketPrice_downData);
                
                let sellingPrice_downData = this.specification.sellingPrice[index + 1];
                this.specification.sellingPrice.splice(index + 1, 1);
                this.specification.sellingPrice.splice(index, 0, sellingPrice_downData);
                
                let duration_downData = this.specification.duration[index + 1];
                this.specification.duration.splice(index + 1, 1);
                this.specification.duration.splice(index, 0, duration_downData);
                
                let unit_downData = this.specification.unit[index + 1];
                this.specification.unit.splice(index + 1, 1);
                this.specification.unit.splice(index, 0, unit_downData);
                
              
                //错误信息移动
                let down_specificationName_error = this.error.specificationName.get(index + 1);
                let current_specificationName_error = this.error.specificationName.get(index);
                this.error.specificationName.delete(index + 1);
                this.error.specificationName.delete(index);
                if(down_specificationName_error != null){
                	this.error.specificationName.set(index,down_specificationName_error);
                }
                if(current_specificationName_error != null){
                	this.error.specificationName.set(index + 1,current_specificationName_error);
                }
                
                let down_enable_error = this.error.enable.get(index + 1);
                let current_enable_error = this.error.enable.get(index);
                this.error.enable.delete(index + 1);
                this.error.enable.delete(index);
                if(down_enable_error != null){
                	this.error.enable.set(index,down_enable_error);
                }
                if(current_enable_error != null){
                	this.error.enable.set(index + 1,current_enable_error);	
                }
                
                let down_stock_error = this.error.stock.get(index + 1);
                let current_stock_error = this.error.stock.get(index);
                this.error.stock.delete(index + 1);
                this.error.stock.delete(index);
                if(down_stock_error != null){
                	this.error.stock.set(index,down_stock_error);
                }
                if(current_stock_error != null){
                	this.error.stock.set(index + 1,current_stock_error);
                }
                
                let down_point_error = this.error.point.get(index + 1);
                let current_point_error = this.error.point.get(index);
                this.error.point.delete(index + 1);
                this.error.point.delete(index);
                if(down_point_error != null){
                	this.error.point.set(index,down_point_error);
                }
                if(current_point_error != null){
                	this.error.point.set(index + 1,current_point_error);
                }
                
                let down_marketPrice_error = this.error.marketPrice.get(index + 1);
                let current_marketPrice_error = this.error.marketPrice.get(index);
                this.error.marketPrice.delete(index + 1);
                this.error.marketPrice.delete(index);
                if(down_marketPrice_error != null){
                	this.error.marketPrice.set(index,down_marketPrice_error);
                }
                if(current_marketPrice_error != null){
                	this.error.marketPrice.set(index + 1,current_marketPrice_error);
                }
                
                let down_sellingPrice_error = this.error.sellingPrice.get(index + 1);
                let current_sellingPrice_error = this.error.sellingPrice.get(index);
                this.error.sellingPrice.delete(index + 1);
                this.error.sellingPrice.delete(index);
                if(down_sellingPrice_error != null){
                	this.error.sellingPrice.set(index,down_sellingPrice_error);
                }
                if(current_sellingPrice_error != null){
                	this.error.sellingPrice.set(index + 1,current_sellingPrice_error);
                }
                
                let down_duration_error = this.error.duration.get(index + 1);
                let current_duration_error = this.error.duration.get(index);
                this.error.duration.delete(index + 1);
                this.error.duration.delete(index);
                if(down_duration_error != null){
                	this.error.duration.set(index,down_duration_error);
                }
                if(current_duration_error != null){
                	this.error.duration.set(index + 1,current_duration_error);
                }
                
                let down_unit_error = this.error.unit.get(index + 1);
                let current_unit_error = this.error.unit.get(index);
                this.error.unit.delete(index + 1);
                this.error.unit.delete(index);
                if(down_unit_error != null){
                	this.error.unit.set(index,down_unit_error);
                }
                if(current_unit_error != null){
                	this.error.unit.set(index + 1,current_unit_error);
                }
	        }
		
		},
		//规格  删除
		specification_removeItem : function(row,index) {
			this.specification.tableList.splice(index, 1);

			this.specification.specificationName.splice(index, 1);
			this.specification.enable.splice(index, 1);
			this.specification.stock.splice(index, 1);
			this.specification.point.splice(index, 1);
			this.specification.marketPrice.splice(index, 1);
			this.specification.sellingPrice.splice(index, 1);
			this.specification.duration.splice(index, 1);
			this.specification.unit.splice(index, 1);
		},

		
		//说明标签 添加项
	    addItem() {
	        this.formOptions.push({
	        	key: '',
	        	value: ''
	        });
		},
		//说明标签 删除项
	    removeItem(formOption) {
	        let index = this.formOptions.indexOf(formOption);
	        if (index !== -1) {
	        	this.formOptions.splice(index, 1);
	        }
		},
		//说明标签 上移
	    moveUp(formOption) {
	        let index = this.formOptions.indexOf(formOption);
	        if (index >0) {
	        	let upData = this.formOptions[index - 1];
	        	this.formOptions.splice(index - 1, 1);
                this.formOptions.splice(index, 0, upData);
	        }
		},
		//说明标签 下移
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
    			if(getType(_self.error[key]) == 'map'){
    				_self.error[key].clear();
    			}else{
    				_self.error[key] = "";
    			}
    	    }
			let formData = new FormData();
			
			formData.append('name', _self.name);
			formData.append('subtitle', _self.subtitle);
			formData.append('userRoleId', _self.userRoleId);

			formData.append('sort', _self.sort);
			if(_self.upper == true){//上架
				formData.append('state', 1);
				
			}else{//下架
				formData.append('state', 2);
			}
			
			for(let i=0; i<_self.specification.tableList.length; i++){
				let table = _self.specification.tableList[i];

				let specificationName = _self.specification.specificationName[i];
				let enable = _self.specification.enable[i];
				let stock = _self.specification.stock[i];
				let point = _self.specification.point[i];
				let marketPrice = _self.specification.marketPrice[i];
				let sellingPrice = _self.specification.sellingPrice[i];
				let duration = _self.specification.duration[i];
				let unit = _self.specification.unit[i];
				
				formData.append('specificationRowTable', i);
				formData.append('specificationName', specificationName);
				formData.append('enable', enable);
				formData.append('stock', stock);
				formData.append('point', point);
				formData.append('marketPrice', marketPrice);
				formData.append('sellingPrice', sellingPrice);
				formData.append('duration', duration);
				formData.append('unit', unit);
				
				

			}
			
			
			
			if(_self.formOptions != null && _self.formOptions.length >0){
				for(let i=0; i<_self.formOptions.length; i++){
					let formOption = _self.formOptions[i];
					formData.append('descriptionTag', formOption.value);
					
				}
			}
			if(_self.$refs.introduction.value != null && _self.$refs.introduction.value !=''){
				formData.append('introduction', _self.$refs.introduction.value);
			}
		
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/membershipCard/manage?method=add',
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
							path : '/admin/control/membershipCard/list',
						});
			    	}else if(returnValue.code === 500){//错误	
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) { 
			    			if(key.indexOf("_") != -1){
			    				let parameterName = (key.lastIndexOf("_") != -1 ? key.substring(0,key.lastIndexOf("_")) : '');//删除最后一个序号 collection_image_name_0
			    				
								if(parameterName != "" && getType(_self.error[parameterName]) == 'map'){
									_self.error[parameterName].set(parseInt(key.substring(key.lastIndexOf("_")+1,key.length)),errorMap[key]);
									continue;
								}
			    			}
			    			
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