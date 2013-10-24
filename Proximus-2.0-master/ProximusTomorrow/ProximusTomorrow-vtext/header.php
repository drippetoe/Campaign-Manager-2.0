<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="x-ua-compatible" content="ie=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1" />
    <meta name="author" content="ValuText" />
    <meta name="copyright" content="ValuText, LLC" />
    <meta name="description" content="ValuText, Real-time deals at your fingertips." />
    <title><?php echo $title; ?></title>
    <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link rel="stylesheet" media="all" href="/assets/css/<?php echo $lang; ?>/less.css"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="/assets/js/jquery-1.8.1.min.js">\x3C/script>')</script>
    <script>
      $(document).ready(function(){

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
          

      });        
    </script>
  </head>
  <body lang="<?php echo $lang; ?>">
    <div id="header">

      <div><img width="180px"  src="/assets/images/logo_trans3.png"/></div>
      <span style="background-color: white;"><?php var_dump($_GET); ?></span>
    </div>