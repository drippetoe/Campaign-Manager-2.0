/*
 * Copyright (c) 2011 Proximus Mobility, LLC 
 * @author Eric Johansson
 */
$(document).ready(function (){
    
    
    function validateName(){
        var name = $(this).val().replace(/[^a-z A-Z]/g, '');
        $(this).val(name);
        var inv = $("<div class=\"invalid\"><span>Invalid Name</span></div>");        
        if($(this).val().length<2){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateLength(){
        var inv = $("<div class=\"invalid\"><span>Invalid Length (min: 2 characters)</span></div>");        
        if($(this).val().length<2){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);                      
        }
    }
    
    function validateAge(){
        var re = /^[1-9]+[0-9]$/;
        var age = $(this).val().replace(/[^0-9]/g, '');
        age = age.substring(0, 3);
        $(this).val(age);
        var inv = $("<div class=\"invalid\"><span>Invalid Age</span></div>");
        var tmp = re.test($(this).val())
        if(!tmp){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateEmail() { 
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        var inv = $("<div class=\"invalid\"><span>Invalid Email</span></div>");
        if(!re.test($(this).val())){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validatePhone() { 
        var phone = $(this).val().replace(/[^0-9]/g, '');
        phone = phone.substring(0, 10);
        if(phone.length>0){
            var formatted = "("+phone.substring(0, 3);
            if(phone.length>=3){
                formatted += ") "+phone.substring(3, 6);
                if(phone.length>=6){
                    formatted += "-"+phone.substring(6, 10);
                }
            }
            $(this).val(formatted);
        }
        var inv = $("<div class=\"invalid\"><span>Invalid Phone Number</span></div>");
        if(phone.length!=10){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateNumeric(){
        var re = /^[0-9]*$/;
        var inv = $("<div class=\"invalid\"><span>Invalid Number</span></div>");
        var tmp = re.test($(this).val());
        if(!tmp || $(this).val()==""){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateAlpha(){
        var re = /^[a-z A-Z]*$/;
        var inv = $("<div class=\"invalid\"><span>Invalid Letters</span></div>");
        var tmp = re.test($(this).val());
        if(!tmp || $(this).val()==""){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateAlphanumeric(){
        var re = /^[a-z A-Z0-9]*$/;
        var inv = $("<div class=\"invalid\"><span>Invalid input; may only contain letters and numbers.</span></div>");
        var tmp = re.test($(this).val());
        if(!tmp || $(this).val()==""){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }
    
    function validateRegExp(){
       
        var re = new RegExp($(this).attr('pattern'),"g");
         
        var msg = $(this).attr('message');
        
        var inv = null;
        if(msg==""){
            inv = $("<div class=\"invalid\"><span>Invalid Input.</span></div>");
        }else    {
            inv = $("<div class=\"invalid\"><span>"+msg+"</span></div>");
        }
        
        //inv = $("<div class=\"invalid\"><span>Invalid Input.</span></div>");
        var tmp = re.test($(this).val());
        
        if(!tmp || $(this).val()==""){
            if(!$(this).next().is("div.invalid")){
                $(inv).css('color','red').insertAfter(this);
            }    
            $(this).data('valid',false);
        }else {
            if($(this).next().is("div.invalid")){
                $(this).next().remove();                
            }
            $(this).data('valid',true);
                      
        }
    }

    
    
    
    $('form > * :input[validate*="length"]').each(function() {
        $(this).bind('keyup',validateLength);
    });
    $('form > * :input[validate*="numeric"]').each(function() {
        $(this).bind('keyup',validateNumeric);
    });
    $('form > * :input[validate*="alpha"]').each(function() {
        $(this).bind('keyup',validateAlpha);
    });
    $('form > * :input[validate*="alphanumeric"]').each(function() {
        $(this).bind('keyup',validateAlphanumeric);
    });
    $('form > * :input[validate*="name"]').each(function() {
        $(this).bind('keyup',validateName);
    });
    $('form > * :input[validate*="age"]').each(function() {
        $(this).bind('keyup',validateAge);
    });
    $('form > * :input[validate*="email"]').each(function() {
        $(this).bind('keyup',validateEmail);
    });
    $('form > * :input[validate*="phone"]').each(function() {
        $(this).bind('keyup',validatePhone);
    });
    $('form > * :input[validate*="regexp"]').each(function() {
        $(this).bind('keyup',validateRegExp);
    });
    
         
         
    $(':input[type="submit"]').click(function(event) {
        event.preventDefault();
        var validated=true;
        $('form > * :input[validate]').each(function() {
            $(this).trigger('keyup',event);
            if($(this).data('valid')==false){
                validated = false;
            }
            if($(this).attr('validate')=="phone"){
                var phone = $(this).val().replace(/[^0-9]/g, '');
                $(this).val(phone);
            }
        });
        if(validated){
            $("form").submit();
        }
    });     
    
    $(':input[type="image"]').click(function(event) {
        event.preventDefault();
        var validated=true;
        $('form > * :input[validate]').each(function() {
            $(this).trigger('keyup',event);
            if($(this).data('valid')==false){
                validated = false;
            }
            if($(this).attr('validate')=="phone"){
                var phone = $(this).val().replace(/[^0-9]/g, '');
                $(this).val(phone);
            }
        });
        if(validated){
            $("form").submit();
        }
    }); 
});
