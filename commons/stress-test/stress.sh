#!/bin/bash

configDir=/mnt/stress-test

cd $configDir

threadTotal=50
testDuration=30s

wrk -t${threadTotal} -c${threadTotal} -d${testDuration} -s $configDir/wrk-config.lua http://127.0.0.1:80 -- $configDir