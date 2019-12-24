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
        var $cross = $('<div class="cross"></div>');
        var $image_box = $('<div class="image-box"></div>');
        var $image = $("<img class='slb'>");
        var fadeSpeed = settings.fadeSpeed;
        var lbIsOpen = false;

        // Modifying theme based on preference
        if (settings.darkMode) {
            $overlay.css('background-color', 'black');
            $cross.addClass('cross--dark');
            $('.slb').addClass('slb--invert');
            $image.addClass('slb--invert');
        } else {
            $overlay.css({"background-color":"white","cursor":"zoom-out"});
            $cross.addClass('cross--light');
        }

        // Function for hiding the overlay.
        var hideOverlay = function() {
        	if(navigator.userAgent.indexOf("MSIE") != -1) {//IE8不支持fadeOut
        		$overlay.hide();
        	}else{
        		$overlay.fadeOut(fadeSpeed);
        	}
            $image.removeClass('slb--opened');
            lbIsOpen = false;
            $body.css("overflow", "auto");
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
            $overlay.append($cross);
            var $this = $(this);

            // When the image is clicked
            $this.click(function() {
                lbIsOpen = true;
                $body.css("overflow", "hidden");

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
