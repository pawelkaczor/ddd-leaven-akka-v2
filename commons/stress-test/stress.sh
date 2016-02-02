#!/bin/bash

configDir=/mnt/stress-test
requestsFileName=$1

if [[ -z "$requestsFileName" ]]; then
    requestsFileName=create-reserve-confirm.json
fi

cd $configDir

threadTotal=60
testDuration=60s

wrk -t${threadTotal} -c${threadTotal} -d${testDuration} -s $configDir/wrk-config.lua http://127.0.0.1:80 -- "$configDir/requests/$requestsFileName"