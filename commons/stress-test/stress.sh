#!/bin/bash

configDir=/mnt/stress-test

cd $configDir

# 50 threads, 30 seconds
wrk -t50 -c50 -d30s -s $configDir/wrk-config.lua http://127.0.0.1:80 -- $configDir