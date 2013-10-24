
/**
 * Basic jQuery Validation Form Demo Code
 * Copyright Sam Deering 2012
 * Licence: http://www.jquery4u.com/license/
 */
(function( $ ) {
	$(document).ready(function(){
			$.validator.addMethod("phone", function(value, element) {					
				var str = value;
				var phoneNumberPattern = /^\(?(\d{3})\)?[- ]?(\d{3})[- ]?(\d{4})$/;
				
				if (phoneNumberPattern.test(str) ){
				return true;
				} else {
					return false;
				}
			},
			"Please enter a valid phone number.");	

			//form validation rules
			$("#register-form > form" ).validate({
				rules: {
					field1: "required",
					field2: {
						required: true,
						email: true
					},
					field3: {
						required: true,
						phone: true

					}
				},
				messages: {
					field1: "Please enter your name.",
					field2: "Please enter a valid email address.",
					field3: "Please enter a valid phone number."
					
				},
				submitHandler: function(form) {
					form.submit();
				}
			});
			
	});
})( jQuery );

        
        
   
