#!/bin/bash

PORT="$1"
LOCAL_PORT="$2"
if [ -z "$LOCAL_PORT" ]; then
    LOCAL_PORT="16222"
fi
REMOTE_SERVER="dev.proximusmobility.net"
REMOTE_CMD="sleep 1000"
    
if [ -z "$PORT" ]; then
    echo "Port must be specified"
    exit 1
fi
if [ "$PORT" -le 1024 ]; then
    echo "Port must be greater than 1024"
    exit 2
fi
ssh -v -f -R ${PORT}:localhost:${LOCAL_PORT} ${REMOTE_SERVER} "${REMOTE_CMD}"
