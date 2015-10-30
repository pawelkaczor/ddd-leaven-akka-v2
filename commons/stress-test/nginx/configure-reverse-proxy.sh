#/bin/bash

template=$1

default_site=/etc/nginx/sites-enabled/default
target=/etc/nginx/sites-enabled/ec

rm -f $default_site

cp $template $target

host=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`
sed -i "s/<host>/$host/" $target

service nginx stop
service nginx start
sleep 1