//当前JS文件路径(这行代码必须放到文件的开头，让文件加载的时候就立即执行，这样页面中的script元素中，当前文件恰好是最后一个script)
var _imageZoom_jsPath = $("script").last().attr("src");


/**
 * parentcontent  //父容器
 * boxcontent   // 评论区图片展示区域
 */
function commentMove(parentcontent, boxcontent,id) {
	
	
	var style = new Object();
	style.activeClass = "tm-current current_"+id;
	style.nextButton = ".navright_"+id;
	style.prevButton = ".navleft_"+id;
	style.id = id;
	this.obj = style;
    this.parentcontent = parentcontent;
    this.boxcontent = boxcontent;
}

commentMove.prototype = {
    init: function () {
        var that = this;
        
        that.start();
        this.lefthover();
        this.righthover();
        this.leftclick();
        this.rightclick();
    },
    start: function () {
        var that = this;
        
        $(that.parentcontent + ' li').click(function () {
            $(this).toggleClass(that.obj.activeClass).siblings().removeClass(that.obj.activeClass);
            var src =  $('.current_' + that.obj.id).attr('data-src');
          
            that.setImageSize(that,src, function(img){//计算图片大小
            	 $(that.boxcontent + " img").attr('src', src);
        	});
            if (!src) {
                $(that.boxcontent).css({ "width": 0, "height": 0,"padding-bottom": 0});
            }
        })
    },
    lefthover: function () {
    	
        var that = this;
        $(that.obj.prevButton).hover(function () {
        	var path = that.getJsPath();
            var index = $(that.parentcontent + ' li').index($(that.parentcontent + ' li.current_' + that.obj.id));
            if (index < 1) {
            	$(that.obj.prevButton).css({ "cursor": "auto"});
            } else {
                $(that.obj.prevButton).css({ "cursor": "url('"+path+"/img/pre.cur'),auto"})
            }
        }, function () {
        	$(that.obj.prevButton).css({ "cursor": "auto"});
        })
    },
    righthover: function () {
    	
        var that = this;
        $(that.obj.nextButton).hover(function () {
        	var path = that.getJsPath();
            var index = $(that.parentcontent + ' li').index($(that.parentcontent + ' li.current_' + that.obj.id));
           
            if (index >= $(that.parentcontent + ' li').length - 1) {
            	$(that.obj.nextButton).css({ "cursor": "auto"});
            } else {
            	$(that.obj.nextButton).css({ "cursor": "url('"+path+"/img/next.cur'),auto"})
            }
        }, function () {
        	$(that.obj.nextButton).css({ "cursor": "auto"});
        })
    },
    leftclick: function () {
    	
        var that = this;
        $(that.obj.prevButton).click(function () {
        	
            var index = $(that.parentcontent + ' li').index($(that.parentcontent + ' li.current_' + that.obj.id));

            index--;
            if (index >= 0) {
                $(that.parentcontent + ' li').eq(index).toggleClass(that.obj.activeClass).siblings().removeClass(that.obj.activeClass);
               
                var src =  $(that.parentcontent + ' li').eq(index).attr('data-src');
                
                that.setImageSize(that,src, function(img){//计算图片大小
                	
                	$(that.boxcontent + " img").attr("src", src);
            	});

            }
            var path = that.getJsPath();
            if (index < 1) {
                index = 0;
                $(that.obj.prevButton).css({ "cursor": "auto"});
                return;
            }
        })
    },
    rightclick: function () {
        var that = this;
        $(that.obj.nextButton).click(function () {
            var index = $(that.parentcontent + ' li').index($(that.parentcontent + ' li.current_' + that.obj.id));
            index++;
            $(that.boxcontent + " img").attr("src", $(that.parentcontent + ' li').eq(index).attr('data-src'))

           
            var src =  $(that.parentcontent + ' li').eq(index).attr('data-src');
                
            that.setImageSize(that,src, function(img){//计算图片大小
            	
            	$(that.boxcontent + " img").attr("src", src);
        	});
            
            var path = that.getJsPath();
            $(that.parentcontent + ' li').eq(index).toggleClass(that.obj.activeClass).siblings().removeClass(that.obj.activeClass);
            if (index >= $(that.parentcontent + ' li').length - 1) {	
            	$(that.obj.nextButton).css({ "cursor": "auto"});
            }
        })
    },
    setImageSize: function (that,src,callback) {
    	if(src != undefined){
    		//获取放大框的宽度
        	var width = $(that.parentcontent).width();
        	
            var img = new Image();
            img.onload = function () {
                var imageWidth = img.width;
                var imageHeight = img.height;
                
                //最大宽度
                if(imageWidth >width){
                	imageWidth = width;
                //	imageHeight = img.height/(img.width/imageWidth);
                	imageHeight =  width*img.height/img.width;
                }
                
                
                $(that.boxcontent).css({ "width": imageWidth, "height": imageHeight,"padding-bottom": 8+'px'})
                $(that.obj.prevButton).css({ "width": imageWidth / 2, "height": imageHeight})

                $(that.obj.nextButton).css({ "width": imageWidth / 2, "height": imageHeight})
     
                callback(img);
            }
            img.src = src;//src 属性一定要写到 onload 的后面，否则程序在 IE 中会出错
    		
    	}
    },
    getJsPath: function () {//获取当前JS文件路径
    	var jsPath = _imageZoom_jsPath
    	var jsPath_array = _imageZoom_jsPath.split("/");
    	jsPath_array.pop();//删除最后一个元素
    	
    	var path = jsPath_array.join("/");//路径 common/default/pc/js/imageZoom
    	return path;
    }
    
    
}


