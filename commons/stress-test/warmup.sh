#!/bin/bash


configDir=/mnt/stress-test

cd $configDir

chmod +x ./nginx/configure-reverse-proxy.sh
./nginx/configure-reverse-proxy.sh $configDir/nginx/template

# start one customer, one session
wrk -t1 -c1 -s $configDir/wrk-config.lua http://127.0.0.1:80 -- $configDir 1