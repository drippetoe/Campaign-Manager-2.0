#!/bin/bash
#Wrapper to start the obexsender
python /home/proximus/bin/obexsender/Main.py -c /home/proximus/config/obexsender.conf > /dev/null 2>&1 &
