CONTENT = """
#    NOTE:
#
# From version 1.10, dnsmasq itself reads a
# config file at /etc/dnsmasq.conf so you may
# want to set options there rather than here.
#
# This file now has only three  functions,
# to completely disable starting dnsmasq,
# to set DOMAIN_SUFFIX by running `dnsdomainname`
# and to select an alternative config file
# by setting DNSMASQ_OPTS to --conf-file=<file>
#
# For upgraders, all the shell variables set here in previous versions
# are still honored by the init script so if you just keep your old
# version of this file nothing will break.

#DOMAIN_SUFFIX=`dnsdomainname`
#DNSMASQ_OPTS="--conf-file=/etc/dnsmasq.alt"
# Whether or not to run the dnsmasq daemon; set to 0 to disable.
ENABLED=1
DNSMASQ_OPTS="--cache-size=200 --interface=PROXIMUS_DNS_INTERFACE --listen-address=192.168.45.3 --conf-file=PROXIMUS_DNSMASQ_CONFIG_FILE"
##
# CLIENT GENERATED FILE
##
"""