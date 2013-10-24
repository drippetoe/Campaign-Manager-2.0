#!/bin/bash
#########################################333
PROX_BIN=/home/proximus/bin
$PROX_BIN/bash_scripts/kill_proximus.sh
sleep 1
rm -r /home/proximus/logs
$PROX_BIN/start_proximus_client.sh &