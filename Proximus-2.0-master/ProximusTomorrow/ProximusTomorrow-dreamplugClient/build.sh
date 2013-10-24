#!/bin/bash
################################
# Requires fakeroot
################################

fakeroot dpkg --build proximus-client

################################
#scp -P 16222 proximus-client.deb root@192.168.45.3:/root/.
################################