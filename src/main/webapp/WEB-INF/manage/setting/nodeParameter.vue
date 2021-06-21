<!-- 服务器节点参数 -->
<template id="nodeParameter-template">
	<div>
		<div class="main">
			<div class="data-view" >
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">服务器时间：</div></el-col>
					<el-col :span="16"><div class="content" id="jclock"></div></el-col>
				</el-row>
				
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">分配最大内存：</div></el-col>
					<el-col :span="16"><div class="content">{{systemNode.maxMemory}}MB <span class="data-help" >-Xmx 当前虚拟机实例从操作系统可分配到的最大内存</span></div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">已分配内存：</div></el-col>
					<el-col :span="16"><div class="content">{{systemNode.totalMemory}}MB <span class="data-help" >-Xms 当前虚拟机实例已经从操作系统占用的内存</span></div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">已分配内存中的剩余空间：</div></el-col>
					<el-col :span="16"><div class="content">{{systemNode.freeMemory}}MB</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">空闲内存：</div></el-col>
					<el-col :span="16"><div class="content">{{systemNode.usableMemory}}MB <span class="data-help" >当前虚拟机实例从操作系统分配到的最大内存中的剩余空间</span></div></el-col>
				</el-row>
				
				<el-row :gutter="10" type="flex">
					<el-col :span="8"><div class="name">缓存服务器：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheName}}</div></el-col>
				</el-row>
				
				<el-row :gutter="10" type="flex" v-for="cacheStatus in memcached_cacheStatusList">
					<el-col :span="8"><div class="name">服务器地址：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.serviceAddress}}</div></el-col>
					<el-col :span="8"><div class="name">当前已使用的内存容量：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_bytes}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，曾存储的Item总个数：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_total_items}}</div></el-col>
					<el-col :span="8"><div class="name">MemCached服务版本：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_version}}</div></el-col>
					<el-col :span="8"><div class="name">MemCached服务器架构：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_pointer_size}}</div></el-col>
					<el-col :span="8"><div class="name">服务器当前时间：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_time}}</div></el-col>
					<el-col :span="8"><div class="name">允许服务支配的最大内存容量：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_limit_maxbytes}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，读取的数据量：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_bytes_read}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，写入的数据量：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_bytes_written}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，总共运行时间：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_uptime}}</div></el-col>
					<el-col :span="8"><div class="name">当前存储的Item个数：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_curr_items}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，执行Get命令总次数：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_cmd_get}}</div></el-col>
					<el-col :span="8"><div class="name">服务器本次启动以来，执行Set命令总次数：</div></el-col>
					<el-col :span="16"><div class="content">{{cacheStatus.memCached_cmd_set}}</div></el-col>
				</el-row>
				
			</div>
		</div>
	</div>
</template>

<script>
//服务器节点参数
export default({
	name: 'nodeParameter',//组件名称，keep-alive缓存需要本参数
	template : '#nodeParameter-template',
	inject:['reload'], 
	data : function data() {
		return {
			systemNode:'',
			cacheName:'',
			memcached_cacheStatusList:'',
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		//初始化
		this.queryNodeParameter();
		this.queryServerTime();
	},
	methods : {
		//查询服务器节点参数
	    queryNodeParameter: function(){
	        let _self = this;
			
			_self.systemNode = '';
			
			_self.$ajax.get('control/systemSetting/manage', {
			    params: {
			    	method : 'nodeParameter',
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
			    			if(key == "systemNode"){
			    				_self.systemNode = mapData[key];
			    			
			    			}else if(key == "memcached_cacheStatusList"){
			    				_self.memcached_cacheStatusList = mapData[key];
			    			}else if(key == "ehCache_cacheStatusList"){
			    				let ehCache_cacheStatusList = mapData[key];
			    			}else if(key == "cacheName"){
			    				_self.cacheName = mapData[key];
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
		
		//查询服务器时间
	    queryServerTime: function(){
	        let _self = this;
			
	        let start = new Date().getTime(); // 开始时间
			
			_self.$ajax.get('admin/currentTime', {
			    params: {
			    	
			    }
			})
			.then(function (response) {
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let timeData = JSON.parse(result);
			    	
			    	var end = new Date().getTime(); // 结束时间
					//请求数据返回时长
					var time = Math.floor((end - start)/2);
					
					

					//服务器时间戳
					var serverDate = parseInt(timeData.currentTime)+time;
					
					var options = {
				        format: '%Y-%m-%d %H:%M:%S', // 24小时制
				      	seedTime: serverDate,
				      	utc: true,
	        			utcOffset: timeData.timezoneOffset
	        		
				    }
					//这是调用jquery.jclock.js插件的方法
				    $('#jclock').jclock(options);
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
	}
});
</script>