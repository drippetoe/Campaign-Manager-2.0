#!/usr/bin/env python

import os, sys, subprocess

if len(sys.argv) != 3:
    print "Usage: %s <add|remove> <domain>" % sys.argv[0]
    exit()

action = sys.argv[1]
action_domain = sys.argv[2].strip()


print "%s %s" % ( action, action_domain )

folder = "/etc/dnsmasq.d"
passthrough_dns = "8.8.8.8"

############################################
"""
server=/homedepot.com/8.8.8.8
address=/#/192.168.3.1
"""
############################################
passthrough_domains = {}
masq_files = os.listdir(folder)
print "files:", masq_files
for file in masq_files:
    path = folder + "/" + file
    inf = open(path, "r")
    for line in inf.readlines():
        if line.find('server') == 0:
            spl = line.split("/")
            domain = spl[1]
            passthrough_domains[domain] = 1
    inf.close()
    os.remove(path)

if ( action == 'add' ):
    passthrough_domains[action_domain] = 1
elif ( action == 'remove' and passthrough_domains.has_key(action_domain) ):
        del passthrough_domains[action_domain]
else:
    print "Domains found: ", passthrough_domains.keys()
    exit()


outf = open(folder + "/" + "captive_passthrough.conf", "w")
for key in passthrough_domains.keys():
    outf.write("server=/%s/%s\n" % (key, passthrough_dns))

outf.write('address=/#/192.168.3.1\n')
outf.close()

subprocess.call("/etc/init.d/dnsmasq restart")
