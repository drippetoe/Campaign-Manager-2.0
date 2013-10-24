CONTENT = """# lighttpd configuration file
#
# See http://www.lighttpd.net/ for documentation and full config file example.

PROXIMUS_HTTP_CONFIG_HERE


PROXIMUS_VIRTUAL_DIR

#######################################
##Virtual Hosting for Proximus Setup
## 1. proxsetup.com   
## 2. setup.proximusmobility.com       
#######################################  
            
$HTTP["host"] =~ "(^|\.)proxsetup\.com$" {                   
 server.document-root = "/home/proximus/bin/setup"                           
}            
         
$HTTP["host"] =~ "(^|\.)setup.proximusmobility\.com$" {      
 server.document-root = "/home/proximus/bin/setup"           
}



## END Virtual Hosting 

############ Options you really have to take care of ####################

## modules to load
# at least mod_access and mod_accesslog should be loaded
# all other module should only be loaded if really neccesary
# - saves some time
# - saves memory
server.modules              = (
    "mod_access",
    "mod_accesslog",
    "mod_expire",
    "mod_alias",
    "mod_auth",
    "mod_cgi",
#    "mod_compress",
#    "mod_evasize",
#    "mod_evhost",
#    "mod_extforward",
#    "mod_fastcgi",
#    "mod_flv-streaming",
#    "mod_proxy",
#    "mod_scgi",
#    "mod_secdownload",
#    "mod_setenv",
     "mod_simple_vhost",
#    "mod_status",
#    "mod_userdir",
#    "mod_usertrack",
#    "mod_webdav",
)

## a static document-root, for virtual-hosting take look at the
## server.virtual-* options
#server.document-root = "/var/www/captive/"

## accesslog module
# accesslog.filename = "/usr/local/captiveportal/captiveportal_access.log"
accesslog.use-syslog = "disable"
# accesslog.format            = "%h %V %M %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\""
#accesslog.format = "[%h] %t [%r] [%s] [%{User-Agent}i] [%V] [%M]"

## where to send error-messages to
server.errorlog = "/home/proximus/logs/lighttpd_error.log"
server.errorlog-use-syslog = "disable"

## files to check for if .../ is requested
index-file.names            = ( "index.cgi", "index.html", "index.htm", "index.do", "index.php" )

## mimetype mapping
mimetype.assign             = (
    ".pdf"          =>      "application/pdf",
    ".sig"          =>      "application/pgp-signature",
    ".spl"          =>      "application/futuresplash",
    ".class"        =>      "application/octet-stream",
    ".ps"           =>      "application/postscript",
    ".torrent"      =>      "application/x-bittorrent",
    ".dvi"          =>      "application/x-dvi",
    ".gz"           =>      "application/x-gzip",
    ".pac"          =>      "application/x-ns-proxy-autoconfig",
    ".swf"          =>      "application/x-shockwave-flash",
    ".tar.gz"       =>      "application/x-tgz",
    ".tgz"          =>      "application/x-tgz",
    ".tar"          =>      "application/x-tar",
    ".zip"          =>      "application/zip",
    ".mp3"          =>      "audio/mpeg",
    ".m3u"          =>      "audio/x-mpegurl",
    ".wma"          =>      "audio/x-ms-wma",
    ".wax"          =>      "audio/x-ms-wax",
    ".ogg"          =>      "application/ogg",
    ".wav"          =>      "audio/x-wav",
    ".gif"          =>      "image/gif",
    ".jpg"          =>      "image/jpeg",
    ".jpeg"         =>      "image/jpeg",
    ".png"          =>      "image/png",
    ".xbm"          =>      "image/x-xbitmap",
    ".xpm"          =>      "image/x-xpixmap",
    ".xwd"          =>      "image/x-xwindowdump",
    ".css"          =>      "text/css",
    ".html"         =>      "text/html",
    ".do"           =>      "text/html",
    ".htm"          =>      "text/html",
    ".js"           =>      "text/javascript",
    ".asc"          =>      "text/plain",
    ".c"            =>      "text/plain",
    ".cpp"          =>      "text/plain",
    ".log"          =>      "text/plain",
    ".conf"         =>      "text/plain",
    ".text"         =>      "text/plain",
    ".txt"          =>      "text/plain",
    ".dtd"          =>      "text/xml",
    ".xml"          =>      "text/xml",
    ".mpeg"         =>      "video/mpeg",
    ".mpg"          =>      "video/mpeg",
    ".mov"          =>      "video/quicktime",
    ".qt"           =>      "video/quicktime",
    ".avi"          =>      "video/x-msvideo",
    ".asf"          =>      "video/x-ms-asf",
    ".asx"          =>      "video/x-ms-asf",
    ".wmv"          =>      "video/x-ms-wmv",
    ".webm"         =>      "video/webm",
    ".weba"         =>      "audio/webm",
    ".mp4"          =>      "video/mp4",
    ".bz2"          =>      "application/x-bzip",
    ".tbz"          =>      "application/x-bzip-compressed-tar",
    ".tar.bz2"      =>      "application/x-bzip-compressed-tar"
)

## deny access the file-extensions
#
# ~    is for backupfiles from vi, emacs, joe, ...
# .inc is often used for code includes which should in general not be part
#      of the document-root
url.access-deny             = ( "~", ".inc" )

## which extensions should not be handle via static-file transfer
#
# .php, .pl, .fcgi are most often handled by mod_fastcgi or mod_cgi
static-file.exclude-extensions = ( ".php", ".pl", ".fcgi", ".cgi" )


######### Options that are good to be but not neccesary to be changed #######

## bind to port (default: 80)
server.port                 = 80

## bind to localhost (default: all interfaces)
#server.bind                 = "127.0.0.1"

## aliases for files outside doc-root
alias.url = (
    "/errors/" => "/home/proximus/config/www-errors/"
)

## error-handler for status 404
server.error-handler-404    = "/errors/404.php"

## to help the rc.scripts
server.pid-file             = "/var/run/lighttpd-captive.pid"

## virtual directory listings
dir-listing.activate        = "disable"
dir-listing.encoding        = "utf-8"

$HTTP["url"]                == "/cgi-bin/" {
    dir-listing.activate    = "disable"
}

### only root can use these options
#
## chroot() to directory (default: no chroot() )
#server.chroot               = "/"

## change uid to <uid> (default: don't care)
#server.username             = "www"

## change uid to <uid> (default: don't care)
#server.groupname            = "www"


#### CGI module
cgi.assign                  = (
    "/setup"     =>  "",
    "/login"     =>  "",
    "/logout"    =>  "",
    "/redirect"  =>  "",
    ".sh"        =>  "",
    ".cgi"       =>  "",
    ".php"       =>  "PROXIMUS_PHP_PATH",
    ".pl"        =>  "/usr/bin/perl",
)

#### auth module
auth.backend                    = "htpasswd"
auth.backend.htpasswd.userfile  = "/etc/htpasswd"

auth.require                = (
    "/setup"     => (
        "method"     => "basic",
        "realm"      => "AS setup",
        "require"    => "valid-user",
    )
)

#### expire module
expire.url                  = (
    "/index.html" => "modification 0 seconds",
    "/"           => "modification 0 seconds",
)
# eof
"""
