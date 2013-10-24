<div id="footer">
  <center>
    <nav>
      <a href="/<?php echo $COUNTRY_id; ?>"><?php echo $message_home; ?></a><span class="linkDivider">&nbsp;|&nbsp;</span>    
      <a href="/<?php echo $COUNTRY_id; ?>/<?php echo $locale; ?>/contact"><?php echo $message_contact; ?></a><span class="linkDivider">&nbsp;|&nbsp;</span>
      <a href="/<?php echo $COUNTRY_id; ?>/<?php echo $locale; ?>/faq"><?php echo $message_faq; ?></a><span class="linkDivider">&nbsp;|&nbsp;</span>
      <a target="_BLANK" href="http://proximusmobility.com/terms/"><?php echo $message_terms; ?></a><span class="linkDivider">&nbsp;|&nbsp;</span>
      <a target="_BLANK" href="http://valutext.com/privacy-policy/"><?php echo $message_privacy; ?></a>
    </nav><br/>
    <p>Copyright &copy; 2012 ValuText, LLC</p>
  </center>
</div>
<!-- google analytics -->
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-27077184-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

<?php
//php footer
?>
