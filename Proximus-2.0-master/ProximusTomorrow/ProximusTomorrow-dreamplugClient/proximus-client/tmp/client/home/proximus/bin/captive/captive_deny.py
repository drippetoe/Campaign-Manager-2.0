#!/usr/bin/env python

import sys
import subprocess

#######################################################
# this script removes a MAC address from passing through
# the captured portal based on the client's IP address
#######################################################
if ( len(sys.argv) != 2 ):
    print "usage: allow.py <ip address>"
    exit()

process = subprocess.Popen(['/usr/sbin/arp', '-an'], shell=False, stdout=subprocess.PIPE)
info = process.communicate()
data = info[0]

data = data.split("\n")

for item in data:
    x = item.find("(") + 1
    y = item.find(")")
    ipAddress = item[x:y]
    if ( ipAddress.strip() == sys.argv[1].strip() ):
        z = item.find("at") + 3
        macAddress = item[z:(z+17)].upper()
        print "match %s => %s" % (ipAddress, macAddress)
        command = ["iptables", "-t", "mangle", "-D", "internet", "1", "-m",
                "mac", "--mac-source", macAddress]
		# note that the command needs to be in sudo if not run
		# as priviliged user
        subprocess.call(command)


