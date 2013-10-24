/*run me*/
jQuery.noConflict();
(function( $ ) {
    
    $(document).ready(function(){
        $("a.button").each(function(){            
            var page = "index.jsp?pageChosen="+$(this).attr('href');               
            $(this).attr('href',page);
        });
        
        $("a.logoButton").click(function(event) {
            event.preventDefault();
            event.stopPropagation();
        });
		
        $("a.facebookButton").click(function(event) {
            event.preventDefault();
            event.stopPropagation();
        });
        
        var $overlay = $("#overlay");
        var $overlay_info = $("#overlay_info");
        
        
        $overlay.hide();
        $overlay_info.hide();
        
        
        function loadingOverlay() {
            
            $overlay.show();
            $overlay_info.show();
            $overlay.css({
                opacity:0.5
            });
            $overlay_info.css({
                opacity:1.0
            });
            $overlay.blur();
            $overlay_info.blur();
        }
        
        var $form = $("form");
        
        $(".TextEditor").each(function(index) {
            var $hidden = $(this).children("input");
            var $viewable = $(this).children("div");
            var $editor = $(this).children("textarea");	
            if($hidden.val().length>0){		
                $editor.val($hidden.val());
                $editor.hide();
                $viewable.show();
            }else {
                $editor.hide();
                $viewable.text("Click To Edit!").show();
                $viewable.height($(this).height()-4);
                $viewable.addClass("editMe");
            }	
            $viewable.click(function (event){					
                $(this).hide();
                $editor.show();				
                $editor.focus();
            });
            $editor.bind('focusout', function (event){
                loadingOverlay();
                $(this).hide();
                $hidden.val($(this).val());
                $viewable.hide();
                if($("#pageChosen").val() == "null") {
                    $("#pageChosen").val("index.html");
                }
                $("input[type=submit]").trigger('click');		
            });
        });
		

        $(".FacebookButton").each(function(index) {
            var $hidden = $(this).children("input");
            var $viewable = $(this).children("div.accessInternet");
			var $viewableButton = $viewable.children("a");
			var $editorDiv = $(this).children("div.editMeFace");
			var $editor = $('div.editMeFace').children("input[type=text]");
            var $label = $('div.editMeFace').children("label");	
			
            if($hidden.val().length>0){		
				$editorDiv.hide();
                $editor.val($hidden.val());
                $editor.hide();
				$label.hide();
                $viewable.show();
				
            }else {
				$editorDiv.hide();
                $editor.hide();
				$label.hide();
                $viewable.height($(this).height()-4);
               //$viewable.addClass("editMeFace");
				
            }	
			
			$viewableButton.click(function (event){					
                $(this).hide();
				$editorDiv.show();
                $editor.show();	
			    $label.show();
                $editor.focus();
            });
            $editor.bind('focusout', function (event){
                loadingOverlay();
                $(this).hide();
                $hidden.val($(this).val());
                $viewable.hide();
                if($("#pageChosen").val() == "null") {
                    $("#pageChosen").val("index.html");
                }
                $("input[type=submit]").trigger('click');		
            });
			
			
            $viewable.click(function (event){					
                $(this).hide();
				$editorDiv.show();
                $editor.show();	
			    $label.show();
                $editor.focus();
            });
            $editor.bind('focusout', function (event){
                loadingOverlay();
                $(this).hide();
                $hidden.val($(this).val());
                $viewable.hide();
                if($("#pageChosen").val() == "null") {
                    $("#pageChosen").val("index.html");
                }
                $("input[type=submit]").trigger('click');		
            });
        });
        
        
        $(".ImageUploader").each(function(index) {
            
            var $image = $(this).children("img");
            if($image.attr('src')==""){
                $image.attr('src','images/placeholder.png');
            }
            var $input = $(this).children("input[type=file]");
            var $submit = $("input[type=submit]");
            $submit.hide();       
            $(this).click(function(event) {
                $input.click(function(event) {
                    event.stopPropagation();
                });
                $input.trigger('click');
            }).hover(function() {
                $(this).fadeTo(250, 0.5);  
            }, function() {
                $(this).fadeTo(250, 1);
            });
            $input.change(function(){
                loadingOverlay();
                $form.attr('action','upload');
                $submit.trigger('click');
            
            });
        });
        
        
        $(".ui-state-error").delay(3000).fadeOut(500);
    
    });
})( jQuery );