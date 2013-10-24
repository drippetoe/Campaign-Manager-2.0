#!/bin/bash

PORT="$1"
LOCAL_PORT="$2"
if [ -z "$LOCAL_PORT" ]; then
    LOCAL_PORT="16222"
fi

REMOTE_SERVER="proxdev"
REMOTE_CMD="sleep 1000"
    
if [ -z "$PORT" ]; then
    echo "Port must be specified"
    exit 1
fi
if [ "$PORT" -le 1024 ]; then
    echo "Port must be greater than 1024"
    exit 2
fi

MAC=$(/sbin/ifconfig eth0 | grep HWaddr | awk '{print toupper($5)}' | sed 's/://g')
LOG_DIR="/home/proximus/logs/queue"
LOG_FILENAME="shellcommand_${MAC}_reversessh_.$(date +'%Y-%m-%d.%H-%M-%S').log"

echo "Connecting ${PORT}:localhost:${LOCAL_PORT} ${REMOTE_SERVER} ${REMOTE_CMD}"

nohup ssh -v -f -R ${PORT}:localhost:${LOCAL_PORT} ${REMOTE_SERVER} "${REMOTE_CMD}" > ${LOG_DIR}/${LOG_FILENAME} 2&>1 &
