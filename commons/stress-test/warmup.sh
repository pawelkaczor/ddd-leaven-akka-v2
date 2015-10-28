#!/bin/bash

configDir=/mnt/stress-test

cd $configDir

#host=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`
host=example.com

wrk $@ -s $configDir/wrk-config.lua http://$host:80 -- $configDir