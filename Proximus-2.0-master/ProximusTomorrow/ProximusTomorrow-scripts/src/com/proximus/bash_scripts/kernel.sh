#!/bin/bash
if [ "$1" = "version" ]; then
    uname -r
elif [ "$1" = "name" ]; then
    uname
else
    uname -a
fi