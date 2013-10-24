CONTENT = """# Enable captiveportal:
ENABLE=Yes

# Allow captive portal internet access:
INETACCESS=Yes

# Proximus Limited Internet Access
LIMITEDINTERNET=Yes

# Time (in minutes) which authorized captive portal users are allowed to access internet without need of reauthorization
INETTIME=30

# How often INETTIME is checked (in minutes)
CHECKTIME=10

# Standalone mode means that there is no connectivity to internet
STANDALONE_MODE=No

# Forward DNS connections to captivednsd (DNS server which always answers the same answer). Captivednsd is not used if INETACCESS is enabled
USECAPTIVEDNS=No

# This is affects only if USECAPTIVEDNS is No. If yes, captive portal clients' dns requests are routed to dns server configured by /etc/resolv.conf
ALLOWDNSQUERIES=Yes

# This DROPs connections to privete networks (exceptions are DNS queries to configured DNS server)
DENYPRIVATENETWORKS=Yes

# Define if local lighttpd or remote www server is used
USE_LOCAL_SERVER=Yes

# Remote web server's ip if local lighttpd is not used
REMOTE_SERVER=127.0.0.1

# Remote web server's port if local lighttpd is not used
REMOTE_PORT=80

# Enable push login-window to iPhone and BlackBerry
ENABLE_PUSH_BROWSER=Yes

# captive bridge interface ip and netmask
CAPTIVEIP=192.168.45.3
CAPTIVENETMASK=255.255.255.0

# captive interface name
GW_INTERFACE=captive

# uplink interface for Internet access
EXT_INTERFACE=PROXIMUSEXTHERE

# redirect page URL after log in
REDIRECT_URL=http://www.proximusmobility.com/
"""
