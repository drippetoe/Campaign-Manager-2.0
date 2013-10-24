<?php
$randval = rand(1000000000, 99999999999)
?>
<script type='text/javascript'>
  <!--//<![CDATA[
  var m3_u = (location.protocol=='https:'?'https://good.vtext.me/srv/www/delivery/ajs.php':'http://good.vtext.me/srv/www/delivery/ajs.php');
  var m3_r = Math.floor(Math.random()*99999999999);
  if (!document.MAX_used) document.MAX_used = ',';
  document.write ("<scr"+"ipt type='text/javascript' src='"+m3_u);
<?php if (( $COUNTRY_id == "PR" ) || ( $locale == "es" )) {
?>
  document.write ("?zoneid=2");
<?php
}
else
{
?>
  document.write ("?zoneid=1");
<?php
}
?>
  document.write ('&cb=' + m3_r);
  if (document.MAX_used != ',') document.write ("&exclude=" + document.MAX_used);
  document.write (document.charset ? '&charset='+document.charset : (document.characterSet ? '&charset='+document.characterSet : ''));
  document.write ("&loc=" + escape(window.location));
  if (document.referrer) document.write ("&referer=" + escape(document.referrer));
  if (document.context) document.write ("&context=" + escape(document.context));
  if (document.mmm_fo) document.write ("&mmm_fo=1");
  document.write ("'><\/scr"+"ipt>");
  //]]>-->
</script>
<noscript>
<a href='http://good.vtext.me/srv/www/delivery/ck.php?n=acb6bb3&;cb=<?php echo $randval; ?>' target='_blank'>
  <img src='http://good.vtext.me/srv/www/delivery/avw.php?zoneid=&;cb=<?php echo $randval; ?>&n=acb6bb31' border='0' alt='' />
</a>
</noscript>


<!-- -->

