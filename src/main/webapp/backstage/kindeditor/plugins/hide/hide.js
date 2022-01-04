KindEditor.plugin('hide', function(K) {
	//self为编辑器(Editor) API
	var self = this, name = 'hide',
	userGradeList = K.undef(self.userGradeList, []),
	items = K.undef(self.items, []),
	tabIndex = K.undef(0, 0);
	
	
	/**
	 * 替换标签
	 * html html内容
	 * tag 标签名称
	 * className css样式名称
	 * inputValue 替换标签值
	 */
	function replaceTab(html,tag,className,inputValue,description){
		var node = document.createElement("div");
		node.innerHTML = html;
		
		getChildNode(node,tag,className,inputValue,description);
		return node.innerHTML;
	}
	
	/**
	 * 递归获取所有的子节点
	 * node 节点
	 * tag 标签名称
	 * className css样式名称
	 * inputValue 替换标签值
	 * description 描述
	 * 
	 */
	function getChildNode(node,tag,className,inputValue,description){
        //先找到子节点
        var nodeList = node.childNodes;
        for(var i = 0;i < nodeList.length;i++){
            //childNode获取到到的节点包含了各种类型的节点
            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
            var childNode = nodeList[i];
            
            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
            if(childNode.nodeType == 1){
            	if(childNode.nodeName.toLowerCase() == tag.toLowerCase() && 
            			childNode.getAttribute("class") == className){
            		childNode.setAttribute("input-value",inputValue);
            		if(description != null && description != ""){
            			childNode.setAttribute("description",description);
            		}
            	}
                getChildNode(childNode,tag,className,inputValue,description);
            }
        }
    }
	
	/**
	 * 处理html前
	 */
	function processHtmlBefore(html){
		return html.replace(/(<(?:hide|hide\s[^>]*)>)([\s\S]*?)(<\/hide>)/ig, function($0, $1, $2, $3) {
			//因为<p>标签包裹<pre>等块元素时会被浏览器截断，<hide>标签内含的<pre>在“<p><hide>aaa<pre>bbb</pre>ccc</hide></p>”被浏览器执行node.innerHTML后变为“<p><hide>aaa</hide></p><pre>bbb</pre>ccc<p></p>”,发生错位。 
			//我们将<hide>标签用块元素<article>包裹,让<hide>的父级<p>标签自动截断,保护<hide>标签内的<pre>标签不会由截断产生错位。正确结果为“<p></p><hide>aaa<pre>bbb</pre>ccc</hide><p></p>”
			//本正则和方法self.beforeGetHtml配合使用
			return '<article type="__kindeditor_temp_pre__">' +$1 + $2 + $3 + '</article>';
		});
		
	}
	/**
	 * 处理html后
	 */
	function processHtmlAfter(html){
		return html.replace(/<article\s+[^>]*type="([^"]*)"[^>]*>([\s\S]*?)<\/article>/ig, function(full, attr, code) {//将self.beforeSetHtml方法加入的<article>标签删除
			return code;
		});
	}
	
	

    // 点击图标时执行
	self.plugin.hideDialog = function(options) {
		
		
		var userGradeHtml = "";
		if(userGradeList != null && userGradeList.length >0){
			userGradeHtml += '<select id="inputValue_30" name="inputValue_30" >';
			for(var i=0; i<userGradeList.length; i++){
				var userGrade = userGradeList[i];
					
				userGradeHtml += '<option value="'+userGrade.needPoint+'" '+ (options.hideGrade == userGrade.needPoint ? 'selected="selected"' : "")	+'>'+userGrade.name+'</option>';
			}
			userGradeHtml += '</select> 以上可见';
		}
		
		
		
		var lang = self.lang(name + '.'),
		selectedNode = options.selectedNode,
		
		
		
		
		
		html = [
				'<div style="padding:20px;height:160px;">',
					//tabs
					'<div class="tabs"></div>',
					//选项1
					'<div class="tab1" style="display:none;">',
						//密码
						'<div class="ke-dialog-row">',
						'<label for="inputValue_10" style="width:34px;">密码</label>',
						'<input type="text" id="inputValue_10" class="ke-input-text ke-input-number" name="inputValue_10" value="'+options.hidePassword+'" maxlength="20" style="width:200px;" /> ',
						'</div>',
					'</div>',
					//选项2
					'<div class="tab2" style="display:none;">',
					
					'</div>',
					
					//选项3
					'<div class="tab3" style="display:none;">',
						//达到等级
						'<div class="ke-dialog-row">',
						'<label for="inputValue_30" style="width:60px;">达到等级</label>',
						userGradeHtml,
						'</div>',
					'</div>',
					//选项4
					'<div class="tab4" style="display:none;">',
						//积分购买
						'<div class="ke-dialog-row">',
						'<label for="inputValue_40" style="width:60px;">需要支付</label>',
						'<input type="text" id="inputValue_40" class="ke-input-text ke-input-number" name="inputValue_40" value="'+options.hidePoint+'" maxlength="8" style="width:130px;" /> 积分可见',
						'</div>',
					'</div>',
					//选项5
					'<div class="tab5" style="display:none;">',
						//余额购买
						'<div class="ke-dialog-row">',
						'<label for="inputValue_50" style="width:60px;">需要支付</label>',
						'<input type="text" id="inputValue_50" class="ke-input-text ke-input-number" name="inputValue_50" value="'+options.hideAmount+'" maxlength="9" style="width:130px;" /> 元费用可见',
						'</div>',
					'</div>',
					'<div>',
						//提示
						'<div style="color: #909399;position: absolute;left: 21px;bottom: 18px;">',
						'提示：Shift + 回车 换行不换段',
						'</div>',
					'</div>',
				'</div>'
			].join(''),
			
			
		dialog = self.createDialog({
			name : name,
			width : 600,
			title : self.lang(name),
			body : html,
			yesBtn : {
				name : self.lang('yes'),
				click : function(e) {
					
					
					var ancestor = K(self.cmd.range.commonAncestor());
					if(ancestor.name != "body" && ancestor.name != "hide" && ancestor.name != "pre" && ancestor.name != "code"){
						//截断样式
						var _wrapper = K("<span></span>", self.cmd.doc);//插入指定的HTML内容到光标处。
						_wrapper.html("&#8203;");//&#8203;是零宽空格字符
						self.cmd.range.insertNode(_wrapper[0]).selectNodeContents(_wrapper[0]);
						self.cmd.select();
						self.cmd.removeformat();//删除格式。
					}
					
					var bookmark = self.cmd.range.createBookmark(true);//通过在光标处插入指定标签<span id="#__kindeditor_bookmark_start_1__"></span>标记，用于DOM操作后恢复之前光标位置
					
					html = "";
					var password = K.trim(passwordBox.val()),
					grade = K.trim(gradeBox.val()),
					point = K.trim(pointBox.val()),
					amount = K.trim(amountBox.val());
					//等级标签
					var gradeTag = "";
					if(userGradeList != null && userGradeList.length >0){
						for(var i=0; i<userGradeList.length; i++){
							var userGrade = userGradeList[i];
							if(userGrade.needPoint == grade){
								gradeTag = userGrade.name;
							}
						}
					}
					
					
				//	console.log(gradeTag);
				//	console.log(gradeBox.text());
				//	console.log(gradeBox.find("option:selected").text());
					
					
					if(tabIndexBox == 0){//输入密码可见
						if (password == "") {
							K.popupMessage("请输入密码");
							passwordBox[0].focus();
							return;
						}
					}else if(tabIndexBox == 1){//回复话题可见
						
					}else if(tabIndexBox == 2){//达到等级可见
						if (grade == "" || !/^[0-9]*[1-9][0-9]*$/.test(grade)) {//正整数
							K.popupMessage("请选择等级");
							if(gradeBox[0] != null){
								gradeBox[0].focus();
							}
							return;
						}
					}else if(tabIndexBox == 3){//积分购买可见
						if (point == "" || !/^[0-9]*[1-9][0-9]*$/.test(point)) {//正整数
							K.popupMessage("请输入大于0的数字");
							pointBox[0].focus();
							return;
						}
					}else if(tabIndexBox == 4){//余额购买可见  正整数,也可接收正浮点数，两位小数
						if (amount == "" || !/^(([1-9]\d*)(\.\d{1,2})?)$|(0\.0?([1-9]\d?))$/.test(amount)) {
							K.popupMessage("请输入大于0的金额");
							amountBox[0].focus();
							return;
						}
					}
					
					var htmlValue = processHtmlBefore(self.bodyHtml());
					if(tabIndexBox == 0){//输入密码可见
						//替换标签
						htmlValue = replaceTab(htmlValue,"hide","inputValue_10",""+password+"");
						
					}else if(tabIndexBox == 1){//回复话题可见
						
					}else if(tabIndexBox == 2){//达到等级可见
						//替换标签
						htmlValue = replaceTab(htmlValue,"hide","inputValue_30",""+grade+"",""+gradeTag+"");
						
					}else if(tabIndexBox == 3){//积分购买可见
						htmlValue = replaceTab(htmlValue,"hide","inputValue_40",""+point+""); 
						
					}else if(tabIndexBox == 4){//余额购买可见
						htmlValue = replaceTab(htmlValue,"hide","inputValue_50",""+amount+""); 
						
					}
					if(tabIndexBox == 0 || tabIndexBox == 2 || tabIndexBox == 3 || tabIndexBox == 4){
						self.html(processHtmlAfter(htmlValue));
					}

					self.cmd.range.moveToBookmark(bookmark);
					self.cmd.select();
					self.addBookmark();
					//self.focus();
					
	
					
					if(selectedNode != null){//更新
						if(tabIndexBox == 0){//输入密码可见
							selectedNode.attr('class','inputValue_10');
							selectedNode.attr('hide-type',10);
							selectedNode.attr('input-value',password);
						}else if(tabIndexBox == 1){//回复话题可见
							selectedNode.attr('class','inputValue_20');
							selectedNode.attr('hide-type',20);
							selectedNode.attr('input-value','');
						}else if(tabIndexBox == 2){//达到等级可见
							selectedNode.attr('class','inputValue_30');
							selectedNode.attr('hide-type',30);
							selectedNode.attr('input-value',grade);
							selectedNode.attr('description',gradeTag);
						}else if(tabIndexBox == 3){//积分购买可见
							selectedNode.attr('class','inputValue_40');
							selectedNode.attr('hide-type',40);
							selectedNode.attr('input-value',point);
						}else if(tabIndexBox == 4){//余额购买可见
							selectedNode.attr('class','inputValue_50');
							selectedNode.attr('hide-type',50);
							selectedNode.attr('input-value',amount);
						}
						self.hideDialog();
					}else{//添加
						if(tabIndexBox == 0){//输入密码可见
							html = "<hide class='inputValue_10' hide-type='10' input-value='"+password+"'></hide>";
						}else if(tabIndexBox == 1){//评论话题可见
							html = "<hide class='inputValue_20' hide-type='20' ></hide>";
						}else if(tabIndexBox == 2){//达到等级可见
							html = "<hide class='inputValue_30' hide-type='30' input-value='"+grade+"' description='"+gradeTag+"'></hide>";
						}else if(tabIndexBox == 3){//积分购买可见
							html = "<hide class='inputValue_40' hide-type='40' input-value='"+point+"'></hide>";
						}else if(tabIndexBox == 4){//余额购买可见
							html = "<hide class='inputValue_50' hide-type='50' input-value='"+amount+"'></hide>";
						}
					//	self.insertHtml(html).hideDialog().focus();
						self.hideDialog();

						var hideNode =  K(html, self.cmd.doc);//在光标处插入节点
						self.cmd.range.insertNode(hideNode[0]).selectNodeContents(hideNode[0]).collapse(false);
						
						self.cmd.select();
						self.cmd.range.insertNode(self.cmd.doc.createTextNode('\u200B'));
						self.focus();
					}
					
					
				},
				
				
				beforeRemove : function() {
					passwordBox.unbind();//移除所有事件函数
					gradeBox.unbind();
					pointBox.unbind();
					amountBox.unbind();
					
				}
			}
			
		}),
		div = dialog.div;
		
		
		passwordBox = K('[name="inputValue_10"]', div),//密码
		gradeBox = K('[name="inputValue_30"]', div),//达到等级
		pointBox = K('[name="inputValue_40"]', div),//积分
		amountBox = K('[name="inputValue_50"]', div);//金额
		tabIndexBox = 0;
		

		var tabs;
		tabs = K.tabs({
			src : K('.tabs', div),
			afterSelect : function(i) {
				tabIndexBox = i;
			}
		});
		
		
		var tabTitle1 = "";
		var tabTitle2 = "";
		var tabTitle3 = "";
		var tabTitle4 = "";
		var tabTitle5 = "";
		for(var i = 0;i < items.length;i++){
			var item = items[i];
			if(item == "hidePassword"){
				tabTitle1 = "输入密码可见";
			}
			if(item == "hideComment"){
				tabTitle2 = "回复话题可见";
			}
			if(item == "hideGrade"){
				tabTitle3 = "达到等级可见";
			}	
			if(item == "hidePoint"){
				tabTitle4 = "积分购买可见";
			}
			if(item == "hideAmount"){
				tabTitle5 = "余额购买可见";
			}
		}

		tabs.add({
			title : tabTitle1,//输入密码可见
			panel : K('.tab1', div)
		});
		tabs.add({
			title : tabTitle2,//回复话题可见
			panel : K('.tab2', div)
		});
		tabs.add({
			title : tabTitle3,//达到等级可见
			panel : K('.tab3', div)
		});
		tabs.add({
			title : tabTitle4,//积分购买可见
			panel : K('.tab4', div)
		});
		tabs.add({
			title : tabTitle5,//余额购买可见
			panel : K('.tab5', div)
		});
		
		//第一次打开时显示
		if(options.hideVisibleType != ""){
			if(options.hideVisibleType == 10){//输入密码可见
				tabIndex = 0;
			}else if(options.hideVisibleType == 20){//回复话题可见
				tabIndex = 1;
			}else if(options.hideVisibleType == 30){//达到等级可见
				tabIndex = 2;
			}else if(options.hideVisibleType == 40){//积分购买可见
				tabIndex = 3;
			}else if(options.hideVisibleType == 50){//余额购买可见
				tabIndex = 4;
			}
			
		}else{
			//显示第一个标签项内容		
			if(tabTitle5 !=""){
				tabIndex = 4;
			}
			if(tabTitle4 !=""){
				tabIndex = 3;
			}
			if(tabTitle3 !=""){
				tabIndex = 2;
			}
			if(tabTitle2 !=""){
				tabIndex = 1;
			}
			if(tabTitle1 !=""){
				tabIndex = 0;
			}
			
		}
		
		
		tabs.select(tabIndex);

		K('.tab'+(tabIndex+1), div).show();
		
	};
	self.plugin.hide = {
		edit : function() {
			var hide = self.plugin.getSelectedHide();
			
			self.plugin.hideDialog({
				
				selectedNode: hide ? hide : null,
				hideInputValue : hide ? hide.attr('input-value') : '',
				hideVisibleType : hide ? hide.attr('hide-type') : '',
				hidePassword : (hide && hide.attr('hide-type') == 10 )? hide.attr('input-value') : '',	
				hideGrade : (hide && hide.attr('hide-type') == 30 )? hide.attr('input-value') : '',	
				hidePoint : (hide && hide.attr('hide-type') == 40 )? hide.attr('input-value') : '',
				hideAmount : (hide && hide.attr('hide-type') == 50 )? hide.attr('input-value') : '',		
						
			});
		},
		'delete' : function() {
			
			var hide = self.plugin.getSelectedHide();
			
			hide.remove();
			self.addBookmark();
		}
	};
	
	//self.clickToolbar(name, self.plugin.hide.edit);
	self.clickToolbar(name, function() {
		
		self.plugin.hideDialog({
			selectedNode: null,
			hideInputValue : '',
			hideVisibleType : '',
			hidePassword : '',	
			hideGrade : '',	
			hidePoint : '',
			hideAmount : ''	
		});
		
	});
	

	
});


