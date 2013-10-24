$(document).ready(function(){
    $.ajaxSetup({
        url: 'locationUpdate.php'
    });
    var last_position = null;
    function show_position(p)
    {
        
        if(last_position && last_position.coords.latitude==p.coords.latitude && last_position.coords.longitude==p.coords.longitude)
        {
            //document.getElementById('info').innerHTML="User has not moved";
            return;
        }	
        last_position=p;
        //document.getElementById('info').innerHTML="latitude="+p.coords.latitude.toFixed(2)+" longitude="+p.coords.longitude.toFixed(2);
        //var pos=new google.maps.LatLng(p.coords.latitude,p.coords.longitude);
        //alert('lat='+p.coords.latitude.toFixed(2)+';lon='+p.coords.longitude.toFixed(2));
        
        
        $.ajax({
            data: {
                'lat':p.coords.latitude.toFixed(2),
                'long':p.coords.longitude.toFixed(2)
            }
        }).success(function(data) { 
            if(data.length>0){   
                var loc = window.location.href;
                if(data!="NULL"){
                    if(window.location.href.indexOf(data)<0){
                        window.location.href  = window.location.href+data;
                    }         
                }
            }else{
                alert("Unable to find closest location")
            }
        }); 
       
	
    }
    function retrieve()
    {
        geo_position_js.getCurrentPosition(show_position,error_callback,{
            enableHighAccuracy:true
        });
    }

    function success_callback(p)
    {
    //alert('lat='+p.coords.latitude.toFixed(2)+';lon='+p.coords.longitude.toFixed(2));
    }
		
    function error_callback(p)
    {
        document.getElementById('info').innerHTML="Couldn't get location";
          
    }
    function enableMyLocation(event){
        event.preventDefault();       
        if(geo_position_js.init()){
            geo_position_js.getCurrentPosition(show_position,error_callback,{
                enableHighAccuracy:true
            });
        //setInterval(retrieve,1000);
        }else{
            alert("Sorry we were not able to locate your closest location!");
        }  
          
    }
              
        
    function listFilter(){
        var filter = $(this).val();
        if (filter) {
            $("div.property:not(:contains(" + filter + "))").slideUp();
            $("div.property:contains(" + filter + ")").slideDown();
        } else {
            $("div.property").slideDown();
        }
    }
    $("#filter").bind('change',listFilter).bind("keyup",listFilter);
          
    $("#enableMyLocation").bind("click", enableMyLocation);
        
        
}); 
