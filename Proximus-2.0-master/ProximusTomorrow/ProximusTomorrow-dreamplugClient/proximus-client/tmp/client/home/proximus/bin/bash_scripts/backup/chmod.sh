#!/bin/bash
#   @Author Gilberto Gaxiola
### Change Owner and Permissions to the specified user ($1) and dir ($2)
###
##
#
user=$1
dir=$2
#echo "The user is $user"
#echo "The dir is: $dir"
echo chown -R root:$user $dir
chown -R root:$user $dir
echo chmod -R 775 $dir
chmod -R 775 $dir
