<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="x-ua-compatible" content="ie=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1" />
    <meta name="author" content="ValuText" />
    <meta name="copyright" content="ValuText, LLC" />
    <meta name="description" content="ValuText, Real-time deals at your fingertips." />
    <title>ValuText, LLC</title>
    <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link rel="stylesheet" media="all" href="/assets/css/<?php echo $CSS; ?>/less.css"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="/assets/js/jquery-1.8.1.min.js">\x3C/script>')</script>
    <script src="/assets/js/geo-min.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">var switchTo5x=true;</script>
    <script type="text/javascript" src="http://w.sharethis.com/button/buttons.js"></script>
    <script type="text/javascript">stLight.options({publisher: "0be90d8a-f1df-4725-af70-89d60a30e5b6"}); </script>
    <script type="text/javascript" src="http://s.sharethis.com/loader.js"></script>
    <script src="/assets/js/vtext.js" type="text/javascript" charset="utf-8"></script>

  </head>
  <body lang="<?php echo $locale; ?>" onload="initialise()">

    <div id="header">
      <div><img width="180px"  src="/assets/images/logo_trans3.png"/></div>
    </div>
    <div id="ad1space">
      <?php include 'adspace.php'; ?>
    </div>
    <div id="content">
      <?php
      $to = 'ejohansson@proximusmobility.com';
      $subject = 'the subject';
      $message = 'hello';
      $headers = 'From: web@proximusmobility.com' . "\r\n" .
          'Reply-To: web@proximusmobility.com' . "\r\n" .
          'X-Mailer: PHP/' . phpversion();

      mail($to, $subject, $message, $headers);
      ?>
    </div>
    <?php include 'footer.php'; ?>

  </body>
</html>
