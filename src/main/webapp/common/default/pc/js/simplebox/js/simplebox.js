'use strict';

(function ($) {
    $.fn.simplebox = function(options) {
        var settings = $.extend({
            fadeSpeed: 400,
            darkMode: false
        }, options);

        // Helper Variables
        var $body = $("body");
        var $overlay = $('<div id="overlay"></div>');
        var $cross_box = $('<div class="cross-box"></div>');
        var $cross = $('<div class="cross"></div>');
        
        var $image_box = $('<div class="image-box"></div>');
        var $image = $("<img class='slb'>");
        var fadeSpeed = settings.fadeSpeed;
        var lbIsOpen = false;

        $cross_box.append($cross);
        
        // Modifying theme based on preference
        if (settings.darkMode) {
            $overlay.css('background-color', 'black');
            $cross.addClass('cross--dark');
            $('.slb').addClass('slb--invert');
            $image.addClass('slb--invert');
        } else {
            $overlay.css({"background-color":"rgba(0,0,0,.5)","cursor":"zoom-out"});

            $cross.addClass('cross--light');
        }

        //判断是否有滚动条的方法
        var hasScrollbar = function() {
        	return document.body.scrollHeight > (window.innerHeight || document.documentElement.clientHeight);
        }

        //计算滚动条宽度的方法
        //新建一个带有滚动条的 div 元素，通过该元素的 offsetWidth 和 clientWidth 的差值即可获得
        var getScrollbarWidth = function(){
        	var scrollDiv = document.createElement("div");
        	scrollDiv.style.cssText = 'width: 99px; height: 99px; overflow: scroll; position: absolute; top: -9999px;';
        	document.body.appendChild(scrollDiv);
        	var scrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth;
        	document.body.removeChild(scrollDiv);
        	return scrollbarWidth;
        }
        
        // Function for hiding the overlay.
        var hideOverlay = function() {
        	$overlay.css({"overflow": "hidden"});//先隐藏图片上的滚动条防止闪烁，后面再关闭。'功能标记一'
        	
        	
        	if(navigator.userAgent.indexOf("MSIE") != -1) {//IE8不支持fadeOut
        		$overlay.hide();
        	}else{
        		$overlay.fadeOut(fadeSpeed);
        	}
            $image.removeClass('slb--opened');
            lbIsOpen = false;
       
        	
            $("body").children("div").each(function(i,n){
            	var obj = $(n);
            	
            	if(obj.attr("id") !="overlay" && obj.css('position') == 'absolute'){//如果body子元素为position: absolute属性
            		obj.css({"right":""});
            	}
			});
            
        	$body.css({"overflow": "auto","margin-right":""});
            
        };
        
        
       

        // When X is clicked or user clicks on the overlay div
        // Hide lightbox
        $overlay.click(hideOverlay);
        $cross.click(hideOverlay);

        // Attaching ESC listener
        $(document).keyup(function(e) {
             if (e.keyCode == 27 && lbIsOpen) {
            	 
                hideOverlay();
            }
        });

        return this.each(function() {
            $overlay.append($cross_box);
            var $this = $(this);

            // When the image is clicked
            $this.click(function() {
                lbIsOpen = true;
                $overlay.css({"overflow": "auto"});//设置图片上的允许显示滚动条，对应上面的'功能标记一'
                //滚动条宽度
                var scrollbarWidth = 0;
                if(hasScrollbar()){
                	scrollbarWidth = getScrollbarWidth();
                }
                $body.css({"overflow": "hidden","margin-right":scrollbarWidth+"px"});
               // $body.css({"overflow": "hidden"});
                $body.css({"overflow": "hidden"});
                
                $("body").children("div").each(function(i,n){
                	var obj = $(n);
                	if(obj.attr("id") !="overlay" && obj.css('position') == 'absolute'){//如果body子元素为position: absolute属性
                		
                		obj.css({"right":"17px"});
                	}
                	
				});
                
                var $this = $(this);
                var imageSRC = $this.attr("src");
                $image.attr("src", imageSRC);
            //    $image.css("max-height", "80%");
                $image.addClass('pop-in');
                $image.removeClass('pop-out');
                $image.addClass('center');
                $image.addClass('slb--opened');

                $overlay.css('pointer-events', 'initial');

                
                $image_box.append($image);
                $overlay.append($image_box);
                $body.append($overlay);

                // Show all the things!
                $overlay.fadeIn(fadeSpeed);
             
            });
        });
    };
}(jQuery));
