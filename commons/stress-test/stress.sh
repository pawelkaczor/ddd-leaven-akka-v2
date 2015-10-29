#!/bin/bash

configDir=/mnt/stress-test

cd $configDir

# 10 customers, 10 seconds
wrk -t2 -c2 -d10s -s $configDir/wrk-config.lua http://127.0.0.1:80 -- $configDir